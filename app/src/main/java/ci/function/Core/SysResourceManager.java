package ci.function.Core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import ci.function.Core.SLog;
import android.view.View;
import android.view.WindowManager;

import ci.function.Core.Location.ILocationListener;
import ci.function.Core.Location.SSingleLocationUpdater;


/**
 * Not necessary if we all good with remembering how to actually use the apis.
 * Created by 1500242 on 2015/6/26.
 */
public class SysResourceManager {
    private final Context m_app;

    public SysResourceManager(Context appContext) {
        this.m_app = appContext;
    }

    // >>> Network ====================
    /**
     * 取得WIFI管理物件
     * @return ConnectivityManager
     */
    public WifiManager getWifiManager() {
        return (WifiManager) m_app.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 取得連接網路管理物件
     * @return ConnectivityManager
     */
    public ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) m_app.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 判斷網路是否可用
     * @return
     */
    public boolean isNetworkAvailable() {
        boolean bConnective = false;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) m_app.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        bConnective = (null != networkInfo) && networkInfo.isConnected();
        return bConnective;
    }

    /**
     * 存取Mac位址
     * @return
     */
    public String getMacAddress() {
        WifiManager wifiManager = getWifiManager();
        WifiInfo    wInfo       = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress();
    }
    // <<< Network ====================

    // >>> Screen ====================

    /**
     * 獲取Windwos管理物件
     * @return WindowManager
     */
    public WindowManager getWindowManager() {
        return (WindowManager) m_app.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * Get current Screen Rotation state.
     * Using this to determine the is left land-spaced or right land-spaced.
     */
    public int getRotationState() {
        return getWindowManager().getDefaultDisplay().getRotation();
    }

    /**
     * Create Screen shot by specific {@link View}.
     * returns a {@link Bitmap} which we capture view shot from screen.
     */
    public Bitmap takeScreenShotWithView(View view) {
        Bitmap bitmap = null;
        view.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
    // <<< Screen ====================

    // >>> Device info ====================

    /**
     * 獲取行動電話管理物件
     * @return TelephonyManager
     */
    public TelephonyManager getTelephonyManager() {
        return (TelephonyManager) m_app.getSystemService(Context.TELEPHONY_SERVICE);
    }
    // <<< Device info ====================

    // >>> App info ====================
    /**
     * 獲取套件管理物件
     * @return PackageManager
     */
    public PackageManager getPackageManager() {
        return m_app.getPackageManager();
    }


    // >>> Location ====================
    /**
     * 獲取GPS座標管理物件
     * @return PackageManager
     */
    public LocationManager getLocationManager() {
        return (LocationManager) m_app.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * 獲取單次取得GPS座標更新管理物件
     * @return PackageManager
     */
    public SSingleLocationUpdater getLocationUpdater(ILocationListener listener) {
        return new SSingleLocationUpdater(m_app, getLocationManager(), listener);
    }

    /**
     * 判斷是否可取得GPS座標服務可用
     * @return boolean if true 就是可用
     */
    public boolean isLocationServiceAvailable() {
        LocationManager locationManager = getLocationManager();
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    // <<< Location ====================

}
