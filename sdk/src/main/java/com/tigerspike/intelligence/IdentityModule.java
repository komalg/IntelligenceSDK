package com.tigerspike.intelligence;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceInvalidParameterException;
import com.tigerspike.intelligence.exceptions.IntelligenceRequestException;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implementation class of Identity interface.
 * Besides implementation of Identity public interface it provides
 * set of private methods to perform any action required to manage User Identity Data.
 *
 */
final class IdentityModule extends IntelligenceModule implements Identity
{

    /**
     *
     * Available Identity Endpoints
     *
     * */
    static String ENDPOINT_CREATE_INSTALLATION = "projects/{PROJECT_ID}/installations";
    static String ENDPOINT_UPDATE_INSTALLATION = "projects/{PROJECT_ID}/installations";
    static String ENDPOINT_CREATE_IDENTIFIER = "projects/{PROJECT_ID}/identifiers";
    static String ENDPOINT_DELETE_IDENTIFIER = "projects/{PROJECT_ID}/identifiers/{IDENTIFIER_ID}";
    static String ENDPOINT_DELETE_IDENTIFIER_ON_BEHALF = "projects/{PROJECT_ID}/identifiers";

    private static String KEY_INSTALLATION_DATA = "INSTALLATION_DATA";

    private Configuration mConfiguration;
    private TaskExecutor mTaskExecutor;
    private RequestURLBuilder mRequestURLBuilder;
    private OAuth mOAuth;
    private DataStore mDataStore;
    private Application mApplication;

    private Installation mInstallation;

    @Override
    public void registerGCMToken(String gcmToken, @Nullable final OnRegisterIdentifierListener onRegisterIdentifierListener) {
        Identifier gcmIdentifier = new Identifier(mConfiguration.getApplicationID(), Identifier.IdentifierType.ANDROID_GCM_TOKEN, gcmToken, true);
        registerIdentifier(gcmIdentifier, onRegisterIdentifierListener);
    }

    /**
     * Registers the specified Identifier to the current logged in user.
     *
     * @param identifier Identifier to register
     */
    @Override
    public void registerIdentifier(Identifier identifier, @Nullable final OnRegisterIdentifierListener onRegisterIdentifierListener) {
        // we always need to unregister the identifier we are trying to register to avoid getting a duplication error from the backend
        // so, the continuation of the 'unregister identifier' action is to register the same identifier
        deleteIdentifierOnBehalf(identifier, new OnUnregisterIdentifierOnBehalfListener() {
           @Override
           public void OnUnregisterIdentifierOnBehalf(@Nullable Identifier identifier, @Nullable IntelligenceException intelligenceException) {
               proceedRegisterIdentifier(identifier, onRegisterIdentifierListener);
           }
       });
    }

    private void proceedRegisterIdentifier(Identifier identifier, @Nullable final OnRegisterIdentifierListener onRegisterIdentifierListener){
        if (!mTaskExecutor.hasConnection()) {
            if (onRegisterIdentifierListener != null) {
                onRegisterIdentifierListener.onRegisterIdentifier(identifier, new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError));
            }
            return;
        }

        identifier.setUserId(mOAuth.getCurrentUserId());
        identifier.setProjectID(mConfiguration.getProjectID());

        IntelligenceTask.TaskListener taskListener = null;

        if (onRegisterIdentifierListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onRegisterIdentifierListener.onRegisterIdentifier(((IdentityRegisterIdentifierTask) intelligenceTask).identifier, null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException intelligenceException) {
                    onRegisterIdentifierListener.onRegisterIdentifier(null, intelligenceException);
                }
            };

        }

        IdentityRegisterIdentifierTask task = new IdentityRegisterIdentifierTask(identifier, mOAuth, this, taskListener);
        mTaskExecutor.addTask(task);
    }

    /**
     * Returns user object if logged in.
     *
     * @return user object
     */
    public User getUser() {
        return mOAuth.getUser();
    }

    /**
     * Clear current user authentication tokens.
     *
     * @param onLogoutListener Listener triggered after logout
     */
    public void logout(@Nullable OnLogoutListener onLogoutListener) {
        mOAuth.clearUser();
        if (onLogoutListener != null) {
            onLogoutListener.onLogout(null);
        }
    }

    /**
     * Tries to authenticate username and password.
     *
     * @param username Users username
     * @param password Users password
     * @param onLoginListener Listener for login results.
     */
    public void login(@NonNull String username, @NonNull String password, @Nullable final OnLoginListener onLoginListener) {

        if (Utils.isEmpty(username)) {
            if (onLoginListener != null) {
                onLoginListener.onLogin(new IntelligenceInvalidParameterException("Username can't be empty"));
            }
            return;
        }

        if (Utils.isEmpty(password)) {
            if (onLoginListener != null) {
                onLoginListener.onLogin(new IntelligenceInvalidParameterException("Password can't be empty"));
            }
            return;
        }

        if (!mTaskExecutor.hasConnection()) {
            if (onLoginListener != null) {
                onLoginListener.onLogin(new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError));
            }
            return;
        }

        IntelligenceTask.TaskListener taskListener = null;

        if (onLoginListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onLoginListener.onLogin(null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException intelligenceException) {
                    onLoginListener.onLogin(intelligenceException);
                }
            };

        }

        OAuthAuthenticateTask task = new OAuthAuthenticateTask(username, password, OAuth.UserType.User, mOAuth, taskListener);

        mTaskExecutor.addTask(task);

    }

    /**
     * Unregister identifier.
     *
     * @param identifier to unregister
     */
    @Override
    public void unregisterIdentifier(Identifier identifier, @Nullable final OnUnregisterIdentifierListener onUnregisterIdentifierListener) {

        if (!mTaskExecutor.hasConnection()) {
            if (onUnregisterIdentifierListener != null) {
                onUnregisterIdentifierListener.onUnregisterIdentifier(identifier, new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError));
            }
            return;
        }

        IntelligenceTask.TaskListener taskListener = null;

        if (onUnregisterIdentifierListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onUnregisterIdentifierListener.onUnregisterIdentifier(((IdentityUnregisterIdentifierTask) intelligenceTask).identifier, null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException intelligenceException) {
                    onUnregisterIdentifierListener.onUnregisterIdentifier(null, intelligenceException);
                }
            };

        }

        IdentityUnregisterIdentifierTask task = new IdentityUnregisterIdentifierTask(identifier, mOAuth, this, taskListener);
        mTaskExecutor.addTask(task);
    }

    /**
     * Deletes the specified identifier from all users.
     *
     * @param identifier to delete
     */
    @Override
    public void deleteIdentifierOnBehalf(Identifier identifier, @Nullable final OnUnregisterIdentifierOnBehalfListener onUnregisterIdentifierOnBehalfListener) {

        if (!mTaskExecutor.hasConnection()) {
            if (onUnregisterIdentifierOnBehalfListener != null) {
                onUnregisterIdentifierOnBehalfListener.OnUnregisterIdentifierOnBehalf(identifier, new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError,"No network available"));
            }
            return;
        }

        IntelligenceTask.TaskListener taskListener = null;

        if (onUnregisterIdentifierOnBehalfListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onUnregisterIdentifierOnBehalfListener.OnUnregisterIdentifierOnBehalf(((IdentityUnregisterIdentifierOnBehalfTask) intelligenceTask).identifier, null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException intelligenceException) {
                    onUnregisterIdentifierOnBehalfListener.OnUnregisterIdentifierOnBehalf(null, intelligenceException);
                }
            };

        }

        IdentityUnregisterIdentifierOnBehalfTask task = new IdentityUnregisterIdentifierOnBehalfTask(identifier, mOAuth, this, taskListener);
        mTaskExecutor.addTask(task);
    }

    /**
     * Public constructor
     * @param configuration - Intelligence configuration, required parameter. If configuration is null, any Identity action cannot be performed (dependency)
     * @param taskExecutor - TaskExecutor
     * */
    public IdentityModule(@NonNull Configuration configuration, @NonNull TaskExecutor taskExecutor, @NonNull DataStore dataStore, @NonNull OAuth OAuth, @NonNull Application application) {
        mConfiguration = configuration;
        mTaskExecutor = taskExecutor;
        mDataStore = dataStore;
        mOAuth = OAuth;
        mApplication = application;
        mRequestURLBuilder = new RequestURLBuilder(mConfiguration);
    }

    @Override
    void startUp() {
        checkInstallation(mApplication);
    }

    /**
     * Checks if there's installation data present and if it need updating.
     * Send the installation details to the Intelligence server.
     *
     * @param context Application context for retrieval of App version and system properties.
     */
    private void checkInstallation(Context context) {

        // Check if mInstallation is not initialized, if not try getting it from DataStore.
        if (mInstallation == null) {
            String installationString = mDataStore.get(KEY_INSTALLATION_DATA);

            if (installationString != null) {
                try {
                    mInstallation = new Installation(installationString);
                } catch (JSONException jsonException) {
                    // Invalid JSON
                }
            }
        }

        // If mInstallation == null, no installation has been posted to Intelligence yet. Try to post it.
        // If mInstallation != null, check if app version has changed and try to post an update.
        if (mInstallation == null) {
            Installation installation = new Installation(context);
            installation.setApplicationID(mConfiguration.getApplicationID());
            installation.setProjectID(mConfiguration.getProjectID());
            installation.setUserID(mOAuth.getCurrentUserId());
            mTaskExecutor.addTask(new IdentityCreateInstallationTask(this, installation, mInstallationTaskListener));
        } else if (!mInstallation.getInstalledVersion().equals(Installation.getAppVersionName(context))) {
            Installation installation = new Installation(mInstallation);
            installation.updateAppAndOSVersion(context);
            mTaskExecutor.addTask(new IdentityUpdateInstallationTask(this, installation, mInstallationTaskListener));
        }

    }

    /**
     * Creates and executes a Request to signify Intelligence of a new installation.
     *
     * @param installation Installation object containing the installation properties of the app.
     */
    Request createCreateInstallationRequest(@NonNull Installation installation) throws Exception {

        URL url = mRequestURLBuilder.identityBaseURL().urlPath(ENDPOINT_CREATE_INSTALLATION).url();

        JSONArray bodyJSON = new JSONArray().put(installation.toJSONObject());

        return RequestBuilder.POST(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(mOAuth.getCurrentAuthenticationToken())
                .body(bodyJSON.toString())
                .build();

    }

    /**
     * Creates and executes a Request to signify Intelligence of an installation of a different version of the app.
     *
     * @param installation Installation object containing the installation properties of the app.
     */
    Request createUpdateInstallationRequest(@NonNull Installation installation) throws Exception {

        URL url = mRequestURLBuilder.identityBaseURL().urlPath(ENDPOINT_UPDATE_INSTALLATION).url();

        JSONArray bodyJSON = new JSONArray().put(installation.prepareJSONObjectForUpdate());

        return RequestBuilder.PUT(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(mOAuth.getCurrentAuthenticationToken())
                .body(bodyJSON.toString())
                .build();

    }

    /**
     * Private response listener for the createInstallation and updateInstallation request.
     * If the posting of the installation details was succesfull the new Installation data is store on the app.
     *
     */
    private IntelligenceTask.TaskListener mInstallationTaskListener = new IntelligenceTask.TaskListener() {

        @Override
        public void onTaskFinish(IntelligenceTask intelligenceTask) {

            Installation installation = null;

            if (intelligenceTask instanceof IdentityCreateInstallationTask) {
                installation = ((IdentityCreateInstallationTask) intelligenceTask).installation;
            } else if (intelligenceTask instanceof IdentityUpdateInstallationTask) {
                installation = ((IdentityUpdateInstallationTask) intelligenceTask).installation;
            }

            if (installation != null) {
                mInstallation = installation;
                mDataStore.set(KEY_INSTALLATION_DATA, mInstallation.toJSONString());
            }

        }

        @Override
        public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException e) {
            // Error ignored since its done automatic. Will be retried on next launch.
        }

    };

    /**
     *
     * Sends GET User(Me) request
     *
     * @param onGetUserListener - OnGetUserListener in which SDK user can process with received User Identity Data
     *
     * */
    @Override
    public void getMe(@Nullable final OnGetUserListener onGetUserListener)
    {

        if (!mTaskExecutor.hasConnection()) {
            if (onGetUserListener != null) {
                onGetUserListener.onGetUser(null, new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError));
            }
            return;
        }

        IntelligenceTask.TaskListener taskListener = null;

        if (onGetUserListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onGetUserListener.onGetUser(((OAuthGetUserMeTask) intelligenceTask).user, null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException e) {
                    onGetUserListener.onGetUser(null, e);
                }
            };

        }

        OAuthGetUserMeTask task = new OAuthGetUserMeTask(mOAuth, taskListener);
        mTaskExecutor.addTask(task);

    }

    /**
     * Sends GET User request
     *
     * @param id - id of user to be found
     * @param onGetUserListener - OnGetUserListener in which further process of User object can be proceed
     *
     * */
    public void getUserById(int id, @Nullable final OnGetUserListener onGetUserListener)
    {

        if (!mTaskExecutor.hasConnection()) {
            if (onGetUserListener != null) {
                onGetUserListener.onGetUser(null, new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError,"No network available"));
            }
            return;
        }

        IntelligenceTask.TaskListener taskListener = null;

        if (onGetUserListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onGetUserListener.onGetUser(((OAuthGetUserByIdTask) intelligenceTask).user, null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException e) {
                    onGetUserListener.onGetUser(null, e);
                }
            };

        }

        OAuthGetUserByIdTask task = new OAuthGetUserByIdTask(mOAuth, id, taskListener);
        mTaskExecutor.addTask(task);
    }

    @Override
    public void assignRole(int userId, int roleId, final @Nullable OnAssignRoleListener onAssignRoleListener) {
        if (!mTaskExecutor.hasConnection()) {
            if (onAssignRoleListener != null) {
                onAssignRoleListener.onAssignRole(new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError, "No network available"));
            }
            return;
        }

        IntelligenceTask.TaskListener taskListener = null;

        if (onAssignRoleListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onAssignRoleListener.onAssignRole(null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException e) {
                    onAssignRoleListener.onAssignRole(e);
                }
            };

        }

        OAuthAssignRoleTask task = new OAuthAssignRoleTask(mOAuth, userId, roleId, taskListener);
        mTaskExecutor.addTask(task);
    }

    @Override
    public void revokeRole(int userId, int roleId, @Nullable final OnRevokeRoleListener onRevokeRoleListener)
    {

        if (!mTaskExecutor.hasConnection()) {
            if (onRevokeRoleListener != null) {
                onRevokeRoleListener.onRevokeRole(new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError,"No network available"));
            }
            return;
        }

        IntelligenceTask.TaskListener taskListener = null;

        if (onRevokeRoleListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onRevokeRoleListener.onRevokeRole(null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException e) {
                    onRevokeRoleListener.onRevokeRole(e);
                }
            };

        }

        OAuthRevokeRoleTask task = new OAuthRevokeRoleTask(mOAuth, userId, roleId, taskListener);
        mTaskExecutor.addTask(task);
    }

    /**
     *
     * Updates user with user object. UserObject should be requested by getMe or getUser.
     *
     * @param user - @NonNull object that contains data to use to update existing User
     *
     * @param onUpdateUserListener - OnUpdateUserListener to be called after get user request is finished.
     * */
    @Override
    public void updateUser(@NonNull User user, @Nullable final OnUpdateUserListener onUpdateUserListener) throws IntelligenceInvalidParameterException
    {

        if (!mTaskExecutor.hasConnection()) {
            if (onUpdateUserListener != null) {
                onUpdateUserListener.onUpdateUser(null, new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError));
            }
            return;
        }

        isValidUserObjectToUpdate(user);

        IntelligenceTask.TaskListener taskListener = null;

        if (onUpdateUserListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onUpdateUserListener.onUpdateUser(((OAuthUpdateUserTask) intelligenceTask).user, null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException e) {
                    onUpdateUserListener.onUpdateUser(null, e);
                }
            };

        }

        OAuthUpdateUserTask task = new OAuthUpdateUserTask(mOAuth, user, taskListener);
        mTaskExecutor.addTask(task);

    }

    /**
     * Creates user from provided user object, uses callback to report errors or success
     *
     * @param user - @NonNull object that contains data to use to create new Intelligence User
     *
     * @param onCreateUserListener - OnCreateUserListener in which further process of User object can be proceed
     * */
    @Override
    public void createUser(@NonNull User user, @Nullable final OnCreateUserListener onCreateUserListener) throws IntelligenceInvalidParameterException
    {

        if (!mTaskExecutor.hasConnection()) {
            if (onCreateUserListener != null) {
                onCreateUserListener.onCreateUser(null, new IntelligenceRequestException(IntelligenceRequestException.ErrorCode.InternetOfflineError));
            }
            return;
        }

        isValidUserObjectToCreate(user);

        IntelligenceTask.TaskListener taskListener = null;

        if (onCreateUserListener != null) {

            taskListener = new IntelligenceTask.TaskListener() {
                @Override
                public void onTaskFinish(IntelligenceTask intelligenceTask) {
                    onCreateUserListener.onCreateUser(((OAuthCreateUserTask) intelligenceTask).user, null);
                }

                @Override
                public void onTaskError(IntelligenceTask intelligenceTask, IntelligenceException e) {
                    onCreateUserListener.onCreateUser(null, e);
                }
            };

        }

        OAuthCreateUserTask task = new OAuthCreateUserTask(mOAuth, user, taskListener);
        mTaskExecutor.addTask(task);

    }

    /**
     *
     * Validates if User object is valid to be sent through Identity Create User request.
     * Required User properties are: companyId, username, password, first name, last name, avatarUrl
     *
     * @param user - User object to be validated
     *
     * @throws IntelligenceInvalidParameterException
     * */
    private void isValidUserObjectToCreate (User user) throws IntelligenceInvalidParameterException
    {
        if(user.getCompanyId() == null) {
            throw new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.ErrorCode.MissingParameter, "CompanyId parameter is missing");
        }

        if(user.getUsername() == null || user.getUsername().equalsIgnoreCase("")) {
            throw new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.ErrorCode.MissingParameter, "Username parameter is missing");
        }

        if(user.getPassword() == null) {
            throw new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.ErrorCode.MissingParameter, "Password parameter is missing");
        }

        if(user.getFirstName() == null) {
            throw new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.ErrorCode.MissingParameter, "First name parameter is missing");
        }

        if(user.getLastName() == null) {
            throw new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.ErrorCode.MissingParameter, "Last name parameter is missing");
        }

        if(user.getAvatarUrl() == null) {
            throw new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.ErrorCode.MissingParameter, "AvatarUrl parameter is missing");
        }
    }

    /**
     *
     * Validates if User object is valid to be sent through Identity Update User request.
     * Required User properties are: userId, companyId, username, firstname, lastname
     *
     * @param user - User object to be validated
     *
     * @throws IntelligenceInvalidParameterException
     * */
    private void isValidUserObjectToUpdate (User user) throws IntelligenceInvalidParameterException
    {
        if(user.getUserId() == null) {
            throw new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.ErrorCode.MissingParameter, "UserId parameter is missing");
        }

        if(user.getCompanyId() == null) {
            throw new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.ErrorCode.MissingParameter, "CompanyId parameter is missing");
        }
//      API v2 - disabling username update
//        if(user.getUsername() == null || user.getUsername().equalsIgnoreCase("")) {
//            throw new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.MissingParameter, "Username parameter is missing");
//        }

        if(user.getFirstName() == null) {
            throw new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.ErrorCode.MissingParameter, "First name parameter is missing");
        }

        if(user.getLastName() == null) {
            throw new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.ErrorCode.MissingParameter, "Last name parameter is missing");
        }
    }

    /**
     * Create register identifier request
     *
     * @param authenticationToken authentication token object
     * @param identifier identifier to register
     * @return request object for create identifier
     * @throws IntelligenceException, when parameter in URL is missing
     */
    Request createCreateIdentifierRequest(@NonNull AuthenticationToken authenticationToken, @NonNull Identifier identifier) throws IntelligenceException {

        URL url = mRequestURLBuilder.identityBaseURL().urlPath(ENDPOINT_CREATE_IDENTIFIER).url();

        JSONArray identifierJSON = new JSONArray();
        identifierJSON.put(identifier.toJSONObject());

        return RequestBuilder.POST(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(authenticationToken)
                .body(identifierJSON)
                .build();

    }

    /**
     * Create unregister identifier request
     *
     * @param authenticationToken authentication token object
     * @param identifier identifier to be deleted
     * @return request object for delete identifier
     * @throws IntelligenceException, when parameter in URL is missing
     */
    Request createDeleteIdentifierRequest(@NonNull AuthenticationToken authenticationToken, @NonNull Identifier identifier) throws IntelligenceException {

        URL url = mRequestURLBuilder.identityBaseURL().urlPath(ENDPOINT_DELETE_IDENTIFIER)
                .addParam("USER_ID", String.valueOf(identifier.getUserID()))
                .addParam("IDENTIFIER_ID", String.valueOf(identifier.getId()))
                .url();

        return RequestBuilder.DELETE(url)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(authenticationToken)
                .build();

    }


    /**
     * Create unregister identifier (On behalf) request
     *
     * @param authenticationToken authentication token object
     * @param identifier identifier to be deleted on behalf
     * @return request object for delete identifier on behalf
     * @throws IntelligenceException, when parameter in URL is missing
     */
    Request createDeleteIdentifierOnBehalfRequest(@NonNull AuthenticationToken authenticationToken, @NonNull Identifier identifier) throws IntelligenceException {

        URL url = mRequestURLBuilder.identityBaseURL().urlPath(ENDPOINT_DELETE_IDENTIFIER_ON_BEHALF).url();

        Uri uri= Uri.parse(url.toExternalForm())
                .buildUpon()
                .appendQueryParameter(Installation.KEY_APPLICATION_ID, mConfiguration.getApplicationID().toString())
                .appendQueryParameter("identifierValue", identifier.getValue())
                .appendQueryParameter(Identifier.KEY_IDENTIFIER_TYPE, identifier.getIdentifierType().value.toString())
                .build();

        URL queryUrl = null;
        try {
            queryUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return RequestBuilder.DELETE(queryUrl)
                .accept(Constants.CONTENT_TYPE_JSON)
                .authentication(authenticationToken)
                .build();

    }

}
