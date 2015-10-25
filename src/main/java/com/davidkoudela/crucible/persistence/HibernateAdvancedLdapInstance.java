package com.davidkoudela.crucible.persistence;

import com.davidkoudela.crucible.persistence.strategy.HibernateAdvancedLdapPluginConfigurationPersistenceStrategy;
import org.hibernate.SessionFactory;

/**
 * Description: {@link HibernateAdvancedLdapInstance} represents a holder class for hibernate objects needed
 *              by the plugin's DAO objects for managing the persistence storage.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-24
 */
public class HibernateAdvancedLdapInstance {
    private SessionFactory sessionFactory;
    private HibernateAdvancedLdapPluginConfigurationPersistenceStrategy hibernateAdvancedLdapPluginConfigurationPersistenceStrategy;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public HibernateAdvancedLdapPluginConfigurationPersistenceStrategy getHibernateAdvancedLdapPluginConfigurationPersistenceStrategy() {
        return hibernateAdvancedLdapPluginConfigurationPersistenceStrategy;
    }

    public void setHibernateAdvancedLdapPluginConfigurationPersistenceStrategy(HibernateAdvancedLdapPluginConfigurationPersistenceStrategy hibernateAdvancedLdapPluginConfigurationPersistenceStrategy) {
        this.hibernateAdvancedLdapPluginConfigurationPersistenceStrategy = hibernateAdvancedLdapPluginConfigurationPersistenceStrategy;
    }
}
