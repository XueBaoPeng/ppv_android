package org.libsdl.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.location.Location;
import android.telephony.TelephonyManager;

import com.amap.api.location.AMapLocation;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.IOUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.LocationUtil;
import com.star.mobile.video.util.LocationUtil.LocationResult;
import com.star.util.Log;
import com.star.util.app.GA;
import com.star.util.json.JSONUtil;
import com.star.util.monitor.NETSpeedTest;

public class PlayerAnalytic {

	private String TAG = "PlayerAnalytic";
	
	Player mPlayer;
	private NETSpeedTest mNet;
	long epoch = System.currentTimeMillis()/1000;

	public final ArrayBlockingQueue<HashMap<String, Object>> toSend = new ArrayBlockingQueue<HashMap<String, Object>>(40);
	HashSet<Integer> idSet = new HashSet<Integer>();
	HashSet<Integer> idSetDelay = new HashSet<Integer>();
	
	LocationUtil lu;
	double longitude;
	double latitude;
	HashMap<String, Object> map;
	private final String videoURL;
	private final String postUrl;
	private final String appVersion;
	private final String postBasicLogUrl;
	private final String sessionID;
	private int LagTimes=0;
/*	
	private int mPlayerState=0;//0:has't play 1: is starting 2:is playing 3: is pause 4:is seeking 5:is delaying
	private int mLastPosition=0;
	private int mLagTime=0;
	private int mStartTime=0;
	private int mSeekTime=0;
*/
	
//	private final int model;
	public PlayerAnalytic(Player p){
		mPlayer = p;
		mNet = new NETSpeedTest(p);
		lu = new LocationUtil();
		videoURL = p.getFilename();
//		model = mPlayer.getResources().getInteger(R.string.model);
		String baseURLForAna = mPlayer.getResources().getString(R.string.player_analytic_url);
		postUrl = baseURLForAna + "/segment";
		postBasicLogUrl = baseURLForAna + "/basic";
		appVersion = ApplicationUtil.getAppVerisonName(mPlayer);
		sessionID = getSessionID();
	}
	
	private boolean isPostBasicInfo = false;

	public void postBasicInfo() {
		if(sessionID==null){
			return;
		}
		new LoadingDataTask() {
			String location;
			
			LocationUtil util;
			@Override
			public void onPreExecute() {
				util = LocationUtil.getInstance(mPlayer);
			}

			@Override
			public void doInBackground() {
				if (util != null) {
					AMapLocation aMapLocation = util.getAMapLocation();
					if (aMapLocation != null) {
						location = String.format("%.2f,%.2f", aMapLocation.getLongitude(), aMapLocation.getLatitude());				
					}
				}
			}

			@Override
			public void onPostExecute() {
					final String log = sessionID + getEquipID() + location + getNetwork() + getOperator();
					String oriLog = SharedPreferencesUtil.getVideoAnalyticsData(mPlayer);
					if(log.compareTo(oriLog) == 0)  return;
					final String eventID = (mPlayer.isLive?"LIVE_":"VOD_")+getEventID();//For one time of play video.
					new LoadingDataTask() {
						@Override
						public void onPreExecute() {
						}

						@Override
						public void onPostExecute() {
							if(isPostBasicInfo){
								SharedPreferencesUtil.setVideoEventID(mPlayer, eventID);
								SharedPreferencesUtil.setVideoAnalyticsData(mPlayer, log);
							}
						}

						@Override
						public void doInBackground() {
							map = new HashMap<String, Object>();
							map.put("eventID", eventID );
							map.put("sessionID",getSessionID());
							map.put("equipID", getEquipID());
							map.put("location", location);
							map.put("network", getNetwork());
							map.put("operator", getOperator());
							map.put("version", appVersion);
							Log.i(TAG, "Post to basicMsg:" + map);
							Result re = null;
							try {
								String resp = IOUtil.httpPostToJSON(JSONUtil.getJSON(map),postBasicLogUrl);
								Log.d(TAG, "Post to basicMsg:" + resp);
								re = JSONUtil.getFromJSON(resp, Result.class);
							} catch (Exception e) {
								isPostBasicInfo = false;
								Log.d(TAG, "Post to basicMsg:",e);
								return;
							}
							if(re!=null&&"1000".equals(re.code)){
								isPostBasicInfo = true;
							}
						}
					}.execute();
				}
		}.execute();
	}
	
	class Result{
		String code;
		String errMsg;
		
		public void setCode(String code) {
			this.code = code;
		}
		public void setErrMsg(String errMsg) {
			this.errMsg = errMsg;
		}
	}

	/**
	 * 一次播放
	 * @return
	 */
	String getSessionID(){
//		Log.i("initData", "StarApplication.mUser="+StarApplication.mUser);
//		Log.i("initData", "StarApplication.mUser.getId()="+StarApplication.mUser.getId());
		if (StarApplication.mUser != null && StarApplication.mUser.getId() != null) {
			return StarApplication.mUser.getId()+/*"-"+StarApplication.mUser.getUserName()+"-"+*/""+epoch;
		}else{
			return null;
		}
	}
	/**
	 * 一次上传记录
	 * @return
	 */
	String getEventID(){
		return SharedPreferencesUtil.getToken(mPlayer)+System.currentTimeMillis();
	}

	String getEquipID() {
//		String id = SharedPreferencesUtil.getDiciveId(mPlayer);
//		if(id == null) return SharedPreferencesUtil.getUserName(mPlayer);
		TelephonyManager tm = (TelephonyManager)mPlayer.getSystemService(Context.TELEPHONY_SERVICE); 
		return tm.getDeviceId();
	}

	Integer getNetwork() {
		return (Integer)mNet.networkTypePlayer(mPlayer)[0];
	}

	String getOperator() {
		Object oName = mNet.networkTypePlayer(mPlayer)[1];
		if(oName==null||oName.toString().isEmpty()){
			return "UNKNOWN";
		}else{
			return oName.toString();
		}
	}


	String getLocation(){
		lu.getLocation(mPlayer, locationResult);
		String res = String.format("%.2f,%.2f", longitude, latitude);
		return res;
	}
	
	Long getRsID() {
		return mPlayer.contentId;
	}

	public LocationResult locationResult = new LocationResult() {

		@Override
		public void gotLocation(Location location) {

			if (location != null) {
				longitude = location.getLongitude();
				latitude = location.getLatitude();
			}
		}
	};
	
	public boolean getPostBasicInfo(){
		return isPostBasicInfo;
	}
	
	public void clearCachedTask(){
		idSet.clear();
		idSetDelay.clear();
		if(LagTimes!=0) {
			GA.sendEvent("PlayerAnalytic", "Lag", "Count", LagTimes);	
//			GA.sendScreen("can get times");
			Log.d(TAG, "zy receive lag times" +LagTimes);
			LagTimes=0;
		}
		toSend.clear();
	}
	
	protected void postLog() {
		//基本信息没上传成功时，不上传切片信息
		if(!getPostBasicInfo()||toSend.isEmpty()) 
			return;

		map = new HashMap<String, Object>();
		map.put("eventID", SharedPreferencesUtil.getVideoEventID(mPlayer));
//		if(contentId > 0)
//			map.put("rsID", contentId);
//		if( liveRsID != null) {
//			map.put("rsID", liveRsID);
//		}
		String rsID = catchRsID(videoURL);
		if(rsID==null){
			rsID = "UNKNOWN"; 
		} 
		map.put("rsID", rsID);

		List<HashMap<String, Object>> subs = new ArrayList<HashMap<String, Object>>();
		while(!toSend.isEmpty()){
			subs.add(toSend.poll());
		}
		map.put("playerSegmentInfo", subs);

		final String jsonStr = JSONUtil.getJSON(map);
		Log.i(TAG,  "Size: " + jsonStr.length() + " " + jsonStr + " " + postUrl);


		new LoadingDataTask() {
			HttpResponse resp;
			@Override
			public void onPreExecute() {
			}
			@Override
			public void onPostExecute() {
			}
			@Override
			public void doInBackground() {
				try {
					resp = IOUtil.httpPost(postUrl, jsonStr);
					Log.d(TAG, "res: " + EntityUtils.toString(resp.getEntity(),"UTF-8") );
				} catch (Exception e) {
					Log.e(TAG, "Post log for ts: ",e);
				}
			}
		}.execute();

	}
	
	private String catchRsID(String url){
		String[] ps = url.split("/");
		if(ps.length>0){
			return ps[ps.length-2];
		}
		return null;
	}
	
	protected void processLog(String rawStr) {
		
		String[] elems = rawStr.split(" ");
		int logID = Integer.parseInt(elems[0]);
		Long size = Long.parseLong(elems[1]);
		double duration = Double.parseDouble(elems[2]);
		Long endTime = Long.parseLong(elems[3]);
		int speed = (int)(size/duration/1024*8);
		String[] tsElems = elems[4].split("/");
		String tsLast = tsElems[tsElems.length - 1];
		String ts;
		if(tsLast.startsWith("live")) {
//			String liveRsID = tsElems[tsElems.length - 2];
			return;
		}else{
			ts = tsLast.substring(0, tsLast.length()-3);
		}
		
		Log.d(TAG, "newStrs" + " " + logID  + " " + speed  + " " + endTime + " "+ ts);
		
		if(!idSet.contains(logID)) {
			idSet.add(logID);
		   	HashMap<String, Object> subMap1 = new HashMap<String, Object>();
			subMap1.put("segmentName", ts);
			subMap1.put("downloadSpeed", speed);
			subMap1.put("downloadTime", endTime);
			toSend.offer(subMap1);
		}
	}

protected void sendfinishPlayerDelay() {
	if(LagTimes!=0) {
		GA.sendEvent("PlayerAnalytic", "Lag", "Count", LagTimes);
//		GA.sendScreen("can get times");
		Log.d(TAG, "zy receive lag times finish" +LagTimes);
		LagTimes=0;
	}
}
protected void finishPlayerDelay(String rawStr) {
	int logID = Integer.parseInt(rawStr);
	if(!idSetDelay.contains(logID)) {
		idSetDelay.add(logID);
		if(LagTimes!=0) {
			GA.sendEvent("PlayerAnalytic", "Lag", "Count", LagTimes);	
//			GA.sendScreen("can get times");
			Log.d(TAG, "zy receive lag times" +LagTimes);
			LagTimes=0;
		}
	}
	}
protected void processPlayerDelay(String rawStr,int mode, boolean isStart) {
	//Log.d(TAG, "zy receive" + rawStr);
	
	String[] elems = rawStr.split("\\s+");
	int logID = Integer.parseInt(elems[0]);
	long size = Long.parseLong(elems[1]);
		
	if(!idSetDelay.contains(logID)) {
		idSetDelay.add(logID);
		if(mode==0&&isStart) {  //start time
			Log.d(TAG, "zy receive start time" +logID+" "+ size);
			GA.sendEvent("PlayerAnalytic", "StartDelay", "Time", size);
		}
		if(mode==1&&isStart) {  //ka time
			Log.d(TAG, "zy receive ka time" +logID+" "+ size);
			GA.sendEvent("PlayerAnalytic", "Lag", "Time", size);
			LagTimes++;
		}
		if(mode==2&&isStart) {  //seek time
			Log.d(TAG, "zy receive seek time" +logID+" "+ size);
			GA.sendEvent("PlayerAnalytic", "SeekDelay", "Time", size);	
		}
	}
	
	
	/*
		String[] elems = rawStr.split(" ");
		int logID = Integer.parseInt(elems[0]);
		Long size = Long.parseLong(elems[1]);
		double duration = Double.parseDouble(elems[2]);
		Long endTime = Long.parseLong(elems[3]);
		int speed = (int)(size/duration/1024*8);
		String[] tsElems = elems[4].split("/");
		String tsLast = tsElems[tsElems.length - 1];
		String ts;
		if(tsLast.startsWith("live")) {
//			String liveRsID = tsElems[tsElems.length - 2];
			return;
		}else{
			ts = tsLast.substring(0, tsLast.length()-3);
		}
		
		Log.d(TAG, "newStrs" + " " + logID  + " " + speed  + " " + endTime + " "+ ts);
		
		if(!idSet.contains(logID)) {
			idSet.add(logID);
		   	HashMap<String, Object> subMap1 = new HashMap<String, Object>();
			subMap1.put("segmentName", ts);
			subMap1.put("downloadSpeed", speed);
			subMap1.put("downloadTime", endTime);
			toSend.offer(subMap1);
		}
		*/
	}
	/*
	public void processPlayerDelay() {
		android.util.Log.w(TAG, "zy analyzer"+mPlayerState);
		GA.sendScreen("zy");
		if(mPlayerState == 0)
			return;
		if(mPlayerState == 1) {
			if(mPlayer.getCurrentPosition() != 0 && mPlayer.CheckIfPlayStart()) {
				mPlayerState = 2;
				mLastPosition=mPlayer.getCurrentPosition();
				GA.sendEvent("PlayerAnalytic", "StartDelay", "Time", mStartTime);
				android.util.Log.w(TAG, "zy PlayerAnalytic"+"StartDelay"+"Time"+mStartTime);
			}
			else
			{
				mStartTime += 1;
			}
		}
		if(mPlayerState == 2) {
			if(mLastPosition >= mPlayer.getCurrentPosition()) {
				mPlayerState = 5;
				GA.sendEvent("PlayerAnalytic", "Lag", "Count", 1);	
				android.util.Log.w(TAG, "zy PlayerAnalytic"+"Lag"+"Count"+1);
				mLagTime += 1;
			}
			else {
				mLastPosition=mPlayer.getCurrentPosition();
			}
		}
		if(mPlayerState == 3) {
			if(mLastPosition!=mPlayer.getCurrentPosition()) {
				mPlayerState = 2;
			}
		}
		if(mPlayerState == 4) {
			if(mLastPosition != mPlayer.getCurrentPosition()) {
				mPlayerState = 2;
				GA.sendEvent("PlayerAnalytic", "SeekDelay", "Time", mSeekTime);	
				android.util.Log.w(TAG, "zy PlayerAnalytic"+"SeekDelay"+"Time"+mSeekTime);
			}
			else {
				mSeekTime += 1;
			}
		}
		if(mPlayerState == 5) {
			if(mLastPosition != mPlayer.getCurrentPosition()) {
				mPlayerState = 2;
				GA.sendEvent("PlayerAnalytic", "Lag", "Time", mLagTime);
				android.util.Log.w(TAG, "zy PlayerAnalytic"+"Lag"+"Time"+mLagTime);
			}
			else {
				mLagTime += 1;
			}
		}
	}

	public void changePlayerState(int state) {
		switch (state) {
		case 0: 
			mPlayerState=0;
			clearTimerCount();
			break;
		case 1: 
			mPlayerState=1;
			clearTimerCount();
			break;
		case 2: 
			mPlayerState=2;
			clearTimerCount();
			break;
		case 3: 
			mPlayerState=3;
			clearTimerCount();
			break;
		case 4: 
			mPlayerState=4;
			clearTimerCount();
			break;
		case 5: 
			mPlayerState=5;
			clearTimerCount();
			break;
		}
		
	}

	private void clearTimerCount() {
		mLastPosition=mPlayer.getCurrentPosition();
		mLagTime=0;
		mStartTime=0;
		mSeekTime=0;	
	}
*/

}
//class LogTask extends AsyncTask<Object, Integer, Integer>{
//	
//	BufferedReader bufferedReader;
//	HashSet<Integer> idSet;
//	String eventID;
//	Long rsID;
//	@Override
//	protected void onPreExecute(){
//		Process process = null;
//		try {
//			process = Runtime.getRuntime().exec("logcat -d -s SDL/APP");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		bufferedReader = new BufferedReader(
//				new InputStreamReader(process.getInputStream()));
//		idSet = new HashSet<Integer>();
//	}
//
//	@Override
//	protected Integer doInBackground(Object... paras) {
//		String line = "";
//		eventID = (String) paras[0];
//		rsID = (Long) paras[1];
//
//		try {
//			while ( (line = bufferedReader.readLine()) != null) {
//				int index = line.indexOf("http_speed_test");
//				if(index > 0) {
//					processLog(line.substring(index + 16));
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	} 
//	
//	protected void processLog(String rawStr) {
//		
//		String[] elems = rawStr.split(" ");
//		int logID = Integer.parseInt(elems[0]);
//		Long size = Long.parseLong(elems[1]);
//		double duration = Double.parseDouble(elems[2]);
//		Long endTime = Long.parseLong(elems[3]);
//		int speed = (int)(size/duration/1024);
//		String[] tsElems = elems[4].split("/");
//		String tsLast = tsElems[tsElems.length - 1];
//		String ts = tsLast.substring(0, tsLast.length()-3);
//		Log.d("Player", "newStrs" + " " + logID  + " " + speed  + " " + endTime + " "+ ts);
//		
//		if(!idSet.contains(logID)) {
//			idSet.add(logID);
//			
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			map.put("eventID", eventID);
//			map.put("rsID", rsID);
//			HashMap<String, Object> subMap1 = new HashMap<String, Object>();
//			subMap1.put("segmentName", ts);
//			subMap1.put("downloadSpeed", speed);
//			subMap1.put("downloadTime", endTime);
//			
//			List<HashMap<String, Object>> subs = new ArrayList<HashMap<String, Object>>();
//			subs.add(subMap1);
//
//			map.put("playerSegmentInfo", subs);
//			Log.d("Player", "" + logID + ": " + JSONUtil.getJSON(map));
//		}
//	}
//}