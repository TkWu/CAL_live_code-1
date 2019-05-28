package ci.function.Login;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import org.json.JSONException;
import org.json.JSONObject;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIProgressDialog;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CILoginReq;
import ci.ws.Models.entities.CILoginResp;
import ci.ws.Presenter.CILoginWSPresenter;
import ci.ws.Presenter.CIUpdateEmailByMemberNoWSPresenter;
import ci.ws.Presenter.Listener.CILoginWSListener;
import ci.ws.Presenter.Listener.CIUpdateEmailByMemberNoWSListener;

import static ci.function.Login.CIInputDetailEmailFragment.OnInputDetailEmailFragmentListener;
import static ci.function.Login.CIInputDetailEmailFragment.OnInputDetailEmailFragmentParameter;
import static ci.function.Login.CIInputDetailIdentityFragment.OnInputDetailIdentityFragmentListener;
import static ci.function.Login.CIInputDetailPhoneFragment.OnInputDetailPhoneFragmentListener;
import static ci.function.Login.CIInputDetailPhoneFragment.OnInputDetailPhoneFragmentParameter;
import static ci.ui.TextField.CIAccountTextFieldFragment.Type;
import static ci.ui.TextField.CIAccountTextFieldFragment.Type.MOBILE_NO;
import static ci.ui.TextField.CIAccountTextFieldFragment.Type.valueOf;
import static ci.ui.view.NavigationBar.onNavigationbarListener;
import static ci.ui.view.NavigationBar.onNavigationbarParameter;

/**
 * 該email/phone找到多個卡號
 * 1.需輸入身分證字號or護照號碼(目前因為還沒有string及設計稿,所以尚未完成)
 * 2.需輸入新email/phone
 * zeplin: 3.3-3 M2, M3
 * wireframe: p.21
 * Created by jlchen on 2016/2/19.
 */
public class CIInputDetailActivity extends BaseActivity{

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {
        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.input_detail_title);
        }
    };

    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {
        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            CIInputDetailActivity.this.finish();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    private OnInputDetailIdentityFragmentListener m_OnInputDetailIdentityFragmentListener = new OnInputDetailIdentityFragmentListener() {
        @Override
        public void OnSendClick(String strIdentity) {
            //輸入Identity/Passport Id後, 使用Email/Mobile取得卡號
            CILoginWSPresenter.getInstance(m_LoginWsListener).InquiryCardNoByIdentityFromWS(
                    m_strUserData, strIdentity);
        }
    };

    private OnInputDetailEmailFragmentListener m_OnInputDetailEmailFragmentListener = new OnInputDetailEmailFragmentListener() {
        @Override
        public void OnSendClick(String strEmail) {
            //更新Email
            m_updateEmailByMemberNoWSPresenter.UpdateEmailByMemberNoFromWS(m_strUserCardNo, strEmail);
        }
    };

    private OnInputDetailEmailFragmentParameter m_OnInputDetailEmailFragmentParameter = new OnInputDetailEmailFragmentParameter() {
        @Override
        public String getUserName() {
            if ( null != m_strUserName)
                return m_strUserName;
            return "";
        }

        @Override
        public String getUserData() {
            if ( null != m_strUserData)
                return m_strUserData;
            return "";
        }
    };

    private OnInputDetailPhoneFragmentListener m_OnInputDetailPhoneFragmentListener = new OnInputDetailPhoneFragmentListener() {
        @Override
        public void OnSendClick(String strCountry, String strPhone) {
            //更新Mobile No.
            m_updateEmailByMemberNoWSPresenter.UpdatePhoenByMemberNoFromWS(m_strUserCardNo, strCountry, strPhone);
        }
    };

    private OnInputDetailPhoneFragmentParameter m_OnInputDetailPhoneFragmentParameter = new OnInputDetailPhoneFragmentParameter() {
        @Override
        public String getUserName() {
            if ( null != m_strUserName)
                return m_strUserName;
            return "";
        }

        @Override
        public String getUserData() {
            if ( null != m_strUserData)
                return m_strUserData;
            return "";
        }
    };

    private CILoginWSListener m_LoginWsListener = new CILoginWSListener() {
        @Override
        public void onLoginSuccess(String rt_code, String rt_msg, CILoginResp loginResp) {
            m_LoginResp = loginResp;

            m_strUserName = loginResp.first_name + " " + loginResp.last_name;
            m_strUserCardNo = loginResp.card_no;

            switch (m_Type) {
                case MOBILE_NO:
                    m_phoneFragment = new CIInputDetailPhoneFragment();
                    m_phoneFragment.uiSetParameterListener(
                            m_OnInputDetailPhoneFragmentListener,
                            m_OnInputDetailPhoneFragmentParameter);
                    ChangeFragment(m_phoneFragment);
                    break;
                case EMAIL:
                    m_emailFragment = new CIInputDetailEmailFragment();
                    m_emailFragment.uiSetParameterListener(
                            m_OnInputDetailEmailFragmentListener,
                            m_OnInputDetailEmailFragmentParameter);
                    ChangeFragment(m_emailFragment);
                    break;
            }
        }

        @Override
        public void onLoginError(final String rt_code, final String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void onInquirySuccess(String rt_code, String rt_msg, final String strCard_no, final String password) {
            DoLogin(strCard_no, password);
        }

        @Override
        public void onInquiryError(final String rt_code, final String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void onSocialLoginSuccess(String rt_code, String rt_msg, CILoginResp socialLoginResp) {}

        @Override
        public void onSocialLoginError(String rt_code, String rt_msg) {}

        @Override
        public void showProgress() {
            showProgressDialog(new CIProgressDialog.CIProgressDlgListener() {
                @Override
                public void onBackPressed() {
                    CILoginWSPresenter.getInstance(m_LoginWsListener).InquiryCardNoCancel();
                    CILoginWSPresenter.getInstance(m_LoginWsListener).LoginCancel();
                }
            });
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    private CIUpdateEmailByMemberNoWSListener m_UpdateEmailByMemberNoWSListener = new CIUpdateEmailByMemberNoWSListener() {
        @Override
        public void onUpdateSuccess(String rt_code, final String rt_msg) {
            //寫入登入狀態
            CIApplication.getLoginInfo().SetLoginData(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER, m_LoginResp);

            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm),
                    null,
                    new CIAlertDialog.OnAlertMsgDialogListener() {
                        @Override
                        public void onAlertMsgDialog_Confirm() {
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onAlertMsgDialogg_Cancel() {}
                    });
        }

        @Override
        public void onUpdateError(String rt_code, String rt_msg) {

            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void showProgress() {
            showProgressDialog(new CIProgressDialog.CIProgressDlgListener() {
                @Override
                public void onBackPressed() {
                    m_updateEmailByMemberNoWSPresenter.CancelUpdateEmailyPhone();
                }
            });
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
            isProcessWSErrorCodeByOtherActivity(rt_code, rt_msg);
        }
    };

    private CIUpdateEmailByMemberNoWSPresenter m_updateEmailByMemberNoWSPresenter;

    private Type            m_Type              = MOBILE_NO;
    private String          m_strUserName       = null;
    private String          m_strUserData       = null;
    private String          m_strUserCardNo     = null;
    private boolean         m_bIsSocialCombine  = false;

    private Bitmap          m_bitmap            = null;
    private RelativeLayout  m_rlBg              = null;
    private NavigationBar   m_Navigationbar     = null;
    private FrameLayout     m_flContent         = null;

    private CIInputDetailIdentityFragment   m_identityFragment  = null;
    private CIInputDetailEmailFragment      m_emailFragment     = null;
    private CIInputDetailPhoneFragment      m_phoneFragment     = null;

    private CILoginResp         m_LoginResp     = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String strType = getIntent().getStringExtra(UiMessageDef.BUNDLE_INPUT_DETAIL_TYPE_TAG);
        if(null != strType){
            m_Type = valueOf(strType);
        }

        m_strUserName = getIntent().getStringExtra(UiMessageDef.BUNDLE_LOGIN_USERNAME_TAG);
        m_strUserData = getIntent().getStringExtra(UiMessageDef.BUNDLE_LOGIN_ACCOUNT_TAG);
        m_bIsSocialCombine = getIntent().getBooleanExtra(UiMessageDef.BUNDLE_IS_SOCIAL_COMBINE_TAG, false);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initialLayoutComponent() {
        m_rlBg              = (RelativeLayout) findViewById(R.id.rl_bg);
        m_bitmap            = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
        Drawable drawable   = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        m_rlBg.setBackground(drawable);

        m_Navigationbar     = (NavigationBar) findViewById(R.id.toolbar);
        m_flContent         = (FrameLayout) findViewById(R.id.container);

        m_updateEmailByMemberNoWSPresenter = new CIUpdateEmailByMemberNoWSPresenter(m_UpdateEmailByMemberNoWSListener);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {}

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        m_identityFragment = new CIInputDetailIdentityFragment();
        m_identityFragment.uiSetParameterListener(m_OnInputDetailIdentityFragmentListener);
        ChangeFragment(m_identityFragment);
    }

    protected void ChangeFragment(Fragment fragment) {

        // 開始Fragment的事務Transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (fragment != m_identityFragment){
            // 設置轉換效果
            fragmentTransaction.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        // 替换容器(container)原来的Fragment
        fragmentTransaction.replace(m_flContent.getId(),
                fragment,
                fragment.getTag());

        // 提交事務
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    protected void onLanguageChangeUpdateUI() {}

    @Override
    public void onBackPressed() {
        CILoginWSPresenter.getInstance(m_LoginWsListener).InquiryCardNoCancel();
        CILoginWSPresenter.getInstance(m_LoginWsListener).LoginCancel();
        m_updateEmailByMemberNoWSPresenter.CancelUpdateEmailyPhone();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (null != m_rlBg) {
            m_rlBg.setBackground(null);
        }
        if (null != m_bitmap) {
            ImageHandle.recycleBitmap(m_bitmap);
        }
        System.gc();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    private void DoLogin(String strCard_no, String password){
        //取得卡號後作背景登入
        String strCard = "";
        try {
            JSONObject jsonObject = new JSONObject(strCard_no);
            strCard = jsonObject.optString("cardNo");
        } catch (JSONException e) {
        }

        CILoginReq loginReq = new CILoginReq();
        loginReq.user_id = strCard;
        loginReq.password = password;
        //如果已使用社群帳號登入 需進行綁定
        if ( true == m_bIsSocialCombine ){
            loginReq.is_social_combine = CILoginReq.SOCIAL_COMBINE_YES;
            loginReq.social_id = CIApplication.getLoginInfo().GetSocialLoginId();
            loginReq.social_vendor = CIApplication.getLoginInfo().GetLoginType();
            loginReq.social_email = CIApplication.getLoginInfo().GetUserEmail();
        }

        CILoginWSPresenter.getInstance(m_LoginWsListener).LoginWithCombineSocialFromWS(loginReq);
    }
}
