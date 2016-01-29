package com.star.mobile.video.service;

import java.util.Date;
import java.util.Random;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.star.mobile.video.base.BaseService;
import com.star.mobile.video.model.MyTimer;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.DateFormat;

public class SmartCardService extends BaseService {
	private MyTimer smartTimer;
	private UserService userService;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			userService.saveExpectedStopSmartcard(getApplicationContext());
		};
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.d("SmartCardService", "service onCreate.");
		userService = new UserService();
		userService.saveExpectedStopSmartcard(getApplicationContext());
		smartTimer = new MyTimer();
		smartTimer.innerTask = new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
		};
		smartTimer.schedule(smartTimer.innerTask, DateFormat.getDateOfAfterDays(new Date(), 1, 0, 30+new Random().nextInt(30), DateFormat.getTimeZone(SharedPreferencesUtil.getAreaCode(getApplicationContext()))), 24*60*60*1000);
//		smartTimer.schedule(smartTimer.innerTask, DateFormat.getDateOfAfterDays(new Date(), 0, 9, 37, DateFormat.getTimeZone(SharedPreferencesUtil.getAreaCode(getApplicationContext()))), 30*1000);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		Log.d("SmartCardService", "service onDestroy.");
		smartTimer.cancel();
		super.onDestroy();
	}
}
