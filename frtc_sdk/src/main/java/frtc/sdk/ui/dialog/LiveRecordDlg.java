package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import frtc.sdk.R;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.util.BaseDialog;
import frtc.sdk.util.MathUtil;


public class LiveRecordDlg extends BaseDialog {

    private static final String TAG = LiveRecordDlg.class.getSimpleName();
    private Context mContext;

    private TextView title;
    private TextView content;
    private TextView content2;
    private CheckBox cbPwd;

    private Button btnCancel;
    private Button btnConfirm;

    private IConfirmDlgListener mListener;
    private boolean isVisibleCheck = false;

    String titleStr = "";
    String contentStr = "";
    String cancelStr = "";
    String confirmStr = "";

    public LiveRecordDlg(Context context, String titleStr, String contentStr, String cancelStr, String confirmStr) {
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

        setContentView(R.layout.live_record_dialog);

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.CENTER);
        }

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        content2 =findViewById(R.id.content2);
        cbPwd = findViewById(R.id.checkbox);

        if(isVisibleCheck){
            content2.setVisibility(View.VISIBLE);
            cbPwd.setVisibility(View.VISIBLE);
        }

        btnCancel = findViewById(R.id.negative_btn);
        btnConfirm = findViewById(R.id.positive_btn);

        title.setText(titleStr);
        content.setText(contentStr);
        btnConfirm.setText(confirmStr);
        btnCancel.setText(cancelStr);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        cbPwd.setChecked(true);
        cbPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    if(cbPwd.isChecked()){
                        LocalStore localStore = LocalStoreBuilder.getInstance(mContext).getLocalStore();
                        localStore.setLivePassword(MathUtil.getRandom(6));
                    }
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


    public void setStartLiveListener( IConfirmDlgListener listener) {
        mListener = listener;
    }

    public void setCheckVisible(boolean isVisible) {
        isVisibleCheck = isVisible;
    }

}
