package ci.ws.Models;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIProfileEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/6.
 * 3.1.	InquiryProfile (取得個人資訊)
 * 功能說明: 傳入會員卡號，取得個人資訊
 * 對應CI API : QueryProfile
 */
public class CIInquiryProfileModel extends CIWSBaseModel{


    public interface InquiryCallback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryProfileSuccess( String rt_code, String rt_msg, CIProfileEntity profile );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryProfileError( String rt_code, String rt_msg );
    }


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

    private static final String API_NAME = "/CIAPP/api/InquiryProfile";

    private InquiryCallback m_callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryProfileModel( InquiryCallback callback ){
        this.m_callback = callback;
    }

    public void InquiryProfileFromWS( String strCardNo ){

        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.login_token.getString(), CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.card_no.getString(),     strCardNo);
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

        CIProfileEntity profile = GsonTool.toObject( respBody.strData, CIProfileEntity.class);

        if ( null == profile ){
            SendError_Response_can_not_parse();
            return;
        }

        if ( null != profile.con_facebook && "N".equals(profile.con_facebook) ){
            profile.facebook_mail = "";
            profile.facebook_uid = "";
        }
        if ( null != profile.con_google && "N".equals(profile.con_google) ){
            profile.google_uid = "";
            profile.google_mail = "";
        }
        //針對地址資訊特殊處理
        try {
            JSONObject jsData = new JSONObject(respBody.strData);
            JSONObject jsCountry = jsData.optJSONObject("countryGroup");
            if ( null != jsCountry ){
                profile.countryName = jsCountry.optString(profile.countryCode);
            }
            if ( TextUtils.isEmpty(profile.countryName) ){
                profile.countryName = profile.countryCode;
            }

            JSONObject jsCity = jsData.optJSONObject("cityGroup");
            if ( null != jsCity ){
                profile.cityName = jsCity.optString(profile.cityCode);
            }
            if ( TextUtils.isEmpty(profile.cityName) ){
                profile.cityName = profile.cityCode;
            }

            JSONObject jsCounty = jsData.optJSONObject("countyGroup");
            if ( null != jsCounty ){
                profile.countyName = jsCounty.optString(profile.countyCode);
            }
            if ( TextUtils.isEmpty(profile.countyName) ){
                profile.countyName = profile.countyCode;
            }

            JSONObject jsStreet1 = jsData.optJSONObject("streetGroup");
            if ( null != jsStreet1 ){
                profile.street1_name = jsStreet1.optString(profile.street1);
            }
            if ( TextUtils.isEmpty(profile.street1_name) ){
                profile.street1_name = profile.street1;
            }

            JSONObject jsZipCode = jsData.optJSONObject("zipCodeGroup");
            profile.zipCodeList = new ArrayList<>();
            if ( null != jsZipCode ){
                Iterator x = jsZipCode.keys();
                while ( x.hasNext() ){
                    String key = (String) x.next();
                    profile.zipCodeList.add(key);
                }
            }

            JSONObject jsCurrArea = jsData.optJSONObject("currAreaGroup");
            if ( null != jsCurrArea ){
                profile.currAreaName = jsCurrArea.optString(profile.currAreaCode);
                Iterator x = jsCurrArea.keys();
                if ( TextUtils.isEmpty(profile.currAreaName) && x.hasNext() ){
                    String strKey = (String) x.next();
                    profile.currAreaName = jsCurrArea.getString(strKey);
                }
            }
            if ( TextUtils.isEmpty(profile.currAreaName) ){
                profile.currAreaName = profile.currAreaCode;
            }

            /**代碼*/
            profile.addressType = CheckIsNull(profile.addressType);
            profile.countryCode = CheckIsNull(profile.countryCode);
            profile.countryName = CheckIsNull(profile.countryName);
            profile.cityCode = CheckIsNull(profile.cityCode);
            profile.cityName = CheckIsNull(profile.cityName);
            profile.countyCode = CheckIsNull(profile.countyCode);
            profile.countyName = CheckIsNull(profile.countyName);
            profile.street1 = CheckIsNull(profile.street1);
            profile.street1_name = CheckIsNull(profile.street1_name);
            profile.street2 = CheckIsNull(profile.street2);
            profile.street3 = CheckIsNull(profile.street3);
            profile.zipCode = CheckIsNull(profile.zipCode);
            profile.currAreaCode = CheckIsNull(profile.currAreaCode);
            profile.currAreaName = CheckIsNull(profile.currAreaName);

        } catch ( Exception e ){
            e.printStackTrace();
        }

        if ( null != m_callback ){

            m_callback.onInquiryProfileSuccess(respBody.rt_code, respBody.rt_msg, profile);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryProfile)) ,"");
        } else if ( null != m_callback ){
            m_callback.onInquiryProfileError(code, strMag);
        }
    }

    private String CheckIsNull( String strOrg ){
        if ( null == strOrg ){
            return "";
        }
        return strOrg;
    }
}
