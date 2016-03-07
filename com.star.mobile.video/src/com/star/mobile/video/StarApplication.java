package com.star.mobile.video;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.star.cms.model.User;
import com.star.cms.model.vo.TaskVO;
import com.star.mobile.video.account.ChooseAreaActivity;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.service.FunctionService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.http.HTTPClient;
import com.star.util.DifferentUrlContral;
import com.star.util.ServerUrlDao;
import com.star.util.app.Application;
import com.star.util.http.IOUtil;

public class StarApplication extends Application {

	public static ChooseAreaActivity mChooseAreaActivity;
	public static User mUser;
	public static List<Long> mChannelIdOfChatRoom = new ArrayList<Long>();
	public static List<TaskVO> mUndoTask = new ArrayList<TaskVO>();
	public volatile static List<FunctionType> mAreaFunctions = new ArrayList<FunctionType>();
	public static boolean nowDownLoadNewVersion = false;
	public static Context context;
	private SparseArray<String> UIS = new SparseArray<String>(); //需要退出提示的activity集合
	private SparseArray<Activity> activitys = new SparseArray<Activity>(); //界面集合
	private int SparseArray_flag = 1;
	/**当前版本*/
	public static int CURRENT_VERSION ;

	@Override
	public void onCreate() {
		super.onCreate();
		IOUtil.context = getApplicationContext();
		context = getApplicationContext();
		CURRENT_VERSION = Integer.parseInt(getString(R.string.app_current_version));
//		Fresco.initialize(this);
//		SSLUtil.disableCertificateValidation();
		HTTPClient.instance.setContext(getApplicationContext());
		ServerUrlDao serverUrlDao = DifferentUrlContral.diffUrlContral(this);
		if(Constant.getServerIp() == null || "".equals(Constant.getServerIp())) {
//			Constant.setServerIP(getResources().getString(R.string.server_url));
			Constant.setServerIP(serverUrlDao.getServerUrl());
//			Constant.setBbsNewTopicUrl(getResources().getString(R.string.bbs_new_topic_url));
			Constant.setBbsNewTopicUrl(serverUrlDao.getBBSNewTopicUrl());
		}
		String areaName = SharedPreferencesUtil.getAreaname(getApplicationContext());
		if(!TextUtils.isEmpty(areaName))
			FunctionService.initAreaFunctions(getApplicationContext(), areaName);
		CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(getApplicationContext(),this);

	}
	public SparseArray<String> getUIS(){
		return UIS;
	}
	
	/**
	 * 添加Activity到自定义回退栈中
	 * @param activity
	 */
	public void addActivity(Activity activity){
		if(activitys.indexOfValue(activity) == -1)
			activitys.put(SparseArray_flag, activity);
		else{
			activitys.remove(activitys.keyAt(activitys.indexOfValue(activity)));
			activitys.put(SparseArray_flag, activity);
		}
		SparseArray_flag++;
	}
	
	/**
	 * 退出该应用程序
	 */
	public void exit(){
		for(int i = 0 ; i < activitys.size() ; i++){
			Activity activity = activitys.valueAt(i);
			activity.finish();
		}
		UIS.clear();
		activitys.clear();
		SparseArray_flag = 1;
	}
	
	/**
	 * 清除除参数外其他activity
	 */
	public void finishAllExceptOne(Class<? extends Activity> clazz){
		Activity a = null;
		for(int i = 0 ; i < activitys.size() ; i++){
			Activity activity = activitys.valueAt(i);
			if(clazz.equals(activity.getClass())){
				a = activity;
				break;
			}
			activity.finish();
		}
		UIS.clear();
		activitys.clear();
		SparseArray_flag = 1;
		if(a != null)
			addActivity(a);
	}
	/**
	 * 清除除参数外其他activity
	 */
	public void finishActivityBClazz(Class<? extends Activity> clazz){
		int a=-1;
		for(int i = 0 ; i < activitys.size() ; i++){
			Activity activity = activitys.valueAt(i);
			if(clazz.equals(activity.getClass())){
				activity.finish();
				a = i;
				break;
			}
		}
		if(a !=-1)
			activitys.remove(a);
	}
	
	/**
	 * 从自定义回退栈中删除activity并执行finish()方法
	 * @param activity
	 */
	public void finishActivity(Activity activity){
		int index = activitys.indexOfValue(activity);
		if(index!=-1)
			activitys.remove(activitys.keyAt(index));
	}
	
	public SparseArray<Activity> getActivitys(){
		return activitys;
	}
}
