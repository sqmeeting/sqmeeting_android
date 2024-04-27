package com.frtc.sdkdemo;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.os.Process;

import java.util.List;

import frtc.sdk.FrtcSdk;

public class FrtcDemoApplication extends Application {
    public FrtcSdk mFrtcSdk;

    @Override
    public void onCreate() {
        super.onCreate();
        startFrtcSDK();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mFrtcSdk.terminate();
    }

    private void startFrtcSDK() {
        if (getPackageName().equals(getCurrentRunningPackageName())) {
            mFrtcSdk = FrtcSdk.getInstance();
            mFrtcSdk.initialize(this);
        }
    }

    private String getCurrentRunningPackageName() {
        String name = "";
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo info : runningAppProcesses) {
            if (info.pid == Process.myPid()) {
                name = info.processName;
                break;
            }
        }
        return name;
    }

}

