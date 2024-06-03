package com.frtc.sqmeetingce;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.frtc.sqmeetingce.ui.component.BaseFragment;
import com.frtc.sqmeetingce.ui.component.EditSingleRecurrenceMeetingFragment;

import frtc.sdk.model.RecurrenceMeetingListResult;
import frtc.sdk.ui.dialog.InformDlg;

import com.frtc.sqmeetingce.ui.component.FragmentFactory;
import com.frtc.sqmeetingce.ui.component.FragmentTagEnum;
import com.frtc.sqmeetingce.ui.component.InvitedUserFragment;
import com.frtc.sqmeetingce.ui.component.ConnectingFragment;
import com.frtc.sqmeetingce.ui.component.MeetingDetailsFragment;
import com.frtc.sqmeetingce.ui.component.ScheduleMeetingRepetitionFreqFragment;
import com.frtc.sqmeetingce.ui.component.ScheduleMeetingRepetitionFreqSettingFragment;
import com.frtc.sqmeetingce.ui.component.ScheduleRecurrenceMeetingListFragment;
import com.frtc.sqmeetingce.ui.component.ScheduledMeetingDetailsFragment;
import com.frtc.sqmeetingce.ui.component.UploadLogFragment;
import com.frtc.sqmeetingce.ui.component.UserFragment;
import com.frtc.sqmeetingce.util.MeetingUtil;
import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import frtc.sdk.FrtcCall;
import frtc.sdk.FrtcManagement;
import frtc.sdk.IFrtcCommonListener;
import frtc.sdk.IFrtcManagementListener;
import frtc.sdk.IMeetingReminderListener;
import frtc.sdk.internal.model.FindUserResult;
import frtc.sdk.internal.model.FrtcMeetingStatus;
import frtc.sdk.internal.model.FrtcSDKMeetingType;
import frtc.sdk.internal.model.QRCodeResult;
import frtc.sdk.internal.model.ResultType;
import frtc.sdk.internal.model.UploadLogsStatus;
import frtc.sdk.log.Log;
import frtc.sdk.model.AddMeetingToListParam;
import frtc.sdk.model.CreateInstantMeetingResult;
import frtc.sdk.model.CreateScheduledMeetingParam;
import frtc.sdk.model.CreateScheduledMeetingResult;
import frtc.sdk.model.DeleteScheduledMeetingParam;
import frtc.sdk.model.FindUserParam;
import frtc.sdk.model.GetScheduledMeetingListParam;
import frtc.sdk.model.GetScheduledMeetingParam;
import frtc.sdk.model.InstantMeetingParam;
import frtc.sdk.model.JoinMeetingOption;
import frtc.sdk.model.JoinMeetingParam;
import frtc.sdk.model.JoinMeetingQRCodeParam;
import frtc.sdk.model.MeetingRoom;
import frtc.sdk.model.PasswordUpdateParam;
import frtc.sdk.model.QueryMeetingInfoParam;
import frtc.sdk.model.QueryMeetingInfoResult;
import frtc.sdk.model.QueryMeetingRoomParam;
import frtc.sdk.model.QueryMeetingRoomResult;
import frtc.sdk.model.QueryUserInfoResult;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.model.ScheduledMeetingListResult;
import frtc.sdk.model.ScheduledMeetingResult;
import frtc.sdk.model.SignInParam;
import frtc.sdk.model.SignInResult;
import frtc.sdk.model.SignOutParam;
import frtc.sdk.model.UpdateScheduledMeetingParam;
import frtc.sdk.model.UserInfo;
import frtc.sdk.ui.FrtcMeetingActivity;
import frtc.sdk.ui.component.BaseToast;
import frtc.sdk.ui.dialog.IConfirmDlgListener;
import frtc.sdk.ui.dialog.InvitationInformationDlg;
import frtc.sdk.ui.dialog.MeetingReminderDlg;
import frtc.sdk.ui.dialog.MessageDlg;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.ui.model.MeetingCall;
import frtc.sdk.ui.model.MuteUponEntry;
import frtc.sdk.ui.model.ScheduledMeetingSetting;
import frtc.sdk.util.ActivityUtils;
import frtc.sdk.util.JSONUtil;
import frtc.sdk.util.LanguageUtil;
import frtc.sdk.util.PermissionManager;
import frtc.sdk.util.StringUtils;



public class MainActivity extends AppCompatActivity implements IFrtcManagementListener, IFrtcCommonListener{

    protected final String TAG = this.getClass().getSimpleName();
    private ConnectivityManager connManager;

    private final int meetingReminderTimerId = 1234;

    private PermissionManager mPermissionManager;
    public boolean isGetScheduleMeetings = false;
    private int mTractionId = -1;
    public FragmentTagEnum currentTag = FragmentTagEnum.FRAGMENT_HOME;
    public FragmentTagEnum previousTag = FragmentTagEnum.FRAGMENT_HOME;

    public FrtcCall frtcCall;
    public FrtcManagement frtcManagement;
    private LocalStore localStore;

    private String url;
    private boolean isUrlMeeting = false;

    Handler mHandler = new Handler();

    public static final int SCAN_QRCODE_REQUEST_CODE = 1001;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1002;
    public static final int CALL_END_REASON_REQUEST_CODE = 1003;
    public static final int PERMISSION_REQUEST_CODE = 1004;

    private static final String MEETING_END_CODE = "meeting_end_code";
    public static final String QR_URL = "qr_url";
    private static final String CHANNEL_ID = "reminder";
    public static final String MEETING_REMINDER_NOTIFICATION = "meetingReminderNotification";
    private BroadcastReceiver clickNotificationReceiver;
    private MeetingReminderDlg meetingReminderDlg;
    private InformDlg signInUnauthorizedInformDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (Intent.ACTION_MAIN.equals(action)
                    && intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
                finish();
                return;
            }
        }
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        LanguageUtil.setLanguage(this);
        setContentView(R.layout.main_activity);
        ActivityUtils.add(MainActivity.this);

        localStore = LocalStoreBuilder.getInstance(getApplicationContext()).getLocalStore();
        connManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        frtcCall = FrtcCall.getInstance();
        frtcCall.registerCommonListener(this);
        frtcManagement = FrtcManagement.getInstance();
        frtcManagement.registerManagementListener(this);

        url = getIntent().getStringExtra(MeetingUtil.URL_KEY);
        isUrlMeeting = null != url;

        clickNotificationReceiver = new ClickNotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MEETING_REMINDER_NOTIFICATION);
        registerReceiver(clickNotificationReceiver, filter);

        mPermissionManager = new PermissionManager(getApplicationContext(), PERMISSION_REQUEST_CODE);
        requestPermission();
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }

    public boolean checkAutoSignIn() {
        if (localStore == null || !localStore.isAutoSignIn()
                || StringUtils.isBlank(localStore.getUserName())
                || StringUtils.isBlank(localStore.getServer())) {
            return false;
        }
        String userName = localStore.getUserName();
        String key = localStore.getServer() + userName;
        String encryptPassword = localStore.getUserEncryptPassword(key);
        return !StringUtils.isBlank(encryptPassword);
    }

    public boolean isNetworkConnected() {

         if (connManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connManager.getNetworkCapabilities(connManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }  else return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        return true;
                    }
                } catch (Exception e) {

                }
            }
        }
        return false;
    }

    public void showConnectionErrorNotice(){
        BaseToast.showToast(this, getString(R.string.network_unavailable_notice), Toast.LENGTH_SHORT);
    }

    private void showHomeFragment() {
        frtcCall.setServerAddress(localStore.getServer());
        frtcManagement.setServerAddress(localStore.getServer());
        replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                joinMeetingByUrl();
            }
        },150);
    }

    public void showUserFragment(boolean isScheduleMeetingVisible){
        UserFragment fragment = UserFragment.newInstance(isScheduleMeetingVisible);
        replaceFragmentWithInstance(fragment, FragmentTagEnum.FRAGMENT_USER);
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
    protected void onNewIntent(Intent intent) {
        boolean isClickNotification = intent.getBooleanExtra("isClickNotification", false);
        if(isClickNotification){
            String JsonData= intent.getStringExtra("scheduleMeeting");
            ScheduledMeeting scheduledMeeting = new Gson().fromJson(JsonData,ScheduledMeeting.class);
            showMeetingDetails(scheduledMeeting);
        }
        String saveMeetingUrl = intent.getStringExtra("saveMeetingUrl");
        if(!TextUtils.isEmpty(saveMeetingUrl)){
            String saveMeetingNum = intent.getStringExtra("saveMeetingNum");
            saveMeetingIntoMeetingList(saveMeetingUrl, saveMeetingNum);
        }
        super.onNewIntent(intent);
    }

    private void saveMeetingIntoMeetingList(String meetingUrl, String meetingNum) {
        if(frtcCall.getMeetingStatus() != FrtcMeetingStatus.DISCONNECTED ){
            frtcCall.saveMeetingIntoMeetingListNotify();
            return;
        }
        String serverAddress =  frtcCall.getServerAddress();
        if (!meetingUrl.contains(serverAddress)) {
            showServerInconsistentDlg();
            return;
        }
        if(!localStore.isLogged()){
            showLoginDlg();
            return;
        }
        ArrayList<ScheduledMeeting> scheduledMeetings = localStore.getScheduledMeetings();
        for(ScheduledMeeting scheduledMeeting : scheduledMeetings){
            if(scheduledMeeting.getMeeting_number().equals(meetingNum)){
                BaseToast.showToast(MainActivity.this, getString(R.string.added_to_meeting_list_successfully), Toast.LENGTH_SHORT);
                return;
            }
        }
        addMeetingIntoMeetingList(meetingUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!LanguageUtil.isSharePreferenceLan(this)) {
            LanguageUtil.setLanguage(this);
        }
    }


    private void requestPermission() {
        String[] deniedList = mPermissionManager.getDeniedPermissionList();
        if(deniedList != null && deniedList.length > 0){
            requestPermissions(deniedList, mPermissionManager.getRequestCode());
        } else {
            showHomeFragment();
        }
    }

    private void joinMeetingByUrl() {
        if (isUrlMeeting) {
            if (isNetworkConnected()) {
                byte[] bUrl = Base64.decode(url, Base64.DEFAULT);
                Log.i(TAG,"joinMeetingByUrl bUrl = "+new String(bUrl));
                QRCodeResult obj = null;
                try{
                    obj = JSONUtil.transform(new String(bUrl), QRCodeResult.class);
                }catch(Exception e){
                    Log.e(TAG,"toObject QRCodeResult failed : "+e);
                }
                if(obj != null) {
                    String urlOperation = obj.getOperation();
                    if(!TextUtils.isEmpty(urlOperation) && urlOperation.equals("meeting_save")) {
                        saveMeetingIntoMeetingList(obj.getMeeting_url(), obj.getMeeting_number());
                        return;
                    }
                }
                String displayName = localStore.getDisplayName();
                JoinMeetingQRCodeParam joinMeetingQRCodeParam = new JoinMeetingQRCodeParam();
                joinMeetingQRCodeParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
                String userToken = LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken();
                if (userToken != null && !userToken.isEmpty()) {
                    joinMeetingQRCodeParam.setToken(userToken);
                }
                joinMeetingQRCodeParam.setQrcode(url);
                joinMeetingQRCodeParam.setDisplayName(displayName);
                boolean joined = frtcCall.joinMeetingWithQRCode(joinMeetingQRCodeParam);
                if(joined){
                    StartMeetingActivity();
                }else{
                    BaseToast.showToast(this, getString(R.string.network_not_available_try_again), Toast.LENGTH_SHORT);
                }
            }
        }
        isUrlMeeting = false;
    }

    private void addMeetingIntoMeetingList(String meetingUrl) {
        Log.d(TAG,"addMeetingIntoMeetingList");
        String meetingIdentifier = meetingUrl.substring(meetingUrl.lastIndexOf("/") + 1);
        AddMeetingToListParam param = new AddMeetingToListParam();
        param.setServerAddress(LocalStoreBuilder.getInstance(this).getLocalStore().getServer());
        param.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        param.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        param.setMeeting_identifier(meetingIdentifier);
        frtcManagement.addMeetingIntoMeetingList(param);
    }

    private void showLoginDlg() {
        Log.d(TAG,"showLoginDlg enter");
        InformDlg informDlg = new InformDlg(this,
                "",
                getString(R.string.login_first_before_add_meeting),
                getString(R.string.dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                Log.d(TAG,"showLoginDlg onConfirm");

            }
            @Override
            public void onCancel(){
                Log.d(TAG,"showLoginDlg onCancel");
            }
        });
        informDlg.show();
    }

    private void showServerInconsistentDlg() {
        InformDlg informDlg = new InformDlg(this,
                "",
                getString(R.string.not_support_join_external_system),
                getString(R.string.dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {

            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissionList, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissionList, grantResults);
        if (PERMISSION_REQUEST_CODE == requestCode) {
            showHomeFragment();
        }

    }

    public void setNoiseBlock(boolean enabled) {
        frtcCall.setNoiseBlock(enabled);
    }

    private void replaceFragmentWithInstance(BaseFragment fragment, FragmentTagEnum tag){
        Log.d(TAG,"replaceFragmentWithInstance:"+tag);
        if (MeetingUtil.isRunningBackground(getApplicationContext())) {
            return;
        }

        currentTag = tag;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.root_view, fragment,tag.getTypeName())
                .commitNow();
    }

    public void replaceFragmentWithTag(FragmentTagEnum tag) {
        Log.d(TAG,"replaceFragmentWithTag:"+tag);

        if (MeetingUtil.isRunningBackground(getApplicationContext())) {
            return;
        }

        currentTag = tag;

        BaseFragment fragment = FragmentFactory.createFragment(tag);

        if(fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.root_view, fragment, tag.getTypeName())
                    .commitNow();
        }
    }

    public String getBuildVersion() {
        String version = "";
        try{
            PackageManager packageManager = getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
            version = packInfo.versionName;
        }catch(PackageManager.NameNotFoundException e){
            Log.w(TAG,"get Application version name fails:"+e);
        }
        return version;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            BaseFragment fragment = getFragmentByTag(currentTag);
            if(fragment != null){
                fragment.onBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exitApplication(){
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy:FrtcCall:"+frtcCall+",FrtcManagement:"+frtcManagement+","+MainActivity.this);

        ActivityUtils.remove(MainActivity.this);
        if (frtcManagement != null) {
            frtcManagement.unRegisterManagementListener(this);
        }

        if(meetingReminderDlg != null){
            meetingReminderDlg.dismiss();
            meetingReminderDlg = null;
        }

        if(frtcCall != null){
            frtcCall.stopTimer(meetingReminderTimerId);
            frtcCall.unregisterCommonListener(this);
        }

        if(clickNotificationReceiver != null) {
            unregisterReceiver(clickNotificationReceiver);
        }
        if(localStore != null) {
            localStore.clearMeetingReminder();
            LocalStoreBuilder.getInstance(getApplicationContext()).setLocalStore(localStore);
        }
        super.onDestroy();
    }

    public void joinMeeting(String number, String password, boolean audioOnly, String displayName, boolean muteAudio, boolean muteVideo, int callRate, String serverAddr) {
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this).getLocalStore();
        }

        frtcCall.setServerAddress(localStore.getServer());
        frtcManagement.setServerAddress(localStore.getServer());

        JoinMeetingParam joinMeetingParam = new JoinMeetingParam();
        joinMeetingParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        String userToken = LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken();
        if (userToken != null && !userToken.isEmpty()) {
            joinMeetingParam.setToken(userToken);
        }
        joinMeetingParam.setMeetingNumber(number);
        joinMeetingParam.setDisplayName(displayName);
        joinMeetingParam.setServerAddress(serverAddr);
        if(password != null){
            joinMeetingParam.setMeetingPassword(password);
        }

        JoinMeetingOption option = new JoinMeetingOption();
        option.setNoAudio(muteAudio);
        option.setNoVideo(muteVideo);
        option.setAudioOnly(audioOnly);
        option.setCallRate(audioOnly ? 64 : callRate);

        if(password != null){
            localStore.setMeetingPassword(password);
        }
        localStore.setAudioOn(!muteAudio);
        localStore.setCameraOn(!muteVideo);
        localStore.setAudioCall(audioOnly);

        frtcCall.joinMeetingWithParam(joinMeetingParam, option);

    }

    public void joinMeeting(String number, String password, String displayName, boolean muteVideo, String meetingType) {
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this).getLocalStore();
        }

        frtcCall.setServerAddress(localStore.getServer());
        frtcManagement.setServerAddress(localStore.getServer());

        JoinMeetingParam joinMeetingParam = new JoinMeetingParam();
        joinMeetingParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        String userToken = LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken();
        if (userToken != null && !userToken.isEmpty()) {
            joinMeetingParam.setToken(userToken);
        }
        joinMeetingParam.setMeetingNumber(number);
        joinMeetingParam.setDisplayName(displayName);
        joinMeetingParam.setMeetingPassword(password);

        JoinMeetingOption option = new JoinMeetingOption();
        option.setNoAudio(true);
        option.setNoVideo(muteVideo);
        option.setAudioOnly(false);

        localStore.setMeetingPassword(password);
        localStore.setAudioOn(false);
        localStore.setCameraOn(!muteVideo);
        localStore.setAudioCall(false);
        localStore.setMeetingType(meetingType);
        frtcCall.joinMeetingWithParam(joinMeetingParam, option);
    }

    public void joinMeeting(String number, String password, String displayName, String serverAddr, String meetingType) {
        if(localStore == null){
            localStore = LocalStoreBuilder.getInstance(this).getLocalStore();
        }

        frtcCall.setServerAddress(localStore.getServer());
        frtcManagement.setServerAddress(localStore.getServer());

        JoinMeetingParam joinMeetingParam = new JoinMeetingParam();
        joinMeetingParam.setClientId(localStore.getClientId());
        String userToken = LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken();
        if (userToken != null && !userToken.isEmpty()) {
            joinMeetingParam.setToken(userToken);
        }
        joinMeetingParam.setMeetingNumber(number);
        joinMeetingParam.setDisplayName(displayName);
        joinMeetingParam.setMeetingPassword(password);
        joinMeetingParam.setServerAddress(serverAddr);

        JoinMeetingOption option = new JoinMeetingOption();
        option.setCallRate(Integer.parseInt(localStore.getCallRate()));
        option.setNoVideo(true);
        option.setNoAudio(true);
        option.setAudioOnly(false);

        localStore.setMeetingPassword(password);
        localStore.setMeetingType(meetingType);
        localStore.setAudioOn(false);
        localStore.setCameraOn(false);
        localStore.setAudioCall(false);
        frtcCall.joinMeetingWithParam(joinMeetingParam, option);
    }

    public void showMeetingDetails(ScheduledMeeting scheduledMeetingCall){
        if(scheduledMeetingCall == null){
            return;
        }
        if(scheduledMeetingCall.getMeeting_type().equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
            Log.d(TAG,"showMeetingDetails:"+scheduledMeetingCall.getRecurrence_gid()+","+scheduledMeetingCall.getReservation_id());
            queryScheduledRecurrenceMeetingList(1, 500, scheduledMeetingCall.getRecurrence_gid());
            updateRecurrenceMeeting(scheduledMeetingCall.getReservation_id());
        }
        storeScheduledMeetingSetting(scheduledMeetingCall);
        replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_DETAILS);
    }

    private void storeScheduledMeetingSetting(ScheduledMeeting scheduledMeeting){
        if(scheduledMeeting != null){
            if(localStore == null){
                localStore = LocalStoreBuilder.getInstance(this).getLocalStore();
            }

            String reservationId = localStore.getScheduledMeetingSetting().getReservationId();
            if(!TextUtils.isEmpty(reservationId)
                    && !scheduledMeeting.getReservation_id().equals(reservationId)){
                localStore.resetScheduledMeetingSetting();
            }
            dumpScheduledMeetingSetting(scheduledMeeting,localStore.getScheduledMeetingSetting());
        }
    }

    private void dumpScheduledMeetingSetting(ScheduledMeeting scheduledMeeting, ScheduledMeetingSetting scheduledMeetingSetting){
        if(scheduledMeeting == null){
            return;
        }

        if(scheduledMeetingSetting == null){
            return;
        }

        scheduledMeetingSetting.setMeetingNumber(scheduledMeeting.getMeeting_number());
        scheduledMeetingSetting.setMeetingName(scheduledMeeting.getMeeting_name());
        scheduledMeetingSetting.setPassword(scheduledMeeting.getMeeting_password());
        scheduledMeetingSetting.setMeetingType(scheduledMeeting.getMeeting_type());
        scheduledMeetingSetting.setReservationId(scheduledMeeting.getReservation_id());

        scheduledMeetingSetting.setStartTime(scheduledMeeting.getSchedule_start_time());
        scheduledMeetingSetting.setEndTime(scheduledMeeting.getSchedule_end_time());
        scheduledMeetingSetting.setOwnerId(scheduledMeeting.getOwner_id());
        scheduledMeetingSetting.setOwnerName(scheduledMeeting.getOwner_name());
        scheduledMeetingSetting.setMeeting_url(scheduledMeeting.getMeeting_url());
        scheduledMeetingSetting.setParticipantUsers(scheduledMeeting.getParticipantUsers());
        scheduledMeetingSetting.setGroupMeetingUrl(scheduledMeeting.getGroupMeetingUrl());
        scheduledMeetingSetting.setReservationGid(scheduledMeeting.getRecurrence_gid());

        if(FrtcSDKMeetingType.RECURRENCE.getTypeName().equals(scheduledMeeting.getMeeting_type())){
            scheduledMeetingSetting.setRecurrence_type(scheduledMeeting.getRecurrence_type());
            scheduledMeetingSetting.setRecurrenceInterval(scheduledMeeting.getRecurrenceInterval());
            scheduledMeetingSetting.setRecurrenceStartTime(scheduledMeeting.getRecurrenceStartTime());
            scheduledMeetingSetting.setRecurrenceEndTime(scheduledMeeting.getRecurrenceEndTime());
            scheduledMeetingSetting.setRecurrenceStartDay(scheduledMeeting.getRecurrenceStartDay());
            scheduledMeetingSetting.setRecurrenceEndDay(scheduledMeeting.getRecurrenceEndDay());
            scheduledMeetingSetting.setRecurrenceDaysOfWeek(scheduledMeeting.getRecurrenceDaysOfWeek());
            scheduledMeetingSetting.setRecurrenceDaysOfMonth(scheduledMeeting.getRecurrenceDaysOfMonth());

            scheduledMeetingSetting.clearRecurrenceMeetings();
            scheduledMeetingSetting.getRecurrenceMeetings().addAll(scheduledMeeting.getRecurrenceReservationList());
        }
    }

    public void showScheduledRecurrenceMeetingDetails(){
        replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_DETAILS);
    }

    private String getStrSpentTime(long startTime, long endTime){
        String strSpentTime = "";
        long seconds = (endTime - startTime)/1000;
        long hour = seconds / 3600;
        long minutes = seconds % 3600 / 60;
        if(hour > 0){
            if(minutes == 0){
                strSpentTime = String.valueOf(hour) + getString(R.string.time_hours);
            }else{
                strSpentTime = String.valueOf(hour) + getString(R.string.time_hours) + String.valueOf(minutes) + getString(R.string.time_minutes);
            }

        }else if (minutes > 0){
            strSpentTime = String.valueOf(minutes) + getString(R.string.time_minutes) ;
        }else {
            strSpentTime = String.valueOf(seconds) + getString(R.string.time_seconds) ;
        }
        return strSpentTime;
    }

    public void showMeetingDetails(MeetingCall meetingCall){
        Bundle bundle = new Bundle();
        bundle.putString("MeetingName",meetingCall.getMeetingName());
        bundle.putString("MeetingNumber",meetingCall.getMeetingNumber());
        bundle.putString("Time",meetingCall.getTime());
        bundle.putString("Password",meetingCall.getPassword());
        bundle.putString("DisplayName",meetingCall.getDisplayName());
        bundle.putString("SpentTime",getStrSpentTime(meetingCall.getCreateTime(),meetingCall.getLeaveTime()));
        bundle.putString("ServerAddress",meetingCall.getServerAddress());
        bundle.putString("MeetingType",meetingCall.getMeetingType());

        MeetingDetailsFragment fragment = new MeetingDetailsFragment();
        fragment.setArguments(bundle);
        replaceFragmentWithInstance(fragment, FragmentTagEnum.FRAGMENT_MEETING_DETAILS);
    }

    public void updateRecurrenceMeeting(String reservationId){
        getScheduledMeetingById(reservationId);
    }


    public void doSignIn(String userName, String clearPassword) {
        Log.i(TAG, "doSignIn:");
        if(isNetworkConnected()){
            localStore.setUserName(userName);
            localStore.setUserClearPassword(clearPassword);
            signIn(userName, clearPassword);
        }else{
            showConnectionErrorNotice();
        }
    }

    public void doAutoSignIn() {
        Log.i(TAG, "doAutoSignIn...");
        if (checkAutoSignIn()) {
            String userName = localStore.getUserName();
            String key = localStore.getServer() + userName;
            String encryptPassword = localStore.getUserEncryptPassword(key);
            String clearPassword = localStore.decryptPassword(encryptPassword);
            if(isNetworkConnected()){
                Log.i(TAG, "doAutoSignIn");
                signIn(userName, clearPassword);
                replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_CONNECTING);
            }else{
                showConnectionErrorNotice();
            }

        }
    }

    private boolean isConnectingFragmentVisible(){
        BaseFragment fragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_CONNECTING);
        return fragment instanceof ConnectingFragment && fragment.isVisible();
    }

    private void signIn(String userName, String clearPassword) {
        final SignInParam signInParam = new SignInParam();
        signInParam.setUsername(userName);
        signInParam.setPassword(clearPassword);
        signInParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        frtcManagement.signIn(signInParam, null);
    }

    public void signOut() {
        if (!isNetworkConnected()) {
            showConnectionErrorNotice();
            return;
        }
        if (localStore != null) {
            SignOutParam param = new SignOutParam();
            param.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
            param.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
            frtcManagement.signOut(param);
        }
    }


    public void setServerToSdk(String address, Boolean isInitializing) {

        frtcCall.setServerAddress(address);
        frtcManagement.setServerAddress(address);
        if(localStore.isLogged()){
            if(isNetworkConnected()){
                signOut();
            }
            cleanSignInfo();
            showUserChangeServerInformDlg();
        }else {
            if(isInitializing){
                replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
            }else{
                replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER_SETTINGS);
            }
            BaseToast.showToast(this, getString(R.string.setting_saved_notice), Toast.LENGTH_SHORT);
        }
    }

    private void showUserChangeServerInformDlg(){
        InformDlg informDlg = new InformDlg(this,
                getString(R.string.set_server_dialog_title),
                getString(R.string.set_server_dialog_content),
                getString(R.string.dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_SIGN_IN);
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void showUserChangePasswordInformDlg(){
        InformDlg informDlg = new InformDlg(this,
                getString(R.string.password_change_dialog_title),
                getString(R.string.password_change_dialog_content),
                getString(R.string.dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_SIGN_IN);
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    public void showScheduleMeetingFragment(boolean isUpdate){
        previousTag = FragmentTagEnum.FRAGMENT_USER;
        if(isUpdate){
            replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_UPDATE_SCHEDULED_MEETING);
        }else {
            replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING);
        }
    }

    public void createInstantMeeting(String meetingName) {
        InstantMeetingParam param = new InstantMeetingParam();
        param.setMeetingName(meetingName);
        param.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        param.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        frtcManagement.createInstantMeeting(param);
    }

    public void createScheduleMeeting(){
        Log.d(TAG,"createScheduleMeeting.");
        if(localStore != null){
            CreateScheduledMeetingParam param = new CreateScheduledMeetingParam();
            param.setServerAddress(localStore.getServer());
            param.setClientId(localStore.getClientId());
            param.setToken(localStore.getUserToken());

            param.setMeeting_name(localStore.getScheduledMeetingSetting().getMeetingName());
            param.setMeeting_password(localStore.getScheduledMeetingSetting().getPassword());

            param.setMeeting_description("");
            param.setSchedule_start_time(localStore.getScheduledMeetingSetting().getStartTime());
            param.setSchedule_end_time(localStore.getScheduledMeetingSetting().getEndTime());
            param.setMeeting_room_id(localStore.getScheduledMeetingSetting().getMeetingRoomId());
            param.setCall_rate_type(localStore.getScheduledMeetingSetting().getRate());
            List<String> users = new ArrayList<>();
            List<UserInfo> invitedUsers = localStore.getScheduledMeetingSetting().getInvitedUsers();
            if (invitedUsers != null && !invitedUsers.isEmpty()) {
                for (UserInfo invitedUser : invitedUsers) {
                    users.add(invitedUser.getUser_id());
                }
            }
            param.setInvited_users(users);

            param.setMute_upon_entry(localStore.getScheduledMeetingSetting().isMute() ? MuteUponEntry.ENABLE.getValue() : MuteUponEntry.DISABLE.getValue());
            param.setGuest_dial_in(localStore.getScheduledMeetingSetting().isGuestDialIn());
            param.setWatermark(localStore.getScheduledMeetingSetting().isWatermarkEnable());
            param.setWatermark_type(localStore.getScheduledMeetingSetting().getWatermarkType());
            param.setTime_to_join(localStore.getScheduledMeetingSetting().getJoinTime());
            String meetingType = localStore.getScheduledMeetingSetting().getMeetingType();
            if(TextUtils.isEmpty(meetingType)) {
                param.setMeeting_type(FrtcSDKMeetingType.RESERVATION.getTypeName());
            }else {
                param.setMeeting_type(meetingType);
            }
            if(meetingType.equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
                param.setRecurrence_type(localStore.getScheduledMeetingSetting().getRecurrence_type());
                param.setRecurrenceInterval(localStore.getScheduledMeetingSetting().getRecurrenceInterval());
                param.setRecurrenceStartTime(Long.parseLong(localStore.getScheduledMeetingSetting().getStartTime()));
                param.setRecurrenceEndTime(Long.parseLong(localStore.getScheduledMeetingSetting().getEndTime()));
                param.setRecurrenceStartDay(localStore.getScheduledMeetingSetting().getRecurrenceStartDay());
                param.setRecurrenceEndDay(localStore.getScheduledMeetingSetting().getRecurrenceEndDay());
                param.setRecurrenceDaysOfWeek(localStore.getScheduledMeetingSetting().getRecurrenceDaysOfWeek());
                param.setRecurrenceDaysOfMonth(localStore.getScheduledMeetingSetting().getRecurrenceDaysOfMonth());
            }
            frtcManagement.createScheduledMeeting(param);
            Log.d(TAG,"will resetScheduledMeetingSetting.");
            localStore.resetScheduledMeetingSetting();
            LocalStoreBuilder.getInstance(getApplicationContext()).setLocalStore(localStore);
        }

    }


    public void updateSingleScheduleMeeting(ScheduledMeeting scheduledMeeting){
        if(scheduledMeeting == null){
            return;
        }

        Log.d(TAG,"updateSingleScheduleMeeting:"+scheduledMeeting.getReservation_id());

        if(localStore != null){
            UpdateScheduledMeetingParam param = new UpdateScheduledMeetingParam();
            param.setServerAddress(localStore.getServer());
            param.setClientId(localStore.getClientId());
            param.setToken(localStore.getUserToken());

            param.setReservationId(scheduledMeeting.getReservation_id());
            param.setMeeting_name(scheduledMeeting.getMeeting_name());
            param.setSchedule_start_time(scheduledMeeting.getSchedule_start_time());
            param.setSchedule_end_time(scheduledMeeting.getSchedule_end_time());
            param.setMeeting_type(scheduledMeeting.getMeeting_type());

            param.setMeeting_description("");
            param.setMeeting_password(localStore.getScheduledMeetingSetting().getPassword());
            param.setMeeting_room_id(localStore.getScheduledMeetingSetting().getMeetingRoomId());
            param.setCall_rate_type(localStore.getScheduledMeetingSetting().getRate());
            List<String> users = new ArrayList<>();
            List<UserInfo> invitedUsers = localStore.getScheduledMeetingSetting().getInvitedUsers();
            if (invitedUsers != null && !invitedUsers.isEmpty()) {
                for (UserInfo invitedUser : invitedUsers) {
                    users.add(invitedUser.getUser_id());
                }
            }
            param.setInvited_users(users);
            param.setMute_upon_entry(localStore.getScheduledMeetingSetting().isMute() ? MuteUponEntry.ENABLE.getValue() : MuteUponEntry.DISABLE.getValue());
            param.setGuest_dial_in(localStore.getScheduledMeetingSetting().isGuestDialIn() ? "true" : "false");
            param.setWatermark(localStore.getScheduledMeetingSetting().isWatermarkEnable()? "true" : "false");
            param.setWatermark_type(localStore.getScheduledMeetingSetting().getWatermarkType());
            param.setSingle(true);
            frtcManagement.updateScheduledMeeting(param);
        }
    }

    public void updateScheduleMeeting(String reservationId){
        Log.d(TAG,"updateSingleScheduleMeeting:"+reservationId);
        if(localStore != null){
            UpdateScheduledMeetingParam param = new UpdateScheduledMeetingParam();
            param.setServerAddress(localStore.getServer());
            param.setClientId(localStore.getClientId());
            param.setToken(localStore.getUserToken());
            param.setReservationId(reservationId);

            param.setMeeting_name(localStore.getScheduledMeetingSetting().getMeetingName());
            param.setMeeting_password(localStore.getScheduledMeetingSetting().getPassword());
            param.setMeeting_description("");
            param.setSchedule_start_time(localStore.getScheduledMeetingSetting().getStartTime());
            param.setSchedule_end_time(localStore.getScheduledMeetingSetting().getEndTime());
            param.setMeeting_room_id(localStore.getScheduledMeetingSetting().getMeetingRoomId());
            param.setCall_rate_type(localStore.getScheduledMeetingSetting().getRate());
            List<String> users = new ArrayList<>();
            List<UserInfo> invitedUsers = localStore.getScheduledMeetingSetting().getInvitedUsers();
            if (invitedUsers != null && !invitedUsers.isEmpty()) {
                for (UserInfo invitedUser : invitedUsers) {
                    users.add(invitedUser.getUser_id());
                }
            }
            param.setInvited_users(users);

            param.setMute_upon_entry(localStore.getScheduledMeetingSetting().isMute() ? MuteUponEntry.ENABLE.getValue() : MuteUponEntry.DISABLE.getValue());
            param.setGuest_dial_in(localStore.getScheduledMeetingSetting().isGuestDialIn() ? "true" : "false");
            param.setWatermark(localStore.getScheduledMeetingSetting().isWatermarkEnable()? "true" : "false");
            param.setWatermark_type(localStore.getScheduledMeetingSetting().getWatermarkType());

            param.setMeeting_type(localStore.getScheduledMeetingSetting().getMeetingType());
            param.setSingle(false);

            if(FrtcSDKMeetingType.RECURRENCE.getTypeName().equals(param.getMeeting_type())){
                param.setRecurrenceType(localStore.getScheduledMeetingSetting().getRecurrenceType());
                param.setRecurrenceInterval(localStore.getScheduledMeetingSetting().getRecurrenceInterval());
                param.setRecurrenceStartTime(localStore.getScheduledMeetingSetting().getRecurrenceStartTime());
                param.setRecurrenceEndTime(localStore.getScheduledMeetingSetting().getRecurrenceEndTime());
                param.setRecurrenceStartDay(localStore.getScheduledMeetingSetting().getRecurrenceStartDay());
                param.setRecurrenceEndDay(localStore.getScheduledMeetingSetting().getRecurrenceEndDay());
                param.setRecurrenceDaysOfWeek(localStore.getScheduledMeetingSetting().getRecurrenceDaysOfWeek());
                param.setRecurrenceDaysOfMonth(localStore.getScheduledMeetingSetting().getRecurrenceDaysOfMonth());
            }

            frtcManagement.updateScheduledMeeting(param);
            Log.d(TAG,"resetScheduledMeetingSetting.");
            localStore.resetScheduledMeetingSetting();
            LocalStoreBuilder.getInstance(getApplicationContext()).setLocalStore(localStore);
        }
    }

    public void deleteScheduledMeeting(String reservationId, boolean isCheck){

        Log.d(TAG,"deleteScheduledMeeting:"+reservationId);
        DeleteScheduledMeetingParam param = new DeleteScheduledMeetingParam();
        param.setServerAddress(LocalStoreBuilder.getInstance(this).getLocalStore().getServer());
        param.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        param.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        param.setReservationId(reservationId);
        if(isCheck) {
            param.setDeleteGroup("true");
        }else{
            param.setDeleteGroup("false");
        }
        frtcManagement.deleteScheduledMeeting(param);
    }

    public void getScheduledMeetingById(String reservationId){
        Log.d(TAG,"getScheduledMeetingById:"+reservationId);
        GetScheduledMeetingParam param = new GetScheduledMeetingParam();
        param.setServerAddress(LocalStoreBuilder.getInstance(this).getLocalStore().getServer());
        param.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        param.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        param.setReservationId(reservationId);
        frtcManagement.getScheduledMeeting(param);
    }

    public void queryScheduledMeetingList(int num, int size){
        Log.d(TAG,"queryScheduledMeetingList");
        GetScheduledMeetingListParam param = new GetScheduledMeetingListParam();
        param.setServerAddress(LocalStoreBuilder.getInstance(this).getLocalStore().getServer());
        param.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        param.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        param.setPage_num(Integer.toString(num));
        param.setPage_size(Integer.toString(size));
        param.setFilter("");
        frtcManagement.getScheduledMeetingList(param);
    }

    private void queryScheduledRecurrenceMeetingList() {
        String recurrenceGid = localStore.getScheduledMeetingSetting().getReservationGid();
        if(TextUtils.isEmpty(recurrenceGid)){
            queryScheduledRecurrenceMeetingList(1, 500, recurrenceGid);
        }
    }

    public void queryScheduledRecurrenceMeetingList(int num, int size, String recurrenceGid) {
        Log.d(TAG,"queryScheduledRecurrenceMeetingList");
        GetScheduledMeetingListParam param = new GetScheduledMeetingListParam();
        param.setServerAddress(LocalStoreBuilder.getInstance(this).getLocalStore().getServer());
        param.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        param.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        param.setPage_num(Integer.toString(num));
        param.setPage_size(Integer.toString(size));
        param.setFilter("");
        param.setRecurrence_gid(recurrenceGid);
        frtcManagement.getScheduledRecurrenceMeetingList(param);
    }

    public void findUserList(int num, int size, String filter){
        Log.d(TAG,"FindUserList:"+filter);
        FindUserParam param = new FindUserParam();
        param.setServerAddress(LocalStoreBuilder.getInstance(this).getLocalStore().getServer());
        param.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        param.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        param.setPage_num(Integer.toString(num));
        param.setPage_size(Integer.toString(size));
        param.setFilter(filter);
        frtcManagement.findUser(param);
    }

    private void handleGetScheduledMeetingResult(ScheduledMeetingResult scheduledMeetingResult) {
        if(scheduledMeetingResult == null){
            return;
        }

        String reservationId = localStore.getScheduledMeetingSetting().getReservationId();
        Log.d(TAG,"handleGetScheduledMeetingResult: scheduledMeetingResult.reservationId = "+scheduledMeetingResult.getReservation_id()
                + "," + "current reservationId:"+reservationId+","+scheduledMeetingResult.getReservation_id().equals(reservationId));

        if(!scheduledMeetingResult.getReservation_id().equals(reservationId)) {
            localStore.resetScheduledMeetingSetting();
        }

        ScheduledMeetingSetting setting = localStore.getScheduledMeetingSetting();
        setting.setRecurrenceCount(setting.getRecurrenceMeetings().size());
        setting.setReservationId(scheduledMeetingResult.getReservation_id());
        setting.setMeetingName(scheduledMeetingResult.getMeeting_name());
        setting.setMeetingType(scheduledMeetingResult.getMeeting_type());
        setting.setRate(scheduledMeetingResult.getCall_rate_type());
        setting.setReservationGid(scheduledMeetingResult.getRecurrence_gid());
        setting.setStartTime(scheduledMeetingResult.getSchedule_start_time());
        setting.setEndTime(scheduledMeetingResult.getSchedule_end_time());
        setting.setMeetingRoomId(scheduledMeetingResult.getMeeting_room_id());
        setting.setPassword(scheduledMeetingResult.getMeeting_password());
        setting.setInvitedUsers(scheduledMeetingResult.getInvited_users_details());
        setting.setMute(scheduledMeetingResult.getMute_upon_entry().equals(MuteUponEntry.ENABLE.getValue()));
        setting.setGuestDialIn(scheduledMeetingResult.getGuest_dial_in().equals("true"));
        setting.setWatermarkEnable(scheduledMeetingResult.getWatermark().equals("true"));
        setting.setWatermarkType(scheduledMeetingResult.getWatermark_type());
        setting.setOwnerId(scheduledMeetingResult.getOwner_id());
        setting.setOwnerName(scheduledMeetingResult.getOwner_name());
        setting.setQrcode(scheduledMeetingResult.getQrcode_string());
        setting.setMeeting_url(scheduledMeetingResult.getMeeting_url());
        setting.setRecurrence_type(scheduledMeetingResult.getRecurrence_type());
        setting.setRecurrenceInterval(scheduledMeetingResult.getRecurrenceInterval());
        setting.setRecurrenceStartTime(scheduledMeetingResult.getRecurrenceStartTime());
        setting.setRecurrenceEndTime(scheduledMeetingResult.getRecurrenceEndTime());
        setting.setRecurrenceStartDay(scheduledMeetingResult.getRecurrenceStartDay());
        setting.setRecurrenceEndDay(scheduledMeetingResult.getRecurrenceEndDay());
        setting.setRecurrenceDaysOfWeek(scheduledMeetingResult.getRecurrenceDaysOfWeek());
        setting.setRecurrenceDaysOfMonth(scheduledMeetingResult.getRecurrenceDaysOfMonth());
    }

    int pageNum = 0;
    private void handleQueryScheduledMeetingList(List<ScheduledMeeting> scheduledMeetings, int totalPageNum, int totalSize) {
        if(scheduledMeetings != null){
            Log.i(TAG, "handleQueryScheduledMeetingList:" + scheduledMeetings.size()
                    + ", total page num: " +totalPageNum
                    + ", total size:" + totalSize
                    + ", pageNum:" + pageNum);

            isGetScheduleMeetings = false;
            localStore.clearScheduledMeetings();
            localStore.getScheduledMeetings().addAll(scheduledMeetings);

            BaseFragment fragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_USER);
            if(fragment instanceof UserFragment && fragment.isVisible()){
                ((UserFragment)fragment).updateScheduledMeetingListview();
            }
        }
    }

    private boolean handleQueryScheduledRecurrenceMeetingList(RecurrenceMeetingListResult result){
        if(result != null){
            List<ScheduledMeeting> scheduledMeetings = result.getMeeting_schedules();
            int totalPageNum = result.getTotal_page_num();
            int totalSize = result.getTotal_size();

            ScheduledMeetingSetting setting = localStore.getScheduledMeetingSetting();

            if(scheduledMeetings != null && !scheduledMeetings.isEmpty() && scheduledMeetings.get(0) != null){
                if(scheduledMeetings.get(0).getRecurrence_gid().equals(setting.getReservationGid())){
                    pageNum ++;
                    Log.i(TAG, "handleQueryScheduledRecurrenceMeetingList:" + scheduledMeetings.size()
                            + ", total page num: " +totalPageNum
                            + ", total size:" + totalSize
                            + ", pageNum:" + pageNum);

                    if(pageNum == totalPageNum){
                        if(pageNum == 1) {
                            setting.clearRecurrenceMeetings();
                        }
                        setting.getRecurrenceMeetings().addAll(scheduledMeetings);
                    }else{
                        if(setting.isRecurrenceMeetingFullList()){
                            setting.clearRecurrenceMeetings();
                            setting.setRecurrenceMeetingFullList(false);
                        }
                        setting.getRecurrenceMeetings().addAll(scheduledMeetings);
                        if(pageNum < totalPageNum){
                            setting.setRecurrenceMeetingFullList(false);
                            queryScheduledRecurrenceMeetingList(pageNum + 1,50, setting.getReservationGid());
                            return false;
                        }
                    }
                    pageNum = 0;
                    setting.setRecurrenceMeetingFullList(true);

                    setting.setRecurrence_type(result.getRecurrenceType());
                    setting.setRecurrenceInterval(result.getRecurrenceInterval());
                    setting.setRecurrenceDaysOfWeek(result.getRecurrenceDaysOfWeek());
                    setting.setRecurrenceDaysOfMonth(result.getRecurrenceDaysOfMonth());
                    setting.setRecurrenceStartTime(result.getRecurrenceStartTime());
                    setting.setRecurrenceEndTime(result.getRecurrenceEndTime());
                    setting.setRecurrenceStartDay(result.getRecurrenceStartDay());
                    setting.setRecurrenceEndDay(result.getRecurrenceEndDay());

                    return true;
                }else{
                    Log.e(TAG,"handleQueryScheduledRecurrenceMeetingList: wrong ReservationGid");
                }
            }else{
                Log.w(TAG,"handleQueryScheduledRecurrenceMeetingList: empty recurrence meeting list");
            }
        }
        return false;
    }

    private void handleFindUserResult(List<UserInfo> users, int totalPageNum, int totalSize){
        if(users != null){
            Log.i(TAG, "handleFindUserResult:" + users.size()
                    + "total page num: " +totalPageNum
                    + "total size:" + totalSize);

            if(users.size() == totalSize){
                localStore.clearUsers();
                localStore.getUsers().addAll(users);
            }else{
                if(localStore.isUsersFullList()){
                    localStore.clearUsers();
                    localStore.setUsersFullList(false);
                }
                localStore.getUsers().addAll(users);
                int size = localStore.getUsers().size();
                if(size < totalSize){
                    int num = size/50 + 1;
                    localStore.setUsersFullList(false);
                    findUserList(num,50,localStore.getUserFilter());
                    return;
                }
            }
            localStore.setUsersFullList(true);
            localStore.setUserFilter("");

            BaseFragment fragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_INVITED_USER);
            if(fragment instanceof InvitedUserFragment && fragment.isVisible()){
                ((InvitedUserFragment)fragment).updateUserListView();
            }
        }
    }

    public void queryMeetingRooms(){
        Log.d(TAG,"queryMeetingRooms");
        QueryMeetingRoomParam param = new QueryMeetingRoomParam();
        param.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        param.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        frtcManagement.queryMeetingRoomInfo(param);
    }

    public void queryMeetingInfo(String url){
        int indexOfServer = url.indexOf("https://");
        int indexOfMeetingToken = url.indexOf("/j/");
        Log.d(TAG,"queryMeetingInfo:"+indexOfServer+","+indexOfMeetingToken);
        if(indexOfServer >= 0 && indexOfMeetingToken > 0){
            String server = url.substring(indexOfServer+("https://").length(),indexOfMeetingToken);
            String meetingToken = url.substring(indexOfMeetingToken + ("/j/").length());
            if(server.isEmpty() || meetingToken.isEmpty()){
                BaseToast.showToast(this, getString(R.string.msg_qrscan_result_error), Toast.LENGTH_SHORT);
                return;
            }
            Log.d(TAG,"queryMeetingInfo:");
            QueryMeetingInfoParam param = new QueryMeetingInfoParam();
            param.setSeverAddress(server);
            param.setMeetingToken(meetingToken);
            frtcManagement.queryMeetingInfo(param);
        }else{
            BaseToast.showToast(this, getString(R.string.msg_qrscan_result_error), Toast.LENGTH_SHORT);
        }
    }

    public void updateUserPassword(String oldPassword,String newPassword) {
        PasswordUpdateParam param = new PasswordUpdateParam();
        param.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
        param.setToken(LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken());
        param.setPasswordOld(oldPassword);
        param.setPasswordNew(newPassword);
        frtcManagement.updatePassword(param);
    }

    private void handleSignInResult(SignInResult signInResult) {
        if(signInResult == null){
            return;
        }
        try {
            localStore.setUserId(signInResult.getUserId());
            localStore.setUserName(signInResult.getUsername());
            localStore.setUserToken(signInResult.getToken());
            localStore.setRealName(signInResult.getRealName());
            localStore.setEmail(signInResult.getEmail());
            localStore.setDisplayName(signInResult.getRealName());
            localStore.setDepartment(signInResult.getDepartment());
            localStore.setMobile(signInResult.getMobile());
            localStore.setRole(new HashSet<String>(signInResult.getRole()));
            localStore.setSecurityLevel(signInResult.getSecurityLevel());
            LocalStoreBuilder.getInstance(getApplicationContext()).setLocalStore(localStore);

        } catch (Exception e) {
            Log.e(TAG,"handleSignInResult error:"+e);
        }
    }

    private void handleSignInUnauthorized(){
        showSignInUnauthorizedInformDlg();
        cleanSignInfo();
    }

    private void showAccountLockedDlg(){
        InformDlg informDlg = new InformDlg(this,
                getString(R.string.sign_in_account_locked_dlg_title),
                getString(R.string.sign_in_account_locked_dlg_content),
                getString(R.string.dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                if(isConnectingFragmentVisible()){
                    replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
                }
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }

    private void showSignInUnauthorizedMultipleTimesDlg(){
        MessageDlg messageDlg = new MessageDlg(this,
                getString(R.string.sign_in_error_multiple_times_dlg_content),
                getString(R.string.dialog_positive_btn));

        messageDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
            }
            @Override
            public void onCancel(){

            }
        });
        messageDlg.show();
    }

    private void showSignInUnauthorizedNotice(){
        BaseToast.showToast(MainActivity.this, getString(R.string.sign_in_wrong), Toast.LENGTH_LONG);
    }

    private void showSignInUnauthorizedOtherNotice(){
        BaseToast.showToast(MainActivity.this, getString(R.string.sign_in_other_error), Toast.LENGTH_LONG);
    }

    private void showSignInUnauthorizedInformDlg(){
        if(signInUnauthorizedInformDlg == null ) {
            signInUnauthorizedInformDlg = new InformDlg(this,
                    getString(R.string.log_out_dialog_title),
                    getString(R.string.log_out_dialog_content),
                    getString(R.string.dialog_positive_btn));

            signInUnauthorizedInformDlg.setConfirmDlgListener(new IConfirmDlgListener() {
                @Override
                public void onConfirm() {
                    replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
                }

                @Override
                public void onCancel() {

                }
            });
            signInUnauthorizedInformDlg.show();
        }else if(!signInUnauthorizedInformDlg.isShowing()){
            signInUnauthorizedInformDlg.show();
        }
    }

    private void handleCreateInstantMeeting(CreateInstantMeetingResult createInstantMeetingResult){

        if(createInstantMeetingResult != null){
            String meetingName = createInstantMeetingResult.getMeetingName();
            String meetingId = createInstantMeetingResult.getMeetingNumber();
            String meetingPassword = createInstantMeetingResult.getMeetingPassword();
            if(localStore == null){
                localStore = LocalStoreBuilder.getInstance(getApplicationContext()).getLocalStore();
            }
            if (!isNetworkConnected()) {
                showConnectionErrorNotice();
                return;
            }
            joinMeeting(meetingId,meetingPassword,localStore.getDisplayName(), !localStore.isCameraOn(), createInstantMeetingResult.getMeeting_type());
            StartMeetingActivity();
        }
    }

    private void handleQueryMeetingRoomInfoResult(QueryMeetingRoomResult queryMeetingRoomResult){
        if(queryMeetingRoomResult != null){
            if(localStore == null){
                localStore = LocalStoreBuilder.getInstance(getApplicationContext()).getLocalStore();
            }
            localStore.setMeetingRooms((ArrayList<MeetingRoom>) queryMeetingRoomResult.getMeetingRooms());
            LocalStoreBuilder.getInstance(this).setLocalStore(localStore);
        }
    }

    private void handleQueryMeetingInfoResult(QueryMeetingInfoResult queryMeetingInfoResult){
        if(queryMeetingInfoResult != null){
            String meetingLink = queryMeetingInfoResult.getMeeting_link();
            if(meetingLink != null){
                String displayName = LocalStoreBuilder.getInstance(this).getLocalStore().getDisplayName();
                JoinMeetingQRCodeParam joinMeetingQRCodeParam = new JoinMeetingQRCodeParam();
                joinMeetingQRCodeParam.setClientId(LocalStoreBuilder.getInstance(this).getLocalStore().getClientId());
                String userToken = LocalStoreBuilder.getInstance(this).getLocalStore().getUserToken();
                if (userToken != null && !userToken.isEmpty()) {
                    joinMeetingQRCodeParam.setToken(userToken);
                }
                joinMeetingQRCodeParam.setQrcode(meetingLink);
                joinMeetingQRCodeParam.setDisplayName(displayName);
                boolean joined = frtcCall.joinMeetingWithQRCode(joinMeetingQRCodeParam);
                if(joined){
                    StartMeetingActivity();
                }
            }
        }
    }

    public void StartMeetingActivity(){
        Intent intent = new Intent();
        intent.setClass(this, FrtcMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, CALL_END_REASON_REQUEST_CODE);
    }

    public void startCaptureActivity() {
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.FORMATS, "QR_CODE");
        startActivityForResult(intent, SCAN_QRCODE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult" + requestCode+"，"+resultCode);
        if (requestCode == SCAN_QRCODE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra(CaptureActivity.SCAN_RESULT);
            Log.d(TAG, "qrcode result is " + result);
            if(result.startsWith("https://") && result.contains("/j/")){
                Log.d(TAG, "enhance qrcode result is " + result);
                queryMeetingInfo(result);
            } else {
                BaseToast.showToast(this, getString(R.string.msg_qrscan_result_error), Toast.LENGTH_SHORT);
            }
        }

        if (requestCode == CALL_END_REASON_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            int callEndCode = data.getIntExtra(MEETING_END_CODE,0);
            Log.d(TAG, "onActivityResult: callEndCode =" + callEndCode);
            if(callEndCode == 13){
                handleSignInUnauthorized();
            }
        }
    }

    private void cleanSignInfo(){
        localStore.clearMeetingRooms();
        localStore.setLastUserName(localStore.getUserName());
        localStore.setUserToken("");
        localStore.setRealName("");
        localStore.setDisplayName("");
        localStore.setMeetingID("");
        localStore.clearRole();
        localStore.setUserClearPassword(null);
        if(StringUtils.isNotBlank(localStore.getServer()) && StringUtils.isNotBlank(localStore.getUserName())) {
            String key = localStore.getServer() + localStore.getUserName();
            localStore.removeUserPassword(key);
        }
        LocalStoreBuilder.getInstance(getApplicationContext()).setLocalStore(localStore);
    }

    public void saveUserNameAndPassword(){
        if (StringUtils.isNotBlank(localStore.getServer())
                && StringUtils.isNotBlank(localStore.getUserName())
                && StringUtils.isNotBlank(localStore.getUserClearPassword())) {
            Log.i(TAG, "saveUserNameAndPassword()");
            String key = localStore.getServer() + localStore.getUserName();
            String passwordEnc = localStore.encryptPassword(localStore.getUserClearPassword());
            localStore.addUserPwd(key, passwordEnc);
            localStore.setUserClearPassword(null);
            LocalStoreBuilder.getInstance(getApplicationContext()).setLocalStore(localStore);
        }
    }

    @Override
    public void onSignOutResult(final ResultType resultType) {
        Log.d(TAG,"onSignOutResult:"+resultType);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
                            cleanSignInfo();
                            frtcCall.stopTimer(meetingReminderTimerId);
                            localStore.clearMeetingReminder();
                            LocalStoreBuilder.getInstance(getApplicationContext()).setLocalStore(localStore);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            Log.e(TAG, "SignOut failed:" + resultType);
                            break;
                        default:
                            break;
                    }
                }
            }
        });

    }
    @Override
    public void onStopMeetingResult(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
             Log.i(TAG, "onStopMeetingResult:"+resultType);
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case SUCCESS:
                        case CONNECTION_FAILED:
                        case FAILED:
                        case UNKNOWN:
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onQueryUserInfoResult(final ResultType resultType, final QueryUserInfoResult queryUserInfoResult) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void onCreateInstantMeetingResult(final ResultType resultType, final CreateInstantMeetingResult createInstantMeetingResult) {
        if (resultType == null || createInstantMeetingResult == null) {
            Log.e(TAG,"onCreateInstantMeetingResult failed");
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, resultType + createInstantMeetingResult.toString());
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            handleCreateInstantMeeting(createInstantMeetingResult);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            Log.e(TAG, "onCreateInstantMeetingResult:" + resultType);
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onUpdatePasswordResult(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            cleanSignInfo();
                            showUserChangePasswordInformDlg();
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            Log.e(TAG, "onUpdatePasswordResult failed");
                            BaseToast.showToast(MainActivity.this, getString(R.string.user_password_changed_failed_notice), Toast.LENGTH_LONG);
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onMuteAllParticipantResult(final ResultType resultType, boolean allowUnMute) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onMuteAllParticipantResult:"+resultType);
                switch (resultType) {
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case SUCCESS:
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onMuteParticipantResult(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onMuteParticipantResult:"+resultType);
                switch (resultType) {
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case SUCCESS:
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onUnMuteAllParticipantResult(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onUnMuteAllParticipantResult:"+resultType);
                switch (resultType) {
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case SUCCESS:
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onUnMuteParticipantResult(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onUnMuteAllParticipantResult:"+resultType);
                switch (resultType) {
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case SUCCESS:
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onChangeDisplayNameResult(final ResultType resultType, String name, boolean isMe) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onChangeDisplayNameResult:"+resultType);
                switch (resultType) {
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case SUCCESS:
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onSetLecturerResult(final ResultType resultType, final boolean isSetLecturer) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, resultType.toString());
                switch (resultType) {
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case SUCCESS:
                    case FAILED:
                    case UNKNOWN:
                        Log.w(TAG,"onSetLecturerResult "+resultType);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onQueryMeetingRoomInfoResult(final ResultType resultType, final QueryMeetingRoomResult queryMeetingRoomResult) {
        Log.i(TAG, "onQueryMeetingRoomInfoResult resultType = "+ resultType+",MeetingRoomResult = " + queryMeetingRoomResult);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            handleQueryMeetingRoomInfoResult(queryMeetingRoomResult);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                        default:
                            Log.e(TAG, "QueryMeetingRoomInfoResult failed:" + resultType);
                    }
                }
            }
        });
    }

    @Override
    public void onQueryMeetingInfoResult(final ResultType resultType, final QueryMeetingInfoResult queryMeetingInfoResult){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onQueryMeetingInfoResult:"+resultType.toString() + queryMeetingInfoResult);
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            handleQueryMeetingInfoResult(queryMeetingInfoResult);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                        default:
                            Log.e(TAG, "onQueryMeetingInfoResult failed:" + resultType);
                    }
                }
            }
        });
    };

    @Override
    public void onStartOverlay(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onStartOverlay:"+resultType);
                switch (resultType) {
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case SUCCESS:
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onStopOverlay(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onStopOverlay:"+resultType);
                switch (resultType) {
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case SUCCESS:
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onDisconnectParticipants(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onDisconnectParticipants:"+resultType);
                switch (resultType) {
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case SUCCESS:
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onDisconnectAllParticipants(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onDisconnectAllParticipants:"+resultType);
                switch (resultType) {
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case SUCCESS:
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onStartRecording(final ResultType resultType, final String errorCode){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onStartRecording:"+resultType);
                switch (resultType) {
                    case SUCCESS:
                        break;
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onStopRecording(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onStopRecording:"+resultType);
                switch (resultType) {
                    case SUCCESS:
                        break;
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onStartLive(final ResultType resultType, final String errorCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onStartLive:"+resultType);
                switch (resultType) {
                    case SUCCESS:
                        break;
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onStopLive(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onStopLive:"+resultType);
                switch (resultType) {
                    case SUCCESS:
                        break;
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onUnMuteSelfResult(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onUnMuteSelfResult:"+resultType);
                switch (resultType) {
                    case SUCCESS:
                        break;
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onAllowUnmuteResult(ResultType resultType, List<String> participants) {

    }

    @Override
    public void onPinForMeeting(final ResultType resultType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onPinForMeeting:"+resultType);
                switch (resultType) {
                    case SUCCESS:
                        break;
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public void onUnPinForMeeting(final ResultType resultType){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "onUnPinForMeeting:"+resultType);
                switch (resultType) {
                    case SUCCESS:
                        break;
                    case UNAUTHORIZED:
                        handleSignInUnauthorized();
                        break;
                    case CONNECTION_FAILED:
                    case FAILED:
                    case UNKNOWN:
                        break;
                    default:
                }
            }
        });
    }


    @Override
    public void onSignInResult(final ResultType resultType, final SignInResult signInResult) {
        Log.i(TAG, "onSignInResult resultType = "+ resultType);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            handleSignInResult(signInResult);
                            queryMeetingRooms();
                            queryScheduledMeetingList(1, 500);
                            findUserList(1, 50, "");
                            localStore.setUserFilter("");
                            localStore.setLastUserName(localStore.getUserName());
                            saveUserNameAndPassword();
                            frtcCall.startMeetingReminderTimer(meetingReminderTimerId, 60*1000, true);
                            replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER);
                            break;
                        case UNAUTHORIZED:
                            String errorCode = signInResult.getErrorCode();
                            cleanSignInfo();
                            switch (errorCode) {
                                case "0x00003000":
                                    showSignInUnauthorizedOtherNotice();
                                    if (isConnectingFragmentVisible()) {
                                        replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
                                    }
                                    break;
                                case "0x00003001":
                                    showSignInUnauthorizedMultipleTimesDlg();
                                    break;
                                case "0x00003002":
                                    showAccountLockedDlg();
                                    break;
                                case "0x00003003":
                                    showSignInUnauthorizedNotice();
                                    if (isConnectingFragmentVisible()) {
                                        replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
                                    }
                                    break;
                                default:
                                    showSignInUnauthorizedOtherNotice();
                                    Log.i(TAG, "onSignInResult:" + resultType + "," + signInResult.getErrorCode());
                                    break;

                            }
                            break;
                        case CONNECTION_FAILED:
                            cleanSignInfo();
                            showConnectionErrorNotice();
                            if (isConnectingFragmentVisible()) {
                                replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
                            }
                            break;
                        case FAILED:
                        case UNKNOWN:
                            Log.i(TAG, "onSignInResult resultType = " + resultType);
                            cleanSignInfo();
                            if (isConnectingFragmentVisible()) {
                                replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_HOME);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onCreateScheduledMeetingResult(final ResultType resultType, final CreateScheduledMeetingResult createScheduledMeetingResult){
        Log.i(TAG, "onCreateScheduledMeetingResult resultType = "+ resultType+",Result = " + createScheduledMeetingResult);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            Log.d(TAG,"will resetScheduledMeetingSetting.");
                            localStore.resetScheduledMeetingSetting();
                            LocalStoreBuilder.getInstance(getApplicationContext()).setLocalStore(localStore);
                            queryScheduledMeetingList(1, 500);
                            showInvitationInfoDlg(createScheduledMeetingResult);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            Log.e(TAG, "onCreateScheduledMeetingResult failed:" + resultType);
                            BaseToast.showToast(MainActivity.this, getString(R.string.scheduled_meeting_failed_notice), Toast.LENGTH_SHORT);
                            break;
                        default:
                    }
                }
            }
        });
    }
    @Override
    public void onUpdateScheduledMeetingResult(final ResultType resultType){
        Log.i(TAG, "onUpdateScheduledMeetingResult resultType = "+ resultType);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER);
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            queryScheduledMeetingList(1, 500);
                            queryScheduledRecurrenceMeetingList();
                            BaseToast.showToast(MainActivity.this, getString(R.string.scheduled_meeting_update_notice), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            Log.e(TAG, "onUpdateScheduledMeetingResult failed:" + resultType);
                            BaseToast.showToast(MainActivity.this, getString(R.string.scheduled_meeting_update_failed_notice), Toast.LENGTH_SHORT);
                            break;
                        default:
                    }
                }
            }
        });
    }
    @Override
    public void onDeleteScheduledMeetingResult(final ResultType resultType){
        Log.i(TAG, "onDeleteScheduledMeetingResult resultType = "+ resultType);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            BaseFragment fragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_SCHEDULE_RECURRENCE_MEETING_LIST);
                            if(fragment instanceof ScheduleRecurrenceMeetingListFragment && fragment.isVisible()){
                                String gid = ((ScheduleRecurrenceMeetingListFragment)fragment).getMeetingRecurrenceGid();
                                isGetScheduleMeetings = true;
                                queryScheduledRecurrenceMeetingList(1, 500, gid);
                            }else{
                                queryScheduledMeetingList(1, 500);
                            }
                            BaseToast.showToast(MainActivity.this, getString(R.string.scheduled_meeting_cancel_notice), Toast.LENGTH_SHORT);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            Log.e(TAG, "onDeleteScheduledMeetingResult failed:" + resultType);
                            BaseToast.showToast(MainActivity.this, getString(R.string.scheduled_meeting_cancel_failed_notice), Toast.LENGTH_SHORT);
                            break;
                        default:

                    }
                }
            }
        });
    }
    @Override
    public void onGetScheduledMeetingResult(final ResultType resultType, final ScheduledMeetingResult scheduledMeetingResult){
        Log.i(TAG, "onGetScheduledMeetingResult resultType = "+ resultType);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            handleGetScheduledMeetingResult(scheduledMeetingResult);

                            BaseFragment detailsFragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_DETAILS);
                            if(detailsFragment instanceof ScheduledMeetingDetailsFragment
                                    && detailsFragment.isVisible()){
                                ((ScheduledMeetingDetailsFragment)detailsFragment).updateMeetingDetailsView();
                            }

                            BaseFragment editFragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_EDIT_SINGLE_RECURRENCE_MEETING);
                            if(editFragment instanceof EditSingleRecurrenceMeetingFragment && editFragment.isVisible()){
                                ((EditSingleRecurrenceMeetingFragment)editFragment).updateRecurrenceText();
                            }

                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            Log.e(TAG, "onGetScheduledMeetingResult failed:" + resultType);
                            break;
                        default:
                    }
                }
            }
        });
    }
    @Override
    public void onGetScheduledMeetingListResult(final ResultType resultType, final ScheduledMeetingListResult scheduledMeetingListResult){
        Log.i(TAG, "onGetScheduledMeetingListResult resultType = "+ resultType);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            if(isGetScheduleMeetings){
                                BaseToast.showToast(MainActivity.this,getString( R.string.text_scheduled_meeting_refresh_success), Toast.LENGTH_SHORT);
                            }
                            if(scheduledMeetingListResult == null){
                                isGetScheduleMeetings = false;
                                break;
                            }
                            handleQueryScheduledMeetingList(scheduledMeetingListResult.getMeeting_schedules(),
                                    scheduledMeetingListResult.getTotal_page_num(),
                                    scheduledMeetingListResult.getTotal_size());
                            addMeetingReminder();
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            isGetScheduleMeetings = false;
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            isGetScheduleMeetings = false;
                            break;
                        case FAILED:
                        case UNKNOWN:
                            Log.e(TAG, "onGetScheduledMeetingListResult failed resultType = " + resultType);
                            isGetScheduleMeetings = false;
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onGetScheduledRecurrenceMeetingListResult(final ResultType resultType, final RecurrenceMeetingListResult recurrenceMeetingListResult){
        Log.i(TAG, "onGetScheduledRecurrenceMeetingListResult resultType = "+ resultType);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:

                            if(recurrenceMeetingListResult == null || recurrenceMeetingListResult.getTotal_size() == 0){
                                BaseToast.showToast(MainActivity.this,getString( R.string.get_recurrence_meetings_fail), Toast.LENGTH_SHORT);
                                break;
                            }

                            /*
                            BaseFragment scheduledMeetingDetailsFragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_MEETING_DETAILS);
                            if(isWillShowScheduledRecurrenceMeetingList) {

                                BaseFragment fragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_SCHEDULE_RECURRENCE_MEETING_LIST);
                                if(fragment instanceof ScheduleRecurrenceMeetingListFragment && fragment.isVisible()){
                                    ((ScheduleRecurrenceMeetingListFragment)fragment).setScheduledMeetingListResult(recurrenceMeetingListResult, true);;
                                }else{
                                    if(recurrenceMeetingListResult.getTotal_size() == 0){
                                        BaseToast.showToast(MainActivity.this,getString( R.string.get_recurrence_meetings_fail), Toast.LENGTH_SHORT);
                                    }else {
                                        showScheduleRecurrenceMeetingListFragment(recurrenceMeetingListResult);
                                    }
                                }
                            }else if(scheduledMeetingDetailsFragment instanceof ScheduledMeetingDetailsFragment && scheduledMeetingDetailsFragment.isVisible()){
                                if(((ScheduledMeetingDetailsFragment)scheduledMeetingDetailsFragment).isAddCalendar()) {
                                    ((ScheduledMeetingDetailsFragment)scheduledMeetingDetailsFragment).saveRecurrenceCalendar();
                                }
                            }else{
                                showMeetingDetails(scheduledMeetingShowing, 0, recurrenceMeetingListResult);
                            }
                             */
                            handleQueryScheduledRecurrenceMeetingList(recurrenceMeetingListResult);

                            if(localStore.getScheduledMeetingSetting().isRecurrenceMeetingFullList()){
                                BaseFragment scheduleRecurrenceMeetingListFragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_SCHEDULE_RECURRENCE_MEETING_LIST);
                                if(scheduleRecurrenceMeetingListFragment instanceof ScheduleRecurrenceMeetingListFragment
                                        && scheduleRecurrenceMeetingListFragment.isVisible()){
                                    ((ScheduleRecurrenceMeetingListFragment)scheduleRecurrenceMeetingListFragment).updateScheduledRecurrenceMeetingListview();
                                }

                                BaseFragment scheduledMeetingDetailsFragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_DETAILS);
                                if(scheduledMeetingDetailsFragment instanceof ScheduledMeetingDetailsFragment
                                        && scheduledMeetingDetailsFragment.isVisible()){
                                    ((ScheduledMeetingDetailsFragment)scheduledMeetingDetailsFragment).updateRecurrenceView();
                                    if(((ScheduledMeetingDetailsFragment)scheduledMeetingDetailsFragment).isAddCalendar()){
                                        ((ScheduledMeetingDetailsFragment)scheduledMeetingDetailsFragment).saveRecurrenceCalendar();
                                    }
                                }
                            }
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            Log.e(TAG, "onGetScheduledRecurrenceMeetingListResult failed resultType = " + resultType);
                            break;
                        default:
                    }
                }
                isGetScheduleMeetings = false;
            }
        });
    }

    @Override
    public void onFindUserResultResult(final ResultType resultType, final FindUserResult findUserResult){
        Log.i(TAG, "onFindUserResultResult resultType = "+ resultType + ", findUserResult = "+findUserResult);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            if(findUserResult == null){
                                return;
                            }
                            if (findUserResult.getUsers() != null) {

                            }
                            handleFindUserResult(findUserResult.getUsers(), findUserResult.getTotal_page_num(), findUserResult.getTotal_size());
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            Log.e(TAG, "onFindUserResultResult failed:" + resultType);
                            break;
                        default:
                    }
                }
            }
        });
    }

    @Override
    public void onFrtcSdkLeaveMeetingNotify() {

    }

    private void onServerCommonError(ResultType resultType){
        switch (resultType) {
            case COMMON_ERROR:
                BaseToast.showToast(MainActivity.this,getString( R.string.server_common_error), Toast.LENGTH_SHORT);
                break;
            case PARAMETERS_ERROR:
                BaseToast.showToast(MainActivity.this,getString( R.string.server_parameters_error), Toast.LENGTH_SHORT);
                break;
            case AUTHORIZATION_ERROR:
                BaseToast.showToast(MainActivity.this,getString( R.string.server_authorization_error), Toast.LENGTH_SHORT);
                break;
            case PERMISSION_ERROR:
                BaseToast.showToast(MainActivity.this,getString( R.string.server_permission_error), Toast.LENGTH_SHORT);
                break;
            case MEETING_NOT_EXIST:
                BaseToast.showToast(MainActivity.this,getString( R.string.server_meeting_not_exist), Toast.LENGTH_SHORT);
                break;
            case MEETING_DATA_ERROR:
                BaseToast.showToast(MainActivity.this,getString( R.string.server_meeting_data_error), Toast.LENGTH_SHORT);
                break;
            case OPERATION_FORBIDDEN:
                BaseToast.showToast(MainActivity.this,getString( R.string.server_operation_forbidden), Toast.LENGTH_SHORT);
                break;
            case MEETING_STATUS_ERROR:
                BaseToast.showToast(MainActivity.this,getString( R.string.server_meeting_status_error), Toast.LENGTH_SHORT);
                break;
            case RECORDING_STREAMING_SERVICE_ERROR:
                BaseToast.showToast(MainActivity.this,getString( R.string.recording_server_error), Toast.LENGTH_SHORT);
                break;
            case LENGTH_ERROR:
                BaseToast.showToast(MainActivity.this,getString( R.string.server_length_error), Toast.LENGTH_SHORT);
                break;
            case FORMAT_ERROR:
                BaseToast.showToast(MainActivity.this,getString( R.string.server_format_error), Toast.LENGTH_SHORT);
                break;
            case LICENSE_ERROR:
                BaseToast.showToast(MainActivity.this,getString( R.string.server_license_error), Toast.LENGTH_SHORT);
                break;
            default:
        }
    }



    public void showOpenCameraInformDlg(){
        String tipFormat = getResources().getString(R.string.msg_qrscan_camera_permission_request);
        String tip = String.format(tipFormat, getResources().getString(R.string.app_name));
        InformDlg informDlg = new InformDlg(this,"",tip, getString(R.string.dialog_positive_btn));

        informDlg.setConfirmDlgListener(new IConfirmDlgListener() {
            @Override
            public void onConfirm() {
                Uri packageURI = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                startActivity(intent);
            }
            @Override
            public void onCancel(){

            }
        });
        informDlg.show();
    }


    private void addMeetingReminder() {
        Log.d(TAG,"addMeetingReminder");

        ArrayList<ScheduledMeeting> scheduledMeetings = localStore.getScheduledMeetings();

        if (scheduledMeetings != null) {
            final ArrayList<ScheduledMeeting> scheduledMeetingsReminders = new ArrayList<>();
            long startTimeMinPre = 0;
            long timeDiffPre= 0;
            try {
                for (int i = 0; i < scheduledMeetings.size(); i++) {
                    ScheduledMeeting meetingCall = scheduledMeetings.get(i);
                    String startTimeStr = meetingCall.getSchedule_start_time();
                    long startTime = Long.parseLong(startTimeStr);
                    long startTimeMin = Long.parseLong(startTimeStr) / 1000 / 60 * 60 * 1000;
                    long currentTimeMin = System.currentTimeMillis() / 1000 / 60 * 60 * 1000;
                    long timeDiff = startTimeMin - currentTimeMin;
                    if(timeDiff < 0){
                        continue;
                    }
                    if (timeDiff >= 0 && timeDiff <= 5 * 60 * 1000) {
                        String meetingNum = meetingCall.getMeeting_number();
                        Log.d(TAG, "meetingReminder  meetingNum = "+ meetingNum + ",  startTimeMin = " + startTimeMin + ",  startTimeMinPre =" + startTimeMinPre);
                        if (scheduledMeetingsReminders.size() == 0 || (i < (scheduledMeetings.size() - 1) && startTimeMin == startTimeMinPre)) {
                           if ((localStore.getMapMeetingStartTime(meetingNum) == startTimeMin)
                                   || meetingCall.getMeeting_type().equals("instant")
                                   || (frtcCall.getMeetingStatus() == FrtcMeetingStatus.CONNECTED && meetingNum.equals(localStore.getMeetingID()))) {
                               if(frtcCall.getMeetingStatus() == FrtcMeetingStatus.CONNECTED && meetingNum.equals(localStore.getMeetingID()) && localStore.getMapMeetingStartTime(meetingNum) ==-1){
                                   String startTStr = meetingCall.getSchedule_start_time();
                                   long startTMin = Long.parseLong(startTStr) / 1000 / 60 * 60 * 1000;
                                   localStore.addMeetingReminder(meetingNum, startTMin);
                               }
                                if (i > 0) {
                                    continue;
                                }
                            } else {
                                Log.d(TAG, "meetingReminder add i = " + i + ",  meetingnumber =" + Integer.parseInt(meetingNum));
                                scheduledMeetingsReminders.add(meetingCall);
                                startTimeMinPre = startTimeMin;
                                timeDiffPre = timeDiff;
                                if (i > 0) {
                                    continue;
                                }
                            }
                        }
                    }
                    Log.d(TAG, "meetingReminder  scheduledMeetingsReminders.size() = " + scheduledMeetingsReminders.size());
                    if (scheduledMeetingsReminders.size() > 0) {
                        Log.d(TAG, "meetingReminder  timeDiffPre = " + timeDiffPre);
                        showMeetingReminder(scheduledMeetingsReminders, (timeDiffPre < 5 * 60 * 1000 && i > 0) ? true : false);
                        break;
                    }
                    if(timeDiff > 5 * 60 * 1000){
                        break;
                    }
                }
            }catch (Exception e) {
                Log.e(TAG,"addMeetingReminder",e);
            }
        }
    }

    private boolean isOtherUIExisting(Context context) {
        boolean existing = false;
        try {
            String currClassName = getClass().getName();
            String currPackageName = getPackageName();
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(10);
            if (list.isEmpty()) {
                existing = false;
            }
            for (ActivityManager.RunningTaskInfo info : list) {
                String activityName = info.baseActivity.getClassName();
                if (activityName.equals(currClassName)) {
                    continue;
                }
                if (info.baseActivity.getPackageName().equals(currPackageName)) {
                    existing = true;
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"isOtherUIExisting failed "+e.toString());
            existing = false;
        }
        return existing;
    }

    private void showMeetingReminder(ArrayList<ScheduledMeeting> scheduledMeetingsReminders, boolean isLessFive) {
        Log.d(TAG, "showMeetingReminder localStore.isMeetingReminder() = "+localStore.isMeetingReminder() + ",  isLessFive = "+isLessFive);
        if(!localStore.isMeetingReminder()){
            return;
        }
        for(ScheduledMeeting meeting : scheduledMeetingsReminders) {
            String startTStr = meeting.getSchedule_start_time();
            long startTMin = Long.parseLong(startTStr) / 1000 / 60 * 60 * 1000;
            localStore.addMeetingReminder(meeting.getMeeting_number(), startTMin);
        }
        if(!ActivityUtils.isExistMeetingActivity()) {
            if (meetingReminderDlg != null && meetingReminderDlg.isShowing()) {
                meetingReminderDlg.dismiss();
            }
            meetingReminderDlg = new MeetingReminderDlg(this, scheduledMeetingsReminders);
            meetingReminderDlg.setMeetingReminderListener(new IMeetingReminderListener() {
                @Override
                public void onIgnoreCallback() {
                    Log.d(TAG, "onIgnoreCallback");
                }

                @Override
                public void onJoinMeetingCallback(ScheduledMeeting scheduledMeeting) {
                    Log.d(TAG, "onJoinMeetingCallback");
                    if(isInMeeting()){
                        return;
                    }
                    if (!isNetworkConnected()) {
                        showConnectionErrorNotice();
                        return;
                    }
                    joinMeeting(scheduledMeeting.getMeeting_number(),
                            scheduledMeeting.getMeeting_password(), localStore.getDisplayName(), "",scheduledMeeting.getMeeting_type());
                    StartMeetingActivity();
                }

            });
            meetingReminderDlg.show();
        }else{
            frtcCall.showMeetingReminder(scheduledMeetingsReminders);
        }
        Log.d(TAG, "showNotification scheduledMeetingsReminders.size() = "+ scheduledMeetingsReminders.size());
        int notifyId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        for(ScheduledMeeting meeting : scheduledMeetingsReminders) {
            showNotification(this, meeting, notifyId);
            notifyId = notifyId +1;
        }
        if(isLessFive){
            addMeetingReminder();
        }
    }

    private void showNotification(Context context, ScheduledMeeting meetingCall, int notifyId) {
        Log.d(TAG, "showNotification");
        String startTimeStr = meetingCall.getSchedule_start_time();
        String endTimeStr = meetingCall.getSchedule_end_time();
        long startTime = Long.parseLong(startTimeStr);
        long endTime = Long.parseLong(endTimeStr);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormatStart = new SimpleDateFormat("MM/dd HH:mm");
        SimpleDateFormat timeFormatEnd = new SimpleDateFormat("HH:mm");
        Date dateStart = new Date(startTime);
        Date dateEnd = new Date(endTime);
        String strTime = timeFormatStart.format(dateStart) + "-" + timeFormatEnd.format(dateEnd);
        Log.d(TAG, "strTime = "+strTime);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, TAG, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);

        Intent intent = new Intent(this, MeetingReminderActivitiy.class);
        intent.setAction(MEETING_REMINDER_NOTIFICATION);
        intent.putExtra("scheduleMeeting",new Gson().toJson(meetingCall));
        intent.putExtra("isClickNotification",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Integer.parseInt(meetingCall.getMeeting_number()), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(meetingCall.getMeeting_name())
                .setContentText(getString(R.string.meeting_time_label) + strTime)
                .setAutoCancel(true)
                .setShowWhen(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        Log.d(TAG, "notifyId = "+notifyId + ",  new Date().getTime() = "+new Date().getTime());
        notificationManager.notify(notifyId, notification);
    }

    private int timerCount = 0;
    private long timePre = 0;
    private boolean isFirst = true;
    @Override
    public void onTimerEvent(int timerId) {
        if(localStore.isLogged()) {
            timerCount++;
            if(isFirst) {
                isFirst = false;
                timePre = System.currentTimeMillis() / 1000 / 60 * 60 * 1000;
            }
            long time = System.currentTimeMillis() / 1000 / 60 * 60 * 1000;
            Log.d(TAG, "onTimerEvent time = " + time + ", timePre = " + timePre);
            if (time - timePre >= 15 * 60 * 1000) {
                timePre = time;
                if(!isGetScheduleMeetings) {
                    queryScheduledMeetingList(1, 500);
                }
            } else if(time - timePre >= 1 * 60 * 1000){
                addMeetingReminder();
            }
        }
    }

    private BaseFragment getFragmentByTag(FragmentTagEnum tag){
        BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
                .findFragmentByTag(tag.getTypeName());
        return fragment;
    }

    @Override
    public void onUploadLogsNotify(int tractionId) {
        this.mTractionId = tractionId;
        Log.d(TAG, "onUploadLogsNotify mTractionId =  " + mTractionId );
        BaseFragment fragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_UPLOAD_LOG);
        if(fragment instanceof UploadLogFragment){
            ((UploadLogFragment) fragment).setTransId(mTractionId);
            ((UploadLogFragment) fragment).setFileType(0);
            ((UploadLogFragment) fragment).getUploadStatus();
        }
    }

    @Override
    public void onUploadLogsStatusNotify(UploadLogsStatus uploadLogsStatus) {
        Log.d(TAG, "onUploadLogsStatusNotify progress : " + uploadLogsStatus.progress);
        BaseFragment fragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_UPLOAD_LOG);
        if(fragment instanceof UploadLogFragment){
            if((uploadLogsStatus.progress == 100 && ((UploadLogFragment)fragment).getFileType() == 2) || uploadLogsStatus.progress < 0){
                mTractionId = -1;
                Log.d(TAG, "onUploadLogsStatusNotify mTractionId = " + mTractionId );
                ((UploadLogFragment)fragment).setTransId(mTractionId);
            }
            ((UploadLogFragment)fragment).setUploadStatus(uploadLogsStatus);
        }
    }

    @Override
    public void onCancelUploadLogsNotify() {
        Log.d(TAG, "onCancelUploadLogsNotify mTractionId = " + mTractionId );
        BaseFragment fragment = getFragmentByTag(FragmentTagEnum.FRAGMENT_UPLOAD_LOG);
        if(fragment instanceof UploadLogFragment){
            ((UploadLogFragment) fragment).reSetParams();
        }
        showCancelUploadNotice();
        replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_PROBLEM_DIAGNOSIS);
    }

    @Override
    public void onSaveMeetingIntoMeetingListNotify() {

    }

    @Override
    public void onShowMeetingReminder(ArrayList<ScheduledMeeting> scheduledMeetingsReminders) {

    }

    public class ClickNotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive: action: " + action);
        }
    }

    public void showUploadLogFragment(String issue) {
        UploadLogFragment uploadLogFragment = new UploadLogFragment();
        uploadLogFragment.setStrIssue(issue);
        replaceFragmentWithInstance(uploadLogFragment, FragmentTagEnum.FRAGMENT_UPLOAD_LOG);
    }

    public void startUploadLogs(String strMetaData, String fileName, int count) {
        frtcCall.startUploadLogs(strMetaData, fileName, count);
    }

    public void getUploadStatus(int fileType) {
        Log.d(TAG, "getUploadStatus mTractionId : " + mTractionId);
        frtcCall.getUploadStatus(mTractionId, fileType);
    }

    public void cancelUploadLogs() {
        if(mTractionId == -1) {
            return;
        }
        frtcCall.cancelUploadLogs(mTractionId);
    }

    public void showCancelUploadNotice(){
        BaseToast.showToast(this, getString(R.string.log_upload_canceled), Toast.LENGTH_SHORT);
    }

    public boolean isInMeeting(){
        FrtcMeetingStatus meetingStatus = frtcCall.getMeetingStatus();
        if(meetingStatus != FrtcMeetingStatus.DISCONNECTED){
            BaseToast.showToast(this, getString(R.string.meeting_system_notification_click_prompt), Toast.LENGTH_LONG);
            return true;
        }
        return false;
    }

    public void showScheduleMeetingRepetitionFreqSetting(String freqType, boolean isUpdateRecurrence){
        Bundle bundle = new Bundle();
        bundle.putString("freqType",freqType);
        bundle.putBoolean("isUpdateRecurrence", isUpdateRecurrence);
        ScheduleMeetingRepetitionFreqSettingFragment fragment = new ScheduleMeetingRepetitionFreqSettingFragment();
        fragment.setArguments(bundle);
        replaceFragmentWithInstance(fragment, FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_REPETITION_FREQ_SETTING);
    }

    private void showInvitationInfoDlg(CreateScheduledMeetingResult createScheduledMeetingResult){
        InvitationInformationDlg invitationInformationDlg = new InvitationInformationDlg(this,this, createScheduledMeetingResult);
        invitationInformationDlg.show();
    }

    public void showEditSingleRecurrenceMeeting(int position) {
        EditSingleRecurrenceMeetingFragment fragment = new EditSingleRecurrenceMeetingFragment();
        fragment.setPosition(position);
        replaceFragmentWithInstance(fragment, FragmentTagEnum.FRAGMENT_EDIT_SINGLE_RECURRENCE_MEETING);
    }

    public void showScheduleMeetingRepetitionFreqFragment(boolean updateRecurrence) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isUpdateRecurrence", updateRecurrence);
        ScheduleMeetingRepetitionFreqFragment fragment = new ScheduleMeetingRepetitionFreqFragment();
        fragment.setArguments(bundle);
        replaceFragmentWithInstance(fragment,FragmentTagEnum.FRAGMENT_SCHEDULE_MEETING_REPETITION_FREQ);
    }

    @Override
    public void onAddMeetingIntoMeetingListResult(ResultType resultType) {
        Log.d(TAG,"onAddMeetingIntoMeetingListResult:"+resultType);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            BaseToast.showToast(MainActivity.this, getString(R.string.added_to_meeting_list_successfully), Toast.LENGTH_SHORT);
                            Log.d(TAG,"onAddMeetingIntoMeetingListResult isGetScheduleMeetings:"+isGetScheduleMeetings);
                            queryScheduledMeetingList(1, 500);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(MainActivity.this, getString(R.string.adding_to_meeting_list_failed), Toast.LENGTH_SHORT);
                            Log.e(TAG, "onAddMeetingIntoMeetingListResult failed:" + resultType);
                            break;
                        default:
                            break;
                    }
                }
            }
        });

    }

    @Override
    public void onRemoveMeetingFromMeetingList(ResultType resultType) {
        Log.d(TAG,"onRemoveMeetingFromMeetingList:"+resultType);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultType.ordinal() >= 3 && resultType.ordinal() <= 14){
                    onServerCommonError(resultType);
                }else {
                    switch (resultType) {
                        case SUCCESS:
                            BaseToast.showToast(MainActivity.this, getString(R.string.remove_from_meeting_list_successfully), Toast.LENGTH_SHORT);
                            queryScheduledMeetingList(1, 500);
                            replaceFragmentWithTag(FragmentTagEnum.FRAGMENT_USER);
                            break;
                        case UNAUTHORIZED:
                            handleSignInUnauthorized();
                            break;
                        case CONNECTION_FAILED:
                            showConnectionErrorNotice();
                            break;
                        case FAILED:
                        case UNKNOWN:
                            BaseToast.showToast(MainActivity.this, getString(R.string.remove_from_meeting_list_successfully), Toast.LENGTH_SHORT);
                            Log.e(TAG, "onRemoveMeetingFromMeetingList failed:" + resultType);
                            break;
                        default:
                            break;
                    }
                }
            }
        });

    }

    public void removeMeetingFromMeetingList(String meetingUrl) {
        Log.d(TAG,"removeMeetingFromMeetingList meetingUrl= "+meetingUrl);
        if(TextUtils.isEmpty(meetingUrl)){
            return;
        }
        String meetingIdentifier = meetingUrl.substring(meetingUrl.lastIndexOf("/") + 1);
        AddMeetingToListParam param = new AddMeetingToListParam();
        param.setServerAddress(localStore.getServer());
        param.setClientId(localStore.getClientId());
        param.setToken(localStore.getUserToken());
        param.setMeeting_identifier(meetingIdentifier);
        frtcManagement.removeMeetingFromMeetingList(param);
    }


}
