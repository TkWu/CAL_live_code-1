package ci.ws.Models;

import android.text.TextUtils;

import com.chinaairlines.mobile30.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.BaggageEntity;
import ci.ws.Models.entities.CIPassengerByPNRReq;
import ci.ws.Models.entities.CIPassengerListResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.CIWSBookingClass;
import ci.ws.define.WSConfig;

/**
 * trip detail-passenger 行程資訊by乘客tab 的 model
 * Created by jlchen on 16/5/11.
 */
public class CIInquiryAllPassengerByPNRModel extends CIWSBaseModel {

    public interface InquiryPassengerByPNRCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param resp      Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryPassengerByPNRSuccess(String rt_code,
                                            String rt_msg,
                                            CIPassengerListResp resp);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryPassengerByPNRError(String rt_code, String rt_msg);
    }

    private InquiryPassengerByPNRCallBack m_Callback = null;

    private static final String API_NAME_ALL_PASSENGER_BY_PNR = "/CIAPP/api/InquiryAllPassengerByPNR";


    private              String  m_strAPI       = "";

    private enum eParaTag {

        pnr_Id("pnr_Id"),
        departure_station("departure_station"),
        arrival_station("arrival_station"),
        language("language"),
        client_ip("client_ip"),
        Version("Version"),
        pnr_seq("pnr_seq"),
//        first_name("first_name"),
//        last_name("last_name"),

        respTag("Pax_Info"),
        segment_num("segment_num");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    @Override
    public String getAPINAME() {
        return m_strAPI;
    }

    public CIInquiryAllPassengerByPNRModel(InquiryPassengerByPNRCallBack listener){

        this.m_Callback = listener;
    }

    /**
     * @param passengerReq Request*/
    public void setAllPassengerReq(CIPassengerByPNRReq passengerReq){

        m_strAPI = API_NAME_ALL_PASSENGER_BY_PNR;
        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.pnr_Id.getString(),              passengerReq.pnr_Id);
            m_jsBody.put( eParaTag.departure_station.getString(),   passengerReq.departure_station);
            m_jsBody.put( eParaTag.arrival_station.getString(),     passengerReq.arrival_station);
            m_jsBody.put( eParaTag.pnr_seq.getString(),             passengerReq.pnr_seq);
            m_jsBody.put( eParaTag.client_ip.getString(),           CIApplication.getClentIp());
            m_jsBody.put( eParaTag.language.getString(),            CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.Version.getString(),             WSConfig.DEF_API_VERSION);
            m_jsBody.put( eParaTag.segment_num.getString(),         passengerReq.Segment_Number);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

//        //test
//        respBody = ResultCodeCheck(getJsonFile("InquiryPassenagerByPNR.json"));
//        //
        Gson gson = new Gson();
        CIPassengerListResp resp = null;

        try {

            resp = gson.fromJson( respBody.strData, CIPassengerListResp.class);

            //

        } catch (Exception e ){
            e.printStackTrace();
        }

        if ( null == resp || null == resp.Pax_Info ){
            SendError_Response_can_not_parse();
            return;
        }

        //解析客艙資訊
        for ( int i = 0 ; i < resp.Pax_Info.size() ; i ++ ){
            resp.Pax_Info.get(i).Class_of_Service
                    = CIWSBookingClass.ParseBookingClass(resp.Pax_Info.get(i).Booking_Class);
            //客艙資訊顯示名稱
            resp.Pax_Info.get(i).Class_of_Service_Name
                    = CIWSBookingClass.BookingClassCodeToName(resp.Pax_Info.get(i).Class_of_Service);

            //置換行李資訊轉為易讀的字串
            resp.Pax_Info.get(i).Baggage_Allowence
                    = ParseBaggage_AllowenceInfo(resp.Pax_Info.get(i).Baggage_Allowence);

        }

        if ( null != m_Callback ){
            m_Callback.onInquiryPassengerByPNRSuccess(
                    respBody.rt_code,
                    respBody.rt_msg,
                    resp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryPassenagerByPNR)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onInquiryPassengerByPNRError(code, strMag);
            }
        }
    }

    /**調整顯示字串, 更換為易讀的文字*/
    public String ParseBaggage_AllowenceInfo( String orgBaggage_Allowence ){

        String strNewBaggage_Allowence = "";

        if ( TextUtils.isEmpty(orgBaggage_Allowence) ){
            //空字串不處理
            strNewBaggage_Allowence = orgBaggage_Allowence;
        } else {
            strNewBaggage_Allowence = orgBaggage_Allowence.toUpperCase();

            strNewBaggage_Allowence = strNewBaggage_Allowence.replace("K", CIApplication.getContext().getString(R.string.trips_detail_luggage_kg));
            strNewBaggage_Allowence = strNewBaggage_Allowence.replace("PC",CIApplication.getContext().getString(R.string.trips_detail_luggage_PC));
        }

        return strNewBaggage_Allowence;
    }

    public static String getBagAmount(String ssramount){
        int iSsramount;
        try {
            iSsramount = Integer.valueOf(ssramount);
            return String.valueOf(iSsramount);
        }catch (Exception e){
            return "";
        }
    }

    public static String getBagUnit(String type){

        if(null == type) {
            type = "";
        }
        if(type.toUpperCase().equals(BaggageEntity.EXWG)) {
            return CIApplication.getContext().getString(R.string.trips_detail_luggage_kg);
        } else if(type.toUpperCase().equals(BaggageEntity.EXPC)) {
            return CIApplication.getContext().getString(R.string.trips_detail_luggage_PC);
        }
        return "";
    }
}
