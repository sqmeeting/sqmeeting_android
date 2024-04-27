package frtc.sdk.ui.component;

import android.content.res.Configuration;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import frtc.sdk.R;
import frtc.sdk.log.Log;
import frtc.sdk.ui.FrtcMeetingActivity;
import frtc.sdk.ui.view.TimerView;
import frtc.sdk.util.LanguageUtil;

public class MeetingControlBar {

    private static final String TAG = MeetingControlBar.class.getSimpleName();

    private FrtcMeetingActivity mActivity;
    private final RelativeLayout controlBarView;
    private ConstraintLayout moreMenu;
    private Chronometer chronometer;
    private TextView callMeetingName;
    private TimerView currentTime;
    private ConstraintLayout titleMoreBtn;
    private Chronometer chronometerPort;
    private TextView callMeetingNamePort;
    private ConstraintLayout titleMoreBtnPort;
    private ImageView switchCameraBtn,switchSpeakerBtn;
    private ConstraintLayout audioMuteBtn,videoMuteBtn,shareContentBtn,participantsBtn,moreBtn;
    private ConstraintLayout inviteBtn;
    private ConstraintLayout startOverlayBtn,stopOverlayBtn;
    private ConstraintLayout liveBtn,recordBtn;
    private ConstraintLayout muteRemoteVideoBtn, muteRemoteVideoHostBtn;
    private ConstraintLayout floatWinDisplayCl, floatWinDisplayHostCl;
    private ImageView imageMute;
    private ImageView imageVideoMute;
    private ImageView imageShareContent;
    private TextView toolbarParticipantCount;
    private ImageView requestNotify;
    private ImageView imageLive;
    private ImageView imageRecord;
    private ImageView imageMuteRemoteVideo, imageMuteRemoteVideoHost;
    private TextView muteTextView;
    private TextView videoTextView;
    private TextView shareContentTextView;
    private TextView liveTextView;
    private TextView recordTextView;
    private TextView muteRemoteVideoTextView, muteRemoteVideoTextViewHost;
    private TextView cancelBtn;

    private IControlBarRequestListener listener;
    private long lastClickTime = 0;

    public MeetingControlBar(FrtcMeetingActivity mActivity, RelativeLayout view) {

        this.mActivity = mActivity;

        controlBarView = view;
        controlBarView.setVisibility(View.INVISIBLE);

        chronometer = controlBarView.findViewById(R.id.duration);
        callMeetingName = controlBarView.findViewById(R.id.meeting_name);
        currentTime = controlBarView.findViewById(R.id.current_time);
        titleMoreBtn = controlBarView.findViewById(R.id.meeting_info_layout);

        chronometerPort = controlBarView.findViewById(R.id.duration_portrait);
        callMeetingNamePort = controlBarView.findViewById(R.id.meeting_name_portrait);
        titleMoreBtnPort = controlBarView.findViewById(R.id.meeting_info_layout_portrait);

        Button endMeetingBtn = controlBarView.findViewById(R.id.end_meeting_btn);

        ImageView callFloat = controlBarView.findViewById(R.id.float_btn);
        switchCameraBtn = controlBarView.findViewById(R.id.camera_switch);
        switchSpeakerBtn = controlBarView.findViewById(R.id.speaker_switch);

        audioMuteBtn = controlBarView.findViewById(R.id.mute_layout);
        videoMuteBtn = controlBarView.findViewById(R.id.video_mute_layout);
        shareContentBtn = controlBarView.findViewById(R.id.share_content_layout);
        participantsBtn = controlBarView.findViewById(R.id.participants_layout);
        moreBtn = controlBarView.findViewById(R.id.more_layout);

        imageVideoMute = controlBarView.findViewById(R.id.video_mute_btn);
        imageMute = controlBarView.findViewById(R.id.mute_btn);
        imageShareContent = controlBarView.findViewById(R.id.share_content_btn);
        toolbarParticipantCount = controlBarView.findViewById(R.id.participant_count);
        requestNotify = controlBarView.findViewById(R.id.request_notify);

        moreMenu = controlBarView.findViewById(R.id.more_control_bar);
        moreMenu.setVisibility(View.GONE);

        inviteBtn = controlBarView.findViewById(R.id.invite_layout);
        startOverlayBtn = controlBarView.findViewById(R.id.start_overlay_layout);
        stopOverlayBtn = controlBarView.findViewById(R.id.stop_overlay_layout);
        liveBtn = controlBarView.findViewById(R.id.live_layout);
        recordBtn = controlBarView.findViewById(R.id.record_layout);
        muteRemoteVideoBtn = controlBarView.findViewById(R.id.remote_video_mute_layout);
        floatWinDisplayCl = controlBarView.findViewById(R.id.float_window_display_layout);
        muteRemoteVideoHostBtn = controlBarView.findViewById(R.id.remote_video_mute_host_layout);
        floatWinDisplayHostCl = controlBarView.findViewById(R.id.float_window_display_host_layout);
        cancelBtn = controlBarView.findViewById(R.id.cancel_btn);

        imageLive = controlBarView.findViewById(R.id.live_btn);
        liveTextView = controlBarView.findViewById(R.id.text_live);
        imageRecord = controlBarView.findViewById(R.id.record_btn);
        recordTextView = controlBarView.findViewById(R.id.text_record);
        imageMuteRemoteVideo = controlBarView.findViewById(R.id.remote_video_mute_btn);
        muteRemoteVideoTextView = controlBarView.findViewById(R.id.text_remote_video_mute);
        imageMuteRemoteVideoHost = controlBarView.findViewById(R.id.remote_video_mute_host_btn);
        muteRemoteVideoTextViewHost = controlBarView.findViewById(R.id.text_remote_video_mute_host);

        endMeetingBtn.setOnClickListener(controlBarListener);
        callFloat.setOnClickListener(controlBarListener);
        switchCameraBtn.setOnClickListener(controlBarListener);
        switchSpeakerBtn.setOnClickListener(controlBarListener);
        titleMoreBtn.setOnClickListener(controlBarListener);
        titleMoreBtnPort.setOnClickListener(controlBarListener);
        videoMuteBtn.setOnClickListener(controlBarListener);
        audioMuteBtn.setOnClickListener(controlBarListener);
        shareContentBtn.setOnClickListener(controlBarListener);
        participantsBtn.setOnClickListener(controlBarListener);
        moreBtn.setOnClickListener(controlBarListener);
        inviteBtn.setOnClickListener(controlBarListener);
        startOverlayBtn.setOnClickListener(controlBarListener);
        stopOverlayBtn.setOnClickListener(controlBarListener);
        liveBtn.setOnClickListener(controlBarListener);
        recordBtn.setOnClickListener(controlBarListener);
        muteRemoteVideoBtn.setOnClickListener(controlBarListener);
        muteRemoteVideoHostBtn.setOnClickListener(controlBarListener);
        floatWinDisplayCl.setOnClickListener(controlBarListener);
        floatWinDisplayHostCl.setOnClickListener(controlBarListener);
        cancelBtn.setOnClickListener(controlBarListener);

        muteTextView = controlBarView.findViewById(R.id.text_mute);
        videoTextView = controlBarView.findViewById(R.id.text_video_mute);
        shareContentTextView = controlBarView.findViewById(R.id.text_share_content);

        setCallTitleBar(mActivity.getResources().getConfiguration().orientation);
    }

    public void setControlBarRequestListener(IControlBarRequestListener listener){
        this.listener = listener;
    }

    public void setCallTitleBar(int orientation) {
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            titleMoreBtnPort.setVisibility(View.GONE);
            titleMoreBtn.setVisibility(View.VISIBLE);
            currentTime.setVisibility(View.VISIBLE);
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT){
            titleMoreBtn.setVisibility(View.GONE);
            titleMoreBtnPort.setVisibility(View.VISIBLE);
            currentTime.setVisibility(View.GONE);
        }
    }

    public void updateHostPermission(boolean isHost, boolean isOperatorOrAdmin){
        if(isHost){
            startOverlayBtn.setVisibility(View.VISIBLE);
            stopOverlayBtn.setVisibility(View.VISIBLE);
            recordBtn.setVisibility(View.VISIBLE);
            if(isOperatorOrAdmin){
                liveBtn.setVisibility(View.VISIBLE);
            }else{
                liveBtn.setVisibility(View.GONE);
            }
            muteRemoteVideoHostBtn.setVisibility(View.VISIBLE);
            floatWinDisplayHostCl.setVisibility(View.VISIBLE);
            muteRemoteVideoBtn.setVisibility(View.GONE);
            floatWinDisplayCl.setVisibility(View.GONE);
        }else{
            startOverlayBtn.setVisibility(View.GONE);
            stopOverlayBtn.setVisibility(View.GONE);
            liveBtn.setVisibility(View.GONE);
            recordBtn.setVisibility(View.GONE);
            muteRemoteVideoHostBtn.setVisibility(View.GONE);
            floatWinDisplayHostCl.setVisibility(View.GONE);
            muteRemoteVideoBtn.setVisibility(View.VISIBLE);
            floatWinDisplayCl.setVisibility(View.VISIBLE);
        }
    }

    public void startChronometer() {
        controlBarView.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        chronometerPort.setBase(SystemClock.elapsedRealtime());
        chronometerPort.start();
    }

    public void resumeChronometer(long elapsedRealtime) {
        controlBarView.setVisibility(View.VISIBLE);
        chronometer.setBase(elapsedRealtime);
        chronometer.start();
        chronometerPort.setBase(elapsedRealtime);
        chronometerPort.start();
    }

    public void stopChronometer() {
        chronometer.stop();
        chronometerPort.stop();
        controlBarView.setVisibility(View.INVISIBLE);
    }

    private View.OnClickListener controlBarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (SystemClock.elapsedRealtime() - lastClickTime < 500){
                return;
            }

            lastClickTime = SystemClock.elapsedRealtime();

            int id = v.getId();

            if (id == R.id.more_layout) {
                changeMoreMenuVisibility();
                return;
            }else if (id == R.id.cancel_btn){
                hideMoreMenu();
                return;
            }

            if(listener == null){
                Log.e(TAG,"control bar request listener is null");
                return;
            }

            if(mActivity.isMeetingNotResponse()){
                return;
            }

            if (id == R.id.end_meeting_btn) {
                Log.i(TAG, "onClick end meeting");
                listener.onEndMeeting();
            } else if(id == R.id.camera_switch){
                Log.i(TAG, "onClick camera switch");
                listener.onSwitchCamera();
            } else if(id == R.id.speaker_switch){
                Log.i(TAG, "onClick speaker switch");
                listener.onSwitchSpeaker();
            } else if (id == R.id.meeting_info_layout || id == R.id.meeting_info_layout_portrait){
                Log.d(TAG,"on click show meeting details");
                listener.onShowMeetingDetails();
            } else if (id == R.id.mute_layout) {
                Log.i(TAG, "onClick audio mute");
                listener.onMuteAudio();
            } else if (id == R.id.video_mute_layout) {
                Log.i(TAG, "onClick video mute");
                listener.onMuteVideo();
            } else if (id == R.id.participants_layout) {
                Log.i(TAG, "onClick local view button");
                listener.onShowParticipants();
            } else if (id == R.id.share_content_layout) {
                listener.onShareContent();
            } else if (id == R.id.invite_layout){
                Log.i(TAG,"onClick INVITE button");
                listener.onShowInviteInfo();
                hideMoreMenu();
            } else if (id == R.id.start_overlay_layout){
                Log.i(TAG,"onClick startChronometer overlay button");
                listener.onStartOverlay();
                hideMoreMenu();
            } else if (id == R.id.stop_overlay_layout){
                Log.i(TAG,"onClick stopChronometer overlay button");
                listener.onStopOverlay();
                hideMoreMenu();
            } else if (id == R.id.live_layout){
                Log.i(TAG,"onClick live button");
                listener.onLive();
                hideMoreMenu();
            } else if (id == R.id.record_layout){
                Log.i(TAG,"onClick record button");
                listener.onRecord();
                hideMoreMenu();
            }else if (id == R.id.remote_video_mute_layout || id == R.id.remote_video_mute_host_layout){
                Log.i(TAG,"onClick muteRemoteVideo button");
                listener.onMuteRemote();
                hideMoreMenu();
            }else if (id == R.id.float_window_display_layout || id == R.id.float_btn || id == R.id.float_window_display_host_layout){
                Log.i(TAG,"onClick floatWinDisplayCl button");
                listener.onFloatMode();
                hideMoreMenu();
            }
        }
    };

    private void changeMoreMenuVisibility(){
        if(moreMenu.getVisibility() == View.VISIBLE){
            moreMenu.setVisibility(View.GONE);
        }else if(moreMenu.getVisibility() == View.GONE){
            moreMenu.setVisibility(View.VISIBLE);
        }
    }

    public void hideMoreMenu(){
        if(moreMenu.getVisibility() == View.VISIBLE){
            moreMenu.setVisibility(View.GONE);
        }
    }

    public void setControlBarVisible(boolean visible) {
        controlBarView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public boolean isVisible() {
        if(controlBarView.getVisibility() == View.VISIBLE){
            return true;
        }
        return false;
    }

    public void setMeetingName(String meetingName){
        callMeetingName.setText(meetingName);
        callMeetingNamePort.setText(meetingName);
    }

    public void setToolbarParticipantCount(String numParticipant){
        Log.i(TAG,"setToolbarParticipantCount = " + numParticipant);
        toolbarParticipantCount.setText(numParticipant);
    }

    public void setLiveState(boolean isLive){
        if(isLive){
            imageLive.setImageResource(R.drawable.call_icon_stop_live);
            liveTextView.setText(R.string.tool_bar_stop_live);
        }else{
            imageLive.setImageResource(R.drawable.call_icon_live);
            liveTextView.setText(R.string.tool_bar_start_live);
        }
    }

    public void setRecordState(boolean isRecording){
        if(isRecording){
            imageRecord.setImageResource(R.drawable.call_icon_stop_record);
            recordTextView.setText(R.string.tool_bar_stop_record);
        }else{
            imageRecord.setImageResource(R.drawable.call_icon_record);
            recordTextView.setText(R.string.tool_bar_start_record);
        }
    }

    public void setCameraMuteState(boolean mute) {
        if (mute) {
            switchCameraBtn.setEnabled(false);
            switchCameraBtn.setImageResource(R.drawable.call_icon_switch_camera_disabled);
            videoTextView.setText(R.string.tool_bar_video_unmute);
            videoTextView.setTextColor(mActivity.getResources().getColor(R.color.text_color_white));
        } else {
            switchCameraBtn.setEnabled(true);
            switchCameraBtn.setImageResource(R.drawable.icon_switch_camera);
            videoTextView.setTextColor(mActivity.getResources().getColor(R.color.text_color_white));
        }
    }


    public void setSpeakerState(boolean isOn) {
        if(isOn){
            switchSpeakerBtn.setImageResource(R.drawable.icon_speaker_on);
        }else{
            switchSpeakerBtn.setImageResource(R.drawable.call_icon_speaker_off);
        }
    }

    public void setRemoteVideoMute(boolean mute) {
        if (mute) {
            imageMuteRemoteVideo.setImageResource(R.drawable.call_icon_mute_remote_video);
            muteRemoteVideoTextView.setText(R.string.tool_bar_receiving_videos);
            imageMuteRemoteVideoHost.setImageResource(R.drawable.call_icon_mute_remote_video);
            muteRemoteVideoTextViewHost.setText(R.string.tool_bar_receiving_videos);
        } else {
            imageMuteRemoteVideo.setImageResource(R.drawable.call_icon_unmute_remote_video);
            muteRemoteVideoTextView.setText(R.string.tool_bar_stop_receiving_videos);
            imageMuteRemoteVideoHost.setImageResource(R.drawable.call_icon_unmute_remote_video);
            muteRemoteVideoTextViewHost.setText(R.string.tool_bar_stop_receiving_videos);
        }
    }

    public void setAudioMute(boolean isAudioMuted) {
        if(!LanguageUtil.isSharePreferenceLan(mActivity)) {
            LanguageUtil.setLanguage(mActivity);
        }
        if (isAudioMuted) {
            imageMute.setImageResource(R.drawable.call_icon_mute);
            muteTextView.setText(R.string.tool_bar_unmute);
        } else {
            imageMute.setImageResource(R.drawable.call_icon_unmute);
            muteTextView.setText(R.string.tool_bar_mute);
        }
    }

    public void setAudioMuteDisable(){
        imageMute.setImageResource(R.drawable.call_icon_mute);
        muteTextView.setText(R.string.tool_bar_unmute);
    }

    public void setVideoMute(boolean isVideoMuted) {
        if (isVideoMuted) {
            imageVideoMute.setImageResource(R.drawable.call_icon_camera_off);
            videoTextView.setText(R.string.tool_bar_video_unmute);
            videoTextView.setTextColor(mActivity.getResources().getColor(R.color.text_color_white));
        } else {
            imageVideoMute.setImageResource(R.drawable.call_icon_camera_on);
            videoTextView.setText(R.string.tool_bar_video_mute);
            videoTextView.setTextColor(mActivity.getResources().getColor(R.color.text_color_white));
        }
    }

    public void enableVideoMuted(boolean enable){
        if(enable){
            imageVideoMute.setAlpha(1.0f);
            videoMuteBtn.setEnabled(true);
            videoTextView.setTextColor(mActivity.getResources().getColor(R.color.text_color_white));
        }else{
            imageVideoMute.setAlpha(0.4f);
            videoMuteBtn.setEnabled(false);
            videoTextView.setTextColor(mActivity.getResources().getColor(R.color.text_color_disable));
        }
    }

    public void setShareContentState(boolean shareContent) {
        if (shareContent) {
            imageShareContent.setImageResource(R.drawable.call_icon_stop_content);
            shareContentTextView.setText(R.string.tool_bar_stop_content);
        } else {
            imageShareContent.setImageResource(R.drawable.call_icon_share_content);
            shareContentTextView.setText(R.string.tool_bar_share_content);
        }
    }

    public void setAudioCall(boolean audioOnly) {
        if (audioOnly) {
            switchCameraBtn.setEnabled(false);
            switchCameraBtn.setImageResource(R.drawable.call_icon_switch_camera_disabled);

            videoMuteBtn.setEnabled(false);
            imageVideoMute.setImageResource(R.drawable.call_icon_camera_on);
            imageVideoMute.setAlpha(0.4f);

            videoTextView.setText(R.string.tool_bar_video_unmute);
            videoTextView.setTextColor(mActivity.getResources().getColor(R.color.text_color_disable));

            imageShareContent.setAlpha(0.4f);
            shareContentBtn.setEnabled(false);
            imageShareContent.setImageResource(R.drawable.call_icon_share_content);

            shareContentTextView.setTextColor(mActivity.getResources().getColor(R.color.text_color_disable));;
            shareContentTextView.setText(R.string.tool_bar_share_content);
        }
    }

    public void setRequestNotifyVisible(boolean visible) {
        requestNotify.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public boolean getRequestNotifyVisible(){
        return requestNotify.getVisibility() == View.VISIBLE;
    }

    public void setImageMuteVolume(int volume) {
        switch (volume){
            case 0:
                imageMute.setImageResource(R.drawable.call_icon_unmute);
                break;
            case 1:
                imageMute.setImageResource(R.drawable.call_icon_unmute1);
                break;
            case 2:
                imageMute.setImageResource(R.drawable.call_icon_unmute2);
                break;
            case 3:
                imageMute.setImageResource(R.drawable.call_icon_unmute3);
                break;
            case 4:
                imageMute.setImageResource(R.drawable.call_icon_unmute4);
                break;
            default:
                imageMute.setImageResource(R.drawable.call_icon_unmute4);
        }
    }

}
