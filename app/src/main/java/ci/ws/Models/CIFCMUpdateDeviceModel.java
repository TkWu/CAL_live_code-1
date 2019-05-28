package ci.ws.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIUpdateDeviceReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;

/**推播使用
 *
 * 上傳TOKEN、航班資訊 給Server */
public class CIFCMUpdateDeviceModel extends CIWSBaseModel {

    public interface UpdateDeviceCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onUpdateDeviceSuccess( String rt_code, String rt_msg);

        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onUpdateDeviceError( String rt_code, String rt_msg );
    }

    private static final String API_NAME = "/mobile30/push/api/calpush/Updatedevice";

    private UpdateDeviceCallBack m_callback = null;


    public CIFCMUpdateDeviceModel(UpdateDeviceCallBack callBack ){
        m_callback = callBack;
    }

    private enum eParaTag {

        customerid("customerid"),

        devicebasic("devicebasic"),
        timezone("timezone"),
        pushtokentype("pushtokentype"),
        deviceid("deviceid"),

        properties("properties"),
        appname("appname"),
        pushtoken("pushtoken"),
        notfiswitch("notfiswitch"),
        applanguages("applanguages"),
        survery("survery"),
        appversion("appversion"),
        events("events"),
        flights("flights");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }



    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {
        if ( null != m_callback ){
            m_callback.onUpdateDeviceSuccess(respBody.rt_code, respBody.rt_msg);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( null != m_callback ){
            m_callback.onUpdateDeviceError(code, strMag);
        }
    }


    public void CIUpdateDevice( CIUpdateDeviceReq req ){

        m_jsBody = new JSONObject();
        try {
            m_jsBody.put( eParaTag.customerid.getString(),          req.customerid);
            //
            JSONObject jsDevicebasic = new JSONObject();
            jsDevicebasic.put( eParaTag.timezone.getString(),       req.timezone);
            jsDevicebasic.put( eParaTag.pushtokentype.getString(),  req.pushtokentype);
            jsDevicebasic.put( eParaTag.deviceid.getString(),       req.deviceid);
            //
            m_jsBody.put( eParaTag.devicebasic.getString(), jsDevicebasic);
            //
            JSONObject jsProperties = new JSONObject();
            jsProperties.put( eParaTag.appname.getString(),         req.appname);
            jsProperties.put( eParaTag.pushtoken.getString(),       req.pushtoken);
            jsProperties.put( eParaTag.notfiswitch.getString(),     req.notfiswitch);
            jsProperties.put( eParaTag.applanguages.getString(),    req.applanguages);
            jsProperties.put( eParaTag.survery.getString(), "");   //保留欄位，先留空
            jsProperties.put( eParaTag.appversion.getString(),      req.appversion);
            //
            m_jsBody.put( eParaTag.properties.getString(), jsProperties);
            //
            JSONObject jsEvents = new JSONObject();
            String strRequest = GsonTool.toJson(req.flights);
            JSONArray jsflights = new JSONArray(strRequest);
            //
            jsEvents.put( eParaTag.flights.getString(),   jsflights);
            //
            m_jsBody.put( eParaTag.events.getString(), jsEvents);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }



}
