package com.tigerspike.intelligence;

import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IdentityRegisterIdentifierTask extends IntelligenceTask {

    private OAuth mOAuth;
    private IdentityModule mIdentity;
    public Identifier identifier;

    public IdentityRegisterIdentifierTask(Identifier identifier, OAuth oAuth, IdentityModule identityModule, TaskListener taskListener) {
        super(taskListener);
        this.identifier = identifier;
        mOAuth = oAuth;
        mIdentity = identityModule;
    }

    @Override
    void execute() throws Exception {

        Response response = mIdentity.createCreateIdentifierRequest(mOAuth.getCurrentAuthenticationToken(), identifier).execute();

        //If this is an error then exception is thrown and code execution is stopped
        handleError(response);

        try {
            JSONObject jsonObject = JSONUtils.parseSimpleJSONObject(response.bodyData());
            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            identifier = new Identifier(jsonArray.getJSONObject(0));
        } catch (JSONException jsonException) {
            identifier = null;
            throw new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.ParseError, "Could not parse installation").addCause(jsonException);
        }

    }

}
