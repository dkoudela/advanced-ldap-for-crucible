package ut.com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapGroupBuilder;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapPerson;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapPersonBuilder;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.SearchRequest;
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
 * Description: Testing {@link AdvancedLdapPersonBuilder}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-09
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapPersonBuilderTest extends TestCase {
    private static AdvancedLdapPersonBuilderDummy advancedLdapPersonBuilderDummy;
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private static AdvancedLdapConnector advancedLdapConnector;
    private static ArgumentCaptor<AdvancedLdapPluginConfiguration> argumentCaptorAdvancedLdapPluginConfiguration;
    private static ArgumentCaptor<SearchRequest> argumentCaptorSearchRequest;
    private static ArgumentCaptor<AdvancedLdapGroupBuilder> argumentCaptorAdvancedLdapGroupBuilder;
    private static SearchResultEntry searchResultEntry;

    public class AdvancedLdapPersonBuilderDummy extends AdvancedLdapPersonBuilder {
        public AdvancedLdapPersonBuilderDummy(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration, Boolean followMembers) {
            super(advancedLdapPluginConfiguration, followMembers);
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
        this.argumentCaptorSearchRequest = ArgumentCaptor.forClass(SearchRequest.class);
        this.argumentCaptorAdvancedLdapGroupBuilder = ArgumentCaptor.forClass(AdvancedLdapGroupBuilder.class);

        this.advancedLdapPluginConfiguration.setUIDAttributeKey("sAMAccountName");
        this.advancedLdapPluginConfiguration.setDisplayNameAttributeKey("displayName");
        this.advancedLdapPluginConfiguration.setEmailAttributeKey("email");

        Collection<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getUIDAttributeKey(), "dkoudela"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getDisplayNameAttributeKey(), "David Koudela"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getEmailAttributeKey(), "dkoudela@example.com"));
        this.searchResultEntry = new SearchResultEntry("cn=dkoudela, ou=group, ou=example, dc=com", attributes);
    }

    @Test
    public void testHandlePagedSearchResultOneEntryDoNotFollowMembers() {
        this.advancedLdapPersonBuilderDummy = new AdvancedLdapPersonBuilderDummy(this.advancedLdapPluginConfiguration, false);
        this.advancedLdapPersonBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorAdvancedLdapPluginConfiguration.capture(),
                this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapGroupBuilder.capture());


        this.advancedLdapPersonBuilderDummy.handlePagedSearchResult(this.searchResultEntry);

        List<AdvancedLdapPerson> advancedLdapPersons = this.advancedLdapPersonBuilderDummy.getPersons();
        assertEquals(1, advancedLdapPersons.size());
        assertEquals("dkoudela", advancedLdapPersons.get(0).getUid());
        assertEquals("David Koudela", advancedLdapPersons.get(0).getDisplayName());
        assertEquals("dkoudela@example.com", advancedLdapPersons.get(0).getEmail());
    }
}
