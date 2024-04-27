package frtc.sdk.model;

public class GetScheduledMeetingListParam extends CommonParam {
    private String filter = "";

    private String page_num = "1";
    private String page_size = "50";
    private String recurrence_gid = "";

    public String getRecurrence_gid() {
        return recurrence_gid;
    }

    public void setRecurrence_gid(String recurrence_gid) {
        this.recurrence_gid = recurrence_gid;
    }

    public String getPage_num() {
        return page_num;
    }

    public void setPage_num(String page_num) {
        this.page_num = page_num;
    }

    public String getPage_size() {
        return page_size;
    }

    public void setPage_size(String page_size) {
        this.page_size = page_size;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

}
