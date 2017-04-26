package com.tigerspike.intelligence;

import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OAuthCreateUserTask extends IntelligenceTask {

    private OAuth mOAuth;
    private User mUser;

    public User user;

    public OAuthCreateUserTask(OAuth oAuth, User user, TaskListener taskListener) {
        super(taskListener);
        mOAuth = oAuth;
        mUser = user;
    }

    @Override
    void execute() throws Exception {

        Response response = mOAuth.createCreateUserRequest(mOAuth.getApplicationUserToken(), mUser).execute();

        //If this is an error then exception is thrown and code execution is stopped
        handleError(response);

        try {
            JSONObject jsonObject = JSONUtils.parseSimpleJSONObject(response.bodyData());
            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            user = new User(jsonArray.getJSONObject(0));
        } catch (JSONException e) {
            throw (new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.ParseError, "Could not parse user"));
        }
    }
}
