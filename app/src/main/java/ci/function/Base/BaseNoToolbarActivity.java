package ci.function.Base;

import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Core.Wifi.NetworkConnectionStateReceiver;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.dialog.CIDisconnectedDialog;
import ci.ui.object.CIProgressDialog;
import ci.ws.Models.entities.CIApisList;

/**
 * Created by jlchen on 2016/7/22.
 */
public abstract class BaseNoToolbarActivity extends FragmentActivity
        implements NetworkConnectionStateReceiver.Callback{

    public Context m_Context = null;
    private	CIProgressDialog    			m_proDlg             	= null;
    private	CIAlertDialog       			m_dialog             	= null;
    protected Context                       m_applicaitonContext    = CIApplication.getContext();
    private NetworkConnectionStateReceiver 	m_NetworkConnectionStateReceiver = null;
    CIDisconnectedDialog                    m_DisconnectedDialog = null;
    protected Bundle                        m_savedInstanceState = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_savedInstanceState = savedInstanceState;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
//			window.setFlags(
//					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        m_Context = this;

        setContentView(getLayoutResourceId());

        initialLayoutComponent();

        ViewScaleDef vScaleDef = ViewScaleDef.getInstance(this);

        setTextSizeAndLayoutParams(vScaleDef);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CIApplication.getLanguageInfo().attachBaseContext(newBase));
    }

    /**
     * BaseActivity在{@link BaseNoToolbarActivity#onCreate(Bundle) onCreate()}時 設定
     * {@link BaseNoToolbarActivity#setContentView(int) setContentView()}用
     *
     * @return 此畫面的 Layout Resource Id
     */
    protected abstract int getLayoutResourceId();

    /**
     * 元件初始化，靜態取得元件實體、動態建製元件實體…等
     */
    protected abstract void initialLayoutComponent();

    /**
     * 設定字型大小及版面所需參數
     *
     * @param vScaleDef
     *            請參閱{@link ViewScaleDef}
     */
    protected abstract void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef );

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
    protected void onPause() {
        super.onPause();
        // 反註冊監聽網路狀態的廣播
        try{
            unregisterReceiver(m_NetworkConnectionStateReceiver);
        }catch (Exception e){}
        m_NetworkConnectionStateReceiver = null;
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, R.anim.anim_alpha_out);
    }

    public void showProgressDialog(){
        showProgressDialog(null);
    }

    public void showProgressDialog(CIProgressDialog.CIProgressDlgListener listener){
        if(true == isDestroyed()){
            return;
        }

        if(null == m_proDlg){
            //m_proDlg = CIProgressDialog.createDialog(m_Context, listener);
            m_proDlg = new CIProgressDialog(m_Context, listener);
        }
        if ( m_proDlg.isShowing() ){
            return;
        }
        m_proDlg.show();
    }

    public void hideProgressDialog(){
        if(null != m_proDlg && true == m_proDlg.isShowing()){
            m_proDlg.dismiss();
        }
        m_proDlg = null;
    }

    protected void showDialog(String strTitle, String strMsg) {
        showDialog(strTitle, strMsg, getString(R.string.confirm), null, null);
    }

    protected void showDialog(String strTitle, String strMsg, String strConfirm) {
        showDialog(strTitle, strMsg, strConfirm, null, null);
    }

    protected void showDialog(String strTitle, String strMsg, String strConfirm, String strCancel, CIAlertDialog.OnAlertMsgDialogListener listener) {
        if(true == isDestroyed()){
            return;
        }
        if (null != m_dialog && true == m_dialog.isShowing()){
            m_dialog.dismiss();
        }

        if(null == listener){
            m_dialog = new CIAlertDialog(m_Context, new CIAlertDialog.OnAlertMsgDialogListener() {

                @Override
                public void onAlertMsgDialog_Confirm() {}

                @Override
                public void onAlertMsgDialogg_Cancel() {}
            });
        } else {
            m_dialog  = new CIAlertDialog(m_Context, listener);
        }

        if(false == TextUtils.isEmpty(strTitle)) {
            m_dialog.uiSetTitleText(strTitle);
        }
        if(false == TextUtils.isEmpty(strMsg)) {
            m_dialog.uiSetContentText(strMsg);
        }
        if(false == TextUtils.isEmpty(strConfirm)) {
            m_dialog.uiSetConfirmText(strConfirm);
        }
        if(false == TextUtils.isEmpty(strCancel)){
            m_dialog.uiSetCancelText(strCancel);
        }
        m_dialog.show();
    }

    public void showNoWifiDialog(){
        if ( null == m_DisconnectedDialog || false == m_DisconnectedDialog.isShowing() ) {
            m_DisconnectedDialog = new CIDisconnectedDialog(this);
            m_DisconnectedDialog.show();
        }
    }

    public void dismissNoWifiDialog(){
        if ( null != m_DisconnectedDialog && true == m_DisconnectedDialog.isShowing() ) {
            m_DisconnectedDialog.dismiss();
        }
    }

    protected boolean getIsShowProgressDialog(){
        return (null != m_proDlg && m_proDlg.isShowing());
    }

    @Override
    public void onNetworkConnect() {
        //do nothing
    }

    @Override
    public void onNetworkDisconnect() {
        //do nothing
    }
}
