package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIDeleteOrderMealReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.WSConfig;

/**
 * Created by kevincheng on 2016/5/19.
 * 文件：CI_APP_API_MealSelection.docx on 16/05/18 20:53
 * 4.4 DeleteOrderMeal
 * 功能說明: 刪除餐點訂單。
 * 對應CI API : DeleteOrders
 */
public class CIDeleteOrderMealModel extends CIWSBaseModel {


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
        pnr_id,
        itinerary_seq,
        ssr_seq,
        pono_num,
        platform,
        language,
        client_ip,
        version,
    }

    private static final String API_NAME = "/CIAPP/api/DeleteOrderMeal";
    private CallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIDeleteOrderMealModel(CallBack Callback ){
        m_Callback = Callback;
    }

    public void sendReqDataToWS(CIDeleteOrderMealReq reqData){

        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.pnr_id.name(),           reqData.pnr_Id);
            m_jsBody.put( eParaTag.itinerary_seq.name(),    reqData.itinerary_seq);
            m_jsBody.put( eParaTag.ssr_seq.name(),          reqData.ssr_seq);
            m_jsBody.put( eParaTag.pono_num.name(),         reqData.pono_num);
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
