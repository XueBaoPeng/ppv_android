package com.star.mobile.video.base;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import com.star.cms.model.User;
import com.star.mobile.video.ServiceHandler;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.account.LoginActivity;
import com.star.mobile.video.activity.WelcomeActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.shared.ToastSharedUtil;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.util.InternalStorage;
import com.star.util.Logger;
import com.star.util.http.IOUtil;
import com.star.util.loader.AsyncTaskHolder;
import com.star.util.loader.BitmapUploadParams;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;
/**
 * 
 * @author dujr
 *
 */
public abstract class AbstractService implements OnHttpInterface {
	protected Context context;
	public AbstractService(Context context) {
		super();
		this.context = context;
	}

	@Override
	public <T> void doGet(final String url, Class<T> clzz, LoadMode mode, final OnResultListener<T> listener) {
		AsyncTaskHolder.getInstance(context).sendGet(url, clzz, getProxyListener(url, listener), mode);
	}
	
	@Override
	public <T> void doPost(final String url, Class<T> clzz, Map<String, Object> params, final OnResultListener<T> listener) {
		AsyncTaskHolder.getInstance(context).sendPost(url, params, clzz, getProxyListener(url, listener));
	}
	
	@Override
	public <T> void doDelete(final String url, Class<T> clzz, final OnResultListener<T> listener) {
		AsyncTaskHolder.getInstance(context).sendDelete(url, clzz, getProxyListener(url, listener));
	}
	
	@Override
	public <T> void doPostImage(Class<T> clzz, List<BitmapUploadParams> bitmapParams, OnResultListener<T> listener) {
		AsyncTaskHolder.getInstance(context).postImage(bitmapParams, clzz, getProxyListener(bitmapParams.get(0).url, listener));
	}

	private <T> OnResultListener<T> getProxyListener(final String url, final OnResultListener<T> listener) {
		if(listener == null)
			return listener;
		OnResultListener<T> proxyListener;
		if(listener instanceof OnListResultListener){
			proxyListener = new OnListResultListener<T>() {

				@Override
				public boolean onIntercept() {
					//需要登录直接跳转，不会再执行后续操作
					if(needLogin(url)){
						return true;
					}
					return listener.onIntercept();
				}

				@Override
				public void onFailure(int errorCode, String msg) {
					if(isLogonValid(errorCode, url))
						listener.onFailure(errorCode, msg);
				}

				@Override
				public void onSuccess(List<T> value) {
					((OnListResultListener<T>)listener).onSuccess(value);
				}
			};
		} else {
			proxyListener = new OnResultListener<T>() {

				@Override
				public boolean onIntercept() {
					//需要登录直接跳转，不会再执行后续操作
					if(needLogin(url)){
						return true;
					}
					return listener.onIntercept();
				}

				@Override
				public void onSuccess(T value) {
					if(value instanceof User){
						StarApplication.mUser = (User) value;
					}
					listener.onSuccess(value);
				}

				@Override
				public void onFailure(int errorCode, String msg) {
					if(isLogonValid(errorCode, url))
						listener.onFailure(errorCode, msg);
				}
			};
		}
		return proxyListener;
	}
	
	/**
	 * 判断是否需要登录
	 * @param url
	 * @return
	 */
	private boolean needLogin(String url){
		if(url.contains("/cms/login")||url.contains("/cms/register")||url.contains("/cms/public")){
			return false;
		}
		if(IOUtil.getTOKEN() == null){
			IOUtil.setTOKEN(SharedPreferencesUtil.getToken(context));
			if(IOUtil.getTOKEN() == null) {
				Logger.e("The token is empty, need login!!! url is: "+url);
				ServiceHandler.getInstance().clearAllService();
				goLoingActivityOrWelcomeActivity();
				return true;
			}
		}else{
			Logger.d(IOUtil.getTOKEN());
		}
		return false;
	}
	
	/**
	 * 
	 * @param url 
	 * @param response
	 * @param url 方便调试
	 * @return
	 */
	private boolean isLogonValid(int statusCode, String url) {
		boolean result = true;
		if(statusCode == 401) {
//			Log.i("TAG", "401"+url); 
			result =  false;
			Logger.e("The token is invalid, need login!!! url is: "+url);
			ServiceHandler.getInstance().clearAllService();
			AsyncTaskHolder.getInstance(context).clearAsyncTask();
			goLoingActivityOrWelcomeActivity();
		}
		return result;
	}
	
	private void goLoingActivityOrWelcomeActivity() {
		if(Looper.myLooper() != Looper.getMainLooper())
			throw new RuntimeException("This method must run at main thread.");
		//如果app 在后台不弹出界面
		if(!ApplicationUtil.isApplicationInBackground(context)) {
			//30秒弹出一次
			long currentTime = System.currentTimeMillis(); 
			if(currentTime - ToastSharedUtil.getLastTime(context) > 30000 && SharedPreferencesUtil.getToken(context) != null) {
				ToastSharedUtil.setCurrentTime(context, currentTime);
				ToastUtil.centerShowToast(context, "Your login has been out of date. Relogining...");
			}
			SharedPreferencesUtil.clearToken(context);
			//如果已经打开了welcome或者login不要再打了
			if(ApplicationUtil.getActivityName(context).equals(WelcomeActivity.class.getSimpleName())||ApplicationUtil.getActivityName(context).equals(LoginActivity.class.getSimpleName())){
				return;
			}
			if(SharedPreferencesUtil.getUserName(context) != null && SharedPreferencesUtil.getPassword(context) != null) {
				Logger.d("go login activity.");
				//登录界面
				Intent intent = new Intent(context,LoginActivity.class);
				intent.putExtra("fill_name_password", true);
				intent.putExtra("isHideSkipBtn", true);
				if(!(context instanceof Activity))
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				if(context.getApplicationContext() instanceof StarApplication){
					((StarApplication)context.getApplicationContext()).finishAllExceptOne(LoginActivity.class);
				}
				
			} else {
				Logger.d("go welecome activity.");
				SharedPreferencesUtil.clearUserInfo(context);
				Intent intent = new Intent(context,WelcomeActivity.class);
				if(!(context instanceof Activity))
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				if(context.getApplicationContext() instanceof StarApplication){
					((StarApplication)context.getApplicationContext()).finishAllExceptOne(WelcomeActivity.class);
				}
			}
		}
	}
	
	/**
	 * 删除本地json 文件
	 * @param cachedFileName
	 * @return
	 */
	public boolean delCachedJSON(String cachedFileName) {
		return InternalStorage.getStorage(context).delJsonFile(cachedFileName);
	}
	
	public void saveCachedJSON(String cachedFileName, String json) throws IOException {
		InternalStorage.getStorage(context).save(cachedFileName, json);
	}
	
	public String getCacheJSON(String cachedFileName){
		try {
			return InternalStorage.getStorage(context).get(cachedFileName);
		} catch (IOException e) {
		}
		return null;
	}
}
