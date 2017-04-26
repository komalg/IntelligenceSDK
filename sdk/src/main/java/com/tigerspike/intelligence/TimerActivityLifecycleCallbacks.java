package com.tigerspike.intelligence;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

class TimerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private static final long SESSION_TIMEOUT_MILLIS = 1000 * 60 * 5; // 5 minutes
    private static final String SHARED_PREFERENCES_LAST_PAUSE_TIME = "SHARED_PREFERENCES_LAST_PAUSE_TIME";
    private static final String SHARED_PREFERENCES_LAST_RESUME_TIME = "SHARED_PREFERENCES_LAST_RESUME_TIME";
    private static final String SHARED_PREFERENCES_SESSION_TIME = "SHARED_PREFERENCES_SESSION_TIME";

    private final Analytics mAnalytics;
    private final DataStore mDataStore;
    private final long mSessionTimeout;

    /**
     * Default constructor
     *
     * @param analytics An analytics module interface which will be used to track events.
     * @param dataStore A data store to keep resume and pause times.
     */
    TimerActivityLifecycleCallbacks(@NonNull Analytics analytics, @NonNull DataStore dataStore, long sessionTimeout) {
        mAnalytics = analytics;
        mDataStore = dataStore;
        mSessionTimeout = sessionTimeout;

        // This way we ensure that when Intelligence is bootstrapped, there will be a on Activity resumed
        // before the paused, thus we avoid having the previous session extended because of the pause method.
        onActivityResumed(null);
    }

    /**
     * Default constructor
     *
     * @param analytics An analytics module interface which will be used to track events.
     * @param dataStore A data store to keep resume and pause times.
     */
    TimerActivityLifecycleCallbacks(@NonNull Analytics analytics, @NonNull DataStore dataStore) {
        this(analytics, dataStore, SESSION_TIMEOUT_MILLIS);
    }

    /**
     * If the time since the last activity paused is bigger than timeout millis, we track the time
     * If not, the timer continues clocking.
     *
     * @param activity the activity that has been resumed
     */
    @Override
    public void onActivityResumed(Activity activity) {
        if (getTimeSinceLastPause() > mSessionTimeout) {
            trackAppTime();
        } else {
            updateResumeTime();
        }
    }

    /**
     * Stores the pause time overriding whatever we had before.
     *
     * @param activity the activity that has been paused
     */
    @Override
    public void onActivityPaused(Activity activity) {
        storePauseTimeAndSessionTime();
    }

    /**
     * @return the time elapsed since the last pause.
     */
    private long getTimeSinceLastPause() {
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis - mDataStore.getLong(SHARED_PREFERENCES_LAST_PAUSE_TIME, currentTimeMillis);
    }

    /**
     * Stores the current time in millis as the pause time and updates the session time.
     */
    private void storePauseTimeAndSessionTime() {
        long currentTimeMillis = System.currentTimeMillis();

        long sessionTimeSoFar = currentTimeMillis - mDataStore.getLong(SHARED_PREFERENCES_LAST_RESUME_TIME, currentTimeMillis);
        sessionTimeSoFar += mDataStore.getLong(SHARED_PREFERENCES_SESSION_TIME, 0l);

        mDataStore.set(SHARED_PREFERENCES_LAST_PAUSE_TIME, currentTimeMillis);
        mDataStore.set(SHARED_PREFERENCES_SESSION_TIME, sessionTimeSoFar);
    }

    /**
     * Tracks via the analytics module the time that the session has been on. Sets pause time to empty.
     * Sets the resume time to now.
     */
    private void resetTimes() {
        mDataStore.set(SHARED_PREFERENCES_LAST_RESUME_TIME, System.currentTimeMillis());
        mDataStore.remove(SHARED_PREFERENCES_LAST_PAUSE_TIME);
        mDataStore.remove(SHARED_PREFERENCES_SESSION_TIME);
    }

    /**
     * Tracks the app time.
     */
    private void trackAppTime() {
        long sessionTime = mDataStore.getLong(SHARED_PREFERENCES_SESSION_TIME, 0l);
        double sessionTimeInSeconds = ((double) sessionTime) / 1000.0;

        if (sessionTimeInSeconds > 0) {
            mAnalytics.trackEvent(new AnalyticsEvent("Phoenix.Analytics.Application.Time", String.valueOf(sessionTimeInSeconds)));
        }

        resetTimes();
    }

    /**
     * Updates the resume time to now.
     */
    private void updateResumeTime() {
        mDataStore.set(SHARED_PREFERENCES_LAST_RESUME_TIME, System.currentTimeMillis());
    }

    // Unneeded interface methods.

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
