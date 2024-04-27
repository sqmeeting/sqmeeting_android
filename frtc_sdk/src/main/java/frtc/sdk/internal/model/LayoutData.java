package frtc.sdk.internal.model;

public class LayoutData {
	private String msid;
	private String uuid;
	private long width;
	private long height;
	private long surface_idx;
	 private String display_name;
	 private int bit_rate;
	 private int frame_rate;

	 public String getUuid() {
		 return uuid;
	 }

	 public void setUuid(String uuid) {
		 this.uuid = uuid;
	 }

	 public String getDisplay_name() {
	 return display_name;
	 }
	 public void setDisplay_name(String display_name) {
		 this.display_name = display_name;
	 }

	 public int getBit_rate() {
	 	return bit_rate;
	 }

	 public void setBit_rate(int bit_rate) {
	 	this.bit_rate = bit_rate;
	 }

	 public int getFrame_rate() {
	 	return frame_rate;
	 }

	 public void setFrame_rate(int frame_rate) {
	 	this.frame_rate = frame_rate;
	 }

	 public int getWidth() {
	 	return (int)width;
	 }

	 public void setWidth(long width) {
	 	this.width = width;
	 }

	 public int getHeight() {
	 	return (int)height;
	 }

	 public void setHeight(long height) {
	 	this.height = height;
	 }

	public String getMsid() {
		return msid;
	}

	public void setMsid(String msid) {
		this.msid = msid;
	}

	public int getSurface_idx() {
		return (int) surface_idx;
	}

	public void setSurface_idx(long surface_idx) {
		this.surface_idx = surface_idx;
	}

}
