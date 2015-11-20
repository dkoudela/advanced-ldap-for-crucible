package ut.com.davidkoudela.crucible.ldap.connect;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.unboundid.ldap.sdk.LDAPException;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Description: Testing {@link com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-11-20
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapConnectorNoMockTest extends TestCase {
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration2;
    private static AdvancedLdapConnectorTest.AdvancedLdapConnectorDummy advancedLdapConnector;

    @Before
    public void init() {
        this.advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        this.advancedLdapPluginConfiguration.setLDAPUrl("ldap://localhost:389");
        this.advancedLdapPluginConfiguration.setConnectTimeoutMillis(1);
        this.advancedLdapPluginConfiguration.setResponseTimeoutMillis(2);
        this.advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");

        this.advancedLdapPluginConfiguration2 = new AdvancedLdapPluginConfiguration();
        this.advancedLdapPluginConfiguration2.setLDAPUrl("ldaps://localhost:389");
        this.advancedLdapPluginConfiguration2.setConnectTimeoutMillis(1);
        this.advancedLdapPluginConfiguration2.setResponseTimeoutMillis(2);
        this.advancedLdapPluginConfiguration2.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");
    }

    @Test(expected = LDAPException.class)
    public void testGetLdapConnectionNonSsl() throws LDAPException, KeyManagementException, NoSuchAlgorithmException {
        this.advancedLdapConnector = new AdvancedLdapConnectorTest.AdvancedLdapConnectorDummy(this.advancedLdapPluginConfiguration);

        this.advancedLdapConnector.getLdapConnection();
    }

    @Test(expected = LDAPException.class)
    public void testGetLdapConnectionSsl() throws LDAPException, KeyManagementException, NoSuchAlgorithmException {
        this.advancedLdapConnector = new AdvancedLdapConnectorTest.AdvancedLdapConnectorDummy(this.advancedLdapPluginConfiguration2);

        this.advancedLdapConnector.getLdapConnection();
    }
}
