package frtc.sdk.ui.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import frtc.sdk.R;
import frtc.sdk.internal.model.MeetingMediaStatsWrapper;
import frtc.sdk.internal.model.MediaStatsInfo;
import frtc.sdk.log.Log;
import frtc.sdk.ui.FrtcMeetingActivity;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.util.BaseDialog;
import frtc.sdk.util.LanguageUtil;

public class MeetingDetailsDlg extends BaseDialog {

    private final String TAG = MeetingDetailsDlg.class.getSimpleName();

    private Context mContext;
    private FrtcMeetingActivity mActivity;
    private LocalStore localStore;

    private TextView tvMeetingName;
    private Button btMeetingInfo;
    private Button btNetworkStat;
    private ConstraintLayout tableMeetingInfo;
    private ConstraintLayout tableNetworkStat;

    private LinearLayout statistics;
    private TextView tvMeetingNumber;
    private TextView tvHost;
    private TextView tvMeetingPassword;

    private boolean isURLAvailable;

    private TextView tvInvitationLinkTitle;
    private TextView tvInvitationLink;

    private ImageView ivCopyMeetingInfo;
    private TextView tvCopyMeetingInfo;

    private TextView tvDelay;
    private TextView rateSendTextView;
    private TextView rateRecvTextView;
    private TextView audioSendTextView;
    private TextView audioRecvTextView;
    private TextView videoSendTextView;
    private TextView videoRecvTextView;
    private TextView shareSendTextView;
    private TextView shareRecvTextView;

    public MeetingDetailsDlg(Context context, FrtcMeetingActivity mActivity) {
        super(context, R.style.DialogTheme);
        mContext = context;
        this.mActivity = mActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meeting_details_dialog);

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
        }

        tvMeetingName = findViewById(R.id.meeting_name);

        btMeetingInfo = findViewById(R.id.meeting_info);
        btMeetingInfo.setSelected(true);
        btNetworkStat = findViewById(R.id.network_stat);

        statistics = findViewById(R.id.statistics);
        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mActivity.showMeetingStatsDlg();
            }
        });

        btMeetingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoTable(true);
            }
        });
        btNetworkStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoTable(false);
            }
        });

        ivCopyMeetingInfo = findViewById(R.id.iv_copy_text);
        tvCopyMeetingInfo = findViewById(R.id.copy_text);
        ivCopyMeetingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!LanguageUtil.isSharePreferenceLan(mContext)) {
                    LanguageUtil.setLanguage(mContext);
                }
                copyMeetingInfo();
            }
        });
        tvCopyMeetingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!LanguageUtil.isSharePreferenceLan(mContext)) {
                    LanguageUtil.setLanguage(mContext);
                }
                copyMeetingInfo();
            }
        });
        tableMeetingInfo = findViewById(R.id.meeting_info_table);
        tableNetworkStat = findViewById(R.id.network_condition_table);

        tvMeetingNumber = findViewById(R.id.meeting_number_content);
        tvHost = findViewById(R.id.host_content);
        tvMeetingPassword = findViewById(R.id.meeting_password_content);

        tvInvitationLinkTitle = findViewById(R.id.invitation_link);
        tvInvitationLink = findViewById(R.id.invitation_link_content);

        localStore = LocalStoreBuilder.getInstance(mContext).getLocalStore();

        tvMeetingName.setText(localStore.getMeetingName());
        tvMeetingNumber.setText(localStore.getMeetingID());
        tvHost.setText(localStore.getMeetingOwnerName());
        tvMeetingPassword.setText(localStore.getMeetingPassword());

        String meetingURl = localStore.getMeetingURl();
        isURLAvailable = (meetingURl != null && !meetingURl.isEmpty());
        if(isURLAvailable){
            tvInvitationLinkTitle.setVisibility(View.VISIBLE);
            tvInvitationLink.setVisibility(View.VISIBLE);
            tvInvitationLink.setText(meetingURl);
        }else{
            tvInvitationLinkTitle.setVisibility(View.INVISIBLE);
            tvInvitationLink.setVisibility(View.INVISIBLE);
            tvInvitationLink.setText("");
        }

        tvDelay = findViewById(R.id.delay_content);


        rateSendTextView = findViewById(R.id.rate_tx);
        rateRecvTextView = findViewById(R.id.rate_rx);
        audioSendTextView = findViewById(R.id.audio_tx);
        audioRecvTextView = findViewById(R.id.audio_rx);
        videoSendTextView = findViewById(R.id.video_tx);
        videoRecvTextView = findViewById(R.id.video_rx);
        shareSendTextView = findViewById(R.id.share_tx);
        shareRecvTextView = findViewById(R.id.share_rx);

        showInfoTable(true);
    }

    private String formatInfoString(String str){
        if(str == null){
            return "";
        }
        return str;
    }

    private void copyMeetingInfo() {

        String meetingInfoTemplate = "";

        if(localStore.isLogged()){
            meetingInfoTemplate = meetingInfoTemplate + localStore.getRealName()
                    +" "+mContext.getResources().getString(R.string.invite_you_to_meeting)+"\n";
        }else{
            meetingInfoTemplate = meetingInfoTemplate
                    +" "+mContext.getResources().getString(R.string.guest_invite_you_to_meeting)+"\n";
        }

        meetingInfoTemplate = meetingInfoTemplate
                + mContext.getResources().getString(R.string.meeting_theme_title) + localStore.getMeetingName() + "\n";

        long startTime = localStore.getScheduleStartTime();
        long endTime = localStore.getScheduleEndTime();
        if(startTime != 0 && endTime !=0){
            meetingInfoTemplate = meetingInfoTemplate
                    + mContext.getResources().getString(R.string.start_time_title) + formatMeetingTime(startTime) + "\n"
                    + mContext.getResources().getString(R.string.end_time_title) + formatMeetingTime(endTime) + "\n";
        }

        meetingInfoTemplate = meetingInfoTemplate
                + mContext.getResources().getString(R.string.meeting_number_title) + formatInfoString(localStore.getMeetingID()) + "\n";

        String password = localStore.getMeetingPassword();
        if(password != null && !password.isEmpty()){
            meetingInfoTemplate = meetingInfoTemplate
                    + mContext.getResources().getString(R.string.meeting_password_title) + formatInfoString(localStore.getMeetingPassword()) + "\n"
                    + "\n"
                    + mContext.getResources().getString(R.string.copy_invitation_notice_with_password) + "\n";
        }else {
            meetingInfoTemplate = meetingInfoTemplate
                    + "\n"
                    + mContext.getResources().getString(R.string.copy_invitation_notice_without_password) + "\n";
        }

        String url = localStore.getMeetingURl();
        if(url != null && !url.isEmpty()){
            meetingInfoTemplate = meetingInfoTemplate
                    + mContext.getResources().getString(R.string.invitation_url_title) + "\n"
                    + formatInfoString(localStore.getMeetingURl()) + "\n";
        }

        try {
            ClipboardManager clipboard = (ClipboardManager) this.mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(this.mContext.getResources().getString(R.string.meeting_info), meetingInfoTemplate);
            clipboard.setPrimaryClip(clip);
            BaseToast.showToast(this.mContext, this.mContext.getString(R.string.copy_meeting_info), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Log.e(TAG, "copyMeetingInfo()" + e.getMessage());
        }
    }

    private String formatMeetingTime(long meetingTime){
        if(meetingTime != 0){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dateStart = new Date(meetingTime);
            return simpleDateFormat.format(dateStart);
        }
        return "";
    }

    private void showInfoTable(boolean isMeetingInfoVisible){

        btMeetingInfo.setSelected(isMeetingInfoVisible);
        btNetworkStat.setSelected(!isMeetingInfoVisible);

        tableMeetingInfo.setVisibility(isMeetingInfoVisible ? View.VISIBLE :View.INVISIBLE);
        tableNetworkStat.setVisibility(isMeetingInfoVisible ? View.INVISIBLE :View.VISIBLE);

    }

    public void updateStatistics(MeetingMediaStatsWrapper stat){
        int delay = 0;
        int rateRecv = 0;
        int rateSend = 0;

        int audioSend = 0;
        int audioRecv = 0;
        int audioRecvPackageLostCount = 0;
        int audioSendPackageLostCount = 0;
        int audioRecvPackageLostRate = 0;
        int audioSendPackageLostRate = 0;

        int videoSend = 0;
        int videoRecv = 0;
        int videoSendPackageLostCount = 0;
        int videoRecvPackageLostCount = 0;
        int videoRecvPackageLostRate = 0;
        int videoSendPackageLostRate = 0;

        int contentSend = 0;
        int contentRecv = 0;
        int contentRecvPackageLostCount = 0;
        int contentSendPackageLostCount = 0;
        int contentRecvPackageLostRate = 0;
        int contentSendPackageLostRate = 0;
        if(stat.signalStatistics != null){
            if (stat.signalStatistics.callRate > 100000) {
                rateSend = stat.signalStatistics.callRate / 100000;
                rateRecv = stat.signalStatistics.callRate % 100000;
            }else{
                rateSend = stat.signalStatistics.callRate;
                rateRecv = stat.signalStatistics.callRate;
            }
        }
        if(stat.mediaStatistics != null){
            if(stat.mediaStatistics.aps != null && !stat.mediaStatistics.aps.isEmpty()){
                for(MediaStatsInfo aps : stat.mediaStatistics.aps){
                    if("local".equals(aps.getParticipantName())){
                        delay = aps.getRoundTripTime();
                        audioSend += aps.getRtpActualBitRate();
                        if(aps.getPackageLossRate() > 0) {
                            audioSendPackageLostRate += aps.getPackageLossRate();
                            audioSendPackageLostCount++;
                        }
                    }
                }
            }
            if(stat.mediaStatistics.apr != null && !stat.mediaStatistics.apr.isEmpty()){
                for(MediaStatsInfo apr : stat.mediaStatistics.apr){
                    audioRecv += apr.getRtpActualBitRate();
                    if(apr.getPackageLossRate() > 0) {
                        audioRecvPackageLostRate += apr.getPackageLossRate();
                        audioRecvPackageLostCount++;
                    }
                }
            }
            if(stat.mediaStatistics.vpr != null && !stat.mediaStatistics.vpr.isEmpty()){
                for(MediaStatsInfo vpr : stat.mediaStatistics.vpr){
                    videoRecv += vpr.getRtpActualBitRate();
                    if(vpr.getPackageLossRate() > 0) {
                        videoRecvPackageLostRate += vpr.getPackageLossRate();
                        videoRecvPackageLostCount++;
                    }
                }
            }
            if(stat.mediaStatistics.vps != null && !stat.mediaStatistics.vps.isEmpty()){
                for(MediaStatsInfo vps : stat.mediaStatistics.vps){
                    if("local".equals(vps.getParticipantName())){
                        videoSend += vps.getRtpActualBitRate();
                        if(vps.getPackageLossRate() > 0) {
                            videoSendPackageLostRate += vps.getPackageLossRate();
                            videoSendPackageLostCount++;
                        }
                    }
                }
            }
            if(stat.mediaStatistics.vcs != null && !stat.mediaStatistics.vcs.isEmpty()){
                for(MediaStatsInfo vcs : stat.mediaStatistics.vcs){
                    if("local".equals(vcs.getParticipantName())){
                        contentSend += vcs.getRtpActualBitRate();
                        if(vcs.getPackageLossRate() > 0) {
                            contentSendPackageLostRate += vcs.getPackageLossRate();
                            contentSendPackageLostCount++;
                        }
                    }
                }
            }
            if(stat.mediaStatistics.vcr != null && !stat.mediaStatistics.vcr.isEmpty()){
                for(MediaStatsInfo vcr : stat.mediaStatistics.vcr){
                    contentRecv += vcr.getRtpActualBitRate();
                    if(vcr.getPackageLossRate() > 0) {
                        contentRecvPackageLostRate += vcr.getPackageLossRate();
                        contentRecvPackageLostCount++;
                    }
                }
            }
            if(audioSendPackageLostCount > 0) {
                audioSendPackageLostRate = audioSendPackageLostRate/audioSendPackageLostCount;
            }
            if(audioRecvPackageLostCount > 0) {
                audioRecvPackageLostRate = audioRecvPackageLostRate/audioRecvPackageLostCount;
            }
            if(videoSendPackageLostCount > 0) {
                videoSendPackageLostRate = videoSendPackageLostRate/videoSendPackageLostCount;
            }
            if(videoRecvPackageLostCount > 0) {
                videoRecvPackageLostRate = videoRecvPackageLostRate/videoRecvPackageLostCount;
            }
            if(contentSendPackageLostCount > 0) {
                contentSendPackageLostRate = contentSendPackageLostRate/contentSendPackageLostCount;
            }
            if(contentRecvPackageLostCount > 0) {
                contentRecvPackageLostRate = contentRecvPackageLostRate/contentRecvPackageLostCount;
            }
            tvDelay.setText(String.valueOf(delay) + " ms");
            rateSendTextView.setText(String.valueOf(rateSend));
            rateRecvTextView.setText(String.valueOf(rateRecv));
            audioSendTextView.setText(audioSend + " ( " + audioSendPackageLostRate + "% )");
            audioRecvTextView.setText( audioRecv + " ( " + audioRecvPackageLostRate + "% )");
            videoSendTextView.setText(videoSend + " ( " + videoSendPackageLostRate + "% )");
            videoRecvTextView.setText(videoRecv + " ( " + videoRecvPackageLostRate + "% )");
            shareSendTextView.setText(contentSend + " ( " + contentSendPackageLostRate + "%)");
            shareRecvTextView.setText(contentRecv + " ( " + contentRecvPackageLostRate + "%)");
        }
    }

}
