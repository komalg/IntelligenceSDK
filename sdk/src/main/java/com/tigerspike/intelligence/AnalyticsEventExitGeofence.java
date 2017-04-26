package com.tigerspike.intelligence;

import android.support.annotation.NonNull;

class AnalyticsEventExitGeofence extends AnalyticsEvent {

    public AnalyticsEventExitGeofence(@NonNull Integer geofenceID) {
        super(Constants.APPLICATION_GEOFENCE_EXIT_EVENT, String.valueOf(geofenceID.doubleValue()));
        setTargetID(geofenceID.toString());
    }

}
