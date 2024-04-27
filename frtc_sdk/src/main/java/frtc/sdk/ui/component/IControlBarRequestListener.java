package frtc.sdk.ui.component;

public interface IControlBarRequestListener {

    void onEndMeeting();

    void onSwitchCamera();

    void onSwitchSpeaker();

    void onShowMeetingDetails();

    void onMuteAudio();

    void onMuteVideo();

    void onShowParticipants();

    void onShareContent();

    void onShowInviteInfo();

    void onStartOverlay();

    void onStopOverlay();

    void onLive();

    void onRecord();

    void onMuteRemote();

    void onFloatMode();
}
