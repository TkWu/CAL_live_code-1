package ci.function.Login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Base64;
import ci.function.Core.SLog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Login.api.FacebookLoginApi;
import ci.function.Login.api.GooglePlusLoginApi;
import ci.function.Login.listener.SocialConnectListener;
import ci.function.Main.BaseActivity;
import ci.function.MyTrips.CIFindMyBookingFragment;
import ci.function.MyTrips.CIFindMyBookingNotesActivity;
import ci.function.Signup.CIBecomeDynastyFlyerActivity;
import ci.ui.TextField.CIAccountTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewIdDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.toast.CIToastView;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ui.view.TwoItemNavigationBar;
import ci.ws.Models.entities.CILoginReq;
import ci.ws.Models.entities.CILoginResp;
import ci.ws.Presenter.CILoginWSPresenter;
import ci.ws.Presenter.Listener.CILoginWSListener;
import ci.ws.define.CICardType;
import ci.ws.define.CIWSResultCode;
import ci.ws.define.SocialNetworkKind;

import static ci.function.Login.CILoginFragment.onLoginListener;
import static ci.function.MyTrips.CIFindMyBookingFragment.FindMyBookingType;
import static ci.ui.view.NavigationBar.onNavigationbarInterface;
import static ci.ui.view.NavigationBar.onNavigationbarListener;
import static ci.ui.view.NavigationBar.onNavigationbarParameter;
import static ci.ui.view.TwoItemNavigationBar.EInitItem.LEFT;
import static ci.ui.view.TwoItemNavigationBar.EInitItem.RIGHT;

/** By Ling
 * zeplin: 3.2-2 / 8.6-2 / 8.6-3
 * wireframe: p.20 / p.58
 *
 * 登入方式:
 * 1. 一般登入(華航會員/電子信箱/電話)
 * (!) 當使用 信箱 或 電話 登入, 卻沒找到對應的會員帳號時, 將show出 input detail頁面,
 *     需輸入身分證或passport id進行進一步的認證.
 * (!) 若認證成功時, 再show出 input detail頁面(輸入新電話or新信箱)
 * 2. 社群登入(FB/google+)
 *
 * 登入UI:
 * 1.
 * 2.
 * 3. 登入及取回行程兩項功能切換的頁面
 * 4. 取回行程頁面
 */
public class CILoginActivity extends BaseActivity
        implements SocialConnectListener, TwoItemNavigationBar.ItemClickListener {

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            if (m_iViewId == ViewIdDef.VIEW_ID_CHECK_IN) {
                return m_Context.getString(R.string.menu_title_check_in);
            }
            switch (m_mode) {
                case BASE:
                    return m_Context.getString(R.string.member_login_title);
                case FIND_MYBOOKING_MEMBER:
                case FIND_MYBOOKING_RETRIEVE:
                case FIND_MYBOOKING_ONLY_RETRIEVE:
                    return m_Context.getString(R.string.find_my_booking_title);
            }
            return null;
        }
    };

    //start of 644336 2019 2月3月 AI/行事曆/截圖/注意事項
//    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {
//
//        @Override
//        public void onRightMenuClick() {}
//
//        @Override
//        public void onLeftMenuClick() {}
//
//        @Override
//        public void onBackClick() {
//            CILoginActivity.this.finish();
//        }
//
//        @Override
//        public void onDeleteClick() {}
//
//        @Override
//        public void onDemoModeClick() {}
//    };

    private NavigationBar.AboutBtn m_onNavigationbarListener = new NavigationBar.AboutBtn() {

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

        @Override
        public void onAboutClick() {
            if (m_iViewId == ViewIdDef.VIEW_ID_CHECK_IN) {
                changeActivity(CIFindMyBookingNotesActivity.class, UiMessageDef.BUNDLE_CHKIN_FINDMYBOOKING_NOTES, "2");
            } else {
                changeActivity(CIFindMyBookingNotesActivity.class, UiMessageDef.BUNDLE_CHKIN_FINDMYBOOKING_NOTES, "1");
            }
        }
    };
    //end of 644336 2019 2月3月 AI/行事曆/截圖/注意事項

    private onLoginListener m_onLoginListener = new onLoginListener() {

        @Override
        public void onMemberLoginClick(String strAccout, String strPassword, boolean bKeep) {
//            if ( WSConfig.WS_TESTMODE ){

                //測試模式 by ryan for demo
//                if (strAccout.length() > 0 && "DEMO".equals(strAccout)) {
//
//                    AppInfo.getInstance(CIApplication.getContext()).SetDemoMode(true);
//                    //m_onNavigationbarInterface.onDemoMode();
//
//                    LoginFinish(ViewIdDef.VIEW_ID_DEMO);
//                    return;
//                }
//
//                //假登入
//                if (strAccout.length() > 0 && "TEST".equals(strAccout)){
//                    //寫入登入狀態
//                    CIApplication.getLoginInfo().SetLoginStatus(true);
//                    CIApplication.getLoginInfo().SetLoginType(
//                            UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER);
//                    CIApplication.getLoginInfo().SetCardType(CICardType.PARA);
//                    CIApplication.getLoginInfo().SetUserName("Ling");
//                    CIApplication.getLoginInfo().SetUserMemberCardNo("WB1234567");
//                    CIApplication.getLoginInfo().SetMiles("0");
//
//                    LoginFinish(m_iViewId);
//                    return;
//                }
//            }

            m_bKeep = bKeep;
            m_strAccount = strAccout;

            CILoginReq loginReq = new CILoginReq();
            loginReq.user_id = strAccout;
            loginReq.password = strPassword;
            //如果已使用社群帳號登入 需進行綁定
            if ( true == m_bIsSocialCombine ){
                loginReq.is_social_combine = CILoginReq.SOCIAL_COMBINE_YES;
                loginReq.social_id = CIApplication.getLoginInfo().GetSocialLoginId();
                loginReq.social_vendor = CIApplication.getLoginInfo().GetLoginType();
                loginReq.social_email = CIApplication.getLoginInfo().GetUserEmail();
            }

            CILoginWSPresenter.getInstance(m_LoginWsListener).
                    LoginWithCombineSocialFromWS(loginReq);
        }

        @Override
        public void onGoogleLoginClick(boolean bKeep) {
            m_bIsNetworkAvailable = AppInfo.getInstance(m_Context).bIsNetworkAvailable();
            if (m_bIsNetworkAvailable) {
                if ( true == m_GPlusApi.checkPlayServicesShowDialog(CILoginActivity.this) ){
                    //是否保持登入狀態
                    m_bKeep = bKeep;

                    m_GPlusApi.GoogleSignIn(GPLUS_SIGN_IN);
                }
            } else {
                //顯示沒有網路的提示訊息
                showNoWifiDialog();
            }
        }

        @Override
        public void onFacebookLoginClick(boolean bKeep) {
            m_bIsNetworkAvailable = AppInfo.getInstance(m_Context).bIsNetworkAvailable();
            if (m_bIsNetworkAvailable) {
//                if ( true == m_GPlusApi.checkPlayServicesShowDialog(CILoginActivity.this) ){
                    //是否保持登入狀態
                    m_bKeep = bKeep;

                    ArrayList<String> permissions = new ArrayList<>();
                    permissions.add("email");
                    permissions.add("public_profile");
                    permissions.add("user_location");
                    m_FBApi.signIn(FB_SIGN_IN, permissions);
//                }
            } else {
                //顯示沒有網路的提示訊息
                showNoWifiDialog();
            }
        }
    };

    private CILoginWSListener m_LoginWsListener = new CILoginWSListener() {
        @Override
        public void onLoginSuccess(String rt_code, String rt_msg, final CILoginResp loginResp) {

            switch (CIApplication.getLoginInfo().GetLoginType()){
                case UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE:
//                    m_GPlusApi.GoogleSignOut();
                    break;
                case UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK:
                    m_FBApi.signOut();
                    break;
            }

            //是否保持登入狀態
            CIApplication.getLoginInfo().SetKeepLogin(m_bKeep);

            //已進行社群登入的話 要先登出
//                    if ( CIApplication.getLoginInfo().GetLoginType().equals(UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK) ){
//                        m_FBApi.signOut();
//                    }else if ( CIApplication.getLoginInfo().GetLoginType().equals(UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE) ){
//                        m_GPlusApi.GoogleSignOut();
//                    }

            //寫入登入狀態
            CIApplication.getLoginInfo().SetLoginData(
                    UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER, loginResp);

            LoginFinish(m_iViewId);
        }

        @Override
        public void onLoginError(final String rt_code, final String rt_msg) {

            Intent intent = new Intent();
//            intent.putExtra(UiMessageDef.BUNDLE_LOGIN_USERNAME_TAG, "Ling");
            intent.putExtra(UiMessageDef.BUNDLE_LOGIN_ACCOUNT_TAG, m_strAccount);

            switch (rt_code){
                case CIWSResultCode.LOGIN_ERROR_NOT_SINGLE_MOBILE:
                    //使用電話登入 找到多筆卡號 需update電話資料
                    intent.putExtra(
                            UiMessageDef.BUNDLE_INPUT_DETAIL_TYPE_TAG,
                            CIAccountTextFieldFragment.Type.MOBILE_NO.name());
                    intent.putExtra(UiMessageDef.BUNDLE_IS_SOCIAL_COMBINE_TAG, m_bIsSocialCombine);

                    ChangeActivity(
                            intent,
                            CIInputDetailActivity.class,
                            UiMessageDef.REQUEST_CODE_LOGIN_INPUT_DETAIL);
                    break;
                case CIWSResultCode.LOGIN_ERROR_NOT_SINGLE_EMAIL:
                    //使用mail登入 找到多筆卡號 需更新mail資料
                    intent.putExtra(
                            UiMessageDef.BUNDLE_INPUT_DETAIL_TYPE_TAG,
                            CIAccountTextFieldFragment.Type.EMAIL.name());
                    intent.putExtra(UiMessageDef.BUNDLE_IS_SOCIAL_COMBINE_TAG, m_bIsSocialCombine);

                    ChangeActivity(
                            intent,
                            CIInputDetailActivity.class,
                            UiMessageDef.REQUEST_CODE_LOGIN_INPUT_DETAIL);
                    break;
                case CIWSResultCode.NO_INTERNET_CONNECTION:
                    //網路錯誤
                    showNoWifiDialog();
                    break;
//                case  CIWSResultCode.LOGIN_ERROR_HAVE_OTHER_OPEN_ID:
//                    //已綁定其他社群帳號, 就用不綁定的方式在登入一次
//                    break;
                default:

                    showDialog(
                            getString(R.string.warning),
                            rt_msg,
                            getString(R.string.confirm));
                    break;
            }
        }

        @Override
        public void onInquirySuccess(String rt_code,
                                     String rt_msg,
                                     String strCard_no,
                                     String password) {}

        @Override
        public void onInquiryError(String rt_code, String rt_msg) {}

        @Override
        public void onSocialLoginSuccess(String rt_code,
                                         String rt_msg,
                                         final CILoginResp socialLoginResp) {

            switch (CIApplication.getLoginInfo().GetLoginType()){
                case UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE:
//                    m_GPlusApi.GoogleSignOut();
                    break;
                case UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK:
                    m_FBApi.signOut();
                    break;
            }

            //寫入登入狀態
            CIApplication.getLoginInfo().SetLoginData(
                    UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER, socialLoginResp);

            //2016-06-25 不顯示登入訊息
//            showDialog(
//                    getString(R.string.warning),
//                    CIApplication.getLoginInfo().GetUserName() + "\n" + getString(R.string.member_login_title),
//                    getString(R.string.confirm),
//                    null,
//                    new CIAlertDialog.OnAlertMsgDialogListener() {
//                        @Override
//                        public void onAlertMsgDialog_Confirm() {

//                        }
//
//                        @Override
//                        public void onAlertMsgDialogg_Cancel() {}
//                    });
            LoginFinish(m_iViewId);
        }

        @Override
        public void onSocialLoginError(final String rt_code, final String rt_msg) {

            switch (rt_code){
                case CIWSResultCode.LOGIN_OPEN_ID_ERROR_NOT_MATCH:
                    //進入成為正式會員的頁面
                    Intent intent = new Intent();
                    intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, m_iViewId);
                    intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE,
                            CIBecomeDynastyFlyerActivity.EMode.TEMPORARY_MEMBER.name());

                    ChangeActivity(
                            intent,
                            CIBecomeDynastyFlyerActivity.class,
                            UiMessageDef.REQUEST_CODE_BECOME_MEMBER);
                    break;
                case CIWSResultCode.NO_INTERNET_CONNECTION:
                    showNoWifiDialog();
                    break;
                default:
                    showDialog(
                            getString(R.string.warning),
                            rt_msg,
                            getString(R.string.confirm));
                    break;
            }
        }

        @Override
        public void showProgress() {
            showProgressDialog(null);
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    View.OnTouchListener m_onBackgroundTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            HidekeyBoard();
            return false;
        }
    };

    //登入頁面分為以下四種類型
    public enum LoginMode {
        /**只顯示登入頁面*/
        BASE,
        /**可選擇登入或find my booking頁面, 預設為登入頁面*/
        FIND_MYBOOKING_MEMBER,
        /**可選擇登入或find my booking頁面, 預設為find my booking頁面*/
        FIND_MYBOOKING_RETRIEVE,
        /**只顯示find my booking頁面*/
        FIND_MYBOOKING_ONLY_RETRIEVE
    }

    private LoginMode m_mode = LoginMode.BASE;

    private GooglePlusLoginApi m_GPlusApi = null;
    private FacebookLoginApi m_FBApi = null;

    public int m_iViewId = 0;
    private boolean m_bKeep = false;
    private boolean m_bIsSocialCombine = false;
    private String m_strAccount = "";

    private CILoginFragment m_loginFragment = null;
    private CIFindMyBookingFragment m_findMyBookingFragment = null;

    /* RequestCode for sign-in */
    private static final int GPLUS_SIGN_IN = 9001;
    private static final int FB_SIGN_IN = 8001;

    private NavigationBar m_Navigationbar = null;
    private FrameLayout m_flContent = null;

    private Bitmap m_bitmap = null;
    private RelativeLayout m_rlBg = null;
    //2016/04/06 針對 Demo 模式需調用interface
    private onNavigationbarInterface m_onNavigationbarInterface = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //fb sdk初始化需寫在super.onCreate()之前
        m_FBApi = new FacebookLoginApi(CILoginActivity.this);

        //設定登入頁面的類型
        String mode = getIntent().getStringExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE);
        if (null != mode) {
            m_mode = LoginMode.valueOf(mode);
        }
        if (null != savedInstanceState) {
            m_mode = LoginMode.valueOf(savedInstanceState.getString(
                    UiMessageDef.BUNDLE_ACTIVITY_MODE));
        }

        //設定登入成功後需跳轉的viewId，此值是指從哪個sideMenu頁面進入此頁面
        m_iViewId = getIntent().getIntExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, 0);

        //是否已使用社群帳號登入 若是,則登入時需進行綁定
        m_bIsSocialCombine = getIntent().getBooleanExtra(
                UiMessageDef.BUNDLE_IS_SOCIAL_COMBINE_TAG, false);
        if ( false == m_bIsSocialCombine ){
            //再檢查一次是否已社群登入
            if ( true == CIApplication.getLoginInfo().GetLoginStatus()
                    && !TextUtils.isEmpty(CIApplication.getLoginInfo().GetLoginType())
                    && !CIApplication.getLoginInfo().GetLoginType().equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)
                    && !TextUtils.isEmpty(CIApplication.getLoginInfo().GetSocialLoginId()) ){
                m_bIsSocialCombine = true;
            }
        }

        /**super的方法一定要在取得LoginMode後面，不然畫面會不正確*/
        super.onCreate(savedInstanceState);

        //以下為 查fb的KeyHash用
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    m_Context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
               SLog.d("KeyHash", ":" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }

        //顯示toast訊息
        String strMsg = getIntent().getStringExtra(UiMessageDef.BUNDLE_TOAST_MSG_TAG);
        if ( !TextUtils.isEmpty(strMsg) ){
            CIToastView.makeText(m_Context, strMsg).show();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initialLayoutComponent() {

//        m_GPlusApi = GooglePlusLoginApi.getInstance();
        m_GPlusApi = new GooglePlusLoginApi();
        m_GPlusApi.createConnection(CILoginActivity.this);
        m_GPlusApi.setUserLoginCallbackListener(CILoginActivity.this);

        m_FBApi.setCallbackManager();
        m_FBApi.setUserCallbackListener(CILoginActivity.this);

        m_rlBg = (RelativeLayout) findViewById(R.id.relativelayout);
        m_bitmap = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
        Drawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        m_rlBg.setBackground(drawable);
        m_rlBg.setOnTouchListener(m_onBackgroundTouchListener);

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_flContent = (FrameLayout) findViewById(R.id.container);

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        if (LoginMode.FIND_MYBOOKING_MEMBER == m_mode
                || LoginMode.FIND_MYBOOKING_RETRIEVE == m_mode) {
            /**可選模式時必須移動framelayout container往下*/
            vScaleDef.setMargins(findViewById(R.id.container), 0, 66, 0, 0);
            vScaleDef.setMargins(findViewById(R.id.two_item_navigation_bar), 0, 20, 0, 0);
        }
    }

    @Override
    protected void setOnParameterAndListener() {
        m_onNavigationbarInterface = m_Navigationbar.uiSetParameterListener(
                m_onNavigationParameter,
                m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        m_loginFragment = new CILoginFragment();

        FindMyBookingType type = FindMyBookingType.BASE;

        switch (m_iViewId){
            //FindMyBooking by BOARDING_PASS->按下查詢行程會呼叫BoardingPass WS, 並將查到的資料傳入登機證頁面
            case ViewIdDef.VIEW_ID_BOARDINGPASS_EWALLET:
                type = FindMyBookingType.BOARDING_PASS;
                break;

            //FindMyBooking by CHECK_IN->按下查詢行程後若成功, 會直接跳轉至check-in頁面
            case  ViewIdDef.VIEW_ID_CHECK_IN:
                type = FindMyBookingType.CHECK_IN;
                break;
        }
        //引入CILoginActivity Mode, FindMyBooking Type, 點擊側邊欄ViewId(按「登入」為0000)
        m_findMyBookingFragment = CIFindMyBookingFragment.newInstance(m_mode, type, m_iViewId);

        m_loginFragment.uiSetParameterListener(m_onLoginListener);

        // 開始Fragment的事務Transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // 設置轉換效果
        //fragmentTransaction.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_left_out);

        String strLeftText = getString(R.string.member_login_member);
        String strRightText = getString(R.string.member_login_booking_ref_ticket);
        if (LoginMode.FIND_MYBOOKING_RETRIEVE == m_mode) {
            TwoItemNavigationBar bar = TwoItemNavigationBar
                    .newInstance(strLeftText, strRightText, RIGHT);
            bar.setListener(this);
            replaceFragment(fragmentTransaction, R.id.two_item_navigation_bar, bar);
            replaceFragment(fragmentTransaction, m_flContent.getId(), m_findMyBookingFragment);
            //start 644336 2019 2月3月 AI/行事曆/截圖/注意事項
            m_onNavigationbarInterface.showAboutButton();
            //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項
        } else if (LoginMode.FIND_MYBOOKING_MEMBER == m_mode) {
            TwoItemNavigationBar bar = TwoItemNavigationBar.newInstance(
                    strLeftText,
                    strRightText, LEFT);
            bar.setListener(this);
            replaceFragment(fragmentTransaction, R.id.two_item_navigation_bar, bar);
            replaceFragment(fragmentTransaction, m_flContent.getId(), m_loginFragment);
        } else if (LoginMode.FIND_MYBOOKING_ONLY_RETRIEVE == m_mode) {
            replaceFragment(fragmentTransaction, m_flContent.getId(), m_findMyBookingFragment);
        } else if (LoginMode.BASE == m_mode) {
            // 替换容器(container)原来的Fragment
            replaceFragment(fragmentTransaction, m_flContent.getId(), m_loginFragment);
        }

        // 提交事務
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void replaceFragment(FragmentTransaction transaction,
                                 int container,
                                 BaseFragment fragment) {
        transaction.replace(container,
                fragment,
                fragment.getTag());
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
        CILoginWSPresenter.getInstance(m_LoginWsListener).LoginWithCombineSocialCancel();
        CILoginWSPresenter.getInstance(m_LoginWsListener).LoginByOpenIdCancel();
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
        CILoginWSPresenter.getInstance(null);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);

//        Log.e(AppInfo.APP_LOG_TAG, "login onActivityResult" + requestCode + "&" + responseCode + "back main" + m_iViewId);

        //成為正式會員 或 輸入個人資訊頁面 完成後 登入頁面將自動關閉
        if ( requestCode == UiMessageDef.REQUEST_CODE_SIGN_UP && responseCode == RESULT_OK
                || ( requestCode == UiMessageDef.REQUEST_CODE_LOGIN_INPUT_DETAIL && responseCode == RESULT_OK )
                || ( requestCode == UiMessageDef.REQUEST_CODE_BECOME_MEMBER && responseCode == RESULT_OK ) ) {
            LoginFinish(m_iViewId);
        } else if ( requestCode == UiMessageDef.REQUEST_CODE_BECOME_MEMBER && responseCode != RESULT_OK ) {
            //CIBecomeDynastyFlyerActivity 未回傳ok就必須回首頁(但登機證及mytrip例外)
            if ( m_iViewId == ViewIdDef.VIEW_ID_BOARDINGPASS_EWALLET
                    || m_iViewId == ViewIdDef.VIEW_ID_MY_TRIPS ){
                LoginFinish(m_iViewId);
            }else {
                LoginFinish(0);
            }
        } else if (requestCode == GPLUS_SIGN_IN) {
            m_GPlusApi.onActivityResult(requestCode, responseCode, data);
        } else {
            m_FBApi.getCallbackManager().onActivityResult(requestCode, responseCode, data);
        }
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    public void onItemClick(View v) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.rl_left_bg:
                m_mode = LoginMode.FIND_MYBOOKING_MEMBER;
                replaceFragment(fragmentTransaction, R.id.container, m_loginFragment);
                //644336 2019 2月3月 AI/行事曆/截圖/注意事項
                m_onNavigationbarInterface.hideAboutButton();
                //644336 2019 2月3月 AI/行事曆/截圖/注意事項
                break;
            case R.id.rl_right_bg:
                m_mode = LoginMode.FIND_MYBOOKING_RETRIEVE;
                replaceFragment(fragmentTransaction, R.id.container, m_findMyBookingFragment);
                //644336 2019 2月3月 AI/行事曆/截圖/注意事項
                m_onNavigationbarInterface.showAboutButton();
                //644336 2019 2月3月 AI/行事曆/截圖/注意事項
                break;
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(UiMessageDef.BUNDLE_ACTIVITY_MODE, m_mode.name());
    }

    private void ChangeActivity(Intent intent, Class clazz, int iResult){
        intent.setClass(m_Context, clazz);
        startActivityForResult(intent, iResult);

        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    //start 644336 2019 2月3月 AI/行事曆/截圖/注意事項
    /**
     * 轉換Activity
     * @param clazz 目標activity名稱
     * @param key   extra key
     * @param extra extra value
     */
    private void changeActivity(Class clazz,String key,String extra){
        Intent intent = new Intent();
        intent.putExtra(key, extra);
        intent.setClass(CILoginActivity.this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        CILoginActivity.this.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }
    //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項

    private void LoginFinish(int iViewId){

        Intent intent = new Intent();

        intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, iViewId);
        //告知上一頁 顯示會員登入成功訊息
        intent.putExtra(UiMessageDef.BUNDLE_TOAST_MSG_TAG, getString(R.string.login_success));

        /*如果是此頁面登入成功後返回時 resultCode OK by kevin */
        setResult(RESULT_OK, intent);

        CILoginActivity.this.finish();
    }

    //google/FB登入成功的callback
    @Override
    public void onUserConnected(int requestIdentifier, String strId, String strMail) {

        CIApplication.getLoginInfo().SetKeepLogin(m_bKeep);

        String strType = SocialNetworkKind.FACEBOOK;
        switch (requestIdentifier) {
            case GPLUS_SIGN_IN:
                strType = SocialNetworkKind.GOOGLE;
               SLog.d("Google+", "connected");
                break;
            case FB_SIGN_IN:
                strType = SocialNetworkKind.FACEBOOK;
               SLog.d("FB", "connected");
                break;
        }

        CILoginWSPresenter.getInstance(m_LoginWsListener).LoginByOpenIdFromWS(
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
               SLog.d("Google+", "onConnectionError" + message);
                break;
            case FB_SIGN_IN:
                strMsg = getString(R.string.facebook);
               SLog.d("FB", "onConnectionError" + message);
                break;
        }

        showDialog(
                getString(R.string.warning),
                strMsg + " " + getString(R.string.menu_log_in) + " " + getString(R.string.error),
                getString(R.string.confirm));
    }

    //google/FB登入取消的callback
    @Override
    public void onCancelled(int requestIdentifier, String message) {

        String strMsg = "";
        switch (requestIdentifier) {
            case GPLUS_SIGN_IN:
                strMsg = getString(R.string.google_plus);
               SLog.d("Google+", "onCancelled:" + message);
                break;
            case FB_SIGN_IN:
                strMsg = getString(R.string.facebook);
               SLog.d("FB", "onCancelled:" + message);
                break;
        }

        CIToastView.makeText(CILoginActivity.this, String.format( strMsg + " " + getString(R.string.menu_log_in) + getString(R.string.cancel)) ).show();

//        showDialog(
//                getString(R.string.warning),
//                strMsg + getString(R.string.menu_log_in) + getString(R.string.cancel),
//                getString(R.string.confirm));
    }
}
