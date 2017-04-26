package com.tigerspike.intelligence;

/**
 * Created by sandeep.vissamsetti on 5/27/16.
 */
public class AnalyticsNewUserCreated extends AnalyticsEvent {
    public AnalyticsNewUserCreated(String userId) {
        super(Constants.APPLICATION_USER_CREATED_EVENT);
        setTargetID(userId);
    }
}
