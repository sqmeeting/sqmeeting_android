package com.frtc.sqmeetingce.ui.component;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;

import frtc.sdk.log.Log;
import frtc.sdk.ui.component.BaseToast;


public class ProblemDiagnosisFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();
    public MainActivity mActivity;

    private EditText problemDiagnosisContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG,"onCreateView");

        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.problem_diagnosis_fragment, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        problemDiagnosisContent = view.findViewById(R.id.problem_diagnosis_content);
        Button btnUpload = view.findViewById(R.id.upload_btn);
        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                problemDiagnosisContent.setText("");
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strProblemDiagnosisContent = problemDiagnosisContent.getText().toString();
                if(TextUtils.isEmpty(strProblemDiagnosisContent)){
                    BaseToast.showToast(mActivity, getString(R.string.problem_diagnosis_content_empty), Toast.LENGTH_SHORT);
                    return;
                }
                if(mActivity.isNetworkConnected()) {
                    problemDiagnosisContent.setText("");
                    mActivity.showUploadLogFragment(strProblemDiagnosisContent);
                }else{
                    mActivity.showConnectionErrorNotice();
                }

            }
        });

    }


    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
    }

}
