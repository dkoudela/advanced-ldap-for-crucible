package com.davidkoudela.crucible.config;

import com.atlassian.fecru.user.User;
import com.atlassian.fecru.user.UserDAO;
import com.atlassian.fecru.user.UserDAOImpl;
import com.cenqua.crucible.hibernate.HibernateUtilCurrentSessionProvider;
import com.cenqua.fisheye.user.UserManager;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapPerson;
import org.springframework.stereotype.Component;

/**
 * Description: Implementation of {@link HibernateAdvancedLdapUserDAO} representing the Data Access Object class
 *              for creating Crucible User instances.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-21
 */
@Component("advancedLdapUserDAO")
public class HibernateAdvancedLdapUserDAOImpl implements HibernateAdvancedLdapUserDAO {
    private UserManager userManager;
    private UserDAO userDAO;

    @org.springframework.beans.factory.annotation.Autowired
    public HibernateAdvancedLdapUserDAOImpl(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void create(AdvancedLdapPerson advancedLdapPerson) {
        String UID = advancedLdapPerson.getUid();
        com.atlassian.fecru.user.User  user = new User(UID);
        user.setDisplayName(advancedLdapPerson.getDisplayName());
        user.setEmail(advancedLdapPerson.getEmail());
        user.setAuthType(User.AuthType.LDAP);
        user.setFisheyeEnabled(true);
        UserDAO userDAO = getUserDAO();
        userDAO.create(user);
        this.userManager.setCrucibleEnabled(UID, true);
    }

    protected UserDAO getUserDAO() {
        if (null != this.userDAO)
            return this.userDAO;
        UserDAOImpl userDAOImpl = new UserDAOImpl();
        userDAOImpl.setCurrentSessionProvider(new HibernateUtilCurrentSessionProvider());
        return userDAOImpl;
    }

    protected void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
