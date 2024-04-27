package frtc.sdk.model;

import java.util.List;

public class MuteParam extends CommonParam {
    private boolean allow_self_unmute;
    private String meetingNumber;
    private List<String> participants;

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public boolean isAllowUnMute() {
        return allow_self_unmute;
    }

    public void setAllowUnMute(boolean allow_self_unmute) {
        this.allow_self_unmute = allow_self_unmute;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
