package frtc.sdk.model;

public class AddMeetingToListParam extends CommonParam {

    private String meeting_identifier;

    public AddMeetingToListParam(){

    }

    public String getMeeting_identifier() {
        return meeting_identifier;
    }

    public void setMeeting_identifier(String meeting_identifier) {
        this.meeting_identifier = meeting_identifier;
    }
}
