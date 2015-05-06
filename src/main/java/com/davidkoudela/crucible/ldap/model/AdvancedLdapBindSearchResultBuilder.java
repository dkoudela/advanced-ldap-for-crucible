package com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.ldap.connect.AdvancedLdapSearchResultBuilder;

import java.util.List;

/**
 * Description: {@link AdvancedLdapBindSearchResultBuilder} extends an interface {@link AdvancedLdapSearchResultBuilder}
 *              adding methods for bind specific operations.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-05-06
 */
public interface AdvancedLdapBindSearchResultBuilder extends AdvancedLdapSearchResultBuilder {
    public List<AdvancedLdapBind> getBinds();
}
