package com.davidkoudela.crucible.admin;

import com.atlassian.crucible.spi.data.UserData;
import com.atlassian.crucible.spi.services.NotFoundException;
import com.atlassian.extras.common.LicenseException;
import com.atlassian.fecru.page.Page;
import com.atlassian.fecru.page.PageRequest;
import com.atlassian.fecru.user.User;
import com.atlassian.fecru.user.UserDAO;
import com.cenqua.crucible.model.Principal;
import com.cenqua.fisheye.LicensePolicyException;
import com.cenqua.fisheye.config.ConfigException;
import com.cenqua.fisheye.config1.ConfigDocument;
import com.cenqua.fisheye.rep.RepositoryHandle;
import com.cenqua.fisheye.user.*;
import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapPluginConfigurationDAO;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapUserDAO;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchFilterFactory;
import com.davidkoudela.crucible.ldap.model.*;
import com.davidkoudela.crucible.statistics.AdvancedLdapGroupUserSyncCount;
import com.unboundid.ldap.sdk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: Implementation of {@link AdvancedLdapUserManager} providing managed LDAP users with their groups.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-13
 */
@org.springframework.stereotype.Service("advancedLdapUserManager")
public class AdvancedLdapUserManagerImpl implements AdvancedLdapUserManager {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private UserManager userManager;
    private HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO;
    private AdvancedLdapConnector advancedLdapConnector = null;
    private AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = null;
    private AdvancedLdapPersonBuilder advancedLdapPersonBuilder = null;
    private AdvancedLdapNestedGroupSearchResultBuilder advancedLdapNestedGroupSearchResultBuilder = null;
    private AdvancedLdapBindBuilder advancedLdapBindBuilder = null;
    private HibernateAdvancedLdapUserDAO hibernateAdvancedLdapUserDAO = null;

    @org.springframework.beans.factory.annotation.Autowired
    public AdvancedLdapUserManagerImpl(UserManager userManager, HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO,
                                       HibernateAdvancedLdapUserDAO hibernateAdvancedLdapUserDAO) {
        this.userManager = userManager;
        this.hibernateAdvancedLdapPluginConfigurationDAO = hibernateAdvancedLdapPluginConfigurationDAO;
        this.hibernateAdvancedLdapUserDAO = hibernateAdvancedLdapUserDAO;
//        AppConfig.getsConfig().setUserManager(this);
        log.info("**************************** AdvancedLdapUserManagerImpl START ****************************");
    }

    @Override
    public void loadUser(UserData userData) {
        this.advancedLdapPluginConfiguration = hibernateAdvancedLdapPluginConfigurationDAO.get();

        if (advancedLdapPluginConfiguration.getLDAPUrl().isEmpty()) {
            return;
        }

        log.info("AdvancedLdapUserManagerImpl.loadUser START");
        SearchRequest searchRequest = null;
        try {
            Filter filter = AdvancedLdapSearchFilterFactory.getSearchFilterForUser(advancedLdapPluginConfiguration.getUserFilterKey(), userData.getUserName());
            searchRequest = new SearchRequest(advancedLdapPluginConfiguration.getLDAPBaseDN(), SearchScope.SUB, filter);
        } catch (Exception e) {
            log.info("Search Request creation failed for filter: " + advancedLdapPluginConfiguration.getUserFilterKey() + " Exception: " + e);
            return;
        }

        AdvancedLdapConnector advancedLdapConnector = getAdvancedLdapConnector();
        AdvancedLdapPersonBuilder advancedLdapPersonBuilder = getAdvancedLdapPersonBuilder();
        advancedLdapConnector.ldapPagedSearch(searchRequest, advancedLdapPersonBuilder);
        List<AdvancedLdapPerson> persons = advancedLdapPersonBuilder.getPersons();

        if (1 != persons.size()) {
            log.info("AdvancedLdapUserManagerImpl: person search returned "+ persons.size() + " entries");
            return;
        }
        AdvancedLdapPerson advancedLdapPerson = persons.get(0);

        for (AdvancedLdapGroup advancedLdapGroup : advancedLdapPerson.getGroupList()) {
            String GID = advancedLdapGroup.getNormalizedGID();
            log.info("AdvancedLdapUserManagerImpl: GID: " + GID);
            try {
                if (!this.userManager.builtInGroupExists(GID)) {
                    log.info("AdvancedLdapUserManagerImpl: GID added: " + GID);
                    this.userManager.addBuiltInGroup(GID);
                }
                if (!this.userManager.isUserInGroup(GID, advancedLdapPerson.getUid())) {
                    this.userManager.addUserToBuiltInGroup(GID, advancedLdapPerson.getUid());
                }
            } catch (Exception e) {
                log.info("AdvancedLdapUserManagerImpl: group: " + GID + " failed: " + e);
            } catch(Throwable e) {
                log.info("AdvancedLdapUserManagerImpl: group: " + GID + " failed unexpected: " + e);
            }
        }

        log.info("AdvancedLdapUserManagerImpl.loadUser END");
    }

    @Override
    public void loadGroups(AdvancedLdapGroupUserSyncCount advancedLdapGroupUserSyncCount) {
        this.advancedLdapPluginConfiguration = hibernateAdvancedLdapPluginConfigurationDAO.get();

        log.info("AdvancedLdapUserManagerImpl.loadGroups START");
        SearchRequest searchRequest = null;
        try {
            Filter filter = AdvancedLdapSearchFilterFactory.getSearchFilterForAllGroups(advancedLdapPluginConfiguration.getGroupFilterKey());
            searchRequest = new SearchRequest(advancedLdapPluginConfiguration.getLDAPBaseDN(), SearchScope.SUB, filter);
        } catch (Exception e) {
            log.info("Search Request creation failed for filter: " + advancedLdapPluginConfiguration.getGroupFilterKey() + " Exception: " + e);
            return;
        }

        AdvancedLdapConnector advancedLdapConnector = getAdvancedLdapConnector();
        AdvancedLdapNestedGroupSearchResultBuilder advancedLdapNestedGroupSearchResultBuilder = getAdvancedLdapNestedGroupSearchResultBuilder();
        advancedLdapConnector.ldapPagedSearch(searchRequest, advancedLdapNestedGroupSearchResultBuilder);
        List<AdvancedLdapGroup> groups = advancedLdapNestedGroupSearchResultBuilder.getGroups();

        advancedLdapGroupUserSyncCount.setGroupCountTotal(advancedLdapNestedGroupSearchResultBuilder.getGroupNames().size());
        advancedLdapGroupUserSyncCount.setNestedGroupCount(advancedLdapNestedGroupSearchResultBuilder.getNestedGroups().size());
        Set<String> noDuplicatedUID = new HashSet<String>();
        for (AdvancedLdapGroup advancedLdapGroup : groups) {
            String GID = advancedLdapGroup.getNormalizedGID();
            log.info("AdvancedLdapUserManagerImpl: GID: " + GID);
            if (!this.userManager.builtInGroupExists(GID)) {
                log.info("AdvancedLdapUserManagerImpl: GID added: " + GID);
                advancedLdapGroupUserSyncCount.incrementGroupCountNew();
                this.userManager.addBuiltInGroup(GID);
            }

            for (AdvancedLdapPerson advancedLdapPerson : advancedLdapGroup.getPersonList()) {
                String UID = advancedLdapPerson.getUid();
                log.info("AdvancedLdapUserManagerImpl: UID: " + UID);
                noDuplicatedUID.add(UID);
                try {
                    if (!this.userManager.userExists(UID)) {
                        log.info("AdvancedLdapUserManagerImpl: UID does not exist in Crucible: " + UID);
                        advancedLdapGroupUserSyncCount.incrementUserCountNew();
                        this.hibernateAdvancedLdapUserDAO.create(UID, advancedLdapPerson.getDisplayName(), advancedLdapPerson.getEmail());
                    }
                    if (!this.userManager.isUserInGroup(GID, advancedLdapPerson.getUid())) {
                        this.userManager.addUserToBuiltInGroup(GID, advancedLdapPerson.getUid());
                    }
                } catch (Exception e) {
                    log.info("AdvancedLdapUserManagerImpl: person: " + UID + " failed: " + e);
                } catch(Throwable e) {
                    log.info("AdvancedLdapUserManagerImpl: person: " + UID + " failed unexpected: " + e);
                }
            }
        }
        advancedLdapGroupUserSyncCount.setUserCountTotal(noDuplicatedUID.size());

        log.info("AdvancedLdapUserManagerImpl: nonperson DNs: " + advancedLdapNestedGroupSearchResultBuilder.getNonpersonDns().toString());
        log.info("AdvancedLdapUserManagerImpl: nested groups: " + advancedLdapNestedGroupSearchResultBuilder.getNestedGroups().toString());
        log.info("AdvancedLdapUserManagerImpl.loadGroups END");
    }

    @Override
    public boolean verifyUserCredentials(String username, String password) {
        this.advancedLdapPluginConfiguration = hibernateAdvancedLdapPluginConfigurationDAO.get();

        if (advancedLdapPluginConfiguration.getLDAPUrl().isEmpty()) {
            return false;
        }

        log.info("AdvancedLdapUserManagerImpl.verifyUserCredentials START");
        SearchRequest searchRequest = null;
        try {
            Filter filter = AdvancedLdapSearchFilterFactory.getSearchFilterForUser(advancedLdapPluginConfiguration.getUserFilterKey(), username);
            searchRequest = new SearchRequest(advancedLdapPluginConfiguration.getLDAPBaseDN(), SearchScope.SUB, filter);
        } catch (Exception e) {
            log.info("Search Request creation failed for filter: " + advancedLdapPluginConfiguration.getUserFilterKey() + " Exception: " + e);
            return false;
        }

        AdvancedLdapConnector advancedLdapConnector = getAdvancedLdapConnector();
        AdvancedLdapBindBuilder advancedLdapBindBuilder = getAdvancedLdapBindBuilder(password);
        advancedLdapConnector.ldapPagedSearch(searchRequest, advancedLdapBindBuilder);
        List<AdvancedLdapBind> binds = advancedLdapBindBuilder.getBinds();

        if (1 != binds.size()) {
            log.info("AdvancedLdapUserManagerImpl: bind search returned "+ binds.size() + " entries");
            return false;
        }
        AdvancedLdapBind advancedLdapBind = binds.get(0);

        return advancedLdapBind.getResult();
    }

    protected AdvancedLdapConnector getAdvancedLdapConnector() {
        if (null != this.advancedLdapConnector)
            return this.advancedLdapConnector;
        return new AdvancedLdapConnector(this.advancedLdapPluginConfiguration);
    }

    protected void setAdvancedLdapConnector(AdvancedLdapConnector advancedLdapConnector) {
        this.advancedLdapConnector = advancedLdapConnector;
    }

    protected AdvancedLdapPersonBuilder getAdvancedLdapPersonBuilder() {
        if (null != this.advancedLdapPersonBuilder)
            return this.advancedLdapPersonBuilder;
        return new AdvancedLdapPersonBuilder(this.advancedLdapPluginConfiguration, true);
    }

    protected void setAdvancedLdapPersonBuilder(AdvancedLdapPersonBuilder advancedLdapPersonBuilder) {
        this.advancedLdapPersonBuilder = advancedLdapPersonBuilder;
    }

    protected AdvancedLdapNestedGroupSearchResultBuilder getAdvancedLdapNestedGroupSearchResultBuilder() {
        if (null != this.advancedLdapNestedGroupSearchResultBuilder)
            return this.advancedLdapNestedGroupSearchResultBuilder;
        return new AdvancedLdapNestedGroupBuilder(this.advancedLdapPluginConfiguration, true);
    }

    protected void setAdvancedLdapNestedGroupSearchResultBuilder(AdvancedLdapNestedGroupSearchResultBuilder advancedLdapNestedGroupSearchResultBuilder) {
        this.advancedLdapNestedGroupSearchResultBuilder = advancedLdapNestedGroupSearchResultBuilder;
    }

    protected AdvancedLdapBindBuilder getAdvancedLdapBindBuilder(String password) {
        if (null != this.advancedLdapBindBuilder)
            return this.advancedLdapBindBuilder;
        return new AdvancedLdapBindBuilder(this.advancedLdapPluginConfiguration, password);
    }

    protected void setAdvancedLdapBindBuilder(AdvancedLdapBindBuilder advancedLdapBindBuilder) {
        this.advancedLdapBindBuilder = advancedLdapBindBuilder;
    }

    /** Implement the origin interface to be able act as {@link UserManager} */

    @Override
    public long countActiveUsers(UserSearchCriteria userSearchCriteria) {
        return this.userManager.countActiveUsers(userSearchCriteria);
    }

    @Override
    public void notifyLicenseUpdate() {
        this.userManager.notifyLicenseUpdate();
    }

    @Override
    public void reload(ConfigDocument.Config config) throws ConfigException {
        this.userManager.reload(config);
    }

    @Override
    public UserLogin validateCurrentUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return this.userManager.validateCurrentUser(httpServletRequest,httpServletResponse);
    }

    @Override
    public UserLogin getCurrentUser(HttpServletRequest httpServletRequest) {
        return this.userManager.getCurrentUser(httpServletRequest);
    }

    @Override
    public UserLogin getCurrentUser(JspContext jspContext) {
        return this.userManager.getCurrentUser(jspContext);
    }

    @Override
    public LoginCookie preCookUrl(HttpServletRequest httpServletRequest, String s, boolean b) {
        return this.userManager.preCookUrl(httpServletRequest,s,b);
    }

    @Override
    public UserLogin login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String s, String s2, boolean b) throws LicensePolicyException {
        return this.userManager.login(httpServletRequest,httpServletResponse,s,s2,b);
    }

    @Override
    public boolean hasUserExceededLoginAttempts(String s) {
        return this.userManager.hasUserExceededLoginAttempts(s);
    }

    @Override
    public UserLogin login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginCookieToken loginCookieToken) {
        return this.userManager.login(httpServletRequest,httpServletResponse,loginCookieToken);
    }

    @Override
    public void synchroniseUsers() throws Exception {
        this.userManager.synchroniseUsers();
    }

    @Override
    public void synchroniseUsers(boolean b) throws Exception {
        this.userManager.synchroniseUsers(b);
    }

    @Override
    public UserLogin tryRequestDelegatedLogin(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws LicensePolicyException {
        return this.userManager.tryRequestDelegatedLogin(httpServletRequest,httpServletResponse);
    }

    @Override
    public UserLogin createTrustedUserLogin(String s, boolean b, boolean b2) throws LicensePolicyException {
        return this.userManager.createTrustedUserLogin(s,b,b2);
    }

    @Override
    public UserLogin createTrustedUserLogin(String s) throws LicensePolicyException {
        return this.userManager.createTrustedUserLogin(s);
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.userManager.logout(httpServletRequest,httpServletResponse);
    }

    @Override
    public void logout2(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean b, boolean b2) {
        this.userManager.logout2(httpServletRequest,httpServletResponse,b,b2);
    }

    @Override
    public void logout2User(String s, boolean b, boolean b2) {
        this.userManager.logout2User(s,b,b2);
    }

    @Override
    public String makeSecureRnd() {
        return this.userManager.makeSecureRnd();
    }

    @Override
    public String makeSecureRnd(int i) {
        return this.userManager.makeSecureRnd(i);
    }

    @Override
    public List<String> getAllActiveUsernames() {
        return this.userManager.getAllActiveUsernames();
    }

    @Override
    public List<String> getAllFishEyeEnabledUsernames() {
        return this.userManager.getAllFishEyeEnabledUsernames();
    }

    @Override
    public List<String> getAllCrucibleEnabledUsernames() {
        return this.userManager.getAllCrucibleEnabledUsernames();
    }

    @Override
    public List<User> getUsersWithTextPrefix(String s) {
        return this.userManager.getUsersWithTextPrefix(s);
    }

    @Override
    public List<User> getUsersWithTextPrefix(String s, UserDAO.UserSet userSet, int i) {
        return this.userManager.getUsersWithTextPrefix(s, userSet, i);
    }

    @Override
    public Iterable<GroupOfUsers> getGroupsWithTextPrefix(String s, int i, int i2) {
        return this.userManager.getGroupsWithTextPrefix(s,i,i2);
    }

    @Override
    public boolean filterUserByTextPrefix(User user, String s) {
        return this.userManager.filterUserByTextPrefix(user,s);
    }

    @Override
    public List<String> getGroupsForUser(String s) {
        return this.userManager.getGroupsForUser(s);
    }

    @Override
    public Page<String> getGroupsForUser(String s, PageRequest pageRequest) {
        return this.userManager.getGroupsForUser(s,pageRequest);
    }

    @Override
    public boolean isUserInGroup(String s, String s2) {
        return this.userManager.isUserInGroup(s,s2);
    }

    @Override
    public Map<String, GroupInfo> getGroupInfos() {
        return this.userManager.getGroupInfos();
    }

    @Override
    public GroupInfo getGroupInfo(String s) {
        return this.userManager.getGroupInfo(s);
    }

    @Override
    public List<String> getExternalGroupNames() {
        return this.userManager.getExternalGroupNames();
    }

    @Override
    public boolean builtInGroupExists(String s) {
        return this.userManager.builtInGroupExists(s);
    }

    @Override
    public void addBuiltInGroup(String s) {
        this.userManager.addBuiltInGroup(s);
    }

    @Override
    public void deleteBuiltInGroup(String s) {
        this.userManager.deleteBuiltInGroup(s);
    }

    @Override
    public void addUserToBuiltInGroup(String s, String s2) {
        this.userManager.addUserToBuiltInGroup(s,s2);
    }

    @Override
    public void removeUserFromBuiltInGroup(String s, String s2) {
        this.userManager.removeUserFromBuiltInGroup(s,s2);
    }

    @Override
    public List<String> getUsersInGroup(String s) {
        return this.userManager.getUsersInGroup(s);
    }

    @Override
    public Page<String> getUsersInGroup(String s, PageRequest pageRequest) {
        return this.userManager.getUsersInGroup(s,pageRequest);
    }

    @Override
    public List<User> getActiveUsers() {
        return this.userManager.getActiveUsers();
    }

    @Override
    public Page<User> searchUsers(UserSearchCriteria userSearchCriteria, PageRequest pageRequest) {
        return this.userManager.searchUsers(userSearchCriteria,pageRequest);
    }

    @Override
    public Page<GroupInfo> searchGroups(GroupSearchCriteria groupSearchCriteria, PageRequest pageRequest) {
        return this.userManager.searchGroups(groupSearchCriteria,pageRequest);
    }

    @Override
    public User getImplicitUserForCommitter(String s) {
        return this.userManager.getImplicitUserForCommitter(s);
    }

    @Override
    public List<User> getFishEyeUsers() {
        return this.userManager.getFishEyeUsers();
    }

    @Override
    public List<User> getCrucibleUsers() {
        return this.userManager.getCrucibleUsers();
    }

    @Override
    public int getActiveUsersCount() {
        return this.userManager.getActiveUsersCount();
    }

    @Override
    public int getFishEyeUsersCount() {
        return this.userManager.getFishEyeUsersCount();
    }

    @Override
    public int getCrucibleUsersCount() {
        return this.userManager.getCrucibleUsersCount();
    }

    @Override
    public void renameUser(String s, String s2) {
        this.userManager.renameUser(s,s2);
    }

    @Override
    public User addUser(User user, String s) throws LicensePolicyException {
        return this.userManager.addUser(user,s);
    }

    @Override
    public void changePassword(User user, String s) {
        this.userManager.changePassword(user,s);
    }

    @Override
    public void requestPasswordReset(User user, String s) {
        this.userManager.requestPasswordReset(user,s);
    }

    @Override
    public void resetPassword(User user, String s) {
        this.userManager.resetPassword(user,s);
    }

    @Override
    public void updateUser(User user) {
        this.userManager.updateUser(user);
    }

    @Override
    public boolean userExists(String s) {
        return this.userManager.userExists(s);
    }

    @Override
    public User ensureUserExists(String s) throws NotFoundException {
        return this.userManager.ensureUserExists(s);
    }

    @Override
    public GroupInfo ensureGroupExists(String s) throws NotFoundException {
        return this.userManager.ensureGroupExists(s);
    }

    @Override
    public User getActiveUser(String s) {
        return this.userManager.getActiveUser(s);
    }

    @Override
    public User getActiveUser(int i) {
        return this.userManager.getActiveUser(i);
    }

    @Override
    public User getUser(String s) {
        return this.userManager.getUser(s);
    }

    @Override
    public User getUserById(int i) {
        return this.userManager.getUserById(i);
    }

    @Override
    public String getUsernameByEmail(String s) {
        return this.userManager.getUsernameByEmail(s);
    }

    @Override
    public void deleteUser(User user) {
        this.userManager.deleteUser(user);
    }

    @Override
    public boolean deleteUserAndRemoveCommitterMappings(String s) {
        return this.userManager.deleteUserAndRemoveCommitterMappings(s);
    }

    @Override
    public boolean deleteMultipleUsersAndRemoveCommitterMappings(List<String> strings) {
        return this.userManager.deleteMultipleUsersAndRemoveCommitterMappings(strings);
    }

    @Override
    public void deactivateUser(User user) {
        this.userManager.deactivateUser(user);
    }

    @Override
    public void deactivateUser(User user, boolean b) {
        this.userManager.deactivateUser(user,b);
    }

    @Override
    public boolean hasPermissionToAccess(Principal principal, RepositoryHandle repositoryHandle) {
        return this.userManager.hasPermissionToAccess(principal,repositoryHandle);
    }

    @Override
    public boolean isCrucibleEnabled(String s) {
        return this.userManager.isCrucibleEnabled(s);
    }

    @Override
    public void setCrucibleEnabled(String s, boolean b) throws LicenseException {
        this.userManager.setCrucibleEnabled(s,b);
    }

    @Override
    public boolean isLoginPossible() {
        return this.userManager.isLoginPossible();
    }

    @Override
    public boolean hasSysAdminPrivileges(String s) {
        return this.userManager.hasSysAdminPrivileges(s);
    }

    @Override
    public boolean hasSysAdminPrivileges(HttpServletRequest httpServletRequest) {
        return this.userManager.hasSysAdminPrivileges(httpServletRequest);
    }

    @Override
    public User getUserFor(Principal principal) {
        return this.userManager.getUserFor(principal);
    }

    @Override
    public boolean isValidPassword(String s, String s2) {
        return this.userManager.isValidPassword(s,s2);
    }

    @Override
    public String encodePassword(String s) {
        return this.userManager.encodePassword(s);
    }

    @Override
    public Auth getAuthenticationProvider() {
        return this.userManager.getAuthenticationProvider();
    }

    @Override
    public boolean isUserNameValid(String s) {
        return this.userManager.isUserNameValid(s);
    }

    @Override
    public boolean isGroupNameValid(String s) {
        return this.userManager.isGroupNameValid(s);
    }

    @Override
    public boolean isAdminGroup(String s) {
        return this.userManager.isAdminGroup(s);
    }

    @Override
    public void setAdminGroup(String s, boolean b) {
        this.userManager.setAdminGroup(s,b);
    }
}
