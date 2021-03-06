package ut.com.davidkoudela.crucible.servlets;

import com.atlassian.crucible.spi.FisheyePluginUtilities;
import com.atlassian.fisheye.plugin.web.helpers.DefaultVelocityHelper;
import com.atlassian.fisheye.plugin.web.helpers.VelocityHelper;
import com.cenqua.fisheye.user.DefaultUserManager;
import com.cenqua.fisheye.user.UserManager;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManagerImpl;
import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.logs.AdvancedLdapLogService;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapPluginConfigurationDAO;
import com.davidkoudela.crucible.servlets.AdvancedLdapConfigurationAdminServlet;
import com.davidkoudela.crucible.statistics.AdvancedLdapGroupUserSyncCount;
import com.davidkoudela.crucible.tasks.AdvancedLdapSynchronizationManager;
import com.davidkoudela.crucible.tasks.AdvancedLdapSynchronizationManagerImpl;
import com.davidkoudela.crucible.timer.AdvancedLdapTimerTrigger;
import junit.framework.TestCase;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Description: Testing {@link AdvancedLdapConfigurationAdminServlet}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-07
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapConfigurationAdminServletTest extends TestCase {
    private static VelocityHelper velocityHelper;
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private static HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO;
    private static AdvancedLdapTimerTrigger advancedLdapTimerTrigger;
    private static UserManager userManager;
    private static AdvancedLdapUserManager advancedLdapUserManager;
    private static ArgumentCaptor<HttpServletRequest> argumentCaptorHttpServletRequest;
    private static ArgumentCaptor<String> argumentCaptorString;
    private static ArgumentCaptor<Map<String, Object>> argumentCaptorMap;
    private static ArgumentCaptor<PrintWriter> argumentCaptorPrintWriter;
    private static AdvancedLdapSynchronizationManager advancedLdapSynchronizationManager;
    private static FisheyePluginUtilities fisheyePluginUtilities;
    private static AdvancedLdapLogService advancedLdapLogService;
    private static Class<Map<String, Object>> mapStringObject;

    public class AdvancedLdapConfigurationAdminServletDummy extends AdvancedLdapConfigurationAdminServlet {
        public AdvancedLdapConfigurationAdminServletDummy(VelocityHelper velocityHelper, HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO,
                                                          AdvancedLdapSynchronizationManager advancedLdapSynchronizationManager, AdvancedLdapUserManager advancedLdapUserManager,
                                                          FisheyePluginUtilities fisheyePluginUtilities, AdvancedLdapLogService advancedLdapLogService) {
            super(velocityHelper, hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapSynchronizationManager, advancedLdapUserManager, fisheyePluginUtilities, advancedLdapLogService);
        }

        public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
        }

        public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doPost(req, resp);
        }
    }

    @Before
    public void init() throws IOException {
        velocityHelper = Mockito.mock(DefaultVelocityHelper.class);
        advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        hibernateAdvancedLdapPluginConfigurationDAO = Mockito.mock(HibernateAdvancedLdapPluginConfigurationDAO.class);
        advancedLdapTimerTrigger = new AdvancedLdapTimerTrigger();
        userManager = Mockito.mock(DefaultUserManager.class);
        advancedLdapUserManager = Mockito.mock(AdvancedLdapUserManagerImpl.class);
        fisheyePluginUtilities = Mockito.mock(FisheyePluginUtilities.class);
        advancedLdapLogService = Mockito.mock(AdvancedLdapLogService.class);

        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        argumentCaptorHttpServletRequest = ArgumentCaptor.forClass(HttpServletRequest.class);
        argumentCaptorString = ArgumentCaptor.forClass(String.class);
        argumentCaptorMap = ArgumentCaptor.forClass(mapStringObject);
        argumentCaptorPrintWriter = ArgumentCaptor.forClass(PrintWriter.class);
        Mockito.doNothing().when(velocityHelper).renderVelocityTemplate(argumentCaptorString.capture(), argumentCaptorMap.capture(), argumentCaptorPrintWriter.capture());
        Mockito.doNothing().when(advancedLdapUserManager).loadGroups(ArgumentCaptor.forClass(AdvancedLdapGroupUserSyncCount.class).capture());
        Mockito.doNothing().when(advancedLdapLogService).setLogLevel(ArgumentCaptor.forClass(Level.class).capture());

        advancedLdapSynchronizationManager =
                new AdvancedLdapSynchronizationManagerImpl(hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapTimerTrigger, advancedLdapUserManager);
    }

    @Test
    public void testDoGetWithAdminPermissions() throws ServletException, IOException {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);

        AdvancedLdapConfigurationAdminServletDummy advancedLdapConfigurationAdminServlet = new AdvancedLdapConfigurationAdminServletDummy(
                velocityHelper, hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapSynchronizationManager, advancedLdapUserManager, fisheyePluginUtilities, advancedLdapLogService);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationAdminServlet.doGet(req, resp);

        assertEquals("templates/configureView.vm", argumentCaptorString.getValue());
        assertEquals(4, argumentCaptorMap.getValue().size());
        assertEquals("[request, advancedLdapPluginConfiguration, webResourceManager, STATICDIR]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapPluginConfiguration advancedLdapPluginConfigurationParam = (AdvancedLdapPluginConfiguration) argumentCaptorMap.getValue().get("advancedLdapPluginConfiguration");
        assertEquals("{ id=\"0\", connectTimeoutMillis=\"10000\", responseTimeoutMillis=\"10000\", LDAPPageSize=\"99\", LDAPSyncPeriod=\"3600\", LDAPUrl=\"url\", LDAPBindDN=\"\", LDAPBindPassword=\"\", LDAPBaseDN=\"\", userFilterKey=\"\", displayNameAttributeKey=\"\", emailAttributeKey=\"\", UIDAttributeKey=\"\", userGroupNamesKey=\"\", groupFilterKey=\"\", GIDAttributeKey=\"\", groupDisplayNameKey=\"\", userNamesKey=\"\", nestedGroupsEnabled=\"false\", logLevel=\"INFO\", removingUsersFromGroupsEnabled=\"false\" }", advancedLdapPluginConfigurationParam.toString());
    }

    @Test
    public void testDoGetWithoutAdminPermissions() throws ServletException, IOException {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(false);

        AdvancedLdapConfigurationAdminServletDummy advancedLdapConfigurationAdminServlet = new AdvancedLdapConfigurationAdminServletDummy(
                velocityHelper, hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapSynchronizationManager, advancedLdapUserManager, fisheyePluginUtilities, advancedLdapLogService);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getContextPath()).thenReturn("fecru");
        advancedLdapConfigurationAdminServlet.doGet(req, resp);

        Mockito.verify(resp).sendRedirect("fecru/admin/login-default.do");
    }

    @Test
    public void testDoPostWithAdminPermissionsAdminEdit() throws ServletException, IOException {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);

        AdvancedLdapConfigurationAdminServletDummy advancedLdapConfigurationAdminServlet = new AdvancedLdapConfigurationAdminServletDummy(
                velocityHelper, hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapSynchronizationManager, advancedLdapUserManager, fisheyePluginUtilities, advancedLdapLogService);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapConfigurationAdminServletEdit.do");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationAdminServlet.doPost(req, resp);

        assertEquals("templates/configureEdit.vm", argumentCaptorString.getValue());
        assertEquals(4, argumentCaptorMap.getValue().size());
        assertEquals("[request, advancedLdapPluginConfiguration, webResourceManager, STATICDIR]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapPluginConfiguration advancedLdapPluginConfigurationParam = (AdvancedLdapPluginConfiguration) argumentCaptorMap.getValue().get("advancedLdapPluginConfiguration");
        assertEquals("{ id=\"0\", connectTimeoutMillis=\"10000\", responseTimeoutMillis=\"10000\", LDAPPageSize=\"99\", LDAPSyncPeriod=\"3600\", LDAPUrl=\"url\", LDAPBindDN=\"\", LDAPBindPassword=\"\", LDAPBaseDN=\"\", userFilterKey=\"\", displayNameAttributeKey=\"\", emailAttributeKey=\"\", UIDAttributeKey=\"\", userGroupNamesKey=\"\", groupFilterKey=\"\", GIDAttributeKey=\"\", groupDisplayNameKey=\"\", userNamesKey=\"\", nestedGroupsEnabled=\"false\", logLevel=\"INFO\", removingUsersFromGroupsEnabled=\"false\" }", advancedLdapPluginConfigurationParam.toString());
    }

    @Test
    public void testDoPostWithAdminPermissionsAdminRemove() throws Exception {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);
        Mockito.doNothing().when(hibernateAdvancedLdapPluginConfigurationDAO).remove(0);

        AdvancedLdapConfigurationAdminServletDummy advancedLdapConfigurationAdminServlet = new AdvancedLdapConfigurationAdminServletDummy(
                velocityHelper, hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapSynchronizationManager, advancedLdapUserManager, fisheyePluginUtilities, advancedLdapLogService);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapConfigurationAdminServletRemove.do");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationAdminServlet.doPost(req, resp);

        assertEquals("templates/configureView.vm", argumentCaptorString.getValue());
        assertEquals(4, argumentCaptorMap.getValue().size());
        assertEquals("[request, advancedLdapPluginConfiguration, webResourceManager, STATICDIR]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapPluginConfiguration advancedLdapPluginConfigurationParam = (AdvancedLdapPluginConfiguration) argumentCaptorMap.getValue().get("advancedLdapPluginConfiguration");
        assertEquals("{ id=\"0\", connectTimeoutMillis=\"10000\", responseTimeoutMillis=\"10000\", LDAPPageSize=\"99\", LDAPSyncPeriod=\"3600\", LDAPUrl=\"url\", LDAPBindDN=\"\", LDAPBindPassword=\"\", LDAPBaseDN=\"\", userFilterKey=\"\", displayNameAttributeKey=\"\", emailAttributeKey=\"\", UIDAttributeKey=\"\", userGroupNamesKey=\"\", groupFilterKey=\"\", GIDAttributeKey=\"\", groupDisplayNameKey=\"\", userNamesKey=\"\", nestedGroupsEnabled=\"false\", logLevel=\"INFO\", removingUsersFromGroupsEnabled=\"false\" }", advancedLdapPluginConfigurationParam.toString());
    }

    @Test
    public void testDoPostWithAdminPermissionsAdminSync() throws ServletException, IOException {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);

        AdvancedLdapConfigurationAdminServletDummy advancedLdapConfigurationAdminServlet = new AdvancedLdapConfigurationAdminServletDummy(
                velocityHelper, hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapSynchronizationManager, advancedLdapUserManager, fisheyePluginUtilities, advancedLdapLogService);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapConfigurationAdminServletSync.do");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationAdminServlet.doPost(req, resp);

        assertEquals("templates/configureView.vm", argumentCaptorString.getValue());
        assertEquals(4, argumentCaptorMap.getValue().size());
        assertEquals("[request, advancedLdapPluginConfiguration, webResourceManager, STATICDIR]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapPluginConfiguration advancedLdapPluginConfigurationParam = (AdvancedLdapPluginConfiguration) argumentCaptorMap.getValue().get("advancedLdapPluginConfiguration");
        assertEquals("{ id=\"0\", connectTimeoutMillis=\"10000\", responseTimeoutMillis=\"10000\", LDAPPageSize=\"99\", LDAPSyncPeriod=\"3600\", LDAPUrl=\"url\", LDAPBindDN=\"\", LDAPBindPassword=\"\", LDAPBaseDN=\"\", userFilterKey=\"\", displayNameAttributeKey=\"\", emailAttributeKey=\"\", UIDAttributeKey=\"\", userGroupNamesKey=\"\", groupFilterKey=\"\", GIDAttributeKey=\"\", groupDisplayNameKey=\"\", userNamesKey=\"\", nestedGroupsEnabled=\"false\", logLevel=\"INFO\", removingUsersFromGroupsEnabled=\"false\" }", advancedLdapPluginConfigurationParam.toString());
    }

    @Test
    public void testDoPostWithAdminPermissionsAdminOther() throws Exception {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);
        Mockito.doNothing().when(hibernateAdvancedLdapPluginConfigurationDAO).store(advancedLdapPluginConfiguration, true);

        AdvancedLdapConfigurationAdminServletDummy advancedLdapConfigurationAdminServlet = new AdvancedLdapConfigurationAdminServletDummy(
                velocityHelper, hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapSynchronizationManager, advancedLdapUserManager, fisheyePluginUtilities, advancedLdapLogService);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapConfigurationAdminServletOther.do");
        Mockito.when(req.getParameter("ldap.url")).thenReturn("url");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationAdminServlet.doPost(req, resp);

        assertEquals("templates/configureView.vm", argumentCaptorString.getValue());
        assertEquals(4, argumentCaptorMap.getValue().size());
        assertEquals("[request, advancedLdapPluginConfiguration, webResourceManager, STATICDIR]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapPluginConfiguration advancedLdapPluginConfigurationParam = (AdvancedLdapPluginConfiguration) argumentCaptorMap.getValue().get("advancedLdapPluginConfiguration");
        assertEquals("{ id=\"0\", connectTimeoutMillis=\"10000\", responseTimeoutMillis=\"10000\", LDAPPageSize=\"99\", LDAPSyncPeriod=\"3600\", LDAPUrl=\"url\", LDAPBindDN=\"\", LDAPBindPassword=\"\", LDAPBaseDN=\"\", userFilterKey=\"\", displayNameAttributeKey=\"\", emailAttributeKey=\"\", UIDAttributeKey=\"\", userGroupNamesKey=\"\", groupFilterKey=\"\", GIDAttributeKey=\"\", groupDisplayNameKey=\"\", userNamesKey=\"\", nestedGroupsEnabled=\"false\", logLevel=\"INFO\", removingUsersFromGroupsEnabled=\"false\" }", advancedLdapPluginConfigurationParam.toString());
    }

    @Test
    public void testDoPostWithoutAdminPermissions() throws ServletException, IOException {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(false);

        AdvancedLdapConfigurationAdminServletDummy advancedLdapConfigurationAdminServlet = new AdvancedLdapConfigurationAdminServletDummy(
                velocityHelper, hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapSynchronizationManager, advancedLdapUserManager, fisheyePluginUtilities, advancedLdapLogService);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getContextPath()).thenReturn("fecru");
        advancedLdapConfigurationAdminServlet.doPost(req, resp);

        Mockito.verify(resp).sendRedirect("fecru/admin/login-default.do");
    }

    @Test
    public void testDoPostWithAdminPermissionsRemoveThrowsException() throws Exception {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);
        Mockito.doThrow(new Exception()).when(hibernateAdvancedLdapPluginConfigurationDAO).remove(0);

        AdvancedLdapConfigurationAdminServletDummy advancedLdapConfigurationAdminServlet = new AdvancedLdapConfigurationAdminServletDummy(
                velocityHelper, hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapSynchronizationManager, advancedLdapUserManager, fisheyePluginUtilities, advancedLdapLogService);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapConfigurationAdminServletRemove.do");
        Mockito.when(req.getParameter("ldap.url")).thenReturn("url");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationAdminServlet.doPost(req, resp);

        assertEquals("templates/configureView.vm", argumentCaptorString.getValue());
        assertEquals(4, argumentCaptorMap.getValue().size());
        assertEquals("[request, advancedLdapPluginConfiguration, webResourceManager, STATICDIR]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapPluginConfiguration advancedLdapPluginConfigurationParam = (AdvancedLdapPluginConfiguration) argumentCaptorMap.getValue().get("advancedLdapPluginConfiguration");
        assertEquals("{ id=\"0\", connectTimeoutMillis=\"10000\", responseTimeoutMillis=\"10000\", LDAPPageSize=\"99\", LDAPSyncPeriod=\"3600\", LDAPUrl=\"url\", LDAPBindDN=\"\", LDAPBindPassword=\"\", LDAPBaseDN=\"\", userFilterKey=\"\", displayNameAttributeKey=\"\", emailAttributeKey=\"\", UIDAttributeKey=\"\", userGroupNamesKey=\"\", groupFilterKey=\"\", GIDAttributeKey=\"\", groupDisplayNameKey=\"\", userNamesKey=\"\", nestedGroupsEnabled=\"false\", logLevel=\"INFO\", removingUsersFromGroupsEnabled=\"false\" }", advancedLdapPluginConfigurationParam.toString());
    }

    @Test
    public void testDoPostWithAdminPermissionsSyncThrowsException() throws Exception {
        AdvancedLdapSynchronizationManager advancedLdapSynchronizationManagerMock = Mockito.mock(AdvancedLdapSynchronizationManager.class);
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);
        Mockito.doThrow(new Exception()).when(advancedLdapSynchronizationManagerMock).runNow();

        AdvancedLdapConfigurationAdminServletDummy advancedLdapConfigurationAdminServlet = new AdvancedLdapConfigurationAdminServletDummy(
                velocityHelper, hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapSynchronizationManagerMock, advancedLdapUserManager, fisheyePluginUtilities, advancedLdapLogService);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapConfigurationAdminServletSync.do");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationAdminServlet.doPost(req, resp);

        assertEquals("templates/configureView.vm", argumentCaptorString.getValue());
        assertEquals(4, argumentCaptorMap.getValue().size());
        assertEquals("[request, advancedLdapPluginConfiguration, webResourceManager, STATICDIR]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapPluginConfiguration advancedLdapPluginConfigurationParam = (AdvancedLdapPluginConfiguration) argumentCaptorMap.getValue().get("advancedLdapPluginConfiguration");
        assertEquals("{ id=\"0\", connectTimeoutMillis=\"10000\", responseTimeoutMillis=\"10000\", LDAPPageSize=\"99\", LDAPSyncPeriod=\"3600\", LDAPUrl=\"url\", LDAPBindDN=\"\", LDAPBindPassword=\"\", LDAPBaseDN=\"\", userFilterKey=\"\", displayNameAttributeKey=\"\", emailAttributeKey=\"\", UIDAttributeKey=\"\", userGroupNamesKey=\"\", groupFilterKey=\"\", GIDAttributeKey=\"\", groupDisplayNameKey=\"\", userNamesKey=\"\", nestedGroupsEnabled=\"false\", logLevel=\"INFO\", removingUsersFromGroupsEnabled=\"false\" }", advancedLdapPluginConfigurationParam.toString());
    }

    @Test
    public void testDoPostWithAdminPermissionsAdminOtherThrowsException() throws Exception {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);
        Mockito.doThrow(new Exception()).when(hibernateAdvancedLdapPluginConfigurationDAO).store(advancedLdapPluginConfiguration, true);

        AdvancedLdapConfigurationAdminServletDummy advancedLdapConfigurationAdminServlet = new AdvancedLdapConfigurationAdminServletDummy(
                velocityHelper, hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapSynchronizationManager, advancedLdapUserManager, fisheyePluginUtilities, advancedLdapLogService);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapConfigurationAdminServletOther.do");
        Mockito.when(req.getParameter("ldap.url")).thenReturn("url");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationAdminServlet.doPost(req, resp);

        assertEquals("templates/configureView.vm", argumentCaptorString.getValue());
        assertEquals(4, argumentCaptorMap.getValue().size());
        assertEquals("[request, advancedLdapPluginConfiguration, webResourceManager, STATICDIR]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapPluginConfiguration advancedLdapPluginConfigurationParam = (AdvancedLdapPluginConfiguration) argumentCaptorMap.getValue().get("advancedLdapPluginConfiguration");
        assertEquals("{ id=\"0\", connectTimeoutMillis=\"10000\", responseTimeoutMillis=\"10000\", LDAPPageSize=\"99\", LDAPSyncPeriod=\"3600\", LDAPUrl=\"url\", LDAPBindDN=\"\", LDAPBindPassword=\"\", LDAPBaseDN=\"\", userFilterKey=\"\", displayNameAttributeKey=\"\", emailAttributeKey=\"\", UIDAttributeKey=\"\", userGroupNamesKey=\"\", groupFilterKey=\"\", GIDAttributeKey=\"\", groupDisplayNameKey=\"\", userNamesKey=\"\", nestedGroupsEnabled=\"false\", logLevel=\"INFO\", removingUsersFromGroupsEnabled=\"false\" }", advancedLdapPluginConfigurationParam.toString());
    }
}
