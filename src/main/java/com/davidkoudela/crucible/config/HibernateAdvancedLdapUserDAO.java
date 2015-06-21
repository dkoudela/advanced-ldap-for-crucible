package com.davidkoudela.crucible.config;

import com.davidkoudela.crucible.ldap.model.AdvancedLdapPerson;

/**
 * Description: {@link HibernateAdvancedLdapUserDAO} represents an interface for Data Access Object class
 *              for creating Crucible User instances.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-21
 */
public interface HibernateAdvancedLdapUserDAO {
    public abstract void create(AdvancedLdapPerson advancedLdapPerson);
}
