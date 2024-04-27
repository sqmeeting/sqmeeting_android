package com.frtc.sqmeetingce.ui.component;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frtc.sqmeetingce.R;

import java.util.List;

public class InvitedUserAdapter extends RecyclerView.Adapter<InvitedUserAdapter.ViewHolder>{

    protected final String TAG = this.getClass().getSimpleName();

    private List<InvitedUserInfo> mUserList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClicked(InvitedUserInfo selectedUsers,int position);
    }

    public void setOnItemClickedListener(InvitedUserAdapter.OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView textName;
        ImageView statusView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textName = view.findViewById(R.id.name);
            statusView = view.findViewById(R.id.status_view);
        }
    }

    public InvitedUserAdapter(List<InvitedUserInfo> userList) {
        mUserList = userList;
    }

    @Override
    public InvitedUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invited_user_item, parent, false);
        final InvitedUserAdapter.ViewHolder holder = new InvitedUserAdapter.ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                InvitedUserInfo user = mUserList.get(position);
                if(mListener != null){
                    mListener.onItemClicked(user, position);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(InvitedUserAdapter.ViewHolder holder, int position) {
        InvitedUserInfo user = mUserList.get(position);
        String name = user.getRealName()+"("+ user.getUsername()+")";
        holder.textName.setText(name);

        if(user.isAdded()){
            holder.statusView.setImageResource(R.drawable.light_blue_checked);
        }else if(user.isSelected()){
            holder.statusView.setImageResource(R.drawable.blue_checked);
        }else{
            holder.statusView.setImageResource(R.drawable.icon_circle_white);
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

}
