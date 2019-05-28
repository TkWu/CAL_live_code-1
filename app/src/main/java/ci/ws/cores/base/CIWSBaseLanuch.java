package ci.ws.cores.base;

import ci.function.Core.SLog;

import android.webkit.URLUtil;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import ci.ws.cores.CIResponseCallback;
import ci.ws.cores.CICertificateManager;
import ci.ws.cores.object.CIRequest;
import ci.ws.cores.object.CIResponse;

/**
 * Created by Ryan on 16/3/27.
 */
public abstract class CIWSBaseLanuch {

    private final String TAG = getClass().getSimpleName();

    private CIRequest m_request           = null;
    private CIResponseCallback m_callback          = null;
    private int         m_iRespCode         = 0;
    private String      m_strRespBody       = null;
    private CIResponse m_response          = null;

    public CIWSBaseLanuch(){}

    public void connection(final CIRequest request, CIResponseCallback callback) {
        this.m_request = request;
        this.m_callback = callback;

        SendRequest(request);
    }

    protected Void SendRequest(CIRequest requests) {
        Exception exception = null;
        try {
            if (URLUtil.isHttpsUrl(requests.url())) {
                httpsConnection(requests);
            } else {
                httpConnection(requests);
            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | ClassCastException e) {
           SLog.e(TAG, "==================================================================");
           SLog.e(TAG, "==================================================================");
            exception = e;
            e.printStackTrace();
        }

        if (exception == null) {
            if (m_response.code() >= HttpURLConnection.HTTP_MULT_CHOICE) {
                m_callback.onError( m_response, m_iRespCode, new ConnectException("Http state:" + m_response.code()) );
            } else {
                m_callback.onSuccess(m_strRespBody, m_iRespCode);
            }
        } else {
            m_callback.onError(m_response, m_iRespCode, exception);
        }
        return null;
    }

    private void httpsConnection(CIRequest request) throws IOException, NoSuchAlgorithmException, KeyManagementException, ClassCastException {
        HttpsURLConnection httpsConn;

        //setting
        URL targetUrl = new URL(request.url());
        httpsConn = (HttpsURLConnection) targetUrl.openConnection();

        HostnameVerifier allHostVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
               SLog.i("GC", "HostnameVerifier.verify:" + hostname);
                return true;
            }
        };
        httpsConn.setHostnameVerifier(allHostVerifier);

        httpsConn.setSSLSocketFactory(CICertificateManager.getSSLContext().getSocketFactory());
        connection(httpsConn, request);
    }

    private void httpConnection(CIRequest request) throws IOException, NoSuchAlgorithmException, KeyManagementException, ClassCastException {
        HttpURLConnection httpConn;
        //setting
        URL targetUrl = new URL(request.url());
        httpConn = (HttpURLConnection) targetUrl.openConnection();
        connection(httpConn, request);
    }

    private void connection(HttpURLConnection httpConn, CIRequest request) throws IOException, NoSuchAlgorithmException, KeyManagementException, ClassCastException {
       SLog.d(TAG, "[WS] request:" + new Gson().toJson(request));
        httpConn.setConnectTimeout(getConnectTimeout());
        httpConn.setReadTimeout(getReadTimeout());
        if (request.method() != null) {
            httpConn.setRequestMethod(request.method().name());
        }

        httpConn.setUseCaches(false);

        //add default headers
        if (null != getDefaultHeaders()) {
            for (String key : getDefaultHeaders().keySet()) {
                httpConn.addRequestProperty(key, getDefaultHeaders().get(key));
            }
        }

        //Override headers
        if (null != request.headers()) {
            for (String key : request.headers().keySet()) {
                httpConn.setRequestProperty(key, request.headers().get(key));
            }
        }

        //output
        outputStream(httpConn, request.body());

        //input
        inputStream(httpConn);

        m_response = new CIResponse(
                m_request.url(),
                m_request.method(),
                httpConn.getHeaderFields(),
                m_strRespBody,
                m_iRespCode);
    }

    private void outputStream(HttpURLConnection httpConn, String requestData) throws IOException {
        //GET 不將資料寫入body
        if (null != requestData &&
                false == httpConn.getRequestMethod().equalsIgnoreCase("GET")) {
            httpConn.setDoInput(true);
            final String strCharset = "UTF-8";
            OutputStream outputStream;
            OutputStreamWriter outputStreamWriter;
            outputStream = httpConn.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream, strCharset);
            outputStreamWriter.write(requestData);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }
    }

    private void inputStream(HttpURLConnection httpConn) throws IOException {
        m_iRespCode = httpConn.getResponseCode();
        StringBuilder sbBody = new StringBuilder();

        InputStream inputStream;
        try {
            inputStream = httpConn.getInputStream();
        } catch (IOException e) {
            inputStream = httpConn.getErrorStream();
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        BufferedReader reader = new BufferedReader(inputStreamReader);
        int count;
        char[] buffer = new char[1024];
        while (true) {
            count = reader.read(buffer);
            if (-1 == count) {
                break;
            }
            sbBody.append(buffer, 0, count);
        }
        reader.close();
        httpConn.disconnect();
        m_strRespBody = sbBody.toString();
    }


    protected abstract int getConnectTimeout();

    protected abstract int getReadTimeout();

    protected abstract Map<String, String> getDefaultHeaders();

}
