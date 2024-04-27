package com.frtc.sqmeetingce;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Process;

import java.util.List;

import frtc.sdk.FrtcCall;
import frtc.sdk.FrtcManagement;
import frtc.sdk.log.CrashHandler;
import frtc.sdk.log.Log;
import frtc.sdk.model.InitParams;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.util.LanguageUtil;

public class FrtcMeetingApp extends Application {

    protected final String TAG = this.getClass().getSimpleName();

    public FrtcCall frtcCall;
    public FrtcManagement frtcManagement;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().initCrashHandler(this);
        Log.getInstance().init(this);
        String name = getCurrentRunningPackageName();
        if (getPackageName().equals(name)) {
            String clientId = LocalStoreBuilder.getInstance(this).getLocalStore().getClientId();
            frtcCall = FrtcCall.getInstance();
            frtcCall.initialize(this, new InitParams(clientId));
            frtcManagement = FrtcManagement.getInstance();
            frtcManagement.initialize(this, new InitParams(clientId));
        }
        LanguageUtil.setLanguage(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LanguageUtil.setLanguage(this);
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level){
        super.onTrimMemory(level);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (frtcCall != null) {
            frtcCall.close();
        }
        if(frtcManagement != null) {
            frtcManagement.shutdown();
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

