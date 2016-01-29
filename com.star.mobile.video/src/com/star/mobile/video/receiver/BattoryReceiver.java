package com.star.mobile.video.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BattoryReceiver extends BroadcastReceiver{
	static final String REMOVEDACTION = "android.intent.action.PACKAGE_REMOVED";
	static final String USERPRESENT = "android.intent.action.USER_PRESENT";
	@Override
	public void onReceive(Context context, Intent intent) {
//		Intent service = new Intent(context,EggAlertService.class);
//		context.startService(service);
		if(intent.getAction().equals(REMOVEDACTION)) {
//			ToastUtil.centerShowToast(context, "222");
		}else if(intent.getAction().equals(USERPRESENT))
		{
//			 ActivityManager mActivityManager =     
//			    (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);    
//			 List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(30);    
//			 //我要判断的服务名字
//			 final String musicClassName = "com.star.mobile.video.service.EggAlertService";    
//			 boolean b = MusicServiceIsStart(mServiceList, musicClassName);   
//			 if(!b){
//			 Intent service = new Intent(context,EggAlertService.class);
//			 context.startService(service);
//			 }
		}
		
	}
	  private boolean MusicServiceIsStart(List<ActivityManager.RunningServiceInfo> mServiceList,String className){    
          
	        for(int i = 0; i < mServiceList.size(); i ++){    
	            if(className.equals(mServiceList.get(i).service.getClassName())){    
	                return true;    
	            }    
	        }    
	        return false;    
	    }    

}
