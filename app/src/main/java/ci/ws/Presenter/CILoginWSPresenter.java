package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.function.Core.CIApplication;
import ci.ws.Models.CIInquiryCardNoByIdentityWSModel;
import ci.ws.Models.CILoginByOpenIdModel;
import ci.ws.Models.CILoginWSModel;
import ci.ws.Models.entities.CILoginReq;
import ci.ws.Models.entities.CILoginResp;
import ci.ws.Presenter.Listener.CILoginWSListener;
import ci.ws.cores.CIWSShareManager;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/4/13.
 */
public class CILoginWSPresenter {

    private static CILoginWSPresenter m_Instance = null;

    private CILoginReq m_loginReq       = null;
    private CILoginWSListener m_listener= null;

    private CILoginWSModel m_loginModel = null;
    private CIInquiryCardNoByIdentityWSModel m_InquiryCardNo = null;
    private CILoginByOpenIdModel m_LoginbyOpenId = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CILoginWSPresenter getInstance(CILoginWSListener listener){
        if ( null == m_Instance ){
            m_Instance = new CILoginWSPresenter(listener);
        } else {
            m_Instance.setCallbackListener(listener);
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        return m_Instance;
    }

    public CILoginWSPresenter(CILoginWSListener listener){
        this.m_listener = listener;
    }

    private void setCallbackListener( CILoginWSListener listener ){
        this.m_listener = listener;
    }

    /** ** 請一律用LoginWithCombineSocialFromWS **
     * 華夏會員登入。不綁定社群帳號
     * @param strUserId 帳號
     * @param strPwd 密碼*/
//    public void LoginFromWS( String strUserId, String strPwd ){
//
//        m_loginReq = new CILoginReq();
//        m_loginReq.user_id = strUserId;
//        m_loginReq.password= strPwd;
//        m_loginReq.is_social_combine = CILoginReq.SOCIAL_COMBINE_NO;
//
//        m_loginModel = new CILoginWSModel(
//                m_loginReq,
//                CIApplication.getLanguageInfo().getWSLanguage(),
//                CIApplication.getDeviceInfo().getAndroidId(),
//                WSConfig.DEF_API_VERSION,
//                m_LoginCallback);
//        m_loginModel.DoConnection();
//
//        if(null != m_listener){
//            m_listener.showProgress();
//        }
//    }

    /**取消登入的WS*/
    public void LoginCancel(){

        if(null != m_listener){
            m_listener.hideProgress();
        }
        if ( null != m_loginModel ){
            m_loginModel.CancelRequest();
        }
    }

    /**
     * 華夏會員登入, 可由參數決定是否進行綁定社群帳號。
     * @param loginReq 登入參數*/
    public void LoginWithCombineSocialFromWS( CILoginReq loginReq ){

        this.m_loginReq = loginReq;

        m_loginModel = new CILoginWSModel(
                m_loginReq,
                CIApplication.getLanguageInfo().getWSLanguage(),
                CIApplication.getDeviceInfo().getAndroidId(),
                WSConfig.DEF_API_VERSION,
                m_LoginCallback);
        m_loginModel.DoConnection();

        if(null != m_listener){
            m_listener.showProgress();
        }
    }

    /**取消登入並綁定社群帳號的WS*/
    public void LoginWithCombineSocialCancel(){

        if(null != m_listener){
            m_listener.hideProgress();
        }
        if ( null != m_loginModel ){
            m_loginModel.CancelRequest();
        }
    }

    /**M2 流程
     * 使用Email/Mobile +Identity/Passport Id取得卡號。
     * @param strAccount Email/Mobile
     * @param stridentity Identity/Passport Id*/
    public void InquiryCardNoByIdentityFromWS( String strAccount, String stridentity ){

        m_InquiryCardNo = new CIInquiryCardNoByIdentityWSModel(
                strAccount,
                stridentity,
                CIApplication.getLanguageInfo().getWSLanguage(),
                CIApplication.getDeviceInfo().getAndroidId(),
                WSConfig.DEF_API_VERSION,
                m_InquiryCallback);
        m_InquiryCardNo.DoConnection();

        if(null != m_listener){
            m_listener.showProgress();
        }
    }

    /**取消取得卡號的WS*/
    public void InquiryCardNoCancel(){

        if(null != m_listener){
            m_listener.hideProgress();
        }
        if ( null != m_InquiryCardNo ){
            m_InquiryCardNo.CancelRequest();
        }
    }

    /**
     * Created by Ryan on 16/4/21.
     * 功能說明: 使用Open Id如Facebook、Google+ Id登入系統。
     * 對應CI API : SocialLogin
     * @param open_id 登入設群組的到d
     * @param open_id_kind 登入類型 CILoginWSPresenter.OPEN_ID_KIND_FACEBOOK / CILoginWSPresenter.OPEN_ID_KIND_GOOGLE
     * @param strEmail 非必填欄位
     */
    public void LoginByOpenIdFromWS( String open_id, String open_id_kind , String strEmail ){

        m_LoginbyOpenId = new CILoginByOpenIdModel(
                open_id,
                open_id_kind,
                strEmail,
                CIApplication.getLanguageInfo().getWSLanguage(),
                CIApplication.getDeviceInfo().getAndroidId(),
                WSConfig.DEF_API_VERSION,
                m_LoginByOpenIdCallback);

        m_LoginbyOpenId.DoConnection();

        if(null != m_listener){
            m_listener.showProgress();
        }
    }

    /**取消使用SocialLogin的WS*/
    public void LoginByOpenIdCancel(){

        if(null != m_listener){
            m_listener.hideProgress();
        }
        if ( null != m_LoginbyOpenId ){
            m_LoginbyOpenId.CancelRequest();
        }
    }

    private CILoginWSModel.LoginCallback m_LoginCallback = new CILoginWSModel.LoginCallback() {
        @Override
        public void onLoginSuccess(final String rt_code, final String rt_msg, final CILoginResp loginResp) {

            //登入後須將登入token 存起來, 之後的WS都須從這邊取資料
            if ( null != loginResp && null != loginResp.member_token ){
                CIWSShareManager.getAPI().setLoginToken(loginResp.member_token);
            }

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_listener ) {
                        m_listener.onLoginSuccess(rt_code, rt_msg, loginResp);
                        m_listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onLoginError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_listener ) {
                        m_listener.onLoginError(rt_code, rt_msg);
                        m_listener.hideProgress();
                    }
                }
            });

        }
    };

    private CIInquiryCardNoByIdentityWSModel.InquiryCallback m_InquiryCallback = new CIInquiryCardNoByIdentityWSModel.InquiryCallback() {
        @Override
        public void onInquirySuccess(final String rt_code, final String rt_msg, final String strCard_no) {
            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_listener ) {
                        //因應畫面流程需要, 將登入輸入的密碼一同回覆,
                        String strPassword = "";
                        if (null != m_loginReq) {
                            strPassword = m_loginReq.password;
                        }
                        m_listener.onInquirySuccess(rt_code, rt_msg, strCard_no, strPassword);
                        m_listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_listener ) {
                        m_listener.onInquiryError(rt_code, rt_msg);
                        m_listener.hideProgress();
                    }
                }
            });
        }
    };

    private CILoginByOpenIdModel.LoginByOpenIdCallback m_LoginByOpenIdCallback = new CILoginByOpenIdModel.LoginByOpenIdCallback(){

        @Override
        public void onSocialLoginSuccess(final String rt_code, final String rt_msg, final CILoginResp socialLoginResp) {
            //登入後須將登入token 存起來, 之後的WS都須從這邊取資料
            if ( null != socialLoginResp && null != socialLoginResp.member_token ){
                CIWSShareManager.getAPI().setLoginToken(socialLoginResp.member_token);
            }

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_listener ) {
                        m_listener.onSocialLoginSuccess(rt_code, rt_msg, socialLoginResp);
                        m_listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onSocialLoginError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_listener ) {
                        m_listener.onSocialLoginError(rt_code, rt_msg);
                        m_listener.hideProgress();
                    }
                }
            });

        }
    };
}
