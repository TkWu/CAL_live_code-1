package ci.ws.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CICheckIn_ItineraryInfo_Resp;
import ci.ws.Models.entities.CICheckIn_Req;
import ci.ws.Models.entities.CICheckIn_Resp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by joannyang on 16/6/16.
 */
public class CICheckInModel extends CIWSBaseModel {
    public interface CheckInCallBack {

        /**
         * Api: CheckInFromWS 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onCheckInSuccess( String rt_code, String rt_msg ,ArrayList<CICheckIn_Resp> arPaxInfo);
        /**
         * Api: CheckInFromWS 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onCheckInError( String rt_code, String rt_msg );
    }

    private static final String API_NAME_CHECK_IN    = "/CIAPP/api/CheckIn";

    private              String     m_testFileName      = "";
    private              boolean    m_bIsOnSuccess      = true;

    private enum eParaTag {
        Pax_Info,
        version,
        language
    }

    private String m_strAPIName = "";
    private CheckInCallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return m_strAPIName;
    }

    public void setCallback(CheckInCallBack callback){
        this.m_Callback = callback;
    }



    /**
     * 3.1.1.	InquiryCheckIn()
     * 功能說明:
     * 對應1A API : DCSCPR_EditCPR
     */
    public void CheckIn( ArrayList<CICheckIn_Req> arCheckInPaxInfo ){

        m_strAPIName = API_NAME_CHECK_IN;

        try {

            String strRequest = GsonTool.toJson(arCheckInPaxInfo);
            m_jsBody = new JSONObject();

            JSONArray jsonArray = new JSONArray(strRequest);

            m_jsBody.put(eParaTag.Pax_Info.name(),  jsonArray );
            m_jsBody.put(eParaTag.version.name(),   WSConfig.DEF_API_VERSION);
            m_jsBody.put( eParaTag.language.name(), CIApplication.getLanguageInfo().getWSLanguage());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();

    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        try {
            JSONObject jsData = new JSONObject(respBody.strData);
            JSONArray jsarPax_info = GsonTool.getJSONArrayFromJsobject(jsData, "Pax_Info");

            if( 0 == jsarPax_info.length() ) {
                SendError_Response_can_not_parse();
                return ;
            }

            final int iLen = jsarPax_info.length();

            ArrayList<CICheckIn_Resp> arPaxInfo = new ArrayList<>();

            for( int iPos = 0 ; iPos < iLen ; iPos++ ) {

                JSONObject jspax_info = jsarPax_info.getJSONObject(iPos);
                CICheckIn_Resp pax_infoEntity = new CICheckIn_Resp();

                pax_infoEntity.First_Name   = GsonTool.getStringFromJsobject(jspax_info, "First_Name");
                pax_infoEntity.Last_Name    = GsonTool.getStringFromJsobject(jspax_info, "Last_Name");
                pax_infoEntity.Pnr_Id       = GsonTool.getStringFromJsobject(jspax_info, "Pnr_Id");
                pax_infoEntity.Uci          = GsonTool.getStringFromJsobject(jspax_info, "Uci");

                JSONArray jsarItinerary_Info = GsonTool.getJSONArrayFromJsobject(jspax_info, "Itinerary_Info");

                int iInfolength = jsarItinerary_Info.length();
                for ( int iJdx = 0; iJdx < iInfolength; iJdx++ ){
                    JSONObject jsItInfo =  jsarItinerary_Info.getJSONObject(iJdx);
                    CICheckIn_ItineraryInfo_Resp itineraryInfoEntity = GsonTool.toObject(jsItInfo.toString(), CICheckIn_ItineraryInfo_Resp.class);

                    pax_infoEntity.Itinerary_Info.add(itineraryInfoEntity);
                }

                arPaxInfo.add(pax_infoEntity);

            }

            if (null != m_Callback) {
                m_Callback.onCheckInSuccess(respBody.rt_code, respBody.rt_msg, arPaxInfo);
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
            m_Callback.onCheckInError(code, strMag);
        }

    }

}
