package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import ci.ws.Models.CIInquiryCheckInPaxByPNRModel;
import ci.ws.Models.entities.CICheckInPaxEntity;
import ci.ws.Presenter.Listener.CIInquiryCheckInPaxListener;

/**
 * Created by Ryan on 16/4/23.
 3.1.1.	CIInquiryCheckInPaxByPNRModel(查詢報到旅客使用PNR)
 * 功能說明: 使用 PNR/First Name/Last  Name取得PNR資料
 * 對應1A API : DCSIDC_CPRIdentification
 */
@Deprecated
public class CIInquiryCheckInPaxPresenter {

    private static CIInquiryCheckInPaxPresenter m_Instance = null;
    private CIInquiryCheckInPaxListener     m_Listener = null;

    private CIInquiryCheckInPaxByPNRModel   m_InquiryCheckInPaxModel = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIInquiryCheckInPaxPresenter getInstance( CIInquiryCheckInPaxListener listener ){

        if ( null == m_Instance ){
            m_Instance = new CIInquiryCheckInPaxPresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallBack(listener);
        return m_Instance;
    }

    private void setCallBack( CIInquiryCheckInPaxListener listener ){
        m_Listener = listener;
    }

    /**
     * 3.1.1.	CIInquiryCheckInPaxByPNRModel(查詢報到旅客使用PNR)
     * 功能說明: 使用 PNR/First Name/Last  Name取得PNR資料
     * */
    public void InquiryCheckInPaxByPNRFromWS( String strPnr_id, String First_Name, String Last_Name ){

        if ( null == m_InquiryCheckInPaxModel){
            m_InquiryCheckInPaxModel = new CIInquiryCheckInPaxByPNRModel();
        }
        m_InquiryCheckInPaxModel.setCallback(m_InquiryCheckInPaxCallback);
        m_InquiryCheckInPaxModel.InquiryCheckInPaxByPNR(strPnr_id, First_Name, Last_Name);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消equest*/
    public void InquiryCheckInPaxCancel(){
        if ( null != m_InquiryCheckInPaxModel){
            m_InquiryCheckInPaxModel.CancelRequest();
        }
        if ( null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    CIInquiryCheckInPaxByPNRModel.InquiryCheckInPaxCallBack m_InquiryCheckInPaxCallback = new CIInquiryCheckInPaxByPNRModel.InquiryCheckInPaxCallBack() {

         @Override
         public void onInquiryCheckInPaxSuccess(final String rt_code, final String rt_msg, final ArrayList<CICheckInPaxEntity> arPaxList) {

             s_hdUIThreadhandler.post(new Runnable() {
                 @Override
                 public void run() {
                     if ( null != m_Listener ) {
                         m_Listener.onInquiryCheckInPaxSuccess(rt_code, rt_msg, arPaxList);
                         m_Listener.hideProgress();
                     }
                 }
             });

         }

         @Override
         public void onInquiryCheckInPaxError(final String rt_code, final String rt_msg) {

             s_hdUIThreadhandler.post(new Runnable() {
                 @Override
                 public void run() {
                     if ( null != m_Listener ) {
                         m_Listener.onInquiryCheckInPaxError(rt_code, rt_msg);
                         m_Listener.hideProgress();
                     }
                 }
             });

         }
    };
}
