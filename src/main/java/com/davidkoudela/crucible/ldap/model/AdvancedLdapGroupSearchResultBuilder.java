package com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchResultBuilder;
import java.util.List;

/**
 * Description: {@link AdvancedLdapGroupSearchResultBuilder} extends an interface {@link AdvancedLdapSearchResultBuilder}
 *              adding methods for group specific operations.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-21
 */
public interface AdvancedLdapGroupSearchResultBuilder extends AdvancedLdapSearchResultBuilder {
    public List<AdvancedLdapGroup> getGroups();
}
