package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.cms.model.Category;

public class CategoryListAdapter extends BaseAdapter {

	private List<Category> categorys;
	private Context context;
	public CategoryListAdapter(Context context, List<Category> categorys) {
		this.categorys = categorys;
		this.context = context;
	}
	@Override
	public int getCount() {
		return categorys.size();
	}

	@Override
	public Object getItem(int position) {
		return categorys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = new TextView(context);
		}
		((TextView)convertView).setText(categorys.get(position).getName());
		convertView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		return convertView;
	}
	
	class ViewHolder {
	}

}
