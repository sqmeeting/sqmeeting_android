package frtc.sdk.ui.media;

import java.nio.ByteBuffer;

public interface IMediaDataHandler {
	void writeBuffer(ByteBuffer bf, int s, int r);
	void write(byte[] array, int by, int r);
}
