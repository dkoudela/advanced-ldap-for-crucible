package com.davidkoudela.crucible.persistence;

import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration;
import org.springframework.stereotype.Component;

/**
 * Description: Implementation of {@link HibernateAdvancedLdapService} providing methods for (re-)initialization
 *              and (re-)configuration of Hibernate storage.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-24
 */
@Component("hibernateAdvancedLdapService")
public class HibernateAdvancedLdapServiceImpl implements HibernateAdvancedLdapService {
    private HibernateAdvancedLdapInstance hibernateAdvancedLdapInstance;
    private AdvancedLdapDatabaseConfigurationDAO advancedLdapDatabaseConfigurationDAO;

    public HibernateAdvancedLdapServiceImpl(AdvancedLdapDatabaseConfigurationDAO advancedLdapDatabaseConfigurationDAO) {
        this.advancedLdapDatabaseConfigurationDAO = advancedLdapDatabaseConfigurationDAO;
        initiate();
    }

    @Override
    public synchronized void initiate() {
        try {
            AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = this.advancedLdapDatabaseConfigurationDAO.get();
            this.hibernateAdvancedLdapInstance = new HibernateAdvancedLdapInstance();
            DatabaseConfig databaseConfig = AdvancedLdapDatabaseConfigFactory.createDatabaseConfig(advancedLdapDatabaseConfiguration);
            this.hibernateAdvancedLdapInstance.setSessionFactory(HibernateSessionFactoryFactory.createHibernateSessionFactory(databaseConfig));
            this.hibernateAdvancedLdapInstance.setHibernateAdvancedLdapPluginConfigurationPersistenceStrategy(HibernateSessionFactoryFactory.createHibernateAdvancedLdapPluginConfigurationPersistenceStrategy(databaseConfig));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyDatabaseConfig(AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration) {
        return AdvancedLdapDatabaseConfigFactory.verifyDatabaseConfig(advancedLdapDatabaseConfiguration);
    }

    @Override
    public synchronized HibernateAdvancedLdapInstance getInstance() {
        return this.hibernateAdvancedLdapInstance;
    }

}
