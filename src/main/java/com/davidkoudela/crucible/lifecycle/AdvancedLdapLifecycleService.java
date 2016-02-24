package com.davidkoudela.crucible.lifecycle;

import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config.RootConfig;
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
    UserManager userManagerAopProxyInPlugin = null;
    UserManager userManagerAopProxyRootConfig = null;
    RootConfig rootConfig = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("**************************** AdvancedLdap: plugin loaded ****************************");
        /**
         * Spring Beans in FECRU are managed by Spring AOP proxies.
         * Spring AOP proxies are created dynamically when needed and have limited lifetime.
         * E.g. userManagerAopProxyInPlugin is valid only during the time when the plugin is enabled.
         * If accessed afterwards, it causes a failure:
         *   org.springframework.osgi.service.importer.ServiceProxyDestroyedException: service proxy has been destroyed
         * On the other hand, userManagerAopProxyRootConfig is valid for the whole application lifetime.
         * Both proxies refer to the same instance of the UserManager class.
         *
         * The UserManager proxy in the RootConfig is used across the FECRU instance. Replacing the instance by
         * the AdvancedLdapUserManager instance ensures tracking all requests for user operations via the plugin.
         * The userManagerAopProxyRootConfig is kept for further replacement when the plugin is unloaded.
         */
/*
        if (null != this.advancedLdapSynchronizationManager &&
                null != this.advancedLdapUserManager &&
                null != this.hibernateAdvancedLdapService &&
                null != this.userManagerAopProxyInPlugin) {
            this.userManagerAopProxyRootConfig = getRootConfig().getUserManager();
            getRootConfig().setUserManager(this.advancedLdapUserManager);
        }
*/
    }

    @Override
    public void destroy() throws Exception {
/*
        getRootConfig().setUserManager(this.userManagerAopProxyRootConfig);
        this.advancedLdapUserManager.restoreUserManager(this.userManagerAopProxyRootConfig);
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
        this.userManagerAopProxyInPlugin = userManager;
    }

    protected void setRootConfig(RootConfig rootConfig) {
        this.rootConfig = rootConfig;
    }

    protected RootConfig getRootConfig() {
        if (null == this.rootConfig)
            return AppConfig.getsConfig();
        return this.rootConfig;
    }
}
