package com.davidkoudela.crucible.ldap.model;

import com.atlassian.fecru.user.GroupName;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: {@link AdvancedLdapGroup} contains all necessary LDAP group parameters and their access methods.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-21
 */
public class AdvancedLdapGroup {
    private String GID;
    private String displayName;
    private List<String> userNames;
    private List<AdvancedLdapPerson> personList = new ArrayList<AdvancedLdapPerson>();

    public String getGID() {
        return GID;
    }

    public String getNormalizedGID() {
        String normalizedGID = this.GID.toLowerCase();
        String[] dictionary = normalizedGID.split(GroupName.GROUPNAME_PATTERN.pattern());
        if (0 != dictionary.length) {
            for (String replaceChar : dictionary) {
                if (!replaceChar.isEmpty())
                    for (char aChar : replaceChar.toCharArray())
                        normalizedGID = normalizedGID.replace(aChar, '-');
            }
        }
        return normalizedGID;
    }

    public void setGID(String GID) {
        this.GID = GID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    public List<AdvancedLdapPerson> getPersonList() {
        return this.personList;
    }

    public void setPersonList(List<AdvancedLdapPerson> personList) {
        this.personList = personList;
    }

    public Boolean isUIDInPersonList(String UID) {
        for (AdvancedLdapPerson advancedLdapPerson : this.personList) {
            if (0 == advancedLdapPerson.getUid().compareToIgnoreCase(UID)) {
                return true;
            }
        }
        return false;
    }
}
