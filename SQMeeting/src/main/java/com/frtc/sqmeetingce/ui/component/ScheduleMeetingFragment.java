package com.frtc.sqmeetingce.ui.component;

import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
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
import com.frtc.sqmeetingce.ui.picker.wheelpicker.entity.DatimeEntity;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.entity.TimeEntity;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.widget.DatimeWheelLayout;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.widget.TimeWheelLayout;
import com.frtc.sqmeetingce.util.MeetingUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import frtc.sdk.internal.model.FrtcSDKMeetingType;
import frtc.sdk.log.Log;
import frtc.sdk.model.MeetingRoom;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;


public class ScheduleMeetingFragment extends BaseFragment implements MeetingRoomAdapter.OnItemClickListener{

    protected final String TAG = this.getClass().getSimpleName();

    private LocalStore localStore;
    public MainActivity mActivity;

    private ScrollView scrollView;
    private EditText etMeetingName;
    private TextView tvStartTime;
    private TextView tvDuration;
    private TextView tvTimeZone;

    private EditText etMeetingRoomId;
    private Switch stMeetingRoomUsed;

    private ConstraintLayout meetingRoomIdLayout;

    private ConstraintLayout meetingRoomsLayout;
    private RecyclerView meetingRoomRecyclerView;
    private ArrayList<MeetingRoom> meetingRooms;

    private MeetingRoomAdapter meetingRoomAdapter;

    private TextView tvInvitedUsers;
    private RelativeLayout rateItem, joinTimeItem, repetitionFreqItem, rlRepetitionEnd;
    private TextView tvRate, tvJoinTime, tvRepetitionFreq, tvRepetitionEnd;
    private Switch stMute;
    private Switch stAllowDialIn;
    private Switch stWatermark;
    private Switch stPassword;
    private View splitSpaceFifth;

    private View rateSplitView;
    private View allowDialInSplitView;
    private View watermarkSplitView;

    private String meetingName = "";

    private boolean mute = false;
    private boolean allowDialIn = true;

    private boolean meetingRoomUsed = false;
    private String meetingRoomId;

    private boolean watermarkEnable = false;

    private long startTimeMill;
    private long durationTime;
    private boolean isPwdCheck;

    public ScheduleMeetingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
        queryMeetingRooms();

        View view = inflater.inflate(R.layout.schedule_meeting_fragment, container, false);

        init(view);
        setClickListener(view);
        setEditChangedListener(view);

        return view;
    }


    private void init(View view) {

        scrollView = view.findViewById(R.id.scroll_view);
        scrollView.requestFocus();

        etMeetingName = view.findViewById(R.id.meeting_name);
        tvStartTime = view.findViewById(R.id.start_time);
        tvDuration = view.findViewById(R.id.meeting_duration);
        tvTimeZone = view.findViewById(R.id.time_zone);

        meetingRoomIdLayout = view.findViewById(R.id.meeting_room_layout);

        etMeetingRoomId = view.findViewById(R.id.edit_meeting_room_id);
        stMeetingRoomUsed = view.findViewById(R.id.switch_use_meeting_Room);

        tvInvitedUsers = view.findViewById(R.id.invited_users);
        rateItem = view.findViewById(R.id.meeting_rate_item);
        tvRate = view.findViewById(R.id.meeting_rate);
        joinTimeItem = view.findViewById(R.id.join_time_item);
        joinTimeItem.setVisibility(View.VISIBLE);
        tvJoinTime = view.findViewById(R.id.join_time);

        repetitionFreqItem = view.findViewById(R.id.repetition_frequency_item);
        repetitionFreqItem.setVisibility(View.VISIBLE);
        tvRepetitionFreq = view.findViewById(R.id.repetition_frequency);
        rlRepetitionEnd = view.findViewById(R.id.rl_repetition_end);
        tvRepetitionEnd = view.findViewById(R.id.repetition_end_content);

        /*
        Bundle bundle = getArguments();
        if(bundle != null){
            String recurrenceEndDay = bundle.getString("recurrenceEndDay");
            int count = bundle.getInt("count");
            if(!TextUtils.isEmpty(recurrenceEndDay)) {
                rlRepetitionEnd.setVisibility(View.VISIBLE);
                String format = String.format(mActivity.getResources().getString(R.string.repetition_end_content), recurrenceEndDay, count+"");
                tvRepetitionEnd.setText(format);
            }
        }
         */

        stMute = view.findViewById(R.id.switch_mute);
        stAllowDialIn = view.findViewById(R.id.switch_allow_visitors_to_dial_in);
        stWatermark = view.findViewById(R.id.switch_watermark);
        stPassword = view.findViewById(R.id.switch_password);
        splitSpaceFifth = view.findViewById(R.id.separator_password);

        rateSplitView = view.findViewById(R.id.separator_mute);
        allowDialInSplitView = view.findViewById(R.id.separator_allow);
        watermarkSplitView = view.findViewById(R.id.separator_watermark);

        meetingRoomsLayout = view.findViewById(R.id.meeting_rooms_layout);

        meetingName = localStore.getScheduledMeetingSetting().getMeetingName();
        if(meetingName == null || meetingName.isEmpty()){
            meetingName = localStore.getRealName() + mActivity.getResources().getString(R.string.schedule_meeting_postfix);
        }
        etMeetingName.setText(meetingName);

        String startTimeStr = localStore.getScheduledMeetingSetting().getStartTime();
        if(startTimeStr == null || startTimeStr.isEmpty()){
            initialStartTime();
            durationTime = 30*60*1000;
            tvDuration.setText("30"+ getString(R.string.time_minutes));
        }else{
            if(isStartTimeValid(startTimeStr)){
                tvStartTime.setText(formatStartTime(startTimeStr));
            }else{
                initialStartTime();
            }
            tvDuration.setText(formatDurationTime(localStore.getScheduledMeetingSetting().getStartTime(),
                    localStore.getScheduledMeetingSetting().getEndTime()));
        }

        String roomId = localStore.getScheduledMeetingSetting().getMeetingRoomId();
        if(roomId == null){
            meetingRoomId = null;
            stMeetingRoomUsed.setChecked(false);
            meetingRoomUsed = false;
            hideMeetingRoomLayout();

        }else{
            stMeetingRoomUsed.setChecked(true);
            meetingRoomUsed = true;
            meetingRoomId = roomId;
            etMeetingRoomId.setText(getMeetingRoomNumberById(roomId));
            showMeetingRoomLayout();
        }

        if(localStore.getScheduledMeetingSetting().getInvitedUsers() == null){
            tvInvitedUsers.setText("无");
        }else{
            tvInvitedUsers.setText(String.valueOf(localStore.getScheduledMeetingSetting().getInvitedUsers().size()));
        }

        tvRate.setText(localStore.getScheduledMeetingSetting().getRate());
        String joinTime = localStore.getScheduledMeetingSetting().getJoinTime();
        tvJoinTime.setText("30".equals(joinTime)?getString(R.string.join_time_30_minutes):getString(R.string.join_time_any_time));

        String freq = localStore.getScheduledMeetingSetting().getRepetitionFreq();
        if(TextUtils.isEmpty(freq)) {
            tvRepetitionFreq.setText(getString(R.string.no_frequency));
        }else{
            tvRepetitionFreq.setText(freq);
        }

        long endDay = localStore.getScheduledMeetingSetting().getRecurrenceEndDay();
        if(endDay != 0){
            String recurrenceEndDay = MeetingUtil.timeFormat(endDay, "yyyy年MM月dd日");
            int count = localStore.getScheduledMeetingSetting().getRecurrenceCount();
            String format = String.format(mActivity.getResources().getString(R.string.repetition_end_content), recurrenceEndDay, count+"");
            tvRepetitionEnd.setText(format);
        }

        String meetingType = localStore.getScheduledMeetingSetting().getMeetingType();
        if(!TextUtils.isEmpty(meetingType) && FrtcSDKMeetingType.RECURRENCE.getTypeName().equals(meetingType)) {
            rlRepetitionEnd.setVisibility(View.VISIBLE);
        }else{
            rlRepetitionEnd.setVisibility(View.GONE);
        }

        mute = localStore.getScheduledMeetingSetting().isMute();
        stMute.setChecked(mute);
        allowDialIn = localStore.getScheduledMeetingSetting().isGuestDialIn();
        stAllowDialIn.setChecked(allowDialIn);
        watermarkEnable = localStore.getScheduledMeetingSetting().isWatermarkEnable();
        stWatermark.setChecked(watermarkEnable);
        isPwdCheck = localStore.getScheduledMeetingSetting().isPwdCheck();
        stPassword.setChecked(isPwdCheck);

        TimeZone tz = TimeZone.getDefault();
        String timezone = "("+ tz.getDisplayName(false,TimeZone.LONG_GMT) + ")"+tz.getDisplayName();
        tvTimeZone.setText(timezone);

        meetingRooms = localStore.getMeetingRooms();
        meetingRoomRecyclerView = view.findViewById(R.id.meeting_room_listView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        meetingRoomRecyclerView.setLayoutManager(layoutManager);
        meetingRoomAdapter = new MeetingRoomAdapter(meetingRooms);
        meetingRoomRecyclerView.setAdapter(meetingRoomAdapter);
        meetingRoomAdapter.setOnItemClickedListener(this);

    }

    private boolean isStartTimeValid(String startTimeStr){
        if(startTimeStr != null && !startTimeStr.isEmpty()){
            long startTime = Long.parseLong(startTimeStr);
            return startTime > System.currentTimeMillis();
        }
        return false;
    }

    private void initialStartTime(){

        Calendar calendar = Calendar.getInstance();

        int minute = calendar.get(Calendar.MINUTE);

        if(minute <30){
            calendar.set(Calendar.MINUTE,30);
        }else{
            calendar.set(Calendar.MINUTE,0);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }

        startTimeMill = calendar.getTimeInMillis();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dateStart = new Date(startTimeMill);
        tvStartTime.setText(simpleDateFormat.format(dateStart));
    }

    private String formatStartTime(String startTimeStr){
        if(startTimeStr != null && !startTimeStr.isEmpty()){
            startTimeMill = Long.parseLong(startTimeStr);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dateStart = new Date(startTimeMill);
            return simpleDateFormat.format(dateStart);
        }
        return "";
    }

    public String formatDurationTime(String start, String end){

        if(start != null && !start.isEmpty() && end != null &&
                !end.isEmpty()){
            long startTime = Long.parseLong(start);
            long endTime = Long.parseLong(end);
            durationTime = endTime - startTime;

            long durationSeconds = durationTime/1000;
            long hour = durationSeconds / 3600;
            long seconds = durationSeconds % 60;
            long minutes = durationSeconds % 3600 / 60;
            if(hour > 0){
                if(minutes == 0){
                    return String.valueOf(hour) + getString(R.string.time_hours);
                }
                return String.valueOf(hour) + getString(R.string.time_hours) + String.valueOf(minutes) + getString(R.string.time_minutes) ;
            }else{
                return String.valueOf(minutes) + getString(R.string.time_minutes) ;
            }
        }
        return "";
    }

    private void onSaveScheduleMeetingSettings() {
        Log.i(TAG, "onSaveScheduleMeetingSettings");
        if(localStore != null){
            meetingName = etMeetingName.getText().toString().trim();
            if(meetingRoomUsed){
                localStore.getScheduledMeetingSetting().setMeetingRoomId(meetingRoomId);
                localStore.getScheduledMeetingSetting().setPassword(null);
            }else{
                localStore.getScheduledMeetingSetting().setMeetingRoomId(null);
                if(stPassword.isChecked()){
                    localStore.getScheduledMeetingSetting().setPassword(null);
                }else{
                    localStore.getScheduledMeetingSetting().setPassword("");
                }

            }
            mute = stMute.isChecked();
            allowDialIn = stAllowDialIn.isChecked();
            watermarkEnable = stWatermark.isChecked();
            isPwdCheck = stPassword.isChecked();
            Log.i(TAG, "onSaveScheduleMeetingSettings meetingName = "+meetingName);
            localStore.getScheduledMeetingSetting().setMeetingName(meetingName);
            localStore.getScheduledMeetingSetting().setStartTime(String.valueOf(startTimeMill));
            localStore.getScheduledMeetingSetting().setEndTime(String.valueOf(startTimeMill+durationTime));
            localStore.getScheduledMeetingSetting().setMute(mute);
            localStore.getScheduledMeetingSetting().setGuestDialIn(allowDialIn);
            localStore.getScheduledMeetingSetting().setWatermarkEnable(watermarkEnable);
            localStore.getScheduledMeetingSetting().setPwdCheck(isPwdCheck);
            String meetingType = localStore.getScheduledMeetingSetting().getMeetingType();
            if(TextUtils.isEmpty(meetingType)) {
                localStore.getScheduledMeetingSetting().setMeetingType(FrtcSDKMeetingType.RESERVATION.getTypeName());
                meetingType = FrtcSDKMeetingType.RESERVATION.getTypeName();
            }
            if(meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
                String strDateDay = MeetingUtil.timeFormat(startTimeMill, "yyyy-MM-dd");
                localStore.getScheduledMeetingSetting().setRecurrenceStartDay(MeetingUtil.timeFormatToLong(strDateDay, "yyyy-MM-dd"));
                localStore.getScheduledMeetingSetting().setRecurrenceStartTime(startTimeMill);
                localStore.getScheduledMeetingSetting().setRecurrenceEndTime(startTimeMill+durationTime);
            }
        }
    }

    private String getMeetingRoomNumberById(String meetingRoomId){
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
        }
        ArrayList<MeetingRoom> meetingRooms = localStore.getMeetingRooms();
        for(MeetingRoom meetingRoom : meetingRooms){
            if(meetingRoom.getMeetingRoomId().equals(meetingRoomId)){
                return meetingRoom.getMeetingNumber();
            }
        }
        return "";
    }

    private void showMeetingRoomLayout(){
        meetingRoomIdLayout.setVisibility(View.VISIBLE);

        rateItem.setVisibility(View.GONE);
        joinTimeItem.setVisibility(View.GONE);
        stAllowDialIn.setVisibility(View.GONE);
        stWatermark.setVisibility(View.GONE);
        rateSplitView.setVisibility(View.GONE);
        allowDialInSplitView.setVisibility(View.GONE);
        watermarkSplitView.setVisibility(View.GONE);
        stPassword.setVisibility(View.GONE);
        splitSpaceFifth.setVisibility(View.GONE);
    }

    private void hideMeetingRoomLayout(){
        meetingRoomIdLayout.setVisibility(View.GONE);

        rateItem.setVisibility(View.VISIBLE);
        joinTimeItem.setVisibility(View.VISIBLE);
        stAllowDialIn.setVisibility(View.VISIBLE);
        stWatermark.setVisibility(View.VISIBLE);
        rateSplitView.setVisibility(View.VISIBLE);
        allowDialInSplitView.setVisibility(View.VISIBLE);
        watermarkSplitView.setVisibility(View.VISIBLE);
        stPassword.setVisibility(View.VISIBLE);
        splitSpaceFifth.setVisibility(View.VISIBLE);
    }

    public void onItemClicked(MeetingRoom selectedMeetingRoom){
        String meetingNumber = selectedMeetingRoom.getMeetingNumber();
        etMeetingRoomId.setText(meetingNumber);
        meetingRoomId = selectedMeetingRoom.getMeetingRoomId();
        hideMeetingRooms();
    }

    @Override
    public void onBack() {
        localStore.resetScheduledMeetingSetting();
        LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).setLocalStore(localStore);
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER);
    }

    private void setClickListener(View view) {

        ImageButton btnBack = view.findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick btnBack");
                localStore.resetScheduledMeetingSetting();
                LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).setLocalStore(localStore);
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER);
            }
        });

        Button btnComplete = view.findViewById(R.id.button_complete);
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScheduleMeeting();
            }
        });

        Button btnSchedule = view.findViewById(R.id.btn_schedule);
        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScheduleMeeting();
            }
        });

        etMeetingName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                }
            }
        });

        etMeetingName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                meetingName = etMeetingName.getText().toString().trim();
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

        ImageView btnShowMeetingRooms = view.findViewById(R.id.btn_show_meeting_rooms);
        btnShowMeetingRooms.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                showMeetingRooms();
                if(meetingRooms!=null && !meetingRooms.isEmpty()){
                    String str = meetingRooms.get(0).getMeetingNumber();
                    etMeetingRoomId.setText(str);
                    meetingRoomId = meetingRooms.get(0).getMeetingRoomId();
                }
            }
        });


        Button btnOk = view.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMeetingRooms();
            }
        });

        stMeetingRoomUsed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(!meetingRooms.isEmpty()){
                    if (!compoundButton.isPressed()) {
                        compoundButton.setChecked(!checked);
                    }
                    meetingRoomUsed = checked;
                    if(checked){
                        String str = meetingRooms.get(0).getMeetingNumber();
                        etMeetingRoomId.setText(str);
                        meetingRoomId = meetingRooms.get(0).getMeetingRoomId();
                        showMeetingRoomLayout();
                    }else{
                        meetingRoomId = null;
                        hideMeetingRoomLayout();
                    }
                }else{
                    compoundButton.setChecked(false);
                    hideMeetingRoomLayout();
                    BaseToast.showToast(mActivity, getString(R.string.no_meeting_room_notice), Toast.LENGTH_SHORT);
                }
            }
        });


        rateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveScheduleMeetingSettings();
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING;
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_RATE);
            }
        });

        joinTimeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveScheduleMeetingSettings();
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING;
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_JOIN_TIME);
            }
        });

        repetitionFreqItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onSaveScheduleMeetingSettings();
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING;
                mActivity.showScheduleMeetingRepetitionFreqFragment(false);
            }
        });

        rlRepetitionEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveScheduleMeetingSettings();
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING;
                mActivity.showScheduleMeetingRepetitionFreqSetting(localStore.getScheduledMeetingSetting().getRecurrenceType(), false);
            }
        });

        RelativeLayout invitedUserItem = view.findViewById(R.id.invited_users_item);
        invitedUserItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveScheduleMeetingSettings();
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING;
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_INVITED_USER);
            }
        });

        stMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                compoundButton.setChecked(checked);
                mute = checked;
            }
        });

        stAllowDialIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.setChecked(isChecked);
                allowDialIn = isChecked;
                if(isChecked){
                    watermarkEnable = false;
                    stWatermark.setChecked(false);
                }
            }
        });

        stWatermark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                compoundButton.setChecked(isChecked);
                watermarkEnable = isChecked;
                if(isChecked){
                    allowDialIn = false;
                    stAllowDialIn.setChecked(false);
                }
            }
        });

        stPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                isPwdCheck = checked;
                compoundButton.setChecked(checked);
            }
        });
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
                Log.d(TAG,"onDatimePicked:"+year+","+month+","+day+","+hour+","+minute);
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
        wheelLayout.setRange(DatimeEntity.now(), DatimeEntity.yearOnFuture(10));
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

        if(etMeetingName.getText().toString().trim().isEmpty()){
            BaseToast.showToast(mActivity, getString(R.string.meeting_name_valid_notice), Toast.LENGTH_SHORT);
            return;
        }
        if(durationTime == 0){
            BaseToast.showToast(mActivity, getString(R.string.duration_time_valid_notice), Toast.LENGTH_SHORT);
            return;
        }
        long current = System.currentTimeMillis();
        if(startTimeMill < System.currentTimeMillis()){
            BaseToast.showToast(mActivity, getString(R.string.start_time_valid_notice), Toast.LENGTH_SHORT);
            return;
        }
        onSaveScheduleMeetingSettings();
        mActivity.createScheduleMeeting();
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER);
    }

    private void queryMeetingRooms(){
        mActivity.queryMeetingRooms();
    }

    private void showMeetingRooms(){

        meetingRoomsLayout.setVisibility(View.VISIBLE);

    }

    private void hideMeetingRooms(){

        meetingRoomsLayout.setVisibility(View.GONE);
    }


    private void setEditChangedListener(View view) {

        etMeetingRoomId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                } else {

                }
            }
        });

        etMeetingRoomId.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


}
