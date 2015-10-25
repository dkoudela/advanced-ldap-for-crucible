package com.davidkoudela.crucible.persistence;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration;
import org.springframework.stereotype.Component;

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
    private PluginSettingsFactory settingsFactory;

    public AdvancedLdapDatabaseConfigurationDAOImpl(PluginSettingsFactory settingsFactory) {
        this.settingsFactory = settingsFactory;
    }

    @Override
    public void store(AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration) throws Exception {

    }

    @Override
    public AdvancedLdapDatabaseConfiguration get() {
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        PluginSettings settings = this.settingsFactory.createGlobalSettings();

        // Get Database configuration from plugin settings first
        advancedLdapDatabaseConfiguration.setDatabaseName((String) settings.get("AdvancedLdap.dbConfig.databaseName"));
        advancedLdapDatabaseConfiguration.setUserName((String) settings.get("AdvancedLdap.dbConfig.username"));
        advancedLdapDatabaseConfiguration.setPassword((String) settings.get("AdvancedLdap.dbConfig.password"));

        if (null != advancedLdapDatabaseConfiguration.getDatabaseName() &&
                null != advancedLdapDatabaseConfiguration.getUserName() &&
                null != advancedLdapDatabaseConfiguration.getPassword())  {
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

        return advancedLdapDatabaseConfiguration;
    }
}
