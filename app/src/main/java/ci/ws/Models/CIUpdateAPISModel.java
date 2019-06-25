package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIApisAddEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by joannyang on 16/5/27.
 */
public class CIUpdateAPISModel extends CIWSBaseModel {


    public interface UpdateApisCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onUpdateApisSuccess( String rt_code, String rt_msg);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onUpdateApisError( String rt_code, String rt_msg );
    }

    private UpdateApisCallBack m_callback = null;
    //private static final String API_NAME = "/CIAPP/api/UpdateApis";
    private static final String API_NAME = "/CIAPP/api/AddApis";


    private enum eParaTag {

        login_token("login_token"),
        culture_info("culture_info"),
        device_id("device_id"),
        version("version"),
        card_no("card_no");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

//    public CIInquiryApisListModel(){}

    public CIUpdateAPISModel(UpdateApisCallBack listener ){ this.m_callback = listener; }

    public void UpdateApisFromWS(String strCardNo) {

        m_jsBody = new JSONObject();
        try {
            m_jsBody.put( eParaTag.login_token.getString(), CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.card_no.getString(),     strCardNo);
            m_jsBody.put( eParaTag.culture_info.getString(), CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.device_id.getString(),   CIApplication.getDeviceInfo().getAndroidId());
            m_jsBody.put( eParaTag.version.getString(),     WSConfig.DEF_API_VERSION);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    public void UpdateApisFromWS(String strCardNo, CIApisAddEntity ciApisEntity) {
        try {

            String strRequest = GsonTool.toJson(ciApisEntity);

            m_jsBody = new JSONObject(strRequest);

            m_jsBody.put( eParaTag.login_token.getString(), CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.card_no.getString(),     strCardNo);
            m_jsBody.put( eParaTag.culture_info.getString(), CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.device_id.getString(),   CIApplication.getDeviceInfo().getAndroidId());
            m_jsBody.put( eParaTag.version.getString(),     WSConfig.DEF_API_VERSION);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        if ( null != m_callback ){
            m_callback.onUpdateApisSuccess(respBody.rt_code, respBody.rt_msg);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.SignUpMealList)) ,"");
        } else {
            if ( null != m_callback ){
                m_callback.onUpdateApisError(code, strMag);
            }
        }
    }

}
