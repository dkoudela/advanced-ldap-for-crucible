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
            return "GRANT ALL PRIVILEGES ON " + pluginDbName + ".* TO '" + databaseConfig.getUsername() + "'@'localhost' IDENTIFIED BY '" + databaseConfig.getPassword() + "'";
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
