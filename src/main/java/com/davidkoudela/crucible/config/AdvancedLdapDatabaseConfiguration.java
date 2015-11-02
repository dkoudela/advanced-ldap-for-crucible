package com.davidkoudela.crucible.config;

/**
 * Description: The database configuration class providing all necessary database parameters and their access methods.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-10-20
 */
public class AdvancedLdapDatabaseConfiguration {
    private String databaseName = "";
    private String userName = "";
    private String password = "";

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if (this.getClass() == obj.getClass()) {
            AdvancedLdapDatabaseConfiguration givenObj = (AdvancedLdapDatabaseConfiguration) obj;
            if (null != this.databaseName && null != givenObj.databaseName &&
                    null != this.userName && null != givenObj.userName &&
                    null != this.password && null != givenObj.password) {
                if (0 == this.databaseName.compareTo(givenObj.databaseName) &&
                        0 == this.userName.compareTo(givenObj.userName) &&
                        0 == this.password.compareTo(givenObj.password)) {
                    isEqual = true;
                }
            } else {
                // TODO: null comparison
            }

        }
        return isEqual;
    }
}
