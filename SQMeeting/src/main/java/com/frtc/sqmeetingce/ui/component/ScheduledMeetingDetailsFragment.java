package com.frtc.sqmeetingce.ui.component;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;
import com.frtc.sqmeetingce.ui.calendar.CalendarEvent;
import com.frtc.sqmeetingce.ui.calendar.CalendarProviderManager;
import com.frtc.sqmeetingce.ui.dialog.ConfirmCheckboxDlg;
import com.frtc.sqmeetingce.ui.dialog.EditMeetingDlg;
import com.frtc.sqmeetingce.ui.dialog.IConfirmCheckboxDlgListener;
import com.frtc.sqmeetingce.util.MeetingUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import frtc.sdk.internal.model.FrtcSDKMeetingType;
import frtc.sdk.log.Log;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.model.ScheduledMeetingListResult;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.dialog.ConfirmDlg;
import frtc.sdk.ui.dialog.IConfirmDlgListener;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;


public class ScheduledMeetingDetailsFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();

    public MainActivity mActivity;
    private Context mContext;
    private LocalStore localStore;

    private String meetingName = "";
    private String meetingNumber = "";
    private String meetingType = "";
    private String meetingStartTime = "";
    private String meetingEndTime = "";
    private String meetingPassword = "";
    private String invitationURl = "";

    private String reservationId = "";
    private String ownerId = "";
    private String ownerName = "";
    private List<String> participantUsers;

    boolean isInvitedMeeting = false;
    boolean isInstantMeeting;
    boolean isOwner;

    private Button btnCancelMeeting, btnRemoveMeeting;
    private Button btnCopyMeeting;
    private Button btnEdit;
    private ConstraintLayout clAddCalendarEvent, clMeetingRecurrence;

    private ScheduledMeeting scheduledMeeting;
    private ScheduledMeetingListResult scheduledMeetingListResult;
    private int position;
    private boolean isAddCalendar;
    private LinearLayout progressView;

    public ScheduledMeetingDetailsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Log.i(TAG, "onCreateView");

        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.scheduled_meeting_details_fragment, container, false);

        TextView tvMeetingName = view.findViewById(R.id.meeting_name);
        TextView tvMeetingTime = view.findViewById(R.id.meeting_time);
        TextView tvMeetingId = view.findViewById(R.id.meeting_id);
        TextView tvMeetingSpendTime = view.findViewById(R.id.meeting_spend_time);
        TextView tvMeetingPassword = view.findViewById(R.id.meeting_password);
        TextView tvMeetingOwner = view.findViewById(R.id.meeting_owner);

        btnCancelMeeting = view.findViewById(R.id.cancel_meeting_btn);
        btnRemoveMeeting = view.findViewById(R.id.remove_meeting_btn);
        btnCopyMeeting = view.findViewById(R.id.copy_meeting_btn);
        btnEdit =view.findViewById(R.id.button_edit);
        clAddCalendarEvent = view.findViewById(R.id.cl_add_calendar_event);
        clMeetingRecurrence = view.findViewById(R.id.cl_meeting_recurrence);
        TextView recurrenceType = view.findViewById(R.id.recurrence_type);
        progressView = view.findViewById(R.id.progressView);

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();

        if(scheduledMeeting != null){

            meetingName = scheduledMeeting.getMeeting_name();
            meetingNumber = scheduledMeeting.getMeeting_number();
            meetingType = scheduledMeeting.getMeeting_type();
            meetingStartTime = scheduledMeeting.getSchedule_start_time();
            meetingEndTime = scheduledMeeting.getSchedule_end_time();
            meetingPassword = scheduledMeeting.getMeeting_password();
            reservationId = scheduledMeeting.getReservation_id();
            ownerId = scheduledMeeting.getOwner_id();
            invitationURl = scheduledMeeting.getMeeting_url();
            ownerName = scheduledMeeting.getOwner_name();
            participantUsers = scheduledMeeting.getParticipantUsers();

            isOwner = localStore.getUserId().equals(ownerId);
            if(participantUsers != null) {
                isInvitedMeeting = participantUsers.contains(localStore.getUserId());
            }
            isInstantMeeting  = "instant".equals(meetingType);
            btnCancelMeeting.setVisibility(isOwner ? View.VISIBLE : View.GONE);
            btnRemoveMeeting.setVisibility((isOwner||isInvitedMeeting||(isInstantMeeting && isOwner)) ? View.GONE : View.VISIBLE);
            btnEdit.setVisibility((!isOwner||isInstantMeeting) ? View.GONE : View.VISIBLE);

            tvMeetingName.setText(meetingName);
            tvMeetingId.setText(meetingNumber);
            tvMeetingPassword.setText(meetingPassword);
            tvMeetingTime.setText(formatMeetingTime(meetingStartTime));
            tvMeetingSpendTime.setText(formatDurationTime());
            tvMeetingOwner.setText(ownerName);
            if(meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
                clMeetingRecurrence.setVisibility(View.VISIBLE);
                String str = MeetingUtil.formatRecurrenceTypeContent(mActivity, scheduledMeetingListResult.getRecurrenceType(), scheduledMeetingListResult.getRecurrenceInterval())
                        + " " + String.format(getString(R.string.recurrence_remain), scheduledMeetingListResult.getTotal_size()+"");
                recurrenceType.setText(str);
            }else {
                clMeetingRecurrence.setVisibility(View.GONE);
            }
        }
        setClickListener(view);

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR}, 1);
        }

        return view;
    }

    public void setScheduledMeeting(ScheduledMeeting scheduledMeeting){
        this.scheduledMeeting = scheduledMeeting;
    }

    private boolean isToday(long startTime){

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);

        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());

        if(cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)){
            int diff = cal.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);
            return diff == 0;
        }
        return false;
    }

    private String formatMeetingTime(String meetingTime){
        if(meetingTime != null && !meetingTime.isEmpty()){
            long startTime = Long.parseLong(meetingTime);
            if(isToday(startTime)){
                SimpleDateFormat todayFormat = new SimpleDateFormat(" HH:mm");
                Date dateStart = new Date(startTime);
                return mActivity.getResources().getString(R.string.today) + todayFormat.format(dateStart);
            }else{
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date dateStart = new Date(startTime);
                return simpleDateFormat.format(dateStart);
            }

        }
        return "";
    }

    private String formatScheduleTime(String meetingTime){
        if(meetingTime != null && !meetingTime.isEmpty()){
            long startTime = Long.parseLong(meetingTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dateStart = new Date(startTime);
            return simpleDateFormat.format(dateStart);
        }
        return "";
    }

    public String formatDurationTime(){

        if(meetingStartTime != null && !meetingStartTime.isEmpty() && meetingEndTime != null &&
                !meetingEndTime.isEmpty()){
            long startTime = Long.parseLong(meetingStartTime);
            long endTime = Long.parseLong(meetingEndTime);

            long durationSeconds = (endTime - startTime)/1000;
            long hour = durationSeconds / 3600;
            long seconds = durationSeconds % 60;
            long minutes = durationSeconds % 3600 / 60;
            if(hour == 0){
                return String.valueOf(minutes) + getString(R.string.time_minutes);
            }else{
                if(minutes == 0){
                    return String.valueOf(hour) + getString(R.string.time_hours);
                }else{
                    return String.valueOf(hour) + getString(R.string.time_hours) + String.valueOf(minutes) + getString(R.string.time_minutes);
                }
            }
        }
        return "";
    }

    private void setClickListener(View view) {

        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.showUserFragment(true);
            }
        });

        Button btnJoinMeeting = view.findViewById(R.id.join_meeting_btn);
        btnJoinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActivity.isInMeeeting()){
                    return;
                }
                if(scheduledMeeting != null){
                    if (!mActivity.isNetworkConnected()) {
                        mActivity.showConnectionErrorNotice();
                        return;
                    }
                    mActivity.joinMeeting(scheduledMeeting.getMeeting_number(),
                            scheduledMeeting.getMeeting_password(), localStore.getDisplayName(),"", scheduledMeeting.getMeeting_type());
                    launchMeeting();
                }
            }
        });

        btnCopyMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyMeetingInfo();
            }
        });

        btnCancelMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelMeetingConfirmDlg();
            }
        });

        btnRemoveMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemoveMeetingConfirmDlg();
            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
                    showEditMeetingDlg();
                }else {
                    mActivity.updateScheduledMeeting(scheduledMeeting);
                }

            }
        });

        clAddCalendarEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.WRITE_CALENDAR,
                                    Manifest.permission.READ_CALENDAR}, 1);
                    return;
                }

                    CalendarEvent calendarEvent = new CalendarEvent(
                            meetingName,
                            getMeetingInfo(),
                            getString(R.string.app_name),
                            Long.parseLong(meetingStartTime),
                            Long.parseLong(meetingEndTime),
                            5, null
                    );


                    int result = CalendarProviderManager.addCalendarEvent(mContext, calendarEvent);
                    if (result == 0) {
                        BaseToast.showToast(mContext, getString(R.string.meeting_add_calendar_success), Toast.LENGTH_SHORT);
                    } else {
                        BaseToast.showToast(mContext, getString(R.string.meeting_add_calendar_fail), Toast.LENGTH_SHORT);
                    }

            }
        });

        clMeetingRecurrence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.showScheduleRecurrenceMeetingListFragment(scheduledMeetingListResult);
            }
        });
    }

    private void showEditMeetingDlg() {
        EditMeetingDlg confirmDlg = new EditMeetingDlg(mActivity);
        confirmDlg.setOnDialogListener(new IEditMeetingDlgListener(){

            @Override
            public void onEditMeeting() {
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_DETAILS;
                mActivity.showEditSingleRecurrenceMeeting(scheduledMeeting, scheduledMeetingListResult, position, FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_DETAILS);
            }

            @Override
            public void onEditRecurrenceMeeting() {
                mActivity.setRecurrenceCount(scheduledMeetingListResult.getTotal_size());
                mActivity.updateScheduledMeeting(scheduledMeeting);
            }
        });
        confirmDlg.show();
    }

    private String getMeetingInfo() {
        String meetingInfoTemplate = localStore.getRealName()+" "+mContext.getResources().getString(R.string.invite_you_to_meeting)+"\n"
                + mContext.getResources().getString(R.string.meeting_theme_title) + formatInfoString(meetingName) + "\n";

        if(meetingStartTime != null && meetingEndTime != null
                && !meetingStartTime.isEmpty() && !meetingEndTime.isEmpty()){
            meetingInfoTemplate = meetingInfoTemplate
                    + mContext.getResources().getString(R.string.start_time_title) + formatScheduleTime(meetingStartTime) + "\n"
                    + mContext.getResources().getString(R.string.end_time_title) + formatScheduleTime(meetingEndTime) + "\n";
        }
        if(meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
            long startTimeMill = scheduledMeetingListResult.getRecurrenceStartTime();
            long endTimeMill = scheduledMeetingListResult.getRecurrenceEndTime();
            String[] strStartTime = MeetingUtil.timeFormat(startTimeMill, "yyyy-MM-dd HH:mm").split(" ");
            String[] strEndTime = MeetingUtil.timeFormat(endTimeMill,"yyyy-MM-dd HH:mm").split(" ");
            long recurrenceEndDay = scheduledMeetingListResult.getRecurrenceEndDay();
            String recurrenceType = scheduledMeetingListResult.getRecurrenceType();
            int recurrenceInterval = scheduledMeetingListResult.getRecurrenceInterval();
            String str = MeetingUtil.formatRecurrenceContent(mContext, recurrenceType, recurrenceInterval, scheduledMeetingListResult.getRecurrenceDaysOfWeek(),
                    scheduledMeetingListResult.getRecurrenceDaysOfMonth());
            meetingInfoTemplate = meetingInfoTemplate
                    + mContext.getResources().getString(frtc.sdk.R.string.repetition_period_title) + strStartTime[0] + " " +  "-" + " " + MeetingUtil.timeFormat(recurrenceEndDay, "yyyy-MM-dd")
                    + "," + str + "\n";
        }

        meetingInfoTemplate = meetingInfoTemplate
                + mContext.getResources().getString(R.string.meeting_number_title) + formatInfoString(meetingNumber) + "\n";

        if(meetingPassword != null && !meetingPassword.isEmpty()){
            meetingInfoTemplate = meetingInfoTemplate
                    + mContext.getResources().getString(R.string.meeting_password_title) + formatInfoString(meetingPassword) + "\n";
        }

        meetingInfoTemplate = meetingInfoTemplate
                + mContext.getResources().getString(R.string.meeting_owner_label) + formatInfoString(ownerName) + "\n"
                + "\n"
                + mContext.getResources().getString(R.string.copy_invitation_notice_without_password) + "\n";

        if(meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
            String groupMeetingUrl = scheduledMeeting.getGroupMeetingUrl();
            if(!TextUtils.isEmpty(groupMeetingUrl)){
                meetingInfoTemplate = meetingInfoTemplate
                        + mContext.getResources().getString(R.string.invitation_url_title) + "\n"
                        + formatInfoString(groupMeetingUrl) + "\n";
            }
        }else {
            if (invitationURl != null && !invitationURl.isEmpty()) {
                meetingInfoTemplate = meetingInfoTemplate
                        + mContext.getResources().getString(R.string.invitation_url_title) + "\n"
                        + formatInfoString(invitationURl) + "\n";
            }
        }

        return  meetingInfoTemplate;
    }

    private void copyMeetingInfo() {
        String meetingInfoTemplate = getMeetingInfo();

        try {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(this.mContext.getResources().getString(R.string.meeting_info), meetingInfoTemplate);
            clipboard.setPrimaryClip(clip);
            BaseToast.showToast(mContext, this.mContext.getString(R.string.copy_meeting_info), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Log.e(TAG, "copyMeetingInfo()" + e.getMessage());
        }
    }

    private String formatInfoString(String str){
        if(str == null){
            return "";
        }
        return str;
    }

    private void showCancelMeetingConfirmDlg() {
        ConfirmCheckboxDlg confirmDlg = new ConfirmCheckboxDlg(getContext(),
                getContext().getString(R.string.cancel_meeting_title1),
                "",
                getContext().getString(R.string.cancel_meeting_negative_btn),
                getContext().getString(R.string.cancel_meeting_positive_btn));

        confirmDlg.setConfirmDlgListener(new IConfirmCheckboxDlgListener() {
            @Override
            public void onConfirm(boolean isCheck) {
                deleteScheduledMeeting(isCheck);
            }
            @Override
            public void onCancel(){

            }
        });
        if(meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
            confirmDlg.setCheckVisible(true);
        }else {
            confirmDlg.setCheckVisible(false);
        }
        confirmDlg.show();
    }


    private void showRemoveMeetingConfirmDlg() {
        String title = "";
        String content = "";
        if(meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
            title = getContext().getString(R.string.remove_recurrence_meeting);
            content = getContext().getString(R.string.remove_recurrence_meeting_from_meeting_list);
        }else{
            title = getContext().getString(R.string.remove_meeting_title);
            content = getContext().getString(R.string.remove_meeting_content);
        }
        ConfirmDlg confirmDlg = new ConfirmDlg(getContext(),
                title,
                content,
                getContext().getString(R.string.cancel_meeting_negative_btn),
                getContext().getString(R.string.remove_meeting));

        confirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                if(meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
                    mActivity.removeMeetingFromMeetingList(scheduledMeeting.getGroupMeetingUrl());
                }else {
                    mActivity.removeMeetingFromMeetingList(scheduledMeeting.getMeeting_url());
                }
            }
            @Override
            public void onCancel(){

            }
        });
        confirmDlg.show();
    }


    private void deleteScheduledMeeting(boolean isCheck) {

        mActivity.deleteScheduledMeeting(scheduledMeeting.getReservation_id(), isCheck);
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER);
    }

    private void launchMeeting() {
        mActivity.StartMeetingActivity();
    }

    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER);
    }


    public void setScheduledMeetingListResult(ScheduledMeetingListResult scheduledMeetingListResult, int position) {
        this.scheduledMeetingListResult = scheduledMeetingListResult;
        this.position = position;
    }

    public void saveRecurrenceCalendar() {
        isAddCalendar = false;
        List<ScheduledMeeting> recurrenceMeetings = localStore.getScheduledRecurrenceMeetings();
        int result = -1;
        Log.d(TAG, "saveRecurrenceCalendar recurrenceMeetings.size() = "+recurrenceMeetings.size());
        for(int i = 0; i < recurrenceMeetings.size(); i++){
            CalendarEvent calendarEvent = new CalendarEvent(
                    meetingName,
                    getMeetingInfo(),
                    getString(R.string.app_name),
                    Long.parseLong(recurrenceMeetings.get(i).getSchedule_start_time()),
                    Long.parseLong(recurrenceMeetings.get(i).getSchedule_end_time()),
                    5, null
            );

            result = CalendarProviderManager.addCalendarEvent(mContext, calendarEvent);
        }
        Log.d(TAG, "saveRecurrenceCalendar result = "+result);
        if (result == 0) {
            BaseToast.showToast(mContext, getString(R.string.meeting_add_calendar_success), Toast.LENGTH_SHORT);
        } else {
            BaseToast.showToast(mContext, getString(R.string.meeting_add_calendar_fail), Toast.LENGTH_SHORT);
        }
        progressView.setVisibility(View.GONE);
    }

    public boolean isAddCalendar(){
        return isAddCalendar;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
