package ut.com.davidkoudela.crucible.config;

import com.cenqua.crucible.hibernate.DBType;
import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.davidkoudela.crucible.config.HibernateSessionFactoryFactory;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Description: Testing {@link HibernateSessionFactoryFactory}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-22
 */
@RunWith(MockitoJUnitRunner.class)
public class HibernateSessionFactoryFactoryTest extends TestCase {
    private static DatabaseConfig databaseConfig;
    private static DatabaseConfig databaseConfig2;
    private static DatabaseConfig databaseConfig3;

    public static class HibernateSessionFactoryFactoryDummy extends HibernateSessionFactoryFactory {
        public static String constructJdbcUrl(DatabaseConfig databaseConfig) {
            return HibernateSessionFactoryFactory.constructJdbcUrl(databaseConfig);
        }
        public static String getCreateDbStatement(DatabaseConfig databaseConfig) {
            return HibernateSessionFactoryFactory.getCreateDbStatement(databaseConfig);
        }
        public static String getGrantDbStatement(DatabaseConfig databaseConfig) {
            return HibernateSessionFactoryFactory.getGrantDbStatement(databaseConfig);
        }
        public static String getFlushDbStatement(DatabaseConfig databaseConfig) {
            return HibernateSessionFactoryFactory.getFlushDbStatement(databaseConfig);
        }
    }

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

    @Test
    public void testCreateHibernateSessionFactory() throws Exception {
        HibernateSessionFactoryFactory.createHibernateSessionFactory();
    }

    @Test
    public void testConstructJdbcUrl() {
        assertEquals("jdbc:mysql://localhost:3306/crucibleadldb", HibernateSessionFactoryFactoryDummy.constructJdbcUrl(this.databaseConfig));
    }

    @Test
    public void testGetCreateDbStatementMySql() {
        assertEquals("CREATE DATABASE crucibleadldb CHARACTER SET utf8 COLLATE utf8_bin", HibernateSessionFactoryFactoryDummy.getCreateDbStatement(this.databaseConfig));
    }

    @Test
    public void testGetCreateDbStatementPostgresql() {
        assertEquals("CREATE DATABASE crucibleadldb ENCODING 'UTF-8' OWNER crucible", HibernateSessionFactoryFactoryDummy.getCreateDbStatement(this.databaseConfig2));
    }

    @Test
    public void testGetCreateDbStatementOracle() {
        assertEquals(null, HibernateSessionFactoryFactoryDummy.getCreateDbStatement(this.databaseConfig3));
    }

    @Test
    public void testGetGrantDbStatementMySql() {
        assertEquals("GRANT ALL PRIVILEGES ON crucibleadldb TO 'crucible'@'localhost' IDENTIFIED BY 'password'", HibernateSessionFactoryFactoryDummy.getGrantDbStatement(this.databaseConfig));
    }

    @Test
    public void testGetGrantDbStatementPostgresql() {
        assertEquals("grant all on database crucibleadldb to crucible", HibernateSessionFactoryFactoryDummy.getGrantDbStatement(this.databaseConfig2));
    }

    @Test
    public void testGetGrantDbStatementOracle() {
        assertEquals(null, HibernateSessionFactoryFactoryDummy.getGrantDbStatement(this.databaseConfig3));
    }

    @Test
    public void testGetFlushDbStatementMySql() {
        assertEquals("FLUSH PRIVILEGES", HibernateSessionFactoryFactoryDummy.getFlushDbStatement(this.databaseConfig));
    }

    @Test
    public void testGetFlushDbStatementPostgresql() {
        assertEquals(null, HibernateSessionFactoryFactoryDummy.getFlushDbStatement(this.databaseConfig2));
    }
}
