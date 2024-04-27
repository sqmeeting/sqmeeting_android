package frtc.sdk.internal.model;


public class DeleteScheduledMeetingRequest{

    private boolean deleteGroup;

    public boolean isDeleteGroup() {
        return deleteGroup;
    }

    public void setDeleteGroup(boolean deleteGroup) {
        this.deleteGroup = deleteGroup;
    }

    public DeleteScheduledMeetingRequest(){

    }
}
