package frtc.sdk.internal.model;

import java.util.List;

import frtc.sdk.IResult;
import frtc.sdk.model.UserInfo;

public class FindUserResult implements IResult {

    private List<UserInfo> users;
    private int total_page_num;
    private int total_size;

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

    public int getTotal_page_num() {
        return total_page_num;
    }

    public void setTotal_page_num(int total_page_num) {
        this.total_page_num = total_page_num;
    }

    public int getTotal_size() {
        return total_size;
    }

    public void setTotal_size(int total_size) {
        this.total_size = total_size;
    }
}
