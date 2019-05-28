package ci.ws.Presenter;

import android.os.Handler;
import android.os.Looper;

import ci.function.Core.CIApplication;
import ci.ws.Presenter.Listener.CIUpdateEmailByMemberNoWSListener;
import ci.ws.Models.CIUpdateEmailByMemberNoWSModel;
import ci.ws.cores.CIWSShareManager;
import ci.ws.define.CIWSResultCode;
import ci.ws.define.WSConfig;

/**
 * Created by ryan on 16/4/16.
 * 功能說明: 以Member No. 為條件更新會員Email。
 */
public class CIUpdateEmailByMemberNoWSPresenter {

    private CIUpdateEmailByMemberNoWSListener   m_Listener = null;
    private CIUpdateEmailByMemberNoWSModel      m_updateModel = null;

    private static Handler s_hdUIThreadhandler = null;

    public CIUpdateEmailByMemberNoWSPresenter( CIUpdateEmailByMemberNoWSListener listener ){

        this.m_Listener = listener;

        if ( null == s_hdUIThreadhandler ){
            s_hdUIThreadhandler = new Handler(Looper.getMainLooper());
        }
    }

    /**功能說明: 以Member No. 為條件更新會員Email。
     *@param strCardno 卡號
     *@param strEmail 信箱
     * */
    public void UpdateEmailByMemberNoFromWS( String strCardno, String strEmail ){
        Connection(strCardno, strEmail, CIUpdateEmailByMemberNoWSModel.eCONDITION.Email.getString());
    }

    /**功能說明: 以Member No. 為條件更新會員Phone。
     *@param strCardno 卡號
     *@param  strCountryCode 國碼
     *@param strPhoen  電話
     */
    public void UpdatePhoenByMemberNoFromWS( String strCardno, String strCountryCode, String strPhoen ){

        Connection(strCardno, strCountryCode + "-" + strPhoen, CIUpdateEmailByMemberNoWSModel.eCONDITION.Phone.getString());
    }

    /**取消更新Email or Phone的WS*/
    public void CancelUpdateEmailyPhone(){
        if ( null != m_updateModel ){
            m_updateModel.CancelRequest();
        }
        if ( null != m_Listener ){
            m_Listener.hideProgress();
        }
    }

    private void Connection( String strCardno, String strAccount, String strCondition ){

        if ( null == m_updateModel ){
            m_updateModel = new CIUpdateEmailByMemberNoWSModel(m_callback);
        }

        m_updateModel.UpdateEmailByMemberNoWS(
                CIWSShareManager.getAPI().getLoginToken(),
                strCardno,
                strAccount,
                strCondition,
                CIApplication.getLanguageInfo().getWSLanguage(),
                CIApplication.getDeviceInfo().getAndroidId(),
                WSConfig.DEF_API_VERSION);

        if ( null != m_Listener ){
            m_Listener.showProgress();
        }
    }

    CIUpdateEmailByMemberNoWSModel.UpdateCallback m_callback = new CIUpdateEmailByMemberNoWSModel.UpdateCallback() {
        @Override
        public void onUpdateSuccess(final String rt_code, final String rt_msg) {

            s_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if ( null != m_Listener ) {
                        m_Listener.onUpdateSuccess(rt_code, rt_msg);
                        m_Listener.hideProgress();
                    }
                }
            });
        }

        @Override
        public void onUpdateError(final String rt_code, final String rt_msg) {

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
                                m_Listener.onUpdateError(rt_code, rt_msg);
                                break;
                        }
                        m_Listener.hideProgress();
                    }
                }
            });
        }
    };
}
