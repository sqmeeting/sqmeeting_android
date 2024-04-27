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
import frtc.sdk.ui.model.ParticipantInfo;


public class ParticipantListAdapter extends ArrayAdapter<ParticipantInfo> {

    private static String TAG = ParticipantListAdapter.class.getSimpleName();
    private List<ParticipantInfo> participantListData = new ArrayList<>();
    private final Context context;
    private OnItemClickListener mListener;
    private final boolean isHost;
    private final LayoutInflater layoutInflater;

    public interface OnItemClickListener{
        void onItemClicked(ParticipantInfo selectedParticipant, int position);
    }
    private class ViewHolder {
        public ImageView pinned;
        public ImageView audioLevel;
        public ImageView videoMuted;
        public TextView participantName;
        public TextView role;
    }

    public ParticipantListAdapter(Context context, int resource, List<ParticipantInfo> list, boolean isHost) {
        super(context, resource, list);
        layoutInflater = LayoutInflater.from(context);
        participantListData = list;
        this.isHost = isHost;
        this.context = context;
    }


    public void setData(List<ParticipantInfo> data) {
        this.participantListData = data;
        this.notifyDataSetChanged();
    }

    @Override
    public ParticipantInfo getItem(int position) {
        return participantListData.get(position);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.participants_listview_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.participantName = view.findViewById(R.id.participant_name);
            viewHolder.role = view.findViewById(R.id.role);
            viewHolder.pinned = view.findViewById(R.id.pin_status);
            viewHolder.audioLevel = view.findViewById(R.id.audio_status);
            viewHolder.videoMuted = view.findViewById(R.id.video_status);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.participantName.setText(participantListData.get(position).getDisplayName());

        if (participantListData.get(position).isMe()) {

            String strMe = "(" + context.getString(R.string.participants_me);
            if(isHost){
                strMe = strMe + "、" + context.getString(R.string.participants_host);
            }

            if(participantListData.get(position).isLecturer()){
                strMe = strMe + "、" + context.getString(R.string.participants_lecturer);
            }

            strMe = strMe + ")";
            viewHolder.role.setText(strMe);
        } else if(participantListData.get(position).isLecturer()){
            String str = "("+ context.getString(R.string.participants_lecturer) +")";
            viewHolder.role.setText(str);
        } else {
            viewHolder.role.setText("");
        }

        if (participantListData.get(position).getMuteAudio()) {
            viewHolder.audioLevel.setImageResource(R.drawable.call_icon_participant_audio_off);
        } else {
            if(position == 0){
                int volume = participantListData.get(0).getAudioVolume();
                switch (volume){
                    case 0:
                        viewHolder.audioLevel.setImageResource(R.drawable.call_icon_participant_audio_on0);
                        break;
                    case 1:
                        viewHolder.audioLevel.setImageResource(R.drawable.call_icon_participant_audio_on1);
                        break;
                    case 2:
                        viewHolder.audioLevel.setImageResource(R.drawable.call_icon_participant_audio_on2);
                        break;
                    case 3:
                        viewHolder.audioLevel.setImageResource(R.drawable.call_icon_participant_audio_on3);
                        break;
                    case 4:
                        viewHolder.audioLevel.setImageResource(R.drawable.call_icon_participant_audio_on4);
                        break;
                    default:
                        viewHolder.audioLevel.setImageResource(R.drawable.call_icon_participant_audio_on4);
                }
            }else{
                viewHolder.audioLevel.setImageResource(R.drawable.call_icon_participant_audio_on);
            }
        }

        if (participantListData.get(position).getMuteVideo()) {
            viewHolder.videoMuted.setImageResource(R.drawable.call_icon_participant_video_off);
        } else {
            viewHolder.videoMuted.setImageResource(R.drawable.call_icon_participant_video_on);
        }

        if(participantListData.get(position).isPinned()){
            viewHolder.pinned.setVisibility(View.VISIBLE);
        }else{
            viewHolder.pinned.setVisibility(View.GONE);
        }

        view.setOnClickListener(v -> {
            ParticipantInfo participantInfo = getItem(position);
            if(mListener != null){
                mListener.onItemClicked(participantInfo, position);
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return participantListData.size();
    }

    public void setOnItemClickedListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

}
