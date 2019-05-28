package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIWSResult;

/**
 * Created by JobsNo5 on 16/4/22.
 * 功能說明: 以First Name+Last Name+Birth Date為條件取得會員編號。
 * 對應CI API : QueryCardNo
 */
public class CIInquiryCardNoModel extends CIWSBaseModel{

    public interface InquiryCardNoCallback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param email     會員信箱
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryCardNoSuccess( String rt_code, String rt_msg, String email );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryCardNoError( String rt_code, String rt_msg );
    }

    private InquiryCardNoCallback m_callback = null;
    private static final String API_NAME = "/CIAPP/api/InquiryCardNo";

    public static final int    CONNECTION_TIME_OUT = 40 * 1000;
    public static final int    READ_TIME_OUT       = 40 * 1000;

    private enum eParaTag {

        first_name("first_name"),
        last_name("last_name"),
        birth_date("birth_date"),
        culture_info("culture_info"),
        device_id("device_id"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    /**
     * @param first_name 會員英文名
     * @param last_name  會員英文姓氏
     * @param birth_date 會員生日      YYYY-MM-DD
     * */
    public CIInquiryCardNoModel( String first_name, String last_name, String birth_date, String strLanguage, String strDevice_id, String strVersion, InquiryCardNoCallback listener ){

        this.m_callback = listener;
        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.first_name.getString(),  first_name);
            m_jsBody.put( eParaTag.last_name.getString(),   last_name);
            m_jsBody.put( eParaTag.birth_date.getString(),  birth_date);
            m_jsBody.put( eParaTag.culture_info.getString(),strLanguage);
            m_jsBody.put( eParaTag.device_id.getString(),   strDevice_id);
            m_jsBody.put( eParaTag.version.getString(),     strVersion);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        String strEmail = "";

        try {
            JSONObject jsObj = new JSONObject(respBody.strData);
            if ( null != jsObj ){
                if ( jsObj.has("email") && jsObj.isNull("email") ){
                    strEmail = jsObj.getString("email");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ( null != m_callback ){
            m_callback.onInquiryCardNoSuccess( respBody.rt_code, respBody.rt_msg, strEmail );
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( null != m_callback ){
            m_callback.onInquiryCardNoError( code , strMag);
        }
    }


    protected int getConnectTimeout() {
        return CONNECTION_TIME_OUT;
    }

    protected int getReadTimeout() {
        return READ_TIME_OUT;
    }
}
