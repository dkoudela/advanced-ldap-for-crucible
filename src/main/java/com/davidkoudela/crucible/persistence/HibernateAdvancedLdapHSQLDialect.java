package com.davidkoudela.crucible.persistence;

import org.hibernate.dialect.HSQLDialect;

/**
 * Description: {@link HibernateAdvancedLdapHSQLDialect} represents custom Hibernate Dialect for HSQL.
 *              It registers some additional Sql Types to be mapped correctly from objects to database structures.
 * Copyright (C) 2016 David Koudela
 *
 * @author dkoudela
 * @since 2015-07-19
 */

public class HibernateAdvancedLdapHSQLDialect extends HSQLDialect {
    public HibernateAdvancedLdapHSQLDialect() {
        /**
         *  It is the same like described in {@link HibernateAdvancedLdapOracle11gDialect}
         */
        this.registerColumnType(16, "bit");
    }
}
