package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIInquiryEBBasicInfoModel;
import ci.ws.Models.entities.CIInquiryEBBasicInfoReq;
import ci.ws.Models.entities.CIInquiryEBBasicInfoResp;
import ci.ws.Presenter.Listener.CIInquiryEBBasicInfoListener;

/**
 * Created by Kevin Cheng on 17/9/14.
 * 加購行李-查詢旅客名單
 */
public class CIInquiryEBBasicInfoPresenter {

    private static CIInquiryEBBasicInfoPresenter m_Instance = null;

    private CIInquiryEBBasicInfoModel m_model = null;

    private CIInquiryEBBasicInfoListener m_Listener = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIInquiryEBBasicInfoPresenter getInstance(CIInquiryEBBasicInfoListener listener){

        if ( null == m_Instance ){
            m_Instance = new CIInquiryEBBasicInfoPresenter(listener);
        }

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallbackListener(listener);

        return m_Instance;
    }

    public static CIInquiryEBBasicInfoPresenter getInstance(){
        if(null == m_Instance){
            return getInstance(null);
        } else {
            return m_Instance;
        }
    }

    private void setCallbackListener( CIInquiryEBBasicInfoListener listener ){
        this.m_Listener = listener;
    }

    CIInquiryEBBasicInfoPresenter(CIInquiryEBBasicInfoListener listener){
        m_Listener = listener;
    }


    public void getInfo(CIInquiryEBBasicInfoReq req){

        if ( null == m_model){
            m_model = new CIInquiryEBBasicInfoModel(m_callback);
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


    CIInquiryEBBasicInfoModel.InquiryCallback m_callback = new CIInquiryEBBasicInfoModel.InquiryCallback() {

        @Override
        public void onSuccess(final String rt_code, final String rt_msg, final CIInquiryEBBasicInfoResp data) {

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
