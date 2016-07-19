package com.davidkoudela.crucible.persistence;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.impl.ImmutableUser;
import com.atlassian.fecru.user.FecruUser;
import com.atlassian.fecru.user.FecruUserDAO;
import com.atlassian.fecru.user.FecruUserDAOImpl;
import com.atlassian.fecru.user.crowd.FecruCrowdDirectoryService;
import com.cenqua.crucible.hibernate.HibernateUtilCurrentSessionProvider;
import com.cenqua.fisheye.user.UserManager;
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
    private FecruUserDAO userDAO;

    @org.springframework.beans.factory.annotation.Autowired
    public HibernateAdvancedLdapUserDAOImpl(UserManager userManager) {
        this.userManager = userManager;
        // FecruCrowdDirectoryService
    }

    @Override
    public void create(String uid, String displayName, String email) {
        FecruUser fecruUser = new FecruUser(uid);
        User user = new ImmutableUser(1, uid, displayName, email, true);
        fecruUser.setBackingCrowdUser(user);
        FecruUserDAO userDAO = getUserDAO();
        userDAO.create(fecruUser);
    }

    protected FecruUserDAO getUserDAO() {
        if (null != this.userDAO)
            return this.userDAO;
        FecruUserDAOImpl userDAOImpl = new FecruUserDAOImpl();
        userDAOImpl.setCurrentSessionProvider(new HibernateUtilCurrentSessionProvider());
        return userDAOImpl;
    }

    protected void setUserDAO(FecruUserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
