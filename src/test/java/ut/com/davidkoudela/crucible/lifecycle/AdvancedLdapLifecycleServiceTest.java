package ut.com.davidkoudela.crucible.lifecycle;

import com.cenqua.fisheye.config.RootConfig;
import com.cenqua.fisheye.user.UserManager;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.lifecycle.AdvancedLdapLifecycleService;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapService;
import com.davidkoudela.crucible.tasks.AdvancedLdapSynchronizationManager;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Description: Testing {@link AdvancedLdapLifecycleService}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2016-02-22
 */
public class AdvancedLdapLifecycleServiceTest extends TestCase {
    AdvancedLdapUserManager advancedLdapUserManager = null;
    AdvancedLdapSynchronizationManager advancedLdapSynchronizationManager = null;
    HibernateAdvancedLdapService hibernateAdvancedLdapService = null;
    UserManager userManager = null;
    RootConfig rootConfig = null;

    public class AdvancedLdapLifecycleServiceDummy extends AdvancedLdapLifecycleService {
        public void setRootConfig(RootConfig rootConfig) {
            super.setRootConfig(rootConfig);
        }
        public void setEnabledUserManagerInjection(Boolean enabledUserManagerInjection) {
            super.setEnabledUserManagerInjection(enabledUserManagerInjection);
        }
    }

    @Test
    public void testAfterPropertiesSetDestroy() throws Exception {
        this.advancedLdapUserManager = Mockito.mock(AdvancedLdapUserManager.class);
        this.advancedLdapSynchronizationManager = Mockito.mock(AdvancedLdapSynchronizationManager.class);
        this.hibernateAdvancedLdapService = Mockito.mock(HibernateAdvancedLdapService.class);
        this.userManager = Mockito.mock(UserManager.class);
        this.rootConfig = new RootConfig();
        this.rootConfig.setUserManager(this.userManager);

        AdvancedLdapLifecycleServiceDummy advancedLdapLifecycleService = new AdvancedLdapLifecycleServiceDummy();
        advancedLdapLifecycleService.setAdvancedLdapUserManager(this.advancedLdapUserManager);
        advancedLdapLifecycleService.setAdvancedLdapSynchronizationManager(this.advancedLdapSynchronizationManager);
        advancedLdapLifecycleService.setHibernateAdvancedLdapService(this.hibernateAdvancedLdapService);
        advancedLdapLifecycleService.setUserManager(this.userManager);
        advancedLdapLifecycleService.setRootConfig(this.rootConfig);
        advancedLdapLifecycleService.setEnabledUserManagerInjection(true);

        advancedLdapLifecycleService.afterPropertiesSet();
        assertEquals(this.advancedLdapUserManager, this.rootConfig.getUserManager());
        advancedLdapLifecycleService.destroy();
        assertEquals(this.userManager, this.rootConfig.getUserManager());
    }
}
