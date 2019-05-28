package ci.ws.Models;

import android.text.TextUtils;
import ci.function.Core.SLog;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ci.function.Core.CIApplication;
import ci.ui.object.CIModelInfo;
import ci.ws.Models.cores.CIWSBaseModel;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CIFlightStationStatusODEntity;
import ci.ws.Models.entities.CIFlightStationStatusODList;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/3.
 * 3.1.	InquiryStation
 * 功能說明: 取得所有航站出發航站對應的目的航站清單資訊。
 * 對應CI API : GetStationInfo
 */
public class CIInquiryFlightStatusODModel extends CIWSBaseModel{


    public interface StationCallBack {
        /**
         * 成功由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param arStationList Response object
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onStationSuccess( String rt_code, String rt_msg,  ArrayList<CIFlightStationEntity> arStationList  );
        /**
         * 失敗由此訊息通知,
         * rt_code 規則同api文件
         *
         * @param rt_code   result code
         * @param rt_msg    result msg
         */
        void onStationError( String rt_code, String rt_msg );
    }

    private enum eParaTag {
        source,             //資料來源
        last_update_date,   //最後更新清單日期
        version,            //API版本
    }

    private enum eRespParaTag {
        is_update,          //是否更新
        station,            //航站資料
        arrival,            //抵達機場
        departure_iata,     //起飛機場代號
        IATA,               //機場代號
    }

    private static final String API_NAME = "/CIAPP/api/InquiryFlightODList";
    private StationCallBack m_callback = null;

    private final String DEF_WS_FORMAT_DATE             = "yyyy-MM-dd HH:mm:ss";

    private CIModelInfo modelInfo                       = null;

    private static ConnectionSource m_connectionSource  = CIApplication.getDbManager().getConnectionSource();

    private static Object   s_lock = new Object();

    private RuntimeExceptionDao<CIFlightStationStatusODEntity, Integer> m_dao
            = CIApplication.getDbManager().getRuntimeExceptionDao(CIFlightStationStatusODEntity.class);

    private RuntimeExceptionDao<CIFlightStationEntity, Integer> m_daoStation
            = CIApplication.getDbManager().getRuntimeExceptionDao(CIFlightStationEntity.class);

    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryFlightStatusODModel(StationCallBack callback ){
        m_callback = callback;
        modelInfo = new CIModelInfo(CIApplication.getContext());
    }

    public void getFromWS(){
        m_jsBody = new JSONObject();
        try {
            m_jsBody.put( eParaTag.version.name(), WSConfig.DEF_API_VERSION);
            m_jsBody.put( eParaTag.last_update_date.name(), modelInfo.GetFlightStatusODLastUpdateDate());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.DoConnection();
    }



    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        CIFlightStationStatusODList List = new CIFlightStationStatusODList();
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

            //如果有被更新，則將更新「最後更新日期」
            if (null != jsStationList && jsStationList.length() > 0) {
                List = paserJsonData(jsStationList);
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
            modelInfo.SetFlightStatusODLastUpdateDate(date);

            if ( null != m_callback ){
                m_callback.onStationSuccess(respBody.rt_code, respBody.rt_msg, null);
            }
        }
    }



    private CIFlightStationStatusODList paserJsonData(JSONArray jsArrayList) throws Exception{
        CIFlightStationStatusODList list = new CIFlightStationStatusODList();
        int ilength = jsArrayList.length();
        for ( int iIdx=0; iIdx < ilength; iIdx++ ){
            JSONObject jsObj = jsArrayList.getJSONObject(iIdx);
            if ( null != jsObj ){
                String strDepartureIATA = jsObj.getString(eRespParaTag.departure_iata.name());
                JSONArray jsarArrival = jsObj.getJSONArray(eRespParaTag.arrival.name());

                //依序依照國家將出發地點放入map
                int iArrlength = jsarArrival.length();
                for ( int iJdx = 0; iJdx<iArrlength; iJdx++ ){
                    String strArrivalIATA = jsarArrival.getJSONObject(iJdx).getString(eRespParaTag.IATA.name());

                    CIFlightStationStatusODEntity data = new CIFlightStationStatusODEntity();
                    data.departure_iata = strDepartureIATA;
                    data.arrival_iata = strArrivalIATA;
                    list.add(data);

                }
            }
        }
        return list;
    }

    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryStatusOD)) ,"");
        } else if ( null != m_callback ){
            m_callback.onStationError(code, strMag);
        }
    }

    private Boolean isTableExists(){
        return m_dao.isTableExists();
    }

    /**
     * 清空資料表所有資料
     * */
    public void Clear(){
        try {
            TableUtils.clearTable(m_connectionSource, CIFlightStationStatusODEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initLastUpdateDate(){
        modelInfo.SetFlightStatusODLastUpdateDate(CIModelInfo.DEF_LAST_UPDATE_DATE);
    }

    private void CreateTable(){
        try {
            TableUtils.createTable(m_connectionSource, CIFlightStationStatusODEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private boolean insert(ArrayList<CIFlightStationStatusODEntity> datas ) {
        try {
            m_dao.create(datas);
        }catch (Exception e){
           SLog.e("Exception", e.toString());
            return false;
        }
        return true;
    }
    public List<CIFlightStationEntity> getArrivalSrtationByDeparture(String strStationCode){
        return getArrivalSrtationByDepartureAndKeyWord(strStationCode, null, false);
    }

    /**取得出發地對應的抵達地*/
    public List<CIFlightStationEntity> getArrivalSrtationByDepartureAndKeyWord(String strStationCode,
                                                                               String key
                                                                                ,boolean isFindByOnlyIATA){
        List<CIFlightStationEntity> dataList = null;
        try {
            QueryBuilder<CIFlightStationStatusODEntity,Integer> ODBuilder = m_dao.queryBuilder();
            ODBuilder.where().eq("departure_iata",strStationCode);
            QueryBuilder<CIFlightStationEntity,Integer> stationBuilder = m_daoStation.queryBuilder();
            if(!TextUtils.isEmpty(key)){
                if(false == isFindByOnlyIATA){
                    stationBuilder.where().like("IATA", "%"+key+"%")
                            //2016-11-01 Ryan, 與iOS統一搜尋邏輯
//                            .or().like("airport_name", "%"+key+"%")
                            .or().like("city", "%"+key+"%")
                            .or().like("localization_name", "%"+key+"%");
                } else {
                    stationBuilder.where().eq("IATA", key);
                }
            }
            dataList = stationBuilder.join("IATA","arrival_iata",ODBuilder).query();
//            Log.e(API_NAME, "sql=> " + stationBuilder.prepareStatementString());
//            Log.e(API_NAME, "data " + GsonTool.getGson().toJson(dataList));
        } catch (SQLException e) {
            e.printStackTrace();
            initLastUpdateDate();
            return null;
        }

        if(dataList == null || dataList.size() <= 0){
            return null;
        } else {
            return dataList;
        }
    }

    public List<CIFlightStationEntity> getDepartureStation(){
        return getDepartureStationByKeyword(null, false);
    }

    /**取得出發地*/
    public List<CIFlightStationEntity> getDepartureStationByKeyword(String key,boolean isFindByOnlyIATA){
        List<CIFlightStationEntity> dataList = null;

        try {
            QueryBuilder<CIFlightStationStatusODEntity,Integer> ODBuilder = m_dao.queryBuilder();
            QueryBuilder<CIFlightStationEntity,Integer> stationBuilder = m_daoStation.queryBuilder();
            ODBuilder.groupBy("departure_iata");
            if(!TextUtils.isEmpty(key)){
                if(false == isFindByOnlyIATA){
                    stationBuilder.where().like("IATA", "%"+key+"%")
                            //2016-11-01 Ryan, 與iOS統一搜尋邏輯
//                            .or().like("airport_name", "%"+key+"%")
                            .or().like("city", "%"+key+"%")
                            .or().like("localization_name", "%"+key+"%");
                } else {
                    stationBuilder.where().eq("IATA", key);
                }
            }
            dataList = stationBuilder.join("IATA","departure_iata",ODBuilder).query();
//            Log.e(API_NAME, "sql=> " + stationBuilder.prepareStatementString());
//            Log.e(API_NAME, "initdata " + GsonTool.getGson().toJson(dataList));
        } catch (SQLException e) {
            e.printStackTrace();
            initLastUpdateDate();
            return null;
        }

//        Log.e(API_NAME, "sortData " + GsonTool.getGson().toJson(sorteddataList));
        if(dataList == null || dataList.size() <= 0){
            return null;
        } else {
            return dataList;
        }
    }

}
