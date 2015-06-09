package com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchFilterFactory;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

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

        if (this.followMembers) {
            List<AdvancedLdapPerson> personList = new ArrayList<AdvancedLdapPerson>();
            if (searchResultEntry.hasAttribute(this.advancedLdapPluginConfiguration.getUserNamesKey())) {
                Attribute personDns = searchResultEntry.getAttribute(this.advancedLdapPluginConfiguration.getUserNamesKey());
                for (String personDn : personDns.getValues()) {
                    System.out.println("AdvancedLdapGroupBuilder: Person: " + personDn);
                    try {
                        SearchRequest searchRequest = new SearchRequest(personDn, SearchScope.BASE,
                                AdvancedLdapSearchFilterFactory.getSearchFilterForAllUsers(this.advancedLdapPluginConfiguration.getUserFilterKey()));
                        AdvancedLdapConnector advancedLdapConnector = new AdvancedLdapConnector();
                        AdvancedLdapPersonBuilder advancedLdapPersonBuilder = new AdvancedLdapPersonBuilder(this.advancedLdapPluginConfiguration, false);
                        advancedLdapConnector.ldapPagedSearch(this.advancedLdapPluginConfiguration, searchRequest, advancedLdapPersonBuilder);

                        List foundPersonsInLdap = advancedLdapPersonBuilder.getPersons();
                        if (1 != foundPersonsInLdap.size()) {
                            System.out.println("AdvancedLdapGroupBuilder: person search returned "+ foundPersonsInLdap.size() + " entries");
                            continue;
                        }
                        personList.addAll(foundPersonsInLdap);

                    } catch (Exception e) {
                        System.out.println("AdvancedLdapGroupBuilder: person search failed: " + personDn + " Exception: " + e);
                    }
                }
            }
            advancedLdapGroup.setPersonList(personList);
        }

        this.advancedLdapGroupList.add(advancedLdapGroup);
    }
}
