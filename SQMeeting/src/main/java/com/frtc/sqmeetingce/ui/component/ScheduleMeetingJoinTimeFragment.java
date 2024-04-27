package com.frtc.sqmeetingce.ui.component;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;

;

public class ScheduleMeetingJoinTimeFragment extends BaseFragment {
    protected final String TAG = this.getClass().getSimpleName();
    private LocalStore localStore;
    public MainActivity mActivity;

    private ImageView imageThirtyMinutes, imageAnyTime;
    private ImageView selectedImage;
    private String joinTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.schedule_meeting_join_time_fragment, container, false);

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();

        init(view);

        return view;
    }

    private void init(View view) {

        imageThirtyMinutes = view.findViewById(R.id.thirty_minutes_selected);
        imageAnyTime = view.findViewById(R.id.any_time_selected);

        updateLocalStoreToView();
        setClickListener(view);
    }

    private void updateLocalStoreToView() {
        if (localStore != null) {
            joinTime = localStore.getScheduledMeetingSetting().getJoinTime();

            if("30".equals(joinTime)){
                imageThirtyMinutes.setVisibility(View.VISIBLE);
                selectedImage = imageThirtyMinutes;
            }else{
                imageAnyTime.setVisibility(View.VISIBLE);
                selectedImage = imageAnyTime;
            }
        }
    }

    private void updateLocalStore(){
        if(localStore != null){
            localStore.getScheduledMeetingSetting().setJoinTime(joinTime);
        }
    }

    private void setClickListener(View view) {
        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        TextView thirtyMinutes = view.findViewById(R.id.thirty_minutes);
        thirtyMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinTime = "30";
                if(selectedImage != imageThirtyMinutes){
                    selectedImage.setVisibility(View.INVISIBLE);
                    selectedImage = imageThirtyMinutes;
                    selectedImage.setVisibility(View.VISIBLE);
                }
                updateLocalStore();
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        TextView anytime = view.findViewById(R.id.any_time);
        anytime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinTime = "-1";
                if(selectedImage != imageAnyTime){
                    selectedImage.setVisibility(View.INVISIBLE);
                    selectedImage = imageAnyTime;
                    selectedImage.setVisibility(View.VISIBLE);
                }
                updateLocalStore();
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });


    }

    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(mActivity.previousTag);
    }
}
