package frtc.sdk;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import frtc.sdk.conf.SDKConfigManager;
import frtc.sdk.internal.service.MeetingService;
import frtc.sdk.internal.model.FrtcMeetingStatus;
import frtc.sdk.log.Log;
import frtc.sdk.model.InitParams;
import frtc.sdk.model.JoinMeetingOption;
import frtc.sdk.model.JoinMeetingParam;
import frtc.sdk.model.JoinMeetingQRCodeParam;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.ui.view.MeetingLayout;
import frtc.sdk.ui.media.ShareContentController;

public final class FrtcCall implements IFrtcCallAPI {
    private final String TAG = this.getClass().getSimpleName();
    private static FrtcCall instance = null;

    private InitParams initParams = null;
    private IMeetingService meetingManager = null;
    private Context context = null;
    private ShareContentController shareContentController;
    private OrientationEventListener orientationEventListener;

    private FrtcCall() {
        Log.d(TAG,"FrtcCall constructor");
    }

    public static synchronized FrtcCall getInstance() {
        if (instance == null) {
            synchronized (FrtcCall.class) {
                if (instance == null) {
                    instance = new FrtcCall();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean initialize(Context context, InitParams initParams) {
        Log.d(TAG,"initialize:");
        this.initParams = initParams;
        if (context == null || context.getApplicationContext() == null) {
            throw new NullPointerException("context cannot be null");
        }
        if (initParams == null || initParams.getClientId() == null || initParams.getClientId().isEmpty() ) {
            throw new NullPointerException("clientId cannot be null");
        }
        SDKConfigManager.getInstance(context).setClientId(initParams.getClientId());
        this.context = context.getApplicationContext();
        meetingManager = new MeetingService(this);
        return true;
    }

    public boolean isiInitialized(){
        return (meetingManager != null);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void close() {
        meetingManager.stop();
    }

    public void closeMicrophoneAudio() {
        meetingManager.closeMicrophoneAudio();
    }

    @Override
    public void registerCallListener(IFrtcCallListener listener) {
        meetingManager.registerMeetingListener(listener);
    }

    @Override
    public void unRegisterCallListener(IFrtcCallListener listener) {
        meetingManager.unregisterMeetingListener(listener);
    }

    @Override
    public String getSDKVersion() {
        return meetingManager.getSDKVersion();
    }

    @Override
    public void setServerAddress(String address) {
        SDKConfigManager.getInstance(context).setServerAddress(address);
    }

    @Override
    public String getServerAddress() {
        return SDKConfigManager.getInstance(context).getServerAddress();
    }

    @Override
    public void setDisplayName(String name) {
        meetingManager.setDisplayName(name);
    }

    @Override
    public FrtcMeetingStatus getMeetingStatus() {
        return meetingManager.getMeetingStatus();
    }

    @Override
    public void joinMeetingWithParam(JoinMeetingParam joinMeetingParam, JoinMeetingOption joinMeetingOption) {
        meetingManager.joinMeetingWithParam(joinMeetingParam, joinMeetingOption);
    }

    @Override
    public boolean joinMeetingWithQRCode(JoinMeetingQRCodeParam joinMeetingQRCodeParam) {
        return meetingManager.joinMeetingWithQRCode(joinMeetingQRCodeParam);
    }

    @Override
    public void setMeetingPassword(String password) {
        meetingManager.setMeetingPassword(password);
    }

    @Override
    public void initMeetingUI(MeetingLayout layout, ViewGroup surfaceView, ViewGroup pagingView, ImageView ivLeft, ImageView ivRight, RelativeLayout slideView) {
        meetingManager.initMeetingUI(layout, surfaceView, pagingView, ivLeft, ivRight, slideView);
    }

    @Override
    public void setVisibleMeetingSlideView(boolean visible) {
        meetingManager.setVisibleCallSlideView(visible);
    }

    @Override
    public void reconnectCall() {
        meetingManager.reconnectCall();
    }

    public void screenOrientationChanged(int rotation) {
        meetingManager.screenOrientationChanged(rotation);
    }

    public void contentOrientationChanged(int rotation){
        meetingManager.contentOrientationChanged(rotation);
    }

    public void resume(boolean muteVideo, boolean muteRemoteVideo) {
        meetingManager.resume(muteVideo, muteRemoteVideo);
    }

    public void stop(boolean isShowCallFloatView) {
        meetingManager.stop(isShowCallFloatView);
    }

    public void setVisibleOverlay(boolean visible) {
        meetingManager.setVisibleOverlay(visible);
    }
    public void setHeadsetEnableSpeaker(boolean enableSpeaker) {
        meetingManager.setHeadsetEnableSpeaker(enableSpeaker);
    }

    public void leaveMeeting() {
        Log.d(TAG,"leaveMeeting:");
        meetingManager.leaveMeeting();
    }

    public void muteLocalAudio(boolean isMuted) {
        meetingManager.muteLocalAudio(isMuted);
    }

    public void muteLocalVideo(boolean isMuted) {
        meetingManager.muteLocalVideo(isMuted);
    }

    public void setLocalPeoplePreview(boolean enabled) {
        meetingManager.setLocalPeopleViewEnable(enabled);
    }

    public void switchSpeaker() {
        meetingManager.switchSpeaker();
    }

    public void switchFrontOrBackCamera() {
        meetingManager.switchFrontOrBackCamera();
    }

    @Override
    public void setNoiseBlock(boolean enabled) {
        meetingManager.setNoiseBlock(enabled);
    }

    public void inviteUser() {
        meetingManager.inviteUser();
    }

    @Override
    public void startSendContent(){
        meetingManager.startSendContent();
    }
    @Override
    public void stopSendContent(){
        meetingManager.stopSendContent();
    }

    @Override
    public String getMeetingServerAddress() {
        return meetingManager.getMeetingServerAddress();
    }

    @Override
    public void startMeetingReminderTimer(int timerID, int duration, boolean periodic) {
        meetingManager.startMeetingReminderTimer(timerID, duration, periodic);
    }

    @Override
    public void stopTimer(int timerID) {
        meetingManager.stopTimer(timerID);
    }

    @Override
    public void registerCommonListener(IFrtcCommonListener listener) {
        meetingManager.registerCommonListener(listener);
    }

    @Override
    public void unregisterCommonListener(IFrtcCommonListener listener) {
        meetingManager.unregisterCommonListener(listener);
    }

    @Override
    public void startUploadLogs(String strMetaData, String fileName, int count) {
        meetingManager.startUploadLogs(strMetaData, fileName, count);
    }

    public void getUploadStatus(int tractionId, int fileType) {
        meetingManager.getUploadStatus(tractionId, fileType);
    }

    @Override
    public void cancelUploadLogs(int tractionId) {
        meetingManager.cancelUploadLogs(tractionId);
    }

    @Override
    public void muteRemoteVideo(boolean isMuted) {
        meetingManager.muteRemoteVideo(isMuted);
    }

    @Override
    public void saveMeetingIntoMeetingListNotify() {
        meetingManager.saveMeetingIntoMeetingListNotify();
    }

    @Override
    public void showMeetingReminder(ArrayList<ScheduledMeeting> scheduledMeetingsReminders) {
        meetingManager.showMeetingReminder(scheduledMeetingsReminders);
    }

    @Override
    public boolean resumeCall() {
        return meetingManager.resumeCall();
    }

    @Override
    public void closeMeetingLayout() {
        meetingManager.closeMeetingLayout();
    }

    @Override
    public void setLocalContentController(ShareContentController shareContentController) {
        this.shareContentController = shareContentController;
    }

    @Override
    public ShareContentController getLocalContentController() {
        return shareContentController;
    }

    private int currentContentRotation = -1;
    @Override
    public void registerOrientationEventListener() {
        orientationEventListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {

                Display contentDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int contentRotation = contentDisplay.getRotation();
                if (currentContentRotation != contentRotation) {
                    Log.i(TAG, "onOrientationChanged currentContentRotation = "+currentContentRotation +" rotation = " + contentRotation);
                    contentOrientationChanged(contentRotation);
                    currentContentRotation = contentRotation;
                }
            }
        };

        if(orientationEventListener.canDetectOrientation()){
            Log.d(TAG,"orientationEventListener enable");
            orientationEventListener.enable();
        }else{
            orientationEventListener.disable();
        }
    }

    @Override
    public void unregisterOrientationEventListener() {
        if(orientationEventListener != null){
            orientationEventListener.disable();
        }
    }

    @Override
    public boolean isAudioMuted() {
        return meetingManager.isAudioMuted();
    }

    @Override
    public int getDisconnectErrorCode() {
        return meetingManager.getDisconnectErrorCode();
    }

    @Override
    public boolean isAudioDisabled() {
        return meetingManager.isAudioDisabled();
    }

    @Override
    public void setAudioDisabled(boolean audioDisabled) {
        meetingManager.setAudioDisabled(audioDisabled);
    }

    @Override
    public boolean isLiveStarted() {
        return meetingManager.isLiveStarted();
    }

    @Override
    public boolean isRecordingStarted() {
        return meetingManager.isRecordingStarted();
    }

    @Override
    public String getLiveMeetingUrl() {
        return meetingManager.getLiveMeetingUrl();
    }

    @Override
    public String getLiveMeetingPwd() {
        return meetingManager.getLiveMeetingPwd();
    }

    @Override
    public boolean isHeadsetEnableSpeaker() {
        return meetingManager.isHeadsetEnableSpeaker();
    }

    @Override
    public int getCameraId() {
        return meetingManager.getCameraId();
    }

    @Override
    public void setCameraId(int cameraId) {
        meetingManager.setCameraId(cameraId);
    }

    @Override
    public boolean isRemoteVideoMuted() {
        return meetingManager.isRemoteVideoMuted();
    }

}
