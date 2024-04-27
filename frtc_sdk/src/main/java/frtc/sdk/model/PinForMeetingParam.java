package frtc.sdk.model;

import java.util.List;

public class PinForMeetingParam extends CommonParam {
    private List<String> participants;
    private String meeting_number;

    public String getMeetingNumber() {
        return meeting_number;
    }

    public void setMeetingNumber(String meeting_number) {
        this.meeting_number = meeting_number;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
