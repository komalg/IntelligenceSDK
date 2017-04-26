package com.tigerspike.intelligence;

final class Constants {

    static final int VERSION_MAJOR = 1;
    static final String API_VERSION_PARAM = "v2";
    static final String PROVIDER_ID_PARAM = "300";


    /* Networking */
    static final int CONNECTION_TIMEOUT_MS = 30000;
    static final int CONNECTION_RESPONSE_TIMEOUT_MS = 20000;

    /* Headers */
    static final String CONTENT_TYPE_JSON = "application/json";
    static final String CONTENT_TYPE_FORM_URL_ENCODED = "application/x-www-form-urlencoded";

    static final String SDK_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    static final String ANDROID_PERMISSION_ACCESS_NETWORK_STATE = "android.permission.ACCESS_NETWORK_STATE";
    static final String ANDROID_PERMISSION_ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";

    /* Event Names */
    static final String APPLICATION_OPENED_EVENT = "Phoenix.Identity.Application.Opened";
    static final String APPLICATION_GEOFENCE_ENTER_EVENT = "Phoenix.Location.Geofence.Entered";
    static final String APPLICATION_GEOFENCE_EXIT_EVENT = "Phoenix.Location.Geofence.Exit";
    static final String APPLICATION_SCREEN_VIEWED_EVENT = "Phoenix.Identity.Application.ScreenViewed";
    static final String APPLICATION_USER_CREATED_EVENT = "Phoenix.Identity.User.Created";

    static final String KEY_INSTALLATION_DATA = "INSTALLATION_DATA";
    static final String KEY_INSTALLATION_ID = "InstallationId";

}
