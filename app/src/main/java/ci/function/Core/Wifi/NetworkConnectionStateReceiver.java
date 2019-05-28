package ci.function.Core.Wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;

import ci.function.Core.CIApplication;
import ci.ui.object.AppInfo;

/**
 * 用來接收WifiManager發出的廣播，並判斷是否可以連接到網路，
 * Created by jlchen on 2016/4/20.
 */
public class NetworkConnectionStateReceiver extends BroadcastReceiver {

    public NetworkConnectionStateReceiver(Callback callback){
        this.m_callback = callback;
    }

    public interface Callback{
        void onNetworkConnect();

        void onNetworkDisconnect();
    }

    private Callback m_callback = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        //網路狀態改變
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            final boolean isConnection = AppInfo.getInstance(CIApplication.getContext()).bIsNetworkAvailable();
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    //網路連線
                    if(true == isConnection) {
                        m_callback.onNetworkConnect();
                    } else {
                        m_callback.onNetworkDisconnect();
                    }
                }
            });
        }
    }

}
