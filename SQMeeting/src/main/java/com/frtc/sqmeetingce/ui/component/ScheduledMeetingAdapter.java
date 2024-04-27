package com.frtc.sqmeetingce.ui.component;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import frtc.sdk.internal.model.FrtcSDKMeetingType;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;

;

public class ScheduledMeetingAdapter extends RecyclerView.Adapter<ScheduledMeetingAdapter.ViewHolder>{

    protected final String TAG = this.getClass().getSimpleName();

    private MainActivity mActivity;

    private ArrayList<ScheduledMeeting> mMeetingCalls;

    private OnItemClickListener mListener;

    private String userId;
    private LocalStore localStore;
    boolean isInvitedMeeting = false;
    boolean isOwner;

    public interface OnItemClickListener{
        void onItemClicked(ScheduledMeeting selectedMeetingCall);
    }

    public void setOnItemClickedListener(ScheduledMeetingAdapter.OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View meetingCallView;
        TextView meetingName;
        TextView meetingNumber;
        TextView meetingTime;
        TextView invitedSign;
        TextView startStatus;
        TextView recurrenceSign;

        public ViewHolder(View view) {
            super(view);
            meetingCallView = view;
            meetingName = view.findViewById(R.id.meeting_name);
            meetingNumber = view.findViewById(R.id.meeting_number);
            meetingTime = view.findViewById(R.id.meeting_time);
            invitedSign = view.findViewById(R.id.invited_sign);
            startStatus = view.findViewById(R.id.start_status);
            recurrenceSign = view.findViewById(R.id.recurrence_sign);

        }
    }

    public ScheduledMeetingAdapter(MainActivity mActivity, ArrayList<ScheduledMeeting> meetingList) {
        this.mActivity = mActivity;
        mMeetingCalls = meetingList;
        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    @Override
    public ScheduledMeetingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scheduled_meeting_item, parent, false);
        final ScheduledMeetingAdapter.ViewHolder holder = new ScheduledMeetingAdapter.ViewHolder(view);
        holder.meetingCallView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ScheduledMeeting meetingCall = mMeetingCalls.get(position);
                if(mListener != null){
                    mListener.onItemClicked(meetingCall);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ScheduledMeetingAdapter.ViewHolder holder, int position) {

        ScheduledMeeting meetingCall = mMeetingCalls.get(position);
        String ownerId = meetingCall.getOwner_id();
        List<String>  participantUsers = meetingCall.getParticipantUsers();
        isOwner = localStore.getUserId().equals(ownerId);
        if(participantUsers != null) {
            isInvitedMeeting = participantUsers.contains(localStore.getUserId());
        }

        holder.meetingName.setText(meetingCall.getMeeting_name());
        holder.meetingNumber.setText(meetingCall.getMeeting_number());
        holder.invitedSign.setVisibility((isOwner || !isInvitedMeeting) ? View.GONE : View.VISIBLE);
        holder.recurrenceSign.setVisibility(meetingCall.getMeeting_type().equals(FrtcSDKMeetingType.RECURRENCE.getTypeName()) ? View.VISIBLE : View.GONE);

        String startTimeStr = meetingCall.getSchedule_start_time();
        String endTimeStr = meetingCall.getSchedule_end_time();

        long startTime = Long.parseLong(startTimeStr);
        long endTime = Long.parseLong(endTimeStr);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat endFormat = new SimpleDateFormat("HH:mm");
        Date dateStart = new Date(startTime);
        Date dateEnd = new Date(endTime);
        String str = startFormat.format(dateStart) + "-" + endFormat.format(dateEnd);
        holder.meetingTime.setText(str);

        long currentTime = System.currentTimeMillis();

        if(startTime < currentTime && endTime > currentTime){
            holder.startStatus.setText(mActivity.getResources().getString(R.string.scheduled_meeting_status_started));
            holder.startStatus.setTextColor(mActivity.getResources().getColor(R.color.started_status_text_color));
            holder.startStatus.setVisibility(View.VISIBLE);
        }else if(startTime > currentTime && (startTime-currentTime) < 900000){
            holder.startStatus.setText(mActivity.getResources().getString(R.string.scheduled_meeting_status_starting));
            holder.startStatus.setTextColor(mActivity.getResources().getColor(R.color.starting_status_text_color));
            holder.startStatus.setVisibility(View.VISIBLE);
        }else {
            holder.startStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mMeetingCalls.size();
    }

}