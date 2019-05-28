package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIInquiryPNRItineraryStatusModel;
import ci.ws.Models.entities.CIPNRItineraryStatusResp;
import ci.ws.Presenter.Listener.CIInquiryPNRItineraryStatusListener;

/**
 * InquiryPNRItineraryStatus WS 的 Presenter
 * 使用PNR ID與Seq Number查詢該航段的online check in status code
 * Created by jlchen on 16/6/13.
 */
public class CIInquiryPNRItineraryStatusPresenter {

    private static Handler                              s_hdUIThreadhandler = null;
    private static CIInquiryPNRItineraryStatusPresenter s_Instance          = null;

    private CIInquiryPNRItineraryStatusListener         m_Listener          = null;
    private CIInquiryPNRItineraryStatusModel            m_Model             = null;

    public static CIInquiryPNRItineraryStatusPresenter getInstance(
            CIInquiryPNRItineraryStatusListener listener ){

        if ( null == s_Instance ){
            s_Instance = new CIInquiryPNRItineraryStatusPresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallBack(listener);
        return s_Instance;
    }

    private void setCallBack( CIInquiryPNRItineraryStatusListener listener ){
        m_Listener = listener;
    }

    /**
     * InquiryPNRItineraryStatus(查詢PNR航班狀態)
     * 功能說明: 使用PNR+航段編號 查詢PNR航班狀態
     * 對應1A API : PNR_RETRIEVE
     */
    public void InquiryPNRItineraryStatusFromWS(String strPnrId, String strSeqNo){

        if ( null == m_Model ){
            m_Model = new CIInquiryPNRItineraryStatusModel(m_ItineraryStatusCallBack);
        }
        m_Model.sendReqDataToWS(strPnrId, strSeqNo);
        m_Model.DoConnection();
        if ( null != m_Listener ){
            m_Listener.showProgress();
        }
    }

    /**取消request*/
    public void InquiryPNRItineraryStatusCancel(){
        if ( null != m_Model ){
            m_Model.CancelRequest();
        }
        if ( null != m_Listener ){
            m_Listener.hideProgress();
        }
    }

    CIInquiryPNRItineraryStatusModel.CallBack m_ItineraryStatusCallBack
            = new CIInquiryPNRItineraryStatusModel.CallBack() {
        @Override
        public void onSuccess(final String rt_code,
                              final String rt_msg,
                              final CIPNRItineraryStatusResp resp) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryPNRItineraryStatusSuccess(rt_code, rt_msg, resp);
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryPNRItineraryStatusError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };
}
