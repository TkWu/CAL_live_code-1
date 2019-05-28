package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIInsertOrderMealReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.WSConfig;

/**
 * Created by kevincheng on 2016/5/19.
 * 文件：CI_APP_API_MealSelection.docx on 16/05/18 20:53
 * 4.4 InsertOrderMeal
 * 功能說明: 新增餐點訂單。
 * 對應CI API : InsertOrders
 */
public class CIInsertOrderMealModel extends CIWSBaseModel {


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
        pax_seq,
        pax_subseq,
        pax_seat_class,
        meal_detail,
        platform,
        language,
        client_ip,
        version,
    }

    private static final String API_NAME = "/CIAPP/api/InsertOrderMeal";
    private CallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInsertOrderMealModel(CallBack Callback ){
        m_Callback = Callback;
    }

    public void sendReqDataToWS(CIInsertOrderMealReq reqData){

        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.pnr_id.name(),           reqData.pnr_Id);
            m_jsBody.put( eParaTag.itinerary_seq.name(),    reqData.itinerary_seq);
            m_jsBody.put( eParaTag.pax_seq.name(),          reqData.pax_seq);
            m_jsBody.put( eParaTag.pax_subseq.name(),       reqData.pax_subseq);
            m_jsBody.put( eParaTag.pax_seat_class.name(),   reqData.pax_seat_class);
            m_jsBody.put( eParaTag.meal_detail.name(),      reqData.meal_detail);
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
