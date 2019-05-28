package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import ci.ws.Models.CICheckInModel;
import ci.ws.Models.CIInquiryBaggageInfoByBGNumModel;
import ci.ws.Models.CIInquiryBaggageInfoByPNRAndBGNumModel;
import ci.ws.Models.entities.CIBaggageInfoContentResp;
import ci.ws.Models.entities.CIBaggageInfoReq;
import ci.ws.Models.entities.CIBaggageInfoResp;
import ci.ws.Models.entities.CICheckIn_Resp;
import ci.ws.Presenter.Listener.CIBaggageInfoListener;
import ci.ws.Presenter.Listener.CICheckInListener;

/**查詢行李追蹤*/
public class CIInquiryBaggageInfoPresenter {

    private static CIInquiryBaggageInfoPresenter m_Instance = null;
    private CIBaggageInfoListener m_Listener = null;

    private CIInquiryBaggageInfoByBGNumModel        m_InquiryBaggageInfoModel  = null;
    private CIInquiryBaggageInfoByPNRAndBGNumModel  m_InquiryBaggageInfoByPNRAndBGNumModel  = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIInquiryBaggageInfoPresenter getInstance( CIBaggageInfoListener listener ){

        if ( null == m_Instance ){
            m_Instance = new CIInquiryBaggageInfoPresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setCallBack(listener);
        return m_Instance;
    }

    private void setCallBack( CIBaggageInfoListener listener ){
        m_Listener = listener;
    }

    /**
     * InquiryBaggageInfoByPNRAndBGNum
     * 功能說明: 查詢個別行李資訊
     */
    public void InquiryBaggageInfoByBGNumFromWS( String strBaggage_num, String strDeparture_station, String strDeparture_date ){

        if( null == m_InquiryBaggageInfoModel ) {
            m_InquiryBaggageInfoModel = new CIInquiryBaggageInfoByBGNumModel(m_InquiryBaggageInfoByBGNumCallBack);
        }

        m_InquiryBaggageInfoModel.setCallback(m_InquiryBaggageInfoByBGNumCallBack);
        m_InquiryBaggageInfoModel.InquiryBaggageInfoByBGNumFromWS(strBaggage_num, strDeparture_station, strDeparture_date);

        if ( null != m_Listener){
            m_Listener.showProgress();
        }
    }


    /**
     * InquiryBaggageInfoByPNRAndBGNum
     * 功能說明: 查詢行李資訊列表
     */
    public void InquiryBaggageInfoByPNRAndBGNumFromWS( String strPnrFirstName, String strPnrLastName, ArrayList<String> arPnrList, ArrayList<CIBaggageInfoReq> BaggageList ) {

        if( null == m_InquiryBaggageInfoByPNRAndBGNumModel ) {
            m_InquiryBaggageInfoByPNRAndBGNumModel = new CIInquiryBaggageInfoByPNRAndBGNumModel(m_InquiryBaggageInfoByPNRAndBGNumCallBack);
        }

        m_InquiryBaggageInfoByPNRAndBGNumModel.setCallback(m_InquiryBaggageInfoByPNRAndBGNumCallBack);
        m_InquiryBaggageInfoByPNRAndBGNumModel.InquiryBaggageInfoByPNRAndBGNumFromWS( strPnrFirstName, strPnrLastName, arPnrList, BaggageList );

        if ( null != m_Listener){
            m_Listener.showProgress();
        }
    }


    CIInquiryBaggageInfoByBGNumModel.InquiryBaggageInfoByBGNumCallBack m_InquiryBaggageInfoByBGNumCallBack = new CIInquiryBaggageInfoByBGNumModel.InquiryBaggageInfoByBGNumCallBack() {

        @Override
        public void onInquiryBaggageInfoByBGNumSuccess(final String rt_code, final String rt_msg, final ArrayList<CIBaggageInfoContentResp> arDataList ) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onBaggageInfoByBGNumSuccess(rt_code, rt_msg, arDataList);
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onInquiryBaggageInfoByBGNumError(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onBaggageInfoByBGNumError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };

    CIInquiryBaggageInfoByPNRAndBGNumModel.CallBack m_InquiryBaggageInfoByPNRAndBGNumCallBack = new CIInquiryBaggageInfoByPNRAndBGNumModel.CallBack() {
        @Override
        public void onInquiryBaggageInfoByPNRAndBGNumSuccess(final String rt_code, final String rt_msg, final ArrayList<CIBaggageInfoResp> arBaggageInfoListResp ) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onBaggageInfoByPNRAndBGNumSuccess(rt_code, rt_msg, arBaggageInfoListResp );
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onInquiryBaggageInfoByPNRAndBGNumError(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onBaggageInfoByPNRAndBGNumError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };


}
