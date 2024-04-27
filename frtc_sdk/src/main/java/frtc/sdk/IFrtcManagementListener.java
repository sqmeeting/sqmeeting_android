package frtc.sdk;

import java.util.List;

import frtc.sdk.internal.model.FindUserResult;
import frtc.sdk.internal.model.ResultType;
import frtc.sdk.model.CreateInstantMeetingResult;
import frtc.sdk.model.CreateScheduledMeetingResult;
import frtc.sdk.model.QueryMeetingInfoResult;
import frtc.sdk.model.QueryMeetingRoomResult;
import frtc.sdk.model.QueryUserInfoResult;
import frtc.sdk.model.ScheduledMeetingListResult;
import frtc.sdk.model.ScheduledMeetingResult;
import frtc.sdk.model.SignInResult;

public interface IFrtcManagementListener {
    void onStopMeetingResult(ResultType resultType);
    void onSignInResult(ResultType resultType, SignInResult signInResult);
    void onSignOutResult(ResultType resultType);
    void onUpdatePasswordResult(ResultType resultType);
    void onQueryUserInfoResult(ResultType resultType, QueryUserInfoResult queryUserInfoResult);

    void onQueryMeetingRoomInfoResult(ResultType resultType, QueryMeetingRoomResult queryMeetingRoomResult);
    void onQueryMeetingInfoResult(ResultType resultType, QueryMeetingInfoResult queryMeetingInfoResult);

    void onCreateInstantMeetingResult(ResultType resultType, CreateInstantMeetingResult createInstantMeetingResult);
    void onMuteAllParticipantResult(ResultType resultType, boolean allowUnMute);
    void onMuteParticipantResult(ResultType resultType);
    void onUnMuteAllParticipantResult(ResultType resultType);
    void onUnMuteParticipantResult(ResultType resultType);
    void onChangeDisplayNameResult(ResultType resultType, String name, boolean isMe);
    void onSetLecturerResult(ResultType resultType, boolean isSetLecturer);

    void onCreateScheduledMeetingResult(ResultType resultType, CreateScheduledMeetingResult createScheduledMeetingResult);
    void onUpdateScheduledMeetingResult(ResultType resultType);
    void onDeleteScheduledMeetingResult(ResultType resultType);

    void onGetScheduledMeetingResult(ResultType resultType, ScheduledMeetingResult scheduledMeetingResult);
    void onGetScheduledMeetingListResult(ResultType resultType, ScheduledMeetingListResult scheduledMeetingListResult);
    void onGetScheduledRecurrenceMeetingListResult(ResultType resultType, ScheduledMeetingListResult scheduledMeetingListResult);
    void onFindUserResultResult(ResultType resultType, FindUserResult findUserResult);

    void onFrtcSdkLeaveMeetingNotify();

    void onStartOverlay(ResultType resultType);
    void onStopOverlay(ResultType resultType);

    void onDisconnectParticipants(ResultType resultType);
    void onDisconnectAllParticipants(ResultType resultType);

    void onStartRecording(ResultType resultType, String errorCode);
    void onStopRecording(ResultType resultType);
    void onStartLive(ResultType resultType, String errorCode);
    void onStopLive(ResultType resultType);

    void onUnMuteSelfResult(ResultType resultType);
    void onAllowUnmuteResult(ResultType resultType, List<String> participants);

    void onPinForMeeting(ResultType resultType);
    void onUnPinForMeeting(ResultType resultType);

    void onAddMeetingIntoMeetingListResult(ResultType resultType);
    void onRemoveMeetingFromMeetingList(ResultType resultType);
}
