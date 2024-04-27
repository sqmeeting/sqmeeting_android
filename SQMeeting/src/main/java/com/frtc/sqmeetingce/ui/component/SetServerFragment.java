package com.frtc.sqmeetingce.ui.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.util.StringUtils;
import frtc.sdk.ui.component.BaseToast;

public class SetServerFragment extends BaseFragment {
    protected final String TAG = this.getClass().getSimpleName();

    public MainActivity mActivity;
    private LocalStore localStore;

    private EditText etServer;

    private String userServerAddress = "";
    private boolean isServerAddressBlank = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.set_server_fragment, container, false);

        etServer = view.findViewById(R.id.edit_server_address);
        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack();
            }
        });

        Button btnNextIn = view.findViewById(R.id.server_save_btn);
        btnNextIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave();
            }
        });

        Button btnCancel = view.findViewById(R.id.server_cancel_btn);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCancel();
            }
        });

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
        if (localStore != null) {
            userServerAddress = localStore.getServer();
            isServerAddressBlank = StringUtils.isBlank(userServerAddress);
        }
        etServer.setText(userServerAddress);

        return view;
    }


    @Override
    public void onBack() {
        onClickBack();
    }

    private void onClickBack(){
        if(isServerAddressBlank){
            mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
        }else{
            mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
        }
    }

    private void onClickCancel() {
        if(isServerAddressBlank){
            mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
        }else{
            mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
        }
    }

    private boolean validateSpecialChar(String text) {
        String PATTERN = "^[a-zA-Z0-9:.\\-\\]]*$";
        if (text.matches(PATTERN)) {
            return true;
        }
        return false;
    }

    private void onClickSave() {
        String text = etServer.getText().toString().trim();
        if (text.isEmpty()) {
            BaseToast.showToast(mActivity, getString(R.string.server_ip_error), Toast.LENGTH_SHORT);
            return;
        }

        if (!validateSpecialChar(text)) {
            BaseToast.showToast(mActivity, getString(R.string.server_address_format_error), Toast.LENGTH_SHORT);
            return;
        }

        if(text.equals(userServerAddress)){
            mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
            return;
        }

        userServerAddress = text;
        if (localStore != null) {
            localStore.setServer(userServerAddress);
            LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).setLocalStore(localStore);
            mActivity.setServerToSdk(userServerAddress,isServerAddressBlank);
        }

    }
}
