package com.tigerspike.intelligence;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

class Utils {

    public static boolean hasPermission(Context context, String permission) {
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static String createRandomString(int length) {

        String cyphers = "AB1abCD2cdEF3efGH4ghIJ5ijKL6klMN7mnOP8opQR9qrST0stUV_uvWX-wxYZ.yz";
        String result = "";

        Random r = new Random();
        int max = cyphers.length() - 1;
        int rnd;

        for (int i = 0; i < length; i++) {
            rnd = r.nextInt(max);
            result += cyphers.substring(rnd, rnd + 1);
        }

        return result;

    }

    public static String upperFirst(String string) {

        if (isEmpty(string)) {
            return "";
        } else if (string.length() == 1) {
            return string.toUpperCase();
        } else {
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        }


    }

    public static String getInstallationId(DataStore dataStore) {
        String installationString = dataStore != null ? dataStore.get(Constants.KEY_INSTALLATION_DATA) : null;
        if (!TextUtils.isEmpty(installationString)) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(installationString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return JSONUtils.getString(jsonObject, Constants.KEY_INSTALLATION_ID, null);
        }
        return "";
    }
}
