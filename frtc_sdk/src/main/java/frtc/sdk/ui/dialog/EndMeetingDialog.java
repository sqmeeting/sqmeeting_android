package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import frtc.sdk.R;
import frtc.sdk.util.BaseDialog;


public class EndMeetingDialog extends BaseDialog{
    private final String TAG = EndMeetingDialog.class.getSimpleName();

    private boolean isHost;
    private IMeetingControlDlgListener mListener;

    public EndMeetingDialog(Context context, boolean isHost) {
        super(context, R.style.NoMaskDialogTheme);
        this.isHost = isHost;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.end_meeting_confirm_dialog);

        Button btnLeaveMeeting = findViewById(R.id.leave_meeting);
        btnLeaveMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onLeaveMeeting();
                }
                dismiss();
            }
        });

        btnLeaveMeeting.setSelected(!isHost);
        if(isHost){
            Button btnEndMeeting = findViewById(R.id.end_meeting);
            btnEndMeeting.setVisibility(View.VISIBLE);
            btnEndMeeting.setSelected(isHost);
            btnEndMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    showEndMeetingConfirmDlg();
                }
            });
        }

        ImageView btnCancel = findViewById(R.id.cancel_btn);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setOnDialogListener(IMeetingControlDlgListener listener) {
        mListener = listener;
    }

    private void showEndMeetingConfirmDlg(){
        ConfirmDlg confirmDlg = new ConfirmDlg(getContext(),
                getContext().getString(R.string.end_meeting_dialog_title),
                getContext().getString(R.string.end_meeting_dialog_content),
                getContext().getString(R.string.meeting_dialog_negative_btn),
                getContext().getString(R.string.meeting_dialog_positive_btn));

        confirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                if (mListener != null) {
                    mListener.onEndMeeting();
                }
                dismiss();
            }
            @Override
            public void onCancel(){

            }
        });
        confirmDlg.show();
    }

}