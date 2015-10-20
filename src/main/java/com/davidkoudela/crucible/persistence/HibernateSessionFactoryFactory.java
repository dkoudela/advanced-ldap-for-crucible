package com.davidkoudela.crucible.persistence;

import com.cenqua.crucible.hibernate.DBType;
import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.davidkoudela.crucible.persistence.strategy.HibernateAdvancedLdapPluginConfigurationNoChangeStrategy;
import com.davidkoudela.crucible.persistence.strategy.HibernateAdvancedLdapPluginConfigurationOracleStrategy;
import com.davidkoudela.crucible.persistence.strategy.HibernateAdvancedLdapPluginConfigurationPersistenceStrategy;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Description: Factory for building {@link SessionFactory} helping with complicated HB setup.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-05-15
 */
public class HibernateSessionFactoryFactory {
    public static SessionFactory createHibernateSessionFactory() throws Exception {
        try {
            DatabaseConfig databaseConfig = AdvancedLdapDatabaseConfigFactory.createDatabaseConfig();

            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.autocommit", "false");
            configuration.setProperty("hibernate.connection.driver_class", databaseConfig.getJdbcDriverClass());
            configuration.setProperty("hibernate.connection.url", databaseConfig.getJdbcURL());
            configuration.setProperty("hibernate.connection.username", databaseConfig.getUsername());
            configuration.setProperty("hibernate.connection.password", databaseConfig.getPassword());
            configuration.setProperty("hibernate.dialect", getDialect(databaseConfig));

            configuration.setProperty("hibernate.show_sql", Boolean.toString(databaseConfig.isShowSQL()));
            configuration.setProperty("hibernate.generate_statistics", Boolean.toString(databaseConfig.isGenerateStatistics()));

            configuration.setProperty("hibernate.connection.isolation", Integer.toString(2));
            configuration.setProperty("hibernate.bytecode.use_reflection_optimizer", "true");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            if (DBType.ORACLE.equals(databaseConfig.getType())) {
                configuration.setProperty("hibernate.jdbc.batch_size", "0");
                configuration.setProperty("hibernate.dbcp.ps.maxIdle", "0");
            }

            configuration.addProperties(databaseConfig.getProperties());
            configuration.addClass(com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration.class);
            return configuration.buildSessionFactory();
        } catch (Exception e) {
            System.out.println("HibernateSessionFactoryFactory: Exception: " + e);
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : e.getCause().getStackTrace()) {
                sb.append(element.toString());
                sb.append("\n");
            }
            System.out.println("HibernateSessionFactoryFactory: Exception: " + sb);
            throw new Exception(e);
        } catch (Throwable e) {
            System.out.println("HibernateSessionFactoryFactory: Unexpected Exception: " + e);
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : e.getCause().getStackTrace()) {
                sb.append(element.toString());
                sb.append("\n");
            }
            System.out.println("HibernateSessionFactoryFactory: Unexpected Exception: " + sb);
            throw new Exception(e);
        }
    }

    public static HibernateAdvancedLdapPluginConfigurationPersistenceStrategy createHibernateAdvancedLdapPluginConfigurationPersistenceStrategy() throws Exception {
        DatabaseConfig databaseConfig = AdvancedLdapDatabaseConfigFactory.createDatabaseConfig();
        HibernateAdvancedLdapPluginConfigurationPersistenceStrategy hibernateAdvancedLdapPluginConfigurationPersistenceStrategy = null;

        if (DBType.ORACLE.equals(databaseConfig.getType())) {
            hibernateAdvancedLdapPluginConfigurationPersistenceStrategy = new HibernateAdvancedLdapPluginConfigurationOracleStrategy();
        } else {
            hibernateAdvancedLdapPluginConfigurationPersistenceStrategy = new HibernateAdvancedLdapPluginConfigurationNoChangeStrategy();
        }

        return hibernateAdvancedLdapPluginConfigurationPersistenceStrategy;
    }

    protected static String getDialect(DatabaseConfig databaseConfig) {
        String dialect = databaseConfig.getDialect();
        if (DBType.ORACLE.equals(databaseConfig.getType())) {
            dialect = HibernateAdvancedLdapOracle11gDialect.class.getCanonicalName();
        }
        System.out.println("Database dialect: " + dialect);
        return dialect;
    }
}
