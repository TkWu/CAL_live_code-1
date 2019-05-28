package ci.ws.GoogleApi;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.chinaairlines.mobile30.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ci.function.Core.CIApplication;
import ci.ws.cores.CIResponseCallback;
import ci.ws.cores.CIWSLanuch;
import ci.ws.cores.object.CIRequest;
import ci.ws.cores.object.CIResponse;
import ci.ws.cores.object.EMethod;

/**
 * Created by ryan on 16/7/26.
 * 新增Google Api 縮短網址功能
 */
public class URLShortenerApi {

    public interface URLShortenerListener {

        void Shortenerfinish( String rt_msg, String strShortURL );

        /*** 顯示進度圖示*/
        void showProgress();
        /*** 隱藏進度圖示*/
        void hideProgress();
    }

    private static final String GOOGLE_API_URL_SHORTENER = "https://www.googleapis.com/urlshortener/v1/url?key=";

    private Handler                 m_hdUIThreadhandler = null;
    private URLShortenerListener    m_Listener          = null;


    public URLShortenerApi(){

        if ( null == m_hdUIThreadhandler ){
            m_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }
    }

    public void setListener( URLShortenerListener listener ){
        m_Listener = listener;
    }

    public void getShoetenerURL( String strlongURL ){

        SendRequest(strlongURL);

        if ( null != m_Listener ) {
            m_Listener.showProgress();
        }
    }

    private String getRquestBody( String strlongURL ){

        JSONObject jsObject = null;

        try {
            jsObject = new JSONObject();

            jsObject.put( "longUrl", strlongURL);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ( null != jsObject ){
            return jsObject.toString();
        } else {
            return "";
        }
    }

    private void SendResponse( final String strMsg, final String strShortURL ){

        if ( null != m_Listener ){
            m_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    m_Listener.Shortenerfinish( strMsg , strShortURL);
                    m_Listener.hideProgress();
                }
            });
        }
    }

    private void SendRequest( final String strLongURL ){

        String strURL = GOOGLE_API_URL_SHORTENER + CIApplication.getContext().getString(R.string.google_urlshortener_api_key);
        final CIRequest request = new CIRequest(strURL, EMethod.POST, null, getRquestBody(strLongURL) );

        URLshortenerLanuch urlRequest = new URLshortenerLanuch();
        urlRequest.connection(request, new CIResponseCallback() {
            @Override
            public void onSuccess(String respBody, int code) {

                String strShortener = "";
                try{

                    JSONObject jsObj = new JSONObject(respBody);
                    if ( null != jsObj && !jsObj.isNull("id") ){
                        strShortener = jsObj.getString("id");
                    }

                } catch ( Exception e ){
                    e.printStackTrace();
                }

                if ( TextUtils.isEmpty(strShortener) ){
                    SendResponse("onError", strLongURL);
                } else {
                    SendResponse("onSuccess", strShortener);
                }
            }

            @Override
            public void onError(CIResponse response, int code, Exception exception) {

                SendResponse("onError", strLongURL);
            }
        });
    }

    class  URLshortenerLanuch extends CIWSLanuch{

        @Override
        protected int getConnectTimeout() {
            return 10000;
        }
        @Override
        protected int getReadTimeout() {
            return 10000;
        }
        @Override
        protected Map<String, String> getDefaultHeaders() {

            Map<String, String > header = new HashMap<>();
            header.put( "Content-Type", "application/json;charset=UTF-8");

            return header;
        }
    }
}
