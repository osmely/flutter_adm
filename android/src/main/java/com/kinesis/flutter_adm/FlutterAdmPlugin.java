package com.kinesis.flutter_adm;

import androidx.annotation.NonNull;
import android.content.Context;
import android.content.Intent;
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
import com.amazon.device.messaging.ADM;


/** FlutterAdmPlugin */
public class FlutterAdmPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private ADM adm;
  private Context context;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_adm");
    channel.setMethodCallHandler(this);

    context = flutterPluginBinding.getApplicationContext();
    adm = new ADM(context);
    adm.startRegister();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getRegistrationId")) {

      final String id = adm.getRegistrationId();
      if (id == null){
          result.error("1", "No registrado", null);
      }else{
          result.success(id);    
      }
    
    }else if(call.method.equals("suscribeToTopic")){

      final String id = adm.getRegistrationId();
      if (id == null){
        
        final String topic = call.argument("topic");

        Log.d("FlutterAdmPlugin", "Received topic: " + topic);
          
        result.success("Android " + android.os.Build.VERSION.RELEASE);
      }else{
        result.error("2", "RegistrationId null", null);
      }
      
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
