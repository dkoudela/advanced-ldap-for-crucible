package com.davidkoudela.crucible.ldap.model;

/**
 * Description: {@link AdvancedLdapBind} contains all necessary LDAP bind parameters and their access methods.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-05-06
 */
public class AdvancedLdapBind {
    private String dn;
    private boolean result;

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
