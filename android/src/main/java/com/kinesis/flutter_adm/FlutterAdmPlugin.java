package com.kinesis.flutter_adm;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.app.Activity;
import io.flutter.embedding.android.FlutterFragmentActivity;
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
import io.flutter.plugin.common.PluginRegistry.NewIntentListener;
import io.flutter.plugin.common.BinaryMessenger;
import android.net.Uri;
import android.os.Bundle;
import android.app.Application;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class FlutterAdmPlugin implements FlutterPlugin, MethodCallHandler, NewIntentListener, ActivityAware, Application.ActivityLifecycleCallbacks {
    
    private static MethodChannel channel;
    private Context context;
    private Activity activity;
    private ADM adm;
    private ActivityPluginBinding activityPluginBinding;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper()); // Handler para el hilo principal

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        LogUtils.debug("onAttachedToEngine");
        setupChannels(binding.getBinaryMessenger(), binding.getApplicationContext());
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        LogUtils.debug("onDetachedFromEngine");
        teardownChannels();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        
        LogUtils.debug("onMethodCall: " + call.method);
        
        if (call.method.equals("startRegister")) {
            this.executorService.execute(() -> handleStartRegister(result));

        } else if (call.method.equals("startUnregister")) {
            this.executorService.execute(() -> handleStartUnregister(result));
        
        } else if (call.method.equals("initialize")) {
            this.executorService.execute(() -> handleInitialize(result));
        
        } else if (call.method.equals("isSupported")) {
            boolean isSupported = adm != null && adm.isSupported();
            result.success(isSupported); 
       
        } else if (call.method.equals("getInitialMessage")) {
            this.executorService.execute(() -> {
                SharedPreferences prefs = this.context.getSharedPreferences(PluginADMConstants.SHARED_PREF_DATA_KEY, Context.MODE_PRIVATE);
                String lastData = prefs.getString("notification_data", null);
                prefs.edit().remove("notification_data").apply();
                mainHandler.post(() -> result.success(lastData));
            });
        
        } else {
            result.notImplemented();
        }
    }
    

    /**
     * ---------------------------------------------------------------------------------------------
     * Dart interaction
     * --------------------------------------------------------------------------------------------
     **/

    public void sendRegistrationIdToDartOnMainThread(String registrationId) {
        Log.d("sendRegistrationIdToDartOnMainThread", "::::");
        mainHandler.post(() -> sendRegistrationIdToDart(registrationId));
    }

    public static void sendRegistrationIdToDart(String registrationId) {
        LogUtils.debug("sendRegistrationIdToDart");
        if (FlutterAdmPlugin.channel != null) {
            FlutterAdmPlugin.channel.invokeMethod("onRegistrationId", registrationId);
        }
    }

    public static void sendMessageToDart(String message) {
        LogUtils.debug("sendMessageToDart");
        if (FlutterAdmPlugin.channel != null) {
            FlutterAdmPlugin.channel.invokeMethod("onMessage", message);
        }
    }

    public static void sendOnNotificationClickedToDart(String message) {
        LogUtils.debug("sendOnNotificationClickedToDart");
        if (FlutterAdmPlugin.channel != null) {
            FlutterAdmPlugin.channel.invokeMethod("onNotificationClicked", message);
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * ActivityAware
     * --------------------------------------------------------------------------------------------
     **/
    @Override
    public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
        LogUtils.debug("onAttachedToActivity");

        this.activityPluginBinding = activityPluginBinding;
        setActivity(activityPluginBinding.getActivity());
        activityPluginBinding.addOnNewIntentListener(this);

         // Verificamos si la app fue abierta desde una notificación
        Intent intent = activity.getIntent();
        if (intent != null && intent.getBooleanExtra("from_notification", false)) {
            
        }
    }

    @Override
    public void onDetachedFromActivity() {
        LogUtils.debug("onDetachedFromActivity");
        activityPluginBinding.removeOnNewIntentListener(this);
        this.activity = null;
    }


    @Override
    public void onDetachedFromActivityForConfigChanges() {
        LogUtils.debug("onDetachedFromActivityForConfigChanges");
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
        LogUtils.debug("onReattachedToActivityForConfigChanges");
        onAttachedToActivity(activityPluginBinding);
    }


    
    /**
     * ---------------------------------------------------------------------------------------------
     * NewIntentListener Interface Methods
     * --------------------------------------------------------------------------------------------
     **/
    @Override
    public boolean onNewIntent(@NonNull Intent intent) {
        LogUtils.debug("onNewIntent");
        if (this.activity == null) {
            return false;
        }


        // Log the intent action
        String action = intent.getAction();
        LogUtils.debug("Intent action: " + (action != null ? action : "null"));

        // Get and log the data URI
        Uri data = intent.getData();
        if (data != null) {
            LogUtils.debug("Intent data URI: " + data.toString());
        }

        // Log any extras
        Bundle extras = intent.getExtras();


        if (extras != null && extras.containsKey("click_action") && "FLUTTER_NOTIFICATION_CLICK".equals(extras.getString("click_action"))) {
            
            Map<String, Object> jsonExtras = new HashMap<>();

            for (String key : extras.keySet()) {
                jsonExtras.put(key, extras.get(key));
            }
            String messageData = (new JSONObject(jsonExtras)).toString();
            mainHandler.post(() -> sendOnNotificationClickedToDart(messageData));

            SharedPreferences prefs = this.context.getSharedPreferences(PluginADMConstants.SHARED_PREF_DATA_KEY, Context.MODE_PRIVATE);
            prefs.edit().remove("notification_data").apply();
            LogUtils.debug("Remove push data");
        }

        return true;
    }



 /**
     * ---------------------------------------------------------------------------------------------
     * ActivityLifecycleCallbacks Interface Methods
     * --------------------------------------------------------------------------------------------
     **/
    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle bundle) {
        LogUtils.debug("triggered onActivityCreated: " + activity.getClass().getName());
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        LogUtils.debug("triggered onActivityStarted: " + activity.getClass().getName());
        
        Intent intent = activity.getIntent();
        Bundle extras = intent.getExtras();
        
        if (extras != null && extras.containsKey("click_action") && "FLUTTER_NOTIFICATION_CLICK".equals(extras.getString("click_action"))) {
            Map<String, Object> jsonExtras = new HashMap<>();
            for (String key : extras.keySet()) {
                jsonExtras.put(key, extras.get(key));
            }
            String messageData = (new JSONObject(jsonExtras)).toString();
            SharedPreferences prefs = this.context.getSharedPreferences(PluginADMConstants.SHARED_PREF_DATA_KEY, Context.MODE_PRIVATE);
            LogUtils.debug("Saving push data...");
            prefs.edit().putString("notification_data", messageData).apply();
            LogUtils.debug("Saved ->>>> " + messageData);
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        LogUtils.debug("triggered onActivityResumed: " + activity.getClass().getName());
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        LogUtils.debug("triggered onActivityPaused: " + activity.getClass().getName());
        // Delay session initialization
        
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        LogUtils.debug("triggered onActivityStopped: " + activity.getClass().getName());
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        LogUtils.debug("triggered onActivityDestroyed: " + activity.getClass().getName());
        if (this.activity == activity) {
            activity.getApplication().unregisterActivityLifecycleCallbacks(this);
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Private
     * --------------------------------------------------------------------------------------------
     **/
    private void teardownChannels() {
        FlutterAdmPlugin.channel.setMethodCallHandler(null);
        this.executorService.shutdown();

        this.activityPluginBinding = null;
        this.activity = null;
        this.context = null;
    }

    private void setupChannels(BinaryMessenger binding, Context context) {
        this.context = context; 
        FlutterAdmPlugin.channel = new MethodChannel(binding, "flutter_adm");
        FlutterAdmPlugin.channel.setMethodCallHandler(this);
        this.adm = new ADM(context);
    }

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

    private void setActivity(Activity activity) {
        LogUtils.debug("triggered setActivity");
        this.activity = activity;
        activity.getApplication().registerActivityLifecycleCallbacks(this);
    }

}
