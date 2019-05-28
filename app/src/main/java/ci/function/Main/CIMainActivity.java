package ci.function.Main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.CIInternalNotificationReceiver;
import com.chinaairlines.mobile30.CINotiflyItem;
import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.List;

import ci.function.About.CIAboutFragment;
import ci.function.BaggageTrack.CIBaggageInfoContentActivity;
import ci.function.BaggageTrack.CIBaggageTrackingListActivity;
import ci.function.BaggageTrack.CIFindMyBaggageActivity;
import ci.function.Base.CIDemoFragment;
import ci.function.BoardingPassEWallet.CIBoardingPassEWalletFragment;
import ci.function.BoardingPassEWallet.CIBoardingWithQRCodeActivity;
import ci.function.BookTicket.CIBookTicketActivity;
import ci.function.Checkin.CICheckInActivity;
import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.function.FlightStatus.CIFlightStatusFragment;
import ci.function.HomePage.CIHomeFragment;
import ci.function.HomePage.CIMainCheckInFragment;
import ci.function.HomePage.CIMainNoTicketFragment;
import ci.function.Login.CILoginActivity;
import ci.function.Login.CISchemeLoginActivity;
import ci.function.Login.api.FacebookLoginApi;
import ci.function.Login.api.GooglePlusLoginApi;
import ci.function.Main.LeftDrawerFragment.OnLeftDrawerInterface;
import ci.function.Main.LeftDrawerFragment.OnLeftDrawerListener;
import ci.function.Main.RightDrawerFragment.OnRightDrawerInterface;
import ci.function.Main.RightDrawerFragment.OnRightDrawerListener;
import ci.function.Main.item.SideMenuItem;
import ci.function.Signup.CIBecomeDynastyFlyerActivity;
import ci.function.Signup.CISignUpActivity;
import ci.function.Start.CIStartActivity;
import ci.ui.WebView.CIWithoutInternetActivity;
import ci.ui.define.HomePage_Status;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewIdDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.AppInfo;
import ci.ui.object.CIFCMManager;
import ci.ui.object.CILoginInfo;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.object.item.CIHomeStatusEntity;
import ci.ui.toast.CIToastView;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIBaggageInfoContentResp;
import ci.ws.Models.entities.CIBaggageInfoReq;
import ci.ws.Models.entities.CIBaggageInfoResp;
import ci.ws.Models.entities.CIBoardPassResp;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CIEWalletReq;
import ci.ws.Models.entities.CIInquiryTripEntity;
import ci.ws.Models.entities.CITripListResp;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CIAIServicePresenter;
import ci.ws.Presenter.CIInquiryBaggageInfoPresenter;
import ci.ws.Presenter.Listener.CIBaggageInfoListener;

import static ci.function.HomePage.CIHomeFragment.OnHomeFragmentInterface;
import static ci.function.HomePage.CIHomeFragment.OnHomeFragmentListener;
import static ci.ui.view.NavigationBar.onNavigationbarInterface;
import static ci.ui.view.NavigationBar.onNavigationbarListener;
import static ci.ui.view.NavigationBar.onNavigationbarParameter;

public class CIMainActivity extends BaseActivity{

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return true;
        }

        @Override
        public String GetTitle() {
            return "";
        }
    };

    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {
            m_DrawerLayout.openDrawer(Gravity.RIGHT);
        }

        @Override
        public void onLeftMenuClick() {
            m_DrawerLayout.openDrawer(Gravity.LEFT);
        }

        @Override
        public void onBackClick() {}

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {
            SelectSideMenu( ViewIdDef.VIEW_ID_DEMO,
                    "",
                    uiNewFuncFragment(CIDemoFragment.class));
        }
    };

    private OnHomeFragmentListener m_OnHomeFragmentListener = new OnHomeFragmentListener() {
        @Override
        public void OnBookTicketClick() {
            Intent intent = new Intent();
            changeActivityForResult(CIBookTicketActivity.class, 0, intent, false);
        }

        @Override
        public void OnFightStatusClick() {
            m_bShowAnim = true;
            SideMenuItem sideMenuItem = ViewIdDef.getInstance(m_Context).getRightMenuList().get(0).get(2);
            SelectSideMenu(sideMenuItem);
        }

        @Override
        public void OnTimeTableClick() {
            m_bShowAnim = true;
            SideMenuItem sideMenuItem = ViewIdDef.getInstance(m_Context).getRightMenuList().get(0).get(3);
            SelectSideMenu(sideMenuItem);
        }

        @Override
        public void OnExtraServicesClick() {
            m_bShowAnim = true;
            SideMenuItem sideMenuItem = ViewIdDef.getInstance(m_Context).getRightMenuList().get(1).get(0);
            SelectSideMenu(sideMenuItem);
        }

        @Override
        public void OnMyTripsClick() {
            m_bShowAnim = true;
            SideMenuItem sideMenuItem = ViewIdDef.getInstance(m_Context).getLeftMenuList().get(0).get(1);
            SelectSideMenu(sideMenuItem);
        }

        @Override
        public void OnMyTripsNoBoardClick() {

            String strMode = CILoginActivity.LoginMode.FIND_MYBOOKING_MEMBER.name();
            //是否已登入
            if ( true == CIApplication.getLoginInfo().GetLoginStatus()) {
                if (CIApplication.getLoginInfo().GetLoginType().equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)) {
                    //正式會員只顯示find my booking
                    strMode = CILoginActivity.LoginMode.FIND_MYBOOKING_ONLY_RETRIEVE.name();
                } else {
                    //臨時會員預設顯示find my booking(可跳轉member登入頁)
                    strMode = CILoginActivity.LoginMode.FIND_MYBOOKING_RETRIEVE.name();
                }
            }

            Intent intent = new Intent();
            intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, ViewIdDef.VIEW_ID_HOME);
            intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE, strMode);
            intent.setClass(m_Context, CILoginActivity.class);
            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_LOGIN);
            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        @Override
        public void OnMilesManageClick() {
            m_bShowAnim = true;
            ArrayList<ArrayList<SideMenuItem>> alMenu =
                    ViewIdDef.getInstance(m_Context).getLeftMenuList();
            SideMenuItem  menuItem   = alMenu.get(0).get(2);
            SelectSideMenu(menuItem);
        }
    };

    private class OnDrawerClosedListener extends DrawerLayout.SimpleDrawerListener {
        protected int m_iNewViewCode;
        protected String m_strNewViewName;
        protected Fragment m_newFragment;

        public OnDrawerClosedListener(final int iNewViewCode, final String strNewViewName, final Fragment newFragment) {
            m_iNewViewCode = iNewViewCode;
            m_strNewViewName = strNewViewName;
            m_newFragment = newFragment;
        }

    }
    private OnHomeFragmentInterface m_Interface                 = null;
    private OnDrawerClosedListener  m_OnDrawerClosedListener    = null;

    private GooglePlusLoginApi m_GplusApi;
    private FacebookLoginApi m_FBApi;

    protected final int TIMER_CHANGEVIEW_DELAY_TIME = 400;//300;
    /** 前一次畫面狀態 */
    protected int       m_iOldViewId        = 0;
    /** 當下畫面的Function Code */
    protected int       m_iCurrViewId       = 0;
    /** 當下畫面的名稱 */
    protected String    m_strCurrViewName   = "";
    /** 當下畫面的Tag */
    protected String    m_strCurrViewTag    = "";
    /** sidemenu x軸 位置*/
    private float       m_moveFactor        = 0.0f;

    /** 是否第一次進入首頁 */
    protected boolean   m_bFirstHome        = true;
    /** 是否點擊回上頁 */
    protected boolean   m_bIsBack           = false;
    /** 是否要切換Fragment*/
    protected boolean   m_bIsChangeFragment = false;
    /** 是否切換Fragment要顯示動畫 */
    public boolean   m_bShowAnim         = false;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private DrawerLayout        m_DrawerLayout          = null;
    private LeftDrawerFragment  m_LeftDrawerFragment    = null;
    private RightDrawerFragment m_RightDrawerFragment   = null;

    private RelativeLayout      m_rlayout_container     = null;
    private FrameLayout         m_flayout_Container     = null;

    private onNavigationbarInterface m_onNavigationbarInterface = null;
    private NavigationBar       m_Navigationbar         = null;

    private LinearLayout        m_llLeftMenu            = null;
    private LinearLayout        m_llRightMenu           = null;

    private float m_flastTranslate = 0.0f;

    private OnLeftDrawerInterface m_onLeftMenuInterface = null;
    private OnRightDrawerInterface m_onRightMenuInterface = null;

    private SideMenuItem        m_HomeMenu              = null;
    private SideMenuItem        m_CurrSideMenuItem      = null;
    //for postdelay, 故將變數拉成member
    private Intent              m_intent                = null;

    private CIEWalletReq        m_EWalletReq            = null;
    private CIBoardPassResp m_BoardPassResp             = null;
    private CITripListResp      m_TripListResp          = null;
    private CITripListResp_Itinerary m_itineraryInfo         = null;
    //新增接CPR的資料
    private ArrayList<CICheckInPax_InfoEntity> m_arCheckInList   = null;
    //加一個Flag判斷資料是否由FindMyBooking來
    private Boolean             m_bCPRfromFindMyBooking = false;
    //加一個Flag判斷是否check-in完後要回首頁
    private Boolean             m_bCheckInBackHome      = false;

    private CINotiflyItem       m_Notify                = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
       SLog.d("[CAL]", "[CAL][CIMainActivity] onCreate start");
        //
        m_FBApi = new FacebookLoginApi(CIMainActivity.this);

        super.onCreate(savedInstanceState);
        //
       SLog.d("[CAL]", "[CAL][CIMainActivity] onCreate finish");
        //
    }

    /**
     * onCreate時 預先載入所有SlidMenu相關的Fragment
     */
    private void addAllSlidMenuFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();

        ArrayList<ArrayList<SideMenuItem>> arLeftMenuList = ViewIdDef.getInstance(this).getLeftMenuList();
        ArrayList<ArrayList<SideMenuItem>> arRightMenuList = ViewIdDef.getInstance(this).getRightMenuList();
        ArrayList<String> arrayList = new ArrayList<>();
        boolean isFirstAddAllFragment;
        isFirstAddAllFragment = null == manager.findFragmentByTag(CIAboutFragment.class.getSimpleName());

            for( SideMenuItem sideMenuItem : arLeftMenuList.get(0) ) {
                Fragment    fragment    = uiNewFuncFragment(sideMenuItem._class);
                if( null != fragment ) {
                    String strViewName = sideMenuItem._class.getSimpleName();
                    arrayList.add(strViewName);
                    if(true == isFirstAddAllFragment){
                        fragmentTransaction.add(m_flayout_Container.getId(),fragment,strViewName ).hide(fragment);
                    }
                }
            }

            for( ArrayList<SideMenuItem> arList : arRightMenuList ) {
                for (SideMenuItem sideMenuItem : arList) {
                    Fragment fragment = uiNewFuncFragment(sideMenuItem._class);
                    if (null != fragment) {
                        String strViewName = sideMenuItem._class.getSimpleName();
                        arrayList.add(strViewName);
                        if(true == isFirstAddAllFragment){
                            fragmentTransaction.add(m_flayout_Container.getId(),fragment,strViewName ).hide(fragment);
                        }
                    }
                }
            }

            //如果不是第一次加入全部fragment，則代表己經加入，所以不需重新加入所有的fragment，只需要隱藏即可 by kevin
            if(false == isFirstAddAllFragment){
                for(String strViewName:arrayList){
                    Fragment    fragment    = manager.findFragmentByTag(strViewName);
                    if( null != fragment ) {
                        fragmentTransaction.hide(fragment);
                    }
                }
            }

            fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
       SLog.d("CAL", "[CAL][onNewIntent]");

        if( true == intent.getBooleanExtra(UiMessageDef.BUNDLE_LOGOUT_REQUEST_TAG, false) ) {

            switch (CIApplication.getLoginInfo().GetLoginType()){
                case UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE:
                    m_GplusApi.GoogleSignOut();
                    break;
                case UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK:
                    m_FBApi.signOut();
                    break;
            }

            CIApplication.getLoginInfo().ClearLoginData();

        }

        if ( ParseGCMNotifyData( intent, false ) ){
            return;
        }

        SelectSideMenu(m_HomeMenu);

        CloseSideMenuChaneView();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initialLayoutComponent() {

//        m_GplusApi = GooglePlusLoginApi.getInstance();
        m_GplusApi = new GooglePlusLoginApi();
        m_GplusApi.createConnection(CIMainActivity.this);

        //判斷user使否有勾選保持登入，若未勾選且當前為登入狀態，需進行登出
        if ( true == getIntent().getBooleanExtra(UiMessageDef.BUNDLE_IS_DO_LOGOUT, false) ){
            m_GplusApi.GoogleSignOut();
            m_FBApi.signOut();
            CIApplication.getLoginInfo().ClearLoginData();
        }

        //設定自製的ToolBar
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);

        m_rlayout_container = (RelativeLayout)findViewById(R.id.rlayout_container);
        m_flayout_Container = (FrameLayout)findViewById(R.id.container);
        m_DrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // 設定 Drawer 的影子
        m_DrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
        m_DrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_right, GravityCompat.END);

        //setScrimColor是在設定側邊選單打開時，剩餘空間的遮罩顏色。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            m_DrawerLayout.setScrimColor(m_Context.getResources().getColor((R.color.transparent), m_Context.getTheme()));
        } else {
            m_DrawerLayout.setScrimColor(m_Context.getResources().getColor(R.color.transparent));
        }
        m_DrawerLayout.setDrawerListener(m_DrawerListener);

        m_llLeftMenu = (LinearLayout) findViewById(R.id.llayout_left);
        m_llRightMenu = (LinearLayout) findViewById(R.id.llayout_right);

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        m_Navigationbar.getLayoutParams().height = vScaleDef.getLayoutHeight(56);

        m_llLeftMenu.getLayoutParams().width = vScaleDef.getLayoutWidth(300);
        m_llRightMenu.getLayoutParams().width = vScaleDef.getLayoutWidth(300);

        ArrayList<ArrayList<SideMenuItem>> arHomeList = ViewIdDef.getInstance(CIMainActivity.this).getHOMEMenuList();
        if ( arHomeList.size() != 0 && arHomeList.get(0).size() != 0 ){
            m_HomeMenu = arHomeList.get(0).get(0);
        }
    }

    @Override
    protected void setOnParameterAndListener() {
        m_onNavigationbarInterface = m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        m_LeftDrawerFragment = (LeftDrawerFragment) fragmentManager.findFragmentById(R.id.drawer_left);
        m_onLeftMenuInterface = m_LeftDrawerFragment.SetOnLeftDrawerListenerAndParameter(m_onLeftMenuListener);

        m_RightDrawerFragment = (RightDrawerFragment) fragmentManager.findFragmentById(R.id.drawer_right);
        m_onRightMenuInterface = m_RightDrawerFragment.SetOnRightDrawerListener(m_onRightMenuListener);

        //預載SlidMenu的所有 fragment
        addAllSlidMenuFragment();

        //初始化畫面
        SelectSideMenu(m_HomeMenu);
        m_bFirstHome = false;

        ParseGCMNotifyData( this.getIntent(), true );

        //2018-06-28 新增url Scheme 串接登入
        if ( AppInfo.getInstance(this).GetIsFromScheme() ){

            m_BaseHandler.postDelayed( new Runnable(){
                @Override
                public void run() {

                    Intent intent = new Intent();
                    intent.setClass(CIMainActivity.this, CISchemeLoginActivity.class);
                    startActivity(intent);

                }
            }, 500);
        }
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        if(msg.what == UiMessageDef.MSG_DO_LOGOUT){

            String strMsg = msg.getData().getString(UiMessageDef.BUNDLE_DIALOG_MSG_TAG);

            DoLogOutAndShowMsg(strMsg);

            return true;
        }
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {
        m_BaseHandler.removeMessages(UiMessageDef.MSG_DO_LOGOUT);
    }

    @Override
    protected void onLanguageChangeUpdateUI() {
        m_strCurrViewName = getString(m_CurrSideMenuItem.iNameResId);
        m_onNavigationbarInterface.changeHomeTitle(m_strCurrViewName);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //for Demo by ryan
        if ( AppInfo.getInstance(m_Context).GetDemoMode() ){
            m_onNavigationbarInterface.onDemoMode();
        }

        if ( true ==  m_bIsNetworkAvailable ) {
            if ( true == CIApplication.getLoginInfo().GetLoginStatus() ) {

                switch (CIApplication.getLoginInfo().GetLoginType()){
                    case UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE:
                        //臨時會員-Google 背景重登
                        m_GplusApi.connectWithGPlus();
                        break;
                    case UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK:
                        //臨時會員-fb 依token取user資料
                        m_FBApi.connectWithFB();
                        break;
                }
            }
        }

        //會員小卡view依登入狀態作改變
        changePersonalView();
    }

    @Override
    public void onPause() {
        super.onPause();

        cancelRefreshHomePage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        /* 進入待命或者是畫面轉換到其他的Activity時，要停止刷新首頁狀態
         * 到了登入頁面登入後因為身份會切換切並且寫入CILoginInfo，
         * 首頁狀態寫入的DB時會根據CILoginInfo資訊寫入不同的Table，
         * 所以要在進入登入頁面時提前停止刷新首頁狀態
         * 避免將A身份的資料寫入到B身份的Table內
         * */
        //cancelRefreshHomePage();
    }

    private void changePersonalView(){



        String strPhoto = CIApplication.getLoginInfo().GetUserPhotoUrl();
        int iPersonalType = ViewIdDef.PERSONAL_TYPE_NOT_LOGIN;

        switch (CIApplication.getLoginInfo().GetLoginType()) {
            case UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER:
                iPersonalType = ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER;
                break;
            case UiMessageDef.BUNDLE_LOGIN_TYPE_GOOGLE:
            case UiMessageDef.BUNDLE_LOGIN_TYPE_FACEBOOK:
                iPersonalType = ViewIdDef.PERSONAL_TYPE_TEMPORARY;
                break;
            default:
                break;
        }
        String strName;
        CILoginInfo loginInfo = CIApplication.getLoginInfo();
        if(iPersonalType == ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER) {
            //CR 2017-7-21 變更左上角小卡名字順序，為了因應使用者名稱會在登入後先寫入sharedPreference
            //故若更新到此版本，持續登入的使用者名稱從sp取出時，而會是舊的格式，故選擇此切換小卡時判斷
            //若是正式會員則重新設定使用者名稱。
            strName = loginInfo.GetUserLastName() + ", " + loginInfo.GetUserFirstName();
            loginInfo.SetUserName(strName);
        } else {
            strName = CIApplication.getLoginInfo().GetUserName();
        }

        if ( true == CIApplication.getLoginInfo().GetLoginStatus() &&
                iPersonalType != ViewIdDef.PERSONAL_TYPE_NOT_LOGIN ) {

            m_onLeftMenuInterface.changeView(
                    iPersonalType, strName, strPhoto);

        }else {
           SLog.d(AppInfo.APP_LOG_TAG, "Not Login");

            //未登入
            m_onLeftMenuInterface.changeView(
                    ViewIdDef.PERSONAL_TYPE_NOT_LOGIN, null, null);
        }
    }

    @Override
    public void onBackPressed() {

        //當畫面不是HomePage時, 按下Back鍵都會先回到首頁！, 在首頁按下Back鍵才會跳出離開App的視窗
        if ( m_iCurrViewId == ViewIdDef.VIEW_ID_HOME ){
                //先把SideMenu收起來在離開App
            if ( m_DrawerLayout.isDrawerOpen(Gravity.LEFT) || m_DrawerLayout.isDrawerOpen(Gravity.RIGHT) ){
                m_DrawerLayout.closeDrawers();
            } else {
                super.onBackPressed();
            }
        } else {
            m_bIsBack = true;
            SelectSideMenu(m_HomeMenu);
        }
    }

    /**
     * 根據傳入的Function Code 轉換對應的畫面
     * 顯示對應的Fragment，隱藏當前顯示的Fragment
     *
     * @param iNewViewCode
     *            畫面的 Function Code
     * @param strNewViewName
     *            下一個畫面的 Function Code
     * @param newFragment
     *            下一個畫面的 Fragment
     */
    protected void ChangeViewByShow( int iNewViewCode, String strNewViewName, Fragment newFragment ) {
        if (null == strNewViewName) {
            return;
        }

        //暫存當前的Fragment tag
        final String strOldViewCode = m_strCurrViewTag;

        // 開始Fragment的事務Transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // 置換舊的Fragment
        m_iOldViewId = m_iCurrViewId;
        // 換上新的畫面Tag以及Flag
        m_iCurrViewId = iNewViewCode;
        m_strCurrViewTag = newFragment.getClass().getSimpleName();

        // 設置轉換效果 (第一次近首頁不需要動畫)
        if (false == m_bFirstHome) {
            if (true == m_bIsBack) {
                fragmentTransaction.setCustomAnimations(R.anim.anim_left_in, R.anim.anim_right_out);
            } else {
                //2016/04/05 移除切換畫面的動畫 避免動畫重複, 造成畫面混亂 by ryan
                if (true == m_bShowAnim){
                    //2016/04/07 當使用快捷鍵跳轉fragment時 仍要顯示動畫 - by Ling
                    fragmentTransaction.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_left_out);
                    m_bShowAnim = false;
                }
            }
        } else {
            m_bFirstHome = false;
        }
        m_bIsBack = false;

        //檢查Fragment是否已存在
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(m_strCurrViewTag);
        Fragment topFragment = getSupportFragmentManager().findFragmentByTag(strOldViewCode);

        if( null != topFragment && !m_strCurrViewTag.equals(strOldViewCode)) {
            fragmentTransaction.hide(topFragment);
        }

        if( null == fragment ) {
            fragmentTransaction.add(m_flayout_Container.getId(), newFragment,m_strCurrViewTag);
        } else {
            fragmentTransaction.show(fragment);
        }

        // 提交事務
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 根據傳入的Function Code 轉換對應的畫面
     *
     * @param iNewViewCode
     *            畫面的 Function Code
     * @param strNewViewName
     *            下一個畫面的 Function Code
     * @param newFragment
     *            下一個畫面的 Fragment
     */
    protected void ChangeViewByReplace( int iNewViewCode, String strNewViewName, Fragment newFragment ) {

        if (null == strNewViewName) {
            return;
        }

        // 開始Fragment的事務Transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //Fragment fragment = getSupportFragmentManager().findFragmentByTag(strNewViewName);
        //if ( m_iCurrViewId != iNewViewCode ) {
        //      fragment = newFragment;
        //    setOnParameterAndListener_Fragment(iNewViewCode, fragment);
        //}

        // 置換舊的Fragment
        m_iOldViewId = m_iCurrViewId;
        // 換上新的畫面Tag以及Flag
        m_iCurrViewId = iNewViewCode;
        m_strCurrViewName = strNewViewName;

        // 設置轉換效果 (第一次近首頁不需要動畫)
        if (false == m_bFirstHome) {
            if (true == m_bIsBack) {
                fragmentTransaction.setCustomAnimations(R.anim.anim_left_in, R.anim.anim_right_out);
            } else {
                //2016/04/05 移除切換畫面的動畫 避免動畫重複, 造成畫面混亂 by ryan
                if (true == m_bShowAnim){
                    //2016/04/07 當使用快捷鍵跳轉fragment時 仍要顯示動畫 - by Ling
                    fragmentTransaction.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_left_out);
                    m_bShowAnim = false;
                }
            }
        } else {
            m_bFirstHome = false;
        }
        m_bIsBack = false;

        // 替换容器(container)原来的Fragment
        fragmentTransaction.replace(m_flayout_Container.getId(), newFragment, strNewViewName);

        // 提交事務
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 設定對應畫面的輸入,輸出參數
     *
     * @param iFlag
     *            畫面的 Function Code
     * @param fragment
     *            需要設定參數的畫面
     */
    protected void setOnParameterAndListener_Fragment(int iFlag, Fragment fragment) {

        switch ( iFlag ){
            case ViewIdDef.VIEW_ID_HOME:
            {
                m_Interface = ((CIHomeFragment)fragment).uiSetParameterListener(m_OnHomeFragmentListener);
            }
            break;
            default:
                break;
        }
    }

    /**
     * 轉換Activity
     * @param clazz     目標activity名稱
     * @param intent    傳遞給下一個Activity的資料
     * @param bRightSideMenu 是否為右邊側邊欄
     */
    private void changeActivityForResult(Class clazz,int requestCode, Intent intent, boolean bRightSideMenu ){
        if(null == clazz){
            return;
        }
        intent.setClass(m_Context, clazz);
        startActivityForResult(intent, requestCode);
        if ( bRightSideMenu ){
            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
        } else {
            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }
    }

    /**
     * 根據傳入的Function Code 轉換對應的畫面 <br>
     *
     * @param sideMenuItem 畫面的相關參數
     */
    public void SelectSideMenu( SideMenuItem sideMenuItem ) {

        int         iViewId     = sideMenuItem.iId;
        String      strViewName = getResources().getString(sideMenuItem.iNameResId);
        String      strClassName= "";
        if(null != sideMenuItem._class){
            strClassName= sideMenuItem._class.getSimpleName();
        }
        //2016.05.18 目前作法fragment需依tag撈現已存在的fragment 才可以傳值-Ling
        Fragment    fragment    = getSupportFragmentManager().findFragmentByTag(strClassName);
        if ( null == fragment ){
            fragment = uiNewFuncFragment(sideMenuItem._class);
        }

        m_CurrSideMenuItem = sideMenuItem;

        SelectSideMenu(iViewId, strViewName, fragment);
    }

    class ChangeUiSetting {
        /**前往activity頁面的代碼*/
        int     requestCode;
        /**轉換Activity class的*/
        Class   clazz;
        /**是否為右側邊欄*/
        boolean bRightSideMenu;
        ChangeUiSetting(){
            clazz               = null;
            requestCode         = 0;
            bRightSideMenu      = false;
        }
    }

    /**
     * 根據傳入的Function Code 轉換對應的畫面
     */
    public void SelectSideMenu( int iViewId, String strViewName, Fragment fragment ) {

        //是否已登入
        boolean bLoginStatus    = CIApplication.getLoginInfo().GetLoginStatus();

        //登入類型
        String  strLoginType    = CIApplication.getLoginInfo().GetLoginType();

        final ChangeUiSetting setting = new ChangeUiSetting();

        //db是否有行程資料
        boolean bHaveBooking = false;
        List<CIInquiryTripEntity> list = CIPNRStatusManager.getInstance(null).getAllTripData();
        if ( null != list && 0 < list.size() ){
            bHaveBooking = true;
        }

        if ( iViewId == m_iCurrViewId ){
            //更新左右選單的背景顏色
            if ( null != m_onLeftMenuInterface ){
                m_onLeftMenuInterface.onSelectMenu(iViewId);
            }
            if ( null != m_onRightMenuInterface ){
                m_onRightMenuInterface.onSelectMenu(iViewId);
            }
            m_DrawerLayout.closeDrawers();
            return ;
        }

        m_intent = new Intent();
        /**透過bundle傳遞點擊側點欄項目的id*/
        m_intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, iViewId);

        switch ( iViewId ){
            /**首頁*/
            case ViewIdDef.VIEW_ID_HOME:
            {
                setIsChangeFragment(true);
                setOnParameterAndListener_Fragment(iViewId, fragment);
                m_onNavigationbarInterface.changeHomeTitle(null);
            }
            break;

            /**登機證*/
            case ViewIdDef.VIEW_ID_BOARDINGPASS_EWALLET:
            {
                if (true == bLoginStatus && strLoginType.equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)) {
                    //正式會員
                    setIsChangeFragment(true);
                    ((CIBoardingPassEWalletFragment)fragment).setBoardingPassData(null, null);
                    m_onNavigationbarInterface.changeHomeTitle(strViewName);

                }else if (true == bLoginStatus && !strLoginType.equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)){
                    //非正式會員 臨時登入身份
                    if ( false == bHaveBooking && null == m_BoardPassResp ){
                        //避免畫面與動畫衝突,統一拉出參數, delay 跳轉畫面 2016/04/09 by ryan
                        //沒有行程要先findMyBooking
                        m_intent.putExtra(
                                UiMessageDef.BUNDLE_ACTIVITY_MODE,
                                CILoginActivity.LoginMode.FIND_MYBOOKING_RETRIEVE.name());
                        //告知下一頁 顯示toast訊息
                        m_intent.putExtra(UiMessageDef.BUNDLE_TOAST_MSG_TAG, getString(R.string.please_input_pnr));
                        setupUIChangeSetting(setting, CILoginActivity.class, false);
                    }else {
                        //取得行程資訊
                        setIsChangeFragment(true);
                        ((CIBoardingPassEWalletFragment)fragment).setBoardingPassData(m_BoardPassResp, m_EWalletReq);
                        m_onNavigationbarInterface.changeHomeTitle(strViewName);
                    }
                }else if (false == bLoginStatus ) {
                    //沒有登入
                    if (false == bHaveBooking && null == m_BoardPassResp) {
                        //沒有行程 Guest身份進入頁面預設顯示多選項頁面的登入
                        m_intent.putExtra(
                                UiMessageDef.BUNDLE_ACTIVITY_MODE,
                                CILoginActivity.LoginMode.FIND_MYBOOKING_MEMBER.name());
                        //告知下一頁 顯示toast訊息
                        m_intent.putExtra(UiMessageDef.BUNDLE_TOAST_MSG_TAG, getString(R.string.please_login));
                        setupUIChangeSetting(setting, CILoginActivity.class, false);
                    } else {
                        //取得行程資訊
                        setIsChangeFragment(true);
                        ((CIBoardingPassEWalletFragment)fragment).setBoardingPassData(m_BoardPassResp, m_EWalletReq);
                        m_onNavigationbarInterface.changeHomeTitle(strViewName);
                    }
                }
                m_EWalletReq = null;
                m_BoardPassResp = null;
            }
            break;

            /**行程管理*/
            case  ViewIdDef.VIEW_ID_MY_TRIPS:
            {
                if (true == bLoginStatus && strLoginType.equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)) {

                    //正式會員 前往我的行程頁面
                    setIsChangeFragment(true);
                    setOnParameterAndListener_Fragment(iViewId, fragment);
                    m_onNavigationbarInterface.changeHomeTitle(strViewName);
                }else if (true == bLoginStatus && !strLoginType.equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)){

                    //非正式會員 臨時登入身份
                    if ( true == bHaveBooking ){
                        //已取回行程狀態
                        setIsChangeFragment(true);
                        setOnParameterAndListener_Fragment(iViewId, fragment);
                        m_onNavigationbarInterface.changeHomeTitle(strViewName);
                    }else {
                        //避免畫面與動畫衝突,統一拉出參數, delay 跳轉畫面 2016/04/09 by ryan
                        //未取得行程資訊
                        //前往FIND_MYBOOKING with PNR/Ticket
                        m_intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE, CILoginActivity.LoginMode.FIND_MYBOOKING_RETRIEVE.name());
                        //告知下一頁 顯示toast訊息
                        m_intent.putExtra(UiMessageDef.BUNDLE_TOAST_MSG_TAG, getString(R.string.please_input_pnr));
                        setupUIChangeSetting(setting, CILoginActivity.class, false);
                    }
                }else if (false == bLoginStatus && true == bHaveBooking ){
                    //沒有登入
                    //已取回行程狀態
                    setIsChangeFragment(true);
                    setOnParameterAndListener_Fragment(iViewId, fragment);
                    m_onNavigationbarInterface.changeHomeTitle(strViewName);
                }else {
                    //避免畫面與動畫衝突,統一拉出參數, delay 跳轉畫面 2016/04/09 by ryan
                    //Guest身份進入頁面預設顯示多選項頁面的登入
                    m_intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE, CILoginActivity.LoginMode.FIND_MYBOOKING_MEMBER.name());
                    //告知下一頁 顯示toast訊息
                    m_intent.putExtra(UiMessageDef.BUNDLE_TOAST_MSG_TAG, getString(R.string.please_login));
                    setupUIChangeSetting(setting, CILoginActivity.class, false);
                }
            }
            break;

            /**里程管理*/
            case ViewIdDef.VIEW_ID_MANAGE_MILES:
            {
                if(true == bLoginStatus){
                    if (strLoginType.equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)) {
                        //正式會員
                        setIsChangeFragment(true);
                        setOnParameterAndListener_Fragment(iViewId, fragment);
                        m_onNavigationbarInterface.changeHomeTitle(strViewName);
                    } else {
                        //避免畫面與動畫衝突,統一拉出參數, delay 跳轉畫面 2016/04/09 by ryan
                        //非正式會員無法進入里程管理. 詢問是否成為正式會員
                        m_intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE, CIBecomeDynastyFlyerActivity.EMode.BECOME_DYNASTY_FLYER.name());
                        setupUIChangeSetting(setting, CIBecomeDynastyFlyerActivity.class, false);
                    }
                } else {
                    //避免畫面與動畫衝突,統一拉出參數, delay 跳轉畫面 2016/04/09 by ryan
                    //非正式會員無法進入里程管理
                    m_intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE, CILoginActivity.LoginMode.BASE.name());
                    //告知下一頁 顯示toast訊息
                    m_intent.putExtra(UiMessageDef.BUNDLE_TOAST_MSG_TAG, getString(R.string.please_login));
                    setupUIChangeSetting(setting, CILoginActivity.class, false);
                }
            }
            break;

            /**個人資料*/
            case ViewIdDef.VIEW_ID_PERSONAL_DETAIL:
                if(true == bLoginStatus){
                    if(strLoginType.equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)){
                        //正式會員
                        setIsChangeFragment(true);
                        setOnParameterAndListener_Fragment(iViewId, fragment);
                        m_onNavigationbarInterface.changeHomeTitle(strViewName);
                    } else {
                        //避免畫面與動畫衝突,統一拉出參數, delay 跳轉畫面 2016/04/09 by ryan
                        //非正式會員無法進入個人頁面. 詢問是否成為正式會員
                        m_intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE, CIBecomeDynastyFlyerActivity.EMode.BECOME_DYNASTY_FLYER.name());
                        setupUIChangeSetting(setting, CIBecomeDynastyFlyerActivity.class, false);
                    }
                } else {
                    //避免畫面與動畫衝突,統一拉出參數, delay 跳轉畫面 2016/04/09 by ryan
                    //非正式會員無法進入個人頁面
                    m_intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE, CILoginActivity.LoginMode.BASE.name());
                    //告知下一頁 顯示toast訊息
                    m_intent.putExtra(UiMessageDef.BUNDLE_TOAST_MSG_TAG, getString(R.string.please_login));
                    setupUIChangeSetting(setting, CILoginActivity.class, false);
                }
                break;

            /**訂票*/
            case ViewIdDef.VIEW_ID_BOOK_TICKET: {
                //避免畫面與動畫衝突,統一拉出參數, delay 跳轉畫面 2016/04/09 by ryan
                setupUIChangeSetting(setting, CIBookTicketActivity.class, true);
            }
            break;

            /**預辦登機*/
            case ViewIdDef.VIEW_ID_CHECK_IN:
            {
                //未登入時,取得首頁狀態, 當首頁狀態符合ChecikIn資格點選SideMenu CheckIn 需要直接進行該筆PNR的CheckIn
                boolean bIsHomePageHaveCheckIn = false;
                CIHomeStatusEntity homeStatusEntity = CIPNRStatusManager.getInstance(null).getHomeStatusFromMemory();
                if ( false == bLoginStatus && null != homeStatusEntity &&
                        homeStatusEntity.iStatus_Code == HomePage_Status.TYPE_D_ONLINE_CHECKIN &&
                        homeStatusEntity.bHaveCPRData == true ){

                    bIsHomePageHaveCheckIn = true;
                    m_arCheckInList = homeStatusEntity.CheckInResp;
                }
                //
                if (true == bLoginStatus && strLoginType.equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)) {

                    //2016-06-22 會員登入直接進入Check-In 由Check-In畫面使用卡號回補CPR
                    setupUIChangeSetting(setting, CICheckInActivity.class, true);

                }else if ( true == bIsHomePageHaveCheckIn ){
                    //2016-06-22 首頁的PNR已經達到Check-In狀態,
                    if ( homeStatusEntity.bIsNewSiteOnlineCheckIn ){
                        //新站
                        m_intent.putExtra(UiMessageDef.BUNDLE_CHECK_IN_FLIGHT_LIST, m_arCheckInList);
                        setupUIChangeSetting(setting, CICheckInActivity.class, true);
                    } else {
                        //舊站
                        m_intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, m_Context.getString(R.string.check_in));
                        String strURL = CIMainCheckInFragment.getWebOnlineCheckInUrl(homeStatusEntity.PNR_Id, homeStatusEntity.strFirst_name, homeStatusEntity.strLast_name);
                        m_intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, strURL);
                        setupUIChangeSetting(setting, CIWithoutInternetActivity.class, true);
                    }
                }else if ( true == m_bCPRfromFindMyBooking ){

                    m_bCPRfromFindMyBooking = false;
                    //2016-06-22 FindMyBooking 有找到CPR
                    m_intent.putExtra(UiMessageDef.BUNDLE_CHECK_IN_FLIGHT_LIST, m_arCheckInList);
                    setupUIChangeSetting(setting, CICheckInActivity.class, true);

                } else {
                    //如果是Guest身份就進到FindMyBooking雙選項頁面，預設為retrieve booking
                    //避免畫面與動畫衝突,統一拉出參數, delay 跳轉畫面 2016/04/09 by ryan
                    m_intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE, CILoginActivity.LoginMode.FIND_MYBOOKING_RETRIEVE.name());
                    //告知下一頁 顯示toast訊息
                    m_intent.putExtra(UiMessageDef.BUNDLE_TOAST_MSG_TAG, getString(R.string.please_input_pnr));
                    setupUIChangeSetting(setting, CILoginActivity.class, true);
                }
            }
            break;
            //2016/04/08,調整判斷邏輯，除非邏輯不同在另外拉出來寫 by ryan
            case ViewIdDef.VIEW_ID_FIGHT_STATUS:    /**航班動態*/
            {
                if(null != fragment){
                    ((CIFlightStatusFragment)fragment).setTripData(m_itineraryInfo);
                }
            }
            case ViewIdDef.VIEW_ID_TIMETABLE:       /**時刻表*/
            case ViewIdDef.VIEW_ID_EXTRA_SERVICES:  /**其他服務*/
            {
                setIsChangeFragment(true);
                setOnParameterAndListener_Fragment(iViewId, fragment);
                m_onNavigationbarInterface.changeHomeTitle(strViewName);
            }
            break;
            case ViewIdDef.VIEW_ID_E_SHOPPING:      /**免稅商品*/
            {
                iViewId = m_iCurrViewId;
                setupUIChangeSetting(setting, null, true);
                //openApp("com.chinaairlines.eshopping");
                openApp(getString(R.string.eshopping_packagename));
            }
            break;
            case ViewIdDef.VIEW_ID_PROMOTIONS:      /**獨享促銷*/
            {
                m_intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, strViewName);
                //2016-07-20 modify by ryan for 調整為同首頁的邏輯
                m_intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, CIMainNoTicketFragment.getDiscoveryUrl());
                //m_intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, getString(R.string.sidemenu_promotions_link));
                setupUIChangeSetting(setting, CIWithoutInternetActivity.class, true);
            }
            break;
            case ViewIdDef.VIEW_ID_ABOUT:           /**關於華航*/
            case ViewIdDef.VIEW_ID_SETTING:         /**設定*/
            {
                setIsChangeFragment(true);
                setOnParameterAndListener_Fragment(iViewId, fragment);
                m_onNavigationbarInterface.changeHomeTitle(strViewName);
            }
            break;
            case ViewIdDef.VIEW_ID_BAGGAGE_TRACKING:/**行李追蹤*/
            {
                iViewId = m_iCurrViewId;
                String strFirstName = "";
                String strLastName = "";
                ArrayList<CIBaggageInfoReq> arValidBagTagList = CIApplication.getBaggageInfo().checkBaggageInfo( CIApplication.getBaggageInfo().getBaggageList() );
                ArrayList<CITripListResp> arTripList = CIPNRStatusManager.getInstance(null).getAllPNRListData();
                ArrayList<String> strValidPnrList = CIApplication.getBaggageInfo().checkPNRforBaggage( arTripList );
                if ( !bLoginStatus || ( true == bLoginStatus && !CIApplication.getLoginInfo().isDynastyFlyerMember()) ) {
                    //非會員 如果有有效的PNR就使用pnr去查
                    if ( strValidPnrList.size() > 0 ){
                        arValidBagTagList.clear();
                        strFirstName = arTripList.get(0).First_Name;
                        strLastName = arTripList.get(0).Last_Name;
                    }
                } else {
                    strFirstName = CIApplication.getLoginInfo().GetUserFirstName();
                    strLastName = CIApplication.getLoginInfo().GetUserLastName();
                }

                if ( arValidBagTagList.size() <= 0 && strValidPnrList.size() <= 0 ){
                    //不需查詢直接跳
                    setupUIChangeSetting(setting, CIFindMyBaggageActivity.class, true);
                } else {
                    m_DrawerLayout.closeDrawers();
                    setupUIChangeSetting(setting, null, true);


                    CIInquiryBaggageInfoPresenter.getInstance(new CIBaggageInfoListener() {
                        @Override
                        public void onBaggageInfoByPNRAndBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoResp> arBaggageInfoListResp) {
                            //儲存資料
                            CIApplication.getBaggageInfo().setBaggageList(arBaggageInfoListResp);

                            Bitmap bitmap = ImageHandle.getScreenShot( CIMainActivity.this );
                            Bitmap blur   = ImageHandle.BlurBuilder( CIMainActivity.this, bitmap, 13.5f, 0.15f);

                            Bundle bundle = new Bundle();
                            bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG,  blur);
                            bundle.putSerializable(UiMessageDef.BUNDLE_BAGGAGE_INFO_RESP,    arBaggageInfoListResp);

                            Intent intent = new Intent();
                            intent.putExtras(bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setClass(CIMainActivity.this, CIBaggageTrackingListActivity.class);
                            startActivity(intent);

                            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                            bitmap.recycle();
                        }

                        @Override
                        public void onBaggageInfoByPNRAndBGNumError(String rt_code, String rt_msg) {

                            showDialog( m_Context.getString(R.string.warning), rt_msg, m_Context.getString(R.string.done), "", new CIAlertDialog.OnAlertMsgDialogListener() {
                                @Override
                                public void onAlertMsgDialog_Confirm() {
                                    changeActivityForResult( CIFindMyBaggageActivity.class , 0, m_intent, true);
                                }

                                @Override
                                public void onAlertMsgDialogg_Cancel() {}
                            });
                        }

                        @Override
                        public void onBaggageInfoByBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoContentResp> arDataList) {}

                        @Override
                        public void onBaggageInfoByBGNumError(String rt_code, String rt_msg) {}

                        @Override
                        public void showProgress() {
                            showProgressDialog();
                        }

                        @Override
                        public void hideProgress() {
                            hideProgressDialog();
                        }
                    }).InquiryBaggageInfoByPNRAndBGNumFromWS(
                            strFirstName,
                            strLastName,
                            strValidPnrList,
                            arValidBagTagList);
                }
            }
            break;
            case ViewIdDef.VIEW_ID_AI_SERVICE:      /**智能客服*/
            {
                iViewId = m_iCurrViewId;
                m_DrawerLayout.closeDrawers();
                setupUIChangeSetting(setting, null, true);
                CIAIServicePresenter ciAIServicePresenter = new CIAIServicePresenter( new CIAIServicePresenter.CallBack(){

                    @Override
                    public void showProgress() {
                        showProgressDialog();
                    }

                    @Override
                    public void hideProgress() {
                        hideProgressDialog();
                    }

                    @Override
                    public void onDataBinded(String webData) {

                        m_intent = new Intent();
                        m_intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, getString(R.string.menu_title_ai_service));
                        m_intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_DATA_TAG, webData);
                        m_intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_IS_SHOW_CLOSE_BTN_TAG, true);
                        changeActivityForResult( CIWithoutInternetActivity.class , 0, m_intent, true);
                    }

                    @Override
                    public void onDataFetchFeild(String msg) {
                        showDialog( m_Context.getString(R.string.warning), msg, m_Context.getString(R.string.done), "", new CIAlertDialog.OnAlertMsgDialogListener() {
                            @Override
                            public void onAlertMsgDialog_Confirm() { }

                            @Override
                            public void onAlertMsgDialogg_Cancel() {}
                        });
                    }
                });

                ciAIServicePresenter.fetchAIServiceWebData();
            }
            break;
            default:
                setIsChangeFragment(true);
                setOnParameterAndListener_Fragment(iViewId, fragment);
                m_onNavigationbarInterface.changeHomeTitle(null);
                break;
        }

        if ( true == getIsChangeFragment() ){
//            ChangeViewByReplace(iViewId, strViewName, fragment);
            CloseSideMenuChaneView(iViewId, strViewName, fragment);
            setIsChangeFragment(false);
        } else {
            m_DrawerLayout.closeDrawers();
            m_BaseHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeActivityForResult( setting.clazz, setting.requestCode, m_intent, setting.bRightSideMenu);
                }
            }, TIMER_CHANGEVIEW_DELAY_TIME);
        }

        //更新左右選單的背景顏色
        if ( null != m_onLeftMenuInterface ){
            m_onLeftMenuInterface.onSelectMenu(iViewId);
        }
        if ( null != m_onRightMenuInterface ){
            m_onRightMenuInterface.onSelectMenu(iViewId);
        }
        //如果這邊又執行CloseSideMenuChaneView() 切換Fragment 會關兩次，故註解 by Kevin 2016/6/5
        //關閉sidemenu, 統一動畫邏輯 by ryan at 2016/04/08
//        if ( setting.bColseSideMenu ){
//            CloseSideMenuChaneView();
//        }
    }

    /**
     * 設定轉換Activity頁面所需要設定值
     * @param data  設定值容器
     * @param clazz 類別
     * @param isRightSideMenu      是否為右邊的側邊欄
     */
    private void setupUIChangeSetting(ChangeUiSetting data,
                                      Class     clazz,
                                      boolean   isRightSideMenu
                                      ){
        int requestCode = 0;
        if(null == data){
            return;
        }

        if(CILoginActivity.class == clazz){
            requestCode = UiMessageDef.REQUEST_CODE_LOGIN;
        }else if(CIBecomeDynastyFlyerActivity.class == clazz){
            requestCode = UiMessageDef.REQUEST_CODE_SIGN_UP;
        }else if(m_CurrSideMenuItem.iId == ViewIdDef.VIEW_ID_CHECK_IN){
            requestCode = UiMessageDef.REQUEST_CODE_CHECK_IN;
        }
        data.clazz          = clazz;
        data.bRightSideMenu = isRightSideMenu;
        data.requestCode    = requestCode;
    }

    private void openApp(String packageName){
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if(null != intent){
            startActivity(intent);
        } else {
            if (true == GooglePlusLoginApi.checkPlayServicesShowDialog(this)) {
                Uri uri = Uri.parse("market://details?id=" + packageName);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }
    }

    /**
     * 取得是否切換首頁Fragment的值，如果true就代表要換
     * @return boolean
     */
    private boolean getIsChangeFragment(){
        return m_bIsChangeFragment;
    }

    /**
     * 設定是否切換首頁Fragment的值，如果true就代表要換
     * @param isChangeFragment 設定是否切換
     */
    private void setIsChangeFragment(boolean isChangeFragment){
        m_bIsChangeFragment = isChangeFragment;
    }

    /** 將Class 轉換成 Fragment */
    public Fragment uiNewFuncFragment(Class<?> _class) {
        try {
            if (null != _class) {
                return (Fragment) _class.newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //登入後要依回傳的view id切換fragment
        int iViewId = 0;

        Boolean bCheckInComplete = false;
        if( null != data ){
            iViewId = data.getIntExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, 0);
            String strMsg = data.getStringExtra(UiMessageDef.BUNDLE_TOAST_MSG_TAG);

            if ( !TextUtils.isEmpty(strMsg) ){
                CIToastView.makeText(m_Context, strMsg).show();
            }

            //接收findmybooking查到的登機證資料
            m_BoardPassResp = (CIBoardPassResp)data.getSerializableExtra(UiMessageDef.BUNDLE_BOARDING_PASS_DATA);
            m_EWalletReq    = (CIEWalletReq)data.getSerializableExtra(UiMessageDef.BUNDLE_EWALLET_REQUEST);

            //接收findmybooking查到的行程資料
            m_TripListResp = (CITripListResp)data.getSerializableExtra(UiMessageDef.BUNDLE_TRIP_LIST_RESP);

            //接收findmybooking查到的CPR資料
			 if( iViewId == ViewIdDef.VIEW_ID_CHECK_IN ) {
                m_arCheckInList= (ArrayList<CICheckInPax_InfoEntity>) data.getSerializableExtra(UiMessageDef.BUNDLE_CHECK_IN_FLIGHT_LIST);
                if ( null != m_arCheckInList && m_arCheckInList.size() > 0 ){
                    m_bCPRfromFindMyBooking = true;
                }
            }

            //確認是否check-in成功
            bCheckInComplete = data.getBooleanExtra(UiMessageDef.BUNDLE_CHECK_IN_RESULT, false);
        }

        if ( resultCode == RESULT_OK &&
                (requestCode == UiMessageDef.REQUEST_CODE_LOGIN || requestCode == UiMessageDef.REQUEST_CODE_SIGN_UP))
        {

            switch (iViewId){
                //2016/04/09 回到原來點擊的畫面, 應使用一開始記錄的item, 避免多處定義畫面 by ryan
                case ViewIdDef.VIEW_ID_BOARDINGPASS_EWALLET:
                case ViewIdDef.VIEW_ID_MY_TRIPS:
                case ViewIdDef.VIEW_ID_MANAGE_MILES:
                case ViewIdDef.VIEW_ID_PERSONAL_DETAIL:
                    SelectSideMenu(m_CurrSideMenuItem);
                    break;
                case ViewIdDef.VIEW_ID_CHECK_IN:
                    m_bCheckInBackHome = true;
                    SelectSideMenu(m_CurrSideMenuItem);
                    break;
                case ViewIdDef.VIEW_ID_DEMO:
                    SelectSideMenu(iViewId,
                            "",
                            uiNewFuncFragment(CIDemoFragment.class));
                    m_onNavigationbarInterface.onDemoMode();
                    break;
                default:
                    //resultCode 如果是 REQUEST_CODE_LOGIN 沒有指定回去的頁面一律回首頁
                    //TODO ci.function.HomePage.CIHomeFragment.onFragmentShow()
                    //TODO 2016-05-30 暫時不在show時更新狀態，所以由此寫入要求首頁更新
                    //TODO 待首頁如果在onFragmentShow()內有刷新，就移除refreshHomePage()
                    SelectSideMenu(m_HomeMenu);

                    if ( null != m_Interface )
                        m_Interface.scrollToTop();

                    refreshHomePage();
                    break;
            }
        } else if ( requestCode == UiMessageDef.REQUEST_CODE_LOGIN &&
                resultCode == UiMessageDef.RESULT_CODE_GO_TRIP_DETAIL ){

            //已登入 但db裡面沒有行程資料 進boarding pass前需先findmybooking
            SelectSideMenu(m_CurrSideMenuItem);

        } else if( requestCode == UiMessageDef.REQUEST_CODE_CHECK_IN) {
            //如果是從check-in登入，不論是否報到成功；或者是點擊首頁的Check In，且報到成功，就要刷新首頁
            if ( true == m_bCheckInBackHome
                    || (resultCode == RESULT_OK && iViewId == ViewIdDef.VIEW_ID_HOME) ){
                SelectSideMenu(m_HomeMenu);

                if ( null != m_Interface )
                    m_Interface.scrollToTop();

                refreshHomePage();
            }

            if ( null != m_onLeftMenuInterface ){
                m_onLeftMenuInterface.onSelectMenu(m_iCurrViewId);
            }
            if ( null != m_onRightMenuInterface ){
                m_onRightMenuInterface.onSelectMenu(m_iCurrViewId);
            }

            m_bCheckInBackHome = false;

            if ( bCheckInComplete && false == AppInfo.getInstance(m_Context).GetBoardingPassIsCloseRemind() ){
                //將目前的畫面截圖下來, 當作背景
                Bitmap bitmap = ImageHandle.getScreenShot((Activity) m_Context);
                Bitmap blur = ImageHandle.BlurBuilder(m_Context, bitmap, 13.5f, 0.15f);

                Bundle bundle = new Bundle();
                bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(m_Context, CIBoardingWithQRCodeActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.anim_alpha_in, 0);

                bitmap.recycle();
            }
        } else if ( requestCode == UiMessageDef.RESULT_CODE_LANGUAGE_SETTING && resultCode == UiMessageDef.RESULT_CODE_LANGUAGE_CHANGE ){

           SLog.d("CAL", "[CAL][onActivityResult][Language Change]");
            onLocaleChangeUpdateView();
        } else if(requestCode == UiMessageDef.REQUEST_CODE_TRIP_DETAIL && resultCode == UiMessageDef.RESULT_CODE_GO_FLIGHT_STATUS){
            if(null != data && null != data.getExtras() ){
                m_itineraryInfo = (CITripListResp_Itinerary)data.getExtras().getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);
            }
            SideMenuItem flightStatus = ViewIdDef.getInstance(m_Context).getRightMenuList().get(0).get(2);
            SelectSideMenu(flightStatus);
            m_itineraryInfo = null;
        } else {
            if ( null != m_onLeftMenuInterface ){
                m_onLeftMenuInterface.onSelectMenu(m_iCurrViewId);
            }
            if ( null != m_onRightMenuInterface ){
                m_onRightMenuInterface.onSelectMenu(m_iCurrViewId);
            }
        }

       SLog.d("CAL", "[CAL][onActivityResult]");
        super.onActivityResult(requestCode,resultCode,data);
    }

    /**
     * 如果首頁頁面存在，刷新首頁狀態
     */
    public void refreshHomePage(){
        String  strViewName = CIHomeFragment.class.getSimpleName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(strViewName);
        if ( null != fragment ){
            ((CIHomeFragment)fragment).onHomePageNoTicket();    //先清空在更新
            ((CIHomeFragment)fragment).RefreshHomePagePNRStatus();
        }
    }

    /**
     * 如果首頁頁面存在，取消刷新首頁狀態
     */
    private void cancelRefreshHomePage(){
        String  strViewName = CIHomeFragment.class.getSimpleName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(strViewName);
        if ( null != fragment ){
            ((CIHomeFragment)fragment).cancelRefreshHomePagePNRStatus();
        }
    }

    private void DoLogOutAndShowMsg(final String strMsg){

        /* 登出時因為身份會切換並且寫入CILoginInfo，
         * 首頁狀態寫入的DB時會根據CILoginInfo資訊寫入不同的Table，所以要提前停止刷新首頁狀態
         * 避免將A身份的資料寫入到B身份的Table內
         * */
        cancelRefreshHomePage();
        //refreshHomePage();    //調整一下Request的順序 避免 callback 被清掉

        //關閉sidemenu
        CloseSideMenuChaneView();

        m_BaseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToHomePage(strMsg);
            }
        }, TIMER_CHANGEVIEW_DELAY_TIME);
    }

    private void goToHomePage(String strMsg) {
        SelectSideMenu(m_HomeMenu);

        CIApplication.getLoginInfo().ClearLoginData();

        //msg有內容時才顯示提示訊息-by Ling
        if ( null != strMsg && 0 < strMsg.length() ){
            showDialog(
                    getString(R.string.warning),
                    strMsg,
                    getString(R.string.done));
        }

        //更新左邊menu個人資訊
        changePersonalView();

        if ( null != m_Interface )
            m_Interface.scrollToTop();

        //清掉Memory資料
        CIPNRStatusManager.getInstance(null).clearMemoryData_HomeStatus();

        //重新刷新的時機點一定要在更新CILoginInfo之後，如前面(執行登出動作)
        refreshHomePage();
    }

    //偵測到網路狀態改變(無網路連線)時,將顯示popupwindow
    @Override
    public void onNetworkDisconnect(){
        super.onNetworkDisconnect();
        m_Popupwindow.moveNotice((int) m_moveFactor);
    }

    DrawerLayout.DrawerListener m_DrawerListener = new DrawerLayout.DrawerListener() {

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

            m_moveFactor = ( ViewScaleDef.getInstance(m_Context).getLayoutWidth(300) * slideOffset);
            if ( drawerView.getId() == R.id.llayout_right  ){
                m_moveFactor *= -1;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            {
                m_rlayout_container.setTranslationX(m_moveFactor);
            }
            else
            {
                TranslateAnimation anim = new TranslateAnimation(m_flastTranslate, m_moveFactor, 0.0f, 0.0f);
                anim.setDuration(0);
                anim.setFillAfter(true);
                m_rlayout_container.startAnimation(anim);

                m_flastTranslate = m_moveFactor;
            }

            //如果當前有顯示的popupwindow, 隨著sidemenu收合動畫做移動
            if ( null != m_Popupwindow ){
                if ( m_Popupwindow.isShowing() ){
                    m_Popupwindow.moveNotice((int)m_moveFactor);
                }
            }

//            Log.d("sidemenu","moveFactor:" +moveFactor);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            //super.onDrawerClosed(drawerView);

            if(m_DrawerLayout.isDrawerOpen(Gravity.LEFT)) {

                // 解開左邊鎖定的狀態
                m_DrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
            } else if(m_DrawerLayout.isDrawerOpen(Gravity.RIGHT)) {

                // 解開右邊鎖定的狀態
                m_DrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
            } else {

                // 代表兩邊都被關上，以防萬一將兩邊的進行解鎖的動作
                m_DrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
                m_DrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
            }

            //m_bShowAnim =true;
            //SelectSideMenu(m_CurrSideMenuItem);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            //super.onDrawerOpened(drawerView);

            if(m_DrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                changePersonalView();
                // 若是左側被打開，便將右側給鎖在關閉的狀態
                m_DrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            } else {

                // 反之，便是將左側給鎖在關閉的狀態
                m_DrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);
            }
        }

        @Override
        public void onDrawerStateChanged(int newState) {}
    };

    OnLeftDrawerListener m_onLeftMenuListener = new OnLeftDrawerListener(){

        @Override
        public void onLeftMenuClick(SideMenuItem sideMenuItem) {

            CloseSideMenuChaneView(sideMenuItem);
        }

        @Override
        public void onLogoutClick() {

            //執行登出動作
            m_GplusApi.GoogleSignOut();
            m_FBApi.signOut();

//            DoLogOutAndShowMsg(getString(R.string.menu_log_out));
            DoLogOutAndShowMsg(null);
        }

        @Override
        public void onLoginClick() {

            final Intent intent = new Intent();
            intent.putExtra(UiMessageDef.BUNDLE_MENU_VIEW_ID_TAG, ViewIdDef.VIEW_ID_HOME);
            intent.setClass(m_Context, CILoginActivity.class);
            intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE,
                    CILoginActivity.LoginMode.BASE.name());
            CloseSideMenuChaneView();
            m_BaseHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(intent, UiMessageDef.REQUEST_CODE_LOGIN);
                    overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                }
            }, TIMER_CHANGEVIEW_DELAY_TIME);


        }

        @Override
        public void onSignUpClick() {

            final Intent intent = new Intent();
            intent.setClass(m_Context, CISignUpActivity.class);
            CloseSideMenuChaneView();
            m_BaseHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(intent, UiMessageDef.REQUEST_CODE_SIGN_UP);
                    overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

                }
            }, TIMER_CHANGEVIEW_DELAY_TIME);
        }

        @Override
        public void onBeMemberClick() {

            final Intent intent = new Intent();
            intent.setClass(m_Context, CIBecomeDynastyFlyerActivity.class);
            CloseSideMenuChaneView();
            m_BaseHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivityForResult(intent, UiMessageDef.REQUEST_CODE_SIGN_UP);
                    overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
                }
            }, TIMER_CHANGEVIEW_DELAY_TIME);
        }
    };

    OnRightDrawerListener m_onRightMenuListener = new OnRightDrawerListener(){

        @Override
        public void onRightMenuClick(final SideMenuItem sideMenuItem) {

            CloseSideMenuChaneView(sideMenuItem);
        }

        @Override
        public void onGoHomePage() {

            CloseSideMenuChaneView(m_HomeMenu);
        }
    };
 
    private void CloseSideMenuChaneView(){
//        m_BaseHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                m_DrawerLayout.closeDrawers();
//            }
//        }, TIMER_CHANGEVIEW_DELAY_TIME);
        m_DrawerLayout.closeDrawers();
    }

    private void CloseSideMenuChaneView( final int iNewViewCode, final String strNewViewName, final Fragment newFragment ){

        if( ViewIdDef.VIEW_ID_HOME == iNewViewCode || !( m_DrawerLayout.isDrawerOpen(Gravity.LEFT) || m_DrawerLayout.isDrawerOpen(Gravity.RIGHT) ) ) {
//            ChangeViewByReplace(iNewViewCode, strNewViewName, newFragment);
            ChangeViewByShow(iNewViewCode, strNewViewName, newFragment);
            CloseSideMenuChaneView(); //by Kevin 2016/6/6
        } else {
//            m_OnDrawerClosedListener = new OnDrawerClosedListener(iNewViewCode, strNewViewName, newFragment) {
//            @Override
//                public void onDrawerClosed(View drawerView) {
//                    m_DrawerLayout.removeDrawerListener(m_OnDrawerClosedListener);
//                    ChangeViewByShow(iNewViewCode, strNewViewName, newFragment);
////                    ChangeViewByReplace(iNewViewCode, strNewViewName, newFragment);
//                }
//            };
//
//            m_DrawerLayout.addDrawerListener(m_OnDrawerClosedListener);

            ChangeViewByShow(iNewViewCode, strNewViewName, newFragment);
            CloseSideMenuChaneView();
        }
    }

    private void CloseSideMenuChaneView( final SideMenuItem sideMenuItem ){

        //2016-04-05 調整SideMenu收合以及切換畫面的順序,
        //先切換完畫面後再收SideMenu,這樣才不會有重複動畫的問題,  by ryan
        SelectSideMenu(sideMenuItem);
        //m_DrawerLayout.closeDrawers();

//        m_BaseHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                SelectSideMenu(sideMenuItem);
//                //m_DrawerLayout.closeDrawers();
//            }
//        }, TIMER_CHANGEVIEW_DELAY_TIME);
    }

    private Boolean ParseGCMNotifyData( Intent intent, Boolean bonCreate ){

        Bundle extras = intent.getExtras();
        if ( null != extras) {
            m_Notify = (CINotiflyItem)intent.getSerializableExtra(CINotiflyItem.NOTIFY_INFO);
            if ( null != m_Notify ) {
               SLog.v("CAL", "[FCM] ParseGCMNotifyData ");

                if ( false == bonCreate && TextUtils.equals(CINotiflyItem.TYPE_EWALLET, m_Notify.type) ) {
                    m_bShowAnim = true;
                    SideMenuItem sideMenuItem = ViewIdDef.getInstance(m_Context).getLeftMenuList().get(0).get(0);
                    SelectSideMenu(sideMenuItem);
                    CloseSideMenuChaneView();
                    //更新推播訊息讀取狀態
                    CIApplication.getFCMManager().UpdateMsgIdToCIServer(m_Notify.msg_id);
                    return true;
                }

                if (    TextUtils.equals(CINotiflyItem.TYPE_EWALLET,        m_Notify.type) ||
                        TextUtils.equals(CINotiflyItem.TYPE_EMERGENCY_MSG,  m_Notify.type) ||
                        TextUtils.equals(CINotiflyItem.TYPE_PROMOTION_SELL, m_Notify.type) ||
                        TextUtils.equals(CINotiflyItem.TYPE_PROMOTION_VIP,  m_Notify.type) ||
                        TextUtils.equals(CINotiflyItem.TYPE_PROMOTION_WIFI, m_Notify.type) ||
                        TextUtils.equals(CINotiflyItem.TYPE_QUESTIONNAIRE,  m_Notify.type)) {

                    m_BaseHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //onReceivePushNotification(m_Notify);
                            Intent notificationReceiver = new Intent();
                            notificationReceiver.setAction(CIInternalNotificationReceiver.Notification_SHOW);
                            notificationReceiver.putExtra(CINotiflyItem.NOTIFY_INFO, m_Notify);
                           SLog.v("CAL", "[gcm] LocalBroadcastManager sendBroadcast ");
                            m_Context.sendBroadcast(notificationReceiver);
                        }
                    }, 2000);

                    return true;
                }
            }
        }

        return false;
    }
}
