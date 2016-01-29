package com.star.mobile.video.service;


import android.content.Context;

import com.star.cms.model.APPInfo;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.util.Constant;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnResultListener;

public class ApplicationService extends AbstractService {

	
	public ApplicationService(Context context) {
		super(context);
	}

//	public APPInfo getDetailAppInfo(int versionCode) {
//		try{
//			String json = IOUtil.httpGetToJSON(Constant.getDetailAppInfo(versionCode));
//			if(json != null) {
//				return JSONUtil.getFromJSON(json, new TypeToken<APPInfo>(){}.getType());
//			}
//		} catch (Exception e) {
//			Log.e("TAG", "Get appInfo error", e);
//		}
//		return null;
//	}
	public void getDetailAppInfo(int versionCode,OnResultListener<APPInfo> listener){
		doGet(Constant.getDetailAppInfo(versionCode), APPInfo.class, LoadMode.NET, listener);
	}
	/**
	 * 检测版本是否激活
	 * @param versionCode
	 * @return
	 */
//	public APPInfo getMyApp(int versionCode) {
//		try{
//			String json = IOUtil.httpGetToJSON(Constant.getMyApp(versionCode));
//			if(json != null) {
//				return JSONUtil.getFromJSON(json, new TypeToken<APPInfo>(){}.getType());
//			}
//		} catch (Exception e) {
//			Log.e("TAG", "Get appInfo error", e);
//		}
//		return null;
//	}
	public void getMyApp(int versionCode,OnResultListener<APPInfo> listener){
		doGet(Constant.getMyApp(versionCode), APPInfo.class, LoadMode.NET, listener);
	}
//	public APPInfo getAppNewest() {
//		try{
//			String json = IOUtil.httpGetToJSON(Constant.getAppNewest());
//			if(json != null) {
//				return JSONUtil.getFromJSON(json, new TypeToken<APPInfo>(){}.getType());
//			}
//		} catch (Exception e) {
//			Log.e("TAG", "Get appInfo error", e);
//		}
//		return null;
//	}
	public void getAppNewest(OnResultListener<APPInfo> listener){
		doGet(Constant.getAppNewest(), APPInfo.class, LoadMode.NET, listener);
	}
}
