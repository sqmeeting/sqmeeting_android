package frtc.sdk.internal.jni;

import java.nio.ByteBuffer;

public class Video {
	public static native boolean write(String msId, byte[] d, int l, int w, int h, int r, int bp);
	public static native boolean writeBuffer(String msId, ByteBuffer d, int w, int h, int r, int s, int st);
}
