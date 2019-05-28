package ci.ui.WebView;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by kevincheng on 2017/9/22.
 */

public class CIWebView extends WebView {

    public CIWebView(Context context) {
        super(context);
        init();
    }

    public CIWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CIWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    public void init(){

        WebSettings m_webSettings = getSettings();
        m_webSettings.setSupportZoom(true);					//縮放
        m_webSettings.setBuiltInZoomControls(true);
        m_webSettings.setJavaScriptEnabled(true);			//啟用JavaScript
        m_webSettings.setLoadsImagesAutomatically(true);
        m_webSettings.setUseWideViewPort(true);				//讓網頁以最適合的版面顯示
        m_webSettings.setLoadWithOverviewMode(true);
        m_webSettings.setDomStorageEnabled(true);
        m_webSettings.setDisplayZoomControls(false);		//不要顯示放大縮小的功能圖
        m_webSettings.setAppCacheEnabled(false);
        m_webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        m_webSettings.setSupportMultipleWindows(true);


        setLongClickable(false); // not work
        clearHistory();
        clearFormData();
        clearCache(true);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setVerticalScrollBarEnabled(false);		//隱藏ScrollBar
        setHorizontalScrollBarEnabled(false);

        setWebViewClient(webViewClient);
        setWebChromeClient(webChromeClient);
    }

    public WebViewClient webViewClient = new WebViewClient();
    public WebChromeClient webChromeClient = new WebChromeClient();
}
