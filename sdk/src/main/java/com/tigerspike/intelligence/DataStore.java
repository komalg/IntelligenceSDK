package com.tigerspike.intelligence;

import java.util.Set;

public abstract class DataStore {

    // Strings.

    abstract String get(String key, String defaultValue);

    abstract void set(String key, String value, boolean commit);

    // String sets

    abstract void setStringSet(String key, Set<String> values);

    abstract Set<String> getStringSet(String key, Set<String> defaultValues);

    /**
     * Removes a value and commits if commit is true.
     * @param key the key to be removed
     * @param commit whether to commit or not.
     */
    abstract void remove(String key, boolean commit);

    /**
     * Value gets converted to string using toString and stored calling
     * set with a string as value.
     *
     * @param key the key
     * @param value the value to save
     * @param commit whether to commit the data
     * @param <T> Any object, so long as toString() can be called. Notice that
     *           when you have to be able to parse it back from the toString() representation.
     */
    final <T> void set(String key, T value, boolean commit) {
        if ( value == null ) {
            remove(key, commit);
        }
        else {
            set(key, value.toString(), commit);
        }
    }

    /**
     * Gets the string stored at the given location.
     * @param key the key
     * @return the value or null.
     */
    final String get(String key) {
        return get(key,null);
    }

    /**
     * Sets the value for the given key without committing it.
     * @param key the key
     * @param value the value.
     */
    final void set(String key, String value) {
        set(key, value, false);
    }

    /**
     * Parses the stored value into a long using Long.parseLong.
     * @param key the key
     * @param defaultValue the default value.
     * @return the value stored or the default value if none where found.
     */
    final Long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(get(key));
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Sets the value for the given key. The object passes will be converted toString() and stored like that.
     *
     * @param key
     * @param value
     * @param <T>
     */
    final <T> void set(String key, T value) {
        set(key, value, false);
    }

    /**
     * Removes the value for the given key, without committing it.
     * @param key the key to remove.
     */
    final void remove(String key) {
        remove(key, false);
    }

}
