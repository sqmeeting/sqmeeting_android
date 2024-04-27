package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import frtc.sdk.R;
import frtc.sdk.util.BaseDialog;

public class RecordFileDlg extends BaseDialog {

    private static final String TAG = RecordFileDlg.class.getSimpleName();
    private Context mContext;

    private TextView title;
    private TextView content;
    private TextView content2;
    private CheckBox cbPwd;

    private Button btnCancel;
    private Button btnConfirm;

    private IConfirmDlgListener mListener;

    public RecordFileDlg(Context context) {
        super(context, R.style.DialogTheme);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.record_file_dialog);

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialogWindow.setGravity(Gravity.BOTTOM);
        }

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);

        btnConfirm = findViewById(R.id.confirm_btn);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

}

