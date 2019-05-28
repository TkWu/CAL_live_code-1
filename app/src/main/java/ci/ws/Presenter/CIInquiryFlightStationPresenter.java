package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ci.ws.Models.CIInquiryFlightBookTicketODListModel;
import ci.ws.Models.CIInquiryFlightStatusODModel;
import ci.ws.Models.CIInquiryFlightTimeTableODListModel;
import ci.ws.Models.CIInquiryStationListModel;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Presenter.Listener.CIInquiryFlightStatusStationListener;

/**
 * Created by Ryan on 16/5/5.
 * 功能說明: 取得所有航站出發航站對應的目的航站清單資訊。
 */
public class CIInquiryFlightStationPresenter {

    public enum ESource {
        FlightStatus, BookTicket, TimeTable
    }
    //哩程補登同 FlightStatus
    private CIInquiryFlightStatusStationListener m_Listener = null;

    private CIInquiryFlightStatusODModel m_StatusODModel = null;

    private CIInquiryFlightBookTicketODListModel m_BookTicketODModel = null;

    private CIInquiryFlightTimeTableODListModel m_TimeTableODModel = null;

    private CIInquiryStationListModel m_StationListModel = null;

    private static Handler s_hdUIThreadhandler = null;

    private ESource m_eESource = ESource.FlightStatus;

    public static CIInquiryFlightStationPresenter getInstance(CIInquiryFlightStatusStationListener listener,
                                                              ESource eSource){

        CIInquiryFlightStationPresenter m_Instance = new CIInquiryFlightStationPresenter(listener,eSource);

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        return m_Instance;
    }

    public CIInquiryFlightStationPresenter(CIInquiryFlightStatusStationListener listener,
                                           ESource eESource){
        this.m_Listener = listener;
        this.m_eESource = eESource;
    }

    /**透過WS取得總站資訊*/
    public void InquiryAllStationListFromWS(){
        s_hdUIThreadhandler.post(new Runnable() {
            @Override
            public void run() {
                if( null != m_Listener ){
                    m_Listener.showProgress();
                }
            }
        });

        if ( null == m_StationListModel ){
            m_StationListModel = new CIInquiryStationListModel(m_StaticListModelcallback);
        }

        m_StationListModel.getFromWS();

    }

    /**透過WS取得起訖站對應資料資訊*/
    public void InquiryStationODListFromWS(){

        s_hdUIThreadhandler.post(new Runnable() {
            @Override
            public void run() {
                if( null != m_Listener ){
                    m_Listener.showProgress();
                }
            }
        });

        switch (m_eESource){
            case FlightStatus:
                if(null == m_StatusODModel){
                    m_StatusODModel = new CIInquiryFlightStatusODModel(m_StatusModelcallback);
                }
                m_StatusODModel.getFromWS();
                break;
            case BookTicket:
                if(null == m_BookTicketODModel) {
                    m_BookTicketODModel = new CIInquiryFlightBookTicketODListModel(m_BookTicketModelcallback);
                }
                m_BookTicketODModel.getFromWS();
                break;
            case TimeTable:
                if(null == m_TimeTableODModel) {
                    m_TimeTableODModel = new CIInquiryFlightTimeTableODListModel(m_TimeTableModelcallback);
                }
                m_TimeTableODModel.getFromWS();
                break;
        }

    }

    /**取得依照Type整理過的所有出發地*/
    public List<CIFlightStationEntity> getDepatureStationList(){

        List<CIFlightStationEntity> StationDatas = null;
        switch (m_eESource){
            case FlightStatus:
                if(null == m_StatusODModel){
                    m_StatusODModel = new CIInquiryFlightStatusODModel(m_StatusModelcallback);
                }
                StationDatas = m_StatusODModel.getDepartureStation();
                break;
            case BookTicket:
                if(null == m_BookTicketODModel) {
                    m_BookTicketODModel = new CIInquiryFlightBookTicketODListModel(m_BookTicketModelcallback);
                }
                StationDatas = m_BookTicketODModel.getDepartureStation();
                break;
            case TimeTable:
                if(null == m_TimeTableODModel) {
                    m_TimeTableODModel = new CIInquiryFlightTimeTableODListModel(m_TimeTableModelcallback);
                }
                StationDatas = m_TimeTableODModel.getDepartureStation();
                break;

        }
        return StationDatas;
    }

    public List<CIFlightStationEntity> getDepatureStationListByIATA(String iata){
        return getDepatureStationListByKeyword(iata, true);
    }

    public List<CIFlightStationEntity> getDepatureStationListByKeyword(String key){
        return getDepatureStationListByKeyword(key, false);
    }

    /**取得依照Type整理過的所有出發地，並可帶入搜尋條件*/
    public List<CIFlightStationEntity> getDepatureStationListByKeyword(String key, boolean isFindByOnlyIATA){

        List<CIFlightStationEntity> StationDatas = null;
        switch (m_eESource){
            case FlightStatus:
                if(null == m_StatusODModel){
                    m_StatusODModel = new CIInquiryFlightStatusODModel(m_StatusModelcallback);
                }
                StationDatas = m_StatusODModel.getDepartureStationByKeyword(key, isFindByOnlyIATA);
                break;
            case BookTicket:
                if(null == m_BookTicketODModel) {
                    m_BookTicketODModel = new CIInquiryFlightBookTicketODListModel(m_BookTicketModelcallback);
                }
                StationDatas = m_BookTicketODModel.getDepartureStationByKeyword(key, isFindByOnlyIATA);
                break;
            case TimeTable:
                if(null == m_TimeTableODModel) {
                    m_TimeTableODModel = new CIInquiryFlightTimeTableODListModel(m_TimeTableModelcallback);
                }
                StationDatas = m_TimeTableODModel.getDepartureStationByKeyword(key, isFindByOnlyIATA);
                break;

        }
        return StationDatas;
    }

    /**取得依照國家整理過的所有出發地*/
    public List<CIFlightStationEntity> getAllDepatureStationList(){

        if ( null == m_StationListModel ){
            m_StationListModel = new CIInquiryStationListModel(m_StaticListModelcallback);
        }

        List<CIFlightStationEntity> datas = m_StationListModel.getAllStationList();

        return datas;
    }

    /**
     * 依據IATA機場代碼資料取得機場資訊
     * */
    public CIFlightStationEntity getStationInfoByIATA(String iata){

        if ( null == m_StationListModel ){
            m_StationListModel = new CIInquiryStationListModel(m_StaticListModelcallback);
        }

        return m_StationListModel.getStationInfoByIATA(iata);
    }

    /**取得依照國家整理過的所有出發地*/
    public HashMap<String, ArrayList<CIFlightStationEntity>> getAllDepatureStationMap(){

        if ( null == m_StationListModel ){
            m_StationListModel = new CIInquiryStationListModel(m_StaticListModelcallback);
        }

        HashMap<String, ArrayList<CIFlightStationEntity>> datas = m_StationListModel.getAllStationMap();

        return datas;
    }

    /**透過出發地代碼取得對應的抵達地*/
    public ArrayList<CIFlightStationEntity> getArrivalSrtationListByDeparture(String strStationCode){

        List<CIFlightStationEntity> arrivaStationDatas = null;
        switch (m_eESource){
            case FlightStatus:
                if(null == m_StatusODModel){
                    m_StatusODModel = new CIInquiryFlightStatusODModel(m_StatusModelcallback);
                }
                arrivaStationDatas = m_StatusODModel.getArrivalSrtationByDeparture(strStationCode);
                break;
            case BookTicket:
                if(null == m_BookTicketODModel) {
                    m_BookTicketODModel = new CIInquiryFlightBookTicketODListModel(m_BookTicketModelcallback);
                }
                arrivaStationDatas = m_BookTicketODModel.getArrivalSrtationByDeparture(strStationCode);
                break;
            case TimeTable:
                if(null == m_TimeTableODModel) {
                    m_TimeTableODModel = new CIInquiryFlightTimeTableODListModel(m_TimeTableModelcallback);
                }
                arrivaStationDatas = m_TimeTableODModel.getArrivalSrtationByDeparture(strStationCode);
                break;

        }

        return (ArrayList<CIFlightStationEntity>)arrivaStationDatas;
    }

    public ArrayList<CIFlightStationEntity> getArrivalSrtationListByIATA(String strStationCode,
                                                                              String iata){
        return getArrivalSrtationListByDeparture(strStationCode, iata, true);
    }

    public ArrayList<CIFlightStationEntity> getArrivalSrtationListByDeparture(String strStationCode,
                                                                              String key){
        return getArrivalSrtationListByDeparture(strStationCode, key, false);
    }

    /**透過出發地代碼取得對應的抵達地，並可帶入搜尋條件*/
    public ArrayList<CIFlightStationEntity> getArrivalSrtationListByDeparture(String strStationCode,
                                                                              String key,
                                                                              boolean isFindOnlyIATA){

        List<CIFlightStationEntity> arrivaStationDatas = null;
        switch (m_eESource){
            case FlightStatus:
                if(null == m_StatusODModel){
                    m_StatusODModel = new CIInquiryFlightStatusODModel(m_StatusModelcallback);
                }
                arrivaStationDatas = m_StatusODModel.getArrivalSrtationByDepartureAndKeyWord(strStationCode,key, isFindOnlyIATA);
                break;
            case BookTicket:
                if(null == m_BookTicketODModel) {
                    m_BookTicketODModel = new CIInquiryFlightBookTicketODListModel(m_BookTicketModelcallback);
                }
                arrivaStationDatas = m_BookTicketODModel.getArrivalSrtationByDepartureAndKeyWord(strStationCode, key, isFindOnlyIATA);
                break;
            case TimeTable:
                if(null == m_TimeTableODModel) {
                    m_TimeTableODModel = new CIInquiryFlightTimeTableODListModel(m_TimeTableModelcallback);
                }
                arrivaStationDatas = m_TimeTableODModel.getArrivalSrtationByDepartureAndKeyWord(strStationCode, key, isFindOnlyIATA);
                break;

        }

        return (ArrayList<CIFlightStationEntity>)arrivaStationDatas;
    }

    /**
     * 初始化總站ＤＢ及最後更新時間
     */
    public void initAllStationDB(){
        if ( null == m_StationListModel ){
            m_StationListModel = new CIInquiryStationListModel(m_StaticListModelcallback);
        }
        m_StationListModel.initLastUpdateDate();
        m_StationListModel.Clear();
    }
    /**
     * 根據Type初始化起訖站對應表ＤＢ及最後更新時間
     */
    public void initStationODDBForType(){
        switch (m_eESource){
            case FlightStatus:
                if(null == m_StatusODModel){
                    m_StatusODModel = new CIInquiryFlightStatusODModel(m_StatusModelcallback);
                }
                m_StatusODModel.initLastUpdateDate();
                m_StatusODModel.Clear();
                break;
            case BookTicket:
                if(null == m_BookTicketODModel) {
                    m_BookTicketODModel = new CIInquiryFlightBookTicketODListModel(m_BookTicketModelcallback);
                }
                m_BookTicketODModel.initLastUpdateDate();
                m_BookTicketODModel.Clear();
                break;
            case TimeTable:
                if(null == m_TimeTableODModel) {
                    m_TimeTableODModel = new CIInquiryFlightTimeTableODListModel(m_TimeTableModelcallback);
                }
                m_TimeTableODModel.initLastUpdateDate();
                m_TimeTableODModel.Clear();
                break;

        }
    }

    public void cancel(){
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_StationListModel ){
            m_StationListModel.CancelRequest();
        }
        if ( null != m_StatusODModel ){
            m_StatusODModel.CancelRequest();
        }
        if ( null != m_BookTicketODModel ){
            m_BookTicketODModel.CancelRequest();
        }
        if ( null != m_TimeTableODModel ){
            m_TimeTableODModel.CancelRequest();
        }
    }

    CIInquiryFlightStatusODModel.StationCallBack m_StatusModelcallback = new CIInquiryFlightStatusODModel.StationCallBack() {

        @Override
        public void onStationSuccess(final String rt_code,final String rt_msg, final ArrayList<CIFlightStationEntity> arStationList ) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onODStationSuccess(rt_code, rt_msg, CIInquiryFlightStationPresenter.this);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onStationError( final String rt_code, final String rt_msg ) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onStationError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIInquiryFlightBookTicketODListModel.StationCallBack m_BookTicketModelcallback = new CIInquiryFlightBookTicketODListModel.StationCallBack() {

        @Override
        public void onStationSuccess(final String rt_code,final String rt_msg, final ArrayList<CIFlightStationEntity> arStationList ) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onODStationSuccess(rt_code, rt_msg, CIInquiryFlightStationPresenter.this);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onStationError( final String rt_code, final String rt_msg ) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onStationError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIInquiryFlightTimeTableODListModel.StationCallBack m_TimeTableModelcallback = new CIInquiryFlightTimeTableODListModel.StationCallBack() {

        @Override
        public void onStationSuccess(final String rt_code,final String rt_msg, final ArrayList<CIFlightStationEntity> arStationList ) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onODStationSuccess(rt_code, rt_msg, CIInquiryFlightStationPresenter.this);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onStationError( final String rt_code, final String rt_msg ) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onStationError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };


    CIInquiryStationListModel.StationCallBack m_StaticListModelcallback = new CIInquiryStationListModel.StationCallBack() {

        @Override
        public void onStationSuccess(final String rt_code,final String rt_msg, final ArrayList<CIFlightStationEntity> arStationList ) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onAllStationSuccess(rt_code, rt_msg, CIInquiryFlightStationPresenter.this);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onStationError( final String rt_code, final String rt_msg ) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onStationError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };
}
