package com.frtc.sqmeetingce.ui.calendar;

import android.support.annotation.NonNull;

import java.util.List;

public class CalendarEvent {

    private long id;
    private long calID;
    private String title;
    private String description;
    private String eventLocation;
    private int displayColor;
    private int status;
    private long start;
    private long end;
    private String duration;
    private String eventTimeZone;
    private String eventEndTimeZone;
    private int allDay;
    private int accessLevel;
    private int availability;
    private int hasAlarm;
    private String rRule;
    private String rDate;
    private int hasAttendeeData;
    private int lastDate;
    private String organizer;
    private String isOrganizer;
    private int advanceTime;
    private List<EventReminders> reminders;


    public CalendarEvent(String title, String description, String eventLocation,
                         long start, long end, int advanceTime, String rRule) {
        this.title = title;
        this.description = description;
        this.eventLocation = eventLocation;
        this.start = start;
        this.end = end;
        this.advanceTime = advanceTime;
        this.rRule = rRule;
    }

    public int getAdvanceTime() {
        return advanceTime;
    }

    public void setAdvanceTime(int advanceTime) {
        this.advanceTime = advanceTime;
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public long getCalID() {
        return calID;
    }

    void setCalID(long calID) {
        this.calID = calID;
    }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public int getDisplayColor() {
        return displayColor;
    }

    void setDisplayColor(int displayColor) {
        this.displayColor = displayColor;
    }

    public int getStatus() {
        return status;
    }

    void setStatus(int status) {
        this.status = status;
    }

    public long getStart() {
        return start;
    }

    void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    void setEnd(long end) {
        this.end = end;
    }

    public String getDuration() {
        return duration;
    }

    void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEventTimeZone() {
        return eventTimeZone;
    }

    void setEventTimeZone(String eventTimeZone) {
        this.eventTimeZone = eventTimeZone;
    }

    public String getEventEndTimeZone() {
        return eventEndTimeZone;
    }

    void setEventEndTimeZone(String eventEndTimeZone) {
        this.eventEndTimeZone = eventEndTimeZone;
    }

    public int getAllDay() {
        return allDay;
    }

    void setAllDay(int allDay) {
        this.allDay = allDay;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public int getAvailability() {
        return availability;
    }

    void setAvailability(int availability) {
        this.availability = availability;
    }

    public int getHasAlarm() {
        return hasAlarm;
    }

    void setHasAlarm(int hasAlarm) {
        this.hasAlarm = hasAlarm;
    }

    public String getRRule() {
        return rRule;
    }

    void setRRule(String rRule) {
        this.rRule = rRule;
    }

    public String getRDate() {
        return rDate;
    }

    void setRDate(String rDate) {
        this.rDate = rDate;
    }

    public int getHasAttendeeData() {
        return hasAttendeeData;
    }

    void setHasAttendeeData(int hasAttendeeData) {
        this.hasAttendeeData = hasAttendeeData;
    }

    public int getLastDate() {
        return lastDate;
    }

    void setLastDate(int lastDate) {
        this.lastDate = lastDate;
    }

    public String getOrganizer() {
        return organizer;
    }

    void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getIsOrganizer() {
        return isOrganizer;
    }

    void setIsOrganizer(String isOrganizer) {
        this.isOrganizer = isOrganizer;
    }

    public List<EventReminders> getReminders() {
        return reminders;
    }

    void setReminders(List<EventReminders> reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public String toString() {
        return "CalendarEvent{" +
                "\n id=" + id +
                "\n calID=" + calID +
                "\n title='" + title + '\'' +
                "\n description='" + description + '\'' +
                "\n eventLocation='" + eventLocation + '\'' +
                "\n displayColor=" + displayColor +
                "\n status=" + status +
                "\n start=" + start +
                "\n end=" + end +
                "\n duration='" + duration + '\'' +
                "\n eventTimeZone='" + eventTimeZone + '\'' +
                "\n eventEndTimeZone='" + eventEndTimeZone + '\'' +
                "\n allDay=" + allDay +
                "\n accessLevel=" + accessLevel +
                "\n availability=" + availability +
                "\n hasAlarm=" + hasAlarm +
                "\n rRule='" + rRule + '\'' +
                "\n rDate='" + rDate + '\'' +
                "\n hasAttendeeData=" + hasAttendeeData +
                "\n lastDate=" + lastDate +
                "\n organizer='" + organizer + '\'' +
                "\n isOrganizer='" + isOrganizer + '\'' +
                "\n reminders=" + reminders +
                '}';
    }

    @Override
    public int hashCode() {
        return (int) (id * 37 + calID);
    }

}
