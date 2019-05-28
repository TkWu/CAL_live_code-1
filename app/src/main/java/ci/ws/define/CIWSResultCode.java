package ci.ws.define;

import android.text.TextUtils;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;

/**
 * Created by Ryan on 16/3/30.
 */
public class CIWSResultCode {

    //登入成功
    public static final String LOGIN_SUCCESS                    = "000";
    //尚未申請密碼
    public static final String LOGIN_ERROR_NO_APPLY_PASSWORD    = "904";
    //已綁定其他社群帳號
    public static final String LOGIN_ERROR_HAVE_OTHER_OPEN_ID   = "973";
    //卡號格式錯誤
    public static final String LOGIN_ERROR_CARD_FORMAT          = "988";
    //此手機號碼與他人重複
    public static final String LOGIN_ERROR_NOT_SINGLE_MOBILE    = "989";
    //此信箱與他人重複
    public static final String LOGIN_ERROR_NOT_SINGLE_EMAIL     = "990";
    //手機序號錯誤
    public static final String LOGIN_ERROR_MOBILE_NUMBER        = "991";
    //密碼或卡號不正確
    public static final String LOGIN_ERROR_PASSWORD             = "993";
    //限制無法使用華夏會員服務專區各項會員網路功能
    public static final String LOGIN_ERROR_APPLY_NO_SERVICE     = "994";
    //系統維護中
    public static final String LOGIN_ERROR_SERVER_MAINTENANCE   = "997";
    //授權失敗
    public static final String LOGIN_ERROR_AUTHORIZATION_FAILED = "999";


    //社群登入-查不到對應的會員資料
    public static final String LOGIN_OPEN_ID_ERROR_NOT_MATCH    = "980";


    //自定義的授權失敗
    public static final String ERROR_LOGOUT_AUTOMATICALLY   = "995";
    public static final String ERROR_AUTHORIZATION_FAILED   = "999";

    //查無資料
    public static final String NO_RESULTS                   = "-998";

    public static final String NO_INTERNET_CONNECTION       = "9999";
    public static final String HTTP_RESPONSE_CAN_NOT_PARSE  = "9998";
    public static final String HTTP_RESPONSE_NULL           = "9997";
    public static final String HTTP_RESPONSE_CODE_NULL      = "9996";
    public static final String HTTP_RESPONSE_MSG_NULL       = "9995";
    public static final String HTTP_RESPONSE_DATA_NULL      = "9994";
    public static final String HTTP_RESPONSE_TIME_OUT       = "9993";

    /**檢查WS 發送是否成功*/
    public static Boolean IsSuccess( String strrt_code ){

        if ( "000".equals(strrt_code) ){
            return true;
        } else {
            return false;
        }
    }

    public static String getResultMessage( String strrt_code ){

        if ( TextUtils.equals(NO_INTERNET_CONNECTION, strrt_code) ){
            return CIApplication.getContext().getString(R.string.system_no_network_connection);
        } else if ( TextUtils.equals(HTTP_RESPONSE_CAN_NOT_PARSE, strrt_code) ){
            return "Webservice response can not parse!";
        } else if ( TextUtils.equals(HTTP_RESPONSE_NULL, strrt_code) ){
            return "Webservice response is null!";
        } else if ( TextUtils.equals(HTTP_RESPONSE_CODE_NULL, strrt_code) ){
            return "Webservice return code is null!";
        } else if ( TextUtils.equals(HTTP_RESPONSE_MSG_NULL, strrt_code) ){
            return "Webservice return messeage is null!";
        } else if ( TextUtils.equals(HTTP_RESPONSE_DATA_NULL, strrt_code )){
            return "Webservice return data is null!";
        } else if ( TextUtils.equals( HTTP_RESPONSE_TIME_OUT, strrt_code )){
            return  CIApplication.getContext().getString(R.string.system_time_out);
        } else if ( TextUtils.equals( NO_RESULTS, strrt_code) ){
            return CIApplication.getContext().getString(R.string.no_match_data);
        }

        return "Connect to Service Error !";
    }

}
