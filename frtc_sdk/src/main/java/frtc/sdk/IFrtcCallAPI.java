package frtc.sdk;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import frtc.sdk.internal.model.FrtcMeetingStatus;
import frtc.sdk.model.InitParams;
import frtc.sdk.model.JoinMeetingOption;
import frtc.sdk.model.JoinMeetingParam;
import frtc.sdk.model.JoinMeetingQRCodeParam;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.ui.view.MeetingLayout;
import frtc.sdk.ui.media.ShareContentController;

public interface IFrtcCallAPI extends IFrtcAPI {

    boolean initialize(Context context, InitParams initParams);
    void setServerAddress(String address);
    void registerCallListener(IFrtcCallListener listener);
    void unRegisterCallListener(IFrtcCallListener listener);
    void close();
    void closeMicrophoneAudio();
    void resume(boolean muteVideo, boolean muteRemoteVideo);
    void stop(boolean isShowCallFloatView);

    void joinMeetingWithParam(JoinMeetingParam joinMeetingParam, JoinMeetingOption joinMeetingOption);
    boolean joinMeetingWithQRCode(JoinMeetingQRCodeParam joinMeetingQRCodeParam);
    void setMeetingPassword(String password);
    FrtcMeetingStatus getMeetingStatus();
    void leaveMeeting();

    void initMeetingUI(MeetingLayout layout, ViewGroup surfaceView, ViewGroup pagingView, ImageView ivLeft, ImageView ivRight, RelativeLayout slideView);
    void setVisibleMeetingSlideView(boolean visible);
    void setVisibleOverlay(boolean visible);
    void setLocalPeoplePreview(boolean enabled);
    void muteLocalAudio(boolean isMuted);
    void muteLocalVideo(boolean isMuted);
    void muteRemoteVideo(boolean isMuted);
    void screenOrientationChanged(int rotation);
    void contentOrientationChanged(int rotation);
    void setHeadsetEnableSpeaker(boolean enableSpeaker);
    void switchSpeaker();
    void switchFrontOrBackCamera();

    void setNoiseBlock(boolean enabled);
    void reconnectCall();

    void startSendContent();
    void stopSendContent();

    String getMeetingServerAddress();

    void startMeetingReminderTimer(int timerID, int duration, boolean periodic);
    void stopTimer(int timerID);
    void registerCommonListener(IFrtcCommonListener listener);
    void unregisterCommonListener(IFrtcCommonListener listener);

    void startUploadLogs(String strMetaData, String fileName, int count);
    void getUploadStatus(int tractionId, int fileType);
    void cancelUploadLogs(int tractionId);

    void setDisplayName(String name);
    void saveMeetingIntoMeetingListNotify();
    void showMeetingReminder(ArrayList<ScheduledMeeting> scheduledMeetingsReminders);

    boolean resumeCall();
    void closeMeetingLayout();
    void setLocalContentController(ShareContentController shareContentController);
    ShareContentController getLocalContentController();
    void registerOrientationEventListener();
    void unregisterOrientationEventListener();
    boolean isAudioMuted();
    int getDisconnectErrorCode();

    boolean isAudioDisabled();
    void setAudioDisabled(boolean audioDisabled);
    boolean isLiveStarted();
    boolean isRecordingStarted();
    String getLiveMeetingUrl();
    String getLiveMeetingPwd();
    boolean isHeadsetEnableSpeaker();
    int getCameraId();
    void setCameraId(int cameraId);
    boolean isRemoteVideoMuted();
    void requestParticipants();
}
