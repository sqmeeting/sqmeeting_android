package frtc.sdk.model;

public class MeetingRoom {
    private String meeting_room_id;
    private String meeting_number;
    private String meetingroom_name;
    private String meeting_password;
    private String owner_id;
    private String owner_name;
    private String creator_id;
    private String creator_name;
    private long created_time;

    public String getMeetingRoomId() {
        return meeting_room_id;
    }

    public void setMeetingRoomId(String meetingRoomId) {
        this.meeting_room_id = meetingRoomId;
    }

    public String getMeetingNumber() {
        return meeting_number;
    }

    public void setMeetingNumber(String meeting_number) {
        this.meeting_number = meeting_number;
    }

    public String getMeetingRoomName() {
        return meetingroom_name;
    }

    public void setMeetingRoomName(String meetingroom_name) {
        this.meetingroom_name = meetingroom_name;
    }

    public String getPassword() {
        return meeting_password;
    }

    public void setPassword(String password) {
        this.meeting_password = password;
    }

    public String getOwnerId() {
        return owner_id;
    }

    public void setOwnerId(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwnerName() {
        return owner_name;
    }

    public void setOwnerName(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getCreatorId() {
        return creator_id;
    }

    public void setCreatorId(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getCreatorName() {
        return creator_name;
    }

    public void setCreatorName(String creator_name) {
        this.creator_name = creator_name;
    }

    public long getCreatedTime() {
        return created_time;
    }

    public void setCreatedTime(long created_time) {
        this.created_time = created_time;
    }
}
