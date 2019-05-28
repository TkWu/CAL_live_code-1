package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.ws.Models.CIConnSocialNetworkModel;
import ci.ws.Models.CIDisConnSocialNetworkModel;
import ci.ws.Models.entities.CIConnSocialResp;
import ci.ws.Presenter.Listener.CIConnSocialNetworkListener;
import ci.ws.define.CIWSResultCode;

/**
 * Created by ryan on 16/5/17.
 * 功能說明: 綁定 / 解除社群帳號。
 * 對應CI API : SocialCombine / SocialSeparate
 */
public class CIConnSocialNetworkPresenter {

    private static CIConnSocialNetworkPresenter m_Instance = null;
    private CIConnSocialNetworkListener m_Listener = null;
    private CIConnSocialNetworkModel    m_connSocialNetwork = null;
    private CIDisConnSocialNetworkModel m_DisconnSocialNetwork = null;

    private static Handler s_hdUIThreadhandler = null;

    public CIConnSocialNetworkPresenter(){}

    public static CIConnSocialNetworkPresenter getInstance( CIConnSocialNetworkListener listener ){

        if ( null == m_Instance ){
            m_Instance = new CIConnSocialNetworkPresenter();
        }

        if(null == s_hdUIThreadhandler) {
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        m_Instance.setcallback(listener);
        return m_Instance;
    }

    private void setcallback( CIConnSocialNetworkListener listener ){
        this.m_Listener = listener;
    }

    /**
     * 綁定社群帳號
     * @param card_no 會員卡號
     * @param open_id Facebook, google+ id
     * @param open_id_kind 社群帳號類型
     * @param strSocialAccount 社群帳號
     * */
    public void ConnSocialNetworkFromWS( String card_no, String open_id, String open_id_kind, String strSocialAccount ){

        if ( null == m_connSocialNetwork ){
            m_connSocialNetwork = new CIConnSocialNetworkModel(m_callback);
        }
        m_connSocialNetwork.ConnSocialNetworkFromWS(card_no, open_id, open_id_kind, strSocialAccount);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * 取消綁定WS
     */
    public void ConnSocialNetworkCancel(){
        if ( null != m_connSocialNetwork ){
            m_connSocialNetwork.CancelRequest();
        }
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    /**
     * 解除綁定社群帳號
     * @param card_no 會員卡號
     * @param open_id Facebook, google+ id
     * @param open_id_kind 社群帳號類型
     * */
    public void DisConnSocialNetworkFromWS( String card_no, String open_id, String open_id_kind ){

        if ( null == m_DisconnSocialNetwork ){
            m_DisconnSocialNetwork = new CIDisConnSocialNetworkModel(m_DisCallback);
        }
        m_DisconnSocialNetwork.DisConnSocialNetworkFromWS(card_no, open_id, open_id_kind);

        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * 取消解除綁定的WS
     */
    public void DisConnSocialNetworkCancel(){
        if ( null != m_DisconnSocialNetwork ){
            m_DisconnSocialNetwork.CancelRequest();
        }
        if(null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    CIConnSocialNetworkModel.ConnSocialNetworkCallback m_callback = new CIConnSocialNetworkModel.ConnSocialNetworkCallback(){

        @Override
        public void onSocialConnSuccess(final String rt_code, final String rt_msg, final CIConnSocialResp connSocialResp) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onSocialConnSuccess(rt_code, rt_msg, connSocialResp);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onSocialConnError(final String rt_code, final String rt_msg) {

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
                                m_Listener.onSocialConnError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIDisConnSocialNetworkModel.DisConnCallback m_DisCallback = new CIDisConnSocialNetworkModel.DisConnCallback() {
        @Override
        public void onDisConnSocialConnSuccess(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onDisConnSocialConnSuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onDisConnSocialConnError(final String rt_code, final String rt_msg) {

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
                                m_Listener.onDisConnSocialConnError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };
}
