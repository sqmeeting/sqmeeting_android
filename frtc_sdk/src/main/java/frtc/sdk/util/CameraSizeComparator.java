package frtc.sdk.util;

import android.hardware.Camera;

import java.util.Comparator;

public class CameraSizeComparator implements Comparator<Camera.Size> {

    private int transResolution(Camera.Size s) {
        return s.height * s.width;
    }

    @Override
    public int compare(Camera.Size size1, Camera.Size size2) {
        int s = transResolution(size1) - transResolution(size2);
        if (s != 0) {
            return s;
        } else {
            return size1.width - size2.width;
        }
    }

}
