package com.star.util.http;

public class NETException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1199876871256653615L;
	
	public NETException() {
		
	}
	
	public NETException(String message) {
		super(message);
	}
	

	public NETException(String message ,Throwable th) {
		super(message,th);
	}
}
