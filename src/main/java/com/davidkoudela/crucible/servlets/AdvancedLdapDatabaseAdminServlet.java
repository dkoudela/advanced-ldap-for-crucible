package com.davidkoudela.crucible.servlets;

import com.atlassian.fisheye.plugin.web.helpers.VelocityHelper;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration;
import com.davidkoudela.crucible.persistence.AdvancedLdapDatabaseConfigFactory;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: Configuration servlet class reacting on HTTP GET and POST requests related to
 *              database settings.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-09-10
 */
public class AdvancedLdapDatabaseAdminServlet extends HttpServlet {
    private final VelocityHelper velocityHelper;
    private AdvancedLdapUserManager advancedLdapUserManager;

    @org.springframework.beans.factory.annotation.Autowired
    public AdvancedLdapDatabaseAdminServlet(VelocityHelper velocityHelper,
                                                AdvancedLdapUserManager advancedLdapUserManager) {
        this.velocityHelper = velocityHelper;
        this.advancedLdapUserManager = advancedLdapUserManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String,Object> params = new HashMap<String,Object>();
        if (advancedLdapUserManager.hasSysAdminPrivileges(req)) {
            if (req.getPathInfo().contains("/advancedLdapDatabaseAdminServletTest.do")) {
                setRestDecorator(req, resp);
                params.put("testResult", AdvancedLdapDatabaseConfigFactory.verifyDatabaseConfig());
                velocityHelper.renderVelocityTemplate("templates/databaseTest.vm", params, resp.getWriter());
            } else {
                setAdminMenuDecorator(req, resp);
                velocityHelper.renderVelocityTemplate("templates/databaseView.vm", params, resp.getWriter());
            }
        } else {
            setAdminMenuDecorator(req, resp);
            resp.sendRedirect(req.getContextPath() + "/admin/login-default.do");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String,Object> params = new HashMap<String,Object>();
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        setParameters(advancedLdapDatabaseConfiguration, req);

        if (advancedLdapUserManager.hasSysAdminPrivileges(req)) {
            if (req.getPathInfo().contains("/advancedLdapDatabaseAdminServletEdit.do")) {
                setAdminMenuDecorator(req, resp);
                velocityHelper.renderVelocityTemplate("templates/databaseEdit.vm", params, resp.getWriter());
            } else if (req.getPathInfo().contains("/advancedLdapDatabaseAdminServletTest.do")) {
                setRestDecorator(req, resp);
                params.put("testResult", AdvancedLdapDatabaseConfigFactory.verifyDatabaseConfig());
                velocityHelper.renderVelocityTemplate("templates/databaseTest.vm", params, resp.getWriter());
            } else {
                setAdminMenuDecorator(req, resp);
                velocityHelper.renderVelocityTemplate("templates/databaseView.vm", params, resp.getWriter());
            }
        } else {
            setAdminMenuDecorator(req, resp);
            resp.sendRedirect(req.getContextPath() + "/admin/login-default.do");
        }
    }

    protected void setAdminMenuDecorator(HttpServletRequest req, HttpServletResponse resp) {
        req.setAttribute("decorator", "atl.admin");
        resp.setContentType("text/html");
    }

    protected void setRestDecorator(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
    }

    private void setParameters(AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration, HttpServletRequest request) {
        advancedLdapDatabaseConfiguration.setDatabaseName(StringUtils.defaultIfEmpty(request.getParameter("dbConfig.dbName"), ""));
        advancedLdapDatabaseConfiguration.setUserName(StringUtils.defaultIfEmpty(request.getParameter("dbConfig.username"), ""));
        if (Boolean.parseBoolean(StringUtils.defaultIfEmpty(request.getParameter("passwordChanged"), "false"))) {
            advancedLdapDatabaseConfiguration.setPassword(StringUtils.defaultIfEmpty(request.getParameter("dbConfig.password"), ""));
        } else {
            // TODO: ensure the password is taken from the system as it was not provided by the user.
            advancedLdapDatabaseConfiguration.setPassword("");
        }
    }

}
