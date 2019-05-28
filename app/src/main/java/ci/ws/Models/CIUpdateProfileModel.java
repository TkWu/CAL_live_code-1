package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIUpdateProfileEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/6.
 * 4.3.	UpdateProfile  (更新個人資訊)
 * 功能說明: 更新會員電子信箱帳號、是否接收促銷訊息、會員手機號碼及是否接受簡訊。
 * 對應CI API : ChangeProfile
 */
public class CIUpdateProfileModel extends CIWSBaseModel{


    public interface UpdateCallback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onUpdateProfileSuccess(String rt_code, String rt_msg );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onUpdateProfileError(String rt_code, String rt_msg);
    }


    private enum eParaTag {

        login_token,
        card_no,
        culture_info,
        device_id,
        version,
    }

    private static final String API_NAME = "/CIAPP/api/UpdateProfile";

    private UpdateCallback m_callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIUpdateProfileModel(UpdateCallback callback){
        this.m_callback = callback;
    }

    public void UpdateProfileFromWS( String strCardNo, CIUpdateProfileEntity profileEntity ){

        try {

//            strCardNo = "WD9751230";
//            profileEntity = new CIUpdateProfileEntity();
//            profileEntity.pass_port   = "1234567890";
//            profileEntity.email       = "barla1986@gmail.com";
//            profileEntity.rcv_email   = "Y";
//            profileEntity.cell_city   = "886";
//            profileEntity.cell_num    = "0912345678";
//            profileEntity.rcv_sms     = "N";
//            profileEntity.meal_type   = "";
//            profileEntity.seat_code   = CIUpdateProfileEntity.SEAT_NSSA;

            String strRequest = GsonTool.toJson(profileEntity);

            m_jsBody = new JSONObject(strRequest);

            m_jsBody.put( eParaTag.login_token.name(), CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.card_no.name(),     strCardNo);
            m_jsBody.put( eParaTag.culture_info.name(),CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.device_id.name(),   CIApplication.getDeviceInfo().getAndroidId());
            m_jsBody.put( eParaTag.version.name(),     WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        if ( null != m_callback ){

            m_callback.onUpdateProfileSuccess(respBody.rt_code, respBody.rt_msg );
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( null != m_callback ){
            m_callback.onUpdateProfileError(code, strMag);
        }
    }
}
