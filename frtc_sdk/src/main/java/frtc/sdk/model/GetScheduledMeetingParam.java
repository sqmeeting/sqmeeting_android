package frtc.sdk.model;

public class GetScheduledMeetingParam extends CommonParam {

    private String reservationId;

    public GetScheduledMeetingParam(){

    }
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
}
