package frtc.sdk.model;

import java.util.List;

public class CreateScheduledMeetingResult {

    private String meeting_type;
    private String meeting_name;
    private String meeting_description;
    private String meeting_number;
    private String call_rate_type;
    private long schedule_start_time;
    private long schedule_end_time;
    private String meeting_password;
    private List<UserInfo> invited_users_detail;
    private String owner_name;
    private String reservation_id;
    private String recurrence_gid;
    private String recurrence_type;
    private String meeting_url;
    private String time_to_join;
    private String groupMeetingUrl;
    private int recurrenceInterval;
    private List<Integer> recurrenceDaysOfWeek;
    private List<Integer> recurrenceDaysOfMonth;
    private long recurrenceStartTime;
    private long recurrenceEndTime;
    private long recurrenceStartDay;
    private long recurrenceEndDay;


    public String getGroupMeetingUrl() {
        return groupMeetingUrl;
    }

    public void setGroupMeetingUrl(String groupMeetingUrl) {
        this.groupMeetingUrl = groupMeetingUrl;
    }

    public int getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public void setRecurrenceInterval(int recurrenceInterval) {
        this.recurrenceInterval = recurrenceInterval;
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

    public String getMeeting_type() {
        return meeting_type;
    }

    public void setMeeting_type(String meeting_type) {
        this.meeting_type = meeting_type;
    }

    public String getMeeting_name() {
        return meeting_name;
    }

    public void setMeeting_name(String meeting_name) {
        this.meeting_name = meeting_name;
    }

    public String getMeeting_description() {
        return meeting_description;
    }

    public void setMeeting_description(String meeting_description) {
        this.meeting_description = meeting_description;
    }

    public String getMeeting_number() {
        return meeting_number;
    }

    public void setMeeting_number(String meeting_number) {
        this.meeting_number = meeting_number;
    }

    public String getCall_rate_type() {
        return call_rate_type;
    }

    public void setCall_rate_type(String call_rate_type) {
        this.call_rate_type = call_rate_type;
    }

    public long getSchedule_start_time() {
        return schedule_start_time;
    }

    public void setSchedule_start_time(long schedule_start_time) {
        this.schedule_start_time = schedule_start_time;
    }

    public long getSchedule_end_time() {
        return schedule_end_time;
    }

    public void setSchedule_end_time(long schedule_end_time) {
        this.schedule_end_time = schedule_end_time;
    }

    public String getMeeting_password() {
        return meeting_password;
    }

    public void setMeeting_password(String meeting_password) {
        this.meeting_password = meeting_password;
    }

    public List<UserInfo> getInvited_users_detail() {
        return invited_users_detail;
    }

    public void setInvited_users_detail(List<UserInfo> invited_users_detail) {
        this.invited_users_detail = invited_users_detail;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getReservation_id() {
        return reservation_id;
    }

    public void setReservation_id(String reservation_id) {
        this.reservation_id = reservation_id;
    }

    public String getRecurrence_gid() {
        return recurrence_gid;
    }

    public void setRecurrence_gid(String recurrence_gid) {
        this.recurrence_gid = recurrence_gid;
    }

    public String getRecurrence_type() {
        return recurrence_type;
    }

    public void setRecurrence_type(String recurrence_type) {
        this.recurrence_type = recurrence_type;
    }

    public String getMeeting_url() {
        return meeting_url;
    }

    public void setMeeting_url(String meeting_url) {
        this.meeting_url = meeting_url;
    }

    public String getTime_to_join() {
        return time_to_join;
    }

    public void setTime_to_join(String time_to_join) {
        this.time_to_join = time_to_join;
    }
}
