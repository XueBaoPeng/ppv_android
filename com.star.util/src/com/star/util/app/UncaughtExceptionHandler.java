package com.star.util.app;

import android.content.Context;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler handler;
	private static UncaughtExceptionHandler uncaughtExceptionHandler;

	public static void init(Context context){
		if(uncaughtExceptionHandler == null){
			uncaughtExceptionHandler = new UncaughtExceptionHandler();
			
			uncaughtExceptionHandler.handler = Thread.getDefaultUncaughtExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
		}
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		handler.uncaughtException(thread, ex);
	}
}
