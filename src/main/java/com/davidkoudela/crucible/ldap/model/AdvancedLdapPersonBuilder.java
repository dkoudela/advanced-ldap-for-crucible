package com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchFilterFactory;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.SearchScope;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: Implementation of {@link AdvancedLdapPersonSearchResultBuilder} build capabilities for
 *              {@link AdvancedLdapPerson} according to results retrieved from the remote LDAP server.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-15
 */
public class AdvancedLdapPersonBuilder implements AdvancedLdapPersonSearchResultBuilder {
    private Logger log = Logger.getLogger(this.getClass());
    private AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private List<AdvancedLdapPerson> advancedLdapPersonList = new ArrayList<AdvancedLdapPerson>();
    private Boolean followMembers = false;
    private AdvancedLdapConnector advancedLdapConnector = null;
    private AdvancedLdapGroupBuilder advancedLdapGroupBuilder = null;
    private Set<String> personNames = new HashSet<String>();

    public AdvancedLdapPersonBuilder(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration, Boolean followMembers) {
        this.advancedLdapPluginConfiguration = advancedLdapPluginConfiguration;
        this.followMembers = followMembers;
    }

    @Override
    public List<AdvancedLdapPerson> getPersons() {
        return this.advancedLdapPersonList;
    }

    @Override
    public Set<String> getPersonNames() {
        return this.personNames;
    }

    @Override
    public void handlePagedSearchResult(SearchResultEntry searchResultEntry) {
        AdvancedLdapPerson advancedLdapPerson = new AdvancedLdapPerson();
        advancedLdapPerson.setUid(searchResultEntry.getAttributeValue(this.advancedLdapPluginConfiguration.getUIDAttributeKey()));
        advancedLdapPerson.setDisplayName(searchResultEntry.getAttributeValue(this.advancedLdapPluginConfiguration.getDisplayNameAttributeKey()));
        advancedLdapPerson.setEmail(searchResultEntry.getAttributeValue(this.advancedLdapPluginConfiguration.getEmailAttributeKey()));

        if (false == this.getPersonNames().contains(advancedLdapPerson.getUid())) {
            if (this.followMembers) {
                List<AdvancedLdapGroup> groupList = new ArrayList<AdvancedLdapGroup>();
                if (searchResultEntry.hasAttribute(this.advancedLdapPluginConfiguration.getUserGroupNamesKey())) {
                    Attribute groupDns = searchResultEntry.getAttribute(this.advancedLdapPluginConfiguration.getUserGroupNamesKey());
                    for (String groupDn : groupDns.getValues()) {
                        log.debug("AdvancedLdapPersonBuilder: Group: " + groupDn);
                        try {
                            SearchRequest searchRequest = new SearchRequest(groupDn, SearchScope.BASE,
                                    AdvancedLdapSearchFilterFactory.getSearchFilterForAllGroups(this.advancedLdapPluginConfiguration.getGroupFilterKey()));
                            AdvancedLdapConnector advancedLdapConnector = getAdvancedLdapConnector();
                            AdvancedLdapGroupBuilder advancedLdapGroupBuilder = getAdvancedLdapGroupBuilder();
                            advancedLdapConnector.ldapPagedSearch(searchRequest, advancedLdapGroupBuilder);

                            List foundGroupsInLdap = advancedLdapGroupBuilder.getGroups();
                            if (1 != foundGroupsInLdap.size()) {
                                log.debug("AdvancedLdapPersonBuilder: group search returned " + foundGroupsInLdap.size() + " entries");
                                continue;
                            }
                            groupList.addAll(foundGroupsInLdap);

                        } catch (Exception e) {
                            log.warn("AdvancedLdapPersonBuilder: group search failed: " + groupDn + " Exception: " + e);
                        }
                    }
                }
                advancedLdapPerson.setGroupList(groupList);
            }
            this.advancedLdapPersonList.add(advancedLdapPerson);
            this.personNames.add(advancedLdapPerson.getUid());
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
        return new AdvancedLdapGroupBuilder(this.advancedLdapPluginConfiguration, false);
    }

}
