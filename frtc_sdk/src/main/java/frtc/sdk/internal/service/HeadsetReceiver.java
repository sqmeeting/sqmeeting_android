package frtc.sdk.internal.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;

import frtc.sdk.FrtcCall;
import frtc.sdk.log.Log;

public class HeadsetReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private FrtcCall frtcCall;
    private int startBluetoothSCOCount = 0;

    private final int DelayTime = 1000;
    private final int maxTryCount = 3;

    private final Runnable bluetoothScoRunnable = new Runnable() {
        @Override
        public void run() {
            startBluetoothSco();
        }
    };

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
            if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                handleBluetoothScoConnected();
            } else {
                handleBluetoothScoOtherState();
            }
        }
    };

    public HeadsetReceiver(Context context) {
        this.context = context;
        this.frtcCall = FrtcCall.getInstance();
    }

    private void startBluetoothSco() {
        AudioManager audioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.startBluetoothSco();
    }

    public void handleHeadsetEnableSpeaker(boolean enableSpeaker) {
        if (frtcCall != null) {
            frtcCall.setHeadsetEnableSpeaker(enableSpeaker);
        }
    }

    private void handleBluetoothScoConnected() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setBluetoothScoOn(true);
        audioManager.setSpeakerphoneOn(false);
        handleHeadsetEnableSpeaker(false);
        unregisterReceiver();
        startBluetoothSCOCount = 0;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(intent.getAction())) {
            handleBluetoothHeadsetStateChanged(intent);
        } else if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
            handleBluetoothHeadsetConnected(intent);
        }
    }

    private void handleBluetoothScoOtherState() {
        startBluetoothSCOCount++;
        if (startBluetoothSCOCount < maxTryCount) {
            Handler handler = new Handler();
            handler.postDelayed(bluetoothScoRunnable, DelayTime);
        } else {
            unregisterReceiver();
            startBluetoothSCOCount = 0;
            setBluetoothHeadsetState(false);
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setSpeakerphoneOn(!audioManager.isWiredHeadsetOn());
            handleHeadsetEnableSpeaker(audioManager.isSpeakerphoneOn());
        }
    }

    private void doStartBluetoothSco() {
        registerReceiver();
        startBluetoothSco();
    }

    private void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        context.registerReceiver(this.receiver, intentFilter);
    }

    private void unregisterReceiver(){
        context.unregisterReceiver(this.receiver);
    }

    public boolean verifyBluetoothHeadsetConnected() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        }
        return (BluetoothProfile.STATE_CONNECTED == bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET));
    }

    public void setBluetoothHeadsetState(boolean state) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (state) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            doStartBluetoothSco();
        } else {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.stopBluetoothSco();
            audioManager.setBluetoothScoOn(false);
        }
    }

    private void handleBluetoothHeadsetConnected(Intent intent) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int state = intent.getIntExtra("state", 0);
        if (state == 1) {
            audioManager.setSpeakerphoneOn(false);
            setBluetoothHeadsetState(false);
        } else if (state == 0) {
            if (!verifyBluetoothHeadsetConnected()) {
                audioManager.setSpeakerphoneOn(true);
            } else {
                setBluetoothHeadsetState(true);
            }
        }
        handleHeadsetEnableSpeaker(audioManager.isSpeakerphoneOn());
    }

    private void handleBluetoothHeadsetStateChanged(Intent intent) {
        int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED);
        if (state == BluetoothProfile.STATE_CONNECTED) {
            setBluetoothHeadsetState(true);
        } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            setBluetoothHeadsetState(false);
            if (!audioManager.isWiredHeadsetOn()) {
                audioManager.setSpeakerphoneOn(true);
            }
            handleHeadsetEnableSpeaker(audioManager.isSpeakerphoneOn());
        }
    }

}
