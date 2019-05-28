package ci.ws.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/15.
 */
public class CIInquiryMealTermsModel extends CIWSBaseModel{


    public interface InquiryMealTermsCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param strMealteams Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryMealTermsSuccess( String rt_code, String rt_msg, String strMealteams );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryMealTermsError( String rt_code, String rt_msg );
    }

    private enum eParaTag {

        platform,
        language,
        client_ip,
        version,
    }

    private static final String API_NAME = "/CIAPP/api/InquiryMealTerms";
    private InquiryMealTermsCallBack m_callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryMealTermsModel( InquiryMealTermsCallBack callback  ){
        m_callback = callback;
    }

    public void InquiryMealTerms(){

        m_jsBody = new JSONObject();
        try {

            m_jsBody.put( eParaTag.platform.name(), "ANDROID");
            m_jsBody.put( eParaTag.language.name(), CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.client_ip.name(),CIApplication.getClentIp());
            m_jsBody.put( eParaTag.version.name(),  WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        String strMealTeams = "";

        try {
            JSONArray jsarData = new JSONArray(respBody.strData);
            int ilength = jsarData.length();
            for ( int iIdx = 0; iIdx < ilength; iIdx++ ){

                JSONObject jsData = jsarData.getJSONObject(iIdx);
                String strTeam = GsonTool.getStringFromJsobject(jsData,"terms_desc");

                strMealTeams += strTeam;
                if ( iIdx < (ilength -1) ){
                    strMealTeams += "\n\n";
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ( strMealTeams.length() <= 0 ){
            SendError_Response_can_not_parse();
            return;
        }

        if ( null != m_callback ){
            m_callback.onInquiryMealTermsSuccess(respBody.rt_code, respBody.rt_msg, strMealTeams);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ) {
            DecodeResponse_Success(ResultCodeCheck(getJsonFile(WSConfig.InquiryMealTerms)), "");
        } else if ( null != m_callback ){
            m_callback.onInquiryMealTermsError(code, strMag);
        }
    }
}
