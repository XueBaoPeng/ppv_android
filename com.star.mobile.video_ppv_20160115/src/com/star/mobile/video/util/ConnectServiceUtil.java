package com.star.mobile.video.util;

import java.io.IOException;
import java.net.Socket;

import android.content.Context;
import android.util.Log;

import com.star.mobile.video.R;

public class ConnectServiceUtil {
	
	public static Context CONTEXT;
	
	private static int TIME = 0;
	
	
	public static void checkConnectStatus() {
//		new Thread(){
//			@Override
//			public void run() {
//					connect();
//			}
//		}.start();
	}

	private static void connect() {
		Socket socket = null;
		String ip = null; int port=80;
		try {
			String[] url = CONTEXT.getString(R.string.server_url).split("/");
			url = url[2].split(":");
			if(url.length==2){
				ip = url[0];
				port = Integer.parseInt(url[1]);
			}else{
				ip = url[0];
			}
		} catch (Exception e) {
			throw new IllegalStateException("server url format error! Check the string.xml.",e);
		}
		
		while (true) {
			try {
				socket = new Socket(ip,port);
				socket.sendUrgentData(0xFF);
				socket.close();
				updateStatus(true);
			} catch (Exception e) {
				if(socket!=null){
					try {
						socket.close();
					} catch (IOException e1) {
					}
				}
				updateStatus(false);
			}
		}
	}
	private static void updateStatus(boolean status) {
		Log.v("Check", "Check Connect Status:" + status +", current status:"+CommonUtil.NETWORK_CONNECT_STATUS);
		try {
			if (CommonUtil.NETWORK_CONNECT_STATUS != status) {
				if(TIME==-1){
					TIME = 1;
				}
				Thread.sleep(1000);
			} else {
				TIME=-1;
				Thread.sleep(10000);
			}
			if (TIME == 2) {
				CommonUtil.NETWORK_CONNECT_STATUS = status;
			} else {
				TIME++;
			}
		} catch (InterruptedException e) {

		}
		// connect();
	}
	
}
