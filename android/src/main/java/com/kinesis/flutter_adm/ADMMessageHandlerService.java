package com.kinesis.flutter_adm;

import com.kinesis.flutter_adm.FlutterAdmPlugin;
import android.content.Intent;
import com.amazon.device.messaging.ADMMessageHandlerBase;

public class ADMMessageHandlerService extends ADMMessageHandlerBase {

    public ADMMessageHandlerService() {
        super(ADMMessageHandlerService.class.getName());
    }

    @Override
    protected void onMessage(Intent intent) {
        String message = intent.getExtras().getString("message");
        FlutterADMPlugin.sendMessageToDart(message); // Enviar mensaje al lado Flutter
    }

    @Override
    protected void onRegistered(String registrationId) {
        FlutterADMPlugin.sendRegistrationIdToDart(registrationId);
    }

    @Override
    protected void onRegistrationError(String error) {
        // Manejar errores si es necesario
    }

    @Override
    protected void onUnregistered(String registrationId) {
        // Manejar lógica de desregistro si es necesario
    }
}
