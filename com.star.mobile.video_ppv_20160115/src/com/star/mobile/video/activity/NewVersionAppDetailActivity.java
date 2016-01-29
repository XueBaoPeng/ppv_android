package com.star.mobile.video.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.star.cms.model.APPInfo;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.AppDetailAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.service.ApplicationService;
import com.star.mobile.video.shared.AppInfoSharedUtil;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.DownloadUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.AppDetailHeadView;
import com.star.util.NetworkUtil;
import com.star.util.loader.OnResultListener;
import com.thin.downloadmanager.ThinDownloadManager;

public class NewVersionAppDetailActivity extends BaseActivity implements OnClickListener{

	private ListView lvAppInfo;
	private ApplicationService applicationService;
	private APPInfo appInfo;
	private NetworkInfo networkInfo;
	private AppDetailHeadView headView;
	private DownloadFileObserver fileObserver;
	private DownloadChangeObserver observer;
	private String appUrl;
	private DownloadCompleteReceiver receiver;
	public static int downloadPercentage;
	private static String TAG =  NewVersionAppDetailActivity.class.getName();



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_detail);
		applicationService = new ApplicationService(this);

		if(DownloadUtil.checkDownloadManager(getApplicationContext()) ==false) {
			finish();
		};
		if(DownloadUtil.isUseDownloadManager)
		{
			observer = new DownloadChangeObserver(new Handler());
			receiver = new DownloadCompleteReceiver();
			registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
			appUrl = AppInfoSharedUtil.getNewVersionApkUrl(this);
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			networkInfo = connectivityManager.getActiveNetworkInfo();
		}

		else {
			DownloadUtil.downloadStatus = (int)SharedPreferencesUtil.getNewVersionDownLoadStatus(getApplicationContext(), SharedPreferencesUtil.getNewVersion(getApplicationContext()));
			DownloadUtil.newVersionActivityInstance = this;
			File fileDir =  DownloadUtil.getDir();
			if(fileDir != null) 
			{
				fileObserver = new DownloadFileObserver(fileDir.toString());
			}
		}

		initView();
		initData();
	}


	private void initView() {

		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.new_version_title));
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		lvAppInfo = (ListView) findViewById(R.id.lv_appinfo);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(DownloadUtil.isUseDownloadManager) {
			getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, observer);
			checkDownStatus();
		}
		else {
			if(fileObserver!=null) fileObserver.startWatching();
			checkDownStatus();
		}


	}



	public void checkDownStatus() {
		if(DownloadUtil.isUseDownloadManager)
		{
			if(appInfo == null)
				return;
			if(!DownloadUtil.needDownload(getApplicationContext(), appInfo.getVersion()))
				setLoadingStatus(DownloadManager.STATUS_SUCCESSFUL);
			else
				setLoadingStatus(DownloadUtil.getDownloadStatus(NewVersionAppDetailActivity.this, appInfo.getVersion(), DownloadManager.COLUMN_STATUS));

		}else {
			setLoadingStatus(DownloadUtil.downloadStatus);
		}
	}

	private void initData() {

//		new LoadingDataTask() {
//
//			@Override
//			public void onPreExecute() {
//				CommonUtil.showProgressDialog(NewVersionAppDetailActivity.this, null, getString(R.string.loading));
//			}
//
//			@Override
//			public void onPostExecute() {
//				CommonUtil.closeProgressDialog();
//				if(appInfo != null) {
//					headView = new AppDetailHeadView(NewVersionAppDetailActivity.this,appInfo.getDescription(),appInfo.getApkSize());
//					checkDownStatus();
//					lvAppInfo.addHeaderView(headView);
//					List<String> datas = new ArrayList<String>();
//					if(appInfo.getUpdateInfo()!=null){
//						String [] data = appInfo.getUpdateInfo().split("\n");
//
//						for(int i = 0;i < data.length;i++) {
//							if(!"".equals(data[i])) {
//								datas.add(data[i]);
//							}
//						}
//					}else{
//						ToastUtil.centerShowToast(NewVersionAppDetailActivity.this, getString(R.string.no_version_info));
//					}
//					AppDetailAdapter adapter = new AppDetailAdapter(datas, NewVersionAppDetailActivity.this);
//					lvAppInfo.setAdapter(adapter);
//				} else {
//					ToastUtil.centerShowToast(NewVersionAppDetailActivity.this, getString(R.string.error_network));
//				}
//
//			}
//
//			@Override
//			public void doInBackground() {
//				appInfo = applicationService.getDetailAppInfo(SharedPreferencesUtil.getNewVersion(NewVersionAppDetailActivity.this));
//			}
//		}.execute();
		applicationService.getDetailAppInfo(SharedPreferencesUtil.getNewVersion(NewVersionAppDetailActivity.this), new OnResultListener<APPInfo>() {
			
			@Override
			public void onSuccess(APPInfo value) {
				appInfo = value;
				CommonUtil.closeProgressDialog();
				if(appInfo != null) {
					headView = new AppDetailHeadView(NewVersionAppDetailActivity.this,appInfo.getDescription(),appInfo.getApkSize());
					checkDownStatus();
					lvAppInfo.addHeaderView(headView);
					List<String> datas = new ArrayList<String>();
					if(appInfo.getUpdateInfo()!=null){
						String [] data = appInfo.getUpdateInfo().split("\n");

						for(int i = 0;i < data.length;i++) {
							if(!"".equals(data[i])) {
								datas.add(data[i]);
							}
						}
					}else{
						ToastUtil.centerShowToast(NewVersionAppDetailActivity.this, getString(R.string.no_version_info));
					}
					AppDetailAdapter adapter = new AppDetailAdapter(datas, NewVersionAppDetailActivity.this);
					lvAppInfo.setAdapter(adapter);
				} else {
					ToastUtil.centerShowToast(NewVersionAppDetailActivity.this, getString(R.string.error_network));
				}
			}
			
			@Override
			public boolean onIntercept() {
				CommonUtil.showProgressDialog(NewVersionAppDetailActivity.this, null, getString(R.string.loading));
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				CommonUtil.closeProgressDialog();
				ToastUtil.centerShowToast(NewVersionAppDetailActivity.this, getString(R.string.error_network));
			}
		});
	}


	private int fileSize = 0;
	public void initProgress() {
		if(DownloadUtil.isUseDownloadManager) {
			Log.i(TAG, "fileSize"+fileSize);
			if(fileSize <= 0) {
				fileSize = DownloadUtil.getDownloadStatus(NewVersionAppDetailActivity.this, appInfo.getVersion(), DownloadManager.COLUMN_TOTAL_SIZE_BYTES);//获取下载文件大小
				setProgressMax(fileSize);
			}
		}
		else setProgressMax(100);

	}

	private void updateProgress() {

		if(DownloadUtil.isUseDownloadManager) {
			if(appInfo == null)
				return;
			try{
				initProgress();
				int downloadFileSize = DownloadUtil.getDownloadStatus(NewVersionAppDetailActivity.this, appInfo.getVersion(), DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);//获取以下下载文件大小
				Log.i("TAG", "downloadFileSize"+downloadFileSize);
				setDownload(downloadFileSize);
			}catch(Exception e){
				Log.e("NewVersionActivity", "setProgress error!", e);
			}
		} 
		else {
			try{
				initProgress();
				setDownload(downloadPercentage);
			}catch(Exception e){
				Log.e("NewVersionAppDetailActivity", "setProgress error!", e);
			}
		}
	}

	private void setDownload(int pressed) {
		if(headView == null ) return;
		headView.setDownloadProgress(pressed);
	}

	private void setProgressMax(int max) {
		if(headView == null ) return;

		headView.setProgressMax(max);
	} 

	public void setLoadingStatus(int status) {
		if(headView == null)
			return;
		Log.d(TAG, "status: " + status);

		if(DownloadUtil.isUseDownloadManager) {
			switch (status) {
			case DownloadManager.STATUS_PAUSED:
			case DownloadManager.STATUS_RUNNING:
				headView.setNewVersionBtnText(getString(R.string.downloading));
				headView.setNewVersionBtnColor(getResources().getColor(R.color.orange));
				headView.setNewVersionBtnBg(null);
				headView.setNewVersionBtnOnClick(null);
				headView.setProgressVisibility(true);
				break;
			case DownloadManager.STATUS_SUCCESSFUL:
				if(DownloadUtil.apkIsExist(this, appInfo.getVersion())){
					headView.setNewVersionBtnText(getString(R.string.install));
					headView.setNewVersionBtnBg(R.drawable.orange_button_bg);
					headView.setNewVersionBtnColor(getResources().getColor(R.color.white));
					headView.setNewVersionBtnOnClick(NewVersionAppDetailActivity.this);
					headView.setProgressVisibility(false);
				}else{
					setLoadingStatus(DownloadManager.STATUS_FAILED);
				}
				break;
			case -1:
			case DownloadManager.STATUS_FAILED:
				headView.setNewVersionBtnText(getString(R.string.update));
				headView.setNewVersionBtnBg(R.drawable.orange_button_bg);
				headView.setNewVersionBtnColor(getResources().getColor(R.color.white));
				headView.setNewVersionBtnOnClick(NewVersionAppDetailActivity.this);
				headView.setProgressVisibility(false);
				break;
			}

		}
		else {
			switch (status) {
			case ThinDownloadManager.STATUS_RUNNING:
			case ThinDownloadManager.STATUS_CONNECTING:
			case ThinDownloadManager.STATUS_STARTED:
				headView.setNewVersionBtnText(getString(R.string.downloading));
				headView.setNewVersionBtnColor(getResources().getColor(R.color.orange));
				headView.setNewVersionBtnBg(null);
				headView.setNewVersionBtnOnClick(null);
				headView.setProgressVisibility(true);
				updateProgress();
				break;
			case ThinDownloadManager.STATUS_SUCCESSFUL:
				if(DownloadUtil.apkIsExist(this)){
					headView.setNewVersionBtnText(getString(R.string.install));
					headView.setNewVersionBtnBg(R.drawable.orange_button_bg);
					headView.setNewVersionBtnColor(getResources().getColor(R.color.white));
					headView.setNewVersionBtnOnClick(NewVersionAppDetailActivity.this);
					headView.setProgressVisibility(false);
				}
				break;
			default:
				headView.setNewVersionBtnText(getString(R.string.update));
				headView.setNewVersionBtnBg(R.drawable.orange_button_bg);
				headView.setNewVersionBtnColor(getResources().getColor(R.color.white));
				headView.setNewVersionBtnOnClick(NewVersionAppDetailActivity.this);
				headView.setProgressVisibility(false);
				break;
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			super.onBackPressed();
			break;
		case R.id.btn_new_version:

			if(DownloadUtil.isUseDownloadManager) {
				if(headView.getNewVersionBtnText().equals(getString(R.string.update)))
					upgrade();
				else if(headView.getNewVersionBtnText().equals(getString(R.string.install)))
					DownloadUtil.setUp(this, appInfo.getVersion());
			}
			else {
				if(DownloadUtil.getDir() == null) {
					ToastUtil.centerShowToast(getApplicationContext(), "Please download the latest version from http://tenbre.me/tenbre.apk"); 
					return;
				}
				if(headView.getNewVersionBtnText().equals(getString(R.string.update))) {
					if(NetworkUtil.isNetworkAvailable(getApplicationContext()))
						upgrade();
					else ToastUtil.centerShowToast(getApplicationContext(), "No Network Connection.");
				}
				else if(headView.getNewVersionBtnText().equals(getString(R.string.install)))
					DownloadUtil.setUp(this, appInfo.getVersion());
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==110 && resultCode==120 && data.getBooleanExtra("ok", false)){
			if(DownloadUtil.isUseDownloadManager) {
				setLoadingStatus(DownloadManager.STATUS_RUNNING);
			}
			else {
				if(DownloadUtil.downloadStatus != ThinDownloadManager.STATUS_FAILED)
					setLoadingStatus(ThinDownloadManager.STATUS_RUNNING);
			}
		}
	}

	private void upgrade() {
		if(appInfo.getVersion() > ApplicationUtil.getAppVerison(NewVersionAppDetailActivity.this)) {
			SharedPreferencesUtil.setNewVersion(this, appInfo.getVersion());
			SharedPreferencesUtil.setNewVersionSize(this,appInfo.getApkSize());			
			Intent i = new Intent(this, AlertInstallActivity.class);
			i.putExtra("what", "update");
			i.putExtra("updateInfo", appInfo.getUpdateInfo());
			startActivityForResult(i, 110);

		}else{
			ToastUtil.centerShowToast(NewVersionAppDetailActivity.this, "This is the lastest version.");
		}
	}

	@Override
	protected void onDestroy() {
		if(DownloadUtil.isUseDownloadManager) 
		{
			unregisterReceiver(receiver);
		}
		else {
			fileObserver.stopWatching();
		}
		super.onDestroy();
	}



	class DownloadFileObserver extends FileObserver {

		public DownloadFileObserver(String path) {
			super(path);
		}

		@Override
		public void onEvent(int event, String path) {
			switch(event) {
			case FileObserver.MODIFY:
				updateProgress();
				break;
			case FileObserver.CLOSE_WRITE:
				Thread thread = new Thread(){
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								checkDownStatus();
							}
						});
					}
				};
				thread.start();
				break;
			}
		}
	}

	//接收下载完成后的intent  
	private class DownloadCompleteReceiver extends BroadcastReceiver {  

		@Override  
		public void onReceive(Context context, Intent intent) { 
			if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){  
				setLoadingStatus(DownloadManager.STATUS_SUCCESSFUL);
			} else {
				ToastUtil.centerShowToast(context, getString(R.string.download_error));
				setLoadingStatus(DownloadManager.STATUS_FAILED);
			} 
		}  
	} 

	class DownloadChangeObserver extends ContentObserver{


		public DownloadChangeObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			updateProgress();
		}
	}
}
