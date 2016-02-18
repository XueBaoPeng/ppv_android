package com.star.mobile.video.base;

import android.os.Bundle;
import android.util.SparseArray;

import com.star.mobile.video.StarApplication;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.util.app.GA;
import com.star.util.loader.AsyncTaskHolder;

public class FragmentActivity extends android.support.v4.app.FragmentActivity {

	private SparseArray<String> activitys;
	private long exitTime = 0;
	public static int flag = 100; //无实际意义，只是为了使用SparseArray类
	private boolean backAlreadyPressed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoadingDataTask.cancelExistedTasks();
		LoadingDataTask.cancelExistedTimers();
		AsyncTaskHolder.getInstance(this).clearAsyncTask();
		if(getApplication() instanceof StarApplication) { // 将activity添加到自定义界面集合
			((StarApplication) getApplication()).addActivity(this);
			activitys = ((StarApplication) getApplication()).getUIS();
		}
	}
	
	@Override
	protected void onStart() {
		GA.sendScreen(getScreenName());
		super.onStart();
	}
	
	protected String getScreenName(){
		return this.getClass().getSimpleName();
	}
	
	@Override
	protected void onDestroy() {
		AsyncTaskHolder.getInstance(this).clearAsyncTask();
		if (getApplication() instanceof StarApplication) {
			((StarApplication) getApplication()).finishActivity(this);
		}
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		/**
		 * 连续执行会出现下面的异常
		 * java.lang.NullPointerException at android.app.FragmentManagerImpl.popBackStackImmediate(FragmentManager.java:495)
		 */
		if(backAlreadyPressed){
			return;
		}
		backAlreadyPressed = true;

		if(!(this instanceof HomeActivity)){
			CommonUtil.finishActivityAnimation(this);
		}
		super.onBackPressed();
	}
	
//	public void addFirstToast() {
//		activitys.put(flag, getScreenName());
//		flag++;
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//			if ((System.currentTimeMillis() - exitTime) > 2000 && (activitys.indexOfValue(getScreenName()) != -1)) {
//				ToastUtil.centerShowToast(this, getString(R.string.quit_again_click));
//				exitTime = System.currentTimeMillis();
//			} else if ((activitys.indexOfValue(getScreenName()) != -1)) {
//				if (getApplication() instanceof StarApplication) {
//					((StarApplication) getApplication()).exit();
//				}
//			} else {
//				if (getApplication() instanceof StarApplication) {
//					((StarApplication) getApplication()).finishActivity(this);
//				}
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}
}
