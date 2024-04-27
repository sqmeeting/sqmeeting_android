package frtc.sdk.model;

import java.util.List;

import frtc.sdk.IResult;

public class UserInfoResult implements IResult {

    private String user_id;
    private String user_token;
    private String username;
    private String email;
    private String real_name;
    private String department;
    private String mobile;
    private List<String> role;
    private String security_level;

    private String error;
    private String errorCode;

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return user_token;
    }

    public void setToken(String user_token) {
        this.user_token = user_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return real_name;
    }

    public void setRealName(String real_name) {
        this.real_name = real_name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    public String getSecurityLevel() {
        return security_level;
    }

    public void setSecurityLevel(String security_level) {
        this.security_level = security_level;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
