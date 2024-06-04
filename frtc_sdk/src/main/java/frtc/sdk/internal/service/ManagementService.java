package frtc.sdk.internal.service;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import frtc.sdk.model.AllowUnmuteParam;
import frtc.sdk.model.BaseParam;
import frtc.sdk.model.CommonMeetingParam;
import frtc.sdk.model.CreateInstantMeetingResult;
import frtc.sdk.model.CreateScheduledMeetingParam;
import frtc.sdk.model.CreateScheduledMeetingResult;
import frtc.sdk.model.DeleteScheduledMeetingParam;
import frtc.sdk.model.DisconnectAllParticipantsParam;
import frtc.sdk.model.DisconnectParticipantParam;
import frtc.sdk.model.FindUserParam;
import frtc.sdk.FrtcManagement;
import frtc.sdk.model.GetScheduledMeetingListParam;
import frtc.sdk.model.GetScheduledMeetingParam;
import frtc.sdk.IFrtcManagementListener;
import frtc.sdk.IManagementService;
import frtc.sdk.model.InstantMeetingParam;
import frtc.sdk.model.LiveMeetingParam;
import frtc.sdk.model.MuteAllParam;
import frtc.sdk.model.MuteParam;
import frtc.sdk.model.PasswordUpdateParam;
import frtc.sdk.model.PinForMeetingParam;
import frtc.sdk.model.QueryMeetingInfoParam;
import frtc.sdk.model.QueryMeetingInfoResult;
import frtc.sdk.model.QueryMeetingRoomParam;
import frtc.sdk.model.QueryMeetingRoomResult;
import frtc.sdk.model.QueryUserInfoParam;
import frtc.sdk.model.QueryUserInfoResult;
import frtc.sdk.model.RecurrenceMeetingListResult;
import frtc.sdk.model.ScheduledMeetingListResult;
import frtc.sdk.model.ScheduledMeetingResult;
import frtc.sdk.model.SignInParam;
import frtc.sdk.model.SignInResult;
import frtc.sdk.model.SignOutParam;
import frtc.sdk.model.StartOverlayParam;
import frtc.sdk.model.StopMeetingParam;
import frtc.sdk.model.StopOverlayParam;
import frtc.sdk.model.UnMuteAllParam;
import frtc.sdk.model.UnMuteParam;
import frtc.sdk.model.UpdateScheduledMeetingParam;
import frtc.sdk.model.AddMeetingToListParam;
import frtc.sdk.model.ChangeDisplayNameParam;
import frtc.sdk.model.LectureParam;
import frtc.sdk.conf.SDKConfigManager;
import frtc.sdk.ISignListener;
import frtc.sdk.internal.ITask;
import frtc.sdk.internal.ITaskResult;
import frtc.sdk.internal.model.CommonResponse;
import frtc.sdk.internal.model.CreateInstantMeetingResponse;
import frtc.sdk.internal.model.CreateScheduledMeetingRequest;
import frtc.sdk.internal.model.FindUserResult;
import frtc.sdk.internal.model.FrtcSDKMeetingType;
import frtc.sdk.internal.model.InstantMeetingRequest;
import frtc.sdk.internal.model.ResultType;
import frtc.sdk.internal.model.UpdateScheduledMeetingRequest;
import frtc.sdk.log.Log;
import frtc.sdk.util.FrtcHttpClient;
import frtc.sdk.util.JSONUtil;
import orgfrtc.apache.commons.codec.digest.DigestUtils;

public class ManagementService implements IManagementService {

    protected final String TAG = this.getClass().getSimpleName();

    private FrtcManagement frtcManagement;
    private Context context = null;

    private List<IFrtcManagementListener> managementListeners = new CopyOnWriteArrayList<>();
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_TOKEN = "token";
    private static final String CONTEXT_SIGN_IN = "user/sign_in";
    private static final String CONTEXT_SIGN_OUT = "user/sign_out";
    private static final String CONTEXT_PASSWORD = "user/password";
    private static final String CONTEXT_USER_INFO = "user/info";
    private static final String CONTEXT_USERS = "user/public/users";

    private static final String CONTEXT_MEETING_SCHEDULE= "meeting_schedule";
    private static final String CONTEXT_MEETING_SCHEDULE_RESERVATION ="meeting_schedule/%s";
    private static final String CONTEXT_MEETING_SCHEDULE_RECURRENCE ="meeting_schedule/recurrence/%s";

    private static final String CONTEXT_MEETING_ROOM = "meeting_room";
    private static final String CONTEXT_MEETING_INFO = "mt/%s";

    private static final String CONTEXT_MUTE = "meeting/%s/mute";
    private static final String CONTEXT_UNMUTE = "meeting/%s/unmute";
    private static final String CONTEXT_CHANGE_DISPLAYNAME= "meeting/%s/participant";
    private static final String CONTEXT_LECTURER= "meeting/%s/lecturer";
    private static final String CONTEXT_MUTEALL = "meeting/%s/mute_all";
    private static final String CONTEXT_UNMUTEALL = "meeting/%s/unmute_all";
    private static final String CONTEXT_STOP_MEETING = "meeting/%s/stop";
    private static final String CONTEXT_OVERLAY = "meeting/%s/overlay";
    private static final String CONTEXT_DISCONNECT_PARTICIPANT = "meeting/%s/disconnect";
    private static final String CONTEXT_DISCONNECT_ALL_PARTICIPANT = "meeting/%s/disconnect_all";
    private static final String CONTEXT_RECORDING = "meeting/%s/recording";
    private static final String CONTEXT_LIVE = "meeting/%s/live";
    private static final String CONTEXT_SELF_UNMUTE = "meeting/%s/request_unmute";
    private static final String CONTEXT_ALLOW_UNMUTE = "meeting/%s/allow_unmute";
    private static final String CONTEXT_PIN = "meeting/%s/pin";
    private static final String CONTEXT_UNPIN = "meeting/%s/unpin";
    private static final String CONTEXT_MEETING_LIST_ADD ="meeting_list/add/%s";
    private static final String CONTEXT_MEETING_LIST_AREMOVE ="meeting_list/remove/%s";

    public static final String SALT = "49d88eb34f77fc9e81cbdc5190c7efdc";

    private static final String KEY_PAGE_NUM = "page_num";
    private static final String KEY_PAGE_SIZE = "page_size";
    private static final String KEY_FILTER = "filter";
    private static final String KEY_SORT = "sort";

    private static final String KEY_DELETE_GROUP = "deleteGroup";

    public ManagementService(FrtcManagement frtcManagement) {
        this.frtcManagement = frtcManagement;
        this.context = this.frtcManagement.getContext();
    }

    @Override
    public String getSDKVersion() {
        return SDKConfigManager.getInstance(this.context).getSDKVersion();
    }

    @Override
    public void registerManagementListener(IFrtcManagementListener listener) {
        managementListeners.add(listener);
    }

    @Override
    public void unRegisterManagementListener(IFrtcManagementListener listener) {
        managementListeners.remove(listener);
    }

    @Override
    public void shutdown() {
        managementListeners.clear();
        FrtcTaskPoolManager.getInstance().shutdown();
    }

    private void broadcastPinForMeeting(ResultType resultType){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onPinForMeeting(resultType);
        }
    }

    private void broadcastUnpinForMeeting(ResultType resultType){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onUnPinForMeeting(resultType);
        }
    }

    private void broadcastStartRecording(ResultType resultType, String errorCode){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onStartRecording(resultType, errorCode);
        }
    }

    private void broadcastStopRecording(ResultType resultType){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onStopRecording(resultType);
        }
    }

    private void broadcastStartLive(ResultType resultType, String errorCode){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onStartLive(resultType, errorCode);
        }
    }

    private void broadcastStopLive(ResultType resultType){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onStopLive(resultType);
        }
    }

    private void broadcastDisconnectParticipants(ResultType resultType){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onDisconnectParticipants(resultType);
        }
    }

    private void broadcastDisconnectAllParticipants(ResultType resultType){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onDisconnectAllParticipants(resultType);
        }
    }

    private void broadcastStartOverlay(ResultType resultType){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onStartOverlay(resultType);
        }
    }

    private void broadcastStopOverlay(ResultType resultType){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onStopOverlay(resultType);
        }
    }

    private void broadcastStopMeetingResult(ResultType resultType){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onStopMeetingResult(resultType);
        }
    }

    private void broadcastChangeDisplayNameResult(ResultType resultType, String name, boolean isMe){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onChangeDisplayNameResult(resultType, name, isMe);
        }
    }

    private void broadcastSetLecturerResult(ResultType resultType, boolean isSetLecturer){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onSetLecturerResult(resultType, isSetLecturer);
        }
    }

    private void broadcastSignInResult(ResultType resultType, SignInResult result, ISignListener signListener) {
        if(signListener == null) {
            for (IFrtcManagementListener listener : managementListeners) {
                listener.onSignInResult(resultType, result);
            }
        }else {
            signListener.onSignInResult(resultType, result);
        }
    }

    private void broadcastSignOutResult(ResultType resultType) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onSignOutResult(resultType);
        }
    }

    private void broadcastQueryUserInfoResult(ResultType resultType, QueryUserInfoResult queryUserInfoResult) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onQueryUserInfoResult(resultType, queryUserInfoResult);
        }
    }

    private void broadcastPasswordUpdateResult(ResultType resultType) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onUpdatePasswordResult(resultType);
        }
    }

    private void broadcastCreateInstantMeetingResult(ResultType resultType, CreateInstantMeetingResult createInstantMeetingResult) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onCreateInstantMeetingResult(resultType, createInstantMeetingResult);
        }
    }

    private void broadcastMuteAllResult(ResultType resultType, boolean allowUnMute) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onMuteAllParticipantResult(resultType, allowUnMute);
        }
    }

    private void broadcastUnMuteAllResult(ResultType resultType) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onUnMuteAllParticipantResult(resultType);
        }
    }

    private void broadcastMuteResult(ResultType resultType) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onMuteParticipantResult(resultType);
        }
    }

    private void broadcastUnMuteResult(ResultType resultType) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onUnMuteParticipantResult(resultType);
        }
    }

    private void broadcastQueryMeetingRoomInfoResult(ResultType resultType, QueryMeetingRoomResult queryMeetingRoomResult) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onQueryMeetingRoomInfoResult(resultType, queryMeetingRoomResult);
        }
    }

    private void broadcastQueryMeetingInfoResult(ResultType resultType, QueryMeetingInfoResult queryMeetingInfoResult) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onQueryMeetingInfoResult(resultType, queryMeetingInfoResult);
        }
    }

    private void broadcastCreateScheduledMeetingResult(ResultType resultType, CreateScheduledMeetingResult createScheduledMeetingResult){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onCreateScheduledMeetingResult(resultType,createScheduledMeetingResult);
        }
    }

    private void broadcastUpdateScheduledMeetingResult(ResultType resultType){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onUpdateScheduledMeetingResult(resultType);
        }
    }

    private void broadcastDeleteScheduledMeetingResult(ResultType resultType){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onDeleteScheduledMeetingResult(resultType);
        }
    }

    private void broadcastGetScheduledMeetingResult(ResultType resultType, ScheduledMeetingResult scheduledMeetingResult){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onGetScheduledMeetingResult(resultType, scheduledMeetingResult);
        }
    }

    private void broadcastGetScheduledMeetingListResult(ResultType resultType, ScheduledMeetingListResult scheduledMeetingListResult){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onGetScheduledMeetingListResult(resultType, scheduledMeetingListResult);
        }
    }

    private void broadcastGetScheduledRecurrenceMeetingListResult(ResultType resultType, RecurrenceMeetingListResult recurrenceMeetingListResult){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onGetScheduledRecurrenceMeetingListResult(resultType, recurrenceMeetingListResult);
        }
    }

    private void broadcastFindUserResult(ResultType resultType, FindUserResult findUserResult){
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onFindUserResultResult(resultType, findUserResult);
        }
    }

    private String getServerAddress(BaseParam param) {
        String configAddress = FrtcManagement.getInstance().getServerAddress();
        if (configAddress != null && !configAddress.isEmpty()) {
            return configAddress;
        }
        return param.getServerAddress();
    }

    private String getMeetingServerAddress(BaseParam param) {
        String serverAddr = param.getServerAddress();
        if(TextUtils.isEmpty(serverAddr)){
            String configAddress = FrtcManagement.getInstance().getServerAddress();
            if (configAddress != null && !configAddress.isEmpty()) {
                return configAddress;
            }
        }
        return serverAddr;
    }

    @Override
    public void signIn(final SignInParam signInParam, ISignListener signListener) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, signInParam.getClientId());
                String url = FrtcHttpClient.buildUrl(getServerAddress(signInParam), CONTEXT_SIGN_IN, params);
                signInParam.setPassword(DigestUtils.sha1Hex(signInParam.getPassword() + SALT));
                String data = JSONUtil.toJSONString(signInParam);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            Log.d(TAG,"signIn resultType = "+resultType);
                            SignInResult signInResult = JSONUtil.transform(result, SignInResult.class);
                            broadcastSignInResult(resultType, signInResult, signListener);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void signOut(final SignOutParam signOutParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, signOutParam.getClientId());
                params.put(KEY_TOKEN, signOutParam.getToken());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(signOutParam), CONTEXT_SIGN_OUT, params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastSignOutResult(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void queryUserInfo(final QueryUserInfoParam queryUserInfoParam) {

        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, queryUserInfoParam.getClientId());
                params.put(KEY_TOKEN, queryUserInfoParam.getToken());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(queryUserInfoParam), CONTEXT_USER_INFO, params);
                try {
                    FrtcHttpClient.getInstance(context).asyncGet(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            QueryUserInfoResult queryUserInfoResult = JSONUtil.transform(result, QueryUserInfoResult.class);
                            broadcastQueryUserInfoResult(resultType, queryUserInfoResult);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void updatePassword(final PasswordUpdateParam passwordUpdateParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, passwordUpdateParam.getClientId());
                params.put(KEY_TOKEN, passwordUpdateParam.getToken());
                passwordUpdateParam.setPasswordOld(DigestUtils.sha1Hex(passwordUpdateParam.getPasswordOld() + SALT));
                passwordUpdateParam.setPasswordNew(DigestUtils.sha1Hex(passwordUpdateParam.getPasswordNew() + SALT));
                String data = JSONUtil.toJSONString(passwordUpdateParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(passwordUpdateParam), CONTEXT_PASSWORD, params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPut(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastPasswordUpdateResult(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void createInstantMeeting(final InstantMeetingParam instantMeetingParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, instantMeetingParam.getClientId());
                params.put(KEY_TOKEN, instantMeetingParam.getToken());
                InstantMeetingRequest instantMeetingRequest = new InstantMeetingRequest();
                instantMeetingRequest.setMeeting_name(instantMeetingParam.getMeetingName());
                instantMeetingRequest.setMeeting_type(FrtcSDKMeetingType.INSTANT.getTypeName());
                String data = JSONUtil.toJSONString(instantMeetingRequest);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(instantMeetingParam), CONTEXT_MEETING_SCHEDULE, params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            CreateInstantMeetingResponse response = JSONUtil.transform(result, CreateInstantMeetingResponse.class);
                            CreateInstantMeetingResult createInstantMeetingResult = new CreateInstantMeetingResult();
                            createInstantMeetingResult.setMeetingName(response.getMeeting_name());
                            createInstantMeetingResult.setMeetingNumber(response.getMeeting_number());
                            createInstantMeetingResult.setMeetingPassword(response.getMeeting_password());
                            createInstantMeetingResult.setOwnerName(response.getOwner_name());
                            createInstantMeetingResult.setMeeting_type(response.getMeeting_type());
                            broadcastCreateInstantMeetingResult(resultType, createInstantMeetingResult);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void createScheduledMeeting(final CreateScheduledMeetingParam createScheduledMeetingParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, createScheduledMeetingParam.getClientId());
                params.put(KEY_TOKEN, createScheduledMeetingParam.getToken());
                CreateScheduledMeetingRequest createScheduledMeetingRequest = new CreateScheduledMeetingRequest();
                createScheduledMeetingRequest.setParam(createScheduledMeetingParam);
                String url = "";
                if(createScheduledMeetingParam.getMeeting_type().equals(FrtcSDKMeetingType.RECURRENCE.getTypeName())){
                    createScheduledMeetingRequest.setMeeting_type(FrtcSDKMeetingType.RECURRENCE.getTypeName());
                    url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(createScheduledMeetingParam), CONTEXT_MEETING_SCHEDULE_RESERVATION, FrtcSDKMeetingType.RECURRENCE.getTypeName(), params);
                }else {
                    createScheduledMeetingRequest.setMeeting_type(FrtcSDKMeetingType.RESERVATION.getTypeName());
                    url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(createScheduledMeetingParam), CONTEXT_MEETING_SCHEDULE, params);
                }
                String data = JSONUtil.toJSONString(createScheduledMeetingRequest);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            CreateScheduledMeetingResult createScheduledMeetingResult = JSONUtil.transform(result, CreateScheduledMeetingResult.class);
                            broadcastCreateScheduledMeetingResult(resultType, createScheduledMeetingResult);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void updateScheduledMeeting(final UpdateScheduledMeetingParam updateScheduledMeetingParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, updateScheduledMeetingParam.getClientId());
                params.put(KEY_TOKEN, updateScheduledMeetingParam.getToken());
                UpdateScheduledMeetingRequest updateScheduledMeetingRequest = new UpdateScheduledMeetingRequest();
                updateScheduledMeetingRequest.setParam(updateScheduledMeetingParam);
                updateScheduledMeetingRequest.setMeeting_type(updateScheduledMeetingParam.getMeeting_type());
                String data = JSONUtil.toJSONString(updateScheduledMeetingRequest);
                String url = "";
                if(updateScheduledMeetingParam.getMeeting_type().equals(FrtcSDKMeetingType.RECURRENCE.getTypeName()) && !updateScheduledMeetingParam.isSingle()){
                    url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(updateScheduledMeetingParam), CONTEXT_MEETING_SCHEDULE_RECURRENCE, updateScheduledMeetingParam.getReservationId(), params);
                }else {
                    url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(updateScheduledMeetingParam), CONTEXT_MEETING_SCHEDULE_RESERVATION, updateScheduledMeetingParam.getReservationId(), params);
                }
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastUpdateScheduledMeetingResult(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }


    @Override
    public void deleteScheduledMeeting(final DeleteScheduledMeetingParam deleteScheduledMeetingParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, deleteScheduledMeetingParam.getClientId());
                params.put(KEY_TOKEN, deleteScheduledMeetingParam.getToken());
                params.put(KEY_DELETE_GROUP, deleteScheduledMeetingParam.getDeleteGroup());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(deleteScheduledMeetingParam), CONTEXT_MEETING_SCHEDULE_RESERVATION, deleteScheduledMeetingParam.getReservationId(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncDelete(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastDeleteScheduledMeetingResult(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }
    @Override
    public void getScheduledMeeting(final GetScheduledMeetingParam getScheduledMeetingParam){

        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, getScheduledMeetingParam.getClientId());
                params.put(KEY_TOKEN, getScheduledMeetingParam.getToken());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(getScheduledMeetingParam), CONTEXT_MEETING_SCHEDULE_RESERVATION, getScheduledMeetingParam.getReservationId(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncGet(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            ScheduledMeetingResult scheduledMeetingResult = JSONUtil.transform(result, ScheduledMeetingResult.class);
                            broadcastGetScheduledMeetingResult(resultType,scheduledMeetingResult);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }
    @Override
    public void getScheduledMeetingList(final GetScheduledMeetingListParam getScheduledMeetingListParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, getScheduledMeetingListParam.getClientId());
                params.put(KEY_TOKEN, getScheduledMeetingListParam.getToken());
                params.put(KEY_PAGE_NUM,getScheduledMeetingListParam.getPage_num());
                params.put(KEY_PAGE_SIZE,getScheduledMeetingListParam.getPage_size());
                params.put(KEY_FILTER,getScheduledMeetingListParam.getFilter());
                params.put(KEY_SORT, "startTime");
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(getScheduledMeetingListParam), CONTEXT_MEETING_SCHEDULE, params);
                try {
                    FrtcHttpClient.getInstance(context).asyncGet(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            ScheduledMeetingListResult scheduledMeetingListResult = JSONUtil.transform(result, ScheduledMeetingListResult.class);
                            broadcastGetScheduledMeetingListResult(resultType,scheduledMeetingListResult);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void getScheduledRecurrenceMeetingList(final GetScheduledMeetingListParam getScheduledMeetingListParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, getScheduledMeetingListParam.getClientId());
                params.put(KEY_TOKEN, getScheduledMeetingListParam.getToken());
                params.put(KEY_PAGE_NUM,getScheduledMeetingListParam.getPage_num());
                params.put(KEY_PAGE_SIZE,getScheduledMeetingListParam.getPage_size());
                params.put(KEY_FILTER,getScheduledMeetingListParam.getFilter());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(getScheduledMeetingListParam), CONTEXT_MEETING_SCHEDULE_RESERVATION,
                        FrtcSDKMeetingType.RECURRENCE.getTypeName() + "/" + getScheduledMeetingListParam.getRecurrence_gid(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncGet(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            RecurrenceMeetingListResult recurrenceMeetingListResult = JSONUtil.transform(result, RecurrenceMeetingListResult.class);
                            broadcastGetScheduledRecurrenceMeetingListResult(resultType,recurrenceMeetingListResult);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void findUser(final FindUserParam findUSerParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, findUSerParam.getClientId());
                params.put(KEY_TOKEN, findUSerParam.getToken());
                params.put(KEY_PAGE_NUM,findUSerParam.getPage_num());
                params.put(KEY_PAGE_SIZE,findUSerParam.getPage_size());
                params.put(KEY_FILTER,findUSerParam.getFilter());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(findUSerParam), CONTEXT_USERS, params);
                try {
                    FrtcHttpClient.getInstance(context).asyncGet(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            FindUserResult findUserResult = JSONUtil.transform(result, FindUserResult.class);
                            broadcastFindUserResult(resultType, findUserResult);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void unMuteSelf(final CommonMeetingParam commonMeetingParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, commonMeetingParam.getClientId());
                params.put(KEY_TOKEN, commonMeetingParam.getToken());
                String data = JSONUtil.toJSONString(commonMeetingParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getMeetingServerAddress(commonMeetingParam), CONTEXT_SELF_UNMUTE, commonMeetingParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastUnMuteSelfResult(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void allowUnmute(final AllowUnmuteParam allowUnmuteParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, allowUnmuteParam.getClientId());
                params.put(KEY_TOKEN, allowUnmuteParam.getToken());
                String data = JSONUtil.toJSONString(allowUnmuteParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(allowUnmuteParam), CONTEXT_ALLOW_UNMUTE, allowUnmuteParam.getMeeting_number(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastAllowUnmuteResult(resultType, allowUnmuteParam.getParticipants());
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    private void broadcastAllowUnmuteResult(ResultType resultType, List<String> participants) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onAllowUnmuteResult(resultType, participants);
        }
    }

    private void broadcastUnMuteSelfResult(ResultType resultType) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onUnMuteSelfResult(resultType);
        }
    }

    @Override
    public void muteAllParticipant(final MuteAllParam muteAllParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, muteAllParam.getClientId());
                params.put(KEY_TOKEN, muteAllParam.getToken());
                String data = JSONUtil.toJSONString(muteAllParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(muteAllParam), CONTEXT_MUTEALL, muteAllParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastMuteAllResult(resultType, muteAllParam.isAllow_self_unmute());
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void muteParticipant(final MuteParam muteParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, muteParam.getClientId());
                params.put(KEY_TOKEN, muteParam.getToken());
                String data = JSONUtil.toJSONString(muteParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(muteParam), CONTEXT_MUTE, muteParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastMuteResult(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void unMuteAllParticipant(final UnMuteAllParam unMuteAllParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, unMuteAllParam.getClientId());
                params.put(KEY_TOKEN, unMuteAllParam.getToken());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(unMuteAllParam), CONTEXT_UNMUTEALL, unMuteAllParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastUnMuteAllResult(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void unMuteParticipant(final UnMuteParam unMuteParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, unMuteParam.getClientId());
                params.put(KEY_TOKEN, unMuteParam.getToken());
                String data = JSONUtil.toJSONString(unMuteParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(unMuteParam), CONTEXT_UNMUTE, unMuteParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastUnMuteResult(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void stopMeeting(final StopMeetingParam stopMeetingParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, stopMeetingParam.getClientId());
                params.put(KEY_TOKEN, stopMeetingParam.getToken());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(stopMeetingParam), CONTEXT_STOP_MEETING, stopMeetingParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastStopMeetingResult(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void changeNameParticipant(final ChangeDisplayNameParam changeDisplayNameParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, changeDisplayNameParam.getClientId());
                if(!TextUtils.isEmpty(changeDisplayNameParam.getToken())) {
                    params.put(KEY_TOKEN, changeDisplayNameParam.getToken());
                }
                String data = JSONUtil.toJSONString(changeDisplayNameParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getMeetingServerAddress(changeDisplayNameParam), CONTEXT_CHANGE_DISPLAYNAME, changeDisplayNameParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastChangeDisplayNameResult(resultType, changeDisplayNameParam.getDisplay_name(), changeDisplayNameParam.getClientId().equals(changeDisplayNameParam.getClient_id()));
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void lectureParticipant(final LectureParam lectureParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, lectureParam.getClientId());
                params.put(KEY_TOKEN, lectureParam.getToken());

                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(lectureParam), CONTEXT_LECTURER, lectureParam.getMeetingNumber(), params);
                try {
                    if(TextUtils.isEmpty(lectureParam.getLecturer())){
                        FrtcHttpClient.getInstance(context).asyncDelete(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                            @Override
                            public void onResult(ResultType resultType, String result) {
                                broadcastSetLecturerResult(resultType, false);
                            }
                        });
                    }else{
                        String data = JSONUtil.toJSONString(lectureParam);
                        FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                            @Override
                            public void onResult(ResultType resultType, String result) {
                                broadcastSetLecturerResult(resultType, true);
                            }
                        });
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void queryMeetingRoomInfo(final QueryMeetingRoomParam queryMeetingRoomParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, queryMeetingRoomParam.getClientId());
                params.put(KEY_TOKEN, queryMeetingRoomParam.getToken());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(queryMeetingRoomParam), CONTEXT_MEETING_ROOM, params);
                try {
                    FrtcHttpClient.getInstance(context).asyncGet(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            QueryMeetingRoomResult queryMeetingRoomResult = JSONUtil.transform(result, QueryMeetingRoomResult.class);
                            broadcastQueryMeetingRoomInfoResult(resultType, queryMeetingRoomResult);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void frtcSdkLeaveMeeting() {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onFrtcSdkLeaveMeetingNotify();
        }
    }

    @Override
    public void queryMeetingInfo(final QueryMeetingInfoParam queryMeetingInfoParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                String url = FrtcHttpClient.getInstance(context).buildUrl(queryMeetingInfoParam.getSeverAddress(), CONTEXT_MEETING_INFO, queryMeetingInfoParam.getMeetingToken());
                try {
                    FrtcHttpClient.getInstance(context).asyncGet(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            QueryMeetingInfoResult queryMeetingInfoResult =  JSONUtil.transform(result, QueryMeetingInfoResult.class);
                            broadcastQueryMeetingInfoResult(resultType, queryMeetingInfoResult);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void startOverlay(final StartOverlayParam startOverlayParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, startOverlayParam.getClientId());
                params.put(KEY_TOKEN, startOverlayParam.getToken());
                String data = JSONUtil.toJSONString(startOverlayParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(startOverlayParam), CONTEXT_OVERLAY, startOverlayParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastStartOverlay(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void stopOverlay(final StopOverlayParam stopOverlayParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, stopOverlayParam.getClientId());
                params.put(KEY_TOKEN, stopOverlayParam.getToken());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(stopOverlayParam), CONTEXT_OVERLAY, stopOverlayParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncDelete(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastStopOverlay(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void disconnectParticipant(final DisconnectParticipantParam disconnectParticipantParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, disconnectParticipantParam.getClientId());
                params.put(KEY_TOKEN, disconnectParticipantParam.getToken());
                String data = JSONUtil.toJSONString(disconnectParticipantParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(disconnectParticipantParam), CONTEXT_DISCONNECT_PARTICIPANT, disconnectParticipantParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncDelete(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastDisconnectParticipants(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public  void disconnectAllParticipants(final DisconnectAllParticipantsParam disconnectAllParticipantsParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, disconnectAllParticipantsParam.getClientId());
                params.put(KEY_TOKEN, disconnectAllParticipantsParam.getToken());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(disconnectAllParticipantsParam), CONTEXT_DISCONNECT_ALL_PARTICIPANT, disconnectAllParticipantsParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncDelete(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastDisconnectAllParticipants(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void startRecording(final CommonMeetingParam startRecordingParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, startRecordingParam.getClientId());
                params.put(KEY_TOKEN, startRecordingParam.getToken());
                String data = JSONUtil.toJSONString(startRecordingParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(startRecordingParam), CONTEXT_RECORDING, startRecordingParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            CommonResponse response = JSONUtil.transform(result, CommonResponse.class);
                            broadcastStartRecording(resultType, (response == null) ? null : response.getErrorCode());
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });

    }
    @Override
    public void stopRecording(final CommonMeetingParam stopRecordingParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, stopRecordingParam.getClientId());
                params.put(KEY_TOKEN, stopRecordingParam.getToken());
                String data = JSONUtil.toJSONString(stopRecordingParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(stopRecordingParam), CONTEXT_RECORDING, stopRecordingParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncDelete(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastStopRecording(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });

    }
    @Override
    public void startLive(final LiveMeetingParam startLiveParam) {
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, startLiveParam.getClientId());
                params.put(KEY_TOKEN, startLiveParam.getToken());
                String data = JSONUtil.toJSONString(startLiveParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(startLiveParam), CONTEXT_LIVE, startLiveParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            CommonResponse response = JSONUtil.transform(result, CommonResponse.class);
                            broadcastStartLive(resultType, (response == null) ? null : response.getErrorCode());
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }
    @Override
    public void stopLive(final LiveMeetingParam stopLiveParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, stopLiveParam.getClientId());
                params.put(KEY_TOKEN, stopLiveParam.getToken());
                String data = JSONUtil.toJSONString(stopLiveParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(stopLiveParam), CONTEXT_LIVE, stopLiveParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncDelete(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastStopLive(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void pinForMeeting(final PinForMeetingParam pinParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, pinParam.getClientId());
                params.put(KEY_TOKEN, pinParam.getToken());
                String data = JSONUtil.toJSONString(pinParam);
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(pinParam), CONTEXT_PIN, pinParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, data), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastPinForMeeting(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void unpinForMeeting(final CommonMeetingParam unpinParam){
        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, unpinParam.getClientId());
                params.put(KEY_TOKEN, unpinParam.getToken());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(unpinParam), CONTEXT_PIN, unpinParam.getMeetingNumber(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncDelete(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastUnpinForMeeting(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    @Override
    public void addMeetingIntoMeetingList(AddMeetingToListParam param) {

        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, param.getClientId());
                params.put(KEY_TOKEN, param.getToken());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(param), CONTEXT_MEETING_LIST_ADD, param.getMeeting_identifier(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncPost(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastAddMeetingIntoMeetingListResult(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }


    @Override
    public void removeMeetingFromMeetingList(AddMeetingToListParam param) {

        FrtcTaskPoolManager.getInstance().submitTask(new ITask() {
            @Override
            public ITaskResult call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_CLIENT_ID, param.getClientId());
                params.put(KEY_TOKEN, param.getToken());
                String url = FrtcHttpClient.getInstance(context).buildUrl(getServerAddress(param), CONTEXT_MEETING_LIST_AREMOVE, param.getMeeting_identifier(), params);
                try {
                    FrtcHttpClient.getInstance(context).asyncDelete(new FrtcHttpClient.RequestWrapper(url, ""), new FrtcHttpClient.IResultCallback() {
                        @Override
                        public void onResult(ResultType resultType, String result) {
                            broadcastRemoveMeetingFromMeetingList(resultType);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        });
    }

    private void broadcastRemoveMeetingFromMeetingList(ResultType resultType) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onRemoveMeetingFromMeetingList(resultType);
        }
    }

    private void broadcastAddMeetingIntoMeetingListResult(ResultType resultType) {
        for (IFrtcManagementListener listener : managementListeners) {
            listener.onAddMeetingIntoMeetingListResult(resultType);
        }
    }

}
