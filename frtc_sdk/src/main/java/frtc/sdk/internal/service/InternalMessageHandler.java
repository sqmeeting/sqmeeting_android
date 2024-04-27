package frtc.sdk.internal.service;

import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;


import frtc.sdk.internal.IFrtcService;
import frtc.sdk.internal.IMessageListener;
import frtc.sdk.internal.model.FrtcConstant;
import frtc.sdk.internal.model.MeetingStatus;
import frtc.sdk.internal.model.MeetingInfoNotify;
import frtc.sdk.internal.model.InternalMessageType;
import frtc.sdk.log.Log;
import frtc.sdk.util.JSONUtil;

public class InternalMessageHandler implements IMessageListener {

    private final static String TAG = InternalMessageHandler.class.getSimpleName();
    private IFrtcService service;

    public InternalMessageHandler(IFrtcService service) {
        this.service = service;
    }
    @Override
    public String handleMessage(String name, String data) {
        InternalMessageType type = InternalMessageType.from(name);
        if (type == null) {
            return null;
        }
        switch (type) {
            case onMeetingJoinInfo:
                onJoinMeetingResponse(data);
                SystemClock.sleep(200);
                break;
            case onMeetingStatusChange:
                onMeetingStateNotify(data);
                break;
            case onMeetingPasswordErrorNotify:
                onMeetingPasswordErrorNotify();
                break;
            case onMeetingPasswordRequireNotify:
                onMeetingPasswordRequireNotify();
                break;
            case onRequestAudioNotify:
                onAudioReqNotify(data);
                break;
            case onAddAudioNotify:
                onAudioReceiveNotify(data);
                break;
            case onStopAudioNotify:
                onAudioReleaseNotify();
                break;
            case onRequestPeopleVideoNotify:
                onPeopleVideoReqNotify(data);
                break;
            case onStopPeopleVideoNotify:
                onPeopleVideoReleaseNotify(data);
                break;
            case onDeletePeopleVideoNotify:
                onPeopleVideoRemoveNotify(data);
                break;
            case onAddPeopleVideoNotify:
                onPeopleVideoReceiveNotify(data);
                break;
            case onMicrophonePermissionNotify:
                onMicrophonePermissionNotify(data);
                break;
            case onContentStatusChangeNotify:
                onContentChangeNotify(data);
                break;
            case onShareContentFailNotify:
                onContentShareRefusedNotify();
                break;
            case onLayoutChange:
                onLayoutChangeNotify(data);
                break;
            case onParticipantCountNotify:
                onParticipantCountChangeNotify(data);
                break;
            case onTextOverlayNotify:
                onOverlayMessageChangeNotify(data);
                break;
            case onMeetingSessionInfoNotify:
                onMeetingExtInfoChangeNotify(data);
                break;
            case onParticipantStateChangeNotify:
                onParticipantStateChangeNotify(data);
                break;
            case onLayoutSetting:
                onLayoutSettingChangeNotify(data);
                break;
            case onUnmuteRequest:
                onUnmuteReqNotify(data);
                break;
            case onUnmuteAllowNotify:
                onAllowUnmuteNotify();
                break;
            case onTimer:
                onTimer(data);
                break;
            case onNetworkStatusNotify:
                onNetworkInfoNotify(data);
                break;
            default:
                Log.w(TAG, "error message:" + name);
                break;
        }
        return null;
    }

    private void onOverlayMessageChangeNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onTextOverlayNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onOverlayMessageChangeNotify error: " + e.getMessage());
        }
    }

    private void onParticipantStateChangeNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onParticipantStateChangeNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onParticipantStateChangeNotify error: " + e.getMessage());
        }
    }

    private void onMeetingPasswordRequireNotify() {
        try {
            service.broadcast(createMessage(InternalMessageType.onMeetingPasswordRequireNotify.getCode()));
        } catch (Exception e) {
            Log.e(TAG, "onMeetingPasswordRequireNotify error: " + e.getMessage());
        }
    }

    private void onMeetingPasswordErrorNotify() {
        try {
            service.broadcast(createMessage(InternalMessageType.onMeetingPasswordErrorNotify.getCode()));
        } catch (Exception e) {
            Log.e(TAG, "onMeetingPasswordErrorNotify error: " + e.getMessage());
        }
    }

    private void onTimer(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onTimer.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onTimer error: " + e.getMessage());
        }
    }

    private void onNetworkInfoNotify(String json){
        try {
            service.broadcast(createMessage(InternalMessageType.onNetworkStatusNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onNetworkInfoNotify error: " + e.getMessage());
        }
    }

    private void onLayoutChangeNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onLayoutChange.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onLayoutChangeNotify error: " + e.getMessage());
        }
    }

    private void onContentChangeNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onContentStatusChangeNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onContentChangeNotify error: " + e.getMessage());
        }
    }

    private void onContentShareRefusedNotify(){
        try {
            service.broadcast(createMessage(InternalMessageType.onShareContentFailNotify.getCode()));
        } catch (Exception e) {
            Log.e(TAG, "onContentShareRefusedNotify error: " + e.getMessage());
        }
    }

    private void onParticipantCountChangeNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onParticipantCountNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onParticipantCountChangeNotify error: " + e.getMessage());
        }
    }

    private void onAudioReceiveNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onAddAudioNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onAudioReceiveNotify error: " + e.getMessage());
        }
    }

    private void onAudioReleaseNotify() {
        try {
            service.broadcast(createMessage(InternalMessageType.onStopAudioNotify.getCode()));
        } catch (Exception e) {
            Log.e(TAG, "onAudioReleaseNotify error: " + e.getMessage());
        }
    }

    private void onMeetingExtInfoChangeNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onMeetingSessionInfoNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG,"onMeetingExtInfoChangeNotify error " + e.getMessage());
        }
    }

    private void onJoinMeetingResponse(String json) {
        try {
            MeetingInfoNotify meetingInfoNotify = JSONUtil.transform(json, MeetingInfoNotify.class);
            String jsonString = JSONUtil.toJSONString(meetingInfoNotify);
            service.broadcast(createMessage(InternalMessageType.onMeetingJoinInfo.getCode(), jsonString));
        } catch (Exception e) {
            Log.e(TAG, "onJoinMeetingResponse error: " + e.getMessage());
        }
    }

    private void onAudioReqNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onRequestAudioNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onAudioReqNotify error: " + e.getMessage());
        }
    }

    private void onPeopleVideoRemoveNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onDeletePeopleVideoNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onPeopleVideoRemoveNotify error: " + e.getMessage());
        }
    }

    private void onMicrophonePermissionNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onMicrophonePermissionNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onMicrophonePermissionNotify error: " + e.getMessage());
        }
    }

    private void onPeopleVideoReceiveNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onAddPeopleVideoNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onPeopleVideoReceiveNotify error: " + e.getMessage());
        }
    }

    private void onPeopleVideoReleaseNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onStopPeopleVideoNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onPeopleVideoReleaseNotify error: " + e.getMessage());
        }
    }

    private void onPeopleVideoReqNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onRequestPeopleVideoNotify.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onPeopleVideoReqNotify error: " + e.getMessage());
        }
    }

    private void onMeetingStateNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onMeetingStatusChange.getCode(), json));
            MeetingStatus meetingStatus = JSONUtil.transform(json, MeetingStatus.class);
            this.service.updateMeetingState(meetingStatus);
        } catch (Exception e) {
            Log.e(TAG, "onMeetingStateNotify error: " + e.getMessage());
        }
    }

    private void onLayoutSettingChangeNotify(String json){
        try {
            service.broadcast(createMessage(InternalMessageType.onLayoutSetting.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onLayoutSettingChangeNotify error: " + e.getMessage());
        }
    }

    private void onUnmuteReqNotify(String json) {
        try {
            service.broadcast(createMessage(InternalMessageType.onUnmuteRequest.getCode(), json));
        } catch (Exception e) {
            Log.e(TAG, "onUnmuteReqNotify error: " + e.getMessage());
        }
    }

    private void onAllowUnmuteNotify() {
        try {
            service.broadcast(createMessage(InternalMessageType.onUnmuteAllowNotify.getCode()));
        } catch (Exception e) {
            Log.e(TAG, "onAllowUnmuteNotify error: " + e.getMessage());
        }
    }

    private Message createMessage(int what) {
        Message message = Message.obtain();
        message.what = what;
        return message;
    }

    private Message createMessage(int what, String jsonData) {
        Message message = Message.obtain();
        message.what = what;
        Bundle bundle = new Bundle();
        bundle.putString(FrtcConstant.DATA, jsonData);
        message.setData(bundle);
        return message;
    }
}
