package com.davidkoudela.crucible.config;

import com.cenqua.crucible.hibernate.DBType;
import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config1.ConfigDocument;
import com.cenqua.fisheye.config1.DatabaseType;
import com.cenqua.fisheye.config1.DriverSource;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.postgresql.util.PSQLException;

import java.sql.*;

/**
 * Description: Factory for building {@link SessionFactory} helping with complicated HB setup.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-05-15
 */
public class HibernateSessionFactoryFactory {
    public static final String pluginDbName = "crucibleadldb";

    public static SessionFactory createHibernateSessionFactory() throws Exception {
        try {
            ConfigDocument configDocument = AppConfig.getsConfig().getConfigDocument();
            DatabaseConfig databaseConfig = null;

            if ((configDocument != null) && (configDocument.getConfig().isSetDatabase())) {
                DatabaseType databaseType = AppConfig.getsConfig().getConfig().getDatabase();
                databaseConfig = new DatabaseConfig(databaseType);
                ensureDatabaseExists(databaseConfig);
                databaseConfig.setJdbcURL(constructJdbcUrl(databaseConfig));
            } else {
                databaseConfig = new DatabaseConfig(DBType.HSQL,
                        "jdbc:hsqldb:file:" + AppConfig.getInstanceDir().getAbsolutePath() + "/var/data/adldb/crucible",
                        "sa", "", DriverSource.BUNDLED, 5, 20);
            }

            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.autocommit", "false");
            configuration.setProperty("hibernate.connection.driver_class", databaseConfig.getJdbcDriverClass());
            configuration.setProperty("hibernate.connection.url", databaseConfig.getJdbcURL());
            configuration.setProperty("hibernate.connection.username", databaseConfig.getUsername());
            configuration.setProperty("hibernate.connection.password", databaseConfig.getPassword());
            configuration.setProperty("hibernate.dialect", databaseConfig.getDialect());

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

    protected static String constructJdbcUrl(DatabaseConfig databaseConfig) {
        String[] jdbcElements = databaseConfig.getJdbcURL().split("/");
        jdbcElements[jdbcElements.length-1] = pluginDbName;
        return StringUtils.join(jdbcElements, "/");
    }

    private static void ensureDatabaseExists(DatabaseConfig databaseConfig) {
        Connection conn = null;
        Statement stmt = null;
        String jdbcUrl = constructJdbcUrl(databaseConfig);

        try {
            Class.forName(databaseConfig.getJdbcDriverClass());

            System.out.println("Connecting to database " + pluginDbName);
            conn = DriverManager.getConnection(jdbcUrl, databaseConfig.getUsername(), databaseConfig.getPassword());

            System.out.println("Database " + pluginDbName + " already exists");
        } catch(SQLException se){
            System.out.println("Database " + pluginDbName + " doesn't exist");

            try {
                conn = DriverManager.getConnection(databaseConfig.getJdbcURL(), databaseConfig.getUsername(), databaseConfig.getPassword());
                String sqlCreateDatabase = getCreateDbStatement(databaseConfig);
                String sqlGrant = getGrantDbStatement(databaseConfig);
                String sqlFlush = getFlushDbStatement(databaseConfig);

                if (sqlCreateDatabase != null && sqlGrant != null) {
                    System.out.println("Creating database " + pluginDbName);
                    stmt = conn.createStatement();

                    stmt.executeUpdate(sqlCreateDatabase);
                    stmt.executeUpdate(sqlGrant);
                    if (null != sqlFlush)
                        stmt.executeUpdate(sqlFlush);

                    System.out.println("Database " + pluginDbName + " created successfully");
                } else {
                    System.out.println("Database " + pluginDbName + " cannot be created automatically");
                    System.out.println("Create database " + pluginDbName + " in the same way as the crucible database");
                }
            } catch (Exception e) {
                System.out.println("Creating database " + pluginDbName + " failed");
                System.out.println("Create database " + pluginDbName + " in the same way as the crucible database");
                e.printStackTrace();
            }
        } catch(Exception e){
            //Handle errors for Class.forName
            System.out.println("Database existence verification failed with general error");
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if(stmt!=null)
                    stmt.close();
            } catch(SQLException se2){
            }
            try {
                if(conn!=null)
                    conn.close();
            } catch(SQLException se){
                se.printStackTrace();
            }
        }
    }

    protected static String getCreateDbStatement(DatabaseConfig databaseConfig) {
        if (databaseConfig.getType() == DBType.POSTGRESQL)
            return "CREATE DATABASE " + pluginDbName + " ENCODING 'UTF-8' OWNER " + databaseConfig.getUsername();
        else  if (databaseConfig.getType() == DBType.MYSQL)
            return "CREATE DATABASE " + pluginDbName + " CHARACTER SET utf8 COLLATE utf8_bin";
        else
            return null;
    }

    protected static String getGrantDbStatement(DatabaseConfig databaseConfig) {
        if (databaseConfig.getType() == DBType.POSTGRESQL)
            return "grant all on database " + pluginDbName + " to " + databaseConfig.getUsername();
        else  if (databaseConfig.getType() == DBType.MYSQL)
            return "GRANT ALL PRIVILEGES ON " + pluginDbName + " TO '" + databaseConfig.getUsername() + "'@'localhost' IDENTIFIED BY '" + databaseConfig.getPassword() + "'";
        else
            return null;
    }

    protected static String getFlushDbStatement(DatabaseConfig databaseConfig) {
        if (databaseConfig.getType() == DBType.MYSQL)
            return "FLUSH PRIVILEGES";
        else
            return null;
    }
}
