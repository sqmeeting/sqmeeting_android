package frtc.sdk.model;

import frtc.sdk.IFrtcParam;

public class BaseParam implements IFrtcParam {
    private String serverAddress;
    private String clientId;

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

}
