package ci.ws.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CICheckInEdiaAPIS_ItineraryInfo_Resp;
import ci.ws.Models.entities.CICheckInEditAPIS_Req;
import ci.ws.Models.entities.CICheckInEditAPIS_Resp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by ryan on 2018/09/02
 */
public class CICheckInEditAPISModel extends CIWSBaseModel {

    public interface CIEditAPISCallBack {

        /**
         * Api: EditAPISFromWS 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onEditAPISSuccess(String rt_code, String rt_msg, String strNeedVISA, ArrayList<CICheckInEditAPIS_Resp> arPaxInfo);
        /**
         * Api: EditAPISFromWS 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onEditAPISError(String rt_code, String rt_msg);
    }

    private static final String API_NAME_EDIT_APIS    = "/CIAPP/api/EditApis";

    private              String     m_testFileName      = "";
    private              boolean    m_bIsOnSuccess      = true;

    private                 Gson m_gsonBuilder = null;
    private enum eParaTag {
        Pax_Info,
        version,
        language
    }

    private CIEditAPISCallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME_EDIT_APIS;
    }

    public void setCallback(CIEditAPISCallBack callback){
        this.m_Callback = callback;
    }

    private Gson getGson(){

        if ( null == m_gsonBuilder ){

            m_gsonBuilder = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
        }

        return m_gsonBuilder;
    }

    /**
     * 3.1.1.	EditApis
     * 功能說明:(檢查Apis資料是否齊全)
     */
    public void EditAPIS( ArrayList<CICheckInEditAPIS_Req> arEditAPISPaxInfo ){

        try {

            String strRequest = getGson().toJson(arEditAPISPaxInfo);
            m_jsBody = new JSONObject();

            JSONArray jsonArray = new JSONArray(strRequest);

            m_jsBody.put(eParaTag.Pax_Info.name(),  jsonArray );
            m_jsBody.put(eParaTag.version.name(),   WSConfig.DEF_API_VERSION);
            m_jsBody.put(eParaTag.language.name(),  CIApplication.getLanguageInfo().getWSLanguage());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();

    }


    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        String strNeedVISA = "";

        try {
            JSONObject jsData = new JSONObject(respBody.strData);
            strNeedVISA = jsData.optString("Need_Visa", "N");
            JSONArray jsarPax_info = getJSONArrayFromJsobject(jsData, "Pax_Info");

            if( 0 == jsarPax_info.length() ) {
                SendError_Response_can_not_parse();
                return ;
            }

            final int iLen = jsarPax_info.length();

            ArrayList<CICheckInEditAPIS_Resp> arPaxInfo = new ArrayList<>();

            for( int iPos = 0 ; iPos < iLen ; iPos++ ) {

                JSONObject jspax_info = jsarPax_info.getJSONObject(iPos);
                CICheckInEditAPIS_Resp pax_info = new CICheckInEditAPIS_Resp();

                pax_info.First_Name   = getStringFromJsobject(jspax_info, "First_Name");
                pax_info.Last_Name    = getStringFromJsobject(jspax_info, "Last_Name");
                pax_info.Pnr_Id       = getStringFromJsobject(jspax_info, "Pnr_Id");
                pax_info.Uci          = getStringFromJsobject(jspax_info, "Uci");
                pax_info.Pax_Type     = getStringFromJsobject(jspax_info, "Pax_Type");

                JSONArray jsarItinerary_Info = getJSONArrayFromJsobject(jspax_info, "Itinerary_Info");

                int iInfolength = jsarItinerary_Info.length();
                for ( int iJdx = 0; iJdx < iInfolength; iJdx++ ){
                    JSONObject jsItInfo =  jsarItinerary_Info.getJSONObject(iJdx);
                    CICheckInEdiaAPIS_ItineraryInfo_Resp itineraryInfoEntity = getGson().fromJson(jsItInfo.toString(), CICheckInEdiaAPIS_ItineraryInfo_Resp.class);

                    pax_info.Itinerary_Info.add(itineraryInfoEntity);
                }

                arPaxInfo.add(pax_info);

            }

            if (null != m_Callback) {
                m_Callback.onEditAPISSuccess(respBody.rt_code, respBody.rt_msg, strNeedVISA, arPaxInfo);
            }
        }  catch (Exception e) {
            e.printStackTrace();
            SendError_Response_can_not_parse();
            return;
        }

    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( null != m_Callback ){
            m_Callback.onEditAPISError(code, strMag);
        }

    }

}
