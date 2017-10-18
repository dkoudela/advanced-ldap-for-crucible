package ut.com.davidkoudela.crucible.admin;

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
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManagerImpl;
import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.logs.AdvancedLdapLogService;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapPluginConfigurationDAO;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapUserDAO;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapUserDAOImpl;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchResultBuilder;
import com.davidkoudela.crucible.ldap.model.*;
import com.davidkoudela.crucible.statistics.AdvancedLdapGroupUserSyncCount;
import com.unboundid.ldap.sdk.SearchRequest;
import junit.framework.TestCase;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import java.util.*;

/**
 * Description: Testing {@link AdvancedLdapUserManagerImpl}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-19
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapUserManagerImplTest extends TestCase {
    private static UserManager userManager;
    private static HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO;
    private static AdvancedLdapConnector advancedLdapConnector = null;
    private static AdvancedLdapPersonBuilder advancedLdapPersonBuilder = null;
    private static AdvancedLdapNestedGroupBuilder advancedLdapNestedGroupBuilder = null;
    private static ArrayList<AdvancedLdapPerson> advancedLdapPersons;
    private static ArrayList<AdvancedLdapPerson> advancedLdapPersons2;
    private static ArrayList<AdvancedLdapGroup> advancedLdapGroups;
    private static ArrayList<AdvancedLdapGroup> advancedLdapGroups2;
    private static ArrayList<AdvancedLdapBind> advancedLdapBinds;
    private static ArrayList<AdvancedLdapBind> advancedLdapBinds2;
    private static ArgumentCaptor<SearchRequest> argumentCaptorSearchRequest;
    private static ArgumentCaptor<AdvancedLdapPersonBuilder> argumentCaptorAdvancedLdapPersonBuilder;
    private static ArgumentCaptor<AdvancedLdapGroupBuilder> argumentCaptorAdvancedLdapGroupBuilder;
    private static ArgumentCaptor<GroupName> argumentCaptorGID;
    private static ArgumentCaptor<AdvancedLdapPerson> argumentCaptorAdvancedLdapPerson;
    private static UserData userData;
    private static HibernateAdvancedLdapUserDAO hibernateAdvancedLdapUserDAO;
    private static AdvancedLdapBindBuilder advancedLdapBindBuilder;
    private static AdvancedLdapLogService advancedLdapLogService;
    private static GroupMembershipManager groupMembershipManager;

    public class AdvancedLdapUserManagerImplDummy extends AdvancedLdapUserManagerImpl {
        public AdvancedLdapUserManagerImplDummy(UserManager userManager,
                                                HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO,
                                                HibernateAdvancedLdapUserDAO hibernateAdvancedLdapUserDAO,
                                                AdvancedLdapLogService advancedLdapLogService,
                                                GroupMembershipManager groupMembershipManager) {
            super(userManager, hibernateAdvancedLdapPluginConfigurationDAO, hibernateAdvancedLdapUserDAO, advancedLdapLogService, groupMembershipManager);
        }

        public void setAdvancedLdapConnector(AdvancedLdapConnector advancedLdapConnector) {
            super.setAdvancedLdapConnector(advancedLdapConnector);
        }

        public void setAdvancedLdapPersonBuilder(AdvancedLdapPersonBuilder advancedLdapPersonBuilder) {
            super.setAdvancedLdapPersonBuilder(advancedLdapPersonBuilder);
        }

        public void setAdvancedLdapGroupBuilder(AdvancedLdapNestedGroupSearchResultBuilder advancedLdapNestedGroupSearchResultBuilder) {
            super.setAdvancedLdapNestedGroupSearchResultBuilder(advancedLdapNestedGroupSearchResultBuilder);
        }

        public void setAdvancedLdapBindBuilder(AdvancedLdapBindBuilder advancedLdapBindBuilder) {
            super.setAdvancedLdapBindBuilder(advancedLdapBindBuilder);
        }
    }

    @Before
    public void init() {
        this.userManager = Mockito.mock(DefaultUserManager.class);
        this.hibernateAdvancedLdapPluginConfigurationDAO = Mockito.mock(HibernateAdvancedLdapPluginConfigurationDAO.class);
        this.advancedLdapConnector = Mockito.mock(AdvancedLdapConnector.class);
        this.advancedLdapPersonBuilder = Mockito.mock(AdvancedLdapPersonBuilder.class);
        this.advancedLdapNestedGroupBuilder = Mockito.mock(AdvancedLdapNestedGroupBuilder.class);
        this.advancedLdapPersons = new ArrayList<AdvancedLdapPerson>();
        this.advancedLdapPersons2 = new ArrayList<AdvancedLdapPerson>();
        AdvancedLdapPerson advancedLdapPerson = new AdvancedLdapPerson();
        advancedLdapPerson.setDisplayName("David Koudela");
        advancedLdapPerson.setUid("dkoudela");
        advancedLdapPerson.setEmail("dkoudela@example.com");
        this.advancedLdapPersons.add(advancedLdapPerson);
        AdvancedLdapPerson advancedLdapPerson2 = new AdvancedLdapPerson();
        advancedLdapPerson2.setDisplayName("David Koudela");
        advancedLdapPerson2.setUid("dkoudela");
        advancedLdapPerson2.setEmail("dkoudela@example.com");
        ArrayList<AdvancedLdapGroup> advancedLdapGroups = new ArrayList<AdvancedLdapGroup>();
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("group");
        advancedLdapGroup.setDisplayName("Default Group");
        advancedLdapGroups.add(advancedLdapGroup);
        advancedLdapPerson2.setGroupList(advancedLdapGroups);
        this.advancedLdapPersons2.add(advancedLdapPerson2);
        this.advancedLdapGroups = new ArrayList<AdvancedLdapGroup>();
        this.advancedLdapGroups2 = new ArrayList<AdvancedLdapGroup>();
        this.advancedLdapGroups.add(advancedLdapGroup);
        AdvancedLdapGroup advancedLdapGroup2 = new AdvancedLdapGroup();
        advancedLdapGroup2.setGID("group");
        advancedLdapGroup2.setDisplayName("Default Group");
        advancedLdapGroup2.setPersonList(this.advancedLdapPersons);
        this.advancedLdapGroups2.add(advancedLdapGroup2);
        this.advancedLdapBinds = new ArrayList<AdvancedLdapBind>();
        AdvancedLdapBind advancedLdapBind = new AdvancedLdapBind();
        advancedLdapBind.setDn("cn=dkoudela, cn=group, ou=example, dc=com");
        advancedLdapBind.setResult(false);
        this.advancedLdapBinds.add(advancedLdapBind);
        this.advancedLdapBinds2 = new ArrayList<AdvancedLdapBind>();
        AdvancedLdapBind advancedLdapBind2 = new AdvancedLdapBind();
        advancedLdapBind2.setDn("cn=dkoudela, cn=group, ou=example, dc=com");
        advancedLdapBind2.setResult(true);
        this.advancedLdapBinds2.add(advancedLdapBind2);

        this.argumentCaptorSearchRequest = ArgumentCaptor.forClass(SearchRequest.class);
        this.argumentCaptorAdvancedLdapPersonBuilder = ArgumentCaptor.forClass(AdvancedLdapPersonBuilder.class);
        this.argumentCaptorAdvancedLdapGroupBuilder = ArgumentCaptor.forClass(AdvancedLdapGroupBuilder.class);
        this.argumentCaptorGID = ArgumentCaptor.forClass(GroupName.class);
        this.argumentCaptorAdvancedLdapPerson = ArgumentCaptor.forClass(AdvancedLdapPerson.class);
        this.userData = new UserData();
        this.userData.setUserName("dkoudela");
        this.hibernateAdvancedLdapUserDAO = Mockito.mock(HibernateAdvancedLdapUserDAOImpl.class);
        this.advancedLdapBindBuilder = Mockito.mock(AdvancedLdapBindBuilder.class);
        this.advancedLdapLogService = Mockito.mock(AdvancedLdapLogService.class);
        this.groupMembershipManager = Mockito.mock(GroupMembershipManager.class);
        Mockito.doNothing().when(this.advancedLdapLogService).setLogLevel(ArgumentCaptor.forClass(Level.class).capture());
    }

    @Test
    public void testLoadUserNoUrl(){
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManager advancedLdapUserManager = new AdvancedLdapUserManagerImpl(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());

        advancedLdapUserManager.loadUser(new UserData());

        Mockito.verify(this.userManager, Mockito.times(0)).groupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadUserWrongFilter(){
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManager advancedLdapUserManager = new AdvancedLdapUserManagerImpl(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);

        advancedLdapUserManager.loadUser(new UserData());

        Mockito.verify(this.userManager, Mockito.times(0)).groupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadUserNoPersons() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());

        advancedLdapUserManager.loadUser(this.userData);

        Mockito.verify(this.userManager, Mockito.times(0)).groupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadUserOnePersonsWithoutGroups() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");
        advancedLdapUserManager.setAdvancedLdapPersonBuilder(this.advancedLdapPersonBuilder);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.when(this.advancedLdapPersonBuilder.getPersons()).thenReturn(this.advancedLdapPersons);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());

        advancedLdapUserManager.loadUser(this.userData);

        Mockito.verify(this.userManager, Mockito.times(0)).groupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadUserOnePersonsWithOneGroup() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");
        advancedLdapUserManager.setAdvancedLdapPersonBuilder(this.advancedLdapPersonBuilder);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.when(this.advancedLdapPersonBuilder.getPersons()).thenReturn(this.advancedLdapPersons2);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());

        advancedLdapUserManager.loadUser(this.userData);

        Mockito.verify(this.userManager, Mockito.times(1)).groupExists(GroupName.create("group"));
        Mockito.verify(this.userManager, Mockito.times(1)).addGroup(GroupName.create("group"));
        Mockito.verify(this.groupMembershipManager, Mockito.times(1)).isUserInGroup(GroupName.create("group"), "dkoudela");
        Mockito.verify(this.groupMembershipManager, Mockito.times(1)).addUserToGroup(GroupName.create("group"), "dkoudela");
    }



    @Test
    public void testLoadGroupsWrongFilter(){
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManager advancedLdapUserManager = new AdvancedLdapUserManagerImpl(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        AdvancedLdapGroupUserSyncCount advancedLdapGroupUserSyncCount = new AdvancedLdapGroupUserSyncCount();

        advancedLdapUserManager.loadGroups(advancedLdapGroupUserSyncCount);

        Mockito.verify(this.userManager, Mockito.times(0)).groupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadGroupsNoGroups() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setGroupFilterKey("(&(objectCategory=cn=Group*)(sAMAccountName=${USERNAME}))");
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapGroupBuilder.capture());
        AdvancedLdapGroupUserSyncCount advancedLdapGroupUserSyncCount = new AdvancedLdapGroupUserSyncCount();

        advancedLdapUserManager.loadGroups(advancedLdapGroupUserSyncCount);

        Mockito.verify(this.userManager, Mockito.times(0)).groupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadGroupsOneGroupWithoutPersons() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setGroupFilterKey("(&(objectCategory=cn=Group*)(sAMAccountName=${USERNAME}))");
        advancedLdapUserManager.setAdvancedLdapGroupBuilder(this.advancedLdapNestedGroupBuilder);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.when(this.advancedLdapNestedGroupBuilder.getGroups()).thenReturn(this.advancedLdapGroups);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapGroupBuilder.capture());
        AdvancedLdapGroupUserSyncCount advancedLdapGroupUserSyncCount = new AdvancedLdapGroupUserSyncCount();

        advancedLdapUserManager.loadGroups(advancedLdapGroupUserSyncCount);

        Mockito.verify(this.userManager, Mockito.times(1)).groupExists(GroupName.create("group"));
        Mockito.verify(this.userManager, Mockito.times(1)).addGroup(GroupName.create("group"));
        Mockito.verify(this.groupMembershipManager, Mockito.times(0)).isUserInGroup(GroupName.create("group"), "dkoudela");
        Mockito.verify(this.groupMembershipManager, Mockito.times(0)).addUserToGroup(GroupName.create("group"), "dkoudela");
    }

    @Test
    public void testLoadGroupsOneGroupWithOnePerson() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setGroupFilterKey("(&(objectCategory=cn=Group*)(sAMAccountName=${USERNAME}))");
        advancedLdapUserManager.setAdvancedLdapGroupBuilder(this.advancedLdapNestedGroupBuilder);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.when(this.advancedLdapNestedGroupBuilder.getGroups()).thenReturn(this.advancedLdapGroups2);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapGroupBuilder.capture());
        AdvancedLdapGroupUserSyncCount advancedLdapGroupUserSyncCount = new AdvancedLdapGroupUserSyncCount();

        advancedLdapUserManager.loadGroups(advancedLdapGroupUserSyncCount);

        Mockito.verify(this.userManager, Mockito.times(1)).groupExists(GroupName.create("group"));
        Mockito.verify(this.userManager, Mockito.times(1)).addGroup(GroupName.create("group"));
        Mockito.verify(this.hibernateAdvancedLdapUserDAO, Mockito.times(1)).create(ArgumentCaptor.forClass(String.class).capture(),
                ArgumentCaptor.forClass(String.class).capture(), ArgumentCaptor.forClass(String.class).capture());
        Mockito.verify(this.groupMembershipManager, Mockito.times(1)).isUserInGroup(GroupName.create("group"), "dkoudela");
        Mockito.verify(this.groupMembershipManager, Mockito.times(1)).addUserToGroup(GroupName.create("group"), "dkoudela");
    }

    @Test
    public void testLoadGroupsOneGroupWithOnePersonOneDelete() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setGroupFilterKey("(&(objectCategory=cn=Group*)(sAMAccountName=${USERNAME}))");
        advancedLdapPluginConfiguration.setRemovingUsersFromGroupsEnabled(true);
        advancedLdapUserManager.setAdvancedLdapGroupBuilder(this.advancedLdapNestedGroupBuilder);
        List<FecruUser> usersInGroup = new ArrayList<FecruUser>();
        FecruUser user1 = new FecruUser("dkoudela");
        FecruUser user2 = new FecruUser("okoudela");
        usersInGroup.add(user1);
        usersInGroup.add(user2);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.when(this.advancedLdapNestedGroupBuilder.getGroups()).thenReturn(this.advancedLdapGroups2);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapGroupBuilder.capture());
        Mockito.when(this.groupMembershipManager.getUsersInGroup(ArgumentCaptor.forClass(GroupName.class).capture())).thenReturn(usersInGroup);
        AdvancedLdapGroupUserSyncCount advancedLdapGroupUserSyncCount = new AdvancedLdapGroupUserSyncCount();

        advancedLdapUserManager.loadGroups(advancedLdapGroupUserSyncCount);

        Mockito.verify(this.userManager, Mockito.times(1)).groupExists(GroupName.create("group"));
        Mockito.verify(this.userManager, Mockito.times(1)).addGroup(GroupName.create("group"));
        Mockito.verify(this.hibernateAdvancedLdapUserDAO, Mockito.times(1)).create(ArgumentCaptor.forClass(String.class).capture(),
                ArgumentCaptor.forClass(String.class).capture(), ArgumentCaptor.forClass(String.class).capture());
        Mockito.verify(this.groupMembershipManager, Mockito.times(1)).isUserInGroup(GroupName.create("group"), "dkoudela");
        Mockito.verify(this.groupMembershipManager, Mockito.times(1)).addUserToGroup(GroupName.create("group"), "dkoudela");
        Mockito.verify(this.groupMembershipManager, Mockito.times(1)).removeUserFromGroup(GroupName.create("group"), "okoudela");
    }



    @Test
    public void testVerifyUserCredentialsNoUrl() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());

        advancedLdapUserManager.verifyUserCredentials("dkoudela", "pwd");

        Mockito.verify(this.advancedLdapConnector, Mockito.times(0)).ldapPagedSearch(ArgumentCaptor.forClass(SearchRequest.class).capture(),
                ArgumentCaptor.forClass(AdvancedLdapSearchResultBuilder.class).capture());
    }

    @Test
    public void testVerifyUserCredentialsWrongFilter() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);

        advancedLdapUserManager.verifyUserCredentials("dkoudela", "pwd");

        Mockito.verify(this.advancedLdapConnector, Mockito.times(0)).ldapPagedSearch(ArgumentCaptor.forClass(SearchRequest.class).capture(),
                ArgumentCaptor.forClass(AdvancedLdapSearchResultBuilder.class).capture());
    }

    @Test
    public void testVerifyUserCredentialsNoBinds() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());

        assertEquals(false, advancedLdapUserManager.verifyUserCredentials("dkoudela", "pwd"));
    }

    @Test
    public void testVerifyUserCredentialsOneBindResultFalse() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");
        advancedLdapUserManager.setAdvancedLdapBindBuilder(this.advancedLdapBindBuilder);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.when(this.advancedLdapBindBuilder.getBinds()).thenReturn(this.advancedLdapBinds);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());

        assertEquals(false, advancedLdapUserManager.verifyUserCredentials("dkoudela", "pwd"));
    }

    @Test
    public void testVerifyUserCredentialsOneBindResultTrue() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");
        advancedLdapUserManager.setAdvancedLdapBindBuilder(this.advancedLdapBindBuilder);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.when(this.advancedLdapBindBuilder.getBinds()).thenReturn(this.advancedLdapBinds2);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());

        assertEquals(true, advancedLdapUserManager.verifyUserCredentials("dkoudela", "pwd"));
    }

    @Test
    public void testRestoreUserManagerForCoverage() {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);
        advancedLdapUserManager.restoreUserManager(null);
    }


    @Test
    public void testSetGetForCoverage() throws Exception {
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());
        AdvancedLdapUserManager advancedLdapUserManager =
                new AdvancedLdapUserManagerImpl(new UserManagerDummy(), this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO, this.advancedLdapLogService, this.groupMembershipManager);

        try {
            advancedLdapUserManager.reload(null);
            advancedLdapUserManager.validateCurrentUser(null, null);
            advancedLdapUserManager.getCurrentUser((HttpServletRequest) null);
            advancedLdapUserManager.getCurrentUser((JspContext) null);
            advancedLdapUserManager.preCookUrl(null, null, false);
            advancedLdapUserManager.login(null, null, null);
            advancedLdapUserManager.hasUserExceededLoginAttempts(null);
            advancedLdapUserManager.login(null, null, null);
            advancedLdapUserManager.login(null, null, null, null, false);
            advancedLdapUserManager.tryRequestDelegatedLogin(null, null);
            advancedLdapUserManager.createTrustedUserLogin(null, false, false);
            advancedLdapUserManager.createTrustedUserLogin(null);
            advancedLdapUserManager.logout(null, null);
            advancedLdapUserManager.logout2(null, null, false, false);
            advancedLdapUserManager.logout2User(null, false, false);
            advancedLdapUserManager.makeSecureRnd() ;
            advancedLdapUserManager.makeSecureRnd(1) ;
            advancedLdapUserManager.getGroupInfo(null) ;
            advancedLdapUserManager.searchUsers(null, null);
            advancedLdapUserManager.searchGroups(null, null);
            advancedLdapUserManager.renameUser(null, null);
            advancedLdapUserManager.addUser(null, null, null, null, false);
            advancedLdapUserManager.changePassword(null, null);
            advancedLdapUserManager.requestPasswordReset(null, null);
            advancedLdapUserManager.resetPassword(null, null);
            advancedLdapUserManager.updateUser(null, null, null);
            advancedLdapUserManager.ensureGroupExists(null);
            advancedLdapUserManager.getUser(null);
            advancedLdapUserManager.getUserById(1);
            advancedLdapUserManager.getUsernameByEmail(null);
            advancedLdapUserManager.deleteUser(null, false);
            advancedLdapUserManager.deleteUserAndRemoveCommitterMappings(null);
            advancedLdapUserManager.deleteMultipleUsersAndRemoveCommitterMappings(null);
            advancedLdapUserManager.hasPermissionToAccess(null, null);
            advancedLdapUserManager.isCrucibleEnabled(null);
            advancedLdapUserManager.isLoginPossible();
            advancedLdapUserManager.hasSysAdminPrivileges((String)null);
            advancedLdapUserManager.hasSysAdminPrivileges((HttpServletRequest)null);
            advancedLdapUserManager.getUserFor(null);
            advancedLdapUserManager.isValidPassword(null, null);
            advancedLdapUserManager.getAuthenticationProvider();
            advancedLdapUserManager.isUserNameValid(null);
            advancedLdapUserManager.isGroupNameValid(null);
            advancedLdapUserManager.isAdminGroup(null);
            advancedLdapUserManager.setAdminGroup(null, false);

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public class UserManagerDummy implements UserManager {
        @Override
        public void resetFailedLoginAttempts(String s) {

        }

        @Override
        public void reload(ConfigDocument.Config config) throws ConfigException {

        }

        @Override
        public UserLogin validateCurrentUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
            return null;
        }

        @Override
        public UserLogin getCurrentUser(HttpServletRequest httpServletRequest) {
            return null;
        }

        @Override
        public UserLogin getCurrentUser(JspContext jspContext) {
            return null;
        }

        @Override
        public LoginCookie preCookUrl(HttpServletRequest httpServletRequest, String s, boolean b) {
            return null;
        }

        @Override
        public UserLogin login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String s, String s2, boolean b) throws LicensePolicyException {
            return null;
        }

        @Override
        public boolean hasUserExceededLoginAttempts(String s) {
            return false;
        }

        @Override
        public UserLogin login(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginCookieToken loginCookieToken) {
            return null;
        }

        @Override
        public UserLogin tryRequestDelegatedLogin(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws LicensePolicyException {
            return null;
        }

        @Override
        public UserLogin createTrustedUserLogin(String s, boolean b, boolean b2) throws LicensePolicyException {
            return null;
        }

        @Override
        public UserLogin createTrustedUserLogin(String s) throws LicensePolicyException {
            return null;
        }

        @Override
        public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        }

        @Override
        public void logout2(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean b, boolean b2) {

        }

        @Override
        public void logout2User(String s, boolean b, boolean b2) {

        }

        @Override
        public String makeSecureRnd() {
            return null;
        }

        @Override
        public String makeSecureRnd(int i) {
            return null;
        }

        @Override
        public List<String> getAllEnabledUsernames() {
            return null;
        }

        @Override
        public Iterable<String> getAllLicensedUsernames() {
            return null;
        }

        @Override
        public boolean groupExists(GroupName var1) {
            return false;
        }

        @Override
        public Optional<GroupInfo> getGroupInfo(GroupName var1) {
            return null;
        }

        @Override
        public GroupInfo addGroup(GroupName var1) {
            return null;
        }

        @Override
        public void deleteGroup(GroupName var1) {

        }

        @Override
        public List<FecruUser> getLicensedUsers() {
            return null;
        }

        @Override
        public Page<FecruUser> searchUsers(UserSearchCriteria userSearchCriteria, PageRequest pageRequest) {
            return null;
        }

        @Override
        public Page<GroupInfo> searchGroups(GroupSearchCriteria groupSearchCriteria, PageRequest pageRequest) {
            return null;
        }

        @Override
        public void renameUser(String s, String s2) {

        }

        @Override
        public FecruUser addUser(String s, String s1, String s2, String s3, boolean b) throws LicensePolicyException {
            return null;
        }

        @Override
        public void changePassword(String s, String s1) {

        }

        @Override
        public void requestPasswordReset(String s, String s1) {

        }

        @Override
        public void resetPassword(String s, String s1) {

        }

        @Override
        public boolean canUpdateUser(String s) {
            return false;
        }

        @Override
        public void updateUser(String s, String s1, String s2) {

        }

        @Override
        public boolean existsLicensedUser(String s) {
            return false;
        }

        @Override
        public boolean existsEnabledUser(String s) {
            return false;
        }

        @Override
        public Optional<FecruUser> getEnabledUser(String s) {
            return null;
        }

        @Override
        public GroupInfo ensureGroupExists(GroupName var1) throws NotFoundException {
            return null;
        }

        @Override
        public FecruUser getLicensedUser(String s) {
            return null;
        }

        @Override
        public FecruUser getLicensedUser(int i) {
            return null;
        }

        @Override
        public FecruUser getUser(String s) {
            return null;
        }

        @Override
        public FecruUser getUserById(int i) {
            return null;
        }

        @Override
        public String getUsernameByEmail(String s) {
            return null;
        }

        @Override
        public void deleteUserFully(String s) {

        }

        @Override
        public boolean deleteUserAndRemoveCommitterMappings(String s) {
            return false;
        }

        @Override
        public boolean deleteMultipleUsersAndRemoveCommitterMappings(List<String> strings) {
            return false;
        }

        @Override
        public void deleteUser(String s, boolean b) {

        }

        @Override
        public boolean hasPermissionToAccess(Principal principal, RepositoryHandle repositoryHandle) {
            return false;
        }

        @Override
        public boolean isCrucibleEnabled(String s) {
            return false;
        }

        @Override
        public boolean isLoginPossible() {
            return false;
        }

        @Override
        public boolean hasSysAdminPrivileges(String s) {
            return false;
        }

        @Override
        public boolean hasSysAdminPrivileges(HttpServletRequest httpServletRequest) {
            return false;
        }

        @Override
        public FecruUser getUserFor(Principal principal) {
            return null;
        }

        @Override
        public boolean isValidPassword(String s, String s2) {
            return false;
        }

        @Override
        public Auth getAuthenticationProvider() {
            return null;
        }

        @Override
        public boolean isUserNameValid(String s) {
            return false;
        }

        @Override
        public boolean isGroupNameValid(String s) {
            return false;
        }

        @Override
        public boolean isAdminGroup(GroupName var1) {
            return false;
        }

        @Override
        public void setAdminGroup(GroupName var1, boolean b) {

        }

        @Override
        public boolean isPasswordlessAuthenticationEnabled() {
            return false;
        }
    }

}
