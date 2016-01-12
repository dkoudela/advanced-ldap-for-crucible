package ut.com.davidkoudela.crucible.servlets;

import com.atlassian.fisheye.plugin.web.helpers.DefaultVelocityHelper;
import com.atlassian.fisheye.plugin.web.helpers.VelocityHelper;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManagerImpl;
import com.davidkoudela.crucible.servlets.AdvancedLdapConfigurationTestServlet;
import com.davidkoudela.crucible.statistics.AdvancedLdapGroupUserSyncCount;
import junit.framework.TestCase;
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
 * Description: Testing {@link AdvancedLdapConfigurationTestServlet}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-08
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapConfigurationTestServletTest extends TestCase {
    private static VelocityHelper velocityHelper;
    private static AdvancedLdapUserManager advancedLdapUserManager;
    private static ArgumentCaptor<HttpServletRequest> argumentCaptorHttpServletRequest;
    private static ArgumentCaptor<String> argumentCaptorString;
    private static ArgumentCaptor<Map<String, Object>> argumentCaptorMap;
    private static ArgumentCaptor<PrintWriter> argumentCaptorPrintWriter;
    private static Class<Map<String, Object>> mapStringObject;

    public class AdvancedLdapConfigurationTestServletDummy extends AdvancedLdapConfigurationTestServlet {
        public AdvancedLdapConfigurationTestServletDummy(VelocityHelper velocityHelper, AdvancedLdapUserManager advancedLdapUserManager) {
            super(velocityHelper, advancedLdapUserManager);
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
        advancedLdapUserManager = Mockito.mock(AdvancedLdapUserManagerImpl.class);

        argumentCaptorHttpServletRequest = ArgumentCaptor.forClass(HttpServletRequest.class);
        argumentCaptorString = ArgumentCaptor.forClass(String.class);
        argumentCaptorMap = ArgumentCaptor.forClass(mapStringObject);
        argumentCaptorPrintWriter = ArgumentCaptor.forClass(PrintWriter.class);
        Mockito.doNothing().when(velocityHelper).renderVelocityTemplate(argumentCaptorString.capture(), argumentCaptorMap.capture(), argumentCaptorPrintWriter.capture());
        Mockito.doNothing().when(advancedLdapUserManager).loadGroups(ArgumentCaptor.forClass(AdvancedLdapGroupUserSyncCount.class).capture());
    }

    @Test
    public void testDoGetWithAdminPermissions() throws ServletException, IOException {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);

        AdvancedLdapConfigurationTestServletDummy advancedLdapConfigurationTestServletDummy = new AdvancedLdapConfigurationTestServletDummy(
                velocityHelper, advancedLdapUserManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationTestServletDummy.doGet(req, resp);

        assertEquals("templates/authTest.vm", argumentCaptorString.getValue());
        assertEquals(0, argumentCaptorMap.getValue().size());
    }

    @Test
    public void testDoGetWithoutAdminPermissions() throws ServletException, IOException {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(false);

        AdvancedLdapConfigurationTestServletDummy advancedLdapConfigurationTestServletDummy = new AdvancedLdapConfigurationTestServletDummy(
                velocityHelper, advancedLdapUserManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getContextPath()).thenReturn("fecru");
        advancedLdapConfigurationTestServletDummy.doGet(req, resp);

        Mockito.verify(resp).sendRedirect("fecru/admin/login-default.do");
    }

    @Test
    public void testDoPostWithAdminPermissionsTestSuccessResults() throws ServletException, IOException {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);
        Mockito.when(advancedLdapUserManager.verifyUserCredentials("", "")).thenReturn(true);

        AdvancedLdapConfigurationTestServletDummy advancedLdapConfigurationTestServletDummy = new AdvancedLdapConfigurationTestServletDummy(
                velocityHelper, advancedLdapUserManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapConfigurationTestServletTestResults.do");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationTestServletDummy.doPost(req, resp);

        assertEquals("templates/authResult.vm", argumentCaptorString.getValue());
        assertEquals(1, argumentCaptorMap.getValue().size());
        assertEquals("[testResult]", argumentCaptorMap.getValue().keySet().toString());
        String testResult = (String) argumentCaptorMap.getValue().get("testResult");
        assertEquals("username/password succeeded", testResult);
    }

    @Test
    public void testDoPostWithAdminPermissionsTestFailedResults() throws ServletException, IOException {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);
        Mockito.when(advancedLdapUserManager.verifyUserCredentials("", "")).thenReturn(false);

        AdvancedLdapConfigurationTestServletDummy advancedLdapConfigurationTestServletDummy = new AdvancedLdapConfigurationTestServletDummy(
                velocityHelper, advancedLdapUserManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapConfigurationTestServletTestResults.do");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationTestServletDummy.doPost(req, resp);

        assertEquals("templates/authResult.vm", argumentCaptorString.getValue());
        assertEquals(1, argumentCaptorMap.getValue().size());
        assertEquals("[testResult]", argumentCaptorMap.getValue().keySet().toString());
        String testResult = (String) argumentCaptorMap.getValue().get("testResult");
        assertEquals("username/password failed", testResult);
    }

    @Test
    public void testDoPostWithAdminPermissionsTestOther() throws ServletException, IOException {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(true);
        Mockito.when(advancedLdapUserManager.verifyUserCredentials("", "")).thenReturn(true);

        AdvancedLdapConfigurationTestServletDummy advancedLdapConfigurationTestServletDummy = new AdvancedLdapConfigurationTestServletDummy(
                velocityHelper, advancedLdapUserManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapConfigurationTestServletTestOther.do");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapConfigurationTestServletDummy.doPost(req, resp);

        assertEquals("templates/authTest.vm", argumentCaptorString.getValue());
        assertEquals(0, argumentCaptorMap.getValue().size());
    }

    @Test
    public void testDoPostWithoutAdminPermissions() throws ServletException, IOException {
        Mockito.when(advancedLdapUserManager.hasSysAdminPrivileges(argumentCaptorHttpServletRequest.capture())).thenReturn(false);

        AdvancedLdapConfigurationTestServletDummy advancedLdapConfigurationTestServletDummy = new AdvancedLdapConfigurationTestServletDummy(
                velocityHelper, advancedLdapUserManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getContextPath()).thenReturn("fecru");
        advancedLdapConfigurationTestServletDummy.doPost(req, resp);

        Mockito.verify(resp).sendRedirect("fecru/admin/login-default.do");
    }
}
