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
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        Log.d("onMethodCall", ":::: -> " + call.method);

        if (call.method.equals("startRegister")) {
            executorService.execute(() -> handleStartRegister(result));
        } else if (call.method.equals("initialize")) {
            executorService.execute(() -> handleInitialize(result));
        } else {
            result.notImplemented();
        }
    }

    private void handleStartRegister(Result result) {
        if (this.adm.isSupported()) {
            Log.d("FlutterAdmPlugin", ":::: isSupported TRUE ::::");
            if (this.adm.getRegistrationId() == null) {
                this.adm.startRegister();
            }
        } else {
            Log.d("FlutterAdmPlugin", ":::: isSupported FALSE ::::");
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

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        FlutterAdmPlugin.channel.setMethodCallHandler(null);
        executorService.shutdown();
    }

    public static void sendRegistrationIdToDart(String registrationId) {
        Log.d("sendRegistrationIdToDart", ":::: -> " + registrationId);
        if (FlutterAdmPlugin.channel != null) {
            FlutterAdmPlugin.channel.invokeMethod("onRegistrationId", registrationId);
        }
    }

    public static void sendMessageToDart(String message) {
        Log.d("sendMessageToDart", ":::: -> " + message);
        if (FlutterAdmPlugin.channel != null) {
            FlutterAdmPlugin.channel.invokeMethod("onMessage", message);
        }
    }

    private void sendRegistrationIdToDartOnMainThread(String registrationId) {
        mainHandler.post(() -> sendRegistrationIdToDart(registrationId));
    }
}
