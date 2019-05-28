package ci.ws.Presenter;


import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIBookTicketModel;
import ci.ws.Models.CIInquiryCoporateIdTokenModel;
import ci.ws.Models.CIInquiryPromoteCodeTokenModel;
import ci.ws.Models.entities.CIInquiryCoporateIdTokenResp;
import ci.ws.Models.entities.CIInquiryPromoteCodeTokenResp;
import ci.ws.cores.object.CIResponse;

/**
 * Created by kevincheng on 2016/4/27.
 */
public class CIBookTicketPresenter {

    private CallBack                       m_view                         = null;
    private Handler                        m_hdUIThreadhandler            = null;
    private CIInquiryPromoteCodeTokenModel m_inquiryPromoteCodeTokenModel = null;
    private CIInquiryCoporateIdTokenModel m_inquiryCoporateIdTokenModel = null;

    public CIBookTicketPresenter(CallBack view) {
        m_view = view;
        startUIThreadHandler();
    }

    public void fetchBookTicketWebData(String postData) {
        m_view.showProgress();
        CIBookTicketModel.findData(callBack, postData);
    }

    public void fetchRromoteCodeToken(String code) {
        m_view.showProgress();
        if(null == m_inquiryPromoteCodeTokenModel) {
            m_inquiryPromoteCodeTokenModel = new CIInquiryPromoteCodeTokenModel(m_InquiryPromoteCodeTokencallBack);
        }
        m_inquiryPromoteCodeTokenModel.getDataFrowWS(code);
    }

    public void fetchCoporateIdToken(String code) {
        m_view.showProgress();
        if(null == m_inquiryCoporateIdTokenModel) {
            m_inquiryCoporateIdTokenModel = new CIInquiryCoporateIdTokenModel(m_InquiryCoporateIdTokencallBack);
        }
        m_inquiryCoporateIdTokenModel.getDataFrowWS(code);
    }

    private void startUIThreadHandler() {
        m_hdUIThreadhandler = new Handler(Looper.getMainLooper());
    }

    CIInquiryPromoteCodeTokenModel.Callback m_InquiryPromoteCodeTokencallBack = new CIInquiryPromoteCodeTokenModel.Callback() {
        @Override
        public void onSuccess(String rt_code, String rt_msg, final CIInquiryPromoteCodeTokenResp data) {
            m_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_view){
                        m_view.hideProgress();
                        m_view.onPromoteCodeTokenBinded(data);
                    }
                }
            });
        }

        @Override
        public void onError(String rt_code, final String rt_msg) {
            m_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_view) {
                        m_view.hideProgress();
                        m_view.onDataFetchFeild(rt_msg);
                    }
                }
            });
        }
    };

    CIInquiryCoporateIdTokenModel.Callback m_InquiryCoporateIdTokencallBack = new CIInquiryCoporateIdTokenModel.Callback() {
        @Override
        public void onSuccess(String rt_code, String rt_msg, final CIInquiryCoporateIdTokenResp data) {
            m_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_view){
                        m_view.hideProgress();
                        m_view.onCoporateIdTokenBinded(data);
                    }
                }
            });
        }

        @Override
        public void onError(String rt_code, final String rt_msg) {
            m_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_view) {
                        m_view.hideProgress();
                        m_view.onDataFetchFeild(rt_msg);
                    }
                }
            });
        }
    };

    CIBookTicketModel.CallBack callBack = new CIBookTicketModel.CallBack() {
        @Override
        public void onSuccess(final String webData) {
            m_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_view){
                        m_view.hideProgress();
                        m_view.onDataBinded(webData);
                    }
                }
            });
        }

        @Override
        public void onError(CIResponse response, int code,final Exception exception) {
            m_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_view) {
                        m_view.hideProgress();
                        m_view.onDataFetchFeild(exception.getMessage());
                    }
                }
            });
        }
    };

    //request and response interrupt on pause
    public void interrupt(){
        m_view.hideProgress();
        CIBookTicketModel.cancel();

        if(null != m_inquiryPromoteCodeTokenModel) {
            m_inquiryPromoteCodeTokenModel.CancelRequest();
        }
    }

    public interface CallBack{
        void showProgress();
        void hideProgress();
        void onDataBinded(String webData);
        void onPromoteCodeTokenBinded(CIInquiryPromoteCodeTokenResp data);
        void onCoporateIdTokenBinded(CIInquiryCoporateIdTokenResp data);
        void onDataFetchFeild(String msg);
    }
}
