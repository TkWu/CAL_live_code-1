package ci.ws.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIInquirtMealInfoResp;
import ci.ws.Models.entities.CIMealDetailEntity;
import ci.ws.Models.entities.CIMealInfoEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/18.
 * 3.1.	InquiryMealInfo
 * 功能說明: 依航班日期、航班編號、航班航段、艙等、餐點類型編號等取得該班機提供的餐點資訊。
 * 對應CI API : GetMenuInfo
 */
public class CIInquiryMealInfoModel extends CIWSBaseModel {

    public interface InquiryMealInfoCallBack {
        /**
         * 取得該班機提供的餐點資訊成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param mealInfoResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryMealInfoSuccess(String rt_code, String rt_msg, CIInquirtMealInfoResp mealInfoResp);
        /**
         * 取得該班機提供的餐點資訊失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryMealInfoError(String rt_code, String rt_msg);
    }

    private enum eParaTag {
        flight_date,
        flight_num,
        flight_sector,
        seat_class,
        platform,
        language,
        client_ip,
        version,
    }

    private static final String API_NAME = "/CIAPP/api/InquiryMealInfo";
    private InquiryMealInfoCallBack m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryMealInfoModel(InquiryMealInfoCallBack Callback){
        m_Callback = Callback;
    }

    public void InquiryMealInfoFromWS( String flight_date, String flight_num, String flight_sector, String seat_class ){

        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.flight_date.name(),  flight_date);
            m_jsBody.put( eParaTag.flight_num.name(),   flight_num);
            m_jsBody.put( eParaTag.flight_sector.name(),flight_sector);
            m_jsBody.put( eParaTag.seat_class.name(),   seat_class);
            m_jsBody.put( eParaTag.platform.name(),     "ANDROID");
            m_jsBody.put( eParaTag.language.name(),     CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.client_ip.name(),    CIApplication.getClentIp());
            m_jsBody.put( eParaTag.version.name(),      WSConfig.DEF_API_VERSION);

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


    public CIMealDetailEntity ParseMealList(JSONObject jsMeal){

        CIMealDetailEntity mealDetailEntity = new CIMealDetailEntity();

        mealDetailEntity.meal_seq       = GsonTool.getStringFromJsobject(jsMeal, "meal_seq");
        mealDetailEntity.mealtype_code  = GsonTool.getStringFromJsobject(jsMeal, "mealtype_code");

        JSONArray jsarCOMMON            = GsonTool.getJSONArrayFromJsobject(jsMeal, "common");
        mealDetailEntity.arCommonList   = ParseMealInfo(jsarCOMMON);
        JSONArray jsarMENU_ONLY         = GsonTool.getJSONArrayFromJsobject(jsMeal, "menu_only");
        mealDetailEntity.arMenuOnlyList = ParseMealInfo(jsarMENU_ONLY);

        if ( null == mealDetailEntity.arCommonList && null == mealDetailEntity.arMenuOnlyList ){
            mealDetailEntity.bIsHaveMealInfo = false;
        } else {
            mealDetailEntity.bIsHaveMealInfo = true;
        }

        return mealDetailEntity;
    }

    public ArrayList<CIMealInfoEntity> ParseMealInfo( JSONArray jsarData ){

        ArrayList<CIMealInfoEntity> arMealInfoList = new ArrayList<CIMealInfoEntity>();

        try {
            int iLength = jsarData.length();
            for ( int iIdx = 0; iIdx < iLength; iIdx++ ){

                CIMealInfoEntity mealinfo = new CIMealInfoEntity();
                JSONObject jsObj = jsarData.getJSONObject(iIdx);

                mealinfo = GsonTool.toObject(jsObj.toString(), CIMealInfoEntity.class);

//                mealinfo.meal_seq = GsonTool.getStringFromJsobject( jsObj, "meal_seq");
//                mealinfo.meal_content_seq   = GsonTool.getStringFromJsobject( jsObj, "meal_content_seq");
//                mealinfo.meal_code          = GsonTool.getStringFromJsobject( jsObj, "meal_code");
//                mealinfo.mealtype_code      = GsonTool.getStringFromJsobject( jsObj, "mealtype_code");
//                mealinfo.mealtype_desc      = GsonTool.getStringFromJsobject( jsObj, "mealtype_desc");
//                mealinfo.meal_name          = GsonTool.getStringFromJsobject( jsObj, "meal_name");

                arMealInfoList.add(mealinfo);
            }

            if ( arMealInfoList.size() <= 0 ){
                arMealInfoList = null;
            }
        } catch ( Exception e ){
            e.printStackTrace();
            arMealInfoList = null;
        }


        return arMealInfoList;
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

//        //test
//        respBody = ResultCodeCheck(getJsonFile(WSConfig.InquiryMealInfo));

        CIInquirtMealInfoResp MealInfo = new CIInquirtMealInfoResp();
        MealInfo.mDetailMap.clear();
        try{

            JSONArray jsArray = new JSONArray(respBody.strData);
            int ilength = jsArray.length();
            for ( int iIdx = 0; iIdx < ilength; iIdx++ ){

                JSONObject jsMeal =  jsArray.getJSONObject(iIdx);

                CIMealDetailEntity mealDetailEntity = ParseMealList(jsMeal);
                //
                MealInfo.mDetailMap.put( mealDetailEntity.meal_seq, mealDetailEntity);
            }

//            JSONObject jsData = new JSONObject(respBody.strData);
//            JSONObject jsBreakfast  = GsonTool.getJsobjectFromJsobject(jsData, "B");
//            MealInfo.breakfast      = ParseMealList(jsBreakfast, MealInfo.breakfast);
//            //重新調整餐點資訊，方便UI作畫
//            if ( null != MealInfo.breakfast &&
//                    (( null != MealInfo.breakfast.arCommonList && MealInfo.breakfast.arCommonList.size() > 0 ) ||
//                    ( null != MealInfo.breakfast.arMenuOnlyList && MealInfo.breakfast.arMenuOnlyList.size() > 0 )) ){
//                MealInfo.breakfast.bIsHaveMealInfo = true;
//            }
//            MealInfo.mDetailMap.put(CIMealDetailInfoEntity.MEALTYPE_BREAKFAST, MealInfo.breakfast);
//            //
//
//            JSONObject jsarLunch    = GsonTool.getJsobjectFromJsobject(jsData, "L");
//            MealInfo.lunch          = ParseMealList(jsarLunch, MealInfo.lunch);
//            //重新調整餐點資訊，方便UI作畫
//            if ( null != MealInfo.lunch &&
//                    (( null != MealInfo.lunch.arCommonList && MealInfo.lunch.arCommonList.size() > 0 ) ||
//                    ( null != MealInfo.lunch.arMenuOnlyList && MealInfo.lunch.arMenuOnlyList.size() > 0 )) ){
//                MealInfo.lunch.bIsHaveMealInfo = true;
//            }
//            MealInfo.mDetailMap.put(CIMealDetailInfoEntity.MEALTYPE_LUNCH, MealInfo.lunch);
//            //
//
//            JSONObject jsarDinner   = GsonTool.getJsobjectFromJsobject(jsData, "D");
//            MealInfo.dinner         = ParseMealList(jsarDinner, MealInfo.dinner);
//            //重新調整餐點資訊，方便UI作畫
//            if ( null != MealInfo.dinner &&
//                    (( null != MealInfo.dinner.arCommonList && MealInfo.dinner.arCommonList.size() > 0 ) ||
//                    ( null != MealInfo.dinner.arMenuOnlyList && MealInfo.dinner.arMenuOnlyList.size() > 0 )) ){
//                MealInfo.dinner.bIsHaveMealInfo = true;
//            }
//            MealInfo.mDetailMap.put(CIMealDetailInfoEntity.MEALTYPE_DINNER, MealInfo.dinner);
//            //

        } catch ( Exception e ){
            e.printStackTrace();
            MealInfo = null;
        }

//        if ( null == MealInfo || ( null == MealInfo.breakfast && null == MealInfo.lunch && null ==  MealInfo.dinner ) ){
//            SendError_Response_can_not_parse();
//            return;
//        }


        if ( null != m_Callback ){
            m_Callback.onInquiryMealInfoSuccess(respBody.rt_code, respBody.rt_msg, MealInfo);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryMealInfo)) ,"");
        } else {
            if ( null != m_Callback ){
                m_Callback.onInquiryMealInfoError(code, strMag);
            }
        }
    }
}
