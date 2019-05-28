package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CICCPageReq;
import ci.ws.Models.entities.CICCPageResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by KevinCheng on 17/9/12.
 * Doc. : CI_APP_API_AddExcessBaggage_v2.docx
 * 功能說明：信用卡付款頁。
 */
public class CICCPageModel extends CIWSBaseModel {


    public interface InquiryCallback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         * @param rt_code   result code
         * @param rt_msg    result msg
         * @param data      result data
         */
        void onSuccess(String rt_code, String rt_msg, CICCPageResp data);
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
        language("language");
        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/CIAPP/api/CCPage";

    private InquiryCallback m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CICCPageModel(InquiryCallback callback){

        this.m_Callback = callback;
    }

    public void getInfoFrowWS(){
        CICCPageReq req = new CICCPageReq();
        m_jsBody = new JSONObject();
        try {
            m_jsBody.put(eParaTag.version.getString(), req.version);
            m_jsBody.put(eParaTag.language.getString(), req.language);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code){


        CICCPageResp data
                = GsonTool
                .getGson()
                .fromJson(respBody.strData, CICCPageResp.class);

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
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.CCPage)) ,"");
        } else {
            if (null != m_Callback) {
                m_Callback.onError(code, strMag);
            }
        }
    }

}
