package ci.ui.WebView;

import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.chinaairlines.mobile30.R;

import java.io.UnsupportedEncodingException;

import ci.function.Core.Wifi.NetworkConnectionStateReceiver;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;

/**
 * Created by jlchen on 2016/4/13.
 */
public class CIWithoutInternetActivity extends FragmentActivity
        implements NetworkConnectionStateReceiver.Callback{

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
//            //允許外部變更模式為 Logo 模式
//            if ( TextUtils.isEmpty(m_strTitle) ){
//                return true;
//            } else {
//                return false;
//            }
//            //
            return false;
        }

        @Override
        public String GetTitle() {
            return m_strTitle;
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

    private CIWebViewFragment.onWebViewFragmentListener m_onWebViewFragmentListener = new CIWebViewFragment.onWebViewFragmentListener() {

        @Override
        public void onPageFinished(WebView view, String url) {
            if (!TextUtils.isEmpty(url) && url.length() >= 4 && url.substring(0, 4).equals("http")){
                m_strURL = url;
            }
        }

        @Override
        public void onProgressChanged(int newProgress) {

            if (newProgress == 0 || newProgress == 100) {
                m_progressBar.setVisibility(View.GONE);
                m_progressBar.setProgress(0);
            } else {
                m_progressBar.setVisibility(View.VISIBLE);
                m_progressBar.setProgress(newProgress);
            }
        }

        @Override
        public void onNoInternetConnection(String url) {
//            m_viewFlipper.setInAnimation(m_Context, R.anim.anim_right_in);
//            m_viewFlipper.setOutAnimation(m_Context, R.anim.anim_left_out);
            m_viewFlipper.setDisplayedChild(DEF_NO_WIFI);
            m_flWebView.setVisibility(View.GONE);
        }

        @Override
        public void onInternetConnection() {
            m_flWebView.setVisibility(View.VISIBLE);
        }
    };

    private CIWebViewFragment.onWebViewFragmentParameter m_onWebViewFragmentParameter = new CIWebViewFragment.onWebViewFragmentParameter() {

        @Override
        public String GetUrl() {
            return m_strURL;
        }

        @Override
        public byte[] GetPostBody() {
            return m_bytePostData;
        }

        @Override
        public String GetWebData() {
            return m_strWebData;
        }

        @Override
        public boolean IsShouldOverrideUrlLoading() {
            return false;
        }

        @Override
        public boolean IsOpenApp() {
            return false;
        }
    };

    private CIWebViewFragment.onWebViewInterface m_onWebViewInterface;

    View.OnClickListener m_OnRefreshClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            m_viewFlipper.setInAnimation(m_Context, R.anim.anim_left_in);
//            m_viewFlipper.setOutAnimation(m_Context, R.anim.anim_right_out);
            ReloadWebView();
        }
    };

    /**網路狀態*/
    public static final int DEF_HAVE_WIFI       = 0;
    public static final int DEF_NO_WIFI         = 1;
    private String          m_strTitle          = "";
    private String          m_strURL            = "http://www.china-airlines.com/";
    private String          m_strWebData        = "";
    private byte[]          m_bytePostData      = null;
    private NavigationBar   m_Navigationbar     = null;
    private FrameLayout     m_flayout_Content   = null;
    private RelativeLayout  m_rlBg              = null;
    private Bitmap          m_bitmap            = null;

    private ViewFlipper     m_viewFlipper       = null;
    private FrameLayout     m_flWebView         = null;
    private ImageView       m_ivNoWifi          = null;
    private TextView        m_tvNoWifi          = null;
    private Button          m_btnRefresh        = null;

    private ProgressBar     m_progressBar 		= null;

    private Context         m_Context           = null;
    private NetworkConnectionStateReceiver m_NetworkConnectionStateReceiver = null;

    private boolean         m_bIsShowCloseButton= false;
    private boolean         m_bIsGDPRmode;
    //644336 2019 2月3月 AI/行事曆/截圖/注意事項
    private boolean         m_bIsAImode;
    private NavigationBar.onNavigationbarInterface m_onNavigationbarInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String strTitle     = getIntent().getStringExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG);
        m_bIsGDPRmode         = getIntent().getBooleanExtra(UiMessageDef.BUNDLE_WEBVIEW_TYPE_GDPR,false); // edited by kevincheng
        //644336 2019 2月3月 AI/行事曆/截圖/注意事項
        m_bIsAImode       = getIntent().getBooleanExtra(UiMessageDef.BUNDLE_WEBVIEW_TYPE_AI,false);
        String strUrl       = getIntent().getStringExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG); // edited by kevincheng
        String strPostData  = getIntent().getStringExtra(UiMessageDef.BUNDLE_WEBVIEW_POST_DATA_TAG); // edited by kevincheng
        String strWebData   = getIntent().getStringExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_DATA_TAG); // edited by kevincheng
        m_bIsShowCloseButton   = getIntent().getBooleanExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_IS_SHOW_CLOSE_BTN_TAG, false); // edited by kevincheng

        if (!TextUtils.isEmpty(strTitle)) {
            m_strTitle = strTitle;
        }

        if (!TextUtils.isEmpty(strUrl)) {  //edited by kevincheng
            m_strURL = strUrl;
        }

        if (!TextUtils.isEmpty(strPostData)) {  //edited by kevincheng
            try {
                m_bytePostData = strPostData.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(strWebData)) {  //edited by kevincheng
            m_strWebData = strWebData;
        }

        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_content );
        m_Context = this;

        m_Navigationbar     = (NavigationBar)findViewById(R.id.toolbar);
        m_flayout_Content   = (FrameLayout) findViewById(R.id.container);

        View contentview    = View.inflate(m_Context, R.layout.layout_viewflipper, null);
        m_viewFlipper       = (ViewFlipper) contentview.findViewById(R.id.viewFlipper);
        m_flayout_Content.addView(contentview);

        View vWebView       = View.inflate(m_Context, R.layout.layout_view_without_internet_webview, null);
        View vNoWifi        = View.inflate(m_Context, R.layout.layout_view_without_internet_refresh, null);
        m_viewFlipper.addView(vWebView, DEF_HAVE_WIFI);
        m_viewFlipper.addView(vNoWifi, DEF_NO_WIFI);
        m_viewFlipper.setDisplayedChild(DEF_HAVE_WIFI);

        m_flWebView         = (FrameLayout) m_viewFlipper.getChildAt(DEF_HAVE_WIFI).findViewById(R.id.fl);
        m_progressBar       = (ProgressBar) m_viewFlipper.getChildAt(DEF_HAVE_WIFI).findViewById(R.id.proBar_load);
        m_progressBar.setVisibility(View.VISIBLE);

        m_rlBg              = (RelativeLayout) m_viewFlipper.getChildAt(DEF_NO_WIFI).findViewById(R.id.rl_bg);
        m_bitmap            = ImageHandle.getLocalBitmap(m_Context, R.drawable.bg_login, 1);
        Drawable drawable   = new BitmapDrawable(m_Context.getResources(), m_bitmap);
        m_rlBg.setBackground(drawable);
        m_ivNoWifi          = (ImageView) m_viewFlipper.getChildAt(DEF_NO_WIFI).findViewById(R.id.iv_no_wifi);
        m_tvNoWifi          = (TextView) m_viewFlipper.getChildAt(DEF_NO_WIFI).findViewById(R.id.tv_no_wifi);
        m_btnRefresh        = (Button) m_viewFlipper.getChildAt(DEF_NO_WIFI).findViewById(R.id.btn_refresh);
        m_btnRefresh.setOnClickListener(m_OnRefreshClick);

        setTextSizeAndLayoutParams(ViewScaleDef.getInstance(m_Context));
        setOnParameterAndListener();
        registerFragment(getSupportFragmentManager());
    }

    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustSameScaleView(m_ivNoWifi, 103.3, 103.4);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) m_ivNoWifi.getLayoutParams();
        params.topMargin = vScaleDef.getLayoutHeight(48.3);

        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_20, m_tvNoWifi);
        params = (RelativeLayout.LayoutParams) m_tvNoWifi.getLayoutParams();
        params.width = vScaleDef.getLayoutWidth(300);
        params.topMargin = vScaleDef.getLayoutHeight(167.7);

        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_btnRefresh);
        params = (RelativeLayout.LayoutParams) m_btnRefresh.getLayoutParams();
        params.width = vScaleDef.getLayoutWidth(260);
        params.height = vScaleDef.getLayoutHeight(40);
        params.topMargin = vScaleDef.getLayoutHeight(231.7);
    }

    protected void setOnParameterAndListener() {
        NavigationBar.onNavigationbarInterface onNavigationbarInterface = m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        if(true == m_bIsShowCloseButton) {
            onNavigationbarInterface.showCloseButton();
        }
        if(true == m_bIsGDPRmode) {
            m_Navigationbar.switchBackBtn(false);
        }
    }

    protected void registerFragment(FragmentManager fragmentManager) {

        CIWebViewFragment fragment = new CIWebViewFragment();
        m_onWebViewInterface = fragment.uiSetParameterListener(
                m_onWebViewFragmentParameter,
                m_onWebViewFragmentListener);

        fragment.setM_GDPRmode(m_bIsGDPRmode);

        //644336 2019 2月3月 AI/行事曆/截圖/注意事項
        fragment.setM_AImode(m_bIsAImode);

        if (m_bIsAImode) {
            fragment.setNavBar(m_onNavigationbarInterface);
        }
        //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction
                .replace(m_flWebView.getId(), fragment)
                .commitAllowingStateLoss();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 關閉鍵盤
        InputMethodManager inputmanager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputmanager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        // 取得推播manager, 清空狀態列上的推播訊息
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        // 註冊監聽網路狀態的廣播
        m_NetworkConnectionStateReceiver = new NetworkConnectionStateReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(m_NetworkConnectionStateReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        // 反註冊監聽網路狀態的廣播
        try{
            unregisterReceiver(m_NetworkConnectionStateReceiver);
        }catch (Exception e){}
        m_NetworkConnectionStateReceiver = null;
    }

    @Override
    public void onBackPressed() {
        if(m_bIsGDPRmode) {

            CIAlertDialog dialog = new CIAlertDialog(CIWithoutInternetActivity.this, new CIAlertDialog.OnAlertMsgDialogListener()
            {
                @Override
                public void onAlertMsgDialog_Confirm() {
                   finish();
                }

                @Override
                public void onAlertMsgDialogg_Cancel() {}
            });

            dialog.uiSetTitleText(getString(R.string.warning));
            dialog.uiSetContentText(getString(R.string.exit_app_message));
            dialog.uiSetConfirmText(getString(R.string.exit));
            dialog.uiSetCancelText(getString(R.string.cancel));
            dialog.show();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
        }
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

    private void ReloadWebView(){
        m_viewFlipper.setDisplayedChild(DEF_HAVE_WIFI);
        if(null != m_bytePostData && !TextUtils.isEmpty(m_strURL)){
            m_onWebViewInterface.reloadURL(m_strURL, m_bytePostData);
        } else if(!TextUtils.isEmpty(m_strWebData)){
            m_onWebViewInterface.sendWebData(m_strWebData);
        } else if(!TextUtils.isEmpty(m_strURL)){
            m_onWebViewInterface.reloadURL(m_strURL);
        }
    }

    @Override
    public void onNetworkConnect() {
//        ReloadWebView();
    }

    @Override
    public void onNetworkDisconnect() {
        ReloadWebView();
    }
}
