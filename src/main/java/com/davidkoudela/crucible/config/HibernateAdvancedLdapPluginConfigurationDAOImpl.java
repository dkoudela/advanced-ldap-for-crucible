package com.davidkoudela.crucible.config;

import com.cenqua.crucible.hibernate.DBType;
import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config1.DriverSource;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
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

    public HibernateAdvancedLdapPluginConfigurationDAOImpl() {
        try {
            DatabaseConfig databaseConfig = new DatabaseConfig(DBType.HSQL, "jdbc:hsqldb:file:" + AppConfig.getInstanceDir().getAbsolutePath() + "/var/data/crudb/crucible", "sa", "", DriverSource.BUNDLED, 5, 20);
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.autocommit", "false");
            configuration.setProperty("hibernate.connection.driver_class", databaseConfig.getJdbcDriverClass());
            configuration.setProperty("hibernate.connection.url", databaseConfig.getJdbcURL());
            configuration.setProperty("hibernate.connection.username", databaseConfig.getUsername());
            configuration.setProperty("hibernate.connection.password", databaseConfig.getPassword());
            configuration.setProperty("hibernate.dialect", databaseConfig.getDialect());
            //configuration.setProperty("hibernate.show_sql", Boolean.toString(databaseConfig.isShowSQL()));
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.generate_statistics", Boolean.toString(databaseConfig.isGenerateStatistics()));
            configuration.setProperty("hibernate.connection.isolation", Integer.toString(2));
            configuration.setProperty("hibernate.bytecode.use_reflection_optimizer", "true");
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            if (DBType.ORACLE.equals(databaseConfig.getType())) {
                configuration.setProperty("hibernate.jdbc.batch_size", "0");
                configuration.setProperty("hibernate.dbcp.ps.maxIdle", "0");
            }
            configuration.addProperties(databaseConfig.getProperties());
            configuration.addClass(com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration.class);
            this.sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            System.out.println("HibernateAdvancedLdapPluginConfigurationDAOImpl: Exception: " + e);
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : e.getCause().getStackTrace()) {
                sb.append(element.toString());
                sb.append("\n");
            }
            System.out.println("HibernateAdvancedLdapPluginConfigurationDAOImpl: Exception: " + sb);
        }
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
