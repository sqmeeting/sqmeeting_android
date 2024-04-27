package frtc.sdk.model;

public class MuteControlStateNotify {
    private Boolean isAudioMute;
    private Boolean isMicDisabled;

    public Boolean getAudioMute() {
        return isAudioMute;
    }

    public void setAudioMute(Boolean audioMute) {
        isAudioMute = audioMute;
    }

    public Boolean getMicDisabled() {
        return isMicDisabled;
    }

    public void setMicDisabled(Boolean micDisable) {
        isMicDisabled = micDisable;
    }

}
