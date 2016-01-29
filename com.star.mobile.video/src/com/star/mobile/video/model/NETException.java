package com.star.mobile.video.model;

public class NETException extends APPException{

	
	private static final long serialVersionUID = -1247959440881564035L;

	public NETException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public NETException(Throwable t) {
		super(t);
	}
	
	public NETException(String msg) {
		super(msg);
	}
	
	public NETException() {
		super();
	}
}
