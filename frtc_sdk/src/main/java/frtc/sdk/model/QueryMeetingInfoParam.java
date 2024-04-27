package frtc.sdk.model;

public class QueryMeetingInfoParam extends CommonParam {
    private String severAddress;
    private String meetingToken;

    public String getSeverAddress() {
        return severAddress;
    }

    public void setSeverAddress(String severAddress) {
        this.severAddress = severAddress;
    }

    public String getMeetingToken() {
        return meetingToken;
    }

    public void setMeetingToken(String meetingToken) {
        this.meetingToken = meetingToken;
    }
}