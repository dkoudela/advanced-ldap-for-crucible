package com.davidkoudela.crucible.persistence;

import com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration;

/**
 * Description: {@link AdvancedLdapDatabaseConfigurationDAO} represents an interface for Data Access Object class
 *              for {@link com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration}.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-25
 */
public interface AdvancedLdapDatabaseConfigurationDAO {
    public abstract void store(AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration) throws Exception;
    public abstract AdvancedLdapDatabaseConfiguration get();
}
