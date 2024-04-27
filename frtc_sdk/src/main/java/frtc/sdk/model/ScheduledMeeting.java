package frtc.sdk.model;

import java.util.List;

public class ScheduledMeeting {
    private String meeting_number;
    private String meeting_type;
    private String meeting_name;
    private String meeting_password;

    private String reservation_id;
    private String recurrence_gid;
    private String schedule_start_time;
    private String schedule_end_time;
    private String recurrence_type;

    private String owner_id;
    private String owner_name;
    private String meeting_url;
    private String groupMeetingUrl;

    private int recurrenceInterval;
    private long recurrenceStartDay;
    private long recurrenceEndDay;
    private long recurrenceStartTime;
    private long recurrenceEndTime;
    private List<Integer> recurrenceDaysOfWeek;
    private List<Integer> recurrenceDaysOfMonth;
    private List<ScheduledMeeting> recurrenceReservationList;
    private List<String> participantUsers;

    public String getGroupMeetingUrl() {
        return groupMeetingUrl;
    }

    public void setGroupMeetingUrl(String groupMeetingUrl) {
        this.groupMeetingUrl = groupMeetingUrl;
    }

    public List<String> getParticipantUsers() {
        return participantUsers;
    }

    public void setParticipantUsers(List<String> participantUsers) {
        this.participantUsers = participantUsers;
    }

    public long getRecurrenceStartDay() {
        return recurrenceStartDay;
    }

    public void setRecurrenceStartDay(long recurrenceStartDay) {
        this.recurrenceStartDay = recurrenceStartDay;
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

    public long getRecurrenceEndDay() {
        return recurrenceEndDay;
    }

    public void setRecurrenceEndDay(long recurrenceEndDay) {
        this.recurrenceEndDay = recurrenceEndDay;
    }

    public int getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public void setRecurrenceInterval(int recurrenceInterval) {
        this.recurrenceInterval = recurrenceInterval;
    }

    public List<ScheduledMeeting> getRecurrenceReservationList() {
        return recurrenceReservationList;
    }

    public void setRecurrenceReservationList(List<ScheduledMeeting> recurrenceReservationList) {
        this.recurrenceReservationList = recurrenceReservationList;
    }

    public String getMeeting_number() {
        return meeting_number;
    }

    public void setMeeting_number(String meeting_number) {
        this.meeting_number = meeting_number;
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

    public String getMeeting_password() {
        return meeting_password;
    }

    public void setMeeting_password(String meeting_password) {
        this.meeting_password = meeting_password;
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

    public String getMeeting_url() {
        return meeting_url;
    }

    public void setMeeting_url(String meeting_url) {
        this.meeting_url = meeting_url;
    }
}
