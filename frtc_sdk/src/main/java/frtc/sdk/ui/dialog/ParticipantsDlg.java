package frtc.sdk.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import frtc.sdk.R;
import frtc.sdk.log.Log;
import frtc.sdk.ui.component.IParticipantListListener;
import frtc.sdk.ui.component.ParticipantListAdapter;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.ui.model.ParticipantInfo;
import frtc.sdk.util.Constants;
import frtc.sdk.util.BaseDialog;

public class ParticipantsDlg extends BaseDialog {

    private static final String TAG = ParticipantsDlg.class.getSimpleName();
    private Context mContext;
    private ListView mListView;
    private ParticipantListAdapter mAdapter;
    private List<ParticipantInfo> participantFullListData = new ArrayList<>();
    private List<ParticipantInfo> participantListData = new ArrayList<>();
    private TextView participantTitle;
    private LinearLayout llUnmuteReq;
    private ImageView ivUnview;
    private TextView name;

    private ConstraintLayout noSearchResult;
    private TextView searchKeyword;
    private EditText searchInput;
    private ImageView btnClear;
    private boolean showNoSearchResults = false;
    private IParticipantListListener listener;
    private boolean isHost = false;


    public ParticipantsDlg(Context context, List<ParticipantInfo> data, boolean isHost) {
        super(context, R.style.NoMaskDialogTheme);
        mContext = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        participantFullListData.clear();
        participantFullListData.addAll(data);
        this.isHost = isHost;
        Log.d(TAG,"ParticipantsDlg():"+isHost);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.participants_listview_layout);

        ImageView backView = findViewById(R.id.back_button);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        llUnmuteReq = findViewById(R.id.ll_unmute_request);
        name = findViewById(R.id.participant_name);
        Button viewBtn = findViewById(R.id.show_request_list_btn);
        ivUnview = findViewById(R.id.request_notify);

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onPermissionsRequestList();
                }
            }
        });

        mListView = findViewById(R.id.participants_list_view);
        mListView.setFooterDividersEnabled(true);

        participantListData.clear();
        participantListData.addAll(participantFullListData);

        mAdapter = new ParticipantListAdapter(getContext(), R.layout.participants_listview_item, participantListData, isHost);
        mAdapter.setOnItemClickedListener(new ParticipantListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(ParticipantInfo selectedParticipant, int position) {
                if(listener != null){
                    if(isHost || position == 0){
                        listener.onParticipantControl(selectedParticipant, isHost, position);
                    }

                }

            }
        });

        mListView.setAdapter(mAdapter);

        participantTitle = findViewById(R.id.participants_title);

        if (participantFullListData != null) {
            String strTitle = getContext().getString(R.string.participants_title) + " (" + participantFullListData.size() + ")";
            participantTitle.setText(strTitle);
        }

        noSearchResult = findViewById(R.id.no_search_result);
        searchKeyword = findViewById(R.id.search_keyword);

        noSearchResult.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        showNoSearchResults = false;

        searchInput = findViewById(R.id.search_input);
        btnClear = findViewById(R.id.btn_clear);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearchInput();
            }
        });
        searchInput.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String search = searchInput.getText().toString().trim();
                btnClear.setVisibility(hasFocus && search.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = searchInput.getText().toString().trim();
                btnClear.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
                searchParticipantsByKeyword(input);

            }
        });

        Button inviteBtn = findViewById(R.id.invite_btn);
        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalStore localStore = LocalStoreBuilder.getInstance(mContext.getApplicationContext()).getLocalStore();
                Constants.SdkType sdkType = localStore.getSdkType();
                if(sdkType == Constants.SdkType.SDK_TYPE_SQ) {
                    showInvitationInfoDlg();
                }
            }
        });

        Button muteAllBtn = findViewById(R.id.mute_all_btn);
        muteAllBtn.setVisibility(isHost ? View.VISIBLE : View.GONE);
        muteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isHost){
                    listener.onMuteAllParticipants();
                }
            }
        });

        Button unmuteAllBtn = findViewById(R.id.unmute_all_btn);
        unmuteAllBtn.setVisibility(isHost ? View.VISIBLE : View.GONE);
        unmuteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHost){
                    listener.onUnmuteAllParticipants();
                }
            }
        });

    }

    private void searchParticipantsByKeyword(String keyword){
        participantListData.clear();
        if(keyword.isEmpty()){
           participantListData.addAll(participantFullListData);
            if(showNoSearchResults){
                noSearchResult.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                showNoSearchResults = false;
            }
        }else{
            if(participantFullListData != null && !participantFullListData.isEmpty()){
                for(ParticipantInfo participant : participantFullListData){
                    if(participant.getDisplayName().toLowerCase().contains(keyword.toLowerCase())){
                        Log.d(TAG,"searchParticipantsByKeyword in :"+participant.getDisplayName());
                        participantListData.add(participant);
                    }
                }
            }
            if(participantListData.isEmpty()){
                showNoSearchResults = true;
                mListView.setVisibility(View.GONE);
                searchKeyword.setText(keyword);
                noSearchResult.setVisibility(View.VISIBLE);
            }else{
                if(showNoSearchResults){
                    noSearchResult.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    showNoSearchResults = false;
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void clearSearchInput(){
        searchInput.setText("");
        participantListData.clear();
        participantListData.addAll(participantFullListData);
        if(showNoSearchResults){
            noSearchResult.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void showInvitationInfoDlg(){
        InvitationInformationDlg invitationInformationDlg = new InvitationInformationDlg(mContext,(Activity)mContext, null);
        invitationInformationDlg.show();
    }

    public void setParticipantListListener(IParticipantListListener mListener){
        this.listener = mListener;
    }

    public void setHost(boolean isHost){
        this.isHost = isHost;
    }


    public void updateParticipantInfos(List<ParticipantInfo> infos) {
        Log.i(TAG, "updateParticipantInfos participantInfo list = " + infos);

        if(infos != null && infos.size() > 0){
            Log.d(TAG,"updateParticipantInfos me:"+infos.get(0).getDisplayName()+","+infos.get(0).getUuid()
                    +","+infos.get(0).isMe()+","+infos.get(0).isLecturer()+","+infos.get(0).isPinned());
        }

        participantFullListData.clear();
        participantFullListData.addAll(infos);

        if (participantFullListData != null) {
            String strTitle = getContext().getString(R.string.participants_title) + " (" + participantFullListData.size() + ")";
            participantTitle.setText(strTitle);
        }

        String searchInputStr = searchInput.getText().toString().trim();
        if(searchInputStr.isEmpty()){
            participantListData.clear();
            participantListData.addAll(participantFullListData);
            mAdapter.notifyDataSetChanged();

        }else{
            searchParticipantsByKeyword(searchInputStr);
        }
    }

    public void setllUnmuteReqVisible(String displayName, boolean visible, boolean unviewVisible) {
        if(!TextUtils.isEmpty(displayName)){
            name.setText(displayName);
        }
        llUnmuteReq.setVisibility(visible ? View.VISIBLE : View.GONE);
        ivUnview.setVisibility(unviewVisible ? View.VISIBLE : View.GONE);
    }

}
