package com.davidkoudela.crucible.lifecycle;

import com.atlassian.fisheye.web.HeaderUtil;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config.RootConfig;
import com.cenqua.fisheye.user.UserManager;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapService;
import com.davidkoudela.crucible.tasks.AdvancedLdapSynchronizationManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

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
    Boolean enabledUserManagerInjection = false;

    private static final Unsafe unsafe;
    static
    {
        try
        {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe)field.get(null);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

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
        if (null != this.advancedLdapSynchronizationManager &&
                null != this.advancedLdapUserManager &&
                null != this.hibernateAdvancedLdapService &&
                null != this.userManagerAopProxyInPlugin) {
            if (enabledUserManagerInjection) {
                this.userManagerAopProxyRootConfig = getRootConfig().getUserManager();
                getRootConfig().setUserManager(this.advancedLdapUserManager);
                replaceUserManagerInHeaderUtil(this.advancedLdapUserManager);
            }
            this.advancedLdapSynchronizationManager.updateTimer();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (null != this.advancedLdapSynchronizationManager &&
                null != this.advancedLdapUserManager &&
                null != this.hibernateAdvancedLdapService &&
                null != this.userManagerAopProxyInPlugin) {
            this.advancedLdapSynchronizationManager.cancelTimer();
            if (enabledUserManagerInjection) {
                getRootConfig().setUserManager(this.userManagerAopProxyRootConfig);
                this.advancedLdapUserManager.restoreUserManager(this.userManagerAopProxyRootConfig);
                replaceUserManagerInHeaderUtil(this.userManagerAopProxyRootConfig);
            }
        }
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

    protected void replaceUserManagerInHeaderUtil(UserManager userManager) throws NoSuchFieldException {
        /**
         * Whole FECRU uses AppConfig.getsConfig().getUserManager() for accessing the UserManager
         * except HeaderUtil. HeaderUtil keeps the UserManager in a static final variable.
         * HeaderUtil is used in the web parts of the FECRU.
         * To be able to track also the web part, the UserManager must be replaced.
         *
         * This is dirty code fixing the issue with the static final variable, otherwise it would be
         * necessary to restart the FECRU application after the plugin installation. That would not
         * be very convenient way of the plugin deployment.
         */
        final Field fieldToUpdate = HeaderUtil.class.getDeclaredField("userManager");
        final Object base = unsafe.staticFieldBase(fieldToUpdate);
        final long offset = unsafe.staticFieldOffset(fieldToUpdate);
        unsafe.putObject(base, offset, userManager);
    }

    protected void setRootConfig(RootConfig rootConfig) {
        this.rootConfig = rootConfig;
    }

    protected RootConfig getRootConfig() {
        if (null == this.rootConfig)
            return AppConfig.getsConfig();
        return this.rootConfig;
    }

    protected void setEnabledUserManagerInjection(Boolean enabledUserManagerInjection) {
        this.enabledUserManagerInjection = enabledUserManagerInjection;
    }
}
