package com.star.mobile.video.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.star.cms.model.APPInfo;
import com.star.cms.model.dto.LogonResult;
import com.star.cms.model.dto.LogonResult.LogonStatus;
import com.star.mobile.video.R;
import com.star.mobile.video.account.AccountService;
import com.star.mobile.video.account.ChooseAreaActivity;
import com.star.mobile.video.account.LoginActivity;
import com.star.mobile.video.appversion.AppInfoCacheService;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.guide.firstenter.WelcomeGuideView;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.service.ApplicationService;
import com.star.mobile.video.service.SyncService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.ABTestSharedPre;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.ConnectServiceUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.IOUtil;
import com.star.mobile.video.util.LanguageUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.PosterContainer;
import com.star.ui.ImageView;
import com.star.util.Logger;
import com.star.util.app.GA;
import com.star.util.loader.OnResultListener;

public class WelcomeActivity extends BaseActivity {
	private AccountService accountService;
	private ApplicationService applicationService;
	private String deviceId;
	private String userNameInCache = null;
	private ABTestSharedPre abSharePre;
	private Long startTime;
	private RelativeLayout mPosterContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_home);
		abSharePre = new ABTestSharedPre(this);
		initConfig();
		initView();
		login();
		if(abSharePre.getABTest().equals("A")){
			GA.sendCustomDimension(4, "A");
		}else if(abSharePre.getABTest().equals("B")){
			GA.sendCustomDimension(4, "B");
		}else if(abSharePre.getABTest().equals("")){
			loadAorB();
		}
	}
	private void loadAorB(){
		Random r = new Random();
		int num = r.nextInt(2) + 1;
		if(num == 1) {
			abSharePre.setABTest("A");
			GA.sendCustomDimension(4, "A");
		} else if(num == 2) {
			abSharePre.setABTest("B");
			GA.sendCustomDimension(4, "B");
		}
	}
	private void initConfig() {
		LanguageUtil.switchLanguage(this,SharedPreferencesUtil.getLanguage(this));
		ConnectServiceUtil.CONTEXT = this;
		ConnectServiceUtil.checkConnectStatus();
		CommonUtil.showHashKey(this);
		PushManager.getInstance().initialize(this.getApplicationContext());
		Constant.setServerIP(getResources().getString(R.string.server_url));
		
		TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE); 
		deviceId = tm.getDeviceId();
		accountService = new AccountService(this);
		applicationService = new ApplicationService(this);
		userNameInCache = SharedPreferencesUtil.getUserName(this);
		startTime = System.currentTimeMillis();
		String token = SharedPreferencesUtil.getToken(this);
		IOUtil.setTOKEN(token);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	
	private void initView() {
		TextView tv_version = (TextView) findViewById(R.id.tv_app_version);
		tv_version.setText("Version "+ApplicationUtil.getAppVerisonName(this));
		setAppPoster();
		mPosterContainer = ((RelativeLayout)findViewById(R.id.vg_poster_container));
		if(!SharedPreferencesUtil.isAppGuideDone(this)){
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				WelcomeGuideView container = new WelcomeGuideView(this);
//				mPosterContainer.addView(container);
//			}else {
				PosterContainer container = new PosterContainer(this);
				mPosterContainer.addView(container);
//			}
		}
	}

	private void setAppPoster() {
		ImageView ivPoster = (ImageView) findViewById(R.id.iv_app_poster);
		AppInfoCacheService cacheService = new AppInfoCacheService(this);
		String posterUrl = cacheService.getAppPoster(ApplicationUtil.getAppVerison(this));
		if(posterUrl!=null&&!posterUrl.equals("")){
			ivPoster.setUrl(posterUrl);
		}else{
			ivPoster.setImageResource(R.drawable.welcome_bg);
		}
	}

	private void hideWelcomeView()  {
		long nowTime = System.currentTimeMillis();
		long diff = nowTime - startTime; 
		if(diff < 2000) {
			try {
				Thread.sleep(2000 - diff);
			} catch (InterruptedException e) {
				
			}
		}
		if(SharedPreferencesUtil.isAppGuideDone(this)){
			goChooseActivity();// 1.5.2 直接跳转到loging 页面
		} else {
			showAppGuidePoster();
		}
	}
	
	private void showAppGuidePoster(){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mPosterContainer.setVisibility(View.VISIBLE);
			}
		}, 600);
	}
	
	private void getMyAppInfo() {
//		new LoadingDataTask() {
//			private APPInfo info;
//			@Override
//			public void onPreExecute() {
//			}
//			
//			@Override
//			public void onPostExecute() {
//				SharedPreferences sharePre = SharedPreferencesUtil.getDBSharePreferences(WelcomeActivity.this);
//				boolean hasClear = sharePre.getBoolean("hasClear", false);
//				if(ApplicationUtil.getAppVerisonName(WelcomeActivity.this).equals("1.2.8")&&!hasClear){
//					Editor editor = sharePre.edit();
//					editor.putBoolean("hasClear", true);
//					editor.putInt("dataVersion", -1).commit();
//				}
//				if(info!=null && info.getDataVersion()!=sharePre.getInt("dataVersion", -1)){
//					sharePre.edit().putInt("dataVersion", info.getDataVersion()).commit();
//					SyncService.getInstance(WelcomeActivity.this).setDBReady(false);
//				}
//				boolean clear = sharePre.getBoolean("hasClear_"+ApplicationUtil.getAppVerison(WelcomeActivity.this), false);
//				if(ApplicationUtil.getAppVerisonName(WelcomeActivity.this).equals("1.3.5")&&!clear){
//					SyncService.getInstance(WelcomeActivity.this).setDBReady(false);
//				}
//			}
//			
//			@Override
//			public void doInBackground() {
//				info =  applicationService.getMyApp(ApplicationUtil.getAppVerison(WelcomeActivity.this));
//			}
//		}.execute();
		applicationService.getMyApp(ApplicationUtil.getAppVerison(WelcomeActivity.this),new OnResultListener<APPInfo>() {

			@Override
			public boolean onIntercept() {
				return false;
			}

			@Override
			public void onSuccess(APPInfo info) {
				SharedPreferences sharePre = SharedPreferencesUtil.getDBSharePreferences(WelcomeActivity.this);
				boolean hasClear = sharePre.getBoolean("hasClear", false);
				if(ApplicationUtil.getAppVerisonName(WelcomeActivity.this).equals("1.2.8")&&!hasClear){
					Editor editor = sharePre.edit();
					editor.putBoolean("hasClear", true);
					editor.putInt("dataVersion", -1).commit();
				}
				if(info!=null && info.getDataVersion()!=sharePre.getInt("dataVersion", -1)){
					sharePre.edit().putInt("dataVersion", info.getDataVersion()).commit();
					SyncService.getInstance(WelcomeActivity.this).setDBReady(false);
				}
				boolean clear = sharePre.getBoolean("hasClear_"+ApplicationUtil.getAppVerison(WelcomeActivity.this), false);
				if(ApplicationUtil.getAppVerisonName(WelcomeActivity.this).equals("1.3.5")&&!clear){
					SyncService.getInstance(WelcomeActivity.this).setDBReady(false);
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				SharedPreferences sharePre = SharedPreferencesUtil.getDBSharePreferences(WelcomeActivity.this);
				boolean hasClear = sharePre.getBoolean("hasClear", false);
				if(ApplicationUtil.getAppVerisonName(WelcomeActivity.this).equals("1.2.8")&&!hasClear){
					Editor editor = sharePre.edit();
					editor.putBoolean("hasClear", true);
					editor.putInt("dataVersion", -1).commit();
				}
				boolean clear = sharePre.getBoolean("hasClear_"+ApplicationUtil.getAppVerison(WelcomeActivity.this), false);
				if(ApplicationUtil.getAppVerisonName(WelcomeActivity.this).equals("1.3.5")&&!clear){
					SyncService.getInstance(WelcomeActivity.this).setDBReady(false);
				}
			}
		});
	}
	
	private void login() {
		if (userNameInCache != null) {
			SharedPreferencesUtil.clearDeviceId(WelcomeActivity.this);
			getMyAppInfo();
//			EggAppearService.getAppearProbabilities();
			if(SharedPreferencesUtil.isAppGuideDone(WelcomeActivity.this)){
				if(SharedPreferencesUtil.getToken(this)!=null){
					if(SharedPreferencesUtil.getAreaCode(WelcomeActivity.this)==null||SyncService.getInstance(WelcomeActivity.this).needInit()){
						goChooseActivity();
					}else{
						new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								goHomeActivity();
							}
						}, 1000);
					}
				}else{
					goLoginActivity();
				}
			}else{
				showAppGuidePoster();
			}
			//彩蛋提醒服务
//			Intent service = new Intent(WelcomeActivity.this,EggAlertService.class);
//			startService(service);
		} else {
			logonWithDevice();
		}
	}
	
	private void goChooseActivity() {
		CommonUtil.startActivity(WelcomeActivity.this, ChooseAreaActivity.class);
		finish();
	}
	
	private void goHomeActivity() {
		CommonUtil.startActivity(WelcomeActivity.this, HomeActivity.class);
		finish();
	}
	
	private void goLoginActivity() {
		Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
		intent.putExtra("fill_name_password", true);
		intent.putExtra("isHideSkipBtn", true);
		CommonUtil.startActivity(WelcomeActivity.this, intent);
		finish();
	}
	
	private void logonWithDevice(){
		accountService.login(deviceId, new OnResultListener<LogonResult>() {

			@Override
			public boolean onIntercept() {
				Logger.d("Try to logon with deviceID:" + deviceId);
				return false;
			}

			@Override
			public void onSuccess(LogonResult value) {
				if(value.getStatus().equals(LogonStatus.DeviceLogon_registe)||value.getStatus().equals(LogonStatus.DeviceLogon_unregiste)){
					Logger.d("TOKEN:" + value.getToken());
					clearLoginInfo();
					Map<String, String> params = new HashMap<String, String>();
					params.put("token", value.getToken());
					params.put("deviceId", deviceId);
					SharedPreferencesUtil.saveUserInfo(WelcomeActivity.this, params);
					hideWelcomeView();
					getMyAppInfo();
				}else{
					ToastUtil.centerShowToast(WelcomeActivity.this, getString(R.string.error_network));
					needGohome();
				}
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				ToastUtil.centerShowToast(WelcomeActivity.this, getString(R.string.error_network));
				needGohome();
			}
		});
	}
	
	private void clearLoginInfo() {
		SharedPreferencesUtil.clearUserInfo(WelcomeActivity.this);
	}
	
	private void needGohome() {
		if(SyncService.getInstance(WelcomeActivity.this).isDBReady()) {
			if(SharedPreferencesUtil.isAppGuideDone(this)){
				goHomeActivity();
			}else{
				showAppGuidePoster();
			}
		} else {
			CommonUtil.showNetworkerror(WelcomeActivity.this);
		}
	}
	
	/*private void checkVerison() {
		if(newApp.getVersion() > ApplicationUtil.getAppVerison(this)) {
			newAppVersion = newApp.getDescription();
			SharedPreferencesUtil.setNewVersion(this,newApp.getVersion());
			AppInfoSharedUtil.setNewVersionApkUrl(this, newApp.getApkUrl());
			SharedPreferencesUtil.setNewVersionSize(this,newApp.getApkSize());
			if(DownloadUtil.needDownload(this, newApp.getVersion()))
				alertUpdate();
			else
				alertInstall();
		}else{
			login();
		}
	}*/
	
	/*private void alertInstall() {
		Intent i = new Intent(this, AlertInstallActivity.class);
		i.putExtra("what", "install");
		i.putExtra("forceUp", newApp.isForceUpdate());
		startActivityForResult(i, 110);
	}*/
	
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==110 && resultCode==120){
			if(!newApp.isForceUpdate()){
				login();
			}else{
				finish();
			}
		}
	}*/
	
//	private void alertUpdate() {
//		if(StarApplication.nowDownLoadNewVersion){
//			if(!newApp.isForceUpdate()){
//				login();
//			}else{
//				finish();
//			}
//		}else{
//			Intent i = new Intent(this, AlertInstallActivity.class);
//			i.putExtra("what", "update");
//			i.putExtra("updateInfo", newApp.getUpdateInfo());
//			i.putExtra("forceUp", newApp.isForceUpdate());
//			startActivityForResult(i, 110);
//		}
//	}
	
//	/**
//	 * 判断logo是否显示
//	 * @return
//	 */
//	private boolean isWelcomeViewVisibile() {
//		if(welcome_view.getVisibility() == View.GONE || welcome_view.getVisibility() == View.INVISIBLE) {
//			return true;
//		} else {
//			return false;
//		}
//	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}