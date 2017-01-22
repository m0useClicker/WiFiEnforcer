package org.m0useclicker.wifienforcer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.m0useclicker.wifienforcer.ReceiverRegistrator;

/**
 * Boot receiver
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_LOCKED_BOOT_COMPLETED)) {
            ReceiverRegistrator.RegisterConnectionChangeReceiver(context);
        }
    }
}