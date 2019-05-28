package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.function.Core.CIApplication;
import ci.ws.Models.CISignUpWSModel;
import ci.ws.Models.entities.CISignUpReq;
import ci.ws.Models.entities.CISignUpResp;
import ci.ws.Presenter.Listener.CISignUpWSListener;
import ci.ws.define.CICardType;
import ci.ws.define.WSConfig;

/**
 * Created by ryan on 16/4/16.
 * 註冊會員
 */
public class CISignUpWSPresenter {

    private static CISignUpWSPresenter m_Instance = null;

    private CISignUpWSListener  m_Listener = null;
    private CISignUpReq         m_Signup = null;

    private CISignUpWSModel     m_SignUpModel = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CISignUpWSPresenter getInstance( CISignUpWSListener listener ){
        if ( null == m_Instance ){
            m_Instance = new CISignUpWSPresenter(listener);
        } else {
            m_Instance.setCallbackListener(listener);
        }
        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }
        return m_Instance;
    }

    public CISignUpWSPresenter( CISignUpWSListener listener ){
        this.m_Listener = listener;
    }

    private void setCallbackListener( CISignUpWSListener listener ){
        this.m_Listener = listener;
    }

    public void SignUpFromWS( CISignUpReq signUpReq ){

        this.m_Signup = signUpReq;

        if ( null == m_SignUpModel ){
            m_SignUpModel = new CISignUpWSModel(m_SignUpCallback);
        }

        m_SignUpModel.SignUpFromWS(
                signUpReq,
                CIApplication.getLanguageInfo().getWSLanguage(),
                CIApplication.getDeviceInfo().getAndroidId(),
                WSConfig.DEF_API_VERSION);

        if ( null != m_Listener ){
            m_Listener.showProgress();
        }
    }

    /**取消註冊的WS*/
    public void CancelSignUp(){

        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_SignUpModel ){
            m_SignUpModel.CancelRequest();
        }
    }

    private CISignUpWSModel.SignUpCallback m_SignUpCallback = new CISignUpWSModel.SignUpCallback() {
        @Override
        public void onSignUpSuccess(final String rt_code, final String rt_msg, final CISignUpResp SignupResp) {

            if ( null != m_Signup && null != SignupResp ){
//                SignupResp.last_name    = m_Signup.last_name;
//                SignupResp.first_name   = m_Signup.first_name;
                //註冊成功一定是華夏卡
                SignupResp.card_type    = CICardType.DYNA;
                //華夏卡 無有效日期
                SignupResp.card_vaild_date = "";
            }

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onSignUpSuccess(rt_code, rt_msg, SignupResp);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onSignUpError(final String rt_code, final String rt_msg) {

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
