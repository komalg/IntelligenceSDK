package com.tigerspike.intelligence;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

class SharedPreferencesDataStore extends DataStore {

    static final String SHARED_PREFERENCES_KEY = "SP_INTELLIGENCE_SDK";

    private SharedPreferences mPreferences;

    SharedPreferencesDataStore(Context context) {
        mPreferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    @Override
    String get(String key, String defaultValue) {
        return mPreferences.getString(key, defaultValue);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    void set(String key, String value, boolean commit) {
        if (commit) {
            mPreferences.edit().putString(key, value).commit();
        } else {
            mPreferences.edit().putString(key, value).apply();
        }
    }

    @Override
    void setStringSet(String key, Set<String> values) {
        mPreferences.edit().putStringSet(key, values).apply();
    }

    @Override
    Set<String> getStringSet(String key, Set<String> defaultValues) {
        return mPreferences.getStringSet(key, defaultValues);
    }

    // Remove

    @SuppressLint("CommitPrefEdits")
    @Override
    void remove(String key, boolean commit) {
        SharedPreferences.Editor editor = mPreferences.edit().remove(key);
        if ( commit ) {
            editor.commit();
        }
        else {
            editor.apply();
        }
    }

}
