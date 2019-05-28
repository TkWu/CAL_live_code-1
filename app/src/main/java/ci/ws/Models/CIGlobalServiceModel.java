package ci.ws.Models;


import ci.function.Core.SLog;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import ci.function.Core.CIApplication;
import ci.ws.Models.entities.CIGlobalServiceEntity;
import ci.ws.Models.entities.CIGlobalServiceList;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by kevincheng on 2016/3/5.
 * 主要執行對資料庫中branch資料表存取的行為
 */
public class CIGlobalServiceModel extends CIWSBaseModel {

    public interface CallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
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

    private static final String TAG          = CIGlobalServiceModel.class.getSimpleName();
    private RuntimeExceptionDao<CIGlobalServiceEntity, Integer> m_dao
            = CIApplication.getDbManager().getRuntimeExceptionDao(CIGlobalServiceEntity.class);
    private static ConnectionSource                                    s_connectionSource
            = CIApplication.getDbManager().getConnectionSource();
    private static final String API_NAME = "/CIAPP/api/InquiryBranch";
    private CallBack            m_callback = null;

    private enum eParaTag {
        language, version
    }

    private enum eRespParaTag {
        BranchBean
    }

    public CIGlobalServiceModel(CallBack callBack){
        m_callback = callBack;
    }

    public void getFromWS(){

        String lang = CIApplication.getLanguageInfo().getWSLanguage();
        m_jsBody    = new JSONObject();
        try {
            m_jsBody.put( eParaTag.language.name(), lang);
            m_jsBody.put( eParaTag.version.name(), WSConfig.DEF_API_VERSION);
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
        CIGlobalServiceList List ;

        try {
            JSONObject jsResp = new JSONObject(respBody.strData);

            JSONArray jsList = jsResp.getJSONArray(eRespParaTag.BranchBean.name());

            if (null != jsList && jsList.length() > 0) {
                List = GsonTool.getGson().fromJson(jsList.toString(), CIGlobalServiceList.class);
            } else {
                SendError_Response_can_not_parse();
                return;
            }
        } catch (Exception e){
            e.printStackTrace();
            SendError_Response_can_not_parse();
            return;
        }

        //檢查DB
        if ( isTableExists() ){
            Clear();
        } else {
            CreateTable();
        }

        if(false == insert(List)){
            DecodeResponse_Error("", SQLException.class.getSimpleName(), null);
            return;
        }


        if ( null != m_callback ){
            m_callback.onSuccess(respBody.rt_code, respBody.rt_msg);
        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {
        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryBranch)) ,"");
        } else if ( null != m_callback ){
            m_callback.onError(code, strMag);
        }
    }

    public CIGlobalServiceList findData() {
        List<CIGlobalServiceEntity> list     = m_dao.queryForAll();
        CIGlobalServiceList         entities = new CIGlobalServiceList();
        for (CIGlobalServiceEntity data : list) {
            entities.add(data);
        }
        return entities;
    }

    public CIGlobalServiceEntity findDataByBranchName(String branchName){
        Where<CIGlobalServiceEntity, Integer> where = m_dao.queryBuilder().where();
        List<CIGlobalServiceEntity> dataList = null;
        try {
            dataList = where.eq("branch_name", branchName).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        if(null == dataList || 0 >= dataList.size() ){
            return null;
        } else {
            return dataList.get(0);
        }
    }


    public CIGlobalServiceList findDataByBranch(String str){
        Where<CIGlobalServiceEntity, Integer> where = m_dao.queryBuilder().where();
        List<CIGlobalServiceEntity> dataList = null;
        try {
            dataList = where.like("branch", "%"+str+"%").or().like("address", "%"+str+"%").query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        CIGlobalServiceList list = new CIGlobalServiceList();
        for(Object data:dataList){
            list.add((CIGlobalServiceEntity)data);
        }

        return list;
    }

    public void update(CIGlobalServiceEntity entity) {
        m_dao.update(entity);
    }

    private Boolean isTableExists(){
        return m_dao.isTableExists();
    }

    private boolean insert(CIGlobalServiceList datas ) {
        try {
            m_dao.create(datas);
        }catch (Exception e){
           SLog.e("Exception", e.toString());
            return false;
        }
        return true;
    }
    /**
     * 清空資料表所有資料
     * */
    public void Clear(){
        try {
            TableUtils.clearTable(s_connectionSource,
                                  CIGlobalServiceEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreateTable(){
        try {
            TableUtils.createTable(s_connectionSource, CIFlightStationEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
