package com.tigerspike.intelligence;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Installation class implementation.
 * Handles create and update Installation requests and responses.
 */
public class Installation {

    //Keys used in requests
    static final String KEY_ID = "Id";
    static final String KEY_USER_ID = "UserId";
    static final String KEY_PROJECT_ID = "ProjectId";
    static final String KEY_APPLICATION_ID = "ApplicationId";
    static final String KEY_INSTALLATION_ID = "InstallationId";
    static final String KEY_INSTALLED_VERSION = "InstalledVersion";
    static final String KEY_DEVICE_TYPE_ID = "DeviceTypeId";
    static final String KEY_OPERATING_SYSTEM_VERSION = "OperatingSystemVersion";
    static final String KEY_CREATE_DATE = "DateCreated";
    static final String KEY_UPDATE_DATE = "DateUpdated";

    // mID is used internally by Intelligence to identify the installation object in the Database.
    // mInstallationId is required, but of unknown use.
    
    // Server generated properties
    private Integer mID;
    private String mCreateDate;
    private String mUpdateDate;
    private String mInstallationID;

    // SDK generated properties
    private String mInstalledVersion;
    private Integer mDeviceTypeID;
    private String mOperatingSystemVersion;
    private String mModelReference;

    // Settable properties
    private Integer mUserID;
    private Integer mProjectID;
    private Integer mApplicationID;

    public Installation(Context context) {

        mDeviceTypeID = DeviceType.SMARTPHONE.getValue();
        mModelReference = Build.MODEL;
        updateAppAndOSVersion(context);

    }

    public Installation(String jsonString) throws JSONException {
        this(new JSONObject(jsonString));
    }

    public Installation(Installation installation) {

        mID = installation.getID();
        mInstallationID = installation.getInstallationID();

        mInstalledVersion = installation.getInstalledVersion();
        mDeviceTypeID = installation.getDeviceTypeID();
        mOperatingSystemVersion = installation.getOperatingSystemVersion();
        mModelReference = installation.getModelReference();

        mUserID = installation.getUserID();
        mProjectID = installation.getProjectID();
        mApplicationID = installation.getApplicationID();

        mCreateDate = installation.getCreateDate();
        mUpdateDate = installation.getUpdateDate();

    }

    public Installation(JSONObject jsonObject) {

        mID = JSONUtils.getInteger(jsonObject, KEY_ID, null);
        mInstallationID = JSONUtils.getString(jsonObject, KEY_INSTALLATION_ID, null);

        mInstalledVersion = JSONUtils.getString(jsonObject, KEY_INSTALLED_VERSION, null);
        mDeviceTypeID = JSONUtils.getInteger(jsonObject, KEY_DEVICE_TYPE_ID, null);
        mOperatingSystemVersion = JSONUtils.getString(jsonObject, KEY_OPERATING_SYSTEM_VERSION, null);


        mUserID = JSONUtils.getInteger(jsonObject, KEY_USER_ID, null);
        mProjectID = JSONUtils.getInteger(jsonObject, KEY_PROJECT_ID, null);
        mApplicationID = JSONUtils.getInteger(jsonObject, KEY_APPLICATION_ID, null);

        mCreateDate = JSONUtils.getString(jsonObject, KEY_CREATE_DATE, null);
        mUpdateDate = JSONUtils.getString(jsonObject, KEY_UPDATE_DATE, null);

    }

    /**
    * @return JSON Dictionary representation used in Create Installation requests.
    */
    public JSONObject toJSONObject() {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(KEY_ID, mID);
            jsonObject.put(KEY_INSTALLATION_ID, mInstallationID);

            jsonObject.put(KEY_INSTALLED_VERSION, mInstalledVersion);
            jsonObject.put(KEY_DEVICE_TYPE_ID, mDeviceTypeID);
            jsonObject.put(KEY_OPERATING_SYSTEM_VERSION, mOperatingSystemVersion);

            jsonObject.put(KEY_USER_ID, mUserID);
            jsonObject.put(KEY_PROJECT_ID, mProjectID);
            jsonObject.put(KEY_APPLICATION_ID, mApplicationID);

            jsonObject.put(KEY_CREATE_DATE, mCreateDate);
            jsonObject.put(KEY_UPDATE_DATE, mUpdateDate);



        } catch (JSONException jsonException) {
            // Not much we can do here.
        }

        return jsonObject;

    }

    /**
     * @return JSON Dictionary representation used in Update Installation requests.
     */
    public JSONObject prepareJSONObjectForUpdate() {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(KEY_ID, mID);

            jsonObject.put(KEY_INSTALLED_VERSION, mInstalledVersion);
            jsonObject.put(KEY_DEVICE_TYPE_ID, mDeviceTypeID);
            jsonObject.put(KEY_OPERATING_SYSTEM_VERSION, mOperatingSystemVersion);

            jsonObject.put(KEY_USER_ID, mUserID);

        } catch (JSONException jsonException) {
            // Not much we can do here.
        }

        return jsonObject;

    }

    public String toJSONString() {
        JSONObject jsonObject = toJSONObject();
        return jsonObject != null ? jsonObject.toString() : null;
    }

    public void updateAppAndOSVersion(Context context) {
        mInstalledVersion = getAppVersionName(context);
        mOperatingSystemVersion = Build.VERSION.RELEASE;
    }

    /**
     * @return version name of an Intelligence application.
     */
    public static String getAppVersionName(Context context) {

        String packageName = context.getPackageName();
        String versionName = null;

        try {
            versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException nameNotFoundException) {
            // Can't find version, so we'll return null.
        }

        return versionName;

    }


    // Getters for properties

    public Integer getID() {
        return mID;
    }

    public String getCreateDate() {
        return mCreateDate;
    }

    public String getUpdateDate() {
        return mUpdateDate;
    }

    public String getInstallationID() {
        return mInstallationID;
    }

    public Integer getUserID() {
        return mUserID;
    }

    public Integer getProjectID() {
        return mProjectID;
    }

    public Integer getApplicationID() {
        return mApplicationID;
    }

    public String getInstalledVersion() {
        return mInstalledVersion;
    }

    public Integer getDeviceTypeID() {
        return mDeviceTypeID;
    }

    public String getOperatingSystemVersion() {
        return mOperatingSystemVersion;
    }

    public String getModelReference() {
        return mModelReference;
    }

    // Setters for setable properties.

    public void setUserID(Integer userID) {
        mUserID = userID;
    }

    public void setProjectID(Integer projectID) {
        mProjectID = projectID;
    }

    public void setApplicationID(Integer applicationID) {
        mApplicationID = applicationID;
    }


    enum DeviceType {
        SMARTPHONE(1),
        TABLET(2),
        DESKTOP(3),
        SMART_TV(4),
        WEARABLE(5);

        public final Integer value;

        DeviceType(Integer value) {
            this.value = value;
        }

        public Integer getValue(){
            return this.value;
        }

    }
}


