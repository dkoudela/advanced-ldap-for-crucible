package it.com.davidkoudela.crucible;

import com.davidkoudela.crucible.admin.AdvancedLdapUserManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;
import com.atlassian.sal.api.ApplicationProperties;

import static org.junit.Assert.assertEquals;

@RunWith(AtlassianPluginsTestRunner.class)
public class AdvancedLdapUserManagerWiredTest
{
    private final ApplicationProperties applicationProperties;
    private final AdvancedLdapUserManager advancedLdapUserManager;

    public AdvancedLdapUserManagerWiredTest(ApplicationProperties applicationProperties, AdvancedLdapUserManager advancedLdapUserManager)
    {
        this.applicationProperties = applicationProperties;
        this.advancedLdapUserManager = advancedLdapUserManager;
    }

    @Test
    public void testMyName()
    {
//        assertEquals("names do not match!", "myComponent:" + applicationProperties.getDisplayName(), advancedLdapUserManager.getName());
    }
}