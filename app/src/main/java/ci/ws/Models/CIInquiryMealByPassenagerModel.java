package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIInquiryMealByPassangerResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/18.
 * 3.1.	InquiryMealByPassanger
 * 功能說明: 依訂位代碼、乘客姓、乘客名取得乘客、航班、預選餐點資料。
 * 對應CI API : GetPaxInfo
 */
public class CIInquiryMealByPassenagerModel extends CIWSBaseModel {


    public interface PassangerCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param mealByPassangerResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryPassenagerSuccess( String rt_code, String rt_msg, CIInquiryMealByPassangerResp mealByPassangerResp );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryPassenagerError( String rt_code, String rt_msg );
    }

    private enum eParaTag {
        departure_date,
        pnr_id,
        first_name,
        last_name,
        flight_sector,
        platform,
        language,
        client_ip,
        version,
    }

    private static final String API_NAME = "/CIAPP/api/InquiryMealByPassanger";
    private PassangerCallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryMealByPassenagerModel(PassangerCallBack Callback ){
        m_Callback = Callback;
    }

    public void InquiryMealByPassangerFromWS( String strPNR, String first_name, String last_name, String flight_sector, String departure_date ){

//        strPNR = "K111AB";
//        first_name = "HUA CHEN";
//        last_name="LIN";
//        flight_sector="TPELAX";
        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.pnr_id.name(),           strPNR);
            m_jsBody.put( eParaTag.departure_date.name(),   departure_date);
            m_jsBody.put( eParaTag.first_name.name(),       first_name);
            m_jsBody.put( eParaTag.last_name.name(),        last_name);
            m_jsBody.put( eParaTag.flight_sector.name(),    flight_sector);
            m_jsBody.put( eParaTag.platform.name(),         "ANDROID");
            m_jsBody.put( eParaTag.language.name(),         CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.client_ip.name(),        CIApplication.getClentIp());
            m_jsBody.put( eParaTag.version.name(),          WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

//    public CIMealDetailInfoEntity ParseDetail( CIMealDetailInfoEntity detailInfo,  JSONObject jsData, String strTag ){
//
//        JSONObject jsObjDetail = GsonTool.getJsobjectFromJsobject(jsData, strTag );
//        if ( null != jsObjDetail ){
//            detailInfo.meal_seq         = GsonTool.getStringFromJsobject(jsObjDetail, "meal_seq");
//            detailInfo.meal_content_seq = GsonTool.getStringFromJsobject(jsObjDetail, "meal_content_seq");
//            detailInfo.meal_code        = GsonTool.getStringFromJsobject(jsObjDetail, "meal_code");
//            detailInfo.mealtype_code    = GsonTool.getStringFromJsobject(jsObjDetail, "mealtype_code");
//            detailInfo.mealtype_desc    = GsonTool.getStringFromJsobject(jsObjDetail, "mealtype_desc");
//            detailInfo.meal_name        = GsonTool.getStringFromJsobject(jsObjDetail, "meal_name");
//            detailInfo.is_menu_only     = GsonTool.getBooleanFromJsobject(jsObjDetail, "is_menu_only");
//        } else {
//            detailInfo = null;
//        }
//
//        return detailInfo;
//    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {
//
//        //test
//        respBody = ResultCodeCheck(getJsonFile("InquiryMealByPassenager.json"));

        CIInquiryMealByPassangerResp mealByPassangerResp = new CIInquiryMealByPassangerResp();
        mealByPassangerResp = GsonTool.toObject( respBody.strData, CIInquiryMealByPassangerResp.class);
        try{

//            JSONObject jsData = new JSONObject(respBody.strData);
//            mealByPassangerResp.pnr_id          = GsonTool.getStringFromJsobject(jsData, "pnr_id");
//            mealByPassangerResp.itinerary_seq   = GsonTool.getIntFromJsobject(jsData, "itinerary_seq");
//            mealByPassangerResp.flight_date     = GsonTool.getStringFromJsobject(jsData, "flight_date");
//            mealByPassangerResp.flight_num      = GsonTool.getStringFromJsobject(jsData, "flight_num");
//            mealByPassangerResp.flight_sector   = GsonTool.getStringFromJsobject(jsData, "flight_sector");
//            mealByPassangerResp.pax_seat_class  = GsonTool.getStringFromJsobject(jsData, "pax_seat_class");
//            JSONArray jsarPassangers = GsonTool.getJSONArrayFromJsobject(jsData, "passangers");
//            int length = jsarPassangers.length();
//            for ( int iIdx = 0; iIdx < length; iIdx++ ){
//                JSONObject jsPassangers = jsarPassangers.getJSONObject(iIdx);
//                CIMealPassenagerEntity passenagerEntity = new CIMealPassenagerEntity();
//
//                passenagerEntity.pax_seq        = GsonTool.getIntFromJsobject(jsPassangers, "pax_seq");
//                passenagerEntity.pax_subseq     = GsonTool.getIntFromJsobject(jsPassangers, "pax_subseq");
//                passenagerEntity.pax_first_name = GsonTool.getStringFromJsobject(jsPassangers, "pax_first_name");
//                passenagerEntity.pax_last_name  = GsonTool.getStringFromJsobject(jsPassangers, "pax_last_name");
//                passenagerEntity.ssr_seq        = GsonTool.getIntFromJsobject(jsPassangers, "ssr_seq");
//                passenagerEntity.pono_number    = GsonTool.getStringFromJsobject(jsPassangers, "pono_number");
//
//                JSONObject jsbreakfast_detail   = GsonTool.getJsobjectFromJsobject(jsPassangers, "breakfast_detail");
//                passenagerEntity.breakfast_detail.is_order = GsonTool.getBooleanFromJsobject(jsbreakfast_detail, "is_order");
//                passenagerEntity.breakfast_detail.detail = ParseDetail(passenagerEntity.breakfast_detail.detail, jsbreakfast_detail, "detail");
//                //整併餐點類型名稱，方便UI顯示判斷用
//                //同時間將Mealtype_code 往上移一層, 方便ui判斷
//                if ( null == passenagerEntity.breakfast_detail.detail ){
//                    passenagerEntity.breakfast_detail.strMealDesc = CIApplication.getContext().getString(R.string.breakfast);
//                    passenagerEntity.breakfast_detail.strMealtype_code = CIMealDetailInfoEntity.MEALTYPE_BREAKFAST;
//                } else {
//                    passenagerEntity.breakfast_detail.strMealDesc = passenagerEntity.breakfast_detail.detail.mealtype_desc;
//                    passenagerEntity.breakfast_detail.strMealtype_code = passenagerEntity.breakfast_detail.detail.mealtype_code;
//                }
//                passenagerEntity.arMealDetailList.add(passenagerEntity.breakfast_detail);
//                //
//                //
//                JSONObject jslunch_detail       = GsonTool.getJsobjectFromJsobject(jsPassangers, "lunch_detail");
//                passenagerEntity.lunch_detail.is_order = GsonTool.getBooleanFromJsobject(jslunch_detail, "is_order");
//                passenagerEntity.lunch_detail.detail = ParseDetail(passenagerEntity.lunch_detail.detail, jslunch_detail, "detail");
//                //整併餐點類型名稱，方便UI顯示判斷用
//                //同時間將Mealtype_code 往上移一層, 方便ui判斷
//                if ( null == passenagerEntity.lunch_detail.detail ){
//                    passenagerEntity.lunch_detail.strMealDesc = CIApplication.getContext().getString(R.string.lunch);
//                    passenagerEntity.lunch_detail.strMealtype_code = CIMealDetailInfoEntity.MEALTYPE_LUNCH;
//                } else {
//                    passenagerEntity.lunch_detail.strMealDesc = passenagerEntity.lunch_detail.detail.mealtype_desc;
//                    passenagerEntity.lunch_detail.strMealtype_code = passenagerEntity.lunch_detail.detail.mealtype_code;
//                }
//                passenagerEntity.arMealDetailList.add(passenagerEntity.lunch_detail);
//                //
//                //
//                JSONObject jsdinner_detail      = GsonTool.getJsobjectFromJsobject(jsPassangers, "dinner_detail");
//                passenagerEntity.dinner_detail.is_order = GsonTool.getBooleanFromJsobject(jsdinner_detail, "is_order");
//                passenagerEntity.dinner_detail.detail = ParseDetail(passenagerEntity.dinner_detail.detail, jsdinner_detail, "detail");
//                //整併餐點類型名稱，方便UI顯示判斷用
//                //同時間將Mealtype_code 往上移一層, 方便ui判斷
//                if ( null == passenagerEntity.dinner_detail.detail ){
//                    passenagerEntity.dinner_detail.strMealDesc = CIApplication.getContext().getString(R.string.dinner);
//                    passenagerEntity.dinner_detail.strMealtype_code = CIMealDetailInfoEntity.MEALTYPE_DINNER;
//                } else {
//                    passenagerEntity.dinner_detail.strMealDesc = passenagerEntity.dinner_detail.detail.mealtype_desc;
//                    passenagerEntity.dinner_detail.strMealtype_code = passenagerEntity.dinner_detail.detail.mealtype_code;
//                }
//                passenagerEntity.arMealDetailList.add(passenagerEntity.dinner_detail);
//                //
//                //
//                mealByPassangerResp.arPassenagerList.add(passenagerEntity);
//            }

        } catch ( Exception e ){
            e.printStackTrace();

            SendError_Response_can_not_parse();
            return;
        }


        if ( null != m_Callback ){
            m_Callback.onInquiryPassenagerSuccess(respBody.rt_code, respBody.rt_msg, mealByPassangerResp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryMealByPassanger)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onInquiryPassenagerError(code, strMag);
            }
        }
    }
}
