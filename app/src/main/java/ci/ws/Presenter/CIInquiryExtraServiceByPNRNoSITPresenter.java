package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import ci.ws.Models.CIInquiryExtraServiceByPNRNoSITModel;
import ci.ws.Models.entities.CIEWalletReq;
import ci.ws.Models.entities.CIEWallet_ExtraService_List;
import ci.ws.Models.entities.CIExtraServiceResp;
import ci.ws.Presenter.Listener.CIInquiryExtraServiceByPNRNoSITListener;

/**
 * Created by kevin cheng on 16/5/19.
 * 功能說明:使用PNR、First Name、Last Name取得ExtraService資訊
 */
public class CIInquiryExtraServiceByPNRNoSITPresenter {

    private CIInquiryExtraServiceByPNRNoSITListener         m_Listener          = null;
    private CIInquiryExtraServiceByPNRNoSITModel            m_Model             = null;
    private static CIInquiryExtraServiceByPNRNoSITPresenter s_Instance          = null;
    private static Handler                                  s_hdUIThreadhandler = null;

    public static CIInquiryExtraServiceByPNRNoSITPresenter getInstance(
            CIInquiryExtraServiceByPNRNoSITListener listener ){

        if ( null == s_Instance){
            s_Instance = new CIInquiryExtraServiceByPNRNoSITPresenter(listener);
        }

        if ( null == s_hdUIThreadhandler){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallbackListener(listener);

        return s_Instance;
    }

    private void setCallbackListener( CIInquiryExtraServiceByPNRNoSITListener listener ){
        this.m_Listener = listener;
    }

    public CIInquiryExtraServiceByPNRNoSITPresenter(CIInquiryExtraServiceByPNRNoSITListener listener ){
        this.m_Listener = listener;
    }

    /**
     * 中斷要求
     */
    public void interrupt(){
        if(null != m_Model) {
            m_Model.CancelRequest();
        }
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    /**從WS取ExtraServices資料*/
    public void InquiryExtraServiceByPNRNoSIT(CIEWalletReq reqData){
        if(null == m_Model){
            m_Model = new CIInquiryExtraServiceByPNRNoSITModel(ModelCallback);
        }
        m_Model.sendReqDataToWS(reqData);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**從DB取ExtraServices資料*/
    public ArrayList<CIEWallet_ExtraService_List> InquiryExtraServiceFromDB(){
        if ( null == m_Model){
            m_Model = new CIInquiryExtraServiceByPNRNoSITModel(ModelCallback);
        }
        return m_Model.getDataFromDB();
    }

    private CIInquiryExtraServiceByPNRNoSITModel.CallBack ModelCallback = new CIInquiryExtraServiceByPNRNoSITModel.CallBack() {
        @Override
        public void onSuccess(final String rt_code, final String rt_msg, final CIExtraServiceResp datas) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_Listener){
                        m_Listener.hideProgress();
                        m_Listener.onSuccess(rt_code, rt_msg, datas);
                    }
                }
            });
        }

        @Override
        public void onError(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_Listener) {
                        m_Listener.hideProgress();
                        m_Listener.onError(rt_code, rt_msg);
                    }
                }
            });
        }
    };
}
