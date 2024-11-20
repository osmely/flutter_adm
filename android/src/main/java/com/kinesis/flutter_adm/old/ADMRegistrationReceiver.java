

package com.kinesis.flutter_adm;

import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.amazon.device.messaging.ADM;

public class ADMRegistrationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Instancia de ADM
        ADM adm = new ADM(context);

        // Verifica si el registro ya está completo
        String registrationId = adm.getRegistrationId();
        if (registrationId == null) {
            // Si no está registrado, inicia el registro
            if (adm.isSupported()) {
                adm.startRegister();
            } else {
                System.out.println("ADM no es compatible con este dispositivo.");
            }
        } else {
            FlutterAdmPlugin.sendRegistrationIdToDart(registrationId);
        }
    }
}
