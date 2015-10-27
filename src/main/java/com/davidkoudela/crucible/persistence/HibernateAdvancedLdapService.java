package com.davidkoudela.crucible.persistence;

import com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration;

/**
 * Description: {@link HibernateAdvancedLdapService} represents an interface for managing
 *              plugin underlying Hibernate session setup.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-24
 */
public interface HibernateAdvancedLdapService {
    public void initiate();
    public boolean verifyDatabaseConfig(AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration);
    public HibernateAdvancedLdapInstance getInstance();
}
