package com.star.util;

import android.os.Handler;
import android.os.Looper;

public class MainUIHandler extends Handler{
	private static MainUIHandler uiHandler;
	
	private MainUIHandler(Looper looper){
		super(looper);
	}
	
	public static synchronized MainUIHandler handler(){
		if(uiHandler == null){
			uiHandler = new MainUIHandler(Looper.getMainLooper());
		}
		return uiHandler;
	}
	
	public void postWaiting(Runnable r){
		PermissionUtil.checkNotInMainThread();
		WaitingRunnableProxy rProxy = new WaitingRunnableProxy(r);
		post(rProxy);
		rProxy.waiting();		
	}
	
	static class WaitingRunnableProxy implements Runnable{
		private Runnable innerRunnable;
		private SingleBlock block = new SingleBlock();
		
		public WaitingRunnableProxy(Runnable r){
			innerRunnable = r;
		}
		
		@Override
		public void run() {
			innerRunnable.run();
			block.signal();
		}
		
		public void waiting(){
			block.waiting();
		}		
	}
}
