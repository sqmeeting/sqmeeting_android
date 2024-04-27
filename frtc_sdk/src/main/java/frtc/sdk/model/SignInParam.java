package frtc.sdk.model;

public class SignInParam extends BaseParam {
    private String username;
    private String secret;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return secret;
    }

    public void setPassword(String password) {
        this.secret = password;
    }
}
