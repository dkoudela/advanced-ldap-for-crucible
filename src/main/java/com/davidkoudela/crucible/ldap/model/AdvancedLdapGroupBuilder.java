package com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.config.AdvancedLdapOptions;
import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.unboundid.ldap.sdk.SearchResultEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: Implementation of {@link AdvancedLdapGroupSearchResultBuilder} build capabilities for
 *              {@link AdvancedLdapGroup} according to results retrieved from the remote LDAP server.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-21
 */
public class AdvancedLdapGroupBuilder implements AdvancedLdapGroupSearchResultBuilder {
    private AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private List<AdvancedLdapGroup> advancedLdapGroupList = new ArrayList<AdvancedLdapGroup>();
    private Boolean followMembers = false;

    public AdvancedLdapGroupBuilder(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration, Boolean followMembers) {
        this.advancedLdapPluginConfiguration = advancedLdapPluginConfiguration;
        this.followMembers = followMembers;
    }

    @Override
    public List<AdvancedLdapGroup> getGroups() {
        return advancedLdapGroupList;
    }

    @Override
    public void handlePagedSearchResult(SearchResultEntry searchResultEntry) {
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID(searchResultEntry.getAttributeValue(this.advancedLdapPluginConfiguration.getGIDAttributeKey()));
        advancedLdapGroup.setDisplayName(searchResultEntry.getAttributeValue(this.advancedLdapPluginConfiguration.getGroupDisplayNameKey()));

        // TODO: Temp Tracing purpose:
        //System.out.println("AdvancedLdapGroupBuilder: Display Name: " + searchResultEntry.getAttributeValue(this.advancedLdapOptions.getGroupDisplayNameKey()));
        //System.out.println("AdvancedLdapGroupBuilder: GID: " + searchResultEntry.getAttributeValue(this.advancedLdapOptions.getGIDAttributeKey()));

        if (this.followMembers) {
            throw new UnsupportedOperationException("AdvancedLdapGroupBuilder with following member users is not supported now");
        }

        this.advancedLdapGroupList.add(advancedLdapGroup);
    }
}
