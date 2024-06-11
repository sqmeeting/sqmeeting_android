package frtc.sdk.ui.model;

import android.util.Base64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import frtc.sdk.model.MeetingRoom;
import frtc.sdk.model.ScheduledMeeting;
import frtc.sdk.model.UserInfo;
import frtc.sdk.log.Log;
import frtc.sdk.util.Constants;
import frtc.sdk.util.StringUtils;

public class LocalStore {

    private String server = "";

    private UserIdentity userIdentify = UserIdentity.GUEST;

    private String userId = "";
    private String userName = "";
    private String clientId = "";
    private String userToken = "";
    private String realName;
    private String email;
    private String department;
    private String mobile;
    private String securityLevel;
    private Set<String> role = new HashSet<>();

    private boolean isAudioCall = false;
    private String callRate = "0";

    private boolean audioOn = false;
    private boolean cameraOn = false;
    private boolean isVideoMuteDisabled = false;

    private String meetingID = "";
    private String meetingName = "";
    private String meetingOwnerId = "";
    private String meetingOwnerName = "";
    private String meetingPassword = "";
    long scheduleStartTime = 0;
    long scheduleEndTime = 0;
    private String meetingURl = "";

    private String lastUserName = "";
    private String clearPassword = null;

    private String displayName = "";

    private boolean rememberSignInfo = false;
    private boolean rememberName = true;
    private boolean rememberPwd = false;

    private boolean autoSignIn = true;
    private String passwordEnc;
    private HashMap<String, String> userPwdMap = new HashMap<>();

    private ScheduledMeetingSetting scheduledMeetingSetting;

    private ArrayList<ScheduledMeeting> scheduledMeetings;
    private boolean isScheduledMeetingFullList;
    private List<UserInfo> users;

    private String userFilter = "";

    private boolean isUsersFullList;
    private String livePassword;
    private String liveMeetingUrl;

    private boolean isMeetingReminder = true;
    private boolean noiseBlock = true;

    private boolean isShowCallFloat = false;

    private Map<String, Long> mapMeetingReminder = new HashMap<>();

    private Constants.SdkType sdkType = Constants.SdkType.SDK_TYPE_SQ;

    private String meetingType;
    private long elapsedRealtime;
    private boolean isSharingContent = false;

    private boolean isRemoteVideoMuted = false;

    public boolean isRemoteVideoMuted() {
        return isRemoteVideoMuted;
    }

    public void setRemoteVideoMuted(boolean remoteVideoMuted) {
        isRemoteVideoMuted = remoteVideoMuted;
    }

    public boolean isSharingContent() {
        return isSharingContent;
    }

    public void setSharingContent(boolean sharingContent) {
        isSharingContent = sharingContent;
    }

    public long getElapsedRealtime() {
        return elapsedRealtime;
    }

    public void setElapsedRealtime(long elapsedRealtime) {
        this.elapsedRealtime = elapsedRealtime;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }

    public Constants.SdkType getSdkType() {
        return sdkType;
    }

    public void setSdkType(Constants.SdkType sdkType) {
        this.sdkType = sdkType;
    }

    public boolean isShowCallFloat() {
        return isShowCallFloat;
    }

    public void setShowCallFloat(boolean showCallFloat) {
        isShowCallFloat = showCallFloat;
    }

    public void addMeetingReminder (String meetingID, long startTime) {
        mapMeetingReminder.put(meetingID, startTime);
    }

    public void removeMeetingReminder(String meetingID) {
        mapMeetingReminder.remove(meetingID);
    }

    public long getMapMeetingStartTime(String meetingID) {
        long startTime = -1;
        if (mapMeetingReminder != null && mapMeetingReminder.containsKey(meetingID)) {
            startTime = mapMeetingReminder.get(meetingID);
        }

        return startTime;
    }

    public void clearMeetingReminder() {
        if (mapMeetingReminder != null) {
            mapMeetingReminder.clear();
        }
    }

    public boolean isNoiseBlock() {
        return noiseBlock;
    }

    public void setNoiseBlock(boolean noiseBlock) {
        this.noiseBlock = noiseBlock;
    }

    public boolean isMeetingReminder() {
        return isMeetingReminder;
    }

    public void setMeetingReminder(boolean meetingReminder) {
        isMeetingReminder = meetingReminder;
    }

    public String getLivePassword() {
        return livePassword;
    }

    public void setLivePassword(String livePassword) {
        this.livePassword = livePassword;
    }

    public String getLiveMeetingUrl() {
        return liveMeetingUrl;
    }

    public void setLiveMeetingUrl(String liveMeetingUrl) {
        this.liveMeetingUrl = liveMeetingUrl;
    }

    public ScheduledMeetingSetting getScheduledMeetingSetting() {
        if(scheduledMeetingSetting == null){
            scheduledMeetingSetting = new ScheduledMeetingSetting();
        }
        return scheduledMeetingSetting;
    }

    public void resetScheduledMeetingSetting(){
        Log.d("LocalStore","resetScheduledMeetingSetting");
        this.scheduledMeetingSetting = null;
    }

    public void setScheduledMeetingSetting(ScheduledMeetingSetting scheduledMeetingSetting) {
        this.scheduledMeetingSetting = scheduledMeetingSetting;
    }

    public void clearScheduledMeetings(){
        if(scheduledMeetings != null){
            scheduledMeetings.clear();
        }
    }

    public boolean isScheduledMeetingFullList() {
        return isScheduledMeetingFullList;
    }

    public void setScheduledMeetingFullList(boolean scheduledMeetingFullList) {
        isScheduledMeetingFullList = scheduledMeetingFullList;
    }

    public void setScheduledMeetings(ArrayList<ScheduledMeeting> scheduledMeetings) {
        this.scheduledMeetings = scheduledMeetings;
    }

    public ArrayList<ScheduledMeeting> getScheduledMeetings() {
        if(scheduledMeetings == null){
            scheduledMeetings = new ArrayList<ScheduledMeeting>();
        }
        return scheduledMeetings;
    }

    public String getUserFilter() {
        return userFilter;
    }

    public void setUserFilter(String userFilter) {
        this.userFilter = userFilter;
    }

    public List<UserInfo> getUsers() {
        if(users == null){
            users = new ArrayList<>();
        }
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }

    public void clearUsers(){
        if( users != null){
            users.clear();
        }
    }

    public boolean isUsersFullList() {
        return isUsersFullList;
    }

    public void setUsersFullList(boolean usersFullList) {
        isUsersFullList = usersFullList;
    }

    public boolean isAutoSignIn() {
        return autoSignIn;
    }

    public void setAutoSignIn(boolean autoSignIn) {
        this.autoSignIn = autoSignIn;
    }

    enum UserIdentity {
        USER,
        GUEST
    }

    public UserIdentity getUserIdentify() {
        return userIdentify;
    }

    public void setUserIdentify(UserIdentity userIdentify) {
        this.userIdentify = userIdentify;
    }

    public long getScheduleStartTime() {
        return scheduleStartTime;
    }

    public void setScheduleStartTime(long scheduleStartTime) {
        this.scheduleStartTime = scheduleStartTime;
    }

    public long getScheduleEndTime() {
        return scheduleEndTime;
    }

    public void setScheduleEndTime(long scheduleEndTime) {
        this.scheduleEndTime = scheduleEndTime;
    }

    public String getMeetingURl() {
        return meetingURl;
    }

    public void setMeetingURl(String meetingURl) {
        this.meetingURl = meetingURl;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingOwnerId() {
        return meetingOwnerId;
    }

    public void setMeetingOwnerId(String meetingOwnerId) {
        this.meetingOwnerId = meetingOwnerId;
        Set<String> roles = getRole();
        roles.remove(RoleConstant.MEETING_OWNER);
        if(meetingOwnerId.equals(userId)){
            roles.add(RoleConstant.MEETING_OWNER);
        }
    }

    public String getMeetingOwnerName() {
        return meetingOwnerName;
    }

    public void setMeetingOwnerName(String meetingOwnerName) {
        this.meetingOwnerName = meetingOwnerName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public void clearRole(){
        if (role != null){
            role.clear();
        }
    }

    public void clearMeetingInfo() {
        this.meetingID = "";
        this.meetingOwnerName = "";
        this.meetingOwnerId = "";
        this.meetingName = "";
        this.meetingPassword = "";
        this.scheduleStartTime = 0;
        this.scheduleEndTime = 0;
        this.meetingURl = "";
        this.meetingType = "";
    }

    public boolean isHost(){
        if(isLogged()){
            if(role!=null && !role.isEmpty()){
                for(String roleItem:role){
                    Log.d("LocalStore","role:"+roleItem);
                    if(RoleConstant.MEETING_OPERATOR.equals(roleItem) || RoleConstant.SYSTEM_ADMIN.equals(roleItem)
                            || RoleConstant.MEETING_OWNER.equals(roleItem)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isOwnerOrAdmin(){
        if(isLogged()){
            if(role!=null && !role.isEmpty()){
                for(String roleItem:role){
                    Log.d("LocalStore","role:"+roleItem);
                    if(RoleConstant.MEETING_OWNER.equals(roleItem) || RoleConstant.SYSTEM_ADMIN.equals(roleItem)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isOperatorOrAdmin(){
        if(isLogged()){
            if(role!=null && !role.isEmpty()){
                for(String roleItem:role){
                    Log.d("LocalStore","role:"+roleItem);
                    if(RoleConstant.MEETING_OPERATOR.equals(roleItem) || RoleConstant.SYSTEM_ADMIN.equals(roleItem)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getLastUserName() {
        return lastUserName;
    }

    public void setLastUserName(String lastUserName) {
        this.lastUserName = lastUserName;
    }

    private ArrayList<MeetingRoom> meetingRooms = new ArrayList<>();
    private Map<String, ArrayList<MeetingCall>> historyMeetingMap = new HashMap<>();

    private String getHistoryMeetingKey(String userName, String server){
        return userName+"@"+server;
    }

    public ArrayList<MeetingCall> getHistoryMeetings() {
        ArrayList<MeetingCall> historyMeetings;
        if (StringUtils.isNotBlank(this.userName) && StringUtils.isNotBlank(this.server)){
            String key = getHistoryMeetingKey(this.userName,this.server);
            historyMeetings = historyMeetingMap.get(key);
            if (historyMeetings == null) {
                historyMeetings = new ArrayList<>();
                historyMeetingMap.put(key, historyMeetings);
            }
            return historyMeetings;
        }else{
            return new ArrayList<>();
        }
    }

    public void clearHistoryMeetings(){
        if (StringUtils.isNotBlank(this.userName) && StringUtils.isNotBlank(this.server)) {
            historyMeetingMap.remove(getHistoryMeetingKey(this.userName,this.server));
        }
    }

    public void addHistoryMeeting(MeetingCall meetingCall){
        if (StringUtils.isNotBlank(this.userName) && StringUtils.isNotBlank(this.server)) {
            String key = getHistoryMeetingKey(this.userName,this.server);
            ArrayList<MeetingCall> meetingCallsTemp = historyMeetingMap.get(key);
            if (meetingCallsTemp == null) {
                meetingCallsTemp = new ArrayList<>();
                historyMeetingMap.put(key, meetingCallsTemp);
            }
            meetingCallsTemp.add(0,meetingCall);
            Log.d("LocalStore","addHistoryMeeting:"+meetingCall.getMeetingNumber()+","+meetingCall.getServerAddress());
        }
    }

    public void deleteHistoryMeeting(String meetingNumber, String meetingTime){
        if (StringUtils.isNotBlank(this.userName) && StringUtils.isNotBlank(this.server) && StringUtils.isNotBlank(meetingNumber)) {
            ArrayList<MeetingCall> historyMeetings = historyMeetingMap.get(getHistoryMeetingKey(this.userName,this.server));
            if (historyMeetings == null) {
                return ;
            }
            for (MeetingCall meetingCall: historyMeetings){
                if(meetingCall.getMeetingNumber().equals(meetingNumber) && meetingCall.getTime().equals(meetingTime)){
                    historyMeetings.remove(meetingCall);
                    break;
                }
            }
        }
    }

    public MeetingCall getMeetingInfoByMeetingNumber(String meetingNumber){
        if (StringUtils.isNotBlank(this.userName) && StringUtils.isNotBlank(this.server) && StringUtils.isNotBlank(meetingNumber)) {
            ArrayList<MeetingCall> historyMeetings = historyMeetingMap.get(getHistoryMeetingKey(this.userName,this.server));
            Log.d("LocalStore","getMeetingInfoByMeetingNumber:"+historyMeetings);
            if (historyMeetings == null) {
                return null;
            }
            for (MeetingCall meetingCall: historyMeetings){
                Log.d("LocalStore","getMeetingInfoByMeetingNumber:"+meetingCall.getMeetingNumber()+","+meetingCall.getCreateTime());
                if(meetingCall.getMeetingNumber().equals(meetingNumber)){
                    return meetingCall;
                }
            }
        }
        return null;
    }

    public boolean isLogged(){
        return userToken != null && !userToken.isEmpty();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void clearMeetingRooms(){
        meetingRooms.clear();
    }

    public ArrayList<MeetingRoom> getMeetingRooms() {
        return meetingRooms;
    }

    public void setMeetingRooms(ArrayList<MeetingRoom> meetingRooms) {
        this.meetingRooms = meetingRooms;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getCallRate() {
        return callRate;
    }

    public void setCallRate(String callRate) {
        this.callRate = callRate;
    }

    public boolean isRememberName() {
        return rememberName;
    }

    public void setRememberName(boolean remember) {
        this.rememberName = remember;
    }

    public boolean isAudioOn() {
        return audioOn;
    }

    public void setAudioOn(boolean on) {
        this.audioOn = on;
    }

    public boolean isCameraOn() {
        return cameraOn;
    }

    public void setCameraOn(boolean on) {
        this.cameraOn = on;
    }

    public boolean isVideoMuteDisabled() {
        return isVideoMuteDisabled;
    }

    public void setVideoMuteDisabled(boolean videoMuteDisabled) {
        isVideoMuteDisabled = videoMuteDisabled;
    }

    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String strId) {
        this.meetingID = strId;
    }

    public String getMeetingPassword(){
        return meetingPassword;
    }

    public void setMeetingPassword(String strPw){
        if (strPw == null) {
            this.meetingPassword = "";
        } else {
            this.meetingPassword = strPw;
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserClearPassword(String clearPassword) {
        this.clearPassword = clearPassword;
    }

    public String getUserClearPassword() {
        return clearPassword;
    }

    public void setSignInRemembered(boolean remembered) {
        this.rememberSignInfo = remembered;
    }

    public boolean getSignInRemembered() {
        return rememberSignInfo;
    }

    public boolean isAudioCall() {
        return isAudioCall;
    }

    public void setAudioCall(boolean audioCall) {
        isAudioCall = audioCall;
    }

    public void setUserToken(String token) {
        this.userToken = token;
    }

    public String getUserToken(){
        return userToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }


    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean isRememberPwd() {
        return rememberPwd;
    }

    public void setRememberPwd(boolean remember) {
        this.rememberPwd = remember;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void addUserPwd(String key, String password) {
        userPwdMap.put(key, password);
    }

    public void removeUserPassword(String key) {
        userPwdMap.remove(key);
    }

    public String getUserEncryptPassword(String key) {
        String password = null;
        if (userPwdMap != null) {
            password = userPwdMap.get(key);
        }

        return password;
    }

    public void clearUserPwd() {
        if (userPwdMap != null) {
            userPwdMap.clear();
        }
    }

    public static final String PASSWORD_ENC_KEY = "FrtcENCkeyPasswd";

    public String encryptPassword(String clearText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(PASSWORD_ENC_KEY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            String encrypedPwd = Base64.encodeToString(cipher.doFinal(clearText.getBytes("UTF-8")), Base64.DEFAULT);

            return encrypedPwd;

        } catch (Exception e) {
            Log.e("LocalStore", "" + e);
        }
        return clearText;
    }

    public String decryptPassword(String encryptedPwd) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(PASSWORD_ENC_KEY.getBytes("UTF-8"), "AES");

            byte[] encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        } catch (Exception e) {
            Log.e("LocalStore", "" + e);
        }

        return encryptedPwd;
    }
}
