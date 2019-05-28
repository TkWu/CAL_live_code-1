package ci.ws.Models;

import ci.function.Core.SLog;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CILoginResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.Models.entities.CISocialLoginResp;

/**
 * Created by Ryan on 16/4/21.
 * 功能說明: 使用Open Id如Facebook、Google+ Id登入系統。
 * 對應CI API : SocialLogin
 */
public class CILoginByOpenIdModel extends CIWSBaseModel {

    public interface LoginByOpenIdCallback {
        /**
         * 發送登入的response, 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param socialLoginResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSocialLoginSuccess( String rt_code, String rt_msg, CILoginResp socialLoginResp );
        /**
         * 發送登入的response, 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSocialLoginError( String rt_code, String rt_msg );
    }

    private static final String API_NAME = "/CIAPP/api/LoginByOpenId";
    private LoginByOpenIdCallback m_Listener = null;

    private enum eParaTag {

        open_id("open_id"),
        open_id_kind("open_id_kind"),
        open_id_email("open_email"),
        culture_info("culture_info"),
        device_id("device_id"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

//    private class CISocialLoginResp {
//
//        @Expose
//        public String flag;
//        @Expose
//        public String open_id_kind;
//        @Expose
//        public String mileage;
//        @Expose
//        public String card_type;
//        @Expose
//        public String open_id;
//        @Expose
//        public String last_name;
//        @Expose
//        public String card_no;
//        @Expose
//        public String first_name;
//        /**卡別到期日*/
//        @Expose
//        public String card_type_exp;
//    }

    /**
     * @param open_id 登入設群組的到d
     * @param open_id_kind 登入類型 CILoginWSPresenter.OPEN_ID_KIND_FACEBOOK / CILoginWSPresenter.OPEN_ID_KIND_GOOGLE
     * @param email 非必填欄位
     */
    public CILoginByOpenIdModel(String open_id, String open_id_kind , String email,  String strLanguage, String strDevice_id, String strVersion , LoginByOpenIdCallback listener ){

        this.m_Listener = listener;
        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.open_id.getString(),        open_id);
            m_jsBody.put( eParaTag.open_id_kind.getString(),   open_id_kind);
            m_jsBody.put( eParaTag.open_id_email.getString(),  email);
            m_jsBody.put( eParaTag.culture_info.getString(),   strLanguage);
            m_jsBody.put( eParaTag.device_id.getString(),      strDevice_id);
            m_jsBody.put( eParaTag.version.getString(),        strVersion);

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

        CISocialLoginResp socialResp = GsonTool.toObject( respBody.strData, CISocialLoginResp.class);

        if ( null == socialResp ){
           SLog.e("[CAL]","[WS Log][ResultCodeCheck]: null == socialResp");
            SendError_Response_can_not_parse();
            return;
        }

        if ( null == socialResp.card_no ){
           SLog.e("[CAL]","[WS Log][DecodeResponse_Success]: null == socialResp.card_no");
            //2016-11-15 Modify By Ryan for 當登入卡號拿不到,塞空字串, 讓登入流程可以走完,避免無法登入的問題
            socialResp.card_no = "";
            //SendError_Response_can_not_parse();
            //return;
        }
        if ( null == socialResp.card_type ){
           SLog.e("[CAL]","[WS Log][DecodeResponse_Success]: null == socialResp.card_type");
            //2016-11-15 Modify By Ryan for 當登入 拿不到塞空字串, 讓登入流程可以走完,避免無法登入的問題
            socialResp.card_type = "";
            //SendError_Response_can_not_parse();
            //return;
        }
        if ( null == socialResp.first_name )
            socialResp.first_name = "";
        if ( null == socialResp.last_name )
            socialResp.last_name = "";
        if ( null == socialResp.mileage )
            socialResp.mileage = "0";
        if ( null == socialResp.flag ){
           SLog.e("[CAL]","[WS Log][DecodeResponse_Success]: null == socialResp.flag");
            //2016-11-15 Modify By Ryan for 當登入token 拿不到塞空字串, 讓登入流程可以走完,避免無法登入的問題
            socialResp.flag = "";
            //SendError_Response_can_not_parse();
            //return;
        }
        if ( null == socialResp.card_type_exp )
            socialResp.card_type_exp = "";
        if ( null == socialResp.email )
            socialResp.email = "";

        CILoginResp loginResp = new CILoginResp();
        loginResp.card_no   = socialResp.card_no;
        loginResp.card_type = socialResp.card_type;
        loginResp.first_name = socialResp.first_name;
        loginResp.last_name = socialResp.last_name;
        loginResp.mileage = socialResp.mileage;
        loginResp.member_token =  socialResp.flag;
        loginResp.card_type_exp = socialResp.card_type_exp;
        //2016-11-21 Add by Ryan , 補上 email
        loginResp.email = socialResp.email;

        if ( null != m_Listener ){
            m_Listener.onSocialLoginSuccess(respBody.rt_code, respBody.rt_msg, loginResp );
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( null != m_Listener ){
            m_Listener.onSocialLoginError( code , strMag);
        }
    }
}
