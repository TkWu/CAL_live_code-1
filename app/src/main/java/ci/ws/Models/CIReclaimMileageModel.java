package ci.ws.Models;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIBoardPassResp;
import ci.ws.Models.entities.CIReclaimErrorResp;
import ci.ws.Models.entities.CIReclaimMileageReq;
import ci.ws.Models.entities.CITimeTableReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.CIWSResultCode;
import ci.ws.define.WSConfig;

/**
 * Created by Kevin Cheng on 16/5/16.
 *
 * API Doc: CI_APP_API_MilesManagement.docx
 * API Doc update date: 2016/5/5 下午7:27 by Wii Lin
 * 4.6.	ReclaimMileage
 * 功能說明: 補登里程。
 */
public class CIReclaimMileageModel extends CIWSBaseModel{


    public interface CallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code, String rt_msg);  //一般錯誤
        void onSuccess007(String rt_code, String rt_msg, CIReclaimErrorResp re_data);  //-999錯誤
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onError(String rt_code, String rt_msg);
    }

    /**其餘參數參照
     * {@link CITimeTableReq}*/
    private enum eParaTag {
        login_token,        //使用Login時回傳的Token,
        card_no,            //會員卡號
        email,              //會員EMail
        update_email_chk,   //是否更新Email(True/False)
        dep_city1,          //啟程站 1 (3碼為英文字母大寫)
        arr_city1,          //終點站 1 (3碼為英文字母大寫)
        dep_date1,          //搭乘日期 1 (YYYY-MM-DD)
        cdc1,               //航空公司 1 (CI/AE)
        fno1,               //班機號碼 1 (4位數字)
        ticket_no1,         //機票號碼 1 (297/803(公司代號)+10位數字(票號))
        dep_city2,          //啟程站 2 (3碼為英文字母大寫)
        arr_city2,          //終點站 2 (3碼為英文字母大寫)
        dep_date2,          //搭乘日期 2 (YYYY-MM-DD)
        cdc2,               //航空公司 2 (CI/AE)
        fno2,               //班機號碼 2 (4位數字)
        ticket_no2,         //機票號碼 2 (297/803(公司代號)+10位數字(票號))
        culture_info,       //回傳結果語言(zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文)
        device_id,          //須能辨識設備之唯一性，長度限制為固定32碼
        version,            //API版本
    }

    private static final String API_NAME = "/CIAPP/api/ReclaimMileage";

    private CallBack m_callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIReclaimMileageModel(CallBack callback){
        m_callback = callback;
    }

    public void setupRquest(CIReclaimMileageReq reqData){

        m_jsBody = new JSONObject();

        try {
            //個人資訊
            m_jsBody.put( eParaTag.login_token.name(), CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.card_no.name(), CIApplication.getLoginInfo().GetUserMemberCardNo());
            m_jsBody.put( eParaTag.email.name(), reqData.email);
            m_jsBody.put( eParaTag.update_email_chk.name(), reqData.update_email_chk);
            //航班資訊  1
            m_jsBody.put( eParaTag.dep_city1.name(), reqData.dep_city1);
            m_jsBody.put( eParaTag.arr_city1.name(), reqData.arr_city1);
            m_jsBody.put( eParaTag.dep_date1.name(), reqData.dep_date1);
            m_jsBody.put( eParaTag.cdc1.name(), reqData.cdc1);
            m_jsBody.put( eParaTag.fno1.name(), reqData.fno1);
            m_jsBody.put( eParaTag.ticket_no1.name(), reqData.ticket_no1);
            //航班資訊  2
            m_jsBody.put( eParaTag.dep_city2.name(), reqData.dep_city2);
            m_jsBody.put( eParaTag.arr_city2.name(), reqData.arr_city2);
            m_jsBody.put( eParaTag.dep_date2.name(), reqData.dep_date2);
            m_jsBody.put( eParaTag.cdc2.name(), reqData.cdc2);
            m_jsBody.put( eParaTag.fno2.name(), reqData.fno2);
            m_jsBody.put( eParaTag.ticket_no2.name(), reqData.ticket_no2);
            //裝置環境資訊
            m_jsBody.put( eParaTag.culture_info.name(), CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.device_id.name(), CIApplication.getDeviceInfo().getAndroidId());
            m_jsBody.put( eParaTag.version.name(), WSConfig.DEF_API_VERSION);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {


        Gson gson = new Gson();
        CIReclaimErrorResp Resp = null;

        try {
            Resp = gson.fromJson( respBody.strData, CIReclaimErrorResp.class);

        } catch (Exception e ){
            e.printStackTrace();
        }

        if ( null == Resp ){
            if ( null != m_callback ){
                m_callback.onError( CIWSResultCode.NO_RESULTS, CIWSResultCode.getResultMessage(CIWSResultCode.NO_RESULTS) );
            }
            return;
        }

        if ( null != m_callback ){
            if (Resp.size() > 0){
                m_callback.onSuccess007(respBody.rt_code, respBody.rt_msg, Resp);
            }else{
                m_callback.onSuccess(respBody.rt_code, respBody.rt_msg);
            }
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( null != m_callback ){
            m_callback.onError(code, strMag);
        }
    }


}
