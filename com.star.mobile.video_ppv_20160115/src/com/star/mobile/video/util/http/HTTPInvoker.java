package com.star.mobile.video.util.http;

import android.os.Handler;

import com.loopj.android.http.RequestHandle;
import com.star.mobile.video.util.AsyTask;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ThreadPoolManager;
import com.star.util.Log;

public abstract class HTTPInvoker<T>{
	
	private String url;
	
	private String httpType;
	
	private Object typeReturn;
	
	private Object objectReturn;
	
	private int statusLine;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what==MessageType.SUCCESS.getInt()){
				onSuccess((T)objectReturn);
			}else{
				onFail(statusLine);
			}
		};
	};
	
	/**
	 * IN MainUI thread
	 * @param statusLine
	 */
	public abstract void onFail(int statusLine);
	
	/**
	 * IN MainUI thread
	 * @param dataReturn
	 */
	public abstract void onSuccess(T dataReturn);
	
	/**
	 * NON MainUI thread
	 * @param dataReturn
	 * @return 
	 */
	public abstract RequestHandle http();
	
	protected void sendUIMessage(MessageType type){
		handler.sendEmptyMessage(type.getInt());
	}
	
	public enum MessageType{
		SUCCESS(0),FAIL(1);
		private int type;
		private MessageType(int t) {
			this.type = t;
		}
		public int getInt(){
			return type;
		}
	}
	
	public void go(){
		executeAsych();
	}
	
	private void executeAsych() {
		final AsyTask at = new AsyTask();
		at.runnable = new Runnable() {
			@Override
			public void run() {
				try {
					LoadingDataTask.tasks.remove(at);
//					at.runThread = Thread.currentThread();
					if(!at.cancel){
						at.requestHandle = http();
					}
				} catch (Throwable e) {
					Log.e("LoadingDataTask", "Backend task error.",e);
				}
				
			}
		};
		LoadingDataTask.tasks.add(at);
		ThreadPoolManager.getInstance().addTask2(at.runnable);
	}
	
	protected void checkSelf(){
		if(getUrl()==null||getHttpType()==null){
			throw new HTTPException("HTTP invoker need URL,httpType and typeReturn params.");
		}
	}

	public Object getTypeReturn() {
		return typeReturn;
	}

	/**
	 * 无返回值 默认为String
	 * @param typeReturn
	 */
	public void setTypeReturn(Object typeReturn) {
		this.typeReturn = typeReturn;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHttpType() {
		return httpType;
	}

	public void setHttpType(String httpType) {
		this.httpType = httpType;
	}

	protected void setObjectReturn(Object objectReturn) {
		this.objectReturn = objectReturn;
	}

	protected void setStatusLine(int statusLine) {
		this.statusLine = statusLine;
	}
	
}
