package frtc.sdk.model;

public class PasswordUpdateParam extends CommonParam {
    private String secret_old;
    private String secret_new;

    public String getPasswordOld() {
        return secret_old;
    }

    public void setPasswordOld(String passwordOld) {
        this.secret_old = passwordOld;
    }

    public String getPasswordNew() {
        return secret_new;
    }

    public void setPasswordNew(String passwordNew) {
        this.secret_new = passwordNew;
    }
}
