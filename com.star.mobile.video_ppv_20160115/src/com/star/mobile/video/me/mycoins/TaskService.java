package com.star.mobile.video.me.mycoins;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.star.cms.model.dto.DoTaskResult;
import com.star.cms.model.vo.TaskVO;
import com.star.mobile.video.base.AbstractService;
import com.star.mobile.video.service.UserService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.Constant;
import com.star.util.loader.LoadMode;
import com.star.util.loader.OnListResultListener;
import com.star.util.loader.OnResultListener;

public class TaskService extends AbstractService{

	public TaskService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void getTasks(int versionCode, OnListResultListener<TaskVO> listener) {
		doGet(Constant.getTasksUrl(versionCode), TaskVO.class, LoadMode.NET, listener);
	}
	
	public void doTask(String code, int appViseroin) {
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("taskCode", code);
		params.put("appVersion", appViseroin);
		doPost(Constant.getTaskUrl(), DoTaskResult.class, params, new OnResultListener<DoTaskResult>() {

			@Override
			public boolean onIntercept() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onFailure(int errorCode, String msg) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(DoTaskResult value) {
				showTaskDialog(context, value);
			}
		});
	}
	
	public void doTask(String code, int appViseroin, OnResultListener<DoTaskResult> listener){
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("taskCode", code);
		params.put("appVersion", appViseroin);
		doPost(Constant.getTaskUrl(), DoTaskResult.class, params, listener);
	}
	
	public void showTaskDialog(Context context, DoTaskResult result, PromptDialogClickListener listener){
		if(result!=null&&result.isAcceptCoin()&&result.getCoins()>0){
			CommonUtil.getInstance().showDoTaskDialog(context, result.getTask().getName(), result.getCoins(), listener);
			new UserService().updateCoins(context, result.getCoins());
		}
	}
	
	public void showTaskDialog(Context context, DoTaskResult result){
		showTaskDialog(context, result, null);
	}
	
	public void getGold(long taskId, int appVersion, OnResultListener<Boolean> listener) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appVersion", appVersion);
		doPost(Constant.getGoldUrl(taskId), Boolean.class, params, listener);
	}
}
