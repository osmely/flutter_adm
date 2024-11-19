package com.kinesis.flutter_adm;

import androidx.annotation.NonNull;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import android.app.Activity;
import com.amazon.device.messaging.ADM;


/** FlutterAdmPlugin */
public class FlutterAdmPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private ADM adm;
  private Context context;
  private BroadcastReceiver msgReceiver;
  private final static String TAG = "FlutterAdmPlugin";
  private Activity activity;


  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
        // TODO: your plugin is now attached to an Activity
        this.activity = activityPluginBinding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        // TODO: the Activity your plugin was attached to was destroyed to change configuration.
        // This call will be followed by onReattachedToActivityForConfigChanges().
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
        // TODO: your plugin is now attached to a new Activity after a configuration change.
    }

    @Override
    public void onDetachedFromActivity() {
        // TODO: your plugin is no longer associated with an Activity. Clean up references.
    }


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_adm");
    channel.setMethodCallHandler(this);

    context = flutterPluginBinding.getApplicationContext();
    adm = new ADM(context);
    
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getRegistrationId")) {

      final String id = adm.getRegistrationId();
      if (id == null){
          result.error("1", "getRegistrationId -> id==null", null);
      }else{
          result.success(id);    
      }
    
    }else if(call.method.equals("suscribeToTopic")){

      final String id = adm.getRegistrationId();
      
      if (id != null){
        final String topic = call.argument("topic");
        Log.d(TAG, "suscribeToTopic topic: " + topic);
        adm.subscribeToTopic(topic);

      }else{
        result.error("2", "suscribeToTopic -> RegistrationId null", null);
      }
      
    }else if(call.method.equals("startRegister")){
      this.onStartRegister();

      
    } else {
      result.notImplemented();
    }
  }

  public void onStartRegister() {

      Log.d(TAG, "startRegister....");
      adm.startRegister();

      final String json_dat_msg_key = "message";
        final String json_data_time_key = "timeStamp";
        final String intent_msg_action = "com.amazon.sample.admmessenger.ON_MESSAGE";
        final String intent_msg_category = "com.amazon.sample.admmessenger.MSG_CATEGORY";


        /* String to access message field from data JSON. */
        final String msgKey = json_dat_msg_key;

        /* String to access timeStamp field from data JSON. */
        final String timeKey = json_data_time_key;

        /* Intent action that will be triggered in onMessage() callback. */
        final String intentAction = intent_msg_action;
        final String msgCategory = intent_msg_category;


      msgReceiver = createBroadcastReceiver(msgKey, timeKey);
      final IntentFilter messageIntentFilter= new IntentFilter(intentAction);
      messageIntentFilter.addCategory(msgCategory);
      this.activity.registerReceiver(msgReceiver, messageIntentFilter);    
  }


   private BroadcastReceiver createBroadcastReceiver(final String msgKey,
            final String timeKey) 
    {

      
      Log.d(TAG, "createBroadcastReceiver....");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
        {

            /** {@inheritDoc} */
            @Override
            public void onReceive(final Context context, final Intent broadcastIntent)
            {

              Log.d(TAG, "onReceive....");

                if(broadcastIntent != null){

                    /* Extract message from the extras in the intent. */
                    final String msg = broadcastIntent.getStringExtra(msgKey);
                    final String srvTimeStamp = broadcastIntent.getStringExtra(timeKey);

                    if (msg != null && srvTimeStamp != null)
                    {
                        Log.d(TAG, msg);
                    }else{
                      Log.d(TAG, msg);
                    }

                }
            }
        };
        return broadcastReceiver;
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
