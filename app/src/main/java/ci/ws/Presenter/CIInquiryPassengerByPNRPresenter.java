package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIInquiryAllPassengerByPNRModel;
import ci.ws.Models.entities.CIPassengerByPNRReq;
import ci.ws.Models.entities.CIPassengerListResp;
import ci.ws.Presenter.Listener.CIInquiryPassengerByPNRListener;

/**
 * trip detail-passenger 行程資訊by乘客tab 的 Presenter
 * Created by jlchen on 16/5/11.
 */
public class CIInquiryPassengerByPNRPresenter {

    private static Handler                          s_hdUIThreadhandler     = null;
    private static CIInquiryPassengerByPNRPresenter s_Instance              = null;
    private        CIInquiryPassengerByPNRListener  m_Listener              = null;
    private        CIInquiryAllPassengerByPNRModel  m_InquiryPassengerByPNR = null;

    public static CIInquiryPassengerByPNRPresenter getInstance(CIInquiryPassengerByPNRListener listener ){
        if ( null == s_Instance){
            s_Instance = new CIInquiryPassengerByPNRPresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallBack(listener);
        return s_Instance;
    }

    private void setCallBack( CIInquiryPassengerByPNRListener listener ){
        m_Listener = listener;
    }

    /**
     * InquiryAllPassengerByPNR(查詢行程的所有乘客資訊)
     * 功能說明: 使用my trip行程資訊 查詢乘客
     * 對應1A API : PNR_RETRIEVE
     */
    public void InquiryAllPassengerByPNRFromWS(CIPassengerByPNRReq passengerReq ){
        if ( null != m_Listener ){
            m_Listener.showProgress();
        }

        if ( null == m_InquiryPassengerByPNR){
            m_InquiryPassengerByPNR = new CIInquiryAllPassengerByPNRModel(m_PassengerCallBack);
        }
        m_InquiryPassengerByPNR.setAllPassengerReq(passengerReq);
        m_InquiryPassengerByPNR.DoConnection();
    }

    /**取消request*/
    public void InquiryAllPassengerByPNRCancel(){
        if ( null != m_InquiryPassengerByPNR){
            m_InquiryPassengerByPNR.CancelRequest();
        }
        if ( null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    CIInquiryAllPassengerByPNRModel.InquiryPassengerByPNRCallBack m_PassengerCallBack = new CIInquiryAllPassengerByPNRModel.InquiryPassengerByPNRCallBack() {
        @Override
        public void onInquiryPassengerByPNRSuccess(final String rt_code, final String rt_msg, final CIPassengerListResp PassengerList) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryPassengerByPNRSuccess(rt_code, rt_msg, PassengerList);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryPassengerByPNRError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryPassengerByPNRError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };
}
