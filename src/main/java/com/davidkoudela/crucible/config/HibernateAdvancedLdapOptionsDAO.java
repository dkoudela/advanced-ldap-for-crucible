package com.davidkoudela.crucible.config;

/**
 * Description: {@link HibernateAdvancedLdapOptionsDAO} represents an interface for Data Access Object class
 *              for {@link AdvancedLdapPluginConfiguration}.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-03-31
 */
public interface HibernateAdvancedLdapOptionsDAO {
    public abstract void store(AdvancedLdapOptions advancedLdapOptions, boolean isUpdate) throws Exception;
    public abstract AdvancedLdapOptions get();
    public abstract void remove(int id);
}
