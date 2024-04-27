package com.frtc.sqmeetingce.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.frtc.sqmeetingce.R;

import frtc.sdk.util.BaseDialog;

public class ConfirmCheckboxDlg extends BaseDialog {

    private static final String TAG = ConfirmCheckboxDlg.class.getSimpleName();
    private Context mContext;

    private TextView title;
    private TextView content;
    private TextView content2;
    private CheckBox checkbox;

    private Button btnCancel;
    private Button btnConfirm;

    private IConfirmCheckboxDlgListener mListener;
    private boolean isVisibleCheck = false;

    String titleStr = "";
    String contentStr = "";
    String cancelStr = "";
    String confirmStr = "";

    public ConfirmCheckboxDlg(Context context, String titleStr, String contentStr, String cancelStr, String confirmStr) {
        super(context, R.style.DialogTheme);
        mContext = context;
        this.titleStr = titleStr;
        this.contentStr = contentStr;
        this.cancelStr = cancelStr;
        this.confirmStr = confirmStr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.confirm_checkbox_dialog);

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.CENTER);
        }

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        content2 =findViewById(R.id.content2);
        checkbox = findViewById(R.id.checkbox);
        if(isVisibleCheck){
            checkbox.setVisibility(View.VISIBLE);
        }else {
            checkbox.setVisibility(View.GONE);
        }

        btnCancel = findViewById(R.id.negative_btn);
        btnConfirm = findViewById(R.id.positive_btn);

        title.setText(titleStr);
        content.setText(contentStr);
        btnConfirm.setText(confirmStr);
        btnCancel.setText(cancelStr);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        checkbox.setChecked(true);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onConfirm(isVisibleCheck && checkbox.isChecked());
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


    public void setConfirmDlgListener( IConfirmCheckboxDlgListener listener) {
        mListener = listener;
    }

    public void setCheckVisible(boolean isVisible) {
        isVisibleCheck = isVisible;
    }

}
