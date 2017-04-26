package com.tigerspike.intelligence;

public class OAuthAssignRoleTask extends IntelligenceTask {

    private OAuth mOAuth;

    private int mUserId;

    private int mRoleId;

    public OAuthAssignRoleTask(OAuth oAuth, int userId, int roleId, TaskListener taskListener) {
        super(taskListener);
        mOAuth = oAuth;
        mUserId = userId;
        mRoleId = roleId;
    }

    @Override
    void execute() throws Exception {

        Response response = mOAuth.createAssignRoleToUserRequest(mOAuth.getApplicationUserToken(), mUserId, mRoleId).execute();

        //If this is an error then exception is thrown and code execution is stopped
        handleError(response);
    }

}
