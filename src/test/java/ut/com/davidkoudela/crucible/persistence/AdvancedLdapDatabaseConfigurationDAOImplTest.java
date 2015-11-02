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
public class AdvancedLdapDatabaseConfigurationDAOImplTest extends TestCase {
    private PluginSettingsFactory pluginSettingsFactory;
    private PluginSettings pluginSettings;
    private static DatabaseConfig databaseConfig;

    public class AdvancedLdapDatabaseConfigurationDAOImplDummy extends AdvancedLdapDatabaseConfigurationDAOImpl {
        public AdvancedLdapDatabaseConfigurationDAOImplDummy(PluginSettingsFactory settingsFactory) {
            super(settingsFactory);
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
        PowerMock.mockStatic(AdvancedLdapDatabaseConfigFactory.class);
        this.databaseConfig = new DatabaseConfig(DBType.ORACLE, "jdbc:oracle:thin:@localhost:1521:crucible", "sa", "", DriverSource.BUNDLED, 5, 20);
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
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfigurationDefault = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfigurationDefault.setDatabaseName(AdvancedLdapDatabaseConfigFactory.pluginDbName);
        advancedLdapDatabaseConfigurationDefault.setUserName("sa");
        advancedLdapDatabaseConfigurationDefault.setPassword("");

        EasyMock.expect(this.pluginSettingsFactory.createGlobalSettings()).andReturn(this.pluginSettings);
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getDatabaseName())).andReturn("crucible");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getUsername())).andReturn("sa");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getPassword())).andReturn("");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getEnabled())).andReturn(Boolean.FALSE.toString());
        EasyMock.expect(AdvancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig()).andReturn(this.databaseConfig).times(2);
        EasyMock.expect(AdvancedLdapDatabaseConfigFactory.verifyDatabaseConfig(advancedLdapDatabaseConfigurationDefault)).andReturn(true);

        PowerMock.replayAll();
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = advancedLdapDatabaseConfigurationDAOImplDummy.get();
        PowerMock.verifyAll();

        assertEquals("crucibleadldb", advancedLdapDatabaseConfiguration.getDatabaseName());
        assertEquals("sa", advancedLdapDatabaseConfiguration.getUserName());
        assertEquals("", advancedLdapDatabaseConfiguration.getPassword());
    }

    @Test
    public void testGetNoSettingsStoredNoSeparateDatabaseFound() {
        AdvancedLdapDatabaseConfigurationDAOImplDummy advancedLdapDatabaseConfigurationDAOImplDummy = new AdvancedLdapDatabaseConfigurationDAOImplDummy(this.pluginSettingsFactory);
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfigurationDefault = new AdvancedLdapDatabaseConfiguration();
        advancedLdapDatabaseConfigurationDefault.setDatabaseName(AdvancedLdapDatabaseConfigFactory.pluginDbName);
        advancedLdapDatabaseConfigurationDefault.setUserName("sa");
        advancedLdapDatabaseConfigurationDefault.setPassword("");

        EasyMock.expect(this.pluginSettingsFactory.createGlobalSettings()).andReturn(this.pluginSettings);
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getDatabaseName())).andReturn("crucible");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getUsername())).andReturn("sa");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getPassword())).andReturn("");
        EasyMock.expect(this.pluginSettings.get(advancedLdapDatabaseConfigurationDAOImplDummy.getEnabled())).andReturn(Boolean.FALSE.toString());
        EasyMock.expect(AdvancedLdapDatabaseConfigFactory.getCrucibleDefaultDatabaseConfig()).andReturn(this.databaseConfig).times(5);
        EasyMock.expect(AdvancedLdapDatabaseConfigFactory.verifyDatabaseConfig(advancedLdapDatabaseConfigurationDefault)).andReturn(false);
        EasyMock.expect(AdvancedLdapDatabaseConfigFactory.extractDatabaseName(this.databaseConfig)).andReturn("crucible");

        PowerMock.replayAll();
        PowerMock.replay(AdvancedLdapDatabaseConfigFactory.class);
        AdvancedLdapDatabaseConfiguration advancedLdapDatabaseConfiguration = advancedLdapDatabaseConfigurationDAOImplDummy.get();
        PowerMock.verifyAll();

        assertEquals("crucible", advancedLdapDatabaseConfiguration.getDatabaseName());
        assertEquals("sa", advancedLdapDatabaseConfiguration.getUserName());
        assertEquals("", advancedLdapDatabaseConfiguration.getPassword());
    }

    @Test
    public void testRemove() {
        AdvancedLdapDatabaseConfigurationDAOImplDummy advancedLdapDatabaseConfigurationDAOImplDummy = new AdvancedLdapDatabaseConfigurationDAOImplDummy(this.pluginSettingsFactory);

        EasyMock.expect(this.pluginSettingsFactory.createGlobalSettings()).andReturn(this.pluginSettings);
        EasyMock.expect(this.pluginSettings.put(advancedLdapDatabaseConfigurationDAOImplDummy.getEnabled(), Boolean.FALSE.toString())).andReturn(null);

        PowerMock.replayAll();
        advancedLdapDatabaseConfigurationDAOImplDummy.remove();
        PowerMock.verifyAll();
    }
}
