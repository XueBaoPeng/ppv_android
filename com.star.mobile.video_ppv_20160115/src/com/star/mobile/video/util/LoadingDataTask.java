package com.star.mobile.video.util;

import java.util.concurrent.ArrayBlockingQueue;

import android.os.Handler;
import android.os.Message;

import com.google.android.gms.analytics.HitBuilders;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.model.MyTimer;
import com.star.util.Log;
import com.star.util.http.HTTPReqt;
import com.star.util.thread.ThreadLocalMap;
import com.star.util.thread.ThreadLocalString;

public abstract class LoadingDataTask {

	public static final ArrayBlockingQueue<AsyTask> tasks = new ArrayBlockingQueue<AsyTask>(100);
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0){
				Log.d("REQ_POSTUI", ""+(System.currentTimeMillis()-(Long)msg.obj));
				try{
					onPostExecute();
				}catch (Exception e) {
					Log.e("LoadingDataTask", "onPost task error.",e);
				}
			}
			
		};
	};

	public abstract void onPreExecute();

	public abstract void doInBackground();

	public abstract void onPostExecute();

	public void execute() {
		onPreExecute();
		final AsyTask at = new AsyTask();
		at.runnable = new Runnable() {
			@Override
			public void run() {
				try {
					tasks.remove(at);
//					at.runThread = Thread.currentThread();
					if(!at.cancel){
//						long b = System.currentTimeMillis();
						doInBackground();
						long now = System.currentTimeMillis();
						Object http = ThreadLocalMap.get(ThreadLocalString.HTTP_REQ_TAG);
						if(http!=null){
							HTTPReqt hrt = (HTTPReqt)http;
							StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(hrt.getUrl()).setAction(hrt.getPath()).setLabel(hrt.getParams()).setValue(now-hrt.getStartDate()).build());
							Log.d("REQ_HTTP", hrt.toString()+(now-hrt.getStartDate()));
//							Log.d("REQ_HTTP1", hrt.toString()+(now - b));
						}
						Message m = new Message();
						m.obj = now;
						handler.sendMessage(m);
					}
				} catch (Throwable e) {
					CommonUtil.closeProgressDialog();
					Log.e("LoadingDataTask", "Backend task error.",e);
				}finally{
					ThreadLocalMap.remove(ThreadLocalString.HTTP_REQ_TAG);
				}
				
			}
		};
//		tasks.add(at);
		tasks.offer(at);
		ThreadPoolManager.getInstance().addTask(at.runnable);
	}
	public static void cancelExistedTasks(){
		Log.w("LoadingDataTask", "Cancel "+tasks.size()+" existed tasks except the running...");
		AsyTask task = tasks.poll();
		while(task!=null){
			task.cancel = true;
//			if(task.runThread!=null&&task.runThread.isAlive()){
//				task.runThread.interrupt();
//			}
			if(task.requestHandle!=null){
				task.requestHandle.cancel(true);
			}
			task = tasks.poll();
		}
	}
	
	private final static ArrayBlockingQueue<MyTimer> timers = new ArrayBlockingQueue<MyTimer>(100);
	
	public static void cancelExistedTimers(){
		Log.w("LoadingDataTask", "Cancel "+timers.size()+" existed timer");
		MyTimer timer = timers.poll();
		while(timer!=null){
			if(timer.innerTask!=null)
				timer.innerTask.cancel();
			timer.cancel();
			timer.cancel = true;
			timer = timers.poll();
		}
		timers.clear();
	}
	
	public static void addTimerToList(MyTimer timer){
		if(timers.size()==100){
			cancelExistedTimers();
		}
		timers.add(timer);
		Log.w("LoadingDataTask", timers.size()+" timers exist now");
	}
	
	public static void removeTimer(MyTimer timer){
		timers.remove(timer);
		Log.w("LoadingDataTask", timers.size()+" timers exist now");
	}
	
}
