package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIAddMemberCardReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.WSConfig;

/**
 * Created by KevinCheng on 17/7/14.
 * Doc. : CI_APP_API_AddFQTV.docx
 * 功能說明：會員卡號補入。
 */
public class CIAddMemberCardModel extends CIWSBaseModel {


    public interface InquiryCallback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code, String rt_msg);
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
        Pnr_id("Pnr_id"),
        Pnr_Seq("Pnr_Seq"),
        PaxNum("PaxNum"),
        CardNo("CardNo"),
        First_Name("First_Name"),
        Last_Name("Last_Name"),
        language("language"),
        Version("Version");
        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/CIAPP/api/AddMemberCard";

    private InquiryCallback m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIAddMemberCardModel(InquiryCallback callback){

        this.m_Callback = callback;
    }

    public void setRequestAndDoConnection(CIAddMemberCardReq req){

        m_jsBody = new JSONObject();
        try {
            m_jsBody.put(eParaTag.Pnr_id.getString(), req.Pnr_id);
            m_jsBody.put(eParaTag.Pnr_Seq.getString(), req.Pnr_Seq);
            m_jsBody.put(eParaTag.PaxNum.getString(), req.Pax_Number);
            m_jsBody.put(eParaTag.CardNo.getString(), req.Card_Id);
            m_jsBody.put(eParaTag.First_Name.getString(), req.First_Name);
            m_jsBody.put(eParaTag.Last_Name.getString(), req.Last_Name);
            m_jsBody.put(eParaTag.language.getString(), req.language);
            m_jsBody.put(eParaTag.Version.getString(), req.Version);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code){

        if ( null != m_Callback){
            m_Callback.onSuccess(respBody.rt_code, respBody.rt_msg);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.AddMemberCard)) ,"");
        } else {
            if (null != m_Callback) {
                m_Callback.onError(code, strMag);
            }
        }
    }

}
