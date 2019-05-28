package ci.function.Signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CISignUpReq;
import ci.ws.Models.entities.CISignUpResp;
import ci.ws.Presenter.CISignUpWSPresenter;
import ci.ws.Presenter.Listener.CISignUpWSListener;
import ci.ws.cores.object.GsonTool;


public class CISignUpAskChangeEmailActivity extends BaseActivity
        implements View.OnClickListener{

    private NavigationBar       m_Navigationbar  = null;
    private CISignUpReq         m_reqData        = null;
    private TextView            m_tvContent      = null;
    private Button              m_btnOk          = null;
    private Button              m_btnCancel      = null;
    private int                 m_iViewId        = 0;

    private TextView            m_tvName         = null;
    private String              m_strName        = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if(null != bundle){
            String reqData = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA);
            if(false == TextUtils.isEmpty(reqData)){
                m_reqData = GsonTool.getGson().fromJson(reqData,CISignUpReq.class);
            }
            m_iViewId = bundle.getInt(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, 0);
            //取得前一頁帶來的名稱
            if(null != m_reqData){
                m_strName = String.format("%s %s", m_reqData.first_name, m_reqData.last_name);
            }
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
            return m_Context.getString(R.string.sign_up_title);
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
        CISignUpAskChangeEmailActivity.this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_sign_up_ask_change_email;
    }

    @Override
    protected void initialLayoutComponent() {
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_tvContent     = (TextView)      findViewById(R.id.tv_ask_change_email_content);
        m_btnOk         = (Button)        findViewById(R.id.btn_ask_change_email_ok) ;
        m_btnCancel     = (Button)        findViewById(R.id.btn_ask_change_email_cancel) ;
        //2016-11-01 Ryan, 需將前一頁的名字帶過來這頁顯示
        //
        m_tvName        = (TextView)      findViewById(R.id.tv_ask_change_email_name);
        m_tvName.setText(m_strName);
        //
        String str = getString(R.string.suggested_to_change_your_membership_mail);
        if(null != m_reqData){
            str = String.format(str,m_reqData.email,
                    m_reqData.social_email);
        }
        m_tvContent.setText(str);
    }



    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_ask_change_email_img), 160, 110);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_btnCancel.setOnClickListener(this);
        m_btnOk.setOnClickListener(this);
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
            case R.id.btn_ask_change_email_cancel:
                sendSignUpDataToWS(false);
                break;
            case R.id.btn_ask_change_email_ok:
                sendSignUpDataToWS(true);
                break;
        }
    }

    private void sendSignUpDataToWS(boolean isMailChange){
        CISignUpReq reqData = (CISignUpReq)m_reqData.clone();
        if(true == isMailChange){
            reqData.email = m_reqData.social_email;
        }
        showProgressDialog();

        CISignUpWSPresenter.getInstance(m_SignUpWSListener).SignUpFromWS(reqData);
    }



    private CISignUpWSListener m_SignUpWSListener = new CISignUpWSListener(){

        @Override
        public void onSignUpSuccess(final String rt_code, final String rt_msg, final CISignUpResp SignupResp) {

            CIAlertDialog.OnAlertMsgDialogListener listener = new CIAlertDialog.OnAlertMsgDialogListener() {
                @Override
                public void onAlertMsgDialog_Confirm() {

                    Intent intent = new Intent();
                    if(null != m_reqData){
                        intent.putExtra(UiMessageDef.BUNDLE_LOGIN_USERNAME_TAG,
                                m_reqData.first_name + ", " + m_reqData.last_name);
                    }
                    intent.putExtra(UiMessageDef.BUNDLE_LOGIN_ACCOUNT_TAG, SignupResp.card_no);
                    intent.putExtra(UiMessageDef.BUNDLE_LOGIN_PASSWORD_TAG, m_reqData.password);

                    changeActivityForResult(CISignUpSuccessActivity.class, intent);

                    //測試切換activity滑入滑出動畫
                    overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                }

                @Override
                public void onAlertMsgDialogg_Cancel() {}
            };

            showDialog(getString(R.string.warning),
                    m_reqData.first_name + " " + m_reqData.last_name + "\n註冊成功",
                    getString(R.string.confirm),
                    null,
                    listener);
        }

        @Override
        public void onSignUpError(final String rt_code, final String rt_msg) {

            showDialog(getString(R.string.warning),
                    rt_msg);
            m_btnOk.setEnabled(false);
            m_btnCancel.setEnabled(false);
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
            m_btnOk.setEnabled(true);
            m_btnCancel.setEnabled(true);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == UiMessageDef.REQUEST_CODE_SIGN_UP){
            setResult(RESULT_OK);
            finish();
        }

    }

    private void changeActivityForResult(Class clazz,Intent intent){
        if(null == intent){
            intent = new Intent();
        }
        intent.setClass(m_Context, clazz);
        intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, m_iViewId);
        startActivityForResult(intent, UiMessageDef.REQUEST_CODE_SIGN_UP);

        //測試切換activity滑入滑出動畫
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CISignUpWSPresenter.getInstance(null);
    }
}
