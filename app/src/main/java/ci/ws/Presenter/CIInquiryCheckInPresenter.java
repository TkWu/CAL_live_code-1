package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Set;

import ci.ws.Models.CIInquiryCheckInAllPaxByPNRModel;
import ci.ws.Models.CIInquiryCheckInModel;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Presenter.Listener.CIInquiryCheckInListener;

/**
 * Created by Ryan on 16/4/23.
 * 3.1.	Check In(預辦登機)
 * CPR
 */
public class CIInquiryCheckInPresenter {

    //private static CIInquiryCheckInPresenter m_Instance = null;
    private CIInquiryCheckInListener m_Listener = null;

    private CIInquiryCheckInModel           m_InquiryCheckIn = null;

    private CIInquiryCheckInAllPaxByPNRModel m_InquiryCheckInAllPax = null;


    private static Handler s_hdUIThreadhandler = null;

    //2016-07-15 ryan 調整Check-in Presenter 使用方式
//    public static CIInquiryCheckInPresenter getInstance( CIInquiryCheckInListener listener ){
//
//        if ( null == m_Instance ){
//            m_Instance = new CIInquiryCheckInPresenter();
//        }
//
//        if ( null == s_hdUIThreadhandler ){
//            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
//        }
//
//        m_Instance.setCallBack(listener);
//        return m_Instance;
//    }

    public CIInquiryCheckInPresenter( CIInquiryCheckInListener listener ){
        m_Listener = listener;

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }
    }

    public void setCallBack(CIInquiryCheckInListener listener ){
        m_Listener = listener;
    }

    /**
     * 3.1.1.	InquiryCheckInByCard(查詢欲報到行程使用卡號)
     * 功能說明: 查詢欲報到行程使用卡號
     * 對應1A API : DCSIDC_CPRIdentification
     *
     * 2018-06-29 第二階段CR 新增FirstName LastName, PNRList 避免PNRId重複使用導致看到別人的資料
     * */
    public void InquiryCheckInByCardFromWS( String strCardNo, String First_Name, String Last_Name, Set<String> pnrData ){

        if ( null == m_InquiryCheckIn){
            m_InquiryCheckIn = new CIInquiryCheckInModel();
        }
        m_InquiryCheckIn.setCallback(m_InquiryCheckIncallback);
        m_InquiryCheckIn.InquiryCheckInByCard(strCardNo, First_Name, Last_Name, pnrData);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }


    /**
     * 3.1.1.	InquiryCheckInByPNR (選擇航班_PNR)
     * 功能說明: 使用 PNR/First Name/Last  Name取得PNR資料
     * 對應1A API : DCSIDC_CPRIdentification
     * */
    public void InquiryCheckInByPNRFromWS( String strPnr_id, String First_Name, String Last_Name ){

        if ( null == m_InquiryCheckIn){
            m_InquiryCheckIn = new CIInquiryCheckInModel();
        }
        m_InquiryCheckIn.setCallback(m_InquiryCheckIncallback);
        m_InquiryCheckIn.InquiryCheckInByPNR(strPnr_id, First_Name, Last_Name);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * 3.1.1.	InquiryCheckInByTicket(選擇航班_電子機票)
     * 功能說明: 使用 Ticket/First Name/Last  Name取得PNR資料
     * 對應1A API : DCSIDC_CPRIdentification
     */
    public void InquiryCheckInByTicketFromWS( String strTicket, String First_Name, String Last_Name ){

        if ( null == m_InquiryCheckIn){
            m_InquiryCheckIn = new CIInquiryCheckInModel();
        }
        m_InquiryCheckIn.setCallback(m_InquiryCheckIncallback);
        m_InquiryCheckIn.InquiryCheckInByTicket(strTicket, First_Name, Last_Name);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * InquiryCheckInAllPaxByPNRFromWS
     * 功能說明: 拿取旅客列表
     */
        public void InquiryCheckInAllPaxByPNRFromWS(String strPNRid, ArrayList<String> arSegmentNumber) {

        if ( null == m_InquiryCheckInAllPax){
            m_InquiryCheckInAllPax = new CIInquiryCheckInAllPaxByPNRModel();
        }
        m_InquiryCheckInAllPax.setCallback(m_InquiryCheckInAllPaxcallback);
        m_InquiryCheckInAllPax.InquiryCheckInAllPaxByPNR(strPNRid, arSegmentNumber, "", "", "");

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * InquiryCheckInAllPaxByPNRFromWS
     * 功能說明: 新增旅客
     */
    public void InquiryCheckInAllPaxByPNRFromWS(String strPNRid, ArrayList<String> arSegmentNumber,String strOtherPNRid, String strFirstName, String strLastName) {
        if ( null == m_InquiryCheckInAllPax){
            m_InquiryCheckInAllPax = new CIInquiryCheckInAllPaxByPNRModel();
        }
        m_InquiryCheckInAllPax.setCallback(m_InquiryCheckInAllPaxcallback);
        m_InquiryCheckInAllPax.InquiryCheckInAllPaxByPNR(strPNRid, arSegmentNumber,strOtherPNRid,strFirstName,strLastName);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * InquiryCheckInAllPaxByPNRFromWS
     * 功能說明: 新增旅客
     */
    public void InquiryCheckInAllPaxByTicketFromWS(String strTicketNo, ArrayList<String> arSegmentNumber,String strOtherPNRid, String strFirstName, String strLastName) {
        if ( null == m_InquiryCheckInAllPax){
            m_InquiryCheckInAllPax = new CIInquiryCheckInAllPaxByPNRModel();
        }
        m_InquiryCheckInAllPax.setCallback(m_InquiryCheckInAllPaxcallback);
        m_InquiryCheckInAllPax.InquiryCheckInAllPaxByTicket(strTicketNo, arSegmentNumber,strOtherPNRid,strFirstName,strLastName);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消equest*/
    public void InquiryCheckInCancel(){
        if ( null != m_InquiryCheckIn){
            m_InquiryCheckIn.CancelRequest();
        }
        if ( null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    CIInquiryCheckInModel.InquiryCheckInCallBack m_InquiryCheckIncallback = new CIInquiryCheckInModel.InquiryCheckInCallBack() {
        @Override
        public void onInquiryCheckInSuccess(final String rt_code, final String rt_msg, final CICheckInAllPaxResp CheckInList) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryCheckInSuccess(rt_code, rt_msg, CheckInList);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryCheckInError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryCheckInError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };


    CIInquiryCheckInAllPaxByPNRModel.InquiryCheckInAllPaxByPNRCallBack m_InquiryCheckInAllPaxcallback = new CIInquiryCheckInAllPaxByPNRModel.InquiryCheckInAllPaxByPNRCallBack() {
        @Override
        public void onInquiryCheckInAllPaxSuccess(final String rt_code, final String rt_msg, final CICheckInAllPaxResp CheckInList) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryCheckInAllPaxSuccess(rt_code, rt_msg, CheckInList);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryCheckInAllPaxError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryCheckInAllPaxError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

}
