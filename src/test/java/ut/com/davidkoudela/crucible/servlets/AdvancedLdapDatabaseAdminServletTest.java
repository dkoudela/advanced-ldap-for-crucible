package ut.com.davidkoudela.crucible.servlets;

import com.atlassian.fisheye.plugin.web.helpers.DefaultVelocityHelper;
import com.atlassian.fisheye.plugin.web.helpers.VelocityHelper;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManagerImpl;
import com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration;
import com.davidkoudela.crucible.persistence.AdvancedLdapDatabaseConfigurationDAO;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapService;
import com.davidkoudela.crucible.servlets.AdvancedLdapDatabaseAdminServlet;
import com.davidkoudela.crucible.tasks.AdvancedLdapSynchronizationManager;
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
 * Description: Testing {@link AdvancedLdapDatabaseAdminServlet}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-11-02
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapDatabaseAdminServletTest extends TestCase {
    private static VelocityHelper velocityHelper;
    private static AdvancedLdapUserManager advancedLdapUserManager;
    private static HibernateAdvancedLdapService hibernateAdvancedLdapService;
    private static AdvancedLdapDatabaseConfigurationDAO advancedLdapDatabaseConfigurationDAO;
    private static AdvancedLdapSynchronizationManager advancedLdapSynchronizationManager;
    private static ArgumentCaptor<String> argumentCaptorString;
    private static ArgumentCaptor<Map<String, Object>> argumentCaptorMap;
    private static ArgumentCaptor<PrintWriter> argumentCaptorPrintWriter;
    private static Class<Map<String, Object>> mapStringObject;

    public class AdvancedLdapDatabaseAdminServletDummy extends AdvancedLdapDatabaseAdminServlet {
        public AdvancedLdapDatabaseAdminServletDummy(VelocityHelper velocityHelper,
                                                     AdvancedLdapUserManager advancedLdapUserManager,
                                                     HibernateAdvancedLdapService hibernateAdvancedLdapService,
                                                     AdvancedLdapDatabaseConfigurationDAO advancedLdapDatabaseConfigurationDAO,
                                                     AdvancedLdapSynchronizationManager advancedLdapSynchronizationManager) {
            super(velocityHelper, advancedLdapUserManager, hibernateAdvancedLdapService, advancedLdapDatabaseConfigurationDAO, advancedLdapSynchronizationManager);
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
        hibernateAdvancedLdapService = Mockito.mock(HibernateAdvancedLdapService.class);
        advancedLdapDatabaseConfigurationDAO = Mockito.mock(AdvancedLdapDatabaseConfigurationDAO.class);
        advancedLdapSynchronizationManager = Mockito.mock(AdvancedLdapSynchronizationManager.class);

        argumentCaptorString = ArgumentCaptor.forClass(String.class);
        argumentCaptorMap = ArgumentCaptor.forClass(mapStringObject);
        argumentCaptorPrintWriter = ArgumentCaptor.forClass(PrintWriter.class);
    }

    @Test
    public void testDoGetWithAdminPermissions() throws IOException, ServletException {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("crucible");
        advancedLdapDatabaseConfiguration.setUserName("sa");
        advancedLdapDatabaseConfiguration.setPassword("pwd");
        Mockito.when(this.advancedLdapDatabaseConfigurationDAO.get()).thenReturn(advancedLdapDatabaseConfiguration);
        Mockito.when(this.advancedLdapUserManager.hasSysAdminPrivileges(ArgumentCaptor.forClass(HttpServletRequest.class).capture())).thenReturn(true);
        Mockito.doNothing().when(this.velocityHelper).renderVelocityTemplate(argumentCaptorString.capture(),
                argumentCaptorMap.capture(), argumentCaptorPrintWriter.capture());

        AdvancedLdapDatabaseAdminServletDummy advancedLdapDatabaseAdminServletDummy = new AdvancedLdapDatabaseAdminServletDummy(velocityHelper, advancedLdapUserManager, hibernateAdvancedLdapService, advancedLdapDatabaseConfigurationDAO, advancedLdapSynchronizationManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapDatabaseAdminServletDummy.doGet(req, resp);

        assertEquals("templates/databaseView.vm", argumentCaptorString.getValue());
        assertEquals(1, argumentCaptorMap.getValue().size());
        assertEquals("[advancedLdapDatabaseConfiguration]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfigurationResult = (AdvancedLdapDatabaseConfiguration) argumentCaptorMap.getValue().get("advancedLdapDatabaseConfiguration");
        assertEquals("{ databaseName=\"crucible\", userName=\"sa\", password=\"pwd\" }", advancedLdapDatabaseConfigurationResult.toString());
    }

    @Test
    public void testDoGetWithoutAdminPermissions() throws ServletException, IOException {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("crucible");
        advancedLdapDatabaseConfiguration.setUserName("sa");
        advancedLdapDatabaseConfiguration.setPassword("pwd");
        Mockito.when(this.advancedLdapDatabaseConfigurationDAO.get()).thenReturn(advancedLdapDatabaseConfiguration);
        Mockito.when(this.advancedLdapUserManager.hasSysAdminPrivileges(ArgumentCaptor.forClass(HttpServletRequest.class).capture())).thenReturn(false);

        AdvancedLdapDatabaseAdminServletDummy advancedLdapDatabaseAdminServletDummy = new AdvancedLdapDatabaseAdminServletDummy(velocityHelper, advancedLdapUserManager, hibernateAdvancedLdapService, advancedLdapDatabaseConfigurationDAO, advancedLdapSynchronizationManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getContextPath()).thenReturn("fecru");
        advancedLdapDatabaseAdminServletDummy.doGet(req, resp);

        Mockito.verify(resp).sendRedirect("fecru/admin/login-default.do");
    }

    @Test
    public void testDoPostWithAdminPermissionsAdminEdit() throws ServletException, IOException {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("crucible");
        advancedLdapDatabaseConfiguration.setUserName("sa");
        advancedLdapDatabaseConfiguration.setPassword("pwd");
        Mockito.when(this.advancedLdapDatabaseConfigurationDAO.get()).thenReturn(advancedLdapDatabaseConfiguration);
        Mockito.when(this.advancedLdapUserManager.hasSysAdminPrivileges(ArgumentCaptor.forClass(HttpServletRequest.class).capture())).thenReturn(true);
        Mockito.doNothing().when(this.velocityHelper).renderVelocityTemplate(argumentCaptorString.capture(),
                argumentCaptorMap.capture(), argumentCaptorPrintWriter.capture());

        AdvancedLdapDatabaseAdminServletDummy advancedLdapDatabaseAdminServletDummy = new AdvancedLdapDatabaseAdminServletDummy(velocityHelper, advancedLdapUserManager, hibernateAdvancedLdapService, advancedLdapDatabaseConfigurationDAO, advancedLdapSynchronizationManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getParameter("dbConfig.dbName")).thenReturn("crucible");
        Mockito.when(req.getParameter("dbConfig.username")).thenReturn("sa");
        Mockito.when(req.getParameter("passwordChanged")).thenReturn("true");
        Mockito.when(req.getParameter("dbConfig.password")).thenReturn("pwd");
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapDatabaseAdminServletEdit.do");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapDatabaseAdminServletDummy.doPost(req, resp);

        assertEquals("templates/databaseEdit.vm", argumentCaptorString.getValue());
        assertEquals(1, argumentCaptorMap.getValue().size());
        assertEquals("[advancedLdapDatabaseConfiguration]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfigurationResult = (AdvancedLdapDatabaseConfiguration) argumentCaptorMap.getValue().get("advancedLdapDatabaseConfiguration");
        assertEquals("{ databaseName=\"crucible\", userName=\"sa\", password=\"pwd\" }", advancedLdapDatabaseConfigurationResult.toString());
    }

    @Test
    public void testDoPostWithAdminPermissionsAdminTest() throws ServletException, IOException {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("crucible");
        advancedLdapDatabaseConfiguration.setUserName("sa");
        advancedLdapDatabaseConfiguration.setPassword("pwd");
        Mockito.when(this.advancedLdapDatabaseConfigurationDAO.get()).thenReturn(advancedLdapDatabaseConfiguration);
        Mockito.when(this.advancedLdapUserManager.hasSysAdminPrivileges(ArgumentCaptor.forClass(HttpServletRequest.class).capture())).thenReturn(true);
        Mockito.doNothing().when(this.velocityHelper).renderVelocityTemplate(argumentCaptorString.capture(),
                argumentCaptorMap.capture(), argumentCaptorPrintWriter.capture());

        AdvancedLdapDatabaseAdminServletDummy advancedLdapDatabaseAdminServletDummy = new AdvancedLdapDatabaseAdminServletDummy(velocityHelper, advancedLdapUserManager, hibernateAdvancedLdapService, advancedLdapDatabaseConfigurationDAO, advancedLdapSynchronizationManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getParameter("dbConfig.dbName")).thenReturn("crucible");
        Mockito.when(req.getParameter("dbConfig.username")).thenReturn("sa");
        Mockito.when(req.getParameter("passwordChanged")).thenReturn("true");
        Mockito.when(req.getParameter("dbConfig.password")).thenReturn("pwd");
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapDatabaseAdminServletTest.do");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapDatabaseAdminServletDummy.doPost(req, resp);

        assertEquals("templates/databaseTest.vm", argumentCaptorString.getValue());
        assertEquals(1, argumentCaptorMap.getValue().size());
        assertEquals("[testResult]", argumentCaptorMap.getValue().keySet().toString());
        Boolean testResult = (Boolean) argumentCaptorMap.getValue().get("testResult");
        assertEquals(false, testResult.booleanValue());
    }

    @Test
    public void testDoPostWithAdminPermissionsAdminRemove() throws ServletException, IOException {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("crucible");
        advancedLdapDatabaseConfiguration.setUserName("sa");
        advancedLdapDatabaseConfiguration.setPassword("pwd");
        Mockito.doNothing().when(this.advancedLdapDatabaseConfigurationDAO).remove();
        Mockito.when(this.advancedLdapDatabaseConfigurationDAO.get()).thenReturn(advancedLdapDatabaseConfiguration);
        Mockito.when(this.advancedLdapUserManager.hasSysAdminPrivileges(ArgumentCaptor.forClass(HttpServletRequest.class).capture())).thenReturn(true);
        Mockito.doNothing().when(this.velocityHelper).renderVelocityTemplate(argumentCaptorString.capture(),
                argumentCaptorMap.capture(), argumentCaptorPrintWriter.capture());

        AdvancedLdapDatabaseAdminServletDummy advancedLdapDatabaseAdminServletDummy = new AdvancedLdapDatabaseAdminServletDummy(velocityHelper, advancedLdapUserManager, hibernateAdvancedLdapService, advancedLdapDatabaseConfigurationDAO, advancedLdapSynchronizationManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getParameter("dbConfig.dbName")).thenReturn("crucible");
        Mockito.when(req.getParameter("dbConfig.username")).thenReturn("sa");
        Mockito.when(req.getParameter("passwordChanged")).thenReturn("true");
        Mockito.when(req.getParameter("dbConfig.password")).thenReturn("pwd");
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapDatabaseAdminServletRemove.do");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapDatabaseAdminServletDummy.doPost(req, resp);

        assertEquals("templates/databaseView.vm", argumentCaptorString.getValue());
        assertEquals(1, argumentCaptorMap.getValue().size());
        assertEquals("[advancedLdapDatabaseConfiguration]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfigurationResult = (AdvancedLdapDatabaseConfiguration) argumentCaptorMap.getValue().get("advancedLdapDatabaseConfiguration");
        assertEquals("{ databaseName=\"crucible\", userName=\"sa\", password=\"pwd\" }", advancedLdapDatabaseConfigurationResult.toString());
    }

    @Test
    public void testDoPostWithAdminPermissionsAdminOther() throws ServletException, IOException {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("crucible");
        advancedLdapDatabaseConfiguration.setUserName("sa");
        advancedLdapDatabaseConfiguration.setPassword("pwd");
        Mockito.doNothing().when(this.advancedLdapDatabaseConfigurationDAO).store(advancedLdapDatabaseConfiguration);
        Mockito.when(this.advancedLdapDatabaseConfigurationDAO.get()).thenReturn(advancedLdapDatabaseConfiguration);
        Mockito.when(this.advancedLdapUserManager.hasSysAdminPrivileges(ArgumentCaptor.forClass(HttpServletRequest.class).capture())).thenReturn(true);
        Mockito.doNothing().when(this.velocityHelper).renderVelocityTemplate(argumentCaptorString.capture(),
                argumentCaptorMap.capture(), argumentCaptorPrintWriter.capture());

        AdvancedLdapDatabaseAdminServletDummy advancedLdapDatabaseAdminServletDummy = new AdvancedLdapDatabaseAdminServletDummy(velocityHelper, advancedLdapUserManager, hibernateAdvancedLdapService, advancedLdapDatabaseConfigurationDAO, advancedLdapSynchronizationManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getParameter("dbConfig.dbName")).thenReturn("crucible");
        Mockito.when(req.getParameter("dbConfig.username")).thenReturn("sa");
        Mockito.when(req.getParameter("passwordChanged")).thenReturn("true");
        Mockito.when(req.getParameter("dbConfig.password")).thenReturn("pwd");
        Mockito.when(req.getPathInfo()).thenReturn("/advancedLdapDatabaseAdminServletOther.do");
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        advancedLdapDatabaseAdminServletDummy.doPost(req, resp);

        assertEquals("templates/databaseView.vm", argumentCaptorString.getValue());
        assertEquals(1, argumentCaptorMap.getValue().size());
        assertEquals("[advancedLdapDatabaseConfiguration]", argumentCaptorMap.getValue().keySet().toString());
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfigurationResult = (AdvancedLdapDatabaseConfiguration) argumentCaptorMap.getValue().get("advancedLdapDatabaseConfiguration");
        assertEquals("{ databaseName=\"crucible\", userName=\"sa\", password=\"pwd\" }", advancedLdapDatabaseConfigurationResult.toString());
    }

    @Test
    public void testDoPostWithoutAdminPermissions() throws ServletException, IOException {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("crucible");
        advancedLdapDatabaseConfiguration.setUserName("sa");
        advancedLdapDatabaseConfiguration.setPassword("pwd");
        Mockito.when(this.advancedLdapDatabaseConfigurationDAO.get()).thenReturn(advancedLdapDatabaseConfiguration);
        Mockito.when(this.advancedLdapUserManager.hasSysAdminPrivileges(ArgumentCaptor.forClass(HttpServletRequest.class).capture())).thenReturn(false);

        AdvancedLdapDatabaseAdminServletDummy advancedLdapDatabaseAdminServletDummy = new AdvancedLdapDatabaseAdminServletDummy(velocityHelper, advancedLdapUserManager, hibernateAdvancedLdapService, advancedLdapDatabaseConfigurationDAO, advancedLdapSynchronizationManager);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getContextPath()).thenReturn("fecru");
        advancedLdapDatabaseAdminServletDummy.doPost(req, resp);

        Mockito.verify(resp).sendRedirect("fecru/admin/login-default.do");
    }
}
