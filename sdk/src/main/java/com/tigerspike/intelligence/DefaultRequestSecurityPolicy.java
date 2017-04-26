package com.tigerspike.intelligence;

import android.net.SSLCertificateSocketFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by marcinowoc on 16/02/16.
 *
 * Implementation class of RequestSecurityPolicy interface.
 * It provides implementation of RequestSecurityPolicy public interface.
 */
public class DefaultRequestSecurityPolicy implements RequestSecurityPolicy {

    private CertificateTrustPolicy mCertificateTrustPolicy;
    private Environment mEnvironment;

    DefaultRequestSecurityPolicy(CertificateTrustPolicy certificateTrustPolicy, Environment environment){
        mCertificateTrustPolicy = certificateTrustPolicy;
        mEnvironment = environment;
    }
    @Override
    public void applySecurityPolicy(HttpsURLConnection connection) {
        if( mCertificateTrustPolicy.equals(CertificateTrustPolicy.Any)
                || mCertificateTrustPolicy.equals(CertificateTrustPolicy.AnyNonProduction)
                || (mCertificateTrustPolicy.equals(CertificateTrustPolicy.AnyNonProduction) && mEnvironment.equals(Environment.Production))) {

            connection.setHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }

            });
            connection.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
        }
    }
}
