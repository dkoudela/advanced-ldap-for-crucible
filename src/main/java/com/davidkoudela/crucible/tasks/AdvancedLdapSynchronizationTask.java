package com.davidkoudela.crucible.tasks;

import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.statistics.AdvancedLdapGroupUserSyncCount;
import com.google.common.base.Stopwatch;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.TimerTask;

/**
 * Description: {@link AdvancedLdapSynchronizationTask} is an LDAP synchronization task
 *              for syncing LDAP users and groups.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-04-27
 */
public class AdvancedLdapSynchronizationTask extends TimerTask {
    private Logger log = Logger.getLogger(this.getClass());
    private AdvancedLdapUserManager advancedLdapUserManager;

    public AdvancedLdapSynchronizationTask(AdvancedLdapUserManager advancedLdapUserManager) {
        this.advancedLdapUserManager = advancedLdapUserManager;
    }

    @Override
    public void run() {
        Stopwatch timer = new Stopwatch();
        timer.start();
        log.info("Timer task started at:" + new Date());
        AdvancedLdapGroupUserSyncCount advancedLdapGroupUserSyncCount = new AdvancedLdapGroupUserSyncCount();
        this.advancedLdapUserManager.loadGroups(advancedLdapGroupUserSyncCount);
        log.info("Timer task ended at: " + new Date());
        timer.stop();
        log.info("Timer task duration: " + timer);
        log.info("Groups total:  " + advancedLdapGroupUserSyncCount.getGroupCountTotal());
        log.info("Groups new:    " + advancedLdapGroupUserSyncCount.getGroupCountNew());
        log.info("Nested Groups: " + advancedLdapGroupUserSyncCount.getNestedGroupCount());
        log.info("Users total:   " + advancedLdapGroupUserSyncCount.getUserCountTotal());
        log.info("Users new:     " + advancedLdapGroupUserSyncCount.getUserCountNew());
    }
}
