package com.tigerspike.intelligence;

import org.json.JSONException;
import org.json.JSONObject;

final class JSONUtils {

    public static JSONObject parseSimpleJSONObject(String json) {

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            return new JSONObject();
        }

        return jsonObject;
    }

    public static Boolean getBoolean(JSONObject jsonObject, String name) {
        return getBoolean(jsonObject, name, null);
    }

    public static Boolean getBoolean(JSONObject jsonObject, String name, Boolean defaultValue) {

        try {
            return Boolean.valueOf(jsonObject.getString(name));
        } catch (Exception exception) {
            return defaultValue;
        }

    }

    public static Integer getInteger(JSONObject jsonObject, String name) {
        return getInteger(jsonObject, name, null);
    }

    public static Integer getInteger(JSONObject jsonObject, String name, Integer defaultValue) {

        try {
            return Integer.valueOf(jsonObject.getString(name));
        } catch (Exception exception) {
            return defaultValue;
        }

    }

    public static Double getDouble(JSONObject jsonObject, String name) {
        return getDouble(jsonObject, name, null);
    }

    public static Double getDouble(JSONObject jsonObject, String name, Double defaultValue) {

        try {
            return Double.valueOf(jsonObject.getString(name));
        } catch (Exception exception) {
            return defaultValue;
        }

    }

    public static String getString(JSONObject jsonObject, String name) {
        return getString(jsonObject, name, null);
    }

    public static String getString(JSONObject jsonObject, String name, String defaultValue) {

        try {
            return jsonObject.getString(name);
        } catch (Exception exception) {
            return defaultValue;
        }

    }

}
