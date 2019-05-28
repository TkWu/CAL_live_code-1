package ci.ui.object;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;

import com.chinaairlines.mobile30.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ci.function.Core.CIApplication;
import ci.function.Core.EncryptValueManager;
import ci.function.Core.SLog;
import ci.ui.define.HomePage_Status;

/**
 * 用來存取APP會用到的設定參數
 */
public class AppInfo
{
    private static Context  m_context				= null;
    private static AppInfo 	m_instance 				= null;

    public static final String APP_LOG_TAG			= "CAL";

    private SharedPreferences m_spCommon 			= null;

    //SharedPreferences 檔名定義
    /** 公用設定 */
    private static final String     FILE_COMMON		            = "AppInfo";
    /** 推播總開關*/
    private static final String     KEY_GCM_SWITCH	            = "KEY_GCM_NOTIFLY";
    /** 取得行程狀態*/
    private static final String     KEY_RETRIEVE_STATUS         = "KEY_RETRIEVE_BOOKING";
    /** Boarding Pass remind*/
    private static final String     KEY_BOARDING_PASS_REMINDD   = "KEY_BOARDING_PASS_REMINDD";
    /**首頁狀態*/
    private static final String     KEY_HOME_PAGE_STATUS        = "KEY_HOME_PAGE_STATUS";
    /**開啟測試模式*/
    private static final String     KEY_DEMO_MODE               = "KEY_DEMO_MODE";
    /**是否AES加密*/
    private static final String     KEY_IS_AES                  = "KEY_IS_AES";
    /**是否同意過GDPR*/
    private static final String     KEY_IS_GDPR                = "KEY_IS_GDPR";
    /**是否已經顯示過GPS授權視窗*/
    private static final String     KEY_IS_SHOW_AUTH_GPS       = "KEY_IS_SHOW_AUTH_GPS";
    /**是否已經顯示過開啟GPS訂位服務的視窗*/
    private static final String     KEY_IS_SHOW_LOCATION_SERVICE      = "KEY_IS_SHOW_LOCATION_SERVICE";
    /**是否由Scheme開啟*/
    private static final String     KEY_IS_FROM_URL_SCHEME      = "KEY_IS_FROM_URL_SCHEME";

    //SharedPreferences 欄位預設值
    /** 推播總開關*/
    private static final boolean    DEF_GCM_SWITCH      	    = true;
    /** Boarding Pass remind*/
    private static final boolean    DEF_BOARDING_PASS_REMINDD   = false;
    /**開啟測試模式*/
    private static final boolean    DEF_DEMO_MODE               = false;
    /**首頁狀態*/
    private static final int        DEF_HOME_PAGE_STATUS        = HomePage_Status.TYPE_A_NO_TICKET;
    /** 是否AES加密*/
    private static final boolean    DEF_IS_AES      	        = false;
    private static final boolean    DEF_IS_GDPR      	        = false;
    private static final boolean    DEF_IS_SHOW_AUTH_GPS      	= false;
    private static final boolean    DEF_IS_SHOW_LOCATION_SERVICE= false;
    private static final boolean    DEF_IS_FROM_URL_SCHEME      = false;

    private String m_strVersionName		    = "1.0.0";		//版本名
    private String m_strVersionFullName	    = "1.0.0R01";	//完整版本名
    private String m_strFilePath			= "";			//外部儲存空間路徑

    public static AppInfo getInstance(Context context)
    {
        m_context = context;
        if (null == m_instance) {
            m_instance = new AppInfo();
            m_instance.initial(m_context);
        }
        return m_instance;
    }

    /** 初始化 */
    private void initial(Context context)
    {
        //設定「完整版本名」
        //版本名，即Manifest.xml中的VersionName，例如：1.0.0
        //開發名，版本控管用的名稱，例如：R01
        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (null != pinfo)
            {
                m_strVersionName = pinfo.versionName.toString();
                m_strVersionFullName = m_strVersionName + context.getString(R.string.app_develop_name);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        //設定「SharedPreference」，用來存取設定值
        m_spCommon = context.getSharedPreferences(AppInfo.FILE_COMMON, Context.MODE_PRIVATE);

        //設定「外部儲存空間路徑」，一般為「(SD卡路徑)/Android/data/(PackageName)/files」
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            m_strFilePath = null;
            //避免部分機型會取不到, 如果需要取用空間 , 請記得 加上權限
            if ( null != context.getExternalFilesDir(null) ){
                m_strFilePath = context.getExternalFilesDir(null).toString();
            }
            if (null == m_strFilePath || "".equals(m_strFilePath)) {
                // 組成getExternalFilesDir(…)需取到的路徑
                m_strFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                        "Android" + File.separator +
                        "data" + File.separator +
                        context.getPackageName() + File.separator +
                        "files";
            }
        }

    }

    /**@return 取出公用設定值*/
    public SharedPreferences getCommonSharedPreferences() {
        return m_spCommon;
    }

    /**@return 版本，例如：1.0.0*/
    public String getVersionName() {
        return m_strVersionName;
    }

    /**@return 完整版本名，例如：1.0.0R01*/
    public String getVersionFullName() {
        return m_strVersionFullName;
    }

    /**@return 取得「外部儲存空間路徑」*/
    public String getFilePath() {
        return m_strFilePath;
    }

    //------------SharedPreferences相關------------

    /**@return 取得是否加密*/
    public boolean GetIsAES()
    {
        if (null == m_spCommon) {
            return DEF_IS_AES;
        }
        return m_spCommon.getBoolean(KEY_IS_AES, DEF_IS_AES);
    }

    /**
     * 設定加密
     * @param bEnable 是否加密
     */
    public void SetIsAES(boolean bEnable)
    {
        if (null == m_spCommon) {
            return;
        }

        m_spCommon.edit().putBoolean(KEY_IS_AES, bEnable).commit();
    }

    /**@return 是否同意GDPR*/
    public boolean GetIsGDPR()
    {
        if (null == m_spCommon) {
            return DEF_IS_GDPR;
        }
        return m_spCommon.getBoolean(KEY_IS_GDPR, DEF_IS_GDPR);
    }

    /**
     * GDPR
     * @param bEnable 是否同意GDPR
     */
    public void SetIsGDPR(boolean bEnable)
    {
        if (null == m_spCommon) {
            return;
        }

        m_spCommon.edit().putBoolean(KEY_IS_GDPR, bEnable).commit();
    }


    /**@return 是否顯示過授權、開啟GPS的視窗*/
    public boolean GetIsShowAuthGPS()
    {
        if (null == m_spCommon) {
            return DEF_IS_SHOW_AUTH_GPS;
        }
        return m_spCommon.getBoolean(KEY_IS_SHOW_AUTH_GPS, DEF_IS_SHOW_AUTH_GPS);
    }

    /**
     * 是否顯示過授權GPS的視窗
     * @param bEnable 是否顯示過授權、開啟GPS的視窗
     */
    public void SetIsShowAuthGPS( boolean bEnable )
    {
        if (null == m_spCommon) {
            return;
        }

        m_spCommon.edit().putBoolean(KEY_IS_SHOW_AUTH_GPS, bEnable).commit();
    }

    /**@return 是否顯示過開啟GPS定位服務*/
    public boolean GetIsShowLocationService()
    {
        if (null == m_spCommon) {
            return DEF_IS_SHOW_LOCATION_SERVICE;
        }
        return m_spCommon.getBoolean(KEY_IS_SHOW_LOCATION_SERVICE, DEF_IS_SHOW_LOCATION_SERVICE);
    }

    /**
     * 是否顯示過開啟GPS定位服務
     * @param bEnable 是否顯示過開啟GPS定位服務
     */
    public void SetIsShowLocationService( boolean bEnable )
    {
        if (null == m_spCommon) {
            return;
        }

        m_spCommon.edit().putBoolean(KEY_IS_SHOW_LOCATION_SERVICE, bEnable).commit();
    }

    /**@return 是否由Scheme開啟app*/
    public boolean GetIsFromScheme()
    {
        if (null == m_spCommon) {
            return DEF_IS_FROM_URL_SCHEME;
        }
        return m_spCommon.getBoolean(KEY_IS_FROM_URL_SCHEME, DEF_IS_FROM_URL_SCHEME);
    }

    /**
     * @param bEnable 是否由Scheme開啟app
     */
    public void SetIsFromScheme(boolean bEnable)
    {
        if (null == m_spCommon) {
            return;
        }

        m_spCommon.edit().putBoolean(KEY_IS_FROM_URL_SCHEME, bEnable).commit();
    }

    /**
     * 設定「APP推播通知」開關
     * @param bEnable 開/關
     */
    public void SetAppNotiflySwitch(boolean bEnable)
    {
        if (null == m_spCommon) {
            return;
        }

        EncryptValueManager.setValue(m_spCommon, KEY_GCM_SWITCH, bEnable);
    }

    /**@return 「APP推播通知」是否開啟*/
    public boolean GetAppNotiflySwitch()
    {
        if (null == m_spCommon) {
            return DEF_GCM_SWITCH;
        }
        return EncryptValueManager.getBoolean(m_spCommon, KEY_GCM_SWITCH, DEF_GCM_SWITCH);
    }

    /**
     * 設定「Boarding with qrcode是否不要顯示提醒視窗」
     * @param bEnable 開/關
     */
    public void SetBoardingPassNoRemind(boolean bEnable)
    {
        if (null == m_spCommon) {
            return;
        }

        EncryptValueManager.setValue(m_spCommon, KEY_BOARDING_PASS_REMINDD, bEnable);
    }

    /**@return 「是否關閉 Boarding with qrcode提醒頁」true = 關閉, false = 不關閉*/
    public boolean GetBoardingPassIsCloseRemind()
    {
        if (null == m_spCommon) {
            return DEF_BOARDING_PASS_REMINDD;
        }
        return EncryptValueManager.getBoolean(m_spCommon, KEY_BOARDING_PASS_REMINDD, DEF_BOARDING_PASS_REMINDD);
    }

    /**
     * 設定「是否要開啟Demo模式」
     * @param bEnable 開/關
     */
    public void SetDemoMode(boolean bEnable)
    {
        if (null == m_spCommon) {
            return;
        }

        EncryptValueManager.setValue(m_spCommon, KEY_DEMO_MODE, bEnable);
    }

    /**@return 「Demo模式」是否開啟*/
    public boolean GetDemoMode()
    {
        if (null == m_spCommon) {
            return DEF_DEMO_MODE;
        }
        return EncryptValueManager.getBoolean(m_spCommon, KEY_DEMO_MODE, DEF_DEMO_MODE);
    }

    /**
     * 設定「首頁狀態」for Demo
     * @param iStatus {@link HomePage_Status}
     */
    public void SetHoemPageStatus( int iStatus )
    {
        if (null == m_spCommon) {
            return;
        }

        EncryptValueManager.setValue(m_spCommon, KEY_HOME_PAGE_STATUS, iStatus);
    }

    /**@return 「首頁狀態」for Demo*/
    public int GetHoemPageStatus()
    {
        if (null == m_spCommon) {
            return DEF_HOME_PAGE_STATUS;
        }
        return EncryptValueManager.getInt(m_spCommon, KEY_HOME_PAGE_STATUS, DEF_HOME_PAGE_STATUS);
    }
    /**
     * 檔名範例為 func01_workshop
     * @param strIconKey function_icon_key或menu_icon_key的值
     * @param strIconTag
     * @return ResourcesId
     */
    public int GetIconResourceId(String strIconKey, String strIconTag) {

        int iResourcesId = 0;
        String strIconName = "";

        strIconName = strIconKey + strIconTag;

        iResourcesId = m_context.getResources().getIdentifier(strIconName, "drawable", m_context.getPackageName());

        return iResourcesId;
    }

    /**
     * 取String.xml ResourcesId
     * @return ResourcesId
     */
    public int GetStringResourceId(String strStringKey, String strStringTag) {

        int iResourcesId = 0;
        String strStringName = "";

        strStringName = strStringKey + strStringTag;

        iResourcesId = m_context.getResources().getIdentifier(strStringName, "string", m_context.getPackageName());

        return iResourcesId;
    }

    public static int getResIdByName(String variableName , Context context ) {

        try {
            Resources res = context.getResources();
            return res.getIdentifier(variableName, "drawable", context.getPackageName() );
        } catch (Exception e) {
            return 0;
        }
    }

    /**取得檔名,移除副檔名*/
    private String getImageNameByDot( String strFullName ){

        String strName = "";
        if ( null != strFullName && 0 < strFullName.length() ){
            strName = strFullName;
            try {
                int nIdx = strFullName.indexOf(".");
                strName = strFullName.substring(0, nIdx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return strName;
    }

    /** 取得與當前時間相減後的日期 */
    public String getMinusTimeForDay(String strDay) {
        String strDate = "";

        // 先行定義時間格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd ");

        // 取得現在時間
        Date dt = new Date();

        // 新增一個Calendar,並且指定時間
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        // 減掉天數, 從當天開始算所以要在多-1
        calendar.add(Calendar.DAY_OF_MONTH, -(Integer.valueOf(strDay) - 1));
        Date tdt = calendar.getTime();// 取得加減過後的Date

        // 年月日
        strDate += sdf.format(tdt);

        return strDate;
    }

    //取今天日期
    public String getTodayDate(){
        Date date = new Date();
        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String str = bartDateFormat.format(date);
        return str;
    }

    //傳入日期資料 並與今天日期做比較 看是否已過期
    public boolean bIsExpired(String strDate){
        if ( null == strDate || 0 >= strDate.length() )
            return true;

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date today = java.sql.Date.valueOf(df.format(new Date()));
            Date date = java.sql.Date.valueOf(strDate);

            if ( (today.equals(date)) || (today.before(date)) ){
                //未過期
                return false;
            }else {
                //過期
                return true;
            }
        }catch (Exception e){
            return true;
        }
    }

    public static String ConvertDateFormatToyyyyMMdd( Calendar calendar ){

        if ( null == calendar )
            return "";

        SimpleDateFormat finalFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = calendar.getTime();
        return finalFormat.format(date);
    }

    /** 將日期字串 yyyy/MM/dd 轉換成Server規定的格式 yyyy-MM-dd HH:mm*/
    public String ConvertDateFormatToyyyyMMddHHmm(String strDay) {
        if ( null == strDay || 0 >= strDay.length() )
            return "";

        // 定義時間格式
        SimpleDateFormat sdfA = new SimpleDateFormat("yyyy/MM/dd aa hh:mm:ss");
        SimpleDateFormat sdfB = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            String strDate = sdfB.format(sdfA.parse(strDay));

            return strDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return strDay;
        }
    }

    /** 將日期字串 yyyy/MM/dd 轉換成Server規定的格式 yyyy-MM-dd */
    public String ConvertDateFormat(String strDay) {
        if ( null == strDay || 0 >= strDay.length() )
            return "";

        // 定義時間格式
        SimpleDateFormat sdfA = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdfB = new SimpleDateFormat("yyyy-MM-dd");

        try {
            String strDate = sdfB.format(sdfA.parse(strDay));

            return strDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return strDay;
        }
    }

    /** 將日期字串 yyyy-MM-dd 轉換成zeplin定義的格式MMM d, yyyy */
    public String ConvertDateFormatByMMMdyyyy(String strDay) {
        if ( null == strDay || 0 >= strDay.length() )
            return "";

        // 定義時間格式
        SimpleDateFormat sdfA = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfB = new SimpleDateFormat("MMM d, yyyy");

        try {
            String strDate = sdfB.format(sdfA.parse(strDay));

            return strDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return strDay;
        }
    }

    /** 將日期字串 yyyy-MM-dd 轉換成zeplin定義的格式MMM, yyyy */
    public String ConvertDateFormatByMMMyyyy(String strDay) {
        if ( null == strDay || 0 >= strDay.length() )
            return "";

        // 定義時間格式
        SimpleDateFormat sdfA = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfB = new SimpleDateFormat("MMM, yyyy");

        try {
            String strDate = sdfB.format(sdfA.parse(strDay));

            return strDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return strDay;
        }
    }

    /** 將日期字串 yyyy-MM-dd 轉換成zeplin定義的格式EEE, MMM d, yyyy */
    public String ConvertDateFormatByEEEMMMdyyyy(String strDay) {
        if ( null == strDay || 0 >= strDay.length() )
            return "";

        // 定義時間格式
        SimpleDateFormat sdfA = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfB = null;
        if ( Locale.ENGLISH == m_context.getResources().getConfiguration().locale ){
            sdfB = new SimpleDateFormat("EEE, MMM d, yyyy");
        } else {
            sdfB = new SimpleDateFormat("EEEE, MMM d, yyyy");
        }


        try {
            String strDate = sdfB.format(sdfA.parse(strDay));

            return strDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return strDay;
        }
    }

    public String ConvertDateFormatToEEEMMMdyyyy( Calendar calendar ){

        if ( null == calendar )
            return "";

        String strFormat = "";
        if ( Locale.ENGLISH == CIApplication.getContext().getResources().getConfiguration().locale ){
            strFormat = "EEE, MMM d, yyyy";
        } else {
            strFormat = "EEEE, MMM d, yyyy";
        }

        SimpleDateFormat finalFormat = new SimpleDateFormat(strFormat);
        Date date = calendar.getTime();
        return finalFormat.format(date);
    }

    public static String ConvertDateFormatToMMMdyyyy( Calendar calendar ){

        if ( null == calendar )
            return "";

        SimpleDateFormat finalFormat = new SimpleDateFormat("MMM d, yyyy");
        Date date = calendar.getTime();
        return finalFormat.format(date);
    }

    public String ConvertDateFormatToyyyyMMddEEE( Calendar calendar ){

        if ( null == calendar )
            return "";

        String strFormat = "";
        if ( Locale.ENGLISH == CIApplication.getContext().getResources().getConfiguration().locale ) {
            strFormat = "yyyy-MM-dd,EEE";
        } else {
            //2016-08-24 Modifly by Ryan for 調整為顯示 週三
            strFormat = "yyyy-MM-dd,E";
            //strFormat = "yyyy-MM-dd, EEEE";
        }

        SimpleDateFormat finalFormat = new SimpleDateFormat(strFormat);
        Date date = calendar.getTime();
        return finalFormat.format(date);
    }

    public String ConvertDateFormatToyyyyMMddEEE(String strDay) {
        if ( null == strDay || 0 >= strDay.length() )
            return "";

        // 定義時間格式
        SimpleDateFormat sdfA = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfB = null;
        if ( Locale.ENGLISH == m_context.getResources().getConfiguration().locale ){
            sdfB = new SimpleDateFormat("yyyy-MM-dd,EEE");
        } else {
            //2016-08-24 Modifly by Ryan for 調整為顯示 週三
            sdfB = new SimpleDateFormat("yyyy-MM-dd,E");
            //sdfB = new SimpleDateFormat("yyyy-MM-dd, EEEE");
        }

        try {
            String strDate = sdfB.format(sdfA.parse(strDay));

            return strDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return strDay;
        }
    }

    /** 將日期字串 yyyy-MM-dd 轉換成EEE */
    public String ConvertDateFormatByEEE(String strDay) {
        if ( null == strDay || 0 >= strDay.length() )
            return "";

        // 定義時間格式
        SimpleDateFormat sdfA = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfB;

        if ( Locale.ENGLISH == m_context.getResources().getConfiguration().locale ){
            sdfB = new SimpleDateFormat("EEE");
        } else {
            sdfB = new SimpleDateFormat("E");
        }

        try {
            String strDate = sdfB.format(sdfA.parse(strDay));

            return strDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return strDay;
        }
    }

    /** 將日期字串 yyyy-MM-dd 轉換成dd */
    public String ConvertDateFormatBydd(String strDay) {
        if ( null == strDay || 0 >= strDay.length() )
            return "";

        // 定義時間格式
        SimpleDateFormat sdfA = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfB = new SimpleDateFormat("dd");;

        try {
            String strDate = sdfB.format(sdfA.parse(strDay));

            return strDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return strDay;
        }
    }

    //start of 644336 2019 2月3月 AI/行事曆/截圖/注意事項
    /** 將日期字串 yyyy-MM-dd HH24:dd 轉換成date */
    public Date ConvertStringtoDate(String strDay) {
        if ( null == strDay || 0 >= strDay.length() )
            return null;

        // 定義時間格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date m_date = sdf.parse(strDay);

            return m_date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    //end of 644336 2019 2月3月 AI/行事曆/截圖/注意事項

    /**檢查是否有網路，不會送出錯誤訊息
     * @return true=有網路 false=無網路狀態**/
    public boolean bIsNetworkAvailable()
    {
        if ( m_context == null ){
            return false;
        }

        @SuppressWarnings("static-access")
        ConnectivityManager connectivityManager = (ConnectivityManager) m_context.getSystemService(m_context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if ( null == networkInfo || !networkInfo.isAvailable() )
            return false;
        else
            return true;
    }

    /**
     * 判斷app是否正在運行
     * @param packageName
     * @return
     */
    public boolean appIsRunning(String packageName)
    {
        ActivityManager am = (ActivityManager) m_context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(20);
       SLog.v(APP_LOG_TAG, "getRunningTasks taskInfo " + taskInfo.size());
        if( null != taskInfo )
        {
            for ( ActivityManager.RunningTaskInfo info : taskInfo ) {

                if( TextUtils.equals(info.topActivity.getPackageName(), packageName) )
                {
                   SLog.v(APP_LOG_TAG, "appIsRunning=Y");
                    return true;
                }
            }
        }
       SLog.v(APP_LOG_TAG, "appIsRunning=N");
        return false;
    }

    /**
     * app 是否在背景運行
     * @param packageName
     * @return
     */
    public boolean appIsBackgroundRunning(String packageName)
    {
        ActivityManager am = (ActivityManager) m_context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        if(runningAppProcesses!=null)
        {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {

                if(runningAppProcessInfo.processName.startsWith(packageName))
                {
                    return runningAppProcessInfo.importance!= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && runningAppProcessInfo.importance!= ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE; //排除无界面的app
                }
            }
        }

       SLog.v(APP_LOG_TAG, "appIsBackgroundRunning=N");
        return false;
    }

    /**檢查googleplay service 是否有安裝以及版本
     * @return 回復狀態Code*/
    public int getGooglePlayServicesisAvailableCode(){

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable( CIApplication.getContext() );

        return resultCode;
    }

    /**檢查google play service 是否有安裝以及支援
     * @return error ＝9, 認定不支援*/
    public Boolean isGooglePlayServicesAvailable(){

        int resultCode = getGooglePlayServicesisAvailableCode();
        if ( resultCode == ConnectionResult.SERVICE_INVALID ){
            return false;
        }

        return true;
    }

    public void clear(){
        m_spCommon.edit().clear().commit();
    }

    public static Date getDate(String date, String pattern){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern(pattern);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String valueFormat( String value ){
        float fValue = 0.0f;
        try {
            fValue = Float.valueOf(value);
        } catch (Exception e){
            return "error";
        }

        DecimalFormat df        = new DecimalFormat("#,###,###,##0.##");
        String        strformat = df.format(fValue);

        return strformat;
    }

    /**
     * 判斷起迄日期顯示隔日+1
     * @param strDisplayDepartureDate 起飛日期
     * @param strDisplayArrivalDate   抵達日期
     * @return 如果日期不同則秀隔日
     */
    public String getShowTomorrowDay(String strDisplayDepartureDate, String strDisplayArrivalDate){
        if(!TextUtils.isEmpty(strDisplayDepartureDate) && !TextUtils.isEmpty(strDisplayArrivalDate)
                ) {
            final String pattern = "yyyy-MM-dd";
            Date departureDate = getDate(strDisplayDepartureDate, pattern);
            Date arrivalDate = getDate(strDisplayArrivalDate, pattern);

            if(departureDate == null || arrivalDate == null) {
                return "";
            }

            long diff = arrivalDate.getTime() - departureDate.getTime();
            if(diff <= 0) {
                return "";
            } else {
              long day = diff / ( 1000 * 60 * 60 * 24);
              return String.format("%1$s%2$s", "+", day);
            }
        } else {
            return "";
        }
    }
}
