package ci.ws.Models;

import ci.function.Core.SLog;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ci.function.Core.CIApplication;
import ci.ui.object.CIModelInfo;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CIFlightStationList;
import ci.ws.Models.entities.CITimeTableReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/3.
 * Last Edited:Kevin Cheng
 *
 * API Doc: CI_APP_API_BookTicket.docx
 * API Doc update date: 2016/05/06 by Wii Lin
 * 4.1.	InquiryStationList
 * 功能說明: 取得所有航站總表。
 */
public class CIInquiryStationListModel extends CIWSBaseModel{


    public interface StationCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param arStationList Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onStationSuccess(String rt_code, String rt_msg, ArrayList<CIFlightStationEntity> arStationList);
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onStationError(String rt_code, String rt_msg);
    }

    /**其餘參數參照
     * {@link CITimeTableReq}*/
    private enum eParaTag {
        source,             //資料來源
        language,           //語系
        last_update_date,   //最後更新清單日期
        version,            //API版本
    }

    private enum eRespParaTag {
        is_update,          //是否更新
        station,            //航站資料
    }

    private static final String API_NAME = "/CIAPP/api/InquiryStationList";
    private StationCallBack m_callback = null;

    private static Object   s_lock = new Object();

    private RuntimeExceptionDao<CIFlightStationEntity, Integer> m_dao
            = CIApplication.getDbManager().getRuntimeExceptionDao(CIFlightStationEntity.class);

    private static ConnectionSource m_connectionSource = CIApplication.getDbManager().getConnectionSource();

    private final String SOURCE                         = "TIMETABLE";

    private final String DEF_WS_FORMAT_DATE             = "yyyy-MM-dd HH:mm:ss";

    private String m_strLang = "";

    private CIModelInfo   modelInfo                     = null;
    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryStationListModel(StationCallBack callback){
        m_callback = callback;
        modelInfo = new CIModelInfo(CIApplication.getContext());
    }

    public void getFromWS(){

        //如果最後更新的語系與系統目前的不同就以預設的最後更新日期去要資料，否則則以儲存的在SharedPreferece的資料
        m_strLang = CIApplication.getLanguageInfo().getWSLanguage();
        String strLastDate = "";
        if(false == modelInfo.GetStationListLastUpdateLanguage().equals(m_strLang)){
            strLastDate = CIModelInfo.DEF_LAST_UPDATE_DATE;
        } else {
            strLastDate = modelInfo.GetStationListLastUpdateDate();
        }

        m_jsBody = new JSONObject();

        try {
            m_jsBody.put( eParaTag.language.name(), m_strLang);
            m_jsBody.put( eParaTag.version.name(), WSConfig.DEF_API_VERSION);
            m_jsBody.put( eParaTag.source.name(), SOURCE);
            m_jsBody.put( eParaTag.last_update_date.name(), strLastDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        ArrayList<CIFlightStationEntity> List = new ArrayList<>();

        try {
            JSONObject jsResp = new JSONObject(respBody.strData);
            boolean jsIsUpate = jsResp.getBoolean(eRespParaTag.is_update.name());

            //沒有更新就跳出
            if (false == jsIsUpate) {
                if ( null != m_callback ){
                    m_callback.onStationSuccess(respBody.rt_code, respBody.rt_msg, null);
                }
                return;
            }

            JSONArray jsStationList = jsResp.getJSONArray(eRespParaTag.station.name());

            //如果有被更新，則將更新「最後更新日期」及「最後更新語言」
            if (null != jsStationList && jsStationList.length() > 0) {
                List = GsonTool.getGson().fromJson(jsStationList.toString(), CIFlightStationList.class);
            } else {
                SendError_Response_can_not_parse();
                return;
            }
        } catch (Exception e){
            e.printStackTrace();
            SendError_Response_can_not_parse();
            return;
        }
        synchronized (s_lock){
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

            //成功刷新資料庫才能刷新更新記錄
            String date = convertDateFormat(DEF_WS_FORMAT_DATE, Calendar.getInstance());
            modelInfo.SetStationListLastUpdateDate(date);
            /*可能同時會有兩個以上請求機場資料，為了避免發生寫入最後更新的語言寫入錯誤
              最後，更新的語言同向server請求資料時的語言 */
            modelInfo.SetStationListLastUpdateLanguage(m_strLang);

            if ( null != m_callback ){
                m_callback.onStationSuccess(respBody.rt_code, respBody.rt_msg, null);
            }

        }
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryStationList)) ,"");
        } else if ( null != m_callback ){
            m_callback.onStationError(code, strMag);
        }
    }

    //DateBase相關介面

    private Boolean isTableExists(){
        return m_dao.isTableExists();
    }

    /**
     * 清空資料表所有資料
     * */
    public void Clear(){
        try {
            TableUtils.clearTable(m_connectionSource, CIFlightStationEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initLastUpdateDate(){
        modelInfo.SetStationListLastUpdateDate(CIModelInfo.DEF_LAST_UPDATE_DATE);
    }

    private void CreateTable(){
        try {
            TableUtils.createTable(m_connectionSource, CIFlightStationEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private boolean insert(ArrayList<CIFlightStationEntity> datas ) {
        try {
            m_dao.create(datas);
        }catch (Exception e){
           SLog.e("Exception", e.toString());
            return false;
        }
        return true;
    }

    /**取得所有出發地*/
    public List<CIFlightStationEntity> getAllStationList(){
        List<CIFlightStationEntity> datas;
        try {
            datas = m_dao.queryForAll();
        } catch (Exception e) {
            e.printStackTrace();
            initLastUpdateDate();
            return null;
        }
        if (null == datas || datas.size() <= 0) {
            return null;
        }
        return datas;
    }

    /**
     * 依據IATA機場代碼取得機場資訊
     * */
    public CIFlightStationEntity getStationInfoByIATA(String iata) {
        List<CIFlightStationEntity> datas;
        try {
            if(null == iata){
                iata = "";
            }
            datas = m_dao.queryBuilder().where().eq("IATA", iata).query();
        } catch (SQLException e) {
            e.printStackTrace();
            initLastUpdateDate();
            return null;
        }
        if (null == datas || datas.size() <= 0) {
            return null;
        }
        return datas.get(0);
    }

    /**取得所有出發地*/
    public HashMap<String, ArrayList<CIFlightStationEntity>> getAllStationMap(){

        List<CIFlightStationEntity> datas = getAllStationList();
        if ( null == datas){
            return null;
        }

        HashMap<String, ArrayList<CIFlightStationEntity>> mapDepatureList = new HashMap<>();
        for ( CIFlightStationEntity stationEntity : datas){
            if ( mapDepatureList.containsKey(stationEntity.country) ){
                mapDepatureList.get(stationEntity.country).add(stationEntity);
            } else {
                ArrayList<CIFlightStationEntity> arList = new ArrayList<>();
                arList.add(stationEntity);
                mapDepatureList.put(stationEntity.country, arList);
            }
        }
        return mapDepatureList;
    }


}
