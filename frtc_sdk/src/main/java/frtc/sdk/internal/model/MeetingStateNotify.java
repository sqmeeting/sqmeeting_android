package frtc.sdk.internal.model;

public class MeetingStateNotify {
    private FrtcMeetingStatus meetingStatus;

    public FrtcMeetingStatus getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(FrtcMeetingStatus meetingStatus) {
        this.meetingStatus = meetingStatus;
    }

}
