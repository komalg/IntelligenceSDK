package com.tigerspike.intelligence;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.tigerspike.intelligence.exceptions.IntelligenceConfigurationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/* Configuration.java - Intelligence SDK
*
* This class holds all configuration parameters.
* It provides helper function to parse a JSON file for configuration parameters.
*
*/

final public class Configuration {

    private HashMap<ConfigurationProperty, Object> mParameters;

    /**
     * Creates a copy of the given configuration object.
     *
     * @param configuration object to copy.
     */
    public Configuration(Configuration configuration) {
        this();
        for (ConfigurationProperty configurationProperty : configuration.mParameters.keySet()) {
            mParameters.put(configurationProperty, configuration.mParameters.get(configurationProperty));
        }
    }

    /**
     * Creates an empty configuration object.
     */
    public Configuration() {
        mParameters = new HashMap<>();
    }

    /**
     * Returns whether all required properties are set.
     *
     * @return whether all required properties are set.
     */
    public boolean hasMissingProperty() {

        for (ConfigurationProperty param : ConfigurationProperty.values()) {
            if (param.isRequired() && !mParameters.containsKey(param)) {
                return true;
            }
        }

        return false;

    }

    /**
     * Returns a configuration object filled with the properties set in the JSON asset file.
     *
     * @param context
     * @param jsonFileName path to json file in the app's assets folder.
     * @return configuration object filled with the properties set in the JSON asset file
     * @throws IntelligenceConfigurationException
     */
    public static Configuration createFromJSON(Context context, String jsonFileName) throws IntelligenceConfigurationException {
        Configuration configuration = new Configuration();
        configuration.loadFromJSON(context, jsonFileName);
        return configuration;
    }

    /**
     * Reads the configuration parameters from a .json file from the apps assets folder.
     *
     * @param context
     * @param jsonFileName path to json file in the app's assets folder.
     * @throws IntelligenceConfigurationException
     */
    public void loadFromJSON(Context context, String jsonFileName) throws IntelligenceConfigurationException {

        JsonObject jsonObject;
        InputStream inputStream = null;

        try {
            inputStream = context.getAssets().open(jsonFileName);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            jsonObject = (JsonObject) new JsonParser().parse(bufferReader);
        } catch (IOException e) {
            throw (new IntelligenceConfigurationException(IntelligenceConfigurationException.ErrorCode.FileNotFound, "Unable to load " + jsonFileName));
        } catch (JsonIOException e) {
            throw (new IntelligenceConfigurationException(IntelligenceConfigurationException.ErrorCode.InvalidFile, "Unable to read " + jsonFileName));
        } catch (JsonSyntaxException e) {
            throw (new IntelligenceConfigurationException(IntelligenceConfigurationException.ErrorCode.InvalidFile, "Unable to parse " + jsonFileName));
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                // Nothing much we can do here.
            }
        }

        for (ConfigurationProperty configurationProperty : ConfigurationProperty.values()) {

            String value;
            try {
                 value = jsonObject.get(configurationProperty.key()).getAsString();
            } catch(Exception e) {
                if(!configurationProperty.isRequired()) {
                    parseParameter(configurationProperty, null);
                    continue;
                }
                throw (new IntelligenceConfigurationException(IntelligenceConfigurationException.ErrorCode.InvalidProperty, "Invalid property value"));
            }

            parseParameter(configurationProperty, value);

        }

    }

    private void parseParameter(ConfigurationProperty param, String value) throws IntelligenceConfigurationException {

        switch (param.type()) {
            case string:
                mParameters.put(param, value);
                break;
            case integer:
                try {
                    int intValue = Integer.parseInt(value);
                    mParameters.put(param, intValue);
                } catch (NumberFormatException e) {
                    throw(new IntelligenceConfigurationException(IntelligenceConfigurationException.ErrorCode.InvalidProperty, param.key() + " is not an integer"));
                }
                break;
            case region:
                Region regionValue = Region.parseString(value);
                if (regionValue == null) {
                    throw (new IntelligenceConfigurationException(IntelligenceConfigurationException.ErrorCode.InvalidProperty, param.key() + " is not a valid region value"));
                }
                mParameters.put(param, regionValue);
                break;
            case environment:
                Environment envValue = Environment.parseString(value);
                if (envValue == null) {
                    throw (new IntelligenceConfigurationException(IntelligenceConfigurationException.ErrorCode.InvalidProperty, param.key() + " is not a valid region value"));
                }
                mParameters.put(param, envValue);
                break;
            case certificate_trust_policy:
                CertificateTrustPolicy certificate = CertificateTrustPolicy.parseString(value);
                if (certificate == null) {
                    throw (new IntelligenceConfigurationException(IntelligenceConfigurationException.ErrorCode.InvalidProperty, param.key() + " is not a valid certificate value"));
                }
                mParameters.put(param, certificate);
                break;
            case bool:
                mParameters.put(param, value);
                break;
        }

    }

    /**
     * Sets sdk user role
     *
     * @param roleId integer of the sdk user role id
     */
    public void setSDKUserRole(@NonNull Integer roleId) {
        mParameters.put(ConfigurationProperty.SDK_USER_ROLE, roleId);
    }

    /**
     * Returns the SDK User role
     *
     * @return SDK user role
     */
    public @Nullable Integer getSDKUserRole() {
        return (Integer) mParameters.get(ConfigurationProperty.SDK_USER_ROLE);
    }

    /**
     * Sets region
     *
     * @param region region to set
     */
    public void setRegion(@NonNull Region region) {
        mParameters.put(ConfigurationProperty.REGION, region);
    }

    /**
     * Returns region
     *
     * @return region
     */
    public @Nullable Region getRegion() {
        return (Region) mParameters.get(ConfigurationProperty.REGION);
    }

    /**
     * Sets environment
     *
     * @param env region to set
     */
    public void setEnvironment(@NonNull Environment env) {
        mParameters.put(ConfigurationProperty.ENVIRONMENT, env);
    }

    /**
     * Returns certificate trust policy
     *
     * @return certificate_trust_policy
     */
    public @Nullable CertificateTrustPolicy getCertificateTrustPolicy() {
        return (CertificateTrustPolicy) mParameters.get(ConfigurationProperty.CERTIFICATE_TRUST_POLICY);
    }

    /**
     * Sets certificate trust policy
     *
     * @param certificate_trust_policy certificate to set
     */
    public void setCertificateTrustPolicy(@NonNull CertificateTrustPolicy cert) {
        mParameters.put(ConfigurationProperty.CERTIFICATE_TRUST_POLICY, cert);
    }

    /**
     * Returns environment
     *
     * @return environment
     */
    public @Nullable Environment getEnvironment() {
        return (Environment) mParameters.get(ConfigurationProperty.ENVIRONMENT);
    }

    /**
     * Sets client id
     *
     * @param clientId client id to set
     */
    public void setClientID(@NonNull String clientId) {
        mParameters.put(ConfigurationProperty.CLIENT_ID, clientId);
    }

    /**
     * Return client id.
     *
     * @return client id.
     */
    public @Nullable String getClientID() {
        return (String) mParameters.get(ConfigurationProperty.CLIENT_ID);
    }

    /**
     * Sets client secret.
     *
     * @param clientSecret client secret to set
     */
    public void setClientSecret(@NonNull String clientSecret) {
        mParameters.put(ConfigurationProperty.CLIENT_SECRET, clientSecret);
    }

    /**
     * Returns client secret
     *
     * @return client secret
     */
    public @Nullable String getClientSecret() {
        return (String) mParameters.get(ConfigurationProperty.CLIENT_SECRET);
    }

    /**
     * Sets application id.
     *
     * @param applicationID application id to set
     */
    public void setApplicationID(@NonNull Integer applicationID) {
        mParameters.put(ConfigurationProperty.APPLICATION_ID, applicationID);
    }

    /**
     * Returns application id.
     *
     * @return application id
     */
    public @Nullable Integer getApplicationID() {
        return (Integer) mParameters.get(ConfigurationProperty.APPLICATION_ID);
    }

    /**
     * Sets project id.
     *
     * @param projectID project id
     */
    public void setProjectID(@NonNull Integer projectID) {
        mParameters.put(ConfigurationProperty.PROJECT_ID, projectID);
    }

    /**
     * Returns project id
     *
     * @return project id
     */
    public @Nullable Integer getProjectID() {
        return (Integer) mParameters.get(ConfigurationProperty.PROJECT_ID);
    }

    /**
     * Sets company id.
     *
     * @param companyID company id
     */
    public void setCompanyID(@NonNull Integer companyID) {
        mParameters.put(ConfigurationProperty.COMPANY_ID, companyID);
    }

    /**
     * Return company id.
     *
     * @return company id.
     */
    public @Nullable Integer getCompanyID() {
        return (Integer) mParameters.get(ConfigurationProperty.COMPANY_ID);
    }

    /**
     * Sets use geofences flag
     *
     * @param useGeofences boolean flag on using geofences
     * */
    public void setUseGeofences(@NonNull Boolean useGeofences) {
        mParameters.put(ConfigurationProperty.USE_GEOFENCES, useGeofences);
    }

    /**
     * Return use geofences flag
     * If no value is specified, the default is true
     *
     * @return use geofences
     * */
    public Boolean getUseGeofences() {

        Object useGeofenceProperty = mParameters.get(ConfigurationProperty.USE_GEOFENCES);

        if(useGeofenceProperty != null && !useGeofenceProperty.toString().equalsIgnoreCase("")) {
            return Boolean.valueOf((String)useGeofenceProperty);
        }

        return true;
    }
}
