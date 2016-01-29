package com.star.mobile.video.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.star.mobile.video.shared.AppInfoSharedUtil;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.DownloadUtil;
import com.star.mobile.video.util.ToastUtil;

public class DownloadService extends Service {

	private int version;
	private String TAG =  DownloadService.class.getName();
	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		version =SharedPreferencesUtil.getNewVersion(getApplicationContext());
		if(version!=0 && DownloadUtil.needDownload(getApplicationContext(), version)) {
			String url = AppInfoSharedUtil.getNewVersionApkUrl(this);
			if(url == null){
				ToastUtil.centerShowToast(getApplicationContext(), "The download url is empty.");
				return;
			}
			
			long id = DownloadUtil.downloadApp(this, url, version);

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}
}
