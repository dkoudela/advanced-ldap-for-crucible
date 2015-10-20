package ut.com.davidkoudela.crucible.persistence;

import com.davidkoudela.crucible.persistence.HibernateSessionFactoryFactory;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Description: Testing {@link HibernateSessionFactoryFactory}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-22
 */
@RunWith(MockitoJUnitRunner.class)
public class HibernateSessionFactoryFactoryTest extends TestCase {
    @Test
    public void testCreateHibernateSessionFactory() throws Exception {
        HibernateSessionFactoryFactory.createHibernateSessionFactory();
    }

}
