package frtc.sdk.internal.service;

import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import java.util.List;

import frtc.sdk.IFrtcCallListener;

public class MicrophoneService {
    private volatile boolean muted = false;
    private String recvMsId;
    private String sendMsId;
    private HeadsetReceiver headsetReceiver = null;
    private int defaultAudioMode;
    private boolean defaultSpeakerState = false;
    private Context ctx = null;
    private MicrophoneRecordManager microphoneRecordManager = null;
    private MicrophonePlayerManager microphonePlayerManager = null;

    private List<IFrtcCallListener> meetingMessageListeners;

    public MicrophoneService(Context context, List<IFrtcCallListener> meetingMessageListeners) {
        this.ctx = context;
        this.meetingMessageListeners = meetingMessageListeners;
        headsetReceiver = new HeadsetReceiver(context);
        defaultAudioMode = getCurrentAudioMode(context);
        defaultSpeakerState = getCurrentSpeakerState(context);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        context.registerReceiver(headsetReceiver, intentFilter);
        setupAudioMode();
    }

    private boolean getCurrentSpeakerState(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.isSpeakerphoneOn();
    }

    private int getCurrentAudioMode(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getMode();
    }

    public void setupAudioMode() {
        AudioManager audioManager = (AudioManager) this.ctx.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        if (headsetReceiver.verifyBluetoothHeadsetConnected()) {
            headsetReceiver.setBluetoothHeadsetState(true);
        } else {
            audioManager.setSpeakerphoneOn(!(audioManager.isBluetoothA2dpOn() || audioManager.isWiredHeadsetOn()));
            headsetReceiver.handleHeadsetEnableSpeaker(audioManager.isSpeakerphoneOn());
        }
    }

    public void stop() {
        if (microphoneRecordManager != null) {
            microphoneRecordManager.stop();
            microphoneRecordManager = null;
        }
        if (microphonePlayerManager != null) {
            microphonePlayerManager.stop();
            microphonePlayerManager = null;
        }
        resetAudioMode();
    }

    public void setSystemPhoneCalling(boolean systemCalling) {
        if (systemCalling) {
            onReleasedAudio();
            onRemovedAudio();
        } else {
            onRequestedAudio(this.sendMsId, this.meetingMessageListeners);
            onReceivedAudio(this.recvMsId);
        }
    }

    private void setAudioMode(int state) {
        AudioManager audioManager = (AudioManager) this.ctx.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(state);
    }

    public void switchAudioSpeakerDevice() {
        AudioManager audioManager = (AudioManager) this.ctx.getSystemService(Context.AUDIO_SERVICE);
        boolean speakerState = audioManager.isSpeakerphoneOn();
        if (!headsetReceiver.verifyBluetoothHeadsetConnected()) {
            audioManager.setSpeakerphoneOn(!speakerState);
            headsetReceiver.handleHeadsetEnableSpeaker(!speakerState);
        } else {
            headsetReceiver.setBluetoothHeadsetState(speakerState);
            if (!speakerState) {
                audioManager.setSpeakerphoneOn(true);
                headsetReceiver.handleHeadsetEnableSpeaker(true);
            }
        }
    }

    private void setSpeakerState(boolean state) {
        AudioManager audioManager = (AudioManager) this.ctx.getSystemService(Context.AUDIO_SERVICE);
        if (headsetReceiver.verifyBluetoothHeadsetConnected() || audioManager.isWiredHeadsetOn()) {
            audioManager.setSpeakerphoneOn(false);
        } else {
            audioManager.setSpeakerphoneOn(state);
        }
    }

    public void resetAudioMode() {
        if (headsetReceiver != null) {
            this.ctx.unregisterReceiver(headsetReceiver);
            if (headsetReceiver.verifyBluetoothHeadsetConnected()) {
                headsetReceiver.setBluetoothHeadsetState(false);
            }
            setAudioMode(defaultAudioMode);
            setSpeakerState(defaultSpeakerState);
            headsetReceiver = null;
        }
    }

    public void onRequestedAudio(String msId, List<IFrtcCallListener> meetingMessageListeners) {
        this.sendMsId = msId;
        this.meetingMessageListeners = meetingMessageListeners;
        if (microphoneRecordManager != null) {
            microphoneRecordManager.stop();
        }
        microphoneRecordManager = new MicrophoneRecordManager(msId);
        microphoneRecordManager.start(meetingMessageListeners);
    }

    public void onReleasedAudio() {
        if (microphoneRecordManager != null) {
            microphoneRecordManager.stop();
            microphoneRecordManager = null;
        }
    }

    public void setMicrophoneMuted(boolean status) {
        this.muted = status;
        if (microphoneRecordManager != null) {
            microphoneRecordManager.setAudioRecordMuted(muted);
        }
    }

    public void onRemovedAudio() {
        if (microphonePlayerManager != null) {
            microphonePlayerManager.stop();
            microphonePlayerManager = null;
        }
    }

    public void onReceivedAudio(String msId) {
        this.recvMsId = msId;
        if (microphonePlayerManager != null) {
            microphonePlayerManager.stop();
        }
        microphonePlayerManager = new MicrophonePlayerManager(msId);
        microphonePlayerManager.start();
    }

}
