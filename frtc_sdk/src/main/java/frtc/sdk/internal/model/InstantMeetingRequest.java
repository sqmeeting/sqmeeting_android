package frtc.sdk.internal.model;

import frtc.sdk.model.CommonParam;

public class InstantMeetingRequest extends CommonParam {
    private String meeting_name;
    private String meeting_type;
    public InstantMeetingRequest() {

    }

    public String getMeeting_name() {
        return meeting_name;
    }

    public void setMeeting_name(String meeting_name) {
        this.meeting_name = meeting_name;
    }

    public String getMeeting_type() {
        return meeting_type;
    }

    public void setMeeting_type(String meeting_type) {
        this.meeting_type = meeting_type;
    }
}
