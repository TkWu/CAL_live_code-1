package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CICCPageModel;
import ci.ws.Models.entities.CICCPageResp;
import ci.ws.Presenter.Listener.CICCPageListener;

/**
 * Created by Kevin Cheng on 17/9/14.
 * 信用卡付款頁。
 */
public class CICCPagePresenter {

    private static CICCPagePresenter m_Instance = null;

    private CICCPageModel m_model = null;

    private CICCPageListener m_Listener = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CICCPagePresenter getInstance(CICCPageListener listener){

        if ( null == m_Instance ){
            m_Instance = new CICCPagePresenter(listener);
        }

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallbackListener(listener);

        return m_Instance;
    }

    public static CICCPagePresenter getInstance(){
        if(null == m_Instance){
            return getInstance(null);
        } else {
            return m_Instance;
        }
    }

    private void setCallbackListener( CICCPageListener listener ){
        this.m_Listener = listener;
    }

    CICCPagePresenter(CICCPageListener listener){
        m_Listener = listener;
    }


    public void getInfo(){

        if ( null == m_model){
            m_model = new CICCPageModel(m_callback);
        }
        if(null != m_Listener){
            m_Listener.showProgress();
        }
        m_model.getInfoFrowWS();
    }

    public void interrupt(){
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_model){
            m_model.CancelRequest();
        }
    }


    CICCPageModel.InquiryCallback m_callback = new CICCPageModel.InquiryCallback() {

        @Override
        public void onSuccess(final String rt_code, final String rt_msg, final CICCPageResp data) {

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
