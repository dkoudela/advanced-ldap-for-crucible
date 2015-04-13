package com.davidkoudela.crucible.ldap.model;

import com.cenqua.fisheye.user.UserManager;

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

    public String getGID() {
        return GID;
    }

    public String getNormalizedGID() {
        String normalizedGID = this.GID;
        String[] dictionary = normalizedGID.split(UserManager.GROUPNAME_PATTERN.pattern());
        if (0 != dictionary.length) {
            for (String replaceChar : dictionary) {
                if (!replaceChar.isEmpty())
                    normalizedGID = normalizedGID.replace(replaceChar, "-");
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
}
