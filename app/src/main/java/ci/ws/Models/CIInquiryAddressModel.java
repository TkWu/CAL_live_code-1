package ci.ws.Models;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CICodeNameEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.WSConfig;

/**
 * Created on 2019-01-27
 * InquiryAddress
 * 功能說明:取得地址資訊
 *
 * 查詢種類，1-國家清單、2-城市與鄰近城市清單、3-區域與鄰近城市清單、4-路名清單、5-郵遞區號清單
 */
public class CIInquiryAddressModel extends CIWSBaseModel {


    public interface InquiryCallback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code, String rt_msg, String strPara);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onError(String rt_code, String rt_msg);
    }

    public static final String NATIONAL = "1";
    public static final String CITY     = "2";
    public static final String COUNTY   = "3";
    public static final String STREET   = "4";
    public static final String ZIPCODE  = "5";

    private enum eParaTag {

        para("para"),
        countryCode("countryCode"),
        cityCode("cityCode"),
        countyCode("countyCode"),
        streetCode("streetCode"),
        zipCode("zipCode"),
        culture_info("cultureInfo"),
        device_id("deviceId");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/CIAPP/api/InquiryAddress";

    private InquiryCallback m_Callback = null;

    private String m_strPara = NATIONAL;

    //private ArrayList<CICodeNameEntity> m_arAddressList = new ArrayList<>();
    //private ArrayList<CICodeNameEntity> m_arCurrAreaList = null;

    private ArrayList<CICodeNameEntity> m_arNationalList = null;
    private ArrayList<CICodeNameEntity> m_arCityList = null;
    private ArrayList<CICodeNameEntity> m_arCountyList = null;
    private ArrayList<CICodeNameEntity> m_arStreetList = null;
    private ArrayList<CICodeNameEntity> m_arZipCodeList = null;
    private ArrayList<CICodeNameEntity> m_arCurrAreaList = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryAddressModel(InquiryCallback callback ){

        this.m_Callback = callback;
    }

    public void getAddressFrowWS( String strPara,
                                  String strCountryCode,
                                  String strCityCode,
                                  String strCountyCode,
                                  String streetCode,
                                  String strZipCode){

        m_jsBody = new JSONObject();
        try {

            JSONObject jsAddrRequParas = new JSONObject();

            m_strPara = strPara;
            jsAddrRequParas.put( eParaTag.para.getString(),        strPara);
            jsAddrRequParas.put( eParaTag.countryCode.getString(), strCountryCode);
            jsAddrRequParas.put( eParaTag.cityCode.getString(),    strCityCode);
            jsAddrRequParas.put( eParaTag.countyCode.getString(),  strCountyCode);
            jsAddrRequParas.put( eParaTag.streetCode.getString(),  streetCode);
            jsAddrRequParas.put( eParaTag.zipCode.getString(),     strZipCode);
            jsAddrRequParas.put( eParaTag.culture_info.getString(),CIApplication.getLanguageInfo().getWSLanguage());
            jsAddrRequParas.put( eParaTag.device_id.getString(),   CIApplication.getDeviceInfo().getAndroidId());
            //m_jsBody.put( eParaTag.version.getString(),             WSConfig.DEF_API_VERSION);

            m_jsBody.put("addrRequParas", jsAddrRequParas);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code){

        Gson gson = new Gson();
        Type listType = new TypeToken<List<CICodeNameEntity>>(){}.getType();

        ArrayList<CICodeNameEntity> arAddressList = new ArrayList<>();
        try {
            JSONObject jsData = new JSONObject(respBody.strData);
            JSONArray jarAddress = jsData.optJSONArray("address");

            if ( null != jarAddress ){
                arAddressList = gson.fromJson( jarAddress.toString(), listType);
            }

            JSONArray jarCurrArea = jsData.optJSONArray("currArea");
            if ( null != jarCurrArea ){
                m_arCurrAreaList = gson.fromJson( jarCurrArea.toString(), listType);
            }

            if ( TextUtils.equals(CIInquiryAddressModel.NATIONAL, m_strPara) ){
                m_arNationalList = arAddressList;
            } else if ( TextUtils.equals(CIInquiryAddressModel.CITY, m_strPara) ){
                m_arCityList = arAddressList;
            } else if ( TextUtils.equals(CIInquiryAddressModel.COUNTY, m_strPara) ){
                m_arCountyList = arAddressList;
            } else if ( TextUtils.equals(CIInquiryAddressModel.STREET, m_strPara) ){
                m_arStreetList = arAddressList;
            } else if ( TextUtils.equals(CIInquiryAddressModel.ZIPCODE, m_strPara) ){
                m_arZipCodeList = arAddressList;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ( null != m_Callback){
            m_Callback.onSuccess(respBody.rt_code, respBody.rt_msg, m_strPara);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if (null != m_Callback) {
            m_Callback.onError(code, strMag);
        }
    }

    public void clearAllData(){

        if ( null != m_arCityList ){
            m_arCityList.clear();
        }

        if ( null != m_arCountyList ){
            m_arCountyList.clear();
        }

        if ( null != m_arStreetList ){
            m_arStreetList.clear();
        }

        if ( null != m_arZipCodeList ){
            m_arZipCodeList.clear();
        }

        if ( null != m_arCurrAreaList ){
            m_arCurrAreaList.clear();
        }
    }

    public ArrayList<CICodeNameEntity> getNationalList() {
        return m_arNationalList;
    }

    public ArrayList<CICodeNameEntity> getCityList() {
        return m_arCityList;
    }

    public ArrayList<CICodeNameEntity> getCountyList() {
        return m_arCountyList;
    }

    public ArrayList<CICodeNameEntity> getStreetList() {
        return m_arStreetList;
    }

    public ArrayList<CICodeNameEntity> getZipCodeList() {
        return m_arZipCodeList;
    }

    public ArrayList<CICodeNameEntity> getCurrAreaList() {
        return m_arCurrAreaList;
    }
}
