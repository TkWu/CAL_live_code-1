package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIChangePasswordModel;
import ci.ws.Presenter.Listener.CIChangePasswordListener;
import ci.ws.define.CIWSResultCode;

/**
 * Created by Ryan on 16/5/17.
 */
public class CIChangePasswordPresenter {

    private static CIChangePasswordPresenter m_Instance = null;
    private CIChangePasswordListener    m_Listener = null;
    private CIChangePasswordModel       m_ChangePwdModel = null;

    private static Handler s_hdUIThreadhandler = null;

    public CIChangePasswordPresenter(){}

    public static CIChangePasswordPresenter getInstance( CIChangePasswordListener listener ){

        if ( null == m_Instance ){
            m_Instance = new CIChangePasswordPresenter();
        }

        if(null == s_hdUIThreadhandler) {
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setcallback(listener);
        return m_Instance;
    }

    private void setcallback( CIChangePasswordListener listener ){
        this.m_Listener = listener;
    }

    /**變更密碼
     * @param strCardNo 卡號
     * @param strOldPwd 舊密碼
     * @param strNewPwd 新密碼*/
    public void ChangePasswordFromWS( String strCardNo, String strOldPwd, String strNewPwd ){

        if ( null == m_ChangePwdModel ){
            m_ChangePwdModel = new CIChangePasswordModel(m_modelcallback);
        }

        m_ChangePwdModel.ChangePassword(strCardNo, strOldPwd, strNewPwd);
        if ( null != m_Listener ){
            m_Listener.showProgress();
        }
    }

    /**
     * 取消變更密碼WS
     */
    public void CancelChangePassword(){
        if ( null != m_ChangePwdModel ){
            m_ChangePwdModel.CancelRequest();
        }
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
    }


    CIChangePasswordModel.ChangePasswordCallBack m_modelcallback = new CIChangePasswordModel.ChangePasswordCallBack() {
        @Override
        public void onChangePasswordSuccess(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        m_Listener.onChangePasswordSuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onChangePasswordError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ){
                        switch (rt_code){
                            case CIWSResultCode.ERROR_LOGOUT_AUTOMATICALLY:
                            case CIWSResultCode.ERROR_AUTHORIZATION_FAILED:
                                m_Listener.onAuthorizationFailedError(rt_code, rt_msg);
                                break;
                            default:
                                m_Listener.onChangePasswordError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };
}
