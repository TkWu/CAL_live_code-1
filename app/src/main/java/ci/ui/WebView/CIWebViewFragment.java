package ci.ui.WebView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.Core.SLog;
import ci.function.Login.api.GooglePlusLoginApi;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.NavigationBar;

/**
 * Created by jlchen on 2016/3/30.
 */
public class CIWebViewFragment extends BaseFragment {


    private boolean m_GDPRmode = false;
    //644336 2019 2月3月 AI/行事曆/截圖/注意事項
    private boolean m_AImode = false;
    private NavigationBar.onNavigationbarInterface m_onNavigationbarInterface;
    //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項

    public interface onWebViewFragmentParameter {
        /**WebView要處理的url*/
        String GetUrl();
        /**Post Body*/
        byte[] GetPostBody();
        /**Web Data*/
        String GetWebData();
        /**是否要攔截點擊的新link*/
        boolean IsShouldOverrideUrlLoading();
        /**是否要外開APP*/
        boolean IsOpenApp();
    }

    public interface onWebViewFragmentListener {
        /**WebView的onPageFinished*/
        void onPageFinished(WebView view, String url);
        /**通知畫面網頁的載入進度*/
        void onProgressChanged(int newProgress);
        /**通知沒有網路*/
        void onNoInternetConnection(String url);
        /**通知有網路*/
        void onInternetConnection();
    }

    public interface onWebViewInterface {
        /**重新讀取頁面*/
        void reloadURL(String strURL);
        void reloadURL(String strURL,byte[] postData);
        void sendWebData(String strWebData);
    }

    onWebViewInterface m_onWebViewInterface = new onWebViewInterface() {
        @Override
        public void reloadURL(String strURL) {
            loadUrl(strURL);
        }

        @Override
        public void sendWebData(String strWebData) {
            m_webView.loadData(strWebData, "text/html", "UTF-8");
        }

        @Override
        public void reloadURL(String strURL, byte[] postData) {
            m_webView.postUrl(strURL, postData);
        }
    };

    WebViewClient m_webvViewClient = new WebViewClient(){

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if ( null != m_onParameter ){
                if ( true == m_onParameter.IsShouldOverrideUrlLoading())
                {
                    //另開滿版webview
                    Intent intent = new Intent();
                    //intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, getString(R.string.app_name));
                    intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, getTitle()); //getString(R.string.home_page_promotion_title));
                    intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, url);
                    intent.setClass(getActivity(), CIWithoutInternetActivity.class);
                    startActivity(intent);

                    getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);

                    return true;
                }else {
                    if ( true == m_onParameter.IsOpenApp() ){
                        //開啟免稅APP
                        String packageName = getString(R.string.eshopping_packagename);//"com.chinaairlines.caleshopping";//"com.chinaairlines.eshopping";
                        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
                        if(null != intent){
                            startActivity(intent);
                        } else {
                            if (true == GooglePlusLoginApi.checkPlayServicesShowDialog(getActivity())) {
                                Uri uri = Uri.parse("market://details?id=" + packageName);
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }
                        return true;
                    }
                }
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

//        @Override
//        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//
//            //忽略https的ssl
//            handler.proceed();
//        }

        @Override
        public void onPageFinished(WebView view, String url) {

            if ( null != m_onListener) {
                m_onListener.onPageFinished(view, url);

                if (!AppInfo.getInstance(getActivity()).bIsNetworkAvailable()) {
                    m_onListener.onNoInternetConnection(url);
                }else {
                    m_onListener.onInternetConnection();
                }
            }

            super.onPageFinished(view, url);
        }

    };

    WebChromeClient m_webChromeClient = new WebChromeClient(){

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {

           SLog.d("WebView", "*** onJsAlert ***" + url + "  message : " + message);

            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            if ( null != m_onListener) {
                m_onListener.onProgressChanged(newProgress);
            }

            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

            WebView webView = new WebView(getContext());
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    m_webView.loadUrl(url);
                    return true;
                }
            });
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(webView);
            resultMsg.sendToTarget();
            return true;
        }
    };

    public onWebViewInterface uiSetParameterListener(onWebViewFragmentParameter onParameter, onWebViewFragmentListener onListener) {
        m_onParameter = onParameter;
        m_onListener = onListener;

        return m_onWebViewInterface;
    }

    //shouldOverrideUrlLoading使用
    private final static String URL_TEL 			= "tel:";
    private final static String URL_EMAIL 			= "mailto:";
    private final static String URL_YOUTUBE 		= "youtube";
    private final static String URL_FACEBOOK 		= "facebook";
    private final static String URL_PDF 			= "pdf";

    //外面傳入參數
    protected	String 							m_strUrl 		= "";
//    private byte[]                              m_byPostBody    = null;

    private     onWebViewFragmentParameter      m_onParameter 	= null;
    protected   onWebViewFragmentListener       m_onListener  	= null;

    protected 	WebView 						m_webView		= null;
    protected   WebSettings                     m_webSettings	= null;
    protected   LinearLayout                    m_linearLayout  = null;

    @Override
    protected int getLayoutResourceId(){
        return R.layout.layout_webview;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {}

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {}

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        getParams();
        m_webView = (WebView)view.findViewById(R.id.webView);

        m_webSettings = m_webView.getSettings();
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

        loadUrl(m_strUrl);
//    	m_webView.loadUrl(strUrl);

        m_webView.setLongClickable(false); // not work
        m_webView.clearHistory();
        m_webView.clearFormData();
        m_webView.clearCache(true);
        m_webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        m_webView.setVerticalScrollBarEnabled(false);		//隱藏ScrollBar
        m_webView.setHorizontalScrollBarEnabled(false);

        m_webView.setWebViewClient(m_webvViewClient);
        m_webView.setWebChromeClient(m_webChromeClient);

        if(true == m_GDPRmode) {
            m_webView.setWebViewClient(new WebViewClient());
            m_webView.setWebChromeClient(new WebChromeClient(){
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    if(TextUtils.equals(message, "OK")) {
                        getActivity().setVisible(false);
                        result.confirm();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                        return true;
                    }
                    return super.onJsAlert(view, url, message, result);
                }
            });
        }
        //644336 2019 2月3月 AI/行事曆/截圖/注意事項
        if(true == m_AImode) {
            m_webView.setWebViewClient(new WebViewClient());
            m_webView.setWebChromeClient(new WebChromeClient(){
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    if(message.contains("openwindow")) {
                        m_onNavigationbarInterface.hideBackButton();
                    }else if(message.contains("closewindow")){
                        m_onNavigationbarInterface.revealBackButton();
                    }

                    return super.onJsAlert(view, url, message, result);
                }
            });
        }
        //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項

//        m_webView.addJavascriptInterface(new JsObject(), "android");
    }

    public void setM_GDPRmode(boolean m_GDPRmode) {
        this.m_GDPRmode = m_GDPRmode;
    }

    //644336 2019 2月3月 AI/行事曆/截圖/注意事項
    public void setM_AImode(boolean m_AImode) {
        this.m_AImode = m_AImode;
    }

    public void setNavBar(NavigationBar.onNavigationbarInterface _onNavigationbarInterface) {
        this.m_onNavigationbarInterface = _onNavigationbarInterface;
    }
    //end 644336 2019 2月3月 AI/行事曆/截圖/注意事項

    /**讓繼承的子fragment覆寫此方法*/
    public void getParams(){
        if ( null == m_onParameter) {
            return;
        }
        m_strUrl = m_onParameter.GetUrl();

//        m_byPostBody = m_onParameter.GetPostBody();
    }

    /**讓繼承的子fragment覆寫此方法*/
    public void loadUrl(String strUrl) {
        if (!AppInfo.getInstance(getActivity()).bIsNetworkAvailable()) {
            if ( null != m_onListener) {
                m_onListener.onNoInternetConnection(strUrl);
            }
        }else {
            if ( null != m_onListener) {
                m_onListener.onInternetConnection();
            }
        }

        byte[] postData = null; //edited by kevincheng
        String webData  = null;
        if(null != m_onParameter){
            postData = m_onParameter.GetPostBody();
            webData  = m_onParameter.GetWebData();
        }

        if(null != postData && !TextUtils.isEmpty(strUrl)){
            m_webView.postUrl(strUrl, postData);
        } else if(!TextUtils.isEmpty(webData)){
            m_webView.loadDataWithBaseURL(null, webData, "text/html", "utf-8", null);
        } else if(!TextUtils.isEmpty(strUrl)){
            m_webView.loadUrl(strUrl);
        }
    }

    public void stopLoading(){
        m_webView.stopLoading();
    }

    public boolean canGoBack() {
        return m_webView != null && m_webView.canGoBack();
    }

    public void goBack() {
        if( m_webView != null) {
            m_webView.goBack();
        }
    }

    @Override
    public void onDestroyView() {
        if(null != m_webView){
            m_webView.setWebViewClient(null);
            m_webView.setWebChromeClient(null);
        }
        if(null != m_linearLayout){
            m_linearLayout.removeAllViews();
        }
        m_webView = null;
        m_linearLayout = null;

        super.onDestroyView();
    }

    public String getTitle(){
        return getString(R.string.home_page_promotion_title);
    }
}
