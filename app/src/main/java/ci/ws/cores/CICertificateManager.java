package ci.ws.cores;

import android.content.res.AssetManager;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
    public static final String CAL_BOOKING_CERT_NEW = "certificate/new_booking_base64.cer";
    public static final String GOOGLE_CERT = "certificate/GIAG3.cer";
    public static final String CAL_AI_CERT = "certificate/CALAICS01CHINA-AIRLINESCOM.cer";

    private static boolean isDateNowBiggerThanUpdatetime(String cer_update_date_utc) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTimeNow = sdf.format(new Date());

        Date dt1 = null;
        Date dt2 = null;

        try {
            dt1 = sdf.parse(gmtTimeNow);
            dt2 = sdf.parse(cer_update_date_utc);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = true;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = false;
        }
        return isBigger;
    }

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
            InputStream caInputCALBooking = null;
            if(isDateNowBiggerThanUpdatetime("2019-07-25 00:00:00")){
                caInputCALBooking = assetManager.open(CAL_BOOKING_CERT_NEW);
            }else{
                caInputCALBooking = assetManager.open(CAL_BOOKING_CERT);
            }

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
