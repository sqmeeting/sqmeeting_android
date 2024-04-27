package frtc.sdk;

import android.content.Context;

import frtc.sdk.model.AddMeetingToListParam;
import frtc.sdk.model.AllowUnmuteParam;
import frtc.sdk.model.ChangeDisplayNameParam;
import frtc.sdk.model.CommonMeetingParam;
import frtc.sdk.model.CreateScheduledMeetingParam;
import frtc.sdk.model.DeleteScheduledMeetingParam;
import frtc.sdk.model.DisconnectAllParticipantsParam;
import frtc.sdk.model.DisconnectParticipantParam;
import frtc.sdk.model.FindUserParam;
import frtc.sdk.model.GetScheduledMeetingListParam;
import frtc.sdk.model.GetScheduledMeetingParam;
import frtc.sdk.model.InitParams;
import frtc.sdk.model.InstantMeetingParam;
import frtc.sdk.model.LectureParam;
import frtc.sdk.model.LiveMeetingParam;
import frtc.sdk.model.MuteAllParam;
import frtc.sdk.model.MuteParam;
import frtc.sdk.model.PasswordUpdateParam;
import frtc.sdk.model.PinForMeetingParam;
import frtc.sdk.model.QueryMeetingInfoParam;
import frtc.sdk.model.QueryMeetingRoomParam;
import frtc.sdk.model.QueryUserInfoParam;
import frtc.sdk.model.SignInParam;
import frtc.sdk.model.SignOutParam;
import frtc.sdk.model.StartOverlayParam;
import frtc.sdk.model.StopMeetingParam;
import frtc.sdk.model.StopOverlayParam;
import frtc.sdk.model.UnMuteAllParam;
import frtc.sdk.model.UnMuteParam;
import frtc.sdk.model.UpdateScheduledMeetingParam;

public interface IFrtcManagementAPI extends IFrtcAPI {

    boolean initialize(Context context, InitParams initParams);
    void registerManagementListener(IFrtcManagementListener listener);
    void unRegisterManagementListener(IFrtcManagementListener listener);
    void shutdown();

    void signIn(SignInParam signInParam);
    void signOut(SignOutParam signOutParam);
    void queryUserInfo(QueryUserInfoParam queryUserInfoParam);
    void updatePassword(PasswordUpdateParam passwordUpdateParam);

    void queryMeetingRoomInfo(QueryMeetingRoomParam queryMeetingRoomParam);
    void queryMeetingInfo(QueryMeetingInfoParam queryMeetingInfoParam);

    void createInstantMeeting(InstantMeetingParam instantMeetingParam);
    void muteAllParticipant(MuteAllParam muteAllParam);
    void muteParticipant(MuteParam muteParam);
    void unMuteAllParticipant(UnMuteAllParam unMuteAllParam);
    void unMuteParticipant(UnMuteParam unMuteParam);
    void stopMeeting(StopMeetingParam stopMeetingParam);
    void changeNameParticipant(ChangeDisplayNameParam changeDisplayNameParam);
    void lectureParticipant(LectureParam lectureParam);

    void startOverlay(StartOverlayParam startOverlayParam);
    void stopOverlay(StopOverlayParam stopOverlayParam);

    void disconnectParticipant(DisconnectParticipantParam disconnectParticipantParam);
    void disconnectAllParticipants(DisconnectAllParticipantsParam disconnectAllParticipantsParam);

    void startRecording(CommonMeetingParam startRecordingParam);
    void stopRecording(CommonMeetingParam stopRecordingParam);
    void startLive(LiveMeetingParam startLiveParam);
    void stopLive(LiveMeetingParam stopLiveParam);

    void pinForMeeting(PinForMeetingParam pinParam);
    void unpinForMeeting(CommonMeetingParam unpinParam);

    void createScheduledMeeting(CreateScheduledMeetingParam createScheduledMeetingParam);
    void updateScheduledMeeting(UpdateScheduledMeetingParam updateScheduledMeetingParam);
    void deleteScheduledMeeting(final DeleteScheduledMeetingParam deleteScheduledMeetingParam);
    void getScheduledMeeting(GetScheduledMeetingParam getScheduledMeetingParam);
    void getScheduledMeetingList(GetScheduledMeetingListParam getScheduledMeetingListParam);
    void getScheduledRecurrenceMeetingList(GetScheduledMeetingListParam getScheduledMeetingListParam);

    void findUser(FindUserParam findUSerParam);

    void frtcSdkLeaveMeeting();

    void unMuteSelf(CommonMeetingParam commonMeetingParam);
    void allowUnmute(AllowUnmuteParam allowUnmuteParam);

    void addMeetingIntoMeetingList(AddMeetingToListParam param);
    void removeMeetingFromMeetingList(AddMeetingToListParam param);
}
