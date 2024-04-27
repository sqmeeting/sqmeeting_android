package frtc.sdk.ui.component;

import frtc.sdk.ui.model.ParticipantInfo;

public interface IParticipantListListener {

    void onMuteAllParticipants();
    void onUnmuteAllParticipants();

    void onParticipantControl(ParticipantInfo participantInfo, boolean isHost, int position);

    void onPermissionsRequestList();

    void onInviteUser();
}
