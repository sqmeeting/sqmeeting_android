package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import frtc.sdk.IMeetingReminderListener;
import frtc.sdk.R;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.ui.view.FixItemListView;
import frtc.sdk.ui.component.MeetingReminderListAdapter;
import frtc.sdk.util.BaseDialog;

public class MeetingReminderDlg extends BaseDialog {

    private static final String TAG = MeetingReminderDlg.class.getSimpleName();
    private Context mContext;
    private FixItemListView mListView;
    private TextView meetingReminderTitle;
    private MeetingReminderListAdapter mAdapter;
    private ArrayList<ScheduledMeeting> meetingReminderList = new ArrayList<>();
    private IMeetingReminderListener mListener;

    public MeetingReminderDlg(Context context, ArrayList<ScheduledMeeting> data) {
        super(context, R.style.DialogTheme);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingReminderList.clear();
        meetingReminderList.addAll(data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.meeting_reminder_listview_layout);

        mListView = findViewById(R.id.meeting_reminder_list_view);
        mListView.setFooterDividersEnabled(true);

        mAdapter = new MeetingReminderListAdapter(getContext(), R.layout.meeting_reminder_listview_item, meetingReminderList);
        mAdapter.setOnClickViewListener(new MeetingReminderListAdapter.ClickViewListener() {
            @Override
            public void onClick(int position, ScheduledMeeting scheduledMeeting) {
                mListener.onJoinMeetingCallback(scheduledMeeting);
                dismiss();
            }

        });

        mListView.setAdapter(mAdapter);

        meetingReminderTitle = findViewById(R.id.meeting_reminder_title);

        if (meetingReminderList != null) {
            String str = mContext.getString(R.string.meeting_reminders);
            String str1 = String.format(str,""+meetingReminderList.size());
            meetingReminderTitle.setText(str1);
        }

        Button ignoreBtn = findViewById(R.id.ignore_btn);
        ignoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onIgnoreCallback();
                dismiss();
            }
        });

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition

            return;
        }


        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < 3 && i < listAdapter.getCount(); i++) //here you can set 5 row at a time if row excceded the scroll automatically available
        {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    public void setMeetingReminderListener(IMeetingReminderListener listener){
        this.mListener = listener;
    }


}
