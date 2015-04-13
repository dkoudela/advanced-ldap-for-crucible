package com.davidkoudela.crucible.ldap.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: {@link AdvancedLdapPerson} contains all necessary LDAP person parameters and their access methods.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-21
 */
public class AdvancedLdapPerson {
    private String uid;
    private String displayName;
    private String email;
    private List<AdvancedLdapGroup> groupList = new ArrayList<AdvancedLdapGroup>();

    public String getUid() {
        return uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public List<AdvancedLdapGroup> getGroupList() {
        return groupList;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGroupList(List<AdvancedLdapGroup> groupList) {
        this.groupList = groupList;
    }
}
