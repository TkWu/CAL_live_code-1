package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIMileageReq;
import ci.ws.Models.entities.CIRedeemRecordResp;
import ci.ws.Models.entities.CIRedeemRecord_Info;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Miles Activity by Redeem tab 里程活動 查詢已兌換明細 的 model
 * Created by jlchen on 16/5/16.
 */
public class CIInquiryRedeemRecordModel extends CIWSBaseModel {

    public interface InquiryRedeemRecordCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param redeemRecordResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryRedeemRecordSuccess(String rt_code, String rt_msg, CIRedeemRecordResp redeemRecordResp);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryRedeemRecordError(String rt_code, String rt_msg);
    }

    private InquiryRedeemRecordCallBack m_Callback = null;

    private static final String API_NAME_REDEEM_RECORD   = "/CIAPP/api/InquiryRedeemRecord";

    private String m_strAPI= "";

    private enum eParaTag {

        card_no("card_no"),
        login_token("login_token"),
        device_id("device_id"),
        culture_info("culture_info"),
        version("version"),
        drow("drow");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    @Override
    public String getAPINAME() {
        return m_strAPI;
    }


    public CIInquiryRedeemRecordModel(InquiryRedeemRecordCallBack listener){

        this.m_Callback = listener;
    }

    /**
     * 查詢里程明細
     * @param mileageReq 向ws發請求時所帶的資料*/
    public void setMileageReq(CIMileageReq mileageReq){

        m_strAPI = API_NAME_REDEEM_RECORD;
        m_jsBody = new JSONObject();

        try {
            m_jsBody.put( eParaTag.card_no.getString(),             mileageReq.card_no);
            m_jsBody.put( eParaTag.login_token.getString(),         mileageReq.login_token);
            m_jsBody.put( eParaTag.device_id.getString(),           mileageReq.device_id);
            m_jsBody.put( eParaTag.culture_info.getString(),        mileageReq.culture_info);
            m_jsBody.put( eParaTag.version.getString(),             mileageReq.version);
            m_jsBody.put( eParaTag.drow.getString(),                "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        CIRedeemRecordResp datas = null;

        try {
            datas = GsonTool.toObject(respBody.strData, CIRedeemRecordResp.class);

            //資料按日期排序
            Collections.sort(datas,
                    new Comparator<CIRedeemRecord_Info>() {
                        public int compare(CIRedeemRecord_Info info1, CIRedeemRecord_Info info2) {
                            return info2.flight_date.compareTo(info1.flight_date);
                        }
                    });
        } catch (Exception e ){
            e.printStackTrace();

            SendError_Response_can_not_parse();
            return;
        }

        if ( null == datas ){
            SendError_Response_can_not_parse();
            return;
        }

        if ( null != m_Callback ){
            m_Callback.onInquiryRedeemRecordSuccess(respBody.rt_code, respBody.rt_msg, datas);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryRedeemRecord)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onInquiryRedeemRecordError(code, strMag);
            }
        }
    }
}
