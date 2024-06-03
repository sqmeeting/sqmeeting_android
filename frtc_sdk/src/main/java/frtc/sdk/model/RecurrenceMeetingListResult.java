package frtc.sdk.model;

import java.util.List;

import frtc.sdk.IResult;

public class RecurrenceMeetingListResult implements IResult {
    private List<ScheduledMeeting> meeting_schedules;

    private int total_page_num;
    private int total_size;
    private String recurrenceType;
    private int recurrenceInterval;
    private long recurrenceStartTime;
    private long recurrenceEndTime;
    private long recurrenceStartDay;
    private long recurrenceEndDay;
    private List<Integer> recurrenceDaysOfWeek;
    private List<Integer> recurrenceDaysOfMonth;
    private String groupMeetingUrl;

    public String getGroupMeetingUrl() {
        return groupMeetingUrl;
    }

    public void setGroupMeetingUrl(String groupMeetingUrl) {
        this.groupMeetingUrl = groupMeetingUrl;
    }

    public String getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(String recurrenceType) {
        this.recurrenceType = recurrenceType;
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

    public int getTotal_page_num() {
        return total_page_num;
    }

    public void setTotal_page_num(int total_page_num) {
        this.total_page_num = total_page_num;
    }

    public int getTotal_size() {
        return total_size;
    }

    public void setTotal_size(int total_size) {
        this.total_size = total_size;
    }

    public List<ScheduledMeeting> getMeeting_schedules() {
        return meeting_schedules;
    }

    public void setMeeting_schedules(List<ScheduledMeeting> meeting_schedules) {
        this.meeting_schedules = meeting_schedules;
    }
}
