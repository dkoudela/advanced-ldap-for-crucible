package ut.com.davidkoudela.crucible.ldap.connect;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchFilterFactory;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchResultBuilder;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapGroupBuilder;
import com.unboundid.ldap.sdk.*;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: Testing {@link AdvancedLdapConnector}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-26
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LDAPConnection.class, SearchResult.class})
public class AdvancedLdapConnectorTest extends TestCase {
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private static AdvancedLdapConnectorDummy advancedLdapConnector;
    private static AdvancedLdapSearchResultBuilder advancedLdapSearchResultBuilder;
    private LDAPConnection ldapConnection;
    private SearchResult searchResult;

    public class AdvancedLdapConnectorDummy extends AdvancedLdapConnector {
        public AdvancedLdapConnectorDummy(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration) {
            super(advancedLdapPluginConfiguration);
        }

        public void setLdapConnection(LDAPConnection ldapConnection) {
            super.setLdapConnection(ldapConnection);
        }
    }

    @Before
    public void init() {
        this.advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        this.advancedLdapPluginConfiguration.setLDAPUrl("ldap://localhost:389");
        this.advancedLdapPluginConfiguration.setConnectTimeoutMillis(1);
        this.advancedLdapPluginConfiguration.setResponseTimeoutMillis(2);
        this.advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");
        this.ldapConnection = PowerMock.createMock(LDAPConnection.class);
        this.searchResult = PowerMock.createMock(SearchResult.class);
        this.advancedLdapSearchResultBuilder = Mockito.mock(AdvancedLdapGroupBuilder.class);
    }
    @Test
    public void testLdapPagedSearchNoEntries() throws LDAPException {
        this.advancedLdapConnector = new AdvancedLdapConnectorDummy(this.advancedLdapPluginConfiguration);
        this.advancedLdapConnector.setLdapConnection(this.ldapConnection);
        SearchRequest searchRequest = new SearchRequest(advancedLdapPluginConfiguration.getLDAPBaseDN(), SearchScope.SUB,
                AdvancedLdapSearchFilterFactory.getSearchFilterForAllUsers(this.advancedLdapPluginConfiguration.getUserFilterKey()));
        List<SearchResultEntry> searchResultEntryList = new ArrayList<SearchResultEntry>();
        Control[] control = new Control[1];
        control[0] = new Control("1.2.840.113556.1.4.319", true);
        EasyMock.expect(this.ldapConnection.search(searchRequest)).andReturn(this.searchResult);
        EasyMock.expect(this.searchResult.getEntryCount()).andReturn(0);
        EasyMock.expect(this.searchResult.getSearchEntries()).andReturn(searchResultEntryList);
        EasyMock.expect(this.searchResult.getResponseControls()).andReturn(control);
        EasyMock.expect(this.searchResult.getResponseControl("1.2.840.113556.1.4.319")).andReturn(control[0]);
        this.ldapConnection.close();
        EasyMock.expectLastCall();

        PowerMock.replayAll();
        this.advancedLdapConnector.ldapPagedSearch(searchRequest, this.advancedLdapSearchResultBuilder);
        PowerMock.verifyAll();
    }
}
