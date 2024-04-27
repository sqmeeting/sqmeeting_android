package frtc.sdk;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import frtc.sdk.model.InitParams;
import frtc.sdk.model.JoinMeetingOption;
import frtc.sdk.model.JoinMeetingParam;
import frtc.sdk.model.LiveRecordStatusNotify;
import frtc.sdk.model.MeetingStateInfoNotify;
import frtc.sdk.model.OverlayNotify;
import frtc.sdk.model.MuteControlStateNotify;
import frtc.sdk.model.Participant;
import frtc.sdk.model.ParticipantNumberChangeNotify;
import frtc.sdk.model.ParticipantStateChangeNotify;
import frtc.sdk.model.SignInParam;
import frtc.sdk.internal.model.LecturerInfo;
import frtc.sdk.model.UnmuteRequest;
import frtc.sdk.internal.model.MeetingJoinInfo;
import frtc.sdk.internal.model.MeetingMediaStatsWrapper;
import frtc.sdk.internal.model.FrtcMeetingStatus;
import frtc.sdk.internal.model.MeetingStateNotify;
import frtc.sdk.log.CrashHandler;
import frtc.sdk.log.Log;
import frtc.sdk.model.ParticipantInfo;
import frtc.sdk.ui.FrtcMeetingActivity;
import frtc.sdk.ui.model.LocalStore;
import frtc.sdk.ui.store.LocalStoreBuilder;
import frtc.sdk.util.Constants;

public class FrtcSdk implements IFrtcCallListener {
    protected final String TAG = this.getClass().getSimpleName();
    private static FrtcSdk instance = null;
    private Context mContext = null;
    public static FrtcCall frtcCall;
    public static FrtcManagement frtcManagement;
    private static LocalStore localStore;
    private IMeetingStatusListener meetingStatusListener;
    private List<ParticipantInfo> participantInfos = new ArrayList<>();

    String version = "3.4.0.504";

    private FrtcSdk() {

    }

    public static synchronized FrtcSdk getInstance() {
        if (instance == null) {
            synchronized (FrtcSdk.class) {
                if (instance == null) {
                    instance = new FrtcSdk();
                }
            }
        }
        return instance;
    }

    public void initialize(Context context){
        this.mContext = context;
        CrashHandler.getInstance().initCrashHandler(context);
        Log.getInstance().init(context);
        localStore = LocalStoreBuilder.getInstance(context).getLocalStore();
        String clientId = localStore.getClientId();
        frtcCall = FrtcCall.getInstance();
        frtcCall.initialize(context, new InitParams(clientId));
        frtcManagement = FrtcManagement.getInstance();
        frtcManagement.initialize(context, new InitParams(clientId));
    }


    public void joinMeeting(MeetingJoinInfo meetingJoinInfo, IMeetingStatusListener meetingStatusListener){
        this.meetingStatusListener = meetingStatusListener;
        frtcCall.registerCallListener(this);
        frtcCall.setServerAddress(meetingJoinInfo.getServer());
        frtcManagement.setServerAddress(meetingJoinInfo.getServer());
        localStore.setServer(meetingJoinInfo.getServer());
        joinMeeting(meetingJoinInfo);
        startupActivity(meetingJoinInfo.isFloatWindow(), meetingJoinInfo.isScreenShare());
    }
    public void joinMeeting(MeetingJoinInfo meetingJoinInfo) {
        String userToken = meetingJoinInfo.getUserToken();
        String userId =meetingJoinInfo.getUserId();
        String meetingNumber= meetingJoinInfo.getMeetingNumber();
        String meetingPwd = meetingJoinInfo.getMeetingPwd();
        String displayName = meetingJoinInfo.getDisplayName();
        String meetingOwnerId = meetingJoinInfo.getMeetingOwnerId();
        boolean audioOnly = meetingJoinInfo.isAudioOnly();
        boolean muteAudio = meetingJoinInfo.isMuteAudio();
        boolean muteVideo = meetingJoinInfo.isMuteVideo();
        String userRole = meetingJoinInfo.getUserRole();
        JoinMeetingParam joinMeetingParam = new JoinMeetingParam();

        if (userToken == null) {
            joinMeetingParam.setToken("");
            localStore.setUserToken("");
        }else{
            joinMeetingParam.setToken(userToken);
            localStore.setUserToken(userToken);
        }

        joinMeetingParam.setMeetingNumber(meetingNumber);
        joinMeetingParam.setDisplayName(displayName);
        joinMeetingParam.setMeetingPassword(meetingPwd);
        joinMeetingParam.setUserId(userId);
        joinMeetingParam.setClientId(getClientId());

        JoinMeetingOption option = new JoinMeetingOption();
        option.setNoAudio(muteAudio);
        option.setNoVideo(muteVideo);
        option.setAudioOnly(audioOnly);
        option.setCallRate(audioOnly ? 64 : 0);
        frtcCall.joinMeetingWithParam(joinMeetingParam, option);

        localStore.setAudioOn(!muteAudio);
        localStore.setCameraOn(!muteVideo);
        localStore.setAudioCall(audioOnly);
        localStore.setUserId(userId);
        localStore.setMeetingPassword(meetingPwd);
        localStore.setMeetingOwnerId(meetingOwnerId);
        List<String> role = new ArrayList<>();
        role.add(userRole);
        localStore.setRole(new HashSet<>(role));

        localStore.setDisplayName(displayName);
        localStore.setRealName(displayName);
        localStore.setSdkType(Constants.SdkType.SDK_TYPE_SQ);

        LocalStoreBuilder.getInstance(mContext).setLocalStore(localStore);
    }

    private void startupActivity(boolean floatWindow, boolean screenShare) {
        Intent intent = new Intent();
        intent.setClass(mContext, FrtcMeetingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isScreenShare",screenShare);
        intent.putExtra("isFloatWindow",floatWindow);
        mContext.startActivity(intent);
    }

    public void setNoiseBlock(boolean enabled){
        frtcCall.setNoiseBlock(enabled);
    }

    public void terminate(){
        if (frtcCall != null) {
            frtcCall.close();
        }
        if(frtcManagement != null) {
            frtcManagement.shutdown();
        }
    }

    public String getVersion() {
        return version;
    }

    public String getClientId(){
        if(localStore == null) {
            localStore = LocalStoreBuilder.getInstance(mContext).getLocalStore();
        }
        return localStore.getClientId();
    }

    @Override
    public void onMeetingInfoNotify(MeetingStateInfoNotify meetingStateInfoNotify) {

    }

    @Override
    public void onMeetingStateNotify(MeetingStateInfoNotify meetingStateInfoNotify) {
        if(meetingStateInfoNotify.getMeetingStatus() == FrtcMeetingStatus.DISCONNECTED){
            frtcCall.unRegisterCallListener(this);
        }
        MeetingStateNotify meetingStateNotify = new MeetingStateNotify();
        meetingStateNotify.setMeetingStatus(meetingStateInfoNotify.getMeetingStatus());
        meetingStatusListener.onMeetingStateNotify(meetingStateNotify);
    }

    @Override
    public void onMeetingPasswordErrorNotify() {

    }

    @Override
    public void onMeetingPasswordRequestNotify() {

    }

    @Override
    public void onOverlayNotify(OverlayNotify overlayNotify) {

    }

    @Override
    public void onParticipantNumberChangeNotify(ParticipantNumberChangeNotify participantNumberChangeNotify) {

    }

    @Override
    public void onParticipantStateChangeNotify(ParticipantStateChangeNotify participantStateChangeNotify) {
        if (participantStateChangeNotify.getParticipantList() == null || participantStateChangeNotify.getParticipantList().size() <= 0) {
            return;
        }
        participantInfos.clear();
        for (Participant participant : participantStateChangeNotify.getParticipantList()){
            ParticipantInfo participantInfo = new ParticipantInfo();
            participantInfo.setUserName(participant.getDisplayName());
            participantInfo.setUserId(participant.getUserId());
            participantInfos.add(participantInfo);
        }
        meetingStatusListener.onParticipantChangeNotify(participantInfos);
    }

    @Override
    public void onMuteControlStateNotify(MuteControlStateNotify muteControlStateNotify) {

    }

    @Override
    public void onTemperatureInfoNotify(boolean overheat) {

    }

    @Override
    public void onHeadsetEnableSpeakerNotify(boolean enableSpeaker) {

    }

    @Override
    public void onMediaStatsInfoNotify(MeetingMediaStatsWrapper stat, boolean isEncrypt) {

    }

    @Override
    public void onInviteUserNotify(List<frtc.sdk.internal.model.ParticipantInfo> participantInfos) {
        meetingStatusListener.onInviteUserNotify(this.participantInfos);
    }

    @Override
    public void onLayoutSettingNotify(LecturerInfo lecturerInfo) {

    }

    @Override
    public void onContentStateNotify(boolean isSharingContent) {

    }

    @Override
    public void onLiveRecordStatusNotify(LiveRecordStatusNotify notify) {

    }

    @Override
    public void onUnmuteRequestNotify(UnmuteRequest unmuteRequest) {

    }

    @Override
    public void onAllowUnmuteNotify() {

    }

    @Override
    public void onPinNotify(String pinUuid) {

    }

    @Override
    public void onNetworkInfoNotify(String networkState){

    }

    @Override
    public void onStartContentRefusedNotify(){

    }

    @Override
    public void onMicVolumeMeterNotify(int volume) {

    }

    public void signIn(SignInParam signInParam, ISignListener signListener){
        localStore.setServer(signInParam.getServerAddress());
        frtcCall.setServerAddress(signInParam.getServerAddress());
        frtcManagement.setServerAddress(signInParam.getServerAddress());
        frtcManagement.signIn(signInParam, signListener);
    }

}
