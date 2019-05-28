package ci.ws.Models;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIMealList;
import ci.ws.Models.entities.CIMealListResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.Models.entities.CIMealEntity;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/4/25.
 * 功能說明:取的餐點偏好餐點列表。
 * 對應CI API : QueryNatMealList
 */
public class CIInquiryMealListModel extends CIWSBaseModel {
    final String MEAL_LIST_FILE = "MealList.json";
    public interface MealLisCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param mealListResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryMealListSuccess( String rt_code, String rt_msg, CIMealListResp mealListResp );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryMealListError( String rt_code, String rt_msg );
    }

    private MealLisCallBack m_callback = null;
    private static final String API_NAME = "/CIAPP/api/InquiryMealList";

    private enum eParaTag {

        culture_info("culture_info"),
        device_id("device_id"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryMealListModel(){}

    public CIInquiryMealListModel( String strLanguage, String strDevice_id, String strVersion, MealLisCallBack listener ){

        this.m_callback = listener;
        m_jsBody = new JSONObject();
        try {
            m_jsBody.put( eParaTag.culture_info.getString(),        strLanguage);
            m_jsBody.put( eParaTag.device_id.getString(),           strDevice_id);
            m_jsBody.put( eParaTag.version.getString(),             strVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        Gson gson = new Gson();
        CIMealListResp mealListResp = new CIMealListResp();

        try{
            mealListResp.arMealList = gson.fromJson( respBody.strData, CIMealList.class);
        } catch ( Exception e ){
            e.printStackTrace();
        }

        if ( null == mealListResp.arMealList ){
            SendError_Response_data_null();
            return;
        }

        if ( null != m_callback ){
            m_callback.onInquiryMealListSuccess(respBody.rt_code, respBody.rt_msg, mealListResp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.SignUpMealList)) ,"");
        } else {
            if ( null != m_callback ){
                m_callback.onInquiryMealListError(code, strMag);
            }
        }
    }

    public CIMealList findData(){
        String json = getJsonFile(MEAL_LIST_FILE);
        CIMealList datas = GsonTool.getGson().fromJson( json, CIMealList.class);
        return datas;
    }

    public HashMap<String, CIMealEntity> getMealMap(){
        HashMap<String,CIMealEntity> map = new HashMap<>();
        CIMealList arList =  findData();
        if ( null != arList ){
            for ( CIMealEntity entity : arList ){
                map.put(entity.meal_code, entity);
            }
            return map;
        } else {
            return null;
        }

    }
}
