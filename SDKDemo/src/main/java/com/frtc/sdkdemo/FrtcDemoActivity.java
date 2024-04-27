package com.frtc.sdkdemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import frtc.sdk.model.SignInParam;
import frtc.sdk.model.SignInResult;
import frtc.sdk.FrtcSdk;
import frtc.sdk.IMeetingStatusListener;
import frtc.sdk.ISignListener;
import frtc.sdk.internal.model.MeetingJoinInfo;
import frtc.sdk.internal.model.MeetingStateNotify;
import frtc.sdk.model.ParticipantInfo;
import frtc.sdk.internal.model.FrtcMeetingStatus;
import frtc.sdk.internal.model.ResultType;
import frtc.sdk.log.Log;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.util.PermissionManager;

public class FrtcDemoActivity extends AppCompatActivity implements IMeetingStatusListener, ISignListener {
    private final String TAG = this.getClass().getSimpleName();

    public FrtcSdk mFrtcSdk;

    private final int reqCode = 100001;

    private PermissionManager permissionManager;
    private String version;

    private EditText meetingIdEt, meetingPwdEt, serverAddressEt, userNameEt, userPwdEt;
    private Button meetingIdClearBtn, meetingPwdClearBtn, serverAddressClearBtn, userNameClearBtn, userPwdClearBtn;
    private CheckBox editType;
    private Context context;
    private ImageView noticeMeetingIv, noticeNameIv;
    private TextView meetingIdErrorTv, nameErrorTv;

    private String meetingIdStr = "";
    private String displayName = "";
    private String userToken = "";
    private String userId = "";
    private String userRole = "";

    private FrtcMeetingStatus meetingStatus = FrtcMeetingStatus.DISCONNECTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_meeting);
        mFrtcSdk = FrtcSdk.getInstance();
        context = this;
        initViews();
        setButtonsListener();
        setEditTextListener();
        permissionManager = new PermissionManager(getApplicationContext(), reqCode);
        verifyApplicationPermissions();
    }

    private void initViews() {
        meetingIdEt = findViewById(R.id.meeting_id);
        meetingIdClearBtn = findViewById(R.id.meeting_id_clear_btn);
        meetingIdErrorTv = findViewById(R.id.meeting_id_error_msg);
        noticeMeetingIv = findViewById(R.id.number_notice);
        meetingPwdEt = findViewById(R.id.meeting_password);
        meetingPwdClearBtn = findViewById(R.id.meeting_password_clear_btn);
        editType = findViewById(frtc.sdk.R.id.edit_type);
        editType.setChecked(false);
        meetingPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
        serverAddressEt = findViewById(R.id.server_address);
        serverAddressClearBtn = findViewById(R.id.server_address_clear_btn);
        userNameEt = findViewById(R.id.user_name);
        userNameClearBtn =findViewById(R.id.user_name_clear_btn);
        nameErrorTv = findViewById(R.id.title_your_name_error_msg);
        noticeNameIv = findViewById(R.id.name_notice);
        userPwdEt = findViewById(R.id.user_pwd);
        userPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
        userPwdClearBtn = findViewById(R.id.user_pwd_clear_btn);

        TextView tvVersionInfo = findViewById(R.id.copyright);
        String strVersion = getString(R.string.version_and_copyright);
        tvVersionInfo.setText(strVersion + getVersion());
    }

    private boolean isScreenShare = false;
    private boolean isFloatWindow = false;
    private void setButtonsListener() {
        Button btnCall = findViewById(R.id.call_btn);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick video call button");
                if(meetingStatus != FrtcMeetingStatus.DISCONNECTED){
                    BaseToast.showToast(context, getString(R.string.meeting_system_notification_click_prompt), Toast.LENGTH_LONG);
                    return;
                }
                displayName  = userNameEt.getText().toString().trim();
                String userPwdStr = userPwdEt.getText().toString().trim();
                String server = serverAddressEt.getText().toString();
                if(TextUtils.isEmpty((server))){
                    BaseToast.showToast(context, getString(R.string.server_empty_error), Toast.LENGTH_LONG);
                    return;
                }
                if(!TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(userPwdStr)){
                    if(userPwdStr.length() >= 6 && userPwdStr.length() <= 32){
                        isScreenShare = false;
                        isFloatWindow = false;
                        doSignIn(displayName, userPwdStr);
                    }else if(userPwdStr.length() < 6){
                        BaseToast.showToast(context, getString(R.string.password_min_length), Toast.LENGTH_LONG);
                    }else {
                        BaseToast.showToast(context, getString(R.string.password_max_length), Toast.LENGTH_LONG);
                    }
                }else {
                    if(TextUtils.isEmpty(userPwdStr)){
                        userToken = "";
                        userId = "";
                        userRole = "";
                    }
                    callWithParam(userToken, userId, userRole, false, false);
                }
            }
        });

        Button btnCallFloat = findViewById(R.id.call_float_btn);
        btnCallFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick call_float_btn");
                if(meetingStatus != FrtcMeetingStatus.DISCONNECTED){
                    BaseToast.showToast(context, getString(R.string.meeting_system_notification_click_prompt), Toast.LENGTH_LONG);
                    return;
                }
                displayName  = userNameEt.getText().toString().trim();
                String userPwdStr = userPwdEt.getText().toString().trim();
                String server = serverAddressEt.getText().toString();
                if(TextUtils.isEmpty((server))){
                    BaseToast.showToast(context, getString(R.string.server_empty_error), Toast.LENGTH_LONG);
                    return;
                }
               if(!TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(userPwdStr)){
                    if(userPwdStr.length() >= 6 && userPwdStr.length() <= 32){
                        isScreenShare = false;
                        isFloatWindow = true;
                        doSignIn(displayName, userPwdStr);
                    }else if(userPwdStr.length() < 6){
                        BaseToast.showToast(context, getString(R.string.password_min_length), Toast.LENGTH_LONG);
                    }else {
                        BaseToast.showToast(context, getString(R.string.password_max_length), Toast.LENGTH_LONG);
                    }
                }else {
                    if(TextUtils.isEmpty(userPwdStr)){
                        userToken = "";
                        userId = "";
                        userRole = "";
                    }
                    callWithParam(userToken, userId, userRole, true, false);
                }
            }
        });

        Button btnCallShare = findViewById(R.id.call_screen_share_btn);
        btnCallShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick call_screen_share_btn");
                if(meetingStatus != FrtcMeetingStatus.DISCONNECTED){
                    BaseToast.showToast(context, getString(R.string.meeting_system_notification_click_prompt), Toast.LENGTH_LONG);
                    return;
                }
                displayName  = userNameEt.getText().toString().trim();
                String userPwdStr = userPwdEt.getText().toString().trim();
                String server = serverAddressEt.getText().toString();
                if(TextUtils.isEmpty((server))){
                    BaseToast.showToast(context, getString(R.string.server_empty_error), Toast.LENGTH_LONG);
                    return;
                }
                if(!TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(userPwdStr)){
                    if(userPwdStr.length() >= 6 && userPwdStr.length() <= 32){
                        isScreenShare = true;
                        isFloatWindow = false;
                        doSignIn(displayName, userPwdStr);
                    }else if(userPwdStr.length() < 6){
                        BaseToast.showToast(context, getString(R.string.password_min_length), Toast.LENGTH_LONG);
                    }else {
                        BaseToast.showToast(context, getString(R.string.password_max_length), Toast.LENGTH_LONG);
                    }
                }else {
                    if(TextUtils.isEmpty(userPwdStr)){
                        userToken = "";
                        userId = "";
                        userRole = "";
                    }
                    callWithParam(userToken, userId, userRole, true, true);
                }
            }
        });

        meetingIdClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingIdEt.setText("");
            }
        });

        meetingPwdClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingPwdEt.setText("");
            }
        });

        serverAddressClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverAddressEt.setText("");
            }
        });

        userNameClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameEt.setText("");
            }
        });

        userPwdClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdEt.setText("");
            }
        });

    }

    private void setEditTextListener() {
        meetingIdEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(TAG, "meetingIdEt: get focus hasFocus = "+hasFocus);
                if (hasFocus) {
                    meetingIdErrorTv.setText("");
                    noticeMeetingIv.setVisibility(View.GONE);
                }
                String input = meetingIdEt.getText().toString();
                meetingIdClearBtn.setVisibility(hasFocus && input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        meetingIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                meetingIdErrorTv.setText("");
                if(noticeMeetingIv.getVisibility() ==View.VISIBLE){
                    noticeMeetingIv.setVisibility(View.GONE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = meetingIdEt.getText().toString();
                meetingIdClearBtn.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        meetingPwdEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String input = meetingPwdEt.getText().toString();
                boolean visible = hasFocus && input.length() > 0;
                meetingPwdClearBtn.setVisibility(visible ? View.VISIBLE : View.GONE);
                editType.setVisibility(visible ? View.VISIBLE : View.GONE);

            }
        });

        meetingPwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = meetingPwdEt.getText().toString();
                meetingPwdClearBtn.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
                editType.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
            }

        });


        editType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    meetingPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    meetingPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        serverAddressEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String input = serverAddressEt.getText().toString();
                serverAddressClearBtn.setVisibility(hasFocus && input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        meetingIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = serverAddressEt.getText().toString();
                serverAddressClearBtn.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        userNameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    nameErrorTv.setText("");
                    noticeNameIv.setVisibility(View.GONE);
                }
                String input = userNameEt.getText().toString();
                userNameClearBtn.setVisibility(hasFocus && input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        userNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameErrorTv.setText("");
                if(noticeNameIv.getVisibility() ==View.VISIBLE){
                    noticeNameIv.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = userNameEt.getText().toString();
                userNameClearBtn.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        userPwdEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String input = userPwdEt.getText().toString();
                userPwdClearBtn.setVisibility(hasFocus && input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        userPwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = userPwdEt.getText().toString();
                userPwdClearBtn.setVisibility(input.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities capabilities = connManager.getNetworkCapabilities(connManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true;
                }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                    return true;
                }
            }
        } else {
            try {
                NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void verifyApplicationPermissions() {
        String[] deniedPermissionList = permissionManager.getDeniedPermissionList();
        if(deniedPermissionList != null && deniedPermissionList.length > 0){
            requestPermissions(deniedPermissionList, permissionManager.getRequestCode());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    public String getVersion() {
        version = mFrtcSdk.getVersion();
        return version;
    }

    public void doSignIn(String userName, String clearPassword) {
        if(isNetworkAvailable()){
            signIn(userName, clearPassword);
        }else{
            showConnectionErrorNotice();
        }
    }

    private void signIn(String userName, String clearPassword) {
        final SignInParam signInParam = new SignInParam();
        signInParam.setUsername(userName);
        signInParam.setPassword(clearPassword);
        signInParam.setClientId(mFrtcSdk.getClientId());
        signInParam.setServerAddress(serverAddressEt.getText().toString());;

        mFrtcSdk.signIn(signInParam, this);

    }

    public void showConnectionErrorNotice(){
        BaseToast.showToast(this, getString(R.string.network_unavailable_notice), Toast.LENGTH_SHORT);
    }

    private void callWithParam(String userToken, String userId, String userRole, boolean isFloatWindow, boolean isScreenShare) {

        meetingIdStr = meetingIdEt.getText().toString().trim();
        if (meetingIdStr.isEmpty()) {
            meetingIdErrorTv.setVisibility(View.VISIBLE);
            noticeMeetingIv.setVisibility(View.VISIBLE);
            meetingIdErrorTv.setText(R.string.meeting_id_empty_error);
            return;
        }

        if (displayName.isEmpty()) {
            nameErrorTv.setVisibility(View.VISIBLE);
            noticeNameIv.setVisibility(View.VISIBLE);
            nameErrorTv.setText(R.string.username_empty_error);
            return;
        }

        if (!isNetworkAvailable()) {
            showConnectionErrorNotice();
            return;
        }

        MeetingJoinInfo meetingJoinInfo = new MeetingJoinInfo();
        meetingJoinInfo.setUserToken(userToken);
        meetingJoinInfo.setUserId(userId);
        meetingJoinInfo.setServer(serverAddressEt.getText().toString());
        meetingJoinInfo.setMeetingNumber(meetingIdStr);
        meetingJoinInfo.setMeetingPwd(meetingPwdEt.getText().toString());
        meetingJoinInfo.setMeetingOwnerId("");
        meetingJoinInfo.setDisplayName(displayName);
        meetingJoinInfo.setAudioOnly(false);
        meetingJoinInfo.setMuteAudio(true);
        meetingJoinInfo.setMuteVideo(true);
        meetingJoinInfo.setUserRole(userRole);
        meetingJoinInfo.setTimeStamp(System.currentTimeMillis());
        meetingJoinInfo.setScreenShare(isScreenShare);
        meetingJoinInfo.setFloatWindow(isFloatWindow);
        mFrtcSdk.joinMeeting(meetingJoinInfo, this);

    }

    @Override
    public void onMeetingStateNotify(MeetingStateNotify meetingStateNotify) {
        meetingStatus = meetingStateNotify.getMeetingStatus();
    }

    @Override
    public void onInviteUserNotify(List<ParticipantInfo> participantList) {

    }

    @Override
    public void onParticipantChangeNotify(List<ParticipantInfo> participantList) {

    }

    @Override
    public void onSignInResult(ResultType resultType, SignInResult signInResult) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                switch (resultType) {
                    case SUCCESS:
                        userToken = signInResult.getToken();
                        userId = signInResult.getUserId();
                        userRole = signInResult.getRole().get(0);
                        callWithParam(userToken, userId, userRole, isFloatWindow, isScreenShare);
                        break;
                    case UNAUTHORIZED:
                        BaseToast.showToast(FrtcDemoActivity.this, getString(R.string.sign_in_wrong), Toast.LENGTH_LONG);
                        break;
                    case CONNECTION_FAILED:
                        showConnectionErrorNotice();
                        break;
                    case FAILED:
                    case UNKNOWN:
                        Log.i(TAG, "onSignInResult resultType = " + resultType);
                        break;
                    default:
                        break;
                }
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
