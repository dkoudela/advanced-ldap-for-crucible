package com.davidkoudela.crucible.ldap.connect;

import com.davidkoudela.crucible.config.AdvancedLdapOptions;
import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPURL;

/**
 * Description: Factory for building {@link LDAPConnectionOptions} and related objects
 *              used for LDAP connection establishment.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-25
 */
public class AdvancedLdapConnectionOptionsFactory {
    private final AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private final LDAPURL ldapurl;

    public AdvancedLdapConnectionOptionsFactory(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration) throws LDAPException {
        this.advancedLdapPluginConfiguration = advancedLdapPluginConfiguration;
        ldapurl = new LDAPURL(advancedLdapPluginConfiguration.getLDAPUrl());
    }

    public LDAPConnectionOptions getConnectionOptions() {
        LDAPConnectionOptions ldapConnectionOptions = new LDAPConnectionOptions();
        ldapConnectionOptions.setAbandonOnTimeout(true);
        ldapConnectionOptions.setConnectTimeoutMillis(this.advancedLdapPluginConfiguration.getConnectTimeoutMillis());
        ldapConnectionOptions.setResponseTimeoutMillis(this.advancedLdapPluginConfiguration.getResponseTimeoutMillis());
        return ldapConnectionOptions;
    }

    public String getLDAPHost() {
        return this.ldapurl.getHost();
    }

    public int getLDAPPort() {
        return this.ldapurl.getPort();
    }
}
