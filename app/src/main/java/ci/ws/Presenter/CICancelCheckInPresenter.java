package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CICancelCheckInModel;
import ci.ws.Models.entities.CICancelCheckInReq;
import ci.ws.Models.entities.CICancelCheckInResp;
import ci.ws.Presenter.Listener.CICancelCheckInListener;

/**
 * Created by jlchen on 2016/6/17.
 */
public class CICancelCheckInPresenter {

    private CICancelCheckInListener         m_Listener          = null;
    private CICancelCheckInModel            m_Model             = null;
    private static CICancelCheckInPresenter s_Instance          = null;
    private static Handler                  s_hdUIThreadhandler = null;

    public static CICancelCheckInPresenter getInstance(
            CICancelCheckInListener listener) {

        if (null == s_Instance) {
            s_Instance = new CICancelCheckInPresenter(listener);
        }

        if (null == s_hdUIThreadhandler) {
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallbackListener(listener);

        return s_Instance;
    }

    public CICancelCheckInPresenter(CICancelCheckInListener listener) {
        this.m_Listener = listener;
    }

    private void setCallbackListener(CICancelCheckInListener listener) {
        this.m_Listener = listener;
    }

    /**
     * 要求取消Check-in
     */
    public void CancelCheckInFromWS(CICancelCheckInReq req) {
        if (null != m_Listener) {
            m_Listener.showProgress();
        }

        if (null == m_Model) {
            m_Model = new CICancelCheckInModel(ModelCallback);
        }
        m_Model.sendReqDataToWS(req);
    }

    /**
     * 中斷要求
     */
    public void interrupt() {
        if (null != m_Model) {
            m_Model.CancelRequest();
        }
        if (null != m_Listener) {
            m_Listener.hideProgress();
        }
    }

    private CICancelCheckInModel.CallBack ModelCallback
            = new CICancelCheckInModel.CallBack() {

        @Override
        public void onSuccess(final String rt_code,
                              final String rt_msg,
                              final CICancelCheckInResp resp) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != m_Listener) {
                        m_Listener.hideProgress();
                        m_Listener.onSuccess(rt_code, rt_msg, resp);
                    }
                }
            });
        }

        @Override
        public void onError(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != m_Listener) {
                        m_Listener.hideProgress();
                        m_Listener.onError(rt_code, rt_msg);
                    }
                }
            });
        }
    };
}
