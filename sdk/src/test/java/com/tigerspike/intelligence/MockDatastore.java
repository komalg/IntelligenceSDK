package com.tigerspike.intelligence;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by joseprodriguez on 14/10/15.
 */
public class MockDatastore extends DataStore {

    private HashMap<String,String> underlyingData = new HashMap<>();

    @Override
    void setStringSet(String key, Set<String> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    Set<String> getStringSet(String key, Set<String> defaultValues) {
        throw new UnsupportedOperationException();
    }

    @Override
    void set(String key, String value, boolean commit) {
        underlyingData.put(key,value);
    }

    @Override
    void remove(String key, boolean commit) {
        underlyingData.remove(key);
    }

    @Override
    String get(String key, String defaultValue) {
        String string = underlyingData.get(key);
        return (string == null) ? defaultValue : string;
    }
}
