package ci.ws.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIBaggageInfoContentResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan
 * InquiryBaggageInfoByBGNum
 * 功能說明: 個別行李條號帶出該行李詳細狀態
 */
public class CIInquiryBaggageInfoByBGNumModel extends CIWSBaseModel {

    public interface InquiryBaggageInfoByBGNumCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param arDataList Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryBaggageInfoByBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoContentResp> arDataList );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryBaggageInfoByBGNumError(String rt_code, String rt_msg);
    }

    private enum eParaTag {
        baggage_num,
        departure_station,
        departure_date,
        language,
        version,
    }

    private static final String API_NAME = "/CIAPP/api/InquiryBaggageInfoByBGNum";
    private InquiryBaggageInfoByBGNumCallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryBaggageInfoByBGNumModel( InquiryBaggageInfoByBGNumCallBack Callback ){
        m_Callback = Callback;
    }

    public void setCallback( InquiryBaggageInfoByBGNumCallBack Callback ){
        m_Callback = Callback;
    }

    public void InquiryBaggageInfoByBGNumFromWS( String strBaggage_num, String strDeparture_station, String strDeparture_date ){

        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.baggage_num.name(),      strBaggage_num);
            m_jsBody.put( eParaTag.departure_station.name(),strDeparture_station);
            m_jsBody.put( eParaTag.departure_date.name(),   strDeparture_date);
            m_jsBody.put( eParaTag.language.name(),         CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.version.name(),          WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }


    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {


        ArrayList<CIBaggageInfoContentResp> arDataList = new ArrayList<>();

        try {
            JSONArray jarBaggage = new JSONArray(respBody.strData);

            int iSize = jarBaggage.length();
            for ( int iIdx = 0; iIdx < iSize; iIdx++ ){
                CIBaggageInfoContentResp baggageContentResp = GsonTool.toObject( jarBaggage.optJSONObject(iIdx).toString(), CIBaggageInfoContentResp.class );

                arDataList.add(baggageContentResp);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



        if ( null != m_Callback ){
            m_Callback.onInquiryBaggageInfoByBGNumSuccess(respBody.rt_code, respBody.rt_msg, arDataList);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( null != m_Callback ){
            m_Callback.onInquiryBaggageInfoByBGNumError(code, strMag);
        }
    }
}
