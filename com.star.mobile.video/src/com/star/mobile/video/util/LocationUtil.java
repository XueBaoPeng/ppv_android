package com.star.mobile.video.util;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

public class LocationUtil {

	private static final String TAG = "LocationUtil";
	private static LocationUtil util;
	private LocationManagerProxy mAMapLocManager;
	private AMapLocation aMapLocation;
	private LocationUtil(Context context){
		mAMapLocManager = LocationManagerProxy.getInstance(context);
		mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 60*1000, 15, locationListener);
		mAMapLocManager.setGpsEnable(false);
	}

	public LocationUtil(){

	}
	
	public static LocationUtil getInstance(Context context){
		if(util == null){
			util = new LocationUtil(context);
		}
		return util;
	}

	private void stopLocation(){
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(locationListener);
			mAMapLocManager.destory();
		}
		mAMapLocManager = null;
	}

	public static boolean netWorkProviderEnable(Context context){
		LocationManagerProxy mAMapLocManager = LocationManagerProxy.getInstance(context);
		return mAMapLocManager.isProviderEnabled(LocationManagerProxy.NETWORK_PROVIDER);
	}

	public AMapLocation getAMapLocation(){
		int i = 0;
		while (true) {
			if (aMapLocation != null) {
				return aMapLocation;
			}
			i++;
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				Log.e(TAG, "aMapLocation=null", e);
			}
			if(i==10)
				return null;
			Log.w(TAG, "try to get aMapLocation at " + i);
		}
	}

	private AMapLocationListener locationListener = new AMapLocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(AMapLocation location) {
			aMapLocation = location;
			if (location != null) {
				stopLocation();
				Double geoLat = location.getLatitude();
				Double geoLng = location.getLongitude();
				String cityCode = "";
				String desc = "";
				Bundle locBundle = location.getExtras();
				if (locBundle != null) {
					cityCode = locBundle.getString("citycode");
					desc = locBundle.getString("desc");
				}
				String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
						+ "\n精    度    :" + location.getAccuracy() + "米"
						+ "\n定位方式:" + location.getProvider() + "\n定位时间:"
						+ location.getTime() + "\n城市编码:" + cityCode + "\n位置描述:"
						+ desc + "\n省:" + location.getProvince() + "\n市:"
						+ location.getCity() + "\n区(县):" + location.getDistrict()
						+ "\n城市编码:" + location.getCityCode() + "\n区域编码:" + location
						.getAdCode());
				Log.d(TAG, str);
			}
		}
	};


	Timer timer1;
	LocationManager lm;
	LocationResult locationResult;
	boolean gps_enabled=false;
	boolean network_enabled=false;

	public boolean getLocation(Context context, LocationResult result)
	{
		//I use LocationResult callback class to pass location value from MyLocation to user code.
		locationResult=result;
		
		if(lm==null)
			lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		//exceptions will be thrown if provider is not permitted.
		try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
		try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

		//don't start listeners if no provider is enabled
		if(!gps_enabled && !network_enabled)
			return false;

		if(gps_enabled)
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1000, locationListenerGps);
	    if(network_enabled)
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1000, locationListenerNetwork);
		timer1=new Timer();
		timer1.schedule(new GetLastLocation(), 1000);
		return true;
	}

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer1.cancel();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerNetwork);
		}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer1.cancel();
			locationResult.gotLocation(location);
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerGps);
		}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	class GetLastLocation extends TimerTask {
		@Override

		public void run() {

			//Context context = getClass().getgetApplicationContext();
			Location net_loc=null, gps_loc=null;
			if(gps_enabled)
				gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(network_enabled)
				net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			//if there are both values use the latest one
			if(gps_loc!=null && net_loc!=null){
				if(gps_loc.getTime()>net_loc.getTime())
					locationResult.gotLocation(gps_loc);
				else
					locationResult.gotLocation(net_loc);
				return;
			}

			if(gps_loc!=null){
				locationResult.gotLocation(gps_loc);
				return;
			}
			if(net_loc!=null){
				locationResult.gotLocation(net_loc);
				return;
			}
			locationResult.gotLocation(null);
		}
	}

	public static abstract class LocationResult{
		public abstract void gotLocation(Location location);
	}

}
