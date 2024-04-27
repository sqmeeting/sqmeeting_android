package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import frtc.sdk.R;
import frtc.sdk.util.BaseDialog;

public class MeetingInformDlg extends BaseDialog {

    private final String TAG = MeetingDetailsDlg.class.getSimpleName();

    private Context mContext;

    private TextView title;
    private TextView content;

    private Button btnOk;

    String titleStr = "";
    String contentStr = "";
    String okStr = "";

    private IConfirmDlgListener mListener;

    public MeetingInformDlg(Context context, String titleStr, String contentStr, String okStr) {
        super(context, R.style.DialogTheme);

        this.titleStr = titleStr;
        this.contentStr = contentStr;
        this.okStr = okStr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inform_dialog);

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
           dialogWindow.setGravity(Gravity.CENTER);
        }

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);

        btnOk = findViewById(R.id.ok_btn);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        if(TextUtils.isEmpty(titleStr)){
            title.setVisibility(View.GONE);
        }else {
            title.setText(titleStr);
        }
        content.setText(contentStr);
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
