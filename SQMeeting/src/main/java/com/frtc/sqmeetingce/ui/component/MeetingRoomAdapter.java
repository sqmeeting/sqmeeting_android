package com.frtc.sqmeetingce.ui.component;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frtc.sqmeetingce.R;

import java.util.ArrayList;

import frtc.sdk.model.MeetingRoom;

public class MeetingRoomAdapter extends RecyclerView.Adapter<MeetingRoomAdapter.ViewHolder>{

    protected final String TAG = this.getClass().getSimpleName();

    final private ArrayList<MeetingRoom> mMeetingRooms;

    private OnItemClickListener mListener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View MeetingRoomView;
        TextView meetingName;
        TextView meetingNumber;

        public ViewHolder(View view) {
            super(view);
            MeetingRoomView = view;
            meetingName = view.findViewById(R.id.meeting_room_name);
            meetingNumber = view.findViewById(R.id.meeting_room_number);
        }
    }

    public MeetingRoomAdapter(ArrayList<MeetingRoom> meetingRoomList) {
        mMeetingRooms = meetingRoomList;
    }

    public interface OnItemClickListener{
        void onItemClicked(MeetingRoom selectedMeetingRoom);
    }

    public void setOnItemClickedListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_room_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.MeetingRoomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MeetingRoom meetingRoom = mMeetingRooms.get(position);
                if(mListener != null){
                    mListener.onItemClicked(meetingRoom);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MeetingRoom meetingRoom = mMeetingRooms.get(position);
        holder.meetingName.setText(meetingRoom.getMeetingRoomName());
        holder.meetingNumber.setText(meetingRoom.getMeetingNumber());
    }

    @Override
    public int getItemCount() {
        return mMeetingRooms.size();
    }

}

