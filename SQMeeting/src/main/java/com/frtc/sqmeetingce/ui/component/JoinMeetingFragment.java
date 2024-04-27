package com.frtc.sqmeetingce.ui.component;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import java.util.ArrayList;
import java.util.Iterator;

import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.dialog.IConfirmDlgListener;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.ui.model.MeetingCall;
import frtc.sdk.ui.dialog.ConfirmDlg;

public class JoinMeetingFragment extends BaseFragment implements MeetingNumberAdapter.OnItemClickListener{

    protected final String TAG = this.getClass().getSimpleName();

    private LocalStore localStore;
    public MainActivity mActivity;

    private ImageView noticeId;
    private ImageView noticeName;

    private TextView meetingIdErrorNotice;
    private TextView nameErrorNotice;

    private EditText inputMeetingId;
    private EditText inputDisplayName;

    private Button btnClearMeetingId;
    private Button btnShowMeetingRooms;
    private Button btnClearDisplayName;

    private Switch switchAudio;
    private Switch switchCamera;
    private Switch switchOnlyVoice;

    private String meetingId = "";
    private String displayName = "";

    private boolean audioOn = true;
    private boolean videoOn = true;
    private boolean voiceOnly = false;

    private ConstraintLayout meetingNumberLayout;
    private RecyclerView meetingRecyclerView;
    private ArrayList<MeetingCall> meetings = new ArrayList<>();

    private MeetingNumberAdapter meetingNumberAdapter;

    private String serverAddress;

    public JoinMeetingFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.join_meeting_fragment, container, false);
        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
        init(view);
        return view;
    }


    private void init(View view) {
        inputMeetingId = view.findViewById(R.id.meeting_id);
        meetingIdErrorNotice = view.findViewById(R.id.meeting_id_error_msg);
        noticeId = view.findViewById(R.id.number_notice);
        btnClearMeetingId = view.findViewById(R.id.meeting_id_clear_btn);
        btnShowMeetingRooms = view.findViewById(R.id.btn_show_meeting_rooms);

        meetingNumberLayout = view.findViewById(R.id.meeting_number_layout);

        inputDisplayName = view.findViewById(R.id.display_name);
        nameErrorNotice = view.findViewById(R.id.title_your_name_error_msg);
        noticeName = view.findViewById(R.id.name_notice);
        btnClearDisplayName = view.findViewById(R.id.display_name_clear_btn);

        switchAudio = view.findViewById(R.id.switch_audio);
        switchCamera = view.findViewById(R.id.switch_camera);
        switchOnlyVoice = view.findViewById(R.id.switch_only_voice);

        switchAudio.setChecked(false);
        switchCamera.setChecked(false);
        switchOnlyVoice.setChecked(false);

        meetingRecyclerView = view.findViewById(R.id.meeting_listView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        meetingRecyclerView.setLayoutManager(layoutManager);
        meetingNumberAdapter = new MeetingNumberAdapter(meetings);
        meetingRecyclerView.setAdapter(meetingNumberAdapter);
        meetingNumberAdapter.setOnItemClickedListener(this);

        getMeetingSettings();
        setViewListener(view);
    }


    @Override
    public void onResume() {
        super.onResume();
        setMeetingNumberList();
        if(!meetings.isEmpty()){
            btnShowMeetingRooms.setVisibility(View.VISIBLE);
        }

    }

    private void setMeetingNumberList(){

        ArrayList<MeetingCall> historyMeetings = localStore.getHistoryMeetings();
        Iterator iterator =  historyMeetings.iterator();

        meetings.clear();
        while (iterator.hasNext()) {
            MeetingCall meeting = (MeetingCall) iterator.next();;
            if (!meetings.contains(meeting)) {
                meetings.add(meeting);
            }
        }

    }

    private void saveMeetingSettings() {
        meetingId = inputMeetingId.getText().toString().trim();
        displayName = inputDisplayName.getText().toString().trim();
        audioOn = switchAudio.isChecked();
        videoOn = switchCamera.isChecked();
        voiceOnly = switchOnlyVoice.isChecked();

        if (localStore != null) {
            localStore.setMeetingID(meetingId);
            localStore.setAudioOn(audioOn);
            localStore.setCameraOn(videoOn);
            localStore.setAudioCall(voiceOnly);

            LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).setLocalStore(localStore);
        }

    }


    @Override
    public void onBack() {
        saveMeetingSettings();
        mActivity.replaceFragmentWithTag(mActivity.previousTag);
    }

    private final CompoundButton.OnCheckedChangeListener switchCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if (!compoundButton.isPressed()) {
                compoundButton.setChecked(!checked);
            }
        }
    };

    private void setViewListener(View view) {
        setClickListener(view);
        setEditChangedListener(view);
    }

    private void setClickListener(View view) {

        Button btnBack = view.findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack();
            }
        });

        Button btnCall = view.findViewById(R.id.call_btn);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActivity.isInMeeeting()){
                    return;
                }
                joinMeetingWithParam();
            }
        });

        switchAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    compoundButton.setChecked(isChecked);
                    audioOn = isChecked;

            }
        });

        switchCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                compoundButton.setChecked(isChecked);
                videoOn = isChecked;

                if(isChecked){
                    switchOnlyVoice.setChecked(false);
                    voiceOnly = false;
                }
            }
        });

        switchOnlyVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                compoundButton.setChecked(isChecked);
                voiceOnly = isChecked;

                if(isChecked){
                    switchCamera.setChecked(false);
                    videoOn = false;
                    BaseToast.showToast(mActivity, getString(R.string.join_meeting_audio_only_notice), Toast.LENGTH_SHORT);
                }

            }
        });

        btnClearMeetingId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMeetingId.setText("");
            }
        });

        btnClearDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDisplayName.setText("");
            }
        });


        btnShowMeetingRooms.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                showMeetingList();
                if(meetings!=null && !meetings.isEmpty()){
                    inputMeetingId.setText(meetings.get(0).getMeetingNumber());
                }
            }
        });

        Button btnClearRecords = view.findViewById(R.id.btn_clear);
        btnClearRecords.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                showClearHistoryMeetingConfirmDlg();
                hideMeetingList();
            }
        });

        Button btnOk = view.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMeetingList();
            }
        });
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
                cleanHistoryMeetingsRecords();
            }
            @Override
            public void onCancel(){

            }
        });
        confirmDlg.show();
    }

    public void onItemClicked(MeetingCall selectedMeeting){
        String meetingNumber = selectedMeeting.getMeetingNumber();
        inputMeetingId.setText(meetingNumber);
        serverAddress = selectedMeeting.getServerAddress();
        hideMeetingList();
    }

    private void showMeetingList(){

        meetingNumberLayout.setVisibility(View.VISIBLE);

    }

    private void hideMeetingList(){

        meetingNumberLayout.setVisibility(View.GONE);
    }

    private void cleanHistoryMeetingsRecords() {
        meetings.clear();
        meetingNumberAdapter.notifyDataSetChanged();
        localStore.clearHistoryMeetings();
        LocalStoreBuilder.getInstance(mActivity).setLocalStore(localStore);
    }

    private void setEditChangedListener(View view) {

        inputMeetingId.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    meetingIdErrorNotice.setText("");
                    noticeId.setVisibility(View.GONE);
                }
                String input = inputMeetingId.getText().toString();
                btnClearMeetingId.setVisibility(hasFocus && input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        inputMeetingId.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                meetingIdErrorNotice.setText("");
                if(noticeId.getVisibility() ==View.VISIBLE){
                    noticeId.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = inputMeetingId.getText().toString();
                btnClearMeetingId.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        inputDisplayName.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    nameErrorNotice.setText("");
                    noticeName.setVisibility(View.GONE);
                }
                String input = inputDisplayName.getText().toString();
                btnClearDisplayName.setVisibility(hasFocus && input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        inputDisplayName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameErrorNotice.setText("");
                if(noticeName.getVisibility() ==View.VISIBLE){
                    noticeName.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = inputDisplayName.getText().toString();
                btnClearDisplayName.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void joinMeetingWithParam() {

        saveMeetingSettings();

        meetingId = inputMeetingId.getText().toString().trim();
        displayName = inputDisplayName.getText().toString().trim();

        if (meetingId.isEmpty() || displayName.isEmpty()) {
            if (meetingId.isEmpty()) {
                meetingIdErrorNotice.setVisibility(View.VISIBLE);
                noticeId.setVisibility(View.VISIBLE);
                meetingIdErrorNotice.setText(R.string.meeting_id_error);
            }

            if (displayName.isEmpty()) {
                nameErrorNotice.setVisibility(View.VISIBLE);
                noticeName.setVisibility(View.VISIBLE);
                nameErrorNotice.setText(R.string.username_error);
            }
            return;
        }

        if (!mActivity.isNetworkConnected()) {
            mActivity.showConnectionErrorNotice();
            return;
        }

        if(meetings!=null && !meetings.isEmpty()){
            for (MeetingCall meeting:meetings) {
                if(meetingId.equals(meeting.getMeetingNumber())){
                    String password = meeting.getPassword();
                    mActivity.joinMeeting(meetingId, password, voiceOnly, displayName,  !audioOn, !videoOn, getQaRate(),meeting.getServerAddress());
                    joinMeeting();
                    return;
                }
            }
        }

        mActivity.joinMeeting(meetingId, null, voiceOnly, displayName,  !audioOn, !videoOn, getQaRate(),serverAddress);
        joinMeeting();
    }

    private int getQaRate(){
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(mActivity).getLocalStore();
        }
        return Integer.parseInt(localStore.getCallRate());
    }

    private void joinMeeting() {
        mActivity.StartMeetingActivity();
    }

    private void onClickBack() {
        saveMeetingSettings();
        mActivity.replaceFragmentWithTag(mActivity.previousTag);
    }

    private void getMeetingSettings() {
        if(localStore == null ){
            localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
        }

        meetingId = localStore.getMeetingID();
        displayName = localStore.getDisplayName();

        inputMeetingId.setText(meetingId);
        inputDisplayName.setText(displayName);

    }
}
