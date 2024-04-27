package frtc.sdk.internal.model;

public class MeetingInfoNotify {
    private String meeting_alias;
    private String meeting_name;
    private String display_name;
    private String owner_id;
    private String owner_name;
    private String meeting_url;
    private String group_meeting_url = "";
    private long start_time;
    private long end_time;


    public String getGroup_meeting_url() {
        return group_meeting_url;
    }

    public void setGroup_meeting_url(String group_meeting_url) {
        this.group_meeting_url = group_meeting_url;
    }

    public String getMeeting_alias() {
        return meeting_alias;
    }

    public void setMeeting_alias(String dialString) {
        this.meeting_alias = dialString;
    }

    public void setMeeting_name(String meeting_name) {
        this.meeting_name = meeting_name;
    }

    public String getMeeting_name() {
        return meeting_name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getMeeting_url() {
        return meeting_url;
    }

    public void setMeeting_url(String meeting_url) {
        this.meeting_url = meeting_url;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

}
