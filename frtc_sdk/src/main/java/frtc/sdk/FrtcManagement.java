package frtc.sdk;

import android.content.Context;

import frtc.sdk.model.AddMeetingToListParam;
import frtc.sdk.model.AllowUnmuteParam;
import frtc.sdk.model.ChangeDisplayNameParam;
import frtc.sdk.model.CommonMeetingParam;
import frtc.sdk.model.CreateScheduledMeetingParam;
import frtc.sdk.model.DeleteScheduledMeetingParam;
import frtc.sdk.model.DisconnectAllParticipantsParam;
import frtc.sdk.model.DisconnectParticipantParam;
import frtc.sdk.model.FindUserParam;
import frtc.sdk.model.GetScheduledMeetingListParam;
import frtc.sdk.model.GetScheduledMeetingParam;
import frtc.sdk.model.InitParams;
import frtc.sdk.model.InstantMeetingParam;
import frtc.sdk.model.LectureParam;
import frtc.sdk.conf.SDKConfigManager;
import frtc.sdk.internal.service.ManagementService;
import frtc.sdk.log.Log;
import frtc.sdk.model.LiveMeetingParam;
import frtc.sdk.model.MuteAllParam;
import frtc.sdk.model.MuteParam;
import frtc.sdk.model.PasswordUpdateParam;
import frtc.sdk.model.PinForMeetingParam;
import frtc.sdk.model.QueryMeetingInfoParam;
import frtc.sdk.model.QueryMeetingRoomParam;
import frtc.sdk.model.QueryUserInfoParam;
import frtc.sdk.model.SignInParam;
import frtc.sdk.model.SignOutParam;
import frtc.sdk.model.StartOverlayParam;
import frtc.sdk.model.StopMeetingParam;
import frtc.sdk.model.StopOverlayParam;
import frtc.sdk.model.UnMuteAllParam;
import frtc.sdk.model.UnMuteParam;
import frtc.sdk.model.UpdateScheduledMeetingParam;

public final class FrtcManagement implements IFrtcManagementAPI {
    private final String TAG = this.getClass().getSimpleName();
    private static FrtcManagement instance = null;

    private InitParams initParams = null;
    private IManagementService managementService = null;
    private Context context = null;

    private FrtcManagement() {
        Log.d(TAG,"FrtcManagement constructor");
    }

    public static synchronized FrtcManagement getInstance() {
        if (instance == null) {
            synchronized (FrtcManagement.class) {
                if (instance == null) {
                    instance = new FrtcManagement();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean initialize(Context context, InitParams initParams) {
        Log.d(TAG,"initialize:");
        this.initParams = initParams;
        if (context == null || context.getApplicationContext() == null) {
            throw new NullPointerException("context cannot be null");
        }
        if (initParams == null || initParams.getClientId() == null || initParams.getClientId().isEmpty() ) {
            throw new NullPointerException("clientId cannot be null");
        }
        SDKConfigManager.getInstance(context).setClientId(initParams.getClientId());
        this.context = context.getApplicationContext();
        managementService = new ManagementService(this);
        return true;
    }

    public boolean isiInitialized(){
        return (managementService != null);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void shutdown() {
        managementService.shutdown();
    }

    @Override
    public void signIn(SignInParam signInParam) {

    }

    @Override
    public String getSDKVersion() {
        return managementService.getSDKVersion();
    }

    @Override
    public void registerManagementListener(IFrtcManagementListener listener) {
        managementService.registerManagementListener(listener);
    }

    @Override
    public void unRegisterManagementListener(IFrtcManagementListener listener) {
        managementService.unRegisterManagementListener(listener);
    }

    @Override
    public void setServerAddress(String address) {
        SDKConfigManager.getInstance(context).setServerAddress(address);
    }

    @Override
    public String getServerAddress() {
        return SDKConfigManager.getInstance(context).getServerAddress();
    }

    public void signIn(SignInParam signInParam, ISignListener signListener) {
        managementService.signIn(signInParam, signListener);
    }

    @Override
    public void signOut(SignOutParam signOutParam) {
        managementService.signOut(signOutParam);
    }

    @Override
    public void queryUserInfo(QueryUserInfoParam queryUserInfoParam) {
        managementService.queryUserInfo(queryUserInfoParam);
    }

    @Override
    public void updatePassword(PasswordUpdateParam passwordUpdateParam) {
        managementService.updatePassword(passwordUpdateParam);
    }

    @Override
    public void createInstantMeeting(InstantMeetingParam instantMeetingParam) {
        managementService.createInstantMeeting(instantMeetingParam);
    }

    @Override
    public void createScheduledMeeting(CreateScheduledMeetingParam createScheduledMeetingParam){
        managementService.createScheduledMeeting(createScheduledMeetingParam);
    }
    @Override
    public void updateScheduledMeeting(UpdateScheduledMeetingParam updateScheduledMeetingParam){
        managementService.updateScheduledMeeting(updateScheduledMeetingParam);
    }
    @Override
    public void deleteScheduledMeeting(final DeleteScheduledMeetingParam deleteScheduledMeetingParam) {
        managementService.deleteScheduledMeeting(deleteScheduledMeetingParam);
    }

    @Override
    public void getScheduledMeeting(GetScheduledMeetingParam getScheduledMeetingParam) {
        managementService.getScheduledMeeting(getScheduledMeetingParam);
    }

    @Override
    public void getScheduledMeetingList(GetScheduledMeetingListParam getScheduledMeetingListParam){
        managementService.getScheduledMeetingList(getScheduledMeetingListParam);
    }

    @Override
    public void getScheduledRecurrenceMeetingList(GetScheduledMeetingListParam getScheduledMeetingListParam){
        managementService.getScheduledRecurrenceMeetingList(getScheduledMeetingListParam);
    }

    @Override
    public void findUser(FindUserParam findUSerParam) {
        managementService.findUser(findUSerParam);
    }

    @Override
    public void muteAllParticipant(MuteAllParam muteAllParam) {
        managementService.muteAllParticipant(muteAllParam);
    }

    @Override
    public void muteParticipant(MuteParam muteParam) {
        managementService.muteParticipant(muteParam);
    }

    @Override
    public void unMuteAllParticipant(UnMuteAllParam unMuteAllParam) {
        managementService.unMuteAllParticipant(unMuteAllParam);
    }

    @Override
    public void unMuteParticipant(UnMuteParam unMuteParam) {
        managementService.unMuteParticipant(unMuteParam);
    }

    @Override
    public void stopMeeting(StopMeetingParam stopMeetingParam) {
        managementService.stopMeeting(stopMeetingParam);
    }

    @Override
    public void changeNameParticipant(ChangeDisplayNameParam param) {
        managementService.changeNameParticipant(param);
    }

    @Override
    public void lectureParticipant(LectureParam lectureParam) {
        managementService.lectureParticipant(lectureParam);
    }

    @Override
    public void queryMeetingRoomInfo(QueryMeetingRoomParam queryMeetingRoomParam) {
        managementService.queryMeetingRoomInfo(queryMeetingRoomParam);
    }

    @Override
    public void queryMeetingInfo(QueryMeetingInfoParam queryMeetingInfoParam) {
        managementService.queryMeetingInfo(queryMeetingInfoParam);
    }

    @Override
    public void frtcSdkLeaveMeeting() {
        managementService.frtcSdkLeaveMeeting();
    }

    public void startOverlay(StartOverlayParam startOverlayParam){
        managementService.startOverlay(startOverlayParam);
    }

    @Override
    public void stopOverlay(StopOverlayParam stopOverlayParam){
        managementService.stopOverlay(stopOverlayParam);
    }

    @Override
    public void disconnectParticipant(DisconnectParticipantParam disconnectParticipantParam) {
        managementService.disconnectParticipant(disconnectParticipantParam);
    }

    @Override
    public void disconnectAllParticipants(DisconnectAllParticipantsParam disconnectAllParticipantsParam) {
        managementService.disconnectAllParticipants(disconnectAllParticipantsParam);
    }

    @Override
    public void startRecording(CommonMeetingParam startRecordingParam) {
        managementService.startRecording(startRecordingParam);
    }
    @Override
    public void stopRecording(CommonMeetingParam stopRecordingParam){
        managementService.stopRecording(stopRecordingParam);
    }
    @Override
    public void startLive(LiveMeetingParam startLiveParam){
        managementService.startLive(startLiveParam);
    }
    @Override
    public void stopLive(LiveMeetingParam stopLiveParam){
        managementService.stopLive(stopLiveParam);
    }
    @Override
    public void unMuteSelf(CommonMeetingParam commonMeetingParam) {
        managementService.unMuteSelf(commonMeetingParam);
    }

    @Override
    public void allowUnmute(AllowUnmuteParam allowUnmuteParam) {
        managementService.allowUnmute(allowUnmuteParam);
    }

    @Override
    public void pinForMeeting(PinForMeetingParam pinParam){
        managementService.pinForMeeting(pinParam);
    }

    @Override
    public void unpinForMeeting(CommonMeetingParam unpinParam){
        managementService.unpinForMeeting(unpinParam);
    }

    @Override
    public void addMeetingIntoMeetingList(AddMeetingToListParam param) {
        managementService.addMeetingIntoMeetingList(param);
    }

    @Override
    public void removeMeetingFromMeetingList(AddMeetingToListParam param) {
        managementService.removeMeetingFromMeetingList(param);
    }
}
