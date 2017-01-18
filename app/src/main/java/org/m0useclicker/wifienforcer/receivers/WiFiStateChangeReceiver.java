package org.m0useclicker.wifienforcer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.m0useclicker.wifienforcer.R;

import java.util.List;

/**
 * Receives WiFi status changes and forces to connect to target network if it's in range.
 */

public class WiFiStateChangeReceiver extends BroadcastReceiver {
    private Context context;
    private WifiManager wifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        if (!isWiFiEnabled()) {
            return;
        }

        if (isConnectedToTargetNetwork()) {
            return;
        }

        //connected outside of desired area where both networks available
        if (!isConnectedToSecondaryNetwork()) {
            return;
        }

        scanWiFiNetworks();
    }

    private boolean isWiFiEnabled() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    private boolean isConnectedToTargetNetwork() {
        return isConnectedToNetwork(context.getString(R.string.target_wifi_network));
    }

    private boolean isConnectedToSecondaryNetwork() {
        return isConnectedToNetwork(context.getString(R.string.secondary_wifi_network));
    }

    private boolean isConnectedToNetwork(String networkSsid) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID().equals(networkSsid);
    }

    private void connectToTargetNetworkIfAvailable(List<ScanResult> scans) {
        if (isTargetNetworkAvailable(scans)) {
            int targetNetworkId = getTargetNetworkId(wifiManager.getConfiguredNetworks());
            wifiManager.enableNetwork(targetNetworkId, true);
            wifiManager.reconnect();
        } else {
            //if connected to 2.4G network but 5G network not in range yet wait and repeat
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scanWiFiNetworks();
        }
    }

    private boolean isTargetNetworkAvailable(List<ScanResult> scanResults) {
        for (ScanResult scan : scanResults) {
            if (scan.SSID.equals(context.getString(R.string.target_wifi_network))) {
                return true;
            }
        }
        return false;
    }

    private int getTargetNetworkId(List<WifiConfiguration> configurations) {
        for (WifiConfiguration config : configurations) {
            String targetNetworkName = "\"" + context.getString(R.string.target_wifi_network) + "\"";
            if (config.SSID.equals(targetNetworkName)) {
                return config.networkId;
            }
        }
        throw new RuntimeException("Network ID not found.");
    }

    private void scanWiFiNetworks() {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                connectToTargetNetworkIfAvailable(wifiManager.getScanResults());
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }
}