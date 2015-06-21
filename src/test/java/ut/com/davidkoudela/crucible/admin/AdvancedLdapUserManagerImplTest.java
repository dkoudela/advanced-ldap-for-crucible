package ut.com.davidkoudela.crucible.admin;

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
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManagerImpl;
import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.config.HibernateAdvancedLdapPluginConfigurationDAO;
import com.davidkoudela.crucible.config.HibernateAdvancedLdapUserDAO;
import com.davidkoudela.crucible.config.HibernateAdvancedLdapUserDAOImpl;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchResultBuilder;
import com.davidkoudela.crucible.ldap.model.*;
import com.persistit.CLI;
import com.unboundid.ldap.sdk.SearchRequest;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private static AdvancedLdapGroupBuilder advancedLdapGroupBuilder = null;
    private static ArrayList<AdvancedLdapPerson> advancedLdapPersons;
    private static ArrayList<AdvancedLdapPerson> advancedLdapPersons2;
    private static ArrayList<AdvancedLdapGroup> advancedLdapGroups;
    private static ArrayList<AdvancedLdapGroup> advancedLdapGroups2;
    private static ArrayList<AdvancedLdapBind> advancedLdapBinds;
    private static ArrayList<AdvancedLdapBind> advancedLdapBinds2;
    private static ArgumentCaptor<SearchRequest> argumentCaptorSearchRequest;
    private static ArgumentCaptor<AdvancedLdapPersonBuilder> argumentCaptorAdvancedLdapPersonBuilder;
    private static ArgumentCaptor<AdvancedLdapGroupBuilder> argumentCaptorAdvancedLdapGroupBuilder;
    private static ArgumentCaptor<String> argumentCaptorGID;
    private static ArgumentCaptor<AdvancedLdapPerson> argumentCaptorAdvancedLdapPerson;
    private static UserData userData;
    private static HibernateAdvancedLdapUserDAO hibernateAdvancedLdapUserDAO;
    private static AdvancedLdapBindBuilder advancedLdapBindBuilder;

    public class AdvancedLdapUserManagerImplDummy extends AdvancedLdapUserManagerImpl {
        public AdvancedLdapUserManagerImplDummy(UserManager userManager,
                                                HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO,
                                                HibernateAdvancedLdapUserDAO hibernateAdvancedLdapUserDAO) {
            super(userManager, hibernateAdvancedLdapPluginConfigurationDAO, hibernateAdvancedLdapUserDAO);
        }

        public void setAdvancedLdapConnector(AdvancedLdapConnector advancedLdapConnector) {
            super.setAdvancedLdapConnector(advancedLdapConnector);
        }

        public void setAdvancedLdapPersonBuilder(AdvancedLdapPersonBuilder advancedLdapPersonBuilder) {
            super.setAdvancedLdapPersonBuilder(advancedLdapPersonBuilder);
        }

        public void setAdvancedLdapGroupBuilder(AdvancedLdapGroupBuilder advancedLdapGroupBuilder) {
            super.setAdvancedLdapGroupBuilder(advancedLdapGroupBuilder);
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
        this.advancedLdapGroupBuilder = Mockito.mock(AdvancedLdapGroupBuilder.class);
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
        this.argumentCaptorGID = ArgumentCaptor.forClass(String.class);
        this.argumentCaptorAdvancedLdapPerson = ArgumentCaptor.forClass(AdvancedLdapPerson.class);
        this.userData = new UserData();
        this.userData.setUserName("dkoudela");
        this.hibernateAdvancedLdapUserDAO = Mockito.mock(HibernateAdvancedLdapUserDAOImpl.class);
        this.advancedLdapBindBuilder = Mockito.mock(AdvancedLdapBindBuilder.class);
    }

    @Test
    public void testLoadUserNoUrl(){
        AdvancedLdapUserManager advancedLdapUserManager = new AdvancedLdapUserManagerImpl(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());

        advancedLdapUserManager.loadUser(new UserData());

        Mockito.verify(this.userManager, Mockito.times(0)).builtInGroupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadUserWrongFilter(){
        AdvancedLdapUserManager advancedLdapUserManager = new AdvancedLdapUserManagerImpl(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);

        advancedLdapUserManager.loadUser(new UserData());

        Mockito.verify(this.userManager, Mockito.times(0)).builtInGroupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadUserNoPersons() {
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());

        advancedLdapUserManager.loadUser(this.userData);

        Mockito.verify(this.userManager, Mockito.times(0)).builtInGroupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadUserOnePersonsWithoutGroups() {
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
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

        Mockito.verify(this.userManager, Mockito.times(0)).builtInGroupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadUserOnePersonsWithOneGroup() {
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
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

        Mockito.verify(this.userManager, Mockito.times(1)).builtInGroupExists("group");
        Mockito.verify(this.userManager, Mockito.times(1)).addBuiltInGroup("group");
        Mockito.verify(this.userManager, Mockito.times(1)).isUserInGroup("group", "dkoudela");
        Mockito.verify(this.userManager, Mockito.times(1)).addUserToBuiltInGroup("group", "dkoudela");
    }



    @Test
    public void testLoadGroupsWrongFilter(){
        AdvancedLdapUserManager advancedLdapUserManager = new AdvancedLdapUserManagerImpl(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);

        advancedLdapUserManager.loadGroups();

        Mockito.verify(this.userManager, Mockito.times(0)).builtInGroupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadGroupsNoGroups() {
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setGroupFilterKey("(&(objectCategory=cn=Group*)(sAMAccountName=${USERNAME}))");
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapGroupBuilder.capture());

        advancedLdapUserManager.loadGroups();

        Mockito.verify(this.userManager, Mockito.times(0)).builtInGroupExists(this.argumentCaptorGID.capture());
    }

    @Test
    public void testLoadGroupsOneGroupWithoutPersons() {
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setGroupFilterKey("(&(objectCategory=cn=Group*)(sAMAccountName=${USERNAME}))");
        advancedLdapUserManager.setAdvancedLdapGroupBuilder(this.advancedLdapGroupBuilder);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.when(this.advancedLdapGroupBuilder.getGroups()).thenReturn(this.advancedLdapGroups);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapGroupBuilder.capture());

        advancedLdapUserManager.loadGroups();

        Mockito.verify(this.userManager, Mockito.times(1)).builtInGroupExists("group");
        Mockito.verify(this.userManager, Mockito.times(1)).addBuiltInGroup("group");
        Mockito.verify(this.userManager, Mockito.times(0)).isUserInGroup("group", "dkoudela");
        Mockito.verify(this.userManager, Mockito.times(0)).addUserToBuiltInGroup("group", "dkoudela");
    }

    @Test
    public void testLoadGroupsOneGroupWithOnePerson() {
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBaseDN("ou=example, dc=com");
        advancedLdapPluginConfiguration.setGroupFilterKey("(&(objectCategory=cn=Group*)(sAMAccountName=${USERNAME}))");
        advancedLdapUserManager.setAdvancedLdapGroupBuilder(this.advancedLdapGroupBuilder);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        Mockito.when(this.advancedLdapGroupBuilder.getGroups()).thenReturn(this.advancedLdapGroups2);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapGroupBuilder.capture());

        advancedLdapUserManager.loadGroups();

        Mockito.verify(this.userManager, Mockito.times(1)).builtInGroupExists("group");
        Mockito.verify(this.userManager, Mockito.times(1)).addBuiltInGroup("group");
        Mockito.verify(this.hibernateAdvancedLdapUserDAO, Mockito.times(1)).create(this.argumentCaptorAdvancedLdapPerson.capture());
        Mockito.verify(this.userManager, Mockito.times(1)).isUserInGroup("group", "dkoudela");
        Mockito.verify(this.userManager, Mockito.times(1)).addUserToBuiltInGroup("group", "dkoudela");
    }



    @Test
    public void testVerifyUserCredentialsNoUrl() {
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(new AdvancedLdapPluginConfiguration());

        advancedLdapUserManager.verifyUserCredentials("dkoudela", "pwd");

        Mockito.verify(this.advancedLdapConnector, Mockito.times(0)).ldapPagedSearch(new ArgumentCaptor<SearchRequest>().capture(),
                new ArgumentCaptor<AdvancedLdapSearchResultBuilder>().capture());
    }

    @Test
    public void testVerifyUserCredentialsWrongFilter() {
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
        advancedLdapUserManager.setAdvancedLdapConnector(this.advancedLdapConnector);
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);

        advancedLdapUserManager.verifyUserCredentials("dkoudela", "pwd");

        Mockito.verify(this.advancedLdapConnector, Mockito.times(0)).ldapPagedSearch(new ArgumentCaptor<SearchRequest>().capture(),
                new ArgumentCaptor<AdvancedLdapSearchResultBuilder>().capture());
    }

    @Test
    public void testVerifyUserCredentialsNoBinds() {
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
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
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
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
        AdvancedLdapUserManagerImplDummy advancedLdapUserManager = new AdvancedLdapUserManagerImplDummy(this.userManager, this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);
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
    public void testSetGetForCoverage() throws Exception {
        AdvancedLdapUserManager advancedLdapUserManager =
                new AdvancedLdapUserManagerImpl(new UserManagerDummy(), this.hibernateAdvancedLdapPluginConfigurationDAO, this.hibernateAdvancedLdapUserDAO);

        try {
            advancedLdapUserManager.countActiveUsers(null);
            advancedLdapUserManager.notifyLicenseUpdate();
            advancedLdapUserManager.reload(null);
            advancedLdapUserManager.validateCurrentUser(null, null);
            advancedLdapUserManager.getCurrentUser((HttpServletRequest) null);
            advancedLdapUserManager.getCurrentUser((JspContext) null);
            advancedLdapUserManager.preCookUrl(null, null, false);
            advancedLdapUserManager.login(null, null, null);
            advancedLdapUserManager.hasUserExceededLoginAttempts(null);
            advancedLdapUserManager.login(null, null, null);
            advancedLdapUserManager.login(null, null, null, null, false);
            advancedLdapUserManager.synchroniseUsers();
            advancedLdapUserManager.synchroniseUsers(false);
            advancedLdapUserManager.tryRequestDelegatedLogin(null, null);
            advancedLdapUserManager.createTrustedUserLogin(null, false, false);
            advancedLdapUserManager.createTrustedUserLogin(null);
            advancedLdapUserManager.logout(null, null);
            advancedLdapUserManager.logout2(null, null, false, false);
            advancedLdapUserManager.logout2User(null, false, false);
            advancedLdapUserManager.makeSecureRnd() ;
            advancedLdapUserManager.makeSecureRnd(1) ;
            advancedLdapUserManager.getAllActiveUsernames();
            advancedLdapUserManager.getAllFishEyeEnabledUsernames();
            advancedLdapUserManager.getAllCrucibleEnabledUsernames();
            advancedLdapUserManager.getUsersWithTextPrefix(null);
            advancedLdapUserManager.getUsersWithTextPrefix(null, null, 1);
            advancedLdapUserManager.getGroupsWithTextPrefix(null, 1, 1);
            advancedLdapUserManager.filterUserByTextPrefix(null, null);
            advancedLdapUserManager.getGroupsForUser(null);
            advancedLdapUserManager.getGroupsForUser(null, null);
            advancedLdapUserManager.isUserInGroup(null, null) ;
            advancedLdapUserManager.getGroupInfos() ;
            advancedLdapUserManager.getGroupInfo(null) ;
            advancedLdapUserManager.getExternalGroupNames() ;
            advancedLdapUserManager.builtInGroupExists(null);
            advancedLdapUserManager.addBuiltInGroup(null);
            advancedLdapUserManager.deleteBuiltInGroup(null);
            advancedLdapUserManager.addUserToBuiltInGroup(null, null);
            advancedLdapUserManager.removeUserFromBuiltInGroup(null, null);
            advancedLdapUserManager.getUsersInGroup(null);
            advancedLdapUserManager.getUsersInGroup(null, null);
            advancedLdapUserManager.getActiveUsers();
            advancedLdapUserManager.searchUsers(null, null);
            advancedLdapUserManager.searchGroups(null, null);
            advancedLdapUserManager.getImplicitUserForCommitter(null);
            advancedLdapUserManager.getFishEyeUsers();
            advancedLdapUserManager.getCrucibleUsers();
            advancedLdapUserManager.getActiveUsersCount();
            advancedLdapUserManager.getFishEyeUsersCount();
            advancedLdapUserManager.getCrucibleUsersCount();
            advancedLdapUserManager.renameUser(null, null);
            advancedLdapUserManager.addUser(null, null);
            advancedLdapUserManager.changePassword(null, null);
            advancedLdapUserManager.requestPasswordReset(null, null);
            advancedLdapUserManager.resetPassword(null, null);
            advancedLdapUserManager.updateUser(null);
            advancedLdapUserManager.userExists(null);
            advancedLdapUserManager.ensureUserExists(null);
            advancedLdapUserManager.ensureGroupExists(null);
            advancedLdapUserManager.getActiveUser(null);
            advancedLdapUserManager.getActiveUser(1);
            advancedLdapUserManager.getUser(null);
            advancedLdapUserManager.getUserById(1);
            advancedLdapUserManager.getUsernameByEmail(null);
            advancedLdapUserManager.deleteUser(null);
            advancedLdapUserManager.deleteUserAndRemoveCommitterMappings(null);
            advancedLdapUserManager.deleteMultipleUsersAndRemoveCommitterMappings(null);
            advancedLdapUserManager.deactivateUser(null);
            advancedLdapUserManager.deactivateUser(null, false);
            advancedLdapUserManager.hasPermissionToAccess(null, null);
            advancedLdapUserManager.isCrucibleEnabled(null);
            advancedLdapUserManager.setCrucibleEnabled(null, false);
            advancedLdapUserManager.isLoginPossible();
            advancedLdapUserManager.hasSysAdminPrivileges((String)null);
            advancedLdapUserManager.hasSysAdminPrivileges((HttpServletRequest)null);
            advancedLdapUserManager.getUserFor(null);
            advancedLdapUserManager.isValidPassword(null, null);
            advancedLdapUserManager.encodePassword(null);
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
        public long countActiveUsers(UserSearchCriteria userSearchCriteria) {
            return 0;
        }

        @Override
        public void notifyLicenseUpdate() {

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
        public void synchroniseUsers() throws Exception {

        }

        @Override
        public void synchroniseUsers(boolean b) throws Exception {

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
        public List<String> getAllActiveUsernames() {
            return null;
        }

        @Override
        public List<String> getAllFishEyeEnabledUsernames() {
            return null;
        }

        @Override
        public List<String> getAllCrucibleEnabledUsernames() {
            return null;
        }

        @Override
        public List<User> getUsersWithTextPrefix(String s) {
            return null;
        }

        @Override
        public List<User> getUsersWithTextPrefix(String s, UserDAO.UserSet userSet, int i) {
            return null;
        }

        @Override
        public Iterable<GroupOfUsers> getGroupsWithTextPrefix(String s, int i, int i2) {
            return null;
        }

        @Override
        public boolean filterUserByTextPrefix(User user, String s) {
            return false;
        }

        @Override
        public List<String> getGroupsForUser(String s) {
            return null;
        }

        @Override
        public Page<String> getGroupsForUser(String s, PageRequest pageRequest) {
            return null;
        }

        @Override
        public boolean isUserInGroup(String s, String s2) {
            return false;
        }

        @Override
        public Map<String, GroupInfo> getGroupInfos() {
            return null;
        }

        @Override
        public GroupInfo getGroupInfo(String s) {
            return null;
        }

        @Override
        public List<String> getExternalGroupNames() {
            return null;
        }

        @Override
        public boolean builtInGroupExists(String s) {
            return false;
        }

        @Override
        public void addBuiltInGroup(String s) {

        }

        @Override
        public void deleteBuiltInGroup(String s) {

        }

        @Override
        public void addUserToBuiltInGroup(String s, String s2) {

        }

        @Override
        public void removeUserFromBuiltInGroup(String s, String s2) {

        }

        @Override
        public List<String> getUsersInGroup(String s) {
            return null;
        }

        @Override
        public Page<String> getUsersInGroup(String s, PageRequest pageRequest) {
            return null;
        }

        @Override
        public List<User> getActiveUsers() {
            return null;
        }

        @Override
        public Page<User> searchUsers(UserSearchCriteria userSearchCriteria, PageRequest pageRequest) {
            return null;
        }

        @Override
        public Page<GroupInfo> searchGroups(GroupSearchCriteria groupSearchCriteria, PageRequest pageRequest) {
            return null;
        }

        @Override
        public User getImplicitUserForCommitter(String s) {
            return null;
        }

        @Override
        public List<User> getFishEyeUsers() {
            return null;
        }

        @Override
        public List<User> getCrucibleUsers() {
            return null;
        }

        @Override
        public int getActiveUsersCount() {
            return 0;
        }

        @Override
        public int getFishEyeUsersCount() {
            return 0;
        }

        @Override
        public int getCrucibleUsersCount() {
            return 0;
        }

        @Override
        public void renameUser(String s, String s2) {

        }

        @Override
        public User addUser(User user, String s) throws LicensePolicyException {
            return null;
        }

        @Override
        public void changePassword(User user, String s) {

        }

        @Override
        public void requestPasswordReset(User user, String s) {

        }

        @Override
        public void resetPassword(User user, String s) {

        }

        @Override
        public void updateUser(User user) {

        }

        @Override
        public boolean userExists(String s) {
            return false;
        }

        @Override
        public User ensureUserExists(String s) throws NotFoundException {
            return null;
        }

        @Override
        public GroupInfo ensureGroupExists(String s) throws NotFoundException {
            return null;
        }

        @Override
        public User getActiveUser(String s) {
            return null;
        }

        @Override
        public User getActiveUser(int i) {
            return null;
        }

        @Override
        public User getUser(String s) {
            return null;
        }

        @Override
        public User getUserById(int i) {
            return null;
        }

        @Override
        public String getUsernameByEmail(String s) {
            return null;
        }

        @Override
        public void deleteUser(User user) {

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
        public void deactivateUser(User user) {

        }

        @Override
        public void deactivateUser(User user, boolean b) {

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
        public void setCrucibleEnabled(String s, boolean b) throws LicenseException {

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
        public User getUserFor(Principal principal) {
            return null;
        }

        @Override
        public boolean isValidPassword(String s, String s2) {
            return false;
        }

        @Override
        public String encodePassword(String s) {
            return null;
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
        public boolean isAdminGroup(String s) {
            return false;
        }

        @Override
        public void setAdminGroup(String s, boolean b) {

        }
    }

}
