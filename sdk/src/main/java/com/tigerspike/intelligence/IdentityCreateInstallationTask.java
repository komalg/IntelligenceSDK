package com.tigerspike.intelligence;

import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IdentityCreateInstallationTask extends IntelligenceTask {

    private IdentityModule mIdentity;
    private Installation mInstallation;

    public Installation installation;

    public IdentityCreateInstallationTask(IdentityModule identity, Installation installation, TaskListener taskListener) {
        super(taskListener);
        mIdentity = identity;
        mInstallation = installation;
    }

    @Override
    void execute() throws Exception {

        Response response = mIdentity.createCreateInstallationRequest(mInstallation).execute();
        //If this is an error then exception is thrown and code execution is stopped
        handleError(response);

        try {
            JSONObject jsonObject = JSONUtils.parseSimpleJSONObject(response.bodyData());
            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            installation = new Installation(jsonArray.getJSONObject(0));
        } catch (JSONException jsonException) {
            throw new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.ParseError, "Could not parse installation").addCause(jsonException);
        }
    }

}
