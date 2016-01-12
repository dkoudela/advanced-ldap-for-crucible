package ut.com.davidkoudela.crucible.persistence;

import com.cenqua.crucible.hibernate.DBType;
import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.cenqua.fisheye.AppConfig;
import com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration;
import com.davidkoudela.crucible.persistence.AdvancedLdapDatabaseConfigFactory;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Description: Testing {@link AdvancedLdapDatabaseConfigFactory}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-20
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapDatabaseConfigFactoryTest extends TestCase {
    private static DatabaseConfig databaseConfig;
    private static DatabaseConfig databaseConfig2;
    private static DatabaseConfig databaseConfig3;
    private static DatabaseConfig databaseConfig4;
    private static AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfigurationDefault;

    @Before
    public void init() {
        this.databaseConfig = new DatabaseConfig(DBType.MYSQL);
        this.databaseConfig.setJdbcURL("jdbc:mysql://localhost:3306/crucible");
        this.databaseConfig.setUsername("crucible");
        this.databaseConfig.setPassword("password");
        this.databaseConfig2 = new DatabaseConfig(DBType.POSTGRESQL);
        this.databaseConfig2.setUsername("crucible");
        this.databaseConfig2.setPassword("password");
        this.databaseConfig3 = new DatabaseConfig(DBType.ORACLE);
        this.databaseConfig3.setJdbcURL("jdbc:oracle:thin:@localhost:1521:oracru");
        this.databaseConfig4 = new DatabaseConfig(DBType.ORACLE);
        this.databaseConfig4.setJdbcURL("jdbc:oracle:thin:@localhost:1521:oraclecrucible");
        this.advancedLdapDatabaseConfigurationDefault = new AdvancedLdapDatabaseConfiguration();
        this.advancedLdapDatabaseConfigurationDefault.setDatabaseName("crucible");
        this.advancedLdapDatabaseConfigurationDefault.setUserName("sa");
        this.advancedLdapDatabaseConfigurationDefault.setPassword("");
    }

    public static class AdvancedLdapDatabaseConfigFactoryDummy extends AdvancedLdapDatabaseConfigFactory {
        public static String constructJdbcUrl(DatabaseConfig databaseConfig, String databaseName) {
            return AdvancedLdapDatabaseConfigFactory.constructJdbcUrl(databaseConfig, databaseName);
        }
    }

    @Test
    public void testConstructJdbcUrlMySQL() {
        assertEquals("jdbc:mysql://localhost:3306/myowndb", AdvancedLdapDatabaseConfigFactoryDummy.constructJdbcUrl(this.databaseConfig, "myowndb"));
    }

    @Test
    public void testConstructJdbcUrlOracleShort() {
        assertEquals("jdbc:oracle:thin:@localhost:1521:myowndb", AdvancedLdapDatabaseConfigFactoryDummy.constructJdbcUrl(this.databaseConfig3, "myowndb"));
    }

    @Test
    public void testConstructJdbcUrlOracleLong() {
        assertEquals("jdbc:oracle:thin:@localhost:1521:myowndbwitha", AdvancedLdapDatabaseConfigFactoryDummy.constructJdbcUrl(this.databaseConfig3, "myowndbwithalongname"));
    }

    @Test
    public void testGetCrucibleDefaultDatabaseConfig() {
        DatabaseConfig databaseConfigLocal = AdvancedLdapDatabaseConfigFactoryDummy.getCrucibleDefaultDatabaseConfig();
        assertEquals("org.hibernate.dialect.HSQLDialect", databaseConfigLocal.getDialect());
        assertEquals("jdbc:hsqldb:file:" + AppConfig.getInstanceDir().getAbsolutePath() + "/var/data/crudb/crucible", databaseConfigLocal.getJdbcURL());
    }

    @Test
    public void testExtractDatabaseNameMySQL() {
        assertEquals("crucible", AdvancedLdapDatabaseConfigFactoryDummy.extractDatabaseName(databaseConfig));
    }

    @Test
    public void testExtractDatabaseNameOracleShort() {
        assertEquals("oracru", AdvancedLdapDatabaseConfigFactoryDummy.extractDatabaseName(databaseConfig3));
    }

    @Test
    public void testExtractDatabaseNameOracleLong() {
        assertEquals("oraclecrucible", AdvancedLdapDatabaseConfigFactoryDummy.extractDatabaseName(databaseConfig4));
    }

    @Test
    public void testCreateDatabaseConfig() {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("myowndb");
        advancedLdapDatabaseConfiguration.setUserName("cru");
        advancedLdapDatabaseConfiguration.setPassword("pass");
        DatabaseConfig databaseConfigLocal = AdvancedLdapDatabaseConfigFactoryDummy.createDatabaseConfig(advancedLdapDatabaseConfiguration);
        assertEquals("jdbc:hsqldb:file:" + AppConfig.getInstanceDir().getAbsolutePath() + "/var/data/myowndb/crucible", databaseConfigLocal.getJdbcURL());
        assertEquals("cru", databaseConfigLocal.getUsername());
        assertEquals("pass", databaseConfigLocal.getPassword());
    }

    @Test
    public void testVerifyDatabaseConfigDefault() {
        assertEquals(true, AdvancedLdapDatabaseConfigFactoryDummy.verifyDatabaseConfig(this.advancedLdapDatabaseConfigurationDefault));
    }

    @Test
    public void testVerifyDatabaseConfigNoDatabaseName() {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("");
        advancedLdapDatabaseConfiguration.setUserName("cru");
        advancedLdapDatabaseConfiguration.setPassword("pass");
        assertEquals(false, AdvancedLdapDatabaseConfigFactoryDummy.verifyDatabaseConfig(advancedLdapDatabaseConfiguration));
    }

    @Test
    public void testVerifyDatabaseConfigWrongCredentials() {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("crucible");
        advancedLdapDatabaseConfiguration.setUserName("cru");
        advancedLdapDatabaseConfiguration.setPassword("cible");
        assertEquals(false, AdvancedLdapDatabaseConfigFactoryDummy.verifyDatabaseConfig(advancedLdapDatabaseConfiguration));
    }
}
