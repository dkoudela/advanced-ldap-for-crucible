package ut.com.davidkoudela.crucible.persistence;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapInstance;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapPluginConfigurationDAOImpl;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapService;
import com.davidkoudela.crucible.persistence.strategy.HibernateAdvancedLdapPluginConfigurationNoChangeStrategy;
import com.davidkoudela.crucible.persistence.strategy.HibernateAdvancedLdapPluginConfigurationOracleStrategy;
import junit.framework.TestCase;
import org.easymock.Mock;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: Testing {@link HibernateAdvancedLdapPluginConfigurationDAOImpl}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-28
 */
@RunWith(MockitoJUnitRunner.class)
public class HibernateAdvancedLdapPluginConfigurationDAOImplTest extends TestCase {
    private static HibernateAdvancedLdapPluginConfigurationDAOImpl hibernateAdvancedLdapPluginConfigurationDAO;
    private static HibernateAdvancedLdapService hibernateAdvancedLdapService;
    private static HibernateAdvancedLdapInstance hibernateAdvancedLdapInstance;
    private static SessionFactory sessionFactory;
    private static Session session;
    private static Transaction tx;
    private static ArgumentCaptor<AdvancedLdapPluginConfiguration> argumentCaptorAdvancedLdapPluginConfiguration1;
    private static ArgumentCaptor<AdvancedLdapPluginConfiguration> argumentCaptorAdvancedLdapPluginConfiguration2;
    private static ArgumentCaptor<AdvancedLdapPluginConfiguration> argumentCaptorAdvancedLdapPluginConfiguration3;

    @Before
    public void init() throws Exception {
        this.hibernateAdvancedLdapService = Mockito.mock(HibernateAdvancedLdapService.class);
        this.sessionFactory = Mockito.mock(SessionFactory.class);
        this.session = Mockito.mock(Session.class);
        this.tx = Mockito.mock(Transaction.class);
        this.hibernateAdvancedLdapPluginConfigurationDAO = new HibernateAdvancedLdapPluginConfigurationDAOImpl(this.hibernateAdvancedLdapService);
        this.hibernateAdvancedLdapInstance = new HibernateAdvancedLdapInstance();
        this.hibernateAdvancedLdapInstance.setSessionFactory(this.sessionFactory);
        this.hibernateAdvancedLdapInstance.setHibernateAdvancedLdapPluginConfigurationPersistenceStrategy(new HibernateAdvancedLdapPluginConfigurationNoChangeStrategy());

        this.argumentCaptorAdvancedLdapPluginConfiguration1 = ArgumentCaptor.forClass(AdvancedLdapPluginConfiguration.class);
        this.argumentCaptorAdvancedLdapPluginConfiguration2 = ArgumentCaptor.forClass(AdvancedLdapPluginConfiguration.class);
        this.argumentCaptorAdvancedLdapPluginConfiguration3 = ArgumentCaptor.forClass(AdvancedLdapPluginConfiguration.class);
    }

    @Test
    public void testStoreNoChangeStrategySave() throws Exception {
        Mockito.when(this.hibernateAdvancedLdapService.getInstance()).thenReturn(this.hibernateAdvancedLdapInstance);
        Mockito.when(this.sessionFactory.openSession()).thenReturn(this.session);
        Mockito.when(this.session.beginTransaction()).thenReturn(this.tx);
        Mockito.when(this.session.isConnected()).thenReturn(true);
        Mockito.when(this.session.save(this.argumentCaptorAdvancedLdapPluginConfiguration1.capture())).thenReturn(true);
        Mockito.when(this.session.getIdentifier(this.argumentCaptorAdvancedLdapPluginConfiguration2.capture())).thenReturn(new Integer(666));
        Mockito.doNothing().when(this.session).update(this.argumentCaptorAdvancedLdapPluginConfiguration3.capture());
        Mockito.doNothing().when(this.tx).commit();

        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setId(0);
        this.hibernateAdvancedLdapPluginConfigurationDAO.store(advancedLdapPluginConfiguration, false);

        Mockito.verify(this.hibernateAdvancedLdapService, Mockito.times(1)).getInstance();
        Mockito.verify(this.sessionFactory, Mockito.times(1)).openSession();
        Mockito.verify(this.session, Mockito.times(1)).beginTransaction();
        Mockito.verify(this.session, Mockito.times(1)).isConnected();
        Mockito.verify(this.session, Mockito.times(1)).save(this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue());
        Mockito.verify(this.session, Mockito.times(1)).getIdentifier(this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue());
        Mockito.verify(this.session, Mockito.times(1)).update(this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue());
        Mockito.verify(this.tx, Mockito.times(1)).commit();

        assertEquals(666, this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue().getId());
        assertEquals("", this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue().getLDAPUrl());
    }

    @Test
    public void testStoreNoChangeStrategySaveOrUpdate() throws Exception {
        Mockito.when(this.hibernateAdvancedLdapService.getInstance()).thenReturn(this.hibernateAdvancedLdapInstance);
        Mockito.when(this.sessionFactory.openSession()).thenReturn(this.session);
        Mockito.when(this.session.beginTransaction()).thenReturn(this.tx);
        Mockito.when(this.session.isConnected()).thenReturn(true);
        Mockito.doNothing().when(this.session).saveOrUpdate(this.argumentCaptorAdvancedLdapPluginConfiguration1.capture());
        Mockito.when(this.session.getIdentifier(this.argumentCaptorAdvancedLdapPluginConfiguration2.capture())).thenReturn(new Integer(666));
        Mockito.doNothing().when(this.session).update(this.argumentCaptorAdvancedLdapPluginConfiguration3.capture());
        Mockito.doNothing().when(this.tx).commit();

        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setId(0);
        this.hibernateAdvancedLdapPluginConfigurationDAO.store(advancedLdapPluginConfiguration, true);

        Mockito.verify(this.hibernateAdvancedLdapService, Mockito.times(1)).getInstance();
        Mockito.verify(this.sessionFactory, Mockito.times(1)).openSession();
        Mockito.verify(this.session, Mockito.times(1)).beginTransaction();
        Mockito.verify(this.session, Mockito.times(1)).isConnected();
        Mockito.verify(this.session, Mockito.times(1)).saveOrUpdate(this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue());
        Mockito.verify(this.session, Mockito.times(1)).getIdentifier(this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue());
        Mockito.verify(this.session, Mockito.times(1)).update(this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue());
        Mockito.verify(this.tx, Mockito.times(1)).commit();

        assertEquals(666, this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue().getId());
        assertEquals("", this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue().getLDAPUrl());
    }

    @Test
    public void testStoreOracleStrategySave() throws Exception {
        this.hibernateAdvancedLdapInstance.setHibernateAdvancedLdapPluginConfigurationPersistenceStrategy(new HibernateAdvancedLdapPluginConfigurationOracleStrategy());

        Mockito.when(this.hibernateAdvancedLdapService.getInstance()).thenReturn(this.hibernateAdvancedLdapInstance);
        Mockito.when(this.sessionFactory.openSession()).thenReturn(this.session);
        Mockito.when(this.session.beginTransaction()).thenReturn(this.tx);
        Mockito.when(this.session.isConnected()).thenReturn(true);
        Mockito.when(this.session.save(this.argumentCaptorAdvancedLdapPluginConfiguration1.capture())).thenReturn(true);
        Mockito.when(this.session.getIdentifier(this.argumentCaptorAdvancedLdapPluginConfiguration2.capture())).thenReturn(new Integer(666));
        Mockito.doNothing().when(this.session).update(this.argumentCaptorAdvancedLdapPluginConfiguration3.capture());
        Mockito.doNothing().when(this.tx).commit();

        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setId(0);
        this.hibernateAdvancedLdapPluginConfigurationDAO.store(advancedLdapPluginConfiguration, false);

        Mockito.verify(this.hibernateAdvancedLdapService, Mockito.times(1)).getInstance();
        Mockito.verify(this.sessionFactory, Mockito.times(1)).openSession();
        Mockito.verify(this.session, Mockito.times(1)).beginTransaction();
        Mockito.verify(this.session, Mockito.times(1)).isConnected();
        Mockito.verify(this.session, Mockito.times(1)).save(this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue());
        Mockito.verify(this.session, Mockito.times(1)).getIdentifier(this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue());
        Mockito.verify(this.session, Mockito.times(1)).update(this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue());
        Mockito.verify(this.tx, Mockito.times(1)).commit();

        assertEquals(666, this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue().getId());
        assertEquals(" ", this.argumentCaptorAdvancedLdapPluginConfiguration1.getValue().getLDAPUrl());
    }

    @Test
    public void testGetNoChangeStrategyOneResult() {
        Query query = Mockito.mock(Query.class);
        ArgumentCaptor<String> argumentCaptorString = ArgumentCaptor.forClass(String.class);

        List<AdvancedLdapPluginConfiguration> advancedLdapPluginConfigurationList = new ArrayList<AdvancedLdapPluginConfiguration>();
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setId(666);
        advancedLdapPluginConfiguration.setLDAPUrl("ldap://localhost:389");
        advancedLdapPluginConfigurationList.add(advancedLdapPluginConfiguration);

        Mockito.when(this.hibernateAdvancedLdapService.getInstance()).thenReturn(this.hibernateAdvancedLdapInstance);
        Mockito.when(this.sessionFactory.openSession()).thenReturn(this.session);
        Mockito.when(this.session.beginTransaction()).thenReturn(this.tx);
        Mockito.when(this.session.isConnected()).thenReturn(true);
        Mockito.when(query.list()).thenReturn(advancedLdapPluginConfigurationList);
        Mockito.when(this.session.createQuery(argumentCaptorString.capture())).thenReturn(query);
        Mockito.doNothing().when(this.tx).rollback();

        AdvancedLdapPluginConfiguration advancedLdapPluginConfigurationResult = this.hibernateAdvancedLdapPluginConfigurationDAO.get();

        Mockito.verify(this.hibernateAdvancedLdapService, Mockito.times(1)).getInstance();
        Mockito.verify(this.sessionFactory, Mockito.times(1)).openSession();
        Mockito.verify(this.session, Mockito.times(1)).beginTransaction();
        Mockito.verify(this.session, Mockito.times(1)).isConnected();
        Mockito.verify(this.session, Mockito.times(1)).createQuery(argumentCaptorString.getValue());
        Mockito.verify(this.tx, Mockito.times(1)).rollback();

        assertEquals(666, advancedLdapPluginConfigurationResult.getId());
        assertEquals("ldap://localhost:389", advancedLdapPluginConfigurationResult.getLDAPUrl());
    }

    @Test
    public void testGetNoChangeStrategyNoResult() {
        Query query = Mockito.mock(Query.class);
        ArgumentCaptor<String> argumentCaptorString = ArgumentCaptor.forClass(String.class);

        List<AdvancedLdapPluginConfiguration> advancedLdapPluginConfigurationList = new ArrayList<AdvancedLdapPluginConfiguration>();

        Mockito.when(this.hibernateAdvancedLdapService.getInstance()).thenReturn(this.hibernateAdvancedLdapInstance);
        Mockito.when(this.sessionFactory.openSession()).thenReturn(this.session);
        Mockito.when(this.session.beginTransaction()).thenReturn(this.tx);
        Mockito.when(this.session.isConnected()).thenReturn(true);
        Mockito.when(query.list()).thenReturn(advancedLdapPluginConfigurationList);
        Mockito.when(this.session.createQuery(argumentCaptorString.capture())).thenReturn(query);
        Mockito.doNothing().when(this.tx).rollback();

        AdvancedLdapPluginConfiguration advancedLdapPluginConfigurationResult = this.hibernateAdvancedLdapPluginConfigurationDAO.get();

        Mockito.verify(this.hibernateAdvancedLdapService, Mockito.times(1)).getInstance();
        Mockito.verify(this.sessionFactory, Mockito.times(1)).openSession();
        Mockito.verify(this.session, Mockito.times(1)).beginTransaction();
        Mockito.verify(this.session, Mockito.times(1)).isConnected();
        Mockito.verify(this.session, Mockito.times(1)).createQuery(argumentCaptorString.getValue());
        Mockito.verify(this.tx, Mockito.times(1)).rollback();

        assertEquals(0, advancedLdapPluginConfigurationResult.getId());
        assertEquals("", advancedLdapPluginConfigurationResult.getLDAPUrl());
    }

    @Test
    public void testGetOracleStrategyOneResult() {
        this.hibernateAdvancedLdapInstance.setHibernateAdvancedLdapPluginConfigurationPersistenceStrategy(new HibernateAdvancedLdapPluginConfigurationOracleStrategy());

        Query query = Mockito.mock(Query.class);
        ArgumentCaptor<String> argumentCaptorString = ArgumentCaptor.forClass(String.class);

        List<AdvancedLdapPluginConfiguration> advancedLdapPluginConfigurationList = new ArrayList<AdvancedLdapPluginConfiguration>();
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setId(666);
        advancedLdapPluginConfiguration.setLDAPUrl(" ");
        advancedLdapPluginConfigurationList.add(advancedLdapPluginConfiguration);

        Mockito.when(this.hibernateAdvancedLdapService.getInstance()).thenReturn(this.hibernateAdvancedLdapInstance);
        Mockito.when(this.sessionFactory.openSession()).thenReturn(this.session);
        Mockito.when(this.session.beginTransaction()).thenReturn(this.tx);
        Mockito.when(this.session.isConnected()).thenReturn(true);
        Mockito.when(query.list()).thenReturn(advancedLdapPluginConfigurationList);
        Mockito.when(this.session.createQuery(argumentCaptorString.capture())).thenReturn(query);
        Mockito.doNothing().when(this.tx).rollback();

        AdvancedLdapPluginConfiguration advancedLdapPluginConfigurationResult = this.hibernateAdvancedLdapPluginConfigurationDAO.get();

        Mockito.verify(this.hibernateAdvancedLdapService, Mockito.times(1)).getInstance();
        Mockito.verify(this.sessionFactory, Mockito.times(1)).openSession();
        Mockito.verify(this.session, Mockito.times(1)).beginTransaction();
        Mockito.verify(this.session, Mockito.times(1)).isConnected();
        Mockito.verify(this.session, Mockito.times(1)).createQuery(argumentCaptorString.getValue());
        Mockito.verify(this.tx, Mockito.times(1)).rollback();

        assertEquals(666, advancedLdapPluginConfigurationResult.getId());
        assertEquals("", advancedLdapPluginConfigurationResult.getLDAPUrl());
    }

    @Test
    public void testRemove() throws Exception {
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setId(666);
        advancedLdapPluginConfiguration.setLDAPUrl(" ");

        Mockito.when(this.hibernateAdvancedLdapService.getInstance()).thenReturn(this.hibernateAdvancedLdapInstance);
        Mockito.when(this.sessionFactory.openSession()).thenReturn(this.session);
        Mockito.when(this.session.beginTransaction()).thenReturn(this.tx);
        Mockito.when(this.session.isConnected()).thenReturn(true);
        Mockito.when(this.session.get(AdvancedLdapPluginConfiguration.class, 666)).thenReturn(advancedLdapPluginConfiguration);
        Mockito.doNothing().when(this.session).delete(advancedLdapPluginConfiguration);
        Mockito.doNothing().when(this.tx).commit();

        this.hibernateAdvancedLdapPluginConfigurationDAO.remove(666);

        Mockito.verify(this.hibernateAdvancedLdapService, Mockito.times(1)).getInstance();
        Mockito.verify(this.sessionFactory, Mockito.times(1)).openSession();
        Mockito.verify(this.session, Mockito.times(1)).beginTransaction();
        Mockito.verify(this.session, Mockito.times(1)).isConnected();
        Mockito.verify(this.session, Mockito.times(1)).get(AdvancedLdapPluginConfiguration.class, 666);
        Mockito.verify(this.session, Mockito.times(1)).delete(advancedLdapPluginConfiguration);
        Mockito.verify(this.tx, Mockito.times(1)).commit();
    }
}
