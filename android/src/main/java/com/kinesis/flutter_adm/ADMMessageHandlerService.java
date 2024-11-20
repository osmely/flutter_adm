package com.kinesis.flutter_adm;
import android.content.Intent;
import com.amazon.device.messaging.ADMMessageHandlerBase;

public class ADMMessageHandlerService extends ADMMessageHandlerBase {

    public ADMMessageHandlerService() {
        super(ADMMessageHandlerService.class.getName());
    }

    @Override
    protected void onMessage(Intent intent) {
        String message = intent.getExtras().getString("message");
        FlutterAdmPlugin.sendMessageToDart(message); // Enviar mensaje al lado Flutter
    }

    @Override
    protected void onRegistered(String registrationId) {
        FlutterAdmPlugin.sendRegistrationIdToDart(registrationId);
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
