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
 *              database settings.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-09-10
 */
public class AdvancedLdapDatabaseAdminServlet extends HttpServlet {
    private final VelocityHelper velocityHelper;
    private AdvancedLdapUserManager advancedLdapUserManager;
    private String testResponse = "{ " +
            "    \"errorMsg\": \"No error\", " +
            "    \"worked\": true, " +
            "    \"payload\": \"  \\u003cdiv class=\\\"aui-message closeable aui-message-success\\\"\\u003e\\n            \\u003cp\\u003eConnection succeeded\\u003c/p\\u003e\\n                  \\u003c/div\\u003e\\n        \\u003cdiv class=\\\"db_test\\\"\\u003e\\n            \\u003cbutton id=\\\"db_test\\\" value=\\\"Test Connection\\\" class=\\\"aui-button\\\"\\u003eTest Connection\\u003c/button\\u003e\\n        \\u003c/div\\u003e\\n    \",\n" +
            "    \"connection\": true, " +
            "    \"sameUrl\": true, " +
            "    \"changed\": false, " +
            "    \"message\": null " +
            "}";
    private String testErrorResponse = "{\n" +
            "    \"errorMsg\": \"No error\",\n" +
            "    \"worked\": true,\n" +
            "    \"payload\": \"  \\u003cdiv class=\\\"buttons-container\\\"\\u003e\\n  \\u003cdiv class=\\\"aui-message closeable aui-message-error\\\"\\u003e\\n   \\u003cp class=\\\"title\\\"\\u003e \\u003cstrong\\u003eFailed\\u003c/strong\\u003e  \\u003c/p\\u003e\\n             \\u003cp\\u003eUnable to connect to MySQL database\\u003c/p\\u003e\\n   \\u003c/div\\u003e\\n    \\u003c/div\\u003e\\n       \\u003cdiv class=\\\"buttons-container\\\"\\u003e\\n  \\u003cdiv id=\\\"db_status\\\" class=\\\"buttons\\\"\\u003e\\n   \\u003cdiv class=\\\"db_test\\\"\\u003e\\n            \\u003cbutton id=\\\"db_test\\\" value=\\\"Test Connection\\\" class=\\\"aui-button\\\"\\u003eTest Connection\\u003c/button\\u003e\\n        \\u003c/div\\u003e\\n    \\u003c/div\\u003e\\n    \\u003c/div\\u003e\\n    \",\n" +
            "    \"connection\": false,\n" +
            "    \"sameUrl\": true,\n" +
            "    \"changed\": true,\n" +
            "    \"message\": null " +
            "}";

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
                params.put("testResult", this.testErrorResponse);
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
        if (advancedLdapUserManager.hasSysAdminPrivileges(req)) {
            if (req.getPathInfo().contains("/advancedLdapDatabaseAdminServletEdit.do")) {
                setAdminMenuDecorator(req, resp);
                velocityHelper.renderVelocityTemplate("templates/databaseEdit.vm", params, resp.getWriter());
            } else if (req.getPathInfo().contains("/advancedLdapDatabaseAdminServletTest.do")) {
                setRestDecorator(req, resp);
                params.put("testResult", this.testErrorResponse);
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
}
