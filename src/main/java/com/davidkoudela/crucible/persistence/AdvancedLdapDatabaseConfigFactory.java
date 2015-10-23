package com.davidkoudela.crucible.persistence;

import com.cenqua.crucible.hibernate.DBType;
import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config1.ConfigDocument;
import com.cenqua.fisheye.config1.DatabaseType;
import com.cenqua.fisheye.config1.DriverSource;
import org.apache.commons.lang.StringUtils;

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
    public static final String pluginDbName = "crucibleadldb";

    public static DatabaseConfig createDatabaseConfig() {
        return getDatabaseConfig();
    }

    public static boolean verifyDatabaseConfig() {
        DatabaseConfig databaseConfig = getDatabaseConfig();
        return ensureDatabaseExists(databaseConfig);
    }

    protected static DatabaseConfig getDatabaseConfig() {
        ConfigDocument configDocument = AppConfig.getsConfig().getConfigDocument();
        DatabaseConfig databaseConfig = null;

        if ((configDocument != null) && (configDocument.getConfig().isSetDatabase())) {
            DatabaseType databaseType = AppConfig.getsConfig().getConfig().getDatabase();
            databaseConfig = new DatabaseConfig(databaseType);
            databaseConfig.setJdbcURL(constructJdbcUrl(databaseConfig));
        } else {
            databaseConfig = new DatabaseConfig(DBType.HSQL,
                    "jdbc:hsqldb:file:" + AppConfig.getInstanceDir().getAbsolutePath() + "/var/data/adldb/crucible",
                    "sa", "", DriverSource.BUNDLED, 5, 20);
        }
        return databaseConfig;
    }

    protected static String constructJdbcUrl(DatabaseConfig databaseConfig) {
        if (DBType.ORACLE.equals(databaseConfig.getType())) {
            String[] ojdbcElements = databaseConfig.getJdbcURL().split(":");
            int lastIndex = (12 < pluginDbName.length()) ? 12 : pluginDbName.length();
            ojdbcElements[ojdbcElements.length - 1] = pluginDbName.substring(0, lastIndex);
            return StringUtils.join(ojdbcElements, ":");
        }

        String[] jdbcElements = databaseConfig.getJdbcURL().split("/");
        jdbcElements[jdbcElements.length - 1] = pluginDbName;
        return StringUtils.join(jdbcElements, "/");
    }

    private static boolean ensureDatabaseExists(DatabaseConfig databaseConfig) {
        Connection conn = null;
        Statement stmt = null;
        String jdbcUrl = constructJdbcUrl(databaseConfig);

        try {
            Class.forName(databaseConfig.getJdbcDriverClass());

            System.out.println("Connecting to database " + pluginDbName);
            conn = DriverManager.getConnection(jdbcUrl, databaseConfig.getUsername(), databaseConfig.getPassword());

            System.out.println("Database " + pluginDbName + " exists");
            return true;
        } catch(SQLException se){
            System.out.println("Database " + pluginDbName + " doesn't exist");

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
        return false;
    }
}
