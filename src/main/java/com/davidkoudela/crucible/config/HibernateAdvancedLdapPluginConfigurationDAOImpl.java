package com.davidkoudela.crucible.config;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: Implementation of {@link HibernateAdvancedLdapPluginConfigurationDAO} representing the Data Access Object class
 *              for {@link AdvancedLdapPluginConfiguration}.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-31
 */
@Component("advancedLdapOptionsDAO")
public class HibernateAdvancedLdapPluginConfigurationDAOImpl implements HibernateAdvancedLdapPluginConfigurationDAO {
    private SessionFactory sessionFactory;

    public HibernateAdvancedLdapPluginConfigurationDAOImpl() throws Exception {
        this.sessionFactory = HibernateSessionFactoryFactory.createHibernateSessionFactory();
    }

    @Override
    @Transactional
    public void store(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration, boolean isUpdate) throws Exception {
        Session session = null;
        Transaction tx = null;
        try {
            session = this.sessionFactory.openSession();
            tx = session.beginTransaction();

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
            List advancedLdapPluginConfigurationList = session.createQuery("FROM com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration").list();
            if (advancedLdapPluginConfigurationList == null || advancedLdapPluginConfigurationList.isEmpty())
                advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
            else
                advancedLdapPluginConfiguration = (AdvancedLdapPluginConfiguration)advancedLdapPluginConfigurationList.get(advancedLdapPluginConfigurationList.size()-1);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
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
}
