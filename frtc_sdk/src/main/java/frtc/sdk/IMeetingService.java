package frtc.sdk;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import frtc.sdk.internal.model.FrtcMeetingStatus;
import frtc.sdk.model.JoinMeetingOption;
import frtc.sdk.model.JoinMeetingParam;
import frtc.sdk.model.JoinMeetingQRCodeParam;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.ui.view.MeetingLayout;

public interface IMeetingService {

    String getSDKVersion();
    void registerMeetingListener(IFrtcCallListener listener);
    void unregisterMeetingListener(IFrtcCallListener listener);
    void stop();

    void initMeetingUI(MeetingLayout callLayout, ViewGroup surfaceArea, ViewGroup pagingArea, ImageView slideViewLeft, ImageView slideViewRight, RelativeLayout callSlideView);
    void setVisibleCallSlideView(boolean visible);
    void reconnectCall();
    void joinMeetingWithParam(JoinMeetingParam joinMeetingParam, JoinMeetingOption joinMeetingOption);
    boolean joinMeetingWithQRCode(JoinMeetingQRCodeParam joinMeetingQRCodeParam);
    void leaveMeeting();

    void setUnMuteAudio();
    void setMeetingPassword(String password);
    void setVisibleOverlay(boolean visibile);
    void setHeadsetEnableSpeaker(boolean enableSpeaker);
    void screenOrientationChanged(int rotation);
    void contentOrientationChanged(int rotation);

    FrtcMeetingStatus getMeetingStatus();
    void resume(boolean muteVideo, boolean muteRemoteVideo);
    void resumeAudio(boolean muteAudio);
    void stop(boolean isShowCallFloatView);
    void muteLocalAudio(boolean isMuted);
    void muteLocalVideo(boolean isMuted);
    void muteRemoteVideo(boolean isMuted);

    void setLocalPeopleViewEnable(boolean enabled);
    void switchSpeaker();
    void switchFrontOrBackCamera();

    void setNoiseBlock(boolean enabled);
    void inviteUser();
    void startSendContent();
    void stopSendContent();

    String getMeetingServerAddress();

    void startMeetingReminderTimer(int timerID, int duration, boolean periodic);
    void stopTimer(int timerID);
    void registerCommonListener(IFrtcCommonListener listener);
    void unregisterCommonListener(IFrtcCommonListener listener);

    void closeMicrophoneAudio();

    void startUploadLogs(String strMetaData, String fileName, int count);
    void getUploadStatus(int tractionId, int fileType);
    void cancelUploadLogs(int tractionId);

    void setDisplayName(String name);
    void saveMeetingIntoMeetingListNotify();

    void showMeetingReminder(ArrayList<ScheduledMeeting> scheduledMeetingsReminders);

    boolean resumeCall();
    void closeMeetingLayout();
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
}
