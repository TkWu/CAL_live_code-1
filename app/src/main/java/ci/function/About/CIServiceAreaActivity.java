package ci.function.About;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.ws.Models.entities.CIGlobalServiceEntity;
import ci.function.Login.api.GooglePlusLoginApi;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.AppInfo;
import ci.ui.view.NavigationBar;
import ci.ws.cores.object.GsonTool;

/**
 * Created by kevincheng on 2016/4/7.
 * 全球客服地址：提供客服資訊及Google Map服務
 */
public class CIServiceAreaActivity extends BaseActivity
    implements View.OnClickListener {
    private              NavigationBar          m_Navigationbar      = null;
    private              String                 m_strTitle           = "";
    private              WebView                m_webView            = null;
    private              TextView               m_tv_address         = null;
    private              TextView               m_tv_fax             = null;
    private              TextView               m_tv_counter_service = null;
    private              Button                 m_btnGetDirections   = null;
    private              double                 m_Lat                = 0;
    private              double                 m_Long               = 0;
    private              CIGlobalServiceEntity  m_data               = null;
    private              String                 m_jsSrc              = null;
    private static final String                 MAP_URL              = "file:///android_asset/html/GoogleMAP.html";
    private static final String                 JS_KEY               = "AndroidFunction";
    private              ProgressBar            m_progressBar        = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle   = getIntent().getExtras();
        String strBranchName = null;
        if (null != bundle) {
            strBranchName = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA_BRANCH);
        }

        if(null != strBranchName){
            m_data = GsonTool.toObject(strBranchName, CIGlobalServiceEntity.class);
        }

        if(null != m_data){
            m_strTitle = m_data.branch;
        }

        super.onCreate(savedInstanceState);
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_strTitle;
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
        public void onDemoModeClick() {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_service_area;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar         = (NavigationBar) findViewById(R.id.toolbar);
        m_tv_address            = (TextView) findViewById(R.id.tv_address_value);
        m_tv_fax                = (TextView) findViewById(R.id.tv_fax_value);
        m_tv_counter_service    = (TextView) findViewById(R.id.tv_counter_service_value);
        m_btnGetDirections      = (Button) findViewById(R.id.btn_get_directions);
        m_progressBar           = (ProgressBar) findViewById(R.id.proBar_load);
        m_webView               = (WebView) findViewById(R.id.webview);

        String webApiKey        = "key=" + getString(R.string.web_api_key);
        String mapVersion       = "v=3";
        String sensor           = "sensor=false";
        String language         = "language=";
        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
        switch (locale.toString()){
            case "zh_TW":
                language = language + "zh-TW";
                break;
            case "zh_CN":
                language = language + "zh-CN";
                break;
            case "en":
                language = language + "en";
                break;
            case "ja_JP":
                language = language + "ja";
                break;
            default:
                language = language + "zh-TW";
                break;
        }

        m_jsSrc = "https://maps.googleapis.com/maps/api/js?" + webApiKey + "&" + mapVersion + "&" + sensor + "&" + language;

        if(null != m_data){
            initData();
        } else {
            showDownloadErrorDialog("ERROR");
        }

    }

    private void initData(){
        m_Lat   = m_data.lat;
        m_Long  = m_data.lng;
        m_tv_address.setText(m_data.address);
        m_tv_fax.setText(m_data.ticket_op_fax);
        m_tv_counter_service.setText(m_data.open_time);
        m_bIsNetworkAvailable = AppInfo.getInstance(m_Context).bIsNetworkAvailable();
        if ( false == m_bIsNetworkAvailable) {
            //沒網路
            showNoWifiDialog();
            return;
        } else {
            setMapLocation();
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(findViewById(R.id.iv_locate), 34.7, 34.7);
        vScaleDef.setViewSize(findViewById(R.id.rl_fax), 300, 93.3);
        vScaleDef.setViewSize(findViewById(R.id.rl_counter_service), 300, 93.3);
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_btnGetDirections.setOnClickListener(this);
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

    protected void setMapLocation() {
        m_webView.getSettings().setJavaScriptEnabled(true);
        m_webView.addJavascriptInterface(this, JS_KEY);
        m_webView.setWebChromeClient(m_webChromeClient);
        m_webView.setClickable(true);
        m_webView.loadUrl(MAP_URL);

    }

    @JavascriptInterface
    public double getLat() {
        return m_Lat;
    }

    @JavascriptInterface
    public double getLong() {
        return m_Long;
    }

    @JavascriptInterface
    public String getJavaScriptSrc() {
        return m_jsSrc;
    }

    private void showDownloadErrorDialog(String msg){
        showDialog(getString(R.string.warning),
                msg,
                getString(R.string.confirm),
                null,
                m_alertDialoglistener);
    }

    CIAlertDialog.OnAlertMsgDialogListener m_alertDialoglistener = new CIAlertDialog.OnAlertMsgDialogListener() {
        @Override
        public void onAlertMsgDialog_Confirm() {onBackPressed();}
        @Override
        public void onAlertMsgDialogg_Cancel() {}
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_directions:

                Uri uri = Uri.parse("google.navigation:q=" + String.valueOf(m_Lat) + "," + String.valueOf(m_Long));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");

                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    if (true == GooglePlusLoginApi.checkPlayServicesShowDialog(CIServiceAreaActivity.this)) {

                        uri = Uri.parse("market://details?id=com.google.android.apps.maps");
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }

                break;
        }
    }

    WebChromeClient m_webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 0 || newProgress == 100) {
                m_progressBar.setVisibility(View.GONE);
                m_progressBar.setProgress(0);
            } else {
                m_progressBar.setVisibility(View.VISIBLE);
                m_progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    };
}
