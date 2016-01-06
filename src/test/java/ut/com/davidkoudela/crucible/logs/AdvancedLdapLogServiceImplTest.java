package ut.com.davidkoudela.crucible.logs;

import com.davidkoudela.crucible.logs.AdvancedLdapLogServiceImpl;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Test;

import org.apache.log4j.Level;

/**
 * Description: Testing {@link AdvancedLdapLogServiceImpl}
 * Copyright (C) 2016 David Koudela
 *
 * @author dkoudela
 * @since 2016-01-06
 */
public class AdvancedLdapLogServiceImplTest extends TestCase {
    @Test
    public void testToggleLevel() {
        Logger log = Logger.getLogger("com.davidkoudela.crucible");
        AdvancedLdapLogServiceImpl advancedLdapLogService = new AdvancedLdapLogServiceImpl();
        if (Level.INFO == log.getLevel()) {
            advancedLdapLogService.setLogLevel(Level.DEBUG);
            assertEquals(Level.DEBUG.toString(), log.getLevel().toString());
        } else {
            advancedLdapLogService.setLogLevel(Level.INFO);
            assertEquals(Level.INFO.toString(), log.getLevel().toString());
        }
    }
}
