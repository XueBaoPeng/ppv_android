package com.star.mobile.video.shared;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.Area;
import com.star.cms.model.Channel;
import com.star.cms.model.ChatRoom;
import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.model.ShareChatRoomModel;
import com.star.mobile.video.service.FeedbackService;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.util.json.JSONUtil;

public class SharedPreferencesUtil {
	
	private static SharedPreferences mAlertSharedPre;
	private static SharedPreferences mDBSharedPre;
	private static SharedPreferences mChatRSharedPre;
	private static SharedPreferences mUserSharedPre;
	private static SharedPreferences mVersionSharedPre;
	private static SharedPreferences mTaskCoinSharedPre;
	private static SharedPreferences mVideoGuideSharedPre;
	private static SharedPreferences mTenbSharePre;
	
	public static SharedPreferences getTenbSharePre(Context context){
		if(mTenbSharePre == null)
			mTenbSharePre = context.getSharedPreferences("mytenb", Context.MODE_PRIVATE);
		return mTenbSharePre;
	}
	
	public static SharedPreferences getAlertSharePreferences(Context context){
		if(mAlertSharedPre == null)
			mAlertSharedPre = context.getSharedPreferences("alert_info", Context.MODE_PRIVATE);
		return mAlertSharedPre;
	}
	
	public static SharedPreferences getDBSharePreferences(Context context){
		if(mDBSharedPre == null)
			mDBSharedPre = context.getSharedPreferences(Constant.DB_INFO, Context.MODE_PRIVATE);
		return mDBSharedPre;
	}
	
	public static SharedPreferences getChatRoomSharePreferences(Context context){
		if(mChatRSharedPre == null)
			mChatRSharedPre = context.getSharedPreferences("chat_room", Context.MODE_PRIVATE);
		return mChatRSharedPre;
	}
	
	public static SharedPreferences getGuideSharePreferencesByUser(Context context){
		if(mUserSharedPre == null)
			mUserSharedPre = context.getSharedPreferences(Constant.GUIDE_BY_USER, Context.MODE_PRIVATE);
		return mUserSharedPre;
	}
	
	public static SharedPreferences getGuideSharePreferencesByAppVersion(Context context){
		if(mVersionSharedPre == null)
			mVersionSharedPre = context.getSharedPreferences(Constant.GUIDE_BY_VERSION, Context.MODE_PRIVATE);
		return mVersionSharedPre;
	}
	
	public static SharedPreferences getTaskCoinSharedPreferences(Context context){
		if(mTaskCoinSharedPre == null)
			mTaskCoinSharedPre = context.getSharedPreferences("task_coin", Context.MODE_PRIVATE);
		return mTaskCoinSharedPre;
	}
	public static SharedPreferences getVideoGuideSharedPreferences(Context context){
		if(mVideoGuideSharedPre == null)
			mVideoGuideSharedPre = context.getSharedPreferences("video_gudie", Context.MODE_PRIVATE);
		return mVideoGuideSharedPre;
	}
	
	public static void setAppGuideDone(Context context){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("app_guide", Context.MODE_PRIVATE);
		mSharedPreferences.edit().putBoolean("guideOf_"+ApplicationUtil.getAppVerison(context), true).commit();
	}
	
	public static boolean isAppGuideDone(Context context){
		if(StarApplication.CURRENT_VERSION == Constant.FINAL_VERSION) {
			SharedPreferences mSharedPreferences = context.getSharedPreferences("app_guide", Context.MODE_PRIVATE);
			return mSharedPreferences.getBoolean("guideOf_"+ApplicationUtil.getAppVerison(context), false);
		} else {
			return true;
		} 
	}
	
	public static int getChatRoomMsgCount(Context context, Long roomId){
		return getChatRoomSharePreferences(context).getInt("channel_"+roomId, 0);
	}
	
	public static void saveChatRoomMsgCount(Context context, Long roomId, int count){
		getChatRoomSharePreferences(context).edit().putInt("channel_"+roomId, count).commit();
	}
	
	public static void saveChatRoomMsgCount(Context context, ChatRoom room, int size){
//		if(channel.getHotChatRate()!=null && channel.getHotChatRate()==getChatRoomMsgCount(context, channel.getId()))
//			return;
		if(getChatRoomMsgCount(context, room.getId())==0)
			saveChatRoomMsgCount(context, room.getId(), room.getHotChatRate()==null?1:(room.getHotChatRate()+size));
		else
			saveChatRoomMsgCount(context, room.getId(), getChatRoomMsgCount(context, room.getId())+size);
	}
	
	public static void saveCommentId(Context context,String key,boolean status) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("userPraise", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(key, status);
		mEditor.commit();
	}
	
	public static boolean getCommentStatus(Context context,String key) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("userPraise", Context.MODE_WORLD_READABLE);
		return  mSharedPreferences.getBoolean(key, false); 
	} 
	
	
	public static void saveUserInfo(Context context,Map<String,String> params) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		for(String key:params.keySet()) {
			if("token".equals(key)) {
				IOUtil.setTOKEN(params.get(key));
			}
			mEditor.putString(key, params.get(key));
		}
		mEditor.commit();
	}
	
	public static String getToken(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("token", null);
	}
	
	public static void saveToKen(Context context,String toKen) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("token", toKen);
		IOUtil.setTOKEN(toKen);
		mEditor.commit();
	}
	
	public static User getUserInfo(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.MODE_WORLD_READABLE);
		String usrinfo =  mSharedPreferences.getString("userInfo", null);
		try{
			if(usrinfo != null) {
				return JSONUtil.getFromJSON(usrinfo, new TypeToken<User>() {}.getType());
			}
		}catch(Exception e){
			Log.e("User", "get user info from local error!");
		}
		return null;
	}
	
	public static void saveUserInfo(Context context, String info) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.MODE_WORLD_READABLE);
		mSharedPreferences.edit().putString("userInfo", info).commit();
	}
	
	public static String getUserName(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("userName", null);
	}
	
	public static String getPassword(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("password", null);
	}
	
	public static void saveDeviceId(Context context,String value) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("deviceId", value);
		mEditor.commit();
	}
	
	public static void clearDeviceId(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("deviceId", "");
		mEditor.commit();
	}
	
	public static String getDiciveId(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("deviceId", null);
	}
	
	public static void clearToken(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("token",null);
		mEditor.commit();
	}
	
	public static void clearUserInfo(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.clear();
		mEditor.commit();
		IOUtil.setTOKEN(null);
		StarApplication.mUser = null;
		SharedPreferences mShare = context.getSharedPreferences("bind_status", Context.MODE_PRIVATE);
		mShare.edit().clear().commit();
	}
	
	public static void saveAreaId(Context context,long areaId) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("areaId", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putLong("areaId",areaId);
		mEditor.commit();
	}
	
	public static void saveLastAccessTime(Context context, String smartcardNo, int days) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.MODE_PRIVATE);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean("sc_"+smartcardNo+"_"+days, true);
		mEditor.commit();
	}
	public static boolean getLastAccessTime(Context context, String smartcardNo, int days) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.user_info), Context.MODE_PRIVATE);
		return mSharedPreferences.getBoolean("sc_"+smartcardNo+"_"+days, false);
	}
	
	public static long getAreaId(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("area", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getLong("areaId", 0);
	} 
	
	/**
	 * 下次登录记住用户名
	 * @param context
	 * @param userName 用户名
	 */
	public static void keepUserName(Context context,String userName) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("username", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("KeepName",userName);
		mEditor.commit();
	}
	
	public static String getLastUserName(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("username", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("KeepName", null);
	}
	
	/**
	 * 设置手机区号
	 * @param context
	 * @param phoneAreaNumber
	 */
	public static void setPhoneAreaNumber(Context context,String phoneAreaNumber) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("phoneAreaNumber", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("areaNumber",phoneAreaNumber);
		mEditor.commit();
	}
	
	/**
	 * 获取手机区号
	 * @param context
	 * @return
	 */
	public static String getPhoneAreaNumber(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("phoneAreaNumber", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("areaNumber", null);
	}
	
	public static void saveUserNameOrDeviceId(Context context,String userNameOrDeviceId) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("userNameOrDeviceId", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("lastUser", userNameOrDeviceId);
		mEditor.commit();
	} 
	
	public static String getLastUserNameOrDeviceId(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("userNameOrDeviceId", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("lastUser", null);
	}
	
	/**
	 * 做完四格体验
	 * @param context
	 * @param userName
	 * @param appVersion
	 */
	public static void setReportDone(Context context,String userName,int appVersion) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fastUserReport", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(userName+appVersion, true);
		mEditor.commit();
	}
	
	public static boolean getReportDone(Context context,String userName,int nowAppVersion) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fastUserReport", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getBoolean(userName+nowAppVersion, false);
	}
	
	public static void saveUserCardNo(Context context,String cardNo) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("userCard", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("cardNo", cardNo);
		mEditor.commit();
	}
	
	public static String getUserCardNo(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("userCard", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("cardNo", null);
	}
	
	/**
	 * 四格体验第二次提醒后状态
	 * @param context
	 * @param status
	 */
	public static void setSecondaryRemind(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean("four", true);
		mEditor.commit();
	}
	
	public static boolean getSecondaryRemind(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getBoolean("four", false);
	}
	
	public static void setFiveDays(Context context,long fiveDays) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putLong("fiveDays", fiveDays);
		mEditor.commit();
	}
	
	public static void setNewVersionSize(Context context,float size) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("new_version", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putFloat("versionSize", size);
		mEditor.commit();
	}
	
	public static float getNewVersionSize(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("new_version", Context.MODE_WORLD_READABLE);
		float size = mSharedPreferences.getFloat("versionSize", 0.0f);
		return size;
	}
	
	public static void setNewVersion(Context context,int version) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("new_version", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean("version", true);
		mEditor.putInt("versionCode", version);
		mEditor.commit();
	}
	
	public static boolean isNewVersion(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("new_version", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getBoolean("version", false);
	}
	
	public static int getNewVersion(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("new_version", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getInt("versionCode", 0);
	}
	
	public static String getNewVersionName(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("new_version", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("version", "");
	}
	
	public static void setNewVersionDownLoadStatus(Context context, long status, int version) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("new_version", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putLong("downStatus_"+version, status);
		mEditor.commit();
	}
	
	public static long getNewVersionDownLoadStatus(Context context, int version) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("new_version", Context.MODE_WORLD_READABLE);
		long status = mSharedPreferences.getLong("downStatus_"+version, -1);
		return status;
	}
	
	
	public static void setNewVersionDownLoadId(Context context, long id, int version) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("new_version", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putLong("downId_"+version, id);
		mEditor.commit();
	}
	
	public static long getNewVersionDownLoadId(Context context, int version) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("new_version", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getLong("downId_"+version, -1);
	}
	
	
	public static void setVideoFirstTime(Context context, boolean isFirst) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("video", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean("videoFirstTime", isFirst);
		mEditor.commit();
	}
	
	public static boolean getVideoFirstTime(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("video", Context.CONTEXT_IGNORE_SECURITY);
		return mSharedPreferences.getBoolean("videoFirstTime", true);
	}
	
	public static void setVideoAnalyticsData(Context context, String log) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("video", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("video_analytics", log);
		mEditor.commit();
	}
	
	public static String getVideoAnalyticsData(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("video", Context.CONTEXT_IGNORE_SECURITY);
		return mSharedPreferences.getString("video_analytics", " ");
	}
	
	public static void setVideoEventID(Context context, String log) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("video", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("video_eventid", log);
		mEditor.commit();
	}
	
	public static String getVideoEventID(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("video", Context.CONTEXT_IGNORE_SECURITY);
		return mSharedPreferences.getString("video_eventid", " ");
	}
	
	
	public static long getFiveDays(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getLong("fiveDays", 0);
	}
	
	/**
	 * 第一次后台提醒
	 * @param context
	 */
	public static void setOneRemind(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean("remind", true);
		mEditor.commit();
	}
	
	
	public static boolean isOneRemind(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getBoolean("remind", false);
	}
	
	public static void addGuideNumber(final Context context, String pageTag) {
		if(FunctionService.doHideFuncation(FunctionType.FastReport)||StarApplication.CURRENT_VERSION==Constant.FINAL_VERSION){
			return;
		}
		if(getGuideFinish(context)){
			Log.d("PageGuide", "Page guide has been finished");
			return;
		}
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.CONTEXT_IGNORE_SECURITY);
		boolean isGuide = mSharedPreferences.getBoolean(pageTag, false);
		if(isGuide){
			Log.d("PageGuide", "Page of "+pageTag+" has been recorded, return!!!");
			return;
		}
		int guideNumber = getGuideNumber(context);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(pageTag, true);
		mEditor.putInt("guide", ++guideNumber).commit();
		Log.d("PageGuide", "Page of "+pageTag+" has been recorded, current record count is "+ guideNumber);
		if(guideNumber >= Integer.parseInt(context.getResources().getString(R.string.guide_number))) {
			Log.d("PageGuide", "The record is enough to go feedback page, please waiting 2 minute");
			setGuideFinish(context);
			//引导做完后 两分钟后提醒用户
			new Thread(){
				@Override
				public void run() {
					try {
						Thread.sleep(1000 * 60 * 2);
						if(!getReportDone(context, SharedPreferencesUtil.getUserName(context), ApplicationUtil.getAppVerison(context))){
							FeedbackService.getInstance(context).handler.sendEmptyMessage(112);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	public static void setAppQuestionActive(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditot = mSharedPreferences.edit();
		mEditot.putBoolean("active", true);
		mEditot.commit();
	}
	
	
	public static boolean isQuestionActive(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getBoolean("active", false);
	}
	
	public static int getGuideNumber(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getInt("guide", 0);
	}
	
	public static void clearGuideInfo(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.MODE_PRIVATE);
		mSharedPreferences.edit().clear().commit();
	}
	
	/**
	 * 引导是否做完
	 * @param context
	 */
	public static void setGuideFinish(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditot = mSharedPreferences.edit();
		mEditot.putBoolean("finish", true);
		mEditot.commit();
	}
	
	public static boolean getGuideFinish(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getBoolean("finish", false);
	}
	
	/**
	 * 升级后清空数据
	 * @param context
	 */
	public static void upgradeClear(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("fourlayer", Context.CONTEXT_IGNORE_SECURITY);
		SharedPreferences sharedPreferences = context.getSharedPreferences("new_version", Context.CONTEXT_IGNORE_SECURITY);
		Editor editor = sharedPreferences.edit();
		editor.clear().commit();
		
		Editor mEditor = mSharedPreferences.edit();
		mEditor.clear().commit();
	}
	
	public static void saveArea(Context context, Area area) {
		FunctionService.initAreaFunctions(context, area.getName());
		SharedPreferences mSharedPreferences = context.getSharedPreferences("area", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditot = mSharedPreferences.edit();
		mEditot.putString("customerPhone", area.getPhoneNumber());
		mEditot.putString("nationalFlag", area.getNationalFlag());
		mEditot.putString("currencSymbol", area.getCurrencySymbol());
		mEditot.putString("areaName", area.getName());
		mEditot.putLong("areaId", area.getId());
		mEditot.putString("areaCode", area.getCode());
		mEditot.putString("timezone", area.getTimezone());
		mEditot.putInt("smartCardType", area.getSmartCardType()!= null ? area.getSmartCardType().getNum():-1);
		mEditot.commit();
	}
	
	public static void clearArea(Context context){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("area", Context.CONTEXT_IGNORE_SECURITY);
		mSharedPreferences.edit().clear().commit();
	}
	
	public static String getTimeZone(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("area", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("timezone", null);
	}
	
	public static String getAreaCode(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("area", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("areaCode", null);
	}
	/**
	 * 获得smart card类型
	 * @param context
	 * @return
	 */
	public static int getSmartCardType(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("area", Context.MODE_WORLD_READABLE);
		int smartCardTypeNum = mSharedPreferences.getInt("smartCardType", -1);
		return smartCardTypeNum;
	}
	
	/**
	 * 客服热线
	 * @param context
	 * @return
	 */
	public static String getCustomerPhone(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("area", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("customerPhone", null);
	}
	
	/**
	 * 国旗
	 * @param context
	 * @return
	 */
	public static String getNationalFlag(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("area", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("nationalFlag", null);
	}
	
	/**
	 * 货币符号
	 * @param context
	 * @return
	 */
	public static String getCurrencSymbol(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("area", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("currencSymbol", "₦");
	}
	
	/**
	 * 货币符号
	 * @param context
	 * @return
	 */
	public static String getAreaname(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("area", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("areaName", null);
	}
	
	/**
	 * 第三方账号绑定状态
	 * @param context
	 * @return
	 */
	public static void setBindStatusOf3Account(Context context, String plat, boolean status){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("bind_status", Context.MODE_PRIVATE);
		if("Facebook".equals(plat)){
			mSharedPreferences.edit().putBoolean("aFacebook", status).commit();
		}else if("Twitter".equals(plat)){
			mSharedPreferences.edit().putBoolean("aTwitter", status).commit();
		}
	}
	
	public static boolean getBindStatusOf3Account(Context context, String plat){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("bind_status", Context.MODE_PRIVATE);
		if("Facebook".equals(plat)){
			return mSharedPreferences.getBoolean("aFacebook", false);
		}else if("Twitter".equals(plat)){
			return mSharedPreferences.getBoolean("aTwitter", false);
		}else{
			return false;
		}
	}
	
	public static void setSoundChatAlertAll(Context context,boolean soundalertSwitch) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("chat_alert_sound", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean("soundalert", soundalertSwitch);
		mEditor.commit();
	}
	
	public static boolean getSoundChatAlertAll(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("chat_alert_sound", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getBoolean("soundalert", false);
	}
	public static void setVibrationChatAlertAll(Context context,boolean vibrationalertSwitch) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("chat_alert_vibrationalert", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean("vibrationalert", vibrationalertSwitch);
		mEditor.commit();
	}
	
	public static boolean getVibrationChatAlertAll(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("chat_alert_vibrationalert", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getBoolean("vibrationalert", false);
	}
	
	public static void setLastUrl(String url,Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("last_url", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString("url", url);
		mEditor.commit();
	}
	
	public static String getLastUrl(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("last_url", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getString("url", null);
	}
	
	public static void setSettingChatRoomIds(Context context,List<Long> ids) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("roomIds", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.clear();
		for(Long id:ids) {
			mEditor.putLong("id"+id, id);
		}
		mEditor.commit();
	}
	
	public static List<Long> getSettingChatRoomIds(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("roomIds", Context.MODE_WORLD_READABLE);
		Map<String, Long> mids =  (Map<String, Long>) mSharedPreferences.getAll();
		List<Long> ids = new ArrayList<Long>();
		for(String key :mids.keySet()) {
			ids.add(mids.get(key));
		}
		return ids;
	}
	
	public static void setShareChatRoomData(Context context, ShareChatRoomModel model){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("share_chatroom", Context.MODE_PRIVATE);
		if(model != null){
			mSharedPreferences.edit().putString("model", JSONUtil.getJSON(model)).commit();
		}
	}
	
	public static ShareChatRoomModel getShareChatRoomData(Context context){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("share_chatroom", Context.MODE_PRIVATE);
		String modelJson = mSharedPreferences.getString("model", null);
		if(modelJson == null) return null;
		return JSONUtil.getFromJSON(modelJson, ShareChatRoomModel.class);
	}
	
	public static boolean isDiciveIdLogin(Context context) {
		if(getDiciveId(context) != null && !"".equals(getDiciveId(context))) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void setChannelVersion(Context context, List<Channel> channels){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("channel_verison", Context.MODE_PRIVATE);
		Editor editor = mSharedPreferences.edit();
		for(Channel channel : channels){
			if(channel.getVersion()==null)
				continue;
			if(mSharedPreferences.getLong("channel_v_"+channel.getId(), -1)!=channel.getVersion()){
				editor.putLong("channel_v_"+channel.getId(), channel.getVersion());
				editor.putBoolean("channel_"+channel.getId(), true);
			}
		}
		editor.commit();
	}
	
	public static boolean isChannelHasNewVideo(Context context, Long channelId){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("channel_verison", Context.MODE_PRIVATE);
		return mSharedPreferences.getBoolean("channel_"+channelId, true);
	}
	
	public static void setChannelHasNewVideo(Context context, Long channelId, boolean hasNew){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("channel_verison", Context.MODE_PRIVATE);
		mSharedPreferences.edit().putBoolean("channel_"+channelId, hasNew).commit();
	}
	
	public static boolean isChannelHasNewVideo(Context context){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("channel_verison", Context.MODE_PRIVATE);
		Map<String, ?> map = mSharedPreferences.getAll();
		for(Object obj : map.values()){
			if(obj instanceof Boolean && (Boolean)obj){
				return true;
			}
		}
		return false;
	}
	
	public static void clearChannelVersionInfo(Context context){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("channel_verison", Context.MODE_PRIVATE);
		mSharedPreferences.edit().clear().commit();
	}
	/**
	 * 记录设置的语言
	 * @param language
	 * @param context
	 */
	public static void setLanguage(String language,Context context){
		SharedPreferences mSharedPreferences=context.getSharedPreferences("language",Context.MODE_PRIVATE);
		Editor mEditor=mSharedPreferences.edit();
		mEditor.putString("language", language);
		mEditor.commit();
	}
	/**
	 * 获取设置的语言
	 * @param context
	 * @return
	 */
	public static String  getLanguage(Context context){
		SharedPreferences mSharedPreferences = context.getSharedPreferences("language", Context.MODE_WORLD_READABLE);
	//	String defaultlanguage=Locale.getDefault().getLanguage().toString();
		return mSharedPreferences.getString("language", null);
	}
	/**
	 * 记录查看邀请码
	 * @param userName
	 * @param context
	 */
	public static void setSelInvitation(String userName,Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("invitation", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString(userName, userName);
		mEditor.commit();
	}
	
	/**
	 * 是否查看过邀请码
	 * @param context
	 * @param userName
	 * @return
	 */
	public static boolean isSelInvitation(Context context,String userName) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("invitation", Context.MODE_WORLD_READABLE);
		if(mSharedPreferences.getString(userName, null) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean hasNewTopic(Context context){
		SharedPreferences mSharedPreferences = getAlertSharePreferences(context);
		return mSharedPreferences.getBoolean("newTopic", false);
	}
	
	public static void setNewTopic(Context context, boolean newTopic){
		SharedPreferences mSharedPreferences = getAlertSharePreferences(context);
		mSharedPreferences.edit().putBoolean("newTopic", newTopic).commit();
	}
	/**
	 * 存储channel name
	 * @param channelName
	 * @param context
	 */
	public static void setCurrentChannel(Long channelId,Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("current_channel", Context.CONTEXT_IGNORE_SECURITY);
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putLong("channelId", channelId);
		mEditor.commit();
	}
	/**
	 * 获得channel name
	 * @param context
	 * @return
	 */
	public static Long getCurrentChannel(Context context) {
		SharedPreferences mSharedPreferences = context.getSharedPreferences("current_channel", Context.MODE_WORLD_READABLE);
		return mSharedPreferences.getLong("channelId", 0);
	}
	
}
