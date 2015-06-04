package com.davidkoudela.crucible.servlets;

import com.atlassian.fisheye.plugin.web.helpers.VelocityHelper;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
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
 *              configuration testing.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-05-05
 */
public class AdvancedLdapConfigurationTestServlet extends HttpServlet {
    private final VelocityHelper velocityHelper;
    private AdvancedLdapUserManager advancedLdapUserManager;

    @org.springframework.beans.factory.annotation.Autowired
    public AdvancedLdapConfigurationTestServlet(VelocityHelper velocityHelper,
                                                AdvancedLdapUserManager advancedLdapUserManager) {
        this.velocityHelper = velocityHelper;
        this.advancedLdapUserManager = advancedLdapUserManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String,Object> params = new HashMap<String,Object>();
        req.setAttribute("decorator", "atl.admin");
        resp.setContentType("text/html");
        if (advancedLdapUserManager.hasSysAdminPrivileges(req)) {
            velocityHelper.renderVelocityTemplate("templates/authTest.vm", params, resp.getWriter());
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/login-default.do");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String,Object> params = new HashMap<String,Object>();
        req.setAttribute("decorator", "atl.admin");
        resp.setContentType("text/html");
        if (advancedLdapUserManager.hasSysAdminPrivileges(req)) {
            if (req.getPathInfo().contains("/advancedLdapConfigurationTestServletTestResults.do")) {
                String testResult = "username/password succeeded";

                boolean validCredentials = this.advancedLdapUserManager.verifyUserCredentials(StringUtils.defaultIfEmpty(req.getParameter("username"), ""),
                        StringUtils.defaultIfEmpty(req.getParameter("password"), ""));
                if (!validCredentials)
                    testResult = "username/password failed";

                params.put("testResult", testResult);
                velocityHelper.renderVelocityTemplate("templates/authResult.vm", params, resp.getWriter());
            } else {
                velocityHelper.renderVelocityTemplate("templates/authTest.vm", params, resp.getWriter());
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/login-default.do");
        }
    }
}
