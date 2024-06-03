
package com.frtc.sqmeetingce.ui.component;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;
import com.frtc.sqmeetingce.util.MeetingUtil;

import frtc.sdk.internal.model.FrtcSDKMeetingType;
import frtc.sdk.log.Log;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.ui.model.RecurrenceType;




public class ScheduleMeetingRepetitionFreqFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();
    private LocalStore localStore;
    public MainActivity mActivity;

    private ImageView ivFreqNo;
    private TextView freqDay, freqWeek, freqMonth, freqEndDay, freqEndWeek, freqEndMonth;

    private boolean isUpdate = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG,"onCreateView");
        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.schedule_meeting_repetition_freq_fragment, container, false);

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
        init(view);
        setClickListener(view);
        /*
        if(isUpdate){
            mActivity.previousTag = FragmentTagEnum.FRAGMENT_UPDATE_SCHEDULED_MEETING;
        }else {
            mActivity.previousTag = FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING;
        }
         */
        return view;
    }

    private void init(View view) {

        ivFreqNo = view.findViewById(R.id.freq_no_selected);
        freqDay = view.findViewById(R.id.freq_day);
        freqEndDay = view.findViewById(R.id.freq_end_day);
        freqWeek = view.findViewById(R.id.freq_week);
        freqEndWeek = view.findViewById(R.id.freq_end_week);
        freqMonth = view.findViewById(R.id.freq_month);
        freqEndMonth = view.findViewById(R.id.freq_end_month);

        Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = bundle.getBoolean("isUpdate");
        }

        String meetingType = localStore.getScheduledMeetingSetting().getMeetingType();
        if(!TextUtils.isEmpty(meetingType) && meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
            ivFreqNo.setVisibility(View.GONE);

            String repetitionFreq = MeetingUtil.formatRecurrenceTypeContent(mActivity, localStore.getScheduledMeetingSetting().getRecurrenceType(),
                    localStore.getScheduledMeetingSetting().getRecurrenceInterval());

            String recurrenceEndDay = MeetingUtil.timeFormat(localStore.getScheduledMeetingSetting().getRecurrenceEndDay(), "yyyy年MM月dd日");
            int totalSize = localStore.getScheduledMeetingSetting().getRecurrenceCount();
            String repetitionEndDes = String.format(mActivity.getResources().getString(R.string.recurrence_end), recurrenceEndDay, totalSize+"");

            String recurrenceType = localStore.getScheduledMeetingSetting().getRecurrence_type();
            if(!TextUtils.isEmpty(recurrenceType)) {
                if (recurrenceType.equals(RecurrenceType.DAILY.getTypeName())){
                    freqDay.setText(repetitionFreq);
                    freqEndDay.setText(repetitionEndDes);
                }else if (recurrenceType.equals(RecurrenceType.WEEKLY.getTypeName())) {
                    freqWeek.setText(repetitionFreq);
                    freqEndWeek.setText(repetitionEndDes);
                } else if (recurrenceType.equals(RecurrenceType.MONTHLY.getTypeName())) {
                    freqMonth.setText(repetitionFreq);
                    freqEndMonth.setText(repetitionEndDes);
                }
            }
        }
    }
    private void updateLocalStore(){
        if(localStore != null){
            localStore.getScheduledMeetingSetting().setRepetitionFreq("");
            localStore.getScheduledMeetingSetting().setMeetingType(FrtcSDKMeetingType.RESERVATION.getTypeName());
        }
    }

    private void setClickListener(View view) {
        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.showScheduleMeetingFragment(isUpdate);
            }
        });

        TextView freqNo = view.findViewById(R.id.freq_no);
        if(isUpdate){
            freqNo.setTextColor(mActivity.getResources().getColor(R.color.text_color_default_hint));
        }
        freqNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUpdate){
                    return;
                }
                if(ivFreqNo.getVisibility() != View.VISIBLE){
                    ivFreqNo.setVisibility(View.VISIBLE);
                }
                updateLocalStore();
            }
        });

        freqDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_REPETITION_FREQ;
                mActivity.showScheduleMeetingRepetitionFreqSetting(RecurrenceType.DAILY.getTypeName(), isUpdate);
            }
        });

        freqWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_REPETITION_FREQ;
                mActivity.showScheduleMeetingRepetitionFreqSetting(RecurrenceType.WEEKLY.getTypeName(), isUpdate);
            }
        });

        freqMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_REPETITION_FREQ;
                mActivity.showScheduleMeetingRepetitionFreqSetting(RecurrenceType.MONTHLY.getTypeName(), isUpdate);
            }
        });

    }

    @Override
    public void onBack() {
        mActivity.showScheduleMeetingFragment(isUpdate);
    }


}
