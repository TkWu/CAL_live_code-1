package ci.ws.Models;

import ci.function.Core.SLog;

import com.google.gson.Gson;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CICouponRespList;
import ci.ws.Models.entities.CIInquiryCouponDBEntity;
import ci.ws.Models.entities.CIInquiryCouponResp;
import ci.ws.Models.entities.CIInquiryCoupon_Info;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by jlchen on 16/6/16.
 * 文件：CI_APP_API_Coupon.docx
 * 4.1.1 InquiryCouponInfo
 * 功能說明:取得大於系統日的Coupon資訊
 * 對應OracleDB : PPDB.PPTCUPN
 */
public class CIInquiryCouponInfoModel extends CIWSBaseModel {

    public interface CallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code, String rt_msg, CIInquiryCouponResp datas);
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
        language("language"),
        client_ip("client_ip"),
        Version("Version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/CIAPP/api/InquiryCouponInfo";
    private CallBack m_Callback = null;

    private RuntimeExceptionDao<CIInquiryCouponDBEntity, Integer> m_dao
            = CIApplication.getDbManager().getRuntimeExceptionDao(CIInquiryCouponDBEntity.class);

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryCouponInfoModel(CallBack Callback ){
        m_Callback = Callback;
    }

    public void sendReqDataToWS(){
        m_jsBody = new JSONObject();

        try {

            m_jsBody.put( eParaTag.language.name(), CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.client_ip.name(),CIApplication.getClentIp());
            m_jsBody.put( eParaTag.Version.name(),  WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();

            if ( null != m_Callback ){
                SendError_Response_can_not_parse();
            }
            return;
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        Gson gson = new Gson();
        CIInquiryCouponResp Resp = null;

        try {
            Resp = gson.fromJson( respBody.strData, CIInquiryCouponResp.class);

        } catch (Exception e ){
            e.printStackTrace();
            SendError_Response_can_not_parse();
            return;
        }

        if ( null == Resp || null == Resp.CouponInfo ){
            SendError_Response_can_not_parse();
            return;
        }

        //撈到資料存DB
        CIInquiryCouponDBEntity entity = new CIInquiryCouponDBEntity();
        entity.respResult = GsonTool.toJson(Resp.CouponInfo);

        saveDataToDB(entity);

        if ( null != m_Callback ){
            m_Callback.onSuccess(respBody.rt_code, respBody.rt_msg, Resp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(
                    WSConfig.InquiryCouponInfo)) ,"");
        } else
        if ( null != m_Callback ){
            m_Callback.onError(code, strMag);
        }
    }

    //回傳true表示資料存入db成功
    public boolean saveDataToDB(CIInquiryCouponDBEntity entity){
        try {
            entity.setId(0);

            m_dao.createOrUpdate(entity);
           SLog.d("BoardPassDB saveData", " "+entity.respResult);
        }catch (Exception e){
           SLog.e("CouponDB Exception", e.toString());
            return false;
        }

        //若存入的資料無法被解析，表示存取失敗
        if ( null == getDataFromDB() )
            return false;

        return true;
    }

    public ArrayList<CIInquiryCoupon_Info> getDataFromDB() {
        CICouponRespList list;
        try {
           SLog.d("CouponDB daoSize"," "+m_dao.queryForAll().size());
           SLog.d("CouponDB getData", ""+m_dao.queryForAll().get(0).respResult);
            list = GsonTool.toObject(
                    m_dao.queryForAll().get(0).respResult,
                    CICouponRespList.class);
        } catch (Exception e ){
           SLog.e("CouponDB Exception", e.toString());
            return null;
        }
        return list;
    }
}
