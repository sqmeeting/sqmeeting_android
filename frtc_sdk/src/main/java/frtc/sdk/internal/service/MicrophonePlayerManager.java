package frtc.sdk.internal.service;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.concurrent.atomic.AtomicBoolean;

import frtc.sdk.internal.jni.Audio;
import frtc.sdk.log.Log;

public class MicrophonePlayerManager {

    private final String TAG = this.getClass().getSimpleName();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final String msId;
    private AudioTrack audioTrack = null;
    private Thread playerThread = null;

    public MicrophonePlayerManager(String msId) {
        this.msId = msId;
    }

    private static int getBufferSize() {
        return MicrophoneConstant.sampleLength * MicrophoneConstant.sampleRate * MicrophoneConstant.sampleDuration / 1000;
    }

    private static int getMinBufferSize() {
        return AudioTrack.getMinBufferSize(MicrophoneConstant.sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }

    public synchronized void stop() {
        if (playerThread == null) {
            return;
        }
        running.set(false);
        try {
            playerThread.join(2000);
        } catch (InterruptedException e) {
            Log.e(TAG, "Exception:" + e);
        }
        playerThread = null;
    }

    private boolean checkThreadRunning() {
        return playerThread != null && playerThread.isAlive();
    }

    public synchronized void start() {
        try {
            if (checkThreadRunning()) {
                return;
            }
            closeAudioTrack();
            audioTrack = createNewAudioTrack();
            audioTrack.play();
            running.set(true);
            playerThread = createNewPlayerThread();
            playerThread.start();
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e);
        }
    }

    private byte[] createBuffer() {
        return new byte[getBufferSize()];
    }

    private Thread createNewPlayerThread() {
        return new Thread(() -> {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            try {
                int bufferSize = getBufferSize();
                byte[] buffer = createBuffer();
                long total = 0;
                while (running.get()) {
                    try {
                        long remained = total - 2L * audioTrack.getPlaybackHeadPosition();
                        boolean r = Audio.read(msId, buffer, bufferSize, MicrophoneConstant.sampleRate, (int) remained);
                        if (!r) {
                            continue;
                        }
                        total += bufferSize;
                        audioTrack.write(buffer, 0, buffer.length);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception:" + e);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception:" + e);
            } finally {
                closeAudioTrack();
            }
        }, "MicroPhoneManagerThread");
    }

    private void closeAudioTrack() {
        try {
            if (audioTrack != null) {
                audioTrack.stop();
                audioTrack.release();
                audioTrack = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e);
        }
    }

    private AudioTrack createNewAudioTrack() {
        return new AudioTrack(AudioManager.STREAM_VOICE_CALL, MicrophoneConstant.sampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, getMinBufferSize(),
                AudioTrack.MODE_STREAM);
    }

}
