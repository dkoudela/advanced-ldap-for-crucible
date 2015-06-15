package ut.com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapGroupBuilder;
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

/**
 * Description: Testing {@link AdvancedLdapGroupBuilder}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-10
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapGroupBuilderTest extends TestCase {
    private static AdvancedLdapGroupBuilderDummy advancedLdapGroupBuilderDummy;
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private static AdvancedLdapConnector advancedLdapConnector;
    private static AdvancedLdapPersonBuilder advancedLdapPersonBuilder;
    private static ArgumentCaptor<AdvancedLdapPluginConfiguration> argumentCaptorAdvancedLdapPluginConfiguration;
    private static ArgumentCaptor<SearchRequest> argumentCaptorSearchRequest;
    private static ArgumentCaptor<AdvancedLdapPersonBuilder> argumentCaptorAdvancedLdapPersonBuilder;
    private static SearchResultEntry searchResultEntry;
    private static SearchResultEntry searchResultEntry2;

    public class AdvancedLdapGroupBuilderDummy extends AdvancedLdapGroupBuilder {
        public AdvancedLdapGroupBuilderDummy(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration, Boolean followMembers) {
            super(advancedLdapPluginConfiguration, followMembers);
        }

        public void setAdvancedLdapConnector(AdvancedLdapConnector advancedLdapConnector) {
            super.setAdvancedLdapConnector(advancedLdapConnector);
        }

        public void setAdvancedLdapPersonBuilder(AdvancedLdapPersonBuilder advancedLdapPersonBuilder) {
            super.setAdvancedLdapPersonBuilder(advancedLdapPersonBuilder);
        }
    }

    @Before
    public void init() {
        this.advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        this.advancedLdapConnector = Mockito.mock(AdvancedLdapConnector.class);
        this.advancedLdapPersonBuilder = Mockito.mock(AdvancedLdapPersonBuilder.class);
        this.argumentCaptorAdvancedLdapPluginConfiguration = ArgumentCaptor.forClass(AdvancedLdapPluginConfiguration.class);
        this.argumentCaptorSearchRequest = ArgumentCaptor.forClass(SearchRequest.class);
        this.argumentCaptorAdvancedLdapPersonBuilder = ArgumentCaptor.forClass(AdvancedLdapPersonBuilder.class);

        this.advancedLdapPluginConfiguration.setUIDAttributeKey("sAMAccountName");
        this.advancedLdapPluginConfiguration.setDisplayNameAttributeKey("displayName");
        this.advancedLdapPluginConfiguration.setEmailAttributeKey("email");
        this.advancedLdapPluginConfiguration.setUserGroupNamesKey("memberOf");
        this.advancedLdapPluginConfiguration.setGroupFilterKey("(&(objectCategory=cn=Group*)(sAMAccountName=*))");

        Collection<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getUIDAttributeKey(), "dkoudela"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getDisplayNameAttributeKey(), "David Koudela"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getEmailAttributeKey(), "dkoudela@example.com"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getUserGroupNamesKey(), "ou=group, ou=example, dc=com", "ou=product, ou=example, dc=com"));
        this.searchResultEntry = new SearchResultEntry("cn=dkoudela, ou=group, ou=example, dc=com", attributes);

        attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getUIDAttributeKey(), "jdoe"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getDisplayNameAttributeKey(), "John Doe"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getEmailAttributeKey(), "jdoe@example.com"));
        this.searchResultEntry2 = new SearchResultEntry("cn=jdoe, ou=group, ou=example, dc=com", attributes);
    }

    @Test
    public void testHandlePagedSearchResultOneEntryDoNotFollowMembers() {
    }

    @Test
    public void testHandlePagedSearchResultTwoEntryDoNotFollowMembers() {
    }

    @Test
    public void testHandlePagedSearchResultOneEntryFollowTwoMembersWithOneMemberEntryInEachMember() {
    }

    @Test
    public void testHandlePagedSearchResultOneEntryFollowTwoMembersWithTwoMemberEntryInEachMember() {
    }
}
