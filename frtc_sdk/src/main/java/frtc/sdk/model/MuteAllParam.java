package frtc.sdk.model;

public class MuteAllParam extends CommonParam {
    private boolean allow_self_unmute;
    private String meetingNumber;

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public boolean isAllow_self_unmute() {
        return allow_self_unmute;
    }

    public void setAllow_self_unmute(boolean allow_self_unmute) {
        this.allow_self_unmute = allow_self_unmute;
    }
}
