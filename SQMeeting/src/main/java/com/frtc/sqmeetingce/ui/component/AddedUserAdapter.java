package com.frtc.sqmeetingce.ui.component;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frtc.sqmeetingce.R;

import java.util.List;

public class AddedUserAdapter extends RecyclerView.Adapter<AddedUserAdapter.ViewHolder>{

    protected final String TAG = this.getClass().getSimpleName();

    private List<InvitedUserInfo> invitedUserInfos;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClicked(InvitedUserInfo selectedUsers, int position);
    }

    public void setOnItemClickedListener(AddedUserAdapter.OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View holderView;
        TextView textName;

        public ViewHolder(View view) {
            super(view);
            holderView = view;
            textName = view.findViewById(R.id.name);

        }
    }

    public AddedUserAdapter(List<InvitedUserInfo> invitedUserInfos) {
        this.invitedUserInfos = invitedUserInfos;
    }


    @Override
    public AddedUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.added_user_item, parent, false);
        final AddedUserAdapter.ViewHolder holder = new AddedUserAdapter.ViewHolder(view);
        holder.holderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                InvitedUserInfo user = invitedUserInfos.get(position);
                if(mListener != null){
                    mListener.onItemClicked(user,position);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(AddedUserAdapter.ViewHolder holder, int position) {
        InvitedUserInfo info = invitedUserInfos.get(position);
        String name = info.getRealName()+"("+ info.getUsername()+")";
        holder.textName.setText(name);
    }

    @Override
    public int getItemCount() {
        return invitedUserInfos.size();
    }

}
