package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.define.WSConfig;

/**
 * Created by ryan on 16/5/16.
 * 3.1.	DisConnSocialNetwork (解除社群帳號FB/Google)
 * 功能說明: 解除社群帳號。
 * 對應CI API : SocialSeparate
 */
public class CIDisConnSocialNetworkModel extends CIWSBaseModel {

    public interface DisConnCallback {
        /**
         * 綁定社群帳號, 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onDisConnSocialConnSuccess(String rt_code, String rt_msg);
        /**
         * 綁定社群帳號, 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onDisConnSocialConnError(String rt_code, String rt_msg);
    }

    private enum eParaTag {

        login_token,
        card_no,
        open_id,
        open_id_kind,
        culture_info,
        device_id,
        version,
    }

    private static final String API_NAME = "/CIAPP/api/DisConnSocialNetwork";
    private DisConnCallback m_Listener = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIDisConnSocialNetworkModel(DisConnCallback listener){

        this.m_Listener = listener;
    }

    public void DisConnSocialNetworkFromWS( String card_no, String open_id, String open_id_kind ){

        m_jsBody = new JSONObject();
        try {
            m_jsBody.put( eParaTag.login_token.name(),  CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.card_no.name(),      card_no);
            m_jsBody.put( eParaTag.open_id.name(),      open_id);
            m_jsBody.put( eParaTag.open_id_kind.name(), open_id_kind);
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

        if ( null != m_Listener ){
            m_Listener.onDisConnSocialConnSuccess(respBody.rt_code, respBody.rt_msg);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( null != m_Listener ){
            m_Listener.onDisConnSocialConnError(code, strMag);
        }
    }
}
