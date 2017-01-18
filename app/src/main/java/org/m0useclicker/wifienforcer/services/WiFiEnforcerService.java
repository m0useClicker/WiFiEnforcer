package org.m0useclicker.wifienforcer.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.m0useclicker.wifienforcer.receivers.WiFiStateChangeReceiver;

/**
 * Created by admin on 1/16/2017.
 */

public class WiFiEnforcerService extends Service {
    private boolean isRegistered;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            getApplicationContext().registerReceiver(new WiFiStateChangeReceiver(), intentFilter);
            isRegistered = true;
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
