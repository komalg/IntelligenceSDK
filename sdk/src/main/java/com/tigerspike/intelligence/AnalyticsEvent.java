package com.tigerspike.intelligence;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class AnalyticsEvent {

    private static final long MILLIS_BEFORE_EVENT_EXPIRATION = 24 * 60 * 60 * 1000;

    private static final String KEY_EVENT_DATE = "EventDate";
    private static final String KEY_PROJECT_ID = "ProjectId";
    private static final String KEY_IP_ADDRESS = "IpAddress";
    private static final String KEY_TYPE = "EventType";
    private static final String KEY_VALUE = "EventValue";
    private static final String KEY_TARGET_ID = "TargetId";

    private static final String KEY_USER_ID = "PhoenixIdentity_UserId";
    private static final String KEY_APPLICATION_ID = "PhoenixIdentity_ApplicationId";
    private static final String KEY_INSTALLATION_ID = "PhoenixIdentity_InstallationId";

    private static final String KEY_APPLICATION_VERSION = "ApplicationVersion";
    private static final String KEY_DEVICE_TYPE = "DeviceType";
    private static final String KEY_OPERATING_SYSTEM = "OperatingSystemVersion";

    private static final String KEY_LOCATION = "Geolocation";
    private static final String KEY_LOCATION_LONGITUDE = "Longitude";
    private static final String KEY_LOCATION_LATITUDE = "Latitude";
    private static final String KEY_META_DATA = "MetaData";

    private String  mType;
    private String  mValue;
    private String mTargetID;

    private String mDate;

    private Integer mUserId;
    private Integer mApplicationID;
    private String  mInstallationID;

    private String mApplicationVersion;
    private String mDeviceType;
    private String mOperatingSystem;
    private Integer mProjectId;
    private String mIpAddress;

    private Double mLatitude;
    private Double mLongitude;
    private HashMap<String,String> mMetaData;

    public AnalyticsEvent(String type) {
        this(type, "0");
    }

    public AnalyticsEvent(String type, String value) {
        mType = type;
        mValue = value;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.SDK_DATE_FORMAT);

        //according to Intelligence team (Jin) the event dates should always be in UTC. that
        //is why I am adding this line.
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        mDate = simpleDateFormat.format(new Date());
    }

    public String toJSONString() {
        return toJSONObject().toString();
    }

    public JSONObject toJSONObject() {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(KEY_EVENT_DATE, mDate);
            jsonObject.put(KEY_PROJECT_ID, mProjectId);
            jsonObject.put(KEY_TYPE, mType);
            jsonObject.put(KEY_VALUE, mValue);
            jsonObject.put(KEY_TARGET_ID, mTargetID);

            jsonObject.put(KEY_USER_ID, mUserId);
            jsonObject.put(KEY_APPLICATION_ID, mApplicationID);
            jsonObject.put(KEY_INSTALLATION_ID, mInstallationID);

            jsonObject.put(KEY_APPLICATION_VERSION, mApplicationVersion);
            jsonObject.put(KEY_DEVICE_TYPE, mDeviceType);
            jsonObject.put(KEY_OPERATING_SYSTEM, mOperatingSystem);

            if (mLatitude != null && mLongitude != null) {
                JSONObject location = new JSONObject();
                location.put(KEY_LOCATION_LATITUDE, mLatitude);
                location.put(KEY_LOCATION_LONGITUDE, mLongitude);
                jsonObject.put(KEY_LOCATION,location);
            }

            if (mMetaData != null) {
                JSONObject metaData = new JSONObject(mMetaData);
                jsonObject.put(KEY_META_DATA, metaData);
            }
            jsonObject.put(KEY_IP_ADDRESS, mIpAddress);
        } catch (JSONException jsonException) {
            // Not much we can do here.
        }

        return jsonObject;

    }

    void setUserID(Integer userID) {
        mUserId = userID;
    }

    void setApplicationID(Integer applicationID) {
        mApplicationID = applicationID;
    }

    void setInstallationID(String installationID) {
        mInstallationID = installationID;
    }

    void setApplicationVersion(String applicationVersion) {
        mApplicationVersion = applicationVersion;
    }

    public void setProjectId(Integer mProjectId) {
        this.mProjectId = mProjectId;
    }

    public void setIpAddress(String mIpAddress) {
        this.mIpAddress = mIpAddress;
    }

    void setDeviceType(String deviceType) {
        mDeviceType = deviceType;
    }

    void setOperatingSystem(String operatingSystem) {
        mOperatingSystem = operatingSystem;
    }

    void setLocation(android.location.Location location) {
        if (location == null) {
            mLatitude = null;
            mLongitude = null;
        } else {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
        }
    }

    void setLocation(Double longitude, Double latitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public void setTargetID(String targetID) {
        mTargetID = targetID;
    }

    public void setMetaData(HashMap<String,String> metaData) {
        mMetaData = metaData;
    }

    /**
     * Checks the JSON event and tells if it should be discarded or not.
     * @param jsonEvent
     * @return
     */
    static boolean shouldDropJSONObjectFromQueue(JSONObject jsonEvent) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.SDK_DATE_FORMAT);
            String dateString = jsonEvent.optString(KEY_EVENT_DATE);
            Date date = simpleDateFormat.parse(dateString);
            return ((new Date()).getTime() - date.getTime()) > MILLIS_BEFORE_EVENT_EXPIRATION;
        }
        catch (Exception e) {
            // The JSON is invalid, drop it to avoid corrupted data.
            return true;
        }
    }

    /**
     * @return the type of the event.
     */
    public String getType() {
        return mType;
    }
}
