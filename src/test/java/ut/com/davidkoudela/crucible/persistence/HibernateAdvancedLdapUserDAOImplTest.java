package ut.com.davidkoudela.crucible.persistence;

import com.atlassian.fecru.user.UserDAO;
import com.cenqua.fisheye.user.DefaultUserManager;
import com.cenqua.fisheye.user.UserManager;
import com.davidkoudela.crucible.persistence.HibernateAdvancedLdapUserDAOImpl;
import com.davidkoudela.crucible.ldap.model.AdvancedLdapPerson;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Description: Testing {@link HibernateAdvancedLdapUserDAOImpl}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-21
 */
@RunWith(MockitoJUnitRunner.class)
public class HibernateAdvancedLdapUserDAOImplTest extends TestCase {
    public class HibernateAdvancedLdapUserDAOImplDummy extends HibernateAdvancedLdapUserDAOImpl {
        public HibernateAdvancedLdapUserDAOImplDummy(UserManager userManager) {
            super(userManager);
        }

        public void setUserDAO(UserDAO userDAO) {
            super.setUserDAO(userDAO);
        }
    }

    @Test
    public void testCreate() {
        UserManager userManager = Mockito.mock(DefaultUserManager.class);
        UserDAO userDAO = Mockito.mock(UserDAO.class);
        ArgumentCaptor<String> argumentCaptorUID = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> argumentCaptorBool = ArgumentCaptor.forClass(Boolean.class);
        Mockito.doNothing().when(userManager).setCrucibleEnabled(argumentCaptorUID.capture(), argumentCaptorBool.capture());
        HibernateAdvancedLdapUserDAOImplDummy hibernateAdvancedLdapUserDAO = new HibernateAdvancedLdapUserDAOImplDummy(userManager);
        hibernateAdvancedLdapUserDAO.setUserDAO(userDAO);
        AdvancedLdapPerson advancedLdapPerson = new AdvancedLdapPerson();
        advancedLdapPerson.setUid("dkoudela");
        advancedLdapPerson.setEmail("dkoudela@example.com");
        advancedLdapPerson.setDisplayName("David Koudela");
        Mockito.doNothing().when(userDAO).create(ArgumentCaptor.forClass(com.atlassian.fecru.user.User.class).capture());

        hibernateAdvancedLdapUserDAO.create(advancedLdapPerson.getUid(), advancedLdapPerson.getDisplayName(), advancedLdapPerson.getEmail());

        Mockito.verify(userManager, Mockito.times(1)).setCrucibleEnabled("dkoudela", true);
        Mockito.verify(userDAO, Mockito.times(1)).create(ArgumentCaptor.forClass(com.atlassian.fecru.user.User.class).capture());
    }
}
