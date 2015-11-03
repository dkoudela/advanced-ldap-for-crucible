package ut.com.davidkoudela.crucible.config;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Description: Testing {@link AdvancedLdapPluginConfiguration}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-19
 */
public class AdvancedLdapPluginConfigurationTest extends TestCase {
    @Test
    public void testSetGetForCoverage() {
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();

        advancedLdapPluginConfiguration.setId(1);
        advancedLdapPluginConfiguration.setConnectTimeoutMillis(2);
        advancedLdapPluginConfiguration.setResponseTimeoutMillis(3);
        advancedLdapPluginConfiguration.setLDAPPageSize(4);
        advancedLdapPluginConfiguration.setLDAPSyncPeriod(5);
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        advancedLdapPluginConfiguration.setLDAPBindDN("bindDn");
        advancedLdapPluginConfiguration.setLDAPBindPassword("bindPwd");
        advancedLdapPluginConfiguration.setLDAPBaseDN("baseDn");
        advancedLdapPluginConfiguration.setUserFilterKey("userFilter");
        advancedLdapPluginConfiguration.setDisplayNameAttributeKey("displayName");
        advancedLdapPluginConfiguration.setEmailAttributeKey("email");
        advancedLdapPluginConfiguration.setUIDAttributeKey("uid");
        advancedLdapPluginConfiguration.setUserGroupNamesKey("memberOf");
        advancedLdapPluginConfiguration.setGroupFilterKey("groupFilter");
        advancedLdapPluginConfiguration.setGIDAttributeKey("gid");
        advancedLdapPluginConfiguration.setGroupDisplayNameKey("member");
        advancedLdapPluginConfiguration.setUserNamesKey("names");
        advancedLdapPluginConfiguration.setRecordRevision("666");

        assertEquals(1, advancedLdapPluginConfiguration.getId());
        assertEquals(2, advancedLdapPluginConfiguration.getConnectTimeoutMillis());
        assertEquals(3, advancedLdapPluginConfiguration.getResponseTimeoutMillis());
        assertEquals(4, advancedLdapPluginConfiguration.getLDAPPageSize());
        assertEquals(5, advancedLdapPluginConfiguration.getLDAPSyncPeriod());
        assertEquals("url", advancedLdapPluginConfiguration.getLDAPUrl());
        assertEquals("bindDn", advancedLdapPluginConfiguration.getLDAPBindDN());
        assertEquals("bindPwd", advancedLdapPluginConfiguration.getLDAPBindPassword());
        assertEquals("baseDn", advancedLdapPluginConfiguration.getLDAPBaseDN());
        assertEquals("userFilter", advancedLdapPluginConfiguration.getUserFilterKey());
        assertEquals("displayName", advancedLdapPluginConfiguration.getDisplayNameAttributeKey());
        assertEquals("email", advancedLdapPluginConfiguration.getEmailAttributeKey());
        assertEquals("uid", advancedLdapPluginConfiguration.getUIDAttributeKey());
        assertEquals("memberOf", advancedLdapPluginConfiguration.getUserGroupNamesKey());
        assertEquals("groupFilter", advancedLdapPluginConfiguration.getGroupFilterKey());
        assertEquals("gid", advancedLdapPluginConfiguration.getGIDAttributeKey());
        assertEquals("member", advancedLdapPluginConfiguration.getGroupDisplayNameKey());
        assertEquals("names", advancedLdapPluginConfiguration.getUserNamesKey());
        assertEquals("666", advancedLdapPluginConfiguration.getRecordRevision());
    }
}
