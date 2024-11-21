package com.kinesis.flutter_adm;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import com.amazon.device.messaging.ADM;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FlutterAdmPlugin implements FlutterPlugin, MethodCallHandler {
    private static MethodChannel channel;
    private Context applicationContext;
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
        
        } else if (call.method.equals("initialize")) {
            executorService.execute(() -> handleInitialize(result));
        
        } else if (call.method.equals("isSupported")) {
            boolean isSupported = adm != null && adm.isSupported();
            result.success(isSupported); 
        
        } else if (call.method.equals("setTopicSuscription")) {
            String topic = call.argument("topic");
            Boolean suscribe = call.argument("suscribe");

            executorService.execute(() -> handleSuscription(result, topic, suscribe));


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

    public static void sendOnSuscribeToDart(String message) {
        if (FlutterAdmPlugin.channel != null) {
            FlutterAdmPlugin.channel.invokeMethod("onSuscribe", message);
        }
    }

    public static void sendOnUnsubscribeToDart(String message) {
        if (FlutterAdmPlugin.channel != null) {
            FlutterAdmPlugin.channel.invokeMethod("onUnsubscribe", message);
        }
    }

    //=====================================================================
    // Private ============================================================
    //=====================================================================

    private void handleStartRegister(Result result) {
        if (this.adm.isSupported()) {
            if (this.adm.getRegistrationId() == null) {
                this.adm.startRegister();
            }
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
            }
        }

        // Mover la respuesta al hilo principal
        mainHandler.post(() -> result.success(null));
    }


    private void handleSuscription(Result result, String topic, Boolean suscribe) {
       
       if (suscribe) {
            this.adm.subscribeToTopic(topic);
       } else {
            this.adm.unsubscribeFromTopic(topic);
       }

        // Mover la respuesta al hilo principal
        mainHandler.post(() -> result.success(null));
    }

    private void sendRegistrationIdToDartOnMainThread(String registrationId) {
        mainHandler.post(() -> sendRegistrationIdToDart(registrationId));
    }
}
