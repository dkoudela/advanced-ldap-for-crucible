package ut.com.davidkoudela.crucible.timer;

import com.davidkoudela.crucible.timer.AdvancedLdapTimerTrigger;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.TimerTask;

/**
 * Description: Testing {@link AdvancedLdapTimerTrigger}
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2015-06-04
 */
public class AdvancedLdapTimerTriggerTest extends TestCase {
    public class AdvancedLdapTestTask extends TimerTask {
        @Override
        public void run() {}
    }

    @Test
    public void testRunNow() throws InterruptedException {
        AdvancedLdapTestTask advancedLdapTestTask = new AdvancedLdapTestTask();
        AdvancedLdapTestTask spy = Mockito.spy(advancedLdapTestTask);
        Mockito.doNothing().when(spy).run();

        AdvancedLdapTimerTrigger advancedLdapTimerTrigger = new AdvancedLdapTimerTrigger();
        advancedLdapTimerTrigger.runNow(spy);
        Thread.sleep(1000);
        Mockito.verify(spy).run();
    }

    @Test
    public void testCreateAndDeleteTimer() {
        AdvancedLdapTimerTrigger advancedLdapTimerTrigger = new AdvancedLdapTimerTrigger();
        AdvancedLdapTestTask advancedLdapTestTask = new AdvancedLdapTestTask();

        int index = advancedLdapTimerTrigger.createTimer(advancedLdapTestTask, 3600);
        assertEquals(1, advancedLdapTimerTrigger.activeTimers());
        advancedLdapTimerTrigger.deleteTimer(index);
        assertEquals(0, advancedLdapTimerTrigger.activeTimers());
    }
}
