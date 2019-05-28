package ci.ws.Models;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIBoardPassResp;
import ci.ws.Models.entities.CIBoardPassRespItineraryList;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;
import ci.ws.Models.entities.CIBoardPassResp_PaxInfo;
import ci.ws.Models.entities.CIBoardPassResp_PnrInfo;
import ci.ws.Models.entities.CIEWalletReq;
import ci.ws.Models.entities.CIInquiryBoardPassDBEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.CIWSCommon;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.CIWSResultCode;
import ci.ws.define.WSConfig;

/**
 * Created by jlchen on 16/5/31.
 * 文件：CI_APP_API_BoardingPass.docx
 * 4.1.1 InquiryBoardingPass
 * 功能說明:使用Card No、PNR(List)取得Ewallet BoardingPass資訊
 * 對應1A API : PNR_SearchByFrequentFlyer、DCSIDC_CPRIdentification
 * 對應CI DB : TCDB.TCTBPP4
 */
public class CIInquiryBoardingPassModel extends CIWSBaseModel {

    public interface CallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas);
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
        Card_Id,
        PNR_List,
        First_Name,
        Last_Name,
        PNR_ID ,
        TICKET,
        Language,
        Version
    }

    private static final String API_NAME = "/CIAPP/api/InquiryBoardingPass";
    private CallBack m_Callback = null;

    private RuntimeExceptionDao<CIInquiryBoardPassDBEntity, Integer> m_dao
            = CIApplication.getDbManager().getRuntimeExceptionDao(CIInquiryBoardPassDBEntity.class);

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryBoardingPassModel(CallBack Callback ){
        m_Callback = Callback;
    }

    //2018-06-29 第二階段CR 新增FirstName LastName 避免PNRId重複使用導致看到別人的資料
    public void sendReqDataToWSByPNRListAndCardNo(String strCard, String strFirstName, String strLastName, Set<String> pnrData){

        CIEWalletReq req    = new CIEWalletReq();
        req.Card_Id         = strCard;
        req.First_Name_C    = strFirstName;
        req.Last_Name_C     = strLastName;
        req.Pnr_List        = pnrData;

        sendReqDataToWS(req);
    }

    public void sendReqDataToWSByTicket(String ticket,
                                        String strFirstName,
                                        String strLastName){
        CIEWalletReq req    = new CIEWalletReq();
        req.Ticket          = ticket;
        req.First_Name_T    = strFirstName;
        req.Last_Name_T     = strLastName;

        sendReqDataToWS(req);
    }

    public void sendReqDataToWSByPNRNo(String PNRNo,
                                       String strFirstName,
                                       String strLastName){

        Set<String> pnrList = new LinkedHashSet<>();
        if ( null != PNRNo && 6 == PNRNo.length() ){
            pnrList.add(PNRNo);
        }

        CIEWalletReq req    = new CIEWalletReq();
        req.Pnr_List        = pnrList;
        req.PNR_ID          = PNRNo;
        req.First_Name_P    = strFirstName;
        req.Last_Name_P     = strLastName;

        sendReqDataToWS(req);
    }

    protected void sendReqDataToWS(CIEWalletReq reqData){
        m_jsBody = new JSONObject();

        if ( null == reqData.Card_Id ){
            reqData.Card_Id = "";
        }
        if ( null == reqData.First_Name_C ){
            reqData.First_Name_C = "";
        }
        if ( null == reqData.Last_Name_C ){
            reqData.Last_Name_C = "";
        }
        if ( null == reqData.Ticket ){
            reqData.Ticket = "";
        }
        if ( null == reqData.First_Name_T ){
            reqData.First_Name_T = "";
        }
        if ( null == reqData.Last_Name_T ){
            reqData.Last_Name_T = "";
        }
        if ( null == reqData.PNR_ID ){
            reqData.PNR_ID = "";
        }
        if ( null == reqData.First_Name_P ){
            reqData.First_Name_P = "";
        }
        if ( null == reqData.Last_Name_P ){
            reqData.Last_Name_P = "";
        }
        if ( null == reqData.Pnr_List ){
            reqData.Pnr_List = new LinkedHashSet<>();
        }

        try {
            //卡號登入
            JSONArray array = new JSONArray();
            Iterator it = reqData.Pnr_List.iterator();
            while (it.hasNext()) {
                array.put(it.next());
            }
//                for ( int i = 0 ; i < reqData.Pnr_List.size() ; i ++ ){
//                array.put(reqData.Pnr_List.);
//            }
            m_jsBody.put( eParaTag.Card_Id.name(),   reqData.Card_Id);
            m_jsBody.put( eParaTag.First_Name.name(),reqData.First_Name_C);
            m_jsBody.put( eParaTag.Last_Name.name(), reqData.Last_Name_C);
            m_jsBody.put( eParaTag.PNR_List.name(),  array);

            //TICKET登入
            JSONObject jsTicket = new JSONObject();
            jsTicket.put( eParaTag.TICKET.name(),    reqData.Ticket);
            jsTicket.put( eParaTag.First_Name.name(),reqData.First_Name_T);
            jsTicket.put( eParaTag.Last_Name.name(), reqData.Last_Name_T);
            m_jsBody.put( eParaTag.TICKET.name(),    jsTicket);

            //PNR登入
            JSONObject jsPnrId = new JSONObject();
            jsPnrId.put( eParaTag.PNR_ID.name(),     reqData.PNR_ID);
            jsPnrId.put( eParaTag.First_Name.name(), reqData.First_Name_P);
            jsPnrId.put( eParaTag.Last_Name.name(),  reqData.Last_Name_P);
            m_jsBody.put( eParaTag.PNR_ID.name(),    jsPnrId);

            m_jsBody.put( eParaTag.Language.name(), CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.Version.name(),  WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();

            if ( null != m_Callback ){
                SendError_Response_can_not_parse();
            }
            return;
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        Gson gson = new Gson();
        CIBoardPassResp Resp = null;

        try {
            Resp = gson.fromJson( respBody.strData, CIBoardPassResp.class);

        } catch (Exception e ){
            e.printStackTrace();
        }

        if ( null == Resp || null == Resp.Pnr_Info ){
            if ( null != m_Callback ){
                m_Callback.onError( CIWSResultCode.NO_RESULTS, CIWSResultCode.getResultMessage(CIWSResultCode.NO_RESULTS) );
            }
            return;
        }
        // 確認
        CIBoardPassResp BoardingPassInfo = new CIBoardPassResp();
        //2016-06-20 modifly by ryan for 統一調整Flight number 為四碼, 不足則捕0
        for ( CIBoardPassResp_PnrInfo Pnr_Info : Resp.Pnr_Info ){
            //
            CIBoardPassResp_PnrInfo newPnr_Info = (CIBoardPassResp_PnrInfo)Pnr_Info.clone();
            newPnr_Info.Itinerary = new ArrayList<CIBoardPassResp_Itinerary>();
            //
            if(null == Pnr_Info.Itinerary) {
                continue;
            }
            for ( CIBoardPassResp_Itinerary itinerary : Pnr_Info.Itinerary ){
                //從PnrInfo把id塞入itinerary
                itinerary.Pnr_Id = Pnr_Info.Pnr_Id;
                CIBoardPassResp_Itinerary newItinerary = (CIBoardPassResp_Itinerary)itinerary.clone();
                newItinerary.Pax_Info = new ArrayList<CIBoardPassResp_PaxInfo>();
                //
                newItinerary.Flight_Number = CIWSCommon.ConvertFlightNumber(itinerary.Flight_Number);
                //
                if(null == itinerary.Pax_Info) {
                    continue;
                }
                for ( CIBoardPassResp_PaxInfo boardingpass : itinerary.Pax_Info ){
                    //過濾該登機證是否可以顯示
                    if ( TextUtils.equals("Y", boardingpass.Is_Check_In ) &&
                            false == TextUtils.isEmpty(boardingpass.Boarding_Pass) &&
                            TextUtils.equals("Y", boardingpass.Is_Display_Boarding_Pass ) ){

                        newItinerary.Pax_Info.add(boardingpass);
                    }
                }
                if ( newItinerary.Pax_Info.size() > 0 ){
                    newPnr_Info.Itinerary.add(newItinerary);
                }
                //
            }
            if ( newPnr_Info.Itinerary.size() > 0 ){
                BoardingPassInfo.Pnr_Info.add(newPnr_Info);
            }
        }

        if ( BoardingPassInfo.Pnr_Info.size() <= 0 ){
            if ( null != m_Callback ){
                m_Callback.onError( CIWSResultCode.NO_RESULTS, CIWSResultCode.getResultMessage(CIWSResultCode.NO_RESULTS) );
            }
            return;
        }
        //
        if ( null != m_Callback ){
            m_Callback.onSuccess(respBody.rt_code, respBody.rt_msg, BoardingPassInfo);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryBoardPass)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onError(code, strMag);
            }
        }
    }

    //回傳true表示資料存入db成功
    public boolean saveDataToDB(CIInquiryBoardPassDBEntity entity){
        try {
            entity.setId(0);

            m_dao.createOrUpdate(entity);
           SLog.d("BoardPassDB saveData", " "+entity.respResult);
        }catch (Exception e){
           SLog.e("BoardPassDB Exception", e.toString());
            return false;
        }

        //若存入的資料無法被解析，表示存取失敗
        if ( null == getDataFromDB() )
            return false;

        return true;
    }

    public ArrayList<CIBoardPassResp_Itinerary> getDataFromDB() {
        CIBoardPassRespItineraryList list;
        try {
           SLog.d("BoardPassDB daoSize"," "+m_dao.queryForAll().size());
           SLog.d("BoardPassDB getData", " "+m_dao.queryForAll().get(0).respResult);
            list = GsonTool.toObject(
                    m_dao.queryForAll().get(0).respResult,
                    CIBoardPassRespItineraryList.class);
        } catch (Exception e ){
           SLog.e("BoardPassDB Exception", e.toString());
            return null;
        }
        return list;
    }
}