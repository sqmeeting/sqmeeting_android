package com.frtc.sqmeetingce.ui.component;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import java.util.Locale;

import frtc.sdk.log.Log;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.util.SettingUtil;


public class UserSettingsFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();
    private LocalStore localStore;
    public MainActivity mActivity;
    private TextView languageType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG,"onCreateView");

        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.user_settings_fragment, container, false);

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();

        init(view);

        return view;
    }

    private void init(View view) {
        languageType = view.findViewById(R.id.language_type);
        setLanType();

        TextView buildVersionCode = view.findViewById(R.id.settings_version_code);
        buildVersionCode.setText(mActivity.getBuildVersion());

        TextView address = view.findViewById(R.id.server_address);
        if (localStore != null) {
            address.setText(localStore.getServer());
        }

        ImageButton backButton = view.findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragmentWithTag(mActivity.previousTag);
            }
        });

        ConstraintLayout serverAddressItem = view.findViewById(R.id.server_address_item);
        serverAddressItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_SET_SERVER);
            }
        });

        Switch noiseBlockSwitch = view.findViewById(R.id.switch_noise_block);
        noiseBlockSwitch.setChecked(localStore.isNoiseBlock());
        noiseBlockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mActivity.setNoiseBlock(isChecked);
                localStore.setNoiseBlock(isChecked);
                LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).setLocalStore(localStore);
            }
        });

        Switch meetingReminderSwitch = view.findViewById(R.id.switch_meeting_reminder);
        meetingReminderSwitch.setChecked(localStore.isMeetingRemider());
        meetingReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    BaseToast.showToast(mActivity, getString(R.string.open_meeting_reminders_prompt), Toast.LENGTH_SHORT);
                }
                localStore.setMeetingRemider(isChecked);
                LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).setLocalStore(localStore);
            }
        });

        ConstraintLayout languageItem = view.findViewById(R.id.settings_language_item);
        languageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_SET_LANGUAGE);
            }
        });

        ConstraintLayout problemDiagnosisItem = view.findViewById(R.id.problem_diagnosis_item);
        problemDiagnosisItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_PROBLEM_DIAGNOSIS);
            }
        });

        TextView userName = view.findViewById(R.id.user_name);
        TextView displayName = view.findViewById(R.id.information_display_name);

        ConstraintLayout accountItem = view.findViewById(R.id.user_item);
        ConstraintLayout displayNameItem = view.findViewById(R.id.user_information_display_name_item);
        ConstraintLayout changePasswordItem = view.findViewById(R.id.user_information_password_item);
        ConstraintLayout recordingItem = view.findViewById(R.id.recording_item);
        Button btnSignOut = view.findViewById(R.id.sign_out_btn);

        if(localStore != null){
            userName.setText(localStore.getUserName());
            displayName.setText(localStore.getDisplayName());
            if(localStore.isLogged()){
                accountItem.setVisibility(View.VISIBLE);
                displayNameItem.setVisibility(View.VISIBLE);
                changePasswordItem.setVisibility(View.VISIBLE);
                recordingItem.setVisibility(View.VISIBLE);
                btnSignOut.setVisibility(View.VISIBLE);
                meetingReminderSwitch.setVisibility(View.VISIBLE);
            }else{
                accountItem.setVisibility(View.GONE);
                displayNameItem.setVisibility(View.GONE);
                changePasswordItem.setVisibility(View.GONE);
                recordingItem.setVisibility(View.GONE);
                btnSignOut.setVisibility(View.GONE);
                meetingReminderSwitch.setVisibility(View.GONE);
            }
        }

        changePasswordItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_CHANGE_PASSWORD);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignOut();
            }
        });

        Button btnWebPortal = view.findViewById(R.id.web_portal);
        Button btnOpensource = view.findViewById(R.id.opensource_website);

        String wpStr = getString(R.string.web_portal_label) + getString(R.string.web_portal);
        btnWebPortal.setText(wpStr);
        String osStr = getString(R.string.opensource_website_label) + getString(R.string.opensource_website);
        btnOpensource.setText(osStr);

        btnWebPortal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowser(getString(R.string.web_portal));
            }
        });

        btnOpensource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowser(getString(R.string.opensource_website));
            }
        });
    }

    private void openBrowser(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void doSignOut(){
        mActivity.signOut();
    }

    private void setLanType() {
        int nLanOptionPos = SettingUtil.getInstance(mActivity.getApplicationContext()).getLanguage();
        if(nLanOptionPos == -1){
            Locale locale = mActivity.getResources().getConfiguration().locale;
            String lang = locale.getLanguage() + "-"+locale.getCountry();

            switch (lang) {
                case "zh-CN":
                    languageType.setText(mActivity.getResources().getString(R.string.language_simplified_chinese));
                    break;
                case "zh-TW":
                    languageType.setText(mActivity.getResources().getString(R.string.language_traditional_chinese));
                    break;
                case "en-US":
                    languageType.setText(mActivity.getResources().getString(R.string.language_english));
                    break;
            }
        }
        if(nLanOptionPos == 0) {
            languageType.setText(mActivity.getResources().getString(R.string.language_simplified_chinese));
        }else if(nLanOptionPos == 1) {
            languageType.setText(mActivity.getResources().getString(R.string.language_traditional_chinese));
        }else if(nLanOptionPos == 2) {
            languageType.setText(mActivity.getResources().getString(R.string.language_english));
        }

    }


    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(mActivity.previousTag);
    }

}
