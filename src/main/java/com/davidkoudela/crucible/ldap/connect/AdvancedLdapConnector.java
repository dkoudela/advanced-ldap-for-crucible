package com.davidkoudela.crucible.ldap.connect;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.util.LDAPTestUtils;

/**
 * Description: {@link AdvancedLdapConnector} provides methods for retrieving LDAP data from the remove LDAP server.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-20
 */
public class AdvancedLdapConnector {
    private AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private LDAPConnection ldapConnection = null;

    public AdvancedLdapConnector(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration) {
        this.advancedLdapPluginConfiguration = advancedLdapPluginConfiguration;
    }

    public void ldapPagedSearch(SearchRequest searchRequest, AdvancedLdapSearchResultBuilder advancedLdapSearchResultBuilder) {
        LDAPConnection connection = null;
        try {
            connection = getLdapConnection();

            int numSearches = 0;
            int totalEntriesReturned = 0;
            ASN1OctetString resumeCookie = null;
            while (true) {
                searchRequest.setControls(new SimplePagedResultsControl(advancedLdapPluginConfiguration.getLDAPPageSize(), resumeCookie));
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

    public boolean bindDn(String dn, String password) {
        LDAPConnection connection = null;
        boolean result = false;
        try {
            connection = getLdapConnection();

            BindResult bindResult = connection.bind(dn, password);
            if (com.unboundid.ldap.sdk.ResultCode.SUCCESS_INT_VALUE == bindResult.getResultCode().intValue())
                result = true;
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

        return result;
    }

    protected LDAPConnection getLdapConnection() throws LDAPException {
        if (null != this.ldapConnection)
            return this.ldapConnection;

        AdvancedLdapConnectionOptionsFactory advancedLdapConnectionOptionsFactory = new AdvancedLdapConnectionOptionsFactory(advancedLdapPluginConfiguration);

        LDAPConnectionOptions ldapConnectionOptions = advancedLdapConnectionOptionsFactory.getConnectionOptions();
        String ldapHost = advancedLdapConnectionOptionsFactory.getLDAPHost();
        int ldapPort = advancedLdapConnectionOptionsFactory.getLDAPPort();
        String ldapBindDN = this.advancedLdapPluginConfiguration.getLDAPBindDN();
        String ldapBindPassword = this.advancedLdapPluginConfiguration.getLDAPBindPassword();
        System.out.println("LDAP Connection parameters: ldapHost: " + ldapHost + " ldapPort: " + ldapPort + " ldapBindDN: " + ldapBindDN);

        return new LDAPConnection(
                ldapConnectionOptions,
                ldapHost,
                ldapPort,
                ldapBindDN,
                ldapBindPassword);
    }

    protected void setLdapConnection(LDAPConnection ldapConnection) {
        this.ldapConnection = ldapConnection;
    }
}
