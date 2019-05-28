package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIInquiryMileageRecordModel;
import ci.ws.Models.CIInquiryTripModel;
import ci.ws.Models.entities.CIMileageRecordResp;
import ci.ws.Models.entities.CIMileageReq;
import ci.ws.Models.entities.CITripListResp;
import ci.ws.Models.entities.CITripbyCardReq;
import ci.ws.Models.entities.CITripbyPNRReq;
import ci.ws.Models.entities.CITripbyTicketReq;
import ci.ws.Presenter.Listener.CIFindMyBookingForRecordListener;
import ci.ws.Presenter.Listener.CIFindMyBookingListener;
import ci.ws.define.CIWSResultCode;

/**
 * Created by Ryan on 16/4/23.
 * Find My Booking(查詢行程)
 * 適用於My Trip, Find My Booking
 */
public class CIFindMyBookingPresenter {


    private static CIFindMyBookingPresenter   m_Instance                = null;
    private CIFindMyBookingListener           m_Listener                = null;

    private CIInquiryTripModel              m_InquiryTrip               = null;
    private CIInquiryMileageRecordModel     m_InquiryMileageRecordModel = null;
    private static Handler                  s_hdUIThreadhandler         = null;

    public static CIFindMyBookingPresenter getInstance( CIFindMyBookingListener listener ){

        if ( null == m_Instance ){
            m_Instance = new CIFindMyBookingPresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallBack(listener);
        return m_Instance;
    }

    private void setCallBack( CIFindMyBookingListener listener ){
        m_Listener = listener;
    }

    /**
     * InquiryTripByCard(查詢行程使用卡號)
     * 功能說明: 使用卡號取得行程
     * 對應1A API : PNR_SearchByFrequentFlyer
     */
    public void InquiryTripByCardFromWS( CITripbyCardReq tripReq ){

        if ( null == m_InquiryTrip){
            m_InquiryTrip = new CIInquiryTripModel(m_tripcallback);
        }
        m_InquiryTrip.InquiryTripByCardNo(tripReq);
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * InquiryMileageRecord(查詢里程明細)
     * 功能說明: 使用卡號查詢，主要拿來當作歷史行程的顯示
     * 對應1A API : QueryMileageRecord
     */
    public void InquiryMileageRecordFromWS(){

        if ( null == m_InquiryMileageRecordModel){
            m_InquiryMileageRecordModel
                    = new CIInquiryMileageRecordModel(m_MileageRecordCallBack);
        }

        m_InquiryMileageRecordModel.setMileageReq(new CIMileageReq());

        m_InquiryMileageRecordModel.DoConnection();
        if ( null != m_Listener){
            m_Listener.showProgress();
        }
    }


    /**
     * InquiryTripByPNR(查詢行程使用訂位代號)
     * 功能說明: 使用PNR、First Name及Last Name取得行程
     * 對應1A API : PNR_Retrieve
     */
    public void InquiryTripByPNRFromWS( CITripbyPNRReq tripReq ){

        if ( null == m_InquiryTrip){
            m_InquiryTrip = new CIInquiryTripModel(m_tripcallback);
        }
        m_InquiryTrip.InquiryTripByPNR(tripReq);
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * 3.1.1.	InquiryTripByTicket(查詢行程使用機票)
     * 功能說明: 使用機票號碼、First Name及Last Name取得行程
     * 對應1A API : Ticket_ProcessEDoc
     * PNR_Retrieve
     */
    public void InquiryTripByTicketFromWS( CITripbyTicketReq tripReq ){

        if ( null == m_InquiryTrip){
            m_InquiryTrip = new CIInquiryTripModel(m_tripcallback);
        }
        m_InquiryTrip.InquiryTripByTicket(tripReq);
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消equest*/
    public void InquiryTripCancel(){
        if ( null != m_InquiryTrip){
            m_InquiryTrip.CancelRequest();
        }
        if ( null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    CIInquiryTripModel.InquiryTripsCallBack m_tripcallback = new CIInquiryTripModel.InquiryTripsCallBack() {
        @Override
        public void onInquiryTripsSuccess(final String rt_code, final String rt_msg, final CITripListResp Tripslist) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryTripsSuccess(rt_code, rt_msg, Tripslist);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryTripsError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryTripsError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIInquiryMileageRecordModel.InquiryMileageRecordCallBack m_MileageRecordCallBack = new CIInquiryMileageRecordModel.InquiryMileageRecordCallBack() {
        @Override
        public void onInquiryMileageRecordSuccess(final String rt_code, final String rt_msg, final CIMileageRecordResp mileageRecordResp) {
            final CIFindMyBookingForRecordListener listener = (CIFindMyBookingForRecordListener)m_Listener;
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != listener ) {
                        listener.onInquiryMileageRecordSuccess(
                                rt_code,
                                rt_msg,
                                mileageRecordResp);
                        listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryMileageRecordError(final String rt_code, final String rt_msg) {
            final CIFindMyBookingForRecordListener listener = (CIFindMyBookingForRecordListener)m_Listener;
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        switch (rt_code) {
                            case CIWSResultCode.ERROR_LOGOUT_AUTOMATICALLY:
                            case CIWSResultCode.ERROR_AUTHORIZATION_FAILED:
                                listener.onAuthorizationFailedError(rt_code, rt_msg);
                                break;
                            default:
                                listener.onInquiryMileageRecordError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };
}
