package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import frtc.sdk.R;
import frtc.sdk.util.BaseDialog;

public class MessageDlg extends BaseDialog {

    private final String TAG = MeetingDetailsDlg.class.getSimpleName();

    private Context mContext;

    private TextView message;

    private Button btnOk;

    String messageStr = "";
    String okStr = "";

    private IConfirmDlgListener mListener;

    public MessageDlg(Context context, String messageStr, String okStr) {
        super(context, R.style.DialogTheme);

        this.messageStr = messageStr;
        this.okStr = okStr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_dialog);

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
           dialogWindow.setGravity(Gravity.CENTER);
        }

        message = findViewById(R.id.message);

        btnOk = findViewById(R.id.ok_btn);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        message.setText(messageStr);
        btnOk.setText(okStr);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onConfirm();
                }
                dismiss();
            }
        });


    }

    public void setConfirmDlgListener(IConfirmDlgListener listener){
        mListener = listener;
    }

    @Override
    public void onStop(){
        super.onStop();
    }

}
