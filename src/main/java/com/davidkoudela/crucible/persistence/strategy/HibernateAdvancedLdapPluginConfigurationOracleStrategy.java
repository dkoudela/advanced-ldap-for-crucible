package com.davidkoudela.crucible.persistence.strategy;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;

/**
 * Description: {@link HibernateAdvancedLdapPluginConfigurationOracleStrategy}
 *              provides a persistence storage strategies for Oracle database.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-09-04
 */
public class HibernateAdvancedLdapPluginConfigurationOracleStrategy implements HibernateAdvancedLdapPluginConfigurationPersistenceStrategy {
    @Override
    public AdvancedLdapPluginConfiguration transformFromStorage(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration) {
        final String fromString = " ";
        final String toString = "";
        convertAdvancedLdapPluginConfigurationStrings(
                advancedLdapPluginConfiguration, fromString, toString);
        return advancedLdapPluginConfiguration;
    }

    @Override
    public AdvancedLdapPluginConfiguration transformToStorage(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration) {
        final String fromString = "";
        final String toString = " ";

        convertAdvancedLdapPluginConfigurationStrings(
                advancedLdapPluginConfiguration, fromString, toString);
        return advancedLdapPluginConfiguration;
    }

    private static void convertAdvancedLdapPluginConfigurationStrings(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration,
                                                                     String fromString, String toString) {
        if (null == advancedLdapPluginConfiguration.getLDAPUrl() || 0 == advancedLdapPluginConfiguration.getLDAPUrl().compareTo(fromString))
            advancedLdapPluginConfiguration.setLDAPUrl(toString);
        if (null == advancedLdapPluginConfiguration.getLDAPBindDN() || 0 == advancedLdapPluginConfiguration.getLDAPBindDN().compareTo(fromString))
            advancedLdapPluginConfiguration.setLDAPBindDN(toString);
        if (null == advancedLdapPluginConfiguration.getLDAPBindPassword() || 0 == advancedLdapPluginConfiguration.getLDAPBindPassword().compareTo(fromString))
            advancedLdapPluginConfiguration.setLDAPBindPassword(toString);
        if (null == advancedLdapPluginConfiguration.getLDAPBaseDN() || 0 == advancedLdapPluginConfiguration.getLDAPBaseDN().compareTo(fromString))
            advancedLdapPluginConfiguration.setLDAPBaseDN(toString);
        if (null == advancedLdapPluginConfiguration.getUserFilterKey() || 0 == advancedLdapPluginConfiguration.getUserFilterKey().compareTo(fromString))
            advancedLdapPluginConfiguration.setUserFilterKey(toString);
        if (null == advancedLdapPluginConfiguration.getDisplayNameAttributeKey() || 0 == advancedLdapPluginConfiguration.getDisplayNameAttributeKey().compareTo(fromString))
            advancedLdapPluginConfiguration.setDisplayNameAttributeKey(toString);
        if (null == advancedLdapPluginConfiguration.getEmailAttributeKey() || 0 == advancedLdapPluginConfiguration.getEmailAttributeKey().compareTo(fromString))
            advancedLdapPluginConfiguration.setEmailAttributeKey(toString);
        if (null == advancedLdapPluginConfiguration.getUIDAttributeKey() || 0 == advancedLdapPluginConfiguration.getUIDAttributeKey().compareTo(fromString))
            advancedLdapPluginConfiguration.setUIDAttributeKey(toString);
        if (null == advancedLdapPluginConfiguration.getUserGroupNamesKey() || 0 == advancedLdapPluginConfiguration.getUserGroupNamesKey().compareTo(fromString))
            advancedLdapPluginConfiguration.setUserGroupNamesKey(toString);
        if (null == advancedLdapPluginConfiguration.getGroupFilterKey() || 0 == advancedLdapPluginConfiguration.getGroupFilterKey().compareTo(fromString))
            advancedLdapPluginConfiguration.setGroupFilterKey(toString);
        if (null == advancedLdapPluginConfiguration.getGIDAttributeKey() || 0 == advancedLdapPluginConfiguration.getGIDAttributeKey().compareTo(fromString))
            advancedLdapPluginConfiguration.setGIDAttributeKey(toString);
        if (null == advancedLdapPluginConfiguration.getGroupDisplayNameKey() || 0 == advancedLdapPluginConfiguration.getGroupDisplayNameKey().compareTo(fromString))
            advancedLdapPluginConfiguration.setGroupDisplayNameKey(toString);
        if (null == advancedLdapPluginConfiguration.getUserNamesKey() || 0 == advancedLdapPluginConfiguration.getUserNamesKey().compareTo(fromString))
            advancedLdapPluginConfiguration.setUserNamesKey(toString);
    }
}
