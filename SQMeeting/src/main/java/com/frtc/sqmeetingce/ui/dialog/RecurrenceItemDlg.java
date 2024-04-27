package com.frtc.sqmeetingce.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.frtc.sqmeetingce.R;
import com.frtc.sqmeetingce.ui.component.IRecurrenceItemDlgListener;

import frtc.sdk.util.BaseDialog;

public class RecurrenceItemDlg extends BaseDialog implements View.OnClickListener {

    private IRecurrenceItemDlgListener mListener;
    private Context context;

    private final String TAG = "RecurrenceItemDlg";


    public RecurrenceItemDlg(Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;
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

        setContentView(R.layout.edit_recurrence_item_dlg);

        Button btnCancel = findViewById(R.id.confirm_cancel);
        btnCancel.setOnClickListener(this);
        Button btnEditMeeting = findViewById(R.id.edit_meeting);
        btnEditMeeting.setOnClickListener(this);
        Button btnCancelMeeting = findViewById(R.id.cancel_meeting);
        btnCancelMeeting.setOnClickListener(this);
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
        } else if (i == R.id.cancel_meeting){
            if (mListener !=null) {
                mListener.onCancelMeeting();
            }
            dismiss();
        }
    }

    public void setOnDialogListener(IRecurrenceItemDlgListener listener) {
        mListener = listener;
    }
}