package com.tigerspike.intelligence;

import android.app.Application;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LocationModule
 *
 * Implementation class of Location interface.
 *
 */
final class LocationModule extends IntelligenceModule implements Location
{
    private Configuration mConfiguration;
    private OAuth mOAuth;
    private Analytics mAnalytics;
    private TaskExecutor mTaskExecutor;
    private RequestURLBuilder mRequestURLBuilder;
    private DataStore mDataStore;
    private android.location.Location mLastKnownLocation;

    GeoManager mGeoManager;

    private boolean mHasLocationPermissions = false;


    /**
     * Set of private keys used to parse Location requests
     *
     * */
    private static String GEOFENCE_CACHE_KEY = "GeoFences";

    /**
     * Available Location Endpoints
     *
     * */
    static final String ENDPOINT_GET_GEOFENCES = "projects/{PROJECT_ID}/geofences";

    static final String KEY_LONGITUDE = "longitude";
    static final String KEY_LATITUDE = "latitude";
    static final String KEY_RADIUS = "radius";
    static final String KEY_PAGE_SIZE = "page_size";
    static final String KEY_PAGE_NUMBER = "pagenumber";

    /**
     * Public constructor
     * @param configuration Intelligence configuration, required parameter. If configuration is null, any Identity action cannot be performed (dependency)
     * @param taskExecutor TaskExecuter object
     * @param dataStore DataStore object, required parameter. If DataStore object is null, Geofences data cannot be cached and stored.
     * */
    public LocationModule(@NonNull Configuration configuration,  @NonNull TaskExecutor taskExecutor, @NonNull DataStore dataStore, @NonNull OAuth oAuth, @NonNull Application application, @NonNull Analytics analytics) {
        mConfiguration = configuration;
        mOAuth = oAuth;
        mTaskExecutor = taskExecutor;
        mDataStore = dataStore;
        mAnalytics = analytics;

        mRequestURLBuilder = new RequestURLBuilder(mConfiguration);

        mHasLocationPermissions = Utils.hasPermission(application, Constants.ANDROID_PERMISSION_ACCESS_FINE_LOCATION);

        if (!mHasLocationPermissions) {
            Log.w("Intelligence.Location","To use geofences and location tracking, set the " + Constants.ANDROID_PERMISSION_ACCESS_FINE_LOCATION + " permission in the app's manifest.");
        } else {
            mGeoManager = new GeoManager(application);
            mGeoManager.setListener(mGeoManagerListener);
        }


    }

    @Override
    void startUp() {

        if (!mHasLocationPermissions) {
            return;
        }

        updateGeofences();

    }

    GeoManager.Listener mGeoManagerListener = new GeoManager.Listener() {
        @Override
        public void onEnterGeofence(Integer geofenceID) {
            mAnalytics.trackEvent(new AnalyticsEventEnterGeofence(geofenceID));
        }

        @Override
        public void onExitGeofence(Integer geofenceID) {
            mAnalytics.trackEvent(new AnalyticsEventExitGeofence(geofenceID));
        }

        @Override
        public void onLocationUpdate(android.location.Location location) {
            mAnalytics.setLastKnownLocation(location);
            mLastKnownLocation = location;
        }

    };

    /**
     * Returns last known location
     *
     * @return last known location as android.location.Location
     */
    @Override
    public @Nullable android.location.Location getLastKnownLocation() {
        return mLastKnownLocation;
    }

    void updateGeofences() {

        if (mConfiguration.getUseGeofences()) {

            if (mLastKnownLocation != null) {
                //50 is page size for geofences listing
                getGeofences(mOnUpdateGeofencesListener, mLastKnownLocation.getLongitude(), mLastKnownLocation.getLatitude(), 10000.0, 50, 0);
            } else {
                //50 is page size for geofences listing
                getGeofences(mOnUpdateGeofencesListener, null, null, 10000.0, 50, 0);
            }

        }

    }

    private OnGetGeofencesListener mOnUpdateGeofencesListener = new OnGetGeofencesListener() {

        @Override
        public void onGetGeofences(List<IntelligenceGeofence> geofences, IntelligenceException intelligenceException) {
            if (intelligenceException == null) {
                if (geofences != null) {
                    cacheGeofences(geofences);
                    mGeoManager.reloadIntelligenceGeofencesList(geofences);
                }
            }
        }

    };

    /**
     * Sends GET Geofences request to retreive list of Geofences
     *
     * @param onGetGeofencesListener - a callback to be invoked after GET Geofences request is finished.
     * */
    @Override
    public void getGeofences(@Nullable final OnGetGeofencesListener onGetGeofencesListener)
    {

        if (!mTaskExecutor.hasConnection()) {
            if (onGetGeofencesListener != null) {
                onGetGeofencesListener.onGetGeofences(null, new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError));
            }
            return;
        }

        IntelligenceTask.TaskListener taskListener = null;

        if (onGetGeofencesListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onGetGeofencesListener.onGetGeofences(((LocationGetGeoFencesTask) intelligenceTask).geoFences, null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException e) {
                    onGetGeofencesListener.onGetGeofences(null, e);
                }
            };

        }

        mTaskExecutor.addTask(new LocationGetGeoFencesTask(this, taskListener));

    }

    @Override
    public void getGeofences(final OnGetGeofencesListener onGetGeofencesListener,
                                       @Nullable Double longitude,
                                       @Nullable Double latitude,
                                       @Nullable Double radius,
                                       @Nullable Integer page_size,
                                       @Nullable Integer page_number) {

        if (!mTaskExecutor.hasConnection()) {
            if (onGetGeofencesListener != null) {
                onGetGeofencesListener.onGetGeofences(null, new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError));
            }
            return;
        }

        IntelligenceTask.TaskListener taskListener = null;

        if (onGetGeofencesListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onGetGeofencesListener.onGetGeofences(((LocationGetGeoFencesTask) intelligenceTask).geoFences, null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException e) {
                    onGetGeofencesListener.onGetGeofences(null, e);
                }
            };

        }

        mTaskExecutor.addTask(new LocationGetGeoFencesTask(this, longitude, latitude, radius, page_size, page_number, taskListener));

    }

    Request createGetGeofencesRequest(@Nullable Double longitude,
                                      @Nullable Double latitude,
                                      @Nullable Double radius,
                                      @Nullable Integer pageSize,
                                      @Nullable Integer pageNumber) throws Exception {

        URL url = mRequestURLBuilder.locationBaseURL().urlPath(ENDPOINT_GET_GEOFENCES).url();


        Uri uri= Uri.parse(url.toExternalForm())
                .buildUpon()
                .appendQueryParameter(KEY_LONGITUDE, longitude.toString())
                .appendQueryParameter(KEY_LATITUDE, latitude.toString())
                .appendQueryParameter(KEY_RADIUS, new Integer(radius.intValue()).toString())
                .appendQueryParameter(KEY_PAGE_SIZE, pageSize.toString())
                .appendQueryParameter(KEY_PAGE_NUMBER, pageNumber.toString())
                .build();

        URL queryUrl = null;
        try {
            queryUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return RequestBuilder.GET(queryUrl)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(mOAuth.getCurrentAuthenticationToken())
                .build();
    }

    public List<IntelligenceGeofence> getCachedGeofences() {

        Set<String> cachedGeofences = mDataStore.getStringSet(GEOFENCE_CACHE_KEY, null);
        List<IntelligenceGeofence> geofencesList = new ArrayList<>();

        if (cachedGeofences != null) {
            for (String geofence : cachedGeofences) {
                try {
                    geofencesList.add(new IntelligenceGeofence(geofence));
                } catch (JSONException ignore) {}
            }
        }

        return geofencesList;
    }

    /**
     * Caches Geofences data - puts it into DataStore.
     *
     * @param geofences - a list of geofences. Every String contains JSON data for single Geofence.
     * */
    private void cacheGeofences(List<IntelligenceGeofence> geofences) {
        HashSet<String> geofenceSet = new HashSet<>();
        for (IntelligenceGeofence geofence:geofences) {
            String jsonFence = geofence.toJSONString();
            if (jsonFence != null) {
                geofenceSet.add(jsonFence);
            }
        }
        mDataStore.setStringSet(GEOFENCE_CACHE_KEY, geofenceSet);
    }

}
