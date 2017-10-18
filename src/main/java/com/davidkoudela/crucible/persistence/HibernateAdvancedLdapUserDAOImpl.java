package com.davidkoudela.crucible.persistence;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.impl.ImmutableUser;
import com.atlassian.fecru.user.FecruUser;
import com.atlassian.fecru.user.FecruUserDAO;
import com.atlassian.fecru.user.FecruUserDAOImpl;
import com.atlassian.fecru.user.crowd.FecruCrowdDirectoryService;
import com.cenqua.crucible.hibernate.HibernateUtilCurrentSessionProvider;
import com.cenqua.fisheye.user.UserManager;
import org.springframework.stereotype.Component;

import java.util.List;

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
    private Long directoryId = -1L;

    @org.springframework.beans.factory.annotation.Autowired
    public HibernateAdvancedLdapUserDAOImpl(UserManager userManager/*, FecruCrowdDirectoryService crowdDirectoryService*/) {
        this.userManager = userManager;
        /*List<Directory> directories = crowdDirectoryService.findAllDirectories();
        for (Directory directory : directories) {
            if (DirectoryType.INTERNAL == directory.getType()) {
                directoryId = directory.getId();
                break;
            }
        }*/
    }

    @Override
    public void create(String uid, String displayName, String email) {
        FecruUser fecruUser = new FecruUser(uid);
        User user = new ImmutableUser(directoryId, uid, displayName, email, true);
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
