package frtc.sdk.model;

public class QueryMeetingInfoResult {
    private String meeting_number;
    private String meeting_password;
    private String meeting_name;
    private String meeting_link;
    private String meeting_url;
    private String schedule_start_time;
    private String schedule_end_time;
    private String meeting_status;

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

    public String getMeeting_name() {
        return meeting_name;
    }

    public void setMeeting_name(String meeting_name) {
        this.meeting_name = meeting_name;
    }

    public String getMeeting_link() {
        return meeting_link;
    }

    public void setMeeting_link(String meeting_link) {
        this.meeting_link = meeting_link;
    }

    public String getMeeting_url() {
        return meeting_url;
    }

    public void setMeeting_url(String meeting_url) {
        this.meeting_url = meeting_url;
    }

    public String getSchedule_start_time() {
        return schedule_start_time;
    }

    public void setSchedule_start_time(String schedule_start_time) {
        this.schedule_start_time = schedule_start_time;
    }

    public String getSchedule_end_time() {
        return schedule_end_time;
    }

    public void setSchedule_end_time(String schedule_end_time) {
        this.schedule_end_time = schedule_end_time;
    }

    public String getMeeting_status() {
        return meeting_status;
    }

    public void setMeeting_status(String meeting_status) {
        this.meeting_status = meeting_status;
    }
}
