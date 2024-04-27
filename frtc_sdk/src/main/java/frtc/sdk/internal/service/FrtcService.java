package frtc.sdk.internal.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import frtc.sdk.conf.SDKConfigManager;
import frtc.sdk.internal.IFrtcService;
import frtc.sdk.internal.ITemperatureStateListener;
import frtc.sdk.internal.jni.FrtcSDKHelper;
import frtc.sdk.internal.model.FrtcInitParam;
import frtc.sdk.internal.model.MeetingStateType;
import frtc.sdk.internal.model.MeetingStatus;
import frtc.sdk.internal.model.JoinInfo;
import frtc.sdk.internal.model.InternalMessageType;
import frtc.sdk.internal.model.FrtcConstant;
import frtc.sdk.internal.model.TemperatureInfo;
import frtc.sdk.internal.model.TemperatureThresholdType;
import frtc.sdk.log.Log;
import frtc.sdk.util.DeviceUtil;
import frtc.sdk.util.JSONUtil;


public class FrtcService extends Service implements IFrtcService, ITemperatureStateListener {
    private static final String TAG = FrtcService.class.getSimpleName();
    private InboundMessageHandler inboundMessageHandler;
    private List<Messenger> messengers;
    private FrtcSDKHelper frtcSDKHelper;
    private JoinInfo joinInfo;
    private volatile MeetingStatus meetingStatus;
    private volatile boolean temperatureManagerRunning = false;
    private String clientId;
    private TemperatureControlManager temperatureControlManager;
    private HandlerThread handlerThread;
    public static final String action = "frtc.sdk.FrtcService";
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        messengers = new CopyOnWriteArrayList<>();
        initSDK();
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        inboundMessageHandler = new InboundMessageHandler(handlerThread, this);
        temperatureControlManager = new TemperatureControlManager(this);
        temperatureControlManager.register(this);
    }

    private void initSDK() {
        frtcSDKHelper = new FrtcSDKHelper(new InternalMessageHandler(this));
        this.clientId = SDKConfigManager.getInstance(getApplicationContext()).getClientId();
        FrtcInitParam param = new FrtcInitParam();
        param.setCpu_level(DeviceUtil.getCPUCapabilityLevel());
        param.setMy_uuid(this.clientId);
        String logPath = getApplicationContext().getExternalFilesDir("").getAbsolutePath() + "/"+ FrtcConstant.logFileName;
        param.setLog_path(logPath);
        frtcSDKHelper.initialize(JSONUtil.toJSONString(param));
    }

    @Override
    public void onDestroy() {
        frtcSDKHelper.onDestroy();
        frtcSDKHelper = null;
        temperatureControlManager.unregister(this);
        super.onDestroy();
    }

    private boolean isMeetingIDLE() {
        return (meetingStatus == null
                || MeetingStateType.kDisconnected == meetingStatus.getMeeting_status())
                || MeetingStateType.kIdle == meetingStatus.getMeeting_status();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return inboundMessageHandler.getMessenger().getBinder();
    }

    public void prepareJoinMeeting(JoinInfo joinInfo) {
        if (!isMeetingIDLE()) {
            return;
        }
        this.joinInfo = joinInfo;
    }

    public void joinMeeting() {
        sendMessage(InternalMessageType.joinMeeting.getName(), JSONUtil.toJSONString(joinInfo));
    }

    public void addListener(Messenger messenger) {
        messengers.add(messenger);
    }

    public void removeListener(Messenger messenger) {
        messengers.remove(messenger);
    }

    @Override
    public void broadcast(Message message) {
        for (Messenger messenger : messengers) {
            Message msg = Message.obtain(message);
            try {
                messenger.send(msg);
            } catch (RemoteException ex) {
                Log.e(TAG, "Exception:"+ex);
                messengers.remove(messenger);
            }
        }
    }

    @Override
    public void updateMeetingState(MeetingStatus meetingStatus) {
        this.meetingStatus = meetingStatus;
        if (MeetingStateType.kDisconnected == meetingStatus.getMeeting_status()) {
            this.temperatureControlManager.stop();
            temperatureManagerRunning = false;
        } else if (MeetingStateType.kConnected == meetingStatus.getMeeting_status()) {
            if (!temperatureManagerRunning) {
                this.temperatureControlManager.start();
                temperatureManagerRunning = true;
            }
        }
    }

    private Message createMessage(int what, Bundle bundle) {
        Message message = Message.obtain();
        message.what = what;
        message.setData(bundle);
        return message;
    }

    @Override
    public void onTemperatureChanged(float threshold) {
        TemperatureInfo info = new TemperatureInfo();
        if (!this.temperatureControlManager.reachedBaseThreshold(threshold)) {
            info.setBattery_threshold(TemperatureThresholdType.BASE_THRESHOLD_VALUE);
            Bundle bundle = new Bundle();
            bundle.putBoolean(TemperatureThresholdType.EXCEED_THRESHOLD, true);
            Message message = createMessage(InternalMessageType.temperatureInfoNotify.getCode(), bundle);
            broadcast(message);
        } else {
            info.setBattery_threshold(TemperatureThresholdType.MAX_THRESHOLD_VALUE);
        }
        String jsonStr = JSONUtil.toJSONString(info);
        sendMessage(InternalMessageType.temperatureInfoNotify.getName(), jsonStr);
    }

    public String sendMessage(String name, String data) {
        return frtcSDKHelper.sendMessage(name, data);
    }

}