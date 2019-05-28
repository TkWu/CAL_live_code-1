package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIConnSocialResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by ryan on 16/4/22.
 * 功能說明: 以Member No. 為條件會員更新綁定的Open Id。
 * 對應CI API : SocialCombine
 */
public class CIConnSocialNetworkModel extends CIWSBaseModel {

    public interface ConnSocialNetworkCallback {
        /**
         * 綁定社群帳號, 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param connSocialResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSocialConnSuccess( String rt_code, String rt_msg, CIConnSocialResp connSocialResp );
        /**
         * 綁定社群帳號, 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSocialConnError( String rt_code, String rt_msg );
    }

    private enum eParaTag {

        login_token("login_token"),
        card_no("card_no"),
        open_id("open_id"),
        open_id_kind("open_id_kind"),
        open_id_email("open_id_email"),
        culture_info("culture_info"),
        device_id("device_id"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/CIAPP/api/ConnSocialNetwork";
    private ConnSocialNetworkCallback m_Listener = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIConnSocialNetworkModel(  ConnSocialNetworkCallback listener ){

        this.m_Listener = listener;
    }

    public void ConnSocialNetworkFromWS( String card_no, String open_id, String open_id_kind, String strSocialAccount ){

        m_jsBody = new JSONObject();
        try {
            m_jsBody.put( eParaTag.login_token.name(),  CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.card_no.name(),      card_no);
            m_jsBody.put( eParaTag.open_id.name(),      open_id);
            m_jsBody.put( eParaTag.open_id_kind.name(), open_id_kind);
            m_jsBody.put( eParaTag.open_id_email.name(),strSocialAccount);
            m_jsBody.put( eParaTag.culture_info.name(), CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.device_id.name(),    CIApplication.getDeviceInfo().getAndroidId());
            m_jsBody.put( eParaTag.version.name(),      WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        CIConnSocialResp socialResp = GsonTool.toObject(respBody.strData, CIConnSocialResp.class);

        if ( null == socialResp ){
            SendError_Response_can_not_parse();
            return;
        }

//        String strTag = "";
//        try
//        {
//            JSONObject jsObj = new JSONObject(respBody.strData);
//            if ( jsObj.has("status") && false == jsObj.isNull("status") ){
//                strTag = jsObj.getString("status");
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        if ( null != m_Listener ){
            m_Listener.onSocialConnSuccess(respBody.rt_code, respBody.rt_msg, socialResp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( null != m_Listener ){
            m_Listener.onSocialConnError(code, strMag);
        }
    }
}
