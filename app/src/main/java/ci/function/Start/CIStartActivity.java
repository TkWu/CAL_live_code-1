package ci.function.Start;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.chinaairlines.mobile30.R;
import com.chinaairlines.mobile30.RegistrationIntentService;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.function.HomePage.CIHomePageBgManager;
import ci.function.Login.CISchemeLoginActivity;
import ci.function.Main.CIMainActivity;
import ci.ui.WebView.CIWithoutInternetActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.AppInfo;
import ci.ui.object.CIFlightTrackInfo;
import ci.ui.object.CILanguageInfo;
import ci.ui.object.CILoginInfo;
import ci.ui.object.CIModelInfo;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.object.item.CIHomeStatusEntity;
import ci.ws.Models.CICheckVersionAndAnnouncementModel;
import ci.ws.Models.CIGlobalServiceModel;
import ci.ws.Models.CIInquiryFlightBookTicketODListModel;
import ci.ws.Models.CIInquiryFlightStatusODModel;
import ci.ws.Models.CIInquiryFlightTimeTableODListModel;
import ci.ws.Models.CIInquiryStationListModel;
import ci.ws.Models.entities.CICheckVersionAndAnnouncementEntity;
import ci.ws.Models.entities.CICheckVersionAndAnnouncementResp;
import ci.ws.Presenter.CIInquiryNationalPresenter;
import ci.ws.cores.CIWSShareManager;
import io.fabric.sdk.android.Fabric;

public class CIStartActivity extends AppCompatActivity {

    //public static final int CHANGE_ACTIVITY =    1000;

    private Bundle m_Extras = null;

    protected Handler m_handler = new Handler();

    //private RelativeLayout m_root = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CIApplication.getLanguageInfo().attachBaseContext(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);

        AppInfo appInfo = AppInfo.getInstance(CIApplication.getContext());
        //CR 2017-06 加入AES加密，所以必須在起始頁面加入判斷如果資料沒有加密，就清除所有重要資料，並設定為加密
        if(!appInfo.GetIsAES()) {

            CILoginInfo loginInfo = new CILoginInfo(this);
            CIWSShareManager ciwsShareManager = new CIWSShareManager();
            CIFlightTrackInfo flightTrackInfo = new CIFlightTrackInfo(this);
            CILanguageInfo languageInfo       = CIApplication.getLanguageInfo();
            String lang = languageInfo.LoadLanguage();
            //清除前先把資料表名稱清單取出
            appInfo.clear();
            loginInfo.clear();
            ciwsShareManager.clear();
            flightTrackInfo.clear();
            //重新寫入語言,不需加密
            languageInfo.SaveLanguage(lang);
            appInfo.SetIsAES(true);
        }

        //2016.07.15 Ling - 加入Crashlytics
        Fabric.with(CIApplication.getContext(), new Crashlytics());

       SLog.d("[CAL]", "[CAL][CIStartActivity] onCreate");
       //2018-06-27 加上url Scheme for eShopping
        boolean bHasScheme = DeCodeScheme();
        AppInfo.getInstance(this).SetIsFromScheme(bHasScheme);
        //
        if ( appIsduplicateOpen() ){
           SLog.v("[CAL]", "[CAL][CIStartActivity] appIsduplicateOpen");

            //2018-06-27 加上url Scheme for eShopping
            if ( bHasScheme && AppInfo.getInstance(this).GetIsGDPR() ){
                Intent intent = new Intent();
                intent.setClass(CIStartActivity.this, CISchemeLoginActivity.class);
                startActivity(intent);
            }
            this.finish();
            return;
        }
        //取得上次儲存的語言設定, 並設定在App取不到, 則預設為英文
        CIApplication.getLanguageInfo().initialAppLanguage();

        setContentView(R.layout.activity_start);
        //
       SLog.d("[CAL]", "[CAL][CIStartActivity] Sender WS");
        //
        //取得最新的國籍表
        CIInquiryNationalPresenter.getInstance(null).InquiryNationalListFromWS();
        //取得最新的機場總表
        new CIInquiryStationListModel(null).getFromWS();
        //取得最新的Fliaht Status 起訖站對應資訊
        new CIInquiryFlightStatusODModel(null).getFromWS();
        //取得最新的Book Ticket 起訖站對應資訊
        new CIInquiryFlightBookTicketODListModel(null).getFromWS();
        //取得最新的Time Table 起訖站對應資訊
        new CIInquiryFlightTimeTableODListModel(null).getFromWS();
        //取得全球營業處資料
        new CIGlobalServiceModel(null).getFromWS();

        //隨機挑選一組背景圖
        CIHomePageBgManager.getInstance().RandomBackgroundImage();

        //重新開啟app就清除首頁Boarding Pass資料
        clearHomepageBoardingPassData();
        //
        //確認Token是否有異動
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        //
        //啟動時, 要重送推播主機需要的資料
        CIHomeStatusEntity homeStatusEntity = CIPNRStatusManager.getInstanceWithoutSetListener().getHomeStatusFromMemory();
        if ( null == homeStatusEntity ) {
            CIApplication.getFCMManager().UpdateDeviceToCIServer(null);
        } else {
            CIApplication.getFCMManager().UpdateDeviceToCIServer(homeStatusEntity.AllItineraryInfo);
        }

        //m_root = (RelativeLayout)findViewById(R.id.root);
        //
        m_Extras = this.getIntent().getExtras();
        if ( null != m_Extras ){
           SLog.v("CAL", "[gcm][CIStartActivity]  getExtras ");
        }

       SLog.d("[CAL]", "[CAL][CIStartActivity] postDelayed");
        //
        boolean isNetworkAvailable = CIApplication.getSysResourceManager().isNetworkAvailable();
        if(isNetworkAvailable) {
            new CICheckVersionAndAnnouncementModel(new CICheckVersionAndAnnouncementModel.InquiryCallback() {
                @Override
                public void onSuccess(String rt_code, String rt_msg,final CICheckVersionAndAnnouncementResp datas) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            SeparationRespData(datas);
                        }
                    });
                }

                @Override
                public void onError(String rt_code, String rt_msg) {
                    actionAfterDontNeedUpdate();
                }
            }).getInfoFrowWS();
        } else {
            actionAfterDontNeedUpdate();
        }
        //測試Crashlytics是否啟用成功用的
        //throw new RuntimeException("This is a crash");
    }

    private void actionAfterDontNeedUpdate(){
        if(!AppInfo.getInstance(this).GetIsGDPR()) {
            //沒同意過GDPR，則顯示滿版網頁
            showFillScreenGDPRWeb();
        } else {
            m_handler.postDelayed( new Runnable(){
                @Override
                public void run() {
                    changeToMainActivity();
                }
            }, 100);
        }
    }

    private void showFillScreenGDPRWeb(){
        Intent intent = new Intent(this, CIWithoutInternetActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG,getString(R.string.gdpr_title));
        bundle.putBoolean(UiMessageDef.BUNDLE_WEBVIEW_TYPE_GDPR,true);
        String url = getString(R.string.gdpr_url) + CIApplication.getDeviceInfo().getAndroidId();
        bundle.putString(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, url);
        intent.putExtras(bundle);
        startActivityForResult(intent, UiMessageDef.REQUEST_CODE_GDPR_WEB);
    }

    private void SeparationRespData(CICheckVersionAndAnnouncementResp resp) {
        CICheckVersionAndAnnouncementEntity dataForTypeA = null,dataForTypeV = null;
        for(CICheckVersionAndAnnouncementEntity data: resp){
            if(TextUtils.isEmpty(data.TYPE)) {
                continue;
            }
            if(data.TYPE.toUpperCase().equals("A")) {
                dataForTypeA = data;
            } else if(data.TYPE.toUpperCase().equals("V")) {
                dataForTypeV = data;
            }
        }
        handleDataTypeVandA(dataForTypeV, dataForTypeA);
    }

    private void handleDataTypeVandA(CICheckVersionAndAnnouncementEntity dataForTypeV,
                                     CICheckVersionAndAnnouncementEntity dataForTypeA){
        if(null != dataForTypeV && !TextUtils.isEmpty(dataForTypeV.CONTENT)) {
            if(!TextUtils.isEmpty(dataForTypeV.TYPE)
                    && dataForTypeV.IS_FORCED_UPDATE.toUpperCase().equals("Y")) {
                showNoCancelDialogForCheckVersionAndAnnouncement(dataForTypeV);
            } else {
                if(null != dataForTypeA && !TextUtils.isEmpty(dataForTypeA.CONTENT)) {
                    showDialogForCheckVersionAndAnnouncement(dataForTypeV , dataForTypeA);
                } else {
                    showDialogForCheckVersionAndAnnouncement(dataForTypeV , null);
                }
            }
        } else if(null != dataForTypeA
                && !TextUtils.isEmpty(dataForTypeA.CONTENT)) {
            showDialogForCheckVersionAndAnnouncement(dataForTypeA , null);
        } else {
            actionAfterDontNeedUpdate();
        }
    }

    private void showDialogForCheckVersionAndAnnouncement(CICheckVersionAndAnnouncementEntity data,
                                                          CICheckVersionAndAnnouncementEntity anotherData) {

        showDialog(createDialogListener(data, anotherData),data.CONTENT,getConfirmTextByDataType(data));
    }

    private void showNoCancelDialogForCheckVersionAndAnnouncement(CICheckVersionAndAnnouncementEntity data) {
        showDialogForNoCancel(createDialogListener(data, null),data.CONTENT,getConfirmTextByDataType(data));
    }

    private String getConfirmTextByDataType(CICheckVersionAndAnnouncementEntity data){
        if(data.TYPE.toUpperCase().equals("A")) {
            return getString(R.string.start_page_more);
        } else if (data.TYPE.toUpperCase().equals("V")){
            return getString(R.string.start_page_update);
        }
        return "";
    }

    private CIAlertDialog.OnAlertMsgDialogListener createDialogListener(
            final CICheckVersionAndAnnouncementEntity data,
            final CICheckVersionAndAnnouncementEntity anotherData
    ){

        return new CIAlertDialog.OnAlertMsgDialogListener() {
            @Override
            public void onAlertMsgDialog_Confirm() {
                if(!TextUtils.isEmpty(data.URL)) {
                    viewUrl(data.URL);
                }
            }

            @Override
            public void onAlertMsgDialogg_Cancel() {
                handleAnotherData();
            }

            private void handleAnotherData(){
                if(null == anotherData) {
                    actionAfterDontNeedUpdate();
                } else {
                    showDialogForCheckVersionAndAnnouncement(anotherData, null);
                }
            }
        };
    }

    private void showDialogForNoCancel(CIAlertDialog.OnAlertMsgDialogListener listener,
                                       String content,
                                       String confirm){

        CIAlertDialog dialog = new CIAlertDialog(CIStartActivity.this,
                                                 listener);
        dialog.uiSetContentText(content);
        dialog.uiSetTitleText(getString(R.string.warning));
        dialog.uiSetConfirmText(confirm);
        dialog.uiSetIsDismissByConfirm(false);
        if(!isFinishing()) {
            dialog.show();
        }
    }

    private void showDialog(CIAlertDialog.OnAlertMsgDialogListener listener,
                            String content,
                            String confirm){
        CIAlertDialog dialog = new CIAlertDialog(CIStartActivity.this,
                                                 listener);
        dialog.uiSetContentText(content);
        dialog.uiSetTitleText(getString(R.string.warning));
        dialog.uiSetConfirmText(confirm);
        dialog.uiSetCancelText(getString(R.string.start_page_cancel));
        dialog.uiSetIsDismissByConfirm(false);
        if(!isFinishing()) {
            dialog.show();
        }
    }


    private void viewUrl(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try {
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void changeToMainActivity(){
        Intent intent = new Intent();
        intent.setClass(CIStartActivity.this, CIMainActivity.class);

        //判斷user使否有勾選保持登入，若未勾選且當前為登入狀態，需進行登出
        if (!CIApplication.getLoginInfo().GetKeepLogin()){
            if (CIApplication.getLoginInfo().GetLoginStatus()){
                intent.putExtra(UiMessageDef.BUNDLE_IS_DO_LOGOUT, true);
            }
        }

        if ( null != m_Extras ){
            intent.putExtras(m_Extras);
        }
        startActivity(intent);
        finish();
    }

    private void clearHomepageBoardingPassData(){
        CIModelInfo info = new CIModelInfo(this);
        info.setHomepageBoardingPassData("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** 判斷是否重複開啟APP */
    public Boolean appIsduplicateOpen() {

        try {
            //回傳的string判斷是否為null,null就將新開啟的App關閉
            if (getCurrentActivityName(CIStartActivity.this) == null) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getCurrentActivityName(Context context) throws Exception {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(20);
        String activity = null;
        ActivityManager.RunningTaskInfo currentTaskInfo = filterApplicationTask(context, taskInfo);
        //取得現有的Activity,若大於1個回傳最上層的ClassName,若小於等於1回傳null
        if (currentTaskInfo.numActivities == 0
                || currentTaskInfo.numActivities == 1) {
            activity = currentTaskInfo.topActivity.getClassName();
        } else {
            activity = null;
        }

        return activity;
    }

    private ActivityManager.RunningTaskInfo filterApplicationTask( Context context,
                                                                   List<ActivityManager.RunningTaskInfo> taskInfo) {
        String strApplicationName = context.getPackageName();
        ActivityManager.RunningTaskInfo currentTaskInfo = null;
        for (ActivityManager.RunningTaskInfo info : taskInfo) {
            if (info.topActivity.getPackageName().equals(strApplicationName)
                    && info.baseActivity.getPackageName().equals(
                    strApplicationName)) {
                currentTaskInfo = info;
                return currentTaskInfo;
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == UiMessageDef.REQUEST_CODE_GDPR_WEB) {
            if(resultCode == RESULT_OK) {
                AppInfo.getInstance(this).SetIsGDPR(true);
                //changeToMainActivity();
                m_handler.postDelayed( new Runnable(){
                    @Override
                    public void run() {
                        changeToMainActivity();
                    }
                }, 50);

            } else {
                finish();
                System.exit(0);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected boolean DeCodeScheme(){

        Intent intent = this.getIntent();
        if ( null == intent ){
            return false;
        }

        String strScheme = getString(R.string.scheme);
        if ( !TextUtils.equals(strScheme, intent.getScheme()) ){
            return false;
        }

        String scheme = intent.getScheme();
        Uri uri = intent.getData();
        System.out.println("scheme:"+scheme);
        if(uri != null) {
            String id_1 = uri.getQueryParameter("source");
            String id_2 = uri.getQueryParameter("action");

            return (TextUtils.equals(getString(R.string.scheme_eshopping_source), id_1) && TextUtils.equals(getString(R.string.scheme_eshopping_action), id_2));
        }

        return false;
    }
}