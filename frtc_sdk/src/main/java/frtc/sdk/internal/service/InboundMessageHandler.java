package frtc.sdk.internal.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Messenger;

import frtc.sdk.internal.model.FrtcConstant;
import frtc.sdk.internal.model.JoinInfo;
import frtc.sdk.internal.model.InboundMessage;
import frtc.sdk.internal.model.InternalMessageType;
import frtc.sdk.log.Log;
import frtc.sdk.util.JSONUtil;
import frtc.sdk.util.StringUtils;

public class InboundMessageHandler extends Handler {
    private static final String TAG = InboundMessageHandler.class.getSimpleName();

    private Messenger messenger;

    private FrtcService frtcService;

    public InboundMessageHandler(HandlerThread handlerThread, FrtcService frtcService) {
        super(handlerThread.getLooper());
        this.frtcService = frtcService;
    }

    public Messenger getMessenger() {
        if (messenger == null) {
            messenger = new Messenger(this);
        }
        return messenger;
    }

    @Override
    public void handleMessage(Message msg) {
        int what = msg.what;
        InboundMessage messageType = InboundMessage.from(what);
        if (messageType == null) {
            Log.e(TAG, "unknown message type: " + what);
            return ;
        }
        switch (messageType) {
            case register:
                frtcService.addListener(msg.replyTo);
                break;
            case unregister:
                frtcService.removeListener(msg.replyTo);
                break;
            case joinMeetingPrepare: {
                JoinInfo joinInfo = JSONUtil.transform(msg.getData().getString(FrtcConstant.DATA), JoinInfo.class);
                frtcService.prepareJoinMeeting(joinInfo);
            }
            break;
            case joinMeeting:
                frtcService.joinMeeting();
                break;
            case leaveMeeting:
                frtcService.sendMessage(InternalMessageType.endMeeting.getName(), null);
                break;
            case mediaStatsReq: {
                String result = frtcService.sendMessage(InternalMessageType.getMediaStats.getName(), null);
                if (StringUtils.isNotBlank(result)) {
                    frtcService.broadcast(createMessage(InternalMessageType.getMediaStats.getCode(), result));
                }
            }
            break;
            case setLocalPeopleAudio:
                frtcService.sendMessage(InternalMessageType.setLocalPeopleAudio.getName(), msg.getData().getString(FrtcConstant.DATA));
                break;
            case setLocalPeopleVideo:
                frtcService.sendMessage(InternalMessageType.setLocalPeopleVideo.getName(), msg.getData().getString(FrtcConstant.DATA));
                break;
            case setRemotePeopleVideo:
                frtcService.sendMessage(InternalMessageType.setRemotePeopleVideo.getName(), msg.getData().getString(FrtcConstant.DATA));
                break;
            case inputPassword:
                frtcService.sendMessage(InternalMessageType.verifyMeetingPassword.getName(), msg.getData().getString(FrtcConstant.DATA));
                break;
            case setAudioAndVideo:
                frtcService.sendMessage(InternalMessageType.setAudioAndVideo.getName(), msg.getData().getString(FrtcConstant.DATA));
                break;
            case setNoiseBlock:
                frtcService.sendMessage(InternalMessageType.setNoiseBlock.getName(), msg.getData().getString(FrtcConstant.DATA));
                break;
            case startShareContent:
                frtcService.sendMessage(InternalMessageType.startShareContent.getName(), null);
                break;
            case stopShareContent:
                frtcService.sendMessage(InternalMessageType.stopShareContent.getName(), null);
                break;
            case getVersion: {
                String result = frtcService.sendMessage(InternalMessageType.getRTCVersion.getName(), null);
                if (StringUtils.isNotBlank(result)) {
                    frtcService.broadcast(createMessage(InternalMessageType.getRTCVersion.getCode(), result));
                }
            }
            break;
            case startTimer:
                frtcService.sendMessage(InternalMessageType.startTimer.getName(), msg.getData().getString(FrtcConstant.DATA));
                break;
            case stopTimer:
                frtcService.sendMessage(InternalMessageType.stopTimer.getName(), msg.getData().getString(FrtcConstant.DATA));
                break;
            case startUploadLogs: {
                String jsonString = msg.getData().getString(FrtcConstant.DATA);
                String result = frtcService.sendMessage(InternalMessageType.onStartUploadLogs.getName(), jsonString);
                if (StringUtils.isNotBlank(result)) {
                    frtcService.broadcast(createMessage(InternalMessageType.onStartUploadLogs.getCode(), result));
                }
            }
            break;
            case getUploadLogsStatus: {
                String jsonString = msg.getData().getString(FrtcConstant.DATA);
                String result = frtcService.sendMessage(InternalMessageType.onGetUploadLogsStatus.getName(), jsonString);
                if (StringUtils.isNotBlank(result)) {
                    frtcService.broadcast(createMessage(InternalMessageType.onGetUploadLogsStatus.getCode(), result));
                }
            }
            break;
            case cancelUploadLogs: {
                String jsonString = msg.getData().getString(FrtcConstant.DATA);
                frtcService.sendMessage(InternalMessageType.onCancelUploadLogs.getName(), jsonString);
                frtcService.broadcast(createMessage(InternalMessageType.onCancelUploadLogs.getCode()));
            }
            break;
            default:
                break;
        }
    }

    private Message createMessage(int what, String data) {
        Message message = Message.obtain();
        message.what = what;
        Bundle bundle = new Bundle();
        bundle.putString(FrtcConstant.DATA, data);
        message.setData(bundle);
        return message;
    }

    private Message createMessage(int what) {
        Message message = Message.obtain();
        message.what = what;
        return message;
    }
}
