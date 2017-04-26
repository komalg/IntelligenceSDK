package com.tigerspike.intelligence;

/* Environment.java - Intelligence SDK
*
* Defines environments available on the Intelligence platform.
*
*/
public enum Environment {

    // Defines the  modules for the Intelligence platform API
    // The values that should be used are "local", "development", "integration", "uat", "staging" and "production"
    /// Production Environment
    Production("production"),
    // UAT Environment
    UAT("uat"),
    // Local Environment
    Local("local"),
    // Development Environment
    Development("development"),
    // Integration Environment
    Integration("integration"),
    // Staging Environment
    Staging("staging");


    private final String name;

    Environment(String envName) {
        name = envName;
    }

    public static Environment parseString(String value) {

        for (Environment env : Environment.values()) {
            if (env.name.equalsIgnoreCase(value)) {
                return env;
            }
        }

        return null;
    }

    public String getEnvironmentName() {
        return this.name;
    }
}
