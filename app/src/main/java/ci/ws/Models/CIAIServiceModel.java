package ci.ws.Models;

import android.os.Build;

import com.chinaairlines.mobile30.R;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.ws.cores.CICertificateManager;
import ci.ws.cores.CIResponseCallback;
import ci.ws.cores.base.CIWSBaseReqLanuch;
import ci.ws.cores.object.CIRequest;
import ci.ws.cores.object.CIResponse;
import ci.ws.cores.object.EMethod;
import ci.ws.cores.object.NoSllSocketFactory;


public class CIAIServiceModel {

    private static WebDataAsyncTask m_asynctask = null;

    public static void findData(CallBack callback,String body) {
        m_asynctask = new WebDataAsyncTask();
        String url  = CIApplication.getContext().getString(R.string.ai_service_ws_url);
        m_asynctask.connection(getRequest(url,body), getResponceCallback(callback));
    }

    public static void cancel(){
        if(null != m_asynctask){
            try {
                m_asynctask.cancel(true);
            }catch (Exception e){}
            finally {
                m_asynctask = null;
            }
        }
    }

    private static CIRequest getRequest(String url,String body) {
        return new CIRequest(url, EMethod.POST, null, body);
    }

    static class WebDataAsyncTask extends CIWSBaseReqLanuch{

        @Override
        protected void httpsConnection(CIRequest request) throws IOException, NoSuchAlgorithmException, KeyManagementException, ClassCastException {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                super.httpsConnection(request);
            } else {
                HttpsURLConnection httpsConn;
                //setting
                URL targetUrl = new URL(request.url());
                httpsConn = (HttpsURLConnection)targetUrl.openConnection();
                SSLContext sslcontext = CICertificateManager.getSSLContext();
                NoSllSocketFactory NoSLL = new NoSllSocketFactory(sslcontext.getSocketFactory(), false);
                httpsConn.setSSLSocketFactory(NoSLL);
                httpsConn.setHostnameVerifier( new NullHostNameVerifier() );
                connection(httpsConn, request);
            }
        }

        public class NullHostNameVerifier implements HostnameVerifier{
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //Log.i("RestUtilImpl", "Approving certificate for " + hostname);
                return true;
            }
        }

        @Override
        protected int getConnectTimeout() {
            return 30000;
        }

        @Override
        protected int getReadTimeout() {
            return 30000;
        }

        @Override
        protected Map<String, String> getDefaultHeaders() {

            Map<String, String> header = new HashMap<>();
            header.put("charset", "utf-8");
            header.put("Content-Type", "text/xml");

            return header;
        }
    }

    private static CIResponseCallback getResponceCallback(final CallBack listener){
        CIResponseCallback callback = new CIResponseCallback() {
            @Override
            public void onSuccess(String respBody, int code) {
                if(null != listener){
                    listener.onSuccess(respBody);
                    m_asynctask = null;
                }
            }

            @Override
            public void onError(CIResponse response, int code, Exception exception) {
                if(null != listener){
                   SLog.e("onError",exception.toString());
                    listener.onError(response, code, exception);
                    m_asynctask = null;
                }
            }
        };
        return callback;
    }

    public interface CallBack{
        void onSuccess(String respBody);
        void onError(CIResponse response, int code, Exception exception);
    }
    
}
