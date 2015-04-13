package com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchResultBuilder;

import java.util.List;

/**
 * Description: {@link AdvancedLdapPersonSearchResultBuilder} extends an interface {@link AdvancedLdapSearchResultBuilder}
 *              adding methods for person specific operations.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-15
 */
public interface AdvancedLdapPersonSearchResultBuilder extends AdvancedLdapSearchResultBuilder {
    public List<AdvancedLdapPerson> getPersons();
}
