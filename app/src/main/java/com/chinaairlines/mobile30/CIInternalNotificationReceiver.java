package com.chinaairlines.mobile30;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import ci.function.Core.SLog;

/**
 * Created by Ryan on 16/7/12.
 */
public class CIInternalNotificationReceiver extends BroadcastReceiver {

    public static final String Notification_SHOW = "com.cal.pushmsg.show";
    private static final String FILTER_TAG = "GCM ";
    private CINotiflyItem m_Notifly = null;

    private Callback m_callback = null;

    public CIInternalNotificationReceiver(){}

    public CIInternalNotificationReceiver(Callback callback){
        this.m_callback = callback;
    }

    public interface Callback{

        void onReceivePushNotification(CINotiflyItem notiflyItem);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       SLog.i("CAL", FILTER_TAG+"CIInternalNotificationReceiver onReceive ");
        if(intent.getAction().equals(Notification_SHOW)){
            //Log.i("CAL", FILTER_TAG+"CIInternalNotificationReceiver onReceive Notification_SHOW ");
            m_Notifly = (CINotiflyItem)intent.getSerializableExtra(CINotiflyItem.NOTIFY_INFO);
            if ( null == m_Notifly ){
                return;
            }

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if( null != m_callback )
                        m_callback.onReceivePushNotification(m_Notifly);
                }
            });
        }
    }
}
