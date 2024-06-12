package frtc.sdk.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.projection.MediaProjectionManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import frtc.sdk.model.AllowUnmuteParam;
import frtc.sdk.model.CommonMeetingParam;
import frtc.sdk.model.CreateInstantMeetingResult;
import frtc.sdk.model.CreateScheduledMeetingResult;
import frtc.sdk.model.DisconnectParticipantParam;
import frtc.sdk.FrtcCall;
import frtc.sdk.FrtcManagement;
import frtc.sdk.IFrtcCallListener;
import frtc.sdk.IFrtcCommonListener;
import frtc.sdk.IFrtcManagementListener;
import frtc.sdk.IMeetingReminderListener;
import frtc.sdk.model.LiveMeetingParam;
import frtc.sdk.model.LiveRecordStatusNotify;
import frtc.sdk.model.MeetingStateInfoNotify;
import frtc.sdk.model.OverlayNotify;
import frtc.sdk.model.MuteAllParam;
import frtc.sdk.model.MuteControlStateNotify;
import frtc.sdk.model.MuteParam;
import frtc.sdk.model.Participant;
import frtc.sdk.model.ParticipantNumberChangeNotify;
import frtc.sdk.model.ParticipantStateChangeNotify;
import frtc.sdk.model.PinForMeetingParam;
import frtc.sdk.model.QueryMeetingInfoResult;
import frtc.sdk.model.QueryMeetingRoomResult;
import frtc.sdk.model.QueryUserInfoResult;
import frtc.sdk.R;
import frtc.sdk.model.RecurrenceMeetingListResult;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.model.ScheduledMeetingListResult;
import frtc.sdk.model.ScheduledMeetingResult;
import frtc.sdk.model.SignInResult;
import frtc.sdk.model.StartOverlayParam;
import frtc.sdk.model.StopMeetingParam;
import frtc.sdk.model.StopOverlayParam;
import frtc.sdk.model.UnMuteAllParam;
import frtc.sdk.model.UnMuteParam;
import frtc.sdk.model.ChangeDisplayNameParam;
import frtc.sdk.model.LectureParam;
import frtc.sdk.internal.model.LecturerInfo;
import frtc.sdk.model.UnmuteRequest;
import frtc.sdk.internal.model.MeetingMediaStatsWrapper;
import frtc.sdk.internal.model.FindUserResult;
import frtc.sdk.internal.model.FrtcMeetingStatus;
import frtc.sdk.internal.model.MeetingErrorCode;
import frtc.sdk.internal.model.ResultType;
import frtc.sdk.internal.model.UploadLogsStatus;
import frtc.sdk.log.Log;
import frtc.sdk.ui.component.FloatView;
import frtc.sdk.ui.dialog.EndMeetingDialog;
import frtc.sdk.service.BackgroundProcessNoticeService;
import frtc.sdk.service.ShareScreenNoticeService;
import frtc.sdk.ui.component.FrtcFragmentManager;
import frtc.sdk.ui.dialog.ChangeDisplayNameDlg;
import frtc.sdk.ui.component.MeetingControlBar;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.dialog.IMeetingControlDlgListener;
import frtc.sdk.ui.component.IMeetingFloatViewListener;
import frtc.sdk.ui.component.IControlBarRequestListener;
import frtc.sdk.ui.component.IParticipantListListener;
import frtc.sdk.ui.dialog.InvitationInformationDlg;
import frtc.sdk.ui.dialog.LiveRecordDlg;
import frtc.sdk.ui.component.LiveRecordingMenu;
import frtc.sdk.ui.dialog.MeetingDetailsDlg;
import frtc.sdk.ui.dialog.MeetingMediaStatsDlg;
import frtc.sdk.ui.component.TextOverlayManager;
import frtc.sdk.ui.view.OverlayTextView;
import frtc.sdk.ui.dialog.MuteAllParticipantsDlg;
import frtc.sdk.ui.dialog.ParticipantsControlDlg;
import frtc.sdk.ui.dialog.ParticipantsDlg;
import frtc.sdk.ui.component.PasswordInputFragment;
import frtc.sdk.ui.dialog.PermissionsRequestListDlg;
import frtc.sdk.ui.dialog.RecordFileDlg;
import frtc.sdk.ui.component.ScreenListener;
import frtc.sdk.ui.dialog.ShareLiveInfoDlg;
import frtc.sdk.ui.dialog.StartOverlayDlg;
import frtc.sdk.ui.dialog.UnmuteAllParticipantsDlg;
import frtc.sdk.ui.dialog.UnmuteRequestNotifyDlg;
import frtc.sdk.ui.component.JoinMeetingFailedListener;
import frtc.sdk.ui.model.MeetingCall;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.model.ViewConstant;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.ui.dialog.ConfirmDlg;
import frtc.sdk.ui.dialog.IConfirmDlgListener;
import frtc.sdk.ui.dialog.MeetingInformDlg;
import frtc.sdk.ui.dialog.MeetingReminderDlg;
import frtc.sdk.ui.dialog.MessageDlg;
import frtc.sdk.ui.model.ParticipantInfo;
import frtc.sdk.util.ActivityUtils;
import frtc.sdk.util.Constants;
import frtc.sdk.util.LanguageUtil;
import frtc.sdk.ui.view.MeetingLayout;
import frtc.sdk.ui.media.ShareContentController;


public class FrtcMeetingActivity extends AppCompatActivity implements JoinMeetingFailedListener,
        IMeetingControlDlgListener, IParticipantListListener, IFrtcCallListener,
        IFrtcManagementListener, IFrtcCommonListener, IMeetingFloatViewListener {
    private static final String TAG = FrtcMeetingActivity.class.getSimpleName();

    private boolean isCrossServer = false;
    private boolean isReconnecting = false;
    private boolean isLiveStarted = false;
    private boolean isRecordingStarted = false;
    private boolean isRequestUnmuteSelf = false;
    private boolean isSharingContent = false;
    private boolean isEnabledLocalPeoplePreview = false;
    private boolean entered_first = true;
    private boolean isLocalAction = false;
    private boolean audio_muted = false;
    private boolean video_muted = false;
    private boolean audio_disabled = false;
    private boolean meeting_canceled = false;
    private boolean video_mute_disabled = false;
    private boolean remote_video_muted = false;
    private boolean isShowDlg = false;
    private boolean isAllowUnmute = true;
    private boolean needRecoverVideoMuteState = false;
    private boolean isScreenShare = false;
    private boolean isFloatWindow = false;
    private boolean isBackFromPhoneSetting = false;
    private boolean requestOverlayPermissionForShareContent = false;
    private boolean requestOverlayPermissionForFloatMode = false;
    private String crossServerAddr = "";
    private int reconnectCount = 0;
    private final int permissionReqCode = 1;
    public static String MEETING_END_CODE = "meeting_end_code";
    private String pinnedUuid = "";
    private long reqUnmuteTimestamp;
    private int volumeLevel = 0;
    private int cur_rotation = -1;
    private String lecturerUuid;
    private long joinTimeMs = 0;
    private volatile boolean clean_mode;

    private MeetingDetailsDlg meetingDetailsDlg;
    private MeetingMediaStatsDlg meetingMediaStatsDlg;
    private MeetingReminderDlg meetingReminderDlg;
    private List<ParticipantInfo> participantInfos = new ArrayList<>();
    private List<UnmuteRequest> unmuteRequestParticipants = new ArrayList<>();
    private FrtcFragmentManager frtcFragmentManager;
    private LiveRecordingMenu liveRecordingMenu;
    private TextOverlayManager textOverlayManager;
    private MeetingMediaStatsWrapper meetingMediaStatsInfo;
    private MeetingLayout meetingLayout;
    private MeetingControlBar meetingControlBar;
    private Handler handler;
    public FrtcCall frtcCall;
    public FrtcManagement frtcManagement;
    protected ViewGroup stage;
    protected ViewGroup layoutGroup;
    private LinearLayout reconnectingView;
    private ParticipantsDlg participantsDlg;
    private OrientationEventListener orientationEventListener;
    private LocalStore localStore;
    private ConfirmDlg unMuteConfirmDlg;
    private ShareContentController shareContentController;
    private ScreenListener screenListener;
    private PermissionsRequestListDlg permissionsRequestDlg;
    private MeetingInformDlg signInUnauthorizedInformDlg;
    private ConfirmDlg networkStatusChangedDlg;
    private RecordFileDlg recordFileDlg;
    private MeetingInformDlg endMeetingInformDlg;
    private FloatView floatView;

    private final Runnable reconnect_runnable = new Runnable() {
        @Override
        public void run() {
            joinMeeting();
        }
    };


    private Runnable dismissDlgRunnable = new Runnable() {
        @Override
        public void run() {
            if(endMeetingInformDlg != null && endMeetingInformDlg.isShowing()){
                leaveMeeting();
                endMeetingInformDlg.dismiss();
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            finish();
            return;
        }
        LanguageUtil.setLanguage(this);
        ActivityUtils.add(FrtcMeetingActivity.this);
        frtcCall = FrtcCall.getInstance();
        frtcCall.registerCallListener(this);
        frtcCall.registerCommonListener(this);
        frtcManagement = FrtcManagement.getInstance();
        frtcManagement.registerManagementListener(this);
        handler = new Handler();
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        initShareContentController();
        Intent intent = getIntent();
        isFloatWindow = intent.getBooleanExtra("isFloatWindow", false);
        isScreenShare = intent.getBooleanExtra("isScreenShare", false);
        initializeMeetingLayout();

        ViewGroup fragContainer = findViewById(R.id.meeting_state_manager);
        frtcFragmentManager = new FrtcFragmentManager(fragContainer, getFragmentManager());
        RelativeLayout controlBarView = findViewById(R.id.control_bar);
        meetingControlBar = new MeetingControlBar(this, controlBarView);
        meetingControlBar.setControlBarVisible(false);
        meetingControlBar.setControlBarRequestListener(initControlBarRequestListener());
        initLiveRecordingMenu();
        RelativeLayout messageLayout = findViewById(R.id.overlay_text_layout);
        OverlayTextView overlayTextView = messageLayout.findViewById(R.id.text_overlay);
        textOverlayManager = new TextOverlayManager(this, messageLayout, overlayTextView);
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        audio_muted = frtcCall.isAudioMuted();
        if (localStore.isAudioCall()) {
            video_muted = true;
            meetingControlBar.setAudioCall(true);
        } else {
            video_muted = !localStore.isCameraOn();
            meetingControlBar.setAudioCall(false);
            meetingControlBar.setVideoMute(video_muted);
            meetingControlBar.setCameraMuteState(video_muted);
        }
        isEnabledLocalPeoplePreview = !video_muted;
        frtcCall.setLocalPeoplePreview(isEnabledLocalPeoplePreview);
        meetingControlBar.setAudioMute(audio_muted);
        int callEndReason = frtcCall.getDisconnectErrorCode();
        if(frtcCall.getMeetingStatus() != FrtcMeetingStatus.CONNECTED
                && callEndReason != MeetingErrorCode.RemovedFromMeeting.getCode()
                && callEndReason != MeetingErrorCode.MeetingStop.getCode()) {
            showConnectingView();
            showSaveServerDlg();
        } else {
            isSharingContent = localStore.isSharingContent();
            if(!frtcCall.resumeCall()){
                return;
            }
            meetingResume();
        }
        startBackgroundProcessNoticeService();
        initOrientationEventListener();
    }

    private void meetingResume() {
        entered_first = false;
        isCrossServer = frtcCall.isCrossServer();
        crossServerAddr = isCrossServer ? frtcCall.getMeetingServerAddress() : "";
        if(isLocalParticipantInfoAvailable()){
            meetingControlBar.setToolbarParticipantCount(Integer.toString(participantInfos.size()));
        }else{
            frtcCall.requestParticipants();
        }
        meetingControlBar.setMeetingName(localStore.getMeetingName());
        meetingControlBar.resumeChronometer(localStore.getElapsedRealtime());
        meetingControlBar.updateHostPermission(isHost(), isOperatorOrAdmin());
        onHeadsetEnableSpeakerNotify(frtcCall.isHeadsetEnableSpeaker());
        remote_video_muted = localStore.isRemoteVideoMuted();
        meetingControlBar.setRemoteVideoMute(remote_video_muted);

        if(liveRecordingMenu != null){
            liveRecordingMenu.updateHostPermission(isHost(),isOperatorOrAdmin());
        }
        isLiveStarted = frtcCall.isLiveStarted();
        setLiveMenuVisibility(isLiveStarted);
        if(isLiveStarted){
            localStore.setLiveMeetingUrl(frtcCall.getLiveMeetingUrl());
            localStore.setLivePassword(frtcCall.getLiveMeetingPwd());
        }
        isRecordingStarted = frtcCall.isRecordingStarted();
        setRecordingMenuVisibility(isRecordingStarted);

        if(isSharingContent){
            meetingControlBar.setShareContentState(isSharingContent);
            setVideoMuteDisabled();
            setScreenListener();
        }
    }

    private void showSaveServerDlg() {
        String serverAddress = frtcCall.getMeetingServerAddress();
        if(!localStore.isLogged() && !serverAddress.isEmpty() && !serverAddress.equals(localStore.getServer())){
            showSaveServerInformDlg(serverAddress);
        }
    }

    private void showSaveServerInformDlg(final String server) {
        String tipFormat = getResources().getString(R.string.msg_save_server);
        String tip = String.format(tipFormat, server);
        ConfirmDlg confirmDlg = new ConfirmDlg(this,"",tip, getString(R.string.meeting_dialog_negative_btn), getString(R.string.meeting_dialog_positive_btn));
        confirmDlg.setStr(getResources().getString(R.string.msg_question_contact_administrator));
        confirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                localStore.setServer(server);
                frtcCall.setServerAddress(server);
                frtcManagement.setServerAddress(server);
                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.save_notice), Toast.LENGTH_SHORT);
            }
            @Override
            public void onCancel(){

            }
        });
        confirmDlg.show();
    }

    private void checkPermission() {
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, permissionReqCode);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissionReqCode == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                showOpenCameraInformDlg();
            }

        }
    }

    private void showOpenCameraInformDlg(){
        String tipFormat = getResources().getString(R.string.msg_qrscan_camera_permission_request);
        String tip = String.format(tipFormat, getResources().getString(R.string.app_name));
        ConfirmDlg confirmDlg = new ConfirmDlg(this,"",tip, getString(R.string.meeting_dialog_negative_btn), getString(R.string.dialog_settings_btn));

        confirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                Uri packageURI = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                startActivity(intent);
            }
            @Override
            public void onCancel(){

            }
        });
        confirmDlg.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        FrtcMeetingStatus meetingStatus = frtcCall.getMeetingStatus();
        if (FrtcMeetingStatus.CONNECTED == meetingStatus) {
            if (MotionEvent.ACTION_UP == event.getAction()) {
                cleanMessageHandler();
                if (!clean_mode) {
                    enterCleanMode();
                } else {
                    showControlTool();
                }
            }
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        ActivityUtils.remove(FrtcMeetingActivity.this);
        FrtcMeetingStatus meetingStatus = frtcCall.getMeetingStatus();
        if (meetingStatus != FrtcMeetingStatus.DISCONNECTING && meetingStatus != FrtcMeetingStatus.DISCONNECTED && !meeting_canceled
            && !localStore.isShowCallFloat()) {
            if(frtcCall != null) {
                if (isSharingContent) {
                    frtcCall.stopSendContent();
                }
                frtcCall.leaveMeeting();
            }
        }
        if (frtcManagement != null) {
            frtcManagement.unRegisterManagementListener(this);
        }
        stopBackgroundProcessNoticeService();
        if(frtcCall != null) {
            frtcCall.unRegisterCallListener(this);
            frtcCall.unregisterCommonListener(this);
        }
        if(orientationEventListener != null){
            orientationEventListener.disable();
        }
        if(screenListener != null){
            screenListener.stop();
        }
        if(!localStore.isShowCallFloat()) {
            if (frtcCall != null) {
                frtcCall.closeMicrophoneAudio();
                frtcCall.unregisterOrientationEventListener();
            }
            dismissCallFloatView();
        }
        super.onDestroy();
    }


    public void sendMeetingPassword(String meetingPw) {
        localStore.setMeetingPassword(meetingPw);
        frtcCall.setMeetingPassword(meetingPw);
    }


    protected void showMeetingView() {
        frtcFragmentManager.switchToConnectedFragment();
        meetingControlBar.startChronometer();
        localStore.setElapsedRealtime(SystemClock.elapsedRealtime());
        resetCleanMode();
    }

    private void disconnectingWithDelay(MeetingErrorCode errorCode) {
        if(isReconnecting && reconnectCount <=3){
            if(reconnectingView.getVisibility() == View.GONE) {
                reconnectingView.setVisibility(View.VISIBLE);
            }
            long delayMillis = 0;
            if(reconnectCount == 2){
                delayMillis = 10000;
            }else if(reconnectCount == 3){
                delayMillis = 20000;
            }
            if (handler != null) {
                handler.postDelayed(reconnect_runnable, delayMillis);
            }

        }else {
            if(reconnectCount > 3){
                if(reconnectingView.getVisibility() == View.VISIBLE) {
                    reconnectingView.setVisibility(View.GONE);
                }
                showReconnectConfirmDlg();
                return;
            }
            switch (errorCode) {
                case AuthenticationFail:
                    showJoinMeetingUnauthorizedInformDlg(errorCode.getCode());
                    break;
                case ServerError:
                    showConnectionFailInformDlg();
                    break;
                case MeetingNoExist:
                    showJoinMeetingFailedInform();
                    break;
                case MeetingLocked:
                    showMeetingLockedInformDlg();
                    break;
                case MeetingStop:
                    showMeetingStopInformDlg();
                    break;
                case RemovedFromMeeting:
                    showRemovedFromMeetingInformDlg();
                    break;
                case PasscodeTooManyRetries:
                    handlePasswordTooManyRetries();
                    break;
                case MeetingExpired:
                    showMeetingExpiredInformDlg();
                    break;
                case MeetingNoStarted:
                    showMeetingNotStartedInformDlg();
                    break;
                case GuestUnallowed:
                    showGuestUnAllowedInformDlg();
                    break;
                case MeetingFull:
                    showMeetingFullInformDlg();
                    break;
                case LicenseNoFound:
                    showJoinMeetingNoLicenseInformDlg(getString(R.string.meeting_failed_dialog_title));
                    break;
                case LicenseLimitReached:
                    showJoinMeetingLicenseMaxLimitReachedInformDlg(getString(R.string.meeting_failed_dialog_title),
                            getString(R.string.license_max_reached_dialog_content));
                    break;
                default:
                    if (handler != null) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                leaveMeeting();
                            }
                        }, ViewConstant.FINISH_DELAYED);
                    }
            }
        }
    }

    private void leaveMeeting(){
        Log.d(TAG,"leaveMeeting:");
        if (meetingControlBar != null) {
            meetingControlBar.stopChronometer();
        }

        if(isSharingContent){
            meetingLayout.stopSendContent();
        }

        cleanMessageHandler();

        if(localStore.isLogged()){
            addMeetingCallToHistoryList();
        }

        localStore.clearMeetingInfo();
        LocalStoreBuilder.getInstance(getApplicationContext()).setLocalStore(localStore);

        frtcFragmentManager.removeAllFragment();

        if(floatView != null){
            dismissCallFloatView();
        }
        localStore.setShowCallFloat(false);
        localStore.setSharingContent(false);
        localStore.setRemoteVideoMuted(false);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void showJoinMeetingUnauthorizedInformDlg(final int callEndReason){
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                getString(R.string.meeting_failed_dialog_title),
                getString(R.string.user_log_out_dialog_content),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                Intent intent = new Intent();
                intent.putExtra(MEETING_END_CODE, callEndReason);
                setResult(Activity.RESULT_OK, intent);
                leaveMeeting();
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        frtcFragmentManager.pause();
    }

    private void showConnectionFailInformDlg(){
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                getString(R.string.connection_failed_dialog_title),
                getString(R.string.network_unavailable_notice),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                leaveMeeting();
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void showJoinMeetingFailedInform(){
        BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.meeting_not_exist_notice), Toast.LENGTH_SHORT);

        if (handler != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    leaveMeeting();
                }
            }, ViewConstant.NOTICE_DELAY);
        }
    }

    private void showMeetingStopInformDlg(){
        if (isLocalAction) {
            leaveMeeting();
            return;
        }
        Constants.SdkType sdkType = localStore.getSdkType();
        if(endMeetingInformDlg == null ) {
            endMeetingInformDlg = new MeetingInformDlg(this,
                    getString(R.string.meeting_end_dialog_title),
                    getString(R.string.meeting_end_dialog_content_end),
                    getString(R.string.meeting_dialog_positive_btn));
            endMeetingInformDlg.setConfirmDlgListener(new IConfirmDlgListener() {
                @Override
                public void onConfirm() {
                    leaveMeeting();
                    if(sdkType != Constants.SdkType.SDK_TYPE_SQ) {
                        handler.removeCallbacks(dismissDlgRunnable);
                    }
                }

                @Override
                public void onCancel() {

                }
            });
            endMeetingInformDlg.show();
        }else if(!endMeetingInformDlg.isShowing()){
            endMeetingInformDlg.show();
        }
        if(floatView != null && sdkType == Constants.SdkType.SDK_TYPE_SQ){
            dismissCallFloatView();
        }
        if(sdkType != Constants.SdkType.SDK_TYPE_SQ) {
            if (handler != null) {
                handler.postDelayed(dismissDlgRunnable, 3000);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        frtcFragmentManager.resume();
        if(isSharingContent){
            frtcCall.resume(true, remote_video_muted);
        } else {
            frtcCall.resume(video_muted, remote_video_muted);
            if(needRecoverVideoMuteState){
                frtcCall.setLocalPeoplePreview(isEnabledLocalPeoplePreview);
                meetingControlBar.setCameraMuteState(video_muted);
                meetingControlBar.setVideoMute(video_muted);
                if (isLocalParticipantInfoAvailable()) {
                    participantInfos.get(0).setMuteVideo(video_muted);
                }
                needRecoverVideoMuteState = false;
            }
        }
        if(isBackFromPhoneSetting){
            isBackFromPhoneSetting = false;
        }else{
            dismissCallFloatView();
        }
    }

    private void showRemovedFromMeetingInformDlg(){
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                getString(R.string.meeting_end_dialog_title),
                getString(R.string.meeting_end_dialog_content_out),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                leaveMeeting();
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
        if(floatView != null){
            dismissCallFloatView();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
    }

    private void handlePasswordTooManyRetries(){
        Bundle bundle = new Bundle();
        bundle.putString(FrtcFragmentManager.MESSAGE, getString(R.string.password_dialog_multiple_times_error));
        frtcFragmentManager.switchToDisconnectingFragment(bundle);

        if (handler != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    leaveMeeting();
                }
            }, ViewConstant.NOTICE_DELAY);
        }

    }

    private void showMeetingLockedInformDlg(){
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                getString(R.string.meeting_failed_dialog_title),
                getString(R.string.meeting_locked_dialog_content),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                leaveMeeting();
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void showMeetingExpiredInformDlg(){
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                getString(R.string.meeting_failed_dialog_title),
                getString(R.string.meeting_expired_dialog_content),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                leaveMeeting();
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void showMeetingNotStartedInformDlg(){
        MessageDlg messageDlg = new MessageDlg(this,
                getString(R.string.meeting_not_started_dialog_content),
                getString(R.string.meeting_dialog_positive_btn));

        messageDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                leaveMeeting();
            }
            @Override
            public void onCancel(){

            }
        });
        messageDlg.show();
    }

    private void showGuestUnAllowedInformDlg(){
        MessageDlg messageDlg = new MessageDlg(this,
                getString(R.string.guest_not_allowed_dialog_content),
                getString(R.string.meeting_dialog_positive_btn));

        messageDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                leaveMeeting();
            }
            @Override
            public void onCancel(){

            }
        });
        messageDlg.show();
    }

    private void showMeetingFullInformDlg(){
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                getString(R.string.meeting_failed_dialog_title),
                getString(R.string.meeting_full_dialog_content),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                leaveMeeting();
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void showJoinMeetingNoLicenseInformDlg(String title){
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                title,
                getString(R.string.no_license_dialog_content),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                leaveMeeting();
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void showNoLicenseInformDlg(String title){
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                title,
                getString(R.string.no_license_dialog_content),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {

            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void showJoinMeetingLicenseMaxLimitReachedInformDlg(String title, String content){
        MeetingInformDlg informDlg = new MeetingInformDlg(this,title, content,
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                leaveMeeting();
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void showLicenseMaxLimitReachedInformDlg(String title, String content){
        MeetingInformDlg informDlg = new MeetingInformDlg(this,title, content,
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {

            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void doStartContent(){
        if(shareContentController != null){
            stopBackgroundProcessNoticeService();
            startShareScreenNoticeService();

            MediaProjectionManager mediaProjectionManager = shareContentController.getMediaProjectionManager();
            Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, ViewConstant.SYSTEM_SHARE_SCREEN_REQUEST_CODE);
        }
    }

    private void startShareContent(){
        if(checkFloatPermission()){
            doStartContent();
        }else{
            requestOverlayPermissionForShareContent = true;
            requestOverlaysPermission();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ViewConstant.SYSTEM_SHARE_SCREEN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (shareContentController != null && !isSharingContent) {
                    isSharingContent = true;
                    shareContentController.createMediaProduction(resultCode, data);
                    frtcCall.startSendContent();
                    meetingControlBar.setShareContentState(isSharingContent);
                    setVideoMuteDisabled();
                    if(isFloatWindow){
                        setFloatMode();
                        isFloatWindow = false;
                    }
                    setScreenListener();
                }
            } else {
                stopShareScreenNoticeService();
                startBackgroundProcessNoticeService();
            }
        } else if(requestCode == ViewConstant.SYSTEM_OVERLAY_REQUEST_CODE){
            if(requestOverlayPermissionForFloatMode){
                isBackFromPhoneSetting = true;
                if(checkFloatPermission()){
                    showCallFloatView();
                }else{
                    isBackFromPhoneSetting =false;
                }
                requestOverlayPermissionForFloatMode = false;
            }
            if(requestOverlayPermissionForShareContent){
                if(checkFloatPermission()){
                    doStartContent();
                }
                requestOverlayPermissionForShareContent = false;
            }
        }

    }

    private void setScreenListener(){
        screenListener = new ScreenListener(FrtcMeetingActivity.this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {

            }
            @Override
            public void onScreenOff() {
                if (isSharingContent) {
                    stopShareContent();
                    if(floatView != null){
                        floatView.modifyCallFloat();
                    }
                }
            }

            @Override
            public void onUserPresent() {

            }
        });
    }

    private void setVideoMuteDisabled(){
        video_mute_disabled = true;
        localStore.setVideoMuteDisabled(video_mute_disabled);
        frtcCall.muteLocalVideo(true);
        frtcCall.setLocalPeoplePreview(false);
        meetingControlBar.setCameraMuteState(true);
        meetingControlBar.setVideoMute(true);
        meetingControlBar.enableVideoMuted(false);
        if (isLocalParticipantInfoAvailable()) {
            participantInfos.get(0).setMuteVideo(true);
        }
    }

    private void stopShareContent(){
        frtcCall.stopSendContent();
    }

    private void startShareScreenNoticeService(){
        startService(new Intent(this, ShareScreenNoticeService.class));
    }

    private void stopShareScreenNoticeService(){
        stopService(new Intent(this, ShareScreenNoticeService.class));
    }

    private void startBackgroundProcessNoticeService(){
        startService(new Intent(this, BackgroundProcessNoticeService.class));
    }

    private void stopBackgroundProcessNoticeService(){
        stopService(new Intent(this, BackgroundProcessNoticeService.class));
    }

    private void sendRecordRequest(boolean isRecording){
        CommonMeetingParam recordingParam = new CommonMeetingParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        recordingParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        recordingParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        recordingParam.setMeetingNumber(localStore.getMeetingID());
        if(isRecording){
            frtcManagement.stopRecording(recordingParam);
        }else {
            frtcManagement.startRecording(recordingParam);
        }
    }

    private void sendLiveRequest(boolean isLiving){
        LiveMeetingParam liveParam = new LiveMeetingParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this).getLocalStore();
        }
        liveParam.setClientId(localStore.getClientId());
        liveParam.setToken(localStore.getUserToken());
        liveParam.setMeetingNumber(localStore.getMeetingID());
        if(isLiving){
            frtcManagement.stopLive(liveParam);
        }else {
            liveParam.setLive_password(localStore.getLivePassword());
            frtcManagement.startLive(liveParam);
        }
    }

    private void showRequestUnmuteInformDlg() {
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                getString(R.string.unmute_self_requested_dlg_title),
                getString(R.string.unmute_self_requested_dlg_content),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {

            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void showRequestUnmuteDlg() {
        ConfirmDlg requestUnmuteDlg = new ConfirmDlg(this,
                getString(R.string.unmute_self_dlg_title),
                getString(R.string.unmute_self_dlg_content),
                getString(R.string.meeting_dialog_negative_btn),
                getString(R.string.unmute_self_dlg_positive_btn));

        requestUnmuteDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                requestSelfUnmute();
            }
            @Override
            public void onCancel(){

            }
        });
        requestUnmuteDlg.show();
    }

    private void requestSelfUnmute() {
        CommonMeetingParam commonMeetingParam = new CommonMeetingParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        commonMeetingParam.setClientId(localStore.getClientId());
        commonMeetingParam.setMeetingNumber(localStore.getMeetingID());
        if(isCrossServer){
            commonMeetingParam.setServerAddress(crossServerAddr);
        }else{
            commonMeetingParam.setServerAddress(localStore.getServer());
            commonMeetingParam.setToken(localStore.getUserToken());
        }
        if(frtcManagement != null){
            frtcManagement.unMuteSelf(commonMeetingParam);
        }
    }

    private void initOrientationEventListener(){
        orientationEventListener = new OrientationEventListener(this,SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int rotation = display.getRotation();
                if (cur_rotation != rotation) {
                    frtcCall.screenOrientationChanged(rotation);
                    cur_rotation = rotation;
                }
            }
        };

        if(orientationEventListener.canDetectOrientation()){
            orientationEventListener.enable();
        }else{
            orientationEventListener.disable();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LanguageUtil.setLanguage(this);

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        meetingLayout.resetLayoutView(width, height, video_muted);

        meetingControlBar.setCallTitleBar(getResources().getConfiguration().orientation);

        if(textOverlayManager != null){
            textOverlayManager.onOrientationChanged(width,height,display.getRotation());
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (KeyEvent.ACTION_DOWN != keyEvent.getAction()) {
            return super.dispatchKeyEvent(keyEvent);
        }
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ENDCALL:
                FrtcMeetingStatus state = frtcCall.getMeetingStatus();
                if(FrtcMeetingStatus.CONNECTING != state) {
                    showLeaveMeetingConfirmDlg();
                }
                return true;
            case KeyEvent.KEYCODE_DEL:
                Fragment curFragment = frtcFragmentManager.getCurFragment();
                if (curFragment instanceof PasswordInputFragment) {
                    PasswordInputFragment passwordInputFragment = (PasswordInputFragment) curFragment;
                    if (KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
                        passwordInputFragment.deleteOne();
                    }
                }
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(keyEvent);
    }


    private void resetCleanMode() {
        if (frtcFragmentManager.getCurFragment() != null) {
            return;
        }
        if (!clean_mode) {
            cleanMessageHandler();
            startCleanMode();
        } else {
            showControlTool();
        }
    }

    private void showControlTool() {
        meetingControlBar.setControlBarVisible(true);
        frtcCall.setVisibleMeetingSlideView(false);
        clean_mode = false;
        frtcCall.setVisibleOverlay(true);
        startCleanMode();
    }

    private void startCleanMode() {
        if (handler != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterCleanMode();
                }
            }, ViewConstant.CLEAN_MODE_DELAY);
        }
    }

    private void cleanMessageHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void enterCleanMode() {
        if (!clean_mode) {
            meetingControlBar.setControlBarVisible(false);
            meetingControlBar.hideMoreMenu();
            frtcCall.setVisibleMeetingSlideView(true);
            clean_mode = true;
            frtcCall.setVisibleOverlay(false);
        }
    }


    private void initShareContentController(){
        if(frtcCall.getMeetingStatus() == FrtcMeetingStatus.CONNECTED) {
            shareContentController = frtcCall.getLocalContentController();
        }else {
            shareContentController = new ShareContentController(this.getApplicationContext());
            frtcCall.registerOrientationEventListener();
        }
        shareContentController.setShareContentStatusListener(new ShareContentController.ShareContentStatusListener() {
            @Override
            public void onStopped() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isSharingContent){
                            showContentStoppedStatus();
                            shareContentController.setIsSQMeetingSelfStop(false);
                        }
                    }
                });
            }

            @Override
            public void onRefused(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isSharingContent){
                            showContentRefusedStatus();
                        }
                    }
                });
            }

            @Override
            public void toStop(){
                if(isSharingContent){
                    frtcCall.stopSendContent();
                }
            }
        });
    }

    private void initializeMeetingLayout() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.meeting_activity);
        meetingLayout = findViewById(R.id.meeting_activity);
        stage = findViewById(R.id.stage);
        layoutGroup = stage.findViewById(R.id.meeting_layout);
        ImageView firstPage = findViewById(R.id.first_page);
        ImageView secondPage = findViewById(R.id.second_page);
        RelativeLayout slideView = findViewById(R.id.page_number_layout);
        reconnectingView = findViewById(R.id.reconnecting_view);
        frtcCall.initMeetingUI(meetingLayout, stage, layoutGroup, firstPage, secondPage, slideView);
        meetingLayout.setLocalContentController(shareContentController);
    }

    private void initLiveRecordingMenu(){
        RelativeLayout controlBarView = findViewById(R.id.liveRecordingMenu);
        liveRecordingMenu = new LiveRecordingMenu(this, controlBarView,
                isOperatorOrAdmin(),isHost());

        liveRecordingMenu.setLivaRecordingMenuListener(new LiveRecordingMenu.LiveRecordingMenuListener() {
            @Override
            public void onShareLive() {
                showShareInfoDlg();
            }

            @Override
            public void onLiveStop() {
                showStopLiveDlg();
            }

            @Override
            public void onRecordingStop() {
                showStopRecordDlg();
            }
        });
    }

    public void setLiveMenuVisibility(boolean show){
        if(meetingControlBar != null){
            meetingControlBar.setLiveState(isOperatorOrAdmin() && show && isLiveStarted);
        }

        if(liveRecordingMenu != null){
            liveRecordingMenu.setLiveStatus(show);
        }
    }

    public void setRecordingMenuVisibility(boolean show){
        if(meetingControlBar != null){
            meetingControlBar.setRecordState(isOperatorOrAdmin() && show && isRecordingStarted);
        }
        if(liveRecordingMenu != null){
            liveRecordingMenu.setRecordingStatus(show);
        }
    }

    private void showShareInfoDlg() {
        ShareLiveInfoDlg shareLiveInfoDlg = new ShareLiveInfoDlg(this);
        shareLiveInfoDlg.show();
    }

    public void autoLeaveMeeting() {
        meeting_canceled = true;

        if(isSharingContent){
            frtcCall.stopSendContent();
        }
        frtcCall.leaveMeeting();
        disconnectingWithDelay(MeetingErrorCode.HangUp);
    }

    public boolean isMeetingNotResponse(){
        FrtcMeetingStatus meetingStatus = frtcCall.getMeetingStatus();
        if ((meetingStatus == FrtcMeetingStatus.DISCONNECTING) || (meetingStatus == FrtcMeetingStatus.DISCONNECTED)) {
            return true;
        }
        return false;
    }

    private IControlBarRequestListener initControlBarRequestListener(){

        IControlBarRequestListener listener = new IControlBarRequestListener() {

            @Override
            public void onEndMeeting() {
                showLeaveMeetingConfirmDlg();
            }

            @Override
            public void onSwitchCamera() {
                onCameraSwitch();
            }

            @Override
            public void onSwitchSpeaker() {
                switchSpeaker();
            }

            @Override
            public void onShowMeetingDetails() {
                showMeetingDetails();
            }

            @Override
            public void onMuteAudio() {
                muteAudio();
            }

            @Override
            public void onMuteVideo() {
                muteVideo();
            }

            @Override
            public void onShowParticipants() {
                showParticipantsDlg();
            }

            @Override
            public void onShareContent() {
                shareContent();
            }

            @Override
            public void onShowInviteInfo() {
                showInvitationInfoDlg();
            }

            @Override
            public void onStartOverlay() {
                showStartOverlayDlg();
            }

            @Override
            public void onStopOverlay() {
                stopOverlay();
            }

            @Override
            public void onLive() {
                live();
            }

            @Override
            public void onRecord() {
                record();
            }

            @Override
            public void onMuteRemote() {
                muteRemoteVideo();
            }

            @Override
            public void onFloatMode() {
                setFloatMode();
            }
        };

        return listener;
    }

    private void showLeaveMeetingConfirmDlg() {
        EndMeetingDialog dialog = new EndMeetingDialog(this, isHost());
        dialog.setOnDialogListener(this);
        dialog.show();
    }

    private void onCameraSwitch(){
        frtcCall.switchFrontOrBackCamera();
    }

    private void switchSpeaker(){
        frtcCall.switchSpeaker();
    }

    public void showMeetingDetails(){
        if(meetingDetailsDlg == null){
            meetingDetailsDlg = new MeetingDetailsDlg(this, this);
        }
        meetingDetailsDlg.show();
    }

    private void muteAudio() {
        if(audio_muted && frtcCall.isAudioDisabled() && !isHost()){
            if(isRequestUnmuteSelf && (System.currentTimeMillis() - reqUnmuteTimestamp < 60000)){
                showRequestUnmuteInformDlg();
                return;
            }
            showRequestUnmuteDlg();
            return;
        }
        audio_muted = !audio_muted;
        localStore.setAudioOn(!audio_muted);
        meetingControlBar.setAudioMute(audio_muted);
        frtcCall.muteLocalAudio(audio_muted);
        if(audio_muted){
            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.muted_notice), Toast.LENGTH_SHORT);
        }
        if (isLocalParticipantInfoAvailable()) {
            participantInfos.get(0).setMuteAudio(audio_muted ? true: false);
        }
    }

    private void muteVideo() {
        video_muted = !video_muted;
        isEnabledLocalPeoplePreview = !video_muted;
        localStore.setCameraOn(!video_muted);

        meetingControlBar.setVideoMute(video_muted);
        meetingControlBar.setCameraMuteState(video_muted);

        frtcCall.muteLocalVideo(video_muted);
        frtcCall.setLocalPeoplePreview(isEnabledLocalPeoplePreview);

        if (isLocalParticipantInfoAvailable()) {
            participantInfos.get(0).setMuteVideo(video_muted);
        }
    }

    private void showParticipantsDlg() {
        participantsDlg = new ParticipantsDlg(this, participantInfos, isHost());
        participantsDlg.setParticipantListListener(this);
        participantsDlg.show();
        if(unmuteRequestParticipants != null && unmuteRequestParticipants.size() > 0) {
            participantsDlg.setllUnmuteReqVisible(unmuteRequestParticipants.get(0).getDisplay_name(), true, meetingControlBar.getRequestNotifyVisible());
        }
    }

    private boolean isLocalParticipantInfoAvailable(){
        return participantInfos != null && participantInfos.size() > 0
                && participantInfos.get(0) != null;
    }

    private void updateParticipantDlg(List<ParticipantInfo> participantInfos){
        if(participantsDlg != null && participantsDlg.isShowing() && participantInfos != null){
            participantsDlg.updateParticipantInfos(participantInfos);
        }
    }

    private void shareContent(){
        if(isSharingContent){
            stopShareContent();
        }else{
            startShareContent();
        }
    }

    private void showInvitationInfoDlg(){
        InvitationInformationDlg invitationInformationDlg = new InvitationInformationDlg(this,this, null);
        invitationInformationDlg.show();
    }

    private void showStartOverlayDlg() {
        StartOverlayDlg startOverlayDlg = new StartOverlayDlg(this);
        startOverlayDlg.setStartOverlayListener(this);
        startOverlayDlg.show();
    }

    private void stopOverlay(){
        StopOverlayParam stopOverlayParam = new StopOverlayParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        stopOverlayParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        stopOverlayParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        stopOverlayParam.setMeetingNumber(localStore.getMeetingID());
        removeEventsAndEnterCleanMode();
        frtcManagement.stopOverlay(stopOverlayParam);
    }

    private void live(){
        if(isOperatorOrAdmin()) {
            showLiveDlg(isLiveStarted);
        }else {
            showLiveRefusedDlg();
        }
    }

    private void showLiveDlg(boolean isLiving) {
        if(isLiving){
            showStopLiveDlg();
        }else{
            showStartLiveDlg();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean isShowCallFloatView = (floatView == null) ? false : true;
        frtcCall.stop(isShowCallFloatView);
    }

    private void showStopLiveDlg() {
        ConfirmDlg confirmDlg = new ConfirmDlg(this,
                "",
                getString(R.string.stop_live_dlg_content),
                getString(R.string.meeting_dialog_negative_btn),
                getString(R.string.stop_live_dlg_ok_btn));

        confirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                sendLiveRequest(true);
            }
            @Override
            public void onCancel(){

            }
        });
        confirmDlg.show();
    }

    private void showStartLiveDlg() {
        LiveRecordDlg liveRecordDlg = new LiveRecordDlg(this,
                getString(R.string.start_live_dlg_title),
                getString(R.string.start_live_dlg_content),
                getString(R.string.meeting_dialog_negative_btn),
                getString(R.string.start_live_dlg_ok_btn));
        liveRecordDlg.setCheckVisible(true);
        liveRecordDlg.setStartLiveListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                sendLiveRequest(false);
            }
            @Override
            public void onCancel(){

            }
        });
        liveRecordDlg.show();
    }

    private void showLiveRefusedDlg() {
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                getString(R.string.user_log_out_dialog_title),
                getString(R.string.user_log_out_dialog_content),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {

            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void record(){
        if(isHost()) {
            showRecordDlg(isRecordingStarted);
        }else{
            showRecordRefusedDlg();
        }
    }

    private void showRecordDlg(boolean isRecording) {
        if(isRecording){
            showStopRecordDlg();
        }else{
            showStartRecordDlg();
        }
    }

    private void showStopRecordDlg() {
        LiveRecordDlg liveRecordDlg = new LiveRecordDlg(this,
                getString(R.string.stop_record_dlg_title),
                getString(R.string.stop_record_dlg_content),
                getString(R.string.meeting_dialog_negative_btn),
                getString(R.string.stop_record_dlg_ok_btn));
        liveRecordDlg.setStartLiveListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                sendRecordRequest(true);
            }
            @Override
            public void onCancel(){

            }
        });
        liveRecordDlg.show();
    }


    private void showStartRecordDlg() {
        LiveRecordDlg liveRecordDlg = new LiveRecordDlg(this,
                getString(R.string.start_record_dlg_title),
                getString(R.string.start_record_dlg_content),
                getString(R.string.meeting_dialog_negative_btn),
                getString(R.string.start_record_dlg_ok_btn));
        liveRecordDlg.setStartLiveListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                sendRecordRequest(false);
            }
            @Override
            public void onCancel(){

            }
        });
        liveRecordDlg.show();
    }

    private void showRecordRefusedDlg() {
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                getString(R.string.user_log_out_dialog_title),
                getString(R.string.user_log_out_dialog_content),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {

            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void showConnectingView() {
       String number = "";
       if (localStore != null) {
           number = localStore.getMeetingID();
       }
       Bundle bundle = new Bundle();
       bundle.putString("number", number);
       frtcFragmentManager.switchToConnectingFragment(bundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void muteRemoteVideo(){
        remote_video_muted = !remote_video_muted;
        meetingControlBar.setRemoteVideoMute(remote_video_muted);
        frtcCall.muteRemoteVideo(remote_video_muted);
        localStore.setRemoteVideoMuted(remote_video_muted);

        if(remote_video_muted){
            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.mute_remote_video_notice), Toast.LENGTH_SHORT);
        }else{
            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.unmute_remote_video_notice), Toast.LENGTH_SHORT);
        }
    }

    private void setFloatMode(){
        if(checkFloatPermission()){
            showCallFloatView();
        }else{
            requestOverlayPermissionForFloatMode = true;
            requestOverlaysPermission();
        }
    }

    public void showMeetingStatsDlg(){
        if(meetingMediaStatsDlg == null){
            meetingMediaStatsDlg = new MeetingMediaStatsDlg(this, localStore.getMeetingID(),localStore.getMeetingName());
        }
        meetingMediaStatsDlg.show();
        meetingMediaStatsDlg.updateMeetingStatsData(meetingMediaStatsInfo);
    }

    private void removeEventsAndEnterCleanMode() {
        cleanMessageHandler();
        enterCleanMode();
    }

    public void onLeaveMeeting() {
        Log.d(TAG,"onLeaveMeeting");
        removeEventsAndEnterCleanMode();
        if(isSharingContent){
            frtcCall.stopSendContent();
        }
        frtcCall.leaveMeeting();
    }

    public void onEndMeeting(){
        StopMeetingParam stopMeetingParam = new StopMeetingParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        stopMeetingParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        stopMeetingParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        stopMeetingParam.setMeetingNumber(localStore.getMeetingID());
        removeEventsAndEnterCleanMode();
        frtcManagement.stopMeeting(stopMeetingParam);
        if(localStore.isLogged()){
            addMeetingCallToHistoryList();
        }
        localStore.clearMeetingInfo();
        LocalStoreBuilder.getInstance(this).setLocalStore(localStore);
        isLocalAction = true;
    }

    @Override
    public void onClickConfirmMuteAll(boolean isAllowUnmute) {
        MuteAllParam muteAllParam = new MuteAllParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        muteAllParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        muteAllParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        muteAllParam.setAllow_self_unmute(isAllowUnmute);
        muteAllParam.setMeetingNumber(localStore.getMeetingID());
        if (frtcManagement != null) {
            frtcManagement.muteAllParticipant(muteAllParam);

            if(audio_muted){
                audio_muted = false;
                localStore.setAudioOn(!audio_muted);
                frtcCall.muteLocalAudio(false);

                meetingControlBar.setAudioMute(false);

                if (isLocalParticipantInfoAvailable()) {
                    participantInfos.get(0).setMuteAudio(false);
                    updateParticipantDlg(participantInfos);
                }
            }
        }
    }

    @Override
    public void onClickConfirmUnmuteAll() {
        UnMuteAllParam unMuteAllParam = new UnMuteAllParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        unMuteAllParam.setMeetingNumber(localStore.getMeetingID());
        unMuteAllParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        unMuteAllParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        if(frtcManagement != null){
            frtcManagement.unMuteAllParticipant(unMuteAllParam);

            if(audio_muted){
                audio_muted = false;
                localStore.setAudioOn(!audio_muted);
                frtcCall.muteLocalAudio(false);

                meetingControlBar.setAudioMute(false);
                if (isLocalParticipantInfoAvailable()) {
                    participantInfos.get(0).setMuteAudio(false);
                    updateParticipantDlg(participantInfos);
                }
            }
        }

    }

    @Override
    public void onClickConfirmMute(List<String> participants) {
        MuteParam muteParam = new MuteParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        muteParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        muteParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        muteParam.setMeetingNumber(localStore.getMeetingID());
        muteParam.setAllowUnMute(isAllowUnmute);
        muteParam.setParticipants(participants);
        if(frtcManagement != null){
            frtcManagement.muteParticipant(muteParam);
        }
    }

    @Override
    public void onClickConfirmUnMute(List<String> participants) {
        if(audio_disabled && !isHost()){
            showRequestUnmuteDlg();
            return;
        }
        UnMuteParam unMuteParam = new UnMuteParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        unMuteParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        unMuteParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        unMuteParam.setMeetingNumber(localStore.getMeetingID());
        unMuteParam.setParticipants(participants);
        if(frtcManagement != null){
            frtcManagement.unMuteParticipant(unMuteParam);
        }
    }

    @Override
    public void onClickAudio() {
        muteAudio();
        updateParticipantDlg(participantInfos);
    }

    @Override
    public void onChangeDisplayNameInParticipants(ParticipantInfo participantInfo) {
        ChangeDisplayNameDlg dlg = new ChangeDisplayNameDlg(this, participantInfo);

        dlg.setOnDialogListener(this);
        dlg.show();
    }

    @Override
    public void onClickConfirmChangeName(String uuid, String displayName) {
        ChangeDisplayNameParam param = new ChangeDisplayNameParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        param.setClientId(localStore.getClientId());
        if(!isCrossServer) {
            param.setToken(localStore.getUserToken());
        }
        param.setMeetingNumber(localStore.getMeetingID());
        param.setClient_id(uuid);
        param.setDisplay_name(displayName);
        if(isCrossServer){
            param.setServerAddress(crossServerAddr);
        }else{
            param.setServerAddress(localStore.getServer());
        }
        if(frtcManagement != null){
            frtcManagement.changeNameParticipant(param);
        }
    }

    @Override
    public void onStartOverlay(String content,int repeat,int position,boolean scroll){
        StartOverlayParam startOverlayParam = new StartOverlayParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        startOverlayParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        startOverlayParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        startOverlayParam.setMeetingNumber(localStore.getMeetingID());
        startOverlayParam.setContent(content);
        startOverlayParam.setRepeat(repeat);
        startOverlayParam.setPosition(position);
        startOverlayParam.setScroll(scroll);
        removeEventsAndEnterCleanMode();
        frtcManagement.startOverlay(startOverlayParam);
    }

    @Override
    public void onClickConfirmLecturer(String uuid) {
        LectureParam lectureParam = new LectureParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        lectureParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        lectureParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        lectureParam.setMeetingNumber(localStore.getMeetingID());
        if(!uuid.equals(lecturerUuid)) {
            lectureParam.setLecturer(uuid);
        }
        if(frtcManagement != null){
            frtcManagement.lectureParticipant(lectureParam);
        }
    }

    private void onClickDisconnectParticipant(String uuid){
        DisconnectParticipantParam disconnectParticipantParam = new DisconnectParticipantParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        disconnectParticipantParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        disconnectParticipantParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        disconnectParticipantParam.setMeetingNumber(localStore.getMeetingID());
        List<String> participants = new ArrayList<>();
        participants.add(uuid);
        disconnectParticipantParam.setParticipants(participants);
        frtcManagement.disconnectParticipant(disconnectParticipantParam);
    }

    @Override
    public void onDisconnectParticipant(final String uuid){
        ConfirmDlg disconnectParticipantDlg = new ConfirmDlg(this,
                getString(R.string.meeting_disconnect_participants_dialog_title),
                getString(R.string.meeting_disconnect_participants_dialog_content),
                getString(R.string.meeting_dialog_negative_btn),
                getString(R.string.meeting_dialog_positive_btn));

        disconnectParticipantDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                onClickDisconnectParticipant(uuid);
            }
            @Override
            public void onCancel(){

            }
        });
        disconnectParticipantDlg.show();
    }

    @Override
    public void onPin(String pinUuid){
        if(!TextUtils.isEmpty(pinUuid)){
            PinForMeetingParam pinParam = new PinForMeetingParam();
            if(localStore == null){
                localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
            }
            pinParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
            pinParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
            pinParam.setMeetingNumber(localStore.getMeetingID());
            List<String> participants = new ArrayList<>();
            participants.add(pinUuid);
            pinParam.setParticipants(participants);
            frtcManagement.pinForMeeting(pinParam);
        }
    }

    @Override
    public void onUnpin(String pinUuid){
        if(!TextUtils.isEmpty(pinnedUuid) && pinUuid.equals(pinnedUuid)){
            CommonMeetingParam unpinParam = new CommonMeetingParam();
            if(localStore == null){
                localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
            }
            unpinParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
            unpinParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
            unpinParam.setMeetingNumber(localStore.getMeetingID());
            frtcManagement.unpinForMeeting(unpinParam);
        }
    }

    private boolean isHost(){
        if(isCrossServer){
            return false;
        }
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        return localStore.isHost();
    }

    private boolean isOperatorOrAdmin(){
        if(isCrossServer){
            return false;
        }
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        return localStore.isOperatorOrAdmin();
    }

    @Override
    public void onMeetingPasswordErrorNotify(){
        meeting_canceled = true;
        disconnectingWithDelay(MeetingErrorCode.PasscodeTooManyRetries);
    }

    @Override
    public void onMeetingPasswordRequestNotify() {
        localStore.setMeetingPassword("");
        stage.setVisibility(View.GONE);
        frtcFragmentManager.switchToPasswordFragment();
    }

    @Override
    public void onLiveRecordStatusNotify(LiveRecordStatusNotify liveRecordStatusNotify){
        boolean liveStarted = liveRecordStatusNotify.isLiveStarted();
        boolean recordStarted = liveRecordStatusNotify.isRecordStarted();
        String liveMeetingUrl = liveRecordStatusNotify.getLiveMeetingUrl();

        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this).getLocalStore();
        }
        localStore.setLiveMeetingUrl(liveMeetingUrl);
        localStore.setLivePassword(liveRecordStatusNotify.getLiveMeetingPwd());

        if(this.isLiveStarted != liveStarted){
            this.isLiveStarted = liveStarted;
            setLiveMenuVisibility(liveStarted);
            if(liveStarted){
                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.start_live_notice), Toast.LENGTH_SHORT);
            }else{
                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.stop_live_notice), Toast.LENGTH_SHORT);
            }
        }

        if(this.isRecordingStarted != recordStarted){
            this.isRecordingStarted = recordStarted;
            setRecordingMenuVisibility(recordStarted);
            if(recordStarted){
                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.start_recording_notice), Toast.LENGTH_SHORT);
                showRecordFileDlg();
            }else{
                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.stop_recording_notice), Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onUnmuteRequestNotify(UnmuteRequest unmuteRequest) {
        if(!TextUtils.isEmpty(unmuteRequest.getUuid())){
            for(UnmuteRequest unmuteReq : unmuteRequestParticipants){
                if(unmuteReq.getUuid().equals(unmuteRequest.getUuid())){
                    unmuteRequestParticipants.remove(unmuteReq);
                    break;
                }
            }
            unmuteRequestParticipants.add(0,unmuteRequest);
            showUnmuteRequestNotifyDlg(unmuteRequest);
            if(participantsDlg != null) {
                participantsDlg.setllUnmuteReqVisible(unmuteRequest.getDisplay_name(), true, true);
            }
            if(permissionsRequestDlg != null) {
                permissionsRequestDlg.refreshAdapter(unmuteRequestParticipants);
                permissionsRequestDlg.updateNoPermissionsVisible();
            }
            meetingControlBar.setRequestNotifyVisible(true);
        }
    }

    private void showUnmuteRequestNotifyDlg(UnmuteRequest unmuteRequest) {
        UnmuteRequestNotifyDlg unmuteRequestNotifyDlg = new UnmuteRequestNotifyDlg(this, unmuteRequest);
        unmuteRequestNotifyDlg.setUnmuteRequestNotifyListener(this);
        unmuteRequestNotifyDlg.show();
    }

    @Override
    public void onAllowUnmuteNotify() {
        isRequestUnmuteSelf = false;
        if(audio_muted) {
            showAllowUnmuteNotifyDlg();
        }
    }

    private void showAllowUnmuteNotifyDlg() {
        ConfirmDlg confirmDlg = new ConfirmDlg(this,
                getString(R.string.unmute_self_rsp_dlg_title),
                getString(R.string.unmute_self_rsp_dlg_content),
                getString(R.string.unmute_self_rsp_dlg_negative_btn),
                getString(R.string.unmute_self_rsp_dlg_positive_btn));

        confirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                isRequestUnmuteSelf = false;
                audio_disabled = false;
                frtcCall.setAudioDisabled(audio_disabled);

                audio_muted = false;
                localStore.setAudioOn(!audio_muted);
                meetingControlBar.setAudioMute(false);
                frtcCall.muteLocalAudio(false);
                if (isLocalParticipantInfoAvailable()) {
                    participantInfos.get(0).setMuteAudio(audio_muted);
                }

                audio_disabled = true;
                frtcCall.setAudioDisabled(audio_disabled);
                isShowDlg = false;
            }
            @Override
            public void onCancel(){
                isRequestUnmuteSelf = false;
                isShowDlg = false;
            }
        });
        if(!isShowDlg) {
            confirmDlg.show();
            isShowDlg = true;
        }
    }

    @Override
    public void onMeetingInfoNotify(MeetingStateInfoNotify meetingStateInfoNotify) {
        String meetingNumber = meetingStateInfoNotify.getMeetingNumber();
        String meetingName = meetingStateInfoNotify.getMeetingName();
        String ownerId = meetingStateInfoNotify.getOwnerId();
        String ownerName = meetingStateInfoNotify.getOwnerName();
        String meetingURL = meetingStateInfoNotify.getMeetingURL();
        long scheduleStartTime = meetingStateInfoNotify.getScheduleStartTime();
        long scheduleEndTime = meetingStateInfoNotify.getScheduleEndTime();

        meetingControlBar.setMeetingName(meetingName);
        isCrossServer = meetingStateInfoNotify.isCrossServer();

        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this).getLocalStore();
        }
        if(isCrossServer){
            crossServerAddr = meetingStateInfoNotify.getCrossSeverAddr();
        }else{
            crossServerAddr = "";
        }

        localStore.setMeetingID(meetingNumber);
        localStore.setMeetingName(meetingName);
        localStore.setMeetingOwnerId(ownerId);
        localStore.setMeetingOwnerName(ownerName);
        localStore.setScheduleStartTime(scheduleStartTime);
        localStore.setScheduleEndTime(scheduleEndTime);
        localStore.setMeetingURl(meetingURL);

        meetingControlBar.updateHostPermission(isHost(), isOperatorOrAdmin());
        if(liveRecordingMenu != null){
            liveRecordingMenu.updateHostPermission(isHost(),isOperatorOrAdmin());
        }
        if(meetingMediaStatsDlg != null){
            meetingMediaStatsDlg.setMeetingId(meetingNumber);
            meetingMediaStatsDlg.setMeetingName(meetingName);
        }
    }

    @Override
    public void onMeetingStateNotify(MeetingStateInfoNotify meetingStateInfoNotify){
        String meetingNumber = meetingStateInfoNotify.getMeetingNumber();
        String meetingStatus = meetingStateInfoNotify.getMeetingStatus().name();
        int callEndReason = meetingStateInfoNotify.getCallEndReason();
        reconnectCount = meetingStateInfoNotify.getReconnectCount();
        isReconnecting = meetingStateInfoNotify.isReconnecting();

        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this).getLocalStore();
        }
        localStore.setMeetingID(meetingNumber);
        Log.d(TAG,"onMeetingStateNotify:"+meetingStatus+",callEndReason:"+callEndReason);
        handleMeetingStateChangedNotify(meetingStatus, callEndReason);
    }

    @Override
    public void onTemperatureInfoNotify(boolean overheat) {

    }

    @Override
    public void onMediaStatsInfoNotify(MeetingMediaStatsWrapper stat, boolean isEncrypt) {
        if(meetingDetailsDlg != null && meetingDetailsDlg.isShowing()){
            meetingDetailsDlg.updateStatistics(stat);
        }

        this.meetingMediaStatsInfo = stat;
        if(meetingMediaStatsDlg != null && meetingMediaStatsDlg.isShowing()){
            meetingMediaStatsDlg.updateMeetingStatsData(stat);
        }
    }

    @Override
    public void onHeadsetEnableSpeakerNotify(boolean enableSpeaker) {
        if (meetingControlBar != null) {
            meetingControlBar.setSpeakerState(enableSpeaker);
        }
    }

    @Override
    public void onContentStateNotify(boolean isSharing){

    }

    private void showContentStoppedStatus(){
        stopShareScreenNoticeService();
        startBackgroundProcessNoticeService();

        if(screenListener != null){
            screenListener.stop();
            screenListener = null;
        }

        if(isForeground(FrtcMeetingActivity.this)){
            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.share_content_stop_notice), Toast.LENGTH_SHORT);
            if(floatView != null){
                floatView.modifyCallFloat();
            }
        }

        if(video_mute_disabled){
            video_mute_disabled = false;
            localStore.setVideoMuteDisabled(video_mute_disabled);
            meetingControlBar.enableVideoMuted(true);

            if(isForeground(FrtcMeetingActivity.this)){
                frtcCall.muteLocalVideo(video_muted);
                frtcCall.setLocalPeoplePreview(isEnabledLocalPeoplePreview);
                meetingControlBar.setCameraMuteState(video_muted);
                meetingControlBar.setVideoMute(video_muted);

                if (isLocalParticipantInfoAvailable()) {
                    participantInfos.get(0).setMuteVideo(video_muted);
                }
            }else{
                needRecoverVideoMuteState = true;
            }
        }

        isSharingContent = false;
        localStore.setSharingContent(false);
        meetingControlBar.setShareContentState(false);

    }

    private void showContentRefusedStatus(){
        stopShareScreenNoticeService();
        if(screenListener != null){
            screenListener.stop();
            screenListener = null;
        }
        BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.share_content_uplink_bitrate_low_notice), Toast.LENGTH_SHORT);
        if(video_mute_disabled){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    video_mute_disabled = false;
                    localStore.setVideoMuteDisabled(video_mute_disabled);
                    meetingControlBar.enableVideoMuted(true);
                    if(isForeground(FrtcMeetingActivity.this)){
                        frtcCall.muteLocalVideo(video_muted);
                        frtcCall.setLocalPeoplePreview(isEnabledLocalPeoplePreview);
                        meetingControlBar.setCameraMuteState(video_muted);
                        meetingControlBar.setVideoMute(video_muted);

                        if (isLocalParticipantInfoAvailable()) {
                            participantInfos.get(0).setMuteVideo(video_muted);
                        }
                    }else{
                        needRecoverVideoMuteState = true;
                    }
                }
            }, 200);
        }
        isSharingContent = false;
        localStore.setSharingContent(false);
        meetingControlBar.setShareContentState(false);
    }

    @Override
    public void onPinNotify(String pinUuid){
        if(pinUuid != null && !pinnedUuid.equals(pinUuid)){
            if(pinnedUuid.equals(localStore.getClientId())){
                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.unpinned_for_meeting_notice), Toast.LENGTH_SHORT);
            }
            if(pinUuid.equals(localStore.getClientId())){
                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.pinned_for_meeting_notice), Toast.LENGTH_SHORT);
            }
            this.pinnedUuid = pinUuid;
            int index = 0;
            if(participantInfos != null && !participantInfos.isEmpty()){
                for(ParticipantInfo participantInfo : participantInfos){
                    participantInfo.setPinned(!TextUtils.isEmpty(pinnedUuid) && pinnedUuid.equals(participantInfo.getUuid()));
                    if(participantInfo.isPinned()){
                        index = participantInfos.indexOf(participantInfo);
                    }
                }
                if(index > 0){
                    ParticipantInfo pinParticipant = participantInfos.remove(index);
                    participantInfos.add(1, pinParticipant);
                }
                updateParticipantDlg(participantInfos);
            }
        }
    }

    @Override
    public void onLayoutSettingNotify(LecturerInfo lecturerInfo) {
        if(!TextUtils.isEmpty(lecturerInfo.getUuid())){
            if(lecturerInfo.getUuid().equals(localStore.getClientId())){
                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.participant_dialog_lecture_content_noticed), Toast.LENGTH_SHORT);
            }
        }
        lecturerUuid = lecturerInfo.getUuid();
        int index = 0;
        if(participantInfos != null && !participantInfos.isEmpty()){
            for(ParticipantInfo participantInfo : participantInfos){
                participantInfo.setLecturer(!TextUtils.isEmpty(lecturerUuid) && lecturerUuid.equals(participantInfo.getUuid()));
                if(participantInfo.isLecturer()){
                    index = participantInfos.indexOf(participantInfo);
                }
            }
            if(index > 1){
                ParticipantInfo lecturer = participantInfos.remove(index);
                participantInfos.add(1, lecturer);
            }
            updateParticipantDlg(participantInfos);
        }
    }

    @Override
    public void onInviteUserNotify(List<frtc.sdk.internal.model.ParticipantInfo> participantInfos) {

    }

    @Override
    public void onParticipantStateChangeNotify(ParticipantStateChangeNotify participantStateChangeNotify) {
        boolean isFullList = participantStateChangeNotify.isFullList();
        if (participantStateChangeNotify.getParticipantList() == null || participantStateChangeNotify.getParticipantList().isEmpty()) {
            return;
        }
        participantInfos.clear();
        List<ParticipantInfo> participantInfoList = new ArrayList<>();
        for (Participant participant : participantStateChangeNotify.getParticipantList()){
            ParticipantInfo participantInfo = new ParticipantInfo();
            participantInfo.setMuteAudio(participant.getAudioMuted());
            participantInfo.setDisplayName(participant.getDisplayName());
            participantInfo.setUuid(participant.getUuid());
            participantInfo.setMuteVideo(participant.getVideoMuted());
            if(localStore == null){
                localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
            }
            participantInfo.setMe(participant.getUuid() == null ? false : participant.getUuid().equals(localStore.getClientId()));
            participantInfo.setLecturer(!TextUtils.isEmpty(lecturerUuid) && lecturerUuid.equals(participantInfo.getUuid()));
            participantInfo.setPinned(!TextUtils.isEmpty(pinnedUuid) && pinnedUuid.equals(participantInfo.getUuid()));
            if(participantInfo.isMe() || participantInfo.isLecturer() || participantInfo.isPinned()){
                participantInfos.add(participantInfo);
            }else{
                participantInfoList.add(participantInfo);
            }
        }
        participantInfos.addAll(participantInfoList);
        participantInfoList.clear();
        if (isLocalParticipantInfoAvailable()) {
            participantInfos.get(0).setMuteAudio(frtcCall.isAudioMuted());
            participantInfos.get(0).setMuteVideo(localStore.isVideoMuteDisabled() | !localStore.isCameraOn());
        }

        if (isFullList) {
            if (participantInfos != null) {
                int number = participantInfos.size();
                meetingControlBar.setToolbarParticipantCount(Integer.toString(number));
            }
        }

        updateParticipantDlg(participantInfos);
        String selfNewName = participantStateChangeNotify.getSelfNewName();
        if(!TextUtils.isEmpty(selfNewName)){
            BaseToast.showToast(FrtcMeetingActivity.this, String.format("" + getString(R.string.change_display_name_content), selfNewName), Toast.LENGTH_SHORT);
        }

    }


    @Override
    public void onMuteControlStateNotify(MuteControlStateNotify muteControlStateNotify) {
        if(isFinishing()){
            return;
        }
        boolean disabled = muteControlStateNotify.getMicDisabled();
        boolean audioMuted = muteControlStateNotify.getAudioMute();
        if (disabled) {
            audio_disabled = true;
            if(audioMuted) {
                isRequestUnmuteSelf = false;
                audio_muted = true;
                localStore.setAudioOn(!audio_muted);
                meetingControlBar.setAudioMuteDisable();
                if(unmuteRequestParticipants != null && unmuteRequestParticipants.size() > 0) {
                    unmuteRequestParticipants.clear();
                    if(permissionsRequestDlg != null) {
                        permissionsRequestDlg.refreshAdapter(unmuteRequestParticipants);
                        permissionsRequestDlg.updateNoPermissionsVisible();
                    }
                    if(participantsDlg != null) {
                        participantsDlg.setllUnmuteReqVisible("", false, false);
                    }
                    meetingControlBar.setRequestNotifyVisible(false);
                }
                if(unMuteConfirmDlg != null && unMuteConfirmDlg.isShowing()){
                    unMuteConfirmDlg.dismiss();
                }
                if(!isFinishing()) {
                    BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.meeting_muted_forced_notice), Toast.LENGTH_SHORT);
                }
            } else{
                if (audio_muted) {
                    if (participantsDlg != null && participantsDlg.isShowing()) {
                        participantsDlg.dismiss();
                    }
                    if(FrtcMeetingStatus.CONNECTED.equals(frtcCall.getMeetingStatus())) {
                        showUnmuteConfirmDlg();
                    }
                }
            }
            return;
        }
        audio_disabled = false;
        if (!audioMuted) {
            if(unmuteRequestParticipants != null && unmuteRequestParticipants.size() > 0) {
                unmuteRequestParticipants.clear();
                if(permissionsRequestDlg != null) {
                    permissionsRequestDlg.refreshAdapter(unmuteRequestParticipants);
                    permissionsRequestDlg.updateNoPermissionsVisible();
                }
                if(participantsDlg != null) {
                    participantsDlg.setllUnmuteReqVisible("", false, false);
                }
                meetingControlBar.setRequestNotifyVisible(false);
            }
            if (audio_muted) {
                if (participantsDlg != null && participantsDlg.isShowing()) {
                    participantsDlg.dismiss();
                }
                if(FrtcMeetingStatus.CONNECTED.equals(frtcCall.getMeetingStatus())) {
                    showUnmuteConfirmDlg();
                }
            }
            return;
        } else {
            audio_muted = true;
            localStore.setAudioOn(!audio_muted);
            meetingControlBar.setAudioMute(true);
            if(!isFinishing()) {
                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.meeting_muted_notice), Toast.LENGTH_SHORT);
            }
        }

        if (isLocalParticipantInfoAvailable()) {
            participantInfos.get(0).setMuteAudio(audio_muted);
            participantInfos.get(0).setMuteVideo(video_mute_disabled | video_muted);
            updateParticipantDlg(participantInfos);
        }
    }

    private void showUnmuteConfirmDlg(){
        if(unMuteConfirmDlg == null){
            unMuteConfirmDlg = new ConfirmDlg(this,
                    getString(R.string.unmute_dialog_title),
                    getString(R.string.unmute_dialog_content),
                    getString(R.string.unmute_dialog_negative_btn),
                    getString(R.string.unmute_dialog_positive_btn));

            unMuteConfirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
                @Override
                public void onConfirm() {
                    audio_muted = false;
                    localStore.setAudioOn(!audio_muted);
                    meetingControlBar.setAudioMute(false);
                    frtcCall.muteLocalAudio(false);
                    if (isLocalParticipantInfoAvailable()) {
                        participantInfos.get(0).setMuteAudio(audio_muted);
                    }
                    BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.meeting_unmuted_notice), Toast.LENGTH_SHORT);
                }
                @Override
                public void onCancel(){

                }
            });
            unMuteConfirmDlg.show();
        }else if(!unMuteConfirmDlg.isShowing()){
            unMuteConfirmDlg.show();
        }
    }

    @Override
    public void onOverlayNotify(OverlayNotify overlayNotify) {
        if(textOverlayManager != null){
            textOverlayManager.onOverlayNotify(overlayNotify);
        }
    }

    @Override
    public void onParticipantNumberChangeNotify(ParticipantNumberChangeNotify participantNumberChangeNotify) {

    }


    public void handleMeetingStateChangedNotify(String meetingStatus, int errorCode) {
        if(reconnectCount > 3){
            if(reconnectingView.getVisibility() == View.VISIBLE) {
                reconnectingView.setVisibility(View.GONE);
            }
        }
        if ("CONNECTED".equals(meetingStatus)) {
            if(reconnectingView.getVisibility() == View.VISIBLE) {
                reconnectingView.setVisibility(View.GONE);
            }
            if(handler != null && reconnect_runnable != null){
                handler.removeCallbacks(reconnect_runnable);
            }

            if (entered_first) {
                showMeetingView();
                entered_first = false;
                checkPermission();
                joinTimeMs = System.currentTimeMillis();
                if(isScreenShare){
                    startShareContent();
                    isScreenShare = false;
                }else if(isFloatWindow){
                    setFloatMode();
                    isFloatWindow = false;
                }
            }
            stage.setVisibility(View.VISIBLE);

        } else if ("DISCONNECTED".equals(meetingStatus)) {
            if(textOverlayManager != null){
                textOverlayManager.dismiss();
            }
            if (!meeting_canceled) {
                disconnectingWithDelay(MeetingErrorCode.from(errorCode));
            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        return true;
                    }
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("TAG", "isNetworkAvailable:" + e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public void onNetworkInfoNotify(String networkState) {
        if(networkState == null || networkState.isEmpty()){
            return;
        }
        if("kNetworkBad".equals(networkState)){
            if(video_muted && remote_video_muted){
                BaseToast.showToast(this,getString(R.string.network_status_poor_notice),Toast.LENGTH_SHORT);
            }else{
                showNetworkStateBad();
            }
        }
    }

    private void showNetworkStateBad(){
        if(networkStatusChangedDlg == null){
            networkStatusChangedDlg = new ConfirmDlg(this,"",
                    getString(R.string.network_status_dlg_content),
                    getString(R.string.network_status_dlg_negative_btn),
                    getString(R.string.network_status_dlg_positive_btn));
            networkStatusChangedDlg.setConfirmDlgListener(new IConfirmDlgListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onConfirm() {
                    muteLocalAndRemoteVideos();
                }
            });
        }else if(!networkStatusChangedDlg.isShowing()){
            networkStatusChangedDlg.show();
        }
    }

    private void muteLocalAndRemoteVideos(){
        remote_video_muted = true;
        video_muted = true;
        isEnabledLocalPeoplePreview = false;
        localStore.setCameraOn(!video_muted);
        meetingControlBar.setRemoteVideoMute(true);
        meetingControlBar.setVideoMute(video_muted);
        meetingControlBar.setCameraMuteState(video_muted);
        frtcCall.muteRemoteVideo(remote_video_muted);
        frtcCall.muteLocalVideo(video_muted);
        frtcCall.setLocalPeoplePreview(isEnabledLocalPeoplePreview);
        if (isLocalParticipantInfoAvailable()) {
            participantInfos.get(0).setMuteVideo(video_muted);
        }
    }

    @Override
    public void onStartContentRefusedNotify(){
    }

    @Override
    public void onMicVolumeMeterNotify(int volume) {
        if(audio_muted){
            return;
        }

        int volumeLevel = volume <= 10 ? 0 : volume/10 ;
        if(this.volumeLevel == volumeLevel){
            return;
        }
        this.volumeLevel = volumeLevel;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(meetingControlBar.isVisible()) {
                    meetingControlBar.setImageMuteVolume(volumeLevel);
                }
                if (isLocalParticipantInfoAvailable()) {
                    participantInfos.get(0).setAudioVolume(volumeLevel);
                    updateParticipantDlg(participantInfos);
                }
            }
        });
    }

    public void onMuteAllParticipants(){
        MuteAllParticipantsDlg dlg = new MuteAllParticipantsDlg(this);

        dlg.setOnDialogListener(this);
        dlg.show();
    }

    public void onUnmuteAllParticipants(){
        UnmuteAllParticipantsDlg dlg = new UnmuteAllParticipantsDlg(this);
        dlg.setOnDialogListener(this);
        dlg.show();
    }

    private boolean isLecturerMode(){
        if(!TextUtils.isEmpty(lecturerUuid)){
            for(ParticipantInfo info : participantInfos){
                if(info.isLecturer()){
                    return true;
                }
            }
        }
        return false;
    }

    public void onParticipantControl(ParticipantInfo participantInfo, boolean isHost, int position) {
        ParticipantsControlDlg dlg = new ParticipantsControlDlg(this, participantInfo, isHost, position, isLecturerMode());
        dlg.setOnDialogListener(this);
        dlg.show();
    }

    @Override
    public void onPermissionsRequestList() {
        showPermissionsRequest();
    }

    @Override
    public void onInviteUser() {
        frtcCall.inviteUser();
    }


    private void handleSignInUnauthorized(){
        showSignInUnauthorizedInformDlg();
        cleanSignInfo();
    }

    private void cleanSignInfo(){
        localStore.clearMeetingRooms();
        localStore.setLastUserName(localStore.getUserName());
        localStore.setUserToken("");
        localStore.clearRole();
        localStore.clearMeetingInfo();
        LocalStoreBuilder.getInstance(getApplicationContext()).setLocalStore(localStore);
    }

    private void showSignInUnauthorizedInformDlg(){
        if(signInUnauthorizedInformDlg == null ) {
            signInUnauthorizedInformDlg = new MeetingInformDlg(this,
                    getString(R.string.user_log_out_dialog_title),
                    getString(R.string.user_log_out_dialog_content),
                    getString(R.string.meeting_dialog_positive_btn));

            signInUnauthorizedInformDlg.setConfirmDlgListener(new IConfirmDlgListener() {
                @Override
                public void onConfirm() {

                }

                @Override
                public void onCancel() {

                }
            });
            signInUnauthorizedInformDlg.show();
        }else if(!signInUnauthorizedInformDlg.isShowing()){
            signInUnauthorizedInformDlg.show();
        }
    }

    @Override
    public void onStopMeetingResult(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onSignInResult(final ResultType resultType, SignInResult signInResult){

    }

    @Override
    public void onSignOutResult(final ResultType resultType){

    }

    @Override
    public void onUpdatePasswordResult(final ResultType resultType){
    }
    @Override
    public void onQueryUserInfoResult(final ResultType resultType, final QueryUserInfoResult queryUserInfoResult){
    }
    @Override
    public void onQueryMeetingRoomInfoResult(final ResultType resultType, final QueryMeetingRoomResult queryMeetingRoomResult){
    }

    @Override
    public void onQueryMeetingInfoResult(final ResultType resultType, QueryMeetingInfoResult queryMeetingInfoResult){
    };

    @Override
    public void onCreateInstantMeetingResult(final ResultType resultType, final CreateInstantMeetingResult createInstantMeetingResult){
    }
    @Override
    public void onMuteAllParticipantResult(final ResultType resultType, final boolean allowUnMute){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.mute_all_success), Toast.LENGTH_SHORT);
                            isAllowUnmute = allowUnMute;
                            if (unmuteRequestParticipants != null && unmuteRequestParticipants.size() > 0) {
                                unmuteRequestParticipants.clear();
                                if (permissionsRequestDlg != null) {
                                    permissionsRequestDlg.refreshAdapter(unmuteRequestParticipants);
                                    permissionsRequestDlg.updateNoPermissionsVisible();
                                }
                                if (participantsDlg != null) {
                                    participantsDlg.setllUnmuteReqVisible("", false, false);
                                }
                                meetingControlBar.setRequestNotifyVisible(false);
                            }
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }
    @Override
    public void onMuteParticipantResult(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }
    @Override
    public void onUnMuteAllParticipantResult(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.unmute_all_success), Toast.LENGTH_SHORT);
                            meetingControlBar.setRequestNotifyVisible(false);
                            if (participantsDlg != null) {
                                participantsDlg.setllUnmuteReqVisible("", false, false);
                                if (unmuteRequestParticipants != null) {
                                    unmuteRequestParticipants.clear();
                                }
                                if (permissionsRequestDlg != null) {
                                    permissionsRequestDlg.refreshAdapter(unmuteRequestParticipants);
                                    permissionsRequestDlg.updateNoPermissionsVisible();
                                }
                            }
                            meetingControlBar.setRequestNotifyVisible(false);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }
    @Override
    public void onUnMuteParticipantResult(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onChangeDisplayNameResult(final ResultType resultType, final String name, final boolean isMe) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            if (isMe) {
                                participantInfos.get(0).setDisplayName(name);
                                frtcCall.setDisplayName(name);
                                updateParticipantDlg(participantInfos);
                                BaseToast.showToast(FrtcMeetingActivity.this, String.format("" + getString(R.string.change_display_name_content), name), Toast.LENGTH_SHORT);
                            } else {
                                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.change_others_display_name_content), Toast.LENGTH_SHORT);
                            }
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onSetLecturerResult(final ResultType resultType, final boolean isSetLecturer) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            if (isSetLecturer) {
                                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.participant_dialog_lecture_content), Toast.LENGTH_SHORT);
                            } else {
                                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.participant_dialog_unlecture_content), Toast.LENGTH_SHORT);
                            }
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onCreateScheduledMeetingResult(final ResultType resultType, CreateScheduledMeetingResult createScheduledMeetingResult){

    }

    @Override
    public void onUpdateScheduledMeetingResult(final ResultType resultType){
    }
    @Override
    public void onDeleteScheduledMeetingResult(final ResultType resultType){
    }
    @Override
    public void onGetScheduledMeetingResult(final ResultType resultType, ScheduledMeetingResult scheduledMeetingResult){
    }
    @Override
    public void onGetScheduledMeetingListResult(final ResultType resultType, final ScheduledMeetingListResult scheduledMeetingListResult){
    }

    @Override
    public void onGetScheduledRecurrenceMeetingListResult(ResultType resultType, RecurrenceMeetingListResult scheduledMeetingListResult) {
    }

    @Override
    public void onFindUserResultResult(final ResultType resultType, FindUserResult findUserResult){
    }

    @Override
    public void onFrtcSdkLeaveMeetingNotify() {
        removeEventsAndEnterCleanMode();
        if(isSharingContent){
            frtcCall.stopSendContent();
        }
        frtcCall.leaveMeeting();
    }

    @Override
    public void onStartOverlay(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.overlay_started_notice), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onStopOverlay(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.overlay_stopped_notice), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onDisconnectParticipants(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                } else {
                    switch (resultType) {
                        case SUCCESS:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.meeting_disconnect_participants_notice), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onDisconnectAllParticipants(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    private void showRecordFileDlg() {
        if(recordFileDlg == null){
            recordFileDlg = new RecordFileDlg(this);
        }
        if(!recordFileDlg.isShowing()){
            recordFileDlg.show();
        }
    }

    @Override
    public void onStartRecording(final ResultType resultType, final String errorCode){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            isRecordingStarted = true;
                            meetingControlBar.setRecordState(isRecordingStarted);
                            setRecordingMenuVisibility(isRecordingStarted);
                            showRecordFileDlg();
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.start_record_toast_content), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                            switch (errorCode) {
                                case "0x0000f002":
                                    showNoLicenseInformDlg(getString(R.string.record_fail));
                                    break;
                                case "0x0000f003":
                                    showLicenseMaxLimitReachedInformDlg(getString(R.string.record_fail), getString(R.string.record_failed_license_max_reached_dialog_content));
                                    break;
                                case "0x0000f006":
                                    BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.cannot_start_record_repeatedly), Toast.LENGTH_SHORT);
                                    break;
                                default:
                                    BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.record_server_error), Toast.LENGTH_SHORT);
                                    break;
                            }
                            break;
                        case CONNECTION_FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onStopRecording(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            isRecordingStarted = false;
                            meetingControlBar.setRecordState(isRecordingStarted);
                            setRecordingMenuVisibility(isRecordingStarted);
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.record_stopped_notice), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                        case CONNECTION_FAILED:
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onStartLive(final ResultType resultType, final String errorCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            isLiveStarted = true;
                            meetingControlBar.setLiveState(isLiveStarted);
                            setLiveMenuVisibility(isLiveStarted);
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.start_live_toast_content), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case FAILED:
                            if (errorCode.equals("0x00010002")) {
                                showNoLicenseInformDlg(getString(R.string.live_fail));
                            } else if (errorCode.equals("0x00010003")) {
                                showLicenseMaxLimitReachedInformDlg(getString(R.string.live_fail), getString(R.string.live_failed_license_max_reached_dialog_content));
                            } else if (errorCode.equals("0x00010006")) {
                                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.cannot_start_live_repeatedly), Toast.LENGTH_SHORT);
                            } else {
                                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.live_server_error), Toast.LENGTH_SHORT);
                            }
                            break;
                        case CONNECTION_FAILED:
                        case UNKNOWN:
                            localStore.setLivePassword("");
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onStopLive(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            isLiveStarted = false;
                            meetingControlBar.setLiveState(isLiveStarted);
                            setLiveMenuVisibility(isLiveStarted);
                            localStore.setLivePassword("");
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.live_stop), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            localStore.setLivePassword("");
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onUnMuteSelfResult(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            isRequestUnmuteSelf = true;
                            reqUnmuteTimestamp = System.currentTimeMillis();
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.unmute_self_request_success), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onAllowUnmuteResult(final ResultType resultType, final List<String> unMuteParticipants) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            if (unMuteParticipants.size() == unmuteRequestParticipants.size()) {
                                unmuteRequestParticipants.clear();
                                if (permissionsRequestDlg != null) {
                                    permissionsRequestDlg.refreshAdapter(unmuteRequestParticipants);
                                    permissionsRequestDlg.updateNoPermissionsVisible();
                                }
                                if (participantsDlg != null) {
                                    participantsDlg.setllUnmuteReqVisible("", false, false);
                                }
                                meetingControlBar.setRequestNotifyVisible(false);
                                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.participants_permissions_all_rsp_toast), Toast.LENGTH_SHORT);
                            } else {
                                for (String uuid : unMuteParticipants) {
                                    for (int i = 0; i < unmuteRequestParticipants.size(); i++) {
                                        if (uuid.equals(unmuteRequestParticipants.get(i).getUuid())) {
                                            unmuteRequestParticipants.remove(i);
                                        }
                                    }
                                }
                                if (permissionsRequestDlg != null) {
                                    permissionsRequestDlg.refreshAdapter(unmuteRequestParticipants);
                                    permissionsRequestDlg.updateNoPermissionsVisible();
                                }
                                if (participantsDlg != null) {
                                    participantsDlg.setllUnmuteReqVisible("", true, false);
                                }
                                meetingControlBar.setRequestNotifyVisible(false);
                                BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.participants_permissions_rsp_toast), Toast.LENGTH_SHORT);
                            }
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onPinForMeeting(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (resultType.ordinal() >= 3 && resultType.ordinal() <= 14) {
                    onServerCommonError(resultType);
                } else {
                    switch (resultType) {
                        case SUCCESS:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.pin_for_meeting_notice), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onUnPinForMeeting(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.unpin_for_meeting_notice), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(FrtcMeetingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT);
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onAddMeetingIntoMeetingListResult(ResultType resultType) {

    }

    @Override
    public void onRemoveMeetingFromMeetingList(ResultType resultType) {

    }

    private void onServerCommonError(ResultType resultType){
        switch (resultType) {
            case COMMON_ERROR:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.server_common_error), Toast.LENGTH_SHORT);
                break;
            case PARAMETERS_ERROR:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.server_parameters_error), Toast.LENGTH_SHORT);
                break;
            case AUTHORIZATION_ERROR:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.server_authorization_error), Toast.LENGTH_SHORT);
                break;
            case PERMISSION_ERROR:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.server_permission_error), Toast.LENGTH_SHORT);
                break;
            case MEETING_NOT_EXIST:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.server_meeting_not_exist), Toast.LENGTH_SHORT);
                break;
            case MEETING_DATA_ERROR:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.server_meeting_data_error), Toast.LENGTH_SHORT);
                break;
            case OPERATION_FORBIDDEN:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.server_operation_forbidden), Toast.LENGTH_SHORT);
                break;
            case MEETING_STATUS_ERROR:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.server_meeting_status_error), Toast.LENGTH_SHORT);
                break;
            case RECORDING_STREAMING_SERVICE_ERROR:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.recording_server_error), Toast.LENGTH_SHORT);
                break;
            case LENGTH_ERROR:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.server_length_error), Toast.LENGTH_SHORT);
                break;
            case FORMAT_ERROR:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.server_format_error), Toast.LENGTH_SHORT);
                break;
            case LICENSE_ERROR:
                BaseToast.showToast(FrtcMeetingActivity.this,getString( R.string.server_license_error), Toast.LENGTH_SHORT);
                break;
            default:
        }
    }

    private void showReconnectConfirmDlg(){
        ConfirmDlg confirmDlg = new ConfirmDlg(this,
                "",
                getString(R.string.meeting_disconnect_if_reconnect),
                getString(R.string.meeting_leave_btn),
                getString(R.string.meeting_reconnect_btn));

        confirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                joinMeeting();
            }
            @Override
            public void onCancel(){
                leaveMeeting();
            }
        });
        confirmDlg.show();
    }

    private void joinMeeting(){
        frtcCall.reconnectCall();
    }

    private void addMeetingCallToHistoryList(){
        Log.d(TAG,"addMeetingCallToHistoryList:");
        long leaveTime = System.currentTimeMillis();

        if(joinTimeMs == 0 || joinTimeMs >= leaveTime){
            return;
        }

        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this).getLocalStore();
        }

        MeetingCall newMeetingCall = new MeetingCall();
        newMeetingCall.setMeetingNumber(localStore.getMeetingID());

        String meetingName = localStore.getMeetingName();
        if(meetingName.isEmpty()){
            meetingName = localStore.getDisplayName() + getResources().getString(R.string.meeting_name_postfix);
        }
        newMeetingCall.setMeetingName(meetingName);

        String meetingPassword = localStore.getMeetingPassword();
        if(meetingPassword == null || meetingPassword.isEmpty()){
            newMeetingCall.setPassword("");
        } else {
            newMeetingCall.setPassword(meetingPassword);
        }

        newMeetingCall.setDisplayName(localStore.getDisplayName());
        newMeetingCall.setServerAddress(frtcCall.getMeetingServerAddress());
        newMeetingCall.setCreateTime(joinTimeMs);
        newMeetingCall.setLeaveTime(leaveTime);
        newMeetingCall.setMeetingType(localStore.getMeetingType());

        localStore.addHistoryMeeting(newMeetingCall);
    }

    @Override
    public void onViewPermissionsRequest() {
        showPermissionsRequest();
    }

    private void showPermissionsRequest(){
        if(permissionsRequestDlg ==null) {
            permissionsRequestDlg = new PermissionsRequestListDlg(this, unmuteRequestParticipants);
            permissionsRequestDlg.setOnDialogListener(this);
            permissionsRequestDlg.show();
        }else if(!permissionsRequestDlg.isShowing()){
            permissionsRequestDlg.show();
        }
        if(participantsDlg != null) {
            participantsDlg.setllUnmuteReqVisible("", true, false);
        }
        meetingControlBar.setRequestNotifyVisible(false);
    }

    @Override
    public void onAllowUnmute(List<UnmuteRequest> unmuteRequestParticipants) {
        AllowUnmuteParam allowUnmuteParam = new AllowUnmuteParam();
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this.getApplicationContext()).getLocalStore();
        }
        allowUnmuteParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        allowUnmuteParam.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        allowUnmuteParam.setMeeting_number(localStore.getMeetingID());
        List<String> participants = new ArrayList<>();
        for(UnmuteRequest unmutePeople : unmuteRequestParticipants) {
            participants.add(unmutePeople.getUuid());
        }
        allowUnmuteParam.setParticipants(participants);
        frtcManagement.allowUnmute(allowUnmuteParam);
    }

    @Override
    public void onSetUnviewGone() {
        meetingControlBar.setRequestNotifyVisible(false);
        if(participantsDlg != null) {
            if(unmuteRequestParticipants == null || unmuteRequestParticipants.size() == 0){
                participantsDlg.setllUnmuteReqVisible("", false, false);
            }else{
                participantsDlg.setllUnmuteReqVisible("", true, false);
            }
        }
    }

    public void moveToFront(){
        Intent intent = new Intent(this, FrtcMeetingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        this.startActivity(intent);
    }

    public static boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        if (appProcessInfoList != null && appProcessInfoList.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
                if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkFloatPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), getPackageName());
                Log.d(TAG,"checkFloatPermission:"+mode+","+Settings.canDrawOverlays(FrtcMeetingActivity.this));
                return mode == AppOpsManager.MODE_ALLOWED;
            }else{
                return Settings.canDrawOverlays(FrtcMeetingActivity.this);
            }
        }

        return false;
    }

    private void requestOverlaysPermission() {
        int sdkInt = Build.VERSION.SDK_INT;
        if(sdkInt >= Build.VERSION_CODES.M) {
            if(sdkInt >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ViewConstant.SYSTEM_OVERLAY_REQUEST_CODE);
            }else{
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(intent, ViewConstant.SYSTEM_OVERLAY_REQUEST_CODE);
            }
        }
    }

    private void showIsMeeting() {
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                "",
                getString(R.string.meeting_system_notification_click_prompt),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {

            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    @Override
    public void onTimerEvent(int timerId) {

    }

    @Override
    public void onUploadLogsNotify(int tractionId) {

    }

    @Override
    public void onUploadLogsStatusNotify(UploadLogsStatus uploadLogsStatus) {

    }

    @Override
    public void onCancelUploadLogsNotify() {

    }

    @Override
    public void onSaveMeetingIntoMeetingListNotify() {
        MeetingInformDlg informDlg = new MeetingInformDlg(this,
                "",
                getString(R.string.leave_meeting_before_add_meeting),
                getString(R.string.meeting_dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {

            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }


    private void showCallFloatView(){
        floatView = new FloatView(this.getApplicationContext(), isSharingContent);
        floatView.registerCallFloatListener(this);
        floatView.show();
        localStore.setShowCallFloat(true);
        localStore.setSharingContent(isSharingContent);
        localStore.setRemoteVideoMuted(remote_video_muted);
        frtcCall.setLocalContentController(shareContentController);
        if (meetingControlBar != null) {
            meetingControlBar.stopChronometer();
        }
        if(isSharingContent){
            meetingLayout.removeLocalContent();
        }
        cleanMessageHandler();
        frtcFragmentManager.removeAllFragment();
        frtcCall.closeMeetingLayout();
        finish();
    }

    private void dismissCallFloatView(){
        if(floatView != null){
            floatView.unregisterCallFloatListener();
            floatView.dismiss();
            floatView = null;
            localStore.setShowCallFloat(false);
        }
    }

    @Override
    public void onClickMeetingFloatView() {
        dismissCallFloatView();
        Intent intent = new Intent();
        intent.setClass(this, FrtcMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void onShowMeetingReminder(ArrayList<ScheduledMeeting> scheduledMeetingsReminders) {
        if(meetingReminderDlg != null && meetingReminderDlg.isShowing()){
            meetingReminderDlg.dismiss();
        }
        meetingReminderDlg = new MeetingReminderDlg(this, scheduledMeetingsReminders );
        meetingReminderDlg.setMeetingReminderListener(new IMeetingReminderListener() {
            @Override
            public void onIgnoreCallback() {

            }

            @Override
            public void onJoinMeetingCallback(ScheduledMeeting scheduledMeeting) {
                showIsMeeting();
            }

        });
        meetingReminderDlg.show();
    }

}
