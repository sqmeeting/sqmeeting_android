package frtc.sdk.internal.jni;

public class Renderer {
	static {
		Renderer.init();
	}
	//do not remove it
	private long renderObject;
	public static native void init();
	public native boolean start();
	public native void stop();
	public native boolean render(String msid, int w, int h);
}
