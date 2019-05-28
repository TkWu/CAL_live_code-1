package ci.function.ManageMiles;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**
 * Created by kevin on 2016/3/10.
 */
public class CIRedeemMilesWebViewActivity extends BaseActivity {

    private       WebView   m_WebView       = null;
    private final String    URL             = "http://www.china-airlines.com/";
    private NavigationBar   m_Navigationbar = null;

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return getString(R.string.redeem_miles);
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
        return R.layout.activity_web_view;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar = (NavigationBar)findViewById(R.id.toolbar);
        m_WebView = (WebView) findViewById(R.id.webview);
//        m_WebView.addJavascriptInterface(object,string);
        m_WebView.loadUrl(URL);
        m_WebView.setLongClickable(false); // not work
        m_WebView.clearHistory();
        m_WebView.clearFormData();
        m_WebView.clearCache(true);
        m_WebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        m_WebView.setHorizontalScrollBarEnabled(false);
//        m_WebView.setVerticalScrollBarEnabled(false);
        m_WebView.setWebViewClient(new CustomWebViewClient());
        WebSettings websettings = m_WebView.getSettings();
        websettings.setUseWideViewPort(true);
//        websettings.setSupportZoom(false);
//        websettings.setBuiltInZoomControls(false);
        websettings.setJavaScriptEnabled(true);
        websettings.setAppCacheEnabled(false);
        websettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

    }

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
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
