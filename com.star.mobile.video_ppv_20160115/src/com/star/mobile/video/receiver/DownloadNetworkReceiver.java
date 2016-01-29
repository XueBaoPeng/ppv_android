package com.star.mobile.video.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.star.mobile.video.R;
import com.star.mobile.video.activity.NewVersionAppDetailActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.DownloadUtil;
import com.star.util.Log;
import com.star.util.NetworkUtil;
import com.thin.downloadmanager.ThinDownloadManager;

public class DownloadNetworkReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
		//	    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if(!NetworkUtil.isNetworkAvailable(context) 
				&& (DownloadUtil.downloadStatus == ThinDownloadManager.STATUS_RUNNING 
				|| DownloadUtil.downloadStatus == ThinDownloadManager.STATUS_STARTED) 
				)
		{
			DownloadUtil.downloadStatus = ThinDownloadManager.STATUS_FAILED;
			SharedPreferencesUtil.setNewVersionDownLoadStatus(context, ThinDownloadManager.STATUS_FAILED, DownloadUtil.latestVersion);

			NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(R.drawable.app_icon,"Download Tenbre Failed",System.currentTimeMillis());
			PendingIntent pendingIntent = PendingIntent.getActivity(context,0,new Intent(context,NewVersionAppDetailActivity.class),0);
			notification.setLatestEventInfo(context, "Download Failed", "", pendingIntent);

			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.defaults = Notification.DEFAULT_SOUND;
			manager.notify(0, notification);
		}
		
		Log.d("DownloadNetworkReceiver", "Changed");

	}		
}




