package com.frtc.sqmeetingce.ui.calendar;

public class EventReminders {

    private long reminderId;
    private long reminderEventID;
    private int reminderMinute;
    private int reminderMethod;

    public long getReminderId() {
        return reminderId;
    }

    void setReminderId(long reminderId) {
        this.reminderId = reminderId;
    }

    public long getReminderEventID() {
        return reminderEventID;
    }

    void setReminderEventID(long reminderEventID) {
        this.reminderEventID = reminderEventID;
    }

    public int getReminderMinute() {
        return reminderMinute;
    }

    void setReminderMinute(int reminderMinute) {
        this.reminderMinute = reminderMinute;
    }

    public int getReminderMethod() {
        return reminderMethod;
    }

    void setReminderMethod(int reminderMethod) {
        this.reminderMethod = reminderMethod;
    }

}
