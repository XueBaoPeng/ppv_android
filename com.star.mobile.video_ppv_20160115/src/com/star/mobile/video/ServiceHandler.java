package com.star.mobile.video;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;

public class ServiceHandler {

	private ServiceHandler(){};
	
	private static ServiceHandler mHandler;
	private Map<String, Service> services = new HashMap<String, Service>();
	
	public static ServiceHandler getInstance(){
		if(mHandler == null)
			mHandler = new ServiceHandler();
		return mHandler;
	}
	
	public void addService(Service service){
		services.put(service.getClass().getName(), service);
	}
	
	public void stopService(Service service){
		service.stopSelf();
		deleteService(service);
	}
	
	public void deleteService(Service service){
		services.remove(services.getClass().getName());
	}
	
	public void clearAllService(){
		for(Service s : services.values()){
			s.stopSelf();
		}
		services.clear();
	}
}
