package frtc.sdk.ui.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import frtc.sdk.R;
import frtc.sdk.model.UnmuteRequest;
import frtc.sdk.ui.model.ParticipantInfo;

public class UnmuteRequestParticipantListAdapter extends ArrayAdapter<UnmuteRequest> {

    private static String TAG = UnmuteRequestParticipantListAdapter.class.getSimpleName();
    private List<UnmuteRequest> unmuteRequestListData = new ArrayList<>();
    private LayoutInflater inflater;
    private Context mContext;
    private ClickViewListener clickViewListener;

    public UnmuteRequestParticipantListAdapter(Context context, int resource, List<UnmuteRequest> objects) {
        super(context, resource, objects);
        mContext = context;
        unmuteRequestListData = objects;

        inflater = LayoutInflater.from(context);

    }

    public void setData(List<UnmuteRequest> data) {
        this.unmuteRequestListData = data;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClicked(ParticipantInfo selectedParticipant, int position);
    }

    public void setOnClickViewListener(ClickViewListener clickViewListener) {
        this.clickViewListener = clickViewListener;
    }

    @Override
    public int getCount() {
        return unmuteRequestListData.size();
    }

    @Override
    public UnmuteRequest getItem(int position) {
        return unmuteRequestListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.unmute_request_item, parent, false);
            holder = new Holder();
            holder.name = convertView.findViewById(R.id.name);
            holder.permission = convertView.findViewById(R.id.permission);
            holder.agreeBtn = convertView.findViewById(R.id.agree_btn);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.name.setText(unmuteRequestListData.get(position).getDisplay_name());
        holder.agreeBtn.setTag(position);

        holder.agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickViewListener != null && position < unmuteRequestListData.size()){
                    clickViewListener.onClick(position, unmuteRequestListData.get(position));
                }
            }
        });

        return convertView;
    }

    private class Holder {
        public TextView name;
        public TextView permission;
        public Button agreeBtn;
    }

    public interface ClickViewListener {
        public void onClick(int position, UnmuteRequest unmteReq);
    }

}

