package com.star.util.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.star.util.app.Application;
import com.star.util.app.GA;

/**
 * Test speed of our network connection
 */
public class NETSpeedTest{
	private Context context;
	private String downloadFileUrl/* = "http://cdnt.tenbre.me/tenbre.apk"*/;
	/**
	 * Test speed of our network connection
	 */
	public NETSpeedTest(Context context,String downloadFileUrl) {
		this.context = context;
		this.downloadFileUrl = downloadFileUrl;
	}
	public NETSpeedTest(Context context) {
		this.context = context;
		this.downloadFileUrl = "";
	}

	public void asychStart(){
		new Thread(mWorker).start();
	}

	private final Runnable mWorker = new Runnable() {

		@Override
		public void run() {
			if(context==null){
				Log.w(TAG, "Need context to test the network...");
				return;
			}
			InputStream stream = null;
			String[] netType = networkType(context);
			try {
				long startCon = System.currentTimeMillis();
				URL url = new URL(downloadFileUrl);
				URLConnection con = url.openConnection();
				con.setUseCaches(false);
				stream = con.getInputStream();
				int b = stream.read();
				if(b>-1){
					long connectionLatency = System.currentTimeMillis() - startCon;
					GA.sendEvent("NET_LATENCY",netType[0],netType[1],connectionLatency);
					Log.d(TAG, "Latency:"+netType[0]+"#"+netType[1]+"#"+connectionLatency);
				}else{
					Log.e(TAG, "error get the first byte from server.");
					return;
				}
				long start = System.nanoTime();
				int currentByte = 0;
				long updateStart = start;

				byte[] buffer = new byte[1024];
				int nowCount = 0;
				while ((nowCount = stream.read(buffer)) > 0 && currentByte < 20480) {
					currentByte += nowCount;
					//					long now = System.nanoTime();
					//					int progress = (int) ((currentByte / (double) EXPECTED_SIZE_IN_BYTES) * 100);
					//					if(now - updateStart==0){
					//						Log.w(TAG, "");
					//					}
					//					Message msg = Message.obtain(mHandler, MSG_UPDATE_STATUS, calculate(now - updateStart, nowCount));
					//					msg.arg1 = progress;
					//					msg.arg2 = nowCount;
					//					mHandler.sendMessage(msg);

					//					Log.d(TAG, "speed:"+netType[0]+"#"+netType[1]+"#"+currentByte+"#"+(now-updateStart));
					// Reset
					//					updateStart = System.nanoTime();
				}

				long downloadTime = (System.nanoTime() - start);
				// Prevent AritchmeticException
				if (downloadTime == 0) {
					downloadTime = 1;
				}
				long speed = (long)calculate(downloadTime, currentByte).downspeed;
				GA.sendEvent("NET_SPEED",netType[0],netType[1],speed);
				Log.d(TAG, "speed:"+netType[0]+"#"+netType[1]+"#"+speed);
			} catch (MalformedURLException e) {
				Log.e(TAG, e.getMessage()==null?"":e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage()==null?"":e.getMessage());
			} finally {
				try {
					if (stream != null) {
						stream.close();
					}
				} catch (IOException e) {
				}
			}
		}
	};

	/**
	 * Get Network type from download rate
	 * 
	 * @return 0 for Edge and 1 for 3G
	 */
	private int networkType(final double kbps) {
		int type = 1;// 3G
		// Check if its EDGE
		if (kbps < EDGE_THRESHOLD) {
			type = 0;
		}
		return type;
	}

	public String[] networkType(Context context){
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		//	String operateName = telephonyManager.getSimOperatorName();
		String operateName = telephonyManager.getNetworkOperatorName();

		switch (telephonyManager.getNetworkType()) {  
		case TelephonyManager.NETWORK_TYPE_1xRTT:  
			operateName += "_1xRTT"; // ~ 50-100 kbps  
			break;
		case TelephonyManager.NETWORK_TYPE_CDMA:  
			operateName += "_CDMA"; // ~ 14-64 kbps  
			break;
		case TelephonyManager.NETWORK_TYPE_EDGE:  
			operateName += "_EDGE"; // ~ 50-100 kbps 
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:  
			operateName += "_EVDO_0"; // ~ 400-1000 kbps  
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:  
			operateName += "_EVDO_A"; // ~ 600-1400 kbps 
			break;
		case TelephonyManager.NETWORK_TYPE_GPRS:  
			operateName += "_GPRS"; // ~ 100 kbps  
			break;
		case TelephonyManager.NETWORK_TYPE_HSDPA:  
			operateName += "_HSDPA"; // ~ 2-14 Mbps  
			break;
		case TelephonyManager.NETWORK_TYPE_HSPA:  
			operateName += "_HSPA"; // ~ 700-1700 kbps  
			break;
		case TelephonyManager.NETWORK_TYPE_HSUPA:  
			operateName += "_HSUPA"; // ~ 1-23 Mbps  
			break;
		case TelephonyManager.NETWORK_TYPE_UMTS:  
			operateName += "_UMTS"; // ~ 400-7000 kbps 
			break;
		case TelephonyManager.NETWORK_TYPE_EHRPD:  
			operateName += "_EHRPD"; // ~ 1-2 Mbps  
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:  
			operateName += "_EVDO_B"; // ~ 5 Mbps  
			break;
		case TelephonyManager.NETWORK_TYPE_HSPAP:  
			operateName += "_HSPAP"; // ~ 10-20 Mbps  
			break;
		case TelephonyManager.NETWORK_TYPE_IDEN:  
			operateName += "_IDEN"; // ~25 kbps  
			break;
		case TelephonyManager.NETWORK_TYPE_LTE:  
			operateName += "_LTE"; // ~ 10+ Mbps  
			break;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:  
			operateName += "_UNKNOWN";  
		}
		String netType = "INVALID";
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();  

		if (networkInfo != null && networkInfo.isConnected()) {  
			netType = networkInfo.getTypeName();  
		}  

		return new String[]{netType,operateName};  
	}


	public Object[] networkTypePlayer(Context context){
		Integer netType = -1;
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String operateName = telephonyManager.getNetworkOperatorName();
		
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();  

		//WIFI: 1, mobile: 0
		if (networkInfo != null && networkInfo.isConnected()) {  
			netType = networkInfo.getType();  
		}  

		if(netType == 0) { 
			switch (telephonyManager.getNetworkType()) {  
			case TelephonyManager.NETWORK_TYPE_1xRTT:  
				netType = 2; // ~ 50-100 kbps  
				break;
			case TelephonyManager.NETWORK_TYPE_CDMA:  
				netType = 3; // ~ 14-64 kbps  
				break;
			case TelephonyManager.NETWORK_TYPE_EDGE:  
				netType = 4; // ~ 50-100 kbps 
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:  
				netType = 5; // ~ 400-1000 kbps  
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_A:  
				netType = 6; // ~ 600-1400 kbps 
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS:  
				netType = 7; // ~ 100 kbps  
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:  
				netType = 8; // ~ 2-14 Mbps  
				break;
			case TelephonyManager.NETWORK_TYPE_HSPA:  
				netType = 9; // ~ 700-1700 kbps  
				break;
			case TelephonyManager.NETWORK_TYPE_HSUPA:  
				netType = 10; // ~ 1-23 Mbps  
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:  
				netType = 11; // ~ 400-7000 kbps 
				break;
			case TelephonyManager.NETWORK_TYPE_EHRPD:  
				netType = 12; // ~ 1-2 Mbps  
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_B:  
				netType = 13; // ~ 5 Mbps  
				break;
			case TelephonyManager.NETWORK_TYPE_HSPAP:  
				netType = 14; // ~ 10-20 Mbps  
				break;
			case TelephonyManager.NETWORK_TYPE_IDEN:  
				netType = 15; // ~25 kbps  
				break;
			case TelephonyManager.NETWORK_TYPE_LTE:  
				netType = 16; // ~ 10+ Mbps  
				break;
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:  
				netType = 0;  
			}
		}
		if(netType == -1) netType = 0;
		return new Object[]{netType,operateName};  
	}


	/**
	 * 
	 * 1 byte = 0.0078125 kilobits 1 kilobits = 0.0009765625 megabit
	 * 
	 * @param downloadTimeNs
	 *            in miliseconds -> ns
	 * @param bytesIn
	 *            number of bytes downloaded
	 * @return SpeedInfo containing current speed
	 */
	private SpeedInfo calculate(final long downloadTimeNs, final long bytesIn) {
		SpeedInfo info = new SpeedInfo();
		// from mil to sec
		long bytespersecond = (long)((bytesIn*1.0 / downloadTimeNs) * 1000 * 1000 * 1000)/1024;
		double kilobits = bytespersecond * BYTE_TO_KILOBIT;
		double megabits = kilobits * KILOBIT_TO_MEGABIT;
		info.downspeed = bytespersecond;
		info.kilobits = kilobits;
		info.megabits = megabits;

		return info;
	}

	/**
	 * Transfer Object
	 * 
	 * @author devil
	 *
	 */
	private static class SpeedInfo {
		public double kilobits = 0;
		public double megabits = 0;
		public double downspeed = 0;

		@Override
		public String toString() {
			return ""+downspeed;
		}
	}

	// Private fields
	private static final String TAG = NETSpeedTest.class.getSimpleName();
	private static final int EXPECTED_SIZE_IN_BYTES = 1048576 * 6;// 1MB
	// 1024*1024

	private static final double EDGE_THRESHOLD = 176.0;
	private static final double BYTE_TO_KILOBIT = 0.0078125;
	private static final double KILOBIT_TO_MEGABIT = 0.0009765625;

}