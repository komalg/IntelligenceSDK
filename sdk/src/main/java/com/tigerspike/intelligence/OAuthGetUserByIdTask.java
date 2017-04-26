package com.tigerspike.intelligence;

import com.tigerspike.intelligence.exceptions.IntelligenceParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OAuthGetUserByIdTask extends IntelligenceTask {

    private OAuth mOAuth;
    private int mId;

    public User user;

    public OAuthGetUserByIdTask(OAuth oAuth, int id, TaskListener taskListener) {
        super(taskListener);
        mId = id;
        mOAuth = oAuth;
    }

    @Override
    void execute() throws Exception {

        Response response = mOAuth.createGetUserByIdRequest(mOAuth.getApplicationUserToken(), mId).execute();

        //If this is an error then exception is thrown and code execution is stopped
        handleError(response);

        try {
            JSONObject jsonObject = JSONUtils.parseSimpleJSONObject(response.bodyData());
            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            user = new User(jsonArray.getJSONObject(0));
        } catch (JSONException jsonException) {
            throw (new IntelligenceParseException("Could not retrieve user").addCause(jsonException));
        }
    }

}
