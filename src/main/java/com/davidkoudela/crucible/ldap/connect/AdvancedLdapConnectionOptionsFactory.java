package com.davidkoudela.crucible.ldap.connect;

import com.davidkoudela.crucible.config.AdvancedLdapOptions;
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
    private final AdvancedLdapOptions advancedLdapOptions;
    private final LDAPURL ldapurl;

    public AdvancedLdapConnectionOptionsFactory(AdvancedLdapOptions advancedLdapOptions) throws LDAPException {
        this.advancedLdapOptions = advancedLdapOptions;
        ldapurl = new LDAPURL(advancedLdapOptions.getLDAPUrl());
    }

    public LDAPConnectionOptions getConnectionOptions(AdvancedLdapOptions advancedLdapOptions) {
        LDAPConnectionOptions ldapConnectionOptions = new LDAPConnectionOptions();
        ldapConnectionOptions.setAbandonOnTimeout(true);
        ldapConnectionOptions.setConnectTimeoutMillis(advancedLdapOptions.getConnectTimeoutMillis());
        ldapConnectionOptions.setResponseTimeoutMillis(advancedLdapOptions.getResponseTimeoutMillis());
        return ldapConnectionOptions;
    }

    public String getLDAPHost() {
        return this.ldapurl.getHost();
    }

    public int getLDAPPort() {
        return this.ldapurl.getPort();
    }
}
