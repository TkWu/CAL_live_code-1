package ci.ws.Models;

import ci.function.Core.SLog;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIWSResult;

/**
 * Created by Ryan on 16/4/16.
 * 功能說明: 使用Email/Mobile +Identity/Passport Id取得卡號
 */
public class CIInquiryCardNoByIdentityWSModel extends CIWSBaseModel {


    public interface InquiryCallback {
        /**
         * @param strCard_no 會員卡號
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquirySuccess( String rt_code, String rt_msg, String strCard_no );

        /**
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryError( String rt_code, String rt_msg );
    }


    private static final String API_NAME = "/CIAPP/api/InquiryCardNoByIdentity";
    private InquiryCallback m_Listener = null;

    private enum eParaTag {

        account("account"),
        identity("identity"),
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

    public CIInquiryCardNoByIdentityWSModel( String strAccount, String stridentity, String strLanguage, String strDevice_id, String strVersion, InquiryCallback listener ){

        this.m_Listener = listener;
        m_jsBody = new JSONObject();
        try {
            m_jsBody.put( eParaTag.account.getString(),        strAccount);
            m_jsBody.put( eParaTag.identity.getString(),       stridentity);
            m_jsBody.put( eParaTag.culture_info.getString(),   strLanguage);
            m_jsBody.put( eParaTag.device_id.getString(),      strDevice_id);
            m_jsBody.put( eParaTag.version.getString(),        strVersion);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

       SLog.e("[InquiryCardNoById]","[CIWSResult]"+respBody.rt_code+" "+respBody.rt_msg+" "+respBody.strData);
        if ( null != m_Listener ){
            m_Listener.onInquirySuccess(respBody.rt_code, respBody.rt_msg, respBody.strData);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

       SLog.e("[InquiryCardNoById]","[Error]"+code+" "+strMag);
        if ( null != m_Listener ){
            m_Listener.onInquiryError( code , strMag);
        }
    }
}
