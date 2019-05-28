package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.SLog;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIInquiryCoporateIdTokenReq;
import ci.ws.Models.entities.CIInquiryCoporateIdTokenResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.WSConfig;

/**
 * Created by 644336 on 19/04/15.
 * 功能說明：以企業會員代碼查詢token。
 */
public class CIInquiryCoporateIdTokenModel extends CIWSBaseModel {
    public interface Callback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         * @param rt_code   result code
         * @param rt_msg    result msg
         * @param data      result data
         */
        void onSuccess(String rt_code, String rt_msg, CIInquiryCoporateIdTokenResp data);
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

    private static final String API_NAME = "/CIAPP/api/InquiryBusinessMemberToken";

    private Callback m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryCoporateIdTokenModel(Callback callback){

        this.m_Callback = callback;
    }

    public void getDataFrowWS(String code){
        CIInquiryCoporateIdTokenReq req = new CIInquiryCoporateIdTokenReq();
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
        CIInquiryCoporateIdTokenResp data = new CIInquiryCoporateIdTokenResp();
        data.token = respBody.strData;

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
