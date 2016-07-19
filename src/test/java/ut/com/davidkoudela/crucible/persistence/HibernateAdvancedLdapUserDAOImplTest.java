package ut.com.davidkoudela.crucible.persistence;

import com.atlassian.fecru.user.FecruUserDAO;
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

        public void setUserDAO(FecruUserDAO userDAO) {
            super.setUserDAO(userDAO);
        }
    }

    @Test
    public void testCreate() {
        UserManager userManager = Mockito.mock(DefaultUserManager.class);
        FecruUserDAO userDAO = Mockito.mock(FecruUserDAO.class);
        ArgumentCaptor<String> argumentCaptorUID = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> argumentCaptorBool = ArgumentCaptor.forClass(Boolean.class);
        HibernateAdvancedLdapUserDAOImplDummy hibernateAdvancedLdapUserDAO = new HibernateAdvancedLdapUserDAOImplDummy(userManager);
        hibernateAdvancedLdapUserDAO.setUserDAO(userDAO);
        AdvancedLdapPerson advancedLdapPerson = new AdvancedLdapPerson();
        advancedLdapPerson.setUid("dkoudela");
        advancedLdapPerson.setEmail("dkoudela@example.com");
        advancedLdapPerson.setDisplayName("David Koudela");
        Mockito.doNothing().when(userDAO).create(ArgumentCaptor.forClass(com.atlassian.fecru.user.FecruUser.class).capture());

        hibernateAdvancedLdapUserDAO.create(advancedLdapPerson.getUid(), advancedLdapPerson.getDisplayName(), advancedLdapPerson.getEmail());

        Mockito.verify(userDAO, Mockito.times(1)).create(ArgumentCaptor.forClass(com.atlassian.fecru.user.FecruUser.class).capture());
    }
}
