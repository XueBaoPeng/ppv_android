package com.star.mobile.video.util;

import java.io.File;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.RemoteViews;

import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.NewVersionAppDetailActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.util.NetworkUtil;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

public class DownloadUtil {

	private static DownloadManager downloadManager ;
	private static ThinDownloadManager thinDownloadManager;
	private static long AppDownLoadID = -1;
	private static String TAG =  DownloadUtil.class.getName();
	public static int downloadStatus;
	public static int downloadSize;
	public static int processSize;
	public static int latestVersion;
	public static boolean isUseDownloadManager = true;
	private static boolean isUseSD = true;
	public static NewVersionAppDetailActivity newVersionActivityInstance;
	private static String msg ="Please download the latest version from http://startimes.me";


	public static boolean checkDownloadManager(Context context)
	{
		Object dm = getDownloadManager(context);
		if(dm==null) return false;
		if(isUseDownloadManager) {
			downloadManager = (DownloadManager)dm;
			Log.d(TAG, "Use Native Download Manager");
			
		}
		else {
			thinDownloadManager = (ThinDownloadManager)dm;
			Log.d(TAG, "Use ThinDownloadManager");
		}
		return true;
	}

	public static long downloadApp(Context context,String apkUrl, int version) {
		//		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		//			ToastUtil.centerShowToast(activity, "Sorry SD card does not exist!");
		//			return;
		//		}

		Log.d(TAG, apkUrl);

		if(checkDownloadManager(context) == false) return -1;
		if(isUseDownloadManager) {

			try{
				DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
				request.setDestinationInExternalPublicDir("Download", context.getString(R.string.app_name)+"_"+version+".apk");
				request.setTitle("APP from StarTimes");
				//				request.setTitle(appName+" from StarTimes.apk");
				request.setShowRunningNotification(true); 
				request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);
				request.setMimeType("application/vnd.android.package-archive");
				AppDownLoadID = downloadManager.enqueue(request);
				SharedPreferencesUtil.setNewVersionDownLoadId(context, AppDownLoadID, version);
				StarApplication.nowDownLoadNewVersion = true;
			}catch (Exception e) {
				Log.e("DownloadUtil", "download error!", e);
			}
			return AppDownLoadID;
		}
		else {
			downloadAppST(context, apkUrl, version);
		}

		return -1;
	}

	public static void downloadAppST(final Context context,final String apkUrl, final int version) {

		latestVersion = version;
		if(thinDownloadManager == null) thinDownloadManager = new ThinDownloadManager();
		thinDownloadManager.cancelAll();

		if(checkStorage() == false) 
		{
			ToastUtil.centerShowToast(context, "Not enough storage space!");
		}

		if(!NetworkUtil.isNetworkAvailable(context)) {
		}

		//		File fileDir =  context.getExternalCacheDir();
		File fileDir =  getDir();
		if(fileDir == null) 
		{
			ToastUtil.centerShowToast(context, msg);
			return;
		}

		//		File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		//Log.d(TAG, Environment.getDownloadCacheDirectory().toString());
		Uri downloadUri = Uri.parse(apkUrl);
		//		String appName = context.getResources().getString(R.string.app_name);

		Uri destinationUri = Uri.parse(fileDir.toString() + "/" + context.getString(R.string.app_name)+"_"+version+".apk");

		StarDownloadStatusListener downloadStatusListener = new StarDownloadStatusListener();
		RetryPolicy retryPolicy = new DefaultRetryPolicy(15000, 5, 1f);
		final DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
		.setDestinationURI(destinationUri)
		.setPriority(DownloadRequest.Priority.HIGH)
		.setRetryPolicy(retryPolicy)
		.setDownloadListener(downloadStatusListener);


		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					AppDownLoadID = thinDownloadManager.add(downloadRequest);
					SharedPreferencesUtil.setNewVersionDownLoadId(context, AppDownLoadID, version);
					SharedPreferencesUtil.setNewVersionDownLoadStatus(context, ThinDownloadManager.STATUS_STARTED, version);
					DownloadUtil.downloadStatus = ThinDownloadManager.STATUS_STARTED;
					StarApplication.nowDownLoadNewVersion = true;
				}catch (Exception e) {
					Log.e(TAG, "download error!", e);
				}
			}
		}).start();
	}




	public static File getDir()
	{
		checkStorage();
		if(isUseSD) return StarApplication.context.getExternalCacheDir();
		else return //StarApplication.context.getCacheDir();
				//				Environment.getDownloadCacheDirectory();
				null;
	}
	private static boolean checkStorage()
	{
		String path = Environment.getExternalStorageDirectory().getPath();
		if(path == null) {
			path = Environment.getRootDirectory().getPath();
			isUseSD = false;
			return compareStorage(path);
		}

		if(compareStorage(path)) return true;
		path = Environment.getRootDirectory().getPath();
		isUseSD = false;
		return compareStorage(path);


	}

	private static boolean compareStorage(String path)
	{
		StatFs stat = new StatFs(path);
		long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
		long megAvailable = bytesAvailable / 1048576;
		Log.d(TAG, path + " Avaible Megs :"+megAvailable + " App size: " +  SharedPreferencesUtil.getNewVersionSize(StarApplication.context));
		if(megAvailable > 2 * (long)SharedPreferencesUtil.getNewVersionSize(StarApplication.context)) return true;
		else return false;
	}



	public static long getNewAppDownloadId(Context context, int version){
		if(AppDownLoadID ==-1)
			AppDownLoadID = SharedPreferencesUtil.getNewVersionDownLoadId(context, version);
		return AppDownLoadID;
	}



	public static void removeDownload(long downloadId) {
		downloadManager.remove(downloadId);
	}


	public static void removeDownloadAll() {
		thinDownloadManager.cancelAll();
	}


	public static int getDownloadStatus(Context context, int version, String columnName) {
		int status = -1;
		if(getNewAppDownloadId(context, version) == -1)
			return status;
		DownloadManager.Query query = new DownloadManager.Query().setFilterById(AppDownLoadID);
		Cursor c = null;
		try {
			c = downloadManager.query(query);
			if (c != null && c.moveToFirst()) {
				status = c.getInt(c.getColumnIndex(columnName));
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return status;
	}



	private static Object getDownloadManager(Context context){

		int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

		if(state==PackageManager.COMPONENT_ENABLED_STATE_DISABLED||
				state==PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER){
			Log.d(TAG, "Use ThinDownloadManager");
			isUseDownloadManager = false;
			if(thinDownloadManager == null){
				thinDownloadManager = new ThinDownloadManager();
				if(thinDownloadManager != null) return thinDownloadManager;
				else {
					ToastUtil.centerShowToast(context, msg);
					return null;
				}
			}
			else return thinDownloadManager;
		}
		else {
			isUseDownloadManager = true;
			downloadManager =  (DownloadManager)context.getSystemService(context.DOWNLOAD_SERVICE);
			if(downloadManager == null) {
				Log.d(TAG, "Use ThinDownloadManager");
				isUseDownloadManager = false;
				if(thinDownloadManager == null){
					thinDownloadManager = new ThinDownloadManager();
					if(thinDownloadManager != null) return thinDownloadManager;
					else {
						ToastUtil.centerShowToast(context, msg);
						return null;
					}

				}		
			}
			else return downloadManager;
		}
		return null;
	}



	public static String getFileName(Long downloadId,Context context) {
		Query query = new Query();
		query.setFilterById(downloadId); 
		if(downloadManager != null) {
			Cursor c = downloadManager.query(query);
			if(c!=null && c.moveToFirst()) {
				int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS); 
				if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {  
					String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
					c.close();
					if(uriString!=null)
						return uriString.substring(7);  
				}  
			}
		}
		return null;
	}



	//only for downloadManager
	public static boolean apkIsExist(Context context, int version){
		long downloadId = getNewAppDownloadId(context, version);
		String path = getFileName(downloadId, context);
		if(path == null)
			return false;
		File file = new File(path);
		int fileSize = 0;

		if(file!=null && file.exists()) {
			fileSize = Integer.parseInt(String.valueOf(file.length()/1024));
			if(Math.abs(fileSize - SharedPreferencesUtil.getNewVersionSize(context) * 1024) < 512)
				return true;
			else {
				SharedPreferencesUtil.setNewVersionDownLoadStatus(context, DownloadManager.STATUS_FAILED, version);
				if(file.delete()) Log.d(TAG, "Deleted corrupted downloaded file.");
				else  Log.d(TAG, "Failed deleting corrupted downloaded file.");
				return false;
			}
		}
		else {
			SharedPreferencesUtil.setNewVersionDownLoadStatus(context, DownloadManager.STATUS_FAILED, version);
		}

		return false;
	}

	//only for thinDownloadManager
	public static boolean apkIsExist(Context context){
		int version = SharedPreferencesUtil.getNewVersion(context);
		//		File fileDir =  context.getExternalCacheDir();
		File fileDir =  getDir();

		if(fileDir == null) return false;
		//		String appName = context.getResources().getString(R.string.app_name);

		String path = fileDir.toString() + "/" + context.getString(R.string.app_name)+"_"+version+".apk";

		File file = new File(path);
		if(file.exists()) Log.d(TAG, "Apk Exists");

		if( SharedPreferencesUtil.getNewVersionDownLoadStatus(context, version) ==  ThinDownloadManager.STATUS_SUCCESSFUL) Log.d(TAG,"STATUS_SUCCESSFUL");

		if(file!=null && file.exists() && SharedPreferencesUtil.getNewVersionDownLoadStatus(context, version) ==  ThinDownloadManager.STATUS_SUCCESSFUL)
			return true;
		else
			return false;

	}

	public static void setUp(Context context, int version) {
		//		File fileDir =  context.getExternalCacheDir();


		if(isUseDownloadManager) 
		{
			String path = getFileName(getNewAppDownloadId(context, version), context);
			if(path == null)
				return;
			File file = new File(path);
			if(file!=null && file.exists()){
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				context.startActivity(intent);
				NotificationUtil.clertNotification();
			}
		}


		else{
			File fileDir =  getDir();
			if(fileDir == null) 
			{
				ToastUtil.centerShowToast(context, msg);
				return;
			}
			//			String appName = context.getResources().getString(R.string.app_name);

			String path = fileDir.toString() + "/" + context.getString(R.string.app_name)+"_"+version+".apk";

			Log.d(TAG, path);
			File file = new File(path);
			if(file!=null && file.exists()){
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				context.startActivity(intent);
				NotificationUtil.clertNotification();
			}
		}
	}

	public static boolean needDownload(Context context, int version){

		if(checkDownloadManager(context) == false) return false;
		long status = SharedPreferencesUtil.getNewVersionDownLoadStatus(context, version);
		if(!isUseDownloadManager && apkIsExist(context) && status==ThinDownloadManager.STATUS_SUCCESSFUL ){
			return false;
		}
		if( isUseDownloadManager && apkIsExist(context, version) && status==DownloadManager.STATUS_SUCCESSFUL ){
			return false;
		}
		if(!NetworkUtil.isNetworkAvailable(context)) return false;
		return true;
	}
}

class StarDownloadStatusListener implements DownloadStatusListener{
	private static String TAG =  DownloadUtil.class.getName();
	@Override
	public void onDownloadComplete(int id) {
		Context context = StarApplication.context;

		DownloadUtil.downloadStatus = ThinDownloadManager.STATUS_SUCCESSFUL;
		SharedPreferencesUtil.setNewVersionDownLoadStatus(context, ThinDownloadManager.STATUS_SUCCESSFUL, DownloadUtil.latestVersion);
		NotificationUtil.showNotification(context);
		Log.d(TAG, "Download Completed");
	}

	@Override
	public void onDownloadFailed(int id, int errorCode, String errorMessage) {

		Context context = StarApplication.context;
		NotificationUtil.showNotificationFailed(context);

		DownloadUtil.downloadStatus =  ThinDownloadManager.STATUS_FAILED;
		SharedPreferencesUtil.setNewVersionDownLoadStatus(context, ThinDownloadManager.STATUS_FAILED, DownloadUtil.latestVersion);

		DownloadUtil.removeDownloadAll();
		StarApplication.nowDownLoadNewVersion = false;
		NewVersionAppDetailActivity.downloadPercentage = 0;
		if(DownloadUtil.newVersionActivityInstance != null) {
			Thread thread = new Thread(){
				@Override
				public void run() {
					DownloadUtil.newVersionActivityInstance.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							DownloadUtil.newVersionActivityInstance.checkDownStatus();
						}
					});

				}

			};
			thread.start();
		}
		ToastUtil.centerShowToast(context, "Cannot connect to server. Please check your network!");
		Log.e(TAG, "Download Failed. ErrorCode "+errorCode+", "+errorMessage);
	}


	@Override
	public void onProgress(int id, long totalBytes, long downloadedBytes, int progress) {

		if(!StarApplication.nowDownLoadNewVersion) return;
		Context context = StarApplication.context;
		if(! NetworkUtil.isNetworkAvailable(context)) 
			NotificationUtil.showNotificationFailed(context);

		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.app_icon,"Downloading Tenbre",System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity(context,0,new Intent(context,NewVersionAppDetailActivity.class),0);
		//        notification.setLatestEventInfo(context, "Downloading...", progress+"%    " + getBytesDownloaded(progress,totalBytes), pendingIntent);
		notification.contentView = new RemoteViews(context.getPackageName(),R.layout.notification);
		notification.contentView.setProgressBar(R.id.notification_progressBar, 100, progress, false);
		notification.contentView.setTextViewText(R.id.notification_textView, "" + progress+"%");
		notification.contentIntent = pendingIntent;

		notification.flags = Notification.FLAG_AUTO_CANCEL;
		manager.notify(0, notification);

		NewVersionAppDetailActivity.downloadPercentage = progress;

		if(DownloadUtil.downloadStatus == ThinDownloadManager.STATUS_STARTED) {
			SharedPreferencesUtil.setNewVersionDownLoadStatus(context, ThinDownloadManager.STATUS_RUNNING, DownloadUtil.latestVersion);
			DownloadUtil.downloadStatus = ThinDownloadManager.STATUS_RUNNING;
			if(DownloadUtil.newVersionActivityInstance != null) {
				Thread thread = new Thread(){
					@Override
					public void run() {
						DownloadUtil.newVersionActivityInstance.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								DownloadUtil.newVersionActivityInstance.checkDownStatus();
							}
						});

					}

				};
				thread.start();
			}
		}
	}

	private String getBytesDownloaded(int progress, long totalBytes) {
		long bytesCompleted = (progress * totalBytes)/100;
		if (totalBytes >= 1024*1024) {
			return (""+(String.format("%.1f", (float)bytesCompleted/(1024*1024)))+ "/"+ ( String.format("%.1f", (float)totalBytes/(1024*1024))) + "MB");
		} if (totalBytes >= 1024) {
			return (""+(String.format("%.1f", (float)bytesCompleted/1024))+ "/"+ ( String.format("%.1f", (float)totalBytes/1024)) + "Kb");

		} else {
			return ( ""+bytesCompleted+"/"+totalBytes );
		}
	}
}
