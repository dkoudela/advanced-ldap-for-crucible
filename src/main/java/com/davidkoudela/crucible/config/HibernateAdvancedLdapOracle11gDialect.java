package com.davidkoudela.crucible.config;

import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.type.ClobType;
import org.hibernate.type.StringType;
import org.hibernate.type.TextType;

/**
 * Description: {@link HibernateAdvancedLdapOracle11gDialect} represents custom Hibernate Dialect for Oracle11g.
 *              It registers some additional Sql Types to be mapped correctly from objects to database structures.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-09-03
 */
public class HibernateAdvancedLdapOracle11gDialect extends Oracle10gDialect {
    public HibernateAdvancedLdapOracle11gDialect() {
        String clobName = (new ClobType()).getName();

        registerColumnType((new StringType()).sqlType(), clobName);
        registerColumnType((new TextType()).sqlType(), clobName);

        /**
         *  Taken from com.cenqua.crucible.hibernate.dialects.OracleLargeStringsDialect
         *  SqlType -16 not found in FECRU and org.hibernate.type.*
         *  It is not pretty sure what type it represents; it is definitely some kind of long text value
         *
         *  Dear Atlassians, using magic numbers in the code is definitely a bad practice as it can lead
         *  to inconsistencies and misbehavior once the code is refactored.
         *  It has happened right now.
         *  I have to keep it as it is and I have crossed my fingers that the value -16 is not used for any
         *  non-string type...
         */
        registerColumnType(-16, clobName);
    }
}
