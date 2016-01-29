package com.star.mobile.video.me.mycoins;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.star.cms.model.vo.TaskVO;

public class TaskListAdapter extends BaseAdapter{
	
	private List<TaskVO> datas;
	private Context context;
	
	public TaskListAdapter(Context c,List<TaskVO> data) {
		this.context = c;
		this.datas = data;
	}
	
	public void updataDataRefreshUi(List<TaskVO> data) {
		this.datas = data;
		notifyDataSetChanged();
	}
	
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		TaskItemView itemView = (TaskItemView)view;
		if (itemView == null) {
			itemView = new TaskItemView(context);
		}
		itemView.setTask(datas.get(position));
		return itemView;
	}
}
