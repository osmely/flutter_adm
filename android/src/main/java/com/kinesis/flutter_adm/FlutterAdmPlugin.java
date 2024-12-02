package com.kinesis.flutter_adm;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.app.Activity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import com.amazon.device.messaging.ADM;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FlutterAdmPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private static MethodChannel channel;
    private Context applicationContext;
    private Activity activity;
    private ADM adm;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper()); // Handler para el hilo principal

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        applicationContext = binding.getApplicationContext(); 
        FlutterAdmPlugin.channel = new MethodChannel(binding.getBinaryMessenger(), "flutter_adm");
        FlutterAdmPlugin.channel.setMethodCallHandler(this);
        this.adm = new ADM(applicationContext);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        FlutterAdmPlugin.channel.setMethodCallHandler(null);
        executorService.shutdown();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        Log.d("onMethodCall", ":::: -> " + call.method);

        if (call.method.equals("startRegister")) {
            executorService.execute(() -> handleStartRegister(result));

        } else if (call.method.equals("startUnregister")) {
            executorService.execute(() -> handleStartUnregister(result));
        
        } else if (call.method.equals("initialize")) {
            executorService.execute(() -> handleInitialize(result));
        
        } else if (call.method.equals("isSupported")) {
            boolean isSupported = adm != null && adm.isSupported();
            result.success(isSupported); 
       
        } else {
            result.notImplemented();
        }
    }
    

    //=====================================================================
    // Send to Dart =======================================================
    //=====================================================================

    public static void sendRegistrationIdToDart(String registrationId) {
        if (FlutterAdmPlugin.channel != null) {
            FlutterAdmPlugin.channel.invokeMethod("onRegistrationId", registrationId);
        }
    }

    public static void sendMessageToDart(String message) {
        if (FlutterAdmPlugin.channel != null) {
            FlutterAdmPlugin.channel.invokeMethod("onMessage", message);
        }
    }


    //=====================================================================
    // Private ============================================================
    //=====================================================================

    private void handleStartRegister(Result result) {
        if (this.adm.isSupported()) {    
            this.adm.startRegister();
        } else {
            
        }

        // Mover la respuesta al hilo principal
        mainHandler.post(() -> result.success(null));
    }

    private void handleStartUnregister(Result result) {
        if (this.adm.isSupported()) {
            this.adm.startUnregister();
        } else {
            
        }

        // Mover la respuesta al hilo principal
        mainHandler.post(() -> result.success(null));
    }

    private void handleInitialize(Result result) {
        if (this.adm.isSupported()) {
            String registrationId = this.adm.getRegistrationId();
            if (registrationId != null) {
                sendRegistrationIdToDartOnMainThread(registrationId);
            } else {
                this.adm.startRegister();
            }
        }

        // Mover la respuesta al hilo principal
        mainHandler.post(() -> result.success(null));
    }

    public void sendRegistrationIdToDartOnMainThread(String registrationId) {
        Log.d("sendRegistrationIdToDartOnMainThread", "::::");
        mainHandler.post(() -> sendRegistrationIdToDart(registrationId));
    }

    //=====================================================================
    // Private ============================================================
    //=====================================================================



    @Override
    public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
        Log.d("ActivityAware", "onAttachedToActivity :::: ***** ");

        this.activity = binding.getActivity();
        // Verificamos si la app fue abierta desde una notificación
        Intent intent = activity.getIntent();
        if (intent != null && intent.getBooleanExtra("from_notification", false)) {
            checkAndSendNotificationData();
        }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        Log.d("ActivityAware", "onDetachedFromActivityForConfigChanges :::: ***** ");
        this.activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
        Log.d("ActivityAware", "onReattachedToActivityForConfigChanges :::: ***** ");
        this.activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        Log.d("ActivityAware", "onDetachedFromActivity :::: ***** ");
        this.activity = null;
    }

    private void checkAndSendNotificationData() {
        Log.d("checkAndSendNotificationData", ":::: ***** ");

        SharedPreferences prefs = applicationContext.getSharedPreferences("adm_notifications", Context.MODE_PRIVATE);
        String notificationData = prefs.getString("notification_data", null);
        if (notificationData != null) {
            // Enviamos los datos a Flutter
            channel.invokeMethod("onNotificationClicked", notificationData);
            // Limpiamos los datos guardados
            prefs.edit().remove("notification_data").apply();
        }
    }

}
