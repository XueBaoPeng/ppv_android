package com.star.mobile.video.me.mycoins;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.star.cms.model.enm.AccountType;
import com.star.cms.model.vo.TaskVO;
import com.star.mobile.video.R;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.model.FunctionType;
import com.star.mobile.video.util.ApplicationUtil;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.NoScrollListView;
import com.star.util.loader.OnListResultListener;

public class TaskView extends RelativeLayout {

	private List<TaskVO> canDoTodayTasks = new ArrayList<TaskVO>();
	private List<TaskVO> canntDoTodayTask = new ArrayList<TaskVO>();
	private TaskService taskService;
	private NoScrollListView lvTasks;
	private TaskListAdapter adapter;
	private View loadingView;
	
	public TaskView(Context context) {
		super(context);
		initView();
	}

	public TaskView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		taskService = new TaskService(getContext());
		LayoutInflater.from(getContext()).inflate(R.layout.view_task_list, this);
		lvTasks = (NoScrollListView) findViewById(R.id.lv_tasks);
		loadingView = findViewById(R.id.loadingView);
	}
	
	public void getTasks() {
		taskService.getTasks(ApplicationUtil.getAppVerison(getContext()), new OnListResultListener<TaskVO>() {
			
			@Override
			public boolean onIntercept() {
				loadingView.setVisibility(View.VISIBLE);
				return false;
			}
			
			@Override
			public void onFailure(int errorCode, String msg) {
				ToastUtil.centerShowToast(getContext(), getContext().getString(R.string.error_network));
			}
			
			@Override
			public void onSuccess(List<TaskVO> datas) {
				loadingView.setVisibility(View.GONE);
				canntDoTodayTask.clear();
				canDoTodayTasks.clear();
				StarApplication.mUndoTask.clear();
				if(datas != null) {
					for (FunctionType type : StarApplication.mAreaFunctions) {
						for(String code : type.getTaskCodes()){
							Iterator<TaskVO> it = datas.iterator();
							while (it.hasNext()) {
								TaskVO task = it.next();
								if(task.getCode().equals(code)) {
									it.remove();
									break;
								}
							}
						}
					}
					Log.i("TAG", "Size"+datas.size());
					for(TaskVO t :datas) {
						//手机号登录用户没有关联账户任务
						if(StarApplication.mUser != null && StarApplication.mUser.getType() == AccountType.PhoneNumber) {
							if(t.getCode().equals(com.star.cms.model.Task.TaskCode.Link_ThirdAccount)) {
								continue;
							}
						}
						if(t.getTimesToday() < t.getTimesLimitToday() && t.getTimesLimitToday() > 0 && t.getCoins() <= 0) {
							//未做的任务
							canDoTodayTasks.add(t);
							StarApplication.mUndoTask.add(t);
						}else{
							canntDoTodayTask.add(t);
						}
					}
					List<TaskVO> tasks = new ArrayList<TaskVO>();
					tasks.addAll(canDoTodayTasks);
					tasks.addAll(canntDoTodayTask);
					if(adapter == null){
						adapter = new TaskListAdapter(getContext(), tasks);
						lvTasks.setAdapter(adapter);
					}else{
						adapter.updataDataRefreshUi(tasks);
					}
				} else {
					ToastUtil.centerShowToast(getContext(), getContext().getString(R.string.error_network));
				}
			}
		});
	}

}
