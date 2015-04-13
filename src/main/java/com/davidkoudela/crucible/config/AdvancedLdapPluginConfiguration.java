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
public class AdvancedLdapPluginConfiguration implements AdvancedLdapOptions {
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
    private String groupAttributeKey = "";
    private String groupFilterKey = "";
    private String GIDAttributeKey = "";
    private String groupDisplayNameKey = "";
    private String userNamesKey = "";

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    @Override
    public int getResponseTimeoutMillis() {
        return this.responseTimeoutMillis;
    }

    @Override
    public int getLDAPPageSize() {
        return this.LDAPPageSize;
    }

    @Override
    public int getLDAPSyncPeriod() {
        return this.LDAPSyncPeriod;
    }

    @Override
    public String getLDAPUrl() {
        return this.LDAPUrl;
    }

    @Override
    public String getLDAPBindDN() {
        return this.LDAPBindDN;
    }

    @Override
    public String getLDAPBindPassword() {
        return this.LDAPBindPassword;
    }

    @Override
    public String getLDAPBaseDN() {
        return this.LDAPBaseDN;
    }

    @Override
    public String getUserFilterKey() {
        return this.userFilterKey;
    }

    @Override
    public String getDisplayNameAttributeKey() {
        return this.displayNameAttributeKey;
    }

    @Override
    public String getEmailAttributeKey() {
        return this.emailAttributeKey;
    }

    @Override
    public String getUIDAttributeKey() {
        return this.UIDAttributeKey;
    }

    @Override
    public String getGroupAttributeKey() {
        return this.groupAttributeKey;
    }

    @Override
    public String getGroupFilterKey() {
        return this.groupFilterKey;
    }

    @Override
    public String getGIDAttributeKey() {
        return this.GIDAttributeKey;
    }

    @Override
    public String getGroupDisplayNameKey() {
        return this.groupDisplayNameKey;
    }

    @Override
    public String getUserNamesKey() {
        return this.userNamesKey;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    @Override
    public void setResponseTimeoutMillis(int responseTimeoutMillis) {
        this.responseTimeoutMillis = responseTimeoutMillis;
    }

    @Override
    public void setLDAPPageSize(int LDAPPageSize) {
        this.LDAPPageSize = LDAPPageSize;
    }

    @Override
    public void setLDAPSyncPeriod(int LDAPSyncPeriod) {
        this.LDAPSyncPeriod = LDAPSyncPeriod;
    }

    @Override
    public void setLDAPUrl(String LDAPUrl) {
        this.LDAPUrl = LDAPUrl;
    }

    @Override
    public void setLDAPBindDN(String LDAPBindDN) {
        this.LDAPBindDN = LDAPBindDN;
    }

    @Override
    public void setLDAPBindPassword(String LDAPBindPassword) {
        this.LDAPBindPassword = LDAPBindPassword;
    }

    @Override
    public void setLDAPBaseDN(String LDAPBaseDN) {
        this.LDAPBaseDN = LDAPBaseDN;
    }

    @Override
    public void setUserFilterKey(String userFilterKey) {
        this.userFilterKey = userFilterKey;
    }

    @Override
    public void setDisplayNameAttributeKey(String displayNameAttributeKey) {
        this.displayNameAttributeKey = displayNameAttributeKey;
    }

    @Override
    public void setEmailAttributeKey(String emailAttributeKey) {
        this.emailAttributeKey = emailAttributeKey;
    }

    @Override
    public void setUIDAttributeKey(String UIDAttributeKey) {
        this.UIDAttributeKey = UIDAttributeKey;
    }

    @Override
    public void setGroupAttributeKey(String groupAttributeKey) {
        this.groupAttributeKey = groupAttributeKey;
    }

    @Override
    public void setGroupFilterKey(String groupFilterKey) {
        this.groupFilterKey = groupFilterKey;
    }

    @Override
    public void setGIDAttributeKey(String GIDAttributeKey) {
        this.GIDAttributeKey = GIDAttributeKey;
    }

    @Override
    public void setGroupDisplayNameKey(String groupDisplayNameKey) {
        this.groupDisplayNameKey = groupDisplayNameKey;
    }

    @Override
    public void setUserNamesKey(String userNamesKey) {
        this.userNamesKey = userNamesKey;
    }
}
