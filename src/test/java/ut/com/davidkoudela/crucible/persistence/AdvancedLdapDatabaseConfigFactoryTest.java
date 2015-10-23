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
    }

    @Test
    public void testConstructJdbcUrl() {
        assertEquals("jdbc:mysql://localhost:3306/crucibleadldb", AdvancedLdapDatabaseConfigFactoryDummy.constructJdbcUrl(this.databaseConfig));
    }
}
