package com.tigerspike.intelligence;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;

@SuppressLint("Registered")
public class GeofenceTransitionsIntentService extends IntentService
{

    protected static final String TAG = "geofence.transitions.service";

    public static ArrayList<GeoManager> mGeoManagers = new ArrayList<>();

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService()
    {
        super(TAG);
    }

    /**
     * Handles incoming intents.
     *
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {

            switch (geofencingEvent.getErrorCode())
            {
                case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                    Log.d("Intelligence", "Geofence not available");
                case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                    Log.d("Intelligence", "Geofence too many Geofences");
                case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    Log.d("Intelligence", "Geofence too many pending intents");
                default:
                    Log.d("Intelligence", "Unknown error");
            }

            return;

        }

        for (GeoManager geoManager : mGeoManagers) {
            geoManager.onTriggeringGeofences(geofencingEvent.getTriggeringGeofences(), geofencingEvent.getGeofenceTransition());
        }

    }

    /**
     * Adds GeoManager instance to List of GeoManagers
     *
     * @param geoManager - GeoManager instance that will be invoked if any GeofencingEvent approaches
     *
     * */
    public static void addGeoManager(GeoManager geoManager) {
        if (!mGeoManagers.contains(geoManager)) {
            mGeoManagers.add(geoManager);
        }
    }

    /**
     * Removes GeoManager instance from list of GeoManagers
     *
     * @param geoManager - GeoManager instance to be removed
     *
     * */
    public static void removeGeoManager(GeoManager geoManager) {
        mGeoManagers.remove(geoManager);
    }

    /**
     * Removed all GeoManagers
     * */
    public static void removeAllGeoManagers() {
        mGeoManagers.clear();
    }

}
