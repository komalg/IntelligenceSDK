package com.tigerspike.intelligence;

/**
 * Created by marcinowoc on 25/01/16.
 */

/* Module.java - Intelligence SDK
*
* Defines modules available on the Intelligence platform.
*
*/
public enum Module {

    // Defines the 4 modules for the Intelligence platform API
    Authentication("authentication"),
    Identity("identity"),
    Analytics("analytics"),
    Location("location");

    private final String name;

    Module(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}
