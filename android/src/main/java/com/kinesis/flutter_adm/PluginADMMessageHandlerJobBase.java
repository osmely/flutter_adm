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
        Log.i(TAG, "PluginADMMessageHandlerJobBase:onMessage");

        /* String to access message field from data JSON. */
        final String msgKey = PluginADMConstants.JSON_DATA_MSG_KEY;

        /* String to access timeStamp field from data JSON. */
        final String timeKey = PluginADMConstants.JSON_DATA_TIME_KEY;

        /* Intent action that will be triggered in onMessage() callback. */
        final String intentAction = PluginADMConstants.INTENT_MSG_ACTION;

        /* Extras that were included in the intent. */
        final Bundle extras = intent.getExtras();

        verifyMD5Checksum(context, extras);

        /* Extract message from the extras in the intent. */
        final String msg = extras.getString(msgKey);
        final String time = extras.getString(timeKey);

        if (msg == null || time == null)
        {
            Log.w(TAG, "PluginADMMessageHandlerJobBase:onMessage Unable to extract message data." +
                    "Make sure that msgKey and timeKey values match data elements of your JSON message");
        }

        /* Create a notification with message data. */
        /* This is required to test cases where the app or device may be off. */
        ADMHelper.createADMNotification(context, msgKey, timeKey, intentAction, msg, time);

        /* Intent category that will be triggered in onMessage() callback. */
        final String msgCategory = PluginADMConstants.INTENT_MSG_CATEGORY;

        /* Broadcast an intent to update the app UI with the message. */
        /* The broadcast receiver will only catch this intent if the app is within the onResume state of its lifecycle. */
        /* User will see a notification otherwise. */
        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(intentAction);
        broadcastIntent.addCategory(msgCategory);
        broadcastIntent.putExtra(msgKey, msg);
        broadcastIntent.putExtra(timeKey, time);
        context.sendBroadcast(broadcastIntent);

    }

    /**
     * This method verifies the MD5 checksum of the ADM message.
     *
     * @param extras Extra that was included with the intent.
     */
    private void verifyMD5Checksum(final Context context, final Bundle extras)
    {
        /* String to access consolidation key field from data JSON. */
        final String consolidationKey = PluginADMConstants.JSON_DATA_CONSOLIDATION_KEY;

        final Set<String> extrasKeySet = extras.keySet();
        final Map<String, String> extrasHashMap = new HashMap<String, String>();
        for (String key : extrasKeySet)
        {
            if (!key.equals(ADMConstants.EXTRA_MD5) && !key.equals(consolidationKey))
            {
                extrasHashMap.put(key, extras.getString(key));
            }
        }
        final String md5 = ADMMD5ChecksumCalculator.calculateChecksum(extrasHashMap);
        Log.i(TAG, "PluginADMMessageHandlerJobBase:onMessage App md5: " + md5);

        /* Extract md5 from the extras in the intent. */
        final String admMd5 = extras.getString(ADMConstants.EXTRA_MD5);
        Log.i(TAG, "PluginADMMessageHandlerJobBase:onMessage ADM md5: " + admMd5);

        /* Data integrity check. */
        if(!admMd5.trim().equals(md5.trim()))
        {
            Log.w(TAG, "PluginADMMessageHandlerJobBase:onMessage MD5 checksum verification failure. " +
                    "Message received with errors");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onRegistrationError(final Context context, final String string)
    {
        
    }

    public static void sendRegistrationIdToDart(String registrationId) {
        FlutterAdmPlugin.sendRegistrationIdToDart(registrationId);
    }

    /** {@inheritDoc} */
    @Override
    protected void onRegistered(final Context context, final String registrationId)
    {
        mainHandler.post(() -> sendRegistrationIdToDart(registrationId));
        // FlutterAdmPlugin.sendRegistrationIdToDart(registrationId);
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

}
