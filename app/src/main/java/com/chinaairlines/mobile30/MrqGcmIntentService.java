package com.chinaairlines.mobile30;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import ci.function.Core.SLog;
import ci.function.Main.CIMainActivity;
import ci.function.Start.CIStartActivity;
import ci.ui.object.AppInfo;
import ci.ws.cores.object.GsonTool;


//public class MrqGcmIntentService extends MrqPushReceiver {
@Deprecated
public class MrqGcmIntentService  {

    private static final String TAG = MrqGcmIntentService.class.getSimpleName();
    private static final String FILTER_TAG = "GCM ";
    public static final String MESSAGE_KEY = "message";
    public static final String URL_KEY = "mrqUrl";



    public void onReceive(Context context, Intent intent) {
        // 讀取包含在Intent物件中的資料
        Bundle bundle = intent.getExtras();
        for( String strKey : bundle.keySet() ){
            if ( TextUtils.isEmpty(bundle.getString(strKey)) ){
               SLog.d(TAG, String.format(FILTER_TAG +"= %s, Content is Null or len = 0", strKey ));
            } else {
               SLog.d(TAG, String.format(FILTER_TAG +"= %s, len:%d, Content:%s", strKey, bundle.getString(strKey).length(), bundle.getString(strKey).toString() ));
            }
        }

        // 因為這不是Activity元件，需要使用Context物件的時候，
        // 不可以使用「this」，要使用參數提供的Context物件
        if ( AppInfo.getInstance(context).GetAppNotiflySwitch() ){
            DecodeBundle(bundle, context);
        }
    }

    // Put the MobileRQ message into a notification and post it.
    private void sendNotification( CINotiflyItem notiflyItem, Context context) {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CINotiflyItem.NOTIFY_INFO, notiflyItem);
        intent.putExtras(bundle);

        Boolean bAppIsRunning = AppInfo.getInstance(context).appIsRunning(context.getPackageName());
        if ( bAppIsRunning ){

            Boolean bAppIsBackgroundRunning = AppInfo.getInstance(context).appIsBackgroundRunning(context.getPackageName());
           SLog.i(TAG, FILTER_TAG + " appIsBackgroundRunning= " + (bAppIsBackgroundRunning? "Y":"N") );

            if ( bAppIsBackgroundRunning ){

                //推送推播通知
                Class newClass = null;
                try {
                    newClass = Class.forName(CIMainActivity.class.getName());
                    intent.setClass(context, newClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    sendNotify(notiflyItem, intent, context);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                //推送App內部推播通知
                sendAppNotify(notiflyItem, context);
            }

        } else {
            //推送推播通知
            Class newClass = null;
            try {

                newClass = Class.forName(CIStartActivity.class.getName());
                intent.setClass(context, newClass);
                sendNotify(notiflyItem, intent, context);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**丟出推播通知*/
    private void sendNotify( CINotiflyItem notiflyItem, Intent intent, Context context ) {

        int iId = (int) System.currentTimeMillis()/1000;
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent contentIntent = PendingIntent.getActivity(context, iId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_app))
                .setSmallIcon(R.drawable.push_icon_small)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notiflyItem.message).setBigContentTitle(context.getResources().getString(R.string.app_name)))
                .setContentText(notiflyItem.message);
        mBuilder.setContentIntent(contentIntent);
        //notificationManager.notify((int) System.currentTimeMillis()/1000, mBuilder.build());
        notificationManager.notify( iId, mBuilder.build());
    }

    /**丟出App內部推播通知*/
    private void sendAppNotify( CINotiflyItem notiflyItem, Context context) {

        Intent notificationReceiver = new Intent();
        notificationReceiver.setAction(CIInternalNotificationReceiver.Notification_SHOW);
        notificationReceiver.putExtra(CINotiflyItem.NOTIFY_INFO, notiflyItem);
       SLog.i(TAG, FILTER_TAG + " LocalBroadcastManager sendBroadcast ");
        //LocalBroadcastManager.getInstance(context).sendBroadcast(notificationReceiver);
        context.sendBroadcast(notificationReceiver);
    }

    public void DecodeBundle( Bundle extras, Context context ){

        String msg = extras.getString(MESSAGE_KEY);
        String url = extras.getString(URL_KEY);
        if( !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(url) ) {

            CINotiflyItem notiflyItem = GsonTool.toObject( url, CINotiflyItem.class);

            if ( null != notiflyItem &&
                    !TextUtils.isEmpty(notiflyItem.msg) &&
                    !TextUtils.isEmpty(notiflyItem.type) ){

                notiflyItem.message = msg;
                sendNotification(notiflyItem, context);
            } else {
               SLog.e(TAG, FILTER_TAG + " MRQ msg,type Can't Parse!");
            }

        } else {
           SLog.e(TAG,FILTER_TAG + " MRQ Bundle Can't Parse!");
        }
    }


}
