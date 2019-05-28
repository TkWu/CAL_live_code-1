package ci.ws.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CICancelCheckInReq;
import ci.ws.Models.entities.CICancelCheckInResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by jlchen on 2016/6/17.
 */
public class CICancelCheckInModel extends CIWSBaseModel {

    public interface CallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code, String rt_msg, CICancelCheckInResp resp);
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
        Pax_Info            ("Pax_Info"),
        First_Name          ("First_Name"),
        Last_Name           ("Last_Name"),
        Uci                 ("Uci"),
        Pnr_id              ("Pnr_id"),
        Itinerary_Info      ("Itinerary_Info"),
        Departure_Station   ("Departure_Station"),
        Arrival_Station     ("Arrival_Station"),
        Did                 ("Did"),
        version             ("version");

        private String strTag = "";

        eParaTag( String strTag ){
            this.strTag = strTag;
        }

        public String getString(){
            return strTag;
        }
    }

    private static final    String      API_NAME    = "/CIAPP/api/CancelCheckIn";
    private                 CallBack    m_Callback  = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CICancelCheckInModel( CallBack Callback ){
        m_Callback = Callback;
    }

    public void sendReqDataToWS(CICancelCheckInReq req){
        m_jsBody = new JSONObject();
        
        try {

            JSONArray arrayPax = new JSONArray();
            for ( int i = 0; i < req.Pax_Info.size(); i ++ ){
                JSONObject jsPax = new JSONObject();

                jsPax.put( eParaTag.First_Name.name(),  req.Pax_Info.get(i).First_Name );
                jsPax.put( eParaTag.Last_Name.name(),   req.Pax_Info.get(i).Last_Name );
                jsPax.put( eParaTag.Uci.name(),         req.Pax_Info.get(i).Uci );
                jsPax.put( eParaTag.Pnr_id.name(),      req.Pax_Info.get(i).Pnr_Id );

                JSONArray arrayItinerary = new JSONArray();
                for ( int j = 0; j < req.Pax_Info.get(i).Itinerary_Info.size(); j ++ ){
                    JSONObject jsItinerary = new JSONObject();

                    jsItinerary.put( eParaTag.Departure_Station.name(),
                            req.Pax_Info.get(i).Itinerary_Info.get(j).Departure_Station );
                    jsItinerary.put( eParaTag.Arrival_Station.name(),
                            req.Pax_Info.get(i).Itinerary_Info.get(j).Arrival_Station );
                    jsItinerary.put( eParaTag.Did.name(),
                            req.Pax_Info.get(i).Itinerary_Info.get(j).Did );

                    arrayItinerary.put(jsItinerary);
                }
                jsPax.put( eParaTag.Itinerary_Info.name(), arrayItinerary );

                arrayPax.put(jsPax);
            }

            m_jsBody.put( eParaTag.Pax_Info.name(), arrayPax );
            m_jsBody.put( eParaTag.version.name(),  WSConfig.DEF_API_VERSION );

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

        CICancelCheckInResp Resp = null;

        try {
            Resp = GsonTool.toObject(respBody.strData, CICancelCheckInResp.class);
        } catch (Exception e ){
            e.printStackTrace();
            SendError_Response_can_not_parse();
            return;
        }

        if ( null == Resp ){
            SendError_Response_can_not_parse();
            return;
        }

        if ( null != m_Callback ){
            m_Callback.onSuccess(respBody.rt_code, respBody.rt_msg, Resp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.CancelCheckIn)) ,"");
        } else
        if ( null != m_Callback ){
            m_Callback.onError(code, strMag);
        }
    }
}
