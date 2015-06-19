package com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchResultBuilder;
import com.unboundid.ldap.sdk.SearchResultEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: Implementation of {@link AdvancedLdapBindSearchResultBuilder} build capabilities for
 *              {@link AdvancedLdapBind} according to results retrieved from the remote LDAP server.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-05-06
 */
public class AdvancedLdapBindBuilder implements AdvancedLdapBindSearchResultBuilder {
    private AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private String password;
    private List<AdvancedLdapBind> advancedLdapBindList = new ArrayList<AdvancedLdapBind>();
    private AdvancedLdapConnector advancedLdapConnector = null;

    public AdvancedLdapBindBuilder(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration, String password) {
        this.advancedLdapPluginConfiguration = advancedLdapPluginConfiguration;
        this.password = password;
    }

    @Override
    public void handlePagedSearchResult(SearchResultEntry searchResultEntry) {
        AdvancedLdapBind advancedLdapBind = new AdvancedLdapBind();
        String dn = searchResultEntry.getDN().toString();
        advancedLdapBind.setDn(dn);

        AdvancedLdapConnector advancedLdapConnector = getAdvancedLdapConnector();
        boolean result = advancedLdapConnector.bindDn(dn, this.password);
        advancedLdapBind.setResult(result);

        this.advancedLdapBindList.add(advancedLdapBind);
    }

    @Override
    public List<AdvancedLdapBind> getBinds() {
        return this.advancedLdapBindList;
    }

    protected AdvancedLdapConnector getAdvancedLdapConnector() {
        if (null != this.advancedLdapConnector)
            return this.advancedLdapConnector;
        return new AdvancedLdapConnector(this.advancedLdapPluginConfiguration);
    }

    protected void setAdvancedLdapConnector(AdvancedLdapConnector advancedLdapConnector) {
        this.advancedLdapConnector = advancedLdapConnector;
    }
}
