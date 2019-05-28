package ci.function.PersonalDetail.SocialNetwork;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import ci.function.Core.SLog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.function.Login.api.FacebookLoginApi;
import ci.function.Login.api.GooglePlusLoginApi;
import ci.function.Login.listener.SocialConnectListener;
import ci.function.Main.BaseActivity;
import ci.ui.SocialNetworkCard.CIPersonalSocialNetworkView;
import ci.ui.SocialNetworkCard.CIPersonalSocialNetworkView.OnPersonalSocialNetworkViewListener;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.AppInfo;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIConnSocialResp;
import ci.ws.Presenter.CIConnSocialNetworkPresenter;
import ci.ws.Presenter.Listener.CIConnSocialNetworkListener;

/**
 * Created by jlchen on 2016/3/17.
 */
public class CISocialNetworkDetailActivity extends BaseActivity implements SocialConnectListener {

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.social_network);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            finish();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    private OnPersonalSocialNetworkViewListener m_OnPersonalSocialNetworkViewListener = new OnPersonalSocialNetworkViewListener() {
        @Override
        public void OnEditClick() {}

        @Override
        public void OnFacebookConnectClick() {

            // clicked on facebook login
           SLog.d("Facebook onclick", "clicked");

            m_bIsNetworkAvailable = AppInfo.getInstance(m_Context).bIsNetworkAvailable();
            if (m_bIsNetworkAvailable) {
                ArrayList<String> permissions = new ArrayList<>();
                permissions.add("email");
                permissions.add("public_profile");
                permissions.add("user_location");

                m_FBApi.SocialNetworkSignIn(FB_SIGN_IN, permissions);
            } else {
                //沒網路
                showNoWifiDialog();
            }
        }

        @Override
        public void OnFacebookDisconnectClick() {

            Intent intent = new Intent();
            intent.putExtra(UiMessageDef.BUNDLE_SOCIAL_NETWORK_DISCONNECT_TAG, getString(R.string.facebook));
            intent.setClass(m_Context, CISocialNetworkDisconnectActivity.class);
            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_SOCIAL_NETWORK_DISCONNECT_TAG);

            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        @Override
        public void OnGoogleConnectClick() {

            // clicked on google plus login
           SLog.d("Google+ onclick", "clicked");
            m_bIsNetworkAvailable = AppInfo.getInstance(m_Context).bIsNetworkAvailable();
            if (m_bIsNetworkAvailable) {
                if ( true == m_GPlusApi.checkPlayServicesShowDialog(m_Context) ){
                    m_GPlusApi.GoogleSignIn(GPLUS_SIGN_IN);
                }
            } else {
                //沒網路
                showNoWifiDialog();
            }
        }

        @Override
        public void OnGoogleDisconnectClick() {

            Intent intent = new Intent();
            intent.putExtra(UiMessageDef.BUNDLE_SOCIAL_NETWORK_DISCONNECT_TAG, getString(R.string.google_plus));
            intent.setClass(m_Context, CISocialNetworkDisconnectActivity.class);
            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_SOCIAL_NETWORK_DISCONNECT_TAG);

            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }
    };

    private CIConnSocialNetworkListener m_ConnSocialNetworkListener = new CIConnSocialNetworkListener() {
        @Override
        public void onSocialConnSuccess(String rt_code, String rt_msg, CIConnSocialResp connSocialResp) {

            if( UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK.equals(m_strSocialType) ) {
                CIApplication.getLoginInfo().SetFbCombineStatus(true);
                CIApplication.getLoginInfo().SetFbEmail(m_strSocialMail);
                CIApplication.getLoginInfo().SetFbLoginId(m_strSocialOpenId);
            } else {
                CIApplication.getLoginInfo().SetGoogleCombineStatus(true);
                CIApplication.getLoginInfo().SetGoogleEmail(m_strSocialMail);
                CIApplication.getLoginInfo().SetGoogleLoginId(m_strSocialOpenId);
            }

            //m_proDlg.dismiss();
            hideProgressDialog();

            m_BaseHandler.post(new Runnable() {
                @Override
                public void run() {
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }

        @Override
        public void onSocialConnError(final String rt_code, final String rt_msg) {
            //m_proDlg.dismiss();
            hideProgressDialog();
            m_BaseHandler.post(new Runnable() {
                @Override
                public void run() {

                    CIAlertDialog dialog = new CIAlertDialog(m_Context, new CIAlertDialog.OnAlertMsgDialogListener() {
                        @Override
                        public void onAlertMsgDialog_Confirm() {}

                        @Override
                        public void onAlertMsgDialogg_Cancel() {}
                    });

                    dialog.uiSetTitleText("Warning");
                    dialog.uiSetContentText(rt_msg);
                    dialog.uiSetConfirmText(getString(R.string.confirm));
                    dialog.show();
                }
            });
        }

        @Override
        public void onDisConnSocialConnSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void onDisConnSocialConnError(String rt_code, String rt_msg) {

        }

        @Override
        public void showProgress() {

        }

        @Override
        public void hideProgress() {

        }

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
            isProcessWSErrorCodeByOtherActivity(rt_code, rt_msg);
        }
    };

    /* RequestCode for sign-in */
    private static final int GPLUS_SIGN_IN  = 9002;
    private static final int FB_SIGN_IN     = 8002;

    private GooglePlusLoginApi  m_GPlusApi;
    private FacebookLoginApi    m_FBApi;

    public NavigationBar    m_Navigationbar     = null;
    public FrameLayout      m_flayout_Content   = null;

    private Bitmap          m_bitmap            = null;
    private RelativeLayout  m_rlBg              = null;

    private FrameLayout     m_flSocialNetworkView = null;

    //目前尚在測試中！by ryan
    //private CIProgressDialog m_proDlg = null;

    private String m_strSocialType = null;
    private String m_strSocialMail = null;
    private String m_strSocialOpenId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        m_FBApi = new FacebookLoginApi(CISocialNetworkDetailActivity.this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initialLayoutComponent() {

        m_GPlusApi = new GooglePlusLoginApi();
        m_GPlusApi.createConnection(CISocialNetworkDetailActivity.this);
        m_GPlusApi.setUserLoginCallbackListener(CISocialNetworkDetailActivity.this);

        m_FBApi.setCallbackManager();
        m_FBApi.setUserCallbackListener(CISocialNetworkDetailActivity.this);

        m_Navigationbar     = (NavigationBar) findViewById(R.id.toolbar);
        m_flayout_Content   = (FrameLayout) findViewById(R.id.container);

        m_rlBg              = (RelativeLayout) findViewById(R.id.rl_bg);
        m_bitmap            = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
        Drawable drawable   = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        m_rlBg.setBackground(drawable);

        View ViewContent = View.inflate(this, R.layout.fragment_social_network_detail, null);
        m_flayout_Content.addView(ViewContent);

        m_flSocialNetworkView = (FrameLayout) ViewContent.findViewById(R.id.fl_social_network);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_flayout_Content);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);

        CIPersonalSocialNetworkView socialNetworkView = new CIPersonalSocialNetworkView(m_Context);

        String strFbEmail = "";
        String strGoogleEmail = "";

        if (true == CIApplication.getLoginInfo().GetFbCombineStatus())
            strFbEmail = CIApplication.getLoginInfo().GetFbEmail();

        if (true == CIApplication.getLoginInfo().GetGoogleCombineStatus())
            strGoogleEmail = CIApplication.getLoginInfo().GetGoogleEmail();

        socialNetworkView.setEdit(strFbEmail, strGoogleEmail);
        socialNetworkView.uiSetOnParameterAndListener(m_OnPersonalSocialNetworkViewListener);
        m_flSocialNetworkView.addView(socialNetworkView);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    protected void onLanguageChangeUpdateUI() {}

    @Override
    protected void onDestroy() {
        if (null != m_rlBg) {
            m_rlBg.setBackground(null);
        }
        if (null != m_bitmap) {
            ImageHandle.recycleBitmap(m_bitmap);
        }
        CIConnSocialNetworkPresenter.getInstance(null);
        System.gc();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

       SLog.i(AppInfo.APP_LOG_TAG, "onActivityResult" + requestCode + "&" + responseCode);

        if ( requestCode == UiMessageDef.REQUEST_CODE_SOCIAL_NETWORK_DISCONNECT_TAG &&
                responseCode == UiMessageDef.RESULT_CODE_SOCIAL_NETWORK_DISCONNECT_OK_TAG ) {
            setResult( UiMessageDef.RESULT_CODE_SOCIAL_NETWORK_DISCONNECT_OK_TAG );

            finish();
        } else if (requestCode == GPLUS_SIGN_IN) {

            m_GPlusApi.onActivityResult(requestCode, responseCode, data);
        } else {

            m_FBApi.getCallbackManager().onActivityResult(requestCode, responseCode, data);

        }
    }

    //google/FB登入成功的callback
    @Override
    public void onUserConnected(int requestIdentifier, String strId, String strMail) {
        String strType = UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK;
        switch (requestIdentifier) {
            case GPLUS_SIGN_IN:
//                CIApplication.getLoginInfo().SetGoogleCombineStatus(true);
//                CIApplication.getLoginInfo().SetGoogleEmail(strMail);
//                CIApplication.getLoginInfo().SetGoogleLoginId(strId);
//                m_GPlusApi.GoogleSignOut();
                strType = UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE;
               SLog.i("Google+", "connected");
                break;
            case FB_SIGN_IN:
//                CIApplication.getLoginInfo().SetFbCombineStatus(true);
//                CIApplication.getLoginInfo().SetFbEmail(strMail);
//                CIApplication.getLoginInfo().SetGoogleLoginId(strId);
                m_FBApi.signOut();
                strType = UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK;
               SLog.i("FB", "connected");
                break;
        }

        //m_proDlg = CIProgressDialog.createDialog(m_Context);
        //m_proDlg.show();
        showProgressDialog();

        //暫存Login 資料
        m_strSocialType = strType;
        m_strSocialMail = strMail;
        m_strSocialOpenId = strId;

        //2016-05-16 ryan for 調整呼叫方式
//        CIConnSocialNetworkPresenter connSocialNetworkPresenter
//                = new CIConnSocialNetworkPresenter(m_ConnSocialNetworkListener);
        CIConnSocialNetworkPresenter.getInstance(m_ConnSocialNetworkListener).ConnSocialNetworkFromWS(
                CIApplication.getLoginInfo().GetUserMemberCardNo(),
                strId,
                strType,
                strMail);
    }

    //google/FB登入失敗的callback
    @Override
    public void onConnectionError(int requestIdentifier, String message) {

        String strMsg = "";
        switch (requestIdentifier) {
            case GPLUS_SIGN_IN:
                strMsg = getString(R.string.google_plus);
               SLog.i("Google+", "onConnectionError" + message);
                break;
            case FB_SIGN_IN:
                strMsg = getString(R.string.facebook);
               SLog.i("FB", "onConnectionError" + message);
                break;
        }

        CIAlertDialog dialog = new CIAlertDialog(m_Context, new CIAlertDialog.OnAlertMsgDialogListener() {
            @Override
            public void onAlertMsgDialog_Confirm() {}

            @Override
            public void onAlertMsgDialogg_Cancel() {}
        });

        dialog.uiSetTitleText(getString(R.string.warning));
        dialog.uiSetContentText(strMsg + getString(R.string.menu_log_in) + "error");
        dialog.uiSetConfirmText(getString(R.string.confirm));
        dialog.show();
    }

    //google/FB登入取消的callback
    @Override
    public void onCancelled(int requestIdentifier, String message) {

        String strMsg = "";
        switch (requestIdentifier) {
            case GPLUS_SIGN_IN:
                strMsg = getString(R.string.google_plus);
               SLog.i("Google+", "onCancelled:" + message);
                break;
            case FB_SIGN_IN:
                strMsg = getString(R.string.facebook);
               SLog.i("FB", "onCancelled:" + message);
                break;
        }

        CIAlertDialog dialog = new CIAlertDialog(m_Context, new CIAlertDialog.OnAlertMsgDialogListener() {
            @Override
            public void onAlertMsgDialog_Confirm() {}

            @Override
            public void onAlertMsgDialogg_Cancel() {}
        });

        dialog.uiSetTitleText(getString(R.string.warning));
        dialog.uiSetContentText(strMsg + getString(R.string.menu_log_in) + getString(R.string.cancel));
        dialog.uiSetConfirmText(getString(R.string.confirm));
        dialog.show();
    }
}
