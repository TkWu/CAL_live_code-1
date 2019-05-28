package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIEBPaymentModel;
import ci.ws.Models.entities.CIEBPaymentResp;
import ci.ws.Models.entities.CIEBPaymentReq;
import ci.ws.Presenter.Listener.CIEBPaymentListener;

/**
 * Created by Kevin Cheng on 17/9/14.
 * 加購行李付款。
 */
public class CIEBPaymentPresenter {

    private static CIEBPaymentPresenter m_Instance = null;

    private CIEBPaymentModel m_model = null;

    private CIEBPaymentListener m_Listener = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIEBPaymentPresenter getInstance(CIEBPaymentListener listener){

        if ( null == m_Instance ){
            m_Instance = new CIEBPaymentPresenter(listener);
        }

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallbackListener(listener);

        return m_Instance;
    }

    public static CIEBPaymentPresenter getInstance(){
        if(null == m_Instance){
            return getInstance(null);
        } else {
            return m_Instance;
        }
    }

    private void setCallbackListener( CIEBPaymentListener listener ){
        this.m_Listener = listener;
    }

    CIEBPaymentPresenter(CIEBPaymentListener listener){
        m_Listener = listener;
    }


    public void getInfo(CIEBPaymentReq req){

        if ( null == m_model){
            m_model = new CIEBPaymentModel(m_callback);
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


    CIEBPaymentModel.InquiryCallback m_callback = new CIEBPaymentModel.InquiryCallback() {

        @Override
        public void onSuccess(final String rt_code, final String rt_msg, final CIEBPaymentResp data) {

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
