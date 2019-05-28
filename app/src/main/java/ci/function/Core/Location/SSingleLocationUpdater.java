package ci.function.Core.Location;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ci.ui.object.AppInfo;


/**
 * Used for get the current location from providers,
 * receive only once location info per every request.
 * using {@link LocationListener} for callback, it will call {@code onLocationChanged}
 * Created by 1500242 on 2015/6/5.
 */
public class SSingleLocationUpdater {
    private final static int    REQUEST_COUNT                 = 1;
    private final static int    TIME_OUT                      = 25000;
    private final static String SINGLE_LOCATION_UPDATE_ACTION =
            "com.softmobile.utils.common.location.SINGLE_LOCATION_UPDATE_ACTION";

    private ILocationListener m_locationListener = null;
    private LocationManager   m_locationManager  = null;
    private PendingIntent     m_updateIntent     = null;
    private Criteria          m_criteria         = null;
    private Context           m_context          = null;
    private int               m_count            = 1; /* starts at 1 */
    private Timer             m_timer            = null;
    private Location          m_lastLocation     = null;
    public final static int  PERMISSIONS_REQUEST_CODE = 1;
    public SSingleLocationUpdater(Context context,
                                  LocationManager locationManager,
                                  ILocationListener locationListener) {
        this.m_context = context;
        this.m_locationManager = locationManager;
        this.m_locationListener = locationListener;
        this.m_timer = new Timer();

        Intent updateIntent = new Intent(SINGLE_LOCATION_UPDATE_ACTION);
        this.m_updateIntent = PendingIntent.getBroadcast(context,
                                                         0,
                                                         updateIntent,
                                                         PendingIntent.FLAG_CANCEL_CURRENT);
        if ( m_criteria == null ){
            /** specific low-accuracy for faster possible result, for lower-level permission require*/
            this.m_criteria = new Criteria();
            this.m_criteria.setAccuracy(Criteria.ACCURACY_LOW);
        }
    }

    public String GetLocationProvider(){

        String strProvider = "";
        boolean m_bGPS		= m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean m_bNetwork	= m_locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if ( m_bGPS || m_bNetwork ){
            strProvider = m_locationManager.getBestProvider(m_criteria, true);
        }

        return strProvider;
    }

    public void getLastKnownLocation(){
        m_lastLocation
                = m_locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(m_lastLocation == null)
        {
            m_lastLocation
                    = m_locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (null != m_lastLocation) {
            m_locationListener.onLocationChanged(m_lastLocation);
        }
    }

    public void requestUpdate() {
        IntentFilter locIntentFilter =
                new IntentFilter(SINGLE_LOCATION_UPDATE_ACTION);
        m_context.registerReceiver(m_singleUpdateReceiver, locIntentFilter);
        m_timer.schedule(m_timeoutTask, TIME_OUT);
        requestSingleUpdateByCriteria();

    }


    private BroadcastReceiver m_singleUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = (Location) intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);

            if (null != location) {
                m_lastLocation = location;
            }

            if (m_count < REQUEST_COUNT) {
                m_count += 1;
                requestSingleUpdateByCriteria();
            } else {
                m_timer.cancel();
                processResult();
            }
        }
    };

    private void requestSingleUpdateByCriteria(){
        try {
            m_locationManager.requestSingleUpdate(m_criteria, m_updateIntent);
        } catch (Exception e) {
            requestSingleUpdateByBestProvider();
        }
    }

    private void requestSingleUpdateByBestProvider(){
        try {
            m_locationManager.requestSingleUpdate(GetLocationProvider(), m_updateIntent);
        } catch (Exception e) {
            m_timer.cancel();
            processResult();
        }
    }

    private TimerTask m_timeoutTask = new TimerTask() {
        @Override
        public void run() {
            processResult();
        }
    };

    private void processResult() {
        m_count = 1;
        try {
            m_context.unregisterReceiver(m_singleUpdateReceiver);
        } catch (Exception e){}
        m_locationManager.removeUpdates(m_updateIntent);
        if (null != m_lastLocation) {
            m_locationListener.onLocationChanged(m_lastLocation);
        } else {
            m_locationListener.onFailed();
        }
    }

    public void cancel() {
        try{
            m_context.unregisterReceiver(m_singleUpdateReceiver);
            m_locationManager.removeUpdates(m_updateIntent);
            m_timer.cancel();
        }catch (Exception e){
            //do nothing
        }
    }

    /**
     * SDK Version 6.0以上取GPS位置需要在執行階段要求所需權限
     * @return if true 就是確認是否取得權限的回應是已經取得權限
     */
    public static boolean requestPermission(Context context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            //2018-06-22 新增授權GPS定位服務訊息僅顯示一次
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.ACCESS_FINE_LOCATION) && !AppInfo.getInstance(context).GetIsShowAuthGPS() ) {

//                CIToastView.makeText(this, getString(R.string.gps_permissions_msg)).show();
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_CODE);

                AppInfo.getInstance(context).SetIsShowAuthGPS(true);

            } else {

                //2018-06-22 新增授權GPS定位服務訊息僅顯示一次
                if ( AppInfo.getInstance(context).GetIsShowAuthGPS() ){
                    return false;
                }
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_CODE);
                AppInfo.getInstance(context).SetIsShowAuthGPS(true);

            }
            return false;
        }
        return true;
    }

    /**
     * SDK Version 6.0以上取GPS位置需要在執行階段要求所需權限
     * @return if true 就是確認是否取得權限的回應是已經取得權限
     */
    public static boolean requestPermission(Fragment fragment){
        if (ActivityCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            //2018-06-22 新增授權GPS定位服務訊息僅顯示一次
            if (fragment.shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)  && !AppInfo.getInstance(fragment.getContext()).GetIsShowAuthGPS() ) {

//                CIToastView.makeText(this, getString(R.string.gps_permissions_msg)).show();
                fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_CODE);

                AppInfo.getInstance(fragment.getContext()).SetIsShowAuthGPS(true);

            } else {
                //2018-06-22 新增授權GPS定位服務訊息僅顯示一次
                if ( AppInfo.getInstance(fragment.getContext()).GetIsShowAuthGPS() ){
                    return false;
                }
                // No explanation needed, we can request the permission.
                fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_CODE);

                AppInfo.getInstance(fragment.getContext()).SetIsShowAuthGPS(true);

            }
            return false;
        }
        return true;
    }

    /**
     * 計算所有辦事處距離與GPS座標比較後重整資料，並初始化資料列表
     * */
    public static int getRecentDistanceIndex(Location location, List<LocationItem> list){

        //儲存單次計算出的距離
        float result[] = new float[1];
        //儲存所有計算出的距離
        float savedDistance[] = new float[list.size()];
        for(int loop = 0;loop < list.size();loop++){
            LocationItem locationItem = list.get(loop);

            //如果經緯度小於零就直接距離為最大值
            if(0 > locationItem.Lat || 0 > locationItem.Lat){
                savedDistance[loop] = Float.MAX_VALUE;
                continue;
            }

            try{
                location.distanceBetween(location.getLatitude(),
                        location.getLongitude(),
                        locationItem.Lat,
                        locationItem.Long,
                        result);
//                Log.e("result", result[0] + "");
                savedDistance[loop] = result[0];
            }catch (Exception e){
                savedDistance[loop] = Float.MAX_VALUE;
            }
        }
        //將第一筆儲存的距離資料設定給最小
        float minDistance = savedDistance[0];
        for(int loop = 0;loop < savedDistance.length;loop++){
            //比較距離，將最小的設定給minDistance
            minDistance = Math.min(savedDistance[loop],minDistance);
//            Log.e("minF", minDistance+"");
        }
        int index = 0;
        for(float f:savedDistance){
            if(f == minDistance){
                //比對距離以取得距離最近距離的物件
//                Log.e("result", index+"");
                break;
            }
            index++;
        }
        return index;
    }

    public static class LocationItem{
        public Double Lat;
        public Double Long;
    }

}