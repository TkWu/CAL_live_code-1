package ci.function.Signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import ci.function.Core.SLog;
import android.view.View;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Login.CILoginActivity;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;


public class CIBecomeDynastyFlyerActivity extends BaseActivity
        implements View.OnClickListener{

    private NavigationBar m_Navigationbar = null;
    private EMode         m_EMode         = EMode.BECOME_DYNASTY_FLYER;
    private final int     REQUEST_CODE    = 1000;
    private TextView      m_tvName        = null;
    private int           m_iViewId       = 0;
    private String        m_strUserName   = null;

    public enum EMode {
        BECOME_DYNASTY_FLYER, TEMPORARY_MEMBER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m_strUserName = getIntent().getStringExtra(UiMessageDef.BUNDLE_NOT_LOGIN_USERNAME_TAG);
        String name = getIntent().getStringExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE);
        m_iViewId =   getIntent().getIntExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, 0);
        if (null != name) {
            m_EMode = EMode.valueOf(name);
        }
        super.onCreate(savedInstanceState);
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.become_a_dynasty_flyer_title);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}
        @Override
        public void onLeftMenuClick() {}
        @Override
        public void onBackClick() {
            onBackPressed();
        }
        @Override
        public void onDeleteClick() {}
        @Override
        public void onDemoModeClick() {}
    };

    @Override
    public void onBackPressed() {
        CIBecomeDynastyFlyerActivity.this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_become_dynasty_flyer;
    }

    @Override
    protected void initialLayoutComponent() {
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        switch (m_EMode){
            case BECOME_DYNASTY_FLYER:
                findViewById(R.id.btn_become_dynasty_flyer_sign_up).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_become_dynasty_flyer_ask).setVisibility(View.GONE);
                break;
            case TEMPORARY_MEMBER:
                findViewById(R.id.btn_become_dynasty_flyer_sign_up).setVisibility(View.GONE);
                findViewById(R.id.ll_become_dynasty_flyer_ask).setVisibility(View.VISIBLE);
                break;
        }

        m_tvName = (TextView) findViewById(R.id.tv_become_dynasty_flyer_member_name);

        String strName = CIApplication.getLoginInfo().GetUserName();
        if ( null != strName && 0 < strName.length() ){
            m_tvName.setText(strName);
        }else {
            if ( null != m_strUserName ){
                m_tvName.setText(m_strUserName);
            }else {
               SLog.e("BecomeDynastyFlyer", "Get Not User Name");
            }
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.become_dynasty_flyer_img), 160, 110);
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.ibtn_login), 16, 16);
        vScaleDef.setTextSize(18, (TextView)findViewById(R.id.tv_become_dynasty_flyer_member_name));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        findViewById(R.id.btn_become_dynasty_flyer_sign_up).setOnClickListener(this);
        findViewById(R.id.btn_become_dynasty_flyer_ok).setOnClickListener(this);
        findViewById(R.id.btn_become_dynasty_flyer_cancel).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.ibtn_login).setOnClickListener(this);
    }

    @Override
    protected void registerFragment(android.support.v4.app.FragmentManager fragmentManager) {

    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {

    }

    @Override
    protected void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_become_dynasty_flyer_sign_up:
                changeActivityForResult(CISignUpActivity.class);
                break;
            case R.id.btn_login:
            case R.id.ibtn_login:
                changeActivityForResult(CILoginActivity.class);
                break;
            case R.id.btn_become_dynasty_flyer_ok:
                //進入加入會員頁面
                changeActivityForResult(CISignUpActivity.class);
                break;
            case R.id.btn_become_dynasty_flyer_cancel:
                //返回首頁
                finish();
                overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && RESULT_OK == resultCode){
            if(null == data){
                data = new Intent();
            }
            data.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, m_iViewId);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void changeActivity(Class clazz){
        Intent intent = new Intent();
        intent.setClass(m_Context, clazz);
        startActivity(intent);

        //測試切換activity滑入滑出動畫
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private void changeActivityForResult(Class clazz){
        Intent intent = new Intent();
        intent.setClass(m_Context, clazz);
        intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, m_iViewId);
        //已社群登入 告訴sign up或login 需進行綁定
        boolean bIsSocialCombine = false;

        if ( true == CIApplication.getLoginInfo().GetLoginStatus()
                && !TextUtils.isEmpty(CIApplication.getLoginInfo().GetLoginType())
                && !CIApplication.getLoginInfo().GetLoginType().equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)
                && !TextUtils.isEmpty(CIApplication.getLoginInfo().GetSocialLoginId()) ){
            bIsSocialCombine = true;
        }

        intent.putExtra(UiMessageDef.BUNDLE_IS_SOCIAL_COMBINE_TAG, bIsSocialCombine);

        startActivityForResult(intent, REQUEST_CODE);

        //測試切換activity滑入滑出動畫
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }
}
