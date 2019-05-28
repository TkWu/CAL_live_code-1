package ci.ws.cores;

import android.content.res.AssetManager;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;

/**
 * Created by kevincheng on 2017/6/20.
 */

public class CICertificateManager {
    //憑證名稱不能包含 * 字元
    public static final String CAL_CERT = "certificate/china-airlines.com.cer";
    public static final String CAL_BOOKING_CERT = "certificate/www.china-airlines.com.cer";
    public static final String GOOGLE_CERT = "certificate/GIAG3.cer";
    public static final String CAL_AI_CERT = "certificate/CALAICS01TCHINA-AIRLINESCOM.der";

    public static SSLContext getSSLContext(){
        // Create the SSL connection
        SSLContext sc = null;
        try{
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            AssetManager assetManager = CIApplication.getContext().getAssets();
            InputStream caInputCAL = assetManager.open(CAL_CERT);
            InputStream caInputCALBooking = assetManager.open(CAL_BOOKING_CERT);
            InputStream caInputGoogle = assetManager.open(GOOGLE_CERT);
            InputStream caInputCAL_AI = assetManager.open(CAL_AI_CERT);
            Certificate certCAL,certCALBooking,certGoogle,calAI;
            try {
                certCAL     = cf.generateCertificate(caInputCAL);
                certGoogle  = cf.generateCertificate(caInputGoogle);
                certCALBooking  = cf.generateCertificate(caInputCALBooking);
                calAI       = cf.generateCertificate(caInputCAL_AI);
                System.out.println("certCAL=" + ((X509Certificate) certCAL).getSubjectDN());
            } finally {
                caInputCAL.close();
                caInputGoogle.close();
                caInputCALBooking.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("cal", certCAL);
            keyStore.setCertificateEntry("cal_booking", certCALBooking);
            keyStore.setCertificateEntry("google", certGoogle);
            keyStore.setCertificateEntry("cal_ai", calAI);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            sc = SSLContext.getInstance("TLS");

            sc.init(null, tmf.getTrustManagers(), null);
        }catch (Exception e){
            SLog.d("ca", e.getMessage());
        }
        return sc;
    }
}
