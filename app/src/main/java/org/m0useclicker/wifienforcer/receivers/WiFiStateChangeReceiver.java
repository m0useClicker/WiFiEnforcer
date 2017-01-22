package org.m0useclicker.wifienforcer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.m0useclicker.wifienforcer.WiFiNetworkConnector;

/**
 * Receives WiFi status changes and forces to connect to target network if it's in range.
 */

public class WiFiStateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new WiFiNetworkConnector(context).Connect();
    }
}