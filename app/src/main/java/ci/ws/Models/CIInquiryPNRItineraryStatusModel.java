package ci.ws.Models;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIPNRItineraryStatusResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.CIWSHomeStatus_Code;
import ci.ws.define.WSConfig;

/**
 * InquiryPNRItineraryStatus WS 的 Model
 * 使用PNR ID與Seq Number查詢該航段的online check in status code
 * Created by jlchen on 16/6/13.
 */
public class CIInquiryPNRItineraryStatusModel extends CIWSBaseModel {

    public interface CallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param resp      Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code,
                       String rt_msg,
                       CIPNRItineraryStatusResp resp);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onError(String rt_code, String rt_msg);
    }

    private CallBack m_Callback = null;

    private static final String API_NAME = "/CIAPP/api/InquiryPNRItineraryStatus";

    private String m_strAPI= "";

    private enum eParaTag {

        Pnr_id("Pnr_id"),
        Segment_Num("Segment_Num"),
        language("language"),
        Version("Version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    @Override
    public String getAPINAME() {
        return m_strAPI;
    }

    public CIInquiryPNRItineraryStatusModel(CallBack listener){
        this.m_Callback = listener;
    }

    /**
     * @param strPnrId 訂位代號
     * @param strSeqNo 航段編號
     */
    public void sendReqDataToWS(String strPnrId, String strSeqNo){

        m_strAPI = API_NAME;
        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.Pnr_id.getString(),      strPnrId);
            m_jsBody.put( eParaTag.Segment_Num.getString(), strSeqNo);
            m_jsBody.put( eParaTag.language.getString(),    CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.Version.getString(),     WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        Gson gson = new Gson();
        CIPNRItineraryStatusResp resp = null;

        try {

            resp = gson.fromJson( respBody.strData, CIPNRItineraryStatusResp.class);

        } catch (Exception e ){
            e.printStackTrace();
            SendError_Response_can_not_parse();
            return;
        }

        if ( null == resp || null == resp.PNR_Status ){
            SendError_Response_can_not_parse();
            return;
        }

        //放置PNR 狀態
        try {
            resp.PNR_Status.iStatus_Code = Integer.parseInt(resp.PNR_Status.Status_Code );
        } catch(Exception e) {
            resp.PNR_Status.iStatus_Code = CIWSHomeStatus_Code.TYPE_A_NO_TICKET;
        }

        if ( null != m_Callback ){
            m_Callback.onSuccess(
                    respBody.rt_code,
                    respBody.rt_msg,
                    resp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryPNRItineraryStatus)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onError(code, strMag);
            }
        }
    }
}
