package frtc.sdk;

import java.util.List;

import frtc.sdk.internal.model.MeetingStateNotify;
import frtc.sdk.model.ParticipantInfo;

public interface IMeetingStatusListener {
    void onMeetingStateNotify(MeetingStateNotify meetingStateNotify);
    void onInviteUserNotify(List<ParticipantInfo> participantList);
    void onParticipantChangeNotify(List<ParticipantInfo> participantList);

}
