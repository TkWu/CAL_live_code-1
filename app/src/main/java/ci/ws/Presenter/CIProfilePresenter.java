package ci.ws.Presenter;


import android.os.Handler;
import android.os.Looper;


import ci.ws.Models.CIInquiryProfileModel;
import ci.ws.Models.CIUpdateProfileModel;
import ci.ws.Models.entities.CIProfileEntity;
import ci.ws.Models.entities.CIUpdateProfileEntity;
import ci.ws.Presenter.Listener.CIInquiryProfileListener;
import ci.ws.define.CIWSResultCode;

/**
 * Created by Ryan on 16/5/10.
 * Profile (取得/ 更新個人資訊)
 * 功能說明: 傳入會員卡號，取得個人資訊 / 更新會員電子信箱帳號、是否接收促銷訊息、會員手機號碼及是否接受簡訊。
 * 對應CI API : QueryProfile / ChangeProfile
 */
public class CIProfilePresenter {

    private static CIProfilePresenter m_Instance = null;
    private CIInquiryProfileListener m_Listener = null;

    private CIInquiryProfileModel   m_InquiryModel = null;
    private CIUpdateProfileModel    m_UpdateModel = null;

    private static Handler s_hdUIThreadhandler = null;

    public static CIProfilePresenter getInstance( CIInquiryProfileListener listener ){

        if ( null == m_Instance ){
            m_Instance = new CIProfilePresenter();
        }
        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }
        m_Instance.setCallBack(listener);

        return m_Instance;
    }

    private void setCallBack( CIInquiryProfileListener listener ){
        m_Listener = listener;
    }


    /**
     * 功能說明: 傳入會員卡號，取得個人資訊
     * @param strCardNo 卡號
     * */
    public void InquiryProfileFromWS( String strCardNo ){

        if ( null == m_InquiryModel ){
            m_InquiryModel = new CIInquiryProfileModel(m_modelCallback);
        }

        m_InquiryModel.InquiryProfileFromWS(strCardNo);
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消登入的WS*/
    public void InquiryProfileCancel(){

        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_InquiryModel ){
            m_InquiryModel.CancelRequest();
        }
    }

    /**
     * 3.1.	UpdateProfile (更新個人資訊)
     * 功能說明: 更新會員電子信箱帳號、是否接收促銷訊息、會員手機號碼及是否接受簡訊。
     * @param strCardNo 卡號
     * @param  profileEntity 待更新的欄位
     * */
    public void UpdateProfileFromWS( String strCardNo, CIUpdateProfileEntity profileEntity ){

        if ( null == m_UpdateModel ){
            m_UpdateModel = new CIUpdateProfileModel(m_updateCallback);
        }

        m_UpdateModel.UpdateProfileFromWS(strCardNo, profileEntity);
        if(null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**取消登入的WS*/
    public void UpdateProfileCancel(){

        if(null != m_Listener){
            m_Listener.hideProgress();
        }
        if ( null != m_UpdateModel ){
            m_UpdateModel.CancelRequest();
        }
    }

    CIInquiryProfileModel.InquiryCallback m_modelCallback = new CIInquiryProfileModel.InquiryCallback() {
        @Override
        public void onInquiryProfileSuccess(final String rt_code, final String rt_msg, final CIProfileEntity profile) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onInquiryProfileSuccess(rt_code, rt_msg, profile);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryProfileError(final String rt_code, final String rt_msg) {

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
                                m_Listener.onInquiryProfileError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIUpdateProfileModel.UpdateCallback m_updateCallback = new CIUpdateProfileModel.UpdateCallback() {
        @Override
        public void onUpdateProfileSuccess(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onUpdateProfileSuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onUpdateProfileError(final String rt_code, final String rt_msg) {

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
                                m_Listener.onUpdateProfileError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };
}
