package com.frtc.sqmeetingce.ui.component;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import java.util.ArrayList;

import frtc.sdk.log.Log;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.dialog.ConfirmDlg;
import frtc.sdk.ui.dialog.IConfirmDlgListener;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.ui.model.MeetingCall;

public class UserFragment extends BaseFragment implements HistoryMeetingAdapter.OnItemClickListener, ScheduledMeetingAdapter.OnItemClickListener{

    protected final String TAG = this.getClass().getSimpleName();

    public MainActivity mActivity;
    private LocalStore localStore;

    private ConstraintLayout noMeetingLayout;
    private TextView noMeetingText;
    private Button btnClearRecords;
    private ImageButton btnRefresh;

    private Button tabHistoryMeeting;
    private Button tabScheduledMeeting;
    private ImageView historyMeetingSelected;
    private ImageView scheduledMeetingSelected;

    private ArrayList<MeetingCall> historyMeetings;
    private ArrayList<ScheduledMeeting> scheduledMeetings;

    private RecyclerView meetingRecyclerView;
    private HistoryMeetingAdapter historyMeetingAdapter;
    private ScheduledMeetingAdapter scheduledMeetingAdapter;

    public static final String KEY_TAB_SCHEDULE = "ScheduleMeeting";

    private boolean onRefresh = false;

    public static UserFragment newInstance(Boolean param) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_TAB_SCHEDULE, param);
        fragment.setArguments(args);
        return fragment;
    }

    public UserFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        boolean isScheduleMeetingVisible = true;
        Bundle bundle = getArguments();
        if (bundle != null) {
            isScheduleMeetingVisible = getArguments().getBoolean(KEY_TAB_SCHEDULE);
        }

        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.user_fragment, container, false);

        noMeetingLayout = view.findViewById(R.id.no_meeting_layout);
        noMeetingText = view.findViewById(R.id.no_meeting_text);

        tabHistoryMeeting = view.findViewById(R.id.tab_history_meeting);
        tabScheduledMeeting = view.findViewById(R.id.tab_schedule_meeting);
        historyMeetingSelected = view.findViewById(R.id.history_meeting_selected);
        scheduledMeetingSelected = view.findViewById(R.id.schedule_meeting_selected);
        btnClearRecords = view.findViewById(R.id.btn_clear_history_meeting);
        btnRefresh = view.findViewById(R.id.btn_refresh);

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();

        scheduledMeetings = localStore.getScheduledMeetings();
        historyMeetings = localStore.getHistoryMeetings();

        meetingRecyclerView = view.findViewById(R.id.meeting_listView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        meetingRecyclerView.setLayoutManager(layoutManager);

        scheduledMeetingAdapter = new ScheduledMeetingAdapter(mActivity, scheduledMeetings);
        scheduledMeetingAdapter.setUserId(localStore.getUserId());
        scheduledMeetingAdapter.setOnItemClickedListener(this);

        historyMeetingAdapter = new HistoryMeetingAdapter(historyMeetings);
        historyMeetingAdapter.setOnItemClickedListener(this);
        historyMeetingAdapter.notifyDataSetChanged();

        meetingRecyclerView.setAdapter(scheduledMeetingAdapter);
        scheduledMeetingAdapter.notifyDataSetChanged();
        updateNoScheduledMeetingVisible();
        showScheduleMeetingView(isScheduleMeetingVisible);

        setClickListener(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        historyMeetings = localStore.getHistoryMeetings();
        historyMeetingAdapter.setHistoryMeetingList(historyMeetings);
        if(historyMeetingSelected.getVisibility() == View.VISIBLE) {
            updateNoHistoryMeetingVisible();
        }
    }

    private void setClickListener(View view) {

        ImageView btnScan = view.findViewById(R.id.menu_scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckPermissionAndScan();
            }
        });

        ImageView btnSettings = view.findViewById(R.id.menu_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_USER;
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
            }
        });

        ImageButton btnNewMeeting = view.findViewById(R.id.ImageButton_new_meeting);
        btnNewMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivity.previousTag = FragmentTagEnum.FRAGMENT_USER;
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_CREATE_MEETING);
            }
        });

        ImageButton btnJoinMeeting = view.findViewById(R.id.ImageButton_join_meeting);
        btnJoinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mActivity.previousTag = FragmentTagEnum.FRAGMENT_USER;
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_JOIN_MEETING);
            }
        });

        ImageView btnScheduleMeeting = view.findViewById(R.id.ImageButton_schedule_meeting);
        btnScheduleMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.showScheduleMeetingFragment(false);
            }
        });

        tabScheduledMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScheduleMeetingView(true);
            }
        });

        tabHistoryMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScheduleMeetingView(false);
            }
        });


        btnClearRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearHistoryMeetingConfirmDlg();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh = true;
                mActivity.queryScheduledMeetingList(1, 500);
            }
        });
    }

    public void updateScheduledMeetingListview(){
        scheduledMeetingAdapter.notifyDataSetChanged();
        if(tabScheduledMeeting.isSelected()){
            updateNoScheduledMeetingVisible();
        }
    }

    public void onRefreshScheduledMeetingList(){
        if(onRefresh){
            BaseToast.showToast(mActivity,getString( R.string.text_scheduled_meeting_refresh_success), Toast.LENGTH_SHORT);
            onRefresh = false;
        }
    }

    private void showClearHistoryMeetingConfirmDlg(){
        ConfirmDlg confirmDlg = new ConfirmDlg(getContext(),
                getContext().getString(R.string.clear_history_meeting_dialog_title),
                getContext().getString(R.string.clear_history_meeting_dialog_content),
                getContext().getString(R.string.dialog_negative_btn),
                getContext().getString(R.string.dialog_positive_btn));

        confirmDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                cleanHistoryMeetings();
            }
            @Override
            public void onCancel(){

            }
        });
        confirmDlg.show();
    }

    private void cleanHistoryMeetings(){
        cleanHistoryMeetingsRecords();
        historyMeetings.clear();
        updateNoHistoryMeetingVisible();
        historyMeetingAdapter.notifyDataSetChanged();
    }

    private void cleanHistoryMeetingsRecords() {
        localStore.clearHistoryMeetings();
        LocalStoreBuilder.getInstance(mActivity).setLocalStore(localStore);
    }

    public void onItemClicked(MeetingCall selectedMeetingCall){
        showMeetingDetails(selectedMeetingCall);
    }

    public void onItemClicked(ScheduledMeeting selectedMeetingCall){

        showScheduledMeetingDetails(selectedMeetingCall);
    }

    public void showScheduledMeetingDetails(ScheduledMeeting selectedMeetingCall){
        mActivity.showMeetingDetails(selectedMeetingCall);
    }

    public void showMeetingDetails(MeetingCall selectedMeetingCall){
        mActivity.showMeetingDetails(selectedMeetingCall);
    }

    private void showScheduleMeetingView(boolean isScheduleMeetingVisible){

        tabScheduledMeeting.setSelected(isScheduleMeetingVisible);
        tabHistoryMeeting.setSelected(!isScheduleMeetingVisible);

        scheduledMeetingSelected.setVisibility(isScheduleMeetingVisible ? View.VISIBLE :View.INVISIBLE);
        historyMeetingSelected.setVisibility(isScheduleMeetingVisible ? View.INVISIBLE :View.VISIBLE);

        if(isScheduleMeetingVisible){
            meetingRecyclerView.setAdapter(scheduledMeetingAdapter);
            scheduledMeetingAdapter.notifyDataSetChanged();
            btnRefresh.setVisibility(View.VISIBLE);
            btnClearRecords.setVisibility(View.INVISIBLE);
            updateNoScheduledMeetingVisible();
        }else {
            meetingRecyclerView.setAdapter(historyMeetingAdapter);
            historyMeetingAdapter.notifyDataSetChanged();
            btnRefresh.setVisibility(View.INVISIBLE);
            updateNoHistoryMeetingVisible();
        }
    }

    private void updateNoHistoryMeetingVisible(){
        historyMeetings = localStore.getHistoryMeetings();
        if(historyMeetings.isEmpty()){
            noMeetingLayout.setVisibility(View.VISIBLE);
            noMeetingText.setText(R.string.text_no_history_meeting);
            btnClearRecords.setVisibility(View.INVISIBLE);
        }else{
            noMeetingLayout.setVisibility(View.GONE);
            btnClearRecords.setVisibility(View.VISIBLE);
        }
    }

    private void updateNoScheduledMeetingVisible(){
        if(scheduledMeetings.isEmpty()){
            noMeetingLayout.setVisibility(View.VISIBLE);
            noMeetingText.setText(R.string.text_no_scheduled_meeting);
        }else{
            noMeetingLayout.setVisibility(View.GONE);
        }
    }

    private void onCheckPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, MainActivity.CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            mActivity.startCaptureActivity();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()" );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MainActivity.CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mActivity.startCaptureActivity();
            } else {
                mActivity.showOpenCameraInformDlg();
            }
        }
    }

}
