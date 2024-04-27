package frtc.sdk.ui.model;

public class SecurityConstant {

    public static final String SECURITY_LEVEL_HIGH = "HIGH";
    public static final String SECURITY_LEVEL_NORMAL = "NORMAL";

    public static String HIGH_PATTERN = "(?=^[a-zA-Z0-9])(?=(?:.*?\\d))(?=(?:.*?[a-z]))(?=(?:.*?[A-Z]))(?=(?:.*?[!@#$%&*()_+^}{:;?.\\[\\]}{:;?.]))[A-Za-z\\d$!@#$%&*()_+^}{:;?.\\[\\]]*$";
    public static String NORMAL_PATTERN = "^[a-zA-Z0-9][a-zA-Z0-9#\\.\\-_\\(\\)\\[\\]\\$\\%\\@\\+=\\^\\!\\&\\*\\}\\{\\?;:]*$";

}
