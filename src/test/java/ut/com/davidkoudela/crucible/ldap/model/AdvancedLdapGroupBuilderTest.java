package ut.com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapGroup;
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
    private static SearchResultEntry searchResultEntry3;

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

        this.advancedLdapPluginConfiguration.setGIDAttributeKey("sAMAccountName");
        this.advancedLdapPluginConfiguration.setGroupDisplayNameKey("name");
        this.advancedLdapPluginConfiguration.setUserGroupNamesKey("memberOf");
        this.advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");

        Collection<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getGIDAttributeKey(), "group"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getGroupDisplayNameKey(), "Default Group"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getUserNamesKey(), "cn=dkoudela, ou=group, ou=example, dc=com", "cn=dkoudela, ou=product, ou=example, dc=com"));
        this.searchResultEntry = new SearchResultEntry("ou=group, ou=example, dc=com", attributes);

        attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getGIDAttributeKey(), "product"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getGroupDisplayNameKey(), "Product Group"));
        this.searchResultEntry2 = new SearchResultEntry("ou=product, ou=example, dc=com", attributes);

        attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getGIDAttributeKey(), "Wrong group!containing%strange#symbols+intentionally"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getGroupDisplayNameKey(), "Wrong Product Group"));
        this.searchResultEntry3 = new SearchResultEntry("ou=wrongProduct, ou=example, dc=com", attributes);
    }

    @Test
    public void testHandlePagedSearchResultOneEntryDoNotFollowMembers() {
        this.advancedLdapGroupBuilderDummy = new AdvancedLdapGroupBuilderDummy(this.advancedLdapPluginConfiguration, false);
        this.advancedLdapGroupBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorAdvancedLdapPluginConfiguration.capture(),
                this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());


        this.advancedLdapGroupBuilderDummy.handlePagedSearchResult(this.searchResultEntry);

        List<AdvancedLdapGroup> advancedLdapGroup = this.advancedLdapGroupBuilderDummy.getGroups();
        assertEquals(1, advancedLdapGroup.size());
        assertEquals("group", advancedLdapGroup.get(0).getGID());
        assertEquals("Default Group", advancedLdapGroup.get(0).getDisplayName());
    }

    @Test
    public void testHandlePagedSearchResultOneEntryDoNotFollowMembersGIDWithIncorrectFormat() {
        this.advancedLdapGroupBuilderDummy = new AdvancedLdapGroupBuilderDummy(this.advancedLdapPluginConfiguration, false);
        this.advancedLdapGroupBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorAdvancedLdapPluginConfiguration.capture(),
                this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());


        this.advancedLdapGroupBuilderDummy.handlePagedSearchResult(this.searchResultEntry3);

        List<AdvancedLdapGroup> advancedLdapGroup = this.advancedLdapGroupBuilderDummy.getGroups();
        assertEquals(1, advancedLdapGroup.size());
        assertEquals("Wrong group!containing%strange#symbols+intentionally", advancedLdapGroup.get(0).getGID());
        assertEquals("Wrong-group-containing-strange-symbols-intentionally", advancedLdapGroup.get(0).getNormalizedGID());
        assertEquals("Wrong Product Group", advancedLdapGroup.get(0).getDisplayName());

        advancedLdapGroup.get(0).setUserNames(new ArrayList<String>()); // for better test coverage
        advancedLdapGroup.get(0).getUserNames(); // for better test coverage
    }

    @Test
    public void testHandlePagedSearchResultTwoEntryDoNotFollowMembers() {
        this.advancedLdapGroupBuilderDummy = new AdvancedLdapGroupBuilderDummy(this.advancedLdapPluginConfiguration, false);
        this.advancedLdapGroupBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorAdvancedLdapPluginConfiguration.capture(),
                this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());


        this.advancedLdapGroupBuilderDummy.handlePagedSearchResult(this.searchResultEntry);
        this.advancedLdapGroupBuilderDummy.handlePagedSearchResult(this.searchResultEntry2);

        List<AdvancedLdapGroup> advancedLdapGroups = this.advancedLdapGroupBuilderDummy.getGroups();
        assertEquals(2, advancedLdapGroups.size());
        assertEquals("group", advancedLdapGroups.get(0).getGID());
        assertEquals("Default Group", advancedLdapGroups.get(0).getDisplayName());

        assertEquals("product", advancedLdapGroups.get(1).getGID());
        assertEquals("Product Group", advancedLdapGroups.get(1).getDisplayName());
    }

    @Test
    public void testHandlePagedSearchResultOneEntryFollowTwoMembersWithOneMemberEntryInEachMember() {
        this.advancedLdapGroupBuilderDummy = new AdvancedLdapGroupBuilderDummy(this.advancedLdapPluginConfiguration, true);
        this.advancedLdapGroupBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        this.advancedLdapGroupBuilderDummy.setAdvancedLdapPersonBuilder(this.advancedLdapPersonBuilder);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorAdvancedLdapPluginConfiguration.capture(),
                this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());
        List<AdvancedLdapPerson> personList = new ArrayList<AdvancedLdapPerson>();
        AdvancedLdapPerson advancedLdapPerson = new AdvancedLdapPerson();
        advancedLdapPerson.setUid("dkoudela");
        advancedLdapPerson.setEmail("dkoudela@example.com");
        advancedLdapPerson.setDisplayName("David Koudela");
        personList.add(advancedLdapPerson);
        Mockito.when(this.advancedLdapPersonBuilder.getPersons()).thenReturn(personList);

        this.advancedLdapGroupBuilderDummy.handlePagedSearchResult(this.searchResultEntry);

        List<AdvancedLdapGroup> advancedLdapGroups = this.advancedLdapGroupBuilderDummy.getGroups();
        assertEquals(1, advancedLdapGroups.size());
        assertEquals("group", advancedLdapGroups.get(0).getGID());
        assertEquals("Default Group", advancedLdapGroups.get(0).getDisplayName());

        assertEquals(2, advancedLdapGroups.get(0).getPersonList().size());
        assertEquals("dkoudela", advancedLdapGroups.get(0).getPersonList().get(0).getUid());
        assertEquals("dkoudela@example.com", advancedLdapGroups.get(0).getPersonList().get(0).getEmail());
        assertEquals("David Koudela", advancedLdapGroups.get(0).getPersonList().get(0).getDisplayName());
        assertEquals("dkoudela", advancedLdapGroups.get(0).getPersonList().get(1).getUid());
        assertEquals("dkoudela@example.com", advancedLdapGroups.get(0).getPersonList().get(1).getEmail());
        assertEquals("David Koudela", advancedLdapGroups.get(0).getPersonList().get(1).getDisplayName());
    }

    @Test
    public void testHandlePagedSearchResultOneEntryFollowTwoMembersWithTwoMemberEntryInEachMember() {
        this.advancedLdapGroupBuilderDummy = new AdvancedLdapGroupBuilderDummy(this.advancedLdapPluginConfiguration, true);
        this.advancedLdapGroupBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        this.advancedLdapGroupBuilderDummy.setAdvancedLdapPersonBuilder(this.advancedLdapPersonBuilder);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(this.argumentCaptorAdvancedLdapPluginConfiguration.capture(),
                this.argumentCaptorSearchRequest.capture(), this.argumentCaptorAdvancedLdapPersonBuilder.capture());
        List<AdvancedLdapPerson> personList = new ArrayList<AdvancedLdapPerson>();
        AdvancedLdapPerson advancedLdapPerson = new AdvancedLdapPerson();
        advancedLdapPerson.setUid("dkoudela");
        advancedLdapPerson.setEmail("dkoudela@example.com");
        advancedLdapPerson.setDisplayName("David Koudela");
        personList.add(advancedLdapPerson);
        advancedLdapPerson.setUid("jdoe");
        advancedLdapPerson.setEmail("jdoe@example.com");
        advancedLdapPerson.setDisplayName("John Doe");
        personList.add(advancedLdapPerson);
        Mockito.when(this.advancedLdapPersonBuilder.getPersons()).thenReturn(personList);

        this.advancedLdapGroupBuilderDummy.handlePagedSearchResult(this.searchResultEntry);

        List<AdvancedLdapGroup> advancedLdapGroups = this.advancedLdapGroupBuilderDummy.getGroups();
        assertEquals(1, advancedLdapGroups.size());
        assertEquals("group", advancedLdapGroups.get(0).getGID());
        assertEquals("Default Group", advancedLdapGroups.get(0).getDisplayName());

        assertEquals(0, advancedLdapGroups.get(0).getPersonList().size());
    }
}
