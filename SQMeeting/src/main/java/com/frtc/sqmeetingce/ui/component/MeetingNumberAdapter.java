package com.frtc.sqmeetingce.ui.component;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frtc.sqmeetingce.R;

import java.util.ArrayList;

import frtc.sdk.ui.model.MeetingCall;

public class MeetingNumberAdapter extends RecyclerView.Adapter<MeetingNumberAdapter.ViewHolder>{

    protected final String TAG = this.getClass().getSimpleName();

    final private ArrayList<MeetingCall> mHistoryMeetings;

    private OnItemClickListener mListener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View meetingView;
        TextView meetingName;
        TextView meetingNumber;

        public ViewHolder(View view) {
            super(view);
            meetingView = view;
            meetingName = view.findViewById(R.id.meeting_room_name);
            meetingNumber = view.findViewById(R.id.meeting_room_number);
        }
    }

    public MeetingNumberAdapter(ArrayList<MeetingCall> MeetingCallList) {
        mHistoryMeetings = MeetingCallList;
    }

    public interface OnItemClickListener{
        void onItemClicked(MeetingCall selectedMeetingCall);
    }

    public void setOnItemClickedListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_room_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.meetingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MeetingCall meeting = mHistoryMeetings.get(position);
                if(mListener != null){
                    mListener.onItemClicked(meeting);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MeetingCall meeting = mHistoryMeetings.get(position);
        holder.meetingName.setText(meeting.getMeetingName());
        holder.meetingNumber.setText(meeting.getMeetingNumber());
    }

    @Override
    public int getItemCount() {
        return mHistoryMeetings.size();
    }
}

