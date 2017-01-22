package org.m0useclicker.wifienforcer;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import org.m0useclicker.wifienforcer.receivers.WiFiStateChangeReceiver;

/**
 * Created by admin on 1/21/2017.
 */

public class ReceiverRegistrator {
    public static void RegisterConnectionChangeReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.getApplicationContext().registerReceiver(new WiFiStateChangeReceiver(), intentFilter);
    }
}