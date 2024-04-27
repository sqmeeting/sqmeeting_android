package frtc.sdk.internal.service;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import frtc.sdk.internal.model.FrtcConstant;
import frtc.sdk.internal.model.JoinInfo;
import frtc.sdk.internal.model.AudioVideoControl;
import frtc.sdk.internal.model.InboundMessage;
import frtc.sdk.internal.model.LocalPeopleAudioParam;
import frtc.sdk.internal.model.NoiseBlockParam;
import frtc.sdk.internal.model.PasswordParam;
import frtc.sdk.internal.model.InternalVideoControlMessage;
import frtc.sdk.internal.model.LogUploadGet;
import frtc.sdk.internal.model.LogUploadCancel;
import frtc.sdk.internal.model.LogUploadReq;
import frtc.sdk.internal.model.StartTimerParam;
import frtc.sdk.internal.model.StopTimerParam;
import frtc.sdk.log.Log;
import frtc.sdk.util.JSONUtil;
import frtc.sdk.util.StringUtils;


public class MessengerManager {
    private static final String TAG = MessengerManager.class.getSimpleName();

    private Messenger msgr;

    public MessengerManager(IBinder binder) {
        msgr = new Messenger(binder);
    }

    public void register(Messenger messenger) {
        Message message = Message.obtain();
        message.what = InboundMessage.register.getCode();
        message.replyTo = messenger;
        sendMessage(message);
    }

    public void unregister(Messenger messenger) {
        Message message = Message.obtain();
        message.what = InboundMessage.unregister.getCode();
        message.replyTo = messenger;
        sendMessage(message);
    }

    private Message createMessage(InboundMessage inboundMessage) {
        if (inboundMessage == null) {
            return null;
        }
        Message message = Message.obtain();
        message.what = inboundMessage.getCode();
        return message;
    }

    private Message createMessage(InboundMessage inboundMessage, String data) {
        if (inboundMessage == null) {
            return null;
        }
        Message message = Message.obtain();
        message.what = inboundMessage.getCode();
        Bundle bundle = message.getData();
        bundle.putString(FrtcConstant.DATA, data);
        return message;
    }

    public void joinMeetingPrepare(JoinInfo joinInfo) {
        try {
            if (joinInfo == null) {
                Log.e(TAG, "joinMeetingPrepare: joinInfo is null");
                return;
            }
            sendMessage(createMessage(InboundMessage.joinMeetingPrepare, JSONUtil.toJSONString(joinInfo)));
        } catch (Exception e) {
            Log.e(TAG, "joinMeetingPrepare error:" + e);
        }
    }

    public void joinMeeting() {
        try {
            sendMessage(createMessage(InboundMessage.joinMeeting));
        } catch (Exception e) {
            Log.e(TAG, "joinMeeting error:" + e);
        }
    }

    public void leaveMeeting() {
        try {
            sendMessage(createMessage(InboundMessage.leaveMeeting));
        } catch (Exception e) {
            Log.e(TAG, "leaveMeeting error:" + e);
        }
    }

    public void setVideoMute(boolean muted) {
        try {
            InternalVideoControlMessage internalVideoControlMessage = new InternalVideoControlMessage();
            internalVideoControlMessage.setVideo_mute(muted);
            sendMessage(createMessage(InboundMessage.setLocalPeopleVideo, JSONUtil.toJSONString(internalVideoControlMessage)));
        } catch (Exception e) {
            Log.e(TAG, "setVideoMute error:" + e);
        }
    }

    public void setAudioMute(boolean muted) {
        try {
            LocalPeopleAudioParam localPeopleAudioParam = new LocalPeopleAudioParam();
            localPeopleAudioParam.setAudio_mute(muted);
            sendMessage(createMessage(InboundMessage.setLocalPeopleAudio, JSONUtil.toJSONString(localPeopleAudioParam)));
        } catch (Exception e) {
            Log.e(TAG, "setAudioMute error:" + e);
        }
    }

    public void setRemoteVideoMute(boolean muted){
        try {
            InternalVideoControlMessage internalVideoControlMessage = new InternalVideoControlMessage();
            internalVideoControlMessage.setVideo_mute(muted);
            sendMessage(createMessage(InboundMessage.setRemotePeopleVideo, JSONUtil.toJSONString(internalVideoControlMessage)));
        } catch (Exception e) {
            Log.e(TAG, "setRemoteVideoMute error:" + e);
        }
    }

    public void inputPassword(String password) {
        try {
            if (StringUtils.isBlank(password)) {
                Log.e(TAG, "inputPassword: password is null");
                return;
            }
            PasswordParam passwordParam = new PasswordParam();
            passwordParam.setPasscode(password);
            sendMessage(createMessage(InboundMessage.inputPassword, JSONUtil.toJSONString(passwordParam)));
        } catch (Exception e) {
            Log.e(TAG, "inputPassword error:" + e);
        }
    }

    public void setAudioVideoControl(boolean audioMuted, boolean videoMuted) {
        try {
            AudioVideoControl control = new AudioVideoControl();
            control.setAudio_mute(audioMuted);
            control.setVideo_mute(videoMuted);
            sendMessage(createMessage(InboundMessage.setAudioAndVideo, JSONUtil.toJSONString(control)));
        } catch (Exception e) {
            Log.e(TAG, "sendMuteStateReport error:" + e);
        }
    }

    public void setNoiseBlock(boolean enabled) {
        try {
            NoiseBlockParam param = new NoiseBlockParam();
            param.setNb_enable(enabled);
            sendMessage(createMessage(InboundMessage.setNoiseBlock, JSONUtil.toJSONString(param)));
        } catch (Exception e) {
            Log.e(TAG, "setNoiseBlock error:" + e);
        }
    }

    public void fetchSDKVersion() {
        try {
            sendMessage(createMessage(InboundMessage.getVersion));
        } catch (Exception e) {
            Log.e(TAG, "fetchSDKVersion error:" + e);
        }
    }

    public void startSendContent(){
        try {
            sendMessage(createMessage(InboundMessage.startShareContent));
        } catch (Exception e) {
            Log.e(TAG, "startSendContent error:" + e);
        }
    }

    public void stopSendContent(){
        try {
            sendMessage(createMessage(InboundMessage.stopShareContent));
        } catch (Exception e) {
            Log.e(TAG, "stopSendContent error:" + e);
        }
    }

    public void startTimer(int timerID, int duration, boolean periodic){
        try {
            StartTimerParam startTimerParam = new StartTimerParam();
            startTimerParam.setTimer_id(timerID);
            startTimerParam.setTimer_interval(duration);
            startTimerParam.setTimer_periodic(periodic);
            sendMessage(createMessage(InboundMessage.startTimer, JSONUtil.toJSONString(startTimerParam)));
        } catch (Exception e) {
            Log.e(TAG, "startTimer error:" + e);
        }
    }

    public void stopTimer(int timer_id){
        try {
            StopTimerParam param = new StopTimerParam();
            param.setTimer_id(timer_id);
            sendMessage(createMessage(InboundMessage.stopTimer, JSONUtil.toJSONString(param)));
        } catch (Exception e) {
            Log.e(TAG, "stopTimer error:" + e);
        }
    }

    public void startUploadLogs(String strMetaData, String fileName, int count) {
        try {
            LogUploadReq req = new LogUploadReq();
            req.setLog_name(fileName);
            req.setMeta_data(strMetaData);
            req.setLog_count(count);
            sendMessage(createMessage(InboundMessage.startUploadLogs, JSONUtil.toJSONString(req)));
        } catch (Exception e) {
            Log.e(TAG, "startUploadLogs error:" + e);
        }
    }

    public void getUploadStatus(int tractionId, int fileType) {
        try {
            LogUploadGet logUploadGet = new LogUploadGet();
            logUploadGet.setUpload_trans_id(tractionId);
            logUploadGet.setLog_type(fileType);
            sendMessage(createMessage(InboundMessage.getUploadLogsStatus, JSONUtil.toJSONString(logUploadGet)));
        } catch (Exception e) {
            Log.e(TAG, "getUploadStatus error:" + e);
        }
    }

    public void cancelUploadLogs(int tractionId) {
        try {
            LogUploadCancel cancel = new LogUploadCancel();
            cancel.setUpload_trans_id(tractionId);
            sendMessage(createMessage(InboundMessage.cancelUploadLogs, JSONUtil.toJSONString(cancel)));
        } catch (Exception e) {
            Log.e(TAG, "cancelUploadLogs error:" + e);
        }
    }

    public void sendMessage(Message m) {
        try {
            if (m == null) {
                Log.e(TAG, "message is null");
            }
            msgr.send(m);
        } catch (Exception e) {
            Log.e(TAG, "send message error"+e);
        }
    }


}
