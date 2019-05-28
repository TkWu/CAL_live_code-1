package ci.ws.Presenter;


import android.os.Handler;
import android.os.Looper;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.ws.Models.CIBookTicketModel;
import ci.ws.Models.CIInquiryPromoteCodeTokenModel;
import ci.ws.Models.CIManageTicketModel;
import ci.ws.Models.entities.CIInquiryPromoteCodeTokenResp;
import ci.ws.cores.object.CIResponse;

/**
 * Created by ryan on 2018/6/26.
 */
public class CIManageTicketPresenter {

    private CallBack    m_view              = null;
    private Handler     m_hdUIThreadhandler = null;

    public CIManageTicketPresenter(CallBack view) {
        m_view = view;
        startUIThreadHandler();
    }

    public void fetchBookTicketWebData( String postData) {
        m_view.showProgress();
        String strURL = CIApplication.getContext().getString(R.string.manage_ticket_ws_url);
        CIManageTicketModel.findData(callBack, strURL, postData);
    }

    private void startUIThreadHandler() {
        m_hdUIThreadhandler = new Handler(Looper.getMainLooper());
    }

    CIManageTicketModel.CallBack callBack = new CIManageTicketModel.CallBack() {
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
        CIManageTicketModel.cancel();
    }

    public interface CallBack{
        void showProgress();
        void hideProgress();
        void onDataBinded(String webData);
        void onDataFetchFeild(String msg);
    }
}
