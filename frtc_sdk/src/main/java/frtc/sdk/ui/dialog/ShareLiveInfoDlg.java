package frtc.sdk.ui.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import frtc.sdk.R;
import frtc.sdk.log.Log;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.util.BaseDialog;

public class ShareLiveInfoDlg extends BaseDialog {

    private final String TAG = InvitationInformationDlg.class.getSimpleName();

    private Context mContext;
    private LocalStore localStore;

    private TextView shareTitle;
    private TextView inviteNotice;
    private TextView livePasswordTitle;
    private TextView livePassword;
    private TextView urlTitle;
    private TextView url;

    private LinearLayout copyBtn;

    public ShareLiveInfoDlg(Context context) {
        super(context, R.style.DialogTheme);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_live_info_dialog);

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        }

        localStore = LocalStoreBuilder.getInstance(mContext).getLocalStore();

        shareTitle = findViewById(R.id.share_title);
        inviteNotice = findViewById(R.id.invite_meeting_live);

        livePasswordTitle = findViewById(R.id.live_password_title);
        livePassword = findViewById(R.id.live_password);
        urlTitle = findViewById(R.id.url_title);
        url = findViewById(R.id.url);

        inviteNotice.setText(localStore.getRealName() + " " + String.format("" + mContext.getString(R.string.share_live_dlg_content), localStore.getMeetingName()));


        String password = localStore.getLivePassword();
        if(!TextUtils.isEmpty(password)){
            livePassword.setVisibility(View.VISIBLE);
            livePasswordTitle.setVisibility(View.VISIBLE);
            livePassword.setText(localStore.getLivePassword());
        }else{
            livePassword.setVisibility(View.GONE);
            livePasswordTitle.setVisibility(View.GONE);
        }

        String liveURl = localStore.getLiveMeetingUrl();
        if(liveURl != null && !liveURl.isEmpty()){
            url.setText(liveURl);
            url.setVisibility(View.VISIBLE);
            urlTitle.setVisibility(View.VISIBLE);
        }else{
            url.setVisibility(View.GONE);
            urlTitle.setVisibility(View.GONE);
        }

        copyBtn = findViewById(R.id.copy_btn);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyInvitationInfo();
                dismiss();
            }
        });

    }

    private void copyInvitationInfo() {

        String shareInfoTemplate = "";

        shareInfoTemplate = shareInfoTemplate + localStore.getRealName() + " "
                    +String.format("" + mContext.getString(R.string.share_live_dlg_content), localStore.getMeetingName())+"\n";



        String url = localStore.getLiveMeetingUrl();
        if(url != null && !url.isEmpty()){
            shareInfoTemplate = shareInfoTemplate
                    + mContext.getResources().getString(R.string.share_live_url_title) + "\n"
                    + formatInfoString(localStore.getLiveMeetingUrl()) + "\n";
        }

        String password = localStore.getLivePassword();
        if(password != null && !password.isEmpty()){
            shareInfoTemplate = shareInfoTemplate
                    + mContext.getResources().getString(R.string.share_live_pwd_title) + formatInfoString(localStore.getLivePassword()) + "\n"
                    + "\n";
        }
        try {
            ClipboardManager clipboard = (ClipboardManager) this.mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = android.content.ClipData.newPlainText(this.mContext.getResources().getString(R.string.live_info), shareInfoTemplate);
            clipboard.setPrimaryClip(clip);
            dismiss();
            BaseToast.showToast(this.mContext, this.mContext.getString(R.string.copy_live_notice), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Log.e(TAG, "copyInvitationInfo()" + e.getMessage());
        }
    }

    private String formatInfoString(String str){
        if(str == null){
            return "";
        }
        return str;
    }

}

