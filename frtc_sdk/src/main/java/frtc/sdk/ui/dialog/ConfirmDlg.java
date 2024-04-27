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

public class ConfirmDlg extends BaseDialog {

    private final String TAG = ConfirmDlg.class.getSimpleName();

    private Context mContext;

    private TextView title;
    private TextView content;
    private TextView content2;

    private Button btnCancel;
    private Button btnConfirm;

    String titleStr = "";
    String contentStr = "";
    String cancelStr = "";
    String confirmStr = "";
    String content2Str = "";

    private boolean enablePositiveTextColor = false;

    private IConfirmDlgListener mListener;


    public ConfirmDlg(Context context, String titleStr, String contentStr, String cancelStr, String confirmStr) {
        super(context, R.style.DialogTheme);
        this.titleStr = titleStr;
        this.contentStr = contentStr;
        this.cancelStr = cancelStr;
        this.confirmStr = confirmStr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.confirm_dialog);

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.CENTER);
        }

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        content2 =findViewById(R.id.content2);

        btnCancel = findViewById(R.id.negative_btn);
        btnConfirm = findViewById(R.id.positive_btn);

        if(enablePositiveTextColor){
            btnConfirm.setTextColor(getContext().getResources().getColor(R.color.text_color_red));
        }

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        if(TextUtils.isEmpty(titleStr)){
            title.setVisibility(View.GONE);
        }else {
            title.setText(titleStr);
        }
        if(TextUtils.isEmpty(content2Str)){
            content2.setVisibility(View.GONE);
        }else {
            content2.setText(content2Str);
            content2.setVisibility(View.VISIBLE);
        }
        if(TextUtils.isEmpty(contentStr)){
            content.setVisibility(View.GONE);
        }else {
            content.setText(contentStr);
            content.setVisibility(View.VISIBLE);
        }
        btnConfirm.setText(confirmStr);
        btnCancel.setText(cancelStr);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onConfirm();
                }
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onCancel();
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


    public void setStr(String str) {
        content2Str = str;
    }

    public void enablePositiveTextColor(boolean enable) {
        this.enablePositiveTextColor = enablePositiveTextColor;
    }
}
