package frtc.sdk.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import frtc.sdk.R;
import frtc.sdk.internal.model.MediaStatsInfo;
import frtc.sdk.internal.model.MeetingMediaStatsWrapper;
import frtc.sdk.ui.component.MeetingStatsAdapter;
import frtc.sdk.util.BaseDialog;
import frtc.sdk.ui.model.MeetingStatsInfo;


public class MeetingMediaStatsDlg extends BaseDialog {
    private static final String TAG = MeetingMediaStatsDlg.class.getSimpleName();
    private Context context;

    private List<MeetingStatsInfo> meetingStatsInfoList = new ArrayList<>();
    private MeetingStatsAdapter adapter;

    private ListView listView;

    private TextView tvMeetingName;
    private TextView tvMeetingId;
    private TextView rate;
    private ImageButton closeBtn;

    private String meetingId = "";
    private String meetingName = "";

    public MeetingMediaStatsDlg(Context context, String meetingId, String meetingName) {
        super(context, R.style.NoMaskDialogTheme);
        this.context = context;
        this.meetingName = meetingName;
        this.meetingId = meetingId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.meeting_stats_dialog);
        tvMeetingName = findViewById(R.id.room_name);
        tvMeetingId = findViewById(R.id.room_number);
        closeBtn = findViewById(R.id.back_button);
        rate = findViewById(R.id.rate);

        listView = findViewById(R.id.stats_list_view);
        listView.setFooterDividersEnabled(true);

        adapter = new MeetingStatsAdapter(context,R.layout.meeting_stats_item, meetingStatsInfoList);
        listView.setAdapter(adapter);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setMeetingId(meetingId);
        setMeetingName(meetingName);

    }

    public void setMeetingId(String meetingId) {
        if(meetingId != null && !meetingId.isEmpty()){
            this.meetingId = meetingId;
            if(tvMeetingId != null){
                tvMeetingId.setVisibility(View.VISIBLE);
                tvMeetingId.setText(meetingId);
            }
        }
    }

    public void setMeetingName(String name) {
        if(name != null && !name.isEmpty()){
            this.meetingName = name;
            if(tvMeetingName != null){
                tvMeetingName.setVisibility(View.VISIBLE);
                tvMeetingName.setText(name);
            }
        }

    }

    public void updateMeetingStatsData(MeetingMediaStatsWrapper data){
        if(data != null && data.mediaStatistics != null){
            meetingStatsInfoList.clear();

            for(MediaStatsInfo info : data.mediaStatistics.apr){
                meetingStatsInfoList.add(new MeetingStatsInfo("apr", info));
            }
            for(MediaStatsInfo info : data.mediaStatistics.aps){
                meetingStatsInfoList.add(new MeetingStatsInfo("aps", info));
            }
            for(MediaStatsInfo info : data.mediaStatistics.vcr){
                meetingStatsInfoList.add(new MeetingStatsInfo("vcr", info));
            }
            for(MediaStatsInfo info : data.mediaStatistics.vcs){
                meetingStatsInfoList.add(new MeetingStatsInfo("vcs", info));
            }
            for(MediaStatsInfo info : data.mediaStatistics.vpr){
                meetingStatsInfoList.add(new MeetingStatsInfo("vpr", info));
            }
            for(MediaStatsInfo info : data.mediaStatistics.vps){
                meetingStatsInfoList.add(new MeetingStatsInfo("vps", info));
            }

            adapter.notifyDataSetChanged();
        }

        if(data != null && data.signalStatistics != null){
            String callRate;
            if (data.signalStatistics.callRate > 100000) {
                callRate = String.format(Locale.US, "%d", data.signalStatistics.callRate / 100000) + " / "
                        + String.format(Locale.US, "%d", data.signalStatistics.callRate % 100000) + " Kbps";
            }else{
                callRate = String.format(Locale.US, "%d", data.signalStatistics.callRate)
                        + " Kbps";
            }
            rate.setText(callRate);
        }
    }

}
