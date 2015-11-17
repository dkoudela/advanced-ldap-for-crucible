package ut.com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapGroup;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapGroupBuilder;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapNestedGroupBuilder;
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

import java.util.*;

/**
 * Description: Testing {@link AdvancedLdapNestedGroupBuilder}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-11-17
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvancedLdapNestedGroupBuilderTest extends TestCase {
    private static AdvancedLdapNestedGroupBuilderDummy advancedLdapNestedGroupBuilderDummy;
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration;
    private static AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration2;
    private static AdvancedLdapConnector advancedLdapConnector;
    private static AdvancedLdapGroupBuilder advancedLdapGroupBuilder;
    private static SearchResultEntry searchResultEntry;

    public class AdvancedLdapNestedGroupBuilderDummy extends AdvancedLdapNestedGroupBuilder {
        public AdvancedLdapNestedGroupBuilderDummy(AdvancedLdapPluginConfiguration advancedLdapPluginConfiguration, Boolean followMembers) {
            super(advancedLdapPluginConfiguration, followMembers);
        }

        public void setAdvancedLdapConnector(AdvancedLdapConnector advancedLdapConnector) {
            super.setAdvancedLdapConnector(advancedLdapConnector);
        }

        public void setAdvancedLdapGroupBuilder(AdvancedLdapGroupBuilder advancedLdapGroupBuilder) {
            super.setAdvancedLdapGroupBuilder(advancedLdapGroupBuilder);
        }
    }

    @Before
    public void init() {
        this.advancedLdapPluginConfiguration = new AdvancedLdapPluginConfiguration();
        this.advancedLdapPluginConfiguration2  = new AdvancedLdapPluginConfiguration();
        this.advancedLdapConnector = Mockito.mock(AdvancedLdapConnector.class);
        this.advancedLdapGroupBuilder = Mockito.mock(AdvancedLdapGroupBuilder.class);

        this.advancedLdapPluginConfiguration.setGIDAttributeKey("sAMAccountName");
        this.advancedLdapPluginConfiguration.setGroupDisplayNameKey("name");
        this.advancedLdapPluginConfiguration.setUserGroupNamesKey("memberOf");
        this.advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");
        this.advancedLdapPluginConfiguration.setGroupFilterKey("(&(objectCategory=cn=Group*)(sAMAccountName=${USERNAME}))");
        this.advancedLdapPluginConfiguration2.setGIDAttributeKey("sAMAccountName");
        this.advancedLdapPluginConfiguration2.setGroupDisplayNameKey("name");
        this.advancedLdapPluginConfiguration2.setUserGroupNamesKey("memberOf");
        this.advancedLdapPluginConfiguration2.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");
        this.advancedLdapPluginConfiguration2.setNestedGroupsEnabled(true);
        this.advancedLdapPluginConfiguration2.setGroupFilterKey("(&(objectCategory=cn=Group*)(sAMAccountName=${USERNAME}))");

        Collection<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getGIDAttributeKey(), "group"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getGroupDisplayNameKey(), "Default Group"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getUserNamesKey(), "cn=dkoudela, ou=group, ou=example, dc=com", "cn=dkoudela, ou=product, ou=example, dc=com"));
        this.searchResultEntry = new SearchResultEntry("ou=group, ou=example, dc=com", attributes);

    }

    @Test
    public void testHandlePagedSearchResultNoNestedGroupsEnabled() {
        this.advancedLdapNestedGroupBuilderDummy = new AdvancedLdapNestedGroupBuilderDummy(this.advancedLdapPluginConfiguration, false);
        this.advancedLdapNestedGroupBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        this.advancedLdapNestedGroupBuilderDummy.setAdvancedLdapGroupBuilder(this.advancedLdapGroupBuilder);
        this.advancedLdapPluginConfiguration.setNestedGroupsEnabled(false);
        List<AdvancedLdapGroup> advancedLdapGroupList = new ArrayList<AdvancedLdapGroup>();
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("Ldap Users");
        advancedLdapGroupList.add(advancedLdapGroup);
        Set<String> groupNames = new HashSet<String>();
        groupNames.add("Ldap Users");
        Mockito.doNothing().when(this.advancedLdapGroupBuilder).handlePagedSearchResult(ArgumentCaptor.forClass(SearchResultEntry.class).capture());
        Mockito.when(this.advancedLdapGroupBuilder.getGroups()).thenReturn(advancedLdapGroupList);
        Mockito.when(this.advancedLdapGroupBuilder.getGroupNames()).thenReturn(groupNames);

        this.advancedLdapNestedGroupBuilderDummy.handlePagedSearchResult(this.searchResultEntry);

        List<AdvancedLdapGroup> advancedLdapGroup2 = this.advancedLdapNestedGroupBuilderDummy.getGroups();
        assertEquals(1, advancedLdapGroup2.size());
        assertEquals("Ldap Users", advancedLdapGroup2.get(0).getGID());
    }

    @Test
    public void testHandlePagedSearchResultNestedGroupsEnabledNoNestedGroupsFound() {
        this.advancedLdapNestedGroupBuilderDummy = new AdvancedLdapNestedGroupBuilderDummy(this.advancedLdapPluginConfiguration2, false);
        this.advancedLdapNestedGroupBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        this.advancedLdapNestedGroupBuilderDummy.setAdvancedLdapGroupBuilder(this.advancedLdapGroupBuilder);
        this.advancedLdapPluginConfiguration.setNestedGroupsEnabled(false);
        List<AdvancedLdapGroup> advancedLdapGroupList = new ArrayList<AdvancedLdapGroup>();
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("Ldap Users");
        advancedLdapGroupList.add(advancedLdapGroup);
        Set<String> groupNames = new HashSet<String>();
        groupNames.add("Ldap Users");
        Set<String> nestedGroups = new HashSet<String>();
        Mockito.doNothing().when(this.advancedLdapGroupBuilder).handlePagedSearchResult(ArgumentCaptor.forClass(SearchResultEntry.class).capture());
        Mockito.when(this.advancedLdapGroupBuilder.getGroups()).thenReturn(advancedLdapGroupList);
        Mockito.when(this.advancedLdapGroupBuilder.getGroupNames()).thenReturn(groupNames);
        Mockito.when(this.advancedLdapGroupBuilder.getNonpersonDns()).thenReturn(nestedGroups);

        this.advancedLdapNestedGroupBuilderDummy.handlePagedSearchResult(this.searchResultEntry);

        List<AdvancedLdapGroup> advancedLdapGroup2 = this.advancedLdapNestedGroupBuilderDummy.getGroups();
        assertEquals(1, advancedLdapGroup2.size());
        assertEquals("Ldap Users", advancedLdapGroup2.get(0).getGID());
    }

    @Test
    public void testHandlePagedSearchResultNestedGroupsEnabledOneNestedGroupLevel1NotFound() {
        this.advancedLdapNestedGroupBuilderDummy = new AdvancedLdapNestedGroupBuilderDummy(this.advancedLdapPluginConfiguration2, false);
        this.advancedLdapNestedGroupBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        this.advancedLdapNestedGroupBuilderDummy.setAdvancedLdapGroupBuilder(this.advancedLdapGroupBuilder);
        this.advancedLdapPluginConfiguration.setNestedGroupsEnabled(false);
        List<AdvancedLdapGroup> advancedLdapGroupList = new ArrayList<AdvancedLdapGroup>();
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("Ldap Users");
        advancedLdapGroupList.add(advancedLdapGroup);
        Set<String> groupNames = new HashSet<String>();
        groupNames.add("Ldap Users");
        Set<String> nestedGroups = new HashSet<String>();
        nestedGroups.add("ou=group, ou=example, dc=com");
        Mockito.doNothing().when(this.advancedLdapGroupBuilder).handlePagedSearchResult(ArgumentCaptor.forClass(SearchResultEntry.class).capture());
        Mockito.when(this.advancedLdapGroupBuilder.getGroups())
                .thenReturn(advancedLdapGroupList)
                .thenReturn(new ArrayList<AdvancedLdapGroup>());
        Mockito.when(this.advancedLdapGroupBuilder.getGroupNames()).thenReturn(groupNames);
        Mockito.when(this.advancedLdapGroupBuilder.getNonpersonDns()).thenReturn(nestedGroups);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(ArgumentCaptor.forClass(SearchRequest.class).capture(),
                ArgumentCaptor.forClass(AdvancedLdapGroupBuilder.class).capture());

        this.advancedLdapNestedGroupBuilderDummy.handlePagedSearchResult(this.searchResultEntry);

        List<AdvancedLdapGroup> advancedLdapGroup2 = this.advancedLdapNestedGroupBuilderDummy.getGroups();
        assertEquals(1, advancedLdapGroup2.size());
        assertEquals("Ldap Users", advancedLdapGroup2.get(0).getGID());
    }

    @Test
    public void testHandlePagedSearchResultNestedGroupsEnabledOneNestedGroupLevel1FoundNoNestedGroupLevel2() {
        this.advancedLdapNestedGroupBuilderDummy = new AdvancedLdapNestedGroupBuilderDummy(this.advancedLdapPluginConfiguration2, false);
        this.advancedLdapNestedGroupBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        this.advancedLdapNestedGroupBuilderDummy.setAdvancedLdapGroupBuilder(this.advancedLdapGroupBuilder);
        this.advancedLdapPluginConfiguration.setNestedGroupsEnabled(false);
        List<AdvancedLdapGroup> advancedLdapGroupList = new ArrayList<AdvancedLdapGroup>();
        Mockito.doNothing().when(this.advancedLdapGroupBuilder).handlePagedSearchResult(ArgumentCaptor.forClass(SearchResultEntry.class).capture());

        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("Ldap Users");
        advancedLdapGroupList.add(advancedLdapGroup);
        List<AdvancedLdapGroup> advancedLdapGroupList2 = new ArrayList<AdvancedLdapGroup>();
        AdvancedLdapGroup advancedLdapGroup2 = new AdvancedLdapGroup();
        advancedLdapGroup2.setGID("group");
        advancedLdapGroupList2.add(advancedLdapGroup2);

        Set<String> groupNames = new HashSet<String>();
        groupNames.add("Ldap Users");
        Set<String> groupNames2 = new HashSet<String>();
        groupNames2.add("group");
        Set<String> nestedGroups = new HashSet<String>();
        nestedGroups.add("ou=group, ou=example, dc=com");

        Mockito.when(this.advancedLdapGroupBuilder.getGroups())
                .thenReturn(advancedLdapGroupList)
                .thenReturn(advancedLdapGroupList2);
        Mockito.when(this.advancedLdapGroupBuilder.getGroupNames())
                .thenReturn(groupNames)
                .thenReturn(groupNames2);
        Mockito.when(this.advancedLdapGroupBuilder.getNonpersonDns())
                .thenReturn(nestedGroups)
                .thenReturn(new HashSet<String>());
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(ArgumentCaptor.forClass(SearchRequest.class).capture(),
                ArgumentCaptor.forClass(AdvancedLdapGroupBuilder.class).capture());

        this.advancedLdapNestedGroupBuilderDummy.handlePagedSearchResult(this.searchResultEntry);

        List<AdvancedLdapGroup> advancedLdapGroup3 = this.advancedLdapNestedGroupBuilderDummy.getGroups();
        assertEquals(2, advancedLdapGroup3.size());
        assertEquals("Ldap Users", advancedLdapGroup3.get(0).getGID());
        assertEquals("group", advancedLdapGroup3.get(1).getGID());
    }

    @Test
    public void testHandlePagedSearchResultNestedGroupsEnabledOneNestedGroupLevel1FoundDuplicates() {
        this.advancedLdapNestedGroupBuilderDummy = new AdvancedLdapNestedGroupBuilderDummy(this.advancedLdapPluginConfiguration2, false);
        this.advancedLdapNestedGroupBuilderDummy.setAdvancedLdapConnector(this.advancedLdapConnector);
        this.advancedLdapNestedGroupBuilderDummy.setAdvancedLdapGroupBuilder(this.advancedLdapGroupBuilder);
        this.advancedLdapPluginConfiguration.setNestedGroupsEnabled(false);
        List<AdvancedLdapGroup> advancedLdapGroupList = new ArrayList<AdvancedLdapGroup>();
        Mockito.doNothing().when(this.advancedLdapGroupBuilder).handlePagedSearchResult(ArgumentCaptor.forClass(SearchResultEntry.class).capture());

        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("Ldap Users");
        advancedLdapGroupList.add(advancedLdapGroup);
        List<AdvancedLdapGroup> advancedLdapGroupList2 = new ArrayList<AdvancedLdapGroup>();
        AdvancedLdapGroup advancedLdapGroup2 = new AdvancedLdapGroup();
        advancedLdapGroup2.setGID("group");
        advancedLdapGroupList2.add(advancedLdapGroup2);
        List<AdvancedLdapGroup> advancedLdapGroupList3 = new ArrayList<AdvancedLdapGroup>();
        AdvancedLdapGroup advancedLdapGroup3 = new AdvancedLdapGroup();
        advancedLdapGroup3.setGID("product");
        advancedLdapGroupList3.add(advancedLdapGroup3);
        List<AdvancedLdapGroup> advancedLdapGroupList4 = new ArrayList<AdvancedLdapGroup>();
        AdvancedLdapGroup advancedLdapGroup4 = new AdvancedLdapGroup();
        advancedLdapGroup4.setGID("developers");
        advancedLdapGroupList4.add(advancedLdapGroup4);
        List<AdvancedLdapGroup> advancedLdapGroupList5 = new ArrayList<AdvancedLdapGroup>();
        AdvancedLdapGroup advancedLdapGroup5 = new AdvancedLdapGroup();
        advancedLdapGroup5.setGID("testers");
        advancedLdapGroupList5.add(advancedLdapGroup5);

        Set<String> groupNames = new HashSet<String>();
        groupNames.add("Ldap Users");
        Set<String> groupNames2 = new HashSet<String>();
        groupNames2.add("group");
        Set<String> groupNames3 = new HashSet<String>();
        groupNames3.add("developers");
        groupNames3.add("testers");
        groupNames3.add("group");

        Set<String> nestedGroups = new HashSet<String>();
        nestedGroups.add("ou=group, ou=example, dc=com");
        nestedGroups.add("ou=product, ou=example, dc=com");
        Set<String> nestedGroups2 = new HashSet<String>();
        nestedGroups2.add("ou=developers, ou=group, ou=example, dc=com");
        nestedGroups2.add("ou=testers, ou=group, ou=example, dc=com");
        nestedGroups2.add("ou=product, ou=example, dc=com");

        Mockito.when(this.advancedLdapGroupBuilder.getGroups())
                .thenReturn(advancedLdapGroupList)
                .thenReturn(advancedLdapGroupList2)
                .thenReturn(advancedLdapGroupList3)
                .thenReturn(advancedLdapGroupList4)
                .thenReturn(advancedLdapGroupList5);
        Mockito.when(this.advancedLdapGroupBuilder.getGroupNames())
                .thenReturn(groupNames)
                .thenReturn(groupNames2)
                .thenReturn(groupNames3);
        Mockito.when(this.advancedLdapGroupBuilder.getNonpersonDns())
                .thenReturn(nestedGroups)
                .thenReturn(new HashSet<String>())
                .thenReturn(nestedGroups2);
        Mockito.doNothing().when(this.advancedLdapConnector).ldapPagedSearch(ArgumentCaptor.forClass(SearchRequest.class).capture(),
                ArgumentCaptor.forClass(AdvancedLdapGroupBuilder.class).capture());

        this.advancedLdapNestedGroupBuilderDummy.handlePagedSearchResult(this.searchResultEntry);

        List<AdvancedLdapGroup> advancedLdapGroup333 = this.advancedLdapNestedGroupBuilderDummy.getGroups();
        assertEquals(5, advancedLdapGroup333.size());
        assertEquals("Ldap Users", advancedLdapGroup333.get(0).getGID());
        assertEquals("group", advancedLdapGroup333.get(1).getGID());
        assertEquals("product", advancedLdapGroup333.get(2).getGID());
        assertEquals("developers", advancedLdapGroup333.get(3).getGID());
        assertEquals("testers", advancedLdapGroup333.get(4).getGID());
    }
}
