package ci.ws.Models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIBaggageInfoReq;
import ci.ws.Models.entities.CIBaggageInfoResp;
import ci.ws.Models.entities.CITimeTable_InfoEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.CIWSResultCode;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan
 * InquiryBaggageInfoByPNRAndBGNum
 * 功能說明: 以PNR或行李條號登入時帶出所有行李資訊
 */
public class CIInquiryBaggageInfoByPNRAndBGNumModel extends CIWSBaseModel {

    public interface CallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param arBaggageInfoListResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryBaggageInfoByPNRAndBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoResp> arBaggageInfoListResp );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryBaggageInfoByPNRAndBGNumError(String rt_code, String rt_msg);
    }

    private enum eParaTag {
        PNR,
        PNR_List,
        First_Name,
        Last_Name,
        Baggage_List,
        Baggage_Number,
        language,
        version,
    }

    private static final String API_NAME = "/CIAPP/api/InquiryBaggageInfoByPNRAndBGNum";
    private CallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryBaggageInfoByPNRAndBGNumModel(CallBack Callback){
        m_Callback = Callback;
    }


    public void setCallback( CallBack Callback ){
        m_Callback = Callback;
    }

    public void InquiryBaggageInfoByPNRAndBGNumFromWS( String strPnrFirstName, String strPnrLastName, ArrayList<String> arPnrList, ArrayList<CIBaggageInfoReq> BaggageList ){

        m_jsBody = new JSONObject();
        try {

            JSONObject jsPnr = new JSONObject();
            if ( null != arPnrList && arPnrList.size() > 0 ){
                String strPnrList = GsonTool.toJson(arPnrList);
                JSONArray jsPnrList = new JSONArray(strPnrList);

                jsPnr.put( eParaTag.PNR_List.name(),    jsPnrList);
                jsPnr.put( eParaTag.First_Name.name(),  strPnrFirstName);
                jsPnr.put( eParaTag.Last_Name.name(),   strPnrLastName);
            }

            m_jsBody.put( eParaTag.PNR.name(),  jsPnr);

            String strBaggageList = GsonTool.toJson(BaggageList);
            JSONArray jsBaggageList = new JSONArray(strBaggageList);

            m_jsBody.put( eParaTag.Baggage_List.name(),   jsBaggageList);
            m_jsBody.put( eParaTag.language.name(),     CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.version.name(),      WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }


    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        ArrayList<CIBaggageInfoResp> arBaggageInfoList = new ArrayList<>();

        try {

            Type listType = new TypeToken<List<CIBaggageInfoResp>>(){}.getType();
            Gson gson = new Gson();
            arBaggageInfoList = gson.fromJson( respBody.strData, listType);

        } catch (Exception e ){
            e.printStackTrace();
        }

        if ( null == arBaggageInfoList || arBaggageInfoList.size() <= 0 ){
            if ( null != m_Callback ){
                m_Callback.onInquiryBaggageInfoByPNRAndBGNumError(CIWSResultCode.NO_RESULTS, CIWSResultCode.getResultMessage(CIWSResultCode.NO_RESULTS) );
            }
        }

        if ( null != m_Callback ){
            m_Callback.onInquiryBaggageInfoByPNRAndBGNumSuccess(respBody.rt_code, respBody.rt_msg, arBaggageInfoList );
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( null != m_Callback ){
            m_Callback.onInquiryBaggageInfoByPNRAndBGNumError(code, strMag);
        }
    }
}
