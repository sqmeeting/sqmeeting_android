package frtc.sdk.internal.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import frtc.sdk.log.Log;


public class SystemPhoneReceiver extends BroadcastReceiver {
    private final String TAG = getClass().toString();
    private MicrophoneService microphoneService = null;
    private volatile boolean flag = false;

    public static final String PHONE_STATE = "android.intent.action.PHONE_STATE";
    public static final String NEW_OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL";

    public SystemPhoneReceiver(MicrophoneService microphoneService) {
        this.microphoneService = microphoneService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (!Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
                TelephonyManager manager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                int callState = manager.getCallState();
                if (callState == TelephonyManager.CALL_STATE_OFFHOOK
                        || callState == TelephonyManager.CALL_STATE_RINGING) {
                    handleOffHook();
                } else if (callState == TelephonyManager.CALL_STATE_IDLE) {
                    handleCallStateIdle();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
        }
    }

    private void handleCallStateIdle() {
        if (flag) {
            flag = false;
            if (microphoneService != null) {
                microphoneService.setupAudioMode();
                microphoneService.setSystemPhoneCalling(false);
            }
        }
    }

    private void handleOffHook() {
        if (!flag) {
            flag = true;
            if (microphoneService != null) {
                microphoneService.setSystemPhoneCalling(true);
            }
        }
    }

}
