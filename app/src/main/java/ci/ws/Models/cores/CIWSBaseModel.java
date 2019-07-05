package ci.ws.Models.cores;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import ci.function.Core.SLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ci.function.Core.CIApplication;
import ci.ui.object.AppInfo;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIResponseCallback;
import ci.ws.cores.CIWSCheckAuthLanuch;
import ci.ws.cores.object.CIRequest;
import ci.ws.cores.object.CIResponse;
import ci.ws.cores.object.EMethod;
import ci.ws.define.CIWSResultCode;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/4/13.
 */
public abstract class CIWSBaseModel {

    /**預設的Timeout -1, 代表不特別設定Timeout時間, 統一由WSConfig設定*/
    public static final int DEF_CONNECTION_TIME_OUT = -1;

    protected JSONObject m_jsBody = new JSONObject();

    protected String getJsonBody(){
       SLog.d("[CAL]","[WS Log][getJsonBody]: " + m_jsBody.toString());
        return m_jsBody.toString();
    }

    protected CIWSCheckAuthLanuch m_lanuch = null;

    protected CIWSResult ResultCodeCheck( String strBody ){

        CIWSResult result = new CIWSResult();
        try {

            JSONObject Jsobj = new JSONObject(strBody);
            if ( Jsobj.has("rt_code") && false == Jsobj.isNull("rt_code") ){
                result.rt_code = Jsobj.getString("rt_code");
            }
            if ( Jsobj.has("rt_msg") && false == Jsobj.isNull("rt_msg") ){
                result.rt_msg = Jsobj.getString("rt_msg");
            }
            if ( Jsobj.has("rt_data") && false == Jsobj.isNull("rt_data") ){
                result.strData = Jsobj.getString("rt_data");
            }

           //SLog.d("[CAL]","[WS Log][ResultCodeCheck]: "+strBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected EMethod getMethod(){
        return EMethod.POST;
    }

    protected String getURL(){

        return  WSConfig.DEF_WS_SITE;
    }

    public void DoConnectionWithoutAuth(){
        DoConnection(false);
    }

    public void DoConnection(){
        DoConnection(true);
    }

    private void DoConnection(boolean isNeedAuth){

        if ( false == AppInfo.getInstance(CIApplication.getContext()).bIsNetworkAvailable() ){

            DecodeResponse_Error(
                    CIWSResultCode.NO_INTERNET_CONNECTION,
                    CIWSResultCode.getResultMessage(CIWSResultCode.NO_INTERNET_CONNECTION), null);
            return;
        }
       SLog.d("[CAL]","[WS Log]: " + getURL() + getAPINAME());
        final CIRequest reqLogin = new CIRequest( getURL() + getAPINAME(), getMethod() , null, getJsonBody());

        m_lanuch = new CIWSCheckAuthLanuch();
        m_lanuch.setIsNeedAuth(isNeedAuth);
        m_lanuch.setConnectTimeout(getConnectTimeout());
        m_lanuch.setReadTimeout(getReadTimeout());
        m_lanuch.connection(reqLogin, new CIResponseCallback() {

            @Override
            public void onSuccess(String respBody, int code) {

                //回傳值為空
                if ( null == respBody ){
                    DecodeResponse_Error( CIWSResultCode.HTTP_RESPONSE_NULL,
                            CIWSResultCode.getResultMessage(CIWSResultCode.HTTP_RESPONSE_NULL),
                            new IOException("[" + CIWSResultCode.HTTP_RESPONSE_NULL + "]Http state:"
                                    + CIWSResultCode.getResultMessage(CIWSResultCode.HTTP_RESPONSE_NULL)) );
                    return;
                }

                String strCode = "";
                if ( code > 0 ){
                    strCode = String.valueOf(code);
                }

                CIWSResult result = ResultCodeCheck(respBody);
                Boolean bOk = ResultDataCheck(result);
                //Log.e("[CIWSBaseModel]","[ResultDataCheck]bOk:"+bOk);

                if ( CIWSResultCode.IsSuccess(result.rt_code) && true == bOk ){
                    DecodeResponse_Success(result, strCode);
                } else {

                    if ( TextUtils.equals( CIWSResultCode.NO_RESULTS, result.rt_code) ){
                        result.rt_msg = CIWSResultCode.getResultMessage(result.rt_code);
                    }

                    DecodeResponse_Error( result.rt_code, result.rt_msg, new ConnectException(result.rt_msg) );
                }
            }

            @Override
            public void onError(CIResponse response, int code, Exception exception) {

                String strMsg = "";
                if ( null != response ){
                    strMsg = response.body();
                } else if ( null != exception ) {
                    strMsg = exception.toString();
                }
                String strCode = "";
                if ( code > 0 ){
                    strCode = String.valueOf(code);
                }
                if ( TextUtils.equals( CIWSResultCode.HTTP_RESPONSE_TIME_OUT, strCode) ){
                    strMsg = CIWSResultCode.getResultMessage(strCode);
                } else if ( TextUtils.equals( CIWSResultCode.NO_RESULTS, strCode) ){
                    strMsg = CIWSResultCode.getResultMessage(strCode);
                }

                DecodeResponse_Error( strCode, strMsg, exception);
            }
        });
    }

    public Boolean ResultDataCheck( CIWSResult result ){

        if ( null == result ){
            SendError_Response_can_not_parse();
            return false;
        }
        //成功 rt_code =000, 失敗為其他值, 但不該為空值
        if ( null == result.rt_code || result.rt_code.length() <= 0 ){
            SendError_Response_Code_null();
            return false;
        }
        //失敗, 需要有錯誤訊息
        if ( false == CIWSResultCode.IsSuccess(result.rt_code) && (null == result.rt_msg || result.rt_msg.length() <= 0) ){
            SendError_Response_msg_null();
            return false;
        }
        //待確認邏輯
//        //rt_code 等於 000, 表示成功, 需要有回復資料
//        if ( CIWSResultCode.IsSuccess(result.rt_code) && null == result.strData ){
//            SendError_Response_data_null();
//            return false;
//        }

        return true;
    }

    public void SendError_Response_can_not_parse(){
        DecodeResponse_Error(CIWSResultCode.HTTP_RESPONSE_CAN_NOT_PARSE,
                CIWSResultCode.getResultMessage(CIWSResultCode.HTTP_RESPONSE_CAN_NOT_PARSE),
                new IOException("[" + CIWSResultCode.HTTP_RESPONSE_CAN_NOT_PARSE + "]Http state:"
                        + CIWSResultCode.getResultMessage(CIWSResultCode.HTTP_RESPONSE_CAN_NOT_PARSE)));
    }

    public void SendError_Response_Code_null(){
        DecodeResponse_Error( CIWSResultCode.HTTP_RESPONSE_CODE_NULL,
                CIWSResultCode.getResultMessage(CIWSResultCode.HTTP_RESPONSE_CODE_NULL),
                new IOException("[" + CIWSResultCode.HTTP_RESPONSE_CODE_NULL + "]Http state:"
                        + CIWSResultCode.getResultMessage(CIWSResultCode.HTTP_RESPONSE_CODE_NULL)) );
    }

    public void SendError_Response_msg_null(){
        DecodeResponse_Error( CIWSResultCode.HTTP_RESPONSE_MSG_NULL,
                CIWSResultCode.getResultMessage(CIWSResultCode.HTTP_RESPONSE_MSG_NULL),
                new IOException("[" + CIWSResultCode.HTTP_RESPONSE_MSG_NULL + "]Http state:"
                        + CIWSResultCode.getResultMessage(CIWSResultCode.HTTP_RESPONSE_MSG_NULL)) );
    }

    public void SendError_Response_data_null(){
        DecodeResponse_Error( CIWSResultCode.HTTP_RESPONSE_DATA_NULL,
                CIWSResultCode.getResultMessage(CIWSResultCode.HTTP_RESPONSE_DATA_NULL),
                new IOException("[" + CIWSResultCode.HTTP_RESPONSE_DATA_NULL + "]Http state:"
                        + CIWSResultCode.getResultMessage(CIWSResultCode.HTTP_RESPONSE_DATA_NULL)) );
    }

    /**取消當下的 Request*/
    public void CancelRequest(){

        if ( null != m_lanuch && false == m_lanuch.isCancelled() ){
            m_lanuch.cancel(true);
        }
    }

    public static String getJsonFile( String strJson ) {

        Context context      = CIApplication.getContext();
        String       strText      = "";
        AssetManager assetManager = context.getAssets();
        InputStream inputStream  = null;
        try {
            inputStream = assetManager.open( "json" + File.separator + strJson);
            strText = readTextFile(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return strText;
    }

    private static String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[]                buf          = new byte[1024];
        int                   len;

        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toString();
    }


    /**取得JSONObject內的JSONObject*/
    public JSONObject getJsobjectFromJsobject( JSONObject jsObj , String strTag ){

        JSONObject jsValue = null;
        try{
            if ( null == jsObj ){
                return jsValue;
            }

            jsValue = jsObj.optJSONObject(strTag);

        } catch (Exception e){
            e.printStackTrace();
        }

        return jsValue;
    }
    /**取得JSONObject內的JSONObject*/
    public JSONArray getJSONArrayFromJsobject(JSONObject jsObj , String strTag ){

        JSONArray jsarValue = null;
        try{

            if ( null == jsObj ){
                return jsarValue;
            }

            jsarValue = jsObj.optJSONArray(strTag);

        } catch (Exception e){
            e.printStackTrace();
        }

        return jsarValue;
    }

    /**取得JSONObject內的StringValue*/
    public String getStringFromJsobject( JSONObject jsObj , String strTag ){

        String strValue = "";
        try{
            if ( null == jsObj ){
                return strValue;
            }

            strValue = jsObj.optString(strTag, "");

        } catch (Exception e){
            e.printStackTrace();
        }

        return strValue;
    }

    public abstract String getAPINAME();

    protected abstract void DecodeResponse_Success( CIWSResult respBody, String code);

    protected abstract void DecodeResponse_Error( String code, String strMag, Exception exception );

    protected String convertDateFormat(String template ,Calendar calendar) {
        // 定義時間格式
        SimpleDateFormat sdf = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        sdf.applyPattern(template);
        return sdf.format(calendar.getTime());
    }

    protected int getConnectTimeout() {
        return DEF_CONNECTION_TIME_OUT;
    }

    protected int getReadTimeout() {
        return DEF_CONNECTION_TIME_OUT;
    }

}
