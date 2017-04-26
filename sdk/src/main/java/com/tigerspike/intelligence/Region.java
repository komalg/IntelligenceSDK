package com.tigerspike.intelligence;

/* Region.java - Intelligence SDK
*
* Defines regions available on the Intelligence platform.
*
*/
public enum Region {

    // Defines the 4 regions for the Intelligence platform, their configuration key name and region url extensions.
    Europe("EU","eu"),
    UnitedStates("US","com"),
    Singapore("SG","com.sg"),
    Australia("AU","com.au");

    String key;
    String url;

    Region(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public static Region parseString(String value) {

        for (Region region : Region.values()) {
            if (region.key.equalsIgnoreCase(value)) {
                return region;
            }
        }

        return null;

    }

    public String getExtension() {
        return url;
    }

    public String key() {
        return key;
    }

}