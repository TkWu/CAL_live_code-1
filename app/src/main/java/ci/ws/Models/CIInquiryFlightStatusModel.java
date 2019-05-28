package ci.ws.Models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIFlightStatusReq;
import ci.ws.Models.entities.CIFlightStatusResp;
import ci.ws.Models.entities.CIFlightStatus_infoEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.CIDisplayDateTimeInfo;
import ci.ws.cores.object.CIWSCommon;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/4/27.
 * 功能說明: 依查詢方式by route或by flight no.查詢出航班狀態
 * 對應CI API : GetFlightInfo
 */
public class CIInquiryFlightStatusModel extends CIWSBaseModel{

    public interface FlightStatusCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param flightStatusResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onFlightStatusSuccess( String rt_code, String rt_msg, CIFlightStatusResp flightStatusResp );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onFlightStatusError( String rt_code, String rt_msg );
    }

    private FlightStatusCallBack m_flightCallBack = null;
    private static final String API_NAME = "/CIAPP/api/InquiryFlightStatus";

    private enum eParaTag {

        search_type("search_type"),
        departure_station("departure_station"),
        arrival_station("arrival_station"),
        flight_date("flight_date"),
        flight_number("flight_number"),
        flight_carrier("flight_carrier"),
        by_depature_arrival_date("by_depature_arrival_date"),
        language("language"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private enum eRespParaTag {

        flight_info("flight_info");

        private String strTag = "";

        eRespParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    public CIInquiryFlightStatusModel(FlightStatusCallBack flightCallBack){
        this.m_flightCallBack = flightCallBack;
    }

    public void InquiryFlightStatusFromWS( CIFlightStatusReq req, String strLanguage, String strVersion ){

//        req = new CIFlightStatusReq();
//        req.search_type = CIFlightStatusReq.BY_ROUTE;
//        req.departure_station = "TPE";
//        req.arrival_station = "HKG";
//        req.flight_date = "2016-04-27";
//        req.by_depature_arrival_date = "1";
//
//        req.search_type = CIFlightStatusReq.BY_FLIGHT;
//        req.flight_number = "0051";
//        req.flight_carrier= "CI";
//        req.flight_date = "2016-04-27";
//        req.by_depature_arrival_date = "1";

        try {
            m_jsBody = new JSONObject(GsonTool.toJson(req));

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
        CIFlightStatusResp flightStatusResp = new CIFlightStatusResp();
        Type listType = new TypeToken<List<CIFlightStatus_infoEntity>>(){}.getType();
        try{
            JSONObject jsObk = new JSONObject(respBody.strData);
            JSONArray jsArray = GsonTool.getJSONArrayFromJsobject(jsObk, eRespParaTag.flight_info.getString());

            if ( null != jsArray ){
                flightStatusResp.arFlightList = gson.fromJson( jsArray.toString(), listType);
            }
        } catch ( Exception e ){
            e.printStackTrace();
        }

        if ( null == flightStatusResp.arFlightList || flightStatusResp.arFlightList.size() <= 0 ){
            SendError_Response_can_not_parse();
            return;
        }

        //2016-06-20 modifly by ryan for 統一調整Flight number 為四碼, 不足則捕0
        for ( CIFlightStatus_infoEntity flight : flightStatusResp.arFlightList ){
            flight.flight_number = CIWSCommon.ConvertFlightNumber(flight.flight_number);

            //轉換顯示時間
            CIDisplayDateTimeInfo ciDisDeparture = CIWSCommon.ConvertDisplayDateTime(flight.stdd, flight.stdt, flight.etdd, flight.etdt, flight.atdd, flight.atdt);
            flight.strDisDepartureDate = ciDisDeparture.strDisplayDate;
            flight.strDisDepartureTime = ciDisDeparture.strDisplayTime;
            flight.strDisDepartureName = ciDisDeparture.strDisplayTagName;

            CIDisplayDateTimeInfo ciDisArrival = CIWSCommon.ConvertDisplayDateTime(flight.stad, flight.stat, flight.etad, flight.etat, flight.atad, flight.atat);
            flight.strDisArrivalDate = ciDisArrival.strDisplayDate;
            flight.strDisArrivalTime = ciDisArrival.strDisplayTime;
            flight.strDisArrivalName = ciDisArrival.strDisplayTagName;
        }
        //
        if ( null != m_flightCallBack ){
            m_flightCallBack.onFlightStatusSuccess(respBody.rt_code, respBody.rt_msg, flightStatusResp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.FlightStatus)) ,"");
        } else {
            if ( null != m_flightCallBack ){
                m_flightCallBack.onFlightStatusError( code, strMag );
            }
        }
    }
}
