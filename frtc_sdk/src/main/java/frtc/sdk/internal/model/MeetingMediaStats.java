package frtc.sdk.internal.model;

import java.util.List;

public class MeetingMediaStats {
    public List<MediaStatsInfo> apr;
    public List<MediaStatsInfo> aps;
    public List<MediaStatsInfo> vcr;
    public List<MediaStatsInfo> vcs;
    public List<MediaStatsInfo> vpr;
    public List<MediaStatsInfo> vps;

    public List<MediaStatsInfo> getApr() {
        return apr;
    }

    public void setApr(List<MediaStatsInfo> apr) {
        this.apr = apr;
    }

    public List<MediaStatsInfo> getAps() {
        return aps;
    }

    public void setAps(List<MediaStatsInfo> aps) {
        this.aps = aps;
    }

    public List<MediaStatsInfo> getVcr() {
        return vcr;
    }

    public void setVcr(List<MediaStatsInfo> vcr) {
        this.vcr = vcr;
    }

    public List<MediaStatsInfo> getVcs() {
        return vcs;
    }

    public void setVcs(List<MediaStatsInfo> vcs) {
        this.vcs = vcs;
    }

    public List<MediaStatsInfo> getVpr() {
        return vpr;
    }

    public void setVpr(List<MediaStatsInfo> vpr) {
        this.vpr = vpr;
    }

    public List<MediaStatsInfo> getVps() {
        return vps;
    }

    public void setVps(List<MediaStatsInfo> vps) {
        this.vps = vps;
    }
}