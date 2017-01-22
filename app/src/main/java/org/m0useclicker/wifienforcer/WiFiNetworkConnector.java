package org.m0useclicker.wifienforcer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.m0useclicker.wifienforcer.Utils.IConnectionChangedListenable;
import org.m0useclicker.wifienforcer.Utils.IConnectionListener;

import java.util.List;

/**
 * Connector to desired WiFi network.
 */

public class WiFiNetworkConnector implements IConnectionChangedListenable {
    private final Context context;
    private WifiManager wifiManager;
    private IConnectionListener connectionListener;

    public WiFiNetworkConnector(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public void setListener(IConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public void Connect() {
        if (!isWiFiEnabled()) {
            notifyConnectionListener();
            return;
        }

        if (isConnectedToTargetNetwork()) {
            notifyConnectionListener();
            return;
        }

        //connected outside of desired area where both networks available
        if (isConnectedToSecondaryNetwork()) {
            scanWiFiNetworks();
        }
    }

    private boolean isWiFiEnabled() {
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
        boolean isConnected = wifiInfo.getSSID().equals("\"" + networkSsid + "\"");
        return isConnected;
    }

    private void connectToTargetNetworkIfAvailable(List<ScanResult> scans) {
        if (isTargetNetworkAvailable(scans)) {
            int targetNetworkId = getTargetNetworkId(wifiManager.getConfiguredNetworks());
            wifiManager.enableNetwork(targetNetworkId, true);
            wifiManager.reconnect();
            notifyConnectionListener();
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

    private void notifyConnectionListener() {
        if (connectionListener != null) {
            connectionListener.onConnectedToDesiredNetwork();
        }
    }
}