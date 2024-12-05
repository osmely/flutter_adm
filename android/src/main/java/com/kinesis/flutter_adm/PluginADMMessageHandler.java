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

    public PluginADMMessageHandler()
    {
        super(PluginADMMessageHandler.class.getName());
    }
	
    public PluginADMMessageHandler(final String className) 
    {
        super(className);
    }

    @Override
    protected void onMessage(final Intent intent) 
    {
       
    }

    @Override
    protected void onRegistrationError(final String string)
    {
        
    }

    public static void sendRegistrationIdToDart(String registrationId) {
        
    }

    @Override
    protected void onRegistered(final String registrationId) 
    {
        
    }

    @Override
    protected void onUnregistered(final String registrationId) 
    {
        
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
