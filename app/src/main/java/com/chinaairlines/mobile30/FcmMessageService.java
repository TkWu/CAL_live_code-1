package com.chinaairlines.mobile30;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import ci.function.Core.SLog;
import ci.function.Main.CIMainActivity;
import ci.function.Start.CIStartActivity;
import ci.ui.object.AppInfo;
import ci.ws.cores.object.GsonTool;

public class FcmMessageService extends FirebaseMessagingService {

    private static final String TAG = FcmMessageService.class.getSimpleName();
    private static final String FILTER_TAG = "[FCM] ";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //LogUtil.log("onMessageReceived:"+remoteMessage.getFrom());

        // Check if message contains a data payload.

        if (remoteMessage.getData().size() > 0) {
            SLog.d( TAG,FILTER_TAG +" onMessageReceived data payload: " + remoteMessage.getData());

            Map<String, String> map = remoteMessage.getData();

            if ( AppInfo.getInstance(getBaseContext()).GetAppNotiflySwitch() ){
                DecodeBundle(remoteMessage.getMessageId(), map, getBaseContext());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            SLog.d(TAG,FILTER_TAG +" onMessageReceived Notification Body: " + remoteMessage.getNotification().getBody());
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
                    sendFCMNotify(notiflyItem, intent, context);

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
                sendFCMNotify(notiflyItem, intent, context);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**丟出推播通知*/
    private void sendFCMNotify( CINotiflyItem notiflyItem, Intent intent, Context context ) {

        int iId = (int) System.currentTimeMillis()/1000;
        PendingIntent contentIntent = PendingIntent.getActivity(context, iId, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "0";//getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_app))
                .setSmallIcon(R.drawable.push_icon_small)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notiflyItem.message).setBigContentTitle(context.getResources().getString(R.string.app_name)))
                .setContentText(notiflyItem.message);
        mBuilder.setContentIntent(contentIntent);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    context.getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify( iId, mBuilder.build());
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
        context.sendBroadcast(notificationReceiver);
    }

    public void DecodeBundle( String strMsgId, Map<String, String> mapMessage, Context context ){

        String msg = mapMessage.get("msg");
        String type = mapMessage.get("type");
        if( !TextUtils.isEmpty(msg) && !TextUtils.isEmpty(type) ) {

            CINotiflyItem notiflyItem = new CINotiflyItem();

            notiflyItem.message = mapMessage.get("title")==null? "":mapMessage.get("title");
            notiflyItem.url     = mapMessage.get("url")==null? "":mapMessage.get("url");
            notiflyItem.type    = type;
            notiflyItem.msg     = msg;
            notiflyItem.msg_id  = strMsgId;

            String strData = mapMessage.get("data");

            if ( !TextUtils.isEmpty(strData) ){
                try {
                    JSONObject jsData = new JSONObject(strData);

                    notiflyItem.data = new CINotiflyItem.Data();
                    notiflyItem.data.token  = jsData.optString( "token","");
                    notiflyItem.data.version= jsData.optString( "version","");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            notiflyItem.message = msg;
            sendNotification(notiflyItem, context);

        } else {
            SLog.e(TAG,FILTER_TAG + " FCM Message Can't Parse!");
        }
    }


}