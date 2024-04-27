package frtc.sdk.model;

public class LiveMeetingParam extends CommonParam {
    private String meeting_number;
    private String live_password;

    public String getMeetingNumber() {
        return meeting_number;
    }

    public void setMeetingNumber(String meeting_number) {
        this.meeting_number = meeting_number;
    }

    public String getLive_password() {
        return live_password;
    }

    public void setLive_password(String live_password) {
        this.live_password = live_password;
    }
}
