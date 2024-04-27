package frtc.sdk.model;

import java.util.List;

public class UnMuteParam extends CommonParam {
    private List<String> participants;
    private String meetingNumber;

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
