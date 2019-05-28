package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIInquiryMilesProgressModel;
import ci.ws.Models.entities.CIMilesProgressEntity;
import ci.ws.Presenter.Listener.CIInquiryMilesProgressListener;
import ci.ws.define.CIWSResultCode;

/**
 * Created by ryan on 16/5/6.
 * 3.1.	InquiryMilesProgress(取得累積哩程記錄)
 * 功能說明: 傳入會員卡號，取得累積哩程記錄
 * 對應CI API : QueryExpiringMileage
 */
public class CIInquiryMilesProgressPresenter {

    private static CIInquiryMilesProgressPresenter m_Instance = null;

    private CIInquiryMilesProgressListener m_Listener = null;

    private CIInquiryMilesProgressModel m_MilesModel = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIInquiryMilesProgressPresenter getInstance( CIInquiryMilesProgressListener listener ){

        if ( null == m_Instance ){
            m_Instance = new CIInquiryMilesProgressPresenter(listener);
        }

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallbackListener(listener);

        return m_Instance;
    }

    public CIInquiryMilesProgressPresenter( CIInquiryMilesProgressListener listener ){
        this.m_Listener = listener;
    }

    private void setCallbackListener( CIInquiryMilesProgressListener listener ){
        this.m_Listener = listener;
    }


    /**傳入會員卡號，取得累積哩程記錄
     * @param strCardNo 華夏會員卡號*/
    public void InquiryMilesProgressFromWS( String strCardNo ){

        if ( null == m_MilesModel ){
            m_MilesModel = new CIInquiryMilesProgressModel(m_Modelcallback);
        }

        m_MilesModel.InquiryMilesProgressFromWS(strCardNo);
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消WS*/
    public void interrupt(){
        if ( null != m_MilesModel ){
            m_MilesModel.CancelRequest();
        }
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    CIInquiryMilesProgressModel.MilesProgressCallBack m_Modelcallback = new CIInquiryMilesProgressModel.MilesProgressCallBack(){

        @Override
        public void onStationSuccess(final String rt_code, final String rt_msg, final CIMilesProgressEntity miles) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onStationSuccess(rt_code, rt_msg, miles);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onStationError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {

                    if ( null != m_Listener ) {
                        switch (rt_code) {
                            case CIWSResultCode.ERROR_LOGOUT_AUTOMATICALLY:
                            case CIWSResultCode.ERROR_AUTHORIZATION_FAILED:
                                m_Listener.onAuthorizationFailedError(rt_code, rt_msg);
                                break;
                            default:
                                m_Listener.onStationError(rt_code, rt_msg);
                                break;
                        }
                    }
                }
            });

        }
    };
}
