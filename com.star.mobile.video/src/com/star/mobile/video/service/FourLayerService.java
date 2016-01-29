package com.star.mobile.video.service;

import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.star.mobile.video.AlertManager;
import com.star.mobile.video.R;
import com.star.mobile.video.me.feedback.UserReportActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.FourLayerServiceSharedUtil;

public class FourLayerService extends Service {

	private long poorTime = 0; //距离设定的时间差
	private long oneDayTime;
	private long fiveDays;
	private IBinder binder = new FourLayerService.FourLoayBinder();
	private final String TAG = FourLayerService.class.getName();
	
	@Override
	public void onCreate() {
		super.onCreate();
		Date now = new Date();
		oneDayTime = 24*60*60*1000;//一天时间
		fiveDays = oneDayTime * 3;//三天时间
		if(SharedPreferencesUtil.getFiveDays(this) == 0) { //第一次做四格体验时间
			long time = 0;
			if(FourLayerServiceSharedUtil.getOneRemindTime(this) == 0) {
				time = now.getTime() + oneDayTime;
				FourLayerServiceSharedUtil.setOneRemindTime(this, time);
			} else {
				time = FourLayerServiceSharedUtil.getOneRemindTime(this);
			}
			FourLayerServiceSharedUtil.setOneRemindTime(this, time);
			String setingTime = DateFormat.format(new Date(time), "yyyy-MM-dd ") + getString(R.string.four_layer_time);//第二天下午三点
			Date setingDate = DateFormat.stringToDate(setingTime, "yyyy-MM-dd HH:mm",null);
			poorTime = setingDate.getTime();
			//本地测试 5分钟后提醒
//			poorTime = 1000 * 60 * 5; 
			Log.i(TAG, "One alert sleep:"+poorTime+",hours:"+poorTime/1000/60/60);
		} else {
			getDiffToTommorow();
		}
		startAlertThread(); 
	}
	
	private void getDiffToTommorow(){
		String nextDayTime = DateFormat.format(new Date( SharedPreferencesUtil.getFiveDays(this) - FourLayerServiceSharedUtil.getOneRemindTime(this)), "yyyy-MM-dd ") + getString(R.string.four_layer_time);//第二次做的时间
		Date setingDate = DateFormat.stringToDate(nextDayTime, "yyyy-MM-dd HH:mm",null);
		SharedPreferencesUtil.setSecondaryRemind(this);
		poorTime = setingDate.getTime(); 
		//本地测试第二次十分钟后提醒
//		poorTime = 1000 * 60 * 10;
	}
	
	private void startAlertThread() {
		new Thread(){
			public void run() {
				boolean isRemind = true;
				while(isRemind){
					try {
						Log.i(TAG, "Alert sleep:"+poorTime+",hours:"+poorTime);
						Thread.sleep(poorTime);
						Log.i(TAG, "Do Alert");
						handler.sendEmptyMessage(200);
						SharedPreferencesUtil.setGuideFinish(FourLayerService.this);
						if(!SharedPreferencesUtil.isOneRemind(FourLayerService.this)) {
							SharedPreferencesUtil.setOneRemind(FourLayerService.this);
						}
						SharedPreferencesUtil.setFiveDays(FourLayerService.this, new Date().getTime()+fiveDays);
						if(SharedPreferencesUtil.getSecondaryRemind(FourLayerService.this)) {
							isRemind = false;
						}
						getDiffToTommorow();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			
		}.start();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return binder;
	}
	
	public class FourLoayBinder extends Binder {
		FourLayerService getFourLoayService () {
			return FourLayerService.this;
		}
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 200) {
				alertUserDoFourLayer();
			}
		}
	};
	
	private void alertUserDoFourLayer() {
		if(!SharedPreferencesUtil.getReportDone(FourLayerService.this, SharedPreferencesUtil.getUserName(FourLayerService.this), ApplicationUtil.getAppVerison(FourLayerService.this))) {
			if(ApplicationUtil.isApplicationInBackground(FourLayerService.this)) {
				String title = getString(R.string.four_layer_notify_title);
				String content = getString(R.string.four_layer_notify_content);
				AlertManager.getInstance(FourLayerService.this).notifyAlertProgram(content, title,title, UserReportActivity.class);
			} 
		} 
	}
}
