/*
 * [PluginADMMessageHandlerJobBase.java]
 *
 * (c) 2019, Amazon.com, Inc. or its affiliates. All Rights Reserved.
 */

package com.kinesis.flutter_adm;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.amazon.device.messaging.ADMConstants;
import com.amazon.device.messaging.ADMMessageHandlerJobBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * The PluginADMMessageHandlerJobBase class receives messages sent by ADM via the SampleADMMessageReceiver receiver.
 *
 * @version Revision: 1, Date: 11/20/2019
 */
public class PluginADMMessageHandlerJobBase extends ADMMessageHandlerJobBase
{

    private final Handler mainHandler = new Handler(Looper.getMainLooper()); // Handler para el hilo principal

    /**
     * Class constructor.
     */
    public PluginADMMessageHandlerJobBase()
    {
        super();
        LogUtils.debug("PluginADMMessageHandlerJobBase");
    }

    /** {@inheritDoc} */
    @Override
    protected void onMessage(final Context context, final Intent intent){
        LogUtils.debug("onMessage");

        Bundle extras = intent.getExtras();
        
        if (extras != null) {
            Map<String, Object> jsonExtras = new HashMap<>();

            for (String key : extras.keySet()) {
                jsonExtras.put(key, extras.get(key));
            }

            // If app is in foreground, send message directly
            if (isAppInForeground(context)) {
                String messageData = (new JSONObject(jsonExtras)).toString();
                mainHandler.post(() -> sendMessageToDart(messageData));
            } 
            
        }

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

}
