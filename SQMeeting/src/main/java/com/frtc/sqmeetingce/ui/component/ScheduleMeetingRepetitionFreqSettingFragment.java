
package com.frtc.sqmeetingce.ui.component;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;
import com.frtc.sqmeetingce.ui.picker.view.MonthContentAdapter;
import com.frtc.sqmeetingce.ui.picker.view.MonthContentView;
import com.frtc.sqmeetingce.ui.picker.view.WeekAdapter;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.DatePicker;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.annotation.DateMode;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.contract.OnDatePickedListener;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.contract.OnLinkageSelectedListener;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.entity.DateEntity;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.impl.UnitDateFormatter;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.widget.DateWheelLayout;
import com.frtc.sqmeetingce.ui.picker.wheelpicker.widget.LinkageWheelLayout;
import com.frtc.sqmeetingce.util.MeetingUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import frtc.sdk.internal.model.FrtcSDKMeetingType;
import frtc.sdk.log.Log;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.ui.model.RecurrenceType;



public class ScheduleMeetingRepetitionFreqSettingFragment extends BaseFragment implements OnDatePickedListener, MonthContentAdapter.OnKeyboardClickListener {

    protected final String TAG = this.getClass().getSimpleName();
    private LocalStore localStore;
    public MainActivity mActivity;

    protected LinkageWheelLayout repetitiveRulePicker;
    private TextView meetingEndDate;
    private GridView weekView;
    private WeekAdapter weekAdapter;
    private MonthContentView viewMonthContent;

    private ConstraintLayout clCalendar;
    private TextView freqDateTitle;
    private TextView freqDesc, freqContent;
    private String firstValue, secondValue;
    private String[] dataWeek = new String[7];
    private String[] dataMonth = new String[31];
    private int startWeekDayPosition;
    private int startDayPosition;
    private int defaultFrequencyCount = 7;
    private String startTimeFormat;
    private boolean isUpdateRecurrence = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG,"onCreateView");
        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.schedule_meeting_repetition_freq_setting_fragment, container, false);

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();

        init(view);
        setClickListener(view);

        return view;
    }

    private void init(View view) {
        freqDesc = view.findViewById(R.id.freq_desc);
        freqContent = view.findViewById(R.id.freq_content);

        repetitiveRulePicker = view.findViewById(R.id.repetitive_rule_picker);

        meetingEndDate = view.findViewById(R.id.meeting_end_date);

        clCalendar = view.findViewById(R.id.cl_calendar);
        freqDateTitle = view.findViewById(R.id.freq_date_title);

        String startTime = localStore.getScheduledMeetingSetting().getStartTime();
        startTimeFormat = MeetingUtil.strTimeFormat(startTime, "yyyy-MM-dd");
        startWeekDayPosition = MeetingUtil.dateToWeekPosition(mActivity, startTimeFormat);
        weekAdapter = new WeekAdapter(mActivity);
        String freqType = "";
        Bundle bundle = getArguments();
        if (bundle != null) {
            freqType = getArguments().getString("freqType");
            isUpdateRecurrence = bundle.getBoolean("isUpdateRecurrence");
        }
        String meetingType = localStore.getScheduledMeetingSetting().getMeetingType();
        initData(freqType, meetingType);
        weekAdapter.setStartWeekDay(startWeekDayPosition);
        weekAdapter.setDataWeek(dataWeek);
        dataWeek[startWeekDayPosition] = weekAdapter.getItem(startWeekDayPosition).toString();

        weekView = view.findViewById(R.id.calendar_body_week);
        weekView.setNumColumns(weekAdapter.getCount());
        weekView.setAdapter(weekAdapter);
        weekAdapter.setOnItemClickListener(new WeekAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, WeekAdapter.ViewHolder holder, int position) {
                Log.d(TAG,"onItemClick position = " + position);
                if(position == startWeekDayPosition){
                    BaseToast.showToast(mActivity, getString(R.string.not_cancel_selected_meeting_start_date), Toast.LENGTH_SHORT);
                    return;
                }
                if(TextUtils.isEmpty(dataWeek[position])) {
                    dataWeek[position] = weekAdapter.getItem(position).toString();
                    ((TextView)view).setBackground(getResources().getDrawable(R.drawable.week_blue));
                }else{
                    dataWeek[position] = "";
                    ((TextView)view).setBackground(getResources().getDrawable(R.drawable.week_gray));
                }
                setFreqDescText();
            }
        });

        viewMonthContent = view.findViewById(R.id.view_monthcontent);
        viewMonthContent.setOnKeyBoardClickListener(this);
        String day = startTimeFormat.substring(8);
        startDayPosition = Integer.parseInt(day) - 1;
        dataMonth[startDayPosition] = day;
        viewMonthContent.setStartDayPosition(startDayPosition);
        viewMonthContent.setDataMonth(dataMonth);

        setFreqContentText(freqType, meetingType);
        setClCalendarVisible(freqType, secondValue);
        setFreqDescText();
        setEndDay(freqType, meetingType);

        repetitiveRulePicker.setData(new RepetitionTypeProvider((Context)mActivity));
        repetitiveRulePicker.setAtmosphericEnabled(true);
        repetitiveRulePicker.setVisibleItemCount(7);
        repetitiveRulePicker.setCyclicEnabled(false);
        repetitiveRulePicker.setIndicatorEnabled(false);
        repetitiveRulePicker.setTextColor(0xFFCCCCCC);
        repetitiveRulePicker.setSelectedTextColor(0xFF333333);
        repetitiveRulePicker.setCurtainEnabled(true);
        repetitiveRulePicker.setCurvedEnabled(false);
        repetitiveRulePicker.setOnLinkageSelectedListener(new OnLinkageSelectedListener() {
            @Override
            public void onLinkageSelected(Object first, Object second, Object third) {
                Log.d(TAG,"onLinkageSelected" );
                firstValue = first.toString();
                secondValue = second.toString();
                String freqType = "";
                if(firstValue.equals(getString(R.string.repetition_type_week))){
                    freqType = RecurrenceType.WEEKLY.getTypeName();
                }else if(firstValue.equals(getString(R.string.repetition_type_month))){
                    freqType = RecurrenceType.MONTHLY.getTypeName();
                }else{
                    freqType = RecurrenceType.DAILY.getTypeName();
                }
                setClCalendarVisible(freqType, secondValue);
                setFreqDescText();
                setEndDay(freqType, meetingType);
                String str = mActivity.getString(R.string.repetition_frequency_content);
                String str1 = String.format(str, secondValue + firstValue);
                freqContent.setText(str1);
            }
        });
    }

    private void initData(String freqType, String meetingType) {
        String recurrenceType = localStore.getScheduledMeetingSetting().getRecurrenceType();
        if(!TextUtils.isEmpty(meetingType) && meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())
                && !TextUtils.isEmpty(recurrenceType) && recurrenceType.equals(freqType)){
            int position = 0;
            if (freqType.equals(RecurrenceType.WEEKLY.getTypeName())) {
                List<Integer> dataOfWeek = localStore.getScheduledMeetingSetting().getRecurrenceDaysOfWeek();
                for(int i = 0; i < dataOfWeek.size(); i++){
                    position = dataOfWeek.get(i) -1;
                    dataWeek[position] = weekAdapter.getItem(position).toString();
                }
            } else if (freqType.equals(RecurrenceType.MONTHLY.getTypeName())) {
                List<Integer> dataOfMonth = localStore.getScheduledMeetingSetting().getRecurrenceDaysOfMonth();
                for(int i = 0; i < dataOfMonth.size(); i++){
                    position = dataOfMonth.get(i) -1;
                    dataMonth[position] = dataOfMonth.get(i) + "";
                }

            }
        }
    }

    private void setFreqContentText(String freqType, String meetingType) {
        String recurrenceType = localStore.getScheduledMeetingSetting().getRecurrenceType();
        if(!TextUtils.isEmpty(meetingType) && meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())
                && !TextUtils.isEmpty(recurrenceType) && recurrenceType.equals(freqType)){
            secondValue = localStore.getScheduledMeetingSetting().getRecurrenceInterval() + "";
        }else {
            secondValue = "1";
        }

        String str2 = mActivity.getString(R.string.repetition_frequency_content);
        String strFreqContent = "";
        String secondValueTmp = secondValue;
        if(secondValueTmp.equals("1")){
            secondValueTmp = "";
        }
        if (freqType.equals(RecurrenceType.DAILY.getTypeName())) {
            strFreqContent = String.format(str2, secondValueTmp + getString(R.string.repetition_type_day));
            firstValue = getResources().getString(R.string.repetition_type_day);

        } else if (freqType.equals(RecurrenceType.WEEKLY.getTypeName())) {
            strFreqContent = String.format(str2, secondValueTmp + getString(R.string.repetition_type_week));
            firstValue = getResources().getString(R.string.repetition_type_week);
        } else if (freqType.equals(RecurrenceType.MONTHLY.getTypeName())) {
            strFreqContent = String.format(str2, secondValueTmp + getString(R.string.repetition_type_month));
            firstValue = getResources().getString(R.string.repetition_type_month);
        } else {
            strFreqContent = String.format(str2, secondValueTmp + getString(R.string.repetition_type_day));
            firstValue = getResources().getString(R.string.repetition_type_day);
        }
        freqContent.setText(strFreqContent);
    }

    private void setClCalendarVisible(String freqType, String second) {
        if(freqType.equals(RecurrenceType.WEEKLY.getTypeName())){
            freqDateTitle.setText(R.string.repetition_week_day);
            if(clCalendar.getVisibility() != View.VISIBLE){
                clCalendar.setVisibility(View.VISIBLE);
            }
            weekView.setVisibility(View.VISIBLE);
            viewMonthContent.setVisibility(View.GONE);
            repetitiveRulePicker.setDefaultValue(getResources().getString(R.string.repetition_type_week), second,"");
        }else if(freqType.equals(RecurrenceType.MONTHLY.getTypeName())){
            freqDateTitle.setText(R.string.repetition_month_day);
            if(clCalendar.getVisibility() != View.VISIBLE){
                clCalendar.setVisibility(View.VISIBLE);
            }
            weekView.setVisibility(View.GONE);
            viewMonthContent.setVisibility(View.VISIBLE);
            repetitiveRulePicker.setDefaultValue(getResources().getString(R.string.repetition_type_month), second,"");
        }else{
            if(clCalendar.getVisibility() == View.VISIBLE){
                clCalendar.setVisibility(View.GONE);
            }
            repetitiveRulePicker.setDefaultValue(getResources().getString(R.string.repetition_type_day), second,"");
        }
    }

    private void updateLocalStore(){
        if(localStore != null){
            localStore.getScheduledMeetingSetting().setMeetingType(FrtcSDKMeetingType.RECURRENCE.getTypeName());
            if(firstValue.equals(getString(R.string.repetition_type_day))){
                localStore.getScheduledMeetingSetting().setRecurrence_type(RecurrenceType.DAILY.getTypeName());
            }else if(firstValue.equals(getString(R.string.repetition_type_week))){
                localStore.getScheduledMeetingSetting().setRecurrence_type(RecurrenceType.WEEKLY.getTypeName());
                List<Integer> saveList = new ArrayList<>();
                for(int i=0; i < dataWeek.length; i++){
                    if(!TextUtils.isEmpty(dataWeek[i])){
                        saveList.add(i + 1);
                    }
                }
                localStore.getScheduledMeetingSetting().setRecurrenceDaysOfWeek(saveList);
            }else if(firstValue.equals(getString(R.string.repetition_type_month))){
                localStore.getScheduledMeetingSetting().setRecurrence_type(RecurrenceType.MONTHLY.getTypeName());
                List<Integer> saveList = new ArrayList<>();
                for(int i=0; i < dataMonth.length; i++){
                    if(!TextUtils.isEmpty(dataMonth[i])){
                        saveList.add(Integer.parseInt(dataMonth[i]));
                    }
                }
                localStore.getScheduledMeetingSetting().setRecurrenceDaysOfMonth(saveList);
            }
            localStore.getScheduledMeetingSetting().setRecurrenceInterval(Integer.parseInt(secondValue));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            Date date;
            try {
                date = sdf.parse(meetingEndDate.getText().toString());
            } catch (ParseException e) {
                Log.e(TAG,"ParseException:"+e.toString());
                return;
            }
            localStore.getScheduledMeetingSetting().setRecurrenceEndDay(date.getTime() + 24 * 60 * 60 * 1000L - 1000L);
            localStore.getScheduledMeetingSetting().setRepetitionFreq(freqContent.getText().toString());
        }
    }

    private void setClickListener(View view){
        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Arrays.fill(dataWeek, "");
                Arrays.fill(dataMonth, "");
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        Button btnOk = view.findViewById(R.id.button_complete);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocalStore();
                int count = calcCount();
                Arrays.fill(dataWeek, "");
                Arrays.fill(dataMonth, "");
                mActivity.showScheduleMeetingFragment(meetingEndDate.getText().toString(), count, isUpdateRecurrence);
            }
        });

        RelativeLayout llMeetingEndDate = view.findViewById(R.id.ll_meeting_end_date);
        llMeetingEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onYearMonthDay();
            }
        });

    }

    private void onYearMonthDay() {
        DatePicker picker = new DatePicker(mActivity);
        DateWheelLayout wheelLayout = picker.getWheelLayout();
        wheelLayout.setDateMode(DateMode.YEAR_MONTH_DAY);
        wheelLayout.setDateFormatter(new UnitDateFormatter());
        wheelLayout.setRange(DateEntity.today(), DateEntity.target(2050, 12, 31));
        wheelLayout.setCurtainEnabled(true);
        wheelLayout.setCurtainColor(0xFFF4F4F5);
        wheelLayout.setIndicatorEnabled(false);
        wheelLayout.setTextColor(0xFFCCCCCC);

        wheelLayout.setTextSize(15 * getResources().getDisplayMetrics().scaledDensity);
        wheelLayout.setSelectedTextSize(18 * getResources().getDisplayMetrics().scaledDensity);
        wheelLayout.setSelectedTextColor(0xFF333333);
        picker.setOnDatePickedListener(this);
        picker.getWheelLayout().setResetWhenLinkage(false);
        picker.setTitle(mActivity.getResources().getString(R.string.repetition_meeting_end_date));
        picker.show();
    }


    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(mActivity.previousTag);
    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        long endTimeMill = calendar.getTimeInMillis();
        long endTimeMill1Year = Long.parseLong(localStore.getScheduledMeetingSetting().getStartTime()) + 365 * 24 * 60 * 60 *1000L;
        if(endTimeMill > endTimeMill1Year){
            endTimeMill = endTimeMill1Year;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        Date dateEnd = new Date(endTimeMill);
        String date = simpleDateFormat.format(dateEnd);
        meetingEndDate.setText(date);
    }

    @Override
    public void onKeyClick(View view, RecyclerView.ViewHolder holder, int position) {
        String day = viewMonthContent.getDatas().get(position);
        if(position == startDayPosition){
            BaseToast.showToast(mActivity, getString(R.string.not_cancel_selected_meeting_start_date), Toast.LENGTH_SHORT);
            return;
        }
        if(TextUtils.isEmpty(dataMonth[position])) {
            dataMonth[position] = day;
            ((TextView)view).setBackground(getResources().getDrawable(R.drawable.blue_oval));
            ((TextView)view).setTextColor(Color.WHITE);
        }else{
            dataMonth[position] = "";
            ((TextView)view).setBackground(null);
            ((TextView)view).setTextColor(Color.BLACK);
        }
        setFreqDescText();
    }

    private void setFreqDescText() {
        String datatmp = "";
        if(firstValue.equals(getResources().getString(R.string.repetition_type_week))){
            for (int i = 0, n = dataWeek.length; i < n; i++) {
                String week = dataWeek[i];
                if (!TextUtils.isEmpty(week)) {
                    if(datatmp.equals("")){
                        datatmp = getString(R.string.repetition_adj) + week;
                    }else{
                        datatmp += "、" + week;
                    }

                }
            }

        }else if(firstValue.equals(getResources().getString(R.string.repetition_type_month))){
            for (int i = 0, n = dataMonth.length; i < n; i++) {
                String monthDay = dataMonth[i];
                if (!TextUtils.isEmpty(monthDay)) {
                    if(datatmp.equals("")){
                        datatmp = getString(R.string.repetition_adj) + " " + monthDay + getString(R.string.repetition_type_day_th);
                    }else{
                        datatmp += "、" + monthDay + getString(R.string.repetition_type_day_th);
                    }

                }
            }
        }
        
        String str = mActivity.getString(R.string.repetition_frequency_desc);
        String str1 = "";
        if(secondValue.equals("1")){
            str1 = String.format(str, firstValue + datatmp);
        }else {
            str1 = String.format(str, secondValue + firstValue + datatmp);
        }
        freqDesc.setText(str1);
    }

    private void setEndDay(String freqType, String meetingType) {
        String recurrenceType = localStore.getScheduledMeetingSetting().getRecurrenceType();
        if(!TextUtils.isEmpty(meetingType) && meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())
                && !TextUtils.isEmpty(recurrenceType) && recurrenceType.equals(freqType)){
            long endDay = localStore.getScheduledMeetingSetting().getRecurrenceEndDay();
            meetingEndDate.setText(MeetingUtil.timeFormat(endDay, "yyyy年MM月dd日"));
            return;
        }
        ChronoUnit chronoUnit = null;
        if(firstValue.equals(getResources().getString(R.string.repetition_type_day))){
            chronoUnit = ChronoUnit.DAYS;
        }else if(firstValue.equals(getResources().getString(R.string.repetition_type_week))){
            chronoUnit = ChronoUnit.WEEKS;
        }else if(firstValue.equals(getResources().getString(R.string.repetition_type_month))){
            chronoUnit = ChronoUnit.MONTHS;
        }else{
            chronoUnit = ChronoUnit.DAYS;
        }
        LocalDate nowLocalDate = LocalDate.now();
        LocalDate nowLocalDatePlus = nowLocalDate.plus((Long.parseLong(secondValue) * defaultFrequencyCount), chronoUnit);
        Log.d(TAG,"nowLocalDatePlus1Day2: " + nowLocalDatePlus);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        String datePlusStr = nowLocalDatePlus.plusDays(-1).format(fmt);
        long datePlus = MeetingUtil.timeFormatToLong(datePlusStr, "yyyy年MM月dd日");

        LocalDate nowLocalDatePlus1Year = nowLocalDate.plus(1, ChronoUnit.YEARS);
        String nowLocalDatePlus1YearStr = nowLocalDatePlus1Year.plusDays(-1).format(fmt);
        long datePlus1Year = MeetingUtil.timeFormatToLong(nowLocalDatePlus1YearStr, "yyyy年MM月dd日");
        if((datePlus - datePlus1Year) > 0){
            meetingEndDate.setText(nowLocalDatePlus1YearStr);
        }else {
            meetingEndDate.setText(datePlusStr);
        }
    }

    private int calcCount() {
        int count = 0;
        Calendar firstTime = getFirstTime();
        Calendar lastTime = getLastTime();
        Date endDate = new Date(localStore.getScheduledMeetingSetting().getRecurrenceEndDay());
        long now = System.currentTimeMillis();
        if(firstValue.equals(getResources().getString(R.string.repetition_type_day))){
            count = calcDailyCount(endDate);
        }else if(firstValue.equals(getResources().getString(R.string.repetition_type_week))){
            count = calcWeeklyCount(firstTime, lastTime, now);
        }else if(firstValue.equals(getResources().getString(R.string.repetition_type_month))){
            count = calcMonthlyCount(firstTime, lastTime, now);
        }
        return count;
    }

    private int calcMonthlyCount(Calendar firstTime, Calendar lastTime, long now) {
        int count = 0;

        List<Integer> daysOfMonth = localStore.getScheduledMeetingSetting().getRecurrenceDaysOfMonth();
        Collections.sort(daysOfMonth);
        Calendar nextExecutionTime = firstTime;

        while (nextExecutionTime.compareTo(lastTime) < 0) {
            for (int dayOfMonth : daysOfMonth) {
                nextExecutionTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (MeetingUtil.isInvalidDate(dayOfMonth, nextExecutionTime)) {
                    continue;
                }
                if (nextExecutionTime.getTimeInMillis() < now || nextExecutionTime.getTimeInMillis() < localStore.getScheduledMeetingSetting().getRecurrenceStartDay()) {
                    Log.d(TAG,"skip nextExecutionTime =" +nextExecutionTime.getTimeInMillis() + ", now =" + now);
                    continue;
                }
                if (nextExecutionTime.compareTo(firstTime) < 0) {
                    Log.d(TAG,"skip nextExecutionTime"+ nextExecutionTime.getTimeInMillis()+ ", firstTime =" +firstTime);
                    continue;
                }
                if (nextExecutionTime.compareTo(lastTime) > 0) {
                    break;
                }
                count++;
            }

            Calendar cal = Calendar.getInstance();

            nextExecutionTime.set(Calendar.DAY_OF_MONTH, 1);
            LocalDateTime localDateTime = MeetingUtil.toLocalDateTime(nextExecutionTime.getTime(), ZoneId.systemDefault());
            localDateTime = localDateTime.plusMonths(Integer.parseInt(secondValue));
            cal.setTime(MeetingUtil.LocalDateTimetoDate(localDateTime, ZoneId.systemDefault()));

            nextExecutionTime = cal;
        }
        return count;
    }

    private int calcWeeklyCount(Calendar firstTime, Calendar lastTime, long now) {
        int count = 0;
        List<Integer> daysOfWeek = localStore.getScheduledMeetingSetting().getRecurrenceDaysOfWeek();
        Collections.sort(daysOfWeek);
        Calendar nextExecutionTime = firstTime;

        while (nextExecutionTime.compareTo(lastTime) < 0) {
            int minDayOfWeek = daysOfWeek.get(0);
            for (int dayOfWeek : daysOfWeek) {
                nextExecutionTime.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                if (nextExecutionTime.getTimeInMillis() < now || nextExecutionTime.getTimeInMillis() < localStore.getScheduledMeetingSetting().getRecurrenceStartDay()) {
                    Log.d(TAG,"skip nextExecutionTime =" +nextExecutionTime.getTimeInMillis() + ", now =" + now);
                    continue;
                }
                if (nextExecutionTime.compareTo(firstTime) < 0) {
                    Log.d(TAG,"skip nextExecutionTime"+ nextExecutionTime.getTimeInMillis()+ ", firstTime =" +firstTime);
                    continue;
                }
                if (nextExecutionTime.compareTo(lastTime) > 0) {
                    break;
                }
                count++;
            }
            Calendar cal = Calendar.getInstance();
            nextExecutionTime.set(Calendar.DAY_OF_WEEK, minDayOfWeek);
            LocalDateTime localDateTime = MeetingUtil.toLocalDateTime(nextExecutionTime.getTime(), ZoneId.systemDefault());
            localDateTime = localDateTime.plusWeeks(Integer.parseInt(secondValue));
            cal.setTime(MeetingUtil.LocalDateTimetoDate(localDateTime, ZoneId.systemDefault()));

            nextExecutionTime = cal;
        }
        return count;
    }

    private int calcDailyCount(Date endDate) {
        String strStartTime = localStore.getScheduledMeetingSetting().getStartTime();
        Date nextExecutionDate = new Date(Long.parseLong(strStartTime));
        int count = 0;
        while (nextExecutionDate.compareTo(endDate) < 0) {
            count++;

            Calendar cal = Calendar.getInstance();
            LocalDateTime localDateTime = MeetingUtil.toLocalDateTime(nextExecutionDate, ZoneId.systemDefault());
            localDateTime = localDateTime.plusDays(Integer.parseInt(secondValue));
            cal.setTime(MeetingUtil.LocalDateTimetoDate(localDateTime, ZoneId.systemDefault()));

            nextExecutionDate = cal.getTime();
        }
        return count;
    }

    public Calendar getFirstTime() {
        String strStartTime = localStore.getScheduledMeetingSetting().getStartTime();
        Calendar firstTime = Calendar.getInstance();
        firstTime.setTimeInMillis(Long.parseLong(strStartTime));
        return firstTime;

    }

    private Calendar getLastTime() {
        Calendar endDay = Calendar.getInstance();
        endDay.setTimeInMillis(localStore.getScheduledMeetingSetting().getRecurrenceEndDay());
        return endDay;
    }

}
