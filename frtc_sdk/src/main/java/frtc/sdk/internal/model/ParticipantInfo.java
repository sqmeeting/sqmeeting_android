package frtc.sdk.internal.model;


public class ParticipantInfo {
    private String display_name;
    private boolean video_mute;
    private boolean audio_mute;
    private String uuid;
    private String user_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String name) {
        this.display_name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String id) {
        this.uuid = id;
    }

    public boolean getAudioMuted() {
        return audio_mute;
    }

    public void setAudioMuted(boolean muted) {
        this.audio_mute = muted;
    }

    public boolean getVideoMuted() {
        return video_mute;
    }

    public void setVideoMuted(boolean muted) {
        this.video_mute = muted;
    }

}
