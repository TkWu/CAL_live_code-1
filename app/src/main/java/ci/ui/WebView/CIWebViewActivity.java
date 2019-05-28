package ci.ui.WebView;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

import static ci.ui.WebView.CIWebViewFragment.onWebViewFragmentListener;
import static ci.ui.WebView.CIWebViewFragment.onWebViewFragmentParameter;
import static ci.ui.WebView.CIWebViewFragment.onWebViewInterface;
import static ci.ui.view.NavigationBar.onNavigationbarListener;
import static ci.ui.view.NavigationBar.onNavigationbarParameter;

/**
 * Created by jlchen on 2016/3/30.
 */
public class CIWebViewActivity extends BaseActivity {

    private onNavigationbarParameter m_onNavigationParameter = new onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_strTitle;
        }
    };

    private onNavigationbarListener m_onNavigationbarListener = new onNavigationbarListener() {

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

    private onWebViewFragmentListener m_onWebViewFragmentListener = new onWebViewFragmentListener() {

        @Override
        public void onPageFinished(WebView view, String url) {}

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
        public void onNoInternetConnection(String url) {}

        @Override
        public void onInternetConnection() {}
    };

    private onWebViewFragmentParameter m_onWebViewFragmentParameter = new onWebViewFragmentParameter() {

        @Override
        public String GetUrl() {
            return m_strURL;
        }

        @Override
        public byte[] GetPostBody() {
            return null;
        }

        @Override
        public String GetWebData() {
            return null;
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

    private onWebViewInterface m_onWebViewInterface;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    private String          m_strTitle          = "";
    private String          m_strURL            = "http://www.china-airlines.com/";
    private NavigationBar   m_Navigationbar     = null;
    private FrameLayout     m_flayout_Content   = null;

    private ProgressBar     m_progressBar 		= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String strTitle = getIntent().getStringExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG);
        String strUrl   = getIntent().getStringExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG); // edited by kevincheng
        if (null != strTitle && 0 < strTitle.length()) {
            m_strTitle = strTitle;
        }

        if (null != strUrl && 0 < strUrl.length()) {  //edited by kevincheng
            m_strURL = strUrl;
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar = (NavigationBar)findViewById(R.id.toolbar);
        m_flayout_Content = (FrameLayout) findViewById(R.id.container);

        m_progressBar = (ProgressBar) findViewById(R.id.proBar_load);
        m_progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
//        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

        CIWebViewFragment fragment = new CIWebViewFragment();
        m_onWebViewInterface = fragment.uiSetParameterListener(m_onWebViewFragmentParameter, m_onWebViewFragmentListener);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction
                .replace(m_flayout_Content.getId(), fragment)
                .commitAllowingStateLoss();

    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    protected void onLanguageChangeUpdateUI() {}
}
