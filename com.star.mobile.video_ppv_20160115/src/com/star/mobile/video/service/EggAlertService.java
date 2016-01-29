package com.star.mobile.video.service;

import java.util.Date;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.star.cms.model.dto.EggBreakResult;
import com.star.mobile.video.R;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.me.coupon.MyCouponsActivity;
import com.star.mobile.video.model.MyTimer;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.NotificationUtil;
import com.star.util.loader.OnResultListener;

public class EggAlertService extends Service{
	
	private final int ALERT = 100;
	private final int IS_NEW_MSG = 102;
	private MyTimer waitTimer;
	private MyTimer alertTimer;
	private EggService eggService;
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == ALERT) {
				// 11 点 是否还有可以砸的彩蛋5.6 接口
				alertUser();
			} else if(msg.what == IS_NEW_MSG) {
				//监听是否有新消息
				isExistEgg();
			}
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		if(eggService == null) {
			eggService = new EggService(this);
		}
		String setingTime = DateFormat.format(new Date(), "yyyy-MM-dd ") + getString(R.string.egg_alert_time);
		Date setingDate = DateFormat.stringToDate(setingTime, "yyyy-MM-dd HH:mm",null);
		long sleepTime = setingDate.getTime() - System.currentTimeMillis(); 
		Log.v("TAG", "sleep"+sleepTime/(60*1000));
		if(sleepTime > 0) {
			waitAlert(sleepTime);
		}
		newMessageTapped();
	}
	
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		flags = START_STICKY; 
//		return super.onStartCommand(intent, flags, startId);
//	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(waitTimer != null && waitTimer.innerTask != null) {
			waitTimer.innerTask.cancel();
		}
		if(alertTimer != null && alertTimer.innerTask != null) {
			alertTimer.innerTask.cancel();
		}
		Intent serviceIntent = new Intent("EggAlertService");  
		startService(serviceIntent);
	}
	
	
	private  void newMessageTapped() {
		if(waitTimer == null || waitTimer.cancel) {
			waitTimer = new MyTimer();
		}
		waitTimer.innerTask = new TimerTask() {
			
			@Override
			public void run() {
				handler.sendEmptyMessage(IS_NEW_MSG);
//					Log.v("TAG", "Alert...................");
			}
		};
		waitTimer.schedule(waitTimer.innerTask, 0, 1000*60);
	}
	
	private void waitAlert(final long sleepTime) {
		if(alertTimer == null || alertTimer.cancel) {
			alertTimer = new MyTimer();
		}
		alertTimer.innerTask = new TimerTask() {
			
			@Override
			public void run() {
				handler.sendEmptyMessage(ALERT);
			}
		};
		alertTimer.schedule(alertTimer.innerTask, sleepTime);
	}
	
	
	private void isExistEgg() {
//		Log.v("TAG", "Server 1000*60");
		eggService.isExistEgg(new OnResultListener<EggBreakResult>() {
			
			@Override
			public void onSuccess(EggBreakResult result) {
				if(result!=null && result.getBreakResult()==EggBreakResult.BreakReceive_Success) {
					String message = null;
					String title = null;
					String tickerText = null;
					title= getString(R.string.alert_info_title);
					message = getString(R.string.alert_info_);
					Intent intent = new Intent(EggAlertService.this,MyCouponsActivity.class);
//					intent.putExtra("fragment", HomeFragment.class.getSimpleName());
					intent.putExtra("exchange", result.getExchange());
			        intent.addFlags(Intent.FILL_IN_DATA);
			        NotificationUtil.showNotification(message, title, tickerText, intent, EggAlertService.this);
				} 
			}
			
			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void alertUser() {
//		Log.v("TAG", "sleep");
		eggService.isUserBreakEgg(new OnResultListener<Boolean>() {

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result != null && result) {
					String message = getString(R.string.alert_info);
					String title = "";
					Intent intent = new Intent(EggAlertService.this,HomeActivity.class);
			        intent.addFlags(Intent.FILL_IN_DATA);
			        NotificationUtil.showNotification(message, title, message, intent, EggAlertService.this);
				} else {
					Log.v("TAG", "Stop ");
					//直接cancel即可？
//					alertTimer.innerTask.cancel();
					alertTimer.cancel();
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
}
