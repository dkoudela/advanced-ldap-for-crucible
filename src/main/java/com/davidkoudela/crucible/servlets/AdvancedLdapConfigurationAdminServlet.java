package com.davidkoudela.crucible.servlets;

import com.atlassian.crucible.spi.FisheyePluginUtilities;
import com.atlassian.fisheye.plugin.web.helpers.VelocityHelper;
import com.cenqua.fisheye.web.tags.WebResourceManagerUtil;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.logs.AdvancedLdapLogService;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapPluginConfigurationDAO;
import com.davidkoudela.crucible.tasks.AdvancedLdapSynchronizationManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: Configuration servlet class reacting on HTTP GET and POST requests related to
 *              configuration administration actions.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-24
 */
public class AdvancedLdapConfigurationAdminServlet extends HttpServlet {
    private Logger log = Logger.getLogger(this.getClass());
    private final VelocityHelper velocityHelper;
    private HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO;
    private AdvancedLdapSynchronizationManager advancedLdapSynchronizationManager;
    private AdvancedLdapUserManager advancedLdapUserManager;
    private FisheyePluginUtilities fisheyePluginUtilities;
    private AdvancedLdapLogService advancedLdapLogService;

    @org.springframework.beans.factory.annotation.Autowired
    public AdvancedLdapConfigurationAdminServlet(VelocityHelper velocityHelper,
                                                 HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO,
                                                 AdvancedLdapSynchronizationManager advancedLdapSynchronizationManager,
                                                 AdvancedLdapUserManager advancedLdapUserManager,
                                                 FisheyePluginUtilities fisheyePluginUtilities,
                                                 AdvancedLdapLogService advancedLdapLogService) {
        this.velocityHelper = velocityHelper;
        this.hibernateAdvancedLdapPluginConfigurationDAO = hibernateAdvancedLdapPluginConfigurationDAO;
        this.advancedLdapSynchronizationManager = advancedLdapSynchronizationManager;
        this.advancedLdapUserManager = advancedLdapUserManager;
        this.fisheyePluginUtilities = fisheyePluginUtilities;
        this.advancedLdapLogService = advancedLdapLogService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> params = new HashMap<String, Object>();
        req.setAttribute("decorator", "atl.admin");
        resp.setContentType("text/html");
        if (advancedLdapUserManager.hasSysAdminPrivileges(req)) {
            AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = this.hibernateAdvancedLdapPluginConfigurationDAO.get();

            params.put("advancedLdapPluginConfiguration", advancedLdapPluginConfiguration);
            params.put("request", req);
            params.put("STATICDIR", this.fisheyePluginUtilities.getStaticDir());
            params.put("webResourceManager", WebResourceManagerUtil.class);
            velocityHelper.renderVelocityTemplate("templates/configureView.vm", params, resp.getWriter());
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/login-default.do");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String,Object> params = new HashMap<String,Object>();
        req.setAttribute("decorator", "atl.admin");
        resp.setContentType("text/html");
        params.put("request", req);
        params.put("STATICDIR", this.fisheyePluginUtilities.getStaticDir());
        params.put("webResourceManager", WebResourceManagerUtil.class);
        if (advancedLdapUserManager.hasSysAdminPrivileges(req)) {
            AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = this.hibernateAdvancedLdapPluginConfigurationDAO.get();
            if (req.getPathInfo().contains("/advancedLdapConfigurationAdminServletEdit.do")) {
                params.put("advancedLdapPluginConfiguration", advancedLdapPluginConfiguration);
                velocityHelper.renderVelocityTemplate("templates/configureEdit.vm", params, resp.getWriter());
            } else if (req.getPathInfo().contains("/advancedLdapConfigurationAdminServletRemove.do")) {
                try {
                    this.hibernateAdvancedLdapPluginConfigurationDAO.remove(advancedLdapPluginConfiguration.getId());
                    this.advancedLdapSynchronizationManager.updateTimer();
                } catch (Exception e) {
                    log.warn("AdvancedLdapConfigurationAdminServlet.doPost: hibernateAdvancedLdapPluginConfigurationDAO.remove failed: " + e);
                }
                advancedLdapPluginConfiguration = this.hibernateAdvancedLdapPluginConfigurationDAO.get();
                params.put("advancedLdapPluginConfiguration", advancedLdapPluginConfiguration);
                velocityHelper.renderVelocityTemplate("templates/configureView.vm", params, resp.getWriter());
            } else if (req.getPathInfo().contains("/advancedLdapConfigurationAdminServletSync.do")) {
                try {
                    this.advancedLdapSynchronizationManager.runNow();
                } catch (Exception e) {
                    log.warn("AdvancedLdapConfigurationAdminServlet.doPost: LDAP manual sync failed: " + e);
                }
                params.put("advancedLdapPluginConfiguration", advancedLdapPluginConfiguration);
                velocityHelper.renderVelocityTemplate("templates/configureView.vm", params, resp.getWriter());
            } else {
                setParameters(advancedLdapPluginConfiguration, req);
                try {
                    this.advancedLdapLogService.setLogLevel(advancedLdapPluginConfiguration.getLogLevelAsLevel());
                    this.hibernateAdvancedLdapPluginConfigurationDAO.store(advancedLdapPluginConfiguration, true);
                    this.advancedLdapSynchronizationManager.updateTimer();
                } catch (Exception e) {
                    log.warn("AdvancedLdapConfigurationAdminServlet.doPost: hibernateAdvancedLdapPluginConfigurationDAO.store failed: " + e);
                }
                params.put("advancedLdapPluginConfiguration", advancedLdapPluginConfiguration);
                velocityHelper.renderVelocityTemplate("templates/configureView.vm", params, resp.getWriter());
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/login-default.do");
        }
    }

    private void setParameters(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration, HttpServletRequest request) {
        advancedLdapPluginConfiguration.setConnectTimeoutMillis(Integer.parseInt(StringUtils.defaultIfEmpty(request.getParameter("ldap.serverTimeout"), "10000")));
        advancedLdapPluginConfiguration.setResponseTimeoutMillis(Integer.parseInt(StringUtils.defaultIfEmpty(request.getParameter("ldap.serverTimeout"), "10000")));
        advancedLdapPluginConfiguration.setLDAPPageSize(Integer.parseInt(StringUtils.defaultIfEmpty(request.getParameter("ldap.pageSize"), "99")));
        advancedLdapPluginConfiguration.setLDAPSyncPeriod(Integer.parseInt(StringUtils.defaultIfEmpty(request.getParameter("ldap.resyncPeriod"), "3600")));
        advancedLdapPluginConfiguration.setLDAPUrl(StringUtils.defaultIfEmpty(request.getParameter("ldap.url"), ""));
        advancedLdapPluginConfiguration.setLDAPBindDN(StringUtils.defaultIfEmpty(request.getParameter("ldap.initialDn"), ""));
        advancedLdapPluginConfiguration.setLDAPBindPassword(StringUtils.defaultIfEmpty(request.getParameter("ldap.initialSecret"), ""));
        advancedLdapPluginConfiguration.setLDAPBaseDN(StringUtils.defaultIfEmpty(request.getParameter("ldap.baseDn"), ""));
        advancedLdapPluginConfiguration.setUserFilterKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.filter"), ""));
        advancedLdapPluginConfiguration.setDisplayNameAttributeKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.displaynameAttr"), ""));
        advancedLdapPluginConfiguration.setEmailAttributeKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.emailAttr"), ""));
        advancedLdapPluginConfiguration.setUIDAttributeKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.uidAttr"), ""));

        advancedLdapPluginConfiguration.setUserGroupNamesKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.userGroupNamesAttr"), ""));
        advancedLdapPluginConfiguration.setGroupFilterKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.groupFilter"), ""));
        advancedLdapPluginConfiguration.setGIDAttributeKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.gidAttr"), ""));
        advancedLdapPluginConfiguration.setGroupDisplayNameKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.groupDisplaynameAttr"), ""));
        advancedLdapPluginConfiguration.setUserNamesKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.groupUsernameAttr"), ""));
        advancedLdapPluginConfiguration.setNestedGroupsEnabled(Boolean.parseBoolean(StringUtils.defaultIfEmpty(request.getParameter("ldap.nestedGroupsEnabled"), "False")));
        advancedLdapPluginConfiguration.setLogLevel(
                Level.toLevel(StringUtils.defaultIfEmpty(request.getParameter("ldap.logLevel"), Level.INFO.toString()),
                Level.INFO
        ).toString());
        advancedLdapPluginConfiguration.setRemovingUsersFromGroupsEnabled(Boolean.parseBoolean(StringUtils.defaultIfEmpty(request.getParameter("ldap.removingUsersFromGroupsEnabled"), "False")));
    }

}
