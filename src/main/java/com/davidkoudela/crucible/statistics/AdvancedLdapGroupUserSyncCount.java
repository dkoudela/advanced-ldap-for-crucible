package com.davidkoudela.crucible.statistics;

/**
 * Description: Holder class for keeping numbers of synchronized Groups and Users.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-11-09
 */
public class AdvancedLdapGroupUserSyncCount {
    private int userCountTotal = 0;
    private int userCountNew = 0;
    private int groupCountTotal = 0;
    private int groupCountNew = 0;
    private int nestedGroupCount = 0;

    public int getUserCountTotal() {
        return userCountTotal;
    }

    public void setUserCountTotal(int userCountTotal) {
        this.userCountTotal = userCountTotal;
    }

    public int getUserCountNew() {
        return userCountNew;
    }

    public void setUserCountNew(int userCountNew) {
        this.userCountNew = userCountNew;
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

    public void setGroupCountNew(int groupCountNew) {
        this.groupCountNew = groupCountNew;
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
}
