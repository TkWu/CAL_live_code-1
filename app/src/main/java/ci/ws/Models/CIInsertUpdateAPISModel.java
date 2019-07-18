package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIApisAddEntity;
import ci.ws.Models.entities.CIApisEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.CIWSResultCode;
import ci.ws.define.WSConfig;

/**
 * Created by systex on 16/8/23.
 */
public class CIInsertUpdateAPISModel extends CIWSBaseModel {


    private static final String API_NAME = "/CIAPP/api/AddApis";

    private InsertUpdateApisCallBack m_callback = null;

    public interface InsertUpdateApisCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInsertUpdateApisSuccess( String rt_code, String rt_msg);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInsertUpdateApisError( String rt_code, String rt_msg );
    }

    private enum eParaTag {

        login_token("login_token"),
        culture_info("culture_info"),
        device_id("device_id"),
        version("version"),
        card_no("card_no"),
        language("language");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInsertUpdateAPISModel( InsertUpdateApisCallBack callback) {
        this.m_callback = callback;
    }

    public void InsertUpdateApisFromWS( String strCardNo, CIApisAddEntity apisEntity ){

        try {
            m_jsBody = new JSONObject(GsonTool.toJson(apisEntity));
            //補上固定參數
            //m_jsBody.put( eParaTag.login_token.getString(), CIWSShareManager.getAPI().getLoginToken());
            //m_jsBody.put( eParaTag.card_no.getString(),     strCardNo);
            //m_jsBody.put( eParaTag.culture_info.getString(), CIApplication.getLanguageInfo().getWSLanguage());
            //m_jsBody.put( eParaTag.device_id.getString(),   CIApplication.getDeviceInfo().getAndroidId());
            //m_jsBody.put( eParaTag.mode.getString(),     "U");
            m_jsBody.put( CIInsertUpdateAPISModel.eParaTag.language.getString(), CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( CIInsertUpdateAPISModel.eParaTag.version.getString(),     WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {
        if( m_callback != null ) {
            m_callback.onInsertUpdateApisSuccess(respBody.rt_code, respBody.rt_msg);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if( m_callback != null ) {
            m_callback.onInsertUpdateApisError(code,strMag);
        }
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
        //rt_code 等於 000, 表示成功, 需要有回復資料
        if ( false == CIWSResultCode.IsSuccess(result.rt_code) ){
            SendError_Response_data_null();
            return false;
        }

        return true;
    }
}
