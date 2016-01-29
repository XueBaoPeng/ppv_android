package com.star.mobile.video.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.star.mobile.video.R;
import com.star.mobile.video.activity.NewVersionAppDetailActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;

public class NotificationUtil {

	private static NotificationManager nm;
	
	private static List<Integer> free_coupon_msg_list = new ArrayList<Integer>(); 
	
	private static int notId = 1;
	
	 public static void showNotification(Context context ,Long downId) {
		 	if(nm == null) {
		 		nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		 	}
	        CharSequence from = "StarTimes";
	        CharSequence message = "APP from StarTimes";
	        String fileName = DownloadUtil.getFileName(downId,context);
	        if(fileName == null) {
	        	return;
	        }
	        Uri uri = Uri.fromFile( new File(fileName));
	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        intent.setDataAndType(uri, "application/vnd.android.package-archive" );
	        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
	                intent, 0);
	        String tickerText = context.getString(R.string.app_name, message);
	        Notification notif = new Notification(R.drawable.app_icon, tickerText,
	                System.currentTimeMillis());
	        notif.setLatestEventInfo(context, from, message, contentIntent);
	        notif.vibrate = new long[] { 100, 250, 100, 500};
	        notif.flags |= Notification.FLAG_AUTO_CANCEL;  
	        nm.notify(100, notif);
	        free_coupon_msg_list.add(100);
	    }
	 
	public static void showNotificationFailed(Context context){
		 NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	        Notification notification = new Notification(R.drawable.app_icon,"Download Tenbre Failed",System.currentTimeMillis());
	        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,new Intent(context,NewVersionAppDetailActivity.class),0);
	        notification.setLatestEventInfo(context, "Download Failed", "", pendingIntent);

	        notification.flags = Notification.FLAG_AUTO_CANCEL;
	        notification.defaults = Notification.DEFAULT_SOUND;
	        manager.notify(0, notification);
	}
	
	 public static void showNotification(Context context) {
		 	if(nm == null) {
		 		nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		 	}
	        CharSequence from = "StarTimes Downloaded";
	        CharSequence message = "Tap to install the latest version";
//	        String fileName = DownloadUtil.getFileName(downId,context);
	        int version = SharedPreferencesUtil.getNewVersion(context);
//			File fileDir =  context.getExternalCacheDir();
			File fileDir =  DownloadUtil.getDir();
//	        String appName = context.getResources().getString(R.string.app_name);

	        String path = fileDir.toString() + "/" + context.getString(R.string.app_name)+"_"+version+".apk";
	        File file = new File(path);
	        Uri uri = Uri.fromFile( file);
	        
	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
	        intent.setDataAndType(uri, "application/vnd.android.package-archive" );
	        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
	                intent, 0);
	        String tickerText = context.getString(R.string.app_name, message);
	        Notification notif = new Notification(R.drawable.app_icon, tickerText,
	                System.currentTimeMillis());
	        notif.setLatestEventInfo(context, from, message, contentIntent);
	        notif.vibrate = new long[] { 100, 250, 100, 500};
	        notif.flags |= Notification.FLAG_AUTO_CANCEL;  
	        nm.notify(0, notif);
		 
	        free_coupon_msg_list.add(100);
	    }
	
	
	
	 public static void showNotification(String message,String title,String tickerText,Intent intent,Context context){
		 if(nm == null) {
		 		nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		 	}  
		 	intent.putExtra("notId", notId);
		 	PendingIntent pIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);  
//	    	PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);  
	    	Notification notif = new Notification();  
			notif.icon = R.drawable.app_icon;  
			notif.tickerText = tickerText;  
//			notif.defaults |= Notification.DEFAULT_SOUND;  
//			notif.defaults |= Notification.DEFAULT_VIBRATE;  
//			notif.defaults |= Notification.DEFAULT_LIGHTS;  
			notif.flags |= Notification.FLAG_AUTO_CANCEL;  
			notif.setLatestEventInfo(context,title, message, pIntent); 
			nm.notify(notId, notif);
			free_coupon_msg_list.add(notId);
			notId++;
	    }
	public static void clertNotification() {
		for(Integer id :free_coupon_msg_list) {
			if(nm != null) {
				nm.cancel(id);
			}
		}
		free_coupon_msg_list.clear();
		
	}
	public static void clertNotification(int notId) {
		if(nm != null) {
			nm.cancel(notId);
		}
		free_coupon_msg_list.remove(notId);
	}
}
