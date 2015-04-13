package com.davidkoudela.crucible.listener;

import com.atlassian.crucible.actions.admin.project.UserData;
import com.atlassian.crucible.event.ReviewCreatedEvent;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
//import com.atlassian.event.api.EventListener;
import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import org.springframework.beans.factory.DisposableBean;

/**
 * Description: {@link AdvancedLdapEventListener} triggers {@link AdvancedLdapUserManager} for LDAP user and groups sync.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-08
 */
public class AdvancedLdapEventListener implements DisposableBean, EventListener {
    private AdvancedLdapUserManager advancedLdapUserManager;

    @org.springframework.beans.factory.annotation.Autowired
    public AdvancedLdapEventListener(AdvancedLdapUserManager advancedLdapUserManager) {
        this.advancedLdapUserManager = advancedLdapUserManager;
    }

    public void handleEvent(Event event) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Handled an event: " + event);
        if (event instanceof ReviewCreatedEvent) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ---ReviewCreatedEvent START---");
            ReviewCreatedEvent reviewCreatedEvent = (ReviewCreatedEvent) event;
            com.atlassian.crucible.spi.data.UserData userData = reviewCreatedEvent.getActioner();
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ---Actioner: dsp: " + userData.getDisplayName());
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ---Actioner: usr: " + userData.getUserName());
            this.advancedLdapUserManager.loadUser(userData);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ---ReviewCreatedEvent END---");
        }
    }

    public Class[] getHandledEventClasses() {
        return new Class[0];
    }

    /*
    @EventListener
    public void loginEvent(LoginEvent event) {
        System.out.println("Login Event: " + event);
    }
    */

    @Override
    public void destroy() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
