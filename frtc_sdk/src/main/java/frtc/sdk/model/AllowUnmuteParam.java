package frtc.sdk.model;

import java.util.List;

public class AllowUnmuteParam extends CommonParam {
    private String meeting_number;
    private List<String> participants;

    public String getMeeting_number() {
        return meeting_number;
    }

    public void setMeeting_number(String meeting_number) {
        this.meeting_number = meeting_number;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

}