package com.frtc.sqmeetingce.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.frtc.sqmeetingce.R;
import com.frtc.sqmeetingce.ui.component.IEditMeetingDlgListener;

import frtc.sdk.util.BaseDialog;

public class EditMeetingDlg extends BaseDialog implements View.OnClickListener {

    private final String TAG = "EditMeetingDlg";
    private IEditMeetingDlgListener mListener;


    public EditMeetingDlg(Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        }

        setContentView(R.layout.edit_meeting_dialog);
        Button btnCancel = findViewById(R.id.confirm_cancel);
        btnCancel.setOnClickListener(this);
        Button btnEditMeeting = findViewById(R.id.edit_meeting);
        btnEditMeeting.setOnClickListener(this);
        Button btnEditRecurrenceMeeting = findViewById(R.id.edit_recurrence_meeting);
        btnEditRecurrenceMeeting.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.confirm_cancel) {
            dismiss();
        } else if (i == R.id.edit_meeting) {
            if (mListener !=null) {
                mListener.onEditMeeting();
            }
            dismiss();
        } else if (i == R.id.edit_recurrence_meeting){
            if (mListener !=null) {
                mListener.onEditRecurrenceMeeting();
            }
            dismiss();
        }
    }
    public void setOnDialogListener(IEditMeetingDlgListener listener) {
        mListener = listener;
    }



}