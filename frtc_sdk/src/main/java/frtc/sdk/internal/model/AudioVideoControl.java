package frtc.sdk.internal.model;

public class AudioVideoControl {
    private boolean audio_mute;
    private boolean video_mute;

    public boolean isAudio_mute() {
        return audio_mute;
    }

    public void setAudio_mute(boolean audio_mute) {
        this.audio_mute = audio_mute;
    }

    public boolean isVideo_mute() {
        return video_mute;
    }

    public void setVideo_mute(boolean video_mute) {
        this.video_mute = video_mute;
    }
}
