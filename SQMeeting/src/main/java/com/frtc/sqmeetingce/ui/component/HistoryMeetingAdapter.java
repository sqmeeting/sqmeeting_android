package com.frtc.sqmeetingce.ui.component;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frtc.sqmeetingce.R;

import java.util.ArrayList;

import frtc.sdk.internal.model.FrtcSDKMeetingType;
import frtc.sdk.ui.model.MeetingCall;


public class HistoryMeetingAdapter extends RecyclerView.Adapter<HistoryMeetingAdapter.ViewHolder>{

    protected final String TAG = this.getClass().getSimpleName();

    private ArrayList<MeetingCall> mMeetingCalls;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClicked(MeetingCall selectedMeetingCall);
    }

    public void setOnItemClickedListener(HistoryMeetingAdapter.OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View meetingCallView;
        TextView meetingName;
        TextView meetingNumber;
        TextView meetingTime;
        TextView recurrenceSign;

        public ViewHolder(View view) {
            super(view);
            meetingCallView = view;
            meetingName = view.findViewById(R.id.meeting_name);
            meetingNumber = view.findViewById(R.id.meeting_number);
            meetingTime = view.findViewById(R.id.meeting_time);
            recurrenceSign = view.findViewById(R.id.recurrence_sign);
        }
    }

        public HistoryMeetingAdapter(ArrayList<MeetingCall> meetingList) {
            mMeetingCalls = meetingList;
        }

        @SuppressLint("NotifyDataSetChanged")
        public void setHistoryMeetingList(ArrayList<MeetingCall> meetingList){
            mMeetingCalls = meetingList;
            notifyDataSetChanged();
        }

        @Override
        public HistoryMeetingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_meeting_item, parent, false);
            final HistoryMeetingAdapter.ViewHolder holder = new HistoryMeetingAdapter.ViewHolder(view);
            holder.meetingCallView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    MeetingCall meetingCall = mMeetingCalls.get(position);
                    if(mListener != null){
                        mListener.onItemClicked(meetingCall);
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(HistoryMeetingAdapter.ViewHolder holder, int position) {
            MeetingCall meetingCall = mMeetingCalls.get(position);
            holder.meetingName.setText(meetingCall.getMeetingName());
            holder.meetingNumber.setText(meetingCall.getMeetingNumber());
            holder.meetingTime.setText(meetingCall.getTime());
            String meetingType = meetingCall.getMeetingType();
            holder.recurrenceSign.setVisibility((!TextUtils.isEmpty(meetingType) && meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName()))? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return mMeetingCalls.size();
        }

}



