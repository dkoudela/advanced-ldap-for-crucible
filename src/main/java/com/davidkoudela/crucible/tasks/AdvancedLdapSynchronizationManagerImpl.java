package com.davidkoudela.crucible.tasks;

import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.config.HibernateAdvancedLdapPluginConfigurationDAO;
import com.davidkoudela.crucible.timer.AdvancedLdapTimerTrigger;

/**
 * Description: Implementation of {@link AdvancedLdapSynchronizationManager} providing management of periodic synchronizations.
 *
 * @author dkoudela
 * @since 2015-05-04
 */
public class AdvancedLdapSynchronizationManagerImpl implements AdvancedLdapSynchronizationManager {
    private HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO;
    private AdvancedLdapTimerTrigger advancedLdapTimerTrigger;
    private AdvancedLdapUserManager advancedLdapUserManager;
    private int timerIndex = -1;

    @org.springframework.beans.factory.annotation.Autowired
    public AdvancedLdapSynchronizationManagerImpl(HibernateAdvancedLdapPluginConfigurationDAO hibernateAdvancedLdapPluginConfigurationDAO,
                                            AdvancedLdapTimerTrigger advancedLdapTimerTrigger,
                                            AdvancedLdapUserManager advancedLdapUserManager) {
        this.hibernateAdvancedLdapPluginConfigurationDAO = hibernateAdvancedLdapPluginConfigurationDAO;
        this.advancedLdapTimerTrigger = advancedLdapTimerTrigger;
        this.advancedLdapUserManager = advancedLdapUserManager;
        updateTimer();
    }

    @Override
    public void updateTimer() {
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = this.hibernateAdvancedLdapPluginConfigurationDAO.get();
        if (-1 != this.timerIndex) {
            this.advancedLdapTimerTrigger.deleteTimer(this.timerIndex);
            this.timerIndex = -1;
        }
        if (!advancedLdapPluginConfiguration.getLDAPUrl().isEmpty()) {
            AdvancedLdapSynchronizationTask advancedLdapSynchronizationTask = new AdvancedLdapSynchronizationTask(this.advancedLdapUserManager);
            this.timerIndex = this.advancedLdapTimerTrigger.createTimer(advancedLdapSynchronizationTask, advancedLdapPluginConfiguration.getLDAPSyncPeriod());
        }
    }

    @Override
    public void runNow() {
        AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration = this.hibernateAdvancedLdapPluginConfigurationDAO.get();
        if (!advancedLdapPluginConfiguration.getLDAPUrl().isEmpty()) {
            AdvancedLdapSynchronizationTask advancedLdapSynchronizationTask = new AdvancedLdapSynchronizationTask(this.advancedLdapUserManager);
            this.advancedLdapTimerTrigger.runNow(advancedLdapSynchronizationTask);
        }
    }
}
