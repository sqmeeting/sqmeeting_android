package frtc.sdk.model;

import java.util.List;

public class DisconnectParticipantParam extends CommonParam {
    private String meetingNumber;
    private List<String> participants;

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
