package ci.ws.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIInquiryAwardRecordReq;
import ci.ws.Models.entities.CIInquiryAwardRecordResp;
import ci.ws.Models.entities.CIInquiryAwardRecordRespList;
import ci.ws.Models.entities.CITimeTableReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Kevin Cheng on 16/5/16.
 *
 * API Doc: CI_APP_API_MilesManagement.docx
 * API Doc update date: 2016/5/5 下午7:27 by Wii Lin
 * 4.5.	InquiryAwardRecord
 * 功能說明: 查詢兌換里程紀錄。
 */
public class CIInquiryAwardRecordModel extends CIWSBaseModel{


    public interface CallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code, String rt_msg, CIInquiryAwardRecordRespList datas);
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
//        year,               //查詢年份
        culture_info,       //回傳結果語言(zh-TW:繁體中文／zh-CN:簡體中文／ja-JP:日文／en-US:英文)
        device_id,          //須能辨識設備之唯一性，長度限制為固定32碼
        version,            //API版本
    }

    private static final String API_NAME = "/CIAPP/api/InquiryAwardRecord";

    private CallBack m_callback = null;

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryAwardRecordModel(CallBack callback){
        m_callback = callback;
    }

    public void setupRquest(CIInquiryAwardRecordReq reqData){

        m_jsBody = new JSONObject();
        try {
            //個人資訊
            m_jsBody.put( eParaTag.login_token.name(), CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.card_no.name(), CIApplication.getLoginInfo().GetUserMemberCardNo());

            //年份
//            m_jsBody.put( eParaTag.year.name(), reqData.year);

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

        CIInquiryAwardRecordRespList datas = null;

        try {
            datas = GsonTool.toObject(respBody.strData,CIInquiryAwardRecordRespList.class);

            //資料按日期排序
            Collections.sort(datas,
                    new Comparator<CIInquiryAwardRecordResp>() {
                        public int compare(CIInquiryAwardRecordResp info1, CIInquiryAwardRecordResp info2) {
                            return info2.flight_date.compareTo(info1.flight_date);
                        }
                    });
        } catch (Exception e ){
            e.printStackTrace();

            SendError_Response_can_not_parse();
            return;
        }

        if ( null == datas ){
            SendError_Response_can_not_parse();
            return;
        }

        if ( null != m_callback ){
            m_callback.onSuccess(respBody.rt_code, respBody.rt_msg, datas);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryAwardRecord)) ,"");
        } else if ( null != m_callback ){
            m_callback.onError(code, strMag);
        }
    }


}
