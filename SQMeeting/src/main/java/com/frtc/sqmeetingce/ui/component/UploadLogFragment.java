package com.frtc.sqmeetingce.ui.component;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.frtc.sqmeetingce.MainActivity;
import com.frtc.sqmeetingce.R;
import com.frtc.sqmeetingce.util.MeetingUtil;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import frtc.sdk.internal.model.UploadLogMetaData;
import frtc.sdk.internal.model.UploadLogsStatus;
import frtc.sdk.log.Log;
import frtc.sdk.ui.component.BaseToast;

public class UploadLogFragment extends BaseFragment {

    protected final String TAG = this.getClass().getSimpleName();
    public MainActivity mActivity;

    private ProgressBar progressBar;
    private TextView progressContent;
    private Button btnReupload, btnCancel, btnReturn, btnCancelUpload;
    private TextView uploadLogText;
    private ImageView iconSuccess, iconFail;
    private LinearLayout llCanceling;
    private String strIssue;
    private int mTransId = -1;
    private UploadLogsStatus mUploadLogsStatus = new UploadLogsStatus();
    private boolean isUploading = false;
    private static final int FILE_COUNT = 5;
    private int fileType = 0;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG,"onCreateView");


        mActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.upload_log_fragment, container, false);

        init(view);

        startUpload();
        mHandler = new Handler();
        return view;
    }

    private void init(View view) {
        Log.d(TAG,"init");
        uploadLogText = view.findViewById(R.id.upload_log_text);
        iconSuccess = view.findViewById(R.id.icon_success);
        iconFail = view.findViewById(R.id.icon_fail);
        progressBar = view.findViewById(R.id.progress_horizontal);
        progressContent = view.findViewById(R.id.progress_content);
        btnReupload = view.findViewById(R.id.reupload_btn);
        btnCancel = view.findViewById(R.id.cancel_btn);
        btnReturn = view.findViewById(R.id.return_btn);
        btnCancelUpload = view.findViewById(R.id.cancel_upload_btn);
        llCanceling = view.findViewById(R.id.ll_canceling);
        fileType = 0;
        mTransId = -1;
        mUploadLogsStatus.progress = 0;
        progressBar.setProgress(0);
        progressContent.setText("0%");
        ImageButton btnBack = view.findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUploading){
                    BaseToast.showToast(mActivity, getString(R.string.log_uploading), Toast.LENGTH_SHORT);
                }else {
                    progressBar.setProgress(0);
                    mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_PROBLEM_DIAGNOSIS);
                }
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setProgress(0);
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
            }
        });

        btnReupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActivity.isNetworkConnected()) {
                    mUploadLogsStatus.progress = 0;
                    fileType = 0;
                    progressContent.setVisibility(View.VISIBLE);
                    iconSuccess.setVisibility(View.GONE);
                    iconFail.setVisibility(View.GONE);
                    btnReturn.setVisibility(View.GONE);
                    btnCancelUpload.setVisibility(View.VISIBLE);
                    btnReupload.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    startUpload();
                    //startProgress();
                }else{
                    mActivity.showConnectionErrorNotice();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_PROBLEM_DIAGNOSIS);
            }
        });

        btnCancelUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUploading) {
                    isUploading = false;
                    Log.d(TAG, "will cancelUploadLogs  isUploading ="+isUploading);
                    if (mHandler != null) {
                        mHandler.removeCallbacks(getUploadStatusRunnable);
                    }
                    mActivity.cancelUploadLogs();
                    llCanceling.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void startUpload() {
        UploadLogMetaData uploadLogMetaData = new UploadLogMetaData();
        uploadLogMetaData.setVersion(mActivity.getBuildVersion());
        uploadLogMetaData.setPlatform("android");
        if(MeetingUtil.isHarmonyOS()){
            Log.d(TAG,"isHarmonyOS version = "+Build.DISPLAY.split(" ")[1]);
            uploadLogMetaData.setOs("HarmonyOS " + Build.DISPLAY.split(" ")[1]);
        }else {
            uploadLogMetaData.setOs("Android " + Build.VERSION.RELEASE);
        }
        uploadLogMetaData.setDevice(Build.MANUFACTURER + " " +Build.MODEL);
        uploadLogMetaData.setIssue(strIssue);

        String strMetaData = new Gson().toJson(uploadLogMetaData);
        String fileName = mActivity.getExternalFilesDir("").getAbsolutePath() + "/" + "frtc_android.log";
        isUploading = true;
        Log.d(TAG, "will startUpload  isUploading ="+isUploading);
        mActivity.startUploadLogs(strMetaData, fileName, FILE_COUNT);
    }


    @Override
    public void onBack() {
        mActivity.replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_PROBLEM_DIAGNOSIS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        isUploading = false;
    }


    public void setStrIssue(String issue){
        this.strIssue = issue;
    }

    public void setTransId(int transId){
        this.mTransId = transId;
        if(mTransId == -1){
            isUploading = false;
        }
    }

    public void setUploadStatus(UploadLogsStatus uploadLogsStatus){
        Log.d(TAG, "setUploadProgress progress : " + uploadLogsStatus.progress);
        mUploadLogsStatus.progress = uploadLogsStatus.progress;
        mUploadLogsStatus.bitrate = uploadLogsStatus.bitrate;

        if(mUploadLogsStatus.progress < 0){
            isUploading = false;
            Log.d(TAG, "setUploadStatus  isUploading ="+isUploading);
            uploadLogText.setText(R.string.log_upload_fail);
            progressContent.setVisibility(View.GONE);
            iconSuccess.setVisibility(View.GONE);
            iconFail.setVisibility(View.VISIBLE);
            btnReturn.setVisibility(View.GONE);
            btnCancelUpload.setVisibility(View.GONE);
            btnReupload.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            fileType = 0;
        }
        if(mUploadLogsStatus.progress == -1){
            mActivity.showConnectionErrorNotice();
        }else if(mUploadLogsStatus.progress == -2){
            BaseToast.showToast(mActivity, getString(R.string.log_upload_fail), Toast.LENGTH_SHORT);
        }

        if(mUploadLogsStatus.progress >= 0 && mUploadLogsStatus.progress < 100 && fileType == 0) {
            Log.d(TAG, "setUploadProgress mUploadLogsStatus.progress : " + mUploadLogsStatus.progress);
            progressBar.setProgress(mUploadLogsStatus.progress);
            progressContent.setText(mUploadLogsStatus.progress + "%");

            double speed = mUploadLogsStatus.bitrate / (1024*1024);
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            String formattedResult = decimalFormat.format(speed);
            String str = mActivity.getString(R.string.log_uploading_speed);
            String str1 = String.format(str,formattedResult);
            uploadLogText.setText(str1);
        }else if(mUploadLogsStatus.progress == 100){
            if(fileType == 0){
                fileType = 2;
                mUploadLogsStatus.progress = 99;
                progressBar.setProgress(99);
                progressContent.setText("99%");
            }else if(fileType == 2) {
                progressBar.setProgress(mUploadLogsStatus.progress);
                progressContent.setText(mUploadLogsStatus.progress + "%");
                isUploading = false;
                uploadLogText.setText(R.string.log_upload_success);
                progressContent.setVisibility(View.GONE);
                iconSuccess.setVisibility(View.VISIBLE);
                iconFail.setVisibility(View.GONE);
                btnReturn.setVisibility(View.VISIBLE);
                btnCancelUpload.setVisibility(View.GONE);
                btnReupload.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                fileType = 0;
            }
        }
        if(mUploadLogsStatus.progress >= 0 && mUploadLogsStatus.progress < 100 && isUploading) {
            if (mHandler != null) {
                mHandler.postDelayed(getUploadStatusRunnable, 500);
            }
        }
    }

    Runnable getUploadStatusRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "getUploadStatusRunnable enter");
            mActivity.getUploadStatus(fileType);
        }
    };

    public int getFileType(){
        return fileType;
    }


    public void reSetParams() {
        isUploading = false;
        fileType = 0;
        mTransId = -1;
        mUploadLogsStatus.progress = 0;
        progressBar.setProgress(0);
        progressContent.setText("0%");
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public void getUploadStatus() {
        if (mHandler != null) {
            mHandler.postDelayed(getUploadStatusRunnable, 500);
        }
    }
}
