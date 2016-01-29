package com.star.mobile.video.model;

public class APPException extends RuntimeException{


	private static final long serialVersionUID = -5543381845067024120L;

	public APPException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public APPException(Throwable t) {
		super(t);
	}
	
	public APPException(String msg) {
		super(msg);
	}
	
	public APPException() {
		super();
	}
}
