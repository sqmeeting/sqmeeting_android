package frtc.sdk.model;

import java.util.List;

public class QueryMeetingRoomResult {
    private List<MeetingRoom> meeting_rooms;

    public List<MeetingRoom> getMeetingRooms() {
        return meeting_rooms;
    }

    public void setMeetingRooms(List<MeetingRoom> meetingRooms) {
        this.meeting_rooms = meetingRooms;
    }
}
