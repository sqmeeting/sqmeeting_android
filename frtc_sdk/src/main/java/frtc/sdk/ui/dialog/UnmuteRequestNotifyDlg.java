package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import frtc.sdk.R;
import frtc.sdk.model.UnmuteRequest;
import frtc.sdk.util.BaseDialog;

public class UnmuteRequestNotifyDlg extends BaseDialog {

    private static final String TAG = UnmuteRequestNotifyDlg.class.getSimpleName();
    private Context mContext;

    private IMeetingControlDlgListener listener;
    private UnmuteRequest unmuteRequest;

    public UnmuteRequestNotifyDlg(Context context, UnmuteRequest unmuteRequestParticipant) {
        super(context, R.style.DialogTheme);
        mContext = context;
        unmuteRequest = unmuteRequestParticipant;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.unmute_request_notify_dialog);

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialogWindow.setGravity(Gravity.BOTTOM);
        }

        LinearLayout layoutNotify = findViewById(R.id.layout_notify);
        TextView name = findViewById(R.id.name);

        name.setText(unmuteRequest.getDisplay_name());

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 3000);

        layoutNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViewPermissionsRequest();
                dismiss();
            }
        });

    }

    public void setUnmuteRequestNotifyListener(IMeetingControlDlgListener mListener) {
        this.listener = mListener;
    }
}


