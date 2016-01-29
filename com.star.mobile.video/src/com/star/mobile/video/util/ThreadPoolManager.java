package com.star.mobile.video.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
	private ExecutorService service;
	private ExecutorService service2;
	private ThreadPoolManager() {
		int num = Runtime.getRuntime().availableProcessors();
		service = Executors.newFixedThreadPool(num * 2 + 1);
		service2 = Executors.newFixedThreadPool(num * 2 + 1);
	}

	private static final ThreadPoolManager manager = new ThreadPoolManager();

	public static ThreadPoolManager getInstance() {
		return manager;
	}
	
	public ExecutorService getExecutorService(){
		return service;
	}

	public void addTask(Runnable runnable) {
		service.execute(runnable);
	}
	
	public void addTask2(Runnable runnable) {
		service2.execute(runnable);
	}
	
	public void shutdownNow(){
		service.shutdownNow();
	}
}
