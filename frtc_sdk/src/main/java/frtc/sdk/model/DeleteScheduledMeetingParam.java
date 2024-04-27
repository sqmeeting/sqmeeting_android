package frtc.sdk.model;

public class DeleteScheduledMeetingParam extends CommonParam {

    private String reservationId;
    private String deleteGroup;

    public DeleteScheduledMeetingParam(){

    }

    public String getDeleteGroup() {
        return deleteGroup;
    }

    public void setDeleteGroup(String deleteGroup) {
        this.deleteGroup = deleteGroup;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
}
