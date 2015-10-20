package ut.com.davidkoudela.crucible.persistence;

import com.cenqua.crucible.hibernate.DBType;
import com.cenqua.crucible.hibernate.DatabaseConfig;
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
    }

    public static class AdvancedLdapDatabaseConfigFactoryDummy extends AdvancedLdapDatabaseConfigFactory {
        public static String constructJdbcUrl(DatabaseConfig databaseConfig) {
            return AdvancedLdapDatabaseConfigFactory.constructJdbcUrl(databaseConfig);
        }
        public static String getCreateDbStatement(DatabaseConfig databaseConfig) {
            return AdvancedLdapDatabaseConfigFactory.getCreateDbStatement(databaseConfig);
        }
        public static String getGrantDbStatement(DatabaseConfig databaseConfig) {
            return AdvancedLdapDatabaseConfigFactory.getGrantDbStatement(databaseConfig);
        }
        public static String getFlushDbStatement(DatabaseConfig databaseConfig) {
            return AdvancedLdapDatabaseConfigFactory.getFlushDbStatement(databaseConfig);
        }
    }

    @Test
    public void testConstructJdbcUrl() {
        assertEquals("jdbc:mysql://localhost:3306/crucibleadldb", AdvancedLdapDatabaseConfigFactoryDummy.constructJdbcUrl(this.databaseConfig));
    }

    @Test
    public void testGetCreateDbStatementMySql() {
        assertEquals("CREATE DATABASE crucibleadldb CHARACTER SET utf8 COLLATE utf8_bin", AdvancedLdapDatabaseConfigFactoryDummy.getCreateDbStatement(this.databaseConfig));
    }

    @Test
    public void testGetCreateDbStatementPostgresql() {
        assertEquals("CREATE DATABASE crucibleadldb ENCODING 'UTF-8' OWNER crucible", AdvancedLdapDatabaseConfigFactoryDummy.getCreateDbStatement(this.databaseConfig2));
    }

    @Test
    public void testGetCreateDbStatementOracle() {
        assertEquals(null, AdvancedLdapDatabaseConfigFactoryDummy.getCreateDbStatement(this.databaseConfig3));
    }

    @Test
    public void testGetGrantDbStatementMySql() {
        assertEquals("GRANT ALL PRIVILEGES ON crucibleadldb.* TO 'crucible'@'localhost' IDENTIFIED BY 'password'", AdvancedLdapDatabaseConfigFactoryDummy.getGrantDbStatement(this.databaseConfig));
    }

    @Test
    public void testGetGrantDbStatementPostgresql() {
        assertEquals("grant all on database crucibleadldb to crucible", AdvancedLdapDatabaseConfigFactoryDummy.getGrantDbStatement(this.databaseConfig2));
    }

    @Test
    public void testGetGrantDbStatementOracle() {
        assertEquals(null, AdvancedLdapDatabaseConfigFactoryDummy.getGrantDbStatement(this.databaseConfig3));
    }

    @Test
    public void testGetFlushDbStatementMySql() {
        assertEquals("FLUSH PRIVILEGES", AdvancedLdapDatabaseConfigFactoryDummy.getFlushDbStatement(this.databaseConfig));
    }

    @Test
    public void testGetFlushDbStatementPostgresql() {
        assertEquals(null, AdvancedLdapDatabaseConfigFactoryDummy.getFlushDbStatement(this.databaseConfig2));
    }
}
