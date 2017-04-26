package com.tigerspike.intelligence;

/* CertificateTrustPolicy.java - Intelligence SDK
*
* This enum represents the certificate trust policy to apply when the Intelligence SDK connects to the server.
* Certificate validity is defined by Android and not by the SDK.
* When receiving a certificate challenge from Android, the SDK will apply the selected policy.
*
*/
public enum CertificateTrustPolicy {

    /// Trust only certificates that are considered valid by Android. This is the default value
    Valid("valid"),
    /// Trust any certificate, independently of Android considering it valid or invalid
    Any("any"),
    /// Trust only non-production certificates, which implies that the certificates in the production
    // server will need to be considered valid by Android and any other will be trusted
    AnyNonProduction("any_non_production");

    private final String name;

    CertificateTrustPolicy(String envName) {
        name = envName;
    }

    // This method should be used to extract certificate_trust_policy from a configuration file
    // (if it exists) and turn it into an enum value
    public static CertificateTrustPolicy parseString(String value) {

        for (CertificateTrustPolicy env : CertificateTrustPolicy.values()) {
            if (env.name.equalsIgnoreCase(value)) {
                return env;
            }
        }

        return null;
    }

    public String getCertificateName() {
        return this.name;
    }
}
