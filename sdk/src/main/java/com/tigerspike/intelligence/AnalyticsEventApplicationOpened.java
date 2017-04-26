package com.tigerspike.intelligence;

class AnalyticsEventApplicationOpened extends AnalyticsEvent {

    public AnalyticsEventApplicationOpened(String applicationID) {
        super(Constants.APPLICATION_OPENED_EVENT, "0");
        setTargetID(applicationID);
    }

}
