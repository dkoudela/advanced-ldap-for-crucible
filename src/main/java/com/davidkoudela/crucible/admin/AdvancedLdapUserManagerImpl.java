package com.davidkoudela.crucible.admin;

import com.atlassian.crucible.spi.data.UserData;
import com.atlassian.fecru.user.User;
import com.atlassian.fecru.user.UserDAOImpl;
import com.cenqua.crucible.hibernate.HibernateUtilCurrentSessionProvider;
import com.cenqua.fisheye.user.UserManager;
import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.config.HibernateAdvancedLdapPluginConfigurationDAO;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchFilterFactory;
import com.davidkoudela.crucible.ldap.model.*;
import com.unboundid.ldap.sdk.*;

import java.util.List;

/**
 * Description: Implementation of {@link AdvancedLdapUserManager} providing managed LDAP users with their groups.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-13
 */
@org.springframework.stereotype.Service("advancedLdapUserManager")
public class AdvancedLdapUserManagerImpl implements AdvancedLdapUserManager {
    private UserManager userManager;
    private HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO;

    @org.springframework.beans.factory.annotation.Autowired
    public AdvancedLdapUserManagerImpl(UserManager userManager, HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO) {
        this.userManager = userManager;
        this.hibernateAdvancedLdapPluginConfigurationDAO = hibernateAdvancedLdapPluginConfigurationDAO;
        System.out.println("**************************** AdvancedLdapUserManagerImpl START ****************************");
    }

    @Override
    public void loadUser(UserData userData) {
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = hibernateAdvancedLdapPluginConfigurationDAO.get();

        if (advancedLdapPluginConfiguration.getLDAPUrl().isEmpty()) {
            return;
        }

        System.out.println("AdvancedLdapUserManagerImpl.loadUser START");
        SearchRequest searchRequest = null;
        try {
            Filter filter = AdvancedLdapSearchFilterFactory.getSearchFilterForUser(advancedLdapPluginConfiguration.getUserFilterKey(), userData.getUserName());
            searchRequest = new SearchRequest(advancedLdapPluginConfiguration.getLDAPBaseDN(), SearchScope.SUB, filter);
        } catch (Exception e) {
            System.out.println("Search Request creation failed for filter: " + advancedLdapPluginConfiguration.getUserFilterKey() + " Exception: " + e);
            return;
        }

        AdvancedLdapConnector advancedLdapConnector = new AdvancedLdapConnector();
        AdvancedLdapPersonBuilder advancedLdapPersonBuilder = new AdvancedLdapPersonBuilder(advancedLdapPluginConfiguration, true);
        advancedLdapConnector.ldapPagedSearch(advancedLdapPluginConfiguration, searchRequest, advancedLdapPersonBuilder);
        List<AdvancedLdapPerson> persons = advancedLdapPersonBuilder.getPersons();

        if (1 != persons.size()) {
            System.out.println("AdvancedLdapUserManagerImpl: person search returned "+ persons.size() + " entries");
            return;
        }
        AdvancedLdapPerson advancedLdapPerson = persons.get(0);

        for (AdvancedLdapGroup advancedLdapGroup : advancedLdapPerson.getGroupList()) {
            String GID = advancedLdapGroup.getNormalizedGID();
            System.out.println("AdvancedLdapUserManagerImpl: GID: " + GID);
            try {
                if (!this.userManager.builtInGroupExists(GID)) {
                    System.out.println("AdvancedLdapUserManagerImpl: GID added: " + GID);
                    this.userManager.addBuiltInGroup(GID);
                }
                if (!this.userManager.isUserInGroup(GID, advancedLdapPerson.getUid())) {
                    this.userManager.addUserToBuiltInGroup(GID, advancedLdapPerson.getUid());
                }
            } catch (Exception e) {
                System.out.println("AdvancedLdapUserManagerImpl: group: " + GID + " failed: " + e);
            } catch(Throwable e) {
                System.out.println("AdvancedLdapUserManagerImpl: group: " + GID + " failed unexpected: " + e);
            }
        }

        System.out.println("AdvancedLdapUserManagerImpl.loadUser END");
    }

    @Override
    public void loadGroups() {
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = hibernateAdvancedLdapPluginConfigurationDAO.get();

        System.out.println("AdvancedLdapUserManagerImpl.loadGroups START");
        SearchRequest searchRequest = null;
        try {
            Filter filter = AdvancedLdapSearchFilterFactory.getSearchFilterForAllGroups(advancedLdapPluginConfiguration.getGroupFilterKey());
            searchRequest = new SearchRequest(advancedLdapPluginConfiguration.getLDAPBaseDN(), SearchScope.SUB, filter);
        } catch (Exception e) {
            System.out.println("Search Request creation failed for filter: " + advancedLdapPluginConfiguration.getGroupFilterKey() + " Exception: " + e);
            return;
        }

        AdvancedLdapConnector advancedLdapConnector = new AdvancedLdapConnector();
        AdvancedLdapGroupBuilder advancedLdapGroupBuilder = new AdvancedLdapGroupBuilder(advancedLdapPluginConfiguration, true);
        advancedLdapConnector.ldapPagedSearch(advancedLdapPluginConfiguration, searchRequest, advancedLdapGroupBuilder);
        List<AdvancedLdapGroup> groups = advancedLdapGroupBuilder.getGroups();

        for (AdvancedLdapGroup advancedLdapGroup : groups) {
            String GID = advancedLdapGroup.getNormalizedGID();
            System.out.println("AdvancedLdapUserManagerImpl: GID: " + GID);
            if (!this.userManager.builtInGroupExists(GID)) {
                System.out.println("AdvancedLdapUserManagerImpl: GID added: " + GID);
                this.userManager.addBuiltInGroup(GID);
            }

            for (AdvancedLdapPerson advancedLdapPerson : advancedLdapGroup.getPersonList()) {
                String UID = advancedLdapPerson.getUid();
                System.out.println("AdvancedLdapUserManagerImpl: UID: " + UID);
                try {
                    if (!this.userManager.userExists(UID)) {
                        System.out.println("AdvancedLdapUserManagerImpl: UID does not exist in Crucible: " + UID);
                        com.atlassian.fecru.user.User  user = new User(UID);
                        user.setDisplayName(advancedLdapPerson.getDisplayName());
                        user.setEmail(advancedLdapPerson.getEmail());
                        user.setAuthType(User.AuthType.LDAP);
                        user.setFisheyeEnabled(true);
                        UserDAOImpl userDAO = new UserDAOImpl();
                        userDAO.setCurrentSessionProvider(new HibernateUtilCurrentSessionProvider());
                        userDAO.create(user);
                        userManager.setCrucibleEnabled(UID, true);
                    }
                    if (!this.userManager.isUserInGroup(GID, advancedLdapPerson.getUid())) {
                        this.userManager.addUserToBuiltInGroup(GID, advancedLdapPerson.getUid());
                    }
                } catch (Exception e) {
                    System.out.println("AdvancedLdapUserManagerImpl: person: " + UID + " failed: " + e);
                } catch(Throwable e) {
                    System.out.println("AdvancedLdapUserManagerImpl: person: " + UID + " failed unexpected: " + e);
                }
            }
        }

        System.out.println("AdvancedLdapUserManagerImpl.loadGroups END");
    }

    @Override
    public boolean verifyUserCredentials(String username, String password) {
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = hibernateAdvancedLdapPluginConfigurationDAO.get();

        if (advancedLdapPluginConfiguration.getLDAPUrl().isEmpty()) {
            return false;
        }

        System.out.println("AdvancedLdapUserManagerImpl.verifyUserCredentials START");
        SearchRequest searchRequest = null;
        try {
            Filter filter = AdvancedLdapSearchFilterFactory.getSearchFilterForUser(advancedLdapPluginConfiguration.getUserFilterKey(), username);
            searchRequest = new SearchRequest(advancedLdapPluginConfiguration.getLDAPBaseDN(), SearchScope.SUB, filter);
        } catch (Exception e) {
            System.out.println("Search Request creation failed for filter: " + advancedLdapPluginConfiguration.getUserFilterKey() + " Exception: " + e);
            return false;
        }

        AdvancedLdapConnector advancedLdapConnector = new AdvancedLdapConnector();
        AdvancedLdapBindBuilder advancedLdapBindBuilder = new AdvancedLdapBindBuilder(advancedLdapPluginConfiguration, password);
        advancedLdapConnector.ldapPagedSearch(advancedLdapPluginConfiguration, searchRequest, advancedLdapBindBuilder);
        List<AdvancedLdapBind> binds = advancedLdapBindBuilder.getBinds();

        if (1 != binds.size()) {
            System.out.println("AdvancedLdapUserManagerImpl: bind search returned "+ binds.size() + " entries");
            return false;
        }
        AdvancedLdapBind advancedLdapBind = binds.get(0);

        return advancedLdapBind.getResult();
    }
}
