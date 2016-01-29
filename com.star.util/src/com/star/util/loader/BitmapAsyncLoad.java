package com.star.util.loader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapAsyncLoad {
	private static Map<Object,String> obj_m_url = new ConcurrentHashMap<Object, String>();
	private static Map<String,OnBitmapLoad> url_m_load = new ConcurrentHashMap<String, OnBitmapLoad>();
	
	public static interface OnBitmapLoad{
		void onBitmapLoad(Object obj,Bitmap bmp);
	}
	
	public static void setCacheDir(String dirPath){
		
	}
	
	
	public static void setObjectUrlBitmapLoadTask(Object obj,String url,OnBitmapLoad onLoad){
		clearLastTask(obj,url);
		setTaskMap(obj,url,onLoad);
		new Thread(new BitmapLoadRunnable(obj,url)).start();
	}
	
	private static void clearLastTask(Object obj,String url){
		synchronized (obj) {
			String tmpUrl = obj_m_url.remove(obj);
			if(tmpUrl != null){
				url_m_load.remove(tmpUrl);
			}	
		}
	}
	
	private static void setTaskMap(Object obj,String url,OnBitmapLoad onLoad){
		synchronized (obj) {
			obj_m_url.put(obj, url);
			url_m_load.put(url, onLoad);
		}
	}
	
	private static void setObjectBitmap(Object obj,String urlStr,Bitmap bmp){
		synchronized (obj) {
			String tmpUrl = obj_m_url.remove(obj);
			if(tmpUrl != null && tmpUrl.equals(urlStr)){
				OnBitmapLoad onLoad = url_m_load.remove(urlStr);
				if(onLoad != null){
					onLoad.onBitmapLoad(obj, bmp);
				}
			}
		}
	}
	
	static class BitmapLoadRunnable implements Runnable{
		private Object obj;
		private String urlStr;	
		
		public BitmapLoadRunnable(Object obj,String url){
			this.obj = obj;
			this.urlStr = url;
		}
		
		@Override
		public void run() {
			try {
				URL url = new URL(urlStr);
				URLConnection connection = url.openConnection();
				connection.setReadTimeout(10000);
				Bitmap bmp = BitmapFactory.decodeStream(connection.getInputStream());
				setObjectBitmap(obj,urlStr,bmp);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
