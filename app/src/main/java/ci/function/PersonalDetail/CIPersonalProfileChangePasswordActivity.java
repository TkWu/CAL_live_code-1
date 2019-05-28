package ci.function.PersonalDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.TextField.Base.CIBaseTextFieldFragment;
import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.CIPasswordTextFieldFragment;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ws.Presenter.CIChangePasswordPresenter;
import ci.ws.Presenter.Listener.CIChangePasswordListener;

/**
 * Created by kevincheng on 2016/3/23.
 */
public class CIPersonalProfileChangePasswordActivity extends BaseActivity
    implements TextView.OnClickListener{
    private NavigationBar       m_Navigationbar                     = null;
    private CIPasswordTextFieldFragment m_oldPassword                 = null;
    private CIPasswordTextFieldFragment m_newPassword                 = null;
    private CIPasswordTextFieldFragment m_reEnterNewPassword          = null;
    private boolean                   m_bIsPasswordSame             = false;
    private String                      m_errorMsg                  = null;

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.change_password);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {
        }

        @Override
        public void onLeftMenuClick() {
        }

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {

        }
        @Override
        public void onDemoModeClick() {}
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_personal_profile_change_password;
    }

    @Override
    protected void initialLayoutComponent() {
        m_Navigationbar = (NavigationBar)findViewById(R.id.toolbar);
        findViewById(R.id.btn_send).setOnClickListener(this);

        findViewById(R.id.root).setOnTouchListener(m_onTouchListener);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        ((TextView) findViewById(R.id.tv_prompt_msg)).setLineSpacing(vScaleDef.getLayoutHeight(6.7f), 1.0f);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        m_oldPassword = CIPasswordTextFieldFragment.newInstance(getString(R.string.old_password));
        m_newPassword = CIPasswordTextFieldFragment.newInstance(getString(R.string.new_password));
        m_reEnterNewPassword = CIPasswordTextFieldFragment.newInstance(getString(R.string.re_enter_new_password));
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_old_password,m_oldPassword);
        transaction.replace(R.id.fl_new_password,m_newPassword);
        transaction.replace(R.id.fl_re_enter_new_password,m_reEnterNewPassword);
        transaction.commit();
        m_newPassword.setAfterTextChangedListener(passwordListener);
        m_reEnterNewPassword.setAfterTextChangedListener(passwordListener);
    }

    CITextFieldFragment.afterTextChangedListener passwordListener = new CIBaseTextFieldFragment.afterTextChangedListener() {
        @Override
        public void afterTextChangedListener(Editable editable) {
            if (null != m_newPassword.getText()
                    && 0 < m_newPassword.getText().length()
                    && null != m_reEnterNewPassword.getText()
                    && 0 < m_reEnterNewPassword.getText().length()) {
                String pw = m_newPassword.getText();
                String cpw = m_reEnterNewPassword.getText();
                m_bIsPasswordSame = pw.equals(cpw);
                m_reEnterNewPassword.setErrorMsg(getString(R.string.passwords_are_different));
                if (true == m_bIsPasswordSame) {
                    m_reEnterNewPassword.setIsFormatCorrect(true);
                    if (false == m_reEnterNewPassword.isFocused()) {
                        m_reEnterNewPassword.hideError();
                    }
                } else {
                    m_reEnterNewPassword.setIsFormatCorrect(false);
                    if (false == m_reEnterNewPassword.isFocused()) {
                        m_reEnterNewPassword.showError();
                    }
                }
            } else {
                m_reEnterNewPassword.setIsFormatCorrect(true);
                m_reEnterNewPassword.hideError();
            }
        }
    };

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {

    }

    @Override
    protected void onLanguageChangeUpdateUI() {

    }

    @Override
    public void onClick(View v) {
        if(R.id.btn_send == v.getId()){

            if( false == isFillCompleteAndCorrect() ) {
                showDialog(getString(R.string.warning),
                        m_errorMsg);
            } else {
                sendChangePasswordFromWS();

            }
        }
    }

    private void sendChangePasswordFromWS() {
        String strOldPwd = m_oldPassword.getText();
        String strNewPwd = m_newPassword.getText();

        CIChangePasswordPresenter.getInstance(m_changePasswordListener).ChangePasswordFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(), strOldPwd, strNewPwd);
    }

    private CIChangePasswordListener m_changePasswordListener = new CIChangePasswordListener() {
        @Override
        public void onChangePasswordSuccess(String rt_code, String rt_msg) {

            Intent intent = new Intent();
            intent.setClass(CIPersonalProfileChangePasswordActivity.this, CIPersonalProfileChangePasswordSuccessActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

//            CIAlertDialog.OnAlertMsgDialogListener listner = new CIAlertDialog.OnAlertMsgDialogListener() {
//                @Override
//                public void onAlertMsgDialog_Confirm() {
//                    Intent intent = new Intent();
//                    intent.setClass(CIPersonalProfileChangePasswordActivity.this, CIPersonalProfileChangePasswordSuccessActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
//                }
//
//                @Override
//                public void onAlertMsgDialogg_Cancel() {}
//            };
//
//        showDialog(getString(R.string.warning),rt_msg, getString(R.string.confirm), null,listner);


        }

        @Override
        public void onChangePasswordError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg);
        }

        @Override
        public void showProgress() {
            showProgressDialog();
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

    private boolean isFillCompleteAndCorrect() {

        String strOldPwd = m_oldPassword.getText();
        boolean bOldCorrect   = m_oldPassword.getIsFormatCorrect();

        String strNewPwd = m_newPassword.getText();
        boolean bNewCorrect   = m_newPassword.getIsFormatCorrect();
        String strReEnterPwd = m_reEnterNewPassword.getText();
        boolean bReEnterCorrect   = m_reEnterNewPassword.getIsFormatCorrect();

        /**
         * 判斷相關欄位是否有格式錯誤
         * */

        if( TextUtils.isEmpty(strOldPwd) ) {
            m_errorMsg = String.format(getString(R.string.please_input_field), getString(R.string.old_password).replace("*","") );
            return false;
        } else if( false == bOldCorrect ) {
            m_errorMsg = getString(R.string.member_login_input_correvt_format_msg);
            return false;
        }

        if( TextUtils.isEmpty(strNewPwd) ) {
            m_errorMsg = String.format(getString(R.string.please_input_field), getString(R.string.new_password).replace("*", ""));
            return false;
        } else if( false == bNewCorrect ) {
            m_errorMsg = getString(R.string.member_login_input_correvt_format_msg);
            return false;
        }

        if( TextUtils.isEmpty(strReEnterPwd) ) {
            m_errorMsg = String.format(getString(R.string.please_input_field), getString(R.string.re_enter_new_password).replace("*",""));
            return false;
        }

        /**
         * 判斷密碼是否不一致
         * */
        if( !strNewPwd.equals(strReEnterPwd) ) {
            m_errorMsg = getString(R.string.passwords_are_different);
            return false;
        }

        return true;
    }

    /**
     * 點擊畫面外圍，關閉軟體鍵盤
     */
    private View.OnTouchListener m_onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            hideSoftKeyboard(v);
            return false;
        }
    };

    /**
     * 關閉軟體鍵盤
     * @param v
     */
    private void hideSoftKeyboard(View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}
