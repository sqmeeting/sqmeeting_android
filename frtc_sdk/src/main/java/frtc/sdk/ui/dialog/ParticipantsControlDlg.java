package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import frtc.sdk.R;
import frtc.sdk.util.BaseDialog;
import frtc.sdk.ui.model.ParticipantInfo;

public class ParticipantsControlDlg extends BaseDialog {

    private final String TAG = ParticipantsControlDlg.class.getSimpleName();
    private final boolean allowUnmute = true;
    private IMeetingControlDlgListener mListener;
    private final ParticipantInfo participantInfo;
    private boolean isHost = false;
    private int position;
    private final boolean inLecturerMode;

    public ParticipantsControlDlg(Context context, ParticipantInfo participantInfo,
                                  boolean isHost, int position, boolean inLecturerMode) {
        super(context, R.style.DialogTheme);
        this.participantInfo = participantInfo;
        this.isHost = isHost;
        this.position = position;
        this.inLecturerMode = inLecturerMode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }

        setContentView(R.layout.participant_control_dialog);
        TextView name = findViewById(R.id.name);
        name.setText(participantInfo.getDisplayName());

        TextView tvMute = findViewById(R.id.mute_tv);
        if(participantInfo.getMuteAudio()) {
            tvMute.setText(R.string.participant_dialog_unmute_btn);
        }else{
            tvMute.setText(R.string.participant_dialog_mute_btn);
        }
        tvMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    if(participantInfo.isMe()){
                        mListener.onClickAudio();
                    }else {
                        List<String> participants = new ArrayList<>();
                        participants.add(participantInfo.getUuid());
                        if (participantInfo.getMuteAudio()) {
                            mListener.onClickConfirmUnMute(participants);
                        } else {
                            mListener.onClickConfirmMute(participants);
                        }
                    }
                }
                dismiss();
            }
        });

        TextView tvCancel = findViewById(R.id.cancel_tv);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView tvChangeName = findViewById(R.id.change_name_tv);
        tvChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChangeDisplayNameInParticipants(participantInfo);
                }
                dismiss();
            }
        });

        TextView tvLecture = findViewById(R.id.lecture_tv);
        if(participantInfo.isLecturer()) {
            tvLecture.setText(R.string.participant_dialog_unlecture_btn);
        }else{
            tvLecture.setText(R.string.participant_dialog_lecture_btn);
        }
        tvLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickConfirmLecturer(participantInfo.getUuid());
                }
                dismiss();
            }
        });

        TextView tvPin = findViewById(R.id.pin_tv);
        if(participantInfo.isPinned()){
            tvPin.setText(R.string.unpin_for_meeting);
        }else{
            tvPin.setText(R.string.pin_for_meeting);
        }

        tvPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    if(participantInfo.isPinned()){
                        mListener.onUnpin(participantInfo.getUuid());
                    }else{
                        mListener.onPin(participantInfo.getUuid());
                    }
                }
                dismiss();
            }
        });

        TextView tvRemove = findViewById(R.id.remove_tv);
        tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onDisconnectParticipant(participantInfo.getUuid());
                }
                dismiss();
            }
        });

        if(isHost){
            tvLecture.setVisibility(View.VISIBLE);
            if(participantInfo.isLecturer()){
                tvMute.setVisibility(View.GONE);
                tvChangeName.setVisibility(View.VISIBLE);
                tvRemove.setVisibility(View.GONE);
            }else{
                tvMute.setVisibility(View.VISIBLE);
                tvChangeName.setVisibility(View.VISIBLE);
                if(participantInfo.isMe()){
                    tvRemove.setVisibility(View.GONE);
                }else{
                    tvRemove.setVisibility(View.VISIBLE);
                }
            }
            if(inLecturerMode){
                tvPin.setVisibility(View.GONE);
            }else{
                tvPin.setVisibility(View.VISIBLE);
            }
        }else{
            tvLecture.setVisibility(View.GONE);
            tvPin.setVisibility(View.GONE);
            tvRemove.setVisibility(View.GONE);
            if(participantInfo.isMe()){
                tvMute.setVisibility(View.VISIBLE);
                tvChangeName.setVisibility(View.VISIBLE);
            }
        }

        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }

    public void setOnDialogListener(IMeetingControlDlgListener listener) {
        mListener = listener;
    }

}
