
package com.frtc.sqmeetingce.ui.component;

import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.DatimePicker;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.TimePicker;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.annotation.DateMode;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.annotation.TimeMode;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.contract.OnDatimePickedListener;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.contract.OnTimeMeridiemPickedListener;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.entity.DateEntity;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.entity.DatimeEntity;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.entity.TimeEntity;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.widget.DatimeWheelLayout;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.widget.TimeWheelLayout;
import com.frtc.sqmeetingce.util.MeetingUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import frtc.sdk.log.Log;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.model.ScheduledMeetingListResult;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;


public class EditSingleRecurrenceMeetingFragment extends BaseFragment{

    protected final String TAG = this.getClass().getSimpleName();
    private LocalStore localStore;
    public MainActivity mActivity;
    private TextView tvMeetingName;
    private TextView tvStartTime;
    private TextView tvDuration;
    private TextView tvTimeZone;

    private ScheduledMeeting scheduledMeeting;
    private long startTimeMill;
    private long durationTime;
    private ScheduledMeetingListResult scheduledMeetingListResult;
    private int position;
    private FragmentTagEnum preFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.edit_single_recurrence_meeting_fragment, container, false);

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();

        init(view);
        setClickListener(view);

        return view;
    }

    private void init(View view) {
        tvMeetingName = view.findViewById(R.id.meeting_name);
        TextView tvRecurrenceType = view.findViewById(R.id.recurrence_type);
        TextView tvRecurrenceEnd = view.findViewById(R.id.recurrence_end);
        tvStartTime = view.findViewById(R.id.start_time);
        tvDuration = view.findViewById(R.id.meeting_duration);
        tvTimeZone = view.findViewById(R.id.time_zone);

        tvMeetingName.setText(scheduledMeeting.getMeeting_name());
        String recurrenceTypeContent = frtc.sdk.util.MeetingUtil.formatRecurrenceTypeContent(mActivity, scheduledMeetingListResult.getRecurrenceType(), scheduledMeetingListResult.getRecurrenceInterval());
        tvRecurrenceType.setText(recurrenceTypeContent);

        long recurrenceEndDay = scheduledMeetingListResult.getRecurrenceEndDay();
        int count = scheduledMeetingListResult.getTotal_size();
        String endDay = MeetingUtil.timeFormat(recurrenceEndDay, "yyyy年MM月dd日");
        String format = String.format(mActivity.getResources().getString(R.string.recurrence_end), endDay, count+"");
        tvRecurrenceEnd.setText(format);

        startTimeMill = Long.parseLong(scheduledMeeting.getSchedule_start_time());
        tvStartTime.setText(MeetingUtil.strTimeFormat(scheduledMeeting.getSchedule_start_time(), "yyyy-MM-dd HH:mm"));
        durationTime = MeetingUtil.calcDurationTime(mActivity, scheduledMeeting.getSchedule_start_time(),scheduledMeeting.getSchedule_end_time());
        tvDuration.setText(MeetingUtil.formatDurationTime(mActivity, durationTime));
        TimeZone tz = TimeZone.getDefault();
        String timezone = "("+ tz.getDisplayName(false,TimeZone.LONG_GMT) + ")"+tz.getDisplayName();
        tvTimeZone.setText(timezone);
    }


    private void setClickListener(View view){
        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"btnBack onClick");
                showPreviousFragment();
            }
        });
        RelativeLayout startTimeItem = view.findViewById(R.id.start_time_item);
        startTimeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onYearMonthDayTime();
            }
        });

        RelativeLayout durationItem = view.findViewById(R.id.meeting_duration_item);
        durationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDuration();
            }
        });

        Button btnSaveMeeting = view.findViewById(R.id.save_meeting_btn);
        btnSaveMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"btnSaveMeeting");
                onScheduleMeeting();
            }
        });
    }



    @Override
    public void onBack() {
        showPreviousFragment();
    }

    public void setMeeting(ScheduledMeeting scheduledMeeting, ScheduledMeetingListResult scheduledMeetingListResult, int position, FragmentTagEnum preFragment) {
        this.scheduledMeeting = scheduledMeeting;
        this.scheduledMeetingListResult = scheduledMeetingListResult;
        this.position = position;
        this.preFragment = preFragment;
    }

    DatimePicker datimePicker;
    TimePicker timePicker;
    public void onYearMonthDayTime() {
        if(datimePicker == null) {
            datimePicker = new DatimePicker(mActivity);
        }
        final DatimeWheelLayout wheelLayout = datimePicker.getWheelLayout();
        datimePicker.setOnDatimePickedListener(new OnDatimePickedListener() {
            @Override
            public void onDatimePicked(int year, int month, int day, int hour, int minute, int second) {
                Log.d(TAG,"updateStartTime:"+year+","+month+","+day+","+hour+","+minute);
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day, hour, minute);

                startTimeMill = calendar.getTimeInMillis();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date dateStart = new Date(startTimeMill);
                tvStartTime.setText(simpleDateFormat.format(dateStart));
            }
        });
        wheelLayout.setDateMode(DateMode.YEAR_MONTH_DAY);
        wheelLayout.setTimeMode(TimeMode.HOUR_24_NO_SECOND);
        TimeEntity.setStartTimeMill(startTimeMill);
        DatimeEntity entity = DatimeEntity.now();
        entity.setDate(DateEntity.target(new Date(startTimeMill)));
        entity.setTime(TimeEntity.target(new Date(startTimeMill)));
        wheelLayout.setRange(DatimeEntity.now(), DatimeEntity.yearOnFuture(10), entity);
        wheelLayout.setDateLabel(getString(R.string.year_picker), getString(R.string.month_picker), getString(R.string.day_picker));
        wheelLayout.setTimeLabel(getString(R.string.hour), getString(R.string.minute), getString(R.string.second));
        datimePicker.setTitle(getResources().getString(R.string.start_time));
        datimePicker.show();
    }

    private void onDuration() {
        if(timePicker == null) {
            timePicker = new TimePicker(mActivity);
        }
        timePicker.setBodyWidth(140);
        TimeWheelLayout wheelLayout = timePicker.getWheelLayout();
        wheelLayout.setRange(TimeEntity.target(0, 0, 0), TimeEntity.target(24, 59, 59));
        wheelLayout.setTimeMode(TimeMode.HOUR_24_NO_SECOND);
        wheelLayout.setTimeLabel(":", " ", "");
        int hour = (int)durationTime/1000/3600;
        int minute = (int)durationTime/1000%3600/60;
        wheelLayout.setDefaultValue(TimeEntity.target(hour, minute, 0));
        wheelLayout.setTimeStep(1, 1, 1);
        timePicker.setOnTimeMeridiemPickedListener(new OnTimeMeridiemPickedListener() {
            @Override
            public void onTimePicked(int hour, int minute, int second, boolean isAnteMeridiem) {
                Log.d(TAG,"onTimePicked:"+hour+","+minute+","+second);
                String str;
                if(hour == 0){
                    str = minute+getString(R.string.time_minutes);
                }else{
                    if(minute == 0){
                        str = hour +getString(R.string.time_hours);
                    }else{
                        str = hour+getString(R.string.time_hours)+minute+getString(R.string.time_minutes);
                    }
                }
                tvDuration.setText(str);
                durationTime = (hour* 3600L +minute* 60L)*1000;
            }
        });
        timePicker.setTitle(getResources().getString(R.string.meeting_duration));
        timePicker.show();
    }


    private void onScheduleMeeting(){
        if(tvMeetingName.getText().toString().trim().isEmpty()){
            BaseToast.showToast(mActivity, getString(R.string.meeting_name_valid_notice), Toast.LENGTH_SHORT);
            return;
        }

        if(durationTime == 0){
            BaseToast.showToast(mActivity, getString(R.string.duration_time_valid_notice), Toast.LENGTH_SHORT);
            return;
        }
        if(startTimeMill < System.currentTimeMillis()){
            BaseToast.showToast(mActivity, getString(R.string.start_time_valid_notice), Toast.LENGTH_SHORT);
            return;
        }
        if(startTimeMill < scheduledMeetingListResult.getRecurrenceStartDay()){
            BaseToast.showToast(mActivity, getString(R.string.start_time_later_than_recurrence_start_time), Toast.LENGTH_SHORT);
            return;
        }
        if(position >= 1 && startTimeMill < Long.parseLong(scheduledMeetingListResult.getMeeting_schedules().get(position - 1).getSchedule_end_time())){
            BaseToast.showToast(mActivity, getString(R.string.start_time_later_than_previous_single_meeting_end_time), Toast.LENGTH_SHORT);
            return;
        }
        if((position + 1) < scheduledMeetingListResult.getMeeting_schedules().size() && (startTimeMill + durationTime) > Long.parseLong(scheduledMeetingListResult.getMeeting_schedules().get(position + 1).getSchedule_start_time())){
            BaseToast.showToast(mActivity, getString(R.string.end_time_earlier_than_latter_single_meeting_start_time), Toast.LENGTH_SHORT);
            return;
        }
        if(startTimeMill > scheduledMeetingListResult.getRecurrenceEndDay()){
            BaseToast.showToast(mActivity, getString(R.string.start_time_earlier_than_recurrence_end_time), Toast.LENGTH_SHORT);
            return;
        }

        mActivity.isEditSingleRecurrence = true;
        mActivity.updateScheduledMeeting(scheduledMeeting);
    }

    private void onSaveScheduleMeetingSettings() {
        Log.i(TAG, "onSaveScheduleMeetingSettings");
        if(localStore != null){
            String meetingName = tvMeetingName.getText().toString().trim();
            Log.i(TAG, "onSaveScheduleMeetingSettings meetingName = "+meetingName);
            localStore.getScheduledMeetingSetting().setMeetingName(meetingName);
            localStore.getScheduledMeetingSetting().setStartTime(String.valueOf(startTimeMill));
            localStore.getScheduledMeetingSetting().setEndTime(String.valueOf(startTimeMill+durationTime));
            localStore.getScheduledMeetingSetting().setMeetingType(scheduledMeeting.getMeeting_type());
        }
    }


    public void saveMeeting() {
        onSaveScheduleMeetingSettings();
        mActivity.updateScheduleMeeting(scheduledMeeting.getReservation_id(), true);
    }

    public void saveMeetingSuccess() {
        showPreviousFragment();
    }

    private void showPreviousFragment(){
        if(preFragment == FragmentTagEnum.FRAGMENT_SCHEDULE_RECURRENCE_MEETING_LIST){
            mActivity.getScheduledRecurrenceMeetingList(scheduledMeeting.getRecurrence_gid());
        }else {
            scheduledMeeting.setSchedule_start_time(String.valueOf(startTimeMill));
            scheduledMeeting.setSchedule_end_time(String.valueOf(startTimeMill + durationTime));
            scheduledMeetingListResult.getMeeting_schedules().get(position).setSchedule_start_time(String.valueOf(startTimeMill));
            scheduledMeetingListResult.getMeeting_schedules().get(position).setSchedule_end_time(String.valueOf(startTimeMill + durationTime));
            mActivity.showMeetingDetails(scheduledMeeting, position, scheduledMeetingListResult);
        }
    }
}
