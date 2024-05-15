package com.frtc.sqmeetingce.ui.component;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import frtc.sdk.log.Log;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;

public class SignInFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();

    public MainActivity mActivity;
    private LocalStore localStore;

    private EditText inputName;
    private EditText inputPassword;
    private Button btnClearName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.sign_in_fragment, container, false);
        inputPassword = view.findViewById(R.id.edit_password);
        inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        inputName = view.findViewById(R.id.edit_user_name);
        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();
        inputName.setText(localStore.getUserName());
        localStore.setAutoSignIn(true);
        setClickListener(view);
        return view;
    }

    private void setClickListener(View view) {

        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick...");
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
            }
        });

        ImageButton btnSettings = view.findViewById(R.id.imageButton_setting);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Click btnSettings");
                mActivity.previousTag = FragmentTagEnum.FRAGMENT_SIGN_IN;
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
            }
        });

        Button btnSignIn = view.findViewById(R.id.sign_in_btn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput(v);
                String pw = inputPassword.getText().toString().trim();
                if(pw.length() >= 6 && pw.length() <= 32){
                    mActivity.doSignIn(inputName.getText().toString().trim(), inputPassword.getText().toString().trim());
                }else if(pw.length() < 6){
                    BaseToast.showToast(mActivity, getString(R.string.password_min_length), Toast.LENGTH_LONG);
                }else {
                    BaseToast.showToast(mActivity, getString(R.string.password_max_length), Toast.LENGTH_LONG);
                }
            }
        });

        btnClearName = view.findViewById(R.id.user_name_clear_btn);
        btnClearName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputName.setText("");
            }
        });

        inputName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String input = inputName.getText().toString();
                btnClearName.setVisibility(hasFocus && input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = inputName.getText().toString();
                btnClearName.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
            }

        });

        inputPassword.addTextChangedListener(new TextWatcher() {
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

    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
    }

    private void hideSoftInput(View view){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }catch (Exception exception){
            Log.e(TAG,"hideSoftInput failed",exception);
        }
    }

}
