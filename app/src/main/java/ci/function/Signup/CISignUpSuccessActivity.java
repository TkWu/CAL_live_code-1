package ci.function.Signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewIdDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CILoginReq;
import ci.ws.Models.entities.CILoginResp;
import ci.ws.Presenter.CILoginWSPresenter;
import ci.ws.Presenter.Listener.CILoginWSListener;

/**
 * Created by jlchen on 2016/2/19.
 */
public class CISignUpSuccessActivity extends BaseActivity implements View.OnClickListener {

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.sign_up_title);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {}

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    private CILoginWSListener m_LoginWsListener = new CILoginWSListener() {
        @Override
        public void onLoginSuccess(String rt_code, String rt_msg, final CILoginResp loginResp) {

            //寫入登入狀態
            CIApplication.getLoginInfo().SetLoginStatus(true);
            CIApplication.getLoginInfo()
                    .SetLoginData(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER, loginResp);
        }

        @Override
        public void onLoginError(final String rt_code, final String rt_msg) {
            CIAlertDialog.OnAlertMsgDialogListener listener = new CIAlertDialog.OnAlertMsgDialogListener() {
                @Override
                public void onAlertMsgDialog_Confirm() {}

                @Override
                public void onAlertMsgDialogg_Cancel() {}
            };
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm),
                    null,
                    listener);
        }

        @Override
        public void onInquirySuccess(String rt_code, String rt_msg, String strCard_no, String strPassword) {}

        @Override
        public void onInquiryError(String rt_code, String rt_msg) {}

        @Override
        public void onSocialLoginSuccess(String rt_code, String rt_msg, CILoginResp socialLoginResp) {}

        @Override
        public void onSocialLoginError(String rt_code, String rt_msg) {}

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    @Override
    public void onBackPressed() {
        //do Nothing
    }

    private NavigationBar   m_Navigationbar = null;

    private TextView        m_tvCardNo      = null;
    private TextView        m_tvCardMsg     = null;

    private int m_iViewId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if(null != bundle){
            m_iViewId = bundle.getInt(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, 0);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_sign_up_success;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar     = (NavigationBar) findViewById(R.id.toolbar);
        TextView tvBackHome = (TextView) findViewById(R.id.btn_signup_back_home);

        m_tvCardNo          = (TextView) findViewById(R.id.tv_card_no);
        m_tvCardMsg         = (TextView) findViewById(R.id.tv_card_msg);

        switch (m_iViewId) {
            case ViewIdDef.VIEW_ID_MY_TRIPS:
                tvBackHome.setText(getString(R.string.back_to_mytrips));
                break;
            case ViewIdDef.VIEW_ID_MANAGE_MILES:
                tvBackHome.setText(getString(R.string.back_to_manage_miles));
                break;
            case ViewIdDef.VIEW_ID_BOARDINGPASS_EWALLET:
                tvBackHome.setText(getString(R.string.back_to_boarding_pass));
                break;
            case ViewIdDef.VIEW_ID_PERSONAL_DETAIL:
                tvBackHome.setText(getString(R.string.back_to_personal_detail));
                break;
            case ViewIdDef.VIEW_ID_NONE:
                tvBackHome.setText(getString(R.string.back_to_home));
                break;
        }

        String strName          = getIntent().getStringExtra(UiMessageDef.BUNDLE_LOGIN_USERNAME_TAG);
        String strAcct          = getIntent().getStringExtra(UiMessageDef.BUNDLE_LOGIN_ACCOUNT_TAG);
        String strPw            = getIntent().getStringExtra(UiMessageDef.BUNDLE_LOGIN_PASSWORD_TAG);

        m_tvCardNo.setText(strAcct);
        m_tvCardMsg.setText(strName + "\n" + strAcct);


        if (!TextUtils.isEmpty(strAcct) && !TextUtils.isEmpty(strPw)) {

            CILoginReq req = new CILoginReq();
            req.user_id = strAcct;
            req.password = strPw;

            //註冊完成自動幫用戶登入系統
            CILoginWSPresenter.getInstance(m_LoginWsListener).LoginWithCombineSocialFromWS(req);
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        int iLineSpacing = vScaleDef.getLayoutHeight(3);
        ((TextView) findViewById(R.id.tv_sign_up_success_welcome)).setLineSpacing(iLineSpacing, 1);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl_dynasty_flyer_card);
        rl.getLayoutParams().width = vScaleDef.getTextSize(236f);
        rl.getLayoutParams().height = vScaleDef.getTextSize(148f);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        findViewById(R.id.btn_signup_back_home).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.img_back).setVisibility(View.GONE);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup_back_home:
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CILoginWSPresenter.getInstance(null);

    }
}
