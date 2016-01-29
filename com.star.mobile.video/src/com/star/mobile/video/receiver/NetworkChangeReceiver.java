package com.star.mobile.video.receiver;

import java.util.Iterator;
import java.util.Map;

import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.tenb.TenbService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {
	
	public static boolean networkConnected = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo  mobNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo  wifiNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
        	networkConnected = false;
        }else {
        	networkConnected = true;
        	networkConnect(context);
        }
	}

	private void networkConnect(final Context context) {
		final Map<String, Integer> map = (Map<String, Integer>) SharedPreferencesUtil.getTenbSharePre(context).getAll();
		if(map.size()>0){
			new Thread(){
				public void run() {
					TenbService tenbService = new TenbService(context);
					try{
						Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry<String, Integer> entry = it.next();
							Log.d("NetworkChangeReceiver", "key===="+entry.getKey()+";value===="+entry.getValue());
							tenbService.doTenbData(entry.getValue(), Long.parseLong(entry.getKey()));
						}
						SharedPreferencesUtil.getTenbSharePre(context).edit().clear().commit();
					}catch (Exception e) {
					}
				};
			}.start();
		}
	}
}
