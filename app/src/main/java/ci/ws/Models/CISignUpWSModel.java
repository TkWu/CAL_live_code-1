package ci.ws.Models;

import ci.function.Core.SLog;

import org.json.JSONException;
import org.json.JSONObject;

import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CISignUpReq;
import ci.ws.Models.entities.CISignUpResp;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;

/**
 * Created by Ryan on 16/4/14.
 */
public class CISignUpWSModel extends CIWSBaseModel {

    public interface SignUpCallback {
        /**
         * 發送註冊的response, 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param SignupResp Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSignUpSuccess( String rt_code, String rt_msg, CISignUpResp SignupResp );
        /**
         * 發送註冊的response, 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSignUpError( String rt_code, String rt_msg );
    }

    public static final String API_NAME = "/CIAPP/api/SignUp";

    private SignUpCallback m_Listener = null;

    /**詳細變數以及參數定義請參照 {@link CISignUpReq}*/
    public enum eParaTag {

        culture_info("culture_info"),
        device_id("device_id"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    public CISignUpWSModel(  SignUpCallback listener  ){
        this.m_Listener = listener;
    }

    public void SignUpFromWS( CISignUpReq ciSignUpReq, String strLanguage, String strDevice_id, String strVersion ){

        try {
//            //測試ode
//            ciSignUpReq = new CISignUpReq();
//
//            ciSignUpReq.surname="MR";
//            ciSignUpReq.nation_code="TW";
//            ciSignUpReq.first_name="Albert";
//            ciSignUpReq.last_name="Wu";
//            ciSignUpReq.chin_name="Test Wu";
//            ciSignUpReq.birth_date="1969-11-22";
//            ciSignUpReq.guard_card_no="";
//            ciSignUpReq.guard_first_name="";
//            ciSignUpReq.guard_last_name="";
//            ciSignUpReq.guard_birth_date="";
//            ciSignUpReq.id_num="A123126197";
//            ciSignUpReq.passport="";
//            ciSignUpReq.email="albert_wu@systex.com";
//            ciSignUpReq.rcv_email="Y";
//            ciSignUpReq.cell_city="886";
//            ciSignUpReq.cell_num="0984031808";
//            ciSignUpReq.rcv_sms="N";
//            ciSignUpReq.password="123456";
//            ciSignUpReq.seat_code="NSSW";

            String strJson = GsonTool.toJson(ciSignUpReq);
            m_jsBody = new JSONObject(strJson);

            //補上固定參數
            m_jsBody.put(eParaTag.culture_info.getString(),  strLanguage );
            m_jsBody.put(eParaTag.device_id.getString(),     strDevice_id);
            m_jsBody.put(eParaTag.version.getString(),       strVersion);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        CISignUpResp ciSignUpResp = GsonTool.toObject(respBody.strData, CISignUpResp.class);

        if ( null == ciSignUpResp ){
            SendError_Response_can_not_parse();
            return;
        }

       SLog.e("[CISignUpWsModel]","[SignUpResp]"+respBody.strData.toString());
        if ( null != m_Listener ){
            m_Listener.onSignUpSuccess(respBody.rt_code, respBody.rt_msg, ciSignUpResp );
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
       SLog.e("[CISignUpWsModel]","[Error]"+code+" "+strMag);
        if ( null != m_Listener ){
            m_Listener.onSignUpError( code, strMag );
        }
    }


}
