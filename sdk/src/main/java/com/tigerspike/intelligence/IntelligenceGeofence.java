package com.tigerspike.intelligence;

import org.json.JSONException;
import org.json.JSONObject;

public final class IntelligenceGeofence
{
    private Double mLongitude;
    private Double mLatitude;
    private Integer mId;
    private Integer mProjectId;
    private String mName;
    private String mAddress;
    private Double mRadius;
    private String mTags;

    /**
     *
     * Set of private keys used to parse Geofence json
     *
     * */
    private static String GEOFENCE_GEOLOCATION_KEY = "Geolocation";
    private static String GEOFENCE_LATITUDE_KEY = "Latitude";
    private static String GEOFENCE_LONGITUDE_KEY = "Longitude";

    private static String GEOFENCE_ID_KEY = "Id";
    private static String GEOFENCE_PROJECT_ID_KEY = "ProjectId";
    private static String GEOFENCE_NAME_KEY = "Name";
    private static String GEOFENCE_ADDRESS_KEY = "Address";
    private static String GEOFENCE_RADIUS_KEY = "Radius";
    private static String GEOFENCE_TAGS_KEY = "Tags";

    /**
     * Constructs Geofence object from JSONObject
     *
     * @param jsonObject - JSONObject that contains single Geofence data.
     * */
    public IntelligenceGeofence(JSONObject jsonObject) throws JSONException {

        mId = (JSONUtils.getInteger(jsonObject, GEOFENCE_ID_KEY, null));
        mProjectId = (JSONUtils.getInteger(jsonObject, GEOFENCE_PROJECT_ID_KEY, null));
        mName = (JSONUtils.getString(jsonObject, GEOFENCE_NAME_KEY, null));
        mAddress = (JSONUtils.getString(jsonObject, GEOFENCE_ADDRESS_KEY, null));
        mRadius = (JSONUtils.getDouble(jsonObject, GEOFENCE_RADIUS_KEY, null));

        JSONObject geolocation = jsonObject.getJSONObject(GEOFENCE_GEOLOCATION_KEY);
        mLatitude = (JSONUtils.getDouble(geolocation, GEOFENCE_LATITUDE_KEY, null));
        mLongitude = (JSONUtils.getDouble(geolocation, GEOFENCE_LONGITUDE_KEY, null));

        mTags = (JSONUtils.getString(jsonObject, GEOFENCE_TAGS_KEY, null));
    }

    public JSONObject toJSONObject() throws JSONException{

        JSONObject jsonObject = new JSONObject();

        jsonObject.put(GEOFENCE_ID_KEY, mId);
        jsonObject.put(GEOFENCE_PROJECT_ID_KEY, mProjectId);
        jsonObject.put(GEOFENCE_NAME_KEY, mName);
        jsonObject.put(GEOFENCE_ADDRESS_KEY, mAddress);
        jsonObject.put(GEOFENCE_RADIUS_KEY, mRadius);

        JSONObject geolocation = new JSONObject();
        geolocation.put(GEOFENCE_LATITUDE_KEY, mLatitude);
        geolocation.put(GEOFENCE_LONGITUDE_KEY, mLongitude);
        jsonObject.put(GEOFENCE_GEOLOCATION_KEY, geolocation);

        jsonObject.put(GEOFENCE_TAGS_KEY, mTags);

        return jsonObject;

    }

    public String toJSONString()  {
        String result = null;
        try {
            result = toJSONObject().toString();
        } catch (JSONException ignore) {}
        return result;
    }

    /**
     * Constructs Geofence object from JSON String.
     *
     * @param json - String object containing single Geofence object description in JSON format.
     * */
    public IntelligenceGeofence(String json) throws JSONException {
        this(new JSONObject(json));
    }

    /**
     * Public getters
     * */
    public Double getLongitude()
    {
        return mLongitude;
    }


    public Double getLatitude()
    {
        return mLatitude;
    }


    public Integer getId()
    {
        return mId;
    }


    public Integer getProjectId()
    {
        return mProjectId;
    }


    public String getName()
    {
        return mName;
    }


    public String getAddress()
    {
        return mAddress;
    }

    public Double getRadius()
    {
        return mRadius;
    }

    public String getTags() {
        return mTags;
    }
}
