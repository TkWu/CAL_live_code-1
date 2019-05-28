package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CICheckVersionAndAnnouncementReq;
import ci.ws.Models.entities.CICheckVersionAndAnnouncementResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by KevinCheng on 17/8/29.
 * Doc. : CI_APP_API_CheckVersion.docx
 * 功能說明：版本更新通知與重要公告。
 */
public class CICheckVersionAndAnnouncementModel extends CIWSBaseModel {


    public interface InquiryCallback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         * @param rt_code   result code
         * @param rt_msg    result msg
         * @param data      result data
         */
        void onSuccess(String rt_code, String rt_msg, CICheckVersionAndAnnouncementResp data);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onError(String rt_code, String rt_msg);
    }

    private enum eParaTag {
        version("version"),
        deviceType("deviceType"),
        language("language");
        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/CIAPP/api/appVersion";

    private InquiryCallback m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CICheckVersionAndAnnouncementModel(InquiryCallback callback){

        this.m_Callback = callback;
    }

    public void getInfoFrowWS(){
        CICheckVersionAndAnnouncementReq req = new CICheckVersionAndAnnouncementReq();
        m_jsBody = new JSONObject();
        try {
            m_jsBody.put(eParaTag.version.getString(), req.version);
            m_jsBody.put(eParaTag.deviceType.getString(), req.deviceType);
            m_jsBody.put(eParaTag.language.getString(), CIApplication.getLanguageInfo().getWSLanguage());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code){


        CICheckVersionAndAnnouncementResp data
                = GsonTool
                .getGson()
                .fromJson(respBody.strData, CICheckVersionAndAnnouncementResp.class);

        if ( null == data ){
            SendError_Response_can_not_parse();
            return;
        }

        if ( null != m_Callback){
            m_Callback.onSuccess(respBody.rt_code, respBody.rt_msg , data);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.CheckVersionAndAnnouncement)) ,"");
        } else {
            if (null != m_Callback) {
                m_Callback.onError(code, strMag);
            }
        }
    }

}
