package frtc.sdk.internal.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryInfoReceiver extends BroadcastReceiver {

    private int currentTemperature = 0;

    private final int base = 10;

    public int getCurrentTemperature() {
        return currentTemperature;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            currentTemperature =  intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / base;
        }
    }
}
