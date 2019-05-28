package ci.ws.Models;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Models.entities.CITripListResp;
import ci.ws.Models.entities.CITripbyCardReq;
import ci.ws.Models.entities.CITripbyPNRReq;
import ci.ws.Models.entities.CITripbyTicketReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.CIWSBookingClass;
import ci.ws.cores.object.CIWSCommon;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.CIWSHomeStatus_Code;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/4/23.
 */
public class CIInquiryTripModel extends CIWSBaseModel {

    public interface InquiryTripsCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param Tripslist Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryTripsSuccess( String rt_code, String rt_msg, CITripListResp Tripslist );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryTripsError( String rt_code, String rt_msg );
    }

    private InquiryTripsCallBack m_Callback = null;

    private static final String API_NAME_CARD_NO    = "/CIAPP/api/InquiryTripByCard";
    private static final String API_NAME_PNR        = "/CIAPP/api/InquiryTripByPNR";
    private static final String API_NAME_TICKET     = "/CIAPP/api/InquiryTripByTicket";

    private              String  m_strAPI       = "";
    private static       String  s_testFileName = "";
    private static       boolean s_bIsOnSuccess = true;

    private enum eParaTag {

        Type,
        Card_Id,
        Page_Number,
        Page_Count,
        is_pnr_status,
        PNR_List,

        Pnr_id,
        Ticket,
        First_Name,
        Last_Name,

        Language,
        version,
    }

    @Override
    public String getAPINAME() {
        return m_strAPI;
    }


    public CIInquiryTripModel(InquiryTripsCallBack listener){

        this.m_Callback = listener;
    }

    public void setCallback(InquiryTripsCallBack listener){
        this.m_Callback = listener;
    }

    /**
     * @param tripReq Request*/
    public void InquiryTripByCardNo( CITripbyCardReq tripReq ){

        m_strAPI = API_NAME_CARD_NO;
        m_jsBody = new JSONObject();
        try {

//            tripReq = new CITripbyCardReq();
//            tripReq.Card_Id = "WA0000000";
//            tripReq.Type = CITripbyCardReq.TRIP_TYPE_FLIGHTS;
//            tripReq.Page_Count = "1";
//            tripReq.Page_Number= "10";

            m_jsBody.put( eParaTag.Type.name(),        tripReq.Type);
            m_jsBody.put( eParaTag.Card_Id.name(),     tripReq.Card_Id);
            m_jsBody.put( eParaTag.Page_Number.name(), tripReq.Page_Number);
            m_jsBody.put( eParaTag.Page_Count.name(),  tripReq.Page_Count);
            m_jsBody.put( eParaTag.is_pnr_status.name(),tripReq.is_pnr_status);
            m_jsBody.put( eParaTag.Language.name(),     CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.version.name(),      WSConfig.DEF_API_VERSION);

            //2018-06-29 第二階段CR 新增FirstName LastName 避免PNRId重複使用導致看到別人的資料
            m_jsBody.put( eParaTag.First_Name.name(),   tripReq.First_Name);
            m_jsBody.put( eParaTag.Last_Name.name(),    tripReq.Last_Name);
            //
            JSONArray array = new JSONArray();
            Iterator it = tripReq.Pnr_List.iterator();
            while (it.hasNext()) {
                array.put(it.next());
            }
            m_jsBody.put( eParaTag.PNR_List.name(),     array);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    /**
     * @param tripReq Request*/
    public void InquiryTripByPNR( CITripbyPNRReq tripReq ){

        m_strAPI = API_NAME_PNR;
        m_jsBody = new JSONObject();

        try {
//            tripReq = new CITripbyPNRReq();
//            tripReq.Pnr_id = "YLFXK6";
//            tripReq.First_Name = "Okarr";
//            tripReq.Last_Name= "Test";

            m_jsBody.put( eParaTag.Pnr_id.name(),      tripReq.Pnr_id);
            m_jsBody.put( eParaTag.First_Name.name(),  tripReq.First_Name);
            m_jsBody.put( eParaTag.Last_Name.name(),   tripReq.Last_Name);
            m_jsBody.put( eParaTag.is_pnr_status.name(),  tripReq.is_pnr_status);
            m_jsBody.put( eParaTag.Language.name(),     CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.version.name(),      WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    /**
     * @param tripReq Request*/
    public void InquiryTripByTicket( CITripbyTicketReq tripReq ){

        m_strAPI = API_NAME_TICKET;
        m_jsBody = new JSONObject();

        try {

            m_jsBody.put( eParaTag.Ticket.name(),      tripReq.Ticket);
            m_jsBody.put( eParaTag.First_Name.name(),  tripReq.First_Name);
            m_jsBody.put( eParaTag.Last_Name.name(),   tripReq.Last_Name);
            m_jsBody.put( eParaTag.is_pnr_status.name(),  tripReq.is_pnr_status);
            m_jsBody.put( eParaTag.Language.name(),     CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.version.name(),      WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        Gson gson = new Gson();
        CITripListResp TripsResp = null;

        try {

            TripsResp = gson.fromJson(respBody.strData, CITripListResp.class);

            JSONObject jsdata = new JSONObject(respBody.strData);
            JSONObject jsPNR_Status = GsonTool.getJsobjectFromJsobject(jsdata, "PNR_Status");
            TripsResp.Segment_Num   = GsonTool.getStringFromJsobject(jsPNR_Status, "Segment_Num");
            TripsResp.First_Name    = GsonTool.getStringFromJsobject(jsPNR_Status, "First_Name");
            TripsResp.Last_Name     = GsonTool.getStringFromJsobject(jsPNR_Status, "Last_Name");
            TripsResp.Is_Select_Meal= GsonTool.getStringFromJsobject(jsPNR_Status, "Is_Select_Meal");
            TripsResp.Itinerary_Num = GsonTool.getStringFromJsobject(jsPNR_Status, "Itinerary_Num");
            TripsResp.PNR_Id        = GsonTool.getStringFromJsobject(jsPNR_Status, "PNR_Id");
            TripsResp.Status_Code   = GsonTool.getIntFromJsobject(jsPNR_Status, "Status_Code");
            if ( TripsResp.Status_Code == -1 ){
                TripsResp.Status_Code = CIWSHomeStatus_Code.TYPE_A_NO_TICKET;
            }
        } catch (Exception e ){
            e.printStackTrace();
        }

        if ( null == TripsResp || null == TripsResp.Itinerary_Info ){
            SendError_Response_can_not_parse();
            return;
        }

        for ( CITripListResp_Itinerary info : TripsResp.Itinerary_Info ){
            //解析客艙資訊
            info.Booking_Class_Name_Tag = CIWSBookingClass.ParseBookingClass(info.Booking_Class);

            //解析是否為中華航空
            info.bIs_Do_Tag = TextUtils.equals("Y", info.Is_Do)? true:false;

            //放置PNR 狀態
            if ( TextUtils.equals( TripsResp.PNR_Id, info.Pnr_Id ) && TextUtils.equals( TripsResp.Itinerary_Num, info.Itinerary_Num ) ){
                info.Status_Code = TripsResp.Status_Code;
            } else if ( TripsResp.PNR_Id.equals(info.Pnr_Id) ){
                info.Status_Code = CIWSHomeStatus_Code.TYPE_A_NO_TICKET;
            } else {
                info.Status_Code = CITripListResp_Itinerary.UNKNOW_STATUS_CODE;
            }

            //2016-06-20 modifly by ryan for 統一調整Flight number 為四碼, 不足則捕0
            info.Flight_Number = CIWSCommon.ConvertFlightNumber(info.Flight_Number);
            //
        }

        if ( null != m_Callback ){
            m_Callback.onInquiryTripsSuccess(respBody.rt_code, respBody.rt_msg, TripsResp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            if(TextUtils.isEmpty(s_testFileName)){
                DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.MyTripByCardNo)) ,"");
            } else {
                /**Unit test測試用*/
                if(true == s_bIsOnSuccess){
                    DecodeResponse_Success( ResultCodeCheck(getJsonFile(s_testFileName)) ,"");
                } else {
                    CIWSResult result = ResultCodeCheck(getJsonFile(s_testFileName));
                    if ( null != m_Callback ){
                        m_Callback.onInquiryTripsError(result.rt_code, result.rt_msg);
                    }
                }
            }
        } else {
            if ( null != m_Callback ){
                m_Callback.onInquiryTripsError(code, strMag);
            }
        }
    }

    /**
     * 單元測試時用來設定測試檔
     * @param testFileName 檔名
     */
    public static void setTestFileName(String testFileName) {
        s_testFileName = testFileName;
    }

    /**
     * 單元測試時用來設定回呼
     * @param bIsOnSuccess 是否回呼onSuccess
     */
    public static void setIsOnSuccess(boolean bIsOnSuccess) {
        s_bIsOnSuccess = bIsOnSuccess;
    }

}
