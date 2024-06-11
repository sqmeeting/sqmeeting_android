package frtc.sdk.model;

import java.util.List;

public class UpdateScheduledMeetingParam extends CommonParam {

    private String reservationId;
    private String meeting_name;
    private String meeting_type;
    private String meeting_description;
    private String schedule_start_time;
    private String schedule_end_time;
    private String meeting_room_id;
    private String call_rate_type;
    private String time_to_join;
    private String meeting_password;
    private List<String> invited_users;

    private String mute_upon_entry;
    private String guest_dial_in;
    private String watermark;
    private String watermark_type;

    private boolean isSingle = false;

    private String recurrenceType;
    private int recurrenceInterval;
    private long recurrenceStartTime;
    private long recurrenceEndTime;
    private long recurrenceStartDay;
    private long recurrenceEndDay;
    private List<Integer> recurrenceDaysOfWeek;
    private List<Integer> recurrenceDaysOfMonth;

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

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getMeeting_name() {
        return meeting_name;
    }

    public void setMeeting_name(String meeting_name) {
        this.meeting_name = meeting_name;
    }

    public String getMeeting_type() {
        return meeting_type;
    }

    public void setMeeting_type(String meeting_type) {
        this.meeting_type = meeting_type;
    }

    public String getMeeting_description() {
        return meeting_description;
    }

    public void setMeeting_description(String meeting_description) {
        this.meeting_description = meeting_description;
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

    public String getMeeting_room_id() {
        return meeting_room_id;
    }

    public void setMeeting_room_id(String meeting_room_id) {
        this.meeting_room_id = meeting_room_id;
    }

    public String getCall_rate_type() {
        return call_rate_type;
    }

    public void setCall_rate_type(String call_rate_type) {
        this.call_rate_type = call_rate_type;
    }

    public String getTime_to_join() {
        return time_to_join;
    }

    public void setTime_to_join(String time_to_join) {
        this.time_to_join = time_to_join;
    }

    public String getMeeting_password() {
        return meeting_password;
    }

    public void setMeeting_password(String meeting_password) {
        this.meeting_password = meeting_password;
    }

    public List<String> getInvited_users() {
        return invited_users;
    }

    public void setInvited_users(List<String> invited_users) {
        this.invited_users = invited_users;
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
}
