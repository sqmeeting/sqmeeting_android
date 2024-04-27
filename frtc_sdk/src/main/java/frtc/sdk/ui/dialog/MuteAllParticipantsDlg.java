package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import frtc.sdk.R;
import frtc.sdk.util.BaseDialog;

public class MuteAllParticipantsDlg extends BaseDialog {

    private final String TAG = MuteAllParticipantsDlg.class.getSimpleName();
    private Button btnMuteAll;
    private Button btnCancel;
    private boolean allowUnmute = true;
    private Switch stAllowUnmute;
    private IMeetingControlDlgListener mListener;


    public MuteAllParticipantsDlg(Context context) {
        super(context, R.style.DialogTheme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.mute_all_dialog);

        btnMuteAll = findViewById(R.id.mute_all_btn);
        btnMuteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    allowUnmute = stAllowUnmute.isChecked();
                    mListener.onClickConfirmMuteAll(allowUnmute);
                }
                dismiss();
            }
        });

        btnCancel = findViewById(R.id.cancel_btn);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        stAllowUnmute = findViewById(R.id.switch_allow_unmute);
        stAllowUnmute.setChecked(allowUnmute);
        stAllowUnmute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                allowUnmute = isChecked;
            }
        });

        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }


    public void setOnDialogListener(IMeetingControlDlgListener listener) {
        mListener = listener;
    }
}
