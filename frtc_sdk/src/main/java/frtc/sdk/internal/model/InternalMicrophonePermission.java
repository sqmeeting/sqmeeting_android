package frtc.sdk.internal.model;

public class InternalMicrophonePermission {
    private boolean audio_mute;
    private boolean allow_self_unmute;

    public boolean isAudio_mute() {
        return audio_mute;
    }

    public void setAudio_mute(boolean audio_mute) {
        this.audio_mute = audio_mute;
    }

    public boolean isAllow_self_unmute() {
        return allow_self_unmute;
    }

    public void setAllow_self_unmute(boolean allow_self_unmute) {
        this.allow_self_unmute = allow_self_unmute;
    }
}
