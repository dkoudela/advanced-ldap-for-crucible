package com.davidkoudela.crucible.ldap.model;

import java.util.Set;

/**
 * Description: {@link AdvancedLdapNestedGroupSearchResultBuilder} extends an interface {@link AdvancedLdapGroupSearchResultBuilder}
 *              adding methods for nested group specific operations.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-11-15
 */
public interface AdvancedLdapNestedGroupSearchResultBuilder extends AdvancedLdapGroupSearchResultBuilder {
    public Set<String> getNestedGroups();
}
