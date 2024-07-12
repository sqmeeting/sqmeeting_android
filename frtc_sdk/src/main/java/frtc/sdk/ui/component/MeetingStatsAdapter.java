package frtc.sdk.ui.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import frtc.sdk.R;
import frtc.sdk.internal.model.MediaStatsInfo;
import frtc.sdk.log.Log;
import frtc.sdk.ui.model.MeetingStatsInfo;

public class MeetingStatsAdapter extends ArrayAdapter<MeetingStatsInfo> {

    private static String TAG = MeetingStatsAdapter.class.getSimpleName();
    private List<MeetingStatsInfo> meetingStatsInfoList = new ArrayList<>();

    private LayoutInflater inflater;
    private Context mContext;

    private final String AUDIO = "Audio";
    private final String VIDEO = "Video";
    private final String CONTENT = "Content";

    public MeetingStatsAdapter(Context context, int resource, List<MeetingStatsInfo> data) {
        super(context, resource, data);
        mContext = context;
        meetingStatsInfoList = data;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return meetingStatsInfoList.size();
    }

    @Override
    public MeetingStatsInfo getItem(int position) {
        return meetingStatsInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MeetingStatsAdapter.Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.meeting_stats_item, parent, false);
            holder = new MeetingStatsAdapter.Holder();
            holder.name = convertView.findViewById(R.id.name);
            holder.media = convertView.findViewById(R.id.media);
            holder.format = convertView.findViewById(R.id.resolution);
            holder.actualRate = convertView.findViewById(R.id.real_rate);
            holder.frameRate = convertView.findViewById(R.id.frame_rate);
            holder.packageLoss = convertView.findViewById(R.id.packet_loss);
            holder.jitter = convertView.findViewById(R.id.jitter);

            convertView.setTag(holder);
        } else {
            holder = (MeetingStatsAdapter.Holder) convertView.getTag();
        }

        MediaStatsInfo item = meetingStatsInfoList.get(position).getMediaStatsInfo();
        String mediaType = meetingStatsInfoList.get(position).getMediaType();
        if(item != null && mediaType != null){
            holder.name.setText(item.getParticipantName());
            String mediaTypeStr = formatMediaText(mediaType);
            if(isLocal(mediaType)){
                mediaTypeStr += "\u2191";
            }
            holder.media.setText(mediaTypeStr);
            holder.format.setText(item.getResolution());
            holder.actualRate.setText(String.valueOf(item.getRtpActualBitRate()));
            holder.frameRate.setText(String.valueOf(item.getFrameRate()));

            String packageLossStr = "";
            if("apr".equals(mediaType)){
                packageLossStr = item.getPackageLoss() + "(" + item.getPackageLossRate() + "%)/"
                        + item.getPackageLoss() + "(" + item.getLogicPacketLossRate() +"%)";
            }else{
                packageLossStr = item.getPackageLoss() + "(" + item.getPackageLossRate() + "%)";
            }
            holder.packageLoss.setText(packageLossStr);

            holder.jitter.setText(""+item.getJitter());
        }

        return convertView;
    }

    private String formatMediaText(String mediaType){
        String textStr = "";
        switch (mediaType) {
            case "apr":
            case "aps":
                textStr = AUDIO;
                break;
            case "vpr":
            case "vps":
                textStr = VIDEO;
                break;
            case "vcr":
            case "vcs":
                textStr = CONTENT;
                break;
            default:
        }
        return textStr;
    }

    private boolean isLocal(String mediaType){
        return "aps".equals(mediaType) || "vcs".equals(mediaType) || "vps".equals(mediaType);
    }

    class Holder {
        public TextView name;
        public TextView media;
        public TextView format;
        public TextView actualRate;
        public TextView frameRate;
        public TextView packageLoss;
        public TextView jitter;

    }


}
