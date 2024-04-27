package frtc.sdk.model;

import frtc.sdk.internal.model.FrtcMeetingStatus;

public class MeetingStateInfoNotify {
    private String meetingName;
    private String meetingNumber;
    private Boolean isAudioOnly;
    private String ownerId;
    private String ownerName = "";
    private String meetingURL = "";
    private long scheduleStartTime;
    private long scheduleEndTime;
    private FrtcMeetingStatus meetingStatus;
    private int callEndReason;
    private boolean isCrossServer = false;
    private String crossSeverAddr = "";
    private boolean isReconnecting = false;
    private int reconnectCount = 0;

    public String getCrossSeverAddr() {
        return crossSeverAddr;
    }

    public void setCrossSeverAddr(String crossSeverAddr) {
        this.crossSeverAddr = crossSeverAddr;
    }

    public boolean isReconnecting() {
        return isReconnecting;
    }

    public void setReconnecting(boolean reconnecting) {
        isReconnecting = reconnecting;
    }

    public int getReconnectCount() {
        return reconnectCount;
    }

    public void setReconnectCount(int reconnectCount) {
        this.reconnectCount = reconnectCount;
    }

    public boolean isCrossServer() {
        return isCrossServer;
    }

    public void setCrossServer(boolean crossServer) {
        isCrossServer = crossServer;
    }

    public int getCallEndReason() {
        return callEndReason;
    }

    public void setCallEndReason(int callEndReason) {
        this.callEndReason = callEndReason;
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public Boolean getAudioOnly() {
        return isAudioOnly;
    }

    public void setAudioOnly(Boolean audioOnly) {
        isAudioOnly = audioOnly;
    }

    public FrtcMeetingStatus getMeetingStatus() {
        return meetingStatus;
    }

    public void setMeetingStatus(FrtcMeetingStatus meetingStatus) {
        this.meetingStatus = meetingStatus;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
