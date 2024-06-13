package com.frtc.sqmeetingce.ui.component;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import java.util.ArrayList;

import frtc.sdk.internal.model.FrtcSDKMeetingType;
import frtc.sdk.log.Log;
import frtc.sdk.model.MeetingRoom;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;

public class CreateMeetingFragment extends BaseFragment implements MeetingRoomAdapter.OnItemClickListener{

    protected final String TAG = this.getClass().getSimpleName();

    private LocalStore localStore;
    public MainActivity mActivity;

    private EditText etMeetingRoomId;

    private Switch stVideo;
    private Switch stMeetingRoomUsed;

    private boolean videoOn = false;
    private boolean meetingRoomUsed = false;

    private ConstraintLayout meetingRoomIdLayout;

    private ConstraintLayout meetingRoomsLayout;
    private ArrayList<MeetingRoom> meetingRooms;

    private MeetingRoomAdapter meetingRoomAdapter;
    private long lastClickTime = 0;

    public CreateMeetingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
        queryMeetingRooms();

        View view = inflater.inflate(R.layout.create_meeting_fragment, container, false);

        init(view);
        setClickListener(view);
        setEditChangedListener(view);

        Log.d(TAG, "onCreateView");
        return view;
    }


    private void init(View view) {
        Log.i(TAG, "init()");
        meetingRoomIdLayout = view.findViewById(R.id.meeting_room_layout);

        meetingRoomIdLayout.setVisibility(View.GONE);

        meetingRoomsLayout = view.findViewById(R.id.meeting_rooms_layout);

        etMeetingRoomId = view.findViewById(R.id.edit_meeting_room_id);

        stVideo = view.findViewById(R.id.switch_video);
        stVideo.setChecked(false);
        stMeetingRoomUsed = view.findViewById(R.id.switch_use_meeting_Room);

        meetingRooms = localStore.getMeetingRooms();

        RecyclerView meetingRoomRecyclerView = view.findViewById(R.id.meeting_room_listView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        meetingRoomRecyclerView.setLayoutManager(layoutManager);
        meetingRoomAdapter = new MeetingRoomAdapter(meetingRooms);
        meetingRoomRecyclerView.setAdapter(meetingRoomAdapter);
        meetingRoomAdapter.setOnItemClickedListener(this);

    }

    public void onItemClicked(MeetingRoom selectedMeetingRoom){
        String meetingNumber = selectedMeetingRoom.getMeetingNumber();
        etMeetingRoomId.setText(meetingNumber);
        hideMeetingRooms();
    }

    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER);
    }

    private void setClickListener(View view) {

        Button btnBack = view.findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave();
            }
        });

        Button btnStartMeeting = view.findViewById(R.id.start_meeting_btn);
        btnStartMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActivity.isInMeeting()){
                    return;
                }

                if (SystemClock.elapsedRealtime() - lastClickTime < 500){
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                createMeeting();
            }
        });

        ImageButton btnShowMeetingRooms = view.findViewById(R.id.btn_show_meeting_rooms);
        btnShowMeetingRooms.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                showMeetingRooms();
                if(meetingRooms!=null && !meetingRooms.isEmpty()){
                    etMeetingRoomId.setText(meetingRooms.get(0).getMeetingNumber());
                }
            }
        });

        Button btnOk = view.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMeetingRooms();
            }
        });

        stVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (!compoundButton.isPressed()) {
                    compoundButton.setChecked(!checked);
                    videoOn = checked;
                }
            }
        });

        stMeetingRoomUsed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(!meetingRooms.isEmpty()){
                    if (!compoundButton.isPressed()) {
                        compoundButton.setChecked(!checked);
                    }
                    meetingRoomUsed = checked;
                    if(checked){
                        meetingRoomIdLayout.setVisibility(View.VISIBLE);
                        if(meetingRooms!=null && !meetingRooms.isEmpty()){
                            etMeetingRoomId.setText(meetingRooms.get(0).getMeetingNumber());
                        }
                    }else{
                        meetingRoomIdLayout.setVisibility(View.GONE);
                        etMeetingRoomId.setText("");
                    }
                }else{
                    compoundButton.setChecked(false);
                    BaseToast.showToast(mActivity, getString(R.string.no_meeting_room_notice), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void queryMeetingRooms(){
        mActivity.queryMeetingRooms();
    }

    private void showMeetingRooms(){
        meetingRoomsLayout.setVisibility(View.VISIBLE);
    }

    private void hideMeetingRooms(){
        meetingRoomsLayout.setVisibility(View.GONE);
    }


    private void setEditChangedListener(View view) {

        etMeetingRoomId.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                } else {

                }
            }
        });

        etMeetingRoomId.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void createMeeting(){

        if (localStore == null) {
            localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
        }

        String meetingName = localStore.getDisplayName() + mActivity.getResources().getString(R.string.instant_meeting_name_postfix);

        videoOn = stVideo.isChecked();
        localStore.setCameraOn(videoOn);
        localStore.setAudioCall(false);
        LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).setLocalStore(localStore);

        meetingRoomUsed = stMeetingRoomUsed.isChecked();
        if(!meetingRoomUsed){

            mActivity.createInstantMeeting(meetingName);
        }else{

            if (!mActivity.isNetworkConnected()) {
                mActivity.showConnectionErrorNotice();
                return;
            }

            String meetingNumber = etMeetingRoomId.getText().toString();

            if(meetingRooms!=null && !meetingRooms.isEmpty()){
                for (MeetingRoom meetingRoom:meetingRooms) {
                    if(meetingRoom.getMeetingNumber().equals(meetingNumber)){
                        String password = meetingRoom.getPassword();
                        mActivity.joinMeeting(meetingNumber,password,meetingName,!videoOn);
                        launchMeeting();
                        break;
                    }
                }
            }
        }
    }

    private void launchMeeting() {
        mActivity.StartMeetingActivity();
    }
    
    private void onClickSave() {
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER);
    }
}
