package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIMileageRecordResp;
import ci.ws.Models.entities.CIMileageRecord_Info;
import ci.ws.Models.entities.CIMileageReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.CIWSBookingClass;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Miles Activity by Accumulation tab 里程活動 查詢里程明細 的 model
 * Created by jlchen on 16/5/16.
 */
public class CIInquiryMileageRecordModel extends CIWSBaseModel {

    public interface InquiryMileageRecordCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param mileageRecordResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryMileageRecordSuccess(String rt_code, String rt_msg, CIMileageRecordResp mileageRecordResp);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryMileageRecordError(String rt_code, String rt_msg);
    }

    private InquiryMileageRecordCallBack m_Callback = null;

    private static final String API_NAME_MILEAGE_RECORD   = "/CIAPP/api/InquiryMileageRecord";

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


    public CIInquiryMileageRecordModel(InquiryMileageRecordCallBack listener){

        this.m_Callback = listener;
    }

    /**
     * 查詢里程明細
     * @param mileageReq 向ws發請求時所帶的資料*/
    public void setMileageReq(CIMileageReq mileageReq){

        m_strAPI = API_NAME_MILEAGE_RECORD;
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

        CIMileageRecordResp datas = null;

        try {
            datas = GsonTool.toObject(respBody.strData, CIMileageRecordResp.class);

            //資料按日期排序
            Collections.sort(datas,
                    new Comparator<CIMileageRecord_Info>() {
                        public int compare(CIMileageRecord_Info info1, CIMileageRecord_Info info2) {
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

        for ( int i = 0; i < datas.size(); i ++ ){
            //解析客艙資訊
            datas.get(i).Booking_Class_Name_Tag = CIWSBookingClass.ParseBookingClass(datas.get(i).cabin);

            //2016-06-21 modifly by ryan for 統一調整Flight number 為四碼, 不足則捕0
            //2016-07-05 modifly by jlchen - Server會直接給補零過後的航班代號 包含英文 所以不用另外處理
//            datas.get(i).flight_number = CIWSCommon.ConvertFlightNumber(datas.get(i).flight_number);
        }

        if ( null != m_Callback ){
            m_Callback.onInquiryMileageRecordSuccess(respBody.rt_code, respBody.rt_msg, datas);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryMileageRecord)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onInquiryMileageRecordError(code, strMag);
            }
        }
    }

}
