package com.davidkoudela.crucible.persistence;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * Description: Implementation of {@link AdvancedLdapDatabaseConfigurationDAO} representing the Data Access Object class
 *              for {@link com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration}.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-25
 */
@Component("advancedLdapDatabaseConfigurationDAO")
public class AdvancedLdapDatabaseConfigurationDAOImpl implements AdvancedLdapDatabaseConfigurationDAO {
    protected static final String enabled = "AdvancedLdap.dbConfig.enabled";
    protected static final String databaseName = "AdvancedLdap.dbConfig.databaseName";
    protected static final String username = "AdvancedLdap.dbConfig.username";
    protected static final String password = "AdvancedLdap.dbConfig.password";
    private PluginSettingsFactory settingsFactory;

    public AdvancedLdapDatabaseConfigurationDAOImpl(PluginSettingsFactory settingsFactory) {
        this.settingsFactory = settingsFactory;
    }

    @Override
    public void store(AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration) {
        try {
            PluginSettings settings = this.settingsFactory.createGlobalSettings();
            settings.put(this.enabled, Boolean.TRUE.toString());
            settings.put(this.databaseName, advancedLdapDatabaseConfiguration.getDatabaseName());
            settings.put(this.username, advancedLdapDatabaseConfiguration.getUserName());
            settings.put(this.password, advancedLdapDatabaseConfiguration.getPassword());
        } catch (Exception e) {
            System.out.println("Cannot store AdvancedLdapDatabaseConfiguration: " + e);
        }
    }

    @Override
    public AdvancedLdapDatabaseConfiguration get() {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();

        try {
            PluginSettings settings = this.settingsFactory.createGlobalSettings();

            // Get Database configuration from plugin settings first
            advancedLdapDatabaseConfiguration.setDatabaseName((String) settings.get(this.databaseName));
            advancedLdapDatabaseConfiguration.setUserName((String) settings.get(this.username));
            advancedLdapDatabaseConfiguration.setPassword((String) settings.get(this.password));
            Object enabledValue = settings.get(this.enabled);

            if (null != enabledValue &&
                    0 == ((String) enabledValue).compareTo(Boolean.TRUE.toString()) &&
                    null != advancedLdapDatabaseConfiguration.getDatabaseName() &&
                    null != advancedLdapDatabaseConfiguration.getUserName() &&
                    null != advancedLdapDatabaseConfiguration.getPassword()) {
                return advancedLdapDatabaseConfiguration;
            }

            // If it is not found in the plugin settings, try separate database introduced in the first plugin revisions
            advancedLdapDatabaseConfiguration.setDatabaseName(AdvancedLdapDatabaseConfigFactory.pluginDbName);
            advancedLdapDatabaseConfiguration.setUserName(AdvancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig().getUsername());
            advancedLdapDatabaseConfiguration.setPassword(AdvancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig().getPassword());
            if (false != AdvancedLdapDatabaseConfigFactory.verifyDatabaseConfig(advancedLdapDatabaseConfiguration)) {
                return advancedLdapDatabaseConfiguration;
            }

            // If the separate database is not found, use the default crucible database
            advancedLdapDatabaseConfiguration.setDatabaseName(AdvancedLdapDatabaseConfigFactory.extractDatabaseName(AdvancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig()));
            advancedLdapDatabaseConfiguration.setUserName(AdvancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig().getUsername());
            advancedLdapDatabaseConfiguration.setPassword(AdvancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig().getPassword());
        } catch (Exception e) {
            System.out.println("Cannot get AdvancedLdapDatabaseConfiguration: " + e);
        }

        return advancedLdapDatabaseConfiguration;
    }

    @Override
    public void remove() {
        try {
            PluginSettings settings = this.settingsFactory.createGlobalSettings();
            settings.put(this.enabled, Boolean.FALSE.toString());
        } catch (Exception e) {
            System.out.println("Cannot remove AdvancedLdapDatabaseConfiguration: " + e);
        }
    }

}
