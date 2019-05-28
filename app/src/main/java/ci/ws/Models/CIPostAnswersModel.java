package ci.ws.Models;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIPendingQuestionnaireEntity;
import ci.ws.Models.entities.CIPushQuestionnaireReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.WSConfig;

/**
 * Created by Kevin Cheng on 17/5/11.
 * Doc. : CA_app_questionnaire_20170503
 * 功能說明:提交問卷調查。
 */
public class CIPostAnswersModel extends CIWSBaseModel {


    public interface InquiryCallback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onSuccess(String rt_code, String rt_msg);
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
        cardid("cardid"),
        language("language"),
        version("version"),
        answers("answers"),
        departure("departure"),
        departure_date("departure_date"),
        departure_time("departure_time"),
        arrvial_date("arrvial_date"),
        arrvial_time("arrvial_time"),
        arrival("arrival"),
        fltnumber("fltnumber"),
        Name("Name"),
        seat("seat");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/mobile30/quessite/api/PostAnswers";

    private InquiryCallback m_Callback = null;

    private RuntimeExceptionDao<CIPendingQuestionnaireEntity, Integer> m_dao
            = CIApplication.getDbManager().getRuntimeExceptionDao(CIPendingQuestionnaireEntity.class);

    private static ConnectionSource m_connectionSource = CIApplication.getDbManager().getConnectionSource();

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    @Override
    protected String getURL() {
        return WSConfig.DEF_WS_SITE_QUES;
    }

    public CIPostAnswersModel(InquiryCallback callback){

        this.m_Callback = callback;
    }

    public void postQuestionnaireToWS(CIPushQuestionnaireReq req){

        m_jsBody = new JSONObject();
        try {
            JSONObject reqdata = new JSONObject();
            reqdata.put(eParaTag.cardid.getString(), req.cardid);
            reqdata.put(eParaTag.language.getString(), req.language);
            reqdata.put(eParaTag.answers.getString(), req.answers);
            reqdata.put(eParaTag.departure.getString(), req.departure);
            reqdata.put(eParaTag.departure_date.getString(), req.departure_date);
            reqdata.put(eParaTag.arrival.getString(), req.arrival);
            reqdata.put(eParaTag.fltnumber.getString(), req.fltnumber);
            m_jsBody.put(eParaTag.InputObj.getString(), reqdata);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnectionWithoutAuth();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code){


        if ( null != m_Callback){
            m_Callback.onSuccess(respBody.rt_code, respBody.rt_msg);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.PushQuestionnaire)) ,"");
        } else {
            if (null != m_Callback) {
                m_Callback.onError(code, strMag);
            }
        }
    }

    public void ClearDate(){
        try {
            TableUtils.clearTable(m_connectionSource, CIPendingQuestionnaireEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreateTable(){
        try {
            if(false == m_dao.isTableExists()) {
                TableUtils.createTable(m_connectionSource, CIPendingQuestionnaireEntity.class);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertOrUpdate(CIPendingQuestionnaireEntity datas) {
        try {
            CreateTable();
            m_dao.createOrUpdate(datas);
        }catch (Exception e){
           SLog.e("Exception", e.toString());
        }
    }

    public ArrayList<CIPendingQuestionnaireEntity> findDataByCardIdAndVersion(String token, String version){

        ArrayList<CIPendingQuestionnaireEntity> arList = null;
        try {
            CreateTable();
            Map<String, Object> queryKey = new HashMap<>();
            queryKey.put("token", token);
            queryKey.put("version", version);

            arList = (ArrayList<CIPendingQuestionnaireEntity>)m_dao.queryForFieldValues(queryKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  arList;
    }

    public void delete(int id){
        try {
            m_dao.deleteById(id);
        }catch (Exception e){

        }
    }



}
