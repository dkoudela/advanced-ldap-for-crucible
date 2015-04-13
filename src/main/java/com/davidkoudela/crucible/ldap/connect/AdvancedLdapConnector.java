package com.davidkoudela.crucible.ldap.connect;

import com.davidkoudela.crucible.config.AdvancedLdapOptions;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.controls.VirtualListViewResponseControl;
import com.unboundid.util.DebugType;
import com.unboundid.util.LDAPTestUtils;

import java.util.HashSet;

/**
 * Description: {@link AdvancedLdapConnector} provides methods for retrieving LDAP data from the remove LDAP server.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-20
 */
public class AdvancedLdapConnector {

    public void ldapPagedSearch(AdvancedLdapOptions advancedLdapOptions, SearchRequest searchRequest, AdvancedLdapSearchResultBuilder advancedLdapSearchResultBuilder) {
        LDAPConnection connection = null;
        try {
            AdvancedLdapConnectionOptionsFactory advancedLdapConnectionOptionsFactory = new AdvancedLdapConnectionOptionsFactory(advancedLdapOptions);
            connection = new LDAPConnection(
                    advancedLdapConnectionOptionsFactory.getConnectionOptions(advancedLdapOptions),
                    advancedLdapConnectionOptionsFactory.getLDAPHost(),
                    advancedLdapConnectionOptionsFactory.getLDAPPort(),
                    advancedLdapOptions.getLDAPBindDN(),
                    advancedLdapOptions.getLDAPBindPassword());

            int numSearches = 0;
            int totalEntriesReturned = 0;
            ASN1OctetString resumeCookie = null;
            while (true) {
                searchRequest.setControls(new SimplePagedResultsControl(advancedLdapOptions.getLDAPPageSize(), resumeCookie));
                SearchResult searchResult = connection.search(searchRequest);
                numSearches++;
                totalEntriesReturned += searchResult.getEntryCount();
                for (SearchResultEntry entry : searchResult.getSearchEntries()) {
                    System.out.println("AdvancedLdapConnector Entry Dn: " + entry.getDN().toString());
                    advancedLdapSearchResultBuilder.handlePagedSearchResult(entry);
                }

                LDAPTestUtils.assertHasControl(searchResult, SimplePagedResultsControl.PAGED_RESULTS_OID);
                SimplePagedResultsControl responseControl = SimplePagedResultsControl.get(searchResult);
                if (responseControl.moreResultsToReturn()) {
                    // The resume cookie can be included in the simple paged results
                    // control included in the next search to get the next page of results.
                    resumeCookie = responseControl.getCookie();
                } else {
                    break;
                }
            }
        } catch (AssertionError e) {
            System.out.println("**************************** AdvancedLdapConnector AssertionError ****************************" + e);
        } catch (Exception e) {
            System.out.println("**************************** AdvancedLdapConnector EXCEPTION ****************************" + e);
        }
        finally {
            try {
                System.out.println("**************************** AdvancedLdapConnector On Finalize ****************************");
                connection.close();
            } catch (Exception e)
            {
                System.out.println("**************************** AdvancedLdapConnector EXCEPTION on conn closed ****************************" + e);
            }
        }
    }
}
