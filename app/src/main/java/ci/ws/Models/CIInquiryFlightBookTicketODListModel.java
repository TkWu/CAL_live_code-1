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
import ci.ws.Models.entities.CIFlightStationBookTicketODEntity;
import ci.ws.Models.entities.CIFlightStationBookTicketODList;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CITimeTableReq;
import ci.ws.Models.entities.CIWSResult;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/5/3.
 * Last Edited:Kevin Cheng
 *
 * API Doc: CI_APP_API_BookTicket.docx
 * API Doc update date: 2016/05/06 by Wii Lin
 * 4.2.	InquiryODList
 * 功能說明: 取得出發地、目的地清單。
 */
public class CIInquiryFlightBookTicketODListModel extends CIWSBaseModel{


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
        last_update_date,   //最後更新清單日期
        version,            //API版本
    }

    private enum eRespParaTag {
        is_update,          //是否更新
        station,            //航站資料
        arrival,            //抵達機場
        departure_iata,     //起飛機場代號
        IATA,               //機場代號
        isOriginal,         //isOriginal欄位
    }

    private static final String API_NAME = "/CIAPP/api/InquiryODList";
    private StationCallBack m_callback = null;

    private RuntimeExceptionDao<CIFlightStationBookTicketODEntity, Integer> m_dao
            = CIApplication.getDbManager().getRuntimeExceptionDao(CIFlightStationBookTicketODEntity.class);

    private RuntimeExceptionDao<CIFlightStationEntity, Integer> m_daoStation
            = CIApplication.getDbManager().getRuntimeExceptionDao(CIFlightStationEntity.class);
    private static Object   s_lock = new Object();

    private static ConnectionSource m_connectionSource = CIApplication.getDbManager().getConnectionSource();

    private final String SOURCE                         = "BOOKTICKET";

    private final String DEF_WS_FORMAT_DATE             = "yyyy-MM-dd HH:mm:ss";

    private CIModelInfo modelInfo                       = null;


    @Override
    public String getAPINAME() {
        return API_NAME;
    }

    public CIInquiryFlightBookTicketODListModel(StationCallBack callback){
        m_callback = callback;
        modelInfo = new CIModelInfo(CIApplication.getContext());
    }

    public void getFromWS(){

        try {
            m_jsBody.put( eParaTag.version.name(), WSConfig.DEF_API_VERSION);
            m_jsBody.put( eParaTag.source.name(), SOURCE);
            m_jsBody.put( eParaTag.last_update_date.name(), modelInfo.GetBookTicketODLastUpdateDate());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.DoConnection();
    }

    @Override
    protected void DecodeResponse_Success(CIWSResult respBody, String code) {

        CIFlightStationBookTicketODList List = new CIFlightStationBookTicketODList();
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
            SLog.e("insert(List) "+List.size());
            if(false == insert(List)){
                DecodeResponse_Error("", SQLException.class.getSimpleName(), null);
                return;
            }

            //成功刷新資料庫才能刷新更新記錄
            String date = convertDateFormat(DEF_WS_FORMAT_DATE, Calendar.getInstance());
            modelInfo.SetBookTicketODLastUpdateDate(date);

            if ( null != m_callback ){
                m_callback.onStationSuccess(respBody.rt_code, respBody.rt_msg, null);
            }
        }
    }

    private CIFlightStationBookTicketODList paserJsonData(JSONArray jsArrayList) throws Exception{
        CIFlightStationBookTicketODList list = new CIFlightStationBookTicketODList();
        int ilength = jsArrayList.length();
        for ( int iIdx=0; iIdx < ilength; iIdx++ ){
            JSONObject jsObj = jsArrayList.getJSONObject(iIdx);
            if ( null != jsObj ){
                String strDepartureIATA = jsObj.getString(eRespParaTag.departure_iata.name());
                String strDepartureIATA_isOriginal = jsObj.getString(eRespParaTag.isOriginal.name());
                SLog.e("data.isOriginal: "+strDepartureIATA +" "+ strDepartureIATA_isOriginal);
                JSONArray jsarArrival = jsObj.getJSONArray(eRespParaTag.arrival.name());

                //依序依照國家將出發地點放入map
                int iArrlength = jsarArrival.length();
                for ( int iJdx = 0; iJdx<iArrlength; iJdx++ ){
                    String strArrivalIATA = jsarArrival.getJSONObject(iJdx).getString(eRespParaTag.IATA.name());

                    CIFlightStationBookTicketODEntity data = new CIFlightStationBookTicketODEntity();

                    data.isOriginal = strDepartureIATA_isOriginal;
                    data.departure_iata = strDepartureIATA;
                    data.arrival_iata = strArrivalIATA;
                    //對應出發地, 依照國家將出發地點放入map
                    list.add(data);

                }
            }
        }
        return list;
    }



    @Override
    protected void DecodeResponse_Error(String code, String strMag, Exception exception) {

        if ( WSConfig.WS_TESTMODE ){
            DecodeResponse_Success( ResultCodeCheck(getJsonFile(WSConfig.InquiryBookTicketOD)) ,"");
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
            TableUtils.clearTable(m_connectionSource, CIFlightStationBookTicketODEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initLastUpdateDate(){
        modelInfo.SetBookTicketODLastUpdateDate(CIModelInfo.DEF_LAST_UPDATE_DATE);
    }

    private void CreateTable(){
        try {
            TableUtils.createTable(m_connectionSource, CIFlightStationBookTicketODEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private boolean insert(ArrayList<CIFlightStationBookTicketODEntity> datas ) {
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
                                                                               String key,
                                                                               boolean isFindByOnlyIATA){
        List<CIFlightStationEntity> dataList = null;
        try {
            QueryBuilder<CIFlightStationBookTicketODEntity,Integer> ODBuilder = m_dao.queryBuilder();
            ODBuilder.where().eq("departure_iata",strStationCode);
            QueryBuilder<CIFlightStationEntity,Integer> stationBuilder = m_daoStation.queryBuilder();
            if(!TextUtils.isEmpty(key)){
                if(false == isFindByOnlyIATA){
                    stationBuilder.where().like("IATA", "%"+key+"%")
                            //2016-11-01 Ryan, 與iOS統一搜尋邏輯
//                            .or().like("airport_name", "%"+key+"%");
                            .or().like("city", "%"+key+"%")
                            .or().like("localization_name", "%"+key+"%");
                } else {
                    stationBuilder.where().eq("IATA", key);
                }
            }
            dataList = stationBuilder.join("IATA","arrival_iata",ODBuilder).query();
//            SLog.d(API_NAME, "sql=> " + stationBuilder.prepareStatementString());
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
        return getDepartureStationByKeyword(null, false, false);
    }

    public List<CIFlightStationEntity> getDepartureStation_ISORIGINAL_Y(){
        return getDepartureStationByKeyword(null, false, true);
    }

    /**取得出發地*/
    public List<CIFlightStationEntity> getDepartureStationByKeyword(String key,
                                                                    boolean isFindByOnlyIATA, boolean isOriginalY){
        List<CIFlightStationEntity> dataList = null;

        try {
            QueryBuilder<CIFlightStationBookTicketODEntity,Integer> ODBuilder = m_dao.queryBuilder();
            QueryBuilder<CIFlightStationEntity,Integer> stationBuilder = m_daoStation.queryBuilder();
            SLog.e(API_NAME, "sql key=> " + key);
            if(isOriginalY){
                ODBuilder.groupBy("departure_iata")
                        .where().eq("isOriginal", 'Y');
            }else{
                ODBuilder.groupBy("departure_iata");
            }

            if(!TextUtils.isEmpty(key)) {
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
            SLog.e(API_NAME, "sql=> " + stationBuilder.prepareStatementString());
//            Log.e(API_NAME, "initdata " + GsonTool.getGson().toJson(dataList));
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



}
