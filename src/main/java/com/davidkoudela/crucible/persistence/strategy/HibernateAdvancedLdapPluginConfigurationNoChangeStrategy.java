package com.davidkoudela.crucible.persistence.strategy;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;

/**
 * Description: {@link HibernateAdvancedLdapPluginConfigurationNoChangeStrategy}
 *              provides a persistence storage strategies for databases without specific
 *              storage requirements.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-09-04
 */
public class HibernateAdvancedLdapPluginConfigurationNoChangeStrategy implements HibernateAdvancedLdapPluginConfigurationPersistenceStrategy {
    @Override
    public AdvancedLdapPluginConfiguration transformFromStorage(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration) {
        return advancedLdapPluginConfiguration;
    }

    @Override
    public AdvancedLdapPluginConfiguration transformToStorage(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration) {
        return advancedLdapPluginConfiguration;
    }
}
