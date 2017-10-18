package com.davidkoudela.crucible.admin;

import com.atlassian.crucible.spi.data.UserData;
import com.atlassian.crucible.spi.services.NotFoundException;
import com.atlassian.fecru.page.Page;
import com.atlassian.fecru.page.PageRequest;
import com.atlassian.fecru.user.FecruUser;
import com.atlassian.fecru.user.GroupName;
import com.cenqua.crucible.model.Principal;
import com.cenqua.fisheye.LicensePolicyException;
import com.cenqua.fisheye.config.ConfigException;
import com.cenqua.fisheye.config1.ConfigDocument;
import com.cenqua.fisheye.rep.RepositoryHandle;
import com.cenqua.fisheye.user.*;
import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchFilterFactory;
import com.davidkoudela.crucible.ldap.model.*;
import com.davidkoudela.crucible.logs.AdvancedLdapLogService;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapPluginConfigurationDAO;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapUserDAO;
import com.davidkoudela.crucible.statistics.AdvancedLdapGroupUserSyncCount;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchScope;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import java.util.*;

/**
 * Description: Implementation of {@link AdvancedLdapUserManager} providing managed LDAP users with their groups.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-13
 */
@org.springframework.stereotype.Service("advancedLdapUserManager")
public class AdvancedLdapUserManagerImpl implements AdvancedLdapUserManager {
    private Logger log = Logger.getLogger(this.getClass());
    private UserManager userManager;
    private HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO;
    private AdvancedLdapConnector advancedLdapConnector = null;
    private AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = null;
    private AdvancedLdapPersonBuilder advancedLdapPersonBuilder = null;
    private AdvancedLdapNestedGroupSearchResultBuilder advancedLdapNestedGroupSearchResultBuilder = null;
    private AdvancedLdapBindBuilder advancedLdapBindBuilder = null;
    private HibernateAdvancedLdapUserDAO hibernateAdvancedLdapUserDAO = null;
    private GroupMembershipManager groupMembershipManager = null;

    @org.springframework.beans.factory.annotation.Autowired
    public AdvancedLdapUserManagerImpl(UserManager userManager, HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO,
                                       HibernateAdvancedLdapUserDAO hibernateAdvancedLdapUserDAO, AdvancedLdapLogService advancedLdapLogService,
                                       GroupMembershipManager groupMembershipManager) {
        this.userManager = userManager;
        this.hibernateAdvancedLdapPluginConfigurationDAO = hibernateAdvancedLdapPluginConfigurationDAO;
        this.hibernateAdvancedLdapUserDAO = hibernateAdvancedLdapUserDAO;
        this.advancedLdapPluginConfiguration = hibernateAdvancedLdapPluginConfigurationDAO.get();
        this.groupMembershipManager = groupMembershipManager;
        advancedLdapLogService.setLogLevel(this.advancedLdapPluginConfiguration.getLogLevelAsLevel());
        log.info("**************************** AdvancedLdapUserManagerImpl START ****************************");
    }

    public void restoreUserManager(UserManager userManager) {
        this.userManager = userManager;
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
            log.warn("Search Request creation failed for filter: " + advancedLdapPluginConfiguration.getUserFilterKey() + " Exception: " + e);
            return;
        }

        AdvancedLdapConnector advancedLdapConnector = getAdvancedLdapConnector();
        AdvancedLdapPersonBuilder advancedLdapPersonBuilder = getAdvancedLdapPersonBuilder();
        advancedLdapConnector.ldapPagedSearch(searchRequest, advancedLdapPersonBuilder);
        List<AdvancedLdapPerson> persons = advancedLdapPersonBuilder.getPersons();

        if (1 != persons.size()) {
            log.debug("AdvancedLdapUserManagerImpl: person search returned " + persons.size() + " entries");
            return;
        }
        AdvancedLdapPerson advancedLdapPerson = persons.get(0);

        for (AdvancedLdapGroup advancedLdapGroup : advancedLdapPerson.getGroupList()) {
            GroupName GID = GroupName.create(advancedLdapGroup.getNormalizedGID());
            log.debug("AdvancedLdapUserManagerImpl: GID: " + GID);
            try {
                if (!this.userManager.groupExists(GID)) {
                    log.debug("AdvancedLdapUserManagerImpl: GID added: " + GID);
                    this.userManager.addGroup(GID);
                }
                if (!this.groupMembershipManager.isUserInGroup(GID, advancedLdapPerson.getUid())) {
                    this.groupMembershipManager.addUserToGroup(GID, advancedLdapPerson.getUid());
                }
            } catch (Exception e) {
                log.debug("AdvancedLdapUserManagerImpl: group: " + GID + " failed: " + e);
            } catch(Throwable e) {
                log.debug("AdvancedLdapUserManagerImpl: group: " + GID + " failed unexpected: " + e);
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
            log.warn("Search Request creation failed for filter: " + advancedLdapPluginConfiguration.getGroupFilterKey() + " Exception: " + e);
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
            GroupName GID = GroupName.create(advancedLdapGroup.getNormalizedGID());
            log.debug("AdvancedLdapUserManagerImpl: GID: " + GID);
            if (!this.userManager.groupExists(GID)) {
                log.debug("AdvancedLdapUserManagerImpl: GID added: " + GID);
                advancedLdapGroupUserSyncCount.incrementGroupCountNew();
                this.userManager.addGroup(GID);
            }

            for (AdvancedLdapPerson advancedLdapPerson : advancedLdapGroup.getPersonList()) {
                String UID = advancedLdapPerson.getUid();
                log.debug("AdvancedLdapUserManagerImpl: UID: " + UID);
                noDuplicatedUID.add(UID);
                try {
                    if (!this.userManager.existsEnabledUser(UID)) {
                        log.debug("AdvancedLdapUserManagerImpl: UID does not exist in Crucible: " + UID);
                        this.hibernateAdvancedLdapUserDAO.create(UID, advancedLdapPerson.getDisplayName(), advancedLdapPerson.getEmail());
                        advancedLdapGroupUserSyncCount.incrementUserCountNew();
                    }
                    if (!this.groupMembershipManager.isUserInGroup(GID, advancedLdapPerson.getUid())) {
                        this.groupMembershipManager.addUserToGroup(GID, advancedLdapPerson.getUid());
                        advancedLdapGroupUserSyncCount.incrementAddedUsersToGroupsCount();
                    }
                } catch (Exception e) {
                    log.debug("AdvancedLdapUserManagerImpl: person: " + UID + " failed: " + e);
                } catch(Throwable e) {
                    log.debug("AdvancedLdapUserManagerImpl: person: " + UID + " failed unexpected: " + e);
                }
            }

            if (true == advancedLdapPluginConfiguration.isRemovingUsersFromGroupsEnabled()) {
                for (FecruUser userInGroup : this.groupMembershipManager.getUsersInGroup(GID)) {
                    try {
                        if (false == advancedLdapGroup.isUIDInPersonList(userInGroup.getUsername())) {
                            this.groupMembershipManager.removeUserFromGroup(GID, userInGroup.getUsername());
                            advancedLdapGroupUserSyncCount.incrementRemovedUsersFromGroupsCount();
                        }
                    } catch (Exception e) {
                        log.debug("AdvancedLdapUserManagerImpl: removing person: " + userInGroup + " failed: " + e);
                    }
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
            log.warn("Search Request creation failed for filter: " + advancedLdapPluginConfiguration.getUserFilterKey() + " Exception: " + e);
            return false;
        }

        AdvancedLdapConnector advancedLdapConnector = getAdvancedLdapConnector();
        AdvancedLdapBindBuilder advancedLdapBindBuilder = getAdvancedLdapBindBuilder(password);
        advancedLdapConnector.ldapPagedSearch(searchRequest, advancedLdapBindBuilder);
        List<AdvancedLdapBind> binds = advancedLdapBindBuilder.getBinds();

        if (1 != binds.size()) {
            log.warn("AdvancedLdapUserManagerImpl: bind search returned " + binds.size() + " entries");
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
    public void resetFailedLoginAttempts(String s) {
        this.userManager.resetFailedLoginAttempts(s);
    }

    @Override
    public void reload(ConfigDocument.Config config) throws ConfigException {
        this.userManager.reload(config);
    }

    @Override
    public UserLogin validateCurrentUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return this.userManager.validateCurrentUser(httpServletRequest, httpServletResponse);
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
        return this.userManager.preCookUrl(httpServletRequest, s, b);
    }

    @Override
    public UserLogin login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String s, String s1, boolean b) throws LicensePolicyException {
        return this.userManager.login(httpServletRequest, httpServletResponse, s, s1, b);
    }

    @Override
    public boolean hasUserExceededLoginAttempts(String s) {
        return this.userManager.hasUserExceededLoginAttempts(s);
    }

    @Override
    public UserLogin login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginCookieToken loginCookieToken) {
        return this.userManager.login(httpServletRequest, httpServletResponse, loginCookieToken);
    }

    @Override
    public UserLogin tryRequestDelegatedLogin(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws LicensePolicyException {
        return this.userManager.tryRequestDelegatedLogin(httpServletRequest, httpServletResponse);
    }

    @Override
    public UserLogin createTrustedUserLogin(String s, boolean b, boolean b1) throws LicensePolicyException {
        return this.userManager.createTrustedUserLogin(s, b, b1);
    }

    @Override
    public UserLogin createTrustedUserLogin(String s) throws LicensePolicyException {
        return this.userManager.createTrustedUserLogin(s);
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.userManager.logout(httpServletRequest, httpServletResponse);
    }

    @Override
    public void logout2(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean b, boolean b1) {
        this.userManager.logout2(httpServletRequest, httpServletResponse, b, b1);
    }

    @Override
    public void logout2User(String s, boolean b, boolean b1) {
        this.userManager.logout2User(s, b, b1);
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
    public List<String> getAllEnabledUsernames() {
        return this.userManager.getAllEnabledUsernames();
    }

    @Override
    public Iterable<String> getAllLicensedUsernames() {
        return this.userManager.getAllLicensedUsernames();
    }

    @Override
    public boolean groupExists(GroupName var1) {
        return this.userManager.groupExists(var1);
    }

    @Override
    public Optional<GroupInfo> getGroupInfo(GroupName var1) {
        return this.userManager.getGroupInfo(var1);
    }

    @Override
    public GroupInfo addGroup(GroupName var1) {
        return this.userManager.addGroup(var1);
    }

    @Override
    public void deleteGroup(GroupName var1) {
        this.userManager.deleteGroup(var1);
    }

    @Override
    public List<FecruUser> getLicensedUsers() {
        return this.userManager.getLicensedUsers();
    }

    @Override
    public Page<FecruUser> searchUsers(UserSearchCriteria userSearchCriteria, PageRequest pageRequest) {
        return this.userManager.searchUsers(userSearchCriteria, pageRequest);
    }

    @Override
    public Page<GroupInfo> searchGroups(GroupSearchCriteria groupSearchCriteria, PageRequest pageRequest) {
        return this.userManager.searchGroups(groupSearchCriteria, pageRequest);
    }

    @Override
    public void renameUser(String s, String s1) {
        this.userManager.renameUser(s, s1);
    }

    @Override
    public FecruUser addUser(String s, String s1, String s2, String s3, boolean b) throws LicensePolicyException {
        return this.userManager.addUser(s, s1, s2, s3, b);
    }

    @Override
    public void changePassword(String s, String s1) {
        this.userManager.changePassword(s, s1);
    }

    @Override
    public void requestPasswordReset(String s, String s1) {
        this.userManager.requestPasswordReset(s, s1);
    }

    @Override
    public void resetPassword(String s, String s1) {
        this.userManager.resetPassword(s, s1);
    }

    @Override
    public boolean canUpdateUser(String s) {
        return this.userManager.canUpdateUser(s);
    }

    @Override
    public void updateUser(String s, String s1, String s2) {
        this.userManager.updateUser(s, s1, s2);
    }

    @Override
    public boolean existsLicensedUser(String s) {
        return this.userManager.existsLicensedUser(s);
    }

    @Override
    public boolean existsEnabledUser(String s) {
        return this.userManager.existsEnabledUser(s);
    }

    @Override
    public Optional<FecruUser> getEnabledUser(String s) {
        return this.userManager.getEnabledUser(s);
    }

    @Override
    public GroupInfo ensureGroupExists(GroupName var1) throws NotFoundException {
        return this.userManager.ensureGroupExists(var1);
    }

    @Override
    public FecruUser getLicensedUser(String s) {
        return this.userManager.getLicensedUser(s);
    }

    @Override
    public FecruUser getLicensedUser(int i) {
        return this.userManager.getLicensedUser(i);
    }

    @Override
    public FecruUser getUser(String s) {
        return this.userManager.getUser(s);
    }

    @Override
    public FecruUser getUserById(int i) {
        return this.userManager.getUserById(i);
    }

    @Override
    public String getUsernameByEmail(String s) {
        return this.userManager.getUsernameByEmail(s);
    }

    @Override
    public void deleteUserFully(String s) {
        this.userManager.deleteUserFully(s);
    }

    @Override
    public boolean deleteUserAndRemoveCommitterMappings(String s) {
        return this.userManager.deleteUserAndRemoveCommitterMappings(s);
    }

    @Override
    public boolean deleteMultipleUsersAndRemoveCommitterMappings(List<String> list) {
        return this.userManager.deleteMultipleUsersAndRemoveCommitterMappings(list);
    }

    @Override
    public void deleteUser(String s, boolean b) {
        this.userManager.deleteUser(s, b);
    }

    @Override
    public boolean hasPermissionToAccess(Principal principal, RepositoryHandle repositoryHandle) {
        return this.userManager.hasPermissionToAccess(principal, repositoryHandle);
    }

    @Override
    public boolean isCrucibleEnabled(String s) {
        return this.userManager.isCrucibleEnabled(s);
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
    public FecruUser getUserFor(Principal principal) {
        return this.userManager.getUserFor(principal);
    }

    @Override
    public boolean isValidPassword(String s, String s1) {
        return this.userManager.isValidPassword(s, s1);
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
    public boolean isAdminGroup(GroupName var1) {
        return this.userManager.isAdminGroup(var1);
    }

    @Override
    public void setAdminGroup(GroupName var1, boolean b) {
        this.userManager.setAdminGroup(var1, b);
    }

    @Override
    public boolean isPasswordlessAuthenticationEnabled() {
        return this.userManager.isPasswordlessAuthenticationEnabled();
    }
}
