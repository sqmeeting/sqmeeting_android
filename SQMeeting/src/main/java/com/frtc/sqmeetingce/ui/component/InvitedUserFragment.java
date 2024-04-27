package com.frtc.sqmeetingce.ui.component;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import java.util.ArrayList;
import java.util.List;

import frtc.sdk.model.UserInfo;
import frtc.sdk.ui.dialog.IConfirmDlgListener;
import frtc.sdk.ui.dialog.InformDlg;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;

public class InvitedUserFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();

    private LocalStore localStore;
    public MainActivity mActivity;

    private ConstraintLayout invitedUsersTitle;
    private ConstraintLayout searchedUsersTitle;
    private ConstraintLayout addedUsersTitle;

    private ConstraintLayout searchLayout;
    private TextView searchResult;
    private ConstraintLayout addUserLayout;

    private ConstraintLayout noSearchResult;
    private TextView searchKeyword;

    private ImageView btnBack;
    private ImageButton btnAddBack;
    private Button btnCancel;

    private EditText searchInput;
    private ImageView btnClear;

    private ConstraintLayout invitedUsersBottom;
    private ConstraintLayout addUsersBottom;

    private Button btnNext;
    private Button btnComplete;
    private TextView tvAddedNumber;

    private RecyclerView userListView;
    private AddedUserAdapter addedUserAdapter;
    private InvitedUserAdapter invitedUserAdapter;

    private boolean inSearchMode = false;
    private boolean showNoSearchResults = false;

    private List<InvitedUserInfo> searchList = new ArrayList<>();
    private List<InvitedUserInfo> addedUserList = new ArrayList<>();
    private List<InvitedUserInfo> allInvitedUserList = new ArrayList<>();

    private InvitedUserAdapter.OnItemClickListener invitedUserClickListener = new InvitedUserAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(InvitedUserInfo selectedUsers, int position) {
            if(selectedUsers.isAdded()){
                selectedUsers.setAdded(false);
                addedUserList.remove(selectedUsers);
            }else if(selectedUsers.isSelected()){
                selectedUsers.setSelected(false);
                addedUserList.remove(selectedUsers);
            }else{
                addedUserList.add(selectedUsers);
                selectedUsers.setSelected(true);
            }
            invitedUserAdapter.notifyItemChanged(position);
            updateBtnNextText();
        }
    };

    private AddedUserAdapter.OnItemClickListener addedItemClickListener = new AddedUserAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(InvitedUserInfo selectedUsers, int position) {
            selectedUsers.setAdded(false);
            addedUserList.remove(selectedUsers);
            addedUserAdapter.notifyItemRangeRemoved(position,1);
            updateAddNumberText();
        }
    };

    public InvitedUserFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        localStore = LocalStoreBuilder.getInstance(mActivity).getLocalStore();

        View view = inflater.inflate(R.layout.invited_user_fragment, container, false);
        initialUsersData();
        init(view);
        setClickListener(view);
        showAllUserLayout();
        return view;
    }

    private void initialUsersData(){
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
        }

        allInvitedUserList.clear();
        searchList.clear();
        addedUserList.clear();

        List<UserInfo> users = localStore.getUsers();

        String uuid = localStore.getUserId();
        if(users !=null && !users.isEmpty()){
            for(UserInfo user : users){
                if(!uuid.equals(user.getUser_id())){
                    InvitedUserInfo invitedUserInfo = new InvitedUserInfo(user.getUser_id(),user.getUsername(),user.getReal_name());
                    allInvitedUserList.add(invitedUserInfo);
                }
            }
        }

        List<UserInfo> invitedUsers = localStore.getScheduledMeetingSetting().getInvitedUsers();
        if(invitedUsers != null && !invitedUsers.isEmpty()){
            for(UserInfo invitedUser : invitedUsers){
                String userId = invitedUser.getUser_id();
                for(InvitedUserInfo user : allInvitedUserList){
                    if(user.getUserId().equals(userId)){
                        user.setAdded(true);
                        addedUserList.add(user);
                        break;
                    }
                }
            }
        }

        searchList.addAll(allInvitedUserList);
    }

    private void updateAddedUserInfo(){
        if(addedUserList != null && !addedUserList.isEmpty()){
            for(InvitedUserInfo addedUser : addedUserList){
                addedUser.setAdded(true);
                addedUser.setSelected(false);
            }
        }
        searchList.clear();
        searchList.addAll(allInvitedUserList);
    }

    public void updateUserListView(){

    }


    private void init(View view){

        invitedUsersTitle = view.findViewById(R.id.invited_users_title);
        searchedUsersTitle = view.findViewById(R.id.searched_users_title);
        addedUsersTitle = view.findViewById(R.id.added_users_title);

        searchLayout = view.findViewById(R.id.search_layout);
        searchResult = view.findViewById(R.id.search_result);

        addUserLayout = view.findViewById(R.id.add_user_layout);

        noSearchResult = view.findViewById(R.id.no_search_result);
        searchKeyword = view.findViewById(R.id.search_keyword);

        btnBack = view.findViewById(R.id.btn_back);
        btnAddBack = view.findViewById(R.id.btn_add_back);
        btnCancel = view.findViewById(R.id.btn_cancel);

        searchInput = view.findViewById(R.id.search_input);
        btnClear = view.findViewById(R.id.btn_clear);

        invitedUsersBottom = view.findViewById(R.id.invited_users_bottom);
        addUsersBottom = view.findViewById(R.id.added_user_bottom);

        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setEnabled(true);

        btnComplete = view.findViewById(R.id.btn_complete);
        tvAddedNumber = view.findViewById(R.id.added_number);

        userListView = view.findViewById(R.id.user_list_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        userListView.setLayoutManager(layoutManager);

        addedUserAdapter = new AddedUserAdapter(addedUserList);
        addedUserAdapter.setOnItemClickedListener(this.addedItemClickListener);

        invitedUserAdapter = new InvitedUserAdapter(searchList);
        invitedUserAdapter.setOnItemClickedListener(this.invitedUserClickListener);

        userListView.setAdapter(invitedUserAdapter);
        invitedUserAdapter.notifyDataSetChanged();

        showAllUserLayout();
    }

    private void showAllUserLayout(){

        invitedUsersTitle.setVisibility(View.VISIBLE);
        searchedUsersTitle.setVisibility(View.GONE);
        addedUsersTitle.setVisibility(View.GONE);

        invitedUsersBottom.setVisibility(View.VISIBLE);
        addUsersBottom.setVisibility(View.GONE);

        searchLayout.setVisibility(View.VISIBLE);
        searchResult.setVisibility(View.GONE);
        addUserLayout.setVisibility(View.GONE);

        noSearchResult.setVisibility(View.GONE);
        userListView.setVisibility(View.VISIBLE);

        updateBtnNextText();
    }

    private void updateBtnNextText(){

        String str = mActivity.getResources().getString(R.string.next) + " ("+addedUserList.size()+")";
        btnNext.setText(str);
    }

    private void updateAddNumberText(){
        String str = mActivity.getString(R.string.added_user_number);
        String str1 = String.format(str,""+addedUserList.size());
        tvAddedNumber.setText(str1);
    }

    private void showAddedListView(){

        invitedUsersTitle.setVisibility(View.GONE);
        searchedUsersTitle.setVisibility(View.GONE);
        addedUsersTitle.setVisibility(View.VISIBLE);

        invitedUsersBottom.setVisibility(View.GONE);
        addUsersBottom.setVisibility(View.VISIBLE);

        searchLayout.setVisibility(View.GONE);
        addUserLayout.setVisibility(View.VISIBLE);

        noSearchResult.setVisibility(View.GONE);
        userListView.setVisibility(View.VISIBLE);

        updateAddNumberText();
    }

    private void showSearchLayout(){
        invitedUsersTitle.setVisibility(View.GONE);
        searchedUsersTitle.setVisibility(View.VISIBLE);
        searchResult.setVisibility(View.VISIBLE);
    }

    private void showAllInvitedUsers(){
        searchInput.setText("");
        searchList.clear();
        searchList.addAll(allInvitedUserList);

        showAllUserLayout();
        invitedUserAdapter.notifyDataSetChanged();
    }

    private void clearSearchInput(){
        searchInput.setText("");
        searchList.clear();
        searchList.addAll(allInvitedUserList);
        if(showNoSearchResults){
            noSearchResult.setVisibility(View.GONE);
            searchResult.setVisibility(View.VISIBLE);
            userListView.setVisibility(View.VISIBLE);
        }
        invitedUserAdapter.notifyDataSetChanged();
    }

    private void showAddedUsersRecyclerView(){
        userListView.setAdapter(addedUserAdapter);
        addedUserAdapter.notifyDataSetChanged();
    }

    private void showUsersRecyclerView(){
        userListView.setAdapter(invitedUserAdapter);
        invitedUserAdapter.notifyDataSetChanged();
    }

    private void setClickListener(View view){


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });


        btnAddBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllUserLayout();
                updateAddedUserInfo();
                showUsersRecyclerView();
                clearSearchInput();
                invitedUserAdapter.notifyDataSetChanged();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllInvitedUsers();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setText("");
                searchList.clear();
                searchList.addAll(allInvitedUserList);
            }
        });

        searchInput.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                }
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
                if(!inSearchMode){
                    showSearchLayout();
                }
                searchUsersByKeyword(input);

            }
        });

        LinearLayout addUserItem = view.findViewById(R.id.add_user_item);
        addUserItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllUserLayout();
                updateAddedUserInfo();
                showUsersRecyclerView();
                clearSearchInput();
                invitedUserAdapter.notifyDataSetChanged();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddedListView();
                showAddedUsersRecyclerView();
            }
        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addedUserList.size()<=100 ){
                    saveInvitedUsers();
                    mActivity.replaceFragmentWithTag(mActivity.previousTag);
                }else{
                    final InformDlg informDlg = new InformDlg(mActivity,
                            getString(R.string.maximum_number_of_participants_title),
                            getString(R.string.meeting_participants_limitation_content),
                            getString(R.string.dialog_positive_btn));

                    informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
                        @Override
                        public void onConfirm() {
                            informDlg.dismiss();
                        }
                        @Override
                        public void onCancel(){

                        }
                    });
                    informDlg.show();
                }
            }
        });
    }

    private void searchUsersByKeyword(String keyword){
        searchList.clear();
        if(allInvitedUserList != null && !allInvitedUserList.isEmpty()){
            for(InvitedUserInfo user : allInvitedUserList){
                if(user.getRealName().contains(keyword) || user.getUsername().contains(keyword)){
                    searchList.add(user);
                }
            }
        }
        if(searchList.isEmpty()){
            showNoSearchResults = true;
            userListView.setVisibility(View.GONE);
            searchKeyword.setText(keyword);
            noSearchResult.setVisibility(View.VISIBLE);
            searchResult.setVisibility(View.GONE);
        }else{
            if(showNoSearchResults){
                noSearchResult.setVisibility(View.GONE);
                searchResult.setVisibility(View.VISIBLE);
                userListView.setVisibility(View.VISIBLE);
                showNoSearchResults = false;
            }
            invitedUserAdapter.notifyDataSetChanged();
        }
    }

    private void saveInvitedUsers(){
        List<UserInfo> saveList = new ArrayList<>();
        if(addedUserList != null && !addedUserList.isEmpty()){
            for(InvitedUserInfo user : addedUserList){
                UserInfo info = new UserInfo();
                info.setUser_id(user.getUserId());
                info.setUsername(user.getUsername());
                info.setReal_name(user.getRealName());
                saveList.add(info);
            }
        }
        localStore.getScheduledMeetingSetting().setInvitedUsers(saveList);
        allInvitedUserList.clear();
        searchList.clear();
        addedUserList.clear();
    }


    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(mActivity.previousTag);
    }
}
