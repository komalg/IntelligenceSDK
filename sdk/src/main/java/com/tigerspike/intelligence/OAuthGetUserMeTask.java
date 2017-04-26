package com.tigerspike.intelligence;

import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OAuthGetUserMeTask extends IntelligenceTask {

    private OAuth mOAuth;

    public User user;

    public OAuthGetUserMeTask(OAuth oAuth, TaskListener taskListener) {
        super(taskListener);
        mOAuth = oAuth;
    }

    @Override
    void execute() throws Exception {

        Response response = mOAuth.createGetUserRequest(mOAuth.getCurrentAuthenticationToken()).execute();

        //If this is an error then exception is thrown and code execution is stopped
        handleError(response);

        try {
            JSONObject jsonObject = JSONUtils.parseSimpleJSONObject(response.bodyData());
            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            user = new User(jsonArray.getJSONObject(0));
        } catch (JSONException jsonException) {
            throw (new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.ParseError, "Could not parse user").addCause(jsonException));
        }
    }

}
