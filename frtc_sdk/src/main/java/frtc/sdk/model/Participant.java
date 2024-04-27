package frtc.sdk.model;

public class Participant  {

    private String uuid;
    private String displayName;
    private boolean muteAudio;
    private boolean muteVideo;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String id) {
        this.uuid = id;
    }

    public boolean getAudioMuted() {
        return muteAudio;
    }

    public void setAudioMuted(boolean muted) {
        this.muteAudio = muted;
    }

    public boolean getVideoMuted() {
        return muteVideo;
    }

    public void setVideoMuted(boolean muted) {
        this.muteVideo = muted;
    }

}
