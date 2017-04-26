package com.tigerspike.intelligence;

import junit.framework.TestCase;

import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by joseprodriguez on 14/10/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class TimerCallbacksTest extends TestCase {

    private TimerActivityLifecycleCallbacks mTimer;
    private Analytics mAnalytics;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mAnalytics = mock(Analytics.class);
        mTimer = new TimerActivityLifecycleCallbacks(mAnalytics, new MockDatastore(), 20);
    }

    @After
    public void tearDown() throws Exception {
        mAnalytics = null;
        mTimer = null;
        super.tearDown();
    }

    /**
     * Given that I have been outside the app for more than 5 minutes, and have some time
     * logged before that, then I trigger an event.
     */
    @Test public void analyticsIsTriggered() throws Exception {
        // Start the app
        mTimer.onActivityResumed(null);

        // Use the app for 50 msecs
        Thread.sleep(50);

        // Pause it
        mTimer.onActivityPaused(null);

        // On background for 50 msecs (above the 20 we set as session threshold)
        Thread.sleep(50);

        // Resume the app
        mTimer.onActivityResumed(null);

        // Verify that one event was called.
        verify(mAnalytics, times(1)).trackEvent(any(AnalyticsEvent.class));
    }

}
