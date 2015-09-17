package com.davidkoudela.crucible.persistence;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.persistence.strategy.HibernateAdvancedLdapPluginConfigurationPersistenceStrategy;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: Implementation of {@link HibernateAdvancedLdapPluginConfigurationDAO} representing the Data Access Object class
 *              for {@link com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration}.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-31
 */
@Component("advancedLdapOptionsDAO")
public class HibernateAdvancedLdapPluginConfigurationDAOImpl implements HibernateAdvancedLdapPluginConfigurationDAO {
    private SessionFactory sessionFactory;
    private HibernateAdvancedLdapPluginConfigurationPersistenceStrategy hibernateAdvancedLdapPluginConfigurationPersistenceStrategy;

    public HibernateAdvancedLdapPluginConfigurationDAOImpl() throws Exception {
        this.sessionFactory = HibernateSessionFactoryFactory.createHibernateSessionFactory();
        this.hibernateAdvancedLdapPluginConfigurationPersistenceStrategy = HibernateSessionFactoryFactory.createHibernateAdvancedLdapPluginConfigurationPersistenceStrategy();
    }

    @Override
    @Transactional
    public void store(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration, boolean isUpdate) throws Exception {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.sessionFactory.openSession();
            tx = session.beginTransaction();

            testConnection(session);

            this.hibernateAdvancedLdapPluginConfigurationPersistenceStrategy.transformToStorage(advancedLdapPluginConfiguration);

            if (isUpdate)
                session.saveOrUpdate(advancedLdapPluginConfiguration);
            else {
                session.save(advancedLdapPluginConfiguration);
            }
            Integer id = (Integer) session.getIdentifier(advancedLdapPluginConfiguration);
            advancedLdapPluginConfiguration.setId(id);
            session.update(advancedLdapPluginConfiguration);

            tx.commit();
        } catch (HibernateException he) {
            if (tx!=null) tx.rollback();
            throw new Exception("Could not save key");
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    @Transactional
    public AdvancedLdapPluginConfiguration get() {
        Session session = null;
        Transaction tx = null;
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = null;
        try
        {
            session = this.sessionFactory.openSession();
            tx = session.beginTransaction();

            testConnection(session);

            List advancedLdapPluginConfigurationList = session.createQuery("FROM com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration").list();
            if (advancedLdapPluginConfigurationList == null || advancedLdapPluginConfigurationList.isEmpty())
                advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
            else
                advancedLdapPluginConfiguration = (AdvancedLdapPluginConfiguration)advancedLdapPluginConfigurationList.get(advancedLdapPluginConfigurationList.size()-1);

            this.hibernateAdvancedLdapPluginConfigurationPersistenceStrategy.transformFromStorage(advancedLdapPluginConfiguration);

            tx.rollback();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            System.out.println("Hibernate get failed for Configuration data: " + e);
            return new AdvancedLdapPluginConfiguration();
        } finally {
            try {
                if (session != null)
                    session.close();
            }
            catch (Exception e)
            {
            }
        }
        return advancedLdapPluginConfiguration;
    }

    @Override
    @Transactional
    public void remove(int id) {
        Session session = null;
        Transaction tx = null;
        try
        {
            session = this.sessionFactory.openSession();
            tx = session.beginTransaction();

            testConnection(session);

            AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = (AdvancedLdapPluginConfiguration)session.get(AdvancedLdapPluginConfiguration.class, id);
            session.delete(advancedLdapPluginConfiguration);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * Due to lost database connection Hibernate requests can fail.
     * This method asks the Hibernate session about the connection status
     * which causes connection reconnect.
     * This is enough to ensure proper Hibernate CRUD operations.
     */
    private void testConnection(Session session) {
        try {
            session.isConnected();
        } catch (HibernateException e) {
            System.out.println("Hibernate test connection failed: " + e);
        }
    }
}