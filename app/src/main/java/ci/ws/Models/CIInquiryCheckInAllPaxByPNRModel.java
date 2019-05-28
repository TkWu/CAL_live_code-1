package ci.ws.Models;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CICheckInAllPaxReq;
import ci.ws.Models.entities.CICheckInPax_ItineraryInfoEntity;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.CIWSCommon;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by joannyang on 16/6/6.
 */
public class CIInquiryCheckInAllPaxByPNRModel extends CIWSBaseModel {

    public interface InquiryCheckInAllPaxByPNRCallBack {

        /**
         * InquiryCheckInAllPaxByPNRFromWS 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param CheckInList Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryCheckInAllPaxSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList );
        /**
         * InquiryCheckInAllPaxByPNRFromWS 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryCheckInAllPaxError(String rt_code, String rt_msg );
    }

    private static final String API_NAME_ALL_PAX_BY_PNR    = "/CIAPP/api/InquiryCheckInAllPaxByPNR";
    private static final String API_NAME_ALL_PAX_BY_TICKET    = "/CIAPP/api/InquiryCheckInAllPaxByTicket";

    private              String     m_testFileName      = "";
    private              boolean    m_bIsOnSuccess      = true;

    private enum eParaTag {

        Now_Pnr,

        Other,

        Language,
        version,
    }

    private String m_strAPIName = "";
    private InquiryCheckInAllPaxByPNRCallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return m_strAPIName;
    }

    public void setCallback(InquiryCheckInAllPaxByPNRCallBack callback){
        this.m_Callback = callback;
    }



    /**
     * 3.1.1.	InquiryCheckInAllPaxByPNR()
     * 功能說明:
     * 對應1A API : DCSIDC_CPRIdentification
     */
    public void InquiryCheckInAllPaxByPNR( String strNowPnrId, ArrayList<String> arSegmentNo ,String strOtherPnrId, String strFirstName, String strLastName ){

        m_strAPIName = API_NAME_ALL_PAX_BY_PNR;

        try {

            CICheckInAllPaxReq checkInEntity = new CICheckInAllPaxReq();
            checkInEntity.setNowPnr(strNowPnrId, arSegmentNo);
            checkInEntity.setOtherPnr(strOtherPnrId,strFirstName,strLastName);

            String strRequest = GsonTool.toJson(checkInEntity);
            m_jsBody = new JSONObject(strRequest);

            m_jsBody.put( eParaTag.Language.name(),    CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put(eParaTag.version.name(), WSConfig.DEF_API_VERSION);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    /**
     * 3.1.1.	InquiryCheckInAllPaxByPNR()
     * 功能說明:
     * 對應1A API : DCSIDC_CPRIdentification
     */
    public void InquiryCheckInAllPaxByTicket( String strNowPnrId, ArrayList<String> arSegmentNo ,String strTicketNo, String strFirstName, String strLastName ){

        m_strAPIName = API_NAME_ALL_PAX_BY_TICKET;

        try {

            CICheckInAllPaxReq checkInEntity = new CICheckInAllPaxReq();
            checkInEntity.setNowPnr(strNowPnrId, arSegmentNo);
            checkInEntity.setOtherTicket(strTicketNo,strFirstName,strLastName);

            String strRequest = GsonTool.toJson(checkInEntity);
            m_jsBody = new JSONObject(strRequest);

            m_jsBody.put( eParaTag.Language.name(),    CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put(eParaTag.version.name(), WSConfig.DEF_API_VERSION);

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

            JSONArray jsarPax_info = GsonTool.getJSONArrayFromJsobject(jsData, "pax_info");
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
                pax_infoEntity.Pax_Type     = GsonTool.getStringFromJsobject(jspax_info, "Pax_Type");

                JSONArray jsarItinerary_Info = GsonTool.getJSONArrayFromJsobject(jspax_info, "Itinerary_Info");
                int iInfolength = jsarItinerary_Info.length();
                for ( int iJdx = 0; iJdx < iInfolength; iJdx++ ){
                    JSONObject jsItInfo =  jsarItinerary_Info.getJSONObject(iJdx);
                    CICheckInPax_ItineraryInfoEntity itineraryInfoEntity = GsonTool.toObject(jsItInfo.toString(), CICheckInPax_ItineraryInfoEntity.class);

                    itineraryInfoEntity.Is_Black    = "Y".equals(GsonTool.getStringFromJsobject(jsItInfo, "Is_Black"));
                    itineraryInfoEntity.Is_Check_In = "Y".equals(GsonTool.getStringFromJsobject(jsItInfo, "Is_Check_In"));
                    itineraryInfoEntity.Is_Do_Check_In  = "Y".equals(GsonTool.getStringFromJsobject(jsItInfo, "Is_Do_Check_In"));
                    //2016-08-10 ryan 新增是否可以取消Check-in 以及 是否可換位
                    itineraryInfoEntity.Is_Do_Cancel_Check_In   = "Y".equals(GsonTool.getStringFromJsobject(jsItInfo, "Is_Do_Cancel_Check_In"));
                    itineraryInfoEntity.Is_Change_Seat          = "Y".equals(GsonTool.getStringFromJsobject(jsItInfo, "Is_Change_Seat"));

                    //2016-06-21 modifly by ryan for 統一調整Flight number 為四碼, 不足則捕0
                    itineraryInfoEntity.Flight_Number = CIWSCommon.ConvertFlightNumber(itineraryInfoEntity.Flight_Number);
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
            m_Callback.onInquiryCheckInAllPaxSuccess(respBody.rt_code, respBody.rt_msg, CheckInResp);
        }

    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
        m_testFileName = "InquiryCheckInAllPaxByPNR";
            if(TextUtils.isEmpty(m_testFileName)){
                if ( null != m_Callback ){
                    m_Callback.onInquiryCheckInAllPaxError(code, strMag);
                }
            } else {
                /*Unit test測試用*/
                if(m_bIsOnSuccess){
                    DecodeResponse_Success( ResultCodeCheck(getJsonFile(m_testFileName + ".json")) ,"");
                } else {
                    CIWSResult result = ResultCodeCheck(getJsonFile(m_testFileName + ".json"));
                    if ( null != m_Callback ){
                        m_Callback.onInquiryCheckInAllPaxError(result.rt_code, result.rt_msg);
                    }
                }
            }
        } else {
            if ( null != m_Callback ){
                m_Callback.onInquiryCheckInAllPaxError(code, strMag);
            }
        }

    }

    /**
     * 單元測試時用來設定測試檔
     * @param testFileName 檔名
     */
    public void setTestFileName(String testFileName) {
        this.m_testFileName = testFileName;
    }

    /**
     * 單元測試時用來設定回呼
     * @param bIsOnSuccess 是否回呼onSuccess
     */
    public void setIsOnSuccess(boolean bIsOnSuccess) {
        this.m_bIsOnSuccess = bIsOnSuccess;
    }
}
