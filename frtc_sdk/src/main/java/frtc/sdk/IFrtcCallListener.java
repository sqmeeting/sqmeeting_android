package frtc.sdk;


import java.util.List;

import frtc.sdk.internal.model.LecturerInfo;
import frtc.sdk.model.UnmuteRequest;
import frtc.sdk.internal.model.MeetingMediaStatsWrapper;
import frtc.sdk.internal.model.ParticipantInfo;
import frtc.sdk.model.LiveRecordStatusNotify;
import frtc.sdk.model.MeetingStateInfoNotify;
import frtc.sdk.model.OverlayNotify;
import frtc.sdk.model.MuteControlStateNotify;
import frtc.sdk.model.ParticipantNumberChangeNotify;
import frtc.sdk.model.ParticipantStateChangeNotify;

public interface IFrtcCallListener {

    void onMeetingInfoNotify(MeetingStateInfoNotify meetingStateInfoNotify);
    void onMeetingStateNotify(MeetingStateInfoNotify meetingStateInfoNotify);
    void onMeetingPasswordErrorNotify();
    void onMeetingPasswordRequestNotify();
    void onOverlayNotify(OverlayNotify overlayNotify);
    void onParticipantNumberChangeNotify(ParticipantNumberChangeNotify participantNumberChangeNotify);
    void onParticipantStateChangeNotify(ParticipantStateChangeNotify participantStateChangeNotify);
    void onMuteControlStateNotify(MuteControlStateNotify muteControlStateNotify);
    void onTemperatureInfoNotify(boolean overheat);
    void onHeadsetEnableSpeakerNotify(boolean enableSpeaker);
    void onMediaStatsInfoNotify(MeetingMediaStatsWrapper stat, boolean isEncrypt);

    void onInviteUserNotify(List<ParticipantInfo> participantInfos);
    void onLayoutSettingNotify(LecturerInfo lecturerInfo);
    void onContentStateNotify(boolean isSharingContent);
    void onLiveRecordStatusNotify(LiveRecordStatusNotify notify);
    void onUnmuteRequestNotify(UnmuteRequest unmuteRequest);
    void onAllowUnmuteNotify();
    void onPinNotify(String pinUuid);
    void onNetworkInfoNotify(String networkState);
    void onStartContentRefusedNotify();
    void onMicVolumeMeterNotify(int volume);
}
