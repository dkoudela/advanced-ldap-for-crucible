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

    public HibernateAdvancedLdapServiceImpl() {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName(AdvancedLdapDatabaseConfigFactory.pluginDbName);
        advancedLdapDatabaseConfiguration.setUserName(AdvancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig().getUsername());
        advancedLdapDatabaseConfiguration.setPassword(AdvancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig().getPassword());
        initiate(advancedLdapDatabaseConfiguration);
    }

    @Override
    public void initiate(AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration) {
        try {
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
    public HibernateAdvancedLdapInstance getInstance() {
        return this.hibernateAdvancedLdapInstance;
    }

}
