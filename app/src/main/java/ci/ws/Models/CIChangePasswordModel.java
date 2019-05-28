package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/17.
 * 3.1.	ChangePassword (變更密碼)
 * 功能說明: 變更會員密碼。
 * 對應CI API : ChangePassword
 */
public class CIChangePasswordModel extends CIWSBaseModel {


    public interface ChangePasswordCallBack {
        /**
         * 變更密碼成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onChangePasswordSuccess( String rt_code, String rt_msg );
        /**
         * 變更密碼失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onChangePasswordError( String rt_code, String rt_msg );
    }


    private enum eParaTag {

        login_token,
        card_no,
        old_password,
        new_password,
        culture_info,
        device_id,
        version,
    }


    private static final String API_NAME = "/CIAPP/api/ChangePassword";
    private ChangePasswordCallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIChangePasswordModel( ChangePasswordCallBack callBack ){
        m_Callback = callBack;
    }

    public void ChangePassword( String strCardNo, String strOldPwd, String strNewPwd ){

        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.login_token.name(),  CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.card_no.name(),      strCardNo);
            m_jsBody.put( eParaTag.old_password.name(), strOldPwd);
            m_jsBody.put( eParaTag.new_password.name(), strNewPwd);
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

        if ( null != m_Callback ){
            m_Callback.onChangePasswordSuccess(respBody.rt_code, respBody.rt_msg);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( null != m_Callback ){
            m_Callback.onChangePasswordError(code, strMag);
        }
    }
}
