package ci.ws.Models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CITimeTableListResp;
import ci.ws.Models.entities.CITimeTableReq;
import ci.ws.Models.entities.CITimeTable_InfoEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.CIWSCommon;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/4/27.
 * 畫面：TimeTable
 * 功能說明: 使用出發地、目的地、出發日期、回程日期取得班機時刻
 * 對應1A API : Air_MultiAvailability
 */
public class CIInquiryTimeTableModel extends CIWSBaseModel {

    public interface TimetableCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param timetableList Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onTimeTableSuccess( String rt_code, String rt_msg, CITimeTableListResp timetableList );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onTimeTableError( String rt_code, String rt_msg );
    }

    /**其餘參數參照
     * {@link CITimeTableReq}*/
    private enum eParaTag {

        language("language"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private enum eRespParaTag {

        departure_flight("departure_flight_info"),
        return_flight("return_flight_info"),
        journey_status("journey_status");

        private String strTag = "";

        eRespParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/CIAPP/api/InquiryTimetable";
    private TimetableCallBack m_Callback = null;

    public CIInquiryTimeTableModel( TimetableCallBack callback ){

        this.m_Callback = callback;
    }

    public void InquiryTimeTableFromWS( CITimeTableReq Timereq, String strLanguage, String strVersion ){

        try {
//            Timereq = new CITimeTableReq();
//            Timereq.departure = "TPE";
//            Timereq.arrival = "BKK";
//            Timereq.departure_date = "2016-04-28";
//            Timereq.return_date = "2016-05-03";

            m_jsBody = new JSONObject(GsonTool.toJson(Timereq));

            //補上固定參數
            m_jsBody.put(eParaTag.language.getString(),  strLanguage );
            m_jsBody.put(eParaTag.version.getString(),   strVersion);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        Gson gson = new Gson();
        CITimeTableListResp Timetablelist = new CITimeTableListResp();
        Type listType = new TypeToken<List<CITimeTable_InfoEntity>>(){}.getType();
        try{
            JSONObject jsObk = new JSONObject(respBody.strData);
            JSONArray jsarDeparture = GsonTool.getJSONArrayFromJsobject(jsObk, eRespParaTag.departure_flight.getString());
            JSONArray jsarReturn = GsonTool.getJSONArrayFromJsobject(jsObk, eRespParaTag.return_flight.getString());

            if ( null != jsarDeparture ){
                Timetablelist.arDepartureList = gson.fromJson( jsarDeparture.toString(), listType);
                int ilength = jsarDeparture.length();
                for ( int iIdx = 0; iIdx < ilength; iIdx++ ){
                    JSONObject jsInfo = jsarDeparture.getJSONObject(iIdx);
                    JSONArray arTmp = jsInfo.getJSONArray(eRespParaTag.journey_status.getString());
                    if ( null != arTmp && null != arTmp.getString(0) ){
                        Timetablelist.arDepartureList.get(iIdx).Status = arTmp.getString(0);
                    } else {
                        Timetablelist.arDepartureList.get(iIdx).Status = CITimeTable_InfoEntity.JOURNEY_STATUS_D;
                    }
                    //2016-06-20 modifly by ryan for 統一調整Flight number 為四碼, 不足則捕0
                    Timetablelist.arDepartureList.get(iIdx).flight_number = CIWSCommon.ConvertFlightNumber(GsonTool.getIntFromJsobject(jsInfo, "flight_number"));
                    //
                }
            }
            if ( null != jsarReturn ){
                Timetablelist.arReturnList = gson.fromJson( jsarReturn.toString(), listType);
                int ilength = jsarReturn.length();
                for ( int iIdx = 0; iIdx < ilength; iIdx++ ){
                    JSONObject jsInfo = jsarReturn.getJSONObject(iIdx);
                    JSONArray arTmp = GsonTool.getJSONArrayFromJsobject(jsInfo, eRespParaTag.journey_status.getString());
                    if ( null != arTmp && null != arTmp.getString(0) ){
                        Timetablelist.arReturnList.get(iIdx).Status = arTmp.getString(0);
                    } else {
                        Timetablelist.arReturnList.get(iIdx).Status = CITimeTable_InfoEntity.JOURNEY_STATUS_D;
                    }
                    //2016-06-20 modifly by ryan for 統一調整Flight number 為四碼, 不足則捕0
                    Timetablelist.arReturnList.get(iIdx).flight_number = CIWSCommon.ConvertFlightNumber(GsonTool.getIntFromJsobject(jsInfo,"flight_number"));
                    //
                }
            }
        } catch ( Exception e ){
            e.printStackTrace();
            SendError_Response_can_not_parse();
            return;
        }
        if ( null != m_Callback ){
            m_Callback.onTimeTableSuccess( respBody.rt_code, respBody.rt_msg, Timetablelist);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.TimeTable_round)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onTimeTableError( code, strMag);
            }
        }
    }
}
