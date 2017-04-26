package com.tigerspike.intelligence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

final class GeoManager
{
    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;

    private Location mLastKnownLocation;

    private Listener mListener;

    private ArrayList<Geofence> mGeofenceList;
    private ArrayList<IntelligenceGeofence> mIntelligenceGeofences;

    /**
     * Public constructor - only Context is required
     * */
    public GeoManager(@NonNull Context context) {
        mContext = context;
        mIntelligenceGeofences = new ArrayList<>();
        mGeofenceList = new ArrayList<>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        GeofenceTransitionsIntentService.addGeoManager(this);

        mGoogleApiClient = buildGoogleApiClient();
        mGoogleApiClient.connect();

    }

    /**
     * Constructs GoogleApiClient by using its Builder
     * */
    private synchronized GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();

    }

    /**
     * Performs actions required by the Intelligence SDK to work correctly after GoogleApiClient is connected.
     * Sets LastKnownLocation and reloads Geofences to track if needed.
     * */
    private void onGoogleApiClientConnect() {

        // Set last known location
        setLastLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));

        // Create locaiton update request.
        com.google.android.gms.location.LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // Set update request.
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, mLocationListener);

        if (mIntelligenceGeofences.size() > 0) {
            reloadIntelligenceGeofencesList(mIntelligenceGeofences);
        }

    }

    /**
     * LocationListener instance to track LocationChanged Event
     * Updates stored LastKnownLocation
     * */
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            setLastLocation(location);
        }
    };

    /**
     * Sets LastKnownLocation from Location services
     * */
    private void setLastLocation(Location location) {
        mLastKnownLocation = location;
        if (mListener != null) {
            mListener.onLocationUpdate(location);
        }
    }

    /**
     * Returns LastKnownLocation from Location services
     * */
    @Nullable
    public Location getLastKnownLocation() {
        return mLastKnownLocation;
    }

    /**
     * GoogleApiClient required Listeners
     * */
    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener()
    {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult)
        {
            Log.w("Intelligence.Location", "Could not connect to GoogleApiClient");
        }
    };

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks()
    {
        @Override
        public void onConnected(Bundle bundle)
        {
            onGoogleApiClientConnect();
        }
        @Override
        public void onConnectionSuspended(int i)
        {
            Log.w("Intelligence.Location", "Connection to GoogleApiClient suspended");
        }
    };

    /**
     * Allows to connect GoogleApiClient
     * */
    public void connect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Allows to disconnect GoogleApiClient
     * */
    public void disconnect() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            try {
                LocationServices.GeofencingApi.removeGeofences(
                        mGoogleApiClient,
                        getGeofencePendingIntent()
                );
            } catch (SecurityException securityException) {
                Log.w("Intelligence", "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
            } catch (IllegalStateException stateException) {
                Log.w("Intelligence", "ApiClient wasn't connected/disconnected properly");
            }
        }
    }

    /**
     * Reloads actual Geofences List and add it into LocationModule
     * Previously set List of Geofences is removed from LocationModule
     * */
    public void reloadIntelligenceGeofencesList(List<IntelligenceGeofence> intelligenceGeofences) {

        mIntelligenceGeofences.clear();
        mIntelligenceGeofences.addAll(intelligenceGeofences);

        removeGeofencesLocationServices();

        // Only set geofences to the request if there are any.
        if ( mIntelligenceGeofences != null && mIntelligenceGeofences.size() > 0 ) {
            setGoogleApiGeofenceList();
            addGeofencesLocationServices();
        }
    }

    /**
     * Sets up the List<Geofence> objects to track GEOFENCE ENTER/EXIT events.
     * It uses private member mIntelligenceGeofences, initialized in constructor, to populate the list.
     *
     * Uses IntelligenceGeofence.Id property as RequestID to identify every single Geofence.
     * Uses IntelligenceGeofence Latitude, Longitude and Radius property to set circular region of Geofence.
     */
    private void setGoogleApiGeofenceList() {
        if (!mIntelligenceGeofences.isEmpty()) {
            mGeofenceList.clear();
            for(IntelligenceGeofence intelligenceGeofence : mIntelligenceGeofences) {
                mGeofenceList.add(new Geofence.Builder()
                        .setRequestId(String.valueOf(intelligenceGeofence.getId()))
                        .setCircularRegion(
                                intelligenceGeofence.getLatitude(),
                                intelligenceGeofence.getLongitude(),
                                intelligenceGeofence.getRadius().floatValue()
                        )
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            }
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * FLAG_UPDATE_CURRENT so that we get the same pending intent when we add/remove Geofences
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     *
     * The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a GEOFENCE_TRANSITION_ENTER notification
     * when the geofence is added and if the device is already inside that geofence.
     */
    private GeofencingRequest getGeofencingRequest() {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(mGeofenceList)
                .build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesLocationServices() {
        if (!mGoogleApiClient.isConnected()) {
            return;
        }

        try {
            LocationServices.GeofencingApi
                    .addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencePendingIntent())
                    .setResultCallback(mLocationServicesResultCallback);
        } catch (SecurityException securityException) {
            Log.w("Intelligence", "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
        }

    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesLocationServices() {
        if (!mGoogleApiClient.isConnected()) {
            return;
        }
        try {
            LocationServices.GeofencingApi
                    .removeGeofences(mGoogleApiClient, getGeofencePendingIntent())
                    .setResultCallback(mLocationServicesResultCallback);
        } catch (SecurityException securityException) {
            Log.w("Intelligence", "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
        }
    }

    /**
     * LocationService required Callback
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * @param status Status returned through a PendingIntent when addGeofences() or removeGeofences() get called.
     */
    private ResultCallback mLocationServicesResultCallback = new ResultCallback()
    {
        @Override
        public void onResult(Result result)
        {
            if (!result.getStatus().isSuccess()) {
                Log.e("Intelligence", "Error on adding / removing geofences");
            }
        }
    };

    /**
     * GeoManager.Listener public setter
     * Allows listener to track three types of events - Enter/Exit Geofence and LocationUpdate
     *
     * @param listener - object instance that implements GeoManager.Listener interface
     * */
    public void setListener(Listener listener) {
        mListener = listener;
    }

    /**
     * Processes triggeredGeofences and passes every single Geofence event to GeoManager.Listener
     *
     * @param triggeredGeofences - triggeredGeofences tracked in GeofenceTransitionIntentService
     *
     * @param geofenceTransition - Geofence Transition code
     * */
    public void onTriggeringGeofences(List<Geofence> triggeredGeofences, int geofenceTransition) {
        for (Geofence geofence : triggeredGeofences) {

            Integer geofenceID;

            try {
                geofenceID = Integer.parseInt(geofence.getRequestId());
            } catch (NumberFormatException numberFormatException) {
                // Nothing much we can do here.
                geofenceID = null;
            }

            if (geofenceID != null) {

                if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    mListener.onEnterGeofence(geofenceID);
                } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    mListener.onExitGeofence(geofenceID);
                }

            }

        }
    }

    /**
     * GeoManager.Listener public interface
     * Provides interface to track three different events related to Location Services
     *
     * Enter Geofence Event
     * Exit Geofence Event
     * Location Update Event
     * */
    public interface Listener {
        void onEnterGeofence(Integer GeofenceID);
        void onExitGeofence(Integer GeofenceID);
        void onLocationUpdate(Location location);
    }


}
