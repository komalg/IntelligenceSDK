package com.tigerspike.intelligence;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by marcinowoc on 16/02/16.
 *
 * @Class RequestSecurityPolicy
 * Interface definition for managing certificate trust policy.
 * Can be used to challenge trust level against Https connection server certificate.
 */
public interface RequestSecurityPolicy {
    /**
     * When receiving a certificate challenge from Android, the SDK will apply the selected policy for HttpsURLConnection object.
     */
    void applySecurityPolicy(HttpsURLConnection connection);
}
