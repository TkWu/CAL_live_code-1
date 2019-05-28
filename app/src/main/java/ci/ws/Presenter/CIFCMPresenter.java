package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Set;

import ci.function.Core.CIApplication;
import ci.ws.Models.CIFCMUpdateDeviceModel;
import ci.ws.Models.CIFCMUpdateMsgModel;
import ci.ws.Models.CISignUpWSModel;
import ci.ws.Models.entities.CISignUpReq;
import ci.ws.Models.entities.CISignUpResp;
import ci.ws.Models.entities.CIUpdateDeviceReq;
import ci.ws.Presenter.Listener.CIFCMWSListener;
import ci.ws.Presenter.Listener.CISignUpWSListener;
import ci.ws.define.CICardType;
import ci.ws.define.WSConfig;

/**
 * 使用API 上傳裝置基本資訊已供後續推波服務使用
 */
public class CIFCMPresenter {

    private static CIFCMPresenter   m_Instance = null;

    private CIFCMWSListener         m_Listener = null;
    private CIFCMUpdateDeviceModel  m_UpdateDeviceModel;
    private CIFCMUpdateMsgModel     m_UpdateMsg;

    private static Handler s_hdUIThreadhandler = null;

    public static CIFCMPresenter getInstance(CIFCMWSListener listener ){
        if ( null == m_Instance ){
            m_Instance = new CIFCMPresenter(listener);
        } else {
            m_Instance.setCallbackListener(listener);
        }
        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }
        return m_Instance;
    }

    public CIFCMPresenter(CIFCMWSListener listener ){
        this.m_Listener = listener;
    }

    private void setCallbackListener( CIFCMWSListener listener ){
        this.m_Listener = listener;
    }

    public void UpdateDevice( CIUpdateDeviceReq req ){


        if ( null == m_UpdateDeviceModel ){
            m_UpdateDeviceModel = new CIFCMUpdateDeviceModel(m_callBack);
        }

        m_UpdateDeviceModel.CIUpdateDevice(req);

        if ( null != m_Listener ){
            m_Listener.showProgress();
        }
    }

    public void UpdateMsg( ArrayList<String> arMsgIdList ){


        if ( null == m_UpdateMsg ){
            m_UpdateMsg = new CIFCMUpdateMsgModel(m_callBack_UpdateMsg);
        }

        m_UpdateMsg.UpdateMsg(arMsgIdList);

        if ( null != m_Listener ){
            m_Listener.showProgress();
        }
    }

    /**取消的WS*/
    public void CancelSignUp(){

        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_UpdateDeviceModel ){
            m_UpdateDeviceModel.CancelRequest();
        }
    }

    private CIFCMUpdateDeviceModel.UpdateDeviceCallBack m_callBack = new CIFCMUpdateDeviceModel.UpdateDeviceCallBack(){

        @Override
        public void onUpdateDeviceSuccess(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onSignUpSuccess(rt_code, rt_msg );
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onUpdateDeviceError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onSignUpError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };

    private CIFCMUpdateMsgModel.UpdateMsgCallBack m_callBack_UpdateMsg = new CIFCMUpdateMsgModel.UpdateMsgCallBack() {
        @Override
        public void onUpdateMsgSuccess(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onSignUpSuccess(rt_code, rt_msg );
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onUpdateMsgError(final String rt_code, final String rt_msg) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onSignUpError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };

}
