package com.star.mobile.video.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 为了减轻代码的负担，创建BaseAdapter的子类时推荐继承该类
 * 
 * @param <T> 适配器所承载的数据类型
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
	public List<T> data = new ArrayList<T>();
	protected Context context;

	public BaseAdapter(Context context, List<T> data) {
		this.data.clear();
		this.data.addAll(data);
		this.context = context;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public T getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void notifyDataSetChanged(List<T> data){
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);
}