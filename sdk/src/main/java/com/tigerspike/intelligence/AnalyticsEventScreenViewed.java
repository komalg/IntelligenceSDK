/*
 * Created by markvanrees on 14/10/2015.
 * Copyright 2015 Tigerspike. All rights reserved.
 */

package com.tigerspike.intelligence;

/**
 * AnalyticsEventScreenViewed
 */
public class AnalyticsEventScreenViewed extends AnalyticsEvent {


    public AnalyticsEventScreenViewed(String screenName, Double timeViewed) {
        super(Constants.APPLICATION_SCREEN_VIEWED_EVENT, String.valueOf(timeViewed));
        setTargetID(screenName);
    }
}