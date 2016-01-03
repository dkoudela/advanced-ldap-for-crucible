package com.davidkoudela.crucible.persistence;

import com.cenqua.crucible.hibernate.DBType;
import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config1.ConfigDocument;
import com.cenqua.fisheye.config1.DatabaseType;
import com.cenqua.fisheye.config1.DriverSource;
import com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Description: Factory for building {@link com.cenqua.crucible.hibernate.DatabaseConfig} and related data.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-20
 */
public class AdvancedLdapDatabaseConfigFactory {
    private static Logger log = Logger.getLogger(AdvancedLdapDatabaseConfigFactory.class);
    public static final String pluginDbName = "crucibleadldb";

    public static DatabaseConfig createDatabaseConfig(AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration) {
        return getDatabaseConfig(advancedLdapDatabaseConfiguration);
    }

    public static boolean verifyDatabaseConfig(AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration) {
        if (0 == advancedLdapDatabaseConfiguration.getDatabaseName().length()) {
            log.info("Database name must not be empty");
            return false;
        }
        DatabaseConfig databaseConfig = getDatabaseConfig(advancedLdapDatabaseConfiguration);
        return ensureDatabaseExists(databaseConfig, advancedLdapDatabaseConfiguration.getDatabaseName());
    }

    public static DatabaseConfig getCrucibleDefaultDatabaseConfig() {
        ConfigDocument configDocument = AppConfig.getsConfig().getConfigDocument();
        DatabaseConfig databaseConfig = null;

        if ((configDocument != null) && (configDocument.getConfig().isSetDatabase())) {
            DatabaseType databaseType = AppConfig.getsConfig().getConfig().getDatabase();
            databaseConfig = new DatabaseConfig(databaseType);
        } else {
            databaseConfig = new DatabaseConfig(DBType.HSQL,
                    "jdbc:hsqldb:file:" + AppConfig.getInstanceDir().getAbsolutePath() + "/var/data/crudb/crucible",
                    "sa", "", DriverSource.BUNDLED, 5, 20);
        }
        return databaseConfig;
    }

    public static String extractDatabaseName(DatabaseConfig databaseConfig) {
        if (DBType.ORACLE.equals(databaseConfig.getType())) {
            String[] ojdbcElements = databaseConfig.getJdbcURL().split(":");
            return ojdbcElements[ojdbcElements.length - 1];
        }

        String[] jdbcElements = databaseConfig.getJdbcURL().split("/");
        return jdbcElements[jdbcElements.length - 1];
    }

    protected static DatabaseConfig getDatabaseConfig(AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration) {
        ConfigDocument configDocument = AppConfig.getsConfig().getConfigDocument();
        DatabaseConfig databaseConfig = null;

        if ((configDocument != null) && (configDocument.getConfig().isSetDatabase())) {
            DatabaseType databaseType = AppConfig.getsConfig().getConfig().getDatabase();
            databaseConfig = new DatabaseConfig(databaseType);
            databaseConfig.setJdbcURL(constructJdbcUrl(databaseConfig, advancedLdapDatabaseConfiguration.getDatabaseName()));
            databaseConfig.setUsername(advancedLdapDatabaseConfiguration.getUserName());
            databaseConfig.setPassword(advancedLdapDatabaseConfiguration.getPassword());
        } else {
            databaseConfig = new DatabaseConfig(DBType.HSQL,
                    "jdbc:hsqldb:file:" + AppConfig.getInstanceDir().getAbsolutePath() + "/var/data/"+ advancedLdapDatabaseConfiguration.getDatabaseName() + "/crucible",
                    advancedLdapDatabaseConfiguration.getUserName(), advancedLdapDatabaseConfiguration.getPassword(), DriverSource.BUNDLED, 5, 20);
        }
        return databaseConfig;
    }

    protected static String constructJdbcUrl(DatabaseConfig databaseConfig, String databaseName) {
        if (DBType.ORACLE.equals(databaseConfig.getType())) {
            String[] ojdbcElements = databaseConfig.getJdbcURL().split(":");
            int lastIndex = (12 < databaseName.length()) ? 12 : databaseName.length();
            ojdbcElements[ojdbcElements.length - 1] = databaseName.substring(0, lastIndex);
            return StringUtils.join(ojdbcElements, ":");
        }

        String[] jdbcElements = databaseConfig.getJdbcURL().split("/");
        jdbcElements[jdbcElements.length - 1] = databaseName;
        return StringUtils.join(jdbcElements, "/");
    }

    private static boolean ensureDatabaseExists(DatabaseConfig databaseConfig, String databaseName) {
        Connection conn = null;
        Statement stmt = null;
        String jdbcUrl = databaseConfig.getJdbcURL();

        try {
            Class.forName(databaseConfig.getJdbcDriverClass());

            log.info("Connecting to database " + databaseName + " url: " + jdbcUrl);
            conn = DriverManager.getConnection(jdbcUrl, databaseConfig.getUsername(), databaseConfig.getPassword());

            log.info("Database " + databaseName + " exists");
            return true;
        } catch(SQLException se){
            log.info("Database " + databaseName + " doesn't exist");

        } catch(Exception e){
            //Handle errors for Class.forName
            log.info("Database existence verification failed with general error");
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
        return false;
    }
}
