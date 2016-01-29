package com.star.mobile.video.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.star.mobile.video.ServiceHandler;

public class BaseService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		ServiceHandler.getInstance().addService(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		ServiceHandler.getInstance().deleteService(this);
	}
}
