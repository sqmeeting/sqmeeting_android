package com.frtc.sqmeetingce.ui.component;

public class InvitedUserInfo {

    private String userId;
    private String username;
    private String realName;

    private boolean isAdded = false;
    private boolean isSelected = false;

    public InvitedUserInfo(String userId, String username, String realName){
        this.username = username;
        this.userId = userId;
        this.realName = realName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
