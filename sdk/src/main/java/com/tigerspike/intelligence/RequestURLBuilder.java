package com.tigerspike.intelligence;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tigerspike.intelligence.exceptions.IntelligenceInvalidParameterException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

final class RequestURLBuilder {
    private HashMap<String, String> mUrlParameters;
    private String mCurrentUrlScheme;
    private Environment mEnvironment;
    private Region mRegion;

    private static final String VERSION_PARAM = "VERSION";
    private static final String PROJECT_ID_PARAM = "PROJECT_ID";
    private static final String APP_ID_PARAM = "APPLICATION_ID";
    private static final String COMPANY_ID_PARAM = "COMPANY_ID";

    /**
     * Public constructor, returns RequestURLBuilder instance that lets construct any URL based on baseUrl, urlPath provided and added parameters by .addParam method
     *
     * @param configuration - Intelligence configuration object.
     * @throws IntelligenceInvalidParameterException
     */
    RequestURLBuilder(@NonNull Configuration configuration) {
        mUrlParameters = new HashMap<>();
        mUrlParameters.put(VERSION_PARAM, Constants.API_VERSION_PARAM);

        if (configuration.getEnvironment() != null) {
            mEnvironment = configuration.getEnvironment();
        }
        if (configuration.getRegion() != null) {
            mRegion = configuration.getRegion();
        }
        if (configuration.getProjectID() != null) {
            mUrlParameters.put(PROJECT_ID_PARAM, configuration.getProjectID().toString());
        }
        if (configuration.getApplicationID() != null) {
            mUrlParameters.put(APP_ID_PARAM, configuration.getApplicationID().toString());
        }
        if (configuration.getCompanyID() != null) {
            mUrlParameters.put(COMPANY_ID_PARAM, configuration.getCompanyID().toString());
        }
    }

    /**
     * Allows user to append an urlPath to baseUrl, which is base object to construct URL from it.
     *
     * @return returns RequestURLBuilder instance that lets construct any URL based on baseUrl, urlPath provided and added parameters by .addParam method
     * @throws IntelligenceInvalidParameterException
     */
    RequestURLBuilder urlPath(String urlPath) {
        mCurrentUrlScheme += urlPath;
        return this;
    }

    /**
     * @return Identity base url
     */
    public RequestURLBuilder identityBaseURL() {
        mCurrentUrlScheme = baseURLForModule(Module.Identity);
        return this;
    }

    /**
     * @return Authentication base url
     */
    public RequestURLBuilder authenticationBaseURL() {
        mCurrentUrlScheme = baseURLForModule(Module.Authentication);
        return this;
    }

    /**
     * @return Analytics base url
     */
    public RequestURLBuilder analyticsBaseURL() {
        mCurrentUrlScheme = baseURLForModule(Module.Analytics);
        return this;
    }

    /**
     * @return Location base url
     */
    public RequestURLBuilder locationBaseURL() {
        mCurrentUrlScheme = baseURLForModule(Module.Location);
        return this;
    }

    /**
     * Prepare baseUrl for module
     *
     * @param Module module
     * Url is created based on module, environment, region and API version.
     * Url structure : https://MODULE.api.ENV.phoenixplatform.REGION/VERSION.
     * i.e. https://authetication.api.uat.phoenixplatform.eu/v2/token
     * @return String baseUrl for module
     */
    private String baseURLForModule(Module module) {
        String baseUrl = "https://";
        if (module.toString().length() > 0) {
            baseUrl += module.toString();
        }

        if (mapEnvironmentIntoString(mEnvironment) != null) {
            baseUrl += mapEnvironmentIntoString(mEnvironment).length() > 0 ? "-" : "";
            baseUrl += mapEnvironmentIntoString(mEnvironment);
        }

        baseUrl += ".phoenixplatform.";

        if (mRegion.getExtension().length() > 0) {
            baseUrl += mRegion.getExtension();
        }

        baseUrl += "/" + Constants.API_VERSION_PARAM + "/";

        return baseUrl;
    }

    /**
     * @return returns an URL object build based on urlPath and urlParams
     * @throws IntelligenceInvalidParameterException
     */
    URL url() throws IntelligenceInvalidParameterException {
        return constructUrl(mCurrentUrlScheme, mUrlParameters);
    }

    /**
     * This method can construct any URL object if few conditions are fulfilled
     *
     * @param urlScheme - a String that contains URL format in which parameters are enclosed into braces,
     *                  example - identity.api.phoenixplatform.com/v2/companies/{COMPANY_ID}/users"
     * @param urlParams - a nullable HashMap that contains values related to keys, which are included in URLScheme
     *                  If this parameter is null, URLScheme is used as plain URL address
     * @return URL object constructed with combination of baseUrl, urlPath and urlParams
     * @throws IntelligenceInvalidParameterException
     */
    private URL constructUrl(String urlScheme, @Nullable HashMap<String, String> urlParams)
            throws IntelligenceInvalidParameterException {
        if (urlParams == null) {
            URL url = null;
            try {
                url = new URL(urlScheme);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return url;
        }

        String[] URLComponents = urlScheme.split("/");
        String newUrlString = "";
        for (String component : URLComponents) {
            //If there is at least more than one component, put divider at the end of String
            if (!newUrlString.equalsIgnoreCase("")) {
                newUrlString += "/";
            }

            String regEx = "\\{([^}]*.?)\\}";
            if (component.matches(regEx)) {
                String componentKey = component.substring(1, component.length() - 1);
                String parameter = urlParams.get(componentKey);

                if (parameter != null) {
                    newUrlString += parameter;
                } else {
                    throw (new IntelligenceInvalidParameterException(IntelligenceInvalidParameterException.ErrorCode.MissingParameter, "Parameter (" + componentKey + ") not found in parameters collection"));
                }
            } else {
                newUrlString += component;
            }
        }

        URL buildURL = null;
        try {
            buildURL = new URL(newUrlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return buildURL;
    }

    /**
     * Allows user to put any key-value pair as parameter included in urlPath (provided in static constructor)
     *
     * @return returns IntelligenceURLBuilder instance that lets construct any URL based on urlPath provided and added parameters by .addParam method
     * @throws IntelligenceInvalidParameterException
     */
    RequestURLBuilder addParam(String key, String value) {
        //If key is already there, value will be updated
        mUrlParameters.put(key, value);
        return this;
    }

    /**
     * Map Environment object into String
     * @param environment - environment object to map
     * @return returns String object that lets construct base URL for particular module
     */
    private String mapEnvironmentIntoString(Environment environment) {
        switch (environment) {
            case Production:
                return "";
            case UAT:
                return "uat";
            case Staging:
                return "staging";
            case Integration:
                return "int";
            case Development:
                return "dev";
            case Local:
                return "local";
            default:
                return null;
        }
    }
}

