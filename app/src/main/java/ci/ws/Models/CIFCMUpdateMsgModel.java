package ci.ws.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import ci.function.Core.CIApplication;
import ci.ui.object.AppInfo;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIUpdateDeviceReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;

/**推播使用
 *
 * 傳送已讀的訊息Id 給Server */
public class CIFCMUpdateMsgModel extends CIWSBaseModel {

    public interface UpdateMsgCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onUpdateMsgSuccess(String rt_code, String rt_msg);

        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onUpdateMsgError(String rt_code, String rt_msg);
    }

    private static final String API_NAME = "/mobile30/push/api/calpush/Updatemsg";

    private UpdateMsgCallBack m_callback = null;


    public CIFCMUpdateMsgModel( UpdateMsgCallBack callBack ){
        m_callback = callBack;
    }

    private enum eParaTag {

        msgids("msgids");

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
            m_callback.onUpdateMsgSuccess(respBody.rt_code, respBody.rt_msg);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( null != m_callback ){
            m_callback.onUpdateMsgError(code, strMag);
        }
    }


    public void UpdateMsg( ArrayList<String> arMsgIdList ){

        m_jsBody = new JSONObject();
        try {

            JSONArray array = new JSONArray();
            Iterator it = arMsgIdList.iterator();
            while (it.hasNext()) {
                array.put(it.next());
            }

            m_jsBody.put(eParaTag.msgids.getString(), array);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

}
