package frtc.sdk.internal.model;

public class StartTimerParam {
    private int timer_id;
    private int timer_interval;
    private boolean timer_periodic;

    public int getTimer_id() {
        return timer_id;
    }

    public void setTimer_id(int timer_id) {
        this.timer_id = timer_id;
    }

    public int getTimer_interval() {
        return timer_interval;
    }

    public void setTimer_interval(int timer_interval) {
        this.timer_interval = timer_interval;
    }

    public boolean isTimer_periodic() {
        return timer_periodic;
    }

    public void setTimer_periodic(boolean timer_periodic) {
        this.timer_periodic = timer_periodic;
    }
}
