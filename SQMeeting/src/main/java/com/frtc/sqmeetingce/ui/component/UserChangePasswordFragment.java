package com.frtc.sqmeetingce.ui.component;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.ui.model.SecurityConstant;

public class UserChangePasswordFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();

    public MainActivity mActivity;
    private LocalStore localStore;

    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private Button save;
    private Button cancel;

    private String oldPassword = "";
    private String newPassword = "";
    private String confirmPassword = "";

    private CheckBox editTypeOldPassword;
    private CheckBox editTypeNewPassword;
    private CheckBox editTypeConfirmPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.user_change_password_fragment, container, false);

        etOldPassword = view.findViewById(R.id.edit_old_password);
        etNewPassword = view.findViewById(R.id.edit_new_password);
        etConfirmPassword = view.findViewById(R.id.edit_confirm_password);
        etOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        editTypeOldPassword = view.findViewById(R.id.selector_old_password);
        editTypeNewPassword = view.findViewById(R.id.selector_new_password);
        editTypeConfirmPassword = view.findViewById(R.id.selector_confirm_password);

        editTypeOldPassword.setChecked(false);
        editTypeNewPassword.setChecked(false);
        editTypeConfirmPassword.setChecked(false);

        save = view.findViewById(R.id.change_password_save_btn);
        cancel = view.findViewById(R.id.change_password_cancel_btn);

        localStore = LocalStoreBuilder.getInstance(mActivity.getApplicationContext()).getLocalStore();

        setClickListener(view);

        return view;
    }

    private void setClickListener(View view) {

        ImageButton back = view.findViewById(R.id.back_btn);
        back.setOnClickListener(v -> mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS));


        save.setOnClickListener(v -> onChangePasswordSave());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangePasswordCancel();
            }
        });


        etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = etOldPassword.getText().toString();
                editTypeOldPassword.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
            }

        });


        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = etNewPassword.getText().toString();
                editTypeNewPassword.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = etConfirmPassword.getText().toString();
                editTypeConfirmPassword.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        etOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String input = etOldPassword.getText().toString();
                boolean visible = hasFocus && input.length() > 0;
                editTypeOldPassword.setVisibility(visible ? View.VISIBLE : View.GONE);

            }
        });

        etNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String input = etNewPassword.getText().toString();
                boolean visible = hasFocus && input.length() > 0;
                editTypeNewPassword.setVisibility(visible ? View.VISIBLE : View.GONE);

            }
        });

        etConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String input = etConfirmPassword.getText().toString();
                boolean visible = hasFocus && input.length() > 0;
                editTypeConfirmPassword.setVisibility(visible ? View.VISIBLE : View.GONE);

            }
        });

        editTypeOldPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    etOldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        editTypeNewPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        editTypeConfirmPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
    }


    private void onChangePasswordCancel(){
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
    }


    private void onChangePasswordSave() {
        oldPassword = etOldPassword.getText().toString().trim();
        newPassword = etNewPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();

        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm!=null&&mActivity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (mActivity.getCurrentFocus() != null){
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        if(!oldPassword.isEmpty() && !newPassword.isEmpty() && !confirmPassword.isEmpty()){
            if(newPassword.equals(confirmPassword)){

                if(localStore.getSecurityLevel().equals(SecurityConstant.SECURITY_LEVEL_NORMAL)){
                    if(newPassword.length() >= 6 && newPassword.length() <= 48 && newPassword.matches(SecurityConstant.NORMAL_PATTERN)){

                        mActivity.updateUserPassword(oldPassword,newPassword);

                    }else {
                        BaseToast.showToast(mActivity, getString(R.string.password_normal_complexity), Toast.LENGTH_LONG);
                    }

                }else if(localStore.getSecurityLevel().equals(SecurityConstant.SECURITY_LEVEL_HIGH)){

                    if(newPassword.length() >= 8 && newPassword.length() <= 48 && newPassword.matches(SecurityConstant.HIGH_PATTERN)){

                        mActivity.updateUserPassword(oldPassword,newPassword);

                    }else {
                        BaseToast.showToast(mActivity, getString(R.string.password_high_complexity), Toast.LENGTH_LONG);
                    }
                }
            }else{
                BaseToast.showToast(mActivity, getString(R.string.confirm_password_error), Toast.LENGTH_SHORT);
            }

        }else{
            BaseToast.showToast(mActivity, getString(R.string.signin_pwd_error), Toast.LENGTH_SHORT);
        }

    }

}
