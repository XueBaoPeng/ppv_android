package com.star.mobile.video.model;

import java.io.Serializable;

public class ShareChatRoomModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4585430020725118351L;
	private int type;
	private String msg;
	private LinkPkg pkg;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public LinkPkg getPkg() {
		return pkg;
	}

	public void setPkg(LinkPkg pkg) {
		this.pkg = pkg;
	}

}
