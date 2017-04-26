package com.tigerspike.intelligence;

import android.support.annotation.NonNull;

class AnalyticsEventEnterGeofence extends AnalyticsEvent {

    public AnalyticsEventEnterGeofence(@NonNull Integer geofenceID) {
        super(Constants.APPLICATION_GEOFENCE_ENTER_EVENT, String.valueOf(geofenceID.doubleValue()));
        setTargetID(geofenceID.toString());
    }

}
