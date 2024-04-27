package com.frtc.sqmeetingce.ui.component;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import frtc.sdk.internal.model.FrtcSDKMeetingType;
import frtc.sdk.ui.dialog.ConfirmDlg;
import frtc.sdk.ui.dialog.IConfirmDlgListener;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;

;

public class MeetingDetailsFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();

    public MainActivity mActivity;
    private LocalStore localStore;

    String meetingName = "";
    String meetingNumber = "";
    String meetingTime = "";
    String meetingPassword = "";
    String displayName="";
    String spentTime = "";
    String serverAddress = "";
    String meetingType = "";

    public MeetingDetailsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(isAdded()){
            meetingName = getArguments().getString("MeetingName");
            meetingNumber = getArguments().getString("MeetingNumber");
            meetingTime = getArguments().getString("Time");
            meetingPassword = getArguments().getString("Password");
            displayName = getArguments().getString("DisplayName");
            spentTime = getArguments().getString("SpentTime");
            serverAddress = getArguments().getString("ServerAddress");
            meetingType = getArguments().getString("MeetingType");
        }

        mActivity = (MainActivity) getActivity();

        localStore = LocalStoreBuilder.getInstance(mActivity).getLocalStore();

        View view = inflater.inflate(R.layout.meeting_details_fragment, container, false);

        TextView tvMeetingName = view.findViewById(R.id.meeting_name);
        TextView tvMeetingTime = view.findViewById(R.id.meeting_time);
        TextView tvMeetingId = view.findViewById(R.id.meeting_id);
        TextView tvMeetingSpendTime = view.findViewById(R.id.meeting_spend_time);
        TextView tvMeetingPassword = view.findViewById(R.id.meeting_password);
        TextView tvRecurrenceSign = view.findViewById(R.id.recurrence_sign);
        tvRecurrenceSign.setVisibility((!TextUtils.isEmpty(meetingType) && meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())) ? View.VISIBLE : View.GONE);

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();

        tvMeetingName.setText(meetingName);
        tvMeetingId.setText(meetingNumber);
        tvMeetingPassword.setText(meetingPassword);
        tvMeetingTime.setText(meetingTime);
        tvMeetingSpendTime.setText(spentTime);

        setClickListener(view);

        return view;
    }

    private void setClickListener(View view){

        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.showUserFragment(false);
            }
        });

        Button btnStartMeeting = view.findViewById(R.id.start_meeting_btn);
        btnStartMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActivity.isInMeeeting()){
                    return;
                }
                if (!mActivity.isNetworkConnected()) {
                    mActivity.showConnectionErrorNotice();
                    return;
                }
                mActivity.joinMeeting(meetingNumber,meetingPassword,displayName,serverAddress, meetingType);
                joinMeeting();
            }
        });

        Button btnDeleteMeeting = view.findViewById(R.id.delete_meeting_btn);
        btnDeleteMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteMeetingConfirmDlg();
            }
        });
    }

    private void showDeleteMeetingConfirmDlg(){
        ConfirmDlg confirmDlg = new ConfirmDlg(getContext(),
                getContext().getString(R.string.delete_history_meeting_dialog_title),
                getContext().getString(R.string.delete_history_meeting_dialog_content),
                getContext().getString(R.string.dialog_negative_btn),
                getContext().getString(R.string.dialog_positive_btn));

        confirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                deleteHistoryMeeting();
            }
            @Override
            public void onCancel(){

            }
        });
        confirmDlg.show();
    }

    private void deleteHistoryMeeting(){
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(mActivity).getLocalStore();
        }
        localStore.deleteHistoryMeeting(meetingNumber,meetingTime);
        LocalStoreBuilder.getInstance(mActivity).setLocalStore(localStore);

        mActivity.showUserFragment(false);
    }

    private void joinMeeting() {
        mActivity.StartMeetingActivity();
    }

    @Override
    public void onBack() {
        mActivity.showUserFragment(false);
    }
}
