package frtc.sdk.internal.model;

public class UploadLogsStatus {
    public int progress;
    public double bitrate;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public double getBitrate() {
        return bitrate;
    }

    public void setBitrate(double bitrate) {
        this.bitrate = bitrate;
    }
}
