package com.davidkoudela.crucible.admin;

import com.atlassian.crucible.spi.data.UserData;

/**
 * Description: {@link AdvancedLdapUserManager} represents an interface used by {@link com.davidkoudela.crucible.listener.AdvancedLdapEventListener},
 *              {@link com.davidkoudela.crucible.timer.AdvancedLdapTimerTrigger} or any other component requiring managed LDAP users with their groups.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-13
 */
public interface AdvancedLdapUserManager {
    void loadUser(UserData userData);
    void loadGroups();
}
