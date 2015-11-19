package ut.com.davidkoudela.crucible.ldap.connect;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnectionOptionsFactory;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Description: Testing {@link AdvancedLdapConnectionOptionsFactory}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-18
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapConnectionOptionsFactoryTest extends TestCase {
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private static AdvancedLdapConnectionOptionsFactory advancedLdapConnectionOptionsFactory;
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration2;
    private static AdvancedLdapConnectionOptionsFactory advancedLdapConnectionOptionsFactory2;
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration3;
    private static AdvancedLdapConnectionOptionsFactory advancedLdapConnectionOptionsFactory3;

    @Before
    public void init() throws LDAPException {
        this.advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        this.advancedLdapPluginConfiguration.setLDAPUrl("ldap://localhost:389");
        this.advancedLdapPluginConfiguration.setConnectTimeoutMillis(1);
        this.advancedLdapPluginConfiguration.setResponseTimeoutMillis(2);
        this.advancedLdapConnectionOptionsFactory = new AdvancedLdapConnectionOptionsFactory(this.advancedLdapPluginConfiguration);
        this.advancedLdapPluginConfiguration2 = new AdvancedLdapPluginConfiguration();
        this.advancedLdapPluginConfiguration2.setLDAPUrl("ldaps://ldap.jumpcloud.com");
        this.advancedLdapConnectionOptionsFactory2 = new AdvancedLdapConnectionOptionsFactory(this.advancedLdapPluginConfiguration2);
        this.advancedLdapPluginConfiguration3 = new AdvancedLdapPluginConfiguration();
        this.advancedLdapPluginConfiguration3.setLDAPUrl("ldap://ldap.jumpcloud.com");
        this.advancedLdapConnectionOptionsFactory3 = new AdvancedLdapConnectionOptionsFactory(this.advancedLdapPluginConfiguration3);
    }

    @Test
    public void testGetConnectionOptions() throws LDAPException {
        LDAPConnectionOptions ldapConnectionOptions = advancedLdapConnectionOptionsFactory.getConnectionOptions();
        assertEquals(1,ldapConnectionOptions.getConnectTimeoutMillis());
        assertEquals(2,ldapConnectionOptions.getResponseTimeoutMillis());
        assertEquals(true,ldapConnectionOptions.abandonOnTimeout());
    }

    @Test
    public void testGetLDAPHost() throws LDAPException {
        assertEquals("localhost",advancedLdapConnectionOptionsFactory.getLDAPHost());
    }

    @Test
    public void testGetLDAPPort() throws LDAPException {
        assertEquals(389,advancedLdapConnectionOptionsFactory.getLDAPPort());
    }

    @Test
    public void testGetLDAPPortSslNotSpecified() throws LDAPException {
        assertEquals(636,advancedLdapConnectionOptionsFactory2.getLDAPPort());
    }

    @Test
    public void testIsSslBasedOnSslBased() {
        assertEquals(true, advancedLdapConnectionOptionsFactory2.isSslBased());
    }

    @Test
    public void testGetLDAPPortNonSslNotSpecified() throws LDAPException {
        assertEquals(389,advancedLdapConnectionOptionsFactory3.getLDAPPort());
    }

    @Test
    public void testIsSslBasedOnNonSslBased() {
        assertEquals(false, advancedLdapConnectionOptionsFactory3.isSslBased());
    }
}
