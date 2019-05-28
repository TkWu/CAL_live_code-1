package ci.function.Core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.multidex.MultiDexApplication;

import com.chinaairlines.mobile30.R;
import com.crashlytics.android.Crashlytics;

import java.util.Calendar;

import ci.function.BaggageTrack.CIBaggageInfoManager;
import ci.function.Core.Database.CIDatabaseManager;
import ci.function.Core.Location.ILocationListener;
import ci.function.Core.Location.SSingleLocationUpdater;
import ci.ui.object.CIDeviceInfo;
import ci.ui.object.CIFCMManager;
import ci.ui.object.CILanguageInfo;
import ci.ui.object.CILoginInfo;

/**
 * Created by Kevin Cheng on 2016/3/5.
 */
public class CIApplication extends MultiDexApplication {


	private static Context            	s_context            = null;
	public static  SysResourceManager 	s_sysResourceManager = null;
	public static  CIDatabaseManager    s_dbManager          = null;

	private static CIDeviceInfo 		s_deviceInfo    	= null;
	private static CILoginInfo 			s_LoginInfo     	= null;

	private static CIFCMManager 		s_FCMManager		= null;

	private static CIBaggageInfoManager s_BaggageInfoManager= null;

	//private static CIMrqInfoManager 	s_MRQManager		= null;

	//private static MrqSdk 				s_mrqSdk;

	//private static final String clientId = "b4c18e53-957c-4f09-ad1e-8bcdf7389680";

	private static final String DEF_BROADCAST_ACTION_TEXT	= "onTimeRun";
	private static Thread.UncaughtExceptionHandler s_systemCatchExcep = null;
	@Override
	public void onCreate() {
		super.onCreate();
		s_context = getApplicationContext();
	SLog.i("CAL", "CIApplication onCreate");
//		MrqSdk mrqSdk = new MrqSdk();
//		mrqSdk.init(this, s_context.getString(R.string.mobile_rq_client_id));
//		//getMrqManager().setMrqSDK(mrqSdk);
//		getMrqManager().onMrqSdkListenerActivityCreated(mrqSdk);
//	SLog.i("CAL", "MrqSdk init");
		s_systemCatchExcep = Thread.getDefaultUncaughtExceptionHandler();
		initUncaughtExceptionHandler();
	}

	@Override
	protected void attachBaseContext(Context base) {
		s_context = base;
		super.attachBaseContext(getLanguageInfo().attachBaseContext(base));
	}

	public static Context getContext() {
		return s_context;
	}

	public static Thread.UncaughtExceptionHandler getSystemDefaultCatchExcep(){
		return s_systemCatchExcep;
	}

	/**
	 * 取得資料庫管理物件
	 * @return CIDatabaseManager
	 */
	public static synchronized CIDatabaseManager getDbManager() {
		if (null == s_dbManager) {
			s_dbManager = new CIDatabaseManager(s_context);
		}
		return s_dbManager;
	}

	/**
	 * 取得系統資源管理物件
	 * @return SysResourceManager
	 */
	public static synchronized SysResourceManager getSysResourceManager() {
		if (null == s_sysResourceManager) {
			s_sysResourceManager = new SysResourceManager(s_context);
		}
		return s_sysResourceManager;
	}
	/**
	 * 取得單次更新GPS座標物件
	 * @return SysResourceManager
	 */
	public static synchronized SSingleLocationUpdater getLocationUpdater(ILocationListener listener) {
		return new SSingleLocationUpdater(s_context,
										  getSysResourceManager().getLocationManager(),
										  listener);
	}

	/**
	 * 取得裝置相關參數
	 * @return CIDeviceInfo
	 */
	public static synchronized CIDeviceInfo getDeviceInfo() {
		if (null == s_deviceInfo) {
			s_deviceInfo = new CIDeviceInfo(s_context);
		}
		return s_deviceInfo;
	}

	/**
	 * 取得登入相關參數
	 * @return CILoginInfo
	 */
	public static synchronized CILoginInfo getLoginInfo(){
		if (null == s_LoginInfo) {
			s_LoginInfo = new CILoginInfo(s_context);
		}
		return s_LoginInfo;
	}

	/**
	 * 取得語系設定
	 * @return CILanguageInfo
	 */
	public static synchronized CILanguageInfo getLanguageInfo(){
		return new CILanguageInfo(s_context);
	}

	/**
	 * 取得行李追蹤資訊*/
	public static synchronized  CIBaggageInfoManager getBaggageInfo(){
		if (null == s_BaggageInfoManager) {
			s_BaggageInfoManager = new CIBaggageInfoManager(s_context);
		}
		return s_BaggageInfoManager;
	}

	/**
	 * 定時執行BroadcastReceiver
	 * 預設action name DEF_BROADCAST_ACTION_TEXT
	 * 預設下一個整點執行
	 * 如目前 2:33 ，下次執行時間 3:00
	 * 或目前 2:50 ，下次執行時間 3:00
	 */
	public static void onTimeRun() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Intent intent  = new Intent(DEF_BROADCAST_ACTION_TEXT);
		PendingIntent pending = PendingIntent.getBroadcast(
				getContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) getContext().getSystemService(
				Context.ALARM_SERVICE);
		alarm.cancel(pending);
		alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
	}

	/**取得 IP*/
	public static String getClentIp()
	{
		WifiInfo info = null;
		try {
			WifiManager wifi = (WifiManager) getContext()
					.getApplicationContext()
					.getSystemService(Context.WIFI_SERVICE);
			info = wifi.getConnectionInfo();
		} catch ( Exception e ){
			e.printStackTrace();
		}

		if ( null != info ){
			return intToIp(info.getIpAddress());
		} else {
			return intToIp(0);
		}
	}

	private static String intToIp(int ip)
	{
		return (ip & 0xFF) + "."
				+ ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "."
				+ ((ip >> 24) & 0xFF);
	}

	/**
	 * 取得App Version Name
	 * @return
     */
	public static String getVersionName(){
		Context context = getContext();
		return context.getString(R.string.app_version_name) + " " +context.getString(R.string.app_develop_name);
	}

//	public static MrqSdk getMrqSdk() {
//		return getMrqManager().getMrqSdk();
//	}

//	public static CIMrqInfoManager getMrqManager(){
//		if ( null == s_MRQManager ){
//			s_MRQManager = new CIMrqInfoManager();
//		}
//		return s_MRQManager;
//	}

	public  static CIFCMManager getFCMManager(){
		if ( null == s_FCMManager ){
			s_FCMManager = new CIFCMManager(s_context);
		}
		return s_FCMManager;
	}

	/**
	 * 初始化處理未被捕獲的異常
	 */
	private UnCeHandler m_catchExcep;

	public void initUncaughtExceptionHandler(){
		if(m_catchExcep != Thread.getDefaultUncaughtExceptionHandler()
				|| null == Thread.getDefaultUncaughtExceptionHandler()){
			m_catchExcep = new UnCeHandler();
			Thread.setDefaultUncaughtExceptionHandler(m_catchExcep);
		}
	}

	public class UnCeHandler implements Thread.UncaughtExceptionHandler {

		private Thread.UncaughtExceptionHandler mDefaultHandler;
		public static final String TAG = "CatchExcep";

		public UnCeHandler() {
			//獲取系統默認的UncaughtException處理器
			mDefaultHandler = CIApplication.getSystemDefaultCatchExcep();
		}

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			if (!handleException(ex) && mDefaultHandler != null) {
				//如果用戶沒有處理也讓系統默認的異常處理器來處理
				mDefaultHandler.uncaughtException(thread, ex);
			}
		}

		/**
		 * 自定義錯誤處理,收集錯誤訊息 發送錯誤報告等都在此執行.
		 *
		 * @param ex
		 * @return true:如果處理了該異常訊息;否則返回false.
		 */
		private boolean handleException(final Throwable ex) {
			if (ex == null || !ex.toString().contains(OutOfMemoryError.class.getCanonicalName())) {
				return false;
			}
			Crashlytics.logException(ex);
			Intent intent = new Intent(getContext(), CIExceptionActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, intent, 0);
			try {
				pendingIntent.send();
			}
			catch (PendingIntent.CanceledException e) {
				e.printStackTrace();
			}
			System.exit(1);

			return true;
		}
	}
}