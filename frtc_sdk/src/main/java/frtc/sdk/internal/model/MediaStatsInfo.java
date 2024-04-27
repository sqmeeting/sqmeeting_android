package frtc.sdk.internal.model;

public class MediaStatsInfo {
    private String participantName;
    private String resolution;
    private int frameRate;
    private int jitter;
    private int logicPacketLossRate;
    private int packageLoss;
    private int packageLossRate;
    private int roundTripTime;
    private int rtpActualBitRate;

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }


    public int getJitter() {
        return jitter;
    }

    public void setJitter(int jitter) {
        this.jitter = jitter;
    }

    public int getLogicPacketLossRate() {
        return logicPacketLossRate;
    }

    public void setLogicPacketLossRate(int logicPacketLossRate) {
        this.logicPacketLossRate = logicPacketLossRate;
    }

    public int getPackageLoss() {
        return packageLoss;
    }

    public void setPackageLoss(int packageLoss) {
        this.packageLoss = packageLoss;
    }

    public int getPackageLossRate() {
        return packageLossRate;
    }

    public void setPackageLossRate(int packageLossRate) {
        this.packageLossRate = packageLossRate;
    }

    public int getRoundTripTime() {
        return roundTripTime;
    }

    public void setRoundTripTime(int roundTripTime) {
        this.roundTripTime = roundTripTime;
    }

    public int getRtpActualBitRate() {
        return rtpActualBitRate;
    }

    public void setRtpActualBitRate(int rtpActualBitRate) {
        this.rtpActualBitRate = rtpActualBitRate;
    }

}