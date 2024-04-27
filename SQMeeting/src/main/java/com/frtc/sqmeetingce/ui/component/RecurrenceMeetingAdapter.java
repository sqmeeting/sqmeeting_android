package com.frtc.sqmeetingce.ui.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frtc.sqmeetingce.R;
import com.frtc.sqmeetingce.util.MeetingUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;

;

public class RecurrenceMeetingAdapter extends RecyclerView.Adapter<RecurrenceMeetingAdapter.ViewHolder>{

    protected final String TAG = this.getClass().getSimpleName();

    private Activity mActivity;

    private ArrayList<ScheduledMeeting> mMeetingCalls;

    private OnItemClickListener mListener;
    private LocalStore userSetting;

    public interface OnItemClickListener{
        void onItemClicked(ScheduledMeeting selectedMeetingCall, int position);
    }

    public void setOnItemClickedListener(RecurrenceMeetingAdapter.OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View meetingCallView;
        TextView meetingDate;
        TextView meetingDay;
        TextView meetingTime;
        TextView startStatus;
        ImageView itemMore;

        public ViewHolder(View view) {
            super(view);
            meetingCallView = view;
            meetingDate = view.findViewById(R.id.meeting_date);
            meetingDay = view.findViewById(R.id.week_day);
            meetingTime = view.findViewById(R.id.meeting_time);
            startStatus = view.findViewById(R.id.start_status);
            itemMore = view.findViewById(R.id.show_more);

        }
    }

    public RecurrenceMeetingAdapter(Activity mActivity, ArrayList<ScheduledMeeting> meetingList) {
        this.mActivity = mActivity;
        mMeetingCalls = meetingList;
        userSetting = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
    }

    @Override
    public RecurrenceMeetingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recurrence_meeting_item, parent, false);
        final RecurrenceMeetingAdapter.ViewHolder holder = new RecurrenceMeetingAdapter.ViewHolder(view);
        holder.meetingCallView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ScheduledMeeting meetingCall = mMeetingCalls.get(position);
                String ownerId = meetingCall.getOwner_id();
                boolean isOwner = userSetting.getUserId().equals(ownerId);
                if(!isOwner){
                    return;
                }
                if(mListener != null){
                    mListener.onItemClicked(meetingCall, position);
                }
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(RecurrenceMeetingAdapter.ViewHolder holder, int position) {

        ScheduledMeeting meetingCall = mMeetingCalls.get(position);
        String ownerId = meetingCall.getOwner_id();
        boolean isOwner = userSetting.getUserId().equals(ownerId);
        if(!isOwner){
            holder.itemMore.setVisibility(View.GONE);
        }
        String startTimeStr = meetingCall.getSchedule_start_time();
        String endTimeStr = meetingCall.getSchedule_end_time();

        long startTime = Long.parseLong(startTimeStr);
        long endTime = Long.parseLong(endTimeStr);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date dateStart = new Date(startTime);
        Date dateEnd = new Date(endTime);

        String strDate = dateFormat.format(dateStart);
        holder.meetingDate.setText(strDate);
        holder.meetingDay.setText(MeetingUtil.dateToWeek(mActivity, strDate));
        String str = timeFormat.format(dateStart) + "-" + timeFormat.format(dateEnd);
        holder.meetingTime.setText(str);

        long currentTime = System.currentTimeMillis();

        if(startTime < currentTime && endTime > currentTime){
            holder.startStatus.setText(mActivity.getResources().getString(R.string.scheduled_meeting_status_started));
            holder.startStatus.setTextColor(mActivity.getResources().getColor(R.color.started_status_text_color));
            holder.startStatus.setVisibility(View.VISIBLE);
            holder.meetingDate.setTextColor(mActivity.getResources().getColor(R.color.blue));
            holder.meetingDay.setTextColor(mActivity.getResources().getColor(R.color.blue));
            holder.meetingTime.setTextColor(mActivity.getResources().getColor(R.color.blue));
        }else if(startTime > currentTime && (startTime-currentTime) < 900000){
            holder.startStatus.setText(mActivity.getResources().getString(R.string.scheduled_meeting_status_starting));
            holder.startStatus.setTextColor(mActivity.getResources().getColor(R.color.starting_status_text_color));
            holder.startStatus.setVisibility(View.VISIBLE);
            holder.meetingDate.setTextColor(mActivity.getResources().getColor(R.color.text_color_bold));
            holder.meetingDay.setTextColor(mActivity.getResources().getColor(R.color.text_color_gray));
            holder.meetingTime.setTextColor(mActivity.getResources().getColor(R.color.text_color_gray));
        }else {
            holder.startStatus.setVisibility(View.GONE);
            holder.meetingDate.setTextColor(mActivity.getResources().getColor(R.color.text_color_bold));
            holder.meetingDay.setTextColor(mActivity.getResources().getColor(R.color.text_color_gray));
            holder.meetingTime.setTextColor(mActivity.getResources().getColor(R.color.text_color_gray));
        }
        if (position % 2 == 0){
            holder.meetingCallView.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mMeetingCalls.size();
    }

}