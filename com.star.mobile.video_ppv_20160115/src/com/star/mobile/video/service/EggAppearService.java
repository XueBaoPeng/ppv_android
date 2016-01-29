package com.star.mobile.video.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.star.cms.model.Egg;
import com.star.cms.model.User;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.TranslucentBackgroundActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DefaultLoadingTask;
import com.star.mobile.video.util.IOUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.util.ShareUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.InternalStorage;

public class EggAppearService {

	public static int Home_first_in = 0;
	
	public static int Program_book = 1;
	public static int Chatroom_chat = 1;
	public static int Coupons = 1;
	
	public static int Program_in = 2;
	public static int Chatroom_in = 2;
	public static int Task = 2;
	public static int Profile = 2;
	
	public static int ChannelGuide = 3;
	public static int OnAir = 3;
	
	public static int AccountManager = 4;
	public static int Alert_list = 4;
	public static int SpendCoins = 4;
	public static int Search = 4;
	public static int Feedback = 4;
	public static int VersionInfo = 4;
	public static int Comments_praise = 4;
	
	public static int Chatroom_list = 5;
	public static int Comments_in = 5;
	public static int Home_in = 5;
	
	public static int Comments_commit = 6;
	
	private static Map<Integer, Integer> ratMap = new HashMap<Integer, Integer>();
	
	static{
		ratMap.put(0, 100);
	}
	
	public final static String OFFICIAL_MAIL ="startenbre@gmail.com"; //生产彩蛋官方邮箱
	
	public static void appearEgg(final Context context, int level){
		Integer operate = ratMap.get(level);
		if(operate == null)
			return;
		Random random = new Random();
		int rate = random.nextInt(100);
		Log.d("EggAppearService", "random a rate, is "+rate+". and the operation's probability is "+operate);
		if(rate < operate){
			final String userName = SharedPreferencesUtil.getUserName(context);
			if(userName != null) {
				new DefaultLoadingTask() {
					Egg egg;
					@Override
					public void onPostExecute() {
						if(egg != null) {
							User user = StarApplication.mUser;// 空指针异常
							if(user != null) {
								String username = user.getUserName();
								Intent i = new Intent(context, TranslucentBackgroundActivity.class);
								if(user.getUserName().startsWith(User.PrefixOfUsr3Party)){
									try{
										username = username.split("#")[1];
									}catch(Exception e){
										
									}
								}
								i.putExtra("eggCode", egg.getCode());
								i.putExtra("userName", username);
								i.putExtra("userType", user.getType().getNum());
								context.startActivity(i);
							} else {
								ToastUtil.centerShowToast(context, context.getString(R.string.error_network));
							}
						}
					}
					
					@Override
					public void doInBackground() {
//						egg = shareSysEgg();
					}
				}.execute();
			} else {
				Log.v("TAG", "devices id login");
			}
		}
	}
	
	public static boolean delEggCacheJson(Context context) {
		return InternalStorage.getStorage(context).delJsonFile(Constant.shareUserEggUrl());
	}
	
	public static void shareNewExchange(final Context context, final long exchangeId) {
		new LoadingDataTask() {
			private String shareUrl;
			@Override
			public void onPreExecute() {
				CommonUtil.showProgressDialog(context);
			}
			
			@Override
			public void onPostExecute() {
				CommonUtil.closeProgressDialog();
				if(!TextUtils.isEmpty(shareUrl)){
					String info = context.getString(R.string.share_egg_newCus);
					ShareUtil.shareEgg(context, info,shareUrl,ShareUtil.COUPLE_EXCHANGE_EXAMPLES,0.0);
				}
			}
			@Override
			public void doInBackground() {
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("exchangeId",exchangeId);
				try {
					shareUrl = IOUtil.httpPostToJSON(params, Constant.shareExchangeUrl(exchangeId));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}
	
	
	public static void getAppearProbabilities(){
//		getProbabilitiesFromlocal();
		new AsyncTask<Void, Void, Void>(){
			private String json;
			@Override
			protected Void doInBackground(Void... params) {
				try{
					json = IOUtil.httpGetToJSON(Constant.getEggProbabilityUrl(), true);
				}catch (Exception e) {
					Log.e("EggAppearService", "get probabilities error or cannot break today yet.");
				}
				return null;
			}
			
			protected void onPostExecute(Void result) {
				if(!TextUtils.isEmpty(json)){
//					Log.d("EggAppearService", "Probability json from server: "+json);
					parserJson(json, false);
				}else{
					Log.e("EggAppearService", "cannot break today yet.");
				}
			};
		}.execute();
	}
	
	private static void getProbabilitiesFromlocal(){
		new LoadingDataTask() {
			private String json;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if(!TextUtils.isEmpty(json)){
					parserJson(json, true);
				}
			}
			@Override
			public void doInBackground() {
				json = IOUtil.getCachedJSON(Constant.getEggProbabilityUrl());
			}
		}.execute();
	}

	private static void parserJson(String json, boolean local) {
		try{
			String[] keyValue = json.split("&");
			for(String s : keyValue){
				Integer key = Integer.parseInt(s.split("=")[0]);
				Integer value = Integer.parseInt(s.split("=")[1]);
				ratMap.put(key, value);;
			}
			Log.d("EggAppearService", (local?"LOCAL:":"SERVER:")+"json"+json);
		}catch (Exception e) {
			Log.e("EggAppearService", "parser error!", e);
		}
	}
}
