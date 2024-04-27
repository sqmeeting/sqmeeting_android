package frtc.sdk.internal.model;

public class MeetingJoinInfo {
    private String userToken;
    private String userId;
    private String server;
    private String meetingNumber;
    private String meetingPwd;
    private String displayName;
    private String meetingOwnerId;
    private boolean audioOnly;
    private boolean muteAudio;
    private boolean muteVideo;
    private String userRole;
    private long timeStamp;
    private boolean isScreenShare;
    private boolean isFloatWindow;

    public MeetingJoinInfo() {

    }

    public boolean isFloatWindow() {
        return isFloatWindow;
    }

    public void setFloatWindow(boolean floatWindow) {
        isFloatWindow = floatWindow;
    }

    public boolean isScreenShare() {
        return isScreenShare;
    }

    public void setScreenShare(boolean screenShare) {
        isScreenShare = screenShare;
    }

    public String getUserRole() {
        return userRole;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public String getMeetingPwd() {
        return meetingPwd;
    }

    public void setMeetingPwd(String meetingPwd) {
        this.meetingPwd = meetingPwd;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMeetingOwnerId() {
        return meetingOwnerId;
    }

    public void setMeetingOwnerId(String meetingOwnerId) {
        this.meetingOwnerId = meetingOwnerId;
    }

    public boolean isAudioOnly() {
        return audioOnly;
    }

    public void setAudioOnly(boolean audioOnly) {
        this.audioOnly = audioOnly;
    }

    public boolean isMuteAudio() {
        return muteAudio;
    }

    public void setMuteAudio(boolean muteAudio) {
        this.muteAudio = muteAudio;
    }

    public boolean isMuteVideo() {
        return muteVideo;
    }

    public void setMuteVideo(boolean muteVideo) {
        this.muteVideo = muteVideo;
    }
}
