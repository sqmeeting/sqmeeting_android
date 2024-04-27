package frtc.sdk.internal;

import android.os.Message;

import frtc.sdk.internal.model.MeetingStatus;

public interface IFrtcService {
    void broadcast(Message message);

    void updateMeetingState(MeetingStatus meetingStatus);
}
