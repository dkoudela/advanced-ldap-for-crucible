package ut.com.davidkoudela.crucible.persistence;

import com.cenqua.crucible.hibernate.DBType;
import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration;
import com.davidkoudela.crucible.persistence.AdvancedLdapDatabaseConfigurationDAO;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapInstance;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapServiceImpl;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Description: Testing {@link HibernateAdvancedLdapServiceImpl}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-28
 */
@RunWith(MockitoJUnitRunner.class)
public class HibernateAdvancedLdapServiceImplTest extends TestCase {
    private static AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfigurationDefault;
    private static AdvancedLdapDatabaseConfigurationDAO advancedLdapDatabaseConfigurationDAO;

    @Before
    public void init() {
        this.advancedLdapDatabaseConfigurationDefault = new AdvancedLdapDatabaseConfiguration();
        this.advancedLdapDatabaseConfigurationDefault.setDatabaseName("crucible");
        this.advancedLdapDatabaseConfigurationDefault.setUserName("sa");
        this.advancedLdapDatabaseConfigurationDefault.setPassword("");
        this.advancedLdapDatabaseConfigurationDAO = Mockito.mock(AdvancedLdapDatabaseConfigurationDAO.class);
    }

    @Test
    public void testInitiate() {
        Mockito.when(this.advancedLdapDatabaseConfigurationDAO.get()).thenReturn(this.advancedLdapDatabaseConfigurationDefault);
        HibernateAdvancedLdapServiceImpl hibernateAdvancedLdapService  = new HibernateAdvancedLdapServiceImpl(this.advancedLdapDatabaseConfigurationDAO);
    }

    @Test
    public void testInitiateThrowsException() {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName(null);
        advancedLdapDatabaseConfiguration.setUserName(null);
        advancedLdapDatabaseConfiguration.setPassword("");
        Mockito.when(this.advancedLdapDatabaseConfigurationDAO.get()).thenReturn(advancedLdapDatabaseConfiguration);
        HibernateAdvancedLdapServiceImpl hibernateAdvancedLdapService  = new HibernateAdvancedLdapServiceImpl(this.advancedLdapDatabaseConfigurationDAO);
        HibernateAdvancedLdapInstance hibernateAdvancedLdapInstance = hibernateAdvancedLdapService.getInstance();

        assertNull(hibernateAdvancedLdapInstance.getSessionFactory());
        assertNull(hibernateAdvancedLdapInstance.getHibernateAdvancedLdapPluginConfigurationPersistenceStrategy());
    }

    @Test
    public void testGetInstance() {
        Mockito.when(this.advancedLdapDatabaseConfigurationDAO.get()).thenReturn(this.advancedLdapDatabaseConfigurationDefault);
        HibernateAdvancedLdapServiceImpl hibernateAdvancedLdapService  = new HibernateAdvancedLdapServiceImpl(this.advancedLdapDatabaseConfigurationDAO);
        HibernateAdvancedLdapInstance hibernateAdvancedLdapInstance = hibernateAdvancedLdapService.getInstance();

        assertEquals(true, hibernateAdvancedLdapInstance.getSessionFactory().openSession().isConnected());
        assertNotNull(hibernateAdvancedLdapInstance.getHibernateAdvancedLdapPluginConfigurationPersistenceStrategy());
    }

    @Test
    public void testVerifyDatabaseConfig() {
        Mockito.when(this.advancedLdapDatabaseConfigurationDAO.get()).thenReturn(this.advancedLdapDatabaseConfigurationDefault);
        HibernateAdvancedLdapServiceImpl hibernateAdvancedLdapService  = new HibernateAdvancedLdapServiceImpl(this.advancedLdapDatabaseConfigurationDAO);
        assertEquals(true, hibernateAdvancedLdapService.verifyDatabaseConfig(this.advancedLdapDatabaseConfigurationDefault));
    }

}
