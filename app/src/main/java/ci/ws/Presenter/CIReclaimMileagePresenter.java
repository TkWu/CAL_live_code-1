package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIReclaimMileageModel;
import ci.ws.Models.entities.CIReclaimErrorResp;
import ci.ws.Models.entities.CIReclaimMileageReq;
import ci.ws.Presenter.Listener.CIReclaimMileageListener;

/**
 * Reclaim Mileage 哩程補登 的 Presenter
 * Created by Kevin Cheng on 16/5/16.
 */
public class CIReclaimMileagePresenter {

    private static CIReclaimMileagePresenter s_Instance = null;
    private CIReclaimMileageListener m_Listener = null;

    private CIReclaimMileageModel m_InquiryMileage = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIReclaimMileagePresenter getInstance(CIReclaimMileageListener listener ){

        if ( null == s_Instance){
            s_Instance = new CIReclaimMileagePresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallBack(listener);
        return s_Instance;
    }

    private void setCallBack( CIReclaimMileageListener listener ){
        m_Listener = listener;
    }

    /**
     * 哩程補登
     */
    public void ReclaimMileageFromWS(CIReclaimMileageReq reqData){

        if ( null == m_InquiryMileage){
            m_InquiryMileage = new CIReclaimMileageModel(m_CallBack);
        }

        m_InquiryMileage.setupRquest(reqData);

        m_InquiryMileage.DoConnection();
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    CIReclaimMileageModel.CallBack m_CallBack = new CIReclaimMileageModel.CallBack() {
        @Override
        public void onSuccess(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onReclaimMileageSuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onSuccess007(final String rt_code, final String rt_msg, final CIReclaimErrorResp re_data) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onReclaimMileageSuccess007(rt_code, rt_msg, re_data);
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
                        m_Listener.onReclaimMileageError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }

    };
}
