package frtc.sdk.internal.model;

import frtc.sdk.log.Log;

public class MeetingStatusInfo {
    private String displayName = "";
    private String meetingNumber = "";
    private String meetingName = "";
    private String clientId = "";
    private String userToken = "";
    private String serverAddress = "";
    private String callRate = "2048";
    private String ownerId = "";
    private String ownerName = "";
    private String meetingPassword = "";
    private String meetingURL;
    private long scheduleStartTime;
    private long scheduleEndTime;

    private volatile FrtcMeetingStatus meetingStatus = FrtcMeetingStatus.DISCONNECTED;

    private boolean audioOnly = false;
    private boolean isCameraOpenFailed = false;
    private boolean isCameraOpened = false;

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isCrossServer() {
        return isCrossServer;
    }

    public void setCrossServer(boolean crossServer) {
        isCrossServer = crossServer;
    }

    private boolean isCrossServer = false;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getCallRate() {
        return callRate;
    }

    public void setCallRate(String callRate) {
        this.callRate = callRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        Log.d("MeetingStatusInfo","setDisplayName:"+displayName);
        this.displayName = displayName;
    }

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public FrtcMeetingStatus getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(FrtcMeetingStatus meetingStatus) {
        this.meetingStatus = meetingStatus;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getMeetingURL() {
        return meetingURL;
    }

    public void setMeetingURL(String meetingURL) {
        this.meetingURL = meetingURL;
    }

    public long getScheduleStartTime() {
        return scheduleStartTime;
    }

    public void setScheduleStartTime(long scheduleStartTime) {
        this.scheduleStartTime = scheduleStartTime;
    }

    public long getScheduleEndTime() {
        return scheduleEndTime;
    }

    public void setScheduleEndTime(long scheduleEndTime) {
        this.scheduleEndTime = scheduleEndTime;
    }

    public boolean isAudioOnly() {
        return audioOnly;
    }

    public void setAudioOnly(boolean audioOnly) {
        this.audioOnly = audioOnly;
    }

    public boolean isCameraOpenFailed() {
        return isCameraOpenFailed;
    }

    public void setCameraOpenFailed(boolean cameraOpenFailed) {
        isCameraOpenFailed = cameraOpenFailed;
    }

    public boolean isCameraOpened() {
        return isCameraOpened;
    }

    public void setCameraOpened(boolean cameraOpened) {
        isCameraOpened = cameraOpened;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getMeetingPassword() {
        return meetingPassword;
    }

    public void setMeetingPassword(String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }
}
