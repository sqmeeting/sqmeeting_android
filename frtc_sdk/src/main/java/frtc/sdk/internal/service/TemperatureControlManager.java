package frtc.sdk.internal.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import frtc.sdk.internal.ITemperatureStateListener;

public class TemperatureControlManager {

    private Timer timer = null;

    public static int INTERVAL = 30000;
    public static int DELAY5M = 300000;
    public static float BASE_THRESHOLD = 43.0f;

    public static final int MAX_LENGTH = 6;

    private volatile float currentThreshold;

    private final List<Integer> temperatureList = new ArrayList<>();

    private final BatteryInfoReceiver batteryInfoReceiver = new BatteryInfoReceiver();
    private ITemperatureStateListener temperatureStateListener;

    public TemperatureControlManager(ITemperatureStateListener temperatureStateListener) {
        this.temperatureStateListener = temperatureStateListener;
        this.currentThreshold = BASE_THRESHOLD;
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(this.batteryInfoReceiver, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this.batteryInfoReceiver);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void reset() {
        stop();
        temperatureList.clear();
        currentThreshold = BASE_THRESHOLD;
    }

    private float getMaxThreshold() {
        return BASE_THRESHOLD + 4.0f;
    }

    public float getThreshold() {
        return BASE_THRESHOLD;
    }

    public boolean reachedBaseThreshold(float currentTemperature) {
        return currentTemperature > getThreshold();
    }

    public void start() {
        if (timer != null) {
            return;
        }
        temperatureList.clear();
        currentThreshold = BASE_THRESHOLD;
        timer = new Timer("TemperatureControlManager");
        timer.schedule(new TemperatureTimerTask(temperatureList), DELAY5M, INTERVAL);
    }

    private void startMax() {
        reset();
        currentThreshold = getMaxThreshold();
        timer = new Timer("TemperatureControlManager");
        timer.schedule(new TemperatureTimerTask(temperatureList), DELAY5M, INTERVAL);
    }

    private float getAVGTemperature(List<Integer> currentTemperatures) {
        int sum = 0;
        for (int i = 0; i < currentTemperatures.size(); i++) {
            sum += currentTemperatures.get(i);
        }
        return (float) sum / currentTemperatures.size();
    }

    private boolean reachedThreshold(List<Integer> currentTemperatures) {
        if (currentThreshold < getAVGTemperature(currentTemperatures)
            && currentTemperatures.size() >= MAX_LENGTH) {
            return true;
        }
        return false;
    }

    class TemperatureTimerTask extends TimerTask {
        private List<Integer> currentTemperatures;
        public TemperatureTimerTask(List<Integer> currentTemperatures) {
            this.currentTemperatures = currentTemperatures;
        }

        @Override
        public void run() {
            if (currentTemperatures.size() >= MAX_LENGTH) {
                currentTemperatures.remove(0);
            }
            currentTemperatures.add(batteryInfoReceiver.getCurrentTemperature());
            if (reachedThreshold(currentTemperatures)) {
                TemperatureControlManager.this.temperatureStateListener.onTemperatureChanged(currentThreshold);
                if (currentThreshold == TemperatureControlManager.BASE_THRESHOLD) {
                    TemperatureControlManager.this.startMax();
                    return;
                }
                TemperatureControlManager.this.stop();
            }
        }
    }
}
