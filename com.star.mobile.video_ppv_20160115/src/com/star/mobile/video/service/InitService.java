package com.star.mobile.video.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.star.mobile.video.base.BaseService;
import com.star.mobile.video.dao.db.DBHelper;

public class InitService extends BaseService {

	public static String initAction_success = "com.star.mobile.video.initData.Success";
	public static String initAction_failure = "com.star.mobile.video.initData.Failure";
	private DBHelper dbHelper;
	private PackageService packageService;
	private ChannelService channelService;
	private CategoryService categoryService;
	private SyncService syncService;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		dbHelper = DBHelper.getInstence(this);
		categoryService = new CategoryService(this);
		packageService = new PackageService(this);
		channelService = new ChannelService(this);
		syncService = SyncService.getInstance(InitService.this);
		new Thread(initDB).start();
	}
	
	private Runnable initDB = new Runnable() {
		@Override
		public void run() {
			try{
				synchronized (InitService.this) {
					syncService.setLoading(true);
					dbHelper.beginTransaction();
					long start = System.currentTimeMillis();
					boolean categoryReady = categoryService.initCategorys();
					boolean packageReady = packageService.initPackages();
					boolean channelReady = channelService.initChannels();
					long end = System.currentTimeMillis();
					if(/*categoryReady&&packageReady&&*/channelReady){
						syncService.setDBReady(true);
						sendBroadcast(new Intent(initAction_success));
						Log.d("SyncService", "init category,package,channel data, spend "+(end-start)/1000+"seconds.");
					}else{
						sendBroadcast(new Intent(initAction_failure));
						Log.d("SyncService", "init category,package,channel data, fail.");
					}
					dbHelper.commit();
					stopSelf();
					syncService.setLoading(false);
				}
			}catch (Exception e) {
				Log.e("SyncService", "sync init error!", e);
			}
		}
	};
}
