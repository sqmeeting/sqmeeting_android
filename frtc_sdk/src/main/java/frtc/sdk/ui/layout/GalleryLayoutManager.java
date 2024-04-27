package frtc.sdk.ui.layout;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import frtc.sdk.log.Log;
import frtc.sdk.ui.media.LocalPeopleViewModule;
import frtc.sdk.ui.media.MediaModule;

public class GalleryLayoutManager {

    private final String TAG = this.getClass().getSimpleName();

    private int screenWidth;
    private int screenHeight;

    private PagingSizeChangeListener pagingSizeChangeListener;
    private PagingChangeListener pagingChangeListener;
    private boolean localPeopleVisible = true;

    private int screenMargin = 0;
    private boolean contentEnabled = false;
    private boolean currentContent = false;

    private static final int MAX_VIEW_SIZE = 4;
    private static final double PORTRAIT = 9.0/16.0;
    private static final double LANDSCAPE = 16.0/9.0;
    private static final int TOTAL_SPILT_SIZE = 5;

    private static final int DELAY_TIME = 500;
    private static final int SPILT_MARGIN_SIZE = 600;

    private static final int INDEX_0 = 0;
    private static final int INDEX_1 = 1;
    private static final int INDEX_2 = 2;
    private static final int INDEX_3 = 3;

    private static final int L1 = 1;
    private static final int L2 = 2;
    private static final int L3 = 3;
    private static final int L4 = 4;

    private final Map<String, List<GalleryLayoutData>> layouts = initLayoutData();

    public GalleryLayoutManager() {
        screenMargin = 0;
        localPeopleVisible = true;
        currentContent = false;
    }

    private Map<String, List<GalleryLayoutData>> initLayoutData() {
        float zrSize = 0.0f;
        float fullSize = 1.0f;
        float halfSize = 0.5f;
        float quarterSize = 0.25f;
        Map<String, List<GalleryLayoutData>> layouts = new HashMap<>();
        List<GalleryLayoutData> layout1 = new ArrayList<>();
        layout1.add(new GalleryLayoutData(INDEX_0, zrSize, zrSize, fullSize));
        List<GalleryLayoutData> layout2 = new ArrayList<>();
        layout2.add(new GalleryLayoutData(INDEX_0, zrSize, quarterSize, halfSize));
        layout2.add(new GalleryLayoutData(INDEX_1, halfSize, quarterSize, halfSize));
        List<GalleryLayoutData> layout3 = new ArrayList<>();
        layout3.add(new GalleryLayoutData(INDEX_0, quarterSize, zrSize, halfSize));
        layout3.add(new GalleryLayoutData(INDEX_1, zrSize, halfSize, halfSize));
        layout3.add(new GalleryLayoutData(INDEX_2, halfSize, halfSize, halfSize));
        List<GalleryLayoutData> layout4 = new ArrayList<>();
        layout4.add(new GalleryLayoutData(INDEX_0, zrSize, zrSize, halfSize));
        layout4.add(new GalleryLayoutData(INDEX_1, halfSize, zrSize, halfSize));
        layout4.add(new GalleryLayoutData(INDEX_2, zrSize, halfSize, halfSize));
        layout4.add(new GalleryLayoutData(INDEX_3, halfSize, halfSize, halfSize));
        layouts.put(String.valueOf(L1), layout1);
        layouts.put(String.valueOf(L2), layout2);
        layouts.put(String.valueOf(L3), layout3);
        layouts.put(String.valueOf(L4), layout4);
        return layouts;
    }

    private Map<String, List<GalleryLayoutData>> getLayouts() {
        return layouts;
    }

    public void setLocalPeopleVisible(boolean enabled){
        this.localPeopleVisible = enabled;
    }

    private boolean isLocalPeopleVisible(){
        return localPeopleVisible;
    }

    private double getActualAspectRatio() {
        return (double) screenWidth / screenHeight;
    }

    private double getDefaultAspectRatio() {
        return screenWidth > screenHeight ? LANDSCAPE : PORTRAIT;
    }

    private int getDefaultLocalViewWidth() {
        return screenWidth / TOTAL_SPILT_SIZE;
    }

    private int getDefaultLocalViewHeight(double defaultRatio) {
        return (int)(screenWidth / TOTAL_SPILT_SIZE / defaultRatio);
    }

    private int getDefaultLocalViewMarginLeft() {
        return screenWidth * 4 / TOTAL_SPILT_SIZE;
    }

    private int getDisableLocalViewMarginTop(double defaultRatio) {
        return (int)(0 - screenWidth / TOTAL_SPILT_SIZE / defaultRatio);
    }

    public void setResolution(int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }

    private int calPadding(int screenSize, int s) {
        int result = 0;
        if (s > 0) {
            if (screenSize > s) {
                return (screenSize % s) / 2;
            }
        }
        return result;
    }

    private void setLocalViewByRatio(LocalPeopleViewModule localView) {
        double actual = getActualAspectRatio();
        double defaultRatio = getDefaultAspectRatio();
        if (actual > defaultRatio) {
            localView.setLocalView((int) (screenHeight * defaultRatio), screenHeight, (int) ((screenWidth - (screenHeight * defaultRatio)) / 2), 0);
        } else if (actual < defaultRatio) {
            localView.setLocalView(screenWidth, (int) (screenWidth / defaultRatio), 0, (int) ((screenHeight - (screenWidth / defaultRatio)) / 2));
        } else {
            localView.setLocalView(screenWidth, screenHeight, getSplitMarginLeft(), getSplitMarginTop());
        }
    }

    public void setupLocalPeopleViewLayout(LocalPeopleViewModule localPeopleView, List<MediaModule> peoples, List<MediaModule> contents) {
        if ((peoples == null || peoples.isEmpty()) && (contents == null || contents.isEmpty())) {
            if(screenWidth == 0 || screenHeight == 0){
                localPeopleView.setLocalView(screenWidth, screenHeight, 0, 0);
                return;
            }
        }
        if ((peoples != null && !peoples.isEmpty()) || (contents != null && !contents.isEmpty()) ) {
            setDefaultLocalView(localPeopleView);
        } else {
            setLocalViewByRatio(localPeopleView);
        }
    }

    private void setDefaultLocalView(LocalPeopleViewModule localView) {
        double defaultRatio = getDefaultAspectRatio();
        if (isLocalPeopleVisible()) {
            localView.setLocalView(getDefaultLocalViewWidth(), getDefaultLocalViewHeight(defaultRatio), getDefaultLocalViewMarginLeft(), 0);
        } else {
            localView.setLocalView(getDefaultLocalViewWidth(), getDefaultLocalViewHeight(defaultRatio), getDefaultLocalViewMarginLeft(), getDisableLocalViewMarginTop(defaultRatio));
        }
    }

    private void setLocalViewDefaultAnimateRelayout(LocalPeopleViewModule localView) {
        double defaultRatio = getDefaultAspectRatio();
        if (isLocalPeopleVisible()) {
            localView.doLayoutAnimation(getDefaultLocalViewWidth(), getDefaultLocalViewHeight(defaultRatio), getDefaultLocalViewMarginLeft(), 0);
        } else {
            localView.doLayoutAnimation(getDefaultLocalViewWidth(), getDefaultLocalViewHeight(defaultRatio), getDefaultLocalViewMarginLeft(), getDisableLocalViewMarginTop(defaultRatio));
        }
    }

    private void setLocalViewAnimateRelayout(LocalPeopleViewModule localView) {
        double actual = getActualAspectRatio();
        double defaultRatio = getDefaultAspectRatio();
        if (actual > defaultRatio) {
            localView.doLayoutAnimation((int) (screenHeight * defaultRatio), screenHeight, (int) ((screenWidth - (screenHeight * defaultRatio)) / 2), 0);
        } else if (actual < defaultRatio) {
            localView.doLayoutAnimation(screenWidth, (int) (screenWidth / defaultRatio), 0, (int) ((screenHeight - (screenWidth / defaultRatio)) / 2));
        } else {
            localView.doLayoutAnimation(screenWidth, screenHeight, getSplitMarginLeft(), getSplitMarginTop());
        }
    }

    private int getSplitMarginTop() {
        return screenHeight / SPILT_MARGIN_SIZE / 2;
    }

    private int getSplitMarginLeft() {
        return screenWidth / SPILT_MARGIN_SIZE / 2;
    }

    private void setLocalViewAnimateChange(LocalPeopleViewModule localView) {
        double actual = getActualAspectRatio();
        double defaultRatio = getDefaultAspectRatio();
        if (actual > defaultRatio) {
            localView.doUpdateAnimation((int) (screenHeight * defaultRatio), screenHeight, (int) ((screenWidth - (screenHeight * defaultRatio)) / 2), 0);
        } else if (actual < defaultRatio) {
            localView.doUpdateAnimation(screenWidth, (int) (screenWidth / defaultRatio), 0, (int) ((screenHeight - (screenWidth / defaultRatio)) / 2));
        } else {
            localView.doUpdateAnimation(screenWidth, screenHeight, getSplitMarginLeft(), getSplitMarginTop());
        }
    }

    private void setLocalViewDefaultAnimateCreate(LocalPeopleViewModule localView) {
        double defaultRatio = getDefaultAspectRatio();
        if (isLocalPeopleVisible()) {
            localView.doCreateAnimation(getDefaultLocalViewWidth(),  getDefaultLocalViewHeight(defaultRatio), getDefaultLocalViewMarginLeft(), 0);
        } else {
            localView.doCreateAnimation(getDefaultLocalViewWidth(),  getDefaultLocalViewHeight(defaultRatio), getDefaultLocalViewMarginLeft(), getDisableLocalViewMarginTop(defaultRatio));
        }
    }

    private void setAnimateCreateView(LocalPeopleViewModule localView) {
        double actual = getActualAspectRatio();
        double defaultRatio = getDefaultAspectRatio();
        if (actual > defaultRatio) {
            localView.doCreateAnimation((int) (screenHeight * defaultRatio), screenHeight, (int) ((screenWidth - (screenHeight * defaultRatio)) / 2), 0);
        } else if (actual < defaultRatio) {
            localView.doCreateAnimation(screenWidth, (int) (screenWidth / defaultRatio), 0, (int) ((screenHeight - (screenWidth / defaultRatio)) / 2));
        } else {
            localView.doCreateAnimation(screenWidth, screenHeight, getSplitMarginLeft(), getSplitMarginTop());
        }
    }

    private void setContentViewAnimateCreate(MediaModule content) {
        double actual = getActualAspectRatio();
        double defaultRatio = getDefaultAspectRatio();
        if (actual > defaultRatio) {
            content.doCreateAnimation((int) (screenHeight * defaultRatio), screenHeight, (int) ((screenWidth - (screenHeight * defaultRatio)) / 2), 0);
        } else if (actual < defaultRatio) {
            content.doCreateAnimation(screenWidth, (int) (screenWidth / defaultRatio), 0, (int) ((screenHeight - (screenWidth / defaultRatio)) / 2));
        } else {
            content.doCreateAnimation(screenWidth, screenHeight, screenMargin - screenWidth, 0);
        }
    }

    private void setContentViewAnimateUpdate(MediaModule content) {
        double actual = getActualAspectRatio();
        double defaultRatio = getDefaultAspectRatio();
        if (actual > defaultRatio) {
            content.doUpdateAnimation((int) (screenHeight * defaultRatio), screenHeight, (screenMargin == 0) ? (screenMargin - screenWidth) : (int) ((screenMargin - (screenHeight * defaultRatio)) / 2), 0);
        } else if (actual < defaultRatio) {
            content.doUpdateAnimation(screenWidth, (int) (screenWidth / defaultRatio), screenMargin - screenWidth, (int) ((screenHeight - (screenWidth / defaultRatio)) / 2));
        } else {
            content.doUpdateAnimation(screenWidth, screenHeight, screenMargin - screenWidth, 0);
        }
    }

    private void setDefaultContentViewAnimateUpdate(MediaModule content) {
        double actual = getActualAspectRatio();
        double defaultRatio = getDefaultAspectRatio();
        if (actual > defaultRatio) {
            content.doUpdateAnimation((int) (screenHeight * defaultRatio), screenHeight, (int) ((screenWidth - (screenHeight * defaultRatio)) / 2), 0);
        } else if (actual < defaultRatio) {
            content.doUpdateAnimation(screenWidth, (int) (screenWidth / defaultRatio), 0, (int) ((screenHeight - (screenWidth / defaultRatio)) / 2));
        } else {
            content.doUpdateAnimation(screenWidth, screenHeight, 0, 0);
        }
    }

    private void setDefaultContentViewAnimateCreate(MediaModule content) {
        double actual = getActualAspectRatio();
        double defaultRatio = getDefaultAspectRatio();
        if (actual > defaultRatio) {
            content.doCreateAnimation((int) (screenHeight * defaultRatio), screenHeight, (int) ((screenWidth - (screenHeight * defaultRatio)) / 2), 0);
        } else if (actual < defaultRatio) {
            content.doCreateAnimation(screenWidth, (int) (screenWidth / defaultRatio), 0, (int) ((screenHeight - (screenWidth / defaultRatio)) / 2));
        } else {
            content.doCreateAnimation(screenWidth, screenHeight, 0, 0);
        }
    }

    private void setContentViewAnimateRelayoutCreate(MediaModule content) {
        double actual = getActualAspectRatio();
        double defaultRatio = getDefaultAspectRatio();
        if (actual > defaultRatio) {
            content.doLayoutAnimation((int) (screenHeight * defaultRatio), screenHeight, (int) ((screenWidth - (screenHeight * defaultRatio)) / 2), 0);
        } else if (actual < defaultRatio) {
            content.doLayoutAnimation(screenWidth, (int) (screenWidth / defaultRatio), 0, (int) ((screenHeight - (screenWidth / defaultRatio)) / 2));
        } else {
            content.doLayoutAnimation(screenWidth, screenHeight, screenMargin - screenWidth, 0);
        }
    }

    private void setContentViewAnimateRelayoutUpdate(MediaModule content) {
        double actual = getActualAspectRatio();
        double defaultRatio = getDefaultAspectRatio();
        if (actual > defaultRatio) {
            content.doLayoutAnimation((int) (screenHeight * defaultRatio), screenHeight, (screenMargin == 0) ? (screenMargin - screenWidth) : (int) ((screenMargin - (screenHeight * defaultRatio)) / 2), 0);
        } else if (actual < defaultRatio) {
            content.doLayoutAnimation(screenWidth, (int) (screenWidth / defaultRatio), screenMargin - screenWidth, (int) ((screenHeight - (screenWidth / defaultRatio)) / 2));
        } else {
            content.doLayoutAnimation(screenWidth, screenHeight, screenMargin - screenWidth, 0);
        }
    }

    private void setContentViewDefaultAnimateRelayout(MediaModule content) {
        double actual = getActualAspectRatio();
        double defaultRatio = getDefaultAspectRatio();
        if (actual > defaultRatio) {
            content.doLayoutAnimation((int) (screenHeight * defaultRatio), screenHeight, (int) ((screenWidth - (screenHeight * defaultRatio)) / 2), 0);
        } else if (actual < defaultRatio) {
            content.doLayoutAnimation(screenWidth, (int) (screenWidth / defaultRatio), 0, (int) ((screenHeight - (screenWidth / defaultRatio)) / 2));
        } else {
            content.doLayoutAnimation(screenWidth, screenHeight, 0, 0);
        }
    }

    private void updateLocalViewLayout(LocalPeopleViewModule localView, List<MediaModule> peoples, List<MediaModule> contents) {
        if (localView == null) {
            return;
        }
        if (localView.isRendered()) {
            if ((peoples != null && !peoples.isEmpty()) || (contents != null && !contents.isEmpty())) {
                setDefaultLocalView(localView);
            } else {
                setLocalViewAnimateChange(localView);
            }
        } else {
            if ((peoples == null || peoples.isEmpty()) && (contents == null || contents.isEmpty())) {
                setAnimateCreateView(localView);
            } else {
                setLocalViewDefaultAnimateCreate(localView);
            }
            if (peoples != null && !peoples.isEmpty()) {
                new Handler().postDelayed(() -> setDefaultLocalView(localView),DELAY_TIME);
            }
        }
    }

    private List<MediaModule> handleInVisibleView(List<MediaModule> peoples) {
        List<MediaModule> views = new ArrayList<>();
        if (peoples != null && !peoples.isEmpty()) {
            for (MediaModule view : peoples) {
                if (view != null && !view.isLayoutVisible()) {
                    view.doHidden();
                    view.doScale(0.0f);
                } else {
                    views.add(view);
                }
            }
        }
        return views;
    }

    public void registerPagingChangeListener(PagingChangeListener listener) {
        this.pagingChangeListener = listener;
    }

    public void registerPagingSizeChangeListener(PagingSizeChangeListener listener) {
        pagingSizeChangeListener = listener;
    }

    private void changePagingSize() {
        if (pagingSizeChangeListener != null) {
            pagingSizeChangeListener.onSizeChanged(
                    screenWidth - screenWidth / SPILT_MARGIN_SIZE,
                    screenHeight - screenHeight / SPILT_MARGIN_SIZE,
                    getSplitMarginLeft(),
                    getSplitMarginTop());
        }
    }

    private GalleryLayoutData getLayout(int idx, List<GalleryLayoutData> layouts) {
        if (idx < layouts.size()) {
            for (GalleryLayoutData l : layouts) {
                if (null != l) {
                    if (l.getIndex() == idx)
                        return l;
                }
            }
        }
        return null;
    }

    private List<GalleryLayoutData> getLayoutsBySize(int size) {
        List<GalleryLayoutData> result = getLayouts().get(String.valueOf(size));
        if (result == null) {
            List<Integer> idList = new ArrayList<>();
            for (String k : getLayouts().keySet()) {
                try {
                    idList.add(Integer.parseInt(k));
                } catch (Exception e) {
                    Log.e(TAG, "getLayoutsBySize: " + e);
                }
            }
            Collections.sort(idList);
            int find = Collections.binarySearch(idList, size);
            if (find < 0) {
                int matched = Math.abs(find) - 1;
                if (matched > 0 && matched <= idList.size()) {
                    result = getLayouts().get(String.valueOf(idList.get(matched - 1)));
                }
            }
        }
        return result;
    }

    public void doLayoutAnimation(LocalPeopleViewModule localView, List<MediaModule> peoples, List<MediaModule> contents) {
        changePagingSize();
        if (localView != null && (peoples == null || peoples.isEmpty()) &&
                (contents == null || contents.isEmpty())) {
            if (!localView.isLayoutVisible()) {
                localView.doHidden();
                localView.doScale(0.0f);
            } else {
                if (localView.isRendered()) {
                    setLocalViewAnimateChange(localView);
                } else {
                    float scale = getLocalViewScale();
                    localView.doScale(scale);
                    setAnimateCreateView(localView);
                    localView.doScale(scale);
                }
            }
        } else {
            handleArrangeLayout(peoples, contents);
            updateLocalViewLayout(localView, peoples, contents);
        }
    }

    private boolean getContentEnabled() {
        return this.contentEnabled;
    }

    public boolean isCurrentContentPage() {
        return currentContent;
    }

    private void handleArrangeLayout(List<MediaModule> peoples, List<MediaModule> contents) {
        List<MediaModule> localAndPeoples = handleInVisibleView(peoples);
        List<GalleryLayoutData> layouts = getLayoutsBySize(localAndPeoples.size());
        if (layouts != null) {
            for (int index = 0; index < localAndPeoples.size(); index++) {
                if (index < MAX_VIEW_SIZE) {
                    setCommonViewAnimate(localAndPeoples.get(index), getLayout(index, layouts));
                }
            }
            if (contents != null && !contents.isEmpty() && contents.get(0) != null) {
                if (contents.get(0).isRendered()) {
                    setContentViewAnimateUpdate(contents.get(0));
                } else {
                    setContentViewAnimateCreate(contents.get(0));
                }
            }
        } else {
            if (contents != null && !contents.isEmpty() && contents.get(0) != null) {
                if (contents.get(0).isRendered()) {
                    setDefaultContentViewAnimateUpdate(contents.get(0));
                } else {
                    setDefaultContentViewAnimateCreate(contents.get(0));
                }
            }
        }
    }

    private void handlePeopleAndContentRelayout(List<MediaModule> peoples, List<MediaModule> contents) {
        List<MediaModule> localViewAndPeoples = handleInVisibleView(peoples);
        List<GalleryLayoutData> layouts = getLayoutsBySize(localViewAndPeoples.size());
        if (layouts != null) {
            for (int index = 0; index < localViewAndPeoples.size(); index++) {
                if (index < MAX_VIEW_SIZE) {
                    setCommonViewScaleAndAnimateRelayout(localViewAndPeoples.get(index), getLayout(index, layouts));
                }
            }
            if (contents != null && !contents.isEmpty()) {
                if (contents.get(0).isRendered()) {
                    setContentViewAnimateRelayoutUpdate(contents.get(0));
                } else {
                    setContentViewAnimateRelayoutCreate(contents.get(0));
                }
            }
        } else {
            if (contents != null && !contents.isEmpty() && contents.get(0) != null) {
                if (contents.get(0).isRendered()) {
                    setContentViewDefaultAnimateRelayout(contents.get(0));
                    screenMargin = screenWidth;
                    currentContent = true;
                } else {
                    setContentViewDefaultAnimateRelayout(contents.get(0));
                }
            }
        }
        if(getContentEnabled()){
            if (!localViewAndPeoples.isEmpty()) {
                pagingChangeListener.onPagingChanged(!currentContent, currentContent);
            } else {
                pagingChangeListener.onPagingChanged(false,false);
            }
        }
    }

    public void setContentEnabled(boolean enabled) {
        contentEnabled = enabled;
        if (enabled) {
            screenMargin = screenWidth;
            currentContent = true;
        } else {
            screenMargin = 0;
            currentContent = false;
        }
    }

    private void updateCurrentContentAndMargin(List<MediaModule> contents) {
        contentEnabled = (contents != null && !contents.isEmpty());
        if (contentEnabled) {
            if(currentContent){
                screenMargin = screenWidth;
            } else {
                screenMargin = 0;
            }
        } else {
            screenMargin = 0;
            currentContent = false;
        }
    }

    public void doLayoutAnimationByOrientationChanged(LocalPeopleViewModule localView, List<MediaModule> peoples, List<MediaModule> contents){
        changePagingSize();
        updateCurrentContentAndMargin(contents);
        if (localView != null
                && (peoples == null || peoples.isEmpty())
                && (contents == null || contents.isEmpty())) {
            handleLocalViewScaleAndsAnimateRelayout(localView);
        } else {
            handlePeopleAndContentRelayout(peoples, contents);
            handleDefaultLocalViewRelayout(localView, peoples, contents);
        }
    }

    private void handleLocalViewScaleAndsAnimateRelayout(LocalPeopleViewModule localView) {
        if (!localView.isLayoutVisible()) {
            localView.doHidden();
            localView.doScale(0.0f);
        } else {
            float scale = getLocalViewScale();
            localView.doScale(scale);
            setLocalViewAnimateRelayout(localView);
            localView.doScale(scale);
        }
    }

    private float getLocalViewScale() {
        return (float) screenWidth / (screenWidth - (float) screenWidth / SPILT_MARGIN_SIZE);
    }

    private void handleDefaultLocalViewRelayout(LocalPeopleViewModule localView, List<MediaModule> peoples, List<MediaModule> contents) {
        if ((peoples == null || peoples.isEmpty()) && (contents == null || contents.isEmpty())){
            setLocalViewAnimateRelayout(localView);
        } else {
            setLocalViewDefaultAnimateRelayout(localView);
        }
    }

    private void setCommonViewAnimate(MediaModule view, GalleryLayoutData layout) {
        if (view == null || layout == null || layout.getSize() <= 0) {
            return ;
        }
        int width = screenWidth / (int) (1 / layout.getSize());
        int height = screenHeight / (int) (1 / layout.getSize());
        int marginLeft = (int) (width * (layout.getLeft() / layout.getSize())) + calPadding(screenWidth, width);
        int marginTop = (int) (height * (layout.getTop() / layout.getSize())) + calPadding(screenHeight, height);
        if (view.isRendered()) {
            view.doUpdateAnimation(width, height, marginLeft + screenMargin, marginTop);
        } else {
            view.doScale((float) screenWidth / width);
            view.doCreateAnimation(width, height, marginLeft + screenMargin, marginTop);
        }
    }

    private void setCommonViewScaleAndAnimateRelayout(MediaModule view, GalleryLayoutData layout) {
        if (view == null || layout == null || layout.getSize() <= 0) {
            return ;
        }
        int width = screenWidth / (int) (1 / layout.getSize());
        int height = screenHeight / (int) (1 / layout.getSize());
        int marginLeft = (int) (width * (layout.getLeft() / layout.getSize())) + calPadding(screenWidth, width);
        int marginTop = (int) (height * (layout.getTop() / layout.getSize())) + calPadding(screenHeight, height);
        view.doScale((float) screenWidth / width);
        view.doLayoutAnimation(width, height, marginLeft + screenMargin, marginTop);
    }

    public void switchToPrevious(List<MediaModule> peoples, List<MediaModule> contents) {
        if (peoples == null || peoples.isEmpty()) {
            return;
        }
        if (getContentEnabled()) {
            if (!currentContent) {
                screenMargin = screenWidth;
                handleArrangeLayout(peoples, contents);
                currentContent = true;
                pagingChangeListener.onPagingChanged(false, true);
            }
        }
    }

    public void switchToNext(List<MediaModule> peoples, List<MediaModule> contents) {
        if (peoples == null || peoples.isEmpty()) {
            return;
        }
        if (currentContent) {
            screenMargin = 0;
            handleArrangeLayout(peoples, contents);
            currentContent = false;
            pagingChangeListener.onPagingChanged(true, false);
        }
    }


}

