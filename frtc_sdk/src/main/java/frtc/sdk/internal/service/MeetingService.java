package frtc.sdk.internal.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Base64;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import frtc.sdk.internal.model.LayoutData;
import frtc.sdk.internal.model.FrtcConstant;
import frtc.sdk.FrtcCall;
import frtc.sdk.IFrtcCallListener;
import frtc.sdk.IFrtcCommonListener;
import frtc.sdk.IMeetingService;
import frtc.sdk.model.JoinMeetingOption;
import frtc.sdk.model.JoinMeetingParam;
import frtc.sdk.model.JoinMeetingQRCodeParam;
import frtc.sdk.model.LiveRecordStatusNotify;
import frtc.sdk.model.MeetingStateInfoNotify;
import frtc.sdk.model.OverlayNotify;
import frtc.sdk.model.MuteControlStateNotify;
import frtc.sdk.model.Participant;
import frtc.sdk.model.ParticipantNumberChangeNotify;
import frtc.sdk.model.ParticipantStateChangeNotify;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.conf.SDKConfigManager;
import frtc.sdk.internal.model.InboundMessage;
import frtc.sdk.internal.model.InternalCommonMessage;
import frtc.sdk.internal.model.InternalContentMessage;
import frtc.sdk.internal.model.InternalMeetingSessionMessage;
import frtc.sdk.internal.model.InternalMessageType;
import frtc.sdk.internal.model.InternalMicrophonePermission;
import frtc.sdk.internal.model.InternalNetworkInfo;
import frtc.sdk.internal.model.InternalParticipantInfoMessage;
import frtc.sdk.internal.model.InternalPeopleVideoMessage;
import frtc.sdk.internal.model.LogUploadNotify;
import frtc.sdk.internal.model.MeetingConfig;
import frtc.sdk.internal.model.MeetingContentState;
import frtc.sdk.internal.model.MeetingLiveState;
import frtc.sdk.internal.model.MeetingRecordState;
import frtc.sdk.internal.model.MeetingStatusInfo;
import frtc.sdk.internal.model.OverlayParam;
import frtc.sdk.internal.model.TemperatureThresholdType;
import frtc.sdk.internal.model.TimerEvent;
import frtc.sdk.internal.model.MeetingStatus;
import frtc.sdk.internal.model.MeetingContentStatus;
import frtc.sdk.internal.model.LecturerInfo;
import frtc.sdk.internal.model.JoinInfo;
import frtc.sdk.internal.model.MeetingInfoNotify;
import frtc.sdk.internal.model.UnmuteRequestNotify;
import frtc.sdk.internal.model.MeetingMediaStatsWrapper;
import frtc.sdk.internal.model.FrtcMeetingStatus;
import frtc.sdk.internal.model.MeetingErrorCode;
import frtc.sdk.internal.model.ParticipantStateInfo;
import frtc.sdk.internal.model.ParticipantInfo;
import frtc.sdk.internal.model.QRCodeResult;
import frtc.sdk.internal.model.LayoutInfoData;
import frtc.sdk.internal.model.UploadLogsStatus;
import frtc.sdk.log.Log;
import frtc.sdk.ui.model.MediaType;
import frtc.sdk.util.AESUtil;
import frtc.sdk.util.JSONUtil;
import frtc.sdk.util.StringUtils;
import frtc.sdk.ui.view.MeetingLayout;

public class MeetingService implements IMeetingService {

    private final String TAG = this.getClass().getSimpleName();
    private Context context = null;
    private Intent intent;
    private Handler handler;
    private Messenger messenger;
    private final List<IFrtcCallListener> meetingMessageListeners = new CopyOnWriteArrayList<>();
    private final List<IFrtcCommonListener> commonListeners = new CopyOnWriteArrayList<>();
    private List<ParticipantInfo> participantInfos = new CopyOnWriteArrayList<>();
    private ParticipantStateInfo participantState = new ParticipantStateInfo();
    private MeetingStatusInfo meetingStatusInfo = new MeetingStatusInfo();
    private MeetingConfig meetingConfig = new MeetingConfig();
    private MeetingContentState curContentState = MeetingContentState.idle;
    private boolean meetingConnected = false;
    private MeetingErrorCode meetingErrorCode = MeetingErrorCode.Unknown;
    private LayoutInfoData layoutInfoData;
    private MicrophoneService microphoneService;
    private static SystemPhoneReceiver systemPhoneReceiver = null;
    private MessengerManager messengerManager;
    private FrtcCall frtcCall = null;
    private String contentMsid = "";
    private int contentWidth = 0;
    private int contentHeight = 0;
    private Timer statsTimer = null;
    private MeetingLayout meetingLayoutView;
    private boolean isFirstCallConnected = true;
    private boolean isReconnecting = false;
    private int reconnectCount = 0;


    private void initIntent(Context context) {
        intent = new Intent();
        intent.setAction(FrtcService.action);
        intent.setPackage(context.getApplicationContext().getPackageName());
    }

    public MeetingService(FrtcCall frtcCall) {
        this.frtcCall = frtcCall;
        this.context = this.frtcCall.getContext();
        handler = new MessageHandler(new FrtcMessageHandler(this));
        messenger = new Messenger(handler);
        initIntent(this.context);
        this.context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE | Context.BIND_ABOVE_CLIENT);
    }

    public MeetingLayout getMeetingLayoutView() {
        return meetingLayoutView;
    }

    public void setMeetingLayoutView(MeetingLayout meetingLayoutView) {
        this.meetingLayoutView = meetingLayoutView;
    }

    public MeetingStatusInfo getMeetingStatusInfo() {
        return meetingStatusInfo;
    }

    public void setMeetingStatusInfo(MeetingStatusInfo meetingStatusInfo) {
        this.meetingStatusInfo = meetingStatusInfo;
    }


    public List<IFrtcCallListener> getMeetingMessageListeners() {
        return meetingMessageListeners;
    }

    public List<IFrtcCommonListener> getCommonListeners() {
        return commonListeners;
    }

    public List<ParticipantInfo> getParticipantInfos() {
        return participantInfos;
    }

    public void setParticipantInfos(List<ParticipantInfo> participantInfos) {
        this.participantInfos = participantInfos;
    }

    public ParticipantStateInfo getParticipantState() {
        return participantState;
    }

    public void setParticipantState(ParticipantStateInfo participantState) {
        this.participantState = participantState;
    }

    public MessengerManager getMessengerManager() {
        return messengerManager;
    }

    public void setMessengerManager(MessengerManager messengerManager) {
        this.messengerManager = messengerManager;
    }

    public MicrophoneService getMicrophoneService() {
        return microphoneService;
    }

    public void setMicrophoneService(MicrophoneService microphoneService) {
        this.microphoneService = microphoneService;
    }

    public MeetingConfig getMeetingConfig() {
        return meetingConfig;
    }

    public void setMeetingConfig(MeetingConfig meetingConfig) {
        this.meetingConfig = meetingConfig;
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if (messengerManager != null) {
                messengerManager.unregister(messenger);
                messengerManager = null;
            }
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            MeetingService.this.messengerManager = new MessengerManager(binder);
            MeetingService.this.messengerManager.register(messenger);
            MeetingService.this.messengerManager.fetchSDKVersion();
            meetingErrorCode = MeetingErrorCode.Unknown;
        }
    };

    @Override
    public String getSDKVersion() {
        return SDKConfigManager.getInstance(this.context).getSDKVersion();
    }

    @Override
    public void registerCommonListener(IFrtcCommonListener listener) {
        commonListeners.add(listener);
    }

    @Override
    public void unregisterCommonListener(IFrtcCommonListener listener) {
        commonListeners.remove(listener);
    }

    @Override
    public void registerMeetingListener(IFrtcCallListener listener) {
        meetingMessageListeners.add(listener);
    }

    @Override
    public void unregisterMeetingListener(IFrtcCallListener listener) {
        meetingMessageListeners.remove(listener);
    }

    public void onMeetingConnected(final MessengerManager manager) {
        this.meetingConnected = true;
        this.statsTimer = new Timer();
        TimerTask statsTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (manager != null) {
                    Message message = Message.obtain();
                    message.what = InboundMessage.mediaStatsReq.getCode();
                    manager.sendMessage(message);
                }
            }
        };
        this.statsTimer.schedule(statsTimerTask, 0, 5000);
    }

    public int getMeetingDisconnectCode() {
        if (MeetingErrorCode.Unknown != meetingErrorCode) {
            return meetingErrorCode.getCode();
        } else {
            return -1;
        }
    }

    public void onMeetingDisconnected(int errorCode) {
        this.meetingConnected = false;
        if (statsTimer != null) {
            statsTimer.cancel();
            statsTimer = null;
        }
        if (handler != null) {
            handler.removeMessages(InternalMessageType.getMediaStats.getCode());
        }
        meetingErrorCode = MeetingErrorCode.from(errorCode);
    }

    private void initMicrophoneService() {
        microphoneService = new MicrophoneService(context, this.meetingMessageListeners);
    }

    private void registerReceiver() {
        systemPhoneReceiver = new SystemPhoneReceiver(microphoneService);
        final IntentFilter phoneFilter = new IntentFilter();
        phoneFilter.addAction(SystemPhoneReceiver.PHONE_STATE);
        phoneFilter.addAction(SystemPhoneReceiver.NEW_OUTGOING_CALL);
        context.registerReceiver(systemPhoneReceiver, phoneFilter);
    }

    private void unregisterReceiver() {
        if (null != systemPhoneReceiver) {
            context.unregisterReceiver(systemPhoneReceiver);
            systemPhoneReceiver = null;
        }
    }

    public void stop() {
        handler = null;
        if (microphoneService != null) {
            microphoneService.stop();
            microphoneService = null;
        }
        context.unbindService(serviceConnection);
        unregisterReceiver();
    }

    public void closeMicrophoneAudio() {
        try {
            if (microphoneService != null) {
                microphoneService.stop();
                microphoneService = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "closeMicrophoneAudio failed:" + e.toString());
        } finally {
            unregisterReceiver();
        }
    }

    private void joinMeeting() {
        try {
            getMessengerManager().joinMeeting();
            getMeetingStatusInfo().setMeetingStatus(FrtcMeetingStatus.CONNECTING);
        } catch (Exception e) {
            Log.e(TAG, "joinMeeting failed:" + e.toString());
        }
    }

    public void loadCallSettings() {
        try {
            getMeetingLayoutView().setDisplayName(getMeetingStatusInfo().getDisplayName());
            if(getMeetingStatusInfo().isAudioOnly()) {
                getMeetingLayoutView().setLocalPeoplePreviewMute(true);
            } else {
                getMeetingLayoutView().setLocalPeoplePreviewMute(getMeetingConfig().isVideoMuted());
            }
            registerReceiver();
        } catch (Exception e) {
            Log.e(TAG, "loadCallSettings failed:" + e.toString());
        }
    }

    @Override
    public void initMeetingUI(MeetingLayout layout, ViewGroup surfaceView, ViewGroup pagingView, ImageView ivLeft, ImageView ivRight, RelativeLayout slideView) {
        try {
            setMeetingLayoutView(layout);
            IMeeting meeting = new IMeeting() {
                @Override
                public MeetingStatusInfo getMeetingInfo() {
                    return getMeetingStatusInfo();
                }
                @Override
                public MeetingConfig getMeetingConfig() {
                    return MeetingService.this.getMeetingConfig();
                }
            };
            layout.initialize(meeting, surfaceView, pagingView, ivLeft, ivRight, slideView, frtcCall);
            int lastMeetingErrorCode = getMeetingDisconnectCode();
            if(frtcCall.getMeetingStatus() != FrtcMeetingStatus.CONNECTED
                    && lastMeetingErrorCode != MeetingErrorCode.RemovedFromMeeting.getCode()
                    && lastMeetingErrorCode != MeetingErrorCode.MeetingStop.getCode()) {
                initMicrophoneService();
                joinMeeting();
            }
            loadCallSettings();
        } catch (Exception e) {
            Log.e(TAG, "initMeetingUI failed:" + e.toString());
        }
    }

    @Override
    public void setVisibleCallSlideView(boolean visible) {
        try {
            getMeetingLayoutView().setMeetingSlideViewVisible(visible);
        } catch (Exception e) {
            Log.e(TAG, "setVisibleCallSlideView failed:" + e.toString());
        }
    }

    @Override
    public void reconnectCall() {
        try {
            getMeetingStatusInfo().setMeetingStatus(FrtcMeetingStatus.CONNECTING);
            getMessengerManager().joinMeetingPrepare(new JoinInfo(getMeetingStatusInfo().getMeetingNumber(),
                    getMeetingStatusInfo().getServerAddress(),
                    getMeetingStatusInfo().getDisplayName(),
                    getMeetingStatusInfo().getUserToken(),
                    getMeetingStatusInfo().getMeetingPassword(),
                    getMeetingConfig().getCallRate(),
                    getMeetingConfig().getCallRate(),
                    getMeetingStatusInfo().getUserId()));
            joinMeeting();
        } catch (Exception e) {
            Log.e(TAG, "reconnectCall failed:" + e.toString());
        }
    }

    @Override
    public void joinMeetingWithParam(JoinMeetingParam joinMeetingParam, JoinMeetingOption joinMeetingOption) {
        if (messengerManager == null) {
            Log.e(TAG, "joinMeetingWithParam failed, service is not ready!");
        }
        String number = joinMeetingParam.getMeetingNumber();
        String displayName = joinMeetingParam.getDisplayName();
        String userToken = joinMeetingParam.getToken();
        String meetingPassword = joinMeetingParam.getMeetingPassword();
        String serverAddress = joinMeetingParam.getServerAddress();
        String preConfigServerAddress = SDKConfigManager.getInstance(context).getServerAddress();
        String userId = joinMeetingParam.getUserId();

        boolean muteAudio = joinMeetingOption.isNoAudio();
        boolean muteVideo = joinMeetingOption.isNoVideo();
        if (number == null || number.isEmpty()) {
            Log.i(TAG, "The number is empty.");
            return;
        }

        if (!TextUtils.isEmpty(serverAddress)) {
            userToken = "";
            meetingStatusInfo.setCrossServer(true);
            if (TextUtils.isEmpty(displayName)) {
                displayName = Build.PRODUCT + Build.VERSION.RELEASE;
            }
        }else{
            if(TextUtils.isEmpty(preConfigServerAddress)){
                return;
            }
            serverAddress = preConfigServerAddress;
            meetingStatusInfo.setCrossServer(false);
        }

        meetingConfig.setAudioMuted(muteAudio);
        meetingConfig.setVideoMuted(muteVideo);
        meetingConfig.setCallRate(joinMeetingOption.getCallRate());
        meetingConfig.setRemoteVideoMuted(false);
        meetingStatusInfo.setAudioOnly(joinMeetingOption.isAudioOnly());
        meetingStatusInfo.setMeetingStatus(FrtcMeetingStatus.CONNECTING);
        meetingStatusInfo.setMeetingNumber(number);
        meetingStatusInfo.setServerAddress(serverAddress);
        meetingStatusInfo.setDisplayName(displayName);
        meetingStatusInfo.setMeetingPassword(meetingPassword);
        meetingStatusInfo.setUserToken(userToken);
        meetingStatusInfo.setUserId(userId);
        meetingStatusInfo.setClientId(joinMeetingParam.getClientId());

        isFirstCallConnected = true;
        if(messengerManager != null){
            messengerManager.joinMeetingPrepare(new JoinInfo(number, serverAddress,  displayName, userToken, meetingPassword, joinMeetingOption.getCallRate(), joinMeetingOption.getCallRate(), userId));
            Log.d(TAG,"joinMeetingWithParam:"+serverAddress+","+userToken+","+displayName+","+number);
        }
        meetingErrorCode = MeetingErrorCode.Unknown;
    }

    @Override
    public boolean joinMeetingWithQRCode(JoinMeetingQRCodeParam joinMeetingQRCodeParam) {
        Log.d(TAG,"joinMeetingWithQRCode:"+ messengerManager);
        String qrcode = joinMeetingQRCodeParam.getQrcode();
        String displayName = joinMeetingQRCodeParam.getDisplayName();
        if (displayName == null || displayName.isEmpty()) {
            displayName = Build.PRODUCT + Build.VERSION.RELEASE;
        }
        Log.d(TAG,"joinMeetingWithQRCode:displayName="+displayName);
        String serverAddress = joinMeetingQRCodeParam.getServerAddress();
        String preConfigServerAddress = SDKConfigManager.getInstance(context).getServerAddress();
        if ((serverAddress == null || serverAddress.isEmpty())
                && preConfigServerAddress != null || !preConfigServerAddress.isEmpty()) {
            serverAddress = preConfigServerAddress;
        }
        FrtcMeetingStatus callState = meetingStatusInfo.getMeetingStatus();
        Log.i(TAG, "Current call state is " + callState);
        if ((callState != null) && callState.equals(FrtcMeetingStatus.CONNECTED)) {
            return false;
        }

        meetingConfig.setAudioMuted(true);
        meetingConfig.setVideoMuted(true);
        meetingConfig.setCallRate(0);
        meetingConfig.setRemoteVideoMuted(false);
        meetingStatusInfo.setAudioOnly(false);
        meetingStatusInfo.setMeetingStatus(FrtcMeetingStatus.CONNECTING);
        String jsonStr = AESUtil.decrypt(qrcode);
        String pwd = "";
        QRCodeResult obj = null;
        try{
            obj = JSONUtil.transform(jsonStr, QRCodeResult.class);
            if (obj == null) {
                byte[] bUrl = Base64.decode(qrcode, Base64.DEFAULT);
                Log.i(TAG,"joinMeetingWithQRCode bUrl = "+new String(bUrl));
                obj = JSONUtil.transform(new String(bUrl), QRCodeResult.class);
                if(obj == null) {
                    return false;
                }
                displayName = obj.getUsername();
                pwd = obj.getMeeting_passwd();
            }
        }catch(Exception e){
            Log.e(TAG,"toObject QRCodeResult failed : "+e.toString());
        }
        String qrcodeServerAddress = obj.getServer_address();
        String userToken = joinMeetingQRCodeParam.getToken();
        if (!serverAddress.equals(qrcodeServerAddress)) {
            userToken = "";
            meetingStatusInfo.setCrossServer(true);
        }else{
            meetingStatusInfo.setCrossServer(false);
        }
        if (qrcodeServerAddress == null || qrcodeServerAddress.isEmpty()) {
            Log.i(TAG,"joinMeetingWithQRCode qrcodeServerAddress = "+qrcodeServerAddress);
            return false;
        }
        String meetingNumber = obj.getMeeting_number();

        meetingStatusInfo.setMeetingNumber(meetingNumber);
        meetingStatusInfo.setServerAddress(qrcodeServerAddress);
        meetingStatusInfo.setDisplayName(displayName);
        meetingStatusInfo.setMeetingPassword(pwd);
        meetingStatusInfo.setUserToken(userToken);
        meetingStatusInfo.setClientId(joinMeetingQRCodeParam.getClientId());
        isFirstCallConnected = true;

        Log.d(TAG, "joinMeetingWithQRCode service =" + messengerManager);
        int count =0;
        try{
            while(messengerManager == null){
                Log.i(TAG, "bindService count = "+count);
                context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE | Context.BIND_ABOVE_CLIENT);

                if(count == 3){
                    meetingStatusInfo.setMeetingStatus(FrtcMeetingStatus.DISCONNECTED);
                    return false;
                }
                count ++;
                Thread.sleep(500);
            }
        }catch(Exception e){
            Log.e(TAG,"joinMeetingWithQRCode bindService failed:"+e.toString());
        }
        Log.d(TAG, "joinMeetingWithQRCode after while service =" + messengerManager);
        messengerManager.joinMeetingPrepare(new JoinInfo(meetingNumber, qrcodeServerAddress,  displayName, userToken, pwd, 0, 0, ""));
        Log.d(TAG,"joinMeetingWithQRCode:"+qrcodeServerAddress+","+userToken+","+displayName+","+meetingNumber);
        meetingErrorCode = MeetingErrorCode.Unknown;
        return true;
    }

    @Override
    public void leaveMeeting() {
        try {
            getMeetingStatusInfo().setMeetingStatus(FrtcMeetingStatus.DISCONNECTING);
            getMessengerManager().leaveMeeting();
            if (handler != null) {
                handler.removeMessages(InternalMessageType.getMediaStats.getCode());
            }
            getMeetingLayoutView().layout_finish();
        } catch (Exception e) {
            Log.e(TAG, "leaveMeeting failed:" + e.toString());
        }
    }

    @Override
    public void setUnMuteAudio() {
        try {
            getMeetingConfig().setAudioMuted(false);
            getMicrophoneService().setMicrophoneMuted(false);
            getMessengerManager().setAudioMute(false);
            getMessengerManager().setAudioVideoControl(false, meetingConfig.isVideoMuted());
        } catch (Exception e) {
            Log.e(TAG, "setUnMuteAudio failed:" + e.toString());
        }
    }

    @Override
    public void setMeetingPassword(String password) {
        try {
            getMeetingStatusInfo().setMeetingPassword(password);
            getMessengerManager().inputPassword(password);
        } catch (Exception e) {
            Log.e(TAG, "setMeetingPassword failed:" + e.toString());
        }
    }

    @Override
    public void setVisibleOverlay(boolean visible) {
        try {
            getMeetingConfig().setOverlayVisible(visible);
            if (visible) {
                getMeetingLayoutView().setNameVisible();
            } else {
                getMeetingLayoutView().setNameInvisible();
            }
        } catch (Exception e) {
            Log.e(TAG, "setVisibleOverlay failed:" + e.toString());
        }
    }

    @Override
    public void setHeadsetEnableSpeaker(boolean enableSpeaker) {
        try {
            getMeetingConfig().setHeadsetEnableSpeaker(enableSpeaker);
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onHeadsetEnableSpeakerNotify(enableSpeaker);
            }
        } catch (Exception e) {
            Log.e(TAG, "setHeadsetEnableSpeaker failed:" + e.toString());
        }
    }

    @Override
    public void screenOrientationChanged(int rotation) {
        try {
            getMeetingLayoutView().setLocalCameraRotation(rotation);
        } catch (Exception e) {
            Log.e(TAG, "screenOrientationChanged failed:" + e.toString());
        }
    }

    @Override
    public void contentOrientationChanged(int rotation) {
        try {
            getMeetingLayoutView().setContentRotation(rotation);
        } catch (Exception e) {
            Log.e(TAG, "contentOrientationChanged failed:" + e.toString());
        }
    }

    @Override
    public void resume(boolean muteVideo, boolean muteRemoteVideo) {
        try {
            getMeetingLayoutView().layout_resume(muteVideo);
            muteLocalVideo(muteVideo);
            muteRemoteVideo(muteRemoteVideo);
        } catch (Exception e) {
            Log.e(TAG, "resume failed:" + e.toString());
        }
    }

    @Override
    public void resumeAudio(boolean muteAudio) {
        muteLocalAudio(muteAudio);
    }

    private void resumeForReconnect(){
        try {
            getMeetingLayoutView().layout_resume(getMeetingConfig().isVideoMuted());
            muteLocalAudio(getMeetingConfig().isAudioMuted());
            muteLocalVideo(getMeetingConfig().isVideoMuted());
            muteRemoteVideo(getMeetingConfig().isRemoteVideoMuted());
        } catch (Exception e) {
            Log.e(TAG, "resumeForReconnect failed:" + e.toString());
        }
    }

    @Override
    public void stop(boolean isShowCallFloatView) {
        try {
            getMeetingLayoutView().layout_pause();
            muteLocalVideo(true);
            muteRemoteVideo(true);
        } catch (Exception e) {
            Log.e(TAG, "stop failed:" + e.toString());
        }
    }
    @Override
    public void switchSpeaker() {
        try {
            getMicrophoneService().switchAudioSpeakerDevice();
        } catch (Exception e) {
            Log.e(TAG, "switchSpeaker failed:" + e.toString());
        }
    }

    @Override
    public void switchFrontOrBackCamera() {
        try {
            getMeetingLayoutView().switchCamera();
        } catch (Exception e) {
            Log.e(TAG, "switchFrontOrBackCamera failed:" + e.toString());
        }
    }

    @Override
    public void setNoiseBlock(boolean enabled) {
        try {
            getMeetingConfig().setNoiseBlock(enabled);
            getMessengerManager().setNoiseBlock(enabled);
        } catch (Exception e) {
            Log.e(TAG, "setNoiseBlock failed:" + e.toString());
        }
    }

    @Override
    public void startSendContent(){
        try {
            getMessengerManager().startSendContent();
        } catch (Exception e) {
            Log.e(TAG, "startSendContent failed:" + e.toString());
        }
    }

    @Override
    public void stopSendContent() {
        try {
            getMessengerManager().stopSendContent();
            getMeetingLayoutView().stopSendContent();
        } catch (Exception e) {
            Log.e(TAG, "stopSendContent failed:" + e.toString());
        }
    }

    @Override
    public String getMeetingServerAddress() {
        try {
            return getMeetingStatusInfo().getServerAddress();
        } catch (Exception e) {
            Log.e(TAG, "getMeetingServerAddress failed:" + e.toString());
        }
        return null;
    }

    @Override
    public void startMeetingReminderTimer(int timerID, int duration, boolean periodic) {
        try {
            getMessengerManager().startTimer(timerID, duration, periodic);
        } catch (Exception e) {
            Log.e(TAG, "startMeetingReminderTimer failed:" + e.toString());
        }
    }

    @Override
    public void stopTimer(int timerID) {
        try {
            getMessengerManager().stopTimer(timerID);
        } catch (Exception e) {
            Log.e(TAG, "stopTimer failed:" + e.toString());
        }
    }

    @Override
    public void muteLocalAudio(boolean isMuted) {
        try {
            getMeetingConfig().setAudioMuted(isMuted);
            getMicrophoneService().setMicrophoneMuted(isMuted);
            getMessengerManager().setAudioMute(isMuted);
            getMessengerManager().setAudioVideoControl(isMuted, getMeetingConfig().isVideoMuted());
        } catch (Exception e) {
            Log.e(TAG, "muteLocalAudio failed:" + e.toString());
        }
    }


    @Override
    public void muteLocalVideo(boolean isMuted) {
        try {
            getMeetingConfig().setVideoMuted(isMuted);
            if (isMuted) {
                getMeetingLayoutView().stopLocalCameraPreview();
            } else {
                getMeetingLayoutView().startLocalCameraPreview();
            }
            getMeetingLayoutView().setLocalCameraPreviewMute(isMuted);
            getMessengerManager().setVideoMute(isMuted);
            getMeetingLayoutView().setLocalPeoplePreviewMute(isMuted);
        } catch (Exception e) {
            Log.e(TAG, "muteLocalVideo failed:" + e.toString());
        }
    }

    @Override
    public void muteRemoteVideo(boolean isMuted) {
        try {
            getMeetingConfig().setRemoteVideoMuted(isMuted);
            getMessengerManager().setRemoteVideoMute(isMuted);
        } catch (Exception e) {
            Log.e(TAG, "muteRemoteVideo failed:" + e.toString());
        }
    }

    @Override
    public void setLocalPeopleViewEnable(boolean enabled) {
        try {
            getMeetingLayoutView().setLocalPeopleView(enabled);
        } catch (Exception e) {
            Log.e(TAG, " failed:" + e.toString());
        }
    }

    @Override
    public FrtcMeetingStatus getMeetingStatus() {
        try {
            return getMeetingStatusInfo().getMeetingStatus();
        } catch (Exception e) {
            Log.e(TAG, "getMeetingStatus failed:" + e.toString());
        }
        return null;
    }

    @Override
    public void startUploadLogs(String strMetaData, String fileName, int count) {
        try {
            getMessengerManager().startUploadLogs(strMetaData, fileName, count);
        } catch (Exception e) {
            Log.e(TAG, "startUploadLogs failed:" + e.toString());
        }
    }

    @Override
    public void getUploadStatus(int tractionId, int fileType) {
        try {
            getMessengerManager().getUploadStatus(tractionId, fileType);
        } catch (Exception e) {
            Log.e(TAG, "getUploadStatus failed:" + e.toString());
        }
    }

    @Override
    public void cancelUploadLogs(int tractionId) {
        try {
            getMessengerManager().cancelUploadLogs(tractionId);
        } catch (Exception e) {
            Log.e(TAG, "cancelUploadLogs failed:" + e.toString());
        }
    }

    @Override
    public void setDisplayName(String name) {
        try {
            getMeetingStatusInfo().setDisplayName(name);
        } catch (Exception e) {
            Log.e(TAG, "setDisplayName failed:" + e.toString());
        }
    }

    @Override
    public void saveMeetingIntoMeetingListNotify() {
        try {
            for(IFrtcCommonListener listener : getCommonListeners()){
                listener.onSaveMeetingIntoMeetingListNotify();
            }
        } catch (Exception e) {
            Log.e(TAG, "saveMeetingIntoMeetingListNotify failed:" + e.toString());
        }
    }

    @Override
    public void showMeetingReminder(ArrayList<ScheduledMeeting> scheduledMeetingsReminders) {
        try {
            for(IFrtcCommonListener listener : getCommonListeners()){
                listener.onShowMeetingReminder(scheduledMeetingsReminders);
            }
        } catch (Exception e) {
            Log.e(TAG, "showMeetingReminder failed:" + e.toString());
        }
    }

    @Override
    public boolean resumeCall() {
        int lastMeetingErrorCode = getMeetingDisconnectCode();
        if(lastMeetingErrorCode == MeetingErrorCode.RemovedFromMeeting.getCode()
                || lastMeetingErrorCode == MeetingErrorCode.MeetingStop.getCode()){
            broadcastMeetingStateNotify();
            return false;
        }
        onParticipantCountNotify(null, true);
        onParticipantStateNotify(null, true);
        resumeLayout();
        if(curContentState == MeetingContentState.contentSending && meetingLayoutView != null){
            meetingLayoutView.setResolution();
            meetingLayoutView.showLocalContentModule(true);
        }
        if (!TextUtils.isEmpty(contentMsid)) {
            meetingLayoutView.setResolution();
            meetingLayoutView.onAddPeopleVideo(contentMsid, contentWidth, contentHeight);
        }
        return true;
    }

    @Override
    public void closeMeetingLayout() {
        try {
            getMeetingLayoutView().destroyLayout();
        } catch (Exception e) {
            Log.e(TAG, "callLayoutFinish failed:" + e.toString());
        }
    }

    @Override
    public boolean isAudioMuted() {
        return getMeetingConfig().isAudioMuted();
    }

    @Override
    public int getDisconnectErrorCode() {
        return getMeetingDisconnectCode();
    }

    @Override
    public boolean isAudioDisabled() {
        return getMeetingConfig().isAudioDisabled();
    }

    @Override
    public void setAudioDisabled(boolean audioDisabled) {
        getMeetingConfig().setAudioDisabled(audioDisabled);
    }

    @Override
    public boolean isLiveStarted() {
        return getMeetingConfig().isLiveStarted();
    }

    @Override
    public boolean isRecordingStarted() {
        return getMeetingConfig().isRecordingStarted();
    }

    @Override
    public String getLiveMeetingUrl() {
        return getMeetingConfig().getLiveMeetingUrl();
    }

    @Override
    public String getLiveMeetingPwd() {
        return getMeetingConfig().getLivePassword();
    }

    @Override
    public boolean isHeadsetEnableSpeaker() {
        return getMeetingConfig().isHeadsetEnableSpeaker();
    }

    @Override
    public int getCameraId() {
        return getMeetingConfig().getCameraId();
    }

    @Override
    public void setCameraId(int cameraId) {
        getMeetingConfig().setCameraId(cameraId);
    }

    @Override
    public boolean isRemoteVideoMuted() {
        return getMeetingConfig().isRemoteVideoMuted();
    }

    @Override
    public void requestParticipants(){
        Log.d(TAG,"requestParticipants:"+getParticipantInfos());
        broadcastParticipantStateNotify(getParticipantInfos(), true, "");
        getMeetingLayoutView().setMuteState(getParticipantInfos());
    }

    public void onCancelUploadLogsNotify() {
        try {
            for(IFrtcCommonListener listener : getCommonListeners()){
                listener.onCancelUploadLogsNotify();
            }
        } catch (Exception e) {
            Log.e(TAG, "onCancelUploadLogsNotify error:" + e);
        }
    }

    public void onGetUploadLogsStatusNotify(Message msg) {
        try {
            String jsonString = msg.getData().getString(FrtcConstant.DATA);
            UploadLogsStatus status = JSONUtil.transform(jsonString, UploadLogsStatus.class);
            for(IFrtcCommonListener listener : getCommonListeners()){
                listener.onUploadLogsStatusNotify(status);
            }
        } catch (Exception e) {
            Log.e(TAG, "onGetUploadLogsStatusNotify error:" + e);
        }
    }

    public void onStartUploadLogsResultNotify(Message msg) {
        try {
            String jsonString = msg.getData().getString(FrtcConstant.DATA);
            LogUploadNotify response = JSONUtil.transform(jsonString, LogUploadNotify.class);
            Log.i(TAG, "onStartUploadLogsResultNotify tractionId :" + response.getUpload_trans_id());
            for(IFrtcCommonListener listener : getCommonListeners()){
                listener.onUploadLogsNotify(response.getUpload_trans_id());
            }
        } catch (Exception e) {
            Log.e(TAG, "onStartUploadLogsResultNotify error:" + e);
        }
    }

    public void onTimerEventNotify(Message msg) {
        try {
            TimerEvent timerEvent = JSONUtil.transform(msg.getData().getString(FrtcConstant.DATA), TimerEvent.class);
            for(IFrtcCommonListener listener : getCommonListeners()){
                listener.onTimerEvent(timerEvent.getTimer_id());
            }
        } catch (Exception e) {
            Log.e(TAG, "onTimerEventNotify failed:" + e);
        }
    }

    public void onAllowUnmuteNotify() {
        try {
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onAllowUnmuteNotify();
            }
        } catch (Exception e) {
            Log.e(TAG, "onAllowUnmuteNotify error:" + e);
        }
    }

    public void onUnmuteReqNotify(Message msg) {
        try {
            String string = msg.getData().getString(FrtcConstant.DATA);
            UnmuteRequestNotify requestNotify = JSONUtil.transform(string, UnmuteRequestNotify.class);
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onUnmuteRequestNotify(requestNotify.getUnmute_request().get(0));
            }
        } catch (Exception e) {
            Log.e(TAG, "onUnmuteReqNotify error:" + e);
        }
    }

    public void onLayoutSettingNotify(Message msg) {
        try {
            String string = msg.getData().getString(FrtcConstant.DATA);
            LecturerInfo lecturerInfo = JSONUtil.transform(string, LecturerInfo.class);
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onLayoutSettingNotify(lecturerInfo);
            }
        } catch (Exception e) {
            Log.e(TAG, "onLayoutSettingNotify error:" + e);
        }
    }

    public void onGetRTCVersionResultNotify(Message msg) {
        try {
            String version = msg.getData().getString(FrtcConstant.DATA);
            if (StringUtils.isNotBlank(version)) {
                Log.i(TAG, "sdk version:" + version);
                SDKConfigManager.getInstance(this.context).setSDKVersion(version);
            }
        } catch (Exception e) {
            Log.e(TAG, "onGetRTCVersionResultNotify error:" + e);
        }
    }

    public void onMeetingPasswordErrorNotify(){
        try {
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onMeetingPasswordErrorNotify();
            }
        } catch (Exception e) {
            Log.e(TAG, "onMeetingPasswordErrorNotify error:" + e);
        }
    }

    public void onMeetingPasswordRequireNotify() {
        try {
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onMeetingPasswordRequestNotify();
            }
        } catch (Exception e) {
            Log.e(TAG, "onMeetingPasswordRequireNotify error:" + e);
        }
    }

    public void onMeetingJoinInfo(Message msg) {
        try {
            String data = msg.getData().getString(FrtcConstant.DATA);
            MeetingInfoNotify meetingInfoNotify = JSONUtil.transform(data, MeetingInfoNotify.class);
            if (meetingInfoNotify != null) {
                getMeetingStatusInfo().setMeetingNumber(meetingInfoNotify.getMeeting_alias());
                getMeetingStatusInfo().setMeetingName(meetingInfoNotify.getMeeting_name());
                getMeetingStatusInfo().setDisplayName(meetingInfoNotify.getDisplay_name());
                getMeetingStatusInfo().setOwnerId(meetingInfoNotify.getOwner_id());
                getMeetingStatusInfo().setOwnerName(meetingInfoNotify.getOwner_name());
                String meetingUrl = meetingInfoNotify.getGroup_meeting_url();
                if(TextUtils.isEmpty(meetingUrl)){
                    meetingUrl = meetingInfoNotify.getMeeting_url();
                }
                getMeetingStatusInfo().setMeetingURL(meetingUrl);
                getMeetingStatusInfo().setScheduleStartTime(meetingInfoNotify.getStart_time());
                getMeetingStatusInfo().setScheduleEndTime(meetingInfoNotify.getEnd_time());
                MeetingStateInfoNotify notify = new MeetingStateInfoNotify();
                notify.setMeetingNumber(getMeetingStatusInfo().getMeetingNumber());
                notify.setMeetingName(getMeetingStatusInfo().getMeetingName());
                notify.setAudioOnly(getMeetingStatusInfo().isAudioOnly());
                notify.setOwnerId(getMeetingStatusInfo().getOwnerId());
                notify.setOwnerName(getMeetingStatusInfo().getOwnerName());
                notify.setMeetingURL(getMeetingStatusInfo().getMeetingURL());
                notify.setScheduleStartTime(getMeetingStatusInfo().getScheduleStartTime());
                notify.setScheduleEndTime(getMeetingStatusInfo().getScheduleEndTime());
                notify.setCrossServer(getMeetingStatusInfo().isCrossServer());
                notify.setCrossSeverAddr(getMeetingStatusInfo().getServerAddress());
                for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                    listener.onMeetingInfoNotify(notify);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onMeetingJoinInfo error:" + e);
        }
    }

    public void onTemperatureInfoNotify(Message msg) {
        try {
            boolean exceedThreshold = msg.getData().getBoolean(TemperatureThresholdType.EXCEED_THRESHOLD);
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onTemperatureInfoNotify(exceedThreshold);
            }
        } catch (Exception e) {
            Log.e(TAG, "broadcastTemperatureInfoNotify error:" + e);
        }
    }

    public void onRequestPeopleVideoNotify(Message msg) {
        try {
            String jsonString = msg.getData().getString(FrtcConstant.DATA);
            InternalPeopleVideoMessage internalPeopleVideoMessage = JSONUtil.transform(jsonString, InternalPeopleVideoMessage.class);
            String msid = internalPeopleVideoMessage.getMsid();
            getMeetingLayoutView().onRequestPeopleVideo(msid,
                    internalPeopleVideoMessage.getWidth(),
                    internalPeopleVideoMessage.getHeight());
            if (!getMeetingLayoutView().isContentMediaData(msid) || getMeetingStatusInfo().isCameraOpenFailed()) {
                getMessengerManager().setVideoMute(getMeetingConfig().isVideoMuted());
                getMeetingLayoutView().setLocalPeoplePreviewMute(getMeetingConfig().isVideoMuted());
            }
        } catch (Exception e) {
            Log.e(TAG, "onRequestPeopleVideoNotify failed:" + e);
        }
    }

    public void onAddPeopleVideoNotify(Message msg) {
        try {
            String jsonString = msg.getData().getString(FrtcConstant.DATA);
            InternalPeopleVideoMessage internalPeopleVideoMessage = JSONUtil.transform(jsonString, InternalPeopleVideoMessage.class);
            String msid = internalPeopleVideoMessage.getMsid();
            if (msid.startsWith(MediaType.REMOTE_CONTENT.getValue())) {
                this.contentMsid = msid;
                this.contentWidth = internalPeopleVideoMessage.getWidth();
                this.contentHeight = internalPeopleVideoMessage.getHeight();
            }
            getMeetingLayoutView().onAddPeopleVideo(msid, internalPeopleVideoMessage.getWidth(), internalPeopleVideoMessage.getHeight());
        } catch (Exception e) {
            Log.e(TAG, "onAddPeopleVideoNotify failed:" + e);
        }
    }

    public void onStopPeopleVideoNotify(Message msg) {
        try {
            String jsonString = msg.getData().getString(FrtcConstant.DATA);
            InternalCommonMessage internalCommonMessage = JSONUtil.transform(jsonString, InternalCommonMessage.class);
            getMeetingLayoutView().onStopPeopleVideo(internalCommonMessage.getMsid());
        } catch (Exception e) {
            Log.e(TAG, "onStopPeopleVideoNotify failed:" + e);
        }
    }

    public void onDeletePeopleVideoNotify(Message msg) {
        try {
            String jsonString = msg.getData().getString(FrtcConstant.DATA);
            InternalCommonMessage internalCommonMessage = JSONUtil.transform(jsonString, InternalCommonMessage.class);
            String msid = internalCommonMessage.getMsid();
            if (StringUtils.isNotBlank(msid) && msid.startsWith(MediaType.REMOTE_CONTENT.getValue())) {
                contentMsid = "";
                contentWidth = 0;
                contentHeight = 0;
            }
            getMeetingLayoutView().onDeletePeopleVideo(msid);
        } catch (Exception e) {
            Log.e(TAG, "onDeletePeopleVideoNotify failed:" + e);
        }
    }

    public void onParticipantStateNotify(Message msg, boolean isResumePartiState) {
        if(msg != null && !isResumePartiState) {
            try {
                String jsonString = msg.getData().getString(FrtcConstant.DATA);
                setParticipantState(JSONUtil.transform(jsonString, ParticipantStateInfo.class));
            } catch (Exception e) {
                Log.e(TAG, "onParticipantStateNotify getMsg error:" + e);
            }
        }
        if (getParticipantState() == null) {
            return;
        }
        try {
            if (getParticipantState().getFull_list()) {
                if(!isResumePartiState) {
                    getParticipantState().addLocal2ParticipantList(getMeetingStatusInfo().getDisplayName(),
                            getMeetingStatusInfo().getClientId(),
                            getMeetingStatusInfo().getUserId());
                }
                getParticipantInfos().clear();
                getParticipantInfos().addAll(getParticipantState().getParti_status_change());
                broadcastParticipantStateNotify(getParticipantInfos(), true, "");
            } else {
                String selfNewName = "";
                if(getParticipantState().getParti_status_change() != null){
                    for (ParticipantInfo info : getParticipantState().getParti_status_change()) {
                        for (ParticipantInfo participantInfo : getParticipantInfos()) {
                            if (StringUtils.isNotBlank(info.getUuid())
                                    && info.getUuid().equals(participantInfo.getUuid())) {
                                if (info.getUuid().equals(getMeetingStatusInfo().getClientId())) {
                                    if(!getMeetingStatusInfo().getDisplayName().equals(info.getDisplay_name())) {
                                        getMeetingStatusInfo().setDisplayName(info.getDisplay_name());
                                        selfNewName = info.getDisplay_name();
                                    }
                                }
                                participantInfo.setAudioMuted(info.getAudioMuted());
                                participantInfo.setVideoMuted(info.getVideoMuted());
                                participantInfo.setDisplay_name(info.getDisplay_name());
                            }
                        }
                    }
                    broadcastParticipantStateNotify(getParticipantInfos(), false, selfNewName);
                }
            }
            getMeetingLayoutView().setMuteState(getParticipantInfos());
        } catch (Exception e) {
            Log.e(TAG, "onParticipantStateNotify update error:" + e);
        }
    }

    public void onMicrophonePermissionNotify(Message msg) {
        try {
            String jsonString = msg.getData().getString(FrtcConstant.DATA);
            InternalMicrophonePermission permission = JSONUtil.transform(jsonString, InternalMicrophonePermission.class);
            getMeetingConfig().setAudioDisabled(!permission.isAllow_self_unmute());
            if (permission.isAllow_self_unmute()) {
                if (permission.isAudio_mute()) {
                    getMeetingConfig().setAudioMuted(true);
                    getMicrophoneService().setMicrophoneMuted(true);
                    getMessengerManager().setAudioMute(true);
                    broadcastMuteStateNotify(true, false);
                    getMessengerManager().setAudioVideoControl(true, getMeetingConfig().isVideoMuted());
                } else {
                    broadcastMuteStateNotify(false, false);
                }
            } else {
                if (permission.isAudio_mute()) {
                    broadcastMuteStateNotify(true, true);
                    getMessengerManager().setAudioVideoControl(true, getMeetingConfig().isVideoMuted());
                    getMeetingConfig().setAudioMuted(true);
                    getMicrophoneService().setMicrophoneMuted(true);
                    getMessengerManager().setAudioMute(true);
                } else {
                    broadcastMuteStateNotify(false, true);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onMicrophonePermissionNotify error:" + e.getMessage());
        }
    }


    public void onRequestAudioNotify(Message msg) {
        try {
            InternalCommonMessage internalCommonMessage = JSONUtil.transform(msg.getData().getString(FrtcConstant.DATA), InternalCommonMessage.class);
            getMicrophoneService().onRequestedAudio(internalCommonMessage.getMsid(), getMeetingMessageListeners());
            getMicrophoneService().setMicrophoneMuted(getMeetingConfig().isAudioMuted());
            getMessengerManager().setAudioMute(getMeetingConfig().isAudioMuted());
        } catch (Exception e) {
            Log.e(TAG, "onRequestAudioNotify failed:" + e);
        }
    }

    public void onAddAudioNotify(Message msg) {
        try {
            InternalCommonMessage internalCommonMessage = JSONUtil.transform(msg.getData().getString(FrtcConstant.DATA), InternalCommonMessage.class);
            getMicrophoneService().onReceivedAudio(internalCommonMessage.getMsid());
        } catch (Exception e) {
            Log.e(TAG, "onAddAudioNotify error:" + e);
        }
    }

    public void onAudioStopNotify() {
        try {
            getMicrophoneService().onReleasedAudio();
        } catch (Exception e) {
            Log.e(TAG, "onAudioStopNotify error:" + e);
        }
    }

    private void broadcastMuteStateNotify(Boolean isAudioMuted, boolean isMicDisabled) {
        try {
            MuteControlStateNotify notify = new MuteControlStateNotify();
            notify.setAudioMute(isAudioMuted);
            notify.setMicDisabled(isMicDisabled);
            for (IFrtcCallListener listener : meetingMessageListeners) {
                listener.onMuteControlStateNotify(notify);
            }
        } catch (Exception e) {
            Log.e(TAG, "broadcastMuteStateNotify error:" + e);
        }
    }

    private void broadcastContentState(boolean isSharingContent) {
        try {
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onContentStateNotify(isSharingContent);
            }
        } catch (Exception e) {
            Log.e(TAG, "broadcastContentState error:" + e);
        }
    }

    private void resumeLayout() {
        try {
            if (layoutInfoData != null) {
                getMeetingLayoutView().onLayoutInfoNotify(layoutInfoData);
                String pinUuid = layoutInfoData.getPinUuId();
                for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                    listener.onPinNotify(pinUuid);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "resumeLayout error:" + e);
        }
    }

    private LayoutInfoData cloneLayout(LayoutInfoData d) {
        LayoutInfoData l = new LayoutInfoData();
        l.setLayout_mode(d.getLayout_mode());
        l.setActive_speaker_msid(d.getActive_speaker_msid());
        l.setPinUuid(d.getPinUuId());
        l.setTotal_count(d.getTotal_count());
        if (d.getLayout_content() != null) {
            LayoutData content_layout = new LayoutData();
            l.setLayout_content(content_layout);
            content_layout.setMsid(d.getLayout_content().getMsid());
            content_layout.setBit_rate(d.getLayout_content().getBit_rate());
            content_layout.setDisplay_name(d.getLayout_content().getDisplay_name());
            content_layout.setFrame_rate(d.getLayout_content().getFrame_rate());
            content_layout.setHeight(d.getLayout_content().getHeight());
            content_layout.setWidth(d.getLayout_content().getWidth());
            content_layout.setSurface_idx(d.getLayout_content().getSurface_idx());
            content_layout.setUuid(d.getLayout_content().getUuid());
        }
        if (d.getLayout() != null) {
            List<LayoutData> layouts = new ArrayList<>();
            l.setLayout(layouts);
            if (!d.getLayout().isEmpty()) {
                for (LayoutData info : d.getLayout()) {
                    LayoutData newInfo = new LayoutData();
                    newInfo.setUuid(info.getUuid());
                    newInfo.setMsid(info.getMsid());
                    newInfo.setSurface_idx(info.getSurface_idx());
                    newInfo.setFrame_rate(info.getFrame_rate());
                    newInfo.setDisplay_name(info.getDisplay_name());
                    newInfo.setBit_rate(info.getBit_rate());
                    newInfo.setHeight(info.getHeight());
                    newInfo.setWidth(info.getWidth());
                    layouts.add(newInfo);
                }
            }
        }
        return l;
    }
    public void onLayoutInfoNotify(Message msg) {
        try {
            String string = msg.getData().getString(FrtcConstant.DATA);
            LayoutInfoData l = JSONUtil.transform(string, LayoutInfoData.class);
            if (l != null) {
                this.layoutInfoData = cloneLayout(l);
                getMeetingLayoutView().onLayoutInfoNotify(this.layoutInfoData);
                String pinUuid = this.layoutInfoData.getPinUuId();
                for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                    listener.onPinNotify(pinUuid);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onLayoutInfoNotify error:" + e);
        }
    }

    public void onContentStatusNotify(Message msg) {
        try {
            String data = msg.getData().getString(FrtcConstant.DATA);
            MeetingContentStatus status = JSONUtil.transform(data, MeetingContentStatus.class);
            String contentState = status.getContent_status();
            if (MeetingContentState.contentReceiving.getName().equals(contentState)) {
                curContentState = MeetingContentState.contentReceiving;
            } else if (MeetingContentState.idle.getName().equals(contentState)) {
                broadcastContentState(false);
                if (MeetingContentState.contentSending.equals(curContentState)) {
                    getMeetingLayoutView().stopSendContent();
                }
                curContentState = MeetingContentState.idle;
            } else if (MeetingContentState.contentSending.getName().equals(contentState)) {
                curContentState = MeetingContentState.contentSending;
                broadcastContentState(true);
                getMeetingLayoutView().showLocalContentModule(true);
            }
        } catch (Exception e) {
            Log.e(TAG, "onContentStatusNotify error:" + e);
        }
    }

    public void onStartContentFailNotify() {
        try {
            getMeetingLayoutView().onContentSendRefused();
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onStartContentRefusedNotify();
            }
        } catch (Exception e) {
            Log.e(TAG, "onStartContentFailNotify error:" + e);
        }
    }

    public void onGetMediaStatsInfoResultNotify(Message msg) {
        try {
            String data = msg.getData().getString(FrtcConstant.DATA);
            MeetingMediaStatsWrapper stat = JSONUtil.transform(data, MeetingMediaStatsWrapper.class);
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onMediaStatsInfoNotify(stat, true);
            }
        } catch (Exception e) {
            Log.e(TAG, "onGetMediaStatsInfoResultNotify error:" + e);
        }
    }

    private void broadcastMeetingStateNotify() {
        try {
            int lastMeetingErrorCode = getMeetingDisconnectCode();
            MeetingStateInfoNotify notify = new MeetingStateInfoNotify();
            notify.setMeetingNumber(meetingStatusInfo.getMeetingNumber());
            notify.setMeetingStatus(meetingStatusInfo.getMeetingStatus());
            notify.setCallEndReason(lastMeetingErrorCode);
            notify.setReconnectCount(reconnectCount);
            notify.setReconnecting(isReconnecting);
            if (lastMeetingErrorCode == MeetingErrorCode.RemovedFromMeeting.getCode()
                    || lastMeetingErrorCode == MeetingErrorCode.MeetingStop.getCode()) {
                curContentState = MeetingContentState.idle;
            }
            for (IFrtcCallListener listener : meetingMessageListeners) {
                Log.d(TAG, "broadcastMeetingStateNotify listener = " + listener);
                listener.onMeetingStateNotify(notify);
            }
        } catch (Exception e) {
            Log.e(TAG, "broadcastMeetingStateNotify error:" + e);
        }
    }

    private void broadcastParticipantNumNotify(Integer participantNumber) {
        try {
            ParticipantNumberChangeNotify notify = new ParticipantNumberChangeNotify();
            notify.setParticipantNumber(participantNumber);
            for (IFrtcCallListener listener : meetingMessageListeners) {
                listener.onParticipantNumberChangeNotify(notify);
            }
        } catch (Exception e) {
            Log.e(TAG, "broadcastParticipantNumNotify error:" + e);
        }
    }

    public void onParticipantCountNotify(Message msg, boolean isResume) {
        try {
            int participantNum = 0;
            if (!isResume && msg != null) {
                String jsonString = msg.getData().getString(FrtcConstant.DATA);
                InternalParticipantInfoMessage internalParticipantInfoMessage = JSONUtil.transform(jsonString, InternalParticipantInfoMessage.class);
                participantNum = internalParticipantInfoMessage.getParti_count();
            }
            broadcastParticipantNumNotify(participantNum);
            if (participantNum == 1) {
                ParticipantInfo participantInfo = new ParticipantInfo();
                participantInfo.setDisplay_name(getMeetingStatusInfo().getDisplayName());
                participantInfo.setUser_id(getMeetingStatusInfo().getUserId());
                participantInfo.setAudioMuted(getMeetingConfig().isAudioMuted());
                participantInfo.setVideoMuted(getMeetingConfig().isVideoMuted());
                participantInfo.setUuid(SDKConfigManager.getInstance(this.context).getClientId());
                getParticipantInfos().clear();
                getParticipantInfos().add(participantInfo);
                broadcastParticipantStateNotify(getParticipantInfos(), true, "");
            }
        } catch (Exception e) {
            Log.e(TAG, "onParticipantCountNotify error:" + e);
        }
    }

    public void onTextOverlayNotify(Message message) {
        try {
            OverlayParam overlayParam = JSONUtil.transform(message.getData().getString(FrtcConstant.DATA), OverlayParam.class);
            broadcastOnOverlay(overlayParam);
        } catch (Exception e) {
            Log.e(TAG, "onTextOverlayNotify error:" + e);
        }
    }

    private void broadcastOnOverlay(OverlayParam overlayParam) {
        try {
            OverlayNotify notify = new OverlayNotify();
            notify.setContent(overlayParam.getText_overlay_msg());
            notify.setEnabled(overlayParam.isText_overlay_enable());
            notify.setRepeat(overlayParam.getDisplay_repetition());
            notify.setType(overlayParam.getText_overlay_type());
            notify.setSpeed(overlayParam.getDisplay_speed());
            notify.setVerticaPosition(overlayParam.getVertical_position());
            for (IFrtcCallListener listener : meetingMessageListeners) {
                listener.onOverlayNotify(notify);
            }
        } catch (Exception e) {
            Log.e(TAG, "broadcastOnOverlay error:" + e);
        }
    }

    public void onMeetingSessionInfoNotify(Message msg) {
        try {
            String jsonString = msg.getData().getString(FrtcConstant.DATA);
            InternalMeetingSessionMessage message = JSONUtil.transform(jsonString, InternalMeetingSessionMessage.class);
            String contentInfoString = message.getWatermark_msg();
            InternalContentMessage internalContentMessage = JSONUtil.transform(contentInfoString, InternalContentMessage.class);
            getMeetingConfig().setWaterMarkEnabled(internalContentMessage.isEnable());
            broadcastLiveRecordStatusNotify(message.getStreaming_status(),
                    message.getRecording_status(),
                    message.getStreaming_url(),
                    message.getStreaming_pwd());
        } catch (Exception e) {
            Log.e(TAG, "onMeetingSessionInfoNotify error:" + e);
        }
    }

    private void broadcastLiveRecordStatusNotify(String liveStatus, String recordingStatus, String liveMeetingUrl, String liveMeetingPwd) {
        try {
            LiveRecordStatusNotify notify = new LiveRecordStatusNotify();
            if (liveStatus == null || liveStatus.isEmpty() || MeetingLiveState.NOT_STARTED.getName().equals(liveStatus)) {
                notify.setLiveStarted(false);
                getMeetingConfig().setLiveStarted(false);
            } else {
                notify.setLiveStarted(MeetingLiveState.STARTED.getName().equals(liveStatus));
                getMeetingConfig().setLiveStarted(true);
            }
            if (recordingStatus == null || recordingStatus.isEmpty() || MeetingRecordState.NOT_STARTED.getName().equals(recordingStatus)) {
                notify.setRecordStarted(false);
                getMeetingConfig().setRecordingStarted(false);
            } else {
                notify.setRecordStarted(MeetingRecordState.STARTED.getName().equals(recordingStatus));
                getMeetingConfig().setRecordingStarted(true);
            }
            if (liveMeetingUrl == null) {
                notify.setLiveMeetingUrl("");
                getMeetingConfig().setLiveMeetingUrl("");
            } else {
                notify.setLiveMeetingUrl(liveMeetingUrl);
                getMeetingConfig().setLiveMeetingUrl(liveMeetingUrl);
            }
            if (liveMeetingPwd == null) {
                notify.setLiveMeetingPwd("");
                getMeetingConfig().setLivePassword("");
            } else {
                notify.setLiveMeetingPwd(liveMeetingPwd);
                getMeetingConfig().setLivePassword(liveMeetingPwd);
            }
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onLiveRecordStatusNotify(notify);
            }
        } catch (Exception e) {
            Log.e(TAG, "broadcastLiveRecordStatusNotify error:" + e);
        }
    }

    private void broadcastParticipantStateNotify(List<ParticipantInfo> list, boolean isFullList, String selfNewName) {
        try {
            ParticipantStateChangeNotify notify = new ParticipantStateChangeNotify();
            notify.setFullList(isFullList);
            notify.setSelfNewName(selfNewName);
            if (list != null && !list.isEmpty()) {
                notify.setParticipantList(new ArrayList<>());
                for (ParticipantInfo p : list) {
                    Participant participant = new Participant();
                    participant.setVideoMuted(p.getVideoMuted());
                    participant.setUuid(p.getUuid());
                    participant.setAudioMuted(p.getAudioMuted());
                    participant.setDisplayName(p.getDisplay_name());
                    participant.setUserId(p.getUser_id());
                    notify.getParticipantList().add(participant);
                }
            }
            for (IFrtcCallListener listener : meetingMessageListeners) {
                listener.onParticipantStateChangeNotify(notify);
            }
        } catch (Exception e) {
            Log.e(TAG, "broadcastParticipantStateNotify error:" + e);
        }
    }

    public void onNetworkInfoNotify(Message msg) {
        try {
            String jsonString = msg.getData().getString(FrtcConstant.DATA);
            InternalNetworkInfo internalNetworkInfo = JSONUtil.transform(jsonString, InternalNetworkInfo.class);
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onNetworkInfoNotify(internalNetworkInfo.getNetwork_status());
            }
        } catch (Exception e) {
            Log.e(TAG, "onNetworkInfoNotify error:" + e);
        }
    }

    public void onMeetingStatusChange(Message msg) {
        try {
            String data = msg.getData().getString(FrtcConstant.DATA);
            MeetingStatus status = JSONUtil.transform(data, MeetingStatus.class);
            int errorCode = status.getReason_code();
            switch (status.getMeeting_status()) {
                case kConnected:
                    getMeetingStatusInfo().setMeetingStatus(FrtcMeetingStatus.CONNECTED);
                    broadcastMeetingStateNotify();
                    getMessengerManager().setAudioVideoControl(getMeetingConfig().isAudioMuted(), getMeetingConfig().isVideoMuted());
                    if (!meetingConnected) {
                        onMeetingConnected(getMessengerManager());
                    }
                    if(isFirstCallConnected){
                        isFirstCallConnected = false;
                    }
                    if(isReconnecting){
                        resumeForReconnect();
                        isReconnecting = false;
                        reconnectCount = 0;
                    }
                    getMeetingLayoutView().onMeetingConnected();
                    break;
                case kDisconnected:
                    if (getMeetingStatusInfo().getMeetingStatus() == FrtcMeetingStatus.DISCONNECTED) {
                        return;
                    }
                    if(errorCode == MeetingErrorCode.MeetingEndAbnormal.getCode()
                            && getMeetingStatusInfo().getMeetingStatus() == FrtcMeetingStatus.CONNECTED){
                        isReconnecting = true;
                        stopSendContent();
                    }
                    if(isReconnecting && reconnectCount <= 3){
                        reconnectCount++;
                        getMeetingStatusInfo().setMeetingStatus(FrtcMeetingStatus.DISCONNECTED);
                        meetingErrorCode = MeetingErrorCode.from(errorCode);
                        getMeetingLayoutView().onMeetingDisconnectedForReconnect();
                        broadcastMeetingStateNotify();
                        getMeetingConfig().setWaterMarkEnabled(false);
                    }else {
                        if(reconnectCount > 3){
                            isReconnecting = false;
                            reconnectCount = 0;
                        }
                        getMeetingStatusInfo().setMeetingStatus(FrtcMeetingStatus.DISCONNECTED);
                        onMeetingDisconnected(errorCode);
                        getMeetingLayoutView().onMeetingDisconnected();
                        broadcastMeetingStateNotify();
                        getMeetingConfig().setWaterMarkEnabled(false);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "onMeetingStatusChange error:" + e);
        }
    }

    @Override
    public void inviteUser() {
        try {
            for (IFrtcCallListener listener : getMeetingMessageListeners()) {
                listener.onInviteUserNotify(getParticipantInfos());
            }
        } catch (Exception e) {
            Log.e(TAG, "inviteUser error:" + e);
        }
    }

}
