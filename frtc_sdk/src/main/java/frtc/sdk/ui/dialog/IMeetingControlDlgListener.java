package frtc.sdk.ui.dialog;

import java.util.List;

import frtc.sdk.model.UnmuteRequest;
import frtc.sdk.ui.model.ParticipantInfo;

public interface IMeetingControlDlgListener {
    void onLeaveMeeting();
    void onEndMeeting();

    void onClickConfirmMuteAll(boolean isAllowUnmute);
    void onClickConfirmUnmuteAll();

    void onClickConfirmMute(List<String> participants);
    void onClickConfirmUnMute(List<String> participants);
    void onClickAudio();

    void onChangeDisplayNameInParticipants(ParticipantInfo participantInfo);
    void onClickConfirmChangeName(String uuid, String displayName);
    void onStartOverlay(String content,int repeat,int position,boolean scroll);
    void onDisconnectParticipant(String uuid);
    void onClickConfirmLecturer(String uuid);
    void onPin(String pinUuid);
    void onUnpin(String unpinUuid);

    void onViewPermissionsRequest();
    void onAllowUnmute(List<UnmuteRequest> unmuteRequestParticipants);

    void onSetUnviewGone();
}
