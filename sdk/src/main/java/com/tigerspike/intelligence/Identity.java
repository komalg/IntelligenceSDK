package com.tigerspike.intelligence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tigerspike.intelligence.exceptions.IntelligenceException;
import com.tigerspike.intelligence.exceptions.IntelligenceInvalidParameterException;

/**
 * @Class Identity
 *
 * Interface definition for managing Intelligence User Identity Data.
 * Can be used to create, get or update User data.
 * Use related callbacks interface definition to make further data/error processing.
 */
public interface Identity
{
    /**
     * Returns user object if logged in.
     *
     * @return user object
     */
    User getUser();

    /**
     * Clear current user authentication tokens.
     *
     * @param onLogoutListener Listener triggered after logout
     */
    void logout(@Nullable OnLogoutListener onLogoutListener);

    /**
     * Tries to login with username and password.
     *
     * @param username Users username
     * @param password Users password
     * @param onLoginListener Listener for login results.
     */
    void login(@NonNull String username, @NonNull String password, @Nullable final OnLoginListener onLoginListener);

    /**
     * Convenience method to register a GcmToken
     *
     * @param GcmToken
     */
    void registerGCMToken(String GcmToken, @Nullable OnRegisterIdentifierListener onRegisterIdentifierListener);

    /**
     * Registers the specified Identifier to the current logged in user.
     *
     * @param identifier Identifier to register
     */
    void registerIdentifier(Identifier identifier, @Nullable OnRegisterIdentifierListener onRegisterIdentifierListener);

    /**
     * Unregisters the specified Identifier.
     *
     * @param identifier to unregister
     */
    void unregisterIdentifier(Identifier identifier, @Nullable OnUnregisterIdentifierListener onUnregisterIdentifierListener);

    /**
     * Deletes the specified identifier from all users.
     *
     * @param identifier to delete
     */
    void deleteIdentifierOnBehalf(Identifier identifier, @Nullable OnUnregisterIdentifierOnBehalfListener onUnregisterIdentifierOnBehalfListener);


    /**
     * Creates user from provided user object, uses callback to report errors or success
     * Throws a IntelligenceInvalidParameterException if userObject is invalid.
     *
     * @param userObject - object that contains data to use to create new Intelligence User
     *
     * @param onCreateUserListener - OnCreateUserListener in which further process of User object can be proceed
     * @throws IntelligenceInvalidParameterException
     * */
    void createUser(@NonNull User userObject, OnCreateUserListener onCreateUserListener) throws IntelligenceInvalidParameterException;


    /**
     * Sends GET User(Me) request
     *
     * @param onGetUserListener - OnGetUserListener in which further process of User object can be proceed
     *
     *
     * */
    void getMe(OnGetUserListener onGetUserListener);

    /**
     *
     * Sends assign role request
     *
     * @param userId - int user id for which role need to be assigned
     * @param roleId - int id of role that will be assigned
     * @param onAssignRoleListener - OnAssignRoleListener to inform user when role has been assigned
     *
     * */
    void assignRole(int userId, int roleId, @Nullable final OnAssignRoleListener onAssignRoleListener);

    /**
     *
     * Sends Revoke role request
     *
     * @param userId - int user id for which role need to be revoked
     * @param roleId - int id of role that will be revoked
     * @param onRevokeRoleListener - OnRevokeRoleListener to inform user when role has been revoked
     *
     * */
    void revokeRole(int userId, int roleId, @Nullable final OnRevokeRoleListener onRevokeRoleListener);

    /**
     * Sends GET User request
     *
     * @param id - id of user to be found
     * @param onGetUserListener - OnGetUserListener in which further process of User object can be proceed
     *
     * */
    void getUserById(int id, OnGetUserListener onGetUserListener);

    /**
     *
     * Try updating user with user object. UserObject should be requested by getMe or getUser.
     * Throws a IntelligenceInvalidParameterException if userObject is invalid.
     *
     * @param userObject - object that contains data to use to update existing IntelligenceUser
     *
     * @param onUpdateUserListener - OnUpdateUserListener in which further process of User object can be proceed
     * @throws IntelligenceInvalidParameterException
     * */
    void updateUser(@NonNull User userObject, OnUpdateUserListener onUpdateUserListener)
            throws IntelligenceInvalidParameterException;

    public interface OnLoginListener  {
        void onLogin(IntelligenceException intelligenceException);
    }

    public interface OnLogoutListener {
        void onLogout(IntelligenceException intelligenceException);
    }

    interface OnRegisterIdentifierListener {
        void onRegisterIdentifier(@Nullable Identifier identifier, @Nullable IntelligenceException intelligenceException);
    }

    interface OnUnregisterIdentifierListener {
        void onUnregisterIdentifier(@Nullable Identifier identifier, @Nullable IntelligenceException intelligenceException);
    }

    interface OnUnregisterIdentifierOnBehalfListener {
        void OnUnregisterIdentifierOnBehalf(@Nullable Identifier identifier, @Nullable IntelligenceException intelligenceException);
    }

    /**
     * Listener interface definition for a callback to be invoked when a user is created.
     * */
    interface OnCreateUserListener {
        void onCreateUser(@Nullable User user, @Nullable IntelligenceException intelligenceException);
    }

    /**
     * Listener interface definition for a callback to be invoked when a user data is get.
     * */
    interface OnGetUserListener {
        void onGetUser(@Nullable User user, @Nullable IntelligenceException intelligenceException);
    }

    /**
     * Listener interface definition for a callback to be invoked when a user is updated.
     * */
    interface OnUpdateUserListener {
        void onUpdateUser(@Nullable User user, @Nullable IntelligenceException intelligenceException);
    }

    /**
     * Listener interface definition for a callback to be invoked when a role is revoked.
     * */
    interface OnRevokeRoleListener {
        void onRevokeRole(@Nullable IntelligenceException intelligenceException);
    }

    /**
     * Listener interface definition for a callback to be invoked when a role is revoked.
     * */
    interface OnAssignRoleListener {
        void onAssignRole(@Nullable IntelligenceException intelligenceException);
    }
}
