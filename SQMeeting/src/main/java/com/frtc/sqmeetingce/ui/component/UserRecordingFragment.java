package com.frtc.sqmeetingce.ui.component;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;

public class UserRecordingFragment extends BaseFragment{

    protected final String TAG = this.getClass().getSimpleName();

    public MainActivity mActivity;
    private TextView textWebsite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.user_recording_fragment, container, false);

        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack();
            }
        });

        LocalStore localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
        if (localStore != null) {
            String userServerAddress = localStore.getServer();
            String websiteAddress = "https://"+userServerAddress;
            textWebsite = view.findViewById(R.id.website);
            textWebsite.setText(websiteAddress);
        }

        ImageView copyBtn = view.findViewById(R.id.copy_btn);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textWebsite.getText().toString().trim();
                ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", text);
                clipboard.setPrimaryClip(clip);
                BaseToast.showToast(mActivity, getString(R.string.copy_website_notice), Toast.LENGTH_SHORT);
            }
        });

        return view;
    }

    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
    }

    private void onClickBack(){
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
    }


}
