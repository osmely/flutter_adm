/*
 * [PluginADMMessageHandlerJobBase.java]
 *
 * (c) 2019, Amazon.com, Inc. or its affiliates. All Rights Reserved.
 */

package com.kinesis.flutter_adm;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.os.Bundle;

import com.amazon.device.messaging.ADMConstants;
import com.amazon.device.messaging.ADMMessageHandlerJobBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONException;
import android.util.Log;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.os.Build;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * The PluginADMMessageHandlerJobBase class receives messages sent by ADM via the SampleADMMessageReceiver receiver.
 *
 * @version Revision: 1, Date: 11/20/2019
 */
public class PluginADMMessageHandlerJobBase extends ADMMessageHandlerJobBase
{
    /** Tag for logs. */
    private final static String TAG = "ADMSampleJobBase";

    private final Handler mainHandler = new Handler(Looper.getMainLooper()); // Handler para el hilo principal

    /**
     * Class constructor.
     */
    public PluginADMMessageHandlerJobBase()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    protected void onMessage(final Context context, final Intent intent)
    {
        
        Bundle extras = intent.getExtras();
        
        
        if (extras != null) {
            Map<String, Object> jsonExtras = new HashMap<>();

            for (String key : extras.keySet()) {
                jsonExtras.put(key, extras.get(key));
            }

            showNotification(context, jsonExtras);

            // If app is in foreground, send message directly
            // if (isAppInForeground(context)) {
            //     String messageData = (new JSONObject(jsonExtras)).toString();
            //     mainHandler.post(() -> sendMessageToDart(messageData));
                
            // } else {

            //     // If app is in background, show notification
            //     showNotification(context, jsonExtras);

                
            // }
        }

        
        // String messageData = jsonExtras.toString();
        // mainHandler.post(() -> sendMessageToDart(messageData));



        // /* String to access message field from data JSON. */
        // final String msgKey = PluginADMConstants.JSON_DATA_MSG_KEY;

        // /* String to access timeStamp field from data JSON. */
        // final String timeKey = PluginADMConstants.JSON_DATA_TIME_KEY;

        // /* Intent action that will be triggered in onMessage() callback. */
        // final String intentAction = PluginADMConstants.INTENT_MSG_ACTION;


        // /* Extract message from the extras in the intent. */
        // final String msg = extras.getString(msgKey);
        // final String time = extras.getString(timeKey);

        // /* Intent category that will be triggered in onMessage() callback. */
        // final String msgCategory = PluginADMConstants.INTENT_MSG_CATEGORY;

        // Crear y enviar el broadcast para actualización de UI cuando la app está en primer plano
        // final Intent broadcastIntent = new Intent();
        // broadcastIntent.setAction(intentAction);
        // broadcastIntent.addCategory(msgCategory);
        // broadcastIntent.putExtra(msgKey, msg);
        // broadcastIntent.putExtra(timeKey, time);
        // context.sendBroadcast(broadcastIntent);


    }

    /** {@inheritDoc} */
    @Override
    protected void onRegistrationError(final Context context, final String string)
    {
        
    }

    /** {@inheritDoc} */
    @Override
    protected void onRegistered(final Context context, final String registrationId)
    {
        mainHandler.post(() -> sendRegistrationIdToDart(registrationId));
    }

    /** {@inheritDoc} */
    @Override
    protected void onUnregistered(final Context context, final String registrationId)
    {
        
    }

    @Override
    protected void onSubscribe(final Context context, final String topic) {
        
    }

    @Override
    protected void onSubscribeError(final Context context, final String topic, final String errorId) {
        
    }

    @Override
    protected void onUnsubscribe(final Context context, final String topic) {
        
    }

    @Override
    protected void onUnsubscribeError(final Context context, final String topic, final String errorId) {
        
    }


    public static void sendRegistrationIdToDart(String registrationId) {
        FlutterAdmPlugin.sendRegistrationIdToDart(registrationId);
    }

    public static void sendMessageToDart(String message) {
        FlutterAdmPlugin.sendMessageToDart(message);
    }

    private boolean isAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;

        String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND 
                && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    

    private void showNotification(Context context, Map<String, Object> message) {

        Log.d("showNotification", ":::: ***** ");

        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                PluginADMConstants.NOTIFICATION_CHANNEL_ID,
                "ADM Notifications",
                NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Create intent for NotificationActivity
        Intent intent = NotificationActivity.createIntent(
            context,
            new JSONObject(message).toString()
        );

        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PluginADMConstants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(message.containsKey("title") ? message.get("title").toString() : "New Message")
            .setContentText(message.containsKey("message") ? message.get("message").toString() : "You have a new message")
            // .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        notificationManager.notify(1, builder.build());
    }


    public static void createADMNotification(final Context context, final String msgKey, final String timeKey,
                                             final String intentAction, final String msg, final String time)
    {

        /* Clicking the notification should bring up the MainActivity. */
        /* Intent FLAGS prevent opening multiple instances of MainActivity. */
        final Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra(msgKey, msg);
        notificationIntent.putExtra(timeKey, time);

        /* Android reuses intents that have the same action. Adding a time stamp to the action ensures that */
        /* the notification intent received in onResume() isn't one that was recycled and that may hold old extras. */
        notificationIntent.setAction(intentAction + time);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL);

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle("ADM Message Received!")
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.iv_notification_image)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        } else {
            builder = new Notification.Builder(context)
                    .setContentTitle("ADM Message Received!")
                    .setContentText(msg)
                    .setSmallIcon(R.drawable.iv_notification_image)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        }

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(context.getResources().getInteger(R.integer.sample_app_notification_id), notification);
    }

}
