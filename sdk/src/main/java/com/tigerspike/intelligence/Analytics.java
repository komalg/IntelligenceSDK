package com.tigerspike.intelligence;

public interface Analytics {

    void trackEvent(AnalyticsEvent analyticsEvent);
    void trackScreenViewed(String screenName, Double timeViewed);

    void setLastKnownLocation(android.location.Location location);
}
