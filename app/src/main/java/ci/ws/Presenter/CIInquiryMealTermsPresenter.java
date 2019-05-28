package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIInquiryMealTermsModel;
import ci.ws.Presenter.Listener.CIInquiryMealTermsListener;

/**
 * Created by Ryan on 16/5/16.
 * 3.1.	InquiryMealTerms
 * 功能說明: 取得使用規範。
 * 對應CI API : GetUseTerms
 */
public class CIInquiryMealTermsPresenter {

    private static CIInquiryMealTermsPresenter m_Instance = null;

    private CIInquiryMealTermsListener m_Listener   = null;

    private CIInquiryMealTermsModel m_Model         = null;

    private static Handler s_hdUIThreadhandler      = null;

    public static CIInquiryMealTermsPresenter getInstance( CIInquiryMealTermsListener Listener ){

        if ( null == m_Instance ){
            m_Instance = new CIInquiryMealTermsPresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallBack(Listener);
        return m_Instance;
    }

    public void setCallBack( CIInquiryMealTermsListener Listener ){
        this.m_Listener = Listener;
    }

    /**查詢餐點須知*/
    public void InquiryMealTermsFromWS(){

        if ( null == m_Model ){
            m_Model = new CIInquiryMealTermsModel(m_ModelCallback);
        }
        m_Model.InquiryMealTerms();
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消WS*/
    public void interruptWS(){

        if ( null != m_Model ){
            m_Model.CancelRequest();
        }
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    CIInquiryMealTermsModel.InquiryMealTermsCallBack m_ModelCallback = new CIInquiryMealTermsModel.InquiryMealTermsCallBack() {
        @Override
        public void onInquiryMealTermsSuccess(final String rt_code, final String rt_msg, final String strMealteams) {
            if ( null != m_Listener ){
                s_hdUIThreadhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        m_Listener.onInquiryMealTermsSuccess(rt_code, rt_msg, strMealteams);
                        m_Listener.hideProgress();
                    }
                });
            }
        }

        @Override
        public void onInquiryMealTermsError(final String rt_code, final String rt_msg) {
            if ( null != m_Listener ){
                s_hdUIThreadhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        m_Listener.onInquiryMealTermsError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                });
            }
        }
    };
}
