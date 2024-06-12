package frtc.sdk.ui.component;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import frtc.sdk.R;
import frtc.sdk.ui.FrtcMeetingActivity;

public class LiveRecordingMenu {
    private static final String TAG = LiveRecordingMenu.class.getSimpleName();

    private FrtcMeetingActivity mActivity;
    private final RelativeLayout MenuView;

    private ConstraintLayout liveMenu;
    private ConstraintLayout liveStatus;

    private ConstraintLayout recordingMenu;
    private ConstraintLayout recordingStatus;

    private ImageView btnShowLiveMenu;
    private ConstraintLayout liveControl;
    private ImageView shareLiveBtn;
    private ImageView btnShowRecordingMenu;
    private ConstraintLayout recordControl;
    private ImageView stopLiveBtn;
    private ImageView StopRecordingBtn;

    private boolean liveEnable = false;
    private boolean recordingEnable = false;

    private LiveRecordingMenuListener listener;

    public LiveRecordingMenu(FrtcMeetingActivity mActivity, RelativeLayout view, boolean liveEnable, boolean recordingEnable){
        this.mActivity = mActivity;
        this.MenuView = view;
        this.liveEnable = liveEnable;
        this.recordingEnable = recordingEnable;

        liveMenu = MenuView.findViewById(R.id.live_menu);
        recordingMenu = MenuView.findViewById(R.id.recording_menu);

        liveStatus = MenuView.findViewById(R.id.live_status);
        recordingStatus = MenuView.findViewById(R.id.recording_status);
        btnShowLiveMenu = MenuView.findViewById(R.id.live_control_menu);
        btnShowRecordingMenu = MenuView.findViewById(R.id.recording_control_menu);

        liveControl = MenuView.findViewById(R.id.live_control);
        shareLiveBtn = MenuView.findViewById(R.id.share_live_btn);
        stopLiveBtn = MenuView.findViewById(R.id.live_stop_btn);
        recordControl = MenuView.findViewById(R.id.recording_control);
        StopRecordingBtn = MenuView.findViewById(R.id.recording_stop_btn);

        liveStatus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(liveEnable) {
                    if (liveControl.getVisibility() == View.VISIBLE) {
                        liveControl.setVisibility(View.GONE);
                    } else {
                        liveControl.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        shareLiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(listener != null){
                   listener.onShareLive();
               }
            }
        });

        stopLiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onLiveStop();
                }
            }
        });

        recordingStatus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(recordingEnable) {
                    if (recordControl.getVisibility() == View.VISIBLE) {
                        recordControl.setVisibility(View.GONE);
                    } else {
                        recordControl.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        StopRecordingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onRecordingStop();
                }
            }
        });

    }

    public void setLivaRecordingMenuListener(LiveRecordingMenuListener listener){
        this.listener = listener;
    }

    public void setLiveStatus(boolean isLiveStarted){
        liveMenu.setVisibility(isLiveStarted ? View.VISIBLE : View.GONE);
        if(isLiveStarted && liveEnable){
            btnShowLiveMenu.setVisibility(View.VISIBLE);
        }else{
            btnShowLiveMenu.setVisibility(View.GONE);
            liveControl.setVisibility(View.GONE);
        }
    }

    public void setRecordingStatus(boolean isRecordingStarted){
        recordingMenu.setVisibility(isRecordingStarted ? View.VISIBLE : View.GONE);
        if(isRecordingStarted && recordingEnable) {
            btnShowRecordingMenu.setVisibility(View.VISIBLE);
        }else{
            btnShowRecordingMenu.setVisibility(View.GONE);
            recordControl.setVisibility(View.GONE);
        }
    }

    public void updateHostPermission(boolean recordingEnable, boolean liveEnable){
        this.liveEnable = liveEnable;
        this.recordingEnable = recordingEnable;
    }

    public interface LiveRecordingMenuListener{
        void onShareLive();
        void onLiveStop();
        void onRecordingStop();
    }
}
