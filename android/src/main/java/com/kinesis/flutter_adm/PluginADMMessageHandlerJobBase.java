/*
 * [PluginADMMessageHandlerJobBase.java]
 *
 * (c) 2019, Amazon.com, Inc. or its affiliates. All Rights Reserved.
 */

package com.kinesis.flutter_adm;

import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amazon.device.messaging.ADMConstants;
import com.amazon.device.messaging.ADMMessageHandlerJobBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;
import org.json.JSONException;
import android.util.Log;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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

    private void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Crear canal de notificación para Android O y superiores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Obtener el ID del icono de la aplicación principal
        int iconResourceId = context.getResources().getIdentifier("notification_image", "drawable", context.getPackageName());
        if (iconResourceId == 0) {
            // Si no encuentra ic_notification, intenta con el icono launcher
            iconResourceId = context.getResources().getIdentifier("ic_launcher_round", "mipmap", context.getPackageName());
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(iconResourceId)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }

    /** {@inheritDoc} */
    @Override
    protected void onMessage(final Context context, final Intent intent)
    {
        

        Bundle extras = intent.getExtras();
        JSONObject jsonExtras = new JSONObject();
        
        if (extras != null) {
            for (String key : extras.keySet()) {
                try {
                    jsonExtras.put(key, extras.get(key));
                } catch (JSONException e) {
                    Log.e(TAG, "Error converting extras to JSON", e);
                }
            }
        }
        
        String messageData = jsonExtras.toString();
        mainHandler.post(() -> sendMessageToDart(messageData));



        // /* String to access message field from data JSON. */
        final String msgKey = PluginADMConstants.JSON_DATA_MSG_KEY;

        // /* String to access timeStamp field from data JSON. */
        // final String timeKey = PluginADMConstants.JSON_DATA_TIME_KEY;

        // /* Intent action that will be triggered in onMessage() callback. */
        // final String intentAction = PluginADMConstants.INTENT_MSG_ACTION;


        // /* Extract message from the extras in the intent. */
        // final String msg = extras.getString(msgKey);
        // final String time = extras.getString(timeKey);

        // if (msg == null || time == null)
        // {
        //     Log.w(TAG, "PluginADMMessageHandlerJobBase:onMessage Unable to extract message data." +
        //             "Make sure that msgKey and timeKey values match data elements of your JSON message");
        // }


        // /* Intent category that will be triggered in onMessage() callback. */
        // final String msgCategory = PluginADMConstants.INTENT_MSG_CATEGORY;

        // // Crear y enviar el broadcast para actualización de UI cuando la app está en primer plano
        // final Intent broadcastIntent = new Intent();
        // broadcastIntent.setAction(intentAction);
        // broadcastIntent.addCategory(msgCategory);
        // broadcastIntent.putExtra(msgKey, msg);
        // broadcastIntent.putExtra(timeKey, time);
        // context.sendBroadcast(broadcastIntent);


 // Mostrar notificación
        String title = extras.getString("title", "Nuevo mensaje");
        String message = extras.getString("message", "");
        if (message.isEmpty()) {
            message = extras.getString(msgKey, "Tienes un nuevo mensaje");
        }
        showNotification(context, title, message);
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
}
