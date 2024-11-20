package com.kinesis.flutter_adm;

import android.util.Log;
import android.content.Intent;
import com.amazon.device.messaging.ADMMessageHandlerBase;

public class ADMMessageHandlerService extends ADMMessageHandlerBase {

    public ADMMessageHandlerService() {
        super(ADMMessageHandlerService.class.getName());
    }

    @Override
    protected void onMessage(Intent intent) {
        Log.d("ADMMessageHandlerService",":::: onMessage ::::");
        String message = intent.getExtras().getString("message");
        FlutterAdmPlugin.sendMessageToDart(message); // Enviar mensaje al lado Flutter
    }

    @Override
    protected void onRegistered(String registrationId) {
        Log.d("ADMMessageHandlerService",":::: onRegistered :::: -> " + registrationId);
        FlutterAdmPlugin.sendRegistrationIdToDart(registrationId);
    }

    @Override
    protected void onRegistrationError(String error) {
        // Manejar errores si es necesario
        Log.d("ADMMessageHandlerService",":::: onRegistrationError ::::");
    }

    @Override
    protected void onUnregistered(String registrationId) {
        // Manejar lógica de desregistro si es necesario
        Log.d("ADMMessageHandlerService",":::: onUnregistered ::::");
    }
}
