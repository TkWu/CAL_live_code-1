package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.CIWSResultCode;

/**
 * Created by ryan on 16/4/16.
 * 功能說明: 以Member No. 為條件更新會員Email / 電話
 */
public class CIUpdateEmailByMemberNoWSModel extends CIWSBaseModel{


    public interface UpdateCallback {
        /**
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onUpdateSuccess( String rt_code, String rt_msg );

        /**
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onUpdateError( String rt_code, String rt_msg );
    }

    public enum eCONDITION {

        Email("1"),
        Phone("2");

        private String strTag = "";

        eCONDITION( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/CIAPP/api/UpdateEmailByMemberNo";
    private UpdateCallback m_Listener = null;

    private enum eParaTag {

        login_token("login_token"),
        card_no("card_no"),
        account("account"),
        condition("condition"),
        culture_info("culture_info"),
        device_id("device_id"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    public CIUpdateEmailByMemberNoWSModel( UpdateCallback listener ){

        this.m_Listener = listener;
    }

    public void UpdateEmailByMemberNoWS(
            String strlogintoken,
            String strCardno,
            String strAccount,
            String strCondition,
            String strLanguage,
            String strDevice_id,
            String strVersion ){

        m_jsBody = new JSONObject();
        try {
            m_jsBody.put( eParaTag.login_token.getString(), strlogintoken);
            m_jsBody.put( eParaTag.card_no.getString(),     strCardno);
            m_jsBody.put( eParaTag.account.getString(),     strAccount);
            m_jsBody.put( eParaTag.condition.getString(),   strCondition);
            m_jsBody.put( eParaTag.culture_info.getString(),strLanguage);
            m_jsBody.put( eParaTag.device_id.getString(),   strDevice_id);
            m_jsBody.put( eParaTag.version.getString(),     strVersion);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        if ( null != m_Listener ){
            m_Listener.onUpdateSuccess(respBody.rt_code, respBody.rt_msg );
        }
    }

    @Override
    protected void DecodeResponse_Error( String code, String strMag, Exception exception) {

        if ( null != m_Listener ){
            m_Listener.onUpdateError(String.valueOf(code), strMag);
        }
    }


    @Override
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
        if (false == CIWSResultCode.IsSuccess(result.rt_code) &&
                (null == result.rt_msg || result.rt_msg.length() <= 0)) {
            SendError_Response_msg_null();
            return false;
        }

        return true;
    }
}
