package com.davidkoudela.crucible.persistence;

/**
 * Description: {@link HibernateAdvancedLdapUserDAO} represents an interface for Data Access Object class
 *              for creating Crucible User instances.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-21
 */
public interface HibernateAdvancedLdapUserDAO {
    void create(String uid, String displayName, String email);
}
