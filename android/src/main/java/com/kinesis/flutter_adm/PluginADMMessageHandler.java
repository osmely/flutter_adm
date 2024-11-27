/*
 * [PluginADMMessageHandler.java]
 *
 * (c) 2019, Amazon.com, Inc. or its affiliates. All Rights Reserved.
 */

package com.kinesis.flutter_adm;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amazon.device.messaging.ADMConstants;
import com.amazon.device.messaging.ADMMessageHandlerBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The PluginADMMessageHandler class receives messages sent by ADM via the SampleADMMessageReceiver receiver.
 *
 * @version Revision: 1, Date: 11/11/2012
 */
public class PluginADMMessageHandler extends ADMMessageHandlerBase
{
    /** Tag for logs. */
    private final static String TAG = "ADMSampleIntentBase";

    private final Handler mainHandler = new Handler(Looper.getMainLooper()); // Handler para el hilo principal

    /**
     * Class constructor.
     */
    public PluginADMMessageHandler()
    {
        super(PluginADMMessageHandler.class.getName());
    }
	
    /**
     * Class constructor, including the className argument.
     * 
     * @param className The name of the class.
     */
    public PluginADMMessageHandler(final String className) 
    {
        super(className);
    }

    /** {@inheritDoc} */
    @Override
    protected void onMessage(final Intent intent) 
    {
        Log.i(TAG, "PluginADMMessageHandler:onMessage");

        /* String to access message field from data JSON. */
        final String msgKey = PluginADMConstants.JSON_DATA_MSG_KEY;

        /* String to access timeStamp field from data JSON. */
        final String timeKey = PluginADMConstants.JSON_DATA_TIME_KEY;
        
        /* Intent action that will be triggered in onMessage() callback. */
        final String intentAction = PluginADMConstants.INTENT_MSG_ACTION;

        /* Extras that were included in the intent. */
        final Bundle extras = intent.getExtras();

        /* Extract message from the extras in the intent. */
        final String msg = extras.getString(msgKey);
        final String time = extras.getString(timeKey);

        if (msg == null || time == null)
        {
            Log.w(TAG, "PluginADMMessageHandler:onMessage Unable to extract message data." +
                    "Make sure that msgKey and timeKey values match data elements of your JSON message");
        }


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
        this.sendBroadcast(broadcastIntent);

    }

    /**
     * This method verifies the MD5 checksum of the ADM message.
     * 
     * @param extras Extra that was included with the intent.
     */
    private void verifyMD5Checksum(final Bundle extras) 
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
        Log.i(TAG, "PluginADMMessageHandler:onMessage App md5: " + md5);
        
        /* Extract md5 from the extras in the intent. */
        final String admMd5 = extras.getString(ADMConstants.EXTRA_MD5);
        Log.i(TAG, "PluginADMMessageHandler:onMessage ADM md5: " + admMd5);
        
        /* Data integrity check. */
        if(!admMd5.trim().equals(md5.trim()))
        {
            Log.w(TAG, "PluginADMMessageHandler:onMessage MD5 checksum verification failure. " +
            		"Message received with errors");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onRegistrationError(final String string)
    {
        Log.e(TAG, "PluginADMMessageHandler:onRegistrationError " + string);
    }

    public static void sendRegistrationIdToDart(String registrationId) {
        FlutterAdmPlugin.sendRegistrationIdToDart(registrationId);
    }

    /** {@inheritDoc} */
    @Override
    protected void onRegistered(final String registrationId) 
    {
        mainHandler.post(() -> sendRegistrationIdToDart(registrationId));
    }

    /** {@inheritDoc} */
    @Override
    protected void onUnregistered(final String registrationId) 
    {
     

        /* Unregister the app instance's registration ID with your server. */
        // MyServerMsgHandler srv = new MyServerMsgHandler();
        // srv.unregisterAppInstance(getApplicationContext(), registrationId);
    }
    @Override
    protected void onSubscribe(final String topic) {
        
    }

    @Override
    protected void onSubscribeError(final String topic, final String errorId) {
        
    }

    @Override
    protected void onUnsubscribe(final String topic) {
        
    }

    @Override
    protected void onUnsubscribeError(final String topic, final String errorId) {
        
    }
}
