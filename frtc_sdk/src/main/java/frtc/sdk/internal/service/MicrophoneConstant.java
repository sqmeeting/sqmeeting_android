package frtc.sdk.internal.service;

import android.media.AudioFormat;

public class MicrophoneConstant {
    public static final int sampleLength = 2;
    public static final int sampleRate = 48000;
    public static final int sampleDuration = 20;
    public static final int channel = AudioFormat.CHANNEL_IN_MONO;
    public static final int encodingPcm16bit = AudioFormat.ENCODING_PCM_16BIT;
}
