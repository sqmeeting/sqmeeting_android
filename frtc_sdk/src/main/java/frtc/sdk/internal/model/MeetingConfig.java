package frtc.sdk.internal.model;

public class MeetingConfig {
    private boolean videoMuted = false;
    private boolean audioMuted = false;
    private boolean remoteVideoMuted = false;
    private boolean overlayVisible = false;
    private boolean waterMarkEnabled = false;
    private boolean noiseBlock = true;
    private int callRate = 0;
    private boolean audioDisabled = false;
    private boolean isLiveStarted = false;
    private boolean isRecordingStarted = false;
    private String livePassword;
    private String liveMeetingUrl;
    private boolean isHeadsetEnableSpeaker = true;
    private int cameraId = -1;

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public boolean isHeadsetEnableSpeaker() {
        return isHeadsetEnableSpeaker;
    }

    public void setHeadsetEnableSpeaker(boolean headsetEnableSpeaker) {
        isHeadsetEnableSpeaker = headsetEnableSpeaker;
    }

    public String getLivePassword() {
        return livePassword;
    }

    public void setLivePassword(String livePassword) {
        this.livePassword = livePassword;
    }

    public String getLiveMeetingUrl() {
        return liveMeetingUrl;
    }

    public void setLiveMeetingUrl(String liveMeetingUrl) {
        this.liveMeetingUrl = liveMeetingUrl;
    }

    public boolean isLiveStarted() {
        return isLiveStarted;
    }

    public void setLiveStarted(boolean liveStarted) {
        isLiveStarted = liveStarted;
    }

    public boolean isRecordingStarted() {
        return isRecordingStarted;
    }

    public void setRecordingStarted(boolean recordingStarted) {
        isRecordingStarted = recordingStarted;
    }

    public boolean isAudioDisabled() {
        return audioDisabled;
    }

    public void setAudioDisabled(boolean audioDisabled) {
        this.audioDisabled = audioDisabled;
    }

    public boolean isVideoMuted() {
        return videoMuted;
    }

    public void setVideoMuted(boolean videoMuted) {
        this.videoMuted = videoMuted;
    }

    public boolean isAudioMuted() {
        return audioMuted;
    }

    public void setAudioMuted(boolean audioMuted) {
        this.audioMuted = audioMuted;
    }

    public boolean isRemoteVideoMuted() {
        return remoteVideoMuted;
    }

    public void setRemoteVideoMuted(boolean remoteVideoMuted) {
        this.remoteVideoMuted = remoteVideoMuted;
    }

    public boolean isOverlayVisible() {
        return overlayVisible;
    }

    public void setOverlayVisible(boolean overlayVisible) {
        this.overlayVisible = overlayVisible;
    }

    public boolean isWaterMarkEnabled() {
        return waterMarkEnabled;
    }

    public void setWaterMarkEnabled(boolean waterMarkEnabled) {
        this.waterMarkEnabled = waterMarkEnabled;
    }

    public boolean isNoiseBlock() {
        return noiseBlock;
    }

    public void setNoiseBlock(boolean noiseBlock) {
        this.noiseBlock = noiseBlock;
    }

    public int getCallRate() {
        return callRate;
    }

    public void setCallRate(int callRate) {
        this.callRate = callRate;
    }
}
