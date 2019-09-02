package ci.ws.Models;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIEWalletReq;
import ci.ws.Models.entities.CIEWallet_ExtraService_List;
import ci.ws.Models.entities.CIExtraServiceResp;
import ci.ws.Models.entities.CIInquiryExtraServicesDBEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by kevincheng on 2016/5/19.
 * 文件：CI_APP_API_EWAllet.docx 16/05/19 14:47
 * 4.1.1 InquiryExtraServiceByPNRNoSIT
 * 功能說明: 使用PNR、First Name、Last Name取得ExtraService資訊
 * 對應1A API : PNR_Retrieve
 */
public class CIInquiryExtraServiceByPNRNoSITModel extends CIWSBaseModel {

    public interface CallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code, String rt_msg, CIExtraServiceResp datas);
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
        Version,
        login_token
    }

    private enum eRespParaTag {
        ExtraService,
    }

    private static final String API_NAME = "/CIAPP/api/InquiryExtraServiceByPNRNoSIT";
    private CallBack m_Callback = null;
    private RuntimeExceptionDao<CIInquiryExtraServicesDBEntity, Integer> m_dao
            = CIApplication.getDbManager().getRuntimeExceptionDao(CIInquiryExtraServicesDBEntity.class);

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryExtraServiceByPNRNoSITModel(CallBack Callback ){
        m_Callback = Callback;
    }

    public void sendReqDataToWS(CIEWalletReq reqData){

        m_jsBody = new JSONObject();

        if ( null == reqData.Card_Id ){
            reqData.Card_Id = "";
        }
        //2018-06-29 第二階段CR 新增FirstName LastName 避免PNRId重複使用導致看到別人的資料
        if ( null == reqData.First_Name_C ){
            reqData.First_Name_C = "";
        }
        if ( null == reqData.Last_Name_C ){
            reqData.Last_Name_C = "";
        }
        //
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
            m_jsBody.put( eParaTag.login_token.name(), CIWSShareManager.getAPI().getLoginToken());

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
//        if (WSConfig.Todd_WS_TESTMODE){
//            respBody = ResultCodeCheck(getJsonFile(WSConfig.extra_service_vip_act));
//        }
        CIExtraServiceResp List = null;
        try {
            JSONObject jsResp = new JSONObject(respBody.strData);

            JSONArray jsStationList = jsResp.getJSONArray(eRespParaTag.ExtraService.name());

            if ( null != jsStationList ) {
                List = GsonTool.getGson().fromJson(
                        jsStationList.toString(),
                        CIExtraServiceResp.class);
            } else {
                SendError_Response_can_not_parse();
                return;
            }
        } catch (Exception e){
            e.printStackTrace();
            SendError_Response_can_not_parse();
            return;
        }

        //撈到資料存DB
        CIInquiryExtraServicesDBEntity entity = new CIInquiryExtraServicesDBEntity();
        entity.respResult = GsonTool.toJson(List);

        saveToDB(entity);

        if ( null != m_Callback ){
            m_Callback.onSuccess(respBody.rt_code, respBody.rt_msg, List);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success(
                    ResultCodeCheck(getJsonFile(WSConfig.CInquiryExtraServiceByPNRNoSIT)) ,"");
        } else if ( null != m_Callback ){
            m_Callback.onError(code, strMag);
        }
    }

    //回傳true表示資料存入db成功
    public boolean saveToDB(CIInquiryExtraServicesDBEntity entity){
        try {
            entity.setId(0);

            m_dao.createOrUpdate(entity);
           SLog.d("BoardPassDB saveData", " "+entity.respResult);
        }catch (Exception e){
           SLog.e("ExtraSerDB Exception", e.toString());
            return false;
        }

        //若存入的資料無法被解析，表示存取失敗
        if ( null == getDataFromDB() )
            return false;

        return true;
    }

    public ArrayList<CIEWallet_ExtraService_List> getDataFromDB() {
        CIExtraServiceResp lists;
        try {
           SLog.d("ExtraSerDB daoSize"," "+m_dao.queryForAll().size());
           SLog.d("ExtraSerDB getData", ""+m_dao.queryForAll().get(0).respResult);
            lists = GsonTool.toObject(
                    m_dao.queryForAll().get(0).respResult,
                    CIExtraServiceResp.class);
        } catch (Exception e ){
           SLog.e("ExtraSerDB Exception", e.toString());
            return null;
        }
        return lists;
    }
}
