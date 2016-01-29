package com.star.mobile.video.util.http;

public class HTTPException extends RuntimeException{

	private static final long serialVersionUID = 1762549796737182443L;

	public HTTPException() {
		super("HTTP Request error");
	}
	
	public HTTPException(String msg) {
		super("HTTP Request error,"+msg);
	}
	
	public HTTPException(String msg,Throwable t) {
		super("HTTP Request error,"+msg,t);
	}
}
