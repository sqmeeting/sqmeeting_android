package frtc.sdk.model;

import java.util.List;

import frtc.sdk.IResult;

public class ScheduledMeetingListResult implements IResult {

    private List<ScheduledMeeting> meeting_schedules;

    private int total_page_num;
    private int total_size;

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
