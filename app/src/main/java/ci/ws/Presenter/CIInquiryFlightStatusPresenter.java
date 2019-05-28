package ci.ws.Presenter;


import android.os.Handler;
import android.os.Looper;

import ci.function.Core.CIApplication;
import ci.ws.Models.CIInquiryFlightStatusModel;
import ci.ws.Models.entities.CIFlightStatusReq;
import ci.ws.Models.entities.CIFlightStatusResp;
import ci.ws.Presenter.Listener.CIInquiryFlightStatusListener;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/4/27.
 * 畫面：FlightStatus Zeplin 17.2-2
 * 功能說明: 依查詢方式by route或by flight no.查詢出航班狀態
 * 對應CI API : GetFlightInfo
 */
public class CIInquiryFlightStatusPresenter {

    private CIInquiryFlightStatusModel m_flightModel = null;
    private CIInquiryFlightStatusListener m_Listener = null;
    private static Handler s_hdUIThreadhandler = null;

    public CIInquiryFlightStatusPresenter( CIInquiryFlightStatusListener listener ){
        this.m_Listener = listener;

        if(null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

    }

    /**取得FlightStstus的WS
     * @param req 輸入的參數, */
    public void InquiryFlightStatusFromWS( CIFlightStatusReq req ){

        if ( null == m_flightModel ){
            m_flightModel = new CIInquiryFlightStatusModel(m_callback);
        }

        m_flightModel.InquiryFlightStatusFromWS(req, CIApplication.getLanguageInfo().getWSLanguage(), WSConfig.DEF_API_VERSION);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }


    /**取消FlightStstus的WS*/
    public void InquiryFlightStatusCancel(){
        if ( null != m_flightModel ){
            m_flightModel.CancelRequest();
        }
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    CIInquiryFlightStatusModel.FlightStatusCallBack m_callback = new CIInquiryFlightStatusModel.FlightStatusCallBack() {
        @Override
        public void onFlightStatusSuccess(final String rt_code, final String rt_msg, final CIFlightStatusResp flightStatusResp) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onFlightStatusSuccess(rt_code, rt_msg, flightStatusResp);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onFlightStatusError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onFlightStatusError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };
}
