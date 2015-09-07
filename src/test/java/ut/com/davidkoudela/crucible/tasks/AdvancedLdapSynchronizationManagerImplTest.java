package ut.com.davidkoudela.crucible.tasks;

import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapPluginConfigurationDAO;
import com.davidkoudela.crucible.tasks.AdvancedLdapSynchronizationManager;
import com.davidkoudela.crucible.tasks.AdvancedLdapSynchronizationManagerImpl;
import com.davidkoudela.crucible.timer.AdvancedLdapTimerTrigger;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.TimerTask;

/**
 * Description: Testing {@link AdvancedLdapSynchronizationManagerImpl}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-04
 */
public class AdvancedLdapSynchronizationManagerImplTest extends TestCase {
    @Test
    public void testCreateAndUpdateTimer() {
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO = Mockito.mock(HibernateAdvancedLdapPluginConfigurationDAO.class);
        AdvancedLdapTimerTrigger advancedLdapTimerTrigger = Mockito.mock(AdvancedLdapTimerTrigger.class);
        AdvancedLdapUserManager advancedLdapUserManager = Mockito.mock(AdvancedLdapUserManager.class);

        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        ArgumentCaptor<TimerTask> argumentCaptorTimerTask = ArgumentCaptor.forClass(TimerTask.class);
        ArgumentCaptor<Long> argumentCaptorTimerPeriod = ArgumentCaptor.forClass(Long.class);
        Mockito.when(advancedLdapTimerTrigger.createTimer(argumentCaptorTimerTask.capture(), argumentCaptorTimerPeriod.capture())).thenReturn(1);

        AdvancedLdapSynchronizationManager advancedLdapSynchronizationManager =
                new AdvancedLdapSynchronizationManagerImpl(hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapTimerTrigger, advancedLdapUserManager);
        Mockito.verify(advancedLdapTimerTrigger).createTimer(argumentCaptorTimerTask.capture(), argumentCaptorTimerPeriod.capture());
    }

    public void testRunNow() {
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        advancedLdapPluginConfiguration.setLDAPUrl("url");
        HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO = Mockito.mock(HibernateAdvancedLdapPluginConfigurationDAO.class);
        AdvancedLdapTimerTrigger advancedLdapTimerTrigger = Mockito.mock(AdvancedLdapTimerTrigger.class);
        AdvancedLdapUserManager advancedLdapUserManager = Mockito.mock(AdvancedLdapUserManager.class);

        Mockito.when(hibernateAdvancedLdapPluginConfigurationDAO.get()).thenReturn(advancedLdapPluginConfiguration);
        ArgumentCaptor<TimerTask> argumentCaptorTimerTask = ArgumentCaptor.forClass(TimerTask.class);
        ArgumentCaptor<Long> argumentCaptorTimerPeriod = ArgumentCaptor.forClass(Long.class);
        Mockito.when(advancedLdapTimerTrigger.createTimer(argumentCaptorTimerTask.capture(), argumentCaptorTimerPeriod.capture())).thenReturn(1);

        AdvancedLdapSynchronizationManager advancedLdapSynchronizationManager =
                new AdvancedLdapSynchronizationManagerImpl(hibernateAdvancedLdapPluginConfigurationDAO, advancedLdapTimerTrigger, advancedLdapUserManager);
        Mockito.verify(advancedLdapTimerTrigger).createTimer(argumentCaptorTimerTask.capture(), argumentCaptorTimerPeriod.capture());

        advancedLdapSynchronizationManager.runNow();
        Mockito.verify(advancedLdapTimerTrigger).runNow(argumentCaptorTimerTask.capture());
    }
}
