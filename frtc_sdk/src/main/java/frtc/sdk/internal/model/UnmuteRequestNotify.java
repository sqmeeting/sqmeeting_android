package frtc.sdk.internal.model;

import java.util.List;

import frtc.sdk.model.UnmuteRequest;

public class UnmuteRequestNotify {
    private List<UnmuteRequest> unmute_request;

    public List<UnmuteRequest> getUnmute_request() {
        return unmute_request;
    }

    public void setUnmute_request(List<UnmuteRequest> unmute_request) {
        this.unmute_request = unmute_request;
    }
}
