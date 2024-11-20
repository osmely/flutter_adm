package com.kinesis.flutter_adm;

import android.util.Log;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import com.amazon.device.messaging.ADM;
import android.content.Context;


/** FlutterAdmPlugin */
public class FlutterAdmPlugin implements FlutterPlugin, MethodCallHandler {
   private static MethodChannel channel;
   private Context applicationContext;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        applicationContext = binding.getApplicationContext(); 
        FlutterAdmPlugin.channel = new MethodChannel(binding.getBinaryMessenger(), "flutter_adm");
        FlutterAdmPlugin.channel.setMethodCallHandler(this);

    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {

        Log.d("onMethodCall",":::: -> " + call.method);

        if (call.method.equals("startRegister")) {
            ADM adm = new ADM(this.applicationContext);
            if (adm.isSupported()) {
                Log.d("FlutterAdmPlugin",":::: isSupported TRUE ::::");

                if (adm.getRegistrationId() == null) {
                    adm.startRegister();
                }
                
            }else{
                Log.d("FlutterAdmPlugin",":::: isSupported FALSE ::::");
            }
            result.success(null);

        }else if (call.method.equals("initialize")) {

            ADM adm = new ADM(this.applicationContext);
            if (adm.isSupported()) {
                if (adm.getRegistrationId() != null) {
                    FlutterAdmPlugin.sendRegistrationIdToDart(adm.getRegistrationId());
                }
            }

        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        FlutterAdmPlugin.channel.setMethodCallHandler(null);
    }

    public static void sendRegistrationIdToDart(String registrationId) {
        
        Log.d("sendRegistrationIdToDart",":::: -> " + registrationId);

        if (FlutterAdmPlugin.channel != null) {
            FlutterAdmPlugin.channel.invokeMethod("onRegistrationId", registrationId);
        }
    }

    public static void sendMessageToDart(String message) {

        Log.d("sendMessageToDart",":::: -> " + message);

        if (FlutterAdmPlugin.channel != null) {
            FlutterAdmPlugin.channel.invokeMethod("onMessage", message);
        }
    }
}
