package frtc.sdk.model;

public class MeetingOption {
    protected boolean noVideo = false;
    protected boolean noAudio = false;
    protected boolean audioOnly = false;
    private int callRate = 0;

    public boolean isNoVideo() {
        return noVideo;
    }

    public void setNoVideo(boolean noVideo) {
        this.noVideo = noVideo;
    }

    public boolean isNoAudio() {
        return noAudio;
    }

    public void setNoAudio(boolean noAudio) {
        this.noAudio = noAudio;
    }

    public boolean isAudioOnly() {
        return audioOnly;
    }

    public void setAudioOnly(boolean audioOnly) {
        this.audioOnly = audioOnly;
    }

    public int getCallRate() {
        return callRate;
    }

    public void setCallRate(int callRate) {
        this.callRate = callRate;
    }
}
