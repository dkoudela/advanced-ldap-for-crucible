package ut.com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapBind;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapBindBuilder;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.SearchResultEntry;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Description: Testing {@link AdvancedLdapBindBuilder}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-17
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapBindBuilderTest extends TestCase {
    private static AdvancedLdapBindBuilderDummy advancedLdapBindBuilderDummy;
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private static AdvancedLdapConnector advancedLdapConnector;
    private static ArgumentCaptor<AdvancedLdapPluginConfiguration> argumentCaptorAdvancedLdapPluginConfiguration;
    private static ArgumentCaptor<String> argumentCaptorDnString;
    private static ArgumentCaptor<String> argumentCaptorPwdString;
    private static SearchResultEntry searchResultEntry;

    public class AdvancedLdapBindBuilderDummy extends AdvancedLdapBindBuilder {
        public AdvancedLdapBindBuilderDummy(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration, String password) {
            super(advancedLdapPluginConfiguration, password);
        }

        public void setAdvancedLdapConnector(AdvancedLdapConnector advancedLdapConnector) {
            super.setAdvancedLdapConnector(advancedLdapConnector);
        }
    }

    @Before
    public void init() {
        this.advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        this.advancedLdapConnector = Mockito.mock(AdvancedLdapConnector.class);
        this.argumentCaptorAdvancedLdapPluginConfiguration = ArgumentCaptor.forClass(AdvancedLdapPluginConfiguration.class);
        this.argumentCaptorDnString = ArgumentCaptor.forClass(String.class);
        this.argumentCaptorPwdString = ArgumentCaptor.forClass(String.class);

        Collection<Attribute> attributes = new ArrayList<Attribute>();
        this.searchResultEntry = new SearchResultEntry("cn=dkoudela, ou=group, ou=example, dc=com", attributes);
    }

    @Test
    public void testHandlePagedSearchResultOneEntry() {
        this.advancedLdapBindBuilderDummy = new AdvancedLdapBindBuilderDummy(this.advancedLdapPluginConfiguration, "Password");
        this.advancedLdapBindBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        Mockito.when(this.advancedLdapConnector.bindDn(this.argumentCaptorAdvancedLdapPluginConfiguration.capture(),
                this.argumentCaptorDnString.capture(), this.argumentCaptorPwdString.capture())).thenReturn(true);

        this.advancedLdapBindBuilderDummy.handlePagedSearchResult(this.searchResultEntry);

        List<AdvancedLdapBind> AdvancedLdapBinds = this.advancedLdapBindBuilderDummy.getBinds();
        assertEquals(1, AdvancedLdapBinds.size());
        assertEquals("cn=dkoudela, ou=group, ou=example, dc=com", AdvancedLdapBinds.get(0).getDn());
        assertEquals(true, AdvancedLdapBinds.get(0).getResult());
    }

    @Test
    public void testHandlePagedSearchResultTwoEntries() {
        this.advancedLdapBindBuilderDummy = new AdvancedLdapBindBuilderDummy(this.advancedLdapPluginConfiguration, "Password");
        this.advancedLdapBindBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        Mockito.when(this.advancedLdapConnector.bindDn(this.argumentCaptorAdvancedLdapPluginConfiguration.capture(),
                this.argumentCaptorDnString.capture(), this.argumentCaptorPwdString.capture())).thenReturn(true);

        this.advancedLdapBindBuilderDummy.handlePagedSearchResult(this.searchResultEntry);
        this.advancedLdapBindBuilderDummy.handlePagedSearchResult(this.searchResultEntry);

        List<AdvancedLdapBind> AdvancedLdapBinds = this.advancedLdapBindBuilderDummy.getBinds();
        assertEquals(2, AdvancedLdapBinds.size());
        assertEquals("cn=dkoudela, ou=group, ou=example, dc=com", AdvancedLdapBinds.get(0).getDn());
        assertEquals(true, AdvancedLdapBinds.get(0).getResult());
        assertEquals("cn=dkoudela, ou=group, ou=example, dc=com", AdvancedLdapBinds.get(1).getDn());
        assertEquals(true, AdvancedLdapBinds.get(1).getResult());
    }
}
