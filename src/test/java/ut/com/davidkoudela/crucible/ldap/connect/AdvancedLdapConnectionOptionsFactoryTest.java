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

    @Before
    public void init() throws LDAPException {
        this.advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        this.advancedLdapPluginConfiguration.setLDAPUrl("ldap://localhost:389");
        this.advancedLdapPluginConfiguration.setConnectTimeoutMillis(1);
        this.advancedLdapPluginConfiguration.setResponseTimeoutMillis(2);
        this.advancedLdapConnectionOptionsFactory = new AdvancedLdapConnectionOptionsFactory(this.advancedLdapPluginConfiguration);
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
}
