package com.davidkoudela.crucible.lifecycle;

import com.cenqua.fisheye.user.UserManager;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapService;
import com.davidkoudela.crucible.tasks.AdvancedLdapSynchronizationManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * {@link AdvancedLdapLifecycleService} provides a service for managing plugin's Lifecycle events like startup or shutdown.
 *
 * @author dkoudela
 * @since 2016-02-19
 */
public class AdvancedLdapLifecycleService implements InitializingBean, DisposableBean {
    private Logger log = Logger.getLogger(this.getClass());
    AdvancedLdapUserManager advancedLdapUserManager = null;
    AdvancedLdapSynchronizationManager advancedLdapSynchronizationManager = null;
    HibernateAdvancedLdapService hibernateAdvancedLdapService = null;
    UserManager userManager = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("**************************** AdvancedLdap: plugin loaded ****************************");
/*
        if (null != this.advancedLdapSynchronizationManager &&
                null != this.advancedLdapUserManager &&
                null != this.hibernateAdvancedLdapService &&
                null != this.userManager) {
            this.userManager = AppConfig.getsConfig().getUserManager();
            AppConfig.getsConfig().setUserManager(this.advancedLdapUserManager);
        }
*/
    }

    @Override
    public void destroy() throws Exception {
/*
        AppConfig.getsConfig().setUserManager(this.userManager);
        AppConfig.getsConfig().setUserManager(null);
*/
        log.info("**************************** AdvancedLdap: plugin unloaded ****************************");
    }


    public void setAdvancedLdapUserManager(AdvancedLdapUserManager advancedLdapUserManager) {
        this.advancedLdapUserManager = advancedLdapUserManager;
    }

    public void setAdvancedLdapSynchronizationManager(AdvancedLdapSynchronizationManager advancedLdapSynchronizationManager) {
        this.advancedLdapSynchronizationManager = advancedLdapSynchronizationManager;
    }

    public void setHibernateAdvancedLdapService(HibernateAdvancedLdapService hibernateAdvancedLdapService) {
        this.hibernateAdvancedLdapService = hibernateAdvancedLdapService;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
