package com.tigerspike.intelligence;

/* ConfigurationProperty.java - Intelligence SDK
*
* Stores the format of used configuration properties.
* Defines name, type and requisition of property.
*
*/
enum ConfigurationProperty {

    REGION                      ("region",                      ConfigurationPropertyType.region,                   true),
    ENVIRONMENT                 ("environment",                 ConfigurationPropertyType.environment,              true),
    CLIENT_ID                   ("client_id",                   ConfigurationPropertyType.string,                   true),
    CLIENT_SECRET               ("client_secret",               ConfigurationPropertyType.string,                   true),
    PROJECT_ID                  ("project_id",                  ConfigurationPropertyType.integer,                  true),
    APPLICATION_ID              ("application_id",              ConfigurationPropertyType.integer,                  true),
    COMPANY_ID                  ("company_id",                  ConfigurationPropertyType.integer,                  true),
    USE_GEOFENCES               ("use_geofences",               ConfigurationPropertyType.bool,                    false),
    CERTIFICATE_TRUST_POLICY    ("certificate_trust_policy",    ConfigurationPropertyType.certificate_trust_policy, true),
    SDK_USER_ROLE               ("sdk_user_role",               ConfigurationPropertyType.integer,                  true);

    String key;
    ConfigurationPropertyType type;
    boolean required;

    ConfigurationProperty(String key, ConfigurationPropertyType type, boolean required) {
        this.key = key;
        this.type = type;
        this.required = required;
    }

    public String key() { return key; }
    public ConfigurationPropertyType type() {
        return type;
    }
    public boolean isRequired() {
        return required;
    }

}
