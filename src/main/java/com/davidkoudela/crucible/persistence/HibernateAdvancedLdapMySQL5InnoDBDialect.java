package com.davidkoudela.crucible.persistence;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * Description: {@link HibernateAdvancedLdapMySQL5InnoDBDialect} represents custom Hibernate Dialect for MySQL5 InnoDB.
 *              It registers some additional Sql Types to be mapped correctly from objects to database structures.
 * Copyright (C) 2016 David Koudela
 *
 * @author dkoudela
 * @since 2015-07-19
 */

public class HibernateAdvancedLdapMySQL5InnoDBDialect extends MySQL5InnoDBDialect {
    public HibernateAdvancedLdapMySQL5InnoDBDialect() {
        /**
         *  It is the same like described in {@link HibernateAdvancedLdapOracle11gDialect}
         */
        this.registerColumnType(16, "bit");
    }
}
