package com.tigerspike.intelligence;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Identifier class implementation.
 * Handles create and delete Identifier requests and responses.
 */

public class Identifier {

    static final String KEY_ID = "Id";
    static final String KEY_USER_ID = "UserId";
    static final String KEY_APPLICATION_ID = "ApplicationId";
    static final String KEY_PROJECT_ID = "ProjectId";
    static final String KEY_IDENTIFIER_TYPE = "IdentifierTypeId";
    static final String KEY_VALUE = "Value";
    static final String KEY_IS_CONFIRMED = "IsConfirmed";
    static final String KEY_CREATE_DATE = "DateCreated";
    static final String KEY_MODIFY_DATE = "DateUpdated";

    private Integer mID;
    private Integer mUserID;
    private Integer mApplicationID;
    private Integer mProjectID;
    private IdentifierType mIdentifierType;
    private String mValue;
    private Boolean mIsConfirmed;
    private String mCreateDate;
    private String mModifyDate;

    public Identifier() {}

    public Identifier(Integer applicationID, IdentifierType identifierType, String value, Boolean isConfirmed) {
        mApplicationID = applicationID;
        mIdentifierType = identifierType;
        mValue = value;
        mIsConfirmed = isConfirmed;
    }

    public Identifier(JSONObject jsonObject) {
        setId(JSONUtils.getInteger(jsonObject, KEY_ID, null));
        setUserId(JSONUtils.getInteger(jsonObject, KEY_USER_ID, null));
        setProjectID(JSONUtils.getInteger(jsonObject, KEY_PROJECT_ID, null));
        setApplicationID(JSONUtils.getInteger(jsonObject, KEY_APPLICATION_ID, null));
        setIdentifierType(IdentifierType.parseInteger(JSONUtils.getInteger(jsonObject, KEY_IDENTIFIER_TYPE, null)));
        setValue(JSONUtils.getString(jsonObject, KEY_VALUE, null));
        setIsConfirmed(JSONUtils.getBoolean(jsonObject, KEY_IS_CONFIRMED, null));
        setCreateDate(JSONUtils.getString(jsonObject, KEY_CREATE_DATE, null));
        setModifyDate(JSONUtils.getString(jsonObject, KEY_MODIFY_DATE, null));
    }

    /**
     * @return JSON Dictionary representation used in Create Identifier requests.
     */

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_ID, mID);
            jsonObject.put(KEY_USER_ID, mUserID);
            jsonObject.put(KEY_PROJECT_ID, mProjectID);
            jsonObject.put(KEY_APPLICATION_ID, mApplicationID);
            jsonObject.put(KEY_IDENTIFIER_TYPE, mIdentifierType != null ? mIdentifierType.value : null);
            jsonObject.put(KEY_VALUE, mValue);
            jsonObject.put(KEY_IS_CONFIRMED, mIsConfirmed);
            jsonObject.put(KEY_CREATE_DATE, mCreateDate);
            jsonObject.put(KEY_MODIFY_DATE, mModifyDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String toJSONString() {
        JSONObject jsonObject = toJSONObject();
        return jsonObject != null ? jsonObject.toString() : null;
    }

    public Identifier(String jsonString) throws JSONException {
        this(new JSONObject(jsonString));
    }

    public void setId(Integer id) {
        mID = id;
    }

    public Integer getId() {
        return mID;
    }

    public void setUserId(Integer userId) {
        mUserID = userId;
    }

    public Integer getUserID() {
        return mUserID;
    }

    public void setProjectID(Integer projectID) {
        mProjectID = projectID;
    }

    public Integer getProjectID() {
        return mProjectID;
    }

    public void setIdentifierType(IdentifierType identifierType) {
        mIdentifierType = identifierType;
    }

    public IdentifierType getIdentifierType() {
        return mIdentifierType;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

    public void setIsConfirmed(Boolean isConfirmed) {
        mIsConfirmed = isConfirmed;
    }

    public Boolean getIsConfirmed() {
        return mIsConfirmed;
    }

    public void setCreateDate(String createDate) {
        mCreateDate = createDate;
    }

    public String getCreateDate() {
        return mCreateDate;
    }

    public void setModifyDate(String modifyDate) {
        mModifyDate = modifyDate;
    }

    public String getModifyDate() {
        return mModifyDate;
    }

    private Date parseDateValue(String date) {
        if(date != null) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat format = new SimpleDateFormat(Constants.SDK_DATE_FORMAT);
            try {
                return format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Integer getApplicationID() {
        return mApplicationID;
    }

    public void setApplicationID(Integer mApplicationID) {
        this.mApplicationID = mApplicationID;
    }

    enum IdentifierType {
        EMAIL(1),
        MSN(2),
        IOS_PUSH_TOKEN(3),
        ANDROID_GCM_TOKEN(4),
        WINDOWS_PUSH_TOKEN(5);

        public final Integer value;

        IdentifierType(Integer value) {
            this.value = value;
        }

        public static IdentifierType parseInteger(Integer value) {
            for (IdentifierType identifierType : IdentifierType.values()) {
                if (identifierType.value == value) {
                    return identifierType;
                }
            }
            return null;
        }

    }

}
