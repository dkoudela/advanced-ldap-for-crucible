package ut.com.davidkoudela.crucible.ldap.model;

import com.davidkoudela.crucible.ldap.model.AdvancedLdapGroup;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Description: Testing {@link AdvancedLdapGroup}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-11-15
 */
public class AdvancedLdapGroupTest extends TestCase {
    @Test
    public void testGetNormalizedGIDBSpace() {
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("New Group with space symbol");
        assertEquals("New-Group-with-space-symbol", advancedLdapGroup.getNormalizedGID());
    }
    @Test
    public void testGetNormalizedGIDBExclamation() {
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("New Group with ! symbol");
        assertEquals("New-Group-with---symbol", advancedLdapGroup.getNormalizedGID());
    }
    @Test
    public void testGetNormalizedGIDLeftBracket() {
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("New Group with ( symbol");
        assertEquals("New-Group-with---symbol", advancedLdapGroup.getNormalizedGID());
    }
    @Test
    public void testGetNormalizedGIDRightBracket() {
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("New Group with ) symbol");
        assertEquals("New-Group-with---symbol", advancedLdapGroup.getNormalizedGID());
    }
    @Test
    public void testGetNormalizedGIDAt() {
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("New Group with @ symbol");
        assertEquals("New-Group-with-@-symbol", advancedLdapGroup.getNormalizedGID());
    }
    @Test
    public void testGetNormalizedGIDDash() {
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("New Group with - symbol");
        assertEquals("New-Group-with---symbol", advancedLdapGroup.getNormalizedGID());
    }
    @Test
    public void testGetNormalizedGIDSharp() {
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("New Group with # symbol");
        assertEquals("New-Group-with---symbol", advancedLdapGroup.getNormalizedGID());
    }
    @Test
    public void testGetNormalizedGIDDollar() {
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("New Group with $ symbol");
        assertEquals("New-Group-with---symbol", advancedLdapGroup.getNormalizedGID());
    }
    @Test
    public void testGetNormalizedGIDPercent() {
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("New Group with % symbol");
        assertEquals("New-Group-with---symbol", advancedLdapGroup.getNormalizedGID());
    }
    @Test
    public void testGetNormalizedGIDCaret() {
        AdvancedLdapGroup advancedLdapGroup = new AdvancedLdapGroup();
        advancedLdapGroup.setGID("New Group with ^ symbol");
        assertEquals("New-Group-with---symbol", advancedLdapGroup.getNormalizedGID());
    }
}
