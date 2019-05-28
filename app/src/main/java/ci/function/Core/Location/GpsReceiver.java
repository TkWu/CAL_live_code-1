package ci.function.Core.Location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;

/**
 * 主要用來接收系統發出的LocationManager廣播，並判斷是否可以定位，
 * 可定位就要求取位置
 */
public class GpsReceiver extends BroadcastReceiver {

    public GpsReceiver(Callback callback){
        this.m_callback = callback;
    }
    private Callback m_callback = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {

            final String action = intent.getAction();
            if (action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                // GPS is switched off.延遲判斷目前GPS模式
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        m_callback.onGpsModeChangeReceive();
                    }
                },100);

            }
        }
    }

    public interface Callback{
        void onGpsModeChangeReceive();
    }

}