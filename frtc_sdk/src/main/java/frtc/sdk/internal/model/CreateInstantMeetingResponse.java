package frtc.sdk.internal.model;

public class CreateInstantMeetingResponse {
    private String meeting_type;
    private String meeting_name;
    private String meeting_number;
    private String meeting_password;
    private String owner_name;

    public String getMeeting_type() {
        return meeting_type;
    }

    public void setMeeting_type(String meeting_type) {
        this.meeting_type = meeting_type;
    }

    public String getMeeting_name() {
        return meeting_name;
    }

    public void setMeeting_name(String meeting_name) {
        this.meeting_name = meeting_name;
    }

    public String getMeeting_number() {
        return meeting_number;
    }

    public void setMeeting_number(String meeting_number) {
        this.meeting_number = meeting_number;
    }

    public String getMeeting_password() {
        return meeting_password;
    }

    public void setMeeting_password(String meeting_password) {
        this.meeting_password = meeting_password;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }
}
