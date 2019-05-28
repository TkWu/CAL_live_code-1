package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIWSResult;

/**
 * Created by Ryan on 16/4/22.
 * 功能說明:以Card No.+First Name+Last Name+Birth Date為條件取得會員密碼。
 * 對應CI API : QueryPassword
 */
public class CIInquiryPasswordModel extends CIWSBaseModel{

    public interface InquiryPasswordCallback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param email     會員信箱
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryPasswordSuccess(String rt_code, String rt_msg, String email);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryPasswordError(String rt_code, String rt_msg);
    }

    private InquiryPasswordCallback m_callback = null;
    private static final String API_NAME = "/CIAPP/api/InquiryPassword";

    private enum eParaTag {

        card_no("card_no"),
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
     * @param card_no 會員卡號
     * @param first_name 會員英文名
     * @param last_name  會員英文姓氏
     * @param birth_date 會員生日      YYYY-MM-DD
     * */
    public CIInquiryPasswordModel(
            String card_no,
            String first_name,
            String last_name,
            String birth_date,
            String strLanguage,
            String strDevice_id,
            String strVersion,
            InquiryPasswordCallback listener){

        this.m_callback = listener;
        m_jsBody = new JSONObject();

        try {

            m_jsBody.put( eParaTag.card_no.getString(),     card_no);
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
            m_callback.onInquiryPasswordSuccess( respBody.rt_code, respBody.rt_msg, strEmail );
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( null != m_callback ){
            m_callback.onInquiryPasswordError( code , strMag);
        }
    }
}
