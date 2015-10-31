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
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Description: Testing {@link AdvancedLdapDatabaseConfigurationDAOImpl}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-28
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PluginSettingsFactory.class, PluginSettings.class, AdvancedLdapDatabaseConfigFactory.class})
@PowerMockIgnore("javax.management.*")
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
        this.pluginSettingsFactory = PowerMock.createMock(PluginSettingsFactory.class);
        this.pluginSettings = PowerMock.createMock(PluginSettings.class);
        this.advancedLdapDatabaseConfigFactory = PowerMock.createMock(AdvancedLdapDatabaseConfigFactory.class);
        this.argumentCaptorEnable = ArgumentCaptor.forClass(String.class);
        this.argumentCaptorDatabaseName = ArgumentCaptor.forClass(String.class);
        this.argumentCaptorUsername = ArgumentCaptor.forClass(String.class);
        this.argumentCaptorPassword = ArgumentCaptor.forClass(String.class);
        this.databaseConfig = new DatabaseConfig(DBType.ORACLE, "jdbc:oracle:thin:@localhost:1521:crucible", "crucible", "pwd", DriverSource.BUNDLED, 5, 20);
    }

    @Test
    public void testStore() {
        AdvancedLdapDatabaseConfigurationDAOImplDummy advancedLdapDatabaseConfigurationDAOImplDummy = new AdvancedLdapDatabaseConfigurationDAOImplDummy(this.pluginSettingsFactory);
        EasyMock.expect(this.pluginSettingsFactory.createGlobalSettings()).andReturn(this.pluginSettings);
        EasyMock.expect(this.pluginSettings.put(advancedLdapDatabaseConfigurationDAOImplDummy.getEnabled(), Boolean.TRUE.toString())).andReturn(null);
        EasyMock.expect(this.pluginSettings.put(advancedLdapDatabaseConfigurationDAOImplDummy.getDatabaseName(), "crucible")).andReturn(null);
        EasyMock.expect(this.pluginSettings.put(advancedLdapDatabaseConfigurationDAOImplDummy.getUsername(), "sa")).andReturn(null);
        EasyMock.expect(this.pluginSettings.put(advancedLdapDatabaseConfigurationDAOImplDummy.getPassword(), "pwd")).andReturn(null);

        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfiguration.setDatabaseName("crucible");
        advancedLdapDatabaseConfiguration.setUserName("sa");
        advancedLdapDatabaseConfiguration.setPassword("pwd");

        PowerMock.replayAll();
        advancedLdapDatabaseConfigurationDAOImplDummy.store(advancedLdapDatabaseConfiguration);
        PowerMock.verifyAll();
    }

    @Test
    public void testGetSettingsStored() {
        AdvancedLdapDatabaseConfigurationDAOImplDummy advancedLdapDatabaseConfigurationDAOImplDummy = new AdvancedLdapDatabaseConfigurationDAOImplDummy(this.pluginSettingsFactory);
        EasyMock.expect(this.pluginSettingsFactory.createGlobalSettings()).andReturn(this.pluginSettings);
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getDatabaseName())).andReturn("crucible");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getUsername())).andReturn("sa");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getPassword())).andReturn("pwd");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getEnabled())).andReturn(Boolean.TRUE.toString());
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getEnabled())).andReturn(Boolean.TRUE.toString());

        PowerMock.replayAll();
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = advancedLdapDatabaseConfigurationDAOImplDummy.get();
        PowerMock.verifyAll();

        assertEquals("crucible", advancedLdapDatabaseConfiguration.getDatabaseName());
        assertEquals("sa", advancedLdapDatabaseConfiguration.getUserName());
        assertEquals("pwd", advancedLdapDatabaseConfiguration.getPassword());
    }

    @Test
    public void testGetNoSettingsStoredSeparateDatabaseFound() {
        AdvancedLdapDatabaseConfigurationDAOImplDummy advancedLdapDatabaseConfigurationDAOImplDummy = new AdvancedLdapDatabaseConfigurationDAOImplDummy(this.pluginSettingsFactory);
        advancedLdapDatabaseConfigurationDAOImplDummy.setAdvancedLdapDatabaseConfigFactory(this.advancedLdapDatabaseConfigFactory);
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfigurationDefault = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfigurationDefault.setDatabaseName(AdvancedLdapDatabaseConfigFactory.pluginDbName);
        advancedLdapDatabaseConfigurationDefault.setUserName("sa");
        advancedLdapDatabaseConfigurationDefault.setPassword("pwd");

        EasyMock.expect(this.pluginSettingsFactory.createGlobalSettings()).andReturn(this.pluginSettings);
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getDatabaseName())).andReturn("crucible");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getUsername())).andReturn("sa");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getPassword())).andReturn("pwd");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getEnabled())).andReturn(Boolean.FALSE.toString());
        EasyMock.expect(this.advancedLdapDatabaseConfigFactory.verifyDatabaseConfig(advancedLdapDatabaseConfigurationDefault)).andReturn(true);

        PowerMock.replayAll();
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = advancedLdapDatabaseConfigurationDAOImplDummy.get();
        PowerMock.verifyAll();
    }

    @Test
    public void testGetNoSettingsStoredNoSeparateDatabaseFound() {

    }

    @Test
    public void testRemove() {
        AdvancedLdapDatabaseConfigurationDAOImplDummy advancedLdapDatabaseConfigurationDAOImplDummy = new AdvancedLdapDatabaseConfigurationDAOImplDummy(this.pluginSettingsFactory);
        Mockito.when(this.pluginSettingsFactory.createGlobalSettings()).thenReturn(this.pluginSettings);
        Mockito.when(this.pluginSettings.put(ArgumentCaptor.forClass(String.class).capture(), this.argumentCaptorEnable.capture())).thenReturn(null);

        advancedLdapDatabaseConfigurationDAOImplDummy.remove();

        Mockito.verify(this.pluginSettingsFactory, Mockito.times(1)).createGlobalSettings();
        Mockito.verify(this.pluginSettings, Mockito.times(1)).put(advancedLdapDatabaseConfigurationDAOImplDummy.getEnabled(), Boolean.FALSE.toString());
    }
}
