package com.davidkoudela.crucible.persistence.strategy;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;

/**
 * Description: {@link HibernateAdvancedLdapPluginConfigurationPersistenceStrategy} represents an interface used by
 *              {@link com.davidkoudela.crucible.persistence.HibernateAdvancedLdapPluginConfigurationDAO} instances
 *              for dynamic exchange of persistence storage strategies based on underlying database.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-09-04
 */
public interface HibernateAdvancedLdapPluginConfigurationPersistenceStrategy {
    AdvancedLdapPluginConfiguration transformFromStorage(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration);
    AdvancedLdapPluginConfiguration transformToStorage(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration);
}
