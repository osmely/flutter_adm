/*
 * [SampleADMMessageHandlerJobBase.java]
 *
 * (c) 2019, Amazon.com, Inc. or its affiliates. All Rights Reserved.
 */

package com.knesis.kchat;

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
 * The SampleADMMessageHandlerJobBase class receives messages sent by ADM via the SampleADMMessageReceiver receiver.
 *
 * @version Revision: 1, Date: 11/20/2019
 */
public class SampleADMMessageHandlerJobBase extends ADMMessageHandlerJobBase
{
    /** Tag for logs. */
    private final static String TAG = "ADMSampleJobBase";

    /**
     * Class constructor.
     */
    public SampleADMMessageHandlerJobBase()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    protected void onMessage(final Context context, final Intent intent)
    {
        Log.i(TAG, "SampleADMMessageHandlerJobBase:onMessage");
        
        

        /* String to access message field from data JSON. */
        final String msgKey = context.getResources().getIdentifier("json_data_msg_key", "string", context.getPackageName()); //context.getString(R.string.json_data_msg_key);

        /* String to access timeStamp field from data JSON. */
        final String timeKey = context.getString(R.string.json_data_time_key);

        /* Intent action that will be triggered in onMessage() callback. */
        final String intentAction = context.getString(R.string.intent_msg_action);

        /* Extras that were included in the intent. */
        final Bundle extras = intent.getExtras();

        

        /* Extract message from the extras in the intent. */
        final String msg = extras.getString(msgKey);
        final String time = extras.getString(timeKey);

        if (msg == null || time == null)
        {
            Log.w(TAG, "SampleADMMessageHandlerJobBase:onMessage Unable to extract message data." +
                    "Make sure that msgKey and timeKey values match data elements of your JSON message");
        }

        /* Create a notification with message data. */
        /* This is required to test cases where the app or device may be off. */
        ADMHelper.createADMNotification(context, msgKey, timeKey, intentAction, msg, time);

        /* Intent category that will be triggered in onMessage() callback. */
        final String msgCategory = context.getString(R.string.intent_msg_category);

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


    /** {@inheritDoc} */
    @Override
    protected void onRegistrationError(final Context context, final String string)
    {
        Log.e(TAG, "SampleADMMessageHandlerJobBase:onRegistrationError " + string);
    }

    /** {@inheritDoc} */
    @Override
    protected void onRegistered(final Context context, final String registrationId)
    {
        Log.i(TAG, "SampleADMMessageHandlerJobBase:onRegistered");
        Log.i(TAG, registrationId);

        /* Register the app instance's registration ID with your server. */
        MyServerMsgHandler srv = new MyServerMsgHandler();
        srv.registerAppInstance(context.getApplicationContext(), registrationId);
    }

    /** {@inheritDoc} */
    @Override
    protected void onUnregistered(final Context context, final String registrationId)
    {
        Log.i(TAG, "SampleADMMessageHandlerJobBase:onUnregistered");

        /* Unregister the app instance's registration ID with your server. */
        MyServerMsgHandler srv = new MyServerMsgHandler();
        srv.unregisterAppInstance(context.getApplicationContext(), registrationId);
    }

    @Override
    protected void onSubscribe(final Context context, final String topic) {
        Log.i(TAG, "onSubscribe: " + topic);
    }

    @Override
    protected void onSubscribeError(final Context context, final String topic, final String errorId) {
        Log.i(TAG, "onSubscribeError: errorId: " + errorId + " topic: " + topic);
    }

    @Override
    protected void onUnsubscribe(final Context context, final String topic) {
        Log.i(TAG, "onUnsubscribe: " + topic);
    }

    @Override
    protected void onUnsubscribeError(final Context context, final String topic, final String errorId) {
        Log.i(TAG, "onUnsubscribeError: errorId: " + errorId + " topic: " + topic);
    }

}
