package ut.com.davidkoudela.crucible.persistence;

import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.davidkoudela.crucible.persistence.AdvancedLdapDatabaseConfigFactory;
import com.davidkoudela.crucible.persistence.HibernateSessionFactoryFactory;
import junit.framework.TestCase;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

    @Before
    public void init() {
        this.databaseConfig = AdvancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig();
    }

    @Test
    public void testCreateHibernateSessionFactory() throws Exception {
        SessionFactory sessionFactory = HibernateSessionFactoryFactory.createHibernateSessionFactory(this.databaseConfig);
        assertNotNull(sessionFactory);
    }

    @Test(expected = Exception.class)
    public void testCreateHibernateSessionFactoryException() throws Exception {
        DatabaseConfig databaseConfig2 = Mockito.mock(DatabaseConfig.class);
        Mockito.when(databaseConfig2.getJdbcDriverClass()).thenThrow(Exception.class);
        HibernateSessionFactoryFactory.createHibernateSessionFactory(databaseConfig2);
    }

    @Test(expected = Exception.class)
    public void testCreateHibernateSessionFactoryThrowable() throws Exception {
        DatabaseConfig databaseConfig2 = Mockito.mock(DatabaseConfig.class);
        Mockito.when(databaseConfig2.getJdbcDriverClass()).thenThrow(Error.class);
        HibernateSessionFactoryFactory.createHibernateSessionFactory(databaseConfig2);
    }
}
