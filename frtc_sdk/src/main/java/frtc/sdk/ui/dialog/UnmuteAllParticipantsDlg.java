package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import frtc.sdk.R;
import frtc.sdk.util.BaseDialog;

public class UnmuteAllParticipantsDlg extends BaseDialog {

    private final String TAG = UnmuteAllParticipantsDlg.class.getSimpleName();
    private Button btnUnmuteAll;
    private Button btnCancel;
    private IMeetingControlDlgListener mListener;


    public UnmuteAllParticipantsDlg(Context context) {
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

        setContentView(R.layout.unmute_all_dialog);

        btnUnmuteAll = findViewById(R.id.unmute_all_btn);
        btnUnmuteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClickConfirmUnmuteAll();
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

        setCancelable(false);
        setCanceledOnTouchOutside(false);

    }


    public void setOnDialogListener(IMeetingControlDlgListener listener) {
        mListener = listener;
    }

}
