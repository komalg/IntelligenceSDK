package com.tigerspike.intelligence;

import android.support.annotation.Nullable;

import com.tigerspike.intelligence.exceptions.IntelligenceException;

import java.util.List;

/**
 * @Class Location
 *
 * Interface definition for managing Intelligence Location module.
 * Can be used to download Geofences.
 * Use related callbacks interface definition to make further data/error processing.
 *
 */
public interface Location
{
    /**
     * Sends GET Geofences request to retreive list of Geofences
     *
     * @param onGetGeofencesListener - a callback to be invoked after GET Geofences request is finished.
     * */
    void getGeofences(OnGetGeofencesListener onGetGeofencesListener);


    void getGeofences(OnGetGeofencesListener onGetGeofencesListener,
                      @Nullable Double longitude,
                      @Nullable Double latitude,
                      @Nullable Double radius,
                      @Nullable Integer page_size,
                      @Nullable Integer page_number);

    List<IntelligenceGeofence> getCachedGeofences();

    /**
     * Retrieves the last known location
     *
     * @return last known location or Null if not available.
     */
    @Nullable android.location.Location getLastKnownLocation();

    /**
     * Listener interface definition for a callback to be invoked when a geofences are downloaded from backend.
     * */
    interface OnGetGeofencesListener {
        void onGetGeofences(List<IntelligenceGeofence> geofences, IntelligenceException intelligenceException);
    }
}
