package ut.com.davidkoudela.crucible.persistence;

import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapOracle11gDialect;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Description: Testing {@link HibernateAdvancedLdapOracle11gDialect}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-11-02
 */
public class HibernateAdvancedLdapOracle11gDialectTest extends TestCase {
    @Test
    public void testHibernateAdvancedLdapOracle11gDialect() {
        HibernateAdvancedLdapOracle11gDialect hibernateAdvancedLdapOracle11gDialect = new HibernateAdvancedLdapOracle11gDialect();
        assertNotNull(hibernateAdvancedLdapOracle11gDialect);
    }
}
