package ci.ui.object;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TimeZone;


/**
 * 裝置資訊
 */
public class CIDeviceInfo {

    private Context m_context = null;

    public CIDeviceInfo(Context context) {
        this.m_context = context;
    }

    private DisplayMetrics getDisplayMetrics(Activity activity) {
        if (activity == null) {
            return null;
        }
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }


    /**
     * 取得裝置唯一識別碼 Android 2.3 以上都可獲得
     */
    public String getSerialId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Build.SERIAL;
        }
        return "";
    }

    /**
     * 取得螢幕解析度
     */
    public String getScreenSize(Activity activity) {
        if (activity == null) {
            return "";
        }
        DisplayMetrics display = getDisplayMetrics(activity);
        return display.widthPixels + "x" + display.heightPixels;
    }

    /**
     * 取得裝置品牌
     */
    public String getTradeMark() {
        return Build.BRAND;
    }

    /**
     * 取得裝置型號
     */
    public String getCodeName() {
        return Build.MODEL;
    }

    /**
     * 取得裝置版本號
     */
    public String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * MD5編碼
     */
    public static String md5String(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] cipherData = md5 != null ? md5.digest(
                str.getBytes()) : new byte[0];
        StringBuilder builder  = new StringBuilder();
        String        toHexStr = null;
        for (byte cipher : cipherData) {
            toHexStr = Integer.toHexString(cipher & 0xFF);
            builder.append(toHexStr.length() == 1 ? "0" + toHexStr : toHexStr);
        }
        return builder.toString();
    }

    /**
     * 取得裝置唯一代碼
     */
    public String getUID() {
        return md5String(getAndroidId() + m_context.getPackageName());
    }

    public String getAndroidId() {
        return Settings.Secure.getString(m_context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    /**
     * 印出所有裝置資訊
     */
    public String toString() {
        return "[CIDeviceInfo] SerialId:" + getSerialId() +
                " TradeMark:" + getTradeMark() +
                " CodeName:" + getCodeName() +
                " SystemVersion:" + getSystemVersion();
    }

    /**取得時區
     * Timezon id :: Asia/Taipei*/
    public String getTimeZoneId(){

        TimeZone tz = TimeZone.getDefault();
        return tz.getID();
    }

    /**取得時區
     * Timezon :: TimeZone GMT+08:00*/
    public String getTimeZone(){

        TimeZone tz = TimeZone.getDefault();
        return tz.getDisplayName(false, TimeZone.SHORT);
    }
}