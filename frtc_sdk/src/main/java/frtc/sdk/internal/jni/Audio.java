package frtc.sdk.internal.jni;

public class Audio {
	public static native boolean read(String msId, byte[] d, int l, int sample, int ql);
	public static native boolean write(String msId, byte[] d, int l, int sample);
}
