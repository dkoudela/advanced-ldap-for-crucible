package com.davidkoudela.crucible.servlets;

import com.atlassian.fisheye.plugin.web.helpers.VelocityHelper;

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

    @org.springframework.beans.factory.annotation.Autowired
    public AdvancedLdapConfigurationTestServlet(VelocityHelper velocityHelper) {
        this.velocityHelper = velocityHelper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String,Object> params = new HashMap<String,Object>();
        req.setAttribute("decorator", "atl.admin");
        resp.setContentType("text/html");
        velocityHelper.renderVelocityTemplate("templates/authTest.vm", params, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String,Object> params = new HashMap<String,Object>();
        req.setAttribute("decorator", "atl.admin");
        resp.setContentType("text/html");
        if (req.getPathInfo().contains("/advancedLdapConfigurationTestServletTestResults.do")) {
            velocityHelper.renderVelocityTemplate("templates/authResult.vm", params, resp.getWriter());
        } else {
            velocityHelper.renderVelocityTemplate("templates/authTest.vm", params, resp.getWriter());
        }
    }
}
