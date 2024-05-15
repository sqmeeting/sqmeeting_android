package frtc.sdk.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import frtc.sdk.FrtcCall;
import frtc.sdk.ui.layout.GalleryLayoutManager;
import frtc.sdk.ui.device.CameraMediaDataHandler;
import frtc.sdk.ui.device.ICameraStateListener;
import frtc.sdk.ui.layout.IMediaDataListener;
import frtc.sdk.ui.model.ViewConstant;
import frtc.sdk.ui.media.IMeetingMessageHandler;
import frtc.sdk.ui.media.LocalPeopleViewModule;
import frtc.sdk.ui.media.MediaDataManager;
import frtc.sdk.ui.media.MediaModule;
import frtc.sdk.ui.media.RenderManager;
import frtc.sdk.ui.media.ShareContentController;
import frtc.sdk.ui.media.ShareContentModule;
import frtc.sdk.ui.model.CameraErrorType;
import frtc.sdk.ui.device.CameraCallback;
import frtc.sdk.internal.model.LayoutData;
import frtc.sdk.internal.model.ParticipantInfo;
import frtc.sdk.internal.model.LayoutInfoData;
import frtc.sdk.internal.service.IMeeting;
import frtc.sdk.util.StringUtils;
import frtc.sdk.util.LanguageUtil;
import frtc.sdk.ui.device.ContentMediaDataHandler;
import frtc.sdk.ui.model.MediaType;


public class MeetingLayout extends RelativeLayout implements IMeetingMessageHandler, IMediaDataListener {
    private final String TAG = MeetingLayout.this.getClass().getSimpleName();

    private Context context;
    private List<MediaModule> people_list = new CopyOnWriteArrayList<>();
    private static List<MediaModule> content_list = new CopyOnWriteArrayList<>();
    private final List<MediaModule> active_speaker_list = new CopyOnWriteArrayList<>();
    private final List<ParticipantInfo> participants = new ArrayList<>();

    private static final Object active_lock = new Object();
    private static final Object animation_lock = new Object();
    private static final Handler handler = new Handler();
    private Handler animation_handler = new Handler(Looper.getMainLooper());
    private static final Map<String, String> layout_data = new HashMap<>();
    private volatile boolean animator_flag = false;
    private volatile boolean first_run_flag = true;

    private LocalPeopleViewModule local_people;
    private CameraCallback localcamera_callback;
    private CameraMediaDataHandler localcamera_media_data;
    private ContentMediaDataHandler content_media_data_handler;

    private ViewGroup surface_view;
    private ViewGroup paging_view;

    private RelativeLayout meeting_slide_view;
    private ImageView slide_view_left;
    private ImageView slide_view_right;
    private GalleryLayoutManager layout_manager;
    private RenderManager render_manager;
    private MediaDataManager mediaDataManager;

    private LayoutData content_layout_data;

    private String displayName = "";
    private IMeeting meeting = null;
    private int camera_id;
    private ShareContentController share_content_controller;
    private volatile boolean control_bar_visible;
    private FrtcCall frtcCall = null;

    private final Runnable animationTask = new Runnable() {
        @Override
        public void run() {
            synchronized (animation_lock) {
                if (!is_animation_complete()) {
                    animation_handler.postDelayed(this, ViewConstant.delayAnimation);
                    return;
                }
                if (layout_manager != null) {
                    layout_manager.setResolution(surface_view.getWidth(), surface_view.getHeight());
                    layout_manager.doLayoutAnimation(local_people, people_list, content_list);
                }
                animator_flag = false;
            }
        }
    };

    private final Runnable layout_runnable = () -> {
        synchronized (animation_lock) {
            relayoutAnimation();
            animator_flag = false;
        }
    };
    private final Runnable invisible_display_name_runnable = () -> {
        for (MediaModule people : people_list) {
            ((MediaFrameLayout) people.getMediaView()).setDisplayNameVisible(false);
        }
        for (MediaModule content : content_list){
            if(content.getMediaView() instanceof MediaFrameLayout){
                ((MediaFrameLayout) content.getMediaView()).setDisplayNameVisible(false);
            }
        }
    };
    private final ICameraStateListener camera_state_listener = new ICameraStateListener() {
        @Override
        public void onCameraError(int cid, CameraErrorType type) {
            if (type == CameraErrorType.OPENED) {
                onCameraOpenFailed(false);
            } else if (type == CameraErrorType.FAILED) {
                onCameraOpenFailed(true);
            }
        }

        @Override
        public void onCameraOpened(int cid) {
            frtcCall.setCameraId(cid);
            MeetingLayout.this.camera_id = cid;
            setLocalDataSourceRotation();
            onCameraOpenResultChanged(true);
        }

        @Override
        public void onCameraClosed(int cid){
            onCameraOpenResultChanged(false);
            localcamera_callback.clearSurface();
        }
    };

    protected void relayoutAnimation(){
        if (layout_manager != null) {
            layout_manager.setResolution(surface_view.getWidth(), surface_view.getHeight());
            layout_manager.doLayoutAnimationByOrientationChanged(local_people, people_list, content_list);
        }
    }

    public MeetingLayout(Context context) {
        super(context);
        this.context = context;
    }

    public MeetingLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    public MeetingLayout(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.context = context;
    }

    public MeetingLayout(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context, attributeSet, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public List<MediaModule> getContentList() {
        return content_list;
    }

    public RelativeLayout getCallSlideView() {
        return meeting_slide_view;
    }

    public boolean isControlBar_visible() {
        return control_bar_visible;
    }

    public GalleryLayoutManager getLayout_manager() {
        return layout_manager;
    }

    public void layout_pause() {
        if(!LanguageUtil.isSharePreferenceLan(context)) {
            LanguageUtil.setLanguage(context);
        }
        if (render_manager != null){
            render_manager.stopRender();
        }
        if (localcamera_callback != null) {
            localcamera_callback.releaseCamera();
        }
    }

    public void layout_finish() {
        if (localcamera_callback != null) {
            localcamera_callback.releaseCamera();
        }
        localcamera_callback = null;
        if(render_manager != null){
            render_manager.destroy();
            render_manager = null;
        }
    }

    public void layout_resume(boolean muted) {
        if (View.VISIBLE == getVisibility()) {
            if (render_manager != null){
                render_manager.startRender();
            }
            if (localcamera_callback != null) {
                localcamera_callback.resetCamera(context, camera_state_listener, muted, frtcCall.getCameraId());
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent me) {
        return super.onInterceptTouchEvent(me);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if(!LanguageUtil.isSharePreferenceLan(context)) {
            LanguageUtil.setLanguage(context);
        }
    }

    private boolean is_animation_complete() {
        if (local_people != null && local_people.isRunning())
            return false;

        if (people_list != null)
            for (MediaModule people : people_list)
                if (people != null && people.isRunning())
                    return false;

        if (content_list != null)
            for (MediaModule content : content_list)
                if (content != null && content.isRunning())
                    return false;

        if (active_speaker_list != null)
            for (MediaModule active : active_speaker_list)
                if (active != null && active.isRunning())
                    return false;
        return true;
    }

    public void initializeContentMediaData(String msid, int width, int height) {
        if(content_media_data_handler == null){
            content_media_data_handler = new ContentMediaDataHandler(msid);
        }
        content_media_data_handler.setSize(width, height);
    }

    public void showLocalContentModule(boolean visible){
        if(visible){
            addLocalContent();
        }
    }

    public void startSendContent(){
        if(share_content_controller != null){
            content_media_data_handler.start();
            share_content_controller.createVirtualDisplay(content_media_data_handler);
        }
    }


    public void setLocalDataSourceRotation(){
        int dataRotation = 0;
        if (localcamera_media_data != null) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(camera_id, info);
            int degrees = getDisplayRotation((Activity) context);
            int result = 0;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
                if(result == 90){
                    dataRotation = 3;
                }else if(result == 180){
                    dataRotation = 2;
                }else if(result == 270){
                    dataRotation = 1;
                }else if(result == 0){
                    dataRotation = 0;
                }
            } else if(info.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                result = (info.orientation - degrees + 360) % 360;
                if(result == 90){
                    dataRotation = 1;
                }else if(result == 180){
                    dataRotation = 2;
                }else if(result == 270){
                    dataRotation = 3;
                }else if(result == 0){
                    dataRotation = 0;
                }
            }
            localcamera_media_data.setRotation(dataRotation);
        }
    }

    public void setContentRotation(int rotation){
        if(share_content_controller != null){
            share_content_controller.setRotation(rotation);
        }

    }

    private void handleActiveSpeaker(String msid) {
        if (msid != null) {
            synchronized (active_lock) {
                AddOrUpdateActiveSpeaker(msid);
            }
        } else {
            if (!active_speaker_list.isEmpty()) {
                resetActiveSpeakerForPeopleList();
            }
        }
    }

    private void resetActiveSpeakerForPeopleList() {
        if (local_people != null) {
            local_people.setActiveSpeaker(false);
        }
        if (people_list != null)
            for (MediaModule people : people_list) {
                if (people != null) {
                    people.setActiveSpeaker(false);
                }
            }
    }

    private void setActiveSpeakerById(String msid) {
        if (StringUtils.isNotBlank(msid)) {
            if (local_people != null && msid.equals(local_people.getMsid())) {
                local_people.setActiveSpeaker(true);
                return;
            }
            MediaModule peopleBlock = matchOne(people_list, msid);
            if (peopleBlock != null) {
                peopleBlock.setActiveSpeaker(showActive());
            }
        }
    }

    public void addLocalContent(){
        ShareContentModule localContent = new ShareContentModule(context);
        localContent.setShareContentListener(new ShareContentModule.IShareContentControlListener() {
            @Override
            public void onStopContent() {
                sendStopContentCmd();
            }
        });
        localContent.setMediaType(MediaType.LOCAL_CONTENT);
        addViewToPagingView(localContent.getMediaView());
        localContent.setVisible(true);
        content_list.add(0, localContent);
        if (layout_manager != null) {
            layout_manager.setContentEnabled(true);
            if (people_list != null && !people_list.isEmpty()) {
                if(control_bar_visible){
                    meeting_slide_view.setVisibility(GONE);
                }else {
                    meeting_slide_view.setVisibility(VISIBLE);
                }
                slide_view_left.setAlpha(1.0f);
                slide_view_right.setAlpha(0.6f);
            }
        }
        triggerLayoutAnimation();
    }

    public void initialize(IMeeting meeting, final ViewGroup surface_view, ViewGroup pagingView, ImageView ivLeft, ImageView ivRight, RelativeLayout slideView, FrtcCall frtcCall) {
        this.surface_view = surface_view;
        this.paging_view = pagingView;
        this.slide_view_left = ivLeft;
        this.slide_view_right = ivRight;
        this.meeting_slide_view = slideView;
        this.frtcCall = frtcCall;
        content_list.clear();
        GalleryLayoutManager galleryLayoutManager = new GalleryLayoutManager();
        initLayoutManager(galleryLayoutManager);
        layout_manager = galleryLayoutManager;
        mediaDataManager = new MediaDataManager(this);
        paging_view.setOnTouchListener(new SwipeTouchListener(getContext(), this) {
            @Override
            public void onSwipeRight() {
                if (layout_manager != null && is_animation_complete()) {
                    layout_manager.setResolution(surface_view.getWidth(), surface_view.getHeight());
                    layout_manager.switchToPrevious(people_list, content_list);
                }
            }

            @Override
            public void onSwipeLeft() {
                if (layout_manager != null && is_animation_complete()) {
                    layout_manager.setResolution(surface_view.getWidth(), surface_view.getHeight());
                    layout_manager.switchToNext(people_list, content_list);
                }
            }
        });
        render_manager = new RenderManager(people_list, content_list, active_speaker_list);
        render_manager.init();
        local_people = new LocalPeopleViewModule(context);
        if(localcamera_callback == null) {
            localcamera_callback = new CameraCallback(context, camera_state_listener, frtcCall.getCameraId());
        }
        addViewToPagingView(local_people.getView());
        local_people.setVisible(true);
        local_people.setVideoCallback(localcamera_callback);
        this.meeting = meeting;
    }

    private void addViewToPagingView(View view){
        if(view != null){
            paging_view.addView(view, new RelativeLayout.LayoutParams(0, 0));
        }
    }

    public void setLocalContentController(ShareContentController shareContentController){
        this.share_content_controller = shareContentController;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void resetLayoutView(int width, int height, boolean videoMuted) {
        if(layout_manager != null) {
            synchronized (animation_lock){
                animator_flag = true;
                surface_view.layout(0,0,width,height);
                setLocalPeoplePreviewMute(videoMuted);
                for (MediaModule people : people_list) {
                    ((MediaFrameLayout) people.getMediaView()).setLoadingTextView(getResources().getConfiguration().orientation);
                }
                contentScaleReset();
                if (animation_handler != null) {
                    animation_handler.post(layout_runnable);
                }
            }
        }
    }

    public void contentScaleReset(){
        if (content_list != null && !content_list.isEmpty()) {
            for (MediaModule content : content_list){
                if(content.getMediaView() instanceof MediaFrameLayout){
                    float scale = ((MediaFrameLayout) content.getMediaView()).getScale();
                    if(scale != ViewConstant.scale_1){
                        ((MediaFrameLayout) content.getMediaView()).resetContentScale();
                    }
                }
            }
        }
    }

    public void setLocalPeopleView(boolean visible) {
        if (layout_manager != null) {
            layout_manager.setLocalPeopleVisible(visible);
            layout_manager.setupLocalPeopleViewLayout(local_people, people_list, content_list);
        }
    }

    public void triggerLayoutAnimation() {
        synchronized (animation_lock) {
            if (animator_flag)
                return;
            animator_flag = true;
        }
        if (animation_handler != null) {
            animation_handler.postDelayed(animationTask, ViewConstant.delayAnimation);
        }
    }

    private void deleteLayoutView(MediaModule mediaModule, ViewGroup viewGroup) {
        if (viewGroup != null) {
            mediaModule.setVisible(false);
            mediaModule.destroy(viewGroup);
        }
    }


    @Override
    public void onContentSendRefused(){
        removeLocalContent();
        updateContentSendRefusedStatus(true);
        stopContentProjection();
    }

    private void updateContentSendRefusedStatus(boolean refused){
        if(share_content_controller != null){
            share_content_controller.updateContentSendRefusedStatus(refused);
        }
    }

    private void stopContentProjection(){
        if(share_content_controller != null ){
            if(content_media_data_handler != null){
                content_media_data_handler.stop();
                content_media_data_handler = null;
            }
            if(share_content_controller.hasProjection()){
                share_content_controller.stopMediaProjection();
            }
            mediaDataManager.releaseLocalContentMediaData();
        }
    }

    private void stopContentCapture(){
        if(share_content_controller != null){
            if(content_media_data_handler != null){
                content_media_data_handler.stop();
                content_media_data_handler = null;
            }
            share_content_controller.stopVirtualDisplay();
            mediaDataManager.releaseLocalContentMediaData();
        }
    }

    public void setMeetingSlideViewVisible(boolean visible) {
        control_bar_visible = !visible;
        if (content_list != null && !content_list.isEmpty()) {
            for (MediaModule content : content_list){
                if(content.getMediaView() instanceof MediaFrameLayout){
                    if(((MediaFrameLayout) content.getMediaView()).getScale() == ViewConstant.scale_1 && visible) {
                        meeting_slide_view.setVisibility(VISIBLE);
                    }else{
                        meeting_slide_view.setVisibility(GONE);
                    }
                }
            }
        }
    }

    public void setResolution() {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        layout_manager.setResolution(width, height);
    }

    @Override
    public void addLocalPeople(final String msid, int width, int height) {
        if (local_people != null && localcamera_callback != null) {
            local_people.setVisible(true);
            local_people.setMsid(msid);
            if(localcamera_media_data == null){
                localcamera_media_data = new CameraMediaDataHandler(msid);
            }else{
                localcamera_media_data.setMsid(msid);
            }
            localcamera_media_data.setCameraPreview(width, height);
            localcamera_callback.setData(localcamera_media_data);
            if (local_people.isLayoutVisible()) {
                boolean isCameraOpened = MeetingLayout.this.meeting.getMeetingInfo().isCameraOpened();
                if(isCameraOpened){
                    post(localPeopleViewRunnable);
                }else{
                    postDelayed(localPeopleViewRunnable, ViewConstant.delayPreview);
                }
            }
            triggerLayoutAnimation();
        }
    }

    private final Runnable localPeopleViewRunnable = new Runnable() {
        @Override
        public void run() {
            if (localcamera_callback != null) {
                if (MeetingLayout.this.meeting.getMeetingInfo().isCameraOpenFailed()) {
                    return;
                }
                setLocalCameraRotation(((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation());
                if (MeetingLayout.this.meeting.getMeetingConfig().isVideoMuted()) {
                    localcamera_callback.startCameraPreview();
                    return;
                }
                localcamera_callback.startCameraPreview();
                localcamera_callback.startCameraRecord();
            }
        }
    };

    @Override
    public void removeLocalPeople(String mediaId) {
        if (local_people != null) {
            local_people.setMsid(null);
            if (localcamera_callback != null)
                localcamera_callback.stopCameraRecord();
            triggerLayoutAnimation();
        }
    }

    @Override
    public void addPeopleVideo(String msid, int width, int height) {
        if (active_speaker_list != null && active_speaker_list.isEmpty()) {
            synchronized (active_lock) {
                AddOrUpdateActiveSpeaker(msid);
            }
        }
        triggerLayoutAnimation();
    }

    @Override
    public void removePeopleVideo(String sid) {
        MediaModule peopleBlock = matchOne(people_list, sid);
        if (peopleBlock != null) {
            if (!peopleBlock.isEnabledViewRender()) {
                if(this.meeting.getMeetingConfig().isRemoteVideoMuted()){
                    peopleBlock.setLoadingStatus(true);
                    peopleBlock.setViewRender(true);
                }
                removeActiveSpeaker(peopleBlock.getMsid());
                if (!people_list.isEmpty() &&
                        peopleBlock.getMsid().equals(!active_speaker_list.isEmpty() ? active_speaker_list.get(0).getMsid() : null)) {
                    synchronized (active_lock) {
                        AddOrUpdateActiveSpeaker(people_list.get(0).getMsid());
                    }
                    people_list.get(0).setActiveSpeaker(showActive());
                }
                triggerLayoutAnimation();
            }
        }
        synchronized (active_lock) {
            if (people_list != null && people_list.isEmpty() && active_speaker_list != null &&
                    !active_speaker_list.isEmpty()) {
                removeActiveSpeaker(active_speaker_list.get(0).getMsid());
            }
        }
    }

    @Override
    public void updatePeopleVideo(String msid, int width, int height) {

    }

    @Override
    public void addContentVideo(String msid, int width, int height) {
        MediaModule content = new MediaModule(context);
        addViewToPagingView(content.getMediaView());
        content.setVisible(true);
        content.setMsid(msid);
        content.setMediaType(MediaType.REMOTE_CONTENT);
        if (content_layout_data != null) {
            String contentName = content_layout_data.getDisplay_name();
            String uuid = content_layout_data.getUuid();
            if (contentName != null && !contentName.isEmpty()) {
                content.setDisplayName(contentName);
                content.setUuid(uuid);
                if(uuid != null && !uuid.isEmpty()){
                    for (ParticipantInfo info : participants) {
                        String infoUuid = info.getUuid();
                        if(infoUuid != null && !infoUuid.isEmpty()){
                            if (uuid.contains(info.getUuid())) {
                                boolean audioMuted = info.getAudioMuted();
                                content.setAudioMute(audioMuted);
                                break;
                            }
                        }
                    }
                }
                if (this.meeting.getMeetingConfig().isWaterMarkEnabled()) {
                   content.showContentWaterView(true, this.displayName);
                } else {
                    content.showContentWaterView(false, "");
                }
            } else {
                content.hideNameView();
                content.showContentWaterView(false, "");
            }
        }
        content_list.add(0, content);
        if (layout_manager != null) {
            layout_manager.setContentEnabled(true);

            if (people_list != null && !people_list.isEmpty()) {
                if(control_bar_visible){
                    meeting_slide_view.setVisibility(GONE);
                }else {
                    meeting_slide_view.setVisibility(VISIBLE);
                }
                slide_view_left.setAlpha(1.0f);
                slide_view_right.setAlpha(0.6f);
            }
        }
        triggerLayoutAnimation();
    }

    @Override
    public void removeContentVideo(String msid) {
        MediaModule contentBlock = null;
        MediaModule tempOne = matchOne(content_list, msid);
        if (MediaType.REMOTE_CONTENT.equals(tempOne.getMediaType())) {
            contentBlock = tempOne;
        }
        if (contentBlock != null) {
            content_list.remove(contentBlock);
            if (layout_manager != null && content_list.isEmpty()) {
                layout_manager.setContentEnabled(false);
            }
            deleteLayoutView(contentBlock, paging_view);
            meeting_slide_view.setVisibility(GONE);
            triggerLayoutAnimation();
        }
    }

    @Override
    public void updateContentVideo(String msid, int width, int height) {

    }

    private void initLayoutManager(GalleryLayoutManager galleryLayoutManager) {
        if (galleryLayoutManager == null) {
            return;
        }
        galleryLayoutManager.registerPagingChangeListener((has_previous, has_next) -> {
            if (has_previous) {
                if(control_bar_visible){
                    meeting_slide_view.setVisibility(GONE);
                }else {
                    meeting_slide_view.setVisibility(VISIBLE);
                }
                slide_view_left.setAlpha(0.6f);
                slide_view_right.setAlpha(1.0f);
            } else if(has_next){
                if(control_bar_visible){
                    meeting_slide_view.setVisibility(GONE);
                }else {
                    meeting_slide_view.setVisibility(VISIBLE);
                }
                slide_view_left.setAlpha(1.0f);
                slide_view_right.setAlpha(0.6f);
            }else{
                meeting_slide_view.setVisibility(GONE);
            }

        });
        galleryLayoutManager.registerPagingSizeChangeListener((width, height, marginLeft, marginTop) -> {
            if (paging_view != null) {
                LayoutParams params = (LayoutParams) paging_view.getLayoutParams();
                if (params != null) {
                    params.width = width;
                    params.height = height;
                    params.topMargin = marginTop;
                    params.leftMargin = marginLeft;
                    paging_view.setLayoutParams(params);
                }
            }
        });

    }

    public void destroyLayout() {
        if (render_manager != null) {
            render_manager.destroy();
            render_manager = null;
        }
        if (animation_handler != null) {
            animation_handler.removeCallbacks(null);
            animation_handler = null;
        }
    }

    public void onCameraOpenFailed(boolean cameraOpenFailed) {
        this.meeting.getMeetingInfo().setCameraOpenFailed(cameraOpenFailed);
    }

    public void onCameraOpenResultChanged(boolean cameraOpened){
        this.meeting.getMeetingInfo().setCameraOpened(cameraOpened);
    }

    private MediaModule matchOneByType(List<MediaModule> list, MediaType type) {
        MediaModule block = null;
        if (type != null && list != null && !list.isEmpty()) {
            for (MediaModule item : list) {
                if (type.equals(item.getMediaType())) {
                    block = item;
                    break;
                }
            }
        }
        return block;
    }

    private MediaModule matchOne(List<MediaModule> list, String msid) {
        MediaModule m = null;
        if (StringUtils.isNotBlank(msid) && list != null && !list.isEmpty()) {
            for (MediaModule item : list) {
                if (msid.equals(item.getMsid())) {
                    m = item;
                    break;
                }
            }
        }
        return m;
    }


    public void removeLocalContent() {
        if (content_list == null || content_list.isEmpty() || layout_manager == null) {
            return;
        }
        MediaModule contentBlock = matchOneByType(content_list, MediaType.LOCAL_CONTENT);
        if (contentBlock != null) {
            content_list.remove(contentBlock);
            layout_manager.setContentEnabled(false);
            deleteLayoutView(contentBlock, paging_view);
            meeting_slide_view.setVisibility(View.GONE);
            triggerLayoutAnimation();
        }
    }

    public void updateLocalContentMediaData(String msid, int width, int height) {
        if(content_media_data_handler != null){
            content_media_data_handler.setMsid(msid);
            content_media_data_handler.setSize(width, height);
        }
    }

    public void sendStopContentCmd(){
        if(share_content_controller != null && share_content_controller.hasProjection()){
            share_content_controller.sendStopContentCmd();
        }
    }

    public void stopSendContent(){
        removeLocalContent();
        stopContentProjection();
    }

    @Override
    public void onAddPeopleVideo(String msid, int width, int height) {
        if (StringUtils.isNotBlank(msid)) {
            mediaDataManager.changePeopleVideo(msid, width, height);
            if (mediaDataManager.isRemotePeople(msid)) {
                MediaModule people = matchOne(people_list, msid);
                if (people != null) {
                    people.setLoadingStatus(false);
                    people.setViewRender(false);
                }
            }
        }
    }

    @Override
    public void onRequestPeopleVideo(String msid, int width, int height) {
        mediaDataManager.requestPeopleVideo(msid, width, height);
    }

    @Override
    public void onStopPeopleVideo(String msid) {
        mediaDataManager.stopPeopleVideo(msid);
    }

    @Override
    public void onDeletePeopleVideo(String msid) {
        mediaDataManager.stopPeopleVideo(msid);
    }

    private boolean showActive(){
        return this.people_list != null && this.people_list.size() > 1;
    }
    @Override
    public void onLayoutInfoNotify(LayoutInfoData layoutInfoData) {
        List<LayoutData> newLayoutList = null;
        if (layoutInfoData != null) {
            newLayoutList = layoutInfoData.getLayout();
            layout_data.clear();
            for (LayoutData info : newLayoutList) {
                layout_data.put(info.getMsid(), info.getDisplay_name());
            }
            content_layout_data = layoutInfoData.getLayout_content();
            if(content_layout_data != null){
                sendStopContentCmd();
                stopSendContent();
            }
            if(layoutInfoData.getTotal_count() == 0){
                stopContentCapture();
            }
            int newCount = Math.min(newLayoutList.size(), ViewConstant.maxParticipants);
            List<MediaModule> new_List = new ArrayList<>();
            List<MediaModule> remove_List = new ArrayList<>();
            for (MediaModule people : people_list) {
                boolean remove_flag = true;
                for (LayoutData info : newLayoutList) {
                    if (people.getUuid().equals(info.getUuid())) {
                        people.setMsid(info.getMsid());
                        remove_flag = false;
                        new_List.add(people);
                        break;
                    }
                }
                if (remove_flag) {
                    remove_List.add(people);
                }
            }
            boolean has_new_flag = false;
            for (LayoutData data : newLayoutList) {
                boolean add_flag = true;
                for (MediaModule people : people_list) {
                    if (people.getUuid().equals(data.getUuid())) {
                        add_flag = false;
                        people.setMsid(data.getMsid());
                        if (data.getWidth() < 0) {
                            people.setLoadingStatus(true);
                            people.setViewRender(true);
                        } else {
                            people.setViewRender(false);
                        }
                        break;
                    }
                }
                if (add_flag) {
                    has_new_flag = true;
                    MediaModule people;
                    if (!remove_List.isEmpty()) {
                        people = remove_List.remove(0);
                        people.setMsid(data.getMsid());
                        people.setUuid(data.getUuid());
                        people.setDisplayName(data.getDisplay_name());
                       if (data.getWidth() < 0) {
                            people.setLoadingStatus(true);
                            people.setViewRender(true);
                        } else {
                            people.setLoadingStatus(true);
                            people.setViewRender(false);
                        }
                       int index = people_list.indexOf(people);
                        if (index < new_List.size()) {
                            new_List.add(index, people);
                        } else {
                            new_List.add(people);
                        }
                    } else {
                        people = createPeopleModule(data.getMsid(), data.getUuid());
                       if (data.getWidth() < 0) {
                            people.setLoadingStatus(true);
                           people.setViewRender(true);
                        } else {
                           people.setLoadingStatus(true);
                            people.setViewRender(false);
                        }
                        new_List.add(people);
                    }
                }
            }
            people_list.clear();
            for (int i = 0; i < new_List.size(); i++) {
                MediaModule people = new_List.get(i);
                boolean isActive = people.getMsid().equals(layoutInfoData.getActive_speaker_msid());
                people.setActiveSpeaker(newCount > 1 && isActive);
                people_list.add(people);
            }
            for (MediaModule people : remove_List) {
                deleteLayoutView(people, paging_view);
            }
            remove_List.clear();
            new_List.clear();
            if (has_new_flag) {
                setNameVisible();
                if (this.meeting.getMeetingConfig().isOverlayVisible()) {
                    handler.removeCallbacks(invisible_display_name_runnable);
                    handler.postDelayed(invisible_display_name_runnable, ViewConstant.delay_invisible);
                }
            }
            handleActiveSpeaker(layoutInfoData.getActive_speaker_msid());
            String pinUUid = layoutInfoData.getPinUuId();
            int pinned_idx = 0;
            for (MediaModule people : people_list) {
                if (pinUUid != null && pinUUid.equals(people.getUuid())) {
                    pinned_idx = people_list.indexOf(people);
                    people.setPinned(true);
                } else {
                    people.setPinned(false);
                }
            }
            if (pinned_idx != 0) {
                MediaModule tempPeople = people_list.get(0);
                people_list.set(0, people_list.get(pinned_idx));
                people_list.set(pinned_idx, tempPeople);
            }
            updateAudioState();
        }
        if (first_run_flag) {
            synchronized (this) {
                first_run_flag = false;
                postDelayed(() -> {
                    setLocalCameraRotation(((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation());
                    if (localcamera_callback != null) {
                        localcamera_callback.startCameraPreview();
                    }
                }, ViewConstant.delayPreview);
            }
        }
        triggerLayoutAnimation();
    }

    private MediaModule createPeopleModule(String msid, String uuid) {
        MediaModule people = new MediaModule(context);
        addViewToPagingView(people.getMediaView());
        people.setMsid(msid);
        people.setUuid(uuid);
        people.setDisplayName(layout_data.get(msid));
        people.setVisible(true);
        ((MediaFrameLayout) people.getMediaView()).setDisplayNameVisible(true);
        return people;
    }

    public void setMuteState(List<ParticipantInfo> participantInfos){
        this.participants.clear();
        this.participants.addAll(participantInfos);
        updateAudioState();
    }

    private void updateAudioState() {
        if (participants == null || people_list == null) {
            return;
        }
        for (MediaModule people : people_list) {
            for (ParticipantInfo info : participants) {
                if (people.getUuid().equals(info.getUuid())) {
                    boolean audioMuted = info.getAudioMuted();
                    people.setAudioMute(audioMuted);
                    people.setDisplayName(info.getDisplay_name());
                    break;
                }
            }
        }
        for(MediaModule content : content_list){
            for (ParticipantInfo info : participants) {
                String infoUuid = info.getUuid();
                if(infoUuid != null && !infoUuid.isEmpty()){
                    if (content.getUuid().contains(info.getUuid())) {
                        boolean audioMuted = info.getAudioMuted();
                        content.setAudioMute(audioMuted);
                        content.setDisplayName(info.getDisplay_name());
                        break;
                    }
                }
            }
        }
    }

    public void setNameVisible() {
       handler.removeCallbacks(invisible_display_name_runnable);
        for (MediaModule people : people_list) {
            ((MediaFrameLayout) people.getMediaView()).setDisplayNameVisible(true);
        }
        for (MediaModule content : content_list){
            if(content.getMediaView() instanceof MediaFrameLayout){
                ((MediaFrameLayout) content.getMediaView()).setDisplayNameVisible(false);
            }
        }
    }

    public void setNameInvisible() {
        for (MediaModule people : people_list) {
            ((MediaFrameLayout) people.getMediaView()).setDisplayNameVisible(false);
        }
        for (MediaModule content : content_list){
            if(content.getMediaView() instanceof MediaFrameLayout){
                ((MediaFrameLayout) content.getMediaView()).setDisplayNameVisible(false);
            }
        }
    }

    public void startLocalCameraPreview() {
        setLocalCameraRotation(((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation());
        if (localcamera_callback != null) {
           localcamera_callback.openCamera();
            localcamera_callback.startCameraPreview();
            localcamera_callback.startCameraRecord();
        }
    }

    public void stopLocalCameraPreview() {
        if (localcamera_callback != null) {
            localcamera_callback.stopCameraRecord();
            localcamera_callback.stopCameraPreview();
            localcamera_callback.closeCamera();
        }
    }

    public void setLocalCameraPreviewMute(boolean videoMuted) {
        if (localcamera_callback != null) {
            localcamera_callback.setMuteStatus(videoMuted);
        }
    }

    public void onMeetingConnected() {
        onLayoutInfoNotify(null);
    }

    public void onMeetingDisconnected() {
        layout_pause();
        if (local_people != null) {
            local_people.setVisible(false);
            local_people.setVisibility(View.INVISIBLE);
        }
        removeAllPeople();
    }
    public void onMeetingDisconnectedForReconnect(){
        layout_pause();
    }

    public void removeAllPeople() {
        if ((people_list != null) && (!people_list.isEmpty())) {
            for (MediaModule module : people_list) {
                module.setVisible(false);
                module.destroy(this);
            }
            people_list.clear();
        }
    }

    public boolean isContentMediaData(String msid) {
        return mediaDataManager.isContentMediaData(msid);
    }

    public void setLocalPeoplePreviewMute(boolean isMuted) {
        if (local_people != null) {
            local_people.setLocalVideoMuted(isMuted, getResources().getConfiguration().orientation);
        }
    }

    public void switchCamera() {
        localcamera_callback.switchFrontOrBackCamera();
    }

    public static int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                break;
        }
        return 0;
    }

    public void setLocalCameraRotation(int rotation) {
        if (localcamera_callback != null) {
            localcamera_callback.setOrientation(rotation);
        }
        setLocalDataSourceRotation();
    }

    private void removeActiveSpeaker(String msid) {
        synchronized (active_lock) {
            if (!active_speaker_list.isEmpty() && msid != null && msid.equals(active_speaker_list.get(0).getMsid())) {
                deleteLayoutView(active_speaker_list.get(0), surface_view);
                active_speaker_list.clear();
            }
        }
    }

    private void AddOrUpdateActiveSpeaker(String msid) {
        if (active_speaker_list != null) {
            if (active_speaker_list.isEmpty()) {
                active_speaker_list.add(0, new MediaModule(context));
                active_speaker_list.get(0).setVisible(true);
                active_speaker_list.get(0).setMsid(msid);
                setActiveSpeakerById(msid);
            } else if (!msid.equals(active_speaker_list.get(0).getMsid())) {
                resetActiveSpeakerForPeopleList();
                active_speaker_list.get(0).setMsid(msid);
                setActiveSpeakerById(msid);
            }
            triggerLayoutAnimation();
        }
    }

}
