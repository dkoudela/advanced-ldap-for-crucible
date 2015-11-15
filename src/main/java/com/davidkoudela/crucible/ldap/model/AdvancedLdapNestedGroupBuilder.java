package com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchFilterFactory;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: Implementation of {@link AdvancedLdapGroupSearchResultBuilder} build capabilities for
 *              {@link AdvancedLdapGroup} according to results retrieved from the remote LDAP server
 *              extended for Nested Ldap Groups.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-11-15
 */
public class AdvancedLdapNestedGroupBuilder implements AdvancedLdapNestedGroupSearchResultBuilder {
    private AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private List<AdvancedLdapGroup> advancedLdapGroupList = new ArrayList<AdvancedLdapGroup>();
    private Boolean followMembers = false;
    private AdvancedLdapConnector advancedLdapConnector = null;
    private AdvancedLdapGroupBuilder advancedLdapGroupBuilder = null;
    private Set<String> groupNames = new HashSet<String>();
    private Set<String> nonpersonDns = new HashSet<String>();
    private Set<String> nonpersonDnsQueue = new HashSet<String>();
    private Set<String> nestedGroups = new HashSet<String>();

    public AdvancedLdapNestedGroupBuilder(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration, Boolean followMembers) {
        this.advancedLdapPluginConfiguration = advancedLdapPluginConfiguration;
        this.followMembers = followMembers;
    }

    @Override
    public List<AdvancedLdapGroup> getGroups() {
        return this.advancedLdapGroupList;
    }

    @Override
    public Set<String> getGroupNames() {
        return this.groupNames;
    }

    @Override
    public Set<String> getNonpersonDns() {
        return this.nonpersonDns;
    }

    @Override
    public Set<String> getNestedGroups() {
        return nestedGroups;
    }

    @Override
    public void handlePagedSearchResult(SearchResultEntry searchResultEntry) {
        AdvancedLdapGroupBuilder firstLevelAdvancedLdapGroupBuilder = getAdvancedLdapGroupBuilder();
        firstLevelAdvancedLdapGroupBuilder.handlePagedSearchResult(searchResultEntry);
        this.advancedLdapGroupList.addAll(firstLevelAdvancedLdapGroupBuilder.getGroups());
        this.groupNames.addAll(firstLevelAdvancedLdapGroupBuilder.getGroupNames());

        Set<String> currentNonpersonDns = firstLevelAdvancedLdapGroupBuilder.getNonpersonDns();
        if (true == this.advancedLdapPluginConfiguration.isNestedGroupsEnabled()) {
            while (0 != currentNonpersonDns.size()) {
                for (String currentNonpersonDn : currentNonpersonDns) {
                    if (false == this.nonpersonDns.contains(currentNonpersonDn)) {
                        try {
                            SearchRequest searchRequest = new SearchRequest(currentNonpersonDn, SearchScope.BASE,
                                    AdvancedLdapSearchFilterFactory.getSearchFilterForAllGroups(this.advancedLdapPluginConfiguration.getGroupFilterKey()));
                            AdvancedLdapConnector advancedLdapConnector = getAdvancedLdapConnector();
                            AdvancedLdapGroupBuilder currentAdvancedLdapGroupBuilder = getAdvancedLdapGroupBuilder();
                            advancedLdapConnector.ldapPagedSearch(searchRequest, currentAdvancedLdapGroupBuilder);

                            List foundNestedGroupsInLdap = currentAdvancedLdapGroupBuilder.getGroups();
                            if (0 == foundNestedGroupsInLdap.size()) {
                                System.out.println("AdvancedLdapNestedGroupBuilder: potential nested group not found: " + currentNonpersonDn);
                                continue;
                            } else if (1 != foundNestedGroupsInLdap.size()) {
                                System.out.println("AdvancedLdapNestedGroupBuilder: potential nested group search returned " + foundNestedGroupsInLdap.size() + " entries");
                                continue;
                            }

                            this.advancedLdapGroupList.addAll(foundNestedGroupsInLdap);
                            this.groupNames.addAll(currentAdvancedLdapGroupBuilder.getGroupNames());
                            this.nestedGroups.add(currentNonpersonDn);
                            for (String newNonpersonDn : currentAdvancedLdapGroupBuilder.getNonpersonDns()) {
                                if (false == this.nonpersonDns.contains(newNonpersonDn) && false == this.nonpersonDnsQueue.contains(newNonpersonDn)) {
                                    this.nonpersonDnsQueue.add(newNonpersonDn);
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("AdvancedLdapNestedGroupBuilder: nested group search failed: " + currentNonpersonDn + " Exception: " + e);
                        }
                    }
                }
                this.nonpersonDns.addAll(currentNonpersonDns);
                currentNonpersonDns = this.nonpersonDnsQueue;
                this.nonpersonDnsQueue = new HashSet<String>();
            }
        }

    }

    protected void setAdvancedLdapConnector(AdvancedLdapConnector advancedLdapConnector) {
        this.advancedLdapConnector = advancedLdapConnector;
    }

    protected AdvancedLdapConnector getAdvancedLdapConnector() {
        if (null != this.advancedLdapConnector)
            return this.advancedLdapConnector;
        return new AdvancedLdapConnector(this.advancedLdapPluginConfiguration);
    }

    protected void setAdvancedLdapGroupBuilder(AdvancedLdapGroupBuilder advancedLdapGroupBuilder) {
        this.advancedLdapGroupBuilder = advancedLdapGroupBuilder;
    }

    protected AdvancedLdapGroupBuilder getAdvancedLdapGroupBuilder() {
        if (null != this.advancedLdapGroupBuilder)
            return this.advancedLdapGroupBuilder;
        return new AdvancedLdapGroupBuilder(this.advancedLdapPluginConfiguration, true);
    }

}
