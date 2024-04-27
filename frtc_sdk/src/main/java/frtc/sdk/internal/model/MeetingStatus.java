
package frtc.sdk.internal.model;

public class MeetingStatus {
    private MeetingStateType meeting_status;
    private int reason_code;

    public MeetingStateType getMeeting_status() {
        return meeting_status;
    }

    public void setMeeting_status(MeetingStateType meetingState) {
        this.meeting_status = meetingState;
    }

    public int getReason_code() {
        return reason_code;
    }

    public void setReason_code(int reason_code) {
        this.reason_code = reason_code;
    }

}
