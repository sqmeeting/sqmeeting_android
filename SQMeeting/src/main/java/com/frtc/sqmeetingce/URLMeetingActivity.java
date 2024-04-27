package com.frtc.sqmeetingce;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.frtc.sqmeetingce.util.MeetingUtil;

import java.util.List;

import frtc.sdk.FrtcCall;
import frtc.sdk.internal.model.QRCodeResult;
import frtc.sdk.log.Log;
import frtc.sdk.model.JoinMeetingQRCodeParam;
import frtc.sdk.ui.FrtcMeetingActivity;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.util.JSONUtil;
import frtc.sdk.util.LanguageUtil;
import frtc.sdk.util.StringUtils;

public class URLMeetingActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    private FrtcCall frtcCall;
    private LocalStore localStore;

    private static final int max_num = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageUtil.setLanguage(this);
        frtcCall = FrtcCall.getInstance();
        localStore = LocalStoreBuilder.getInstance(getApplicationContext()).getLocalStore();
        verifyLaunchMeeting();
    }

    private boolean verifyUIExisting(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(max_num);
            if (runningTasks.isEmpty()) {
                return false;
            }
            for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks) {
                String name = runningTaskInfo.baseActivity.getClassName();
                if (getClass().getName().equals(name)) {
                    continue;
                }
                if (runningTaskInfo.baseActivity.getPackageName().equals(getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"Exception failed "+e.toString());
        }
        return false;
    }

    private void verifyLaunchMeeting() {
        if (verifyUIExisting(getApplicationContext())) {
            ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = mgr.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable()){
                BaseToast.showToast(this, getString(R.string.network_not_available), Toast.LENGTH_SHORT);
                finish();
                return;
            }
            String meetingUrl = getIntent().getStringExtra(MainActivity.QR_URL);
            if (StringUtils.isNotBlank(meetingUrl)) {
                String displayName = LocalStoreBuilder.getInstance(this).getLocalStore().getDisplayName();
                JoinMeetingQRCodeParam joinMeetingQRCodeParam = new JoinMeetingQRCodeParam();
                joinMeetingQRCodeParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
                String userToken = LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken();
                if (userToken != null && !userToken.isEmpty()) {
                    joinMeetingQRCodeParam.setToken(userToken);
                }
                joinMeetingQRCodeParam.setQrcode(meetingUrl);
                joinMeetingQRCodeParam.setDisplayName(displayName);
                boolean joined = frtcCall.joinMeetingWithQRCode(joinMeetingQRCodeParam);
                if(joined){
                    launchMeeting();
                }
                finish();
                return;
            }
            meetingUrl = getMeetingUrl();
            String displayName = LocalStoreBuilder.getInstance(this).getLocalStore().getDisplayName();
            if (null != meetingUrl) {
                String userToken = LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken();
                byte[] bUrl = Base64.decode(meetingUrl, Base64.DEFAULT);
                QRCodeResult obj = null;
                try{
                    obj = JSONUtil.transform(new String(bUrl), QRCodeResult.class);
                }catch(Exception e){
                    Log.e(TAG,"QRCodeResult failed : "+e.toString());
                }
                if(obj != null) {
                    String urlOperation = obj.getOperation();
                    if(!TextUtils.isEmpty(urlOperation) && urlOperation.equals("meeting_save")) {
                        String qrcodeMeetingUrl = obj.getMeeting_url();
                        Intent intent = new Intent();
                        intent.setClass(URLMeetingActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("saveMeetingUrl", qrcodeMeetingUrl);
                        intent.putExtra("saveMeetingNum", obj.getMeeting_number());
                        startActivity(intent);
                        finish();
                        return;
                    }
                }
                JoinMeetingQRCodeParam joinMeetingQRCodeParam = new JoinMeetingQRCodeParam();
                joinMeetingQRCodeParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
                if (userToken != null && !userToken.isEmpty()) {
                    joinMeetingQRCodeParam.setToken(userToken);
                }
                joinMeetingQRCodeParam.setQrcode(meetingUrl);
                joinMeetingQRCodeParam.setDisplayName(displayName);
                boolean joined = frtcCall.joinMeetingWithQRCode(joinMeetingQRCodeParam);
                if(joined){
                    launchMeeting();
                }
            }
            finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MeetingUtil.URL_KEY, getMeetingUrl());
            startActivity(intent);
            finish();
        }
    }

    private String getMeetingUrl(){
        String meetingUrl = null;
//        Uri uri = getIntent().getData();
//        if (uri == null) {
//            Log.e(TAG, "uri is null");
//            return meetingUrl;
//        }
//        if (uri.getScheme().equalsIgnoreCase("sqmeeting")){
//            String dial = uri.toString();
//            meetingUrl = dial.substring((uri.getScheme() + "://").length());
//        }
        return meetingUrl;
    }

    private void launchMeeting() {
        if(localStore == null) {
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        localStore.setAudioOn(false);
        localStore.setCameraOn(false);
        localStore.setAudioCall(false);
        Intent intent = new Intent();
        intent.setClass(this, FrtcMeetingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



}
