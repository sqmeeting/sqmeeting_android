package frtc.sdk.ui.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import frtc.sdk.log.Log;

public class MeetingCall {

    private String meetingNumber;
    private String meetingName;

    private String password;

    private String displayName;

    private String serverAddress;

    private long createTime;
    private long leaveTime;

    private String time ="";

    private String ownerId;
    private String ownerTime;

    private String creatorName;
    private String creatorId;

    private String spendTime;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSpendTime(String spendTime) {
        this.spendTime = spendTime;
    }

    public String getSpendTime(){
        return spendTime;
    }

    public String getFormattedTime(){
        return "";
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(createTime);
        this.time = simpleDateFormat.format(date);
        Log.d("MeetingCall","setCreateTime:time="+time+",createTime="+createTime);
    }

    public long getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(long leaveTime) {
        this.leaveTime = leaveTime;
        if(leaveTime > createTime){
            spendTime= leaveTime - createTime + "";
        }
        Log.d("MeetingCall","leaveTime:"+leaveTime+",createTime:"+createTime+",spendTime:"+spendTime);
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerTime() {
        return ownerTime;
    }

    public void setOwnerTime(String ownerTime) {
        this.ownerTime = ownerTime;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeetingCall)) return false;
        MeetingCall that = (MeetingCall) o;
        return Objects.equals(getMeetingNumber(), that.getMeetingNumber()) && Objects.equals(getServerAddress(), that.getServerAddress());
    }

}