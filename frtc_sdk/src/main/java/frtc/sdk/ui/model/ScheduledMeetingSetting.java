package frtc.sdk.ui.model;

import java.util.ArrayList;
import java.util.List;

import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.model.UserInfo;

public class ScheduledMeetingSetting {

    private String meetingNumber;
    private String meetingName;
    private String meetingType;
    private String password = null;

    private String Introduction;

    private String reservationId;
    private String reservationGid;

    private String startTime = "";
    private String endTime = "";

    private String reservationType;
    private String ownerId;
    private String ownerName;

    private String rate = MeetingCallRate.RATE_2048K.getRateValue();
    private String recurrence_type;
    private int recurrenceInterval;
    private long recurrenceStartTime;
    private long recurrenceEndTime;
    private long recurrenceStartDay;
    private long recurrenceEndDay;
    private List<Integer> recurrenceDaysOfWeek;
    private List<Integer> recurrenceDaysOfMonth;

    private ArrayList<ScheduledMeeting> recurrenceMeetings;

    private boolean isRecurrenceMeetingFullList;

    private int recurrenceCount;

    private String meetingRoomId = null;
    private List<UserInfo> invitedUsers;

    private List<String> participantUsers;

    private boolean mute = false;
    private boolean guestDialIn = true;
    private boolean watermarkEnable = false;
    private String watermarkType = WatermarkType.SINGLE.getTypeName();
    private String joinTime = "30";

    private String meeting_url;
    private String groupMeetingUrl;
    private String repetitionFreq;
    private boolean isPwdCheck = false;

    public ScheduledMeetingSetting(){

    }

    public boolean isPwdCheck() {
        return isPwdCheck;
    }

    public void setPwdCheck(boolean pwdCheck) {
        isPwdCheck = pwdCheck;
    }

    public String getIntroduction() {
        return Introduction;
    }

    public void setIntroduction(String introduction) {
        Introduction = introduction;
    }


    private String qrcode;

    public String getRepetitionFreq() {
        return repetitionFreq;
    }

    public void setRepetitionFreq(String repetitionFreq) {
        this.repetitionFreq = repetitionFreq;
    }

    public String getRecurrence_type() {
        return recurrence_type;
    }

    public void setRecurrence_type(String recurrence_type) {
        this.recurrence_type = recurrence_type;
    }

    public int getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public void setRecurrenceInterval(int recurrenceInterval) {
        this.recurrenceInterval = recurrenceInterval;
    }

    public long getRecurrenceStartTime() {
        return recurrenceStartTime;
    }

    public void setRecurrenceStartTime(long recurrenceStartTime) {
        this.recurrenceStartTime = recurrenceStartTime;
    }

    public long getRecurrenceEndTime() {
        return recurrenceEndTime;
    }

    public void setRecurrenceEndTime(long recurrenceEndTime) {
        this.recurrenceEndTime = recurrenceEndTime;
    }

    public long getRecurrenceStartDay() {
        return recurrenceStartDay;
    }

    public void setRecurrenceStartDay(long recurrenceStartDay) {
        this.recurrenceStartDay = recurrenceStartDay;
    }

    public long getRecurrenceEndDay() {
        return recurrenceEndDay;
    }

    public void setRecurrenceEndDay(long recurrenceEndDay) {
        this.recurrenceEndDay = recurrenceEndDay;
    }

    public List<Integer> getRecurrenceDaysOfWeek() {
        if(recurrenceDaysOfWeek == null){
            recurrenceDaysOfWeek = new ArrayList<>();
        }
        return recurrenceDaysOfWeek;
    }

    public void setRecurrenceDaysOfWeek(List<Integer> recurrenceDaysOfWeek) {
        this.recurrenceDaysOfWeek = recurrenceDaysOfWeek;
    }

    public List<Integer> getRecurrenceDaysOfMonth() {
        if(recurrenceDaysOfMonth == null){
            recurrenceDaysOfMonth = new ArrayList<>();
        }
        return recurrenceDaysOfMonth;
    }

    public void setRecurrenceDaysOfMonth(List<Integer> recurrenceDaysOfMonth) {
        this.recurrenceDaysOfMonth = recurrenceDaysOfMonth;
    }

    public boolean isRecurrenceMeetingFullList() {
        return isRecurrenceMeetingFullList;
    }

    public void setRecurrenceMeetingFullList(boolean recurrenceMeetingFullList) {
        isRecurrenceMeetingFullList = recurrenceMeetingFullList;
    }

    public void clearRecurrenceMeetings(){
        if(recurrenceMeetings != null){
            recurrenceMeetings.clear();
        }
    }

    public void setRecurrenceMeetings(ArrayList<ScheduledMeeting> recurrenceMeetings) {
        this.recurrenceMeetings = recurrenceMeetings;
    }

    public ArrayList<ScheduledMeeting> getRecurrenceMeetings() {
        if(recurrenceMeetings == null){
            recurrenceMeetings = new ArrayList<ScheduledMeeting>();
        }
        return recurrenceMeetings;
    }

    public ScheduledMeeting getRecurrenceMeetingByPosition(int position){
        if(position >= 0 && recurrenceMeetings != null && recurrenceMeetings.size() > position){
            return recurrenceMeetings.get(position);
        }
        return null;
    }

    public int getRecurrenceCount() {
        return recurrenceCount;
    }

    public void setRecurrenceCount(int recurrenceCount) {
        this.recurrenceCount = recurrenceCount;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRecurrenceType() {
        return recurrence_type;
    }

    public void setRecurrenceType(String recurrenceType) {
        this.recurrence_type = recurrenceType;
    }

    public String getMeetingRoomId() {
        return meetingRoomId;
    }

    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }


    public List<String> getParticipantUsers() {
        return participantUsers;
    }

    public void setParticipantUsers(List<String> participantUsers) {
        this.participantUsers = participantUsers;
    }

    public List<UserInfo> getInvitedUsers() {
        if(invitedUsers == null){
            invitedUsers = new ArrayList<>();
        }
        return invitedUsers;
    }

    public void setInvitedUsers(List<UserInfo> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean isGuestDialIn() {
        return guestDialIn;
    }

    public void setGuestDialIn(boolean guestDialIn) {
        this.guestDialIn = guestDialIn;
    }

    public boolean isWatermarkEnable() {
        return watermarkEnable;
    }

    public void setWatermarkEnable(boolean watermarkEnable) {
        this.watermarkEnable = watermarkEnable;
    }

    public String getWatermarkType() {
        return watermarkType;
    }

    public void setWatermarkType(String watermarkType) {
        this.watermarkType = watermarkType;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
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

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationGid() {
        return reservationGid;
    }

    public void setReservationGid(String reservationGid) {
        this.reservationGid = reservationGid;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getReservationType() {
        return reservationType;
    }

    public void setReservationType(String reservationType) {
        this.reservationType = reservationType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getMeeting_url() {
        return meeting_url;
    }

    public void setMeeting_url(String meeting_url) {
        this.meeting_url = meeting_url;
    }

    public String getGroupMeetingUrl() {
        return groupMeetingUrl;
    }

    public void setGroupMeetingUrl(String groupMeetingUrl) {
        this.groupMeetingUrl = groupMeetingUrl;
    }
}
