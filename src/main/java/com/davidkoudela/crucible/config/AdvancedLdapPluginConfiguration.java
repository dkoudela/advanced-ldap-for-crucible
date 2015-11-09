package com.davidkoudela.crucible.config;

import javax.persistence.Entity;

/**
 * Description: The Configuration data class providing all necessary LDAP configuration parameters and their access methods.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-20
 */
@Entity
public class AdvancedLdapPluginConfiguration {
    public static final String RECORD_REVISION = "1.0";
    private int id;
    private int connectTimeoutMillis = 10000;
    private int responseTimeoutMillis = 10000;
    private int LDAPPageSize = 99;
    private int LDAPSyncPeriod = 3600;
    private String LDAPUrl = "";
    private String LDAPBindDN = "";
    private String LDAPBindPassword = "";
    private String LDAPBaseDN = "";
    private String userFilterKey = "";
    private String displayNameAttributeKey = "";
    private String emailAttributeKey = "";
    private String UIDAttributeKey = "";
    private String userGroupNamesKey = "";
    private String groupFilterKey = "";
    private String GIDAttributeKey = "";
    private String groupDisplayNameKey = "";
    private String userNamesKey = "";
    private String recordRevision = RECORD_REVISION;
    private Boolean nestedGroupsEnabled = false;

    public int getId() {
        return id;
    }

    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    public int getResponseTimeoutMillis() {
        return this.responseTimeoutMillis;
    }

    public int getLDAPPageSize() {
        return this.LDAPPageSize;
    }

    public int getLDAPSyncPeriod() {
        return this.LDAPSyncPeriod;
    }

    public String getLDAPUrl() {
        return this.LDAPUrl;
    }

    public String getLDAPBindDN() {
        return this.LDAPBindDN;
    }

    public String getLDAPBindPassword() {
        return this.LDAPBindPassword;
    }

    public String getLDAPBaseDN() {
        return this.LDAPBaseDN;
    }

    public String getUserFilterKey() {
        return this.userFilterKey;
    }

    public String getDisplayNameAttributeKey() {
        return this.displayNameAttributeKey;
    }

    public String getEmailAttributeKey() {
        return this.emailAttributeKey;
    }

    public String getUIDAttributeKey() {
        return this.UIDAttributeKey;
    }

    public String getUserGroupNamesKey() {
        return this.userGroupNamesKey;
    }

    public String getGroupFilterKey() {
        return this.groupFilterKey;
    }

    public String getGIDAttributeKey() {
        return this.GIDAttributeKey;
    }

    public String getGroupDisplayNameKey() {
        return this.groupDisplayNameKey;
    }

    public String getUserNamesKey() {
        return this.userNamesKey;
    }

    public String getRecordRevision() {
        return recordRevision;
    }

    public Boolean isNestedGroupsEnabled() {
        return nestedGroupsEnabled;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public void setResponseTimeoutMillis(int responseTimeoutMillis) {
        this.responseTimeoutMillis = responseTimeoutMillis;
    }

    public void setLDAPPageSize(int LDAPPageSize) {
        this.LDAPPageSize = LDAPPageSize;
    }

    public void setLDAPSyncPeriod(int LDAPSyncPeriod) {
        this.LDAPSyncPeriod = LDAPSyncPeriod;
    }

    public void setLDAPUrl(String LDAPUrl) {
        this.LDAPUrl = LDAPUrl;
    }

    public void setLDAPBindDN(String LDAPBindDN) {
        this.LDAPBindDN = LDAPBindDN;
    }

    public void setLDAPBindPassword(String LDAPBindPassword) {
        this.LDAPBindPassword = LDAPBindPassword;
    }

    public void setLDAPBaseDN(String LDAPBaseDN) {
        this.LDAPBaseDN = LDAPBaseDN;
    }

    public void setUserFilterKey(String userFilterKey) {
        this.userFilterKey = userFilterKey;
    }

    public void setDisplayNameAttributeKey(String displayNameAttributeKey) {
        this.displayNameAttributeKey = displayNameAttributeKey;
    }

    public void setEmailAttributeKey(String emailAttributeKey) {
        this.emailAttributeKey = emailAttributeKey;
    }

    public void setUIDAttributeKey(String UIDAttributeKey) {
        this.UIDAttributeKey = UIDAttributeKey;
    }

    public void setUserGroupNamesKey(String userGroupNamesKey) {
        this.userGroupNamesKey = userGroupNamesKey;
    }

    public void setGroupFilterKey(String groupFilterKey) {
        this.groupFilterKey = groupFilterKey;
    }

    public void setGIDAttributeKey(String GIDAttributeKey) {
        this.GIDAttributeKey = GIDAttributeKey;
    }

    public void setGroupDisplayNameKey(String groupDisplayNameKey) {
        this.groupDisplayNameKey = groupDisplayNameKey;
    }

    public void setUserNamesKey(String userNamesKey) {
        this.userNamesKey = userNamesKey;
    }

    public void setRecordRevision(String recordRevision) {
        this.recordRevision = recordRevision;
    }

    public void setNestedGroupsEnabled(Boolean nestedGroupsEnabled) {
        if (null == nestedGroupsEnabled)
            this.nestedGroupsEnabled = false;
        else
            this.nestedGroupsEnabled = nestedGroupsEnabled;
    }


    public String toString() {
        return "{ id=\"" + id + "\", connectTimeoutMillis=\"" + connectTimeoutMillis + "\", responseTimeoutMillis=\"" + responseTimeoutMillis +
                "\", LDAPPageSize=\"" + LDAPPageSize + "\", LDAPSyncPeriod=\"" + LDAPSyncPeriod + "\", LDAPUrl=\"" + LDAPUrl +
                "\", LDAPBindDN=\"" + LDAPBindDN + "\", LDAPBindPassword=\"" + LDAPBindPassword + "\", LDAPBaseDN=\"" + LDAPBaseDN +
                "\", userFilterKey=\"" + userFilterKey + "\", displayNameAttributeKey=\"" + displayNameAttributeKey +
                "\", emailAttributeKey=\"" + emailAttributeKey + "\", UIDAttributeKey=\"" + UIDAttributeKey +
                "\", userGroupNamesKey=\"" + userGroupNamesKey + "\", groupFilterKey=\"" + groupFilterKey +
                "\", GIDAttributeKey=\"" + GIDAttributeKey + "\", groupDisplayNameKey=\"" + groupDisplayNameKey +
                "\", userNamesKey=\"" + userNamesKey + "\", nestedGroupsEnabled=\"" + nestedGroupsEnabled + "\" }";
    }
}
