package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIMilesProgressEntity;
import ci.ws.Models.entities.CITimeTableReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/5.
 * 3.1.	InquiryMilesProgress(取得累積哩程記錄)
 * 功能說明: 傳入會員卡號，取得累積哩程記錄
 * 對應CI API : QueryExpiringMileage
 */
public class CIInquiryMilesProgressModel extends CIWSBaseModel{


    public interface MilesProgressCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param miles Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onStationSuccess( String rt_code, String rt_msg,  CIMilesProgressEntity miles  );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onStationError( String rt_code, String rt_msg );
    }

    /**其餘參數參照
     * {@link CITimeTableReq}*/
    private enum eParaTag {

        login_token("login_token"),
        card_no("card_no"),
        culture_info("culture_info"),
        device_id("device_id"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/CIAPP/api/InquiryMilesProgress";

    private MilesProgressCallBack m_callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryMilesProgressModel(MilesProgressCallBack callBack){this.m_callback = callBack;}

    public void InquiryMilesProgressFromWS( String strCardNo ){

        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.card_no.getString(),     strCardNo);
            m_jsBody.put( eParaTag.login_token.getString(), CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.culture_info.getString(),CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.device_id.getString(),   CIApplication.getDeviceInfo().getAndroidId());
            m_jsBody.put( eParaTag.version.getString(),     WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        CIMilesProgressEntity miles = GsonTool.toObject( respBody.strData, CIMilesProgressEntity.class);

        if ( null == miles ){
            SendError_Response_can_not_parse();
            return;
        }

        miles.flight = miles.first_class_flights.crtftrp_accumulation01;

        if ( null != m_callback ){
            m_callback.onStationSuccess(respBody.rt_code, respBody.rt_msg, miles);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryMilesProgress)) ,"");
        } else if ( null != m_callback ){
            m_callback.onStationError(code, strMag);
        }
    }
}
