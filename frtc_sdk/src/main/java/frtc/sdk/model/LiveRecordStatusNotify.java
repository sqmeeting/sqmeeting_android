package frtc.sdk.model;

public class LiveRecordStatusNotify {
    private boolean liveStarted = false;
    private boolean recordStarted = false;
    private String liveMeetingUrl = "";
    private String liveMeetingPwd = "";

    public String getLiveMeetingPwd() {
        return liveMeetingPwd;
    }

    public void setLiveMeetingPwd(String liveMeetingPwd) {
        this.liveMeetingPwd = liveMeetingPwd;
    }

    public boolean isLiveStarted() {
        return liveStarted;
    }

    public void setLiveStarted(boolean liveStarted) {
        this.liveStarted = liveStarted;
    }

    public boolean isRecordStarted() {
        return recordStarted;
    }

    public void setRecordStarted(boolean recordStarted) {
        this.recordStarted = recordStarted;
    }

    public String getLiveMeetingUrl() {
        return liveMeetingUrl;
    }

    public void setLiveMeetingUrl(String liveMeetingUrl) {
        this.liveMeetingUrl = liveMeetingUrl;
    }
}
