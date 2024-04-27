package frtc.sdk.internal.service;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import frtc.sdk.internal.model.InternalMessageType;

public class FrtcMessageHandler implements Handler.Callback {

    private static final String TAG = FrtcMessageHandler.class.getSimpleName();

    private MeetingService meetingService;

    public FrtcMessageHandler(MeetingService meetingService) {
        this.meetingService = meetingService;
    }


    @Override
    public boolean handleMessage(@NonNull Message msg) {
        InternalMessageType type = InternalMessageType.from(msg.what);
        if (type == null) {
            return false;
        }
        switch (type) {
            case onMeetingJoinInfo:
                meetingService.onMeetingJoinInfo(msg);
                break;
            case onMeetingStatusChange:
                meetingService.onMeetingStatusChange(msg);
                break;
            case onMeetingPasswordErrorNotify:
                meetingService.onMeetingPasswordErrorNotify();
                break;
            case onMeetingPasswordRequireNotify:
                meetingService.onMeetingPasswordRequireNotify();
                break;
            case onRequestAudioNotify:
                meetingService.onRequestAudioNotify(msg);
                break;
            case onAddAudioNotify:
                meetingService.onAddAudioNotify(msg);
                break;
            case onStopAudioNotify:
                meetingService.onAudioStopNotify();
                break;
            case onRequestPeopleVideoNotify:
                meetingService.onRequestPeopleVideoNotify(msg);
                break;
            case onAddPeopleVideoNotify:
                meetingService.onAddPeopleVideoNotify(msg);
                break;
            case onStopPeopleVideoNotify:
                meetingService.onStopPeopleVideoNotify(msg);
                break;
            case onDeletePeopleVideoNotify:
                meetingService.onDeletePeopleVideoNotify(msg);
                break;
            case onMicrophonePermissionNotify:
                meetingService.onMicrophonePermissionNotify(msg);
                break;
            case onLayoutChange:
                meetingService.onLayoutInfoNotify(msg);
                break;
            case onLayoutSetting:
                meetingService.onLayoutSettingNotify(msg);
                break;
            case onUnmuteRequest:
                meetingService.onUnmuteReqNotify(msg);
                break;
            case onUnmuteAllowNotify:
                meetingService.onAllowUnmuteNotify();
                break;
            case onShareContentFailNotify:
                meetingService.onStartContentFailNotify();
                break;
            case onContentStatusChangeNotify:
                meetingService.onContentStatusNotify(msg);
                break;
            case onTimer:
                meetingService.onTimerEventNotify(msg);
                break;
            case onParticipantCountNotify:
                meetingService.onParticipantCountNotify(msg, false);
                break;
            case onParticipantStateChangeNotify:
                meetingService.onParticipantStateNotify(msg, false);
                break;
            case onTextOverlayNotify:
                meetingService.onTextOverlayNotify(msg);
                break;
            case onNetworkStatusNotify:
                meetingService.onNetworkInfoNotify(msg);
                break;
            case getRTCVersion:
                meetingService.onGetRTCVersionResultNotify(msg);
                break;
            case getMediaStats:
                meetingService.onGetMediaStatsInfoResultNotify(msg);
                break;
            case onMeetingSessionInfoNotify:
                meetingService.onMeetingSessionInfoNotify(msg);
                break;
            case temperatureInfoNotify:
                meetingService.onTemperatureInfoNotify(msg);
                break;
            case onStartUploadLogs:
                meetingService.onStartUploadLogsResultNotify(msg);
                break;
            case onGetUploadLogsStatus:
                meetingService.onGetUploadLogsStatusNotify(msg);
                break;
            case onCancelUploadLogs:
                meetingService.onCancelUploadLogsNotify();
                break;

            default:
                break;
        }
        return false;
    }
}
