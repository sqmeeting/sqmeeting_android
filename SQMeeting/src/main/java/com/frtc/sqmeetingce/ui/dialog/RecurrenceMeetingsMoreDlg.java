package com.frtc.sqmeetingce.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.frtc.sqmeetingce.R;
import com.frtc.sqmeetingce.ui.component.IRecurrenceMeetingsMoreDlgListener;

import frtc.sdk.util.BaseDialog;

public class RecurrenceMeetingsMoreDlg extends BaseDialog implements View.OnClickListener {

    private IRecurrenceMeetingsMoreDlgListener mListener;

    private final String TAG = "EndMeetingConfirmDlg";
    private boolean isInvited = false;


    public RecurrenceMeetingsMoreDlg(Context context, boolean isInvitedMeeting) {
        super(context, R.style.DialogTheme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        isInvited = isInvitedMeeting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        }

        setContentView(R.layout.recurrence_meeting_more_dlg);

        Button btnCancel = findViewById(R.id.confirm_cancel);
        btnCancel.setOnClickListener(this);
        Button btnCopyInvitation = findViewById(R.id.copy_invitation);
        btnCopyInvitation.setOnClickListener(this);
        Button btnEditRecurrenceMeeting = findViewById(R.id.edit_recurrence_meeting);
        btnEditRecurrenceMeeting.setOnClickListener(this);
        Button btnCancelRecurrenceMeeting = findViewById(R.id.cancel_recurrence_meeting);
        btnCancelRecurrenceMeeting.setOnClickListener(this);

        btnEditRecurrenceMeeting.setVisibility(isInvited ? View.GONE : View.VISIBLE);
        btnCancelRecurrenceMeeting.setVisibility(isInvited ? View.GONE : View.VISIBLE);

    }


    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.confirm_cancel) {
            dismiss();
        } else if (i == R.id.copy_invitation) {
            if (mListener !=null) {
                mListener.onCopyInvitation();
            }
            dismiss();
        } else if (i == R.id.edit_recurrence_meeting){
            if (mListener !=null) {
                mListener.onEditRecurrenceMeeting();
            }
            dismiss();
        }else if (i == R.id.cancel_recurrence_meeting){
            if (mListener !=null) {
                mListener.onCancelRecurrenceMeeting();
            }
            dismiss();
        }
    }
    public void setOnDialogListener(IRecurrenceMeetingsMoreDlgListener listener) {
        mListener = listener;
    }



}