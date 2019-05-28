package ci.function.Base;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIProgressDialog;


public abstract class BaseFragment extends Fragment {

	protected LayoutInflater m_layoutInflater;
	public Handler m_FragmentHandler = new Handler()
	{
		public void handleMessage(Message msg){
			switch (msg.what) {
			default:
				if (bOtherHandleMessage(msg)) {
					break;
				}
				super.handleMessage(msg);
				break;
			}
		}
		private void handleMessage_RestartApp()
		{
			Intent intent = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
			intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );	//Intent.FLAG_ACTIVITY_CLEAR_TASK
			getActivity().startActivity(intent);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		m_layoutInflater = inflater;
		return inflater.inflate(getLayoutResourceId(), container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		initialLayoutComponent(m_layoutInflater, view);

		ViewScaleDef vSacleDef = ViewScaleDef.getInstance(getActivity());

		setTextSizeAndLayoutParams(view, vSacleDef);

		setOnParameterAndListener(view);

		registerFragment(getChildFragmentManager());
	}

	@Override
	public void onResume() {

		//關閉鍵盤
		InputMethodManager inputmanager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanager.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);

		//取得推播manager, 清空狀態列上的推播訊息
		NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		if( !isHidden() ) {
			onFragmentResume();
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		removeHandleMessage();
//		System.gc();
		onFragmentPause();
		hideProgressDialog();
		super.onPause();
	}

	private void removeHandleMessage()
	{
		m_FragmentHandler.removeMessages(UiMessageDef.MSG_RESTART_APP);
		removeOtherHandleMessage();
	}

	//關閉鍵盤
	public void HidekeyBoard() {
		InputMethodManager inputmanager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (null != inputmanager
			&& null != getActivity().getCurrentFocus()
			&& null != getActivity().getCurrentFocus().getWindowToken()) {
			inputmanager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
		}
	}

	/**
	 * BaseFragment在
	 * {@link BaseFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
	 * onCreateView()}時 設定{@link LayoutInflater#inflate(int, ViewGroup, boolean)
	 * inflate()}用
	 * 
	 * @return 此畫面的 Layout Resource Id
	 */
	protected abstract int getLayoutResourceId();

	/**
	 * 元件初始化，靜態取得元件實體、動態建製元件實體…等
	 * 
	 * @param view
	 */
	protected abstract void initialLayoutComponent(LayoutInflater inflater, View view);

	/**
	 * 設定字型大小及版面大小
	 * @param view
	 * @param vScaleDef 請參閱{@link ViewScaleDef}
	 */
	protected abstract void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef);

	/**
	 * 通知父BaseActivity對子BaseFragment設定客製的「OnParameter(參數讀取)」跟「OnListener(
	 * 動線通知)」介面
	 *
	 * @param view
	 */
	protected abstract void setOnParameterAndListener(View view);

	/**
	 * 依需求調用以下函式
	 * 
	 * @param fragmentManager
	 * @see FragmentTransaction FragmentTransaction相關操作
	 */
	protected abstract void registerFragment(FragmentManager fragmentManager);

	/**
	 * 當{@link BaseActivity}收到Back Event時，會往下通知子ImNBaseFragment，
	 * 若有需要的子ImNBaseFragment接到後並處理完成，則{@link BaseActivity}就不會往下傳遞
	 * @return	true 代表該ImNBaseFragment已處理此Back Event<p>
	 * 			false 代表該ImNBaseFragment不處理此Back Event<p>
	 */
	public boolean uiOnBackPressed() {
		return false;
	}
	
	/**
	 * 若收到Handle Message且BaseActivity不認得時，
	 * 視為子class自訂Message，可經由此Function接收通知。
	 * @param msg
	 * @return	true：代表此Message已處理<p>
	 * 			false：代表此Message連子class也不認得<p>
	 */
	protected abstract boolean bOtherHandleMessage(Message msg);
	
	/**若子class有自訂Message，請經由此Function清空Message。*/
	protected abstract void removeOtherHandleMessage();

	/** 當App語言變更後, 會呼叫此介面，藉此更新畫面UI,需要重新呼叫setText*/
	public abstract void onLanguageChangeUpdateUI();

	/**
	 * 當Fragment show/hide時呼叫此介面。
	 * @param hidden
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {

		if( !hidden ) {
			onFragmentShow();
		} else {
			onFragmentHide();
		}
	}

	/**
	 * onResume()被呼叫，且Fragment狀態為 show，則呼叫此介面
	 */
	protected void onFragmentResume() {}

	/**
	 *  onPause()被呼叫時，呼叫此介面
	 */
	protected void onFragmentPause() {}

	/**
	 *  Fragment 狀態由 hide 轉 show 時，
	 *  呼叫此介面
	 */
	public void onFragmentShow() {}

	/**
	 *  Fragment 狀態由 show 轉 hide 時，
	 *  呼叫此介面
	 */
	public void onFragmentHide() {}

	/**丟給{@link BaseActivity}看能不能處理 WebService 的ErrorCode訊息
	 * 處理名單請查閱{@link BaseActivity#isProcessWSErrorCode(String, String)}
	 * @return  true 代表該{@link BaseActivity}已處理此訊息<p>
	 *          false 代表該{@link BaseActivity}不處理此訊息<p>
	 */
	public Boolean isProcessWSErrCode(String strErrCode, String strErrMsg) {

		if ( false == CIApplication.getLoginInfo().GetLoginStatus() )
			return false;

		if (getActivity() instanceof BaseActivity) {
			BaseActivity activity = (BaseActivity) getActivity();
			return activity.isProcessWSErrorCode(strErrCode, strErrMsg);
		}
		return false;
	}

	private 	CIProgressDialog    			m_proDlg             	= null;
	private 	CIAlertDialog       			m_dialog             	= null;
	protected void showProgressDialog(){
		showProgressDialog(null);
	}

	protected void showProgressDialog(CIProgressDialog.CIProgressDlgListener listener){
		if(true == isDetached()){
			return;
		}
		hideProgressDialog();
		if(null == m_proDlg){
			//m_proDlg = CIProgressDialog.createDialog(getContext(), listener);
			m_proDlg = new CIProgressDialog(getContext(), listener);
		}
		m_proDlg.show();
	}

	protected void hideProgressDialog(){
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

	protected void showDialog(String strTitle, String strMsg, String strConfirm,String strCancel, CIAlertDialog.OnAlertMsgDialogListener listener) {
		if(true == isDetached()){
			return;
		}
		if (null != m_dialog && true == m_dialog.isShowing()){
			m_dialog.dismiss();
		}

		if(null == listener){
			m_dialog = new CIAlertDialog(getContext(), new CIAlertDialog.OnAlertMsgDialogListener() {

				@Override
				public void onAlertMsgDialog_Confirm() {}

				@Override
				public void onAlertMsgDialogg_Cancel() {}
			});
		} else {
			m_dialog  = new CIAlertDialog(getContext(), listener);
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
}
