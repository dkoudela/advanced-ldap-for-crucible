package ut.com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.config.AdvancedLdapPluginConfiguration;
import com.davidkoudela.crucible.ldap.connect.AdvancedLdapConnector;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapGroup;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapGroupBuilder;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapNestedGroupBuilder;
import com.unboundid.ldap.sdk.Attribute;
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
        this.advancedLdapConnector = Mockito.mock(AdvancedLdapConnector.class);
        this.advancedLdapGroupBuilder = Mockito.mock(AdvancedLdapGroupBuilder.class);

        this.advancedLdapPluginConfiguration.setGIDAttributeKey("sAMAccountName");
        this.advancedLdapPluginConfiguration.setGroupDisplayNameKey("name");
        this.advancedLdapPluginConfiguration.setUserGroupNamesKey("memberOf");
        this.advancedLdapPluginConfiguration.setUserFilterKey("(&(objectCategory=cn=Person*)(sAMAccountName=${USERNAME}))");

        Collection<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getGIDAttributeKey(), "group"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getGroupDisplayNameKey(), "Default Group"));
        attributes.add(new Attribute(this.advancedLdapPluginConfiguration.getUserNamesKey(), "cn=dkoudela, ou=group, ou=example, dc=com", "cn=dkoudela, ou=product, ou=example, dc=com"));
        this.searchResultEntry = new SearchResultEntry("ou=group, ou=example, dc=com", attributes);

    }

    @Test
    public void testHandlePagedSearchResult() {
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
}
