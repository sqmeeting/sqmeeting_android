/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;

import java.util.ArrayList;
import java.util.List;

import frtc.sdk.R;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;

    private CameraManager cameraManager;
    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
//    private final int resultColor;
//    private final int laserColor;
//    private final int resultPointColor;
    private final int statusColor;
    private int scannerAlpha;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;

    private int scanLineTop;
    private final int SCAN_VELOCITY = 10;
    Bitmap scanLight;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.transparent_37);
//        resultColor = resources.getColor(R.color.result_view);
//        laserColor = resources.getColor(R.color.viewfinder_laser);
//        resultPointColor = resources.getColor(R.color.possible_result_points);
        statusColor = resources.getColor(R.color.white);
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;
//        scanLight = BitmapFactory.decodeResource(resources,
//                R.drawable.scan_light);
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
        Rect frame = cameraManager.getFramingRect();
        Rect previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(maskColor);
        //canvas.drawRect(0, 0, width, frame.top, paint);
        //canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        //canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        //canvas.drawRect(0, frame.bottom + 1, width, height, paint);

//        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
//            paint.setAlpha(CURRENT_POINT_OPACITY);
//            canvas.drawBitmap(resultBitmap, null, frame, paint);
//        } else {

            //drawFrameBounds(canvas, frame);
            //drawStatusText(canvas, frame, width);
//            drawScanLight(canvas,frame);
            // Draw a red "laser scanner" line through the middle to show decoding is active
//            paint.setColor(laserColor);
//            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
//            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//            int middle = frame.height() / 2 + frame.top;
//            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);
//
//            float scaleX = frame.width() / (float) previewFrame.width();
//            float scaleY = frame.height() / (float) previewFrame.height();
//
//            List<ResultPoint> currentPossible = possibleResultPoints;
//            List<ResultPoint> currentLast = lastPossibleResultPoints;
//            int frameLeft = frame.left;
//            int frameTop = frame.top;
//            if (currentPossible.isEmpty()) {
//                lastPossibleResultPoints = null;
//            } else {
//                possibleResultPoints = new ArrayList<>(5);
//                lastPossibleResultPoints = currentPossible;
//                paint.setAlpha(CURRENT_POINT_OPACITY);
//                paint.setColor(resultPointColor);
//                synchronized (currentPossible) {
//                    for (ResultPoint point : currentPossible) {
//                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
//                                frameTop + (int) (point.getY() * scaleY),
//                                POINT_SIZE, paint);
//                    }
//                }
//            }
//            if (currentLast != null) {
//                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
//                paint.setColor(resultPointColor);
//                synchronized (currentLast) {
//                    float radius = POINT_SIZE / 2.0f;
//                    for (ResultPoint point : currentLast) {
//                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
//                                frameTop + (int) (point.getY() * scaleY),
//                                radius, paint);
//                    }
//                }
//            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
//        }
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }
    /**
     * Draw Frame Bounds
     *
     * @param canvas
     * @param frame
     */
    private void drawFrameBounds(Canvas canvas, Rect frame) {

//        paint.setColor(Color.WHITE);
//        paint.setStrokeWidth(2);
//        paint.setStyle(Paint.Style.STROKE);
//
//        canvas.drawRect(frame, paint);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        int corWidth = getResources().getDimensionPixelSize(R.dimen.qrscan_corner_width);
        int corLength = getResources().getDimensionPixelSize(R.dimen.qrscan_corner_length);
        int corSpace = getResources().getDimensionPixelSize(R.dimen.qrscan_corner_margin_scan_frame);
        int corRadius = getResources().getDimensionPixelSize(R.dimen.qrscan_corner_radius);

        // top left corner
        canvas.drawRoundRect(frame.left - corWidth - corSpace, frame.top - corWidth - corSpace,
                frame.left - corSpace, frame.top - corSpace - corWidth + corLength, corRadius, corRadius, paint);
        canvas.drawRoundRect(frame.left - corWidth - corSpace, frame.top - corWidth - corSpace,
                frame.left - corSpace - corWidth + corLength, frame.top - corSpace, corRadius, corRadius, paint);
        // top right corner
        canvas.drawRoundRect(frame.right + corSpace, frame.top - corSpace - corWidth,
                frame.right + corSpace + corWidth, frame.top - corSpace - corWidth + corLength, corRadius, corRadius, paint);
        canvas.drawRoundRect(frame.right + corSpace + corWidth - corLength, frame.top - corSpace - corWidth,
                frame.right + corSpace + corWidth, frame.top - corSpace,  corRadius, corRadius, paint);
        // bottom left corner
        canvas.drawRoundRect(frame.left - corSpace - corWidth, frame.bottom + corSpace + corWidth - corLength,
                frame.left - corSpace, frame.bottom + corSpace + corWidth, corRadius, corRadius, paint);
        canvas.drawRoundRect(frame.left - corSpace - corWidth, frame.bottom + corSpace,
                frame.left - corSpace - corWidth + corLength, frame.bottom + corSpace + corWidth, corRadius, corRadius, paint);
        // bottom right corner
        canvas.drawRoundRect(frame.right + corSpace, frame.bottom + corSpace + corWidth - corLength,
                frame.right + corSpace + corWidth, frame.bottom + corSpace + corWidth, corRadius, corRadius, paint);
        canvas.drawRoundRect(frame.right + corSpace + corWidth - corLength, frame.bottom + corSpace,
                frame.right + corSpace + corWidth, frame.bottom + corSpace + corWidth, corRadius, corRadius, paint);
    }

//    /**
//     * Draw Scan Light
//     *
//     * @param canvas
//     * @param frame
//     */
//    private void drawScanLight(Canvas canvas, Rect frame) {
//
//        if (scanLineTop == 0) {
//            scanLineTop = frame.top;
//        }
//
//        if (scanLineTop >= frame.bottom - 30) {
//            scanLineTop = frame.top;
//        } else {
//            scanLineTop += SCAN_VELOCITY;
//        }
////        Rect scanRect = new Rect(frame.left, scanLineTop - 1, frame.right,
////                scanLineTop + 30);
////        canvas.drawBitmap(scanLight, null, scanRect, paint);
//    }


    /**
     * Draw Status Text
     *
     * @param canvas
     * @param frame
     * @param width
     */
    private void drawStatusText(Canvas canvas, Rect frame, int width) {

//        String statusText1 = getResources().getString(
//                R.string.viewfinderview_status_text1);
        String statusText = getResources().getString(R.string.msg_default_status);

        int statusTextSize = getResources().getDimensionPixelSize(R.dimen.title_text_size);
        int statusPaddingTop = getResources().getDimensionPixelSize(R.dimen.qrscan_status_margin_scan_frame) + statusTextSize/2;

        paint.setColor(statusColor);
        paint.setTextSize(statusTextSize);

        int textWidth = (int) paint.measureText(statusText);
        canvas.drawText(statusText, (width - textWidth) / 2, frame.bottom
                + statusPaddingTop, paint);
    }

}
