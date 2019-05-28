package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIInquiryExcessBaggageInfoModel;
import ci.ws.Models.entities.CIInquiryExcessBaggageInfoReq;
import ci.ws.Models.entities.CIInquiryExcessBaggageInfoResp;
import ci.ws.Presenter.Listener.CIInquiryExcessBaggageInfoListener;

/**
 * Created by Kevin Cheng on 17/9/14.
 * 查詢加購行李價格。
 */
public class CIInquiryExcessBaggageInfoPresenter {

    private static CIInquiryExcessBaggageInfoPresenter m_Instance = null;

    private CIInquiryExcessBaggageInfoModel m_model = null;

    private CIInquiryExcessBaggageInfoListener m_Listener = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIInquiryExcessBaggageInfoPresenter getInstance(CIInquiryExcessBaggageInfoListener listener){

        if ( null == m_Instance ){
            m_Instance = new CIInquiryExcessBaggageInfoPresenter(listener);
        }

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallbackListener(listener);

        return m_Instance;
    }

    public static CIInquiryExcessBaggageInfoPresenter getInstance(){
        if(null == m_Instance){
            return getInstance(null);
        } else {
            return m_Instance;
        }
    }

    private void setCallbackListener( CIInquiryExcessBaggageInfoListener listener ){
        this.m_Listener = listener;
    }

    CIInquiryExcessBaggageInfoPresenter(CIInquiryExcessBaggageInfoListener listener){
        m_Listener = listener;
    }


    public void getInfo(CIInquiryExcessBaggageInfoReq req){

        if ( null == m_model){
            m_model = new CIInquiryExcessBaggageInfoModel(m_callback);
        }
        if(null != m_Listener){
            m_Listener.showProgress();
        }
        m_model.getInfoFrowWS(req);
    }

    public void interrupt(){
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_model){
            m_model.CancelRequest();
        }
    }


    CIInquiryExcessBaggageInfoModel.InquiryCallback m_callback = new CIInquiryExcessBaggageInfoModel.InquiryCallback() {

        @Override
        public void onSuccess(final String rt_code, final String rt_msg, final CIInquiryExcessBaggageInfoResp data) {

                s_hdUIThreadhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if ( null != m_Listener ) {
                            m_Listener.onSuccess(rt_code, rt_msg, data);
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
                        m_Listener.onError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };


}
