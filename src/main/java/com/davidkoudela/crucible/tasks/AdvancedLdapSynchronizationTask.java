package com.davidkoudela.crucible.tasks;

import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import com.davidkoudela.crucible.statistics.AdvancedLdapGroupUserSyncCount;
import com.google.common.base.Stopwatch;

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
    private AdvancedLdapUserManager advancedLdapUserManager;

    public AdvancedLdapSynchronizationTask(AdvancedLdapUserManager advancedLdapUserManager) {
        this.advancedLdapUserManager = advancedLdapUserManager;
    }

    @Override
    public void run() {
        Stopwatch timer = new Stopwatch();
        timer.start();
        System.out.println("Timer task started at:" + new Date());
        AdvancedLdapGroupUserSyncCount advancedLdapGroupUserSyncCount = new AdvancedLdapGroupUserSyncCount();
        this.advancedLdapUserManager.loadGroups(advancedLdapGroupUserSyncCount);
        System.out.println("Timer task ended at: " + new Date());
        timer.stop();
        System.out.println("Timer task duration: " + timer);
        System.out.println("Groups total:  " + advancedLdapGroupUserSyncCount.getGroupCountTotal());
        System.out.println("Groups new:    " + advancedLdapGroupUserSyncCount.getGroupCountNew());
        System.out.println("Nested Groups: " + advancedLdapGroupUserSyncCount.getNestedGroupCount());
        System.out.println("Users total:   " + advancedLdapGroupUserSyncCount.getUserCountTotal());
        System.out.println("Users new:     " + advancedLdapGroupUserSyncCount.getUserCountNew());
    }
}
