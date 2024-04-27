package frtc.sdk.model;

public class InitParams {
    private String clientId;

    public InitParams() {

    }
    public InitParams(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
