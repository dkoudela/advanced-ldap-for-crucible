package ut.com.davidkoudela.crucible.ldap.connect;

import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchFilterFactory;
import com.unboundid.ldap.sdk.LDAPException;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Description: Testing {@link AdvancedLdapSearchFilterFactory}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-18
 */
public class AdvancedLdapSearchFilterFactoryTest extends TestCase {
    @Test
    public void testGetSearchFilterForUser() throws LDAPException {
        assertEquals("(&(objectCategory=user)(sAMAccountName=dkoudela))",
                AdvancedLdapSearchFilterFactory.getSearchFilterForUser(
                        "(&(objectCategory=user)(sAMAccountName=${USERNAME}))",
                        "dkoudela").toString()
        );
    }

    @Test
    public void testGetSearchFilterForAllUsers() throws LDAPException {
        assertEquals("(&(objectCategory=user)(sAMAccountName=*))",
                AdvancedLdapSearchFilterFactory.getSearchFilterForAllUsers(
                        "(&(objectCategory=user)(sAMAccountName=${USERNAME}))").toString()
        );
    }

    @Test
    public void testGetSearchFilterForAllGroups() throws LDAPException {
        assertEquals("(&(objectCategory=group)(sAMAccountName=*))",
                AdvancedLdapSearchFilterFactory.getSearchFilterForAllGroups(
                        "(&(objectCategory=group)(sAMAccountName=${GROUPNAME}))").toString()
        );
    }
}

