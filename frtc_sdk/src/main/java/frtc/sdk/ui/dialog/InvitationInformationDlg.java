package frtc.sdk.ui.dialog;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import frtc.sdk.model.CreateScheduledMeetingResult;
import frtc.sdk.R;
import frtc.sdk.internal.model.FrtcSDKMeetingType;
import frtc.sdk.log.Log;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.util.BaseDialog;
import frtc.sdk.util.MeetingUtil;


public class InvitationInformationDlg extends BaseDialog {

    private final String TAG = InvitationInformationDlg.class.getSimpleName();

    private Context mContext;
    private Activity mActivity;
    private LocalStore localStore;

    private TextView meetingName;
    private TextView userName;
    private TextView inviteNotice;
    private TextView meetingTheme;
    private TextView startTime;
    private TextView startTimeTitle;
    private TextView endTime;
    private TextView endTimeTitle;
    private TextView meetingNumber;
    private TextView meetingPasswordTitle;
    private TextView meetingPassword;
    private TextView noticeWord;
    private TextView urlTitle;
    private TextView url;
    private LinearLayout copyBtn;
    private TextView repetitionPeriodTitle;
    private TextView repetitionPeriod;
    CreateScheduledMeetingResult createScheduledMeetingResult;

    public InvitationInformationDlg(Context context, Activity activity, CreateScheduledMeetingResult createScheduledMeetingResult) {
        super(context, R.style.DialogTheme);
        mContext = context;
        this.mActivity = activity;
        this.createScheduledMeetingResult = createScheduledMeetingResult;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.invitation_information_dialog);

        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        }

        localStore = LocalStoreBuilder.getInstance(mContext).getLocalStore();

        meetingName = findViewById(R.id.meeting_name);
        userName = findViewById(R.id.user_name);
        inviteNotice = findViewById(R.id.invite_to_meeting);
        meetingTheme = findViewById(R.id.meeting_theme);
        startTime = findViewById(R.id.start_time);
        startTimeTitle = findViewById(R.id.start_time_title);
        endTime = findViewById(R.id.end_time);
        endTimeTitle = findViewById(R.id.end_time_title);
        meetingNumber = findViewById(R.id.meeting_number);
        meetingPasswordTitle = findViewById(R.id.meeting_password_title);
        meetingPassword = findViewById(R.id.meeting_password);
        noticeWord = findViewById(R.id.notice_word);
        urlTitle = findViewById(R.id.url_title);
        url = findViewById(R.id.url);
        repetitionPeriod = findViewById(R.id.repetition_period);

        String password = "";
        String meetingURl = "";
        long startTimeMill = 0;
        long endTimeMill = 0;
        if(createScheduledMeetingResult == null) {
            meetingName.setText(localStore.getMeetingName());
            userName.setText(localStore.getRealName());
            meetingTheme.setText(localStore.getMeetingName());

            if (localStore.isLogged()) {
                inviteNotice.setText(mContext.getResources().getString(R.string.invite_you_to_meeting));
            } else {
                inviteNotice.setText(mContext.getResources().getString(R.string.guest_invite_you_to_meeting));
            }

            startTimeMill = localStore.getScheduleStartTime();
            endTimeMill = localStore.getScheduleEndTime();
            meetingNumber.setText(localStore.getMeetingID());
            password = localStore.getMeetingPassword();
            meetingURl = localStore.getMeetingURl();
        }else{
            meetingName.setText(createScheduledMeetingResult.getMeeting_name());
            userName.setText(createScheduledMeetingResult.getOwner_name());
            meetingTheme.setText(createScheduledMeetingResult.getMeeting_name());

            if (localStore.isLogged()) {
                inviteNotice.setText(mContext.getResources().getString(R.string.invite_you_to_meeting));
            } else {
                inviteNotice.setText(mContext.getResources().getString(R.string.guest_invite_you_to_meeting));
            }

            startTimeMill = createScheduledMeetingResult.getSchedule_start_time();
            endTimeMill = createScheduledMeetingResult.getSchedule_end_time();
            meetingNumber.setText(createScheduledMeetingResult.getMeeting_number());
            password = createScheduledMeetingResult.getMeeting_password();

            if(createScheduledMeetingResult.getMeeting_type().equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
                repetitionPeriod.setVisibility(View.VISIBLE);
                String[] strStartTime = formatMeetingTime(startTimeMill).split(" ");
                String[] strEndTime = formatMeetingTime(endTimeMill).split(" ");
                long recurrenceEndDay = createScheduledMeetingResult.getRecurrenceEndDay();
                String recurrenceType = createScheduledMeetingResult.getRecurrence_type();
                int recurrenceInterval = createScheduledMeetingResult.getRecurrenceInterval();
                String str = MeetingUtil.formatRecurrenceContent(mContext, recurrenceType, recurrenceInterval, createScheduledMeetingResult.getRecurrenceDaysOfWeek(),
                        createScheduledMeetingResult.getRecurrenceDaysOfMonth());
                repetitionPeriod.setText(mContext.getResources().getString(R.string.repetition_period_title) + strStartTime[0] + " " +  "-" + " " + formatMeetingDay(recurrenceEndDay) + " " + strStartTime[1] + "-" + strEndTime[1] + "," + str);

                meetingURl = createScheduledMeetingResult.getGroupMeetingUrl();
            }else{
                meetingURl = createScheduledMeetingResult.getMeeting_url();
            }
        }
        if (startTimeMill != 0 && endTimeMill != 0) {
            endTimeTitle.setVisibility(View.VISIBLE);
            endTime.setVisibility(View.VISIBLE);
            startTimeTitle.setVisibility(View.VISIBLE);
            startTime.setVisibility(View.VISIBLE);
            startTime.setText(formatMeetingTime(startTimeMill));
            endTime.setText(formatMeetingTime(endTimeMill));
        } else {
            endTimeTitle.setVisibility(View.GONE);
            endTime.setVisibility(View.GONE);
            startTimeTitle.setVisibility(View.GONE);
            startTime.setVisibility(View.GONE);
        }


        if (password != null && !password.isEmpty()) {
            meetingPassword.setVisibility(View.VISIBLE);
            meetingPasswordTitle.setVisibility(View.VISIBLE);
            meetingPassword.setText(password);
            noticeWord.setText(getContext().getResources().getString(R.string.copy_invitation_notice_with_password));
        } else {
            meetingPassword.setVisibility(View.GONE);
            meetingPasswordTitle.setVisibility(View.GONE);
            noticeWord.setText(getContext().getResources().getString(R.string.copy_invitation_notice_without_password));
        }
        if (meetingURl != null && !meetingURl.isEmpty()) {
            url.setText(meetingURl);
            url.setVisibility(View.VISIBLE);
            urlTitle.setVisibility(View.VISIBLE);
        } else {
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

        String meetingInfoTemplate = "";

        meetingInfoTemplate = userName.getText().toString() + " " + inviteNotice.getText().toString() + "\n"
                + mContext.getResources().getString(R.string.meeting_theme_title) + meetingTheme.getText().toString() + "\n"
                + mContext.getResources().getString(R.string.start_time_title) + startTime.getText().toString() + "\n"
                + mContext.getResources().getString(R.string.end_time_title) + endTime.getText().toString() + "\n";

        if(repetitionPeriod.getVisibility() == View.VISIBLE){
            meetingInfoTemplate += repetitionPeriod.getText().toString() + "\n";
        }
        meetingInfoTemplate += mContext.getResources().getString(R.string.meeting_number_title) + meetingNumber.getText().toString() + "\n";

        if(meetingPassword.getVisibility() == View.VISIBLE){
            meetingInfoTemplate += mContext.getResources().getString(R.string.meeting_password_title) + meetingPassword.getText().toString() + "\n"
                    + "\n"
                    + mContext.getResources().getString(R.string.copy_invitation_notice_with_password) + "\n";
        }else{
            meetingInfoTemplate += "\n"
                    + mContext.getResources().getString(R.string.copy_invitation_notice_without_password) + "\n";
        }

        if(url.getVisibility() == View.VISIBLE){
            meetingInfoTemplate += mContext.getResources().getString(R.string.invitation_url_title) + "\n"
                    + url.getText().toString() + "\n";
        }

        try {
            ClipboardManager clipboard = (ClipboardManager) this.mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = android.content.ClipData.newPlainText(this.mContext.getResources().getString(R.string.meeting_info), meetingInfoTemplate);
            clipboard.setPrimaryClip(clip);
            dismiss();
            BaseToast.showToast(this.mContext, this.mContext.getString(R.string.copy_meeting_notice), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Log.e(TAG, "copyInvitationInfo()" + e.getMessage());
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


    private String formatMeetingDay(long meetingTime){
        if(meetingTime != 0){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateStart = new Date(meetingTime);
            return simpleDateFormat.format(dateStart);
        }
        return "";
    }
}
