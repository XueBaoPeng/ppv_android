package com.star.mobile.video.search;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseAdapter;

public class SearchHistoryAdapter extends BaseAdapter<String> {

	private boolean status = true;

	public SearchHistoryAdapter(Context context, List<String> data) {
		super(context, data);
		// TODO Auto-generated constructor stub
	}
	
	public void setIconStatus(boolean show){
		this.status = show;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.view_search_hot_item, null);
		ImageView ivHot = (ImageView) convertView.findViewById(R.id.iv_hot_icon);
		TextView tvHot = (TextView) convertView.findViewById(R.id.tv_hot_key);
		if(status)
			ivHot.setImageResource(R.drawable.ic_access_time_black_48px);
		else
			ivHot.setVisibility(View.GONE);
		tvHot.setText(data.get(position));
		tvHot.setTextColor(context.getResources().getColor(R.color.list_text_color));
		return convertView;
	}

}
