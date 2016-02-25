package com.star.mobile.video.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.google.gson.reflect.TypeToken;
import com.star.cms.model.APPInfo;
import com.star.cms.model.Channel;
import com.star.cms.model.ChatroomProperty;
import com.star.cms.model.Package;
import com.star.cms.model.dto.SYNCResult;
import com.star.cms.model.dto.SyncAppStatus;
import com.star.cms.model.enm.TVPlatForm;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.ProgramVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.activity.AlertInstallActivity;
import com.star.mobile.video.activity.GooglePlayActivity;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.chatroom.faq.FAQService;
import com.star.mobile.video.dao.db.DBHelper;
import com.star.mobile.video.shared.AppInfoSharedUtil;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.shared.TaskSharedUtil;
import com.star.mobile.video.smartcard.SmartCardSharedPre;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.DownloadUtil;
import com.star.mobile.video.util.IOUtil;
import com.star.util.Logger;
import com.star.util.json.JSONUtil;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnResultListener;
import com.star.util.monitor.NETSpeedTest;

public class SyncService extends AbstractService{
	private ChannelService channelService;
	private PackageService packageService;
	private CategoryService categoryService;
	private ProgramService programService;
	private UserService userService;
	private SharedPreferences mSharePre;
	private DBHelper dbHelper;
	private static SyncService syncService;
	private boolean dbReady = false;
	private boolean loading = false;
	private FAQService faqService;

	private SyncService(Context context){
		super(context);
		mSharePre = SharedPreferencesUtil.getDBSharePreferences(context);
		categoryService = new CategoryService(context);
		packageService = new PackageService(context);
		channelService = new ChannelService(context);
		programService = new ProgramService(context);
		faqService = new FAQService(context);
		userService = new UserService();
		dbHelper = DBHelper.getInstence(context);
		dbReady = mSharePre.getBoolean("isInit", false);
	}
	
	public static SyncService getInstance(Context context){
		if(syncService == null)
			syncService = new SyncService(context);
		return syncService;
	}
	
	public boolean isDBReady(){
		return dbReady;
	}
	
	public void setDBReady(Boolean status){
		Logger.d(!status?"NEED INIT DATA":"");
		dbReady = status;
		dbHelper.setDbReady(dbReady);
		mSharePre.edit().putBoolean("isInit", status).commit();
	}
	
	public boolean isLoading(){
		return loading;
	}
	
	public void setLoading(boolean status){
		this.loading = status;
	}
	
	public void doResetStatus(String username, Long areaid){
		String name = mSharePre.getString("username", "");
		Long id = mSharePre.getLong("areaid", -1);
		if(!name.equals(username) || !id.equals(areaid)){
			clearData();
			setDBReady(false);
			mSharePre.edit().putBoolean("isInit", dbReady).commit();
			EggAppearService.delEggCacheJson(context);
			SharedPreferencesUtil.clearGuideInfo(context);
			clearLocalDate(context);
			SharedPreferencesUtil.clearChannelVersionInfo(context);
			SharedPreferencesUtil.getChatRoomSharePreferences(context).edit().clear().commit();
			new SmartCardSharedPre(context).clear();
		}
		if(!name.equals(username)){
			Logger.d("username diff.");
			mSharePre.edit().putString("username", username).commit();
//			SharedPreferencesUtil.getGuideSharePreferencesByUser(context).edit().clear().commit();
//			SharedPreferencesUtil.getGuideSharePreferencesByAppVersion(context).edit().clear().commit();
			userService.delCachedAllSmartCardNo();
//			TaskSharedUtil.setFirstTaskActivity(context, false);
		}
		if(!id.equals(areaid)&&areaid!=null){
			Logger.d("areaid changed.");
			mSharePre.edit().putLong("areaid", areaid).commit();
		}
	}
	
	private void clearData(){
		programService.removeAllPrograms();
		packageService.clearData();
		categoryService.clearData();
		channelService.clearData();
	}
	
	public boolean needInit(){
//		if(FunctionService.doHideFuncation(FunctionType.SimpleVersion))
//			dbReady = true;
		Logger.d("if need init?"+(!dbReady || !dbHelper.isDbReady()));
		if(!dbReady || !dbHelper.isDbReady()){
			return true;
		}
		return false;
	}
	
	public void doInit(){
		if(!dbReady || !dbHelper.isDbReady())
			context.startService(new Intent(context, InitService.class));
	}
	
	public void doSync(){
		if(needSync())
			new Thread(syncDB).start();
		//网络测速
		new NETSpeedTest(context,context.getString(R.string.apk_url)).asychStart();
	}
	
	private Runnable syncDB = new Runnable() {
		@Override
		public void run() {
			try{
				synchronized (SyncService.this) {
//					syncPacakge();
					long start = System.currentTimeMillis();
					List<ChannelVO> channels = channelService.getNeedSyncChannels();
					List<ProgramVO> programs = programService.getNeedSyncPrograms();
					List<Long> ids = channelService.getLocalEpgOfChannelIDs();
					List<Long> chnIds = new ArrayList<Long>();
					for(ChannelVO c : channels){
						if(!c.isFav()){
							chnIds.add(Long.parseLong("-"+c.getId()));
						}else{
							chnIds.add(c.getId());
						}
					}
					List<Long> epgIds = new ArrayList<Long>();
					List<Long> needChnIds = new ArrayList<Long>();
					for(ProgramVO p : programs){
						if(!needChnIds.contains(p.getChannelId()))
							needChnIds.add(p.getChannelId());
						if(!p.isIsFav()){
							epgIds.add(Long.parseLong("-"+p.getId()));
						}else{
							epgIds.add(p.getId());
						}
					}
					for(Long id : ids){
						if(!needChnIds.contains(id))
							needChnIds.add(id);
					}
					Map<String, Object> params = new HashMap<String, Object>();
					if(needChnIds.size()>0)
						params.put("needProgramChannels", needChnIds);
					if(chnIds.size()>0)
						params.put("favChannels", chnIds);
					if(epgIds.size()>0)
						params.put("favPrograms", epgIds);
					String json = IOUtil.httpPostToJSON(params, Constant.getSyncUrl());
					List<ChannelVO> channelVOs = JSONUtil.getFromJSON(json, new TypeToken<ArrayList<ChannelVO>>() {}.getType());
					if(channelVOs != null){
						dbHelper.beginTransaction();
						for(ChannelVO co : channelVOs){
							channelService.updateChannel(co, true);
							channelService.updateEpgStatus(co, co.isHasEPG());
							List<ProgramVO> programVOs = co.getPrograms();
							if(programVOs!=null && programVOs.size()>0){
								for(ProgramVO po : programVOs){
									programService.updateFavStatus(po, true);
								}
							}
						}
						dbHelper.commit();
					}
					Logger.d("response json:"+json);
					long end = System.currentTimeMillis();
					Logger.d("sync channel,program data, spend "+(end-start)/1000+"seconds.");
					persistSyncDate();
				}
			}catch (Exception e) {
				Logger.e("sync init error!", e);
			}
		}
	};
	
	private boolean needSync(){
		long time = mSharePre.getLong(Constant.LAST_SYNC_TIME, 0);
		long now = System.currentTimeMillis();
		if(DateFormat.getDiffDays(new Date(time), new Date(now)) >= Constant.UPDATE_INTERVAL){
			return true;
		}
		return false;
	}

	protected void syncPacakge() {
		List<Package> ps = packageService.getPackages(null);
		if(ps!=null && ps.size()>0){
			Package p = ps.get(0);
			if(p.getType()==0){
				packageService.initPackages();
			}
		}
	}
	
	private void persistSyncDate(){
		Editor editor = mSharePre.edit();
		editor.putLong(Constant.LAST_SYNC_TIME, System.currentTimeMillis());
		editor.commit();
	}
	
	public void StopGoGooglePlayStatus(){
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("version", ApplicationUtil.getAppVerison(context));
			IOUtil.httpPostToJSON(params, Constant.getServerIp()+"/googleplay");
		} catch (Exception e) {
			Logger.e("StopGoGooglePlayStatus error!", e);
		}
	}
	
	public void clearLocalDate(Context context){
		IOUtil.delCachedJSON(Constant.getChatRoomsUrl(StarApplication.CURRENT_VERSION));
		IOUtil.delCachedJSON(Constant.getRecommendUrl(context));
		IOUtil.delCachedJSON(Constant.shareUserEggUrl());
		IOUtil.delCachedJSON(Constant.getVideoChannelUrl()+"?index="+0+"&count="+10);
		IOUtil.delCachedJSON(Constant.getReportQuestionUrl(String.valueOf(ApplicationUtil.getAppVerison(context))));
		IOUtil.delCachedJSON(Constant.getOrderListUrl()+"?index="+0+"&count="+10);
	}
	
	public void sync_(){
		String url = Constant.getSyncUrl_nw()+"?channelType=1&channelType=2&channelType=0&version="+ApplicationUtil.getAppVerison(context)+"&versionType="+StarApplication.CURRENT_VERSION+"&faqVersion="+faqService.getFAQVersion();
		doGet(url, SYNCResult.class, LoadMode.NET, new OnResultListener<SYNCResult>() {

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onSuccess(SYNCResult result) {
				if(result!=null){
					//频道上是否添加新视频
					List<Channel> chns = result.getVersions();
					if(chns != null && chns.size()>0){
						SharedPreferencesUtil.setChannelVersion(context, chns);
					}
					//四格报告是否可做
					APPInfo appInfo = result.getAppInfo();
					if(appInfo!=null){
						if(appInfo.isQuestionActive()) {
							SharedPreferencesUtil.setAppQuestionActive(context);
						}
					}
					//是否有coins可领
					Integer coins = result.getCoins();
					TaskSharedUtil.setCoinsStatus(context, false);
					if(coins!=null&&coins>0){
						TaskSharedUtil.setCoinsStatus(context, true);
					}
					//是否有coupons
					Integer coupons = result.getCoupons();
					TaskSharedUtil.setCouponsStatus(context, false);
					if(coupons!=null&&coupons>0){
						TaskSharedUtil.setCouponsStatus(context, true);
					}
					//新版本
					APPInfo newApp = result.getNewAppInfo();
					if(newApp!=null && newApp.getVersion()>ApplicationUtil.getAppVerison(context)){
						SharedPreferencesUtil.setNewVersion(context,newApp.getVersion());
						SharedPreferencesUtil.setNewVersionSize(context,newApp.getApkSize());
						AppInfoSharedUtil.setNewVersionApkUrl(context, newApp.getApkUrl());
						//提示升级 alert 避免一天多次提醒
						long dayTime = 24*60*60*1000;
//						long dayTime = 5*60*1000;
						if(System.currentTimeMillis() - AppInfoSharedUtil.getNewVersionAlert(context) > dayTime) {
							AppInfoSharedUtil.setNewVersionAlert(context);
							if(DownloadUtil.needDownload(context, newApp.getVersion())) {
								alertUpdate(context, newApp);
							} else {
								alertInstall(context, newApp);
							}
						}
						
						
					}
					//chatroom setting
					ChatroomProperty cp = result.getCrProperty();
					if(cp != null) {
						SharedPreferencesUtil.setSoundChatAlertAll(context,cp.isSoundable());
						SharedPreferencesUtil.setVibrationChatAlertAll(context,cp.isVibrateable());
						SharedPreferencesUtil.setSettingChatRoomIds(context,cp.getUnAlertChatRIds());
					}
					
					if(result.isGoGooglePlay()!=null&&result.isGoGooglePlay()){
//						Log.i(TAG, "need to comment on Google play. wait.");
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								if(!ApplicationUtil.isApplicationInBackground(context)){
									context.startActivity(new Intent(context, GooglePlayActivity.class));
								}
							}
						}, 1000*60*10);
					}
					if(result.getFaqProperty()!=null){
						faqService.parserAndSaveFaqProperty(result.getFaqProperty());
					}
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void syncStatus(OnResultListener<SyncAppStatus> onResultListener){
		doGet(Constant.getSyncAppStatusUrl(), SyncAppStatus.class, LoadMode.NET, onResultListener);
	}
	
	private void alertUpdate(Context context, APPInfo appinfo) {
		if(!StarApplication.nowDownLoadNewVersion){
			Intent i = new Intent(context, AlertInstallActivity.class);
			i.putExtra("what", "update");
			i.putExtra("updateInfo", appinfo.getUpdateInfo());
			i.putExtra("forceUp", appinfo.isForceUpdate());
			context.startActivity(i);
		}
	}
	
	private void alertInstall(Context context, APPInfo appinfo) {
		Intent i = new Intent(context, AlertInstallActivity.class);
		i.putExtra("what", "install");
		i.putExtra("forceUp", appinfo.isForceUpdate());
		context.startActivity(i);
	}
}
