
package frtc.sdk.ui.component;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import frtc.sdk.R;
import frtc.sdk.ui.FrtcMeetingActivity;


public class PasswordInputFragment extends Fragment {
    private EditText password;
    private Button clearButton;
    private CheckBox checkBoxType;
    private ImageView imageViewMessage;
    private TextView textViewPasswordError;
    private FrtcMeetingActivity activity;

    private JoinMeetingFailedListener mJoinMeetingFailedListener;

    private int requestCount;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mJoinMeetingFailedListener = (JoinMeetingFailedListener) activity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meeting_password_input_fragment, container, false);
        activity = (FrtcMeetingActivity) this.getActivity();
        checkBoxType = view.findViewById(R.id.edit_type);
        clearButton = view.findViewById(R.id.clear_btn);
        password = view.findViewById(R.id.meeting_password);
        checkBoxType.setChecked(false);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        clearButton.setOnClickListener(v -> password.setText(""));
        password.setOnFocusChangeListener((v, hasFocus) -> {
            String str = password.getText().toString();
            boolean vb = false;
            if (hasFocus && !str.isEmpty()) {
                vb = true;
            }
            clearButton.setVisibility(vb ? View.VISIBLE : View.GONE);
            checkBoxType.setVisibility(vb ? View.VISIBLE : View.GONE);
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String str = password.getText().toString();
                clearButton.setVisibility(!str.isEmpty() ? View.VISIBLE : View.GONE);
                checkBoxType.setVisibility(!str.isEmpty() ? View.VISIBLE : View.GONE);
            }

        });
        checkBoxType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        imageViewMessage = view.findViewById(R.id.notice);
        textViewPasswordError = view.findViewById(R.id.password_error);
        Button btnNegative = view.findViewById(R.id.negative_btn);
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJoinMeetingFailedListener.autoLeaveMeeting();
            }
        });
        Button b = view.findViewById(R.id.positive_btn);
        b.setOnClickListener(v -> checkPassword());
        if (requestCount > 0) {
            imageViewMessage.setVisibility(View.VISIBLE);
            textViewPasswordError.setVisibility(View.VISIBLE);
        } else {
            imageViewMessage.setVisibility(View.GONE);
            textViewPasswordError.setVisibility(View.GONE);
        }
        return view;
    }

    public void deleteOne() {
        if(password != null && password.getText() != null && password.getText().toString().length() > 0) {
            String password = this.password.getText().toString();
            String newText =  password.substring(0, password.length() - 1);
            this.password.setText(newText);
            this.password.setSelection(newText.length());
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        requestCount = (int) args.get(FrtcFragmentManager.INPUT_COUNT);
    }

    private void checkPassword() {
        activity.sendMeetingPassword(this.password.getText().toString().trim());
    }

}
