package ci.ws.Models;

import ci.function.Core.SLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CILoginReq;
import ci.ws.Models.entities.CILoginResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;

/**
 * Created by Ryan on 16/4/13.
 */
public class CILoginWSModel extends CIWSBaseModel {

    public interface LoginCallback {
        /**
         * 發送登入的response, 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param loginResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onLoginSuccess( String rt_code, String rt_msg, CILoginResp loginResp );
        /**
         * 發送登入的response, 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onLoginError( String rt_code, String rt_msg );
    }


    private static final String API_NAME = "/CIAPP/api/Login";

    public static final int    CONNECTION_TIME_OUT = 40 * 1000;
    public static final int    READ_TIME_OUT       = 40 * 1000;

    private LoginCallback m_Listener = null;

    private enum eParaTag {

        user_id("user_id"),
        password("password"),
        social_id("social_id"),
        social_vendor("social_vendor"),
        social_email("social_email"),
        is_social_combine("is_social_combine"),
        culture_info("culture_info"),
        device_id("device_id"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    protected int getConnectTimeout() {
        return CONNECTION_TIME_OUT;
    }

    protected int getReadTimeout() {
        return READ_TIME_OUT;
    }

    public CILoginWSModel(
            CILoginReq loginReq,
            String strLanguage,
            String strDevice_id,
            String strVersion,
            LoginCallback Listener ){

        this.m_Listener = Listener;
        m_jsBody = new JSONObject();

        try {

            m_jsBody.put( eParaTag.user_id.getString(),             loginReq.user_id);
            m_jsBody.put( eParaTag.password.getString(),            loginReq.password);
            m_jsBody.put( eParaTag.social_id.getString(),           loginReq.social_id);
            m_jsBody.put( eParaTag.social_vendor.getString(),       loginReq.social_vendor);
            m_jsBody.put( eParaTag.social_email.getString(),        loginReq.social_email);
            m_jsBody.put( eParaTag.is_social_combine.getString(),   loginReq.is_social_combine);
            m_jsBody.put( eParaTag.culture_info.getString(),        strLanguage);
            m_jsBody.put( eParaTag.device_id.getString(),           strDevice_id);
            m_jsBody.put( eParaTag.version.getString(),             strVersion);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        CILoginResp ciLoginResp = GsonTool.toObject(respBody.strData, CILoginResp.class);

        if ( null == ciLoginResp ){
           SLog.e("[CAL]","[WS Log][DecodeResponse_Success]: null == ciLoginResp");
            SendError_Response_can_not_parse();
            return;
        }

        if ( null == ciLoginResp.card_no ){
           SLog.e("[CAL]","[WS Log][DecodeResponse_Success]: null == ciLoginResp.card_no");
            //2016-11-15 Modify By Ryan for 當登入拿不到card_no,先塞空字串, 讓登入流程可以走完,避免無法登入的問題
            ciLoginResp.card_no = "";
            //SendError_Response_can_not_parse();
            //return;
        }
        if ( null == ciLoginResp.card_type ){
           SLog.e("[CAL]","[WS Log][DecodeResponse_Success]: null == ciLoginResp.card_type");
            //2016-11-15 Modify By Ryan for 當登入拿不到card_type,先塞空字串, 讓登入流程可以走完,避免無法登入的問題
            ciLoginResp.card_type = "";
            //SendError_Response_can_not_parse();
            //return;
        }
        if ( null == ciLoginResp.cin_name )
            ciLoginResp.cin_name = "";
        if ( null == ciLoginResp.surname_cht )
            ciLoginResp.surname_cht = "";
        if ( null == ciLoginResp.surname_en )
            ciLoginResp.surname_en = "";
        if ( null == ciLoginResp.first_name )
            ciLoginResp.first_name = "";
        if ( null == ciLoginResp.last_name )
            ciLoginResp.last_name = "";
        if ( null == ciLoginResp.email ) {
            ciLoginResp.email = "";
        } else {
            String strEmail = "";
            try {
                strEmail = URLDecoder.decode(ciLoginResp.email, "UTF-8");
            } catch ( Exception e ){
                e.printStackTrace();
                strEmail = ciLoginResp.email;
            }
            ciLoginResp.email = strEmail;
        }
        if ( null == ciLoginResp.mobile )
            ciLoginResp.mobile = "";
        if ( null == ciLoginResp.home_tel )
            ciLoginResp.home_tel = "";
        if ( null == ciLoginResp.buss_tel )
            ciLoginResp.buss_tel = "";
        if ( null == ciLoginResp.brth_date )
            ciLoginResp.brth_date = "";
        if ( null == ciLoginResp.member_token ){
           SLog.e("[CAL]","[WS Log][DecodeResponse_Success]: null == ciLoginResp.member_token");
            //2016-11-15 Modify By Ryan for 當登入拿不到member_token,先塞空字串, 讓登入流程可以走完,避免無法登入的問題
            ciLoginResp.member_token = "";
            //SendError_Response_can_not_parse();
            //return;
        }
        if ( null == ciLoginResp.mileage )
            ciLoginResp.mileage = "0";
        if ( null == ciLoginResp.card_type_exp )
            ciLoginResp.card_type_exp = "";

        if ( null != m_Listener ){
            m_Listener.onLoginSuccess(respBody.rt_code, respBody.rt_msg, ciLoginResp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( null != m_Listener ){
            m_Listener.onLoginError( code , strMag);
        }
    }
}
