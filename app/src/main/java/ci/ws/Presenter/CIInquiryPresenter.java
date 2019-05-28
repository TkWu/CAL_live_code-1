package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.function.Core.CIApplication;
import ci.ws.Models.CIApplyPasswordModel;
import ci.ws.Models.CIInquiryCardNoModel;
import ci.ws.Models.CIInquiryCardNoModel.InquiryCardNoCallback;
import ci.ws.Models.CIInquiryPasswordModel;
import ci.ws.Presenter.Listener.CIInquiryListener;
import ci.ws.define.WSConfig;

/**
 * Created by Ryan on 16/4/22.
 * 忘記登入資訊畫面使用的WS
 */
public class CIInquiryPresenter {

    private CIInquiryListener           m_Listener          = null;
    private CIInquiryCardNoModel        m_InquiryCard       = null;
    private CIInquiryPasswordModel      m_InquiryPassword   = null;
    private CIApplyPasswordModel        m_ApplyPassword     = null;

    private static Handler              s_hdUIThreadhandler = null;
    private static CIInquiryPresenter   s_Instance          = null;

    public static CIInquiryPresenter getInstance(CIInquiryListener listener ){
        if ( null == s_Instance ){
            s_Instance = new CIInquiryPresenter();
        }

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }

        s_Instance.setCallBack(listener);
        return s_Instance;
    }

    private void setCallBack( CIInquiryListener listener ){
        m_Listener = listener;
    }

    /**
     * 功能說明: 以First Name+Last Name+Birth Date為條件取得會員編號。
     * 對應CI API : QueryCardNo
     *
     * @param first_name 會員英文名
     * @param last_name  會員英文姓氏
     * @param birth_date 會員生日      YYYY-MM-DD
     */
    public void InquiryCardNoFromWS(String first_name, String last_name, String birth_date) {

        m_InquiryCard = new CIInquiryCardNoModel(
                first_name,
                last_name,
                birth_date,
                CIApplication.getLanguageInfo().getWSLanguage(),
                CIApplication.getDeviceInfo().getAndroidId(),
                WSConfig.DEF_API_VERSION,
                m_InquiryCardNoCallback);
        m_InquiryCard.DoConnection();

        if (null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * 取消取得會員編號
     */
    public void InquiryCardNoCancel() {
        if (null != m_InquiryCard) {
            m_InquiryCard.CancelRequest();
        }
        if (null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    /**
     * 功能說明: 以First Name+Last Name+Birth Date為條件取得會員密碼。
     * 對應CI API : QueryCardNo
     *
     * @param first_name 會員英文名
     * @param last_name  會員英文姓氏
     * @param birth_date 會員生日      YYYY-MM-DD
     */
    public void InquiryPasswordFromWS(String card_no, String first_name, String last_name, String birth_date) {

        m_InquiryPassword = new CIInquiryPasswordModel(
                card_no,
                first_name,
                last_name,
                birth_date,
                CIApplication.getLanguageInfo().getWSLanguage(),
                CIApplication.getDeviceInfo().getAndroidId(),
                WSConfig.DEF_API_VERSION,
                m_InquiryPasswordCallback);
        m_InquiryPassword.DoConnection();

        if (null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * 取消取得會員密碼
     */
    public void InquiryPasswordCancel() {
        if (null != m_InquiryPassword) {
            m_InquiryPassword.CancelRequest();
        }
        if (null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    /**
     * 功能說明:以Card No.+First Name+Last Name+Birth Date為條件取得會員密碼。
     * 對應CI API : ApplyPassword
     * @param first_name 會員英文名
     * @param last_name  會員英文姓氏
     * @param birth_date 會員生日      YYYY-MM-DD
     */
    public void ApplyPasswordFromWS(String card_no, String first_name, String last_name, String birth_date) {

        m_ApplyPassword = new CIApplyPasswordModel(
                card_no,
                first_name,
                last_name,
                birth_date,
                CIApplication.getLanguageInfo().getWSLanguage(),
                CIApplication.getDeviceInfo().getAndroidId(),
                WSConfig.DEF_API_VERSION,
                m_ApplyPWDcallback);
        m_ApplyPassword.DoConnection();

        if (null != m_Listener){
            m_Listener.showProgress();
        }
    }

    /**
     * 取消取得會員密碼
     */
    public void ApplyPasswordCancel() {
        if (null != m_ApplyPassword) {
            m_ApplyPassword.CancelRequest();
        }
        if (null != m_Listener){
            m_Listener.hideProgress();
        }
    }

    InquiryCardNoCallback m_InquiryCardNoCallback = new InquiryCardNoCallback() {

        @Override
        public void onInquiryCardNoSuccess(final String rt_code, final String rt_msg, final String email) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != m_Listener) {
                        m_Listener.onInquiryCardNoSuccess(rt_code, rt_msg, email);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryCardNoError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != m_Listener) {
                        m_Listener.onInquiryCardNoError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIInquiryPasswordModel.InquiryPasswordCallback m_InquiryPasswordCallback = new CIInquiryPasswordModel.InquiryPasswordCallback() {
        @Override
        public void onInquiryPasswordSuccess(final String rt_code, final String rt_msg, final String email) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != m_Listener) {
                        m_Listener.onInquiryPasswordSuccess(rt_code, rt_msg, email);
                        m_Listener.hideProgress();
                    }
                }
            });

        }

        @Override
        public void onInquiryPasswordError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != m_Listener) {
                        m_Listener.onInquiryPasswordError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });

        }
    };

    CIApplyPasswordModel.ApplyPasswordCallback m_ApplyPWDcallback = new CIApplyPasswordModel.ApplyPasswordCallback() {
        @Override
        public void onApplyPasswordSuccess(final String rt_code, final String rt_msg, final String email) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != m_Listener) {
                        m_Listener.onApplyPasswordSuccess(rt_code, rt_msg, email);
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onApplyPasswordError(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != m_Listener) {
                        m_Listener.onApplyPasswordError(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };
}
