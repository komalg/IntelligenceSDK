package com.tigerspike.intelligence;

class IdentityUnregisterIdentifierOnBehalfTask extends IntelligenceTask {

    private OAuth mOAuth;
    private IdentityModule mIdentity;
    public Identifier identifier;

    public IdentityUnregisterIdentifierOnBehalfTask(Identifier identifier, OAuth oAuth, IdentityModule identityModule, TaskListener taskListener) {
        super(taskListener);
        this.identifier = identifier;
        mOAuth = oAuth;
        mIdentity = identityModule;
    }

    @Override
    void execute() throws Exception {

        Response response = mIdentity.createDeleteIdentifierOnBehalfRequest(mOAuth.getCurrentAuthenticationToken(), identifier).execute();

        //If this is an error then exception is thrown and code execution is stopped
        handleError(response);

    }

}
