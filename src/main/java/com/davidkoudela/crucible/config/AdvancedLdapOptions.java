package com.davidkoudela.crucible.config;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Description: Interface of the configuration data class.
 *              TODO: drop the interface as it does not provide any useful functionality these days.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-20
 */
@Entity
public interface AdvancedLdapOptions extends Serializable {
    public int getId();
    public int getConnectTimeoutMillis();
    public int getResponseTimeoutMillis();
    public int getLDAPPageSize();
    public int getLDAPSyncPeriod();
    public String getLDAPUrl();
    public String getLDAPBindDN();
    public String getLDAPBindPassword();
    public String getLDAPBaseDN();

    public String getUserFilterKey();
    public String getUIDAttributeKey();
    public String getDisplayNameAttributeKey();
    public String getEmailAttributeKey();
    public String getGroupAttributeKey();

    public String getGroupFilterKey();
    public String getGIDAttributeKey();
    public String getGroupDisplayNameKey();
    public String getUserNamesKey();


    public void setId(int id);
    public void setConnectTimeoutMillis(int connectTimeoutMillis);
    public void setResponseTimeoutMillis(int responseTimeoutMillis);
    public void setLDAPPageSize(int LDAPPageSize);
    public void setLDAPSyncPeriod(int LDAPSyncPeriod);
    public void setLDAPUrl(String LDAPUrl);
    public void setLDAPBindDN(String LDAPBindDN);
    public void setLDAPBindPassword(String LDAPBindPassword);
    public void setLDAPBaseDN(String LDAPBaseDN);
    public void setUserFilterKey(String userFilterKey);
    public void setDisplayNameAttributeKey(String displayNameAttributeKey);
    public void setEmailAttributeKey(String emailAttributeKey);
    public void setUIDAttributeKey(String UIDAttributeKey);
    public void setGroupAttributeKey(String groupAttributeKey);
    public void setGroupFilterKey(String groupFilterKey);
    public void setGIDAttributeKey(String GIDAttributeKey);
    public void setGroupDisplayNameKey(String groupDisplayNameKey);
    public void setUserNamesKey(String userNamesKey);
}
