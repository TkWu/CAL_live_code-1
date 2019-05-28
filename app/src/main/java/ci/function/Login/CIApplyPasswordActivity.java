package ci.function.Login;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIProgressDialog;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Presenter.CIInquiryPresenter;
import ci.ws.Presenter.Listener.CIInquiryListener;

import static ci.function.Login.CIApplyPasswordFragment.onApplyPasswordListener;
import static ci.function.Login.CIForgetSuccessFragment.onForgetSuccessParameter;
import static ci.ui.view.NavigationBar.onNavigationbarListener;
import static ci.ui.view.NavigationBar.onNavigationbarParameter;

/**密碼申請畫面(因為還沒有string及設計稿,所以尚未完成)
 * zeplin: 3.6-1
 * wireframe: p.24
 * Created by jlchen on 2016/2/19.
 */
public class CIApplyPasswordActivity extends BaseActivity{

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.apply_password_inquiry);
        }
    };

    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            CIApplyPasswordActivity.this.finish();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };

    private onForgetSuccessParameter m_onForgetSuccessParameter = new onForgetSuccessParameter() {
        @Override
        public String GetForgetSuccessMsg() {
            return getString(R.string.inquiry_password_inquiry_successfully);
        }

        @Override
        public String GetButtonText() {
            return null;
        }
    };

    private onApplyPasswordListener m_onApplyPasswordListener = new onApplyPasswordListener() {
        @Override
        public void onSendClick(String strCardNo, String strFirstName, String strLastName, String strBirthDay) {

//            if ( 0 < strBirthDay.length() )
//                strBirthDay = AppInfo.getInstance(m_Context).ConvertDateFormat(strBirthDay);

            m_inquiryPresenter.ApplyPasswordFromWS( strCardNo, strFirstName, strLastName, strBirthDay );
        }
    };

    private CIInquiryListener m_InquiryListener = new CIInquiryListener() {
        @Override
        public void onInquiryCardNoSuccess(String rt_code, String rt_msg, String email) {}

        @Override
        public void onInquiryCardNoError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryPasswordSuccess(String rt_code, String rt_msg, String email) {}

        @Override
        public void onInquiryPasswordError(String rt_code, String rt_msg) {}

        @Override
        public void onApplyPasswordSuccess(String rt_code, String rt_msg, String email) {
            ChangeFragment(m_ForgetSuccessFragment);
        }

        @Override
        public void onApplyPasswordError(final String rt_code, final String rt_msg) {

            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void showProgress() {

            showProgressDialog(new CIProgressDialog.CIProgressDlgListener() {
                @Override
                public void onBackPressed() {

                    m_inquiryPresenter.ApplyPasswordCancel();
                }
            });
        }

        @Override
        public void hideProgress() {

            hideProgressDialog();
        }
    };

    private CIInquiryPresenter m_inquiryPresenter;

    private Bitmap          m_bitmap            = null;
    private RelativeLayout  m_rlBg              = null;

    private NavigationBar   m_Navigationbar     = null;
    private FrameLayout     m_flContent         = null;

    private CIForgetSuccessFragment m_ForgetSuccessFragment = null;

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

        m_Navigationbar		= (NavigationBar) findViewById(R.id.toolbar);
        m_flContent         = (FrameLayout) findViewById(R.id.container);

        m_inquiryPresenter  = CIInquiryPresenter.getInstance(m_InquiryListener);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {}

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_ForgetSuccessFragment = new CIForgetSuccessFragment();
        m_ForgetSuccessFragment.uiSetParameterListener(m_onForgetSuccessParameter);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        CIApplyPasswordFragment ApplyPasswordFragment = new CIApplyPasswordFragment();
        ApplyPasswordFragment.uiSetParameterListener(m_onApplyPasswordListener);
        ChangeFragment(ApplyPasswordFragment);
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    protected void onLanguageChangeUpdateUI() {}

    /** 顯示送出結果畫面*/
    protected void ChangeFragment(Fragment fragment) {

        // 開始Fragment的事務Transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (fragment == m_ForgetSuccessFragment){

            m_rlBg.setOnTouchListener(null);

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
    public void onBackPressed() {
        m_inquiryPresenter.ApplyPasswordCancel();

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        m_inquiryPresenter  = CIInquiryPresenter.getInstance(null);

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
}
