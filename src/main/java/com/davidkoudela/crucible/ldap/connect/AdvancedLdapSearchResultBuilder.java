package com.davidkoudela.crucible.ldap.connect;

import com.unboundid.ldap.sdk.SearchResultEntry;

/**
 * Description: {@link AdvancedLdapSearchResultBuilder} represents an interface used by {@link AdvancedLdapConnector}
 *              for handling of the search results retrieved from the remote LDAP server.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-20
 */
public interface AdvancedLdapSearchResultBuilder {
    public void handlePagedSearchResult(SearchResultEntry searchResultEntry);
}
