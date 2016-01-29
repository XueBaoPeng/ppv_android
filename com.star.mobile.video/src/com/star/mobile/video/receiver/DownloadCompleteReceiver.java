package com.star.mobile.video.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.AlertInstallActivity;
import com.star.mobile.video.activity.NewVersionAppDetailActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.NotificationUtil;
import com.star.mobile.video.util.ToastUtil;

public class DownloadCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			StarApplication.nowDownLoadNewVersion = false;
			long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			Log.v("DownloadCompleteReceiver", ""+downId);
			NotificationUtil.showNotification(context, downId);
			if(SharedPreferencesUtil.getNewVersion(context)!=0){
				SharedPreferencesUtil.setNewVersionDownLoadStatus(context, DownloadManager.STATUS_SUCCESSFUL, SharedPreferencesUtil.getNewVersion(context));
				if(goAlertInstallDailog(context)){
					Intent i = new Intent(context, AlertInstallActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}
			}
		} else {
			ToastUtil.centerShowToast(context, "Download error!");
		}
		
	}

	 public boolean goAlertInstallDailog(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (context.getPackageName().equals(topActivity.getPackageName()) && !NewVersionAppDetailActivity.class.getName().equals(topActivity.getClassName())) {
                return true;
            }
        }
        return false;
	 }
}
