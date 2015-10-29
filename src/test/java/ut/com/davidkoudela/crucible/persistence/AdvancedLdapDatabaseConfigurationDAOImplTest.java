package ut.com.davidkoudela.crucible.persistence;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.cenqua.crucible.hibernate.DBType;
import com.cenqua.crucible.hibernate.DatabaseConfig;
import com.cenqua.fisheye.AppConfig;
import com.cenqua.fisheye.config1.DriverSource;
import com.davidkoudela.crucible.config.AdvancedLdapDatabaseConfiguration;
import com.davidkoudela.crucible.persistence.AdvancedLdapDatabaseConfigFactory;
import com.davidkoudela.crucible.persistence.AdvancedLdapDatabaseConfigurationDAOImpl;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Description: Testing {@link AdvancedLdapDatabaseConfigurationDAOImpl}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-28
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapDatabaseConfigurationDAOImplTest extends TestCase {
    private static PluginSettingsFactory pluginSettingsFactory;
    private static PluginSettings pluginSettings;
    private static AdvancedLdapDatabaseConfigFactory advancedLdapDatabaseConfigFactory;
    private static DatabaseConfig databaseConfig;
    private static ArgumentCaptor<String> argumentCaptorEnable;
    private static ArgumentCaptor<String> argumentCaptorDatabaseName;
    private static ArgumentCaptor<String> argumentCaptorUsername;
    private static ArgumentCaptor<String> argumentCaptorPassword;

    public class AdvancedLdapDatabaseConfigurationDAOImplDummy extends AdvancedLdapDatabaseConfigurationDAOImpl {
        public AdvancedLdapDatabaseConfigurationDAOImplDummy(PluginSettingsFactory settingsFactory) {
            super(settingsFactory);
        }

        public void setAdvancedLdapDatabaseConfigFactory(AdvancedLdapDatabaseConfigFactory advancedLdapDatabaseConfigFactory) {
            super.setAdvancedLdapDatabaseConfigFactory(advancedLdapDatabaseConfigFactory);
        }

        public String getEnabled() {return enabled;}
        public String getDatabaseName() {return databaseName;}
        public String getUsername() {return username;}
        public String getPassword() {return password;}
    }

    @Before
    public void init() {
        this.pluginSettingsFactory = Mockito.mock(PluginSettingsFactory.class);
        this.pluginSettings = Mockito.mock(PluginSettings.class);
        this.advancedLdapDatabaseConfigFactory = Mockito.mock(AdvancedLdapDatabaseConfigFactory.class);
        this.argumentCaptorEnable = ArgumentCaptor.forClass(String.class);
        this.argumentCaptorDatabaseName = ArgumentCaptor.forClass(String.class);
        this.argumentCaptorUsername = ArgumentCaptor.forClass(String.class);
        this.argumentCaptorPassword = ArgumentCaptor.forClass(String.class);
        this.databaseConfig = new DatabaseConfig(DBType.ORACLE, "jdbc:oracle:thin:@localhost:1521:crucible", "crucible", "pwd", DriverSource.BUNDLED, 5, 20);
    }

    @Test
    public void testStore() {
        AdvancedLdapDatabaseConfigurationDAOImplDummy advancedLdapDatabaseConfigurationDAOImplDummy = new AdvancedLdapDatabaseConfigurationDAOImplDummy(this.pluginSettingsFactory);
        Mockito.when(this.pluginSettingsFactory.createGlobalSettings()).thenReturn(this.pluginSettings);
        Mockito.when(this.pluginSettings.put(ArgumentCaptor.forClass(String.class).capture(), this.argumentCaptorEnable.capture())).thenReturn(null);
        Mockito.when(this.pluginSettings.put(ArgumentCaptor.forClass(String.class).capture(), this.argumentCaptorDatabaseName.capture())).thenReturn(null);
        Mockito.when(this.pluginSettings.put(ArgumentCaptor.forClass(String.class).capture(), this.argumentCaptorUsername.capture())).thenReturn(null);
        Mockito.when(this.pluginSettings.put(ArgumentCaptor.forClass(String.class).capture(), this.argumentCaptorPassword.capture())).thenReturn(null);

        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("crucible");
        advancedLdapDatabaseConfiguration.setUserName("sa");
        advancedLdapDatabaseConfiguration.setPassword("pwd");

        advancedLdapDatabaseConfigurationDAOImplDummy.store(advancedLdapDatabaseConfiguration);

        Mockito.verify(this.pluginSettingsFactory, Mockito.times(1)).createGlobalSettings();
        Mockito.verify(this.pluginSettings, Mockito.times(1)).put(advancedLdapDatabaseConfigurationDAOImplDummy.getEnabled(), Boolean.TRUE.toString());
        Mockito.verify(this.pluginSettings, Mockito.times(1)).put(advancedLdapDatabaseConfigurationDAOImplDummy.getDatabaseName(), advancedLdapDatabaseConfiguration.getDatabaseName());
        Mockito.verify(this.pluginSettings, Mockito.times(1)).put(advancedLdapDatabaseConfigurationDAOImplDummy.getUsername(), advancedLdapDatabaseConfiguration.getUserName());
        Mockito.verify(this.pluginSettings, Mockito.times(1)).put(advancedLdapDatabaseConfigurationDAOImplDummy.getPassword(), advancedLdapDatabaseConfiguration.getPassword());
    }

    @Test
    public void testGetSettingsStored() {
        AdvancedLdapDatabaseConfigurationDAOImplDummy advancedLdapDatabaseConfigurationDAOImplDummy = new AdvancedLdapDatabaseConfigurationDAOImplDummy(this.pluginSettingsFactory);
        Mockito.when(this.pluginSettingsFactory.createGlobalSettings()).thenReturn(this.pluginSettings);
        Mockito.when(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getDatabaseName())).thenReturn("crucible");
        Mockito.when(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getUsername())).thenReturn("sa");
        Mockito.when(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getPassword())).thenReturn("pwd");
        Mockito.when(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getEnabled())).thenReturn(Boolean.TRUE.toString());

        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = advancedLdapDatabaseConfigurationDAOImplDummy.get();

        assertEquals("crucible", advancedLdapDatabaseConfiguration.getDatabaseName());
        assertEquals("sa", advancedLdapDatabaseConfiguration.getUserName());
        assertEquals("pwd", advancedLdapDatabaseConfiguration.getPassword());
    }

    @Test
    public void testGetNoSettingsStoredSeparateDatabaseFound() {
/*
        AdvancedLdapDatabaseConfigurationDAOImplDummy advancedLdapDatabaseConfigurationDAOImplDummy = new AdvancedLdapDatabaseConfigurationDAOImplDummy(this.pluginSettingsFactory);
        advancedLdapDatabaseConfigurationDAOImplDummy.setAdvancedLdapDatabaseConfigFactory(this.advancedLdapDatabaseConfigFactory);
        Mockito.when(this.pluginSettingsFactory.createGlobalSettings()).thenReturn(this.pluginSettings);
        Mockito.when(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getDatabaseName())).thenReturn(null);
        Mockito.when(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getUsername())).thenReturn(null);
        Mockito.when(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getPassword())).thenReturn(null);
        Mockito.when(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getEnabled())).thenReturn(null);
        //Mockito.when(this.advancedLdapDatabaseConfigFactory.pluginDbName).thenReturn(AdvancedLdapDatabaseConfigFactory.pluginDbName);
        Mockito.when(this.advancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig()).thenReturn(this.databaseConfig);
        Mockito.when(this.advancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig()).thenReturn(this.databaseConfig);
        Mockito.when(this.advancedLdapDatabaseConfigFactory.verifyDatabaseConfig(ArgumentCaptor.forClass(AdvancedLdapDatabaseConfiguration.class).capture())).thenReturn(true);

        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = advancedLdapDatabaseConfigurationDAOImplDummy.get();

        assertEquals("crucible", advancedLdapDatabaseConfiguration.getDatabaseName());
        assertEquals("crucible", advancedLdapDatabaseConfiguration.getUserName());
        assertEquals("pwd", advancedLdapDatabaseConfiguration.getPassword());
*/
    }

    @Test
    public void testGetNoSettingsStoredNoSeparateDatabaseFound() {
    }
}
