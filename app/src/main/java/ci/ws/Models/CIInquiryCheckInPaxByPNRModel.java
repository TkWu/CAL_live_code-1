package ci.ws.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CICheckInPaxEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/27.
 * 3.1.1.	CIInquiryCheckInPaxByPNRModel(查詢報到旅客使用PNR)
 * 功能說明: 使用 PNR/First Name/Last  Name取得PNR資料
 * 對應1A API : DCSIDC_CPRIdentification
 */
@Deprecated
public class CIInquiryCheckInPaxByPNRModel extends CIWSBaseModel {


    public interface InquiryCheckInPaxCallBack {

        /**
         * InquiryCheckInPax成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param arPaxList Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryCheckInPaxSuccess( String rt_code, String rt_msg, ArrayList<CICheckInPaxEntity>  arPaxList );
        /**
         * InquiryCheckInPax失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryCheckInPaxError( String rt_code, String rt_msg );
    }

    private enum eParaTag {

        Pnr_id,
        First_Name,
        Last_Name,

        Language,
        version,
    }

    private InquiryCheckInPaxCallBack m_Callback = null;

    private static final String API_NAME    = "/CIAPP/api/CIInquiryCheckInPaxByPNR";

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public void setCallback(InquiryCheckInPaxCallBack callback){
        this.m_Callback = callback;
    }

    public void InquiryCheckInPaxByPNR( String strPnr_id, String First_Name, String Last_Name ){

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

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        ArrayList<CICheckInPaxEntity> m_PaxList = new ArrayList<>();

        try {

            JSONObject jsData = new JSONObject(respBody.strData);
            JSONArray jsarPax_Info = GsonTool.getJSONArrayFromJsobject(jsData, "Pax_Info");
            int ilength = jsarPax_Info.length();
            for ( int iIdx = 0; iIdx < ilength; iIdx++ ){
                JSONObject jspax = jsarPax_Info.getJSONObject(iIdx);
                CICheckInPaxEntity paxEntity = GsonTool.toObject(jspax.toString(), CICheckInPaxEntity.class);

                if ( null != paxEntity ){
                    paxEntity.Is_Black = "Y".equals(GsonTool.getStringFromJsobject(jspax, "Is_Black"))? true:false;
                    m_PaxList.add(paxEntity);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            SendError_Response_can_not_parse();
            return;
        }

        if ( null != m_Callback  ) {
            m_Callback.onInquiryCheckInPaxSuccess(respBody.rt_code, respBody.rt_msg, m_PaxList);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( null != m_Callback ){
            m_Callback.onInquiryCheckInPaxError(code, strMag);
        }
    }
}
