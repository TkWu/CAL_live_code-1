package ci.ws.Models;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.CIDisplayDateTimeInfo;
import ci.ws.cores.object.CIWSCommon;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/26.
 * 3.1.	Check In(預辦登機)
 */
public class CIInquiryCheckInModel extends CIWSBaseModel {

    public interface InquiryCheckInCallBack {

        /**
         * InquiryCheckIn成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param CheckInList Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryCheckInSuccess( String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList );
        /**
         * InquiryCheckIn失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryCheckInError( String rt_code, String rt_msg );
    }

    private static final String API_NAME_CARD_NO    = "/CIAPP/api/InquiryCheckInByCard";
    private static final String API_NAME_PNR        = "/CIAPP/api/InquiryCheckInByPNR";
    private static final String API_NAME_TICKET     = "/CIAPP/api/InquiryCheckInByTicket";

    private static       String s_testFileName = "";
    private static       boolean s_bIsOnSuccess = true;
    private enum eParaTag {

        Card_Id,
        Pnr_id,
        First_Name,
        Last_Name,
        Ticket,

        Language,
        version,
        PNR_List,
    }

    private String m_strAPIName = "";
    private InquiryCheckInCallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return m_strAPIName;
    }

    public void setCallback(InquiryCheckInCallBack callback){
        this.m_Callback = callback;
    }

    /**
    * 3.1.1.	InquiryCheckInByCard(查詢欲報到行程使用卡號)
    * 功能說明: 查詢欲報到行程使用卡號
    * 對應1A API : DCSIDC_CPRIdentification
     *
     * 2018-06-29 第二階段CR 新增FirstName LastName, PNRList 避免PNRId重複使用導致看到別人的資料
     * */
    public void InquiryCheckInByCard( String strCardNo, String First_Name, String Last_Name, Set<String> pnrData ){

        m_strAPIName = API_NAME_CARD_NO;
        m_jsBody = new JSONObject();

        try {

            m_jsBody.put( eParaTag.Card_Id.name(),      strCardNo);
            m_jsBody.put( eParaTag.First_Name.name(),   First_Name);
            m_jsBody.put( eParaTag.Last_Name.name(),    Last_Name);
            m_jsBody.put( eParaTag.Language.name(),     CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.version.name(),      WSConfig.DEF_API_VERSION);

            //
            //卡號登入
            JSONArray array = new JSONArray();
            Iterator it = pnrData.iterator();
            while (it.hasNext()) {
                array.put(it.next());
            }
            m_jsBody.put( eParaTag.PNR_List.name(),  array);
            //

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    /**
     * 3.1.1.	InquiryCheckInByPNR (選擇航班_PNR)
     * 功能說明: 使用 PNR/First Name/Last  Name取得PNR資料
     * 對應1A API : DCSIDC_CPRIdentification
     * */
    public void InquiryCheckInByPNR( String strPnr_id, String First_Name, String Last_Name ){

        m_strAPIName = API_NAME_PNR;
        m_jsBody = new JSONObject();

        try {
            m_jsBody.put( eParaTag.Pnr_id.name(),       strPnr_id);
            m_jsBody.put( eParaTag.First_Name.name(),   First_Name);
            m_jsBody.put( eParaTag.Last_Name.name(),    Last_Name);
            m_jsBody.put( eParaTag.Language.name(),     CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.version.name(),      WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    /**
     * 3.1.1.	InquiryCheckInByTicket(選擇航班_電子機票)
     * 功能說明: 使用 Ticket/First Name/Last  Name取得PNR資料
     * 對應1A API : DCSIDC_CPRIdentification
     */
    public void InquiryCheckInByTicket( String strTicket, String First_Name, String Last_Name ){

        m_strAPIName = API_NAME_TICKET;
        m_jsBody = new JSONObject();

        try {

            m_jsBody.put( eParaTag.Ticket.name(),      strTicket);
            m_jsBody.put( eParaTag.First_Name.name(),  First_Name);
            m_jsBody.put( eParaTag.Last_Name.name(),   Last_Name);
            m_jsBody.put( eParaTag.Language.name(),    CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.version.name(),     WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        CICheckInAllPaxResp CheckInResp = new CICheckInAllPaxResp();

        try {
            JSONObject jsData = new JSONObject(respBody.strData);

            JSONArray jsarPax_info =GsonTool.getJSONArrayFromJsobject(jsData, "pax_info");
            if ( null == jsarPax_info ){
                SendError_Response_can_not_parse();
                return;
            }
            int ilength = jsarPax_info.length();
            for ( int iIdx = 0; iIdx < ilength; iIdx++ ){
                JSONObject jspax_info = jsarPax_info.getJSONObject(iIdx);
                CICheckInPax_InfoEntity pax_infoEntity = new CICheckInPax_InfoEntity();
                pax_infoEntity.First_Name   = GsonTool.getStringFromJsobject(jspax_info, "First_Name");
                pax_infoEntity.Last_Name    = GsonTool.getStringFromJsobject(jspax_info, "Last_Name");
                pax_infoEntity.Pnr_Id       = GsonTool.getStringFromJsobject(jspax_info, "Pnr_Id");
                pax_infoEntity.Uci          = GsonTool.getStringFromJsobject(jspax_info, "Uci");

                JSONArray jsarItinerary_Info = GsonTool.getJSONArrayFromJsobject(jspax_info, "Itinerary_Info");
                int iInfolength = jsarItinerary_Info.length();
                for ( int iJdx = 0; iJdx < iInfolength; iJdx++ ){
                    JSONObject jsItInfo =  jsarItinerary_Info.getJSONObject(iJdx);
                    CICheckInPax_ItineraryInfoEntity itineraryInfoEntity = GsonTool.toObject(jsItInfo.toString(), CICheckInPax_ItineraryInfoEntity.class);

                    itineraryInfoEntity.Is_Black    = "Y".equals(GsonTool.getStringFromJsobject(jsItInfo, "Is_Black")) ? true:false;
                    itineraryInfoEntity.Is_Check_In = "Y".equals(GsonTool.getStringFromJsobject(jsItInfo, "Is_Check_In")) ? true:false;
                    itineraryInfoEntity.Is_Do_Check_In  = "Y".equals(GsonTool.getStringFromJsobject(jsItInfo, "Is_Do_Check_In")) ? true:false;

                    //2016-06-21 modifly by ryan for 統一調整Flight number 為四碼, 不足則捕0
                    itineraryInfoEntity.Flight_Number = CIWSCommon.ConvertFlightNumber(itineraryInfoEntity.Flight_Number);
                    //
                    //根據實際,表定,預計日期以及時間判斷適合顯示在畫面上的時間
                    CIDisplayDateTimeInfo DisDate = CIWSCommon.ConvertDisplayDateTimeForCPR(itineraryInfoEntity.Departure_Date, itineraryInfoEntity.Departure_Time,
                            itineraryInfoEntity.Estimate_Departure_Date, itineraryInfoEntity.Estimate_Departure_Time,
                            itineraryInfoEntity.Actual_Departure_Date, itineraryInfoEntity.Actual_Departure_Time);

                    itineraryInfoEntity.Display_Departure_Date = DisDate.strDisplayDate;
                    itineraryInfoEntity.Display_Departure_Time = DisDate.strDisplayTime;

                    CIDisplayDateTimeInfo DisDateGmt = CIWSCommon.ConvertDisplayDateTimeForCPR(itineraryInfoEntity.Departure_Date_Gmt, itineraryInfoEntity.Departure_Time_Gmt,
                            itineraryInfoEntity.Estimate_Departure_Date_Gmt, itineraryInfoEntity.Estimate_Departure_Time_Gmt,
                            itineraryInfoEntity.Actual_Departure_Date_Gmt, itineraryInfoEntity.Actual_Departure_Time_Gmt);

                    itineraryInfoEntity.Display_Departure_Date_Gmt = DisDateGmt.strDisplayDate;
                    itineraryInfoEntity.Display_Departure_Time_Gmt = DisDateGmt.strDisplayTime;
                    //
                    CIDisplayDateTimeInfo DisAcDate = CIWSCommon.ConvertDisplayDateTimeForCPR(itineraryInfoEntity.Arrival_Date, itineraryInfoEntity.Arrival_Time,
                            itineraryInfoEntity.Estimate_Arrival_Date, itineraryInfoEntity.Estimate_Arrival_Time,
                            itineraryInfoEntity.Actual_Arrival_Date, itineraryInfoEntity.Actual_Arrival_Time);

                    itineraryInfoEntity.Display_Arrival_Date = DisAcDate.strDisplayDate;
                    itineraryInfoEntity.Display_Arrival_Time = DisAcDate.strDisplayTime;

                    CIDisplayDateTimeInfo DisAcDateGmt = CIWSCommon.ConvertDisplayDateTimeForCPR(itineraryInfoEntity.Arrival_Date_Gmt, itineraryInfoEntity.Arrival_Time_Gmt,
                            itineraryInfoEntity.Estimate_Arrival_Date_Gmt, itineraryInfoEntity.Estimate_Arrival_Time_Gmt,
                            itineraryInfoEntity.Actual_Arrival_Date_Gmt, itineraryInfoEntity.Actual_Arrival_Time_Gmt);

                    itineraryInfoEntity.Display_Arrival_Date_Gmt = DisAcDateGmt.strDisplayDate;
                    itineraryInfoEntity.Display_Arrival_Time_Gmt = DisAcDateGmt.strDisplayTime;
                    //
                    pax_infoEntity.m_Itinerary_InfoList.add(itineraryInfoEntity);
                }
                CheckInResp.add(pax_infoEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
            SendError_Response_can_not_parse();
            return;
        }

        if ( null != m_Callback ){
            m_Callback.onInquiryCheckInSuccess(respBody.rt_code, respBody.rt_msg, CheckInResp);
        }

    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            if(TextUtils.isEmpty(s_testFileName)){
                DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryCheckInByCardNo)) ,"");
            } else {
                /**Unit test測試用*/
                if(true == s_bIsOnSuccess){
                    DecodeResponse_Success( ResultCodeCheck(getJsonFile(s_testFileName)) ,"");
                } else {
                    CIWSResult result = ResultCodeCheck(getJsonFile(s_testFileName));
                    if ( null != m_Callback ){
                        m_Callback.onInquiryCheckInError(result.rt_code, result.rt_msg);
                    }
                }
            }
        } else {
            if ( null != m_Callback ){
                m_Callback.onInquiryCheckInError(code, strMag);
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
