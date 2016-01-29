package com.star.util.loader;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.star.util.InternalStorage;
import com.star.util.Logger;
import com.star.util.http.IOUtil;
import com.star.util.json.JSONUtil;

public abstract class AbsLoader<V> {
	/**
	 * 缓存的核心类
	 */
	private LruCache<String, V> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	/**
	 * 队列的调度方式
	 */
	protected Type mType = Type.LIFO;
	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTasks;
	/**
	 * 轮询的线程
	 */
	private Thread mPoolThread;
	private Handler mPoolThreadHander;

	/**
	 * 运行在UI线程的handler
	 */
	protected Handler mHandler;

	/**
	 * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
	 */
	private volatile Semaphore mSemaphore = new Semaphore(0);

	/**
	 * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
	 */
	protected volatile Semaphore mPoolSemaphore;

	/**
	 * 队列的调度方式
	 * 
	 */
	public enum Type {
		FIFO, LIFO
	}
	
	public LruCache<String, V> getLruCache(){
		if(mLruCache == null){
			// 获取应用程序最大可用内存
			int maxMemory = (int) Runtime.getRuntime().maxMemory();
			int cacheSize = maxMemory / 8;
			mLruCache = new LruCache<String, V>(cacheSize);
		}
		return mLruCache;
	}
	
	public void setLruCache(LruCache<String, V> lruCache){
		this.mLruCache = lruCache;
	}
 
	public AbsLoader(int threadCount, Type type) {
		init(threadCount, type);
	}

	private void init(int threadCount, Type type) {
		// loop thread
		mPoolThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();

				mPoolThreadHander = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						mThreadPool.execute(getTask());
						try {
							mPoolSemaphore.acquire();
						} catch (InterruptedException e) {
						}
					}
				};
				// 释放一个信号量
				mSemaphore.release();
				Looper.loop();
			}
		};
		mPoolThread.start();

		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mPoolSemaphore = new Semaphore(threadCount);
		mTasks = new LinkedList<Runnable>();
		mType = type == null ? Type.LIFO : type;
	}

	/**
	 * 添加一个任务
	 * 
	 * @param runnable
	 */
	protected synchronized void addTask(Runnable runnable) {
		try {
			// 请求信号量，防止mPoolThreadHander为null
			if (mPoolThreadHander == null)
				mSemaphore.acquire();
		} catch (InterruptedException e) {
		}
		mTasks.add(runnable);

		mPoolThreadHander.sendEmptyMessage(0x110);
	}

	/**
	 * 取出一个任务
	 * 
	 * @return
	 */
	private synchronized Runnable getTask() {
		if (mType == Type.FIFO) {
			return mTasks.removeFirst();
		} else if (mType == Type.LIFO) {
			return mTasks.removeLast();
		}
		return null;
	}
	
	/**
	 * 从LruCache中获取，如果不存在就返回null。
	 */
	protected V getFromLruCache(String url) {
		return getLruCache().get(url);
	}

	/**
	 * 往LruCache中添加
	 * 
	 * @param url
	 * @param bitmap
	 */
	protected void addToLruCache(String url, V value) {
//		if (getFromLruCache(url) == null) {
			if (value != null)
				getLruCache().put(url, value);
//		}
	}
	
	/**
	 * 
	 * @param path
	 * @param imageView
	 * @throws Exception 
	 */
	public void load(Context context, final String url, final OnResultTagListener<V> listener, final Class<V> clzz, final LoadMode type){
//		Logger.i("KEY:+++++"+url);
		// UI线程
		listener.setTag(url);
		if (mHandler == null) {
			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					ResponseVO<V> holder = (ResponseVO<V>) msg.obj;
					OnResultTagListener<V> hListener = (OnResultTagListener<V>) holder.listener;
					String hurl = holder.url;
					V hValue = holder.value;
					if (hListener!=null&&hListener.getTag().equals(hurl)) {
						hListener.onSuccess(hValue);
					}
				}
			};
		}

		if(LoadMode.CACHE.equals(type)){
			//先缓存取
			V value = getFromLruCache(url);
			if (value != null) {
				sendHandler(url, listener, value, 200);
				Logger.i("Type:"+type+" load from memory:"+url);
			} else {
				//缓存没有sd卡取
				try {
					String jsonL = InternalStorage.getStorage(context).get(url);
					if(jsonL != null){
						value = JSONUtil.getFromJSON(jsonL,clzz);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(value != null){
					addToLruCache(url, value);
					sendHandler(url, listener, value, 200);
					Logger.i("Type:"+type+" load from sdcard:"+url);
				}else{
					loadFromServer(url, listener, clzz, type);
				}
			}
		}else{
			V value = getFromLruCache(url);
			if (value != null) {
				Logger.i("Type:"+type+" load from memory:"+url);
				sendHandler(url, listener, value, 200);
			} else {
				//缓存没有sd卡取
				try {
					String jsonL = InternalStorage.getStorage(context).get(url);
					if(jsonL != null){
						value = JSONUtil.getFromJSON(jsonL,clzz);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(value != null){
					addToLruCache(url, value);
					sendHandler(url, listener, value, 200);
					Logger.i("Type:"+type+" load from sdcard:"+url);
				}
			}
			loadFromServer(url, listener, clzz, type);
		}
	}
	
	private void loadFromServer(final String url, final OnResultListener<V> listener, final Class<V> clzz, final LoadMode type) {
		addTask(new Runnable() {
			@Override
			public void run() {
				try{
					HttpResponse response = IOUtil.httpGet(url);
					if(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300){
						String jsonS = IOUtil.httpResonseToJSON(response);
						V valueS = JSONUtil.getFromJSON(jsonS,clzz);
						if(valueS!=null){
							Logger.i("Type:"+type+" load from net:"+url);
							addToLruCache(url, valueS);
							sendHandler(url, listener, valueS, 200);
						}else{
							sendHandler(url, listener, valueS, 408);
						}
					}else{
						sendHandler(url, listener, null, response.getStatusLine().getStatusCode());
					}
				}catch(Exception e){
					sendHandler(url, listener, null, 500);
				}
				mPoolSemaphore.release();
			}
		});
	}

	private void sendHandler(final String url, final OnResultListener<V> listener, V value, int statusCode) {
		ResponseVO<V> holder = new ResponseVO<V>();
		Message message = Message.obtain();
		message.obj = holder;
		holder.url = url;
		holder.listener = listener;
		holder.value = value;
		holder.statusCode = statusCode;
		mHandler.sendMessage(message);
	}
}
