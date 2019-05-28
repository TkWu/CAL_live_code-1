package ci.ws.Models;

import android.text.TextUtils;
import ci.function.Core.SLog;

import com.google.gson.Gson;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import ci.function.Core.CIApplication;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisDocmuntTypeList;
import ci.ws.Models.entities.CIApisEntity;
import ci.ws.Models.entities.CIApisList;
import ci.ws.Models.entities.CIApisNationalEntity;
import ci.ws.Models.entities.CIApisNationalList;
import ci.ws.Models.entities.CIApisResp;
import ci.ws.Models.entities.CIApisStateEntity;
import ci.ws.Models.entities.CIApisStateList;
import ci.ws.Models.entities.CICompanionApisEntity;
import ci.ws.Models.entities.CICompanionApisNameEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.CIWSShareManager;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by joannyang on 16/5/23.
 * 功能說明:取得Apis列表。
 * 對應CI API : QueryNatApisList
 */
public class CIInquiryApisListModel extends CIWSBaseModel {
    final String APIS_DOCMENT_LIST_INFO_FILE= "APISDocmentTypeInfo.json";
    final String APIS_DOCMENT_LIST_FILE     = "APISDocmentType.json";
    final String APIS_NATIONAL_LIST_FILE    = "APISNationalList.json";
    final String APIS_STATE_LIST_FILE       = "APISState.json";

    public interface InquiryApisListCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param apis Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryApisListSuccess( String rt_code, String rt_msg, final CIApisResp apis );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onInquiryApisListError( String rt_code, String rt_msg );
    }

    private InquiryApisListCallBack m_callback = null;
    private static final String API_NAME = "/CIAPP/api/InquiryApis";

    private RuntimeExceptionDao<CIApisEntity, String> m_dao
            = CIApplication.getDbManager().getRuntimeExceptionDao(CIApisEntity.class);

    private RuntimeExceptionDao<CICompanionApisEntity, String> m_companionApisDao
            = CIApplication.getDbManager().getRuntimeExceptionDao(CICompanionApisEntity.class);

    private static ConnectionSource m_connectionSource = CIApplication.getDbManager().getConnectionSource();


    private enum eParaTag {

        login_token("login_token"),
        culture_info("culture_info"),
        device_id("device_id"),
        version("version"),
        card_no("card_no");

        private String strTag = "";

        eParaTag( String strTag ){this.strTag = strTag;}

        public String getString(){return strTag;}
    }

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

//    public CIInquiryApisListModel(){}

    public CIInquiryApisListModel(InquiryApisListCallBack listener ){ this.m_callback = listener; }

    public void InquiryApisFromWS(String strCardNo) {

        m_jsBody = new JSONObject();
        try {
            m_jsBody.put( eParaTag.login_token.getString(), CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.card_no.getString(),     strCardNo);
            m_jsBody.put( eParaTag.culture_info.getString(), CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.device_id.getString(),   CIApplication.getDeviceInfo().getAndroidId());
            m_jsBody.put( eParaTag.version.getString(),     WSConfig.DEF_API_VERSION);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    public void InsertApisFromWS(String strCardNo, CIApisEntity ciApisEntity) {
        try {

            String strRequest = GsonTool.toJson(ciApisEntity);

            m_jsBody = new JSONObject(strRequest);

            m_jsBody.put( eParaTag.login_token.getString(), CIWSShareManager.getAPI().getLoginToken());
            m_jsBody.put( eParaTag.card_no.getString(),     strCardNo);
            m_jsBody.put( eParaTag.culture_info.getString(), CIApplication.getLanguageInfo().getWSLanguage());
            m_jsBody.put( eParaTag.device_id.getString(),   CIApplication.getDeviceInfo().getAndroidId());
            m_jsBody.put( eParaTag.version.getString(),     WSConfig.DEF_API_VERSION);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        Gson gson = new Gson();
        CIApisResp apisResp = new CIApisResp();

        try{
            apisResp.arApisList = gson.fromJson( respBody.strData, CIApisList.class);
        } catch ( Exception e ){
            e.printStackTrace();
        }

        if ( null == apisResp.arApisList ){
            SendError_Response_data_null();
            return;
        }

        if ( null != m_callback ){
            m_callback.onInquiryApisListSuccess(respBody.rt_code, respBody.rt_msg, apisResp);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( null != m_callback ){
            m_callback.onInquiryApisListError(code, strMag);
        }
    }

    /**APIS DOC Type，目前只會有護照*/
    public CIApisDocmuntTypeList findDocmuntTypeData() {

        String json = getJsonFile(APIS_DOCMENT_LIST_FILE);

        String strData = ResultCodeCheck(json).strData;

        CIApisDocmuntTypeList datas = GsonTool.getGson().fromJson(strData, CIApisDocmuntTypeList.class);

        return datas;
    }


    /**查詢所有的APIS DOC Type*/
    public CIApisDocmuntTypeList findAllDocmuntTypeData() {

        String json = getJsonFile(APIS_DOCMENT_LIST_INFO_FILE);

        String strData = ResultCodeCheck(json).strData;

        CIApisDocmuntTypeList datas = GsonTool.getGson().fromJson(strData, CIApisDocmuntTypeList.class);

        return datas;
    }

    /**查詢所有的APIS DOC Type*/
    public HashMap<String, CIApisDocmuntTypeEntity> getApisDocmuntMap(){
        HashMap<String,CIApisDocmuntTypeEntity> map = new HashMap<>();
        CIApisDocmuntTypeList arList =  findAllDocmuntTypeData();//findDocmuntTypeData();
        if ( null != arList ){
            for ( CIApisDocmuntTypeEntity entity : arList ){
                map.put(entity.code_1A, entity);
            }
            return map;
        } else {
            return null;
        }

    }

    public CIApisNationalList findNationListData(){

        String json = getJsonFile(APIS_NATIONAL_LIST_FILE);

        String strData = ResultCodeCheck(json).strData;

        CIApisNationalList datas = GsonTool.getGson().fromJson(strData, CIApisNationalList.class);

        return datas;
    }

    public HashMap<String, CIApisNationalEntity> getApisNationalMap(){
        HashMap<String,CIApisNationalEntity> map = new HashMap<>();
        CIApisNationalList arList =  findNationListData();
        if ( null != arList ){
            for ( CIApisNationalEntity entity : arList ){
                map.put(entity.country_cd, entity);
            }
            return map;
        } else {
            return null;
        }

    }

    public HashMap<String,CIApisNationalEntity> getApisNationalResidentMap() {
        HashMap<String,CIApisNationalEntity> map = new HashMap<>();
        CIApisNationalList arList =  findNationListData();
        if ( null != arList ){
            for ( CIApisNationalEntity entity : arList ){
                map.put(entity.resident_cd, entity);
            }
            return map;
        } else {
            return null;
        }
    }

    public HashMap<String,CIApisNationalEntity> getApisNationalIssueMap() {
        HashMap<String,CIApisNationalEntity> map = new HashMap<>();
        CIApisNationalList arList =  findNationListData();
        if ( null != arList ){
            for ( CIApisNationalEntity entity : arList ){
                map.put(entity.issue_cd, entity);
            }
            return map;
        } else {
            return null;
        }
    }

    public CIApisStateList findStateListData() {

        String json = getJsonFile(APIS_STATE_LIST_FILE);

        String strData = ResultCodeCheck(json).strData;

        CIApisStateList datas = GsonTool.getGson().fromJson(strData, CIApisStateList.class);

        return datas;
    }

    public HashMap<String, CIApisStateEntity> getApisStateMap(){
        HashMap<String,CIApisStateEntity> map = new HashMap<>();
        CIApisStateList arList =  findStateListData();
        if ( null != arList ){
            for ( CIApisStateEntity entity : arList ){
                map.put(entity.code, entity);
            }
            return map;
        } else {
            return null;
        }

    }

    //DateBase相關介面
    public void CreateTable(Class _class) {
        try {
            TableUtils.createTable(m_connectionSource, _class );
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isMyApisTableExists() {
        return m_dao.isTableExists();
    }

    public boolean isCompanionsApisTableExists() {
        return m_companionApisDao.isTableExists();
    }


    /**
     * 清空資料表所有資料
     * */
    private void ClearData(Class _class){
        try {
            TableUtils.clearTable(m_connectionSource, _class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CIApisEntity> getMyApisListFromDB(String strCardNo) {
        if( isMyApisTableExists()) {
            return (ArrayList<CIApisEntity>)m_dao.queryForEq("card_no",strCardNo);
        }

        return null;
    }

    /**
     * 清空對應 card_no 的 MyApis 資料
     * @param strCardNo
     */
    public void ClearMyApisData(String strCardNo) {

        if( TextUtils.isEmpty(strCardNo) ) {
            return;
        }

        try {
            DeleteBuilder<CIApisEntity, String> deleteBuilder = m_dao.deleteBuilder();
            deleteBuilder.where().eq("card_no", strCardNo);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMyApisData(CIApisEntity data) {
        if( null == data ) {
            return;
        }

        if( isMyApisTableExists() && m_dao.idExists(data.getId())) {
            m_dao.delete(data);

        }
    }

    public void deleteCompanionApisData(CICompanionApisEntity data) {
        if( null == data ) {
            return;
        }

        if( isCompanionsApisTableExists() ) {
            m_companionApisDao.delete(data);
        } else {
            CreateTable(CICompanionApisEntity.class);
        }
    }



    public void saveMyAPIS(CIApisEntity data) {

        if( null == data ) {
            return;
        }

        if( !isMyApisTableExists() ) {
            CreateTable(CIApisEntity.class);
        }

        try {

            if( m_dao.idExists(data.getId()) ) {
                m_dao.updateId(data,data.getId());
            } else {
                m_dao.createOrUpdate(data);
            }
        }catch (Exception e){
           SLog.e("Exception", e.toString());
        }
    }

    public void saveMyAPIS(String strCardNo, ArrayList<CIApisEntity> ar_datas) {

        if( null == ar_datas ) {
            return;
        }

        if( !isMyApisTableExists() ) {
            CreateTable(CIApisEntity.class);
        }

        try {
            ClearMyApisData(strCardNo);
            m_dao.create(ar_datas);

        }catch (Exception e){
           SLog.e("Exception", e.toString());
        }
    }

    public void saveCompanionApis(CICompanionApisEntity data) {

        if( !isCompanionsApisTableExists()) {
            CreateTable(CICompanionApisEntity.class);
        }

        try {

            if( m_companionApisDao.idExists(data.getId()) ) {
                m_companionApisDao.update(data);
            } else {
                m_companionApisDao.create(data);
            }
        }catch (Exception e){
           SLog.e("Exception", e.toString());
        }

    }

    public ArrayList<CICompanionApisNameEntity> getCompanionApisList(String strCardNo) {
        if( !isCompanionsApisTableExists()) {
            CreateTable(CICompanionApisEntity.class);

            return null;
        }

        try {

            ArrayList<CICompanionApisNameEntity> ar_apisCompanionList = new ArrayList<>();

            QueryBuilder builder = m_companionApisDao.queryBuilder();
            ArrayList<CICompanionApisEntity> arCompanionNameList =
                    (ArrayList<CICompanionApisEntity>)builder.distinct().selectColumns("full_name").orderBy("full_name",true).where().eq("card_no",strCardNo).query();

            if( null == arCompanionNameList ) {
                return null;
            }

            ArrayList<String> arFullName = new ArrayList<String>();

            for( CICompanionApisEntity entity : arCompanionNameList) {

                arFullName.add(entity.full_name);
            }

            for( String strFullName: arFullName ) {

                ArrayList<CICompanionApisEntity> arCompanionList = getCompanionApisList(strCardNo,strFullName);

                CICompanionApisNameEntity nameEntity = new CICompanionApisNameEntity();
                for( CICompanionApisEntity entity : arCompanionList ) {


                    nameEntity.full_name = strFullName;
                    nameEntity.display_name =
                            (TextUtils.isEmpty(entity.first_name)? "" : entity.first_name) +" " + (TextUtils.isEmpty(entity.last_name)? "" : entity.last_name);


                    CIApisEntity apisEntity     = new CIApisEntity();
                    apisEntity.doc_type         = entity.doc_type;
                    apisEntity.doc_no           = entity.doc_no;
                    apisEntity.nationality      = entity.nationality;
                    apisEntity.doc_expired_date = entity.doc_expired_date;
                    apisEntity.issue_country    = entity.issue_country;
                    apisEntity.resident_city    = entity.resident_city;
                    apisEntity.last_name        = entity.last_name;
                    apisEntity.first_name       = entity.first_name;
                    apisEntity.birthday         = entity.birthday;
                    apisEntity.sex              = entity.sex;
                    apisEntity.addr_street      = entity.addr_street;
                    apisEntity.addr_city        = entity.addr_city;
                    apisEntity.addr_state       = entity.addr_state;
                    apisEntity.addr_country     = entity.addr_country;
                    apisEntity.addr_zipcode     = entity.addr_zipcode;
                    apisEntity.card_no          = entity.card_no;
                    apisEntity.setId(entity.getId());

                    nameEntity.arCompanionApisList.add(apisEntity);

                }

                ar_apisCompanionList.add(nameEntity);
            }

            return ar_apisCompanionList;


        } catch (Exception e){
           SLog.e("Exception", e.toString());
        }

        return null;
    }

    public ArrayList<CICompanionApisEntity> getCompanionApisList( String strCardNo, String strFullName ) {

        if( !isCompanionsApisTableExists()) {
            CreateTable(CICompanionApisEntity.class);
            return null;
        }

        Field[] fields = CICompanionApisEntity.class.getDeclaredFields();

        String[] arFieldName = new String[fields.length];
        for( int iPos = 0 ; iPos < fields.length ; iPos ++ ) {
            arFieldName[iPos] = fields[iPos].getName();
        }

        try {
            QueryBuilder builder = m_companionApisDao.queryBuilder();

            ArrayList<CICompanionApisEntity> arCompanionList =
                    (ArrayList<CICompanionApisEntity>) builder.selectColumns(arFieldName).orderBy("doc_type", false).where().eq("full_name", strFullName).eq("card_no", strCardNo).and(2).query();

            return arCompanionList;
        } catch (SQLException e) {
           SLog.e("Exception", e.toString());
        }

        return null;
    }

}
