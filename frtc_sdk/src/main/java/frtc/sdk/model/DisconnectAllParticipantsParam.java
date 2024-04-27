package frtc.sdk.model;

public class DisconnectAllParticipantsParam extends CommonParam {
    private String meetingNumber;

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }
}
