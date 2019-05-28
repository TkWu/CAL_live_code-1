package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIPullQuestionnaireReq;
import ci.ws.Models.entities.CIPullQuestionnaireResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by KevinCheng on 16/5/2.
 * Doc. : CA_app_questionnaire_20170503
 * 功能說明：取得問卷調查題目。
 */
public class CIPostQuestionsModel extends CIWSBaseModel {


    public interface InquiryCallback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         * @param rt_code   result code
         * @param rt_msg    result msg
         * @param data      result data
         */
        void onSuccess(String rt_code, String rt_msg, CIPullQuestionnaireResp data);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onError(String rt_code, String rt_msg);
    }

    private enum eParaTag {
        InputObj("InputObj"),
        PNR("PNR"),
        token("token"),
        cardid("cardid"),
        departure("departure"),
        departure_date("departure_date"),
        arrival("arrival"),
        fltnumber("fltnumber"),
        language("language");
        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/mobile30/quessite/api/PostQuestions";

    private InquiryCallback m_Callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    @Override
    protected String getURL() {
        return WSConfig.DEF_WS_SITE_QUES;
    }

    public CIPostQuestionsModel(InquiryCallback callback){

        this.m_Callback = callback;
    }

    public void getQuestionnaireFrowWS(CIPullQuestionnaireReq req){

        m_jsBody = new JSONObject();
        try {
            JSONObject reqdata = new JSONObject();
            reqdata.put(eParaTag.PNR.getString(), req.PNR);
            reqdata.put(eParaTag.token.getString(), req.token);
            reqdata.put(eParaTag.cardid.getString(), req.cardid);
            reqdata.put(eParaTag.departure.getString(), req.departure);
            reqdata.put(eParaTag.departure_date.getString(), req.departure_date);
            reqdata.put(eParaTag.arrival.getString(), req.arrival);
            reqdata.put(eParaTag.fltnumber.getString(), req.fltnumber);
            reqdata.put(eParaTag.language.getString(), CIApplication.getLanguageInfo().getWSLanguage());

            m_jsBody.put(eParaTag.InputObj.getString(), reqdata);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnectionWithoutAuth();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code){


        CIPullQuestionnaireResp data
                = GsonTool
                .getGson()
                .fromJson(respBody.strData, CIPullQuestionnaireResp.class);

        if ( null == data ){
            SendError_Response_can_not_parse();
            return;
        }

        if ( null != m_Callback){
            m_Callback.onSuccess(respBody.rt_code, respBody.rt_msg , data);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.PullQuestionnaire)) ,"");
        } else {
            if (null != m_Callback) {
                m_Callback.onError(code, strMag);
            }
        }
    }

}
