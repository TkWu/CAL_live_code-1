package ci.function.Main;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.chinaairlines.mobile30.CIInternalNotificationReceiver;
import com.chinaairlines.mobile30.CINotiflyItem;
import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.function.Core.Wifi.NetworkConnectionStateReceiver;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.dialog.CIDisconnectedDialog;
import ci.ui.object.AppInfo;
import ci.ui.object.CIProgressDialog;
import ci.ui.popupwindow.CIGCMNoticePopupwindow;
import ci.ui.popupwindow.CINoInternetNoticePopupwindow;
import ci.ws.define.CIWSResultCode;


public abstract class BaseActivity extends AppCompatActivity
		implements NetworkConnectionStateReceiver.Callback, CIInternalNotificationReceiver.Callback{

	public 	Context m_Context 				= null;
	public 	Boolean m_bIsNetworkAvailable	= false;

	private NetworkConnectionStateReceiver 	m_NetworkConnectionStateReceiver = null;
	public	CINoInternetNoticePopupwindow 	m_Popupwindow 			= null;
	public	CIDisconnectedDialog 			m_DisconnectedDialog 	= null;
	private	CIProgressDialog    			m_proDlg             	= null;
	private	CIAlertDialog       			m_dialog             	= null;

	private CIInternalNotificationReceiver	m_InternalNotificationReceiver = null;
	private CIGCMNoticePopupwindow 			m_GCMNoticePopupwindow	= null;

	//2018-08-02 修正為20s
	private static final int NOTIFY_DISPLAY_TIME = 20 * 1000;

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CIApplication.getLanguageInfo().attachBaseContext(newBase));
	}

	protected Handler m_BaseHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case UiMessageDef.MSG_EXIT_APP: {
					System.exit(0);
				}
				break;
				default:
					if (bOtherHandleMessage(msg)) {
						break;
					}

					super.handleMessage(msg);
					break;
			}
		}
		private void handleMessage_RestartApp() {
			Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(
						getBaseContext().getPackageName() );
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Intent.FLAG_ACTIVITY_CLEAR_TASK
			startActivity(intent);

			finish();
		}
	};

	private CINoInternetNoticePopupwindow.NoInternetNoticePopupwindowListener m_NoInternetNoticePopupwindowListener = new CINoInternetNoticePopupwindow.NoInternetNoticePopupwindowListener() {
		@Override
		public void OnClick() {
			showNoWifiDialog();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		setOnParameterAndListener();

		registerFragment(getSupportFragmentManager());
	}

	@Override
	protected void onResume() {
		super.onResume();
	SLog.d("CAL", "[CAL][onResume]");

		// 關閉鍵盤
		InputMethodManager inputmanager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

		// 取得推播manager, 清空狀態列上的推播訊息
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		// 網路狀態
		m_bIsNetworkAvailable =  AppInfo.getInstance(m_Context).bIsNetworkAvailable();

		// 註冊監聽網路狀態的廣播
		m_NetworkConnectionStateReceiver = new NetworkConnectionStateReceiver(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(m_NetworkConnectionStateReceiver, filter);

		//App開啟時收到推播通知後, Activtity 要註冊Receiver 才會收到內部推播通知
		m_InternalNotificationReceiver = new CIInternalNotificationReceiver(this);
		registerReceiver(m_InternalNotificationReceiver, new IntentFilter(CIInternalNotificationReceiver.Notification_SHOW));
	}

	@Override
	public void onPause() {
		removeHandleMessage();
		super.onPause();

		//判断PopupWindow是不是存在，存在就把它dismiss掉
		dismissNoticePopupwindow();
		dismissGCMNoticePopupwindow();
		// 反註冊監聽網路狀態的廣播
		try{
			unregisterReceiver(m_NetworkConnectionStateReceiver);
		}catch (Exception e){}
		m_NetworkConnectionStateReceiver = null;

		//離開畫面需要反註冊Receiver
		try{
			unregisterReceiver(m_InternalNotificationReceiver);
		}catch (Exception e){}
		m_InternalNotificationReceiver = null;

		SLog.d("CAL", "[CAL][onPause]");
		System.gc();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void removeHandleMessage() {
		m_BaseHandler.removeMessages(UiMessageDef.MSG_RESTART_APP);
		m_BaseHandler.removeMessages(UiMessageDef.MSG_EXIT_APP);

		removeOtherHandleMessage();
	}

	@Override
	public void onBackPressed() {

		if( isTaskRoot() ) //最後一個Activity的話就秀離開呆耳露哥
		{
			CIAlertDialog dialog = new CIAlertDialog(this, new CIAlertDialog.OnAlertMsgDialogListener()
			{
				@Override
				public void onAlertMsgDialog_Confirm() {
					AppInfo.getInstance(m_Context).SetDemoMode(false);
					m_BaseHandler.obtainMessage(UiMessageDef.MSG_EXIT_APP).sendToTarget();
				}

				@Override
				public void onAlertMsgDialogg_Cancel() {}
			});

			dialog.uiSetTitleText(getString(R.string.warning));
			dialog.uiSetContentText(getString(R.string.exit_app_message));
			dialog.uiSetConfirmText(getString(R.string.exit));
			dialog.uiSetCancelText(getString(R.string.cancel));
			dialog.show();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != getSupportFragmentManager().getFragments()) {
				for (Fragment fragment : getSupportFragmentManager().getFragments()) {
					if (fragment instanceof BaseFragment) {
						BaseFragment mBkFragment = (BaseFragment) fragment;
						if (mBkFragment.uiOnBackPressed()) {// 若子Fragment已處理Back
							// Event，則不再往下傳遞
							return true;
						}
					}
				}
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Boolean bRetrun = super.onOptionsItemSelected(item);

		return bRetrun;
	}

	public void onLocaleChangeUpdateView(){

		//先更新Activity
		onLanguageChangeUpdateUI();

		//更新ragment
		if (null != getSupportFragmentManager().getFragments()) {
			for (Fragment fragment : getSupportFragmentManager().getFragments()) {
				if (fragment instanceof BaseFragment) {
					BaseFragment mBkFragment = (BaseFragment) fragment;
					mBkFragment.onLanguageChangeUpdateUI();
				}
			}
		}

	}

	//關閉鍵盤
	public void HidekeyBoard() {
		InputMethodManager inputmanager = (InputMethodManager) m_Context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (null != inputmanager
			&& null != getCurrentFocus()
			&& null != getCurrentFocus().getWindowToken()) {
			inputmanager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	//沒網路顯示提示訊息-按略過後沒有任何動作
	public void showNoWifiDialog(){
		if ( null == m_DisconnectedDialog || false == m_DisconnectedDialog.isShowing() ) {

			m_DisconnectedDialog = new CIDisconnectedDialog(m_Context);
			m_DisconnectedDialog.show();
		}
	}

	//沒網路顯示提示訊息-按略過後顯示無網路狀態的通知popupwindow
	public void showNoWifiDialogAndPopupwindow(){
		if ( null == m_DisconnectedDialog || false == m_DisconnectedDialog.isShowing() ){

			m_DisconnectedDialog = new CIDisconnectedDialog(m_Context, new CIDisconnectedDialog.OnDisconnectedDialogListener() {
				@Override
				public void onDisconnectedDialog_Dismiss() {
					showNoticePopupwindow();
				}
			});
			m_DisconnectedDialog.show();
		}
	}

	//顯示無網路狀態的通知popupwindow
	public void showNoticePopupwindow(){
		final int iNavigationBarHeight = ViewScaleDef.getInstance(m_Context).getLayoutHeight(56);

		if ( null != getWindow().getDecorView() ) {
			if ( null == m_Popupwindow ) {

				m_Popupwindow = new CINoInternetNoticePopupwindow(m_Context, m_NoInternetNoticePopupwindowListener);

					m_Popupwindow.showAtLocation(
						getWindow().getDecorView(),
						Gravity.LEFT | Gravity.TOP,
						0,
						getStatusBarHeight() + iNavigationBarHeight );
			}
		}
	}

	private int getStatusBarHeight(){
		int statusBarHeight = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = getResources().getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

	public void dismissNoticePopupwindow(){

		if ( null != m_Popupwindow ) {
			m_Popupwindow.dismiss();
			m_Popupwindow = null;
		}
	}

	//偵測到網路狀態改變(無網路連線)時,將顯示popupwindow
	@Override
	public void onNetworkDisconnect(){
		showNoticePopupwindow();
	}

	//偵測到網路狀態改變(恢復連線)時,將顯示的popupwindow及dialog給dismiss
	@Override
	public void onNetworkConnect(){
		if ( null != m_Popupwindow ){
			m_Popupwindow.dismissNotice();

			m_BaseHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					dismissNoticePopupwindow();
				}
			}, 500);
		}

		if ( null != m_DisconnectedDialog ){
			m_DisconnectedDialog.dismiss();
			m_DisconnectedDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		//判断PopupWindow是不是存在，存在就把它dismiss掉
		dismissNoticePopupwindow();
		dismissGCMNoticePopupwindow();
		super.onDestroy();
	}

	/**
	 * BaseActivity在{@link BaseActivity#onCreate(Bundle) onCreate()}時 設定
	 * {@link BaseActivity#setContentView(int) setContentView()}用
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

	/**
	 * 通知父BaseActivity對子{@link BaseFragment}
	 * 設定客製的「OnParameter(參數讀取)」跟「OnListener(動線通知)」介面
	 */
	protected abstract void setOnParameterAndListener();

	/**
	 * 依需求調用以下函式
	 *
	 * @param fragmentManager
	 * @see FragmentTransaction FragmentTransaction相關操作
	 */
	protected abstract void registerFragment(FragmentManager fragmentManager);

	/**
	 * 若收到Handle Message且BaseActivity不認得時， 視為子class自訂Message，可經由此Function接收通知。
	 *
	 * @param msg
	 * @return true：代表此Message已處理
	 *         <p>
	 *         false：代表此Message連子class也不認得
	 *         <p>
	 */
	protected abstract boolean bOtherHandleMessage(Message msg);

	/** 若子class有自訂Message，請經由此Function清空Message。 */
	protected abstract void removeOtherHandleMessage();

	/** 當App語言變更後, 會呼叫此介面，藉此更新畫面UI,需要重新呼叫setText*/
	protected abstract void onLanguageChangeUpdateUI();

	/**
	 * 999, 995: 授權失敗, 請重新登入
	 * MainActivity->登出並返回HomeFragment
	 *
	 * @param strErrCode
	 * @param strErrMsg
	 * @return true 已處理 false 未處理
	 */
	public Boolean isProcessWSErrorCode(String strErrCode, String strErrMsg) {

		if ( false == CIApplication.getLoginInfo().GetLoginStatus() )
			return false;

		//清空登入資料
		CIApplication.getLoginInfo().ClearLoginData();

		switch (strErrCode){
			case CIWSResultCode.ERROR_LOGOUT_AUTOMATICALLY:
			case CIWSResultCode.ERROR_AUTHORIZATION_FAILED:

				//登出, 回首頁並show dialog
				Message msg = m_BaseHandler.obtainMessage();
				msg.what = UiMessageDef.MSG_DO_LOGOUT;
				Bundle data = new Bundle();
				data.putString(UiMessageDef.BUNDLE_DIALOG_MSG_TAG, strErrMsg);
				msg.setData(data);
				m_BaseHandler.sendMessage(msg);
				return true;
		}
		return false;
	}
	/**
	 * 999, 995: 授權失敗, 請重新登入
	 * 其他Activity->登出並關閉所有的Activity(除了MainActivity之外)
	 *
	 * @param strErrCode
	 * @param strErrMsg
	 * @return true 已處理 false 未處理
	 */
	public Boolean isProcessWSErrorCodeByOtherActivity(String strErrCode, String strErrMsg){
		if ( false == CIApplication.getLoginInfo().GetLoginStatus() )
			return false;

		//清空登入資料
		CIApplication.getLoginInfo().ClearLoginData();

		switch (strErrCode){
			case CIWSResultCode.ERROR_LOGOUT_AUTOMATICALLY:
			case CIWSResultCode.ERROR_AUTHORIZATION_FAILED:
				showDialog(getString(R.string.warning),
						strErrMsg,
						getString(R.string.confirm),
						null,
						new CIAlertDialog.OnAlertMsgDialogListener() {
							@Override
							public void onAlertMsgDialog_Confirm() {
								// 強制回首頁
								Intent intent = new Intent(m_Context, CIMainActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);

								finish();
							}

							@Override
							public void onAlertMsgDialogg_Cancel() {

							}
						});
				return true;
		}
		return false;
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

	@Override
	public void onReceivePushNotification( final CINotiflyItem notiflyItem ) {
	SLog.i("CAL", "Activity onReceivePushNotification ");
		//CIGCMNotifyToastView.makeText(m_Context, notiflyItem).show();
		m_BaseHandler.post(new Runnable() {
			@Override
			public void run() {
				final int iNavigationBarHeight = ViewScaleDef.getInstance(m_Context).getLayoutHeight(56);

				if (null != getWindow().getDecorView()) {

					if ( null != m_GCMNoticePopupwindow && m_GCMNoticePopupwindow.isShowing() ){
						m_BaseHandler.removeCallbacks(m_RunCloseGCMNotify);
						m_GCMNoticePopupwindow.UpdateNotifyData(notiflyItem);
					} else {

						m_GCMNoticePopupwindow = new CIGCMNoticePopupwindow(m_Context, notiflyItem);

						m_GCMNoticePopupwindow.showAtLocation(
								getWindow().getDecorView(),
								Gravity.LEFT | Gravity.TOP,
								0,
								getStatusBarHeight() + iNavigationBarHeight);
					}
				}
			}
		});

		m_BaseHandler.postDelayed(m_RunCloseGCMNotify, NOTIFY_DISPLAY_TIME);
	}

	Runnable m_RunCloseGCMNotify = new Runnable() {
		@Override
		public void run() {
			dismissGCMNoticePopupwindow();
		}
	};

	public void dismissGCMNoticePopupwindow(){

		if ( null != m_GCMNoticePopupwindow ) {
			m_GCMNoticePopupwindow.dismiss();
			m_GCMNoticePopupwindow = null;
		}
	}
}
