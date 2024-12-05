
package com.kinesis.flutter_adm;

public class PluginADMConstants {

    // JSON keys
    public static final String JSON_DATA_MSG_KEY = "message";
    public static final String JSON_DATA_TIME_KEY = "timeStamp";
    public static final String JSON_DATA_CONSOLIDATION_KEY = "collapse_key";

    // Intent keys
    public static final String INTENT_MSG_ACTION = "com.amazon.sample.admmessenger.ON_MESSAGE";
    public static final String INTENT_MSG_CATEGORY = "com.amazon.sample.admmessenger.MSG_CATEGORY";

    public static final String SHARED_PREF_DATA_KEY = "adm_notifications";
    public static final String DEBUG_TAG = "FLUTTER_ADM_DEBUG";

    private PluginADMConstants() {}
}
