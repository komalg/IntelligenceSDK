package mock;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.tigerspike.intelligence.Installation;

import java.util.Map;
import java.util.Set;

/**
 * Created by markvanrees on 14/08/15.
 */
public class mockSharedPreferences implements SharedPreferences {

    private Installation mInstallation;

    public mockSharedPreferences(Context context) {
        mInstallation = new Installation(context);
    }

    @Override
    public Map<String, ?> getAll() {
        return null;
    }

    @Nullable
    @Override
    public String getString(String key, String defValue) {
        if (key.equals("IDENTITY_INSTALLATION")) {
            return mInstallation.toJSONString();
        }
        return null;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return null;
    }

    @Override
    public int getInt(String key, int defValue) {
        return 0;
    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return false;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return new Editor() {
            @Override
            public Editor putString(String key, String value) {
                return this;
            }

            @Override
            public Editor putStringSet(String key, Set<String> values) {
                return this;
            }

            @Override
            public Editor putInt(String key, int value) {
                return this;
            }

            @Override
            public Editor putLong(String key, long value) {
                return this;
            }

            @Override
            public Editor putFloat(String key, float value) {
                return this;
            }

            @Override
            public Editor putBoolean(String key, boolean value) {
                return null;
            }

            @Override
            public Editor remove(String key) {
                return this;
            }

            @Override
            public Editor clear() {
                return this;
            }

            @Override
            public boolean commit() {
                return false;
            }

            @Override
            public void apply() {

            }
        };

    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}
