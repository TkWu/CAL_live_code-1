package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIInquiryPromoteCodeTokenReq;
import ci.ws.Models.entities.CIInquiryPromoteCodeTokenResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by KevinCheng on 17/12/11.
 * 功能說明：以促銷代碼查詢token。
 */
public class CIInquiryPromoteCodeTokenModel extends CIWSBaseModel {


    public interface Callback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         * @param rt_code   result code
         * @param rt_msg    result msg
         * @param data      result data
         */
        void onSuccess(String rt_code, String rt_msg, CIInquiryPromoteCodeTokenResp data);
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
        OfferCode("OfferCode"),
        Version("Version"),
        language("language");
        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/CIAPP/api/InquiryPromoteCodeToken";

    private Callback m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryPromoteCodeTokenModel(Callback callback){

        this.m_Callback = callback;
    }

    public void getDataFrowWS(String code){
        CIInquiryPromoteCodeTokenReq req = new CIInquiryPromoteCodeTokenReq();
        req.OfferCode = code;
        m_jsBody = new JSONObject();
        try {
            m_jsBody.put(eParaTag.OfferCode.getString(), req.OfferCode);
            m_jsBody.put(eParaTag.Version.getString(), req.Version);
            m_jsBody.put(eParaTag.language.getString(), req.language);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code){


        CIInquiryPromoteCodeTokenResp data
                = GsonTool
                .getGson()
                .fromJson(respBody.strData, CIInquiryPromoteCodeTokenResp.class);

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
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryPromoteCodeToken)) ,"");
        } else {
            if (null != m_Callback) {
                m_Callback.onError(code, strMag);
            }
        }
    }

}
