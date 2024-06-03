
package com.frtc.sqmeetingce.ui.component;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;
import com.frtc.sqmeetingce.ui.dialog.RecurrenceItemDlg;
import com.frtc.sqmeetingce.ui.dialog.RecurrenceMeetingsMoreDlg;
import com.frtc.sqmeetingce.util.MeetingUtil;

import java.util.ArrayList;

import frtc.sdk.log.Log;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.model.ScheduledMeetingListResult;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.dialog.ConfirmDlg;
import frtc.sdk.ui.dialog.IConfirmDlgListener;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.model.ScheduledMeetingSetting;
import frtc.sdk.ui.store.LocalStoreBuilder;

public class ScheduleRecurrenceMeetingListFragment extends BaseFragment implements RecurrenceMeetingAdapter.OnItemClickListener{

    protected final String TAG = this.getClass().getSimpleName();
    private LocalStore localStore;
    public MainActivity mActivity;

    private RecyclerView recurrenceMeetingListView;
    private RecurrenceMeetingAdapter recurrenceMeetingAdapter;
    private ArrayList<ScheduledMeeting> scheduledMeetings;
    private Context mContext;
    private int selectPosition = -1;
    private TextView tvRecurrenceType;
    private TextView tvRecurrenceContent;
    private TextView tvRecurrenceEnd;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.schedule_recurrence_meeting_list_fragment, container, false);

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();

        init(view);

        return view;
    }

    private void init(View view) {
        TextView tvMeetingName = view.findViewById(R.id.meeting_name);
        tvRecurrenceType = view.findViewById(R.id.recurrence_type);
        tvRecurrenceContent = view.findViewById(R.id.recurrence_content);
        tvRecurrenceEnd = view.findViewById(R.id.recurrence_end);

        recurrenceMeetingListView = view.findViewById(R.id.recurrence_meeting_listView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recurrenceMeetingListView.setLayoutManager(layoutManager);

        scheduledMeetings = localStore.getScheduledMeetingSetting().getRecurrenceMeetings();
        recurrenceMeetingAdapter = new RecurrenceMeetingAdapter(mActivity, scheduledMeetings);
        recurrenceMeetingAdapter.setOnItemClickedListener(this);

        recurrenceMeetingListView.setAdapter(recurrenceMeetingAdapter);
        recurrenceMeetingAdapter.notifyDataSetChanged();

        if(scheduledMeetings != null && !scheduledMeetings.isEmpty() && scheduledMeetings.get(0) != null){
            tvMeetingName.setText(scheduledMeetings.get(0).getMeeting_name());
        }

        updateRecurrenceText();

        setClickListener(view);
    }

    private void updateRecurrenceText(){
        ScheduledMeetingSetting setting = localStore.getScheduledMeetingSetting();
        String recurrenceType = setting.getRecurrenceType();
        String recurrenceContent = MeetingUtil.formatRecurrenceContent(mActivity, recurrenceType, setting.getRecurrenceInterval(), setting.getRecurrenceDaysOfWeek(),
                setting.getRecurrenceDaysOfMonth());
        String recurrenceTypeContent = MeetingUtil.formatRecurrenceTypeContent(mActivity, recurrenceType, setting.getRecurrenceInterval());
        long recurrenceEndDay = setting.getRecurrenceEndDay();
        int count = scheduledMeetings.size();

        String endDay = MeetingUtil.timeFormat(recurrenceEndDay, "yyyy-MM-dd");

        tvRecurrenceType.setText(recurrenceTypeContent);
        tvRecurrenceContent.setText(String.format(mActivity.getString(R.string.recurrence_content), recurrenceContent));
        String format = String.format(mActivity.getResources().getString(R.string.recurrence_end), endDay, count+"");
        tvRecurrenceEnd.setText(format);
    }


    private void setClickListener(View view){
        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.showScheduledRecurrenceMeetingDetails();
            }
        });

        Button btnMore = view.findViewById(R.id.button_more);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecurrenceMeetingsMoreDlg();
            }
        });

        Button btnJoinMeeting = view.findViewById(R.id.join_meeting_btn);
        btnJoinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActivity.isInMeeting()){
                    return;
                }
                if(scheduledMeetings.get(0) != null){
                    if (!mActivity.isNetworkConnected()) {
                        mActivity.showConnectionErrorNotice();
                        return;
                    }
                    mActivity.joinMeeting(scheduledMeetings.get(0).getMeeting_number(),
                            scheduledMeetings.get(0).getMeeting_password(), localStore.getDisplayName(),"",scheduledMeetings.get(0).getMeeting_type() );
                    mActivity.StartMeetingActivity();
                }
            }
        });


    }

    private void showRecurrenceMeetingsMoreDlg() {
        if(scheduledMeetings == null || scheduledMeetings.size() == 0){
            Log.d(TAG,"scheduledMeetings size is 0");
            return;
        }
        boolean isInvitedMeeting = !localStore.getUserId().equals(scheduledMeetings.get(0).getOwner_id());
        RecurrenceMeetingsMoreDlg confirmDlg = new RecurrenceMeetingsMoreDlg(mActivity, isInvitedMeeting);
        confirmDlg.setOnDialogListener(new IRecurrenceMeetingsMoreDlgListener(){


            @Override
            public void onCopyInvitation() {
                copyMeetingInfo();
            }

            @Override
            public void onEditRecurrenceMeeting() {
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_UPDATE_SCHEDULED_MEETING);
            }

            @Override
            public void onCancelRecurrenceMeeting() {
                showCancelMeetingConfirmDlg();
            }
        });
        confirmDlg.show();
    }


    @Override
    public void onBack() {
        mActivity.showScheduledRecurrenceMeetingDetails();
    }


    @Override
    public void onItemClicked(ScheduledMeeting selectedMeetingCall, int position) {
        selectPosition = position;
        showItemDlg();
    }

    private void showItemDlg() {
        RecurrenceItemDlg recurrenceItemDlg = new RecurrenceItemDlg(mActivity);
        recurrenceItemDlg.setOnDialogListener(new IRecurrenceItemDlgListener(){

            @Override
            public void onEditMeeting() {
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_SCHEDULE_RECURRENCE_MEETING_LIST;
                mActivity.showEditSingleRecurrenceMeeting(selectPosition);
            }

            @Override
            public void onCancelMeeting() {
                showCancelItemMeetingConfirmDlg();
            }

        });
        recurrenceItemDlg.show();
    }

    private void showCancelItemMeetingConfirmDlg() {
        ConfirmDlg confirmDlg = new ConfirmDlg(getContext(),
                getContext().getString(R.string.cancel_meeting_title1),
                "",
                getContext().getString(R.string.cancel_meeting_negative_btn),
                getContext().getString(R.string.cancel_meeting_positive_btn));

        confirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                mActivity.deleteScheduledMeeting(scheduledMeetings.get(selectPosition).getReservation_id(), false);
            }
            @Override
            public void onCancel(){

            }
        });
        confirmDlg.enablePositiveTextColor(true);
        confirmDlg.show();
    }

    public void updateScheduledRecurrenceMeetingListview(){
        recurrenceMeetingAdapter.notifyDataSetChanged();
        updateRecurrenceText();
    }

    private void deleteScheduledMeeting(boolean isCheck) {

        mActivity.deleteScheduledMeeting(scheduledMeetings.get(0).getReservation_id(), isCheck);
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER);
    }

    private void showCancelMeetingConfirmDlg() {
        ConfirmDlg confirmDlg = new ConfirmDlg(getContext(),
                getContext().getString(R.string.cancel_meeting_title2),
                getContext().getString(R.string.cancel_meeting_content),
                getContext().getString(R.string.cancel_meeting_negative_btn),
                getContext().getString(R.string.cancel_meeting_positive_btn));

        confirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                deleteScheduledMeeting(true);
            }
            @Override
            public void onCancel(){

            }
        });
        confirmDlg.enablePositiveTextColor(true);
        confirmDlg.show();
    }

    private String getMeetingInfo() {
        String meetingInfoTemplate = localStore.getRealName()+" "+mContext.getResources().getString(R.string.invite_you_to_meeting)+"\n"
                + mContext.getResources().getString(R.string.meeting_theme_title) + formatInfoString(scheduledMeetings.get(0).getMeeting_name()) + "\n";
        String meetingStartTime = scheduledMeetings.get(0).getSchedule_start_time();
        String meetingEndTime = scheduledMeetings.get(0).getSchedule_end_time();
        if(!TextUtils.isEmpty(meetingStartTime) && !TextUtils.isEmpty(meetingEndTime)){
            meetingInfoTemplate = meetingInfoTemplate
                    + mContext.getResources().getString(R.string.start_time_title) + MeetingUtil.strTimeFormat(meetingStartTime, "yyyy-MM-dd HH:mm") + "\n"
                    + mContext.getResources().getString(R.string.end_time_title) + MeetingUtil.strTimeFormat(meetingEndTime, "yyyy-MM-dd HH:mm") + "\n";
        }

        ScheduledMeetingSetting setting = localStore.getScheduledMeetingSetting();
        long startTimeMill = setting.getRecurrenceStartTime();
        long endTimeMill = setting.getRecurrenceEndTime();
        String[] strStartTime = MeetingUtil.timeFormat(startTimeMill, "yyyy-MM-dd HH:mm").split(" ");
        String[] strEndTime = MeetingUtil.timeFormat(endTimeMill,"yyyy-MM-dd HH:mm").split(" ");
        long recurrenceEndDay = setting.getRecurrenceEndDay();
        String recurrenceType = setting.getRecurrenceType();
        int recurrenceInterval = setting.getRecurrenceInterval();
        String str = MeetingUtil.formatRecurrenceContent(mContext, recurrenceType, recurrenceInterval, setting.getRecurrenceDaysOfWeek(),
                setting.getRecurrenceDaysOfMonth());
        meetingInfoTemplate = meetingInfoTemplate
                + mContext.getResources().getString(frtc.sdk.R.string.repetition_period_title) + strStartTime[0] + " " +  "-" + " " + MeetingUtil.timeFormat(recurrenceEndDay, "yyyy-MM-dd")
                + "," + str + "\n";

        meetingInfoTemplate = meetingInfoTemplate
                + mContext.getResources().getString(R.string.meeting_number_title) + formatInfoString(scheduledMeetings.get(0).getMeeting_number()) + "\n";

        String meetingPassword = scheduledMeetings.get(0).getMeeting_password();
        if(meetingPassword != null && !meetingPassword.isEmpty()){
            meetingInfoTemplate = meetingInfoTemplate
                    + mContext.getResources().getString(R.string.meeting_password_title) + formatInfoString(meetingPassword) + "\n"
                    + "\n"
                    + mContext.getResources().getString(R.string.copy_invitation_notice_with_password) + "\n";
        }else {
            meetingInfoTemplate = meetingInfoTemplate + "\n"
                    + mContext.getResources().getString(R.string.copy_invitation_notice_without_password) + "\n";
        }

        String invitationURl = setting.getGroupMeetingUrl();
        if(invitationURl != null && !invitationURl.isEmpty()){
            meetingInfoTemplate = meetingInfoTemplate
                    + mContext.getResources().getString(R.string.invitation_url_title) + "\n"
                    + formatInfoString(invitationURl) + "\n";
        }

        return  meetingInfoTemplate;
    }

    private void copyMeetingInfo() {
        String meetingInfoTemplate = getMeetingInfo();

        try {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = android.content.ClipData.newPlainText(this.mContext.getResources().getString(R.string.meeting_info), meetingInfoTemplate);
            clipboard.setPrimaryClip(clip);
            BaseToast.showToast(mContext, mContext.getString(R.string.copy_meeting_info), Toast.LENGTH_SHORT);
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

    public String getMeetingRecurrenceGid(){
        return scheduledMeetings.get(0).getRecurrence_gid();
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
