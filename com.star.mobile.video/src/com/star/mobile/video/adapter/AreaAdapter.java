package com.star.mobile.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.mobile.video.R;

public class AreaAdapter extends BaseAdapter {

	private Context context;
	private String[] datas;
	
	public AreaAdapter(Context context,String[] datas)  {
		this.context = context;
		this.datas = datas;
	}
	
	@Override
	public int getCount() {
		
		return datas.length;
	}

	@Override
	public Object getItem(int position) {
		
		return datas[position];
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.view_select_date_item, null);
			viewHolder.tvDate = (TextView) view.findViewById(R.id.tv_date);
			viewHolder.line = view.findViewById(R.id.v_line);
			viewHolder.tvDate.setTextSize(17.0f);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)view.getTag();
		}
		viewHolder.tvDate.setText(datas[position]);
		if(position >= datas.length -1) {
			viewHolder.line.setVisibility(View.GONE);
		} else {
			viewHolder.line.setVisibility(View.VISIBLE);
		}
		return view;
	}

	class ViewHolder {
		public TextView tvDate;
		public View line;
	}
}
