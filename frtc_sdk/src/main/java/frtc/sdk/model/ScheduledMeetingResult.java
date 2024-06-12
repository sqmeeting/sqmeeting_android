package frtc.sdk.model;

import java.util.List;

import frtc.sdk.IResult;

public class ScheduledMeetingResult implements IResult {

    private String reservation_id;
    private String meeting_type;
    private String meeting_name;
    private String call_rate_type;

    private String recurrence_gid;
    private String schedule_start_time;
    private String schedule_end_time;
    private String recurrence_type;
    private String meeting_room_id;
    private String meeting_password;
    private String time_to_join;
    private List<UserInfo> invited_users_details;

    private String mute_upon_entry;
    private String guest_dial_in;
    private String watermark;
    private String watermark_type;
    private String owner_id;
    private String owner_name;
    private String qrcode_string;
    private String meeting_url;
    private String groupMeetingUrl;
    private int recurrenceInterval;
    private long recurrenceStartTime;
    private long recurrenceEndTime;
    private long recurrenceStartDay;
    private long recurrenceEndDay;
    private List<Integer> recurrenceDaysOfWeek;
    private List<Integer> recurrenceDaysOfMonth;

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
        return recurrenceDaysOfWeek;
    }

    public void setRecurrenceDaysOfWeek(List<Integer> recurrenceDaysOfWeek) {
        this.recurrenceDaysOfWeek = recurrenceDaysOfWeek;
    }

    public List<Integer> getRecurrenceDaysOfMonth() {
        return recurrenceDaysOfMonth;
    }

    public void setRecurrenceDaysOfMonth(List<Integer> recurrenceDaysOfMonth) {
        this.recurrenceDaysOfMonth = recurrenceDaysOfMonth;
    }

    public String getMeeting_type() {
        return meeting_type;
    }

    public void setMeeting_type(String meeting_type) {
        this.meeting_type = meeting_type;
    }

    public String getReservation_id() {
        return reservation_id;
    }

    public void setReservation_id(String reservation_id) {
        this.reservation_id = reservation_id;
    }

    public String getMeeting_name() {
        return meeting_name;
    }

    public void setMeeting_name(String meeting_name) {
        this.meeting_name = meeting_name;
    }

    public String getCall_rate_type() {
        return call_rate_type;
    }

    public void setCall_rate_type(String call_rate_type) {
        this.call_rate_type = call_rate_type;
    }

    public String getRecurrence_gid() {
        return recurrence_gid;
    }

    public void setRecurrence_gid(String recurrence_gid) {
        this.recurrence_gid = recurrence_gid;
    }

    public String getSchedule_start_time() {
        return schedule_start_time;
    }

    public void setSchedule_start_time(String schedule_start_time) {
        this.schedule_start_time = schedule_start_time;
    }

    public String getSchedule_end_time() {
        return schedule_end_time;
    }

    public void setSchedule_end_time(String schedule_end_time) {
        this.schedule_end_time = schedule_end_time;
    }

    public String getRecurrence_type() {
        return recurrence_type;
    }

    public void setRecurrence_type(String recurrence_type) {
        this.recurrence_type = recurrence_type;
    }

    public String getMeeting_room_id() {
        return meeting_room_id;
    }

    public void setMeeting_room_id(String meeting_room_id) {
        this.meeting_room_id = meeting_room_id;
    }

    public String getMeeting_password() {
        return meeting_password;
    }

    public void setMeeting_password(String meeting_password) {
        this.meeting_password = meeting_password;
    }

    public String getTime_to_join() {
        return time_to_join;
    }

    public void setTime_to_join(String time_to_join) {
        this.time_to_join = time_to_join;
    }

    public List<UserInfo> getInvited_users_details() {
        return invited_users_details;
    }

    public void setInvited_users_details(List<UserInfo> invited_users_details) {
        this.invited_users_details = invited_users_details;
    }

    public String getMute_upon_entry() {
        return mute_upon_entry;
    }

    public void setMute_upon_entry(String mute_upon_entry) {
        this.mute_upon_entry = mute_upon_entry;
    }

    public String getGuest_dial_in() {
        return guest_dial_in;
    }

    public void setGuest_dial_in(String guest_dial_in) {
        this.guest_dial_in = guest_dial_in;
    }

    public String getWatermark() {
        return watermark;
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }

    public String getWatermark_type() {
        return watermark_type;
    }

    public void setWatermark_type(String watermark_type) {
        this.watermark_type = watermark_type;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getQrcode_string() {
        return qrcode_string;
    }

    public void setQrcode_string(String qrcode_string) {
        this.qrcode_string = qrcode_string;
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
