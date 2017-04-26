package com.tigerspike.intelligence;

public class IdentityUnregisterIdentifierTask extends IntelligenceTask {

    private OAuth mOAuth;
    private IdentityModule mIdentity;
    public Identifier identifier;

    public IdentityUnregisterIdentifierTask(Identifier identifier,  OAuth oAuth, IdentityModule identityModule, TaskListener taskListener) {
        super(taskListener);
        this.identifier = identifier;
        mOAuth = oAuth;
        mIdentity = identityModule;
    }

    @Override
    void execute() throws Exception {

        Response response = mIdentity.createDeleteIdentifierRequest(mOAuth.getCurrentAuthenticationToken(), identifier).execute();

        //If this is an error then exception is thrown and code execution is stopped
        handleError(response);

    }

}
