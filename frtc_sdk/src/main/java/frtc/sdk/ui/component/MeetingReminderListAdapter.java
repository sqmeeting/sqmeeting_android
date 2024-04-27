package frtc.sdk.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import frtc.sdk.R;
import frtc.sdk.model.ScheduledMeeting;


public class MeetingReminderListAdapter extends ArrayAdapter<ScheduledMeeting> {

    private static String TAG = MeetingReminderListAdapter.class.getSimpleName();
    private List<ScheduledMeeting> meetingReminderListData = new ArrayList<>();
    private LayoutInflater inflater;
    private Context mContext;
    private ClickViewListener clickViewListener;

    public MeetingReminderListAdapter(Context context, int resource, List<ScheduledMeeting> objects) {
        super(context, resource, objects);
        mContext = context;
        meetingReminderListData = objects;

        inflater = LayoutInflater.from(context);

    }

    public void setData(List<ScheduledMeeting> data) {
        this.meetingReminderListData = data;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClicked(ScheduledMeeting selectedMeeting, int position);
    }

    public void setOnClickViewListener(ClickViewListener clickViewListener) {
        this.clickViewListener = clickViewListener;
    }

    @Override
    public int getCount() {
        return meetingReminderListData.size();
    }

    @Override
    public ScheduledMeeting getItem(int position) {
        return meetingReminderListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.meeting_reminder_listview_item, parent, false);
            holder = new Holder();
            holder.meetingName = convertView.findViewById(R.id.meeting_name);
            holder.meetingTime = convertView.findViewById(R.id.meeting_time);
            holder.meetingOwner = convertView.findViewById(R.id.meeting_owner);
            holder.callBtn = convertView.findViewById(R.id.call_btn);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        ScheduledMeeting scheduledMeeting = meetingReminderListData.get(position);
        String startTimeStr = scheduledMeeting.getSchedule_start_time();
        String endTimeStr = scheduledMeeting.getSchedule_end_time();
        long startTime = Long.parseLong(startTimeStr);
        long endTime = Long.parseLong(endTimeStr);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormatStart = new SimpleDateFormat("MM/dd HH:mm");
        SimpleDateFormat timeFormatEnd = new SimpleDateFormat("HH:mm");
        Date dateStart = new Date(startTime);
        Date dateEnd = new Date(endTime);
        String str = timeFormatStart.format(dateStart) + "-" + timeFormatEnd.format(dateEnd);

        holder.meetingName.setText(scheduledMeeting.getMeeting_name());
        holder.meetingTime.setText(str);
        holder.meetingOwner.setText(scheduledMeeting.getOwner_name());
        holder.callBtn.setTag(position);

        holder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickViewListener != null && position < meetingReminderListData.size()){
                    clickViewListener.onClick(position, meetingReminderListData.get(position));
                }
            }
        });

        return convertView;
    }

    private class Holder {
        public TextView meetingName;
        public TextView meetingTime;
        public TextView meetingOwner;
        public Button callBtn;
    }

    public interface ClickViewListener {
        void onClick(int position, ScheduledMeeting meeting);
    }

}
