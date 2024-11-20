

package com.kinesis.flutter_adm;

import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.amazon.device.messaging.ADM;

public class ADMRegistrationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ADM adm = new ADM(context);
        Log.d("ADMRegistrationReceiver",":::: onReceive ::::");

        if (adm.isSupported()) {
            Log.d("ADMRegistrationReceiver",":::: isSupported TRUE ::::");
            adm.startRegister();
        }else{
            Log.d("ADMRegistrationReceiver",":::: isSupported FALSE ::::");
        }
    }
}
