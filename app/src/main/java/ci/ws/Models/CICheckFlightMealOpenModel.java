package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CICheckFlightMealOpenReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.WSConfig;

/**
 * Created by JL Chen on 2016/05/19.
 * 文件：CI_APP_API_MealSelection.docx on 16/05/18 20:53
 * 4.6 CheckFlightMealOpen
 * 功能說明: 依航班檢查是否開放預訂餐點。
 * 對應CI API : CheckFlightOpen
 */
public class CICheckFlightMealOpenModel extends CIWSBaseModel {

    public interface CallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code, String rt_msg);
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
        flight_company,
        flight_num,
        flight_sector,
        flight_date,
        pnr_status,
        seat_class,
        platform,
        language,
        client_ip,
        version
    }

    private static final String API_NAME = "/CIAPP/api/CheckFlightMealOpen";
    private CallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CICheckFlightMealOpenModel(CallBack Callback ){
        m_Callback = Callback;
    }

    public void sendReqDataToWS(CICheckFlightMealOpenReq reqData){

        m_jsBody = new JSONObject();
        try {
            m_jsBody.put( eParaTag.flight_company.name(),   reqData.flight_company);
            m_jsBody.put( eParaTag.flight_num.name(),       reqData.flight_num);
            m_jsBody.put( eParaTag.flight_sector.name(),    reqData.flight_sector);
            m_jsBody.put( eParaTag.flight_date.name(),      reqData.flight_date);
            m_jsBody.put( eParaTag.pnr_status.name(),       reqData.pnr_status);
            m_jsBody.put( eParaTag.seat_class.name(),       reqData.seat_class);
            m_jsBody.put( eParaTag.platform.name(),         "ANDROID");
            m_jsBody.put( eParaTag.language.name(),         CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.client_ip.name(),        CIApplication.getClentIp());
            m_jsBody.put( eParaTag.version.name(),          WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {
        if ( null != m_Callback ){
            m_Callback.onSuccess(respBody.rt_code, respBody.rt_msg);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( null != m_Callback ) {
            m_Callback.onError(code, strMag);
        }
    }
}
