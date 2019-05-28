package ci.function.Login;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;

import com.chinaairlines.mobile30.R;

import java.net.URLDecoder;
import java.net.URLEncoder;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.function.Main.CIMainActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIProfileEntity;
import ci.ws.Presenter.CIProfilePresenter;
import ci.ws.Presenter.Listener.CIInquiryProfileListener;
import ci.ws.define.CIWSResultCode;

public class CISchemeLoginActivity extends BaseActivity{

    private static final String DEF_SCHEME_FORMAT = "%s://?token=%s&cardno=%s";

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.scheme_auth_eshopping_title);
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


    private NavigationBar   m_Navigationbar = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_scheme_auth_login;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar         = (NavigationBar) findViewById(R.id.toolbar);

        Button btnCancel = (Button)findViewById(R.id.btn_cancel);
        if ( null != btnCancel ){
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCancel();
                }
            });
        }

        Button btnConfirm = (Button)findViewById(R.id.btn_confirm);
        if ( null != btnConfirm ){
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onConfirm();
                }
            });
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_Navigationbar.switchBackBtn(false);
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


    protected void onConfirm(){
        boolean isNetworkAvailable = CIApplication.getSysResourceManager().isNetworkAvailable();
        if( isNetworkAvailable && CIApplication.getLoginInfo().GetLoginStatus() ) {

            CIProfilePresenter.getInstance(m_inquiryProfileListener).InquiryProfileFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo());

        } else {
            onCancel();
        }
    }

    protected void onCancel(){

        try {

            String strScheme = String.format(DEF_SCHEME_FORMAT, getString(R.string.scheme_eshopping), "", URLEncoder.encode(CIApplication.getLoginInfo().GetUserMemberCardNo(), "UTF-8") );

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strScheme));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        CISchemeLoginActivity.this.finish();
    }

    protected void gotoeShopping( String strToken ){
        try {

            String strScheme = String.format(DEF_SCHEME_FORMAT, getString(R.string.scheme_eshopping), URLEncoder.encode(strToken, "UTF-8"), URLEncoder.encode(CIApplication.getLoginInfo().GetUserMemberCardNo(), "UTF-8") );

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strScheme));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        CISchemeLoginActivity.this.finish();
    }

    CIInquiryProfileListener m_inquiryProfileListener = new CIInquiryProfileListener(){

        @Override
        public void onInquiryProfileSuccess(String rt_code, String rt_msg, CIProfileEntity profile) {
            //更新成功代表Token 是有效的
            gotoeShopping(CIApplication.getLoginInfo().GetMemberToken());
        }

        @Override
        public void onInquiryProfileError(String rt_code, String rt_msg) {
            onCancel();
        }

        @Override
        public void onUpdateProfileSuccess(String rt_code, String rt_msg) {}

        @Override
        public void onUpdateProfileError(String rt_code, String rt_msg) {}

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

            //清空登入資料
            CIApplication.getLoginInfo().ClearLoginData();

            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm),
                    null,
                    new CIAlertDialog.OnAlertMsgDialogListener() {
                        @Override
                        public void onAlertMsgDialog_Confirm() {
                            onCancel();
                        }

                        @Override
                        public void onAlertMsgDialogg_Cancel() {}
                    });
        }
    };
}
