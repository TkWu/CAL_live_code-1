package ci.ws.Models;

import ci.function.Core.SLog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CINationalEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/2.
 * 3.1.	InquiryNationalList
 * 功能說明:取得國籍列表。
 * 對應CI API : QueryNatMealList
 */
public class CINationalListModel extends CIWSBaseModel {


    public interface InquiryCallback {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onNationalSuccess( String rt_code, String rt_msg );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onNationalError( String rt_code, String rt_msg );
    }

    private enum eParaTag {

        culture_info("culture_info"),
        device_id("device_id"),
        version("version");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    private static final String API_NAME = "/CIAPP/api/InquiryNationalList";

    private InquiryCallback m_Callback = null;

    private RuntimeExceptionDao<CINationalEntity, Integer> m_dao
            = CIApplication.getDbManager().getRuntimeExceptionDao(CINationalEntity.class);

    private static ConnectionSource m_connectionSource = CIApplication.getDbManager().getConnectionSource();

    private static HashMap<String, CINationalEntity> m_mapNational = new LinkedHashMap<String, CINationalEntity>();

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CINationalListModel( InquiryCallback callback ){

        this.m_Callback = callback;
    }

    public void getNationalListFrowWS(){

        m_jsBody = new JSONObject();
        try {
            m_jsBody.put( eParaTag.culture_info.getString(),        CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.device_id.getString(),           CIApplication.getDeviceInfo().getAndroidId());
            m_jsBody.put( eParaTag.version.getString(),             WSConfig.DEF_API_VERSION);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code){

        Gson gson = new Gson();
        ArrayList<CINationalEntity> arNationalList = null;
        Type listType = new TypeToken<List<CINationalEntity>>(){}.getType();

        arNationalList = gson.fromJson( respBody.strData, listType);

        if ( null == arNationalList ){
            SendError_Response_can_not_parse();
            return;
        }

        //

        //檢查DB
        if ( isTableExists() ){
            ClearDate();
        } else {
            CreateTable();
        }

        insert(arNationalList);

        if ( null != m_Callback){
            m_Callback.onNationalSuccess(respBody.rt_code, respBody.rt_msg );
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.NationalList)) ,"");
        } else {
            if (null != m_Callback) {
                m_Callback.onNationalError(code, strMag);
            }
        }
    }


    //DateBase相關介面

    public Boolean isTableExists(){
        return m_dao.isTableExists();
    }

    /**
     * 清空資料表所有資料
     * */
    public void ClearDate(){
        try {
            TableUtils.clearTable(m_connectionSource, CINationalEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreateTable(){
        try {
            TableUtils.createTable(m_connectionSource, CINationalEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insert( ArrayList<CINationalEntity> datas ) {
        try {
            m_dao.create(datas);
        }catch (Exception e){
           SLog.e("Exception", e.toString());
        }
    }

    public ArrayList<CINationalEntity> getNationalListFromDb(){

        ArrayList<CINationalEntity> arList =  (ArrayList<CINationalEntity>)m_dao.queryForAll();

        if ( arList.size() <= 0 ){
            getNationalListFrowWS();
            return null;
        }

        return  arList;
    }

    public HashMap<String, CINationalEntity> getNationalMapFromDb(){

        if ( m_mapNational.size() <= 0 ){

            ArrayList<CINationalEntity> arList =  getNationalListFromDb();
            if ( null != arList ){
                for ( CINationalEntity entity : arList ){

                    m_mapNational.put(entity.country_cd, entity);
                }
                return m_mapNational;
            } else {
                return null;
            }
        } else {
            return m_mapNational;
        }
    }

}
