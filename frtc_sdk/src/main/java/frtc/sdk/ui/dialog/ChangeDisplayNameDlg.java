package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import frtc.sdk.R;
import frtc.sdk.util.BaseDialog;
import frtc.sdk.ui.model.ParticipantInfo;


public class ChangeDisplayNameDlg extends BaseDialog {

    private final String TAG = ChangeDisplayNameDlg.class.getSimpleName();
    private EditText etName;
    private Button btnClear;
    private Context context;
    private ParticipantInfo participantInfo;
    private IMeetingControlDlgListener mListener;
    private ImageView notice;
    private TextView nameErrorView;



    public ChangeDisplayNameDlg(Context context, ParticipantInfo participantInfo) {
        super(context, R.style.DialogTheme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.participantInfo = participantInfo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.change_name_dialog);

        btnClear = findViewById(R.id.clear_btn);
        etName = findViewById(R.id.name_et);
        etName.setText(participantInfo.getDisplayName());

        notice = findViewById(R.id.notice);
        nameErrorView  = findViewById(R.id.name_error);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText("");
            }
        });

        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String input = etName.getText().toString();
                boolean visible = hasFocus && input.length() > 0;
                btnClear.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = etName.getText().toString();
                btnClear.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
                if(input.length() >= 48){
                    notice.setVisibility(View.VISIBLE);
                    nameErrorView.setVisibility(View.VISIBLE);
                    nameErrorView.setText(R.string.change_display_name_length_notice);
                }else {
                    notice.setVisibility(View.GONE);
                    nameErrorView.setVisibility(View.GONE);
                    nameErrorView.setText("");
                }
            }

        });

        Button btnNegative = findViewById(R.id.negative_btn);
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button btnPositive = findViewById(R.id.positive_btn);

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etName.getText().toString();
                if(TextUtils.isEmpty(input)){
                    notice.setVisibility(View.VISIBLE);
                    nameErrorView.setVisibility(View.VISIBLE);
                    nameErrorView.setText(R.string.change_display_name_empty_notice);
                    return;
                }
                if(mListener != null){
                    mListener.onClickConfirmChangeName(participantInfo.getUuid(), etName.getText().toString());
                }
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

