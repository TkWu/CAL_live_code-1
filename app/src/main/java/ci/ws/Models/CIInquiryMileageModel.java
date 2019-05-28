package ci.ws.Models;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIMileageRecordResp;
import ci.ws.Models.entities.CIMileageReq;
import ci.ws.Models.entities.CIMileageResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Manage Miles 里程管理 的 model
 * Created by jlchen on 16/5/13.
 */
public class CIInquiryMileageModel extends CIWSBaseModel {

    public interface InquiryMileageCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param mileageResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryMileageSuccess(String rt_code, String rt_msg, CIMileageResp mileageResp);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryMileageError(String rt_code, String rt_msg);
    }

    private InquiryMileageCallBack m_Callback = null;

    private static final String API_NAME_MILEAGE            = "/CIAPP/api/InquiryMileage";

    private String m_strAPI= "";

    private enum eParaTag {

        card_no("card_no"),
        login_token("login_token"),
        device_id("device_id"),
        culture_info("culture_info"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    @Override
    public String getAPINAME() {
        return m_strAPI;
    }


    public CIInquiryMileageModel(InquiryMileageCallBack listener){

        this.m_Callback = listener;
    }

    /**
     * 查詢累積里程
     * @param mileageReq 向ws發請求時所帶的資料*/
    public void setMileageReq(CIMileageReq mileageReq){

        m_strAPI = API_NAME_MILEAGE;
        m_jsBody = new JSONObject();

        try {
            m_jsBody.put( eParaTag.card_no.getString(),             mileageReq.card_no);
            m_jsBody.put( eParaTag.login_token.getString(),         mileageReq.login_token);
            m_jsBody.put( eParaTag.device_id.getString(),           mileageReq.device_id);
            m_jsBody.put( eParaTag.culture_info.getString(),        mileageReq.culture_info);
            m_jsBody.put( eParaTag.version.getString(),             mileageReq.version);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        Gson gson = new Gson();
        CIMileageResp mileageResp = null;

        try {

            mileageResp = gson.fromJson( respBody.strData, CIMileageResp.class);

        } catch (Exception e ){
            e.printStackTrace();
        }

        if ( null == mileageResp || null == mileageResp.mileage ){
            SendError_Response_can_not_parse();
            return;
        }

        if ( null != m_Callback ){
            m_Callback.onInquiryMileageSuccess(respBody.rt_code, respBody.rt_msg, mileageResp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryMileage)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onInquiryMileageError(code, strMag);
            }
        }
    }
}
