package com.davidkoudela.crucible.config;

import com.cenqua.crucible.hibernate.BoneCPConnectionHook;
import com.cenqua.crucible.hibernate.DBType;
import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config1.ConfigDocument;
import com.cenqua.fisheye.config1.DriverSource;
import com.jolbox.bonecp.provider.BoneCPConnectionProvider;
import org.apache.commons.lang.math.NumberUtils;
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
            ConfigDocument configDocument = AppConfig.getsConfig().getConfigDocument();
            DatabaseConfig databaseConfig = null;

            if ((configDocument != null) && (configDocument.getConfig().isSetDatabase()))
                databaseConfig = new DatabaseConfig(AppConfig.getsConfig().getConfig().getDatabase());
            else
                databaseConfig = new DatabaseConfig(DBType.HSQL,
                        "jdbc:hsqldb:file:" + AppConfig.getInstanceDir().getAbsolutePath() + "/var/data/crudb/crucible",
                        "sa", "", DriverSource.BUNDLED, 5, 20);

            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.autocommit", "false");
            configuration.setProperty("hibernate.connection.driver_class", databaseConfig.getJdbcDriverClass());
            configuration.setProperty("hibernate.connection.url", databaseConfig.getJdbcURL());
            configuration.setProperty("hibernate.connection.username", databaseConfig.getUsername());
            configuration.setProperty("hibernate.connection.password", databaseConfig.getPassword());
            configuration.setProperty("hibernate.dialect", databaseConfig.getDialect());

            configuration.setProperty("hibernate.connection.provider_class", BoneCPConnectionProvider.class.getName());
            int boneCpPartitionsCount = NumberUtils.toInt(databaseConfig.getProperties().getProperty("bonecp.partitionCount"), 3);
            int minPerPartition = databaseConfig.getMinPoolSize() / boneCpPartitionsCount;
            minPerPartition = minPerPartition < 1 ? 1 : minPerPartition;
            int maxPerPartition = databaseConfig.getMaxPoolSize() / boneCpPartitionsCount;
            maxPerPartition = maxPerPartition < 1 ? 1 : maxPerPartition;
            configuration.setProperty("bonecp.idleMaxAgeInMinutes", "0");
            configuration.setProperty("bonecp.idleConnectionTestPeriodInMinutes", "60");
            configuration.setProperty("bonecp.partitionCount", boneCpPartitionsCount + "");
            configuration.setProperty("bonecp.acquireIncrement", "2");
            configuration.setProperty("bonecp.maxConnectionsPerPartition", maxPerPartition + "");
            configuration.setProperty("bonecp.minConnectionsPerPartition", minPerPartition + "");
            configuration.setProperty("bonecp.statementsCacheSize", "50");
            configuration.setProperty("bonecp.releaseHelperThreads", "3");
            configuration.setProperty("bonecp.poolName", "mainPool");
            configuration.setProperty("bonecp.connectionHookClassName", BoneCPConnectionHook.class.getName());

            configuration.setProperty("hibernate.show_sql", Boolean.toString(databaseConfig.isShowSQL()));
            configuration.setProperty("hibernate.generate_statistics", Boolean.toString(databaseConfig.isGenerateStatistics()));
            configuration.setProperty("net.sf.ehcache.configurationResourceName", "ehcache.xml");
            configuration.setProperty("hibernate.cache.region.factory_class", "net.sf.ehcache.hibernate.SingletonEhCacheRegionFactory");

            configuration.setProperty("hibernate.cache.use_second_level_cache", "true");
            configuration.setProperty("hibernate.cache.use_query_cache", "true");

            configuration.setProperty("hibernate.connection.isolation", Integer.toString(2));
            configuration.setProperty("hibernate.bytecode.use_reflection_optimizer", "true");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            if (DBType.ORACLE.equals(databaseConfig.getType())) {
                configuration.setProperty("hibernate.jdbc.batch_size", "0");
                configuration.setProperty("bonecp.statementsCacheSize", "0");
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
        }
    }
}
