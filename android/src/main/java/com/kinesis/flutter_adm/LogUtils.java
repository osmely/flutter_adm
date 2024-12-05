package com.kinesis.flutter_adm;


import android.util.Log;
import io.flutter.BuildConfig;



public class LogUtils {
    public static void debug(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(PluginADMConstants.DEBUG_TAG, message);
        }
    }
}
