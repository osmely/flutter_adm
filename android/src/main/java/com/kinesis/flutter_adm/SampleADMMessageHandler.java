/*
 * [SampleADMMessageHandler.java]
 *
 * (c) 2019, Amazon.com, Inc. or its affiliates. All Rights Reserved.
 */

package com.kinesis.flutter_adm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amazon.device.messaging.ADMConstants;
import com.amazon.device.messaging.ADMMessageHandlerBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The SampleADMMessageHandler class receives messages sent by ADM via the SampleADMMessageReceiver receiver.
 *
 * @version Revision: 1, Date: 11/11/2012
 */
public class SampleADMMessageHandler extends ADMMessageHandlerBase
{
    /** Tag for logs. */
    private final static String TAG = "ADMSampleIntentBase";

    /**
     * Class constructor.
     */
    public SampleADMMessageHandler()
    {
        super(SampleADMMessageHandler.class.getName());
    }
	
    /**
     * Class constructor, including the className argument.
     * 
     * @param className The name of the class.
     */
    public SampleADMMessageHandler(final String className) 
    {
        super(className);
    }

    /** {@inheritDoc} */
    @Override
    protected void onMessage(final Intent intent) 
    {
        Log.i(TAG, "SampleADMMessageHandler:onMessage");

        final String json_dat_msg_key = "message";
        final String json_data_time_key = "timeStamp";
        final String intentAction = "com.amazon.device.messaging.intent.REGISTRATION";
            final String msgCategory = "com.kinesis.flutter_adm";

        /* String to access message field from data JSON. */
        final String msgKey = json_dat_msg_key;

        /* String to access timeStamp field from data JSON. */
        final String timeKey = json_data_time_key;


        /* Extras that were included in the intent. */
        final Bundle extras = intent.getExtras();

        
        /* Extract message from the extras in the intent. */
        final String msg = extras.getString(msgKey);
        final String time = extras.getString(timeKey);

        if (msg == null || time == null)
        {
            Log.w(TAG, "SampleADMMessageHandler:onMessage Unable to extract message data." +
                    "Make sure that msgKey and timeKey values match data elements of your JSON message");
        }

        /* Create a notification with message data. */
        /* This is required to test cases where the app or device may be off. */
        ADMHelper.createADMNotification(this, msgKey, timeKey, intentAction, msg, time);

        
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

    /** {@inheritDoc} */
    @Override
    protected void onRegistrationError(final String string)
    {
        Log.d(TAG, "SampleADMMessageHandler:onRegistrationError " + string);
    }

    /** {@inheritDoc} */
    @Override
    protected void onRegistered(final String registrationId) 
    {
        Log.d(TAG, "SampleADMMessageHandler:onRegistered");
        Log.d(TAG, registrationId);


    }

    /** {@inheritDoc} */
    @Override
    protected void onUnregistered(final String registrationId) 
    {
        Log.d(TAG, "SampleADMMessageHandler:onUnregistered");


    }
    @Override
    protected void onSubscribe(final String topic) {
        Log.d(TAG, "onSubscribe: " + topic);
    }

    @Override
    protected void onSubscribeError(final String topic, final String errorId) {
        Log.d(TAG, "onSubscribeError: errorId: " + errorId + " topic: " + topic);
    }

    @Override
    protected void onUnsubscribe(final String topic) {
        Log.d(TAG, "onUnsubscribe: " + topic);
    }

    @Override
    protected void onUnsubscribeError(final String topic, final String errorId) {
        Log.d(TAG, "onUnsubscribeError: errorId: " + errorId + " topic: " + topic);
    }
}
