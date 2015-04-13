package com.davidkoudela.crucible.ldap.connect;

import com.unboundid.ldap.sdk.*;

/**
 * Description: Factory for building {@link Filter} used for LDAP search requests.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-21
 */
public class AdvancedLdapSearchFilterFactory {
    public static Filter getSearchFilterForUser(String filter, String user) throws LDAPException{
        String replacedFilter = filter.replace("${USERNAME}", user);
        return Filter.create(replacedFilter);
    }

    public static Filter getSearchFilterForAllUsers(String filter) throws LDAPException{
        String replacedFilter = filter.replace("${USERNAME}", "*");
        return Filter.create(replacedFilter);
    }
}
