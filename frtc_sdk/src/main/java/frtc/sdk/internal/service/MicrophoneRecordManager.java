package frtc.sdk.internal.service;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import frtc.sdk.IFrtcCallListener;
import frtc.sdk.internal.jni.Audio;
import frtc.sdk.log.Log;

public class MicrophoneRecordManager {

    private final String TAG = this.getClass().getSimpleName();
    private final int INTERVAL = 50;
    private final int INIT_TIME = 3000;

    private final int mod = 6;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private String msId;
    private AudioRecord audioRecord = null;

    private Thread recordThread = null;

    private volatile boolean audioRecordMuted = false;

    public MicrophoneRecordManager(String msId) {
        this.msId = msId;
    }

    private static int getMinBufferSize() {
        return AudioRecord.getMinBufferSize(MicrophoneConstant.sampleRate, MicrophoneConstant.channel, MicrophoneConstant.encodingPcm16bit);
    }

    public synchronized void stop() {
        if (recordThread == null) {
            return;
        }
        running.set(false);
        try {
            recordThread.join(2000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Exception:" + e);
        }
        recordThread = null;
    }

    private int getBufferSize() {
        return MicrophoneConstant.encodingPcm16bit * (MicrophoneConstant.sampleRate / 1000) * MicrophoneConstant.sampleDuration;
    }

    private byte[] createBuffer() {
        return new byte[getBufferSize()];
    }

    private void handleAudioDataBytes(byte[] data) {
        Audio.write(msId, data, data.length, MicrophoneConstant.sampleRate);
    }

    public boolean isAudioRecordMuted() {
        return this.audioRecordMuted;
    }

    public synchronized void setAudioRecordMuted(boolean audioRecordMuted) {
        this.audioRecordMuted = audioRecordMuted;
    }

    private boolean checkThreadRunning() {
        return recordThread != null && recordThread.isAlive();
    }

    private boolean verifyInitAudioRecord() {
        if (audioRecord == null) {
            return false;
        }
        int maxTime = INIT_TIME;
        while (running.get() && AudioRecord.STATE_INITIALIZED != audioRecord.getState() && maxTime > 0) {
            maxTime -= INTERVAL;
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                Log.e(TAG, "Exception:" + e);
            }
        }
        return maxTime > 0;
    }

    private void handleAudioRecordMuted(byte[] mutedAudioBuffer) {
        handleAudioDataBytes(mutedAudioBuffer);
        try {
            Thread.sleep(MicrophoneConstant.sampleDuration);
        } catch (InterruptedException ex) {
            Log.e(TAG, "Exception:" + ex);
        }
    }

    private int handleAudioRecordVolume(byte[] audioBuffer, int size) {
        long s = 0;
        for (byte b : audioBuffer) {
            s += b * b;
        }
        double m = s / (double) size;
        return (int) (10 * Math.log10(m));
    }

    private void verifyAndHold(int size) {
        if (size <= 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Log.e(TAG, "Exception:" + ex);
            }
        }
    }

    private void notifyMicVolumeMeter(int volume, List<IFrtcCallListener> meetingMessageListeners) {
        if (meetingMessageListeners != null) {
            for (IFrtcCallListener listener : meetingMessageListeners) {
                listener.onMicVolumeMeterNotify(volume);
            }
        }
    }

    private Thread createNewAudioRecordThread(List<IFrtcCallListener> meetingMessageListeners) {
        int cacheSize = getBufferSize();
        byte[] audioBuffer = createBuffer();
        final byte[] mutedAudioBuffer = new byte[cacheSize];
        return new Thread(() -> {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
            try {
                int c = 0;
                if (!verifyInitAudioRecord()) {
                    releaseAudioRecord();
                    return;
                }
                if (running.get()) {
                    audioRecord.startRecording();
                }
                while (running.get()) {
                    if (MicrophoneRecordManager.this.isAudioRecordMuted()) {
                        handleAudioRecordMuted(mutedAudioBuffer);
                    } else {
                        int size = audioRecord.read(audioBuffer, 0, getBufferSize());
                        verifyAndHold(size);
                        handleAudioDataBytes(audioBuffer);
                        c++;
                        if (c % mod == 0) {
                            int volume = handleAudioRecordVolume(audioBuffer, size);
                            notifyMicVolumeMeter(volume, meetingMessageListeners);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception:" + e);
            } finally {
                closeAudioRecord();
            }
        }, "MicrophoneRecordManagerThread");
    }

    private void releaseAudioRecord() {
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }

    private void closeAudioRecord() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    public synchronized void start(List<IFrtcCallListener> meetingMessageListeners) {
        if (checkThreadRunning()) {
            return;
        }
        audioRecord = createNewAudioRecord();
        running.set(true);
        recordThread = createNewAudioRecordThread(meetingMessageListeners);
        recordThread.start();
    }

    private AudioRecord createNewAudioRecord() {
        int minBufferSize = getMinBufferSize();
        int cacheSize = getBufferSize();
        int audioRecordBufferSize = cacheSize > minBufferSize ? cacheSize + minBufferSize : minBufferSize * 2;
        return new AudioRecord(
                MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                MicrophoneConstant.sampleRate,
                MicrophoneConstant.channel,
                MicrophoneConstant.encodingPcm16bit,
                audioRecordBufferSize);
    }

}
