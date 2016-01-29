package com.star.util.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

import com.star.util.InternalStorage;
import com.star.util.Logger;
import com.star.util.NetworkUtil;
import com.star.util.json.JSONUtil;
import com.star.util.loader.AsyncTask.Request;

/**
 * 
 * @author dujr
 *
 */
@SuppressWarnings("unchecked")
public class AsyncTaskHolder {
	
	private static AsyncTaskHolder instance;
	private Context context;
	private final ArrayBlockingQueue<AsyncTask<?>> asyncs = new ArrayBlockingQueue<AsyncTask<?>>(100);
	private ExecutorService service;
	
	private AsyncTaskHolder(Context context){
		this.context = context;
		int num = Runtime.getRuntime().availableProcessors();
		this.service = Executors.newFixedThreadPool(num * 2 + 1);
	};
	
	public static AsyncTaskHolder getInstance(Context context){
		if(instance == null)
			instance = new AsyncTaskHolder(context);
		return instance;
	}

	/**
	 * 发送get请求， 不带提示框
	 * 
	 * @param url 地址
	 * @param listener 回调监听器
	 * @param context 上下文环境
	 */
	public synchronized <T> void sendGet(String url, Class<T> clzz, OnResultListener<T> listener, LoadMode mode) {
		if(listener!=null && listener.onIntercept()){
			return;
		}
		if(LoadMode.CACHE.equals(mode)){
			loadLocal(url, clzz, listener);
			return;
		}else if(LoadMode.IFCACHE_NOTNET.equals(mode)){
			if(loadLocal(url, clzz, listener))
				return;
		}else if(LoadMode.CACHE_NET.equals(mode)){
			loadLocal(url, clzz, listener);
		}
		if(!checkNetStatus(listener, context)){
			return;
		}
		RequestVO<T> params = new RequestVO<T>();
		params.url = url;
		params.clazz = clzz;
		AsyncTask<T> async = new AsyncTask<T>(context);
		async.req = Request.GET;
		params.listener = listener;
		params.mode = mode;
		async.executeOnExecutor(service, params);
		addAsync(url, async);
	}
	
	private <T> boolean loadLocal(String url, Class<T> clazz, OnResultListener<T> listener) {
		try {
			String json = InternalStorage.getStorage(context).get(url);
			if(listener instanceof OnListResultListener){
				((OnListResultListener<T>)listener).onSuccess((List<T>)JSONUtil.parserJsonToList(clazz, json));
			}else if(listener instanceof OnResultListener){
				((OnResultListener<T>)listener).onSuccess((T)JSONUtil.getFromJSON(json, clazz));
			}
			return true;
		} catch (Exception e) {
			Logger.e("load data from local error!");
		}
		return false;
	}

	/**
	 * 发送Post请求到服务器
	 * 
	 * @param url
	 * @param params key=value
	 * @param listener
	 * @param context
	 */
	public synchronized <T> void sendPost(String url, Map<String, Object> params, Class<T> clzz, OnResultListener<T> listener) {
		if(listener!=null && listener.onIntercept()){
			return;
		}
		if(!checkNetStatus(listener, context)){
			return;
		}
		RequestVO<T> param = new RequestVO<T>();
		param.url = url;
		param.clazz = clzz;
		param.params = params;
		AsyncTask<T> async = new AsyncTask<T>(context);
		async.req = Request.POST;
		param.listener = listener;
		async.executeOnExecutor(service, param);
		addAsync(url, async);
	}
	
	/**
	 * 发送delete请求到服务器
	 * 
	 * @param url
	 * @param params key=value
	 * @param listener
	 * @param context
	 */
	public synchronized <T> void sendDelete(String url, Class<T> clzz, OnResultListener<T> listener) {
		if(listener!=null && listener.onIntercept()){
			return;
		}
		if(!checkNetStatus(listener, context)){
			return;
		}
		RequestVO<T> param = new RequestVO<T>();
		param.url = url;
		param.clazz = clzz;
		AsyncTask<T> async = new AsyncTask<T>(context);
		async.req = Request.DELETE;
		param.listener = listener;
		async.executeOnExecutor(service, param);
		addAsync(url, async);
	}
	
	/**
	 * 发送图片
	 * @param url
	 * @param clzz
	 * @param listener
	 */
	public synchronized <T> void postImage(List<BitmapUploadParams> bitmapParams, Class<T> clzz, OnResultListener<T> listener) {
		if(listener!=null && listener.onIntercept()){
			return;
		}
		if(!checkNetStatus(listener, context)){
			return;
		}
		RequestVO<T> param = new RequestVO<T>();
		param.clazz = clzz;
		if(bitmapParams!=null&&bitmapParams.size()>0){
			Map<String, Object> ps = new HashMap<String, Object>();
			ps.put("bitmaps", bitmapParams);
			param.params = ps;
		}else{
			throw new IllegalArgumentException("bitmaps must be set.");
		}
		AsyncTask<T> async = new AsyncTask<T>(context);
		async.req = Request.POST_IMAGE;
		param.listener = listener;
		async.executeOnExecutor(service, param);
		addAsync("post image.....", async);
	}
	
	private <T> boolean checkNetStatus(OnResultListener<T> listener, Context context) {
		boolean netStatus = NetworkUtil.isNetworkAvailable(context);
		if(!netStatus){
			if(listener!=null){
				listener.onFailure(AsyncTask.NETWORK_ERROR, "");
			}
		}
		return netStatus;
	}
	
	/**
	 * 将一个异步任务放入异步请求池
	 * @param async
	 */
	public void addAsync(String url, AsyncTask<?> async){
		asyncs.offer(async);
		Logger.w("add a new asynctask, and "+asyncs.size()+" asynctasks are waiting. url="+url);
	}
	
	/**
	 * 将一个异步任务放入异步请求池
	 * @param async
	 */
	public void removeAsync(AsyncTask<?> async){
		asyncs.remove(async);
		Logger.w("remove a running asynctask, and "+asyncs.size()+" asynctasks are waiting");
	}
	
	/**
	 * 清除所有的网络异步请求任务
	 */
    public void clearAsyncTask() {
    	Logger.w("Cancel "+asyncs.size()+" asynctasks except the running...");
        AsyncTask<?> task = asyncs.poll();
		while(task!=null){
			if(!task.isCancelled())
				task.cancel(true);
			task = asyncs.poll();
		}
    }
}
