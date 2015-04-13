package com.davidkoudela.crucible.config;

import com.atlassian.fisheye.plugin.web.helpers.VelocityHelper;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: Configuration servlet class reacting on HTTP GET and POST requests.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-24
 */
public class AdvancedLdapConfigurationServlet extends HttpServlet {
    private final VelocityHelper velocityHelper;
    private HibernateAdvancedLdapOptionsDAO hibernateAdvancedLdapOptionsDAO;

    @org.springframework.beans.factory.annotation.Autowired
    public AdvancedLdapConfigurationServlet(VelocityHelper velocityHelper,
                                            HibernateAdvancedLdapOptionsDAO hibernateAdvancedLdapOptionsDAO) {
        this.velocityHelper = velocityHelper;
        this.hibernateAdvancedLdapOptionsDAO = hibernateAdvancedLdapOptionsDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AdvancedLdapOptions advancedLdapOptions = this.hibernateAdvancedLdapOptionsDAO.get();

        Map<String,Object> params = new HashMap<String,Object>();
        req.setAttribute("decorator", "atl.admin");
        params.put("advancedLdapOptions", advancedLdapOptions);
        resp.setContentType("text/html");
        velocityHelper.renderVelocityTemplate("templates/configureView.vm", params, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String,Object> params = new HashMap<String,Object>();
        req.setAttribute("decorator", "atl.admin");
        resp.setContentType("text/html");
        AdvancedLdapOptions advancedLdapOptions = this.hibernateAdvancedLdapOptionsDAO.get();
        if (req.getPathInfo().contains("/advancedLdapConfigurationServletView.do")) {
            setParameters(advancedLdapOptions, req);

            try {
                this.hibernateAdvancedLdapOptionsDAO.store(advancedLdapOptions, true);
            } catch (Exception e) {
                System.out.println("AdvancedLdapConfigurationServlet.doPost: hibernateAdvancedLdapOptionsDAO.store failed: " + e);
            }
            params.put("advancedLdapOptions", advancedLdapOptions);
            velocityHelper.renderVelocityTemplate("templates/configureView.vm", params, resp.getWriter());
        } else {
            params.put("advancedLdapOptions", advancedLdapOptions);
            velocityHelper.renderVelocityTemplate("templates/configureEdit.vm", params, resp.getWriter());
        }
    }

    private void setParameters(AdvancedLdapOptions advancedLdapOptions, HttpServletRequest request) {
        advancedLdapOptions.setConnectTimeoutMillis(Integer.parseInt(StringUtils.defaultIfEmpty(request.getParameter("ldap.serverTimeout"), "10000")));
        advancedLdapOptions.setResponseTimeoutMillis(Integer.parseInt(StringUtils.defaultIfEmpty(request.getParameter("ldap.serverTimeout"), "10000")));
        advancedLdapOptions.setLDAPPageSize(Integer.parseInt(StringUtils.defaultIfEmpty(request.getParameter("ldap.pageSize"), "1000")));
        advancedLdapOptions.setLDAPSyncPeriod(Integer.parseInt(StringUtils.defaultIfEmpty(request.getParameter("ldap.resyncPeriod"), "3600")));
        advancedLdapOptions.setLDAPUrl(StringUtils.defaultIfEmpty(request.getParameter("ldap.url"), ""));
        advancedLdapOptions.setLDAPBindDN(StringUtils.defaultIfEmpty(request.getParameter("ldap.initialDn"), ""));
        advancedLdapOptions.setLDAPBindPassword(StringUtils.defaultIfEmpty(request.getParameter("ldap.initialSecret"), ""));
        advancedLdapOptions.setLDAPBaseDN(StringUtils.defaultIfEmpty(request.getParameter("ldap.baseDn"), ""));
        advancedLdapOptions.setUserFilterKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.filter"), null));
        advancedLdapOptions.setDisplayNameAttributeKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.displaynameAttr"), ""));
        advancedLdapOptions.setEmailAttributeKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.emailAttr"), ""));
        advancedLdapOptions.setUIDAttributeKey(StringUtils.defaultIfEmpty(request.getParameter("ldap.uidAttr"), ""));
    }
}
