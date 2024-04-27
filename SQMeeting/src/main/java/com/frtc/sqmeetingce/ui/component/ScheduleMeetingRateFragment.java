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

import java.util.Set;

import frtc.sdk.ui.model.MeetingCallRate;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.ui.model.RoleConstant;

;

public class ScheduleMeetingRateFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();
    private LocalStore localStore;
    public MainActivity mActivity;

    private ImageView image128k;
    private ImageView image512k;
    private ImageView image1m;
    private ImageView image2m;
    private ImageView image2560k;
    private ImageView image3m;
    private ImageView image4m;
    private ImageView image6m;
    private ImageView image8m;

    private TextView rate6m;
    private TextView rate8m;

    private ImageView selectedImage;
    private boolean isNormalUser = true;

    private String rate = MeetingCallRate.RATE_4096K.getRateValue();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.schedule_meeting_rate_fragment, container, false);

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();

        init(view);

        return view;
    }

    private void init(View view) {

        image128k = view.findViewById(R.id.rate_128k_selected);
        image512k = view.findViewById(R.id.rate_512k_selected);
        image1m = view.findViewById(R.id.rate_1m_selected);
        image2m = view.findViewById(R.id.rate_2m_selected);
        image2560k = view.findViewById(R.id.rate_2560k_selected);
        image3m = view.findViewById(R.id.rate_3m_selected);
        image4m = view.findViewById(R.id.rate_4m_selected);
        image6m = view.findViewById(R.id.rate_6m_selected);
        image8m = view.findViewById(R.id.rate_8m_selected);

        rate6m = view.findViewById(R.id.rate_6m);
        rate8m = view.findViewById(R.id.rate_8m);

        updateLocalStoreToView();
        setClickListener(view);

    }

    private boolean isCommonUser(Set<String> role){
        if(role!=null && !role.isEmpty()){
            for(String roleItem:role){
                if(RoleConstant.MEETING_OPERATOR.equals(roleItem) || RoleConstant.SYSTEM_ADMIN.equals(roleItem)){
                    return false;
                }
            }
        }
        return true;
    }

    private void updateLocalStoreToView(){

        if (localStore != null) {
            rate = localStore.getScheduledMeetingSetting().getRate();

            isNormalUser = isCommonUser(localStore.getRole());
            rate6m.setVisibility(isNormalUser ? View.GONE : View.VISIBLE);
            rate8m.setVisibility(isNormalUser ? View.GONE : View.VISIBLE);
            

            if(MeetingCallRate.RATE_128K.getRateValue().equals(rate)){
                image128k.setVisibility(View.VISIBLE);
                selectedImage = image128k;
            }else if(MeetingCallRate.RATE_512K.getRateValue().equals(rate)){
                image512k.setVisibility(View.VISIBLE);
                selectedImage = image512k;
            }else if(MeetingCallRate.RATE_1024K.getRateValue().equals(rate)){
                image1m.setVisibility(View.VISIBLE);
                selectedImage = image1m;
            }else if(MeetingCallRate.RATE_2048K.getRateValue().equals(rate)){
                image2m.setVisibility(View.VISIBLE);
                selectedImage = image2m;
            }else if(MeetingCallRate.RATE_2560K.getRateValue().equals(rate)){
                image2560k.setVisibility(View.VISIBLE);
                selectedImage = image2560k;
            }else if(MeetingCallRate.RATE_3072K.getRateValue().equals(rate)){
                image3m.setVisibility(View.VISIBLE);
                selectedImage = image3m;
            }else if(MeetingCallRate.RATE_4096K.getRateValue().equals(rate)){
                image4m.setVisibility(View.VISIBLE);
                selectedImage = image4m;
            }else if(MeetingCallRate.RATE_6144K.getRateValue().equals(rate)){
                image6m.setVisibility(View.VISIBLE);
                selectedImage = image6m;
            }else if(MeetingCallRate.RATE_8192K.getRateValue().equals(rate)){
                image8m.setVisibility(View.VISIBLE);
                selectedImage = image8m;
            }
        }

    }

    private void updateLocalStore(){
        if(localStore != null){
            localStore.getScheduledMeetingSetting().setRate(rate);
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

        TextView rate128k = view.findViewById(R.id.rate_128k);
        rate128k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = MeetingCallRate.RATE_128K.getRateValue();
                if(selectedImage != image128k){
                    selectedImage.setVisibility(View.INVISIBLE);
                    selectedImage = image128k;
                    selectedImage.setVisibility(View.VISIBLE);
                }
                updateLocalStore();
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        TextView rate512k = view.findViewById(R.id.rate_512k);
        rate512k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = MeetingCallRate.RATE_512K.getRateValue();
                if(selectedImage != image512k){
                    selectedImage.setVisibility(View.INVISIBLE);
                    selectedImage = image512k;
                    selectedImage.setVisibility(View.VISIBLE);
                }
                updateLocalStore();
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        TextView rate1m = view.findViewById(R.id.rate_1m);
        rate1m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = MeetingCallRate.RATE_1024K.getRateValue();
                if(selectedImage != image1m){
                    selectedImage.setVisibility(View.INVISIBLE);
                    selectedImage = image1m;
                    selectedImage.setVisibility(View.VISIBLE);
                }
                updateLocalStore();
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        TextView rate2m = view.findViewById(R.id.rate_2m);
        rate2m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = MeetingCallRate.RATE_2048K.getRateValue();
                if(selectedImage != image2m){
                    selectedImage.setVisibility(View.INVISIBLE);
                    selectedImage = image2m;
                    selectedImage.setVisibility(View.VISIBLE);
                }
                updateLocalStore();
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        TextView rate2560k = view.findViewById(R.id.rate_2560k);
        rate2560k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = MeetingCallRate.RATE_2560K.getRateValue();
                if(selectedImage != image2560k){
                    selectedImage.setVisibility(View.INVISIBLE);
                    selectedImage = image2560k;
                    selectedImage.setVisibility(View.VISIBLE);
                }
                updateLocalStore();
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        TextView rate3m = view.findViewById(R.id.rate_3m);
        rate3m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = MeetingCallRate.RATE_3072K.getRateValue();
                if(selectedImage != image3m){
                    selectedImage.setVisibility(View.INVISIBLE);
                    selectedImage = image3m;
                    selectedImage.setVisibility(View.VISIBLE);
                }
                updateLocalStore();
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        TextView rate4m = view.findViewById(R.id.rate_4m);
        rate4m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = MeetingCallRate.RATE_4096K.getRateValue();
                if(selectedImage != image4m){
                    selectedImage.setVisibility(View.INVISIBLE);
                    selectedImage = image4m;
                    selectedImage.setVisibility(View.VISIBLE);
                }
                updateLocalStore();
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        rate6m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = MeetingCallRate.RATE_6144K.getRateValue();
                if(selectedImage != image6m){
                    selectedImage.setVisibility(View.INVISIBLE);
                    selectedImage = image6m;
                    selectedImage.setVisibility(View.VISIBLE);
                }
                updateLocalStore();
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        rate8m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = MeetingCallRate.RATE_8192K.getRateValue();
                if(selectedImage != image8m){
                    selectedImage.setVisibility(View.INVISIBLE);
                    selectedImage = image8m;
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
