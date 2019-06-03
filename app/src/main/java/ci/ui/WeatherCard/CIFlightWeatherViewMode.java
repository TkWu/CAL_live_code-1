package ci.ui.WeatherCard;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;

import ci.function.Core.SLog;

import com.chinaairlines.mobile30.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import ci.function.Core.CIApplication;
import ci.ui.WeatherCard.resultData.CIWeatherResp;

/**
 * Created by jlchen on 2016/2/3.
 */
public class CIFlightWeatherViewMode {

    public interface Callback {
        void onSuccess(CIWeatherResp resultData);

        void onError(String strError);
    }

    //2016-12-01 Modify by Ryan for 調整為抓緯經度查天氣不抓機場代碼
    //private static final String YQL = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='c'";
    //private static final String YQL = "select * from weather.forecast where woeid in (select woeid from geo.places where text=\"(%s,%s)\") and u='c'";
    //private static final String URL = "https://query.yahooapis.com/v1/public/yql?q=%s&format=json";

    //20190508 weather api 對應新版yahoo 修正
    private static final String appId = "qIXSTN4o";
    private static final String CONSUMER_KEY = "dj0yJmk9QVRmUXhaVU1yaFVrJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PWYx";
    private static final String CONSUMER_SECRET = "43ec3ffbb611cf0ac14917ae9e085edca3ec92b0";
    private static final String baseUrl = "https://weather-ydn-yql.media.yahoo.com/forecastrss";
    private static final String lat_long = "lat=%s&lon=%s";

//    public static void findData(String location, final Callback listener) {
    public static void findData( final String strLatitude, final String strLongitude, final Callback listener) {

        new AsyncTask<String, Void, CIWeatherResp>() {
            @Override
            protected CIWeatherResp doInBackground(String[] strLocations) {

                //欲查詢天氣的地名
                //String strLocation = strLocations[0];
                SLog.d("[CAL]", "weather location lat= " + strLatitude + " long= " + strLongitude);

                //地名帶入YQL語法內
                //String strYQL = String.format(YQL, strLocation);
                //String strYQL = String.format(YQL, strLatitude, strLongitude);

                //YQL語法帶入URL內
                //String strURL = String.format(URL, Uri.encode(strYQL));

                //要接收data的結構
//                CIWeatherResp resultData = new CIWeatherResp();
//
//                try {
//                    URL url = new URL(strURL);
//

//                    URLConnection connection = url.openConnection();
//                    connection.setConnectTimeout(30000);
//                    connection.setReadTimeout(30000);
//                    connection.setUseCaches(false);
//
//                    InputStream inputStream = connection.getInputStream();
//
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                    StringBuilder strResult = new StringBuilder();
//                    String strLine;
//                    while ((strLine = reader.readLine()) != null) {
//                        strResult.append(strLine);
//                    }
//                    reader.close();
//
//                    JSONObject jsResult = new JSONObject(strResult.toString());
//                   SLog.d("weather result", "" + jsResult.toString());
//
//                    JSONObject joQueryResults = jsResult.optJSONObject("query");
//                    int iCount = joQueryResults.optInt("count");
//
//                    if (iCount == 0) {
//                        //查不到天氣資料
//                        resultData.m_strError = CIApplication.getContext().getString(R.string.no_match_data);
//                    }else {
//                        JSONObject joWeatherResp = joQueryResults.optJSONObject("results").optJSONObject("channel");
//                        resultData.DecodeJSON(joWeatherResp);
//                    }
//
//                } catch (Exception e) {
//                    resultData = new CIWeatherResp();
//                    resultData.m_strError = e.toString();
//                   SLog.d("weather exception", "" + e.toString());
//                }

                //20190508 weather api 對應新版yahoo 修正
                //經緯度帶入參數內
                String[] strLATLONG = String.format(lat_long, strLatitude, strLongitude).split("&");


                //組合目標網址
                String targetUrl = baseUrl + "?" + strLATLONG[0] + "&" + strLATLONG[1] + "&format=json&u=c";

                //要接收data的結構
                CIWeatherResp resultData = new CIWeatherResp();


                try {
                    URL url = new URL(targetUrl);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", getAuthHeader(strLATLONG));
                    conn.setRequestProperty("X-Yahoo-App-Id", appId);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setUseCaches(false);

                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(30000);
                    connection.setReadTimeout(30000);
                    connection.setUseCaches(false);
                    conn.connect();
                    //Get Response code
                    int status = conn.getResponseCode();
                    SLog.d("weather status code", status + "");

                    InputStream inputStream = conn.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder strResult = new StringBuilder();
                    String strLine;
                    while ((strLine = reader.readLine()) != null) {
                        strResult.append(strLine);
                    }
                    reader.close();

                    SLog.d("weather strResult", "" + strResult.toString());
                    JSONObject jsResult = new JSONObject(strResult.toString());
                    SLog.d("weather result", "" + jsResult.toString());
                    SLog.d("weather jsResult", "" + jsResult.toString());

                    JSONObject joQueryResults = jsResult.optJSONObject("query");
                    int iCount = joQueryResults.optInt("count");
                    resultData.DecodeJSON(jsResult);

                    if (iCount == 0) {
                        //查不到天氣資料
                        resultData.m_strError = CIApplication.getContext().getString(R.string.no_match_data);
                    } else {
                        JSONObject joWeatherResp = joQueryResults.optJSONObject("results").optJSONObject("channel");
                        resultData.DecodeJSON(joWeatherResp);
                    }
//                    JSONObject joQueryResults = jsResult.optJSONObject("query");
//                    int iCount = joQueryResults.optInt("count");
//
//                    if (iCount == 0) {
//                        //查不到天氣資料
//                        resultData.m_strError = CIApplication.getContext().getString(R.string.no_match_data);
//                    }else {
//                        JSONObject joWeatherResp = joQueryResults.optJSONObject("results").optJSONObject("channel");
//                        resultData.DecodeJSON(joWeatherResp);
//                    }

                } catch (Exception e) {
                    resultData = new CIWeatherResp();
                    resultData.m_strError = e.toString();
                    SLog.d("weather exception", "" + e.toString());
                }

                return resultData;
            }

            @Override
            protected void onPostExecute(CIWeatherResp resultData) {
                if (null != resultData && null == resultData.m_strError) {
                    listener.onSuccess(resultData);
                } else {
                    //直接顯示統一的錯誤訊息
                    listener.onError(CIApplication.getContext().getString(R.string.no_match_data));
//                    if ( TextUtils.isEmpty(resultData.m_strError) ) {
//                        listener.onError(CIApplication.getContext().getString(R.string.no_match_data));
//                    }else{
//                        listener.onError(resultData.m_strError);
//                    }
                }
            }
        }.execute("");

    }

    private static String getAuthHeader(String[] latlong_string) {
        String authorizationLine = "";

        long timestamp = new Date().getTime() / 1000;
        byte[] nonce = new byte[32];
        Random rand = new Random();
        rand.nextBytes(nonce);
        //String oauthNonce = new String(nonce).replaceAll("\\W", "");
        String oauthNonce = timestamp+"";

        List<String> parameters = new ArrayList<>();
        parameters.add("oauth_consumer_key=" + CONSUMER_KEY);
        parameters.add("oauth_nonce=" + oauthNonce);
        parameters.add("oauth_signature_method=HMAC-SHA1");
        parameters.add("oauth_timestamp=" + timestamp);
        parameters.add("oauth_version=1.0");
        parameters.add(latlong_string[0]);
        parameters.add(latlong_string[1]);
        parameters.add("format=json");
        parameters.add("u=c");
        Collections.sort(parameters);

        StringBuffer parametersList = new StringBuffer();
        for (int i = 0; i < parameters.size(); i++) {
            parametersList.append(((i > 0) ? "&" : "") + parameters.get(i));
        }

        String signatureString = "GET&";
        try {
            signatureString = signatureString + URLEncoder.encode(baseUrl, "UTF-8") + "&" +
                    URLEncoder.encode(parametersList.toString(), "UTF-8");
        } catch (Exception e) {
            SLog.e("Unable to append signature");
            return "";
        }

        String signature = null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec((CONSUMER_SECRET + "&").getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHMAC = mac.doFinal(signatureString.getBytes());
            signature = Base64.encodeToString(rawHMAC, Base64.NO_WRAP);

        } catch (Exception e) {
            SLog.e("Unable to append signature");
            return "";
        }

        authorizationLine = "OAuth " +
                "oauth_consumer_key=\"" + CONSUMER_KEY + "\", " +
                "oauth_nonce=\"" + oauthNonce + "\", " +
                "oauth_timestamp=\"" + timestamp + "\", " +
                "oauth_signature_method=\"HMAC-SHA1\", " +
                "oauth_signature=\"" + signature + "\", " +
                "oauth_version=\"1.0\"";

        SLog.d("weather string", authorizationLine);

        return authorizationLine;

    }
}
