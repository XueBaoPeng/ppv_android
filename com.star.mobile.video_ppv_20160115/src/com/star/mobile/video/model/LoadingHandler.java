package com.star.mobile.video.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

public class LoadingHandler extends Handler{
	
	private List<TaskListener> tasks = new ArrayList<LoadingHandler.TaskListener>();
	public static final int a_oncreate = 0;
	public static final int a_onstart = 1;
	public static final int f_onstart = 2;

	@Override
	public void handleMessage(Message msg) {
		for(TaskListener task : tasks){
			if(msg.what == task.getType())
				task.excute();
		}
		super.handleMessage(msg);
	}
	
	public abstract class TaskListener{
		private int type;
		public TaskListener(int type){
			this.type = type;
		}
		
		public int getType(){
			return type;
		}
		public abstract void excute();
	}
	
	public void addTaskListener(TaskListener listener){
		tasks.add(listener);
	}
}
