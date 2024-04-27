package com.frtc.sqmeetingce.ui.component;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import frtc.sdk.log.Log;
import frtc.sdk.ui.dialog.IConfirmDlgListener;
import frtc.sdk.ui.dialog.MessageDlg;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.util.StringUtils;


public class HomeFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();
    public MainActivity mActivity;
    private LocalStore localStore;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        localStore = LocalStoreBuilder.getInstance(mActivity).getLocalStore();
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        setClickListener(view);

        TextView tvVersionInfo = view.findViewById(R.id.copyright);
        String strVersion = getContext().getString(R.string.build_version);
        tvVersionInfo.setText(strVersion + mActivity.getBuildVersion());

        if(StringUtils.isBlank(localStore.getServer())){
            showServerAddressBlankDlg();
        }else{
            mActivity.previousTag = FragmentTagEnum.FRAGMENT_HOME;
            autoSignIn();
        }

        return view;
    }

    private void showServerAddressBlankDlg(){
        MessageDlg messageDlg = new MessageDlg(mActivity,
                getString(R.string.set_server_address_title),
                getString(R.string.dialog_positive_btn));

        messageDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_SET_SERVER);
            }
            @Override
            public void onCancel(){

            }
        });
        messageDlg.show();
    }

    private boolean autoSignIn() {
        if (mActivity.checkAutoSignIn()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mActivity.doAutoSignIn();
                }
            });
        }
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setClickListener(View view) {

        ImageView ButtonScan = view.findViewById(R.id.menu_scan);
        ButtonScan.setOnClickListener(v -> {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)) {
                HomeFragment.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, MainActivity.CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                mActivity.startCaptureActivity();
            }
        });

        Button joinMeetingButton = view.findViewById(R.id.join_meeting_btn);
        joinMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActivity.isInMeeeting()){
                    return;
                }
                if (localStore == null) {

                    localStore = LocalStoreBuilder.getInstance(mActivity).getLocalStore();
                }
                if(localStore.getServer().isEmpty()){
                    showServerAddressBlankDlg();
                }else{
                    mActivity.previousTag = FragmentTagEnum.FRAGMENT_HOME;
                    mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_JOIN_MEETING);
                }
            }
        });

        Button SignInButton = view.findViewById(R.id.sign_in_btn);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localStore == null) {

                    localStore = LocalStoreBuilder.getInstance(mActivity).getLocalStore();
                }
                if(localStore.getServer().isEmpty()){
                    showServerAddressBlankDlg();
                }else{
                    mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_SIGN_IN);
                }
            }
        });

        ImageView settingsButton = view.findViewById(R.id.menu_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MainActivity.CAMERA_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mActivity.startCaptureActivity();
            } else {
                mActivity.showOpenCameraInformDlg();
            }
        }
    }


}
