package org.m0useclicker.wifienforcer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.m0useclicker.wifienforcer.Utils.IConnectionListener;
import org.m0useclicker.wifienforcer.Utils.IListListener;
import org.m0useclicker.wifienforcer.Utils.ListenableSet;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static ListenableSet permissionsRequested = new ListenableSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                int PERMISSIONS_REQUEST = Math.abs(new Random().nextInt());
                requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE}, PERMISSIONS_REQUEST);
                permissionsRequested.add(PERMISSIONS_REQUEST);
            }

            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                int PERMISSIONS_REQUEST = Math.abs(new Random().nextInt());
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST);
                permissionsRequested.add(PERMISSIONS_REQUEST);
            }
        }

        final WiFiNetworkConnector wiFiNetworkConnector = new WiFiNetworkConnector(getBaseContext());

        if (permissionsRequested.isEmpty()) {
            wiFiNetworkConnector.setListener(new IConnectionListener() {
                @Override
                public void onConnectedToDesiredNetwork() {
                    finish();
                }
            });
            wiFiNetworkConnector.Connect();
        } else {
            permissionsRequested.setListener(new IListListener() {
                @Override
                public void afterRemove() {
                    if (permissionsRequested.isEmpty()) {
                        wiFiNetworkConnector.setListener(new IConnectionListener() {
                            @Override
                            public void onConnectedToDesiredNetwork() {
                                finish();
                            }
                        });
                        wiFiNetworkConnector.Connect();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissionsRequested.contains(requestCode) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionsRequested.remove(requestCode);
        }
    }
}