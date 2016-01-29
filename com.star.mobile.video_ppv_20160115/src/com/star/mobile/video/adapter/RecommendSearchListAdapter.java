package com.star.mobile.video.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.star.mobile.video.R;

public class RecommendSearchListAdapter extends BaseAdapter{
	
	private Context context;
	private List<String> datas;
	
	public RecommendSearchListAdapter(Context c,List<String> data) {
		this.context = c;
		this.datas = data;
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
		ViewHolder holder;
		if(view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.view_search_hot_item, null);
			holder = new ViewHolder();
//			holder.programName = (TextView) view.findViewById(R.id.tv_program_name);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.programName.setText(datas.get(position));
		return view;
	}

	class ViewHolder {
		public TextView programName;
	}
}
