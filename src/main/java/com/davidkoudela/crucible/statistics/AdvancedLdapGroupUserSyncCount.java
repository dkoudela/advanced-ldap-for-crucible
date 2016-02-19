package com.davidkoudela.crucible.statistics;

import org.apache.log4j.Logger;

/**
 * Description: Holder class for keeping numbers of synchronized Groups and Users.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-11-09
 */
public class AdvancedLdapGroupUserSyncCount {
    private Logger log = Logger.getLogger(this.getClass());
    private int userCountTotal = 0;
    private int userCountNew = 0;
    private int groupCountTotal = 0;
    private int groupCountNew = 0;
    private int nestedGroupCount = 0;
    private int addedUsersToGroupsCount = 0;
    private int removedUsersFromGroupsCount = 0;

    public int getUserCountTotal() {
        return userCountTotal;
    }

    public void setUserCountTotal(int userCountTotal) {
        this.userCountTotal = userCountTotal;
    }

    public int getUserCountNew() {
        return userCountNew;
    }

    public void incrementUserCountNew() {
        this.userCountNew++;
    }

    public int getGroupCountTotal() {
        return groupCountTotal;
    }

    public void setGroupCountTotal(int groupCountTotal) {
        this.groupCountTotal = groupCountTotal;
    }

    public int getGroupCountNew() {
        return groupCountNew;
    }

    public void incrementGroupCountNew() {
        this.groupCountNew++;
    }

    public int getNestedGroupCount() {
        return nestedGroupCount;
    }

    public void setNestedGroupCount(int nestedGroupCount) {
        this.nestedGroupCount = nestedGroupCount;
    }

    public int getAddedUsersToGroupsCount() {
        return addedUsersToGroupsCount;
    }

    public void incrementAddedUsersToGroupsCount() {
        this.addedUsersToGroupsCount++;
    }

    public int getRemovedUsersFromGroupsCount() {
        return removedUsersFromGroupsCount;
    }

    public void incrementRemovedUsersFromGroupsCount() {
        this.removedUsersFromGroupsCount++;
    }

    public void print() {
        log.info("Groups total:            " + getGroupCountTotal());
        log.info("Groups new:              " + getGroupCountNew());
        log.info("Nested Groups:           " + getNestedGroupCount());
        log.info("Users total:             " + getUserCountTotal());
        log.info("Users new:               " + getUserCountNew());
        log.info("Added Users To Groups:   " + getAddedUsersToGroupsCount());
        log.info("Removed Users To Groups: " + getRemovedUsersFromGroupsCount());
    }
}
