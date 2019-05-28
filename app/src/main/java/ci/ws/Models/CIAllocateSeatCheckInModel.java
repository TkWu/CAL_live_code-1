package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIAllocateSeatCheckInReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by jlchen on 16/7/7.
 * AllocateSeatCheckIn (更換座位CheckIn)
 * 功能說明: 更換座位CheckIn
 * 對應1A API : DCSSTG_AllocateSeat
 */
public class CIAllocateSeatCheckInModel extends CIWSBaseModel {

    public interface CallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code, String rt_msg, String strSeat);
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
        version,
    }

    private static final String API_NAME = "/CIAPP/api/AllocateSeatCheckIn";
    private CallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIAllocateSeatCheckInModel(CallBack callback ){
        m_Callback = callback;
    }

    public void AllocateSeatCheckIn( CIAllocateSeatCheckInReq seat ){

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
            m_Callback.onSuccess(respBody.rt_code, respBody.rt_msg, strSeat);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.AllocateSeat)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onError(code, strMag);
            }
        }
    }
}
