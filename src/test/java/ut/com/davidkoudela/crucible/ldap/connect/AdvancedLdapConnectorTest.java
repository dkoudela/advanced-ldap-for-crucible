package ut.com.davidkoudela.crucible.ldap.connect;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResult;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
        this.ldapConnection = PowerMock.createMock(LDAPConnection.class);
        this.searchResult = PowerMock.createMock(SearchResult.class);
        this.searchResult = null;
    }
    @Test
    public void testLdapPagedSearchNoEntries() throws LDAPException {
        this.advancedLdapConnector = new AdvancedLdapConnectorDummy(this.advancedLdapPluginConfiguration);
        this.advancedLdapConnector.setLdapConnection(this.ldapConnection);

        PowerMock.replay(this.ldapConnection);
//        PowerMock.expect(this.ldapConnection.search()).andReturn(this.searchResult);
        PowerMock.verify(this.ldapConnection);
    }
}
