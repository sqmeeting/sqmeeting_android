package frtc.sdk.ui.device;

import android.annotation.SuppressLint;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;

import java.nio.ByteBuffer;

import frtc.sdk.internal.jni.Video;
import frtc.sdk.internal.model.VideoColorType;
import frtc.sdk.log.Log;
import frtc.sdk.ui.media.IMediaDataHandler;

public class ContentMediaDataHandler implements IMediaDataHandler {
    public static final String TAG = ContentMediaDataHandler.class.getSimpleName();
    private String msid;
    private int w;
    private int h;
    private int imgWidth;
    private int imgHeight;
    private HandlerThread thread;
    private Handler bgHandler;
    private ImageReader iReader;
    private int count = 0;

    public ContentMediaDataHandler(String msid) {
        super();
        this.msid = msid;
    }

    public void setMsid(String msid){
        this.msid = msid;
    }

    public void setSize(int width, int height) {
        this.w = width;
        this.h = height;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    @SuppressLint("WrongConstant")
    public ImageReader getImageReader(final int width, final int height) {
        this.imgWidth = width;
        this.imgHeight = height;
        this.count = 0;
        this.iReader = ImageReader.newInstance(width, height, 0x1, 2);
        this.iReader.setOnImageAvailableListener(reader -> {
            Image image = reader.acquireLatestImage();
            if(image != null){
                try{
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer byte_buffer = planes[0].getBuffer();
                    int pixel_stride = planes[0].getPixelStride();
                    int row_stride = planes[0].getRowStride();
                    int row_padding = row_stride - pixel_stride * width;
                    int row_width = width + row_padding / pixel_stride;
                    writeBuffer(byte_buffer, row_width,0);
                    count++;
                    image.close();
                    if(count % 10 == 0){
                        //TODO: check if this is necessary
                        Runtime.getRuntime().gc();
                        System.runFinalization();
                    }
                } catch (Exception e) {
                    Log.e(TAG,"getImageReader:"+e);
                }
            }
        }, bgHandler);
        return iReader;
    }

    @Override
    public void write(byte[] array, int by, int r) {
        try {
            if (array != null) {
                if (r > 0 || r == 0) {
                    Video.write(this.msid, array, array.length, w, h, r, by);
                } else {
                    Video.write(this.msid, array, array.length, w, h, 0, by);
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"write Exception" + e.getMessage());
        }
    }

    @Override
    public void writeBuffer(ByteBuffer data, int s, int r) {
        try {
            if (r == 0 || r > 0) {
                Video.writeBuffer(this.msid, data, imgWidth, imgHeight, r, VideoColorType.kABGR.getCode(), s);
            } else {
                Video.writeBuffer(this.msid, data, imgWidth, imgHeight, 0, VideoColorType.kABGR.getCode(), s);
            }
        } catch (Exception e) {
            Log.e(TAG,"writeBuffer Exception" + e.getMessage());
        }
    }

    public void start() {
        if (thread == null && bgHandler == null) {
            thread = new HandlerThread("contentBackground");
            thread.start();
            bgHandler = new Handler(thread.getLooper());
        }
    }

    public void stop(){
        try {
            if (thread == null) {
                return;
            }
            thread.quitSafely();
            thread.join();
            thread = null;
            bgHandler = null;
            closeReader();
        } catch (Exception e) {
            Log.e(TAG,"stop failed " + e.getMessage());
        }
    }

    public void discard() {
        try {
            if (thread == null) {
                return;
            }
            thread.quit();
            thread.join();
            thread = null;
            bgHandler = null;
            closeReader();
        } catch (Exception e) {
            Log.e(TAG,"discard failed " + e.getMessage());
        }
    }

    public void closeReader(){
        if (iReader != null) {
            iReader.setOnImageAvailableListener(null, null);
            iReader.close();
            iReader = null;
        }
    }

}
