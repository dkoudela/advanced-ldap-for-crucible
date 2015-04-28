package com.davidkoudela.crucible.tasks;

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
    @Override
    public void run() {
        System.out.println("Timer task started at:"+new Date());
    }
}
