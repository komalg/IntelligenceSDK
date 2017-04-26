package com.tigerspike.intelligence;

import android.annotation.SuppressLint;

import com.tigerspike.intelligence.exceptions.IntelligenceIdentityException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @class User
 *
 * Container class to store data related to User
 * Can be used to manage User data through Identity module
 * Contains any user data which can be sent/received from Intelligence Identity API
 *
 */
public class User
{
    private Integer mUserId;
    private String mUserType;
    private String mUsername;
    private String mFirstName;
    private String mLastName;
    private Integer mLockingCount;
    private String mReference;
    private boolean isActive;
    private Date mCreateDate;
    private Date mModifyDate;
    private String mAvatarUrl;
    private Array mMetaData; //should be empty for now
    private Array mIdentifiers; //should be empty for now

    private Integer mCompanyId;
    private String mPassword;

    private static String passwordVerificationRegExShort = "(?=.*[A-Z])(?=.*[0-9]).{8,}";

    /**
     *
     * Set of private keys used do create/parse Identity requests
     *
     * */
    private static final String PASSWORD_KEY = "Password";
    private static final String USER_ID_KEY = "Id";
    private static final String USER_TYPE_ID_KEY = "UserTypeId";
    private static final String COMPANY_ID_KEY = "CompanyId";
    private static final String USERNAME_KEY = "Username";
    private static final String FIRSTNAME_KEY = "FirstName";
    private static final String LASTNAME_KEY = "LastName";
    private static final String LOCKING_COUNT_KEY = "LockingCount";
    private static final String REFERENCE_KEY = "Reference";
    private static final String AVATAR_KEY = "AvatarUrl";
    private static final String IS_ACTIVE_KEY = "IsActive";
    private static final String CREATE_DATE_KEY = "CreateDate";
    private static final String MODIFY_DATE_KEY = "ModifyDate";
    private static final String METADATA_KEY = "MetaDataParameters";
    private static final String IDENTIFIERS_KEY = "Identifiers";

    public User() {

        //Initialize only default values, which cannot be changed and accessed from outside
        setDefaultValues();
    }

    public static User createRandom(Integer companyId) {

        User result = null;

        try {
            // To satisfy password rules we always create a password with a lower, a capital and a number
            result = new User(companyId, Utils.createRandomString(32), "Aa1" + Utils.createRandomString(29), "SDK", "User", "");
        } catch (Exception e) {
            //
        }

        return result;

    }

    public User(Integer companyId, String username, String password, String firstname, String lastname, String avatarUrl)
            throws IntelligenceIdentityException
    {
        mCompanyId = companyId;
        mUsername = username;

        if(!User.isPasswordValid(password)){
            throw new IntelligenceIdentityException(IntelligenceIdentityException.ErrorCode.WeakPasswordError);
        }

        mPassword = password;
        mFirstName = firstname;
        mLastName = lastname;
        mAvatarUrl = avatarUrl;

        setDefaultValues();
    }

    public User(JSONObject jsonObject) {
        setDefaultValues();

        setUserId(JSONUtils.getInteger(jsonObject, USER_ID_KEY, null));
        setUserType(JSONUtils.getString(jsonObject, USER_TYPE_ID_KEY, null));
        setUsername(JSONUtils.getString(jsonObject, USERNAME_KEY, null));
        setPassword(JSONUtils.getString(jsonObject, PASSWORD_KEY, null));
        setFirstName(JSONUtils.getString(jsonObject, FIRSTNAME_KEY, null));
        setLastName(JSONUtils.getString(jsonObject, LASTNAME_KEY, null));
        setLockingCount(JSONUtils.getInteger(jsonObject, LOCKING_COUNT_KEY, null));
        setReference(JSONUtils.getString(jsonObject, REFERENCE_KEY, null));
        setIsActive(JSONUtils.getBoolean(jsonObject, IS_ACTIVE_KEY, false));
        setCompanyID(JSONUtils.getInteger(jsonObject, COMPANY_ID_KEY, null));

        setCreateDate(parseDateValue(JSONUtils.getString(jsonObject, CREATE_DATE_KEY, null)));
        setModifyDate(parseDateValue(JSONUtils.getString(jsonObject, MODIFY_DATE_KEY, null)));
    }

    public User(String json)
            throws JSONException
    {
        this(new JSONObject(json));

    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(USER_ID_KEY, getUserId() == null ? null : getUserId().toString());
            jsonObject.put(COMPANY_ID_KEY, getCompanyId() == null ? null : getCompanyId().toString());
            jsonObject.put(USERNAME_KEY, getUsername());
            jsonObject.put(PASSWORD_KEY, getPassword() == null ? null : getPassword());
            jsonObject.put(FIRSTNAME_KEY, getFirstName());
            jsonObject.put(LASTNAME_KEY, getLastName());
            jsonObject.put(AVATAR_KEY, getAvatarUrl() == null ? "" : getAvatarUrl());
            jsonObject.put(LOCKING_COUNT_KEY, Integer.toString(0));
            jsonObject.put(REFERENCE_KEY, "");
            jsonObject.put(IS_ACTIVE_KEY, Boolean.toString(true));
            jsonObject.put(METADATA_KEY, "");
            jsonObject.put(USER_TYPE_ID_KEY, "User");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public String toJSONString() {
        JSONObject jsonObject = toJSONObject();
        return jsonObject != null ? jsonObject.toString() : null;
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

    private void setDefaultValues() {
        mLockingCount = 0; //has to be 0
        mReference = ""; //has to be empty
        isActive = true; //has to be true
        mMetaData = null; //has to be empty
        mUserType = "User";  //has to be 'User'
    }

    public static void validatePassword(String password) throws IntelligenceIdentityException {
        if(!password.matches(passwordVerificationRegExShort)) {
            throw new IntelligenceIdentityException(IntelligenceIdentityException.ErrorCode.WeakPasswordError);
        }
    }

    public static boolean isPasswordValid(String password){
        return password.matches(passwordVerificationRegExShort);
    }

    /*
    * Protected setters - not available for world
    * */
    protected void setUserId(Integer mUserId)
    {
        this.mUserId = mUserId;
    }

    protected void setUserType(String userType)
    {
        this.mUserType = userType;
    }

    protected void setLockingCount(Integer lockingCount)
    {
        this.mLockingCount = lockingCount;
    }

    protected void setReference(String reference)
    {
        this.mReference = reference;
    }

    protected void setIsActive(boolean isActive)
    {
        this.isActive = isActive;
    }

    protected void setCreateDate(Date createDate)
    {
        this.mCreateDate = createDate;
    }

    protected void setModifyDate(Date modifyDate)
    {
        this.mModifyDate = modifyDate;
    }

    protected void setIdentifiers(Array identifiers)
    {
        this.mIdentifiers = identifiers;
    }

    protected void setMetaData(Array metaData)
    {
        this.mMetaData = metaData;
    }

    /*
    * Public setters - those properties can be changes by anyone
    * */
    public void setUsername(String username)
    {
        mUsername = username;
    }

    public void setFirstName(String firstName)
    {
        mFirstName = firstName;
    }

    public void setLastName(String lastName)
    {
        mLastName = lastName;
    }

    public void setPassword(String password) { mPassword = password; }

    public void setAvatarUrl(String mAvatarUrl)
    {
        this.mAvatarUrl = mAvatarUrl;
    }

    public void setCompanyID(Integer companyId)
    {
        mCompanyId = companyId;
    }

    /*
    * Public getters - data can be read by anyone
    * */
    public Integer getUserId()
    {
        return mUserId;
    }

    public String getUserType()
    {
        return mUserType;
    }

    public String getUsername()
    {
        return mUsername;
    }

    public String getPassword()
    {
        return mPassword;
    }

    public String getFirstName()
    {
        return mFirstName;
    }

    public String getLastName()
    {
        return mLastName;
    }

    public Integer getLockingCount()
    {
        return mLockingCount;
    }

    public String getReference()
    {
        return mReference;
    }

    public boolean isActive()
    {
        return isActive;
    }

    public Date getCreateDate()
    {
        return mCreateDate;
    }

    public Date getModifyDate()
    {
        return mModifyDate;
    }

    public String getAvatarUrl()
    {
        return mAvatarUrl;
    }

    public Array getMetaData()
    {
        return mMetaData;
    }

    public Array getIdentifiers()
    {
        return mIdentifiers;
    }

    public Integer getCompanyId()
    {
        return mCompanyId;
    }
}
