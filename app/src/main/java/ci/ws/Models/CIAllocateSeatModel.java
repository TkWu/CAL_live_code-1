package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIAllocateSeatReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/17.
 * 3.1.1.	AllocateSeat (更換座位Check  In前)
 * 功能說明: 更換座位
 * 對應1A API : PNR_Retrieve
 * PNR_AddMultiElements
 */
public class CIAllocateSeatModel extends CIWSBaseModel {


    public interface AllocateSeatCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param strSeat Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onAllocateSeatSuccess( String rt_code, String rt_msg, String strSeat );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onAllocateSeatError( String rt_code, String rt_msg );
    }

    private enum eParaTag {
        version,
    }

    private static final String API_NAME = "/CIAPP/api/AllocateSeat";
    private AllocateSeatCallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIAllocateSeatModel( AllocateSeatCallBack callback ){
        m_Callback = callback;
    }

    public void AllocateSeat( CIAllocateSeatReq seat ){

//        //假資料
//        seat = new CIAllocateSeatReq();
//        seat.Pnr_id="YI2RLL";
//        seat.Segment_Number="2";
//        seat.Pax_Number="2";
//        seat.Seat_Number="21A";

        try {

            String strSeat = GsonTool.toJson(seat);

            m_jsBody = new JSONObject(strSeat);

            m_jsBody.put( eParaTag.version.name(),  WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        String strSeat = "";

        try{

            JSONObject jsData = new JSONObject(respBody.strData);
            if ( null != jsData ){
                strSeat = GsonTool.getStringFromJsobject(jsData, "Seat_Number");
            }

        } catch (Exception e){
            e.printStackTrace();
        }

//        if ( null == strSeat || strSeat.length() <= 0 ){
//            SendError_Response_can_not_parse();
//            return;
//        }

        if ( null != m_Callback ){
            m_Callback.onAllocateSeatSuccess(respBody.rt_code, respBody.rt_msg, strSeat);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.AllocateSeat)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onAllocateSeatError(code, strMag);
            }
        }
    }
}
